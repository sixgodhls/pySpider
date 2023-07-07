package com.jakewharton.rxbinding2.widget;

import android.view.View;
import android.widget.AdapterView;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class AdapterViewSelectionObservable extends InitialValueObservable<AdapterViewSelectionEvent> {
    private final AdapterView<?> view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdapterViewSelectionObservable(AdapterView<?> view) {
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super AdapterViewSelectionEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        this.view.setOnItemSelectedListener(listener);
        observer.onSubscribe(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public AdapterViewSelectionEvent mo370getInitialValue() {
        int selectedPosition = this.view.getSelectedItemPosition();
        if (selectedPosition == -1) {
            return AdapterViewNothingSelectionEvent.create(this.view);
        }
        View selectedView = this.view.getSelectedView();
        long selectedId = this.view.getSelectedItemId();
        return AdapterViewItemSelectionEvent.create(this.view, selectedView, selectedPosition, selectedId);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements AdapterView.OnItemSelectedListener {
        private final Observer<? super AdapterViewSelectionEvent> observer;
        private final AdapterView<?> view;

        Listener(AdapterView<?> view, Observer<? super AdapterViewSelectionEvent> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!isDisposed()) {
                this.observer.onNext(AdapterViewItemSelectionEvent.create(parent, view, position, id));
            }
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> parent) {
            if (!isDisposed()) {
                this.observer.onNext(AdapterViewNothingSelectionEvent.create(parent));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnItemSelectedListener(null);
        }
    }
}