package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.util.Queue;

/* loaded from: classes.dex */
public class ModelCache<A, B> {
    private static final int DEFAULT_SIZE = 250;
    private final LruCache<ModelKey<A>, B> cache;

    public ModelCache() {
        this(250L);
    }

    public ModelCache(long size) {
        this.cache = new LruCache<ModelKey<A>, B>(size) { // from class: com.bumptech.glide.load.model.ModelCache.1
            @Override // com.bumptech.glide.util.LruCache
            protected /* bridge */ /* synthetic */ void onItemEvicted(@NonNull Object obj, @Nullable Object obj2) {
                onItemEvicted((ModelKey) ((ModelKey) obj), (ModelKey<A>) obj2);
            }

            protected void onItemEvicted(@NonNull ModelKey<A> key, @Nullable B item) {
                key.release();
            }
        };
    }

    @Nullable
    public B get(A model, int width, int height) {
        ModelKey<A> key = ModelKey.get(model, width, height);
        B result = this.cache.get(key);
        key.release();
        return result;
    }

    public void put(A model, int width, int height, B value) {
        ModelKey<A> key = ModelKey.get(model, width, height);
        this.cache.put(key, value);
    }

    public void clear() {
        this.cache.clearMemory();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class ModelKey<A> {
        private static final Queue<ModelKey<?>> KEY_QUEUE = Util.createQueue(0);
        private int height;
        private A model;
        private int width;

        static <A> ModelKey<A> get(A model, int width, int height) {
            ModelKey<?> poll;
            synchronized (KEY_QUEUE) {
                poll = KEY_QUEUE.poll();
            }
            if (poll == null) {
                poll = new ModelKey();
            }
            poll.init(model, width, height);
            return poll;
        }

        private ModelKey() {
        }

        private void init(A model, int width, int height) {
            this.model = model;
            this.width = width;
            this.height = height;
        }

        public void release() {
            synchronized (KEY_QUEUE) {
                KEY_QUEUE.offer(this);
            }
        }

        public boolean equals(Object o) {
            if (o instanceof ModelKey) {
                ModelKey<A> other = (ModelKey) o;
                return this.width == other.width && this.height == other.height && this.model.equals(other.model);
            }
            return false;
        }

        public int hashCode() {
            int result = this.height;
            return (((result * 31) + this.width) * 31) + this.model.hashCode();
        }
    }
}
