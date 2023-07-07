package io.reactivex.internal.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.schedulers.SchedulerMultiWorkerSupport;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class ComputationScheduler extends Scheduler implements SchedulerMultiWorkerSupport {
    private static final String KEY_COMPUTATION_PRIORITY = "rx2.computation-priority";
    static final FixedSchedulerPool NONE;
    static final RxThreadFactory THREAD_FACTORY;
    private static final String THREAD_NAME_PREFIX = "RxComputationThreadPool";
    final AtomicReference<FixedSchedulerPool> pool;
    final ThreadFactory threadFactory;
    static final String KEY_MAX_THREADS = "rx2.computation-threads";
    static final int MAX_THREADS = cap(Runtime.getRuntime().availableProcessors(), Integer.getInteger(KEY_MAX_THREADS, 0).intValue());
    static final PoolWorker SHUTDOWN_WORKER = new PoolWorker(new RxThreadFactory("RxComputationShutdown"));

    static {
        SHUTDOWN_WORKER.dispose();
        int priority = Math.max(1, Math.min(10, Integer.getInteger(KEY_COMPUTATION_PRIORITY, 5).intValue()));
        THREAD_FACTORY = new RxThreadFactory(THREAD_NAME_PREFIX, priority, true);
        NONE = new FixedSchedulerPool(0, THREAD_FACTORY);
        NONE.shutdown();
    }

    static int cap(int cpuCount, int paramThreads) {
        return (paramThreads <= 0 || paramThreads > cpuCount) ? cpuCount : paramThreads;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class FixedSchedulerPool implements SchedulerMultiWorkerSupport {
        final int cores;
        final PoolWorker[] eventLoops;
        long n;

        FixedSchedulerPool(int maxThreads, ThreadFactory threadFactory) {
            this.cores = maxThreads;
            this.eventLoops = new PoolWorker[maxThreads];
            for (int i = 0; i < maxThreads; i++) {
                this.eventLoops[i] = new PoolWorker(threadFactory);
            }
        }

        public PoolWorker getEventLoop() {
            int c = this.cores;
            if (c == 0) {
                return ComputationScheduler.SHUTDOWN_WORKER;
            }
            PoolWorker[] poolWorkerArr = this.eventLoops;
            long j = this.n;
            this.n = 1 + j;
            return poolWorkerArr[(int) (j % c)];
        }

        public void shutdown() {
            PoolWorker[] poolWorkerArr;
            for (PoolWorker w : this.eventLoops) {
                w.dispose();
            }
        }

        @Override // io.reactivex.internal.schedulers.SchedulerMultiWorkerSupport
        public void createWorkers(int number, SchedulerMultiWorkerSupport.WorkerCallback callback) {
            int c = this.cores;
            if (c == 0) {
                for (int i = 0; i < number; i++) {
                    callback.onWorker(i, ComputationScheduler.SHUTDOWN_WORKER);
                }
                return;
            }
            int index = ((int) this.n) % c;
            for (int i2 = 0; i2 < number; i2++) {
                callback.onWorker(i2, new EventLoopWorker(this.eventLoops[index]));
                index++;
                if (index == c) {
                    index = 0;
                }
            }
            this.n = index;
        }
    }

    public ComputationScheduler() {
        this(THREAD_FACTORY);
    }

    public ComputationScheduler(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        this.pool = new AtomicReference<>(NONE);
        start();
    }

    @Override // io.reactivex.Scheduler
    @NonNull
    public Scheduler.Worker createWorker() {
        return new EventLoopWorker(this.pool.get().getEventLoop());
    }

    @Override // io.reactivex.internal.schedulers.SchedulerMultiWorkerSupport
    public void createWorkers(int number, SchedulerMultiWorkerSupport.WorkerCallback callback) {
        ObjectHelper.verifyPositive(number, "number > 0 required");
        this.pool.get().createWorkers(number, callback);
    }

    @Override // io.reactivex.Scheduler
    @NonNull
    public Disposable scheduleDirect(@NonNull Runnable run, long delay, TimeUnit unit) {
        PoolWorker w = this.pool.get().getEventLoop();
        return w.scheduleDirect(run, delay, unit);
    }

    @Override // io.reactivex.Scheduler
    @NonNull
    public Disposable schedulePeriodicallyDirect(@NonNull Runnable run, long initialDelay, long period, TimeUnit unit) {
        PoolWorker w = this.pool.get().getEventLoop();
        return w.schedulePeriodicallyDirect(run, initialDelay, period, unit);
    }

    @Override // io.reactivex.Scheduler
    public void start() {
        FixedSchedulerPool update = new FixedSchedulerPool(MAX_THREADS, this.threadFactory);
        if (!this.pool.compareAndSet(NONE, update)) {
            update.shutdown();
        }
    }

    @Override // io.reactivex.Scheduler
    public void shutdown() {
        FixedSchedulerPool curr;
        FixedSchedulerPool fixedSchedulerPool;
        do {
            curr = this.pool.get();
            fixedSchedulerPool = NONE;
            if (curr == fixedSchedulerPool) {
                return;
            }
        } while (!this.pool.compareAndSet(curr, fixedSchedulerPool));
        curr.shutdown();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class EventLoopWorker extends Scheduler.Worker {
        volatile boolean disposed;
        private final PoolWorker poolWorker;
        private final ListCompositeDisposable serial = new ListCompositeDisposable();
        private final CompositeDisposable timed = new CompositeDisposable();
        private final ListCompositeDisposable both = new ListCompositeDisposable();

        EventLoopWorker(PoolWorker poolWorker) {
            this.poolWorker = poolWorker;
            this.both.add(this.serial);
            this.both.add(this.timed);
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            if (!this.disposed) {
                this.disposed = true;
                this.both.dispose();
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.disposed;
        }

        @Override // io.reactivex.Scheduler.Worker
        @NonNull
        public Disposable schedule(@NonNull Runnable action) {
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            return this.poolWorker.scheduleActual(action, 0L, TimeUnit.MILLISECONDS, this.serial);
        }

        @Override // io.reactivex.Scheduler.Worker
        @NonNull
        public Disposable schedule(@NonNull Runnable action, long delayTime, @NonNull TimeUnit unit) {
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            return this.poolWorker.scheduleActual(action, delayTime, unit, this.timed);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class PoolWorker extends NewThreadWorker {
        PoolWorker(ThreadFactory threadFactory) {
            super(threadFactory);
        }
    }
}
