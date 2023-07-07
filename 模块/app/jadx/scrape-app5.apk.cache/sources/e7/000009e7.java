package io.reactivex.internal.operators.observable;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.observables.ConnectableObservable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class ObservableInternalHelper {
    private ObservableInternalHelper() {
        throw new IllegalStateException("No instances!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SimpleGenerator<T, S> implements BiFunction<S, Emitter<T>, S> {
        final Consumer<Emitter<T>> consumer;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // io.reactivex.functions.BiFunction
        /* renamed from: apply */
        public /* bridge */ /* synthetic */ Object mo374apply(Object obj, Object obj2) throws Exception {
            return apply((SimpleGenerator<T, S>) obj, (Emitter) ((Emitter) obj2));
        }

        SimpleGenerator(Consumer<Emitter<T>> consumer) {
            this.consumer = consumer;
        }

        public S apply(S t1, Emitter<T> t2) throws Exception {
            this.consumer.accept(t2);
            return t1;
        }
    }

    public static <T, S> BiFunction<S, Emitter<T>, S> simpleGenerator(Consumer<Emitter<T>> consumer) {
        return new SimpleGenerator(consumer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SimpleBiGenerator<T, S> implements BiFunction<S, Emitter<T>, S> {
        final BiConsumer<S, Emitter<T>> consumer;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // io.reactivex.functions.BiFunction
        /* renamed from: apply */
        public /* bridge */ /* synthetic */ Object mo374apply(Object obj, Object obj2) throws Exception {
            return apply((SimpleBiGenerator<T, S>) obj, (Emitter) ((Emitter) obj2));
        }

        SimpleBiGenerator(BiConsumer<S, Emitter<T>> consumer) {
            this.consumer = consumer;
        }

        public S apply(S t1, Emitter<T> t2) throws Exception {
            this.consumer.accept(t1, t2);
            return t1;
        }
    }

    public static <T, S> BiFunction<S, Emitter<T>, S> simpleBiGenerator(BiConsumer<S, Emitter<T>> consumer) {
        return new SimpleBiGenerator(consumer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ItemDelayFunction<T, U> implements Function<T, ObservableSource<T>> {
        final Function<? super T, ? extends ObservableSource<U>> itemDelay;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // io.reactivex.functions.Function
        /* renamed from: apply  reason: collision with other method in class */
        public /* bridge */ /* synthetic */ Object mo401apply(Object obj) throws Exception {
            return mo401apply((ItemDelayFunction<T, U>) obj);
        }

        ItemDelayFunction(Function<? super T, ? extends ObservableSource<U>> itemDelay) {
            this.itemDelay = itemDelay;
        }

        @Override // io.reactivex.functions.Function
        /* renamed from: apply */
        public ObservableSource<T> mo401apply(T v) throws Exception {
            ObservableSource<U> o = (ObservableSource) ObjectHelper.requireNonNull(this.itemDelay.mo401apply(v), "The itemDelay returned a null ObservableSource");
            return new ObservableTake(o, 1L).map(Functions.justFunction(v)).defaultIfEmpty(v);
        }
    }

    public static <T, U> Function<T, ObservableSource<T>> itemDelay(Function<? super T, ? extends ObservableSource<U>> itemDelay) {
        return new ItemDelayFunction(itemDelay);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ObserverOnNext<T> implements Consumer<T> {
        final Observer<T> observer;

        ObserverOnNext(Observer<T> observer) {
            this.observer = observer;
        }

        @Override // io.reactivex.functions.Consumer
        public void accept(T v) throws Exception {
            this.observer.onNext(v);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ObserverOnError<T> implements Consumer<Throwable> {
        final Observer<T> observer;

        ObserverOnError(Observer<T> observer) {
            this.observer = observer;
        }

        @Override // io.reactivex.functions.Consumer
        public void accept(Throwable v) throws Exception {
            this.observer.onError(v);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ObserverOnComplete<T> implements Action {
        final Observer<T> observer;

        ObserverOnComplete(Observer<T> observer) {
            this.observer = observer;
        }

        @Override // io.reactivex.functions.Action
        public void run() throws Exception {
            this.observer.onComplete();
        }
    }

    public static <T> Consumer<T> observerOnNext(Observer<T> observer) {
        return new ObserverOnNext(observer);
    }

    public static <T> Consumer<Throwable> observerOnError(Observer<T> observer) {
        return new ObserverOnError(observer);
    }

    public static <T> Action observerOnComplete(Observer<T> observer) {
        return new ObserverOnComplete(observer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class FlatMapWithCombinerInner<U, R, T> implements Function<U, R> {
        private final BiFunction<? super T, ? super U, ? extends R> combiner;

        /* renamed from: t */
        private final T f155t;

        FlatMapWithCombinerInner(BiFunction<? super T, ? super U, ? extends R> combiner, T t) {
            this.combiner = combiner;
            this.f155t = t;
        }

        @Override // io.reactivex.functions.Function
        /* renamed from: apply */
        public R mo401apply(U w) throws Exception {
            return this.combiner.mo374apply((T) this.f155t, w);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class FlatMapWithCombinerOuter<T, R, U> implements Function<T, ObservableSource<R>> {
        private final BiFunction<? super T, ? super U, ? extends R> combiner;
        private final Function<? super T, ? extends ObservableSource<? extends U>> mapper;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // io.reactivex.functions.Function
        /* renamed from: apply  reason: collision with other method in class */
        public /* bridge */ /* synthetic */ Object mo401apply(Object obj) throws Exception {
            return mo401apply((FlatMapWithCombinerOuter<T, R, U>) obj);
        }

        FlatMapWithCombinerOuter(BiFunction<? super T, ? super U, ? extends R> combiner, Function<? super T, ? extends ObservableSource<? extends U>> mapper) {
            this.combiner = combiner;
            this.mapper = mapper;
        }

        @Override // io.reactivex.functions.Function
        /* renamed from: apply */
        public ObservableSource<R> mo401apply(T t) throws Exception {
            ObservableSource<U> u = (ObservableSource) ObjectHelper.requireNonNull(this.mapper.mo401apply(t), "The mapper returned a null ObservableSource");
            return new ObservableMap(u, new FlatMapWithCombinerInner(this.combiner, t));
        }
    }

    public static <T, U, R> Function<T, ObservableSource<R>> flatMapWithCombiner(Function<? super T, ? extends ObservableSource<? extends U>> mapper, BiFunction<? super T, ? super U, ? extends R> combiner) {
        return new FlatMapWithCombinerOuter(combiner, mapper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class FlatMapIntoIterable<T, U> implements Function<T, ObservableSource<U>> {
        private final Function<? super T, ? extends Iterable<? extends U>> mapper;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // io.reactivex.functions.Function
        /* renamed from: apply  reason: collision with other method in class */
        public /* bridge */ /* synthetic */ Object mo401apply(Object obj) throws Exception {
            return mo401apply((FlatMapIntoIterable<T, U>) obj);
        }

        FlatMapIntoIterable(Function<? super T, ? extends Iterable<? extends U>> mapper) {
            this.mapper = mapper;
        }

        @Override // io.reactivex.functions.Function
        /* renamed from: apply */
        public ObservableSource<U> mo401apply(T t) throws Exception {
            return new ObservableFromIterable((Iterable) ObjectHelper.requireNonNull(this.mapper.mo401apply(t), "The mapper returned a null Iterable"));
        }
    }

    public static <T, U> Function<T, ObservableSource<U>> flatMapIntoIterable(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return new FlatMapIntoIterable(mapper);
    }

    /* loaded from: classes.dex */
    enum MapToInt implements Function<Object, Object> {
        INSTANCE;

        @Override // io.reactivex.functions.Function
        /* renamed from: apply */
        public Object mo401apply(Object t) throws Exception {
            return 0;
        }
    }

    public static <T> Callable<ConnectableObservable<T>> replayCallable(Observable<T> parent) {
        return new ReplayCallable(parent);
    }

    public static <T> Callable<ConnectableObservable<T>> replayCallable(Observable<T> parent, int bufferSize) {
        return new BufferedReplayCallable(parent, bufferSize);
    }

    public static <T> Callable<ConnectableObservable<T>> replayCallable(Observable<T> parent, int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
        return new BufferedTimedReplayCallable(parent, bufferSize, time, unit, scheduler);
    }

    public static <T> Callable<ConnectableObservable<T>> replayCallable(Observable<T> parent, long time, TimeUnit unit, Scheduler scheduler) {
        return new TimedReplayCallable(parent, time, unit, scheduler);
    }

    public static <T, R> Function<Observable<T>, ObservableSource<R>> replayFunction(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, Scheduler scheduler) {
        return new ReplayFunction(selector, scheduler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ZipIterableFunction<T, R> implements Function<List<ObservableSource<? extends T>>, ObservableSource<? extends R>> {
        private final Function<? super Object[], ? extends R> zipper;

        @Override // io.reactivex.functions.Function
        /* renamed from: apply */
        public /* bridge */ /* synthetic */ Object mo401apply(Object obj) throws Exception {
            return apply((List) ((List) obj));
        }

        ZipIterableFunction(Function<? super Object[], ? extends R> zipper) {
            this.zipper = zipper;
        }

        public ObservableSource<? extends R> apply(List<ObservableSource<? extends T>> list) {
            return Observable.zipIterable(list, this.zipper, false, Observable.bufferSize());
        }
    }

    public static <T, R> Function<List<ObservableSource<? extends T>>, ObservableSource<? extends R>> zipIterable(Function<? super Object[], ? extends R> zipper) {
        return new ZipIterableFunction(zipper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplayCallable<T> implements Callable<ConnectableObservable<T>> {
        private final Observable<T> parent;

        ReplayCallable(Observable<T> parent) {
            this.parent = parent;
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public ConnectableObservable<T> mo396call() {
            return this.parent.replay();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class BufferedReplayCallable<T> implements Callable<ConnectableObservable<T>> {
        private final int bufferSize;
        private final Observable<T> parent;

        BufferedReplayCallable(Observable<T> parent, int bufferSize) {
            this.parent = parent;
            this.bufferSize = bufferSize;
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public ConnectableObservable<T> mo391call() {
            return this.parent.replay(this.bufferSize);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class BufferedTimedReplayCallable<T> implements Callable<ConnectableObservable<T>> {
        private final int bufferSize;
        private final Observable<T> parent;
        private final Scheduler scheduler;
        private final long time;
        private final TimeUnit unit;

        BufferedTimedReplayCallable(Observable<T> parent, int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
            this.parent = parent;
            this.bufferSize = bufferSize;
            this.time = time;
            this.unit = unit;
            this.scheduler = scheduler;
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public ConnectableObservable<T> mo392call() {
            return this.parent.replay(this.bufferSize, this.time, this.unit, this.scheduler);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class TimedReplayCallable<T> implements Callable<ConnectableObservable<T>> {
        private final Observable<T> parent;
        private final Scheduler scheduler;
        private final long time;
        private final TimeUnit unit;

        TimedReplayCallable(Observable<T> parent, long time, TimeUnit unit, Scheduler scheduler) {
            this.parent = parent;
            this.time = time;
            this.unit = unit;
            this.scheduler = scheduler;
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public ConnectableObservable<T> mo397call() {
            return this.parent.replay(this.time, this.unit, this.scheduler);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplayFunction<T, R> implements Function<Observable<T>, ObservableSource<R>> {
        private final Scheduler scheduler;
        private final Function<? super Observable<T>, ? extends ObservableSource<R>> selector;

        @Override // io.reactivex.functions.Function
        /* renamed from: apply */
        public /* bridge */ /* synthetic */ Object mo401apply(Object obj) throws Exception {
            return apply((Observable) ((Observable) obj));
        }

        ReplayFunction(Function<? super Observable<T>, ? extends ObservableSource<R>> selector, Scheduler scheduler) {
            this.selector = selector;
            this.scheduler = scheduler;
        }

        public ObservableSource<R> apply(Observable<T> t) throws Exception {
            ObservableSource<R> apply = (ObservableSource) ObjectHelper.requireNonNull(this.selector.mo401apply(t), "The selector returned a null ObservableSource");
            return Observable.wrap(apply).observeOn(this.scheduler);
        }
    }
}