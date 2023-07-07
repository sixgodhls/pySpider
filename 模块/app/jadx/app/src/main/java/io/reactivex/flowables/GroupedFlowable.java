package io.reactivex.flowables;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;

/* loaded from: classes.dex */
public abstract class GroupedFlowable<K, T> extends Flowable<T> {
    final K key;

    /* JADX INFO: Access modifiers changed from: protected */
    public GroupedFlowable(@Nullable K key) {
        this.key = key;
    }

    @Nullable
    public K getKey() {
        return this.key;
    }
}
