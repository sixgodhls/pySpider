package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;

/* loaded from: classes.dex */
public final class SingleMap<T, R> extends Single<R> {
    final Function<? super T, ? extends R> mapper;
    final SingleSource<? extends T> source;

    public SingleMap(SingleSource<? extends T> source, Function<? super T, ? extends R> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override // io.reactivex.Single
    protected void subscribeActual(SingleObserver<? super R> t) {
        this.source.subscribe(new MapSingleObserver(t, this.mapper));
    }

    /* loaded from: classes.dex */
    static final class MapSingleObserver<T, R> implements SingleObserver<T> {
        final Function<? super T, ? extends R> mapper;
        final SingleObserver<? super R> t;

        /* JADX INFO: Access modifiers changed from: package-private */
        public MapSingleObserver(SingleObserver<? super R> t, Function<? super T, ? extends R> mapper) {
            this.t = t;
            this.mapper = mapper;
        }

        @Override // io.reactivex.SingleObserver
        public void onSubscribe(Disposable d) {
            this.t.onSubscribe(d);
        }

        @Override // io.reactivex.SingleObserver
        public void onSuccess(T value) {
            try {
                this.t.onSuccess(ObjectHelper.requireNonNull(this.mapper.mo339apply(value), "The mapper function returned a null value."));
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                onError(e);
            }
        }

        @Override // io.reactivex.SingleObserver
        public void onError(Throwable e) {
            this.t.onError(e);
        }
    }
}
