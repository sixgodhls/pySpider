package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiPredicate;
import io.reactivex.internal.disposables.ArrayCompositeDisposable;
import io.reactivex.internal.fuseable.FuseToObservable;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public final class ObservableSequenceEqualSingle<T> extends Single<Boolean> implements FuseToObservable<Boolean> {
    final int bufferSize;
    final BiPredicate<? super T, ? super T> comparer;
    final ObservableSource<? extends T> first;
    final ObservableSource<? extends T> second;

    public ObservableSequenceEqualSingle(ObservableSource<? extends T> first, ObservableSource<? extends T> second, BiPredicate<? super T, ? super T> comparer, int bufferSize) {
        this.first = first;
        this.second = second;
        this.comparer = comparer;
        this.bufferSize = bufferSize;
    }

    @Override // io.reactivex.Single
    public void subscribeActual(SingleObserver<? super Boolean> observer) {
        EqualCoordinator<T> ec = new EqualCoordinator<>(observer, this.bufferSize, this.first, this.second, this.comparer);
        observer.onSubscribe(ec);
        ec.subscribe();
    }

    @Override // io.reactivex.internal.fuseable.FuseToObservable
    public Observable<Boolean> fuseToObservable() {
        return RxJavaPlugins.onAssembly(new ObservableSequenceEqual(this.first, this.second, this.comparer, this.bufferSize));
    }

    /* loaded from: classes.dex */
    static final class EqualCoordinator<T> extends AtomicInteger implements Disposable {
        private static final long serialVersionUID = -6178010334400373240L;
        volatile boolean cancelled;
        final BiPredicate<? super T, ? super T> comparer;
        final SingleObserver<? super Boolean> downstream;
        final ObservableSource<? extends T> first;
        final EqualObserver<T>[] observers;
        final ArrayCompositeDisposable resources = new ArrayCompositeDisposable(2);
        final ObservableSource<? extends T> second;

        /* renamed from: v1 */
        T f161v1;

        /* renamed from: v2 */
        T f162v2;

        EqualCoordinator(SingleObserver<? super Boolean> actual, int bufferSize, ObservableSource<? extends T> first, ObservableSource<? extends T> second, BiPredicate<? super T, ? super T> comparer) {
            this.downstream = actual;
            this.first = first;
            this.second = second;
            this.comparer = comparer;
            this.observers = as;
            EqualObserver<T>[] as = {new EqualObserver<>(this, 0, bufferSize), new EqualObserver<>(this, 1, bufferSize)};
        }

        boolean setDisposable(Disposable d, int index) {
            return this.resources.setResource(index, d);
        }

        void subscribe() {
            EqualObserver<T>[] as = this.observers;
            this.first.subscribe(as[0]);
            this.second.subscribe(as[1]);
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.resources.dispose();
                if (getAndIncrement() == 0) {
                    EqualObserver<T>[] as = this.observers;
                    as[0].queue.clear();
                    as[1].queue.clear();
                }
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.cancelled;
        }

        void cancel(SpscLinkedArrayQueue<T> q1, SpscLinkedArrayQueue<T> q2) {
            this.cancelled = true;
            q1.clear();
            q2.clear();
        }

        void drain() {
            Throwable e;
            Throwable e2;
            if (getAndIncrement() != 0) {
                return;
            }
            int missed = 1;
            EqualObserver<T>[] as = this.observers;
            EqualObserver<T> observer1 = as[0];
            SpscLinkedArrayQueue<T> q1 = observer1.queue;
            EqualObserver<T> observer2 = as[1];
            SpscLinkedArrayQueue<T> q2 = observer2.queue;
            while (!this.cancelled) {
                boolean d1 = observer1.done;
                if (d1 && (e2 = observer1.error) != null) {
                    cancel(q1, q2);
                    this.downstream.onError(e2);
                    return;
                }
                boolean d2 = observer2.done;
                if (d2 && (e = observer2.error) != null) {
                    cancel(q1, q2);
                    this.downstream.onError(e);
                    return;
                }
                if (this.f161v1 == null) {
                    this.f161v1 = q1.mo399poll();
                }
                boolean e1 = this.f161v1 == null;
                if (this.f162v2 == null) {
                    this.f162v2 = q2.mo399poll();
                }
                boolean e22 = this.f162v2 == null;
                if (d1 && d2 && e1 && e22) {
                    this.downstream.onSuccess(true);
                    return;
                } else if (d1 && d2 && e1 != e22) {
                    cancel(q1, q2);
                    this.downstream.onSuccess(false);
                    return;
                } else {
                    if (!e1 && !e22) {
                        try {
                            boolean c = this.comparer.test((T) this.f161v1, (T) this.f162v2);
                            if (!c) {
                                cancel(q1, q2);
                                this.downstream.onSuccess(false);
                                return;
                            }
                            this.f161v1 = null;
                            this.f162v2 = null;
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            cancel(q1, q2);
                            this.downstream.onError(ex);
                            return;
                        }
                    }
                    if (e1 || e22) {
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    }
                }
            }
            q1.clear();
            q2.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class EqualObserver<T> implements Observer<T> {
        volatile boolean done;
        Throwable error;
        final int index;
        final EqualCoordinator<T> parent;
        final SpscLinkedArrayQueue<T> queue;

        EqualObserver(EqualCoordinator<T> parent, int index, int bufferSize) {
            this.parent = parent;
            this.index = index;
            this.queue = new SpscLinkedArrayQueue<>(bufferSize);
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            this.parent.setDisposable(d, this.index);
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            this.queue.offer(t);
            this.parent.drain();
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            this.parent.drain();
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            this.done = true;
            this.parent.drain();
        }
    }
}