package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.subscriptions.BasicQueueSubscription;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import java.util.Iterator;
import org.reactivestreams.Subscriber;

/* loaded from: classes.dex */
public final class FlowableFromIterable<T> extends Flowable<T> {
    final Iterable<? extends T> source;

    public FlowableFromIterable(Iterable<? extends T> source) {
        this.source = source;
    }

    @Override // io.reactivex.Flowable
    public void subscribeActual(Subscriber<? super T> s) {
        try {
            Iterator<? extends T> it = this.source.iterator();
            subscribe(s, it);
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            EmptySubscription.error(e, s);
        }
    }

    public static <T> void subscribe(Subscriber<? super T> s, Iterator<? extends T> it) {
        try {
            boolean hasNext = it.hasNext();
            if (!hasNext) {
                EmptySubscription.complete(s);
            } else if (s instanceof ConditionalSubscriber) {
                s.onSubscribe(new IteratorConditionalSubscription((ConditionalSubscriber) s, it));
            } else {
                s.onSubscribe(new IteratorSubscription(s, it));
            }
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            EmptySubscription.error(e, s);
        }
    }

    /* loaded from: classes.dex */
    static abstract class BaseRangeSubscription<T> extends BasicQueueSubscription<T> {
        private static final long serialVersionUID = -2252972430506210021L;
        volatile boolean cancelled;
        Iterator<? extends T> it;
        boolean once;

        abstract void fastPath();

        abstract void slowPath(long j);

        BaseRangeSubscription(Iterator<? extends T> it) {
            this.it = it;
        }

        @Override // io.reactivex.internal.fuseable.QueueFuseable
        public final int requestFusion(int mode) {
            return mode & 1;
        }

        @Override // io.reactivex.internal.fuseable.SimpleQueue
        @Nullable
        /* renamed from: poll */
        public final T mo337poll() {
            Iterator<? extends T> it = this.it;
            if (it == null) {
                return null;
            }
            if (!this.once) {
                this.once = true;
            } else if (!it.hasNext()) {
                return null;
            }
            return (T) ObjectHelper.requireNonNull(this.it.next(), "Iterator.next() returned a null value");
        }

        @Override // io.reactivex.internal.fuseable.SimpleQueue
        public final boolean isEmpty() {
            Iterator<? extends T> it = this.it;
            return it == null || !it.hasNext();
        }

        @Override // io.reactivex.internal.fuseable.SimpleQueue
        public final void clear() {
            this.it = null;
        }

        @Override // org.reactivestreams.Subscription
        public final void request(long n) {
            if (SubscriptionHelper.validate(n) && BackpressureHelper.add(this, n) == 0) {
                if (n == Long.MAX_VALUE) {
                    fastPath();
                } else {
                    slowPath(n);
                }
            }
        }

