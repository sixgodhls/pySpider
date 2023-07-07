package io.reactivex.internal.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.SchedulerRunnableIntrospection;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class ExecutorScheduler extends Scheduler {
    static final Scheduler HELPER = Schedulers.single();
    @NonNull
    final Executor executor;

    public ExecutorScheduler(@NonNull Executor executor) {
        this.executor = executor;
    }

    @Override // io.reactivex.Scheduler
    @NonNull
    public Scheduler.Worker createWorker() {
        return new ExecutorWorker(this.executor);
    }

    @Override // io.reactivex.Scheduler
    @NonNull
    public Disposable scheduleDirect(@NonNull Runnable run) {
        Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
        try {
            if (this.executor instanceof ExecutorService) {
                ScheduledDirectTask task = new ScheduledDirectTask(decoratedRun);
                Future<?> f = ((ExecutorService) this.executor).submit(task);
                task.setFuture(f);
                return task;
            }
            ExecutorWorker.BooleanRunnable br = new ExecutorWorker.BooleanRunnable(decoratedRun);
            this.executor.execute(br);
            return br;
        } catch (RejectedExecutionException ex) {
            RxJavaPlugins.onError(ex);
            return EmptyDisposable.INSTANCE;
        }
    }

    @Override // io.reactivex.Scheduler
    @NonNull
    public Disposable scheduleDirect(@NonNull Runnable run, long delay, TimeUnit unit) {
        Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
        if (this.executor instanceof ScheduledExecutorService) {
            try {
                ScheduledDirectTask task = new ScheduledDirectTask(decoratedRun);
                Future<?> f = ((ScheduledExecutorService) this.executor).schedule(task, delay, unit);
                task.setFuture(f);
                return task;
            } catch (RejectedExecutionException ex) {
                RxJavaPlugins.onError(ex);
                return EmptyDisposable.INSTANCE;
            }
        }
        DelayedRunnable dr = new DelayedRunnable(decoratedRun);
        Disposable delayed = HELPER.scheduleDirect(new DelayedDispose(dr), delay, unit);
        dr.timed.replace(delayed);
        return dr;
    }

    @Override // io.reactivex.Scheduler
    @NonNull
    public Disposable schedulePeriodicallyDirect(@NonNull Runnable run, long initialDelay, long period, TimeUnit unit) {
        if (this.executor instanceof ScheduledExecutorService) {
            Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
            try {
                ScheduledDirectPeriodicTask task = new ScheduledDirectPeriodicTask(decoratedRun);
                Future<?> f = ((ScheduledExecutorService) this.executor).scheduleAtFixedRate(task, initialDelay, period, unit);
                task.setFuture(f);
                return task;
            } catch (RejectedExecutionException ex) {
                RxJavaPlugins.onError(ex);
                return EmptyDisposable.INSTANCE;
            }
        }
        return super.schedulePeriodicallyDirect(run, initialDelay, period, unit);
    }

    /* loaded from: classes.dex */
    public static final class ExecutorWorker extends Scheduler.Worker implements Runnable {
        volatile boolean disposed;
        final Executor executor;
        final AtomicInteger wip = new AtomicInteger();
        final CompositeDisposable tasks = new CompositeDisposable();
        final MpscLinkedQueue<Runnable> queue = new MpscLinkedQueue<>();

        public ExecutorWorker(Executor executor) {
            this.executor = executor;
        }

        @Override // io.reactivex.Scheduler.Worker
        @NonNull
        public Disposable schedule(@NonNull Runnable run) {
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
            BooleanRunnable br = new BooleanRunnable(decoratedRun);
            this.queue.offer(br);
            if (this.wip.getAndIncrement() == 0) {
                try {
                    this.executor.execute(this);
                } catch (RejectedExecutionException ex) {
                    this.disposed = true;
                    this.queue.clear();
                    RxJavaPlugins.onError(ex);
                    return EmptyDisposable.INSTANCE;
                }
            }
            return br;
        }

        @Override // io.reactivex.Scheduler.Worker
        @NonNull
        public Disposable schedule(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
            if (delay <= 0) {
                return schedule(run);
            }
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            SequentialDisposable first = new SequentialDisposable();
            SequentialDisposable mar = new SequentialDisposable(first);
            Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
            ScheduledRunnable sr = new ScheduledRunnable(new SequentialDispose(mar, decoratedRun), this.tasks);
            this.tasks.add(sr);
            Executor executor = this.executor;
            if (executor instanceof ScheduledExecutorService) {
                try {
                    Future<?> f = ((ScheduledExecutorService) executor).schedule((Callable) sr, delay, unit);
                    sr.setFuture(f);
                } catch (RejectedExecutionException ex) {
                    this.disposed = true;
                    RxJavaPlugins.onError(ex);
                    return EmptyDisposable.INSTANCE;
                }
            } else {
                Disposable d = ExecutorScheduler.HELPER.scheduleDirect(sr, delay, unit);
                sr.setFuture(new DisposeOnCancel(d));
            }
            first.replace(sr);
            return mar;
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            if (!this.disposed) {
                this.disposed = true;
                this.tasks.dispose();
                if (this.wip.getAndIncrement() == 0) {
                    this.queue.clear();
                }
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.disposed;
        }

        /* JADX WARN: Code restructure failed: missing block: B:14:0x0016, code lost:
            if (r4.disposed == false) goto L15;
         */
        /* JADX WARN: Code restructure failed: missing block: B:15:0x001c, code lost:
            r0 = r4.wip.addAndGet(-r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:16:0x0023, code lost:
            if (r0 != 0) goto L2;
         */
        /* JADX WARN: Code restructure failed: missing block: B:18:0x0026, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:20:0x0018, code lost:
            r1.clear();
         */
        /* JADX WARN: Code restructure failed: missing block: B:21:0x001b, code lost:
            return;
         */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                r4 = this;
                r0 = 1
                io.reactivex.internal.queue.MpscLinkedQueue<java.lang.Runnable> r1 = r4.queue
            L3:
                boolean r2 = r4.disposed
                if (r2 == 0) goto Lb
                r1.clear()
                return
            Lb:
                java.lang.Object r2 = r1.mo337poll()
                java.lang.Runnable r2 = (java.lang.Runnable) r2
                if (r2 != 0) goto L27
            L14:
                boolean r2 = r4.disposed
                if (r2 == 0) goto L1c
                r1.clear()
                return
            L1c:
                java.util.concurrent.atomic.AtomicInteger r2 = r4.wip
                int r3 = -r0
                int r0 = r2.addAndGet(r3)
                if (r0 != 0) goto L3
            L26:
                return
            L27:
                r2.run()
                boolean r3 = r4.disposed
                if (r3 == 0) goto L32
                r1.clear()
                return
            L32:
                goto Lb
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.schedulers.ExecutorScheduler.ExecutorWorker.run():void");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static final class BooleanRunnable extends AtomicBoolean implements Runnable, Disposable {
            private static final long serialVersionUID = -2421395018820541164L;
            final Runnable actual;

            BooleanRunnable(Runnable actual) {
                this.actual = actual;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (get()) {
                    return;
                }
                try {
                    this.actual.run();
                } finally {
                    lazySet(true);
                }
            }

            @Override // io.reactivex.disposables.Disposable
            public void dispose() {
                lazySet(true);
            }

            @Override // io.reactivex.disposables.Disposable
            public boolean isDisposed() {
                return get();
            }
        }

        /* loaded from: classes.dex */
        final class SequentialDispose implements Runnable {
            private final Runnable decoratedRun;
            private final SequentialDisposable mar;

            SequentialDispose(SequentialDisposable mar, Runnable decoratedRun) {
                this.mar = mar;
                this.decoratedRun = decoratedRun;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.mar.replace(ExecutorWorker.this.schedule(this.decoratedRun));
            }
        }
    }

    /* loaded from: classes.dex */
    static final class DelayedRunnable extends AtomicReference<Runnable> implements Runnable, Disposable, SchedulerRunnableIntrospection {
        private static final long serialVersionUID = -4101336210206799084L;
        final SequentialDisposable timed = new SequentialDisposable();
        final SequentialDisposable direct = new SequentialDisposable();

        DelayedRunnable(Runnable run) {
            super(run);
        }

        @Override // java.lang.Runnable
        public void run() {
            Runnable r = get();
            if (r != null) {
                try {
                    r.run();
                } finally {
                    lazySet(null);
                    this.timed.lazySet(DisposableHelper.DISPOSED);
                    this.direct.lazySet(DisposableHelper.DISPOSED);
                }
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return get() == null;
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            if (getAndSet(null) != null) {
                this.timed.dispose();
                this.direct.dispose();
            }
        }

        @Override // io.reactivex.schedulers.SchedulerRunnableIntrospection
        public Runnable getWrappedRunnable() {
            Runnable r = get();
            return r != null ? r : Functions.EMPTY_RUNNABLE;
        }
    }

    /* loaded from: classes.dex */
    final class DelayedDispose implements Runnable {
        private final DelayedRunnable dr;

        DelayedDispose(DelayedRunnable dr) {
            this.dr = dr;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.dr.direct.replace(ExecutorScheduler.this.scheduleDirect(this.dr));
        }
    }
}
