package com.bumptech.glide.load.engine.bitmap_recycle;

import com.bumptech.glide.load.engine.bitmap_recycle.Poolable;
import com.bumptech.glide.util.Util;
import java.util.Queue;

/* loaded from: classes.dex */
abstract class BaseKeyPool<T extends Poolable> {
    private static final int MAX_SIZE = 20;
    private final Queue<T> keyPool = Util.createQueue(20);

    /* renamed from: create */
    abstract T mo228create();

    /* JADX INFO: Access modifiers changed from: package-private */
    public T get() {
        T result = this.keyPool.poll();
        if (result == null) {
            return mo228create();
        }
        return result;
    }

    public void offer(T key) {
        if (this.keyPool.size() < 20) {
            this.keyPool.offer(key);
        }
    }
}
