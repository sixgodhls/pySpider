package io.reactivex.observables;

import io.reactivex.Observable;
import io.reactivex.annotations.Nullable;

/* loaded from: classes.dex */
public abstract class GroupedObservable<K, T> extends Observable<T> {
    final K key;

    /* JADX INFO: Access modifiers changed from: protected */
    public GroupedObservable(@Nullable K key) {
        this.key = key;
    }

    @Nullable
    public K getKey() {
        return this.key;
    }
}
