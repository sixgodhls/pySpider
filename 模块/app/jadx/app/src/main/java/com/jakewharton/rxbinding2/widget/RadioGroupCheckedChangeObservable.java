package com.jakewharton.rxbinding2.widget;

import android.widget.RadioGroup;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class RadioGroupCheckedChangeObservable extends InitialValueObservable<Integer> {
    private final RadioGroup view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RadioGroupCheckedChangeObservable(RadioGroup view) {
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super Integer> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        this.view.setOnCheckedChangeListener(listener);
        observer.onSubscribe(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public Integer mo308getInitialValue() {
        return Integer.valueOf(this.view.getCheckedRadioButtonId());
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements RadioGroup.OnCheckedChangeListener {
        private int lastChecked = -1;
        private final Observer<? super Integer> observer;
        private final RadioGroup view;

        Listener(RadioGroup view, Observer<? super Integer> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.RadioGroup.OnCheckedChangeListener
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (!isDisposed() && checkedId != this.lastChecked) {
                this.lastChecked = checkedId;
                this.observer.onNext(Integer.valueOf(checkedId));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnCheckedChangeListener(null);
        }
    }
}
