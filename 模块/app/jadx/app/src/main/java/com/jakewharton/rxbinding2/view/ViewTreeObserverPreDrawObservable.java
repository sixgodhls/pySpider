package com.jakewharton.rxbinding2.view;

import android.view.View;
import android.view.ViewTreeObserver;
import com.jakewharton.rxbinding2.internal.Notification;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import java.util.concurrent.Callable;

/* loaded from: classes.dex */
final class ViewTreeObserverPreDrawObservable extends Observable<Object> {
    private final Callable<Boolean> proceedDrawingPass;
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewTreeObserverPreDrawObservable(View view, Callable<Boolean> proceedDrawingPass) {
        this.view = view;
        this.proceedDrawingPass = proceedDrawingPass;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Object> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, this.proceedDrawingPass, observer);
        observer.onSubscribe(listener);
        this.view.getViewTreeObserver().addOnPreDrawListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements ViewTreeObserver.OnPreDrawListener {
        private final Observer<? super Object> observer;
        private final Callable<Boolean> proceedDrawingPass;
        private final View view;

        Listener(View view, Callable<Boolean> proceedDrawingPass, Observer<? super Object> observer) {
            this.view = view;
            this.proceedDrawingPass = proceedDrawingPass;
            this.observer = observer;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            if (!isDisposed()) {
                this.observer.onNext(Notification.INSTANCE);
                try {
                    return this.proceedDrawingPass.call().booleanValue();
                } catch (Exception e) {
                    this.observer.onError(e);
                    dispose();
                    return true;
                }
            }
            return true;
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.getViewTreeObserver().removeOnPreDrawListener(this);
        }
    }
}
