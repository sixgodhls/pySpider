package com.jakewharton.rxbinding2.view;

import android.view.View;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class ViewLayoutChangeEventObservable extends Observable<ViewLayoutChangeEvent> {
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewLayoutChangeEventObservable(View view) {
        this.view = view;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super ViewLayoutChangeEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        observer.onSubscribe(listener);
        this.view.addOnLayoutChangeListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements View.OnLayoutChangeListener {
        private final Observer<? super ViewLayoutChangeEvent> observer;
        private final View view;

        Listener(View view, Observer<? super ViewLayoutChangeEvent> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (!isDisposed()) {
                this.observer.onNext(ViewLayoutChangeEvent.create(v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.removeOnLayoutChangeListener(this);
        }
    }
}
