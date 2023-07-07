package com.bumptech.glide.provider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.util.ArrayMap;
import com.bumptech.glide.util.MultiClassKey;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public class ModelToResourceClassCache {
    private final AtomicReference<MultiClassKey> resourceClassKeyRef = new AtomicReference<>();
    private final ArrayMap<MultiClassKey, List<Class<?>>> registeredResourceClassCache = new ArrayMap<>();

    @Nullable
    public List<Class<?>> get(@NonNull Class<?> modelClass, @NonNull Class<?> resourceClass) {
        List<Class<?>> result;
        MultiClassKey key = this.resourceClassKeyRef.getAndSet(null);
        if (key == null) {
            key = new MultiClassKey(modelClass, resourceClass);
        } else {
            key.set(modelClass, resourceClass);
        }
        synchronized (this.registeredResourceClassCache) {
            result = this.registeredResourceClassCache.get(key);
        }
        this.resourceClassKeyRef.set(key);
        return result;
    }

    public void put(@NonNull Class<?> modelClass, @NonNull Class<?> resourceClass, @NonNull List<Class<?>> resourceClasses) {
        synchronized (this.registeredResourceClassCache) {
            this.registeredResourceClassCache.put(new MultiClassKey(modelClass, resourceClass), resourceClasses);
        }
    }

    public void clear() {
        synchronized (this.registeredResourceClassCache) {
            this.registeredResourceClassCache.clear();
        }
    }
}