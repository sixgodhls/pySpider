package io.reactivex.internal.operators.parallel;

import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.parallel.ParallelFailureHandling;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class ParallelMapTry<T, R> extends ParallelFlowable<R> {
    final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
    final Function<? super T, ? extends R> mapper;
    final ParallelFlowable<T> source;

    public ParallelMapTry(ParallelFlowable<T> source, Function<? super T, ? extends R> mapper, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
        this.source = source;
        this.mapper = mapper;
        this.errorHandler = errorHandler;
    }

    @Override // io.reactivex.parallel.ParallelFlowable
    public void subscribe(Subscriber<? super R>[] subscribers) {
        if (!validate(subscribers)) {
            return;
        }
        int n = subscribers.length;
        Subscriber<? super T>[] parents = new Subscriber[n];
        for (int i = 0; i < n; i++) {
            Subscriber<? super R> a = subscribers[i];
            if (a instanceof ConditionalSubscriber) {
                parents[i] = new ParallelMapTryConditionalSubscriber((ConditionalSubscriber) a, this.mapper, this.errorHandler);
            } else {
                parents[i] = new ParallelMapTrySubscriber(a, this.mapper, this.errorHandler);
            }
        }
        this.source.subscribe(parents);
    }

    @Override // io.reactivex.parallel.ParallelFlowable
    public int parallelism() {
        return this.source.parallelism();
    }

    /* loaded from: classes.dex */
    static final class ParallelMapTrySubscriber<T, R> implements ConditionalSubscriber<T>, Subscription {
        boolean done;
        final Subscriber<? super R> downstream;
        final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
        final Function<? super T, ? extends R> mapper;
        Subscription upstream;

        ParallelMapTrySubscriber(Subscriber<? super R> actual, Function<? super T, ? extends R> mapper, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
            this.downstream = actual;
            this.mapper = mapper;
            this.errorHandler = errorHandler;
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            this.upstream.request(n);
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.upstream.cancel();
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (!tryOnNext(t) && !this.done) {
                this.upstream.request(1L);
            }
        }

        @Override // io.reactivex.internal.fuseable.ConditionalSubscriber
        public boolean tryOnNext(T t) {
            boolean z = false;
            if (this.done) {
                return false;
            }
            long retries = 0;
            while (true) {
                try {
                    this.downstream.onNext(ObjectHelper.requireNonNull(this.mapper.mo401apply(t), "The mapper returned a null value"));
                    return true;
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    try {
                        long j = 1 + retries;
                        retries = j;
                        ParallelFailureHandling h = (ParallelFailureHandling) ObjectHelper.requireNonNull(this.errorHandler.mo374apply(Long.valueOf(j), ex), "The errorHandler returned a null item");
                        switch (h) {
                            case RETRY:
                                break;
                            case SKIP:
                                return z;
                            case STOP:
                                cancel();
                                onComplete();
                                return z;
                            default:
                                cancel();
                                onError(ex);
                                return z;
                        }
                    } catch (Throwable exc) {
                        Exceptions.throwIfFatal(exc);
                        cancel();
                        Throwable[] thArr = new Throwable[2];
                        char c = z ? 1 : 0;
                        char c2 = z ? 1 : 0;
                        thArr[c] = ex;
                        thArr[1] = exc;
                        onError(new CompositeException(thArr));
                        return z;
                    }
                }
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.downstream.onError(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            this.downstream.onComplete();
        }
    }

    /* loaded from: classes.dex */
    static final class ParallelMapTryConditionalSubscriber<T, R> implements ConditionalSubscriber<T>, Subscription {
        boolean done;
        final ConditionalSubscriber<? super R> downstream;
        final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
        final Function<? super T, ? extends R> mapper;
        Subscription upstream;

        ParallelMapTryConditionalSubscriber(ConditionalSubscriber<? super R> actual, Function<? super T, ? extends R> mapper, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
            this.downstream = actual;
            this.mapper = mapper;
            this.errorHandler = errorHandler;
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            this.upstream.request(n);
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.upstream.cancel();
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (!tryOnNext(t) && !this.done) {
                this.upstream.request(1L);
            }
        }

        @Override // io.reactivex.internal.fuseable.ConditionalSubscriber
        public boolean tryOnNext(T t) {
            if (this.done) {
                return false;
            }
            long retries = 0;
            while (true) {
                try {
                    return this.downstream.tryOnNext(ObjectHelper.requireNonNull(this.mapper.mo401apply(t), "The mapper returned a null value"));
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    try {
                        long j = 1 + retries;
                        retries = j;
                        ParallelFailureHandling h = (ParallelFailureHandling) ObjectHelper.requireNonNull(this.errorHandler.mo374apply(Long.valueOf(j), ex), "The errorHandler returned a null item");
                        switch (h) {
                            case RETRY:
                                break;
                            case SKIP:
                                return false;
                            case STOP:
                                cancel();
                                onComplete();
                                return false;
                            default:
                                cancel();
                                onError(ex);
                                return false;
                        }
                    } catch (Throwable exc) {
                        Exceptions.throwIfFatal(exc);
                        cancel();
                        onError(new CompositeException(ex, exc));
                        return false;
                    }
                }
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.downstream.onError(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            this.downstream.onComplete();
        }
    }
}