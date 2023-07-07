package io.reactivex.observables;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.operators.observable.ObservableAutoConnect;
import io.reactivex.internal.operators.observable.ObservableRefCount;
import io.reactivex.internal.util.ConnectConsumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public abstract class ConnectableObservable<T> extends Observable<T> {
    public abstract void connect(@NonNull Consumer<? super Disposable> consumer);

    public final Disposable connect() {
        ConnectConsumer cc = new ConnectConsumer();
        connect(cc);
        return cc.disposable;
    }

    @SchedulerSupport(SchedulerSupport.NONE)
    @CheckReturnValue
    @NonNull
    public Observable<T> refCount() {
        return RxJavaPlugins.onAssembly(new ObservableRefCount(this));
    }

    @SchedulerSupport(SchedulerSupport.NONE)
    @CheckReturnValue
    public final Observable<T> refCount(int subscriberCount) {
        return refCount(subscriberCount, 0L, TimeUnit.NANOSECONDS, Schedulers.trampoline());
    }

    @SchedulerSupport(SchedulerSupport.COMPUTATION)
    @CheckReturnValue
    public final Observable<T> refCount(long timeout, TimeUnit unit) {
        return refCount(1, timeout, unit, Schedulers.computation());
    }

    @SchedulerSupport(SchedulerSupport.CUSTOM)
    @CheckReturnValue
    public final Observable<T> refCount(long timeout, TimeUnit unit, Scheduler scheduler) {
        return refCount(1, timeout, unit, scheduler);
    }

    @SchedulerSupport(SchedulerSupport.COMPUTATION)
    @CheckReturnValue
    public final Observable<T> refCount(int subscriberCount, long timeout, TimeUnit unit) {
        return refCount(subscriberCount, timeout, unit, Schedulers.computation());
    }

    @SchedulerSupport(SchedulerSupport.CUSTOM)
    @CheckReturnValue
    public final Observable<T> refCount(int subscriberCount, long timeout, TimeUnit unit, Scheduler scheduler) {
        ObjectHelper.verifyPositive(subscriberCount, "subscriberCount");
        ObjectHelper.requireNonNull(unit, "unit is null");
        ObjectHelper.requireNonNull(scheduler, "scheduler is null");
        return RxJavaPlugins.onAssembly(new ObservableRefCount(this, subscriberCount, timeout, unit, scheduler));
    }

    @NonNull
    public Observable<T> autoConnect() {
        return autoConnect(1);
    }

    @NonNull
    public Observable<T> autoConnect(int numberOfSubscribers) {
        return autoConnect(numberOfSubscribers, Functions.emptyConsumer());
    }

    @NonNull
    public Observable<T> autoConnect(int numberOfSubscribers, @NonNull Consumer<? super Disposable> connection) {
        if (numberOfSubscribers <= 0) {
            connect(connection);
            return RxJavaPlugins.onAssembly((ConnectableObservable) this);
        }
        return RxJavaPlugins.onAssembly(new ObservableAutoConnect(this, numberOfSubscribers, connection));
    }
}
