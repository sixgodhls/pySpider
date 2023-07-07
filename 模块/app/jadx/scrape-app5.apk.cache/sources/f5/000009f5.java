package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.QueueDrainHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowablePublishMulticast<T, R> extends AbstractFlowableWithUpstream<T, R> {
    final boolean delayError;
    final int prefetch;
    final Function<? super Flowable<T>, ? extends Publisher<? extends R>> selector;

    public FlowablePublishMulticast(Flowable<T> source, Function<? super Flowable<T>, ? extends Publisher<? extends R>> selector, int prefetch, boolean delayError) {
        super(source);
        this.selector = selector;
        this.prefetch = prefetch;
        this.delayError = delayError;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super R> s) {
        MulticastProcessor<T> mp = new MulticastProcessor<>(this.prefetch, this.delayError);
        try {
            Publisher<? extends R> other = (Publisher) ObjectHelper.requireNonNull(this.selector.mo401apply(mp), "selector returned a null Publisher");
            OutputCanceller<R> out = new OutputCanceller<>(s, mp);
            other.subscribe(out);
            this.source.subscribe((FlowableSubscriber) mp);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            EmptySubscription.error(ex, s);
        }
    }

    /* loaded from: classes.dex */
    static final class OutputCanceller<R> implements FlowableSubscriber<R>, Subscription {
        final Subscriber<? super R> downstream;
        final MulticastProcessor<?> processor;
        Subscription upstream;

        OutputCanceller(Subscriber<? super R> actual, MulticastProcessor<?> processor) {
            this.downstream = actual;
            this.processor = processor;
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(R t) {
            this.downstream.onNext(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            this.downstream.onError(t);
            this.processor.dispose();
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.downstream.onComplete();
            this.processor.dispose();
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            this.upstream.request(n);
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.upstream.cancel();
            this.processor.dispose();
        }
    }

    /* loaded from: classes.dex */
    static final class MulticastProcessor<T> extends Flowable<T> implements FlowableSubscriber<T>, Disposable {
        static final MulticastSubscription[] EMPTY = new MulticastSubscription[0];
        static final MulticastSubscription[] TERMINATED = new MulticastSubscription[0];
        int consumed;
        final boolean delayError;
        volatile boolean done;
        Throwable error;
        final int limit;
        final int prefetch;
        volatile SimpleQueue<T> queue;
        int sourceMode;
        final AtomicInteger wip = new AtomicInteger();
        final AtomicReference<Subscription> upstream = new AtomicReference<>();
        final AtomicReference<MulticastSubscription<T>[]> subscribers = new AtomicReference<>(EMPTY);

        MulticastProcessor(int prefetch, boolean delayError) {
            this.prefetch = prefetch;
            this.limit = prefetch - (prefetch >> 2);
            this.delayError = delayError;
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this.upstream, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<T> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(3);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = qs;
                        this.done = true;
                        drain();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
                        this.queue = qs;
                        QueueDrainHelper.request(s, this.prefetch);
                        return;
                    }
                }
                this.queue = QueueDrainHelper.createQueue(this.prefetch);
                QueueDrainHelper.request(s, this.prefetch);
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            SimpleQueue<T> q;
            SubscriptionHelper.cancel(this.upstream);
            if (this.wip.getAndIncrement() == 0 && (q = this.queue) != null) {
                q.clear();
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return SubscriptionHelper.isCancelled(this.upstream.get());
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (this.done) {
                return;
            }
            if (this.sourceMode == 0 && !this.queue.offer(t)) {
                this.upstream.get().cancel();
                onError(new MissingBackpressureException());
                return;
            }
            drain();
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            this.done = true;
            drain();
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (!this.done) {
                this.done = true;
                drain();
            }
        }

        boolean add(MulticastSubscription<T> s) {
            MulticastSubscription<T>[] current;
            MulticastSubscription<T>[] next;
            do {
                current = this.subscribers.get();
                if (current == TERMINATED) {
                    return false;
                }
                int n = current.length;
                next = new MulticastSubscription[n + 1];
                System.arraycopy(current, 0, next, 0, n);
                next[n] = s;
            } while (!this.subscribers.compareAndSet(current, next));
            return true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        void remove(MulticastSubscription<T> s) {
            MulticastSubscription<T>[] current;
            MulticastSubscription<T>[] next;
            do {
                current = this.subscribers.get();
                int n = current.length;
                if (n == 0) {
                    return;
                }
                int j = -1;
                int i = 0;
                while (true) {
                    if (i >= n) {
                        break;
                    } else if (current[i] != s) {
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
                    next = EMPTY;
                } else {
                    MulticastSubscription<T>[] next2 = new MulticastSubscription[n - 1];
                    System.arraycopy(current, 0, next2, 0, j);
                    System.arraycopy(current, j + 1, next2, j, (n - j) - 1);
                    next = next2;
                }
            } while (!this.subscribers.compareAndSet(current, next));
        }

        @Override // io.reactivex.Flowable
        protected void subscribeActual(Subscriber<? super T> s) {
            MulticastSubscription<T> ms = new MulticastSubscription<>(s, this);
            s.onSubscribe(ms);
            if (add(ms)) {
                if (ms.isCancelled()) {
                    remove(ms);
                    return;
                } else {
                    drain();
                    return;
                }
            }
            Throwable ex = this.error;
            if (ex != null) {
                s.onError(ex);
            } else {
                s.onComplete();
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:100:0x0141, code lost:
            if (r0 == false) goto L113;
         */
        /* JADX WARN: Code restructure failed: missing block: B:102:0x0147, code lost:
            if (r2.isEmpty() == false) goto L113;
         */
        /* JADX WARN: Code restructure failed: missing block: B:104:0x0149, code lost:
            r7 = r28.error;
         */
        /* JADX WARN: Code restructure failed: missing block: B:105:0x014b, code lost:
            if (r7 == null) goto L108;
         */
        /* JADX WARN: Code restructure failed: missing block: B:106:0x014d, code lost:
            errorAll(r7);
         */
        /* JADX WARN: Code restructure failed: missing block: B:107:?, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:108:0x0151, code lost:
            completeAll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:109:0x0154, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:111:0x012d, code lost:
            r2.clear();
         */
        /* JADX WARN: Code restructure failed: missing block: B:112:0x0130, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:113:0x0155, code lost:
            r0 = r6;
         */
        /* JADX WARN: Code restructure failed: missing block: B:81:0x0109, code lost:
            r9 = r7;
            r0 = r6;
         */
        /* JADX WARN: Code restructure failed: missing block: B:88:0x0125, code lost:
            if (r14 != 0) goto L113;
         */
        /* JADX WARN: Code restructure failed: missing block: B:90:0x012b, code lost:
            if (isDisposed() == false) goto L91;
         */
        /* JADX WARN: Code restructure failed: missing block: B:91:0x0131, code lost:
            r0 = r28.done;
         */
        /* JADX WARN: Code restructure failed: missing block: B:92:0x0133, code lost:
            if (r0 == false) goto L100;
         */
        /* JADX WARN: Code restructure failed: missing block: B:94:0x0137, code lost:
            if (r28.delayError != false) goto L100;
         */
        /* JADX WARN: Code restructure failed: missing block: B:95:0x0139, code lost:
            r7 = r28.error;
         */
        /* JADX WARN: Code restructure failed: missing block: B:96:0x013b, code lost:
            if (r7 == null) goto L100;
         */
        /* JADX WARN: Code restructure failed: missing block: B:98:0x013d, code lost:
            errorAll(r7);
         */
        /* JADX WARN: Code restructure failed: missing block: B:99:0x0140, code lost:
            return;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void drain() {
            /*
                Method dump skipped, instructions count: 371
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowablePublishMulticast.MulticastProcessor.drain():void");
        }

        void errorAll(Throwable ex) {
            MulticastSubscription<T>[] andSet;
            for (MulticastSubscription<T> ms : this.subscribers.getAndSet(TERMINATED)) {
                if (ms.get() != Long.MIN_VALUE) {
                    ms.downstream.onError(ex);
                }
            }
        }

        void completeAll() {
            MulticastSubscription<T>[] andSet;
            for (MulticastSubscription<T> ms : this.subscribers.getAndSet(TERMINATED)) {
                if (ms.get() != Long.MIN_VALUE) {
                    ms.downstream.onComplete();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MulticastSubscription<T> extends AtomicLong implements Subscription {
        private static final long serialVersionUID = 8664815189257569791L;
        final Subscriber<? super T> downstream;
        long emitted;
        final MulticastProcessor<T> parent;

        MulticastSubscription(Subscriber<? super T> actual, MulticastProcessor<T> parent) {
            this.downstream = actual;
            this.parent = parent;
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this, n);
                this.parent.drain();
            }
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                this.parent.remove(this);
                this.parent.drain();
            }
        }

        public boolean isCancelled() {
            return get() == Long.MIN_VALUE;
        }
    }
}