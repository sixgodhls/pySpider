package com.jakewharton.rxbinding2.widget;

import android.view.MenuItem;
import android.widget.PopupMenu;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class PopupMenuItemClickObservable extends Observable<MenuItem> {
    private final PopupMenu view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PopupMenuItemClickObservable(PopupMenu view) {
        this.view = view;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super MenuItem> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        this.view.setOnMenuItemClickListener(listener);
        observer.onSubscribe(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements PopupMenu.OnMenuItemClickListener {
        private final Observer<? super MenuItem> observer;
        private final PopupMenu view;

        Listener(PopupMenu view, Observer<? super MenuItem> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.PopupMenu.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (!isDisposed()) {
                this.observer.onNext(menuItem);
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