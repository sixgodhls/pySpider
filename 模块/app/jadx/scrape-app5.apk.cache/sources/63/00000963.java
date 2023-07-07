package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscribers.InnerQueuedSubscriber;
import io.reactivex.internal.subscribers.InnerQueuedSubscriberSupport;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ErrorMode;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowableConcatMapEager<T, R> extends AbstractFlowableWithUpstream<T, R> {
    final ErrorMode errorMode;
    final Function<? super T, ? extends Publisher<? extends R>> mapper;
    final int maxConcurrency;
    final int prefetch;

    public FlowableConcatMapEager(Flowable<T> source, Function<? super T, ? extends Publisher<? extends R>> mapper, int maxConcurrency, int prefetch, ErrorMode errorMode) {
        super(source);
        this.mapper = mapper;
        this.maxConcurrency = maxConcurrency;
        this.prefetch = prefetch;
        this.errorMode = errorMode;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super R> s) {
        this.source.subscribe((FlowableSubscriber) new ConcatMapEagerDelayErrorSubscriber(s, this.mapper, this.maxConcurrency, this.prefetch, this.errorMode));
    }

    /* loaded from: classes.dex */
    static final class ConcatMapEagerDelayErrorSubscriber<T, R> extends AtomicInteger implements FlowableSubscriber<T>, Subscription, InnerQueuedSubscriberSupport<R> {
        private static final long serialVersionUID = -4255299542215038287L;
        volatile boolean cancelled;
        volatile InnerQueuedSubscriber<R> current;
        volatile boolean done;
        final Subscriber<? super R> downstream;
        final ErrorMode errorMode;
        final Function<? super T, ? extends Publisher<? extends R>> mapper;
        final int maxConcurrency;
        final int prefetch;
        final SpscLinkedArrayQueue<InnerQueuedSubscriber<R>> subscribers;
        Subscription upstream;
        final AtomicThrowable errors = new AtomicThrowable();
        final AtomicLong requested = new AtomicLong();

        /* JADX INFO: Access modifiers changed from: package-private */
        public ConcatMapEagerDelayErrorSubscriber(Subscriber<? super R> actual, Function<? super T, ? extends Publisher<? extends R>> mapper, int maxConcurrency, int prefetch, ErrorMode errorMode) {
            this.downstream = actual;
            this.mapper = mapper;
            this.maxConcurrency = maxConcurrency;
            this.prefetch = prefetch;
            this.errorMode = errorMode;
            this.subscribers = new SpscLinkedArrayQueue<>(Math.min(prefetch, maxConcurrency));
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                int i = this.maxConcurrency;
                s.request(i == Integer.MAX_VALUE ? Long.MAX_VALUE : i);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            try {
                Publisher<? extends R> p = (Publisher) ObjectHelper.requireNonNull(this.mapper.mo401apply(t), "The mapper returned a null Publisher");
                InnerQueuedSubscriber<R> inner = new InnerQueuedSubscriber<>(this, this.prefetch);
                if (this.cancelled) {
                    return;
                }
                this.subscribers.offer(inner);
                p.subscribe(inner);
                if (this.cancelled) {
                    inner.cancel();
                    drainAndCancel();
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.upstream.cancel();
                onError(ex);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            if (this.errors.addThrowable(t)) {
                this.done = true;
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.done = true;
            drain();
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            if (this.cancelled) {
                return;
            }
            this.cancelled = true;
            this.upstream.cancel();
            drainAndCancel();
        }

        void drainAndCancel() {
            if (getAndIncrement() == 0) {
                do {
                    cancelAll();
                } while (decrementAndGet() != 0);
            }
        }

        void cancelAll() {
            while (true) {
                InnerQueuedSubscriber<R> inner = this.subscribers.mo399poll();
                if (inner != null) {
                    inner.cancel();
                } else {
                    return;
                }
            }
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        @Override // io.reactivex.internal.subscribers.InnerQueuedSubscriberSupport
        public void innerNext(InnerQueuedSubscriber<R> inner, R value) {
            if (inner.queue().offer(value)) {
                drain();
                return;
            }
            inner.cancel();
            innerError(inner, new MissingBackpressureException());
        }

        @Override // io.reactivex.internal.subscribers.InnerQueuedSubscriberSupport
        public void innerError(InnerQueuedSubscriber<R> inner, Throwable e) {
            if (this.errors.addThrowable(e)) {
                inner.setDone();
                if (this.errorMode != ErrorMode.END) {
                    this.upstream.cancel();
                }
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        @Override // io.reactivex.internal.subscribers.InnerQueuedSubscriberSupport
        public void innerComplete(InnerQueuedSubscriber<R> inner) {
            inner.setDone();
            drain();
        }

        /* JADX WARN: Code restructure failed: missing block: B:52:0x00cf, code lost:
            if (r8 != r6) goto L81;
         */
        /* JADX WARN: Code restructure failed: missing block: B:54:0x00d3, code lost:
            if (r17.cancelled == false) goto L55;
         */
        /* JADX WARN: Code restructure failed: missing block: B:56:0x00db, code lost:
            if (r4 != io.reactivex.internal.util.ErrorMode.IMMEDIATE) goto L62;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x00e5, code lost:
            if (r17.errors.get() == null) goto L62;
         */
        /* JADX WARN: Code restructure failed: missing block: B:60:0x00e7, code lost:
            r17.current = null;
            r2.cancel();
            cancelAll();
            r3.onError(r17.errors.terminate());
         */
        /* JADX WARN: Code restructure failed: missing block: B:61:0x00f9, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:62:0x00fa, code lost:
            r0 = r2.isDone();
            r14 = r11.isEmpty();
         */
        /* JADX WARN: Code restructure failed: missing block: B:63:0x0102, code lost:
            if (r0 == false) goto L81;
         */
        /* JADX WARN: Code restructure failed: missing block: B:64:0x0104, code lost:
            if (r14 == false) goto L81;
         */
        /* JADX WARN: Code restructure failed: missing block: B:65:0x0106, code lost:
            r2 = null;
            r17.current = null;
            r17.upstream.request(1);
            r10 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:67:0x00d5, code lost:
            cancelAll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:68:0x00d8, code lost:
            return;
         */
        @Override // io.reactivex.internal.subscribers.InnerQueuedSubscriberSupport
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void drain() {
            /*
                Method dump skipped, instructions count: 308
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableConcatMapEager.ConcatMapEagerDelayErrorSubscriber.drain():void");
        }
    }
}