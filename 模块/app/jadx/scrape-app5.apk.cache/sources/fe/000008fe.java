package com.jakewharton.rxbinding2.view;

import android.view.MenuItem;
import com.jakewharton.rxbinding2.internal.Notification;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Predicate;

/* loaded from: classes.dex */
final class MenuItemClickOnSubscribe extends Observable<Object> {
    private final Predicate<? super MenuItem> handled;
    private final MenuItem menuItem;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItemClickOnSubscribe(MenuItem menuItem, Predicate<? super MenuItem> handled) {
        this.menuItem = menuItem;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Object> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.menuItem, this.handled, observer);
        observer.onSubscribe(listener);
        this.menuItem.setOnMenuItemClickListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements MenuItem.OnMenuItemClickListener {
        private final Predicate<? super MenuItem> handled;
        private final MenuItem menuItem;
        private final Observer<? super Object> observer;

        Listener(MenuItem menuItem, Predicate<? super MenuItem> handled, Observer<? super Object> observer) {
            this.menuItem = menuItem;
            this.handled = handled;
            this.observer = observer;
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            if (!isDisposed()) {
                try {
                    if (this.handled.test(this.menuItem)) {
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
            this.menuItem.setOnMenuItemClickListener(null);
        }
    }
}