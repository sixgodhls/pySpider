package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionArbiter;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowableDelaySubscriptionOther<T, U> extends Flowable<T> {
    final Publisher<? extends T> main;
    final Publisher<U> other;

    public FlowableDelaySubscriptionOther(Publisher<? extends T> main, Publisher<U> other) {
        this.main = main;
        this.other = other;
    }

    @Override // io.reactivex.Flowable
    public void subscribeActual(Subscriber<? super T> child) {
        SubscriptionArbiter serial = new SubscriptionArbiter();
        child.onSubscribe(serial);
        FlowableSubscriber<U> otherSubscriber = new DelaySubscriber(serial, child);
        this.other.subscribe(otherSubscriber);
    }

    /* loaded from: classes.dex */
    final class DelaySubscriber implements FlowableSubscriber<U> {
        final Subscriber<? super T> child;
        boolean done;
        final SubscriptionArbiter serial;

        DelaySubscriber(SubscriptionArbiter serial, Subscriber<? super T> child) {
            this.serial = serial;
            this.child = child;
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            this.serial.setSubscription(new DelaySubscription(s));
            s.request(Long.MAX_VALUE);
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(U t) {
            onComplete();
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable e) {
            if (this.done) {
                RxJavaPlugins.onError(e);
                return;
            }
            this.done = true;
            this.child.onError(e);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            FlowableDelaySubscriptionOther.this.main.subscribe(new OnCompleteSubscriber());
        }

        /* loaded from: classes.dex */
        final class DelaySubscription implements Subscription {
            final Subscription upstream;

            DelaySubscription(Subscription s) {
                this.upstream = s;
            }

            @Override // org.reactivestreams.Subscription
            public void request(long n) {
            }

            @Override // org.reactivestreams.Subscription
            public void cancel() {
                this.upstream.cancel();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class OnCompleteSubscriber implements FlowableSubscriber<T> {
            OnCompleteSubscriber() {
            }

            @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
            public void onSubscribe(Subscription s) {
                DelaySubscriber.this.serial.setSubscription(s);
            }

            @Override // org.reactivestreams.Subscriber
            public void onNext(T t) {
                DelaySubscriber.this.child.onNext(t);
            }

            @Override // org.reactivestreams.Subscriber
            public void onError(Throwable t) {
                DelaySubscriber.this.child.onError(t);
            }

            @Override // org.reactivestreams.Subscriber
            public void onComplete() {
                DelaySubscriber.this.child.onComplete();
            }
        }
    }
}