        @Override // org.reactivestreams.Subscription
        public final void cancel() {
            this.cancelled = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class IteratorSubscription<T> extends BaseRangeSubscription<T> {
        private static final long serialVersionUID = -6022804456014692607L;
        final Subscriber<? super T> downstream;

        IteratorSubscription(Subscriber<? super T> actual, Iterator<? extends T> it) {
            super(it);
            this.downstream = actual;
        }

        @Override // io.reactivex.internal.operators.flowable.FlowableFromIterable.BaseRangeSubscription
        void fastPath() {
            Iterator<? extends T> it = this.it;
            Subscriber<? super T> a = this.downstream;
            while (!this.cancelled) {
                try {
                    Object obj = (T) it.next();
                    if (this.cancelled) {
                        return;
                    }
                    if (obj == null) {
                        a.onError(new NullPointerException("Iterator.next() returned a null value"));
                        return;
                    }
                    a.onNext(obj);
                    if (this.cancelled) {
                        return;
                    }
                    try {
                        boolean b = it.hasNext();
                        if (!b) {
                            if (!this.cancelled) {
                                a.onComplete();
                                return;
                            }
                            return;
                        }
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        a.onError(ex);
                        return;
                    }
                } catch (Throwable ex2) {
                    Exceptions.throwIfFatal(ex2);
                    a.onError(ex2);
                    return;
                }
            }
        }

        @Override // io.reactivex.internal.operators.flowable.FlowableFromIterable.BaseRangeSubscription
        void slowPath(long r) {
            long e = 0;
            Iterator<? extends T> it = this.it;
            Subscriber<? super T> a = this.downstream;
            while (true) {
                if (e != r) {
                    if (this.cancelled) {
                        return;
                    }
                    try {
                        Object obj = (T) it.next();
                        if (this.cancelled) {
                            return;
                        }
                        if (obj == null) {
                            a.onError(new NullPointerException("Iterator.next() returned a null value"));
                            return;
                        }
                        a.onNext(obj);
                        if (this.cancelled) {
                            return;
                        }
                        try {
                            boolean b = it.hasNext();
                            if (!b) {
                                if (!this.cancelled) {
                                    a.onComplete();
                                    return;
                                }
                                return;
                            }
                            e++;
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            a.onError(ex);
                            return;
                        }
                    } catch (Throwable ex2) {
                        Exceptions.throwIfFatal(ex2);
                        a.onError(ex2);
                        return;
                    }
                } else {
                    r = get();
                    if (e == r) {
                        r = addAndGet(-e);
                        if (r == 0) {
                            return;
                        }
                        e = 0;
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class IteratorConditionalSubscription<T> extends BaseRangeSubscription<T> {
        private static final long serialVersionUID = -6022804456014692607L;
        final ConditionalSubscriber<? super T> downstream;

        IteratorConditionalSubscription(ConditionalSubscriber<? super T> actual, Iterator<? extends T> it) {
            super(it);
            this.downstream = actual;
        }

        @Override // io.reactivex.internal.operators.flowable.FlowableFromIterable.BaseRangeSubscription
        void fastPath() {
            Iterator<? extends T> it = this.it;
            ConditionalSubscriber<? super T> a = this.downstream;
            while (!this.cancelled) {
                try {
                    Object obj = (T) it.next();
                    if (this.cancelled) {
                        return;
                    }
                    if (obj == null) {
                        a.onError(new NullPointerException("Iterator.next() returned a null value"));
                        return;
                    }
                    a.tryOnNext(obj);
                    if (this.cancelled) {
                        return;
                    }
                    try {
                        boolean b = it.hasNext();
                        if (!b) {
                            if (!this.cancelled) {
                                a.onComplete();
                                return;
                            }
                            return;
                        }
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        a.onError(ex);
                        return;
                    }
                } catch (Throwable ex2) {
                    Exceptions.throwIfFatal(ex2);
                    a.onError(ex2);
                    return;
                }
            }
        }

        @Override // io.reactivex.internal.operators.flowable.FlowableFromIterable.BaseRangeSubscription
        void slowPath(long r) {
            long e = 0;
            Iterator<? extends T> it = this.it;
            ConditionalSubscriber<? super T> a = this.downstream;
            while (true) {
                if (e != r) {
                    if (this.cancelled) {
                        return;
                    }
                    try {
                        Object obj = (T) it.next();
                        if (this.cancelled) {
                            return;
                        }
                        if (obj == null) {
                            a.onError(new NullPointerException("Iterator.next() returned a null value"));
                            return;
                        }
                        boolean b = a.tryOnNext(obj);
                        if (this.cancelled) {
                            return;
                        }
                        try {
                            boolean hasNext = it.hasNext();
                            if (!hasNext) {
                                if (!this.cancelled) {
                                    a.onComplete();
                                    return;
                                }
                                return;
                            } else if (b) {
                                e++;
                            }
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            a.onError(ex);
                            return;
                        }
                    } catch (Throwable ex2) {
                        Exceptions.throwIfFatal(ex2);
                        a.onError(ex2);
                        return;
                    }
                } else {
                    r = get();
                    if (e == r) {
                        r = addAndGet(-e);
                        if (r == 0) {
                            return;
                        }
                        e = 0;
                    } else {
                        continue;
                    }
                }
            }
        }
    }
}
