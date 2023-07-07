package com.jakewharton.rxbinding2.view;

import android.view.View;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class ViewAttachEventObservable extends Observable<ViewAttachEvent> {
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewAttachEventObservable(View view) {
        this.view = view;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super ViewAttachEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        observer.onSubscribe(listener);
        this.view.addOnAttachStateChangeListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements View.OnAttachStateChangeListener {
        private final Observer<? super ViewAttachEvent> observer;
        private final View view;

        Listener(View view, Observer<? super ViewAttachEvent> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            if (!isDisposed()) {
                this.observer.onNext(ViewAttachAttachedEvent.create(this.view));
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            if (!isDisposed()) {
                this.observer.onNext(ViewAttachDetachedEvent.create(this.view));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.removeOnAttachStateChangeListener(this);
        }
    }
}