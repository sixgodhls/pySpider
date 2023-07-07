package com.jakewharton.rxbinding2.widget;

import android.widget.RatingBar;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class RatingBarRatingChangeObservable extends InitialValueObservable<Float> {
    private final RatingBar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RatingBarRatingChangeObservable(RatingBar view) {
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super Float> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer);
        this.view.setOnRatingBarChangeListener(listener);
        observer.onSubscribe(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public Float mo370getInitialValue() {
        return Float.valueOf(this.view.getRating());
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements RatingBar.OnRatingBarChangeListener {
        private final Observer<? super Float> observer;
        private final RatingBar view;

        Listener(RatingBar view, Observer<? super Float> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.RatingBar.OnRatingBarChangeListener
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if (!isDisposed()) {
                this.observer.onNext(Float.valueOf(rating));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnRatingBarChangeListener(null);
        }
    }
}