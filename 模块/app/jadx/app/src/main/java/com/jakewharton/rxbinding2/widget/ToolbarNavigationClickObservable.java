package com.jakewharton.rxbinding2.widget;

import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Toolbar;
import com.jakewharton.rxbinding2.internal.Notification;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

@RequiresApi(21)
/* loaded from: classes.dex */
final class ToolbarNavigationClickObservable extends Observable<Object> {
    private final Toolbar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToolbarNavigationClickObservable(Toolbar view) {
        this.view = view;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Object> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        observer.onSubscribe(listener);
        this.view.setNavigationOnClickListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements View.OnClickListener {
        private final Observer<? super Object> observer;
        private final Toolbar view;

        Listener(Toolbar view, Observer<? super Object> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (!isDisposed()) {
                this.observer.onNext(Notification.INSTANCE);
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setNavigationOnClickListener(null);
        }
    }
}
