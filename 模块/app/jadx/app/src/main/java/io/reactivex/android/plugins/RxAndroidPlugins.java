package io.reactivex.android.plugins;

import io.reactivex.Scheduler;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import java.util.concurrent.Callable;

/* loaded from: classes.dex */
public final class RxAndroidPlugins {
    private static volatile Function<Callable<Scheduler>, Scheduler> onInitMainThreadHandler;
    private static volatile Function<Scheduler, Scheduler> onMainThreadHandler;

    public static void setInitMainThreadSchedulerHandler(Function<Callable<Scheduler>, Scheduler> handler) {
        onInitMainThreadHandler = handler;
    }

    public static Scheduler initMainThreadScheduler(Callable<Scheduler> scheduler) {
        if (scheduler == null) {
            throw new NullPointerException("scheduler == null");
        }
        Function<Callable<Scheduler>, Scheduler> f = onInitMainThreadHandler;
        if (f == null) {
            return callRequireNonNull(scheduler);
        }
        return applyRequireNonNull(f, scheduler);
    }

    public static void setMainThreadSchedulerHandler(Function<Scheduler, Scheduler> handler) {
        onMainThreadHandler = handler;
    }

    public static Scheduler onMainThreadScheduler(Scheduler scheduler) {
        if (scheduler == null) {
            throw new NullPointerException("scheduler == null");
        }
        Function<Scheduler, Scheduler> f = onMainThreadHandler;
        if (f == null) {
            return scheduler;
        }
        return (Scheduler) apply(f, scheduler);
    }

    public static Function<Callable<Scheduler>, Scheduler> getInitMainThreadSchedulerHandler() {
        return onInitMainThreadHandler;
    }

    public static Function<Scheduler, Scheduler> getOnMainThreadSchedulerHandler() {
        return onMainThreadHandler;
    }

    public static void reset() {
        setInitMainThreadSchedulerHandler(null);
        setMainThreadSchedulerHandler(null);
    }

    static Scheduler callRequireNonNull(Callable<Scheduler> s) {
        try {
            Scheduler scheduler = s.call();
            if (scheduler == null) {
                throw new NullPointerException("Scheduler Callable returned null");
            }
            return scheduler;
        } catch (Throwable ex) {
            throw Exceptions.propagate(ex);
        }
    }

    static Scheduler applyRequireNonNull(Function<Callable<Scheduler>, Scheduler> f, Callable<Scheduler> s) {
        Scheduler scheduler = (Scheduler) apply(f, s);
        if (scheduler == null) {
            throw new NullPointerException("Scheduler Callable returned null");
        }
        return scheduler;
    }

    static <T, R> R apply(Function<T, R> f, T t) {
        try {
            return f.mo339apply(t);
        } catch (Throwable ex) {
            throw Exceptions.propagate(ex);
        }
    }

    private RxAndroidPlugins() {
        throw new AssertionError("No instances.");
    }
}
