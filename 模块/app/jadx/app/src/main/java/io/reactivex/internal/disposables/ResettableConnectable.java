package io.reactivex.internal.disposables;

import io.reactivex.annotations.Experimental;
import io.reactivex.disposables.Disposable;

@Experimental
/* loaded from: classes.dex */
public interface ResettableConnectable {
    void resetIf(Disposable disposable);
}
