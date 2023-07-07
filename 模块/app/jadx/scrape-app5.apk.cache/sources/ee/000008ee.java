package com.jakewharton.rxbinding2.view;

import android.view.KeyEvent;
import android.view.View;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Predicate;

/* loaded from: classes.dex */
final class ViewKeyObservable extends Observable<KeyEvent> {
    private final Predicate<? super KeyEvent> handled;
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewKeyObservable(View view, Predicate<? super KeyEvent> handled) {
        this.view = view;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super KeyEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, this.handled, observer);
        observer.onSubscribe(listener);
        this.view.setOnKeyListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements View.OnKeyListener {
        private final Predicate<? super KeyEvent> handled;
        private final Observer<? super KeyEvent> observer;
        private final View view;

        Listener(View view, Predicate<? super KeyEvent> handled, Observer<? super KeyEvent> observer) {
            this.view = view;
            this.handled = handled;
            this.observer = observer;
        }

        @Override // android.view.View.OnKeyListener
        public boolean onKey(View v, int keyCode, KeyEvent event) {
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
            this.view.setOnKeyListener(null);
        }
    }
}