package io.reactivex.internal.operators.single;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import org.reactivestreams.Publisher;

/* loaded from: classes.dex */
public final class SingleInternalHelper {
    private SingleInternalHelper() {
        throw new IllegalStateException("No instances!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum NoSuchElementCallable implements Callable<NoSuchElementException> {
        INSTANCE;

        @Override // java.util.concurrent.Callable
        public NoSuchElementException call() throws Exception {
            return new NoSuchElementException();
        }
    }

    public static <T> Callable<NoSuchElementException> emptyThrower() {
        return NoSuchElementCallable.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ToFlowable implements Function<SingleSource, Publisher> {
        INSTANCE;

        @Override // io.reactivex.functions.Function
        /* renamed from: apply  reason: avoid collision after fix types in other method */
        public Publisher mo401apply(SingleSource v) {
            return new SingleToFlowable(v);
        }
    }

    public static <T> Function<SingleSource<? extends T>, Publisher<? extends T>> toFlowable() {
        return ToFlowable.INSTANCE;
    }

    /* loaded from: classes.dex */
    static final class ToFlowableIterator<T> implements Iterator<Flowable<T>> {
        private final Iterator<? extends SingleSource<? extends T>> sit;

        ToFlowableIterator(Iterator<? extends SingleSource<? extends T>> sit) {
            this.sit = sit;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.sit.hasNext();
        }

        @Override // java.util.Iterator
        /* renamed from: next */
        public Flowable<T> mo400next() {
            return new SingleToFlowable(this.sit.next());
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* loaded from: classes.dex */
    static final class ToFlowableIterable<T> implements Iterable<Flowable<T>> {
        private final Iterable<? extends SingleSource<? extends T>> sources;

        ToFlowableIterable(Iterable<? extends SingleSource<? extends T>> sources) {
            this.sources = sources;
        }

        @Override // java.lang.Iterable
        public Iterator<Flowable<T>> iterator() {
            return new ToFlowableIterator(this.sources.iterator());
        }
    }

    public static <T> Iterable<? extends Flowable<T>> iterableToFlowable(Iterable<? extends SingleSource<? extends T>> sources) {
        return new ToFlowableIterable(sources);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ToObservable implements Function<SingleSource, Observable> {
        INSTANCE;

        @Override // io.reactivex.functions.Function
        /* renamed from: apply  reason: avoid collision after fix types in other method */
        public Observable mo401apply(SingleSource v) {
            return new SingleToObservable(v);
        }
    }

    public static <T> Function<SingleSource<? extends T>, Observable<? extends T>> toObservable() {
        return ToObservable.INSTANCE;
    }
}