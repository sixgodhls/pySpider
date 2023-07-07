package io.reactivex.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.internal.schedulers.ComputationScheduler;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.internal.schedulers.IoScheduler;
import io.reactivex.internal.schedulers.NewThreadScheduler;
import io.reactivex.internal.schedulers.SchedulerPoolFactory;
import io.reactivex.internal.schedulers.SingleScheduler;
import io.reactivex.internal.schedulers.TrampolineScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public final class Schedulers {
    @NonNull
    static final Scheduler SINGLE = RxJavaPlugins.initSingleScheduler(new SingleTask());
    @NonNull
    static final Scheduler COMPUTATION = RxJavaPlugins.initComputationScheduler(new ComputationTask());
    @NonNull

    /* renamed from: IO */
    static final Scheduler f201IO = RxJavaPlugins.initIoScheduler(new IOTask());
    @NonNull
    static final Scheduler TRAMPOLINE = TrampolineScheduler.instance();
    @NonNull
    static final Scheduler NEW_THREAD = RxJavaPlugins.initNewThreadScheduler(new NewThreadTask());

    /* loaded from: classes.dex */
    public static final class SingleHolder {
        static final Scheduler DEFAULT = new SingleScheduler();

        SingleHolder() {
        }
    }

    /* loaded from: classes.dex */
    public static final class ComputationHolder {
        static final Scheduler DEFAULT = new ComputationScheduler();

        ComputationHolder() {
        }
    }

    /* loaded from: classes.dex */
    public static final class IoHolder {
        static final Scheduler DEFAULT = new IoScheduler();

        IoHolder() {
        }
    }

    /* loaded from: classes.dex */
    public static final class NewThreadHolder {
        static final Scheduler DEFAULT = new NewThreadScheduler();

        NewThreadHolder() {
        }
    }

    private Schedulers() {
        throw new IllegalStateException("No instances!");
    }

    @NonNull
    public static Scheduler computation() {
        return RxJavaPlugins.onComputationScheduler(COMPUTATION);
    }

    @NonNull
    /* renamed from: io */
    public static Scheduler m27io() {
        return RxJavaPlugins.onIoScheduler(f201IO);
    }

    @NonNull
    public static Scheduler trampoline() {
        return TRAMPOLINE;
    }

    @NonNull
    public static Scheduler newThread() {
        return RxJavaPlugins.onNewThreadScheduler(NEW_THREAD);
    }

    @NonNull
    public static Scheduler single() {
        return RxJavaPlugins.onSingleScheduler(SINGLE);
    }

    @NonNull
    public static Scheduler from(@NonNull Executor executor) {
        return new ExecutorScheduler(executor);
    }

    public static void shutdown() {
        computation().shutdown();
        m27io().shutdown();
        newThread().shutdown();
        single().shutdown();
        trampoline().shutdown();
        SchedulerPoolFactory.shutdown();
    }

    public static void start() {
        computation().start();
        m27io().start();
        newThread().start();
        single().start();
        trampoline().start();
        SchedulerPoolFactory.start();
    }

    /* loaded from: classes.dex */
    static final class IOTask implements Callable<Scheduler> {
        IOTask() {
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public Scheduler mo405call() throws Exception {
            return IoHolder.DEFAULT;
        }
    }

    /* loaded from: classes.dex */
    static final class NewThreadTask implements Callable<Scheduler> {
        NewThreadTask() {
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public Scheduler mo406call() throws Exception {
            return NewThreadHolder.DEFAULT;
        }
    }

    /* loaded from: classes.dex */
    static final class SingleTask implements Callable<Scheduler> {
        SingleTask() {
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public Scheduler mo407call() throws Exception {
            return SingleHolder.DEFAULT;
        }
    }

    /* loaded from: classes.dex */
    static final class ComputationTask implements Callable<Scheduler> {
        ComputationTask() {
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public Scheduler mo404call() throws Exception {
            return ComputationHolder.DEFAULT;
        }
    }
}