package com.jakewharton.rxbinding2.widget;

import android.widget.SeekBar;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class SeekBarChangeEventObservable extends InitialValueObservable<SeekBarChangeEvent> {
    private final SeekBar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SeekBarChangeEventObservable(SeekBar view) {
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super SeekBarChangeEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        this.view.setOnSeekBarChangeListener(listener);
        observer.onSubscribe(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public SeekBarChangeEvent mo308getInitialValue() {
        SeekBar seekBar = this.view;
        return SeekBarProgressChangeEvent.create(seekBar, seekBar.getProgress(), false);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements SeekBar.OnSeekBarChangeListener {
        private final Observer<? super SeekBarChangeEvent> observer;
        private final SeekBar view;

        Listener(SeekBar view, Observer<? super SeekBarChangeEvent> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!isDisposed()) {
                this.observer.onNext(SeekBarProgressChangeEvent.create(seekBar, progress, fromUser));
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (!isDisposed()) {
                this.observer.onNext(SeekBarStartChangeEvent.create(seekBar));
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!isDisposed()) {
                this.observer.onNext(SeekBarStopChangeEvent.create(seekBar));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnSeekBarChangeListener(null);
        }
    }
}
