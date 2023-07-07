package com.jakewharton.rxbinding2.view;

import android.view.View;
import android.view.ViewGroup;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class ViewGroupHierarchyChangeEventObservable extends Observable<ViewGroupHierarchyChangeEvent> {
    private final ViewGroup viewGroup;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewGroupHierarchyChangeEventObservable(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super ViewGroupHierarchyChangeEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.viewGroup, observer);
        observer.onSubscribe(listener);
        this.viewGroup.setOnHierarchyChangeListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements ViewGroup.OnHierarchyChangeListener {
        private final Observer<? super ViewGroupHierarchyChangeEvent> observer;
        private final ViewGroup viewGroup;

        Listener(ViewGroup viewGroup, Observer<? super ViewGroupHierarchyChangeEvent> observer) {
            this.viewGroup = viewGroup;
            this.observer = observer;
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewAdded(View parent, View child) {
            if (!isDisposed()) {
                this.observer.onNext(ViewGroupHierarchyChildViewAddEvent.create(this.viewGroup, child));
            }
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewRemoved(View parent, View child) {
            if (!isDisposed()) {
                this.observer.onNext(ViewGroupHierarchyChildViewRemoveEvent.create(this.viewGroup, child));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.viewGroup.setOnHierarchyChangeListener(null);
        }
    }
}
