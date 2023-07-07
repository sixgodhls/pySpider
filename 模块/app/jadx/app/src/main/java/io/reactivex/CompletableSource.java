package io.reactivex;

import io.reactivex.annotations.NonNull;

/* loaded from: classes.dex */
public interface CompletableSource {
    void subscribe(@NonNull CompletableObserver completableObserver);
}
