package com.jakewharton.rxbinding2.widget;

import android.widget.CompoundButton;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class CompoundButtonCheckedChangeObservable extends InitialValueObservable<Boolean> {
    private final CompoundButton view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompoundButtonCheckedChangeObservable(CompoundButton view) {
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super Boolean> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        observer.onSubscribe(listener);
        this.view.setOnCheckedChangeListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public Boolean mo370getInitialValue() {
        return Boolean.valueOf(this.view.isChecked());
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements CompoundButton.OnCheckedChangeListener {
        private final Observer<? super Boolean> observer;
        private final CompoundButton view;

        Listener(CompoundButton view, Observer<? super Boolean> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (!isDisposed()) {
                this.observer.onNext(Boolean.valueOf(isChecked));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnCheckedChangeListener(null);
        }
    }
}