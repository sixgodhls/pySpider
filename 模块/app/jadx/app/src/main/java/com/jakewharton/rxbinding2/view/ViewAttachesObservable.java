package com.jakewharton.rxbinding2.view;

import android.view.View;
import com.jakewharton.rxbinding2.internal.Notification;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class ViewAttachesObservable extends Observable<Object> {
    private final boolean callOnAttach;
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewAttachesObservable(View view, boolean callOnAttach) {
        this.view = view;
        this.callOnAttach = callOnAttach;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Object> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, this.callOnAttach, observer);
        observer.onSubscribe(listener);
        this.view.addOnAttachStateChangeListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements View.OnAttachStateChangeListener {
        private final boolean callOnAttach;
        private final Observer<? super Object> observer;
        private final View view;

        Listener(View view, boolean callOnAttach, Observer<? super Object> observer) {
            this.view = view;
            this.callOnAttach = callOnAttach;
            this.observer = observer;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            if (this.callOnAttach && !isDisposed()) {
                this.observer.onNext(Notification.INSTANCE);
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            if (!this.callOnAttach && !isDisposed()) {
                this.observer.onNext(Notification.INSTANCE);
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.removeOnAttachStateChangeListener(this);
        }
    }
}
