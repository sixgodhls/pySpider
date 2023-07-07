package com.jakewharton.rxbinding2.view;

import android.view.View;
import com.jakewharton.rxbinding2.internal.Notification;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import java.util.concurrent.Callable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ViewLongClickObservable extends Observable<Object> {
    private final Callable<Boolean> handled;
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewLongClickObservable(View view, Callable<Boolean> handled) {
        this.view = view;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Object> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, this.handled, observer);
        observer.onSubscribe(listener);
        this.view.setOnLongClickListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements View.OnLongClickListener {
        private final Callable<Boolean> handled;
        private final Observer<? super Object> observer;
        private final View view;

        Listener(View view, Callable<Boolean> handled, Observer<? super Object> observer) {
            this.view = view;
            this.observer = observer;
            this.handled = handled;
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            if (!isDisposed()) {
                try {
                    if (this.handled.call().booleanValue()) {
                        this.observer.onNext(Notification.INSTANCE);
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
            this.view.setOnLongClickListener(null);
        }
    }
}