package io.reactivex.internal.operators.observable;

import io.reactivex.Notification;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;

/* loaded from: classes.dex */
public final class ObservableDematerialize<T> extends AbstractObservableWithUpstream<Notification<T>, T> {
    public ObservableDematerialize(ObservableSource<Notification<T>> source) {
        super(source);
    }

    @Override // io.reactivex.Observable
    public void subscribeActual(Observer<? super T> t) {
        this.source.subscribe(new DematerializeObserver(t));
    }

    /* loaded from: classes.dex */
    static final class DematerializeObserver<T> implements Observer<Notification<T>>, Disposable {
        boolean done;
        final Observer<? super T> downstream;
        Disposable upstream;

        @Override // io.reactivex.Observer
        public /* bridge */ /* synthetic */ void onNext(Object obj) {
            onNext((Notification) ((Notification) obj));
        }

        DematerializeObserver(Observer<? super T> downstream) {
            this.downstream = downstream;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.upstream.dispose();
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.upstream.isDisposed();
        }

        public void onNext(Notification<T> t) {
            if (this.done) {
                if (t.isOnError()) {
                    RxJavaPlugins.onError(t.getError());
                }
            } else if (t.isOnError()) {
                this.upstream.dispose();
                onError(t.getError());
            } else if (t.isOnComplete()) {
                this.upstream.dispose();
                onComplete();
            } else {
                this.downstream.onNext(t.getValue());
            }
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.downstream.onError(t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            this.downstream.onComplete();
        }
    }
}