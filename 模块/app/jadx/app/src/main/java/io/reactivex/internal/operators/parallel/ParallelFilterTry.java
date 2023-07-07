package io.reactivex.internal.operators.parallel;

import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.parallel.ParallelFailureHandling;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class ParallelFilterTry<T> extends ParallelFlowable<T> {
    final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
    final Predicate<? super T> predicate;
    final ParallelFlowable<T> source;

    public ParallelFilterTry(ParallelFlowable<T> source, Predicate<? super T> predicate, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
        this.source = source;
        this.predicate = predicate;
        this.errorHandler = errorHandler;
    }

    @Override // io.reactivex.parallel.ParallelFlowable
    public void subscribe(Subscriber<? super T>[] subscribers) {
        if (!validate(subscribers)) {
            return;
        }
        int n = subscribers.length;
        Subscriber<? super T>[] parents = new Subscriber[n];
        for (int i = 0; i < n; i++) {
            Subscriber<? super T> a = subscribers[i];
            if (a instanceof ConditionalSubscriber) {
                parents[i] = new ParallelFilterConditionalSubscriber((ConditionalSubscriber) a, this.predicate, this.errorHandler);
            } else {
                parents[i] = new ParallelFilterSubscriber(a, this.predicate, this.errorHandler);
            }
        }
        this.source.subscribe(parents);
    }

    @Override // io.reactivex.parallel.ParallelFlowable
    public int parallelism() {
        return this.source.parallelism();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class BaseFilterSubscriber<T> implements ConditionalSubscriber<T>, Subscription {
        boolean done;
        final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
        final Predicate<? super T> predicate;
        Subscription upstream;

        BaseFilterSubscriber(Predicate<? super T> predicate, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
            this.predicate = predicate;
            this.errorHandler = errorHandler;
        }

        @Override // org.reactivestreams.Subscription
        public final void request(long n) {
            this.upstream.request(n);
        }

        @Override // org.reactivestreams.Subscription
        public final void cancel() {
            this.upstream.cancel();
        }

        @Override // org.reactivestreams.Subscriber
        public final void onNext(T t) {
            if (!tryOnNext(t) && !this.done) {
                this.upstream.request(1L);
            }
        }
    }

    /* loaded from: classes.dex */
    static final class ParallelFilterSubscriber<T> extends BaseFilterSubscriber<T> {
        final Subscriber<? super T> downstream;

        ParallelFilterSubscriber(Subscriber<? super T> actual, Predicate<? super T> predicate, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
            super(predicate, errorHandler);
            this.downstream = actual;
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        @Override // io.reactivex.internal.fuseable.ConditionalSubscriber
        public boolean tryOnNext(T t) {
            if (!this.done) {
                long retries = 0;
                while (true) {
                    try {
                        boolean b = this.predicate.test(t);
                        if (!b) {
                            return false;
                        }
                        this.downstream.onNext(t);
                        return true;
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        try {
                            long j = 1 + retries;
                            retries = j;
                            ParallelFailureHandling h = (ParallelFailureHandling) ObjectHelper.requireNonNull(this.errorHandler.mo312apply(Long.valueOf(j), ex), "The errorHandler returned a null item");
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
            } else {
                return false;
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
            if (!this.done) {
                this.done = true;
                this.downstream.onComplete();
            }
        }
    }

    /* loaded from: classes.dex */
    static final class ParallelFilterConditionalSubscriber<T> extends BaseFilterSubscriber<T> {
        final ConditionalSubscriber<? super T> downstream;

        ParallelFilterConditionalSubscriber(ConditionalSubscriber<? super T> actual, Predicate<? super T> predicate, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
            super(predicate, errorHandler);
            this.downstream = actual;
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        @Override // io.reactivex.internal.fuseable.ConditionalSubscriber
        public boolean tryOnNext(T t) {
            if (!this.done) {
                long retries = 0;
                while (true) {
                    try {
                        boolean b = this.predicate.test(t);
                        return b && this.downstream.tryOnNext(t);
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        try {
                            long j = 1 + retries;
                            retries = j;
                            ParallelFailureHandling h = (ParallelFailureHandling) ObjectHelper.requireNonNull(this.errorHandler.mo312apply(Long.valueOf(j), ex), "The errorHandler returned a null item");
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
            } else {
                return false;
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
            if (!this.done) {
                this.done = true;
                this.downstream.onComplete();
            }
        }
    }
}
