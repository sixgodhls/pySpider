package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.observers.QueueDrainObserver;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.SerializedObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.UnicastSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class ObservableWindowBoundarySelector<T, B, V> extends AbstractObservableWithUpstream<T, Observable<T>> {
    final int bufferSize;
    final Function<? super B, ? extends ObservableSource<V>> close;
    final ObservableSource<B> open;

    public ObservableWindowBoundarySelector(ObservableSource<T> source, ObservableSource<B> open, Function<? super B, ? extends ObservableSource<V>> close, int bufferSize) {
        super(source);
        this.open = open;
        this.close = close;
        this.bufferSize = bufferSize;
    }

    @Override // io.reactivex.Observable
    public void subscribeActual(Observer<? super Observable<T>> t) {
        this.source.subscribe(new WindowBoundaryMainObserver(new SerializedObserver(t), this.open, this.close, this.bufferSize));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class WindowBoundaryMainObserver<T, B, V> extends QueueDrainObserver<T, Object, Observable<T>> implements Disposable {
        final int bufferSize;
        final Function<? super B, ? extends ObservableSource<V>> close;
        final ObservableSource<B> open;
        Disposable upstream;
        final AtomicReference<Disposable> boundary = new AtomicReference<>();
        final AtomicLong windows = new AtomicLong();
        final CompositeDisposable resources = new CompositeDisposable();
        final List<UnicastSubject<T>> ws = new ArrayList();

        WindowBoundaryMainObserver(Observer<? super Observable<T>> actual, ObservableSource<B> open, Function<? super B, ? extends ObservableSource<V>> close, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.open = open;
            this.close = close;
            this.bufferSize = bufferSize;
            this.windows.lazySet(1L);
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
                if (this.cancelled) {
                    return;
                }
                OperatorWindowBoundaryOpenObserver<T, B> os = new OperatorWindowBoundaryOpenObserver<>(this);
                if (this.boundary.compareAndSet(null, os)) {
                    this.windows.getAndIncrement();
                    this.open.subscribe(os);
                }
            }
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            if (fastEnter()) {
                for (UnicastSubject<T> w : this.ws) {
                    w.onNext(t);
                }
                if (leave(-1) == 0) {
                    return;
                }
            } else {
                this.queue.offer(NotificationLite.next(t));
                if (!enter()) {
                    return;
                }
            }
            drainLoop();
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            if (this.windows.decrementAndGet() == 0) {
                this.resources.dispose();
            }
            this.downstream.onError(t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            if (this.windows.decrementAndGet() == 0) {
                this.resources.dispose();
            }
            this.downstream.onComplete();
        }

        void error(Throwable t) {
            this.upstream.dispose();
            this.resources.dispose();
            onError(t);
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.cancelled = true;
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeBoundary() {
            this.resources.dispose();
            DisposableHelper.dispose(this.boundary);
        }

        void drainLoop() {
            MpscLinkedQueue<Object> q = (MpscLinkedQueue) this.queue;
            Observer<? super V> observer = this.downstream;
            List<UnicastSubject<T>> ws = this.ws;
            int missed = 1;
            while (true) {
                boolean d = this.done;
                Object o = q.mo337poll();
                boolean empty = o == null;
                if (d && empty) {
                    disposeBoundary();
                    Throwable e = this.error;
                    if (e != null) {
                        for (UnicastSubject<T> w : ws) {
                            w.onError(e);
                        }
                    } else {
                        for (UnicastSubject<T> w2 : ws) {
                            w2.onComplete();
                        }
                    }
                    ws.clear();
                    return;
                } else if (!empty) {
                    if (o instanceof WindowOperation) {
                        WindowOperation<T, B> wo = (WindowOperation) o;
                        if (wo.w != null) {
                            if (ws.remove(wo.w)) {
                                wo.w.onComplete();
                                if (this.windows.decrementAndGet() == 0) {
                                    disposeBoundary();
                                    return;
                                }
                            } else {
                                continue;
                            }
                        } else if (!this.cancelled) {
                            UnicastSubject<T> w3 = UnicastSubject.create(this.bufferSize);
                            ws.add(w3);
                            observer.onNext(w3);
                            try {
                                ObservableSource<V> p = (ObservableSource) ObjectHelper.requireNonNull(this.close.mo339apply((B) wo.open), "The ObservableSource supplied is null");
                                OperatorWindowBoundaryCloseObserver<T, V> cl = new OperatorWindowBoundaryCloseObserver<>(this, w3);
                                if (this.resources.add(cl)) {
                                    this.windows.getAndIncrement();
                                    p.subscribe(cl);
                                }
                            } catch (Throwable e2) {
                                Exceptions.throwIfFatal(e2);
                                this.cancelled = true;
                                observer.onError(e2);
                            }
                        }
                    } else {
                        for (UnicastSubject<T> w4 : ws) {
                            w4.onNext((T) NotificationLite.getValue(o));
                        }
                    }
                } else {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }

        @Override // io.reactivex.internal.observers.QueueDrainObserver, io.reactivex.internal.util.ObservableQueueDrain
        public void accept(Observer<? super Observable<T>> a, Object v) {
        }

        void open(B b) {
            this.queue.offer(new WindowOperation(null, b));
            if (enter()) {
                drainLoop();
            }
        }

        void close(OperatorWindowBoundaryCloseObserver<T, V> w) {
            this.resources.delete(w);
            this.queue.offer(new WindowOperation(w.w, null));
            if (enter()) {
                drainLoop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class WindowOperation<T, B> {
        final B open;
        final UnicastSubject<T> w;

        WindowOperation(UnicastSubject<T> w, B open) {
            this.w = w;
            this.open = open;
        }
    }

    /* loaded from: classes.dex */
    static final class OperatorWindowBoundaryOpenObserver<T, B> extends DisposableObserver<B> {
        final WindowBoundaryMainObserver<T, B, ?> parent;

        OperatorWindowBoundaryOpenObserver(WindowBoundaryMainObserver<T, B, ?> parent) {
            this.parent = parent;
        }

        @Override // io.reactivex.Observer
        public void onNext(B t) {
            this.parent.open(t);
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            this.parent.error(t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            this.parent.onComplete();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class OperatorWindowBoundaryCloseObserver<T, V> extends DisposableObserver<V> {
        boolean done;
        final WindowBoundaryMainObserver<T, ?, V> parent;
        final UnicastSubject<T> w;

        OperatorWindowBoundaryCloseObserver(WindowBoundaryMainObserver<T, ?, V> parent, UnicastSubject<T> w) {
            this.parent = parent;
            this.w = w;
        }

        @Override // io.reactivex.Observer
        public void onNext(V t) {
            dispose();
            onComplete();
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.parent.error(t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            this.parent.close(this);
        }
    }
}
