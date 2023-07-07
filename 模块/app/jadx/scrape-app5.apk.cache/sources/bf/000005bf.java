package com.jakewharton.rxbinding2.widget;

import android.widget.RatingBar;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class RatingBarRatingChangeEventObservable extends InitialValueObservable<RatingBarChangeEvent> {
    private final RatingBar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RatingBarRatingChangeEventObservable(RatingBar view) {
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super RatingBarChangeEvent> observer) {
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
    public RatingBarChangeEvent mo370getInitialValue() {
        RatingBar ratingBar = this.view;
        return RatingBarChangeEvent.create(ratingBar, ratingBar.getRating(), false);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements RatingBar.OnRatingBarChangeListener {
        private final Observer<? super RatingBarChangeEvent> observer;
        private final RatingBar view;

        Listener(RatingBar view, Observer<? super RatingBarChangeEvent> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.widget.RatingBar.OnRatingBarChangeListener
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if (!isDisposed()) {
                this.observer.onNext(RatingBarChangeEvent.create(ratingBar, rating, fromUser));
            }
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnRatingBarChangeListener(null);
        }
    }
}