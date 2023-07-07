package com.jakewharton.rxbinding2.view;

import android.view.MotionEvent;
import android.view.View;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Predicate;

/* loaded from: classes.dex */
final class ViewHoverObservable extends Observable<MotionEvent> {
    private final Predicate<? super MotionEvent> handled;
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewHoverObservable(View view, Predicate<? super MotionEvent> handled) {
        this.view = view;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super MotionEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, this.handled, observer);
        observer.onSubscribe(listener);
        this.view.setOnHoverListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements View.OnHoverListener {
        private final Predicate<? super MotionEvent> handled;
        private final Observer<? super MotionEvent> observer;
        private final View view;

        Listener(View view, Predicate<? super MotionEvent> handled, Observer<? super MotionEvent> observer) {
            this.view = view;
            this.handled = handled;
            this.observer = observer;
        }

        @Override // android.view.View.OnHoverListener
        public boolean onHover(View v, MotionEvent event) {
            if (!isDisposed()) {
                try {
                    if (this.handled.test(event)) {
                        this.observer.onNext(event);
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    this.observer.onError(e);
                    dispose();
                    return false;
                }
            }
            return false;
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnHoverListener(null);
        }
    }
}
