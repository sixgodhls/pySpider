package io.reactivex.processors;

import io.reactivex.annotations.BackpressureKind;
import io.reactivex.annotations.BackpressureSupport;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@SchedulerSupport(SchedulerSupport.NONE)
@BackpressureSupport(BackpressureKind.FULL)
/* loaded from: classes.dex */
public final class MulticastProcessor<T> extends FlowableProcessor<T> {
    static final MulticastSubscription[] EMPTY = new MulticastSubscription[0];
    static final MulticastSubscription[] TERMINATED = new MulticastSubscription[0];
    final int bufferSize;
    int consumed;
    volatile boolean done;
    volatile Throwable error;
    int fusionMode;
    final int limit;
    volatile SimpleQueue<T> queue;
    final boolean refcount;
    final AtomicInteger wip = new AtomicInteger();
    final AtomicReference<MulticastSubscription<T>[]> subscribers = new AtomicReference<>(EMPTY);
    final AtomicReference<Subscription> upstream = new AtomicReference<>();
    final AtomicBoolean once = new AtomicBoolean();

    @CheckReturnValue
    @NonNull
    public static <T> MulticastProcessor<T> create() {
        return new MulticastProcessor<>(bufferSize(), false);
    }

    @CheckReturnValue
    @NonNull
    public static <T> MulticastProcessor<T> create(boolean refCount) {
        return new MulticastProcessor<>(bufferSize(), refCount);
    }

    @CheckReturnValue
    @NonNull
    public static <T> MulticastProcessor<T> create(int bufferSize) {
        return new MulticastProcessor<>(bufferSize, false);
    }

    @CheckReturnValue
    @NonNull
    public static <T> MulticastProcessor<T> create(int bufferSize, boolean refCount) {
        return new MulticastProcessor<>(bufferSize, refCount);
    }

    MulticastProcessor(int bufferSize, boolean refCount) {
        ObjectHelper.verifyPositive(bufferSize, "bufferSize");
        this.bufferSize = bufferSize;
        this.limit = bufferSize - (bufferSize >> 2);
        this.refcount = refCount;
    }

    public void start() {
        if (SubscriptionHelper.setOnce(this.upstream, EmptySubscription.INSTANCE)) {
            this.queue = new SpscArrayQueue(this.bufferSize);
        }
    }

    public void startUnbounded() {
        if (SubscriptionHelper.setOnce(this.upstream, EmptySubscription.INSTANCE)) {
            this.queue = new SpscLinkedArrayQueue(this.bufferSize);
        }
    }

    @Override // org.reactivestreams.Subscriber
    public void onSubscribe(Subscription s) {
        if (SubscriptionHelper.setOnce(this.upstream, s)) {
            if (s instanceof QueueSubscription) {
                QueueSubscription<T> qs = (QueueSubscription) s;
                int m = qs.requestFusion(3);
                if (m == 1) {
                    this.fusionMode = m;
                    this.queue = qs;
                    this.done = true;
                    drain();
                    return;
                } else if (m == 2) {
                    this.fusionMode = m;
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
        if (this.once.get()) {
            return;
        }
        if (this.fusionMode == 0) {
            ObjectHelper.requireNonNull(t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
            if (!this.queue.offer(t)) {
                SubscriptionHelper.cancel(this.upstream);
                onError(new MissingBackpressureException());
                return;
            }
        }
        drain();
    }

    public boolean offer(T t) {
        if (this.once.get()) {
            return false;
        }
        ObjectHelper.requireNonNull(t, "offer called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.fusionMode != 0 || !this.queue.offer(t)) {
            return false;
        }
        drain();
        return true;
    }

    @Override // org.reactivestreams.Subscriber
    public void onError(Throwable t) {
        ObjectHelper.requireNonNull(t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.once.compareAndSet(false, true)) {
            this.error = t;
            this.done = true;
            drain();
            return;
        }
        RxJavaPlugins.onError(t);
    }

    @Override // org.reactivestreams.Subscriber
    public void onComplete() {
        if (this.once.compareAndSet(false, true)) {
            this.done = true;
            drain();
        }
    }

    @Override // io.reactivex.processors.FlowableProcessor
    public boolean hasSubscribers() {
        return this.subscribers.get().length != 0;
    }

    @Override // io.reactivex.processors.FlowableProcessor
    public boolean hasThrowable() {
        return this.once.get() && this.error != null;
    }

    @Override // io.reactivex.processors.FlowableProcessor
    public boolean hasComplete() {
        return this.once.get() && this.error == null;
    }

    @Override // io.reactivex.processors.FlowableProcessor
    public Throwable getThrowable() {
        if (this.once.get()) {
            return this.error;
        }
        return null;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super T> s) {
        Throwable ex;
        MulticastSubscription<T> ms = new MulticastSubscription<>(s, this);
        s.onSubscribe(ms);
        if (add(ms)) {
            if (ms.get() == Long.MIN_VALUE) {
                remove(ms);
            } else {
                drain();
            }
        } else if ((this.once.get() || !this.refcount) && (ex = this.error) != null) {
            s.onError(ex);
        } else {
            s.onComplete();
        }
    }

    boolean add(MulticastSubscription<T> inner) {
        MulticastSubscription<T>[] a;
        MulticastSubscription<T>[] b;
        do {
            a = this.subscribers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            b = new MulticastSubscription[n + 1];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = inner;
        } while (!this.subscribers.compareAndSet(a, b));
        return true;
    }

    void remove(MulticastSubscription<T> inner) {
        while (true) {
            MulticastSubscription<T>[] a = this.subscribers.get();
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
            if (j >= 0) {
                if (n == 1) {
                    if (this.refcount) {
                        if (this.subscribers.compareAndSet(a, TERMINATED)) {
                            SubscriptionHelper.cancel(this.upstream);
                            this.once.set(true);
                            return;
                        }
                    } else if (this.subscribers.compareAndSet(a, EMPTY)) {
                        return;
                    }
                } else {
                    MulticastSubscription<T>[] b = new MulticastSubscription[n - 1];
                    System.arraycopy(a, 0, b, 0, j);
                    System.arraycopy(a, j + 1, b, j, (n - j) - 1);
                    if (this.subscribers.compareAndSet(a, b)) {
                        return;
                    }
                }
            } else {
                return;
            }
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:27:0x005e */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void drain() {
        /*
            Method dump skipped, instructions count: 362
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.MulticastProcessor.drain():void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MulticastSubscription<T> extends AtomicLong implements Subscription {
        private static final long serialVersionUID = -363282618957264509L;
        final Subscriber<? super T> downstream;
        long emitted;
        final MulticastProcessor<T> parent;

        MulticastSubscription(Subscriber<? super T> actual, MulticastProcessor<T> parent) {
            this.downstream = actual;
            this.parent = parent;
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            long r;
            long u;
            if (SubscriptionHelper.validate(n)) {
                do {
                    r = get();
                    if (r != Long.MIN_VALUE && r != Long.MAX_VALUE) {
                        u = r + n;
                        if (u < 0) {
                            u = Long.MAX_VALUE;
                        }
                    } else {
                        return;
                    }
                } while (!compareAndSet(r, u));
                this.parent.drain();
            }
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                this.parent.remove(this);
            }
        }

        void onNext(T t) {
            if (get() != Long.MIN_VALUE) {
                this.emitted++;
                this.downstream.onNext(t);
            }
        }

        void onError(Throwable t) {
            if (get() != Long.MIN_VALUE) {
                this.downstream.onError(t);
            }
        }

        void onComplete() {
            if (get() != Long.MIN_VALUE) {
                this.downstream.onComplete();
            }
        }
    }
}
