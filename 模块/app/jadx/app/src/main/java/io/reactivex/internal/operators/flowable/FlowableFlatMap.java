package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowableFlatMap<T, U> extends AbstractFlowableWithUpstream<T, U> {
    final int bufferSize;
    final boolean delayErrors;
    final Function<? super T, ? extends Publisher<? extends U>> mapper;
    final int maxConcurrency;

    public FlowableFlatMap(Flowable<T> source, Function<? super T, ? extends Publisher<? extends U>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
        super(source);
        this.mapper = mapper;
        this.delayErrors = delayErrors;
        this.maxConcurrency = maxConcurrency;
        this.bufferSize = bufferSize;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super U> s) {
        if (FlowableScalarXMap.tryScalarXMapSubscribe(this.source, s, this.mapper)) {
            return;
        }
        this.source.subscribe((FlowableSubscriber) subscribe(s, this.mapper, this.delayErrors, this.maxConcurrency, this.bufferSize));
    }

    public static <T, U> FlowableSubscriber<T> subscribe(Subscriber<? super U> s, Function<? super T, ? extends Publisher<? extends U>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
        return new MergeSubscriber(s, mapper, delayErrors, maxConcurrency, bufferSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MergeSubscriber<T, U> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -2117620485640801370L;
        final int bufferSize;
        volatile boolean cancelled;
        final boolean delayErrors;
        volatile boolean done;
        final Subscriber<? super U> downstream;
        long lastId;
        int lastIndex;
        final Function<? super T, ? extends Publisher<? extends U>> mapper;
        final int maxConcurrency;
        volatile SimplePlainQueue<U> queue;
        int scalarEmitted;
        final int scalarLimit;
        long uniqueId;
        Subscription upstream;
        static final InnerSubscriber<?, ?>[] EMPTY = new InnerSubscriber[0];
        static final InnerSubscriber<?, ?>[] CANCELLED = new InnerSubscriber[0];
        final AtomicThrowable errs = new AtomicThrowable();
        final AtomicReference<InnerSubscriber<?, ?>[]> subscribers = new AtomicReference<>();
        final AtomicLong requested = new AtomicLong();

        MergeSubscriber(Subscriber<? super U> actual, Function<? super T, ? extends Publisher<? extends U>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
            this.downstream = actual;
            this.mapper = mapper;
            this.delayErrors = delayErrors;
            this.maxConcurrency = maxConcurrency;
            this.bufferSize = bufferSize;
            this.scalarLimit = Math.max(1, maxConcurrency >> 1);
            this.subscribers.lazySet(EMPTY);
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                if (!this.cancelled) {
                    int i = this.maxConcurrency;
                    if (i == Integer.MAX_VALUE) {
                        s.request(Long.MAX_VALUE);
                    } else {
                        s.request(i);
                    }
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (this.done) {
                return;
            }
            try {
                Publisher<? extends U> p = (Publisher) ObjectHelper.requireNonNull(this.mapper.mo339apply(t), "The mapper returned a null Publisher");
                if (p instanceof Callable) {
                    try {
                        Object call = ((Callable) p).call();
                        if (call != null) {
                            tryEmitScalar(call);
                            return;
                        } else if (this.maxConcurrency == Integer.MAX_VALUE || this.cancelled) {
                            return;
                        } else {
                            int i = this.scalarEmitted + 1;
                            this.scalarEmitted = i;
                            int i2 = this.scalarLimit;
                            if (i == i2) {
                                this.scalarEmitted = 0;
                                this.upstream.request(i2);
                                return;
                            }
                            return;
                        }
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        this.errs.addThrowable(ex);
                        drain();
                        return;
                    }
                }
                long j = this.uniqueId;
                this.uniqueId = 1 + j;
                InnerSubscriber<T, U> inner = new InnerSubscriber<>(this, j);
                if (addInner(inner)) {
                    p.subscribe(inner);
                }
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                this.upstream.cancel();
                onError(e);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        boolean addInner(InnerSubscriber<T, U> inner) {
            InnerSubscriber<?, ?>[] a;
            InnerSubscriber[] innerSubscriberArr;
            do {
                a = this.subscribers.get();
                if (a == CANCELLED) {
                    inner.dispose();
                    return false;
                }
                int n = a.length;
                innerSubscriberArr = new InnerSubscriber[n + 1];
                System.arraycopy(a, 0, innerSubscriberArr, 0, n);
                innerSubscriberArr[n] = inner;
            } while (!this.subscribers.compareAndSet(a, innerSubscriberArr));
            return true;
        }

        void removeInner(InnerSubscriber<T, U> inner) {
            InnerSubscriber<?, ?>[] a;
            InnerSubscriber<?, ?>[] b;
            do {
                a = this.subscribers.get();
                int n = a.length;
                if (n == 0) {
                    return;
                }
                int j = -1;
                int i = 0;
                while (true) {
                    if (i >= n) {
                        break;
                    } else if (a[i] != inner) {
                        i++;
                    } else {
                        j = i;
                        break;
                    }
                }
                if (j < 0) {
                    return;
                }
                if (n == 1) {
                    b = EMPTY;
                } else {
                    InnerSubscriber<?, ?>[] b2 = new InnerSubscriber[n - 1];
                    System.arraycopy(a, 0, b2, 0, j);
                    System.arraycopy(a, j + 1, b2, j, (n - j) - 1);
                    b = b2;
                }
            } while (!this.subscribers.compareAndSet(a, b));
        }

        SimpleQueue<U> getMainQueue() {
            SimplePlainQueue<U> q = this.queue;
            if (q == null) {
                int i = this.maxConcurrency;
                if (i == Integer.MAX_VALUE) {
                    q = new SpscLinkedArrayQueue(this.bufferSize);
                } else {
                    q = new SpscArrayQueue<>(i);
                }
                this.queue = q;
            }
            return q;
        }

        void tryEmitScalar(U value) {
            if (get() == 0 && compareAndSet(0, 1)) {
                long r = this.requested.get();
                SimpleQueue<U> q = this.queue;
                if (r != 0 && (q == null || q.isEmpty())) {
                    this.downstream.onNext(value);
                    if (r != Long.MAX_VALUE) {
                        this.requested.decrementAndGet();
                    }
                    if (this.maxConcurrency != Integer.MAX_VALUE && !this.cancelled) {
                        int i = this.scalarEmitted + 1;
                        this.scalarEmitted = i;
                        int i2 = this.scalarLimit;
                        if (i == i2) {
                            this.scalarEmitted = 0;
                            this.upstream.request(i2);
                        }
                    }
                } else {
                    if (q == null) {
                        q = getMainQueue();
                    }
                    if (!q.offer(value)) {
                        onError(new IllegalStateException("Scalar queue full?!"));
                        return;
                    }
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            } else if (!getMainQueue().offer(value)) {
                onError(new IllegalStateException("Scalar queue full?!"));
                return;
            } else if (getAndIncrement() != 0) {
                return;
            }
            drainLoop();
        }

        SimpleQueue<U> getInnerQueue(InnerSubscriber<T, U> inner) {
            SimpleQueue<U> q = inner.queue;
            if (q == null) {
                SimpleQueue<U> q2 = new SpscArrayQueue<>(this.bufferSize);
                inner.queue = q2;
                return q2;
            }
            return q;
        }

        void tryEmit(U value, InnerSubscriber<T, U> inner) {
            if (get() == 0 && compareAndSet(0, 1)) {
                long r = this.requested.get();
                SimpleQueue<U> q = inner.queue;
                if (r != 0 && (q == null || q.isEmpty())) {
                    this.downstream.onNext(value);
                    if (r != Long.MAX_VALUE) {
                        this.requested.decrementAndGet();
                    }
                    inner.requestMore(1L);
                } else {
                    if (q == null) {
                        q = getInnerQueue(inner);
                    }
                    if (!q.offer(value)) {
                        onError(new MissingBackpressureException("Inner queue full?!"));
                        return;
                    }
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            } else {
                SimpleQueue<U> q2 = inner.queue;
                if (q2 == null) {
                    q2 = new SpscArrayQueue(this.bufferSize);
                    inner.queue = q2;
                }
                if (!q2.offer(value)) {
                    onError(new MissingBackpressureException("Inner queue full?!"));
                    return;
                } else if (getAndIncrement() != 0) {
                    return;
                }
            }
            drainLoop();
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
            } else if (this.errs.addThrowable(t)) {
                this.done = true;
                drain();
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            drain();
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            SimpleQueue<U> q;
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.cancel();
                disposeAll();
                if (getAndIncrement() == 0 && (q = this.queue) != null) {
                    q.clear();
                }
            }
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:99:0x01ee, code lost:
            r34.lastIndex = r5;
            r34.lastId = r9[r5].id;
            r4 = r19;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void drainLoop() {
            /*
                Method dump skipped, instructions count: 551
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableFlatMap.MergeSubscriber.drainLoop():void");
        }

        boolean checkTerminate() {
            if (this.cancelled) {
                clearScalarQueue();
                return true;
            } else if (!this.delayErrors && this.errs.get() != null) {
                clearScalarQueue();
                Throwable ex = this.errs.terminate();
                if (ex != ExceptionHelper.TERMINATED) {
                    this.downstream.onError(ex);
                }
                return true;
            } else {
                return false;
            }
        }

        void clearScalarQueue() {
            SimpleQueue<U> q = this.queue;
            if (q != null) {
                q.clear();
            }
        }

        void disposeAll() {
            InnerSubscriber<?, ?>[] a = this.subscribers.get();
            InnerSubscriber<?, ?>[] innerSubscriberArr = CANCELLED;
            if (a != innerSubscriberArr) {
                InnerSubscriber<?, ?>[] a2 = this.subscribers.getAndSet(innerSubscriberArr);
                InnerSubscriber<?, ?>[] a3 = a2;
                if (a3 != CANCELLED) {
                    for (InnerSubscriber<?, ?> inner : a3) {
                        inner.dispose();
                    }
                    Throwable ex = this.errs.terminate();
                    if (ex != null && ex != ExceptionHelper.TERMINATED) {
                        RxJavaPlugins.onError(ex);
                    }
                }
            }
        }

        void innerError(InnerSubscriber<T, U> inner, Throwable t) {
            InnerSubscriber<?, ?>[] andSet;
            if (this.errs.addThrowable(t)) {
                inner.done = true;
                if (!this.delayErrors) {
                    this.upstream.cancel();
                    for (InnerSubscriber<?, ?> a : this.subscribers.getAndSet(CANCELLED)) {
                        a.dispose();
                    }
                }
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class InnerSubscriber<T, U> extends AtomicReference<Subscription> implements FlowableSubscriber<U>, Disposable {
        private static final long serialVersionUID = -4606175640614850599L;
        final int bufferSize;
        volatile boolean done;
        int fusionMode;
        final long id;
        final int limit;
        final MergeSubscriber<T, U> parent;
        long produced;
        volatile SimpleQueue<U> queue;

        InnerSubscriber(MergeSubscriber<T, U> parent, long id) {
            this.id = id;
            this.parent = parent;
            this.bufferSize = parent.bufferSize;
            this.limit = this.bufferSize >> 2;
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<U> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(7);
                    if (m == 1) {
                        this.fusionMode = m;
                        this.queue = qs;
                        this.done = true;
                        this.parent.drain();
                        return;
                    } else if (m == 2) {
                        this.fusionMode = m;
                        this.queue = qs;
                    }
                }
                s.request(this.bufferSize);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(U t) {
            if (this.fusionMode != 2) {
                this.parent.tryEmit(t, this);
            } else {
                this.parent.drain();
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            lazySet(SubscriptionHelper.CANCELLED);
            this.parent.innerError(this, t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.done = true;
            this.parent.drain();
        }

        void requestMore(long n) {
            if (this.fusionMode != 1) {
                long p = this.produced + n;
                if (p >= this.limit) {
                    this.produced = 0L;
                    get().request(p);
                    return;
                }
                this.produced = p;
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            SubscriptionHelper.cancel(this);
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return get() == SubscriptionHelper.CANCELLED;
        }
    }
}
