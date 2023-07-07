package com.jakewharton.rxbinding2.widget;

import android.view.KeyEvent;
import android.widget.TextView;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Predicate;

/* loaded from: classes.dex */
final class TextViewEditorActionObservable extends Observable<Integer> {
    private final Predicate<? super Integer> handled;
    private final TextView view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextViewEditorActionObservable(TextView view, Predicate<? super Integer> handled) {
        this.view = view;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Integer> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer, this.handled);
        observer.onSubscribe(listener);
        this.view.setOnEditorActionListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements TextView.OnEditorActionListener {
        private final Predicate<? super Integer> handled;
        private final Observer<? super Integer> observer;
        private final TextView view;

        Listener(TextView view, Observer<? super Integer> observer, Predicate<? super Integer> handled) {
            this.view = view;
            this.observer = observer;
            this.handled = handled;
        }

        @Override // android.widget.TextView.OnEditorActionListener
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            try {
                if (!isDisposed() && this.handled.test(Integer.valueOf(actionId))) {
                    this.observer.onNext(Integer.valueOf(actionId));
                    return true;
                }
                return false;
            } catch (Exception e) {
                this.observer.onError(e);
                dispose();
                return false;
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnEditorActionListener(null);
        }
    }
}