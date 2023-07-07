package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class ObservableCombineLatest<T, R> extends Observable<R> {
    final int bufferSize;
    final Function<? super Object[], ? extends R> combiner;
    final boolean delayError;
    final ObservableSource<? extends T>[] sources;
    final Iterable<? extends ObservableSource<? extends T>> sourcesIterable;

    public ObservableCombineLatest(ObservableSource<? extends T>[] sources, Iterable<? extends ObservableSource<? extends T>> sourcesIterable, Function<? super Object[], ? extends R> combiner, int bufferSize, boolean delayError) {
        this.sources = sources;
        this.sourcesIterable = sourcesIterable;
        this.combiner = combiner;
        this.bufferSize = bufferSize;
        this.delayError = delayError;
    }

    @Override // io.reactivex.Observable
    public void subscribeActual(Observer<? super R> observer) {
        ObservableSource<? extends T>[] sources = this.sources;
        int count = 0;
        if (sources == null) {
            sources = new Observable[8];
            for (ObservableSource<? extends T> p : this.sourcesIterable) {
                if (count == sources.length) {
                    ObservableSource<? extends T>[] b = new ObservableSource[(count >> 2) + count];
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
            EmptyDisposable.complete(observer);
            return;
        }
        LatestCoordinator<T, R> lc = new LatestCoordinator<>(observer, this.combiner, count, this.bufferSize, this.delayError);
        lc.subscribe(sources);
    }

    /* loaded from: classes.dex */
    static final class LatestCoordinator<T, R> extends AtomicInteger implements Disposable {
        private static final long serialVersionUID = 8567835998786448817L;
        int active;
        volatile boolean cancelled;
        final Function<? super Object[], ? extends R> combiner;
        int complete;
        final boolean delayError;
        volatile boolean done;
        final Observer<? super R> downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        Object[] latest;
        final CombinerObserver<T, R>[] observers;
        final SpscLinkedArrayQueue<Object[]> queue;

        LatestCoordinator(Observer<? super R> actual, Function<? super Object[], ? extends R> combiner, int count, int bufferSize, boolean delayError) {
            this.downstream = actual;
            this.combiner = combiner;
            this.delayError = delayError;
            this.latest = new Object[count];
            CombinerObserver<T, R>[] as = new CombinerObserver[count];
            for (int i = 0; i < count; i++) {
                as[i] = new CombinerObserver<>(this, i);
            }
            this.observers = as;
            this.queue = new SpscLinkedArrayQueue<>(bufferSize);
        }

        public void subscribe(ObservableSource<? extends T>[] sources) {
            CombinerObserver<T, R>[] combinerObserverArr = this.observers;
            int len = combinerObserverArr.length;
            this.downstream.onSubscribe(this);
            for (int i = 0; i < len && !this.done && !this.cancelled; i++) {
                sources[i].subscribe(combinerObserverArr[i]);
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                cancelSources();
                if (getAndIncrement() == 0) {
                    clear(this.queue);
                }
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.cancelled;
        }

        void cancelSources() {
            CombinerObserver<T, R>[] combinerObserverArr;
            for (CombinerObserver<T, R> observer : this.observers) {
                observer.dispose();
            }
        }

        void clear(SpscLinkedArrayQueue<?> q) {
            synchronized (this) {
                this.latest = null;
            }
            q.clear();
        }

        void drain() {
            if (getAndIncrement() != 0) {
                return;
            }
            SpscLinkedArrayQueue<Object[]> q = this.queue;
            Observer<? super R> a = this.downstream;
            boolean delayError = this.delayError;
            int missed = 1;
            while (!this.cancelled) {
                if (!delayError && this.errors.get() != null) {
                    cancelSources();
                    clear(q);
                    a.onError(this.errors.terminate());
                    return;
                }
                boolean d = this.done;
                Object[] s = q.mo399poll();
                boolean empty = s == null;
                if (d && empty) {
                    clear(q);
                    Throwable ex = this.errors.terminate();
                    if (ex == null) {
                        a.onComplete();
                        return;
                    } else {
                        a.onError(ex);
                        return;
                    }
                } else if (!empty) {
                    try {
                        a.onNext((Object) ObjectHelper.requireNonNull(this.combiner.mo401apply(s), "The combiner returned a null value"));
                    } catch (Throwable ex2) {
                        Exceptions.throwIfFatal(ex2);
                        this.errors.addThrowable(ex2);
                        cancelSources();
                        clear(q);
                        a.onError(this.errors.terminate());
                        return;
                    }
                } else {
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
            clear(q);
        }

        /* JADX WARN: Multi-variable type inference failed */
        void innerNext(int index, T item) {
            boolean shouldDrain = false;
            synchronized (this) {
                Object[] latest = this.latest;
                if (latest == null) {
                    return;
                }
                Object o = latest[index];
                int a = this.active;
                if (o == null) {
                    a++;
                    this.active = a;
                }
                latest[index] = item;
                if (a == latest.length) {
                    this.queue.offer(latest.clone());
                    shouldDrain = true;
                }
                if (shouldDrain) {
                    drain();
                }
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:18:0x0025, code lost:
            if (r2 == r1.length) goto L21;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void innerError(int r6, java.lang.Throwable r7) {
            /*
                r5 = this;
                io.reactivex.internal.util.AtomicThrowable r0 = r5.errors
                boolean r0 = r0.addThrowable(r7)
                if (r0 == 0) goto L37
                r0 = 1
                boolean r1 = r5.delayError
                if (r1 == 0) goto L2e
                monitor-enter(r5)
                java.lang.Object[] r1 = r5.latest     // Catch: java.lang.Throwable -> L2b
                if (r1 != 0) goto L14
                monitor-exit(r5)     // Catch: java.lang.Throwable -> L2b
                return
            L14:
                r2 = r1[r6]     // Catch: java.lang.Throwable -> L2b
                r3 = 1
                if (r2 != 0) goto L1b
                r2 = 1
                goto L1c
            L1b:
                r2 = 0
            L1c:
                r0 = r2
                if (r0 != 0) goto L27
                int r2 = r5.complete     // Catch: java.lang.Throwable -> L2b
                int r2 = r2 + r3
                r5.complete = r2     // Catch: java.lang.Throwable -> L2b
                int r4 = r1.length     // Catch: java.lang.Throwable -> L2b
                if (r2 != r4) goto L29
            L27:
                r5.done = r3     // Catch: java.lang.Throwable -> L2b
            L29:
                monitor-exit(r5)     // Catch: java.lang.Throwable -> L2b
                goto L2e
            L2b:
                r1 = move-exception
                monitor-exit(r5)     // Catch: java.lang.Throwable -> L2b
                throw r1
            L2e:
                if (r0 == 0) goto L33
                r5.cancelSources()
            L33:
                r5.drain()
                goto L3a
            L37:
                io.reactivex.plugins.RxJavaPlugins.onError(r7)
            L3a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableCombineLatest.LatestCoordinator.innerError(int, java.lang.Throwable):void");
        }

        /* JADX WARN: Code restructure failed: missing block: B:15:0x0019, code lost:
            if (r2 == r1.length) goto L21;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void innerComplete(int r6) {
            /*
                r5 = this;
                r0 = 0
                monitor-enter(r5)
                java.lang.Object[] r1 = r5.latest     // Catch: java.lang.Throwable -> L27
                if (r1 != 0) goto L8
                monitor-exit(r5)     // Catch: java.lang.Throwable -> L27
                return
            L8:
                r2 = r1[r6]     // Catch: java.lang.Throwable -> L27
                r3 = 1
                if (r2 != 0) goto Lf
                r2 = 1
                goto L10
            Lf:
                r2 = 0
            L10:
                r0 = r2
                if (r0 != 0) goto L1b
                int r2 = r5.complete     // Catch: java.lang.Throwable -> L27
                int r2 = r2 + r3
                r5.complete = r2     // Catch: java.lang.Throwable -> L27
                int r4 = r1.length     // Catch: java.lang.Throwable -> L27
                if (r2 != r4) goto L1d
            L1b:
                r5.done = r3     // Catch: java.lang.Throwable -> L27
            L1d:
                monitor-exit(r5)     // Catch: java.lang.Throwable -> L27
                if (r0 == 0) goto L23
                r5.cancelSources()
            L23:
                r5.drain()
                return
            L27:
                r1 = move-exception
                monitor-exit(r5)     // Catch: java.lang.Throwable -> L27
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableCombineLatest.LatestCoordinator.innerComplete(int):void");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class CombinerObserver<T, R> extends AtomicReference<Disposable> implements Observer<T> {
        private static final long serialVersionUID = -4823716997131257941L;
        final int index;
        final LatestCoordinator<T, R> parent;

        CombinerObserver(LatestCoordinator<T, R> parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            this.parent.innerNext(this.index, t);
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            this.parent.innerError(this.index, t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            this.parent.innerComplete(this.index);
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }
    }
}