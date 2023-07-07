package com.jakewharton.rxbinding2.widget;

import android.support.annotation.Nullable;
import android.widget.SeekBar;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class SeekBarChangeObservable extends InitialValueObservable<Integer> {
    @Nullable
    private final Boolean shouldBeFromUser;
    private final SeekBar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SeekBarChangeObservable(SeekBar view, @Nullable Boolean shouldBeFromUser) {
        this.view = view;
        this.shouldBeFromUser = shouldBeFromUser;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super Integer> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, this.shouldBeFromUser, observer);
        this.view.setOnSeekBarChangeListener(listener);
        observer.onSubscribe(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public Integer mo308getInitialValue() {
        return Integer.valueOf(this.view.getProgress());
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements SeekBar.OnSeekBarChangeListener {
        private final Observer<? super Integer> observer;
        private final Boolean shouldBeFromUser;
        private final SeekBar view;

        Listener(SeekBar view, Boolean shouldBeFromUser, Observer<? super Integer> observer) {
            this.view = view;
            this.shouldBeFromUser = shouldBeFromUser;
            this.observer = observer;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!isDisposed()) {
                Boolean bool = this.shouldBeFromUser;
                if (bool == null || bool.booleanValue() == fromUser) {
                    this.observer.onNext(Integer.valueOf(progress));
                }
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnSeekBarChangeListener(null);
        }
    }
}
