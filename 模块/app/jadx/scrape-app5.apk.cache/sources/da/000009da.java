package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.functions.Function;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowableZip<T, R> extends Flowable<R> {
    final int bufferSize;
    final boolean delayError;
    final Publisher<? extends T>[] sources;
    final Iterable<? extends Publisher<? extends T>> sourcesIterable;
    final Function<? super Object[], ? extends R> zipper;

    public FlowableZip(Publisher<? extends T>[] sources, Iterable<? extends Publisher<? extends T>> sourcesIterable, Function<? super Object[], ? extends R> zipper, int bufferSize, boolean delayError) {
        this.sources = sources;
        this.sourcesIterable = sourcesIterable;
        this.zipper = zipper;
        this.bufferSize = bufferSize;
        this.delayError = delayError;
    }

    @Override // io.reactivex.Flowable
    public void subscribeActual(Subscriber<? super R> s) {
        Publisher<? extends T>[] sources = this.sources;
        int count = 0;
        if (sources == null) {
            sources = new Publisher[8];
            for (Publisher<? extends T> p : this.sourcesIterable) {
                if (count == sources.length) {
                    Publisher<? extends T>[] b = new Publisher[(count >> 2) + count];
                    System.arraycopy(sources, 0, b, 0, count);
                    sources = b;
                }
                sources[count] = p;
                count++;
            }
        } else {
            count = sources.length;
        }
        if (count == 0) {
            EmptySubscription.complete(s);
            return;
        }
        ZipCoordinator<T, R> coordinator = new ZipCoordinator<>(s, this.zipper, count, this.bufferSize, this.delayError);
        s.onSubscribe(coordinator);
        coordinator.subscribe(sources, count);
    }

    /* loaded from: classes.dex */
    static final class ZipCoordinator<T, R> extends AtomicInteger implements Subscription {
        private static final long serialVersionUID = -2434867452883857743L;
        volatile boolean cancelled;
        final Object[] current;
        final boolean delayErrors;
        final Subscriber<? super R> downstream;
        final AtomicThrowable errors;
        final AtomicLong requested;
        final ZipSubscriber<T, R>[] subscribers;
        final Function<? super Object[], ? extends R> zipper;

        ZipCoordinator(Subscriber<? super R> actual, Function<? super Object[], ? extends R> zipper, int n, int prefetch, boolean delayErrors) {
            this.downstream = actual;
            this.zipper = zipper;
            this.delayErrors = delayErrors;
            ZipSubscriber<T, R>[] a = new ZipSubscriber[n];
            for (int i = 0; i < n; i++) {
                a[i] = new ZipSubscriber<>(this, prefetch);
            }
            this.current = new Object[n];
            this.subscribers = a;
            this.requested = new AtomicLong();
            this.errors = new AtomicThrowable();
        }

        void subscribe(Publisher<? extends T>[] sources, int n) {
            ZipSubscriber<T, R>[] a = this.subscribers;
            for (int i = 0; i < n && !this.cancelled; i++) {
                if (!this.delayErrors && this.errors.get() != null) {
                    return;
                }
                sources[i].subscribe(a[i]);
            }
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
            if (!this.cancelled) {
                this.cancelled = true;
                cancelAll();
            }
        }

        void error(ZipSubscriber<T, R> inner, Throwable e) {
            if (this.errors.addThrowable(e)) {
                inner.done = true;
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        void cancelAll() {
            ZipSubscriber<T, R>[] zipSubscriberArr;
            for (ZipSubscriber<T, R> s : this.subscribers) {
                s.cancel();
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:100:0x0161, code lost:
            r12 = r12 + 1;
            r11 = null;
         */
        /* JADX WARN: Code restructure failed: missing block: B:102:0x011b, code lost:
            r16 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:103:0x0115, code lost:
            r15 = r11;
         */
        /* JADX WARN: Code restructure failed: missing block: B:104:0x0147, code lost:
            r0 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:105:0x0148, code lost:
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r19.errors.addThrowable(r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:106:0x0152, code lost:
            if (r19.delayErrors == false) goto L107;
         */
        /* JADX WARN: Code restructure failed: missing block: B:108:0x0154, code lost:
            cancelAll();
            r2.onError(r19.errors.terminate());
         */
        /* JADX WARN: Code restructure failed: missing block: B:109:0x0160, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:114:0x00e6, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:116:0x0169, code lost:
            if (r9 == 0) goto L124;
         */
        /* JADX WARN: Code restructure failed: missing block: B:117:0x016b, code lost:
            r0 = r3.length;
            r11 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:118:0x016d, code lost:
            if (r11 >= r0) goto L120;
         */
        /* JADX WARN: Code restructure failed: missing block: B:119:0x016f, code lost:
            r3[r11].request(r9);
            r11 = r11 + 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:122:0x017e, code lost:
            if (r7 == Long.MAX_VALUE) goto L124;
         */
        /* JADX WARN: Code restructure failed: missing block: B:123:0x0180, code lost:
            r19.requested.addAndGet(-r9);
         */
        /* JADX WARN: Code restructure failed: missing block: B:124:0x0186, code lost:
            r6 = addAndGet(-r6);
         */
        /* JADX WARN: Code restructure failed: missing block: B:67:0x00e0, code lost:
            if (r7 != r9) goto L115;
         */
        /* JADX WARN: Code restructure failed: missing block: B:69:0x00e4, code lost:
            if (r19.cancelled == false) goto L70;
         */
        /* JADX WARN: Code restructure failed: missing block: B:71:0x00e9, code lost:
            if (r19.delayErrors != false) goto L77;
         */
        /* JADX WARN: Code restructure failed: missing block: B:73:0x00f1, code lost:
            if (r19.errors.get() == null) goto L77;
         */
        /* JADX WARN: Code restructure failed: missing block: B:75:0x00f3, code lost:
            cancelAll();
            r2.onError(r19.errors.terminate());
         */
        /* JADX WARN: Code restructure failed: missing block: B:76:0x00ff, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:77:0x0100, code lost:
            r12 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:78:0x0102, code lost:
            if (r12 >= r4) goto L112;
         */
        /* JADX WARN: Code restructure failed: missing block: B:79:0x0104, code lost:
            r13 = r3[r12];
         */
        /* JADX WARN: Code restructure failed: missing block: B:80:0x0108, code lost:
            if (r5[r12] != null) goto L111;
         */
        /* JADX WARN: Code restructure failed: missing block: B:82:0x010a, code lost:
            r0 = r13.done;
            r14 = r13.queue;
         */
        /* JADX WARN: Code restructure failed: missing block: B:83:0x010e, code lost:
            if (r14 == null) goto L103;
         */
        /* JADX WARN: Code restructure failed: missing block: B:84:0x0110, code lost:
            r15 = r14.mo399poll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:85:0x0116, code lost:
            if (r15 != null) goto L102;
         */
        /* JADX WARN: Code restructure failed: missing block: B:86:0x0118, code lost:
            r16 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:87:0x011d, code lost:
            if (r0 == false) goto L96;
         */
        /* JADX WARN: Code restructure failed: missing block: B:88:0x011f, code lost:
            if (r16 == false) goto L96;
         */
        /* JADX WARN: Code restructure failed: missing block: B:90:0x0121, code lost:
            cancelAll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:91:0x012c, code lost:
            if (r19.errors.get() == null) goto L94;
         */
        /* JADX WARN: Code restructure failed: missing block: B:92:0x012e, code lost:
            r2.onError(r19.errors.terminate());
         */
        /* JADX WARN: Code restructure failed: missing block: B:93:?, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:94:0x013a, code lost:
            r2.onComplete();
         */
        /* JADX WARN: Code restructure failed: missing block: B:95:0x013f, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:97:0x0142, code lost:
            if (r16 != false) goto L101;
         */
        /* JADX WARN: Code restructure failed: missing block: B:98:0x0144, code lost:
            r5[r12] = r15;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void drain() {
            /*
                Method dump skipped, instructions count: 401
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableZip.ZipCoordinator.drain():void");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ZipSubscriber<T, R> extends AtomicReference<Subscription> implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -4627193790118206028L;
        volatile boolean done;
        final int limit;
        final ZipCoordinator<T, R> parent;
        final int prefetch;
        long produced;
        SimpleQueue<T> queue;
        int sourceMode;

        ZipSubscriber(ZipCoordinator<T, R> parent, int prefetch) {
            this.parent = parent;
            this.prefetch = prefetch;
            this.limit = prefetch - (prefetch >> 2);
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<T> f = (QueueSubscription) s;
                    int m = f.requestFusion(7);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = f;
                        this.done = true;
                        this.parent.drain();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
                        this.queue = f;
                        s.request(this.prefetch);
                        return;
                    }
                }
                this.queue = new SpscArrayQueue(this.prefetch);
                s.request(this.prefetch);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (this.sourceMode != 2) {
                this.queue.offer(t);
            }
            this.parent.drain();
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            this.parent.error(this, t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.done = true;
            this.parent.drain();
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            SubscriptionHelper.cancel(this);
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            if (this.sourceMode != 1) {
                long p = this.produced + n;
                if (p >= this.limit) {
                    this.produced = 0L;
                    get().request(p);
                    return;
                }
                this.produced = p;
            }
        }
    }
}