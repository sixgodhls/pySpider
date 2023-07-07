package io.reactivex.parallel;

import io.reactivex.functions.BiFunction;

/* loaded from: classes.dex */
public enum ParallelFailureHandling implements BiFunction<Long, Throwable, ParallelFailureHandling> {
    STOP,
    ERROR,
    SKIP,
    RETRY;

    @Override // io.reactivex.functions.BiFunction
    /* renamed from: apply  reason: avoid collision after fix types in other method */
    public ParallelFailureHandling mo374apply(Long t1, Throwable t2) {
        return this;
    }
}