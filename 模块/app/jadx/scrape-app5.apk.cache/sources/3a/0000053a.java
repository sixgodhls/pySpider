package com.trello.rxlifecycle2.android;

import android.view.View;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class ViewDetachesOnSubscribe implements ObservableOnSubscribe<Object> {
    static final Object SIGNAL = new Object();
    final View view;

    public ViewDetachesOnSubscribe(View view) {
        this.view = view;
    }

    @Override // io.reactivex.ObservableOnSubscribe
    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
        MainThreadDisposable.verifyMainThread();
        EmitterListener listener = new EmitterListener(emitter);
        emitter.setDisposable(listener);
        this.view.addOnAttachStateChangeListener(listener);
    }

    /* loaded from: classes.dex */
    class EmitterListener extends MainThreadDisposable implements View.OnAttachStateChangeListener {
        final ObservableEmitter<Object> emitter;

        public EmitterListener(ObservableEmitter<Object> emitter) {
            this.emitter = emitter;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            this.emitter.onNext(ViewDetachesOnSubscribe.SIGNAL);
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            ViewDetachesOnSubscribe.this.view.removeOnAttachStateChangeListener(this);
        }
    }
}