package com.bumptech.glide.load.engine.cache;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.util.LruCache;

/* loaded from: classes.dex */
public class LruResourceCache extends LruCache<Key, Resource<?>> implements MemoryCache {
    private MemoryCache.ResourceRemovedListener listener;

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache
    @Nullable
    public /* bridge */ /* synthetic */ Resource put(@NonNull Key key, @Nullable Resource resource) {
        return (Resource) super.put((LruResourceCache) key, (Key) resource);
    }

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache
    @Nullable
    public /* bridge */ /* synthetic */ Resource remove(@NonNull Key key) {
        return (Resource) super.remove((LruResourceCache) key);
    }

    public LruResourceCache(long size) {
        super(size);
    }

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache
    public void setResourceRemovedListener(@NonNull MemoryCache.ResourceRemovedListener listener) {
        this.listener = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.bumptech.glide.util.LruCache
    public void onItemEvicted(@NonNull Key key, @Nullable Resource<?> item) {
        MemoryCache.ResourceRemovedListener resourceRemovedListener = this.listener;
        if (resourceRemovedListener != null && item != null) {
            resourceRemovedListener.onResourceRemoved(item);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.bumptech.glide.util.LruCache
    public int getSize(@Nullable Resource<?> item) {
        if (item == null) {
            return super.getSize((LruResourceCache) null);
        }
        return item.getSize();
    }

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache
    @SuppressLint({"InlinedApi"})
    public void trimMemory(int level) {
        if (level >= 40) {
            clearMemory();
        } else if (level >= 20 || level == 15) {
            trimToSize(getMaxSize() / 2);
        }
    }
}