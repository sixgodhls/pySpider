package com.bumptech.glide.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class LruCache<T, Y> {
    private final Map<T, Y> cache = new LinkedHashMap(100, 0.75f, true);
    private long currentSize;
    private final long initialMaxSize;
    private long maxSize;

    public LruCache(long size) {
        this.initialMaxSize = size;
        this.maxSize = size;
    }

    public synchronized void setSizeMultiplier(float multiplier) {
        if (multiplier < 0.0f) {
            throw new IllegalArgumentException("Multiplier must be >= 0");
        }
        this.maxSize = Math.round(((float) this.initialMaxSize) * multiplier);
        evict();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getSize(@Nullable Y item) {
        return 1;
    }

    protected synchronized int getCount() {
        return this.cache.size();
    }

    protected void onItemEvicted(@NonNull T key, @Nullable Y item) {
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized long getCurrentSize() {
        return this.currentSize;
    }

    public synchronized boolean contains(@NonNull T key) {
        return this.cache.containsKey(key);
    }

    @Nullable
    public synchronized Y get(@NonNull T key) {
        return this.cache.get(key);
    }

    @Nullable
    public synchronized Y put(@NonNull T key, @Nullable Y item) {
        int itemSize = getSize(item);
        if (itemSize >= this.maxSize) {
            onItemEvicted(key, item);
            return null;
        }
        if (item != null) {
            this.currentSize += itemSize;
        }
        Y old = this.cache.put(key, item);
        if (old != null) {
            this.currentSize -= getSize(old);
            if (!old.equals(item)) {
                onItemEvicted(key, old);
            }
        }
        evict();
        return old;
    }

    @Nullable
    public synchronized Y remove(@NonNull T key) {
        Y value;
        value = this.cache.remove(key);
        if (value != null) {
            this.currentSize -= getSize(value);
        }
        return value;
    }

    public void clearMemory() {
        trimToSize(0L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void trimToSize(long size) {
        while (this.currentSize > size) {
            Iterator<Map.Entry<T, Y>> cacheIterator = this.cache.entrySet().iterator();
            Map.Entry<T, Y> last = cacheIterator.next();
            Y toRemove = last.getValue();
            this.currentSize -= getSize(toRemove);
            T key = last.getKey();
            cacheIterator.remove();
            onItemEvicted(key, toRemove);
        }
    }

    private void evict() {
        trimToSize(this.maxSize);
    }
}