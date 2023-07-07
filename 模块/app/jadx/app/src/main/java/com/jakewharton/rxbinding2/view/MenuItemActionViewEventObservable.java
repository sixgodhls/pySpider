package com.jakewharton.rxbinding2.view;

import android.view.MenuItem;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Predicate;

/* loaded from: classes.dex */
final class MenuItemActionViewEventObservable extends Observable<MenuItemActionViewEvent> {
    private final Predicate<? super MenuItemActionViewEvent> handled;
    private final MenuItem menuItem;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItemActionViewEventObservable(MenuItem menuItem, Predicate<? super MenuItemActionViewEvent> handled) {
        this.menuItem = menuItem;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super MenuItemActionViewEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.menuItem, this.handled, observer);
        observer.onSubscribe(listener);
        this.menuItem.setOnActionExpandListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements MenuItem.OnActionExpandListener {
        private final Predicate<? super MenuItemActionViewEvent> handled;
        private final MenuItem menuItem;
        private final Observer<? super MenuItemActionViewEvent> observer;

        Listener(MenuItem menuItem, Predicate<? super MenuItemActionViewEvent> handled, Observer<? super MenuItemActionViewEvent> observer) {
            this.menuItem = menuItem;
            this.handled = handled;
            this.observer = observer;
        }

        @Override // android.view.MenuItem.OnActionExpandListener
        public boolean onMenuItemActionExpand(MenuItem item) {
            return onEvent(MenuItemActionViewExpandEvent.create(item));
        }

        @Override // android.view.MenuItem.OnActionExpandListener
        public boolean onMenuItemActionCollapse(MenuItem item) {
            return onEvent(MenuItemActionViewCollapseEvent.create(item));
        }

        private boolean onEvent(MenuItemActionViewEvent event) {
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
            this.menuItem.setOnActionExpandListener(null);
        }
    }
}
