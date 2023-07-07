package io.reactivex;

import io.reactivex.annotations.NonNull;

/* loaded from: classes.dex */
public interface SingleConverter<T, R> {
    @NonNull
    R apply(@NonNull Single<T> single);
}
