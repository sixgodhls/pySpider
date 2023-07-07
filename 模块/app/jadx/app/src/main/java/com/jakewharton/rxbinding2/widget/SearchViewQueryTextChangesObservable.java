package com.jakewharton.rxbinding2.widget;

import android.widget.SearchView;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class SearchViewQueryTextChangesObservable extends InitialValueObservable<CharSequence> {
    private final SearchView view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SearchViewQueryTextChangesObservable(SearchView view) {
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super CharSequence> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        this.view.setOnQueryTextListener(listener);
        observer.onSubscribe(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public CharSequence mo308getInitialValue() {
        return this.view.getQuery();
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements SearchView.OnQueryTextListener {
        private final Observer<? super CharSequence> observer;
        private final SearchView view;

        Listener(SearchView view, Observer<? super CharSequence> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.SearchView.OnQueryTextListener
        public boolean onQueryTextChange(String s) {
            if (!isDisposed()) {
                this.observer.onNext(s);
                return true;
            }
            return false;
        }

        @Override // android.widget.SearchView.OnQueryTextListener
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnQueryTextListener(null);
        }
    }
}
