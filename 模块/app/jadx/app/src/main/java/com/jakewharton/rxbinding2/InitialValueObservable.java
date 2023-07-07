package com.jakewharton.rxbinding2;

import io.reactivex.Observable;
import io.reactivex.Observer;

/* loaded from: classes.dex */
public abstract class InitialValueObservable<T> extends Observable<T> {
    /* renamed from: getInitialValue */
    protected abstract T mo308getInitialValue();

    protected abstract void subscribeListener(Observer<? super T> observer);

    @Override // io.reactivex.Observable
    protected final void subscribeActual(Observer<? super T> observer) {
        subscribeListener(observer);
        observer.onNext(mo308getInitialValue());
    }

    public final Observable<T> skipInitialValue() {
        return new Skipped();
    }

    /* loaded from: classes.dex */
    private final class Skipped extends Observable<T> {
        Skipped() {
        }

        @Override // io.reactivex.Observable
        protected void subscribeActual(Observer<? super T> observer) {
            InitialValueObservable.this.subscribeListener(observer);
        }
    }
}
