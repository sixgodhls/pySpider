package io.reactivex.internal.operators.maybe;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public final class MaybeAmb<T> extends Maybe<T> {
    private final MaybeSource<? extends T>[] sources;
    private final Iterable<? extends MaybeSource<? extends T>> sourcesIterable;

    public MaybeAmb(MaybeSource<? extends T>[] sources, Iterable<? extends MaybeSource<? extends T>> sourcesIterable) {
        this.sources = sources;
        this.sourcesIterable = sourcesIterable;
    }

    @Override // io.reactivex.Maybe
    protected void subscribeActual(MaybeObserver<? super T> observer) {
        MaybeSource<? extends T>[] sources = this.sources;
        int count = 0;
        if (sources == null) {
            sources = new MaybeSource[8];
            try {
                for (MaybeSource<? extends T> element : this.sourcesIterable) {
                    if (element == null) {
                        EmptyDisposable.error(new NullPointerException("One of the sources is null"), observer);
                        return;
                    }
                    if (count == sources.length) {
                        MaybeSource<? extends T>[] b = new MaybeSource[(count >> 2) + count];
                        System.arraycopy(sources, 0, b, 0, count);
                        sources = b;
                    }
                    int count2 = count + 1;
                    try {
                        sources[count] = element;
                        count = count2;
                    } catch (Throwable th) {
                        e = th;
                        Exceptions.throwIfFatal(e);
                        EmptyDisposable.error(e, observer);
                        return;
                    }
                }
            } catch (Throwable th2) {
                e = th2;
            }
        } else {
            count = sources.length;
        }
        AmbMaybeObserver<T> parent = new AmbMaybeObserver<>(observer);
        observer.onSubscribe(parent);
        for (int i = 0; i < count; i++) {
            MaybeSource<? extends T> s = sources[i];
            if (parent.isDisposed()) {
                return;
            }
            if (s == null) {
                parent.onError(new NullPointerException("One of the MaybeSources is null"));
                return;
            }
            s.subscribe(parent);
        }
        if (count == 0) {
            observer.onComplete();
        }
    }

    /* loaded from: classes.dex */
    static final class AmbMaybeObserver<T> extends AtomicBoolean implements MaybeObserver<T>, Disposable {
        private static final long serialVersionUID = -7044685185359438206L;
        final MaybeObserver<? super T> downstream;
        final CompositeDisposable set = new CompositeDisposable();

        AmbMaybeObserver(MaybeObserver<? super T> downstream) {
            this.downstream = downstream;
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            if (compareAndSet(false, true)) {
                this.set.dispose();
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return get();
        }

        @Override // io.reactivex.MaybeObserver
        public void onSubscribe(Disposable d) {
            this.set.add(d);
        }

        @Override // io.reactivex.MaybeObserver
        public void onSuccess(T value) {
            if (compareAndSet(false, true)) {
                this.set.dispose();
                this.downstream.onSuccess(value);
            }
        }

        @Override // io.reactivex.MaybeObserver
        public void onError(Throwable e) {
            if (compareAndSet(false, true)) {
                this.set.dispose();
                this.downstream.onError(e);
                return;
            }
            RxJavaPlugins.onError(e);
        }

        @Override // io.reactivex.MaybeObserver
        public void onComplete() {
            if (compareAndSet(false, true)) {
                this.set.dispose();
                this.downstream.onComplete();
            }
        }
    }
}