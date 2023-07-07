package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.operators.observable.ObservableTimeoutTimed;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class ObservableTimeout<T, U, V> extends AbstractObservableWithUpstream<T, T> {
    final ObservableSource<U> firstTimeoutIndicator;
    final Function<? super T, ? extends ObservableSource<V>> itemTimeoutIndicator;
    final ObservableSource<? extends T> other;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface TimeoutSelectorSupport extends ObservableTimeoutTimed.TimeoutSupport {
        void onTimeoutError(long j, Throwable th);
    }

    public ObservableTimeout(Observable<T> source, ObservableSource<U> firstTimeoutIndicator, Function<? super T, ? extends ObservableSource<V>> itemTimeoutIndicator, ObservableSource<? extends T> other) {
        super(source);
        this.firstTimeoutIndicator = firstTimeoutIndicator;
        this.itemTimeoutIndicator = itemTimeoutIndicator;
        this.other = other;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super T> observer) {
        ObservableSource<? extends T> observableSource = this.other;
        if (observableSource == null) {
            TimeoutObserver<T> parent = new TimeoutObserver<>(observer, this.itemTimeoutIndicator);
            observer.onSubscribe(parent);
            parent.startFirstTimeout(this.firstTimeoutIndicator);
            this.source.subscribe(parent);
            return;
        }
        TimeoutFallbackObserver<T> parent2 = new TimeoutFallbackObserver<>(observer, this.itemTimeoutIndicator, observableSource);
        observer.onSubscribe(parent2);
        parent2.startFirstTimeout(this.firstTimeoutIndicator);
        this.source.subscribe(parent2);
    }

    /* loaded from: classes.dex */
    static final class TimeoutObserver<T> extends AtomicLong implements Observer<T>, Disposable, TimeoutSelectorSupport {
        private static final long serialVersionUID = 3764492702657003550L;
        final Observer<? super T> downstream;
        final Function<? super T, ? extends ObservableSource<?>> itemTimeoutIndicator;
        final SequentialDisposable task = new SequentialDisposable();
        final AtomicReference<Disposable> upstream = new AtomicReference<>();

        TimeoutObserver(Observer<? super T> actual, Function<? super T, ? extends ObservableSource<?>> itemTimeoutIndicator) {
            this.downstream = actual;
            this.itemTimeoutIndicator = itemTimeoutIndicator;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this.upstream, d);
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            long idx = get();
            if (idx == Long.MAX_VALUE || !compareAndSet(idx, idx + 1)) {
                return;
            }
            Disposable d = this.task.get();
            if (d != null) {
                d.dispose();
            }
            this.downstream.onNext(t);
            try {
                ObservableSource<?> itemTimeoutObservableSource = (ObservableSource) ObjectHelper.requireNonNull(this.itemTimeoutIndicator.mo401apply(t), "The itemTimeoutIndicator returned a null ObservableSource.");
                TimeoutConsumer consumer = new TimeoutConsumer(1 + idx, this);
                if (this.task.replace(consumer)) {
                    itemTimeoutObservableSource.subscribe(consumer);
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.upstream.get().dispose();
                getAndSet(Long.MAX_VALUE);
                this.downstream.onError(ex);
            }
        }

        void startFirstTimeout(ObservableSource<?> firstTimeoutIndicator) {
            if (firstTimeoutIndicator != null) {
                TimeoutConsumer consumer = new TimeoutConsumer(0L, this);
                if (this.task.replace(consumer)) {
                    firstTimeoutIndicator.subscribe(consumer);
                }
            }
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            if (getAndSet(Long.MAX_VALUE) != Long.MAX_VALUE) {
                this.task.dispose();
                this.downstream.onError(t);
                return;
            }
            RxJavaPlugins.onError(t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            if (getAndSet(Long.MAX_VALUE) != Long.MAX_VALUE) {
                this.task.dispose();
                this.downstream.onComplete();
            }
        }

        @Override // io.reactivex.internal.operators.observable.ObservableTimeoutTimed.TimeoutSupport
        public void onTimeout(long idx) {
            if (compareAndSet(idx, Long.MAX_VALUE)) {
                DisposableHelper.dispose(this.upstream);
                this.downstream.onError(new TimeoutException());
            }
        }

        @Override // io.reactivex.internal.operators.observable.ObservableTimeout.TimeoutSelectorSupport
        public void onTimeoutError(long idx, Throwable ex) {
            if (compareAndSet(idx, Long.MAX_VALUE)) {
                DisposableHelper.dispose(this.upstream);
                this.downstream.onError(ex);
                return;
            }
            RxJavaPlugins.onError(ex);
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            DisposableHelper.dispose(this.upstream);
            this.task.dispose();
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(this.upstream.get());
        }
    }

    /* loaded from: classes.dex */
    static final class TimeoutFallbackObserver<T> extends AtomicReference<Disposable> implements Observer<T>, Disposable, TimeoutSelectorSupport {
        private static final long serialVersionUID = -7508389464265974549L;
        final Observer<? super T> downstream;
        ObservableSource<? extends T> fallback;
        final Function<? super T, ? extends ObservableSource<?>> itemTimeoutIndicator;
        final SequentialDisposable task = new SequentialDisposable();
        final AtomicLong index = new AtomicLong();
        final AtomicReference<Disposable> upstream = new AtomicReference<>();

        TimeoutFallbackObserver(Observer<? super T> actual, Function<? super T, ? extends ObservableSource<?>> itemTimeoutIndicator, ObservableSource<? extends T> fallback) {
            this.downstream = actual;
            this.itemTimeoutIndicator = itemTimeoutIndicator;
            this.fallback = fallback;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this.upstream, d);
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            long idx = this.index.get();
            if (idx == Long.MAX_VALUE || !this.index.compareAndSet(idx, idx + 1)) {
                return;
            }
            Disposable d = this.task.get();
            if (d != null) {
                d.dispose();
            }
            this.downstream.onNext(t);
            try {
                ObservableSource<?> itemTimeoutObservableSource = (ObservableSource) ObjectHelper.requireNonNull(this.itemTimeoutIndicator.mo401apply(t), "The itemTimeoutIndicator returned a null ObservableSource.");
                TimeoutConsumer consumer = new TimeoutConsumer(1 + idx, this);
                if (this.task.replace(consumer)) {
                    itemTimeoutObservableSource.subscribe(consumer);
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.upstream.get().dispose();
                this.index.getAndSet(Long.MAX_VALUE);
                this.downstream.onError(ex);
            }
        }

        void startFirstTimeout(ObservableSource<?> firstTimeoutIndicator) {
            if (firstTimeoutIndicator != null) {
                TimeoutConsumer consumer = new TimeoutConsumer(0L, this);
                if (this.task.replace(consumer)) {
                    firstTimeoutIndicator.subscribe(consumer);
                }
            }
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            if (this.index.getAndSet(Long.MAX_VALUE) != Long.MAX_VALUE) {
                this.task.dispose();
                this.downstream.onError(t);
                this.task.dispose();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            if (this.index.getAndSet(Long.MAX_VALUE) != Long.MAX_VALUE) {
                this.task.dispose();
                this.downstream.onComplete();
                this.task.dispose();
            }
        }

        @Override // io.reactivex.internal.operators.observable.ObservableTimeoutTimed.TimeoutSupport
        public void onTimeout(long idx) {
            if (this.index.compareAndSet(idx, Long.MAX_VALUE)) {
                DisposableHelper.dispose(this.upstream);
                ObservableSource<? extends T> f = this.fallback;
                this.fallback = null;
                f.subscribe(new ObservableTimeoutTimed.FallbackObserver(this.downstream, this));
            }
        }

        @Override // io.reactivex.internal.operators.observable.ObservableTimeout.TimeoutSelectorSupport
        public void onTimeoutError(long idx, Throwable ex) {
            if (this.index.compareAndSet(idx, Long.MAX_VALUE)) {
                DisposableHelper.dispose(this);
                this.downstream.onError(ex);
                return;
            }
            RxJavaPlugins.onError(ex);
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            DisposableHelper.dispose(this.upstream);
            DisposableHelper.dispose(this);
            this.task.dispose();
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(get());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class TimeoutConsumer extends AtomicReference<Disposable> implements Observer<Object>, Disposable {
        private static final long serialVersionUID = 8708641127342403073L;
        final long idx;
        final TimeoutSelectorSupport parent;

        TimeoutConsumer(long idx, TimeoutSelectorSupport parent) {
            this.idx = idx;
            this.parent = parent;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        @Override // io.reactivex.Observer
        public void onNext(Object t) {
            Disposable upstream = (Disposable) get();
            if (upstream != DisposableHelper.DISPOSED) {
                upstream.dispose();
                lazySet(DisposableHelper.DISPOSED);
                this.parent.onTimeout(this.idx);
            }
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            if (get() != DisposableHelper.DISPOSED) {
                lazySet(DisposableHelper.DISPOSED);
                this.parent.onTimeoutError(this.idx, t);
                return;
            }
            RxJavaPlugins.onError(t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            if (get() != DisposableHelper.DISPOSED) {
                lazySet(DisposableHelper.DISPOSED);
                this.parent.onTimeout(this.idx);
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            DisposableHelper.dispose(this);
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(get());
        }
    }
}