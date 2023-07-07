package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.fuseable.HasUpstreamPublisher;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowablePublish<T> extends ConnectableFlowable<T> implements HasUpstreamPublisher<T> {
    static final long CANCELLED = Long.MIN_VALUE;
    final int bufferSize;
    final AtomicReference<PublishSubscriber<T>> current;
    final Publisher<T> onSubscribe;
    final Flowable<T> source;

    public static <T> ConnectableFlowable<T> create(Flowable<T> source, int bufferSize) {
        AtomicReference<PublishSubscriber<T>> curr = new AtomicReference<>();
        Publisher<T> onSubscribe = new FlowablePublisher<>(curr, bufferSize);
        return RxJavaPlugins.onAssembly((ConnectableFlowable) new FlowablePublish(onSubscribe, source, curr, bufferSize));
    }

    private FlowablePublish(Publisher<T> onSubscribe, Flowable<T> source, AtomicReference<PublishSubscriber<T>> current, int bufferSize) {
        this.onSubscribe = onSubscribe;
        this.source = source;
        this.current = current;
        this.bufferSize = bufferSize;
    }

    @Override // io.reactivex.internal.fuseable.HasUpstreamPublisher
    public Publisher<T> source() {
        return this.source;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super T> s) {
        this.onSubscribe.subscribe(s);
    }

    @Override // io.reactivex.flowables.ConnectableFlowable
    public void connect(Consumer<? super Disposable> connection) {
        PublishSubscriber<T> ps;
        while (true) {
            ps = this.current.get();
            if (ps != null && !ps.isDisposed()) {
                break;
            }
            PublishSubscriber<T> u = new PublishSubscriber<>(this.current, this.bufferSize);
            if (this.current.compareAndSet(ps, u)) {
                ps = u;
                break;
            }
        }
        boolean z = true;
        if (ps.shouldConnect.get() || !ps.shouldConnect.compareAndSet(false, true)) {
            z = false;
        }
        boolean doConnect = z;
        try {
            connection.accept(ps);
            if (doConnect) {
                this.source.subscribe((FlowableSubscriber) ps);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            throw ExceptionHelper.wrapOrThrow(ex);
        }
    }

    /* loaded from: classes.dex */
    static final class PublishSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Disposable {
        static final InnerSubscriber[] EMPTY = new InnerSubscriber[0];
        static final InnerSubscriber[] TERMINATED = new InnerSubscriber[0];
        private static final long serialVersionUID = -202316842419149694L;
        final int bufferSize;
        final AtomicReference<PublishSubscriber<T>> current;
        volatile SimpleQueue<T> queue;
        int sourceMode;
        volatile Object terminalEvent;
        final AtomicReference<Subscription> upstream = new AtomicReference<>();
        final AtomicReference<InnerSubscriber<T>[]> subscribers = new AtomicReference<>(EMPTY);
        final AtomicBoolean shouldConnect = new AtomicBoolean();

        PublishSubscriber(AtomicReference<PublishSubscriber<T>> current, int bufferSize) {
            this.current = current;
            this.bufferSize = bufferSize;
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            InnerSubscriber<T>[] innerSubscriberArr = this.subscribers.get();
            InnerSubscriber<T>[] innerSubscriberArr2 = TERMINATED;
            if (innerSubscriberArr != innerSubscriberArr2) {
                InnerSubscriber[] ps = this.subscribers.getAndSet(innerSubscriberArr2);
                if (ps != TERMINATED) {
                    this.current.compareAndSet(this, null);
                    SubscriptionHelper.cancel(this.upstream);
                }
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.subscribers.get() == TERMINATED;
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this.upstream, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<T> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(7);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = qs;
                        this.terminalEvent = NotificationLite.complete();
                        dispatch();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
                        this.queue = qs;
                        s.request(this.bufferSize);
                        return;
                    }
                }
                this.queue = new SpscArrayQueue(this.bufferSize);
                s.request(this.bufferSize);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (this.sourceMode == 0 && !this.queue.offer(t)) {
                onError(new MissingBackpressureException("Prefetch queue is full?!"));
            } else {
                dispatch();
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable e) {
            if (this.terminalEvent == null) {
                this.terminalEvent = NotificationLite.error(e);
                dispatch();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.terminalEvent == null) {
                this.terminalEvent = NotificationLite.complete();
                dispatch();
            }
        }

        boolean add(InnerSubscriber<T> producer) {
            InnerSubscriber<T>[] c;
            InnerSubscriber<T>[] u;
            do {
                c = this.subscribers.get();
                if (c == TERMINATED) {
                    return false;
                }
                int len = c.length;
                u = new InnerSubscriber[len + 1];
                System.arraycopy(c, 0, u, 0, len);
                u[len] = producer;
            } while (!this.subscribers.compareAndSet(c, u));
            return true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        void remove(InnerSubscriber<T> producer) {
            InnerSubscriber<T>[] c;
            InnerSubscriber<T>[] u;
            do {
                c = this.subscribers.get();
                int len = c.length;
                if (len != 0) {
                    int j = -1;
                    int i = 0;
                    while (true) {
                        if (i >= len) {
                            break;
                        } else if (!c[i].equals(producer)) {
                            i++;
                        } else {
                            j = i;
                            break;
                        }
                    }
                    if (j < 0) {
                        return;
                    }
                    if (len == 1) {
                        u = EMPTY;
                    } else {
                        InnerSubscriber<T>[] u2 = new InnerSubscriber[len - 1];
                        System.arraycopy(c, 0, u2, 0, j);
                        System.arraycopy(c, j + 1, u2, j, (len - j) - 1);
                        u = u2;
                    }
                } else {
                    return;
                }
            } while (!this.subscribers.compareAndSet(c, u));
        }

        boolean checkTerminated(Object term, boolean empty) {
            int i = 0;
            if (term != null) {
                if (NotificationLite.isComplete(term)) {
                    if (empty) {
                        this.current.compareAndSet(this, null);
                        InnerSubscriber<T>[] andSet = this.subscribers.getAndSet(TERMINATED);
                        int length = andSet.length;
                        while (i < length) {
                            andSet[i].child.onComplete();
                            i++;
                        }
                        return true;
                    }
                } else {
                    Throwable t = NotificationLite.getError(term);
                    this.current.compareAndSet(this, null);
                    InnerSubscriber[] a = this.subscribers.getAndSet(TERMINATED);
                    if (a.length != 0) {
                        int length2 = a.length;
                        while (i < length2) {
                            InnerSubscriber<?> ip = a[i];
                            ip.child.onError(t);
                            i++;
                        }
                    } else {
                        RxJavaPlugins.onError(t);
                    }
                    return true;
                }
            }
            return false;
        }

        void dispatch() {
            Object term;
            T v;
            SimpleQueue<T> q;
            Object term2;
            if (getAndIncrement() != 0) {
                return;
            }
            AtomicReference<InnerSubscriber<T>[]> subscribers = this.subscribers;
            InnerSubscriber<T>[] ps = subscribers.get();
            int missed = 1;
            while (true) {
                Object term3 = this.terminalEvent;
                SimpleQueue<T> q2 = this.queue;
                boolean empty = q2 == null || q2.isEmpty();
                if (checkTerminated(term3, empty)) {
                    return;
                }
                if (!empty) {
                    int len = ps.length;
                    int cancelled = 0;
                    long maxRequested = Long.MAX_VALUE;
                    for (InnerSubscriber<T> ip : ps) {
                        long r = ip.get();
                        if (r != FlowablePublish.CANCELLED) {
                            maxRequested = Math.min(maxRequested, r - ip.emitted);
                        } else {
                            cancelled++;
                        }
                    }
                    if (len == cancelled) {
                        Object term4 = this.terminalEvent;
                        try {
                            term = q2.mo399poll();
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            this.upstream.get().cancel();
                            term4 = NotificationLite.error(ex);
                            this.terminalEvent = term4;
                            term = null;
                        }
                        if (checkTerminated(term4, term == null)) {
                            return;
                        }
                        if (this.sourceMode != 1) {
                            this.upstream.get().request(1L);
                        }
                    } else {
                        int d = 0;
                        while (d < maxRequested) {
                            Object term5 = this.terminalEvent;
                            try {
                                v = q2.mo399poll();
                            } catch (Throwable ex2) {
                                Exceptions.throwIfFatal(ex2);
                                this.upstream.get().cancel();
                                term5 = NotificationLite.error(ex2);
                                this.terminalEvent = term5;
                                v = null;
                            }
                            empty = v == null;
                            if (checkTerminated(term5, empty)) {
                                return;
                            }
                            if (empty) {
                                break;
                            }
                            Object value = NotificationLite.getValue(v);
                            int length = ps.length;
                            boolean subscribersChanged = false;
                            int i = 0;
                            while (i < length) {
                                T v2 = v;
                                InnerSubscriber<T> ip2 = ps[i];
                                long ipr = ip2.get();
                                if (ipr != FlowablePublish.CANCELLED) {
                                    if (ipr == Long.MAX_VALUE) {
                                        q = q2;
                                        term2 = term5;
                                    } else {
                                        q = q2;
                                        term2 = term5;
                                        ip2.emitted++;
                                    }
                                    ip2.child.onNext(value);
                                } else {
                                    q = q2;
                                    term2 = term5;
                                    subscribersChanged = true;
                                }
                                i++;
                                v = v2;
                                q2 = q;
                                term5 = term2;
                            }
                            SimpleQueue<T> q3 = q2;
                            d++;
                            InnerSubscriber<T>[] freshArray = subscribers.get();
                            if (subscribersChanged || freshArray != ps) {
                                ps = freshArray;
                                break;
                            }
                            q2 = q3;
                        }
                        if (d > 0 && this.sourceMode != 1) {
                            this.upstream.get().request(d);
                        }
                        if (maxRequested != 0 && !empty) {
                        }
                    }
                }
                missed = addAndGet(-missed);
                if (missed != 0) {
                    InnerSubscriber<T>[] ps2 = subscribers.get();
                    ps = ps2;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class InnerSubscriber<T> extends AtomicLong implements Subscription {
        private static final long serialVersionUID = -4453897557930727610L;
        final Subscriber<? super T> child;
        long emitted;
        volatile PublishSubscriber<T> parent;

        InnerSubscriber(Subscriber<? super T> child) {
            this.child = child;
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this, n);
                PublishSubscriber<T> p = this.parent;
                if (p != null) {
                    p.dispatch();
                }
            }
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            PublishSubscriber<T> p;
            long r = get();
            if (r != FlowablePublish.CANCELLED) {
                long r2 = getAndSet(FlowablePublish.CANCELLED);
                if (r2 != FlowablePublish.CANCELLED && (p = this.parent) != null) {
                    p.remove(this);
                    p.dispatch();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class FlowablePublisher<T> implements Publisher<T> {
        private final int bufferSize;
        private final AtomicReference<PublishSubscriber<T>> curr;

        FlowablePublisher(AtomicReference<PublishSubscriber<T>> curr, int bufferSize) {
            this.curr = curr;
            this.bufferSize = bufferSize;
        }

        @Override // org.reactivestreams.Publisher
        public void subscribe(Subscriber<? super T> child) {
            PublishSubscriber<T> r;
            InnerSubscriber<T> inner = new InnerSubscriber<>(child);
            child.onSubscribe(inner);
            while (true) {
                r = this.curr.get();
                if (r == null || r.isDisposed()) {
                    PublishSubscriber<T> u = new PublishSubscriber<>(this.curr, this.bufferSize);
                    if (!this.curr.compareAndSet(r, u)) {
                        continue;
                    } else {
                        r = u;
                    }
                }
                if (r.add(inner)) {
                    break;
                }
            }
            if (inner.get() == FlowablePublish.CANCELLED) {
                r.remove(inner);
            } else {
                inner.parent = r;
            }
            r.dispatch();
        }
    }
}