package io.reactivex.parallel;

import android.support.v7.widget.ActivityChooserView;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.BackpressureKind;
import io.reactivex.annotations.BackpressureSupport;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.LongConsumer;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.operators.parallel.ParallelCollect;
import io.reactivex.internal.operators.parallel.ParallelConcatMap;
import io.reactivex.internal.operators.parallel.ParallelDoOnNextTry;
import io.reactivex.internal.operators.parallel.ParallelFilter;
import io.reactivex.internal.operators.parallel.ParallelFilterTry;
import io.reactivex.internal.operators.parallel.ParallelFlatMap;
import io.reactivex.internal.operators.parallel.ParallelFromArray;
import io.reactivex.internal.operators.parallel.ParallelFromPublisher;
import io.reactivex.internal.operators.parallel.ParallelJoin;
import io.reactivex.internal.operators.parallel.ParallelMap;
import io.reactivex.internal.operators.parallel.ParallelMapTry;
import io.reactivex.internal.operators.parallel.ParallelPeek;
import io.reactivex.internal.operators.parallel.ParallelReduce;
import io.reactivex.internal.operators.parallel.ParallelReduceFull;
import io.reactivex.internal.operators.parallel.ParallelRunOn;
import io.reactivex.internal.operators.parallel.ParallelSortedJoin;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.util.ErrorMode;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.ListAddBiConsumer;
import io.reactivex.internal.util.MergerBiFunction;
import io.reactivex.internal.util.SorterFunction;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public abstract class ParallelFlowable<T> {
    public abstract int parallelism();

    public abstract void subscribe(@NonNull Subscriber<? super T>[] subscriberArr);

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean validate(@NonNull Subscriber<?>[] subscribers) {
        int p = parallelism();
        if (subscribers.length != p) {
            Throwable iae = new IllegalArgumentException("parallelism = " + p + ", subscribers = " + subscribers.length);
            for (Subscriber<?> s : subscribers) {
                EmptySubscription.error(iae, s);
            }
            return false;
        }
        return true;
    }

    @CheckReturnValue
    public static <T> ParallelFlowable<T> from(@NonNull Publisher<? extends T> source) {
        return from(source, Runtime.getRuntime().availableProcessors(), Flowable.bufferSize());
    }

    @CheckReturnValue
    public static <T> ParallelFlowable<T> from(@NonNull Publisher<? extends T> source, int parallelism) {
        return from(source, parallelism, Flowable.bufferSize());
    }

    @CheckReturnValue
    @NonNull
    public static <T> ParallelFlowable<T> from(@NonNull Publisher<? extends T> source, int parallelism, int prefetch) {
        ObjectHelper.requireNonNull(source, "source");
        ObjectHelper.verifyPositive(parallelism, "parallelism");
        ObjectHelper.verifyPositive(prefetch, "prefetch");
        return RxJavaPlugins.onAssembly(new ParallelFromPublisher(source, parallelism, prefetch));
    }

    @CheckReturnValue
    @NonNull
    public final <R> R as(@NonNull ParallelFlowableConverter<T, R> converter) {
        return (R) ((ParallelFlowableConverter) ObjectHelper.requireNonNull(converter, "converter is null")).apply(this);
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> map(@NonNull Function<? super T, ? extends R> mapper) {
        ObjectHelper.requireNonNull(mapper, "mapper");
        return RxJavaPlugins.onAssembly(new ParallelMap(this, mapper));
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> map(@NonNull Function<? super T, ? extends R> mapper, @NonNull ParallelFailureHandling errorHandler) {
        ObjectHelper.requireNonNull(mapper, "mapper");
        ObjectHelper.requireNonNull(errorHandler, "errorHandler is null");
        return RxJavaPlugins.onAssembly(new ParallelMapTry(this, mapper, errorHandler));
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> map(@NonNull Function<? super T, ? extends R> mapper, @NonNull BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
        ObjectHelper.requireNonNull(mapper, "mapper");
        ObjectHelper.requireNonNull(errorHandler, "errorHandler is null");
        return RxJavaPlugins.onAssembly(new ParallelMapTry(this, mapper, errorHandler));
    }

    @CheckReturnValue
    public final ParallelFlowable<T> filter(@NonNull Predicate<? super T> predicate) {
        ObjectHelper.requireNonNull(predicate, "predicate");
        return RxJavaPlugins.onAssembly(new ParallelFilter(this, predicate));
    }

    @CheckReturnValue
    public final ParallelFlowable<T> filter(@NonNull Predicate<? super T> predicate, @NonNull ParallelFailureHandling errorHandler) {
        ObjectHelper.requireNonNull(predicate, "predicate");
        ObjectHelper.requireNonNull(errorHandler, "errorHandler is null");
        return RxJavaPlugins.onAssembly(new ParallelFilterTry(this, predicate, errorHandler));
    }

    @CheckReturnValue
    public final ParallelFlowable<T> filter(@NonNull Predicate<? super T> predicate, @NonNull BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
        ObjectHelper.requireNonNull(predicate, "predicate");
        ObjectHelper.requireNonNull(errorHandler, "errorHandler is null");
        return RxJavaPlugins.onAssembly(new ParallelFilterTry(this, predicate, errorHandler));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> runOn(@NonNull Scheduler scheduler) {
        return runOn(scheduler, Flowable.bufferSize());
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> runOn(@NonNull Scheduler scheduler, int prefetch) {
        ObjectHelper.requireNonNull(scheduler, "scheduler");
        ObjectHelper.verifyPositive(prefetch, "prefetch");
        return RxJavaPlugins.onAssembly(new ParallelRunOn(this, scheduler, prefetch));
    }

    @CheckReturnValue
    @NonNull
    public final Flowable<T> reduce(@NonNull BiFunction<T, T, T> reducer) {
        ObjectHelper.requireNonNull(reducer, "reducer");
        return RxJavaPlugins.onAssembly(new ParallelReduceFull(this, reducer));
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> reduce(@NonNull Callable<R> initialSupplier, @NonNull BiFunction<R, ? super T, R> reducer) {
        ObjectHelper.requireNonNull(initialSupplier, "initialSupplier");
        ObjectHelper.requireNonNull(reducer, "reducer");
        return RxJavaPlugins.onAssembly(new ParallelReduce(this, initialSupplier, reducer));
    }

    @SchedulerSupport(SchedulerSupport.NONE)
    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    public final Flowable<T> sequential() {
        return sequential(Flowable.bufferSize());
    }

    @SchedulerSupport(SchedulerSupport.NONE)
    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @NonNull
    public final Flowable<T> sequential(int prefetch) {
        ObjectHelper.verifyPositive(prefetch, "prefetch");
        return RxJavaPlugins.onAssembly(new ParallelJoin(this, prefetch, false));
    }

    @SchedulerSupport(SchedulerSupport.NONE)
    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @NonNull
    public final Flowable<T> sequentialDelayError() {
        return sequentialDelayError(Flowable.bufferSize());
    }

    @SchedulerSupport(SchedulerSupport.NONE)
    @BackpressureSupport(BackpressureKind.FULL)
    @CheckReturnValue
    @NonNull
    public final Flowable<T> sequentialDelayError(int prefetch) {
        ObjectHelper.verifyPositive(prefetch, "prefetch");
        return RxJavaPlugins.onAssembly(new ParallelJoin(this, prefetch, true));
    }

    @CheckReturnValue
    @NonNull
    public final Flowable<T> sorted(@NonNull Comparator<? super T> comparator) {
        return sorted(comparator, 16);
    }

    @CheckReturnValue
    @NonNull
    public final Flowable<T> sorted(@NonNull Comparator<? super T> comparator, int capacityHint) {
        ObjectHelper.requireNonNull(comparator, "comparator is null");
        ObjectHelper.verifyPositive(capacityHint, "capacityHint");
        int ch = (capacityHint / parallelism()) + 1;
        return RxJavaPlugins.onAssembly(new ParallelSortedJoin(reduce(Functions.createArrayList(ch), ListAddBiConsumer.instance()).map(new SorterFunction(comparator)), comparator));
    }

    @CheckReturnValue
    @NonNull
    public final Flowable<List<T>> toSortedList(@NonNull Comparator<? super T> comparator) {
        return toSortedList(comparator, 16);
    }

    @CheckReturnValue
    @NonNull
    public final Flowable<List<T>> toSortedList(@NonNull Comparator<? super T> comparator, int capacityHint) {
        ObjectHelper.requireNonNull(comparator, "comparator is null");
        ObjectHelper.verifyPositive(capacityHint, "capacityHint");
        int ch = (capacityHint / parallelism()) + 1;
        Flowable<List<T>> merged = reduce(Functions.createArrayList(ch), ListAddBiConsumer.instance()).map(new SorterFunction(comparator)).reduce(new MergerBiFunction(comparator));
        return RxJavaPlugins.onAssembly(merged);
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doOnNext(@NonNull Consumer<? super T> onNext) {
        ObjectHelper.requireNonNull(onNext, "onNext is null");
        return RxJavaPlugins.onAssembly(new ParallelPeek(this, onNext, Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.EMPTY_ACTION, Functions.EMPTY_ACTION, Functions.emptyConsumer(), Functions.EMPTY_LONG_CONSUMER, Functions.EMPTY_ACTION));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doOnNext(@NonNull Consumer<? super T> onNext, @NonNull ParallelFailureHandling errorHandler) {
        ObjectHelper.requireNonNull(onNext, "onNext is null");
        ObjectHelper.requireNonNull(errorHandler, "errorHandler is null");
        return RxJavaPlugins.onAssembly(new ParallelDoOnNextTry(this, onNext, errorHandler));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doOnNext(@NonNull Consumer<? super T> onNext, @NonNull BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
        ObjectHelper.requireNonNull(onNext, "onNext is null");
        ObjectHelper.requireNonNull(errorHandler, "errorHandler is null");
        return RxJavaPlugins.onAssembly(new ParallelDoOnNextTry(this, onNext, errorHandler));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doAfterNext(@NonNull Consumer<? super T> onAfterNext) {
        ObjectHelper.requireNonNull(onAfterNext, "onAfterNext is null");
        return RxJavaPlugins.onAssembly(new ParallelPeek(this, Functions.emptyConsumer(), onAfterNext, Functions.emptyConsumer(), Functions.EMPTY_ACTION, Functions.EMPTY_ACTION, Functions.emptyConsumer(), Functions.EMPTY_LONG_CONSUMER, Functions.EMPTY_ACTION));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doOnError(@NonNull Consumer<Throwable> onError) {
        ObjectHelper.requireNonNull(onError, "onError is null");
        return RxJavaPlugins.onAssembly(new ParallelPeek(this, Functions.emptyConsumer(), Functions.emptyConsumer(), onError, Functions.EMPTY_ACTION, Functions.EMPTY_ACTION, Functions.emptyConsumer(), Functions.EMPTY_LONG_CONSUMER, Functions.EMPTY_ACTION));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doOnComplete(@NonNull Action onComplete) {
        ObjectHelper.requireNonNull(onComplete, "onComplete is null");
        return RxJavaPlugins.onAssembly(new ParallelPeek(this, Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.emptyConsumer(), onComplete, Functions.EMPTY_ACTION, Functions.emptyConsumer(), Functions.EMPTY_LONG_CONSUMER, Functions.EMPTY_ACTION));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doAfterTerminated(@NonNull Action onAfterTerminate) {
        ObjectHelper.requireNonNull(onAfterTerminate, "onAfterTerminate is null");
        return RxJavaPlugins.onAssembly(new ParallelPeek(this, Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.EMPTY_ACTION, onAfterTerminate, Functions.emptyConsumer(), Functions.EMPTY_LONG_CONSUMER, Functions.EMPTY_ACTION));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doOnSubscribe(@NonNull Consumer<? super Subscription> onSubscribe) {
        ObjectHelper.requireNonNull(onSubscribe, "onSubscribe is null");
        return RxJavaPlugins.onAssembly(new ParallelPeek(this, Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.EMPTY_ACTION, Functions.EMPTY_ACTION, onSubscribe, Functions.EMPTY_LONG_CONSUMER, Functions.EMPTY_ACTION));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doOnRequest(@NonNull LongConsumer onRequest) {
        ObjectHelper.requireNonNull(onRequest, "onRequest is null");
        return RxJavaPlugins.onAssembly(new ParallelPeek(this, Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.EMPTY_ACTION, Functions.EMPTY_ACTION, Functions.emptyConsumer(), onRequest, Functions.EMPTY_ACTION));
    }

    @CheckReturnValue
    @NonNull
    public final ParallelFlowable<T> doOnCancel(@NonNull Action onCancel) {
        ObjectHelper.requireNonNull(onCancel, "onCancel is null");
        return RxJavaPlugins.onAssembly(new ParallelPeek(this, Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.emptyConsumer(), Functions.EMPTY_ACTION, Functions.EMPTY_ACTION, Functions.emptyConsumer(), Functions.EMPTY_LONG_CONSUMER, onCancel));
    }

    @CheckReturnValue
    @NonNull
    public final <C> ParallelFlowable<C> collect(@NonNull Callable<? extends C> collectionSupplier, @NonNull BiConsumer<? super C, ? super T> collector) {
        ObjectHelper.requireNonNull(collectionSupplier, "collectionSupplier is null");
        ObjectHelper.requireNonNull(collector, "collector is null");
        return RxJavaPlugins.onAssembly(new ParallelCollect(this, collectionSupplier, collector));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ParallelFlowable<T> fromArray(@NonNull Publisher<T>... publishers) {
        if (publishers.length == 0) {
            throw new IllegalArgumentException("Zero publishers not supported");
        }
        return RxJavaPlugins.onAssembly(new ParallelFromArray(publishers));
    }

    @CheckReturnValue
    @NonNull
    public final <U> U to(@NonNull Function<? super ParallelFlowable<T>, U> converter) {
        try {
            return (U) ((Function) ObjectHelper.requireNonNull(converter, "converter is null")).mo339apply(this);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            throw ExceptionHelper.wrapOrThrow(ex);
        }
    }

    @CheckReturnValue
    @NonNull
    public final <U> ParallelFlowable<U> compose(@NonNull ParallelTransformer<T, U> composer) {
        return RxJavaPlugins.onAssembly(((ParallelTransformer) ObjectHelper.requireNonNull(composer, "composer is null")).apply(this));
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> flatMap(@NonNull Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return flatMap(mapper, false, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, Flowable.bufferSize());
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> flatMap(@NonNull Function<? super T, ? extends Publisher<? extends R>> mapper, boolean delayError) {
        return flatMap(mapper, delayError, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, Flowable.bufferSize());
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> flatMap(@NonNull Function<? super T, ? extends Publisher<? extends R>> mapper, boolean delayError, int maxConcurrency) {
        return flatMap(mapper, delayError, maxConcurrency, Flowable.bufferSize());
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> flatMap(@NonNull Function<? super T, ? extends Publisher<? extends R>> mapper, boolean delayError, int maxConcurrency, int prefetch) {
        ObjectHelper.requireNonNull(mapper, "mapper is null");
        ObjectHelper.verifyPositive(maxConcurrency, "maxConcurrency");
        ObjectHelper.verifyPositive(prefetch, "prefetch");
        return RxJavaPlugins.onAssembly(new ParallelFlatMap(this, mapper, delayError, maxConcurrency, prefetch));
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> concatMap(@NonNull Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return concatMap(mapper, 2);
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> concatMap(@NonNull Function<? super T, ? extends Publisher<? extends R>> mapper, int prefetch) {
        ObjectHelper.requireNonNull(mapper, "mapper is null");
        ObjectHelper.verifyPositive(prefetch, "prefetch");
        return RxJavaPlugins.onAssembly(new ParallelConcatMap(this, mapper, prefetch, ErrorMode.IMMEDIATE));
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> concatMapDelayError(@NonNull Function<? super T, ? extends Publisher<? extends R>> mapper, boolean tillTheEnd) {
        return concatMapDelayError(mapper, 2, tillTheEnd);
    }

    @CheckReturnValue
    @NonNull
    public final <R> ParallelFlowable<R> concatMapDelayError(@NonNull Function<? super T, ? extends Publisher<? extends R>> mapper, int prefetch, boolean tillTheEnd) {
        ObjectHelper.requireNonNull(mapper, "mapper is null");
        ObjectHelper.verifyPositive(prefetch, "prefetch");
        return RxJavaPlugins.onAssembly(new ParallelConcatMap(this, mapper, prefetch, tillTheEnd ? ErrorMode.END : ErrorMode.BOUNDARY));
    }
}
