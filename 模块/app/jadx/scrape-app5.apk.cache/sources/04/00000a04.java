package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.subscribers.QueueDrainSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.UnicastProcessor;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowableWindowBoundarySelector<T, B, V> extends AbstractFlowableWithUpstream<T, Flowable<T>> {
    final int bufferSize;
    final Function<? super B, ? extends Publisher<V>> close;
    final Publisher<B> open;

    public FlowableWindowBoundarySelector(Flowable<T> source, Publisher<B> open, Function<? super B, ? extends Publisher<V>> close, int bufferSize) {
        super(source);
        this.open = open;
        this.close = close;
        this.bufferSize = bufferSize;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super Flowable<T>> s) {
        this.source.subscribe((FlowableSubscriber) new WindowBoundaryMainSubscriber(new SerializedSubscriber(s), this.open, this.close, this.bufferSize));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class WindowBoundaryMainSubscriber<T, B, V> extends QueueDrainSubscriber<T, Object, Flowable<T>> implements Subscription {
        final int bufferSize;
        final Function<? super B, ? extends Publisher<V>> close;
        final Publisher<B> open;
        Subscription upstream;
        final AtomicReference<Disposable> boundary = new AtomicReference<>();
        final AtomicLong windows = new AtomicLong();
        final CompositeDisposable resources = new CompositeDisposable();

        /* renamed from: ws */
        final List<UnicastProcessor<T>> f139ws = new ArrayList();

        WindowBoundaryMainSubscriber(Subscriber<? super Flowable<T>> actual, Publisher<B> open, Function<? super B, ? extends Publisher<V>> close, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.open = open;
            this.close = close;
            this.bufferSize = bufferSize;
            this.windows.lazySet(1L);
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                if (this.cancelled) {
                    return;
                }
                OperatorWindowBoundaryOpenSubscriber<T, B> os = new OperatorWindowBoundaryOpenSubscriber<>(this);
                if (this.boundary.compareAndSet(null, os)) {
                    this.windows.getAndIncrement();
                    s.request(Long.MAX_VALUE);
                    this.open.subscribe(os);
                }
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (this.done) {
                return;
            }
            if (fastEnter()) {
                for (UnicastProcessor<T> w : this.f139ws) {
                    w.onNext(t);
                }
                if (leave(-1) == 0) {
                    return;
                }
            } else {
                this.queue.offer(NotificationLite.next(t));
                if (!enter()) {
                    return;
                }
            }
            drainLoop();
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            if (this.windows.decrementAndGet() == 0) {
                this.resources.dispose();
            }
            this.downstream.onError(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            if (this.windows.decrementAndGet() == 0) {
                this.resources.dispose();
            }
            this.downstream.onComplete();
        }

        void error(Throwable t) {
            this.upstream.cancel();
            this.resources.dispose();
            DisposableHelper.dispose(this.boundary);
            this.downstream.onError(t);
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            requested(n);
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.cancelled = true;
        }

        void dispose() {
            this.resources.dispose();
            DisposableHelper.dispose(this.boundary);
        }

        void drainLoop() {
            SimplePlainQueue<Object> q = this.queue;
            Subscriber<? super V> subscriber = this.downstream;
            List<UnicastProcessor<T>> ws = this.f139ws;
            int missed = 1;
            while (true) {
                boolean d = this.done;
                Object o = q.mo399poll();
                boolean empty = o == null;
                if (d && empty) {
                    dispose();
                    Throwable e = this.error;
                    if (e != null) {
                        for (UnicastProcessor<T> w : ws) {
                            w.onError(e);
                        }
                    } else {
                        for (UnicastProcessor<T> w2 : ws) {
                            w2.onComplete();
                        }
                    }
                    ws.clear();
                    return;
                } else if (!empty) {
                    if (o instanceof WindowOperation) {
                        WindowOperation<T, B> wo = (WindowOperation) o;
                        if (wo.f140w != null) {
                            if (ws.remove(wo.f140w)) {
                                wo.f140w.onComplete();
                                if (this.windows.decrementAndGet() == 0) {
                                    dispose();
                                    return;
                                }
                            } else {
                                continue;
                            }
                        } else if (!this.cancelled) {
                            UnicastProcessor<T> w3 = UnicastProcessor.create(this.bufferSize);
                            long r = requested();
                            if (r != 0) {
                                ws.add(w3);
                                subscriber.onNext(w3);
                                if (r != Long.MAX_VALUE) {
                                    produced(1L);
                                }
                                try {
                                    Publisher<V> p = (Publisher) ObjectHelper.requireNonNull(this.close.mo401apply((B) wo.open), "The publisher supplied is null");
                                    OperatorWindowBoundaryCloseSubscriber<T, V> cl = new OperatorWindowBoundaryCloseSubscriber<>(this, w3);
                                    if (this.resources.add(cl)) {
                                        this.windows.getAndIncrement();
                                        p.subscribe(cl);
                                    }
                                } catch (Throwable e2) {
                                    this.cancelled = true;
                                    subscriber.onError(e2);
                                }
                            } else {
                                this.cancelled = true;
                                subscriber.onError(new MissingBackpressureException("Could not deliver new window due to lack of requests"));
                            }
                        }
                    } else {
                        for (UnicastProcessor<T> w4 : ws) {
                            w4.onNext((T) NotificationLite.getValue(o));
                        }
                    }
                } else {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }

        @Override // io.reactivex.internal.subscribers.QueueDrainSubscriber, io.reactivex.internal.util.QueueDrain
        public boolean accept(Subscriber<? super Flowable<T>> a, Object v) {
            return false;
        }

        void open(B b) {
            this.queue.offer(new WindowOperation(null, b));
            if (enter()) {
                drainLoop();
            }
        }

        void close(OperatorWindowBoundaryCloseSubscriber<T, V> w) {
            this.resources.delete(w);
            this.queue.offer(new WindowOperation(w.f138w, null));
            if (enter()) {
                drainLoop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class WindowOperation<T, B> {
        final B open;

        /* renamed from: w */
        final UnicastProcessor<T> f140w;

        WindowOperation(UnicastProcessor<T> w, B open) {
            this.f140w = w;
            this.open = open;
        }
    }

    /* loaded from: classes.dex */
    static final class OperatorWindowBoundaryOpenSubscriber<T, B> extends DisposableSubscriber<B> {
        final WindowBoundaryMainSubscriber<T, B, ?> parent;

        OperatorWindowBoundaryOpenSubscriber(WindowBoundaryMainSubscriber<T, B, ?> parent) {
            this.parent = parent;
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(B t) {
            this.parent.open(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            this.parent.error(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.parent.onComplete();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class OperatorWindowBoundaryCloseSubscriber<T, V> extends DisposableSubscriber<V> {
        boolean done;
        final WindowBoundaryMainSubscriber<T, ?, V> parent;

        /* renamed from: w */
        final UnicastProcessor<T> f138w;

        OperatorWindowBoundaryCloseSubscriber(WindowBoundaryMainSubscriber<T, ?, V> parent, UnicastProcessor<T> w) {
            this.parent = parent;
            this.f138w = w;
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(V t) {
            cancel();
            onComplete();
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.parent.error(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            this.parent.close(this);
        }
    }
}