package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowableDelay<T> extends AbstractFlowableWithUpstream<T, T> {
    final long delay;
    final boolean delayError;
    final Scheduler scheduler;
    final TimeUnit unit;

    public FlowableDelay(Flowable<T> source, long delay, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        super(source);
        this.delay = delay;
        this.unit = unit;
        this.scheduler = scheduler;
        this.delayError = delayError;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super T> t) {
        Subscriber<? super T> downstream;
        if (this.delayError) {
            downstream = t;
        } else {
            downstream = new SerializedSubscriber<>(t);
        }
        Scheduler.Worker w = this.scheduler.createWorker();
        this.source.subscribe((FlowableSubscriber) new DelaySubscriber(downstream, this.delay, this.unit, w, this.delayError));
    }

    /* loaded from: classes.dex */
    static final class DelaySubscriber<T> implements FlowableSubscriber<T>, Subscription {
        final long delay;
        final boolean delayError;
        final Subscriber<? super T> downstream;
        final TimeUnit unit;
        Subscription upstream;

        /* renamed from: w */
        final Scheduler.Worker f117w;

        DelaySubscriber(Subscriber<? super T> actual, long delay, TimeUnit unit, Scheduler.Worker w, boolean delayError) {
            this.downstream = actual;
            this.delay = delay;
            this.unit = unit;
            this.f117w = w;
            this.delayError = delayError;
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            this.f117w.schedule(new OnNext(t), this.delay, this.unit);
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            this.f117w.schedule(new OnError(t), this.delayError ? this.delay : 0L, this.unit);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.f117w.schedule(new OnComplete(), this.delay, this.unit);
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            this.upstream.request(n);
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.upstream.cancel();
            this.f117w.dispose();
        }

        /* loaded from: classes.dex */
        final class OnNext implements Runnable {

            /* renamed from: t */
            private final T f119t;

            OnNext(T t) {
                this.f119t = t;
            }

            @Override // java.lang.Runnable
            public void run() {
                DelaySubscriber.this.downstream.onNext((T) this.f119t);
            }
        }

        /* loaded from: classes.dex */
        final class OnError implements Runnable {

            /* renamed from: t */
            private final Throwable f118t;

            OnError(Throwable t) {
                this.f118t = t;
            }

            @Override // java.lang.Runnable
            public void run() {
                try {
                    DelaySubscriber.this.downstream.onError(this.f118t);
                } finally {
                    DelaySubscriber.this.f117w.dispose();
                }
            }
        }

        /* loaded from: classes.dex */
        final class OnComplete implements Runnable {
            OnComplete() {
            }

            @Override // java.lang.Runnable
            public void run() {
                try {
                    DelaySubscriber.this.downstream.onComplete();
                } finally {
                    DelaySubscriber.this.f117w.dispose();
                }
            }
        }
    }
}