package com.jakewharton.rxbinding2.widget;

import android.support.annotation.RequiresApi;
import android.view.MenuItem;
import android.widget.Toolbar;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

@RequiresApi(21)
/* loaded from: classes.dex */
final class ToolbarItemClickObservable extends Observable<MenuItem> {
    private final Toolbar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToolbarItemClickObservable(Toolbar view) {
        this.view = view;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super MenuItem> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        observer.onSubscribe(listener);
        this.view.setOnMenuItemClickListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements Toolbar.OnMenuItemClickListener {
        private final Observer<? super MenuItem> observer;
        private final Toolbar view;

        Listener(Toolbar view, Observer<? super MenuItem> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.Toolbar.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            if (!isDisposed()) {
                this.observer.onNext(item);
                return true;
            }
            return false;
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnMenuItemClickListener(null);
        }
    }
}
