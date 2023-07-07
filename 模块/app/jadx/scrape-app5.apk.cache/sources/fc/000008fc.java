package com.jakewharton.rxbinding2.widget;

import android.widget.AbsListView;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class AbsListViewScrollEventObservable extends Observable<AbsListViewScrollEvent> {
    private final AbsListView view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbsListViewScrollEventObservable(AbsListView view) {
        this.view = view;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super AbsListViewScrollEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        observer.onSubscribe(listener);
        this.view.setOnScrollListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements AbsListView.OnScrollListener {
        private int currentScrollState = 0;
        private final Observer<? super AbsListViewScrollEvent> observer;
        private final AbsListView view;

        Listener(AbsListView view, Observer<? super AbsListViewScrollEvent> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            this.currentScrollState = scrollState;
            if (!isDisposed()) {
                AbsListView absListView2 = this.view;
                AbsListViewScrollEvent event = AbsListViewScrollEvent.create(absListView2, scrollState, absListView2.getFirstVisiblePosition(), this.view.getChildCount(), this.view.getCount());
                this.observer.onNext(event);
            }
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!isDisposed()) {
                AbsListViewScrollEvent event = AbsListViewScrollEvent.create(this.view, this.currentScrollState, firstVisibleItem, visibleItemCount, totalItemCount);
                this.observer.onNext(event);
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnScrollListener(null);
        }
    }
}