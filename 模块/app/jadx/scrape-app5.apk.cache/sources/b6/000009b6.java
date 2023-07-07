package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.LinkedArrayList;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowableCache<T> extends AbstractFlowableWithUpstream<T, T> {
    final AtomicBoolean once = new AtomicBoolean();
    final CacheState<T> state;

    public FlowableCache(Flowable<T> source, int capacityHint) {
        super(source);
        this.state = new CacheState<>(source, capacityHint);
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super T> t) {
        ReplaySubscription<T> rp = new ReplaySubscription<>(t, this.state);
        t.onSubscribe(rp);
        boolean doReplay = true;
        if (this.state.addChild(rp) && rp.requested.get() == Long.MIN_VALUE) {
            this.state.removeChild(rp);
            doReplay = false;
        }
        if (!this.once.get() && this.once.compareAndSet(false, true)) {
            this.state.connect();
        }
        if (doReplay) {
            rp.replay();
        }
    }

    boolean isConnected() {
        return this.state.isConnected;
    }

    boolean hasSubscribers() {
        return this.state.subscribers.get().length != 0;
    }

    int cachedEventCount() {
        return this.state.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class CacheState<T> extends LinkedArrayList implements FlowableSubscriber<T> {
        static final ReplaySubscription[] EMPTY = new ReplaySubscription[0];
        static final ReplaySubscription[] TERMINATED = new ReplaySubscription[0];
        volatile boolean isConnected;
        final Flowable<T> source;
        boolean sourceDone;
        final AtomicReference<Subscription> connection = new AtomicReference<>();
        final AtomicReference<ReplaySubscription<T>[]> subscribers = new AtomicReference<>(EMPTY);

        CacheState(Flowable<T> source, int capacityHint) {
            super(capacityHint);
            this.source = source;
        }

        public boolean addChild(ReplaySubscription<T> p) {
            ReplaySubscription<T>[] a;
            ReplaySubscription<T>[] b;
            do {
                a = this.subscribers.get();
                if (a == TERMINATED) {
                    return false;
                }
                int n = a.length;
                b = new ReplaySubscription[n + 1];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = p;
            } while (!this.subscribers.compareAndSet(a, b));
            return true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void removeChild(ReplaySubscription<T> p) {
            ReplaySubscription<T>[] a;
            ReplaySubscription<T>[] b;
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
                    } else if (!a[i].equals(p)) {
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
                    ReplaySubscription<T>[] b2 = new ReplaySubscription[n - 1];
                    System.arraycopy(a, 0, b2, 0, j);
                    System.arraycopy(a, j + 1, b2, j, (n - j) - 1);
                    b = b2;
                }
            } while (!this.subscribers.compareAndSet(a, b));
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this.connection, s, Long.MAX_VALUE);
        }

        public void connect() {
            this.source.subscribe((FlowableSubscriber) this);
            this.isConnected = true;
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (!this.sourceDone) {
                Object o = NotificationLite.next(t);
                add(o);
                for (ReplaySubscription<T> replaySubscription : this.subscribers.get()) {
                    replaySubscription.replay();
                }
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable e) {
            if (!this.sourceDone) {
                this.sourceDone = true;
                Object o = NotificationLite.error(e);
                add(o);
                SubscriptionHelper.cancel(this.connection);
                for (ReplaySubscription<T> replaySubscription : this.subscribers.getAndSet(TERMINATED)) {
                    replaySubscription.replay();
                }
                return;
            }
            RxJavaPlugins.onError(e);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (!this.sourceDone) {
                this.sourceDone = true;
                Object o = NotificationLite.complete();
                add(o);
                SubscriptionHelper.cancel(this.connection);
                for (ReplaySubscription<T> replaySubscription : this.subscribers.getAndSet(TERMINATED)) {
                    replaySubscription.replay();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplaySubscription<T> extends AtomicInteger implements Subscription {
        private static final long CANCELLED = Long.MIN_VALUE;
        private static final long serialVersionUID = -2557562030197141021L;
        final Subscriber<? super T> child;
        Object[] currentBuffer;
        int currentIndexInBuffer;
        long emitted;
        int index;
        final AtomicLong requested = new AtomicLong();
        final CacheState<T> state;

        ReplaySubscription(Subscriber<? super T> child, CacheState<T> state) {
            this.child = child;
            this.state = state;
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this.requested, n);
                replay();
            }
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            if (this.requested.getAndSet(CANCELLED) != CANCELLED) {
                this.state.removeChild(this);
            }
        }

        public void replay() {
            if (getAndIncrement() != 0) {
                return;
            }
            int missed = 1;
            Subscriber<? super T> child = this.child;
            AtomicLong rq = this.requested;
            long e = this.emitted;
            do {
                long r = rq.get();
                if (r == CANCELLED) {
                    return;
                }
                int s = this.state.size();
                if (s != 0) {
                    Object[] b = this.currentBuffer;
                    if (b == null) {
                        b = this.state.head();
                        this.currentBuffer = b;
                    }
                    int n = b.length - 1;
                    int j = this.index;
                    int k = this.currentIndexInBuffer;
                    while (j < s && e != r) {
                        if (rq.get() == CANCELLED) {
                            return;
                        }
                        if (k == n) {
                            b = (Object[]) b[n];
                            k = 0;
                        }
                        if (NotificationLite.accept(b[k], child)) {
                            return;
                        }
                        k++;
                        j++;
                        e++;
                    }
                    if (rq.get() == CANCELLED) {
                        return;
                    }
                    if (r == e) {
                        Object o = b[k];
                        if (NotificationLite.isComplete(o)) {
                            child.onComplete();
                            return;
                        } else if (NotificationLite.isError(o)) {
                            child.onError(NotificationLite.getError(o));
                            return;
                        }
                    }
                    this.index = j;
                    this.currentIndexInBuffer = k;
                    this.currentBuffer = b;
                }
                this.emitted = e;
                missed = addAndGet(-missed);
            } while (missed != 0);
        }
    }
}