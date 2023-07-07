package com.jakewharton.rxbinding2.widget;

import android.database.DataSetObserver;
import android.widget.Adapter;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class AdapterDataChangeObservable<T extends Adapter> extends InitialValueObservable<T> {
    private final T adapter;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdapterDataChangeObservable(T adapter) {
        this.adapter = adapter;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super T> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        ObserverDisposable<T> disposableDataSetObserver = new ObserverDisposable<>(this.adapter, observer);
        this.adapter.registerDataSetObserver(disposableDataSetObserver.dataSetObserver);
        observer.onSubscribe(disposableDataSetObserver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public T mo370getInitialValue() {
        return this.adapter;
    }

    /* loaded from: classes.dex */
    static final class ObserverDisposable<T extends Adapter> extends MainThreadDisposable {
        private final T adapter;
        final DataSetObserver dataSetObserver;

        ObserverDisposable(final T adapter, final Observer<? super T> observer) {
            this.adapter = adapter;
            this.dataSetObserver = new DataSetObserver() { // from class: com.jakewharton.rxbinding2.widget.AdapterDataChangeObservable.ObserverDisposable.1
                @Override // android.database.DataSetObserver
                public void onChanged() {
                    if (!ObserverDisposable.this.isDisposed()) {
                        observer.onNext(adapter);
                    }
                }
            };
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.adapter.unregisterDataSetObserver(this.dataSetObserver);
        }
    }
}