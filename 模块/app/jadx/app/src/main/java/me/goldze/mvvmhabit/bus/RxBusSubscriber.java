package me.goldze.mvvmhabit.bus;

import io.reactivex.observers.DisposableObserver;

/* loaded from: classes.dex */
public abstract class RxBusSubscriber<T> extends DisposableObserver<T> {
    protected abstract void onEvent(T t);

    @Override // io.reactivex.Observer
    public void onNext(T t) {
        try {
            onEvent(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // io.reactivex.Observer
    public void onComplete() {
    }

    @Override // io.reactivex.Observer
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}
