package com.jakewharton.rxbinding2.view;

import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewTreeObserver;
import com.jakewharton.rxbinding2.internal.Notification;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

@RequiresApi(16)
/* loaded from: classes.dex */
final class ViewTreeObserverDrawObservable extends Observable<Object> {
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewTreeObserverDrawObservable(View view) {
        this.view = view;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Object> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        observer.onSubscribe(listener);
        this.view.getViewTreeObserver().addOnDrawListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements ViewTreeObserver.OnDrawListener {
        private final Observer<? super Object> observer;
        private final View view;

        Listener(View view, Observer<? super Object> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.view.ViewTreeObserver.OnDrawListener
        public void onDraw() {
            if (!isDisposed()) {
                this.observer.onNext(Notification.INSTANCE);
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.getViewTreeObserver().removeOnDrawListener(this);
        }
    }
}
