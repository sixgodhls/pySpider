package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.observers.SerializedObserver;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class ObservableDebounceTimed<T> extends AbstractObservableWithUpstream<T, T> {
    final Scheduler scheduler;
    final long timeout;
    final TimeUnit unit;

    public ObservableDebounceTimed(ObservableSource<T> source, long timeout, TimeUnit unit, Scheduler scheduler) {
        super(source);
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    @Override // io.reactivex.Observable
    public void subscribeActual(Observer<? super T> t) {
        this.source.subscribe(new DebounceTimedObserver(new SerializedObserver(t), this.timeout, this.unit, this.scheduler.createWorker()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DebounceTimedObserver<T> implements Observer<T>, Disposable {
        boolean done;
        final Observer<? super T> downstream;
        volatile long index;
        final long timeout;
        Disposable timer;
        final TimeUnit unit;
        Disposable upstream;
        final Scheduler.Worker worker;

        DebounceTimedObserver(Observer<? super T> actual, long timeout, TimeUnit unit, Scheduler.Worker worker) {
            this.downstream = actual;
            this.timeout = timeout;
            this.unit = unit;
            this.worker = worker;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            if (this.done) {
                return;
            }
            long idx = this.index + 1;
            this.index = idx;
            Disposable d = this.timer;
            if (d != null) {
                d.dispose();
            }
            DebounceEmitter<T> de = new DebounceEmitter<>(t, idx, this);
            this.timer = de;
            de.setResource(this.worker.schedule(de, this.timeout, this.unit));
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            Disposable d = this.timer;
            if (d != null) {
                d.dispose();
            }
            this.done = true;
            this.downstream.onError(t);
            this.worker.dispose();
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            Disposable d = this.timer;
            if (d != null) {
                d.dispose();
            }
            DebounceEmitter<T> de = (DebounceEmitter) d;
            if (de != null) {
                de.run();
            }
            this.downstream.onComplete();
            this.worker.dispose();
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.upstream.dispose();
            this.worker.dispose();
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.worker.isDisposed();
        }

        void emit(long idx, T t, DebounceEmitter<T> emitter) {
            if (idx == this.index) {
                this.downstream.onNext(t);
                emitter.dispose();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DebounceEmitter<T> extends AtomicReference<Disposable> implements Runnable, Disposable {
        private static final long serialVersionUID = 6812032969491025141L;
        final long idx;
        final AtomicBoolean once = new AtomicBoolean();
        final DebounceTimedObserver<T> parent;
        final T value;

        DebounceEmitter(T value, long idx, DebounceTimedObserver<T> parent) {
            this.value = value;
            this.idx = idx;
            this.parent = parent;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.once.compareAndSet(false, true)) {
                this.parent.emit(this.idx, this.value, this);
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            DisposableHelper.dispose(this);
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return get() == DisposableHelper.DISPOSED;
        }

        public void setResource(Disposable d) {
            DisposableHelper.replace(this, d);
        }
    }
}