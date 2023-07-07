package io.reactivex.internal.operators.maybe;

import io.reactivex.MaybeSource;
import io.reactivex.functions.Function;
import org.reactivestreams.Publisher;

/* loaded from: classes.dex */
public enum MaybeToPublisher implements Function<MaybeSource<Object>, Publisher<Object>> {
    INSTANCE;

    public static <T> Function<MaybeSource<T>, Publisher<T>> instance() {
        return INSTANCE;
    }

    @Override // io.reactivex.functions.Function
    /* renamed from: apply  reason: avoid collision after fix types in other method */
    public Publisher<Object> mo401apply(MaybeSource<Object> t) throws Exception {
        return new MaybeToFlowable(t);
    }
}