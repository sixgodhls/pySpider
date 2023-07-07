package io.reactivex;

import io.reactivex.annotations.NonNull;

/* loaded from: classes.dex */
public interface ObservableConverter<T, R> {
    @NonNull
    R apply(@NonNull Observable<T> observable);
}
