package com.bumptech.glide.util.pool;

import android.support.annotation.NonNull;
import android.support.p000v4.util.Pools;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class FactoryPools {
    private static final int DEFAULT_POOL_SIZE = 20;
    private static final Resetter<Object> EMPTY_RESETTER = new Resetter<Object>() { // from class: com.bumptech.glide.util.pool.FactoryPools.1
        @Override // com.bumptech.glide.util.pool.FactoryPools.Resetter
        public void reset(@NonNull Object object) {
        }
    };
    private static final String TAG = "FactoryPools";

    /* loaded from: classes.dex */
    public interface Factory<T> {
        /* renamed from: create */
        T mo304create();
    }

    /* loaded from: classes.dex */
    public interface Poolable {
        @NonNull
        StateVerifier getVerifier();
    }

    /* loaded from: classes.dex */
    public interface Resetter<T> {
        void reset(@NonNull T t);
    }

    private FactoryPools() {
    }

    @NonNull
    public static <T extends Poolable> Pools.Pool<T> simple(int size, @NonNull Factory<T> factory) {
        return build(new Pools.SimplePool(size), factory);
    }

    @NonNull
    public static <T extends Poolable> Pools.Pool<T> threadSafe(int size, @NonNull Factory<T> factory) {
        return build(new Pools.SynchronizedPool(size), factory);
    }

    @NonNull
    public static <T> Pools.Pool<List<T>> threadSafeList() {
        return threadSafeList(20);
    }

    @NonNull
    public static <T> Pools.Pool<List<T>> threadSafeList(int size) {
        return build(new Pools.SynchronizedPool(size), new Factory<List<T>>() { // from class: com.bumptech.glide.util.pool.FactoryPools.2
            @Override // com.bumptech.glide.util.pool.FactoryPools.Factory
            @NonNull
            /* renamed from: create  reason: collision with other method in class */
            public List<T> mo304create() {
                return new ArrayList();
            }
        }, new Resetter<List<T>>() { // from class: com.bumptech.glide.util.pool.FactoryPools.3
            @Override // com.bumptech.glide.util.pool.FactoryPools.Resetter
            public void reset(@NonNull List<T> object) {
                object.clear();
            }
        });
    }

    @NonNull
    private static <T extends Poolable> Pools.Pool<T> build(@NonNull Pools.Pool<T> pool, @NonNull Factory<T> factory) {
        return build(pool, factory, emptyResetter());
    }

    @NonNull
    private static <T> Pools.Pool<T> build(@NonNull Pools.Pool<T> pool, @NonNull Factory<T> factory, @NonNull Resetter<T> resetter) {
        return new FactoryPool(pool, factory, resetter);
    }

    @NonNull
    private static <T> Resetter<T> emptyResetter() {
        return (Resetter<T>) EMPTY_RESETTER;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class FactoryPool<T> implements Pools.Pool<T> {
        private final Factory<T> factory;
        private final Pools.Pool<T> pool;
        private final Resetter<T> resetter;

        FactoryPool(@NonNull Pools.Pool<T> pool, @NonNull Factory<T> factory, @NonNull Resetter<T> resetter) {
            this.pool = pool;
            this.factory = factory;
            this.resetter = resetter;
        }

        @Override // android.support.p000v4.util.Pools.Pool
        public T acquire() {
            T result = this.pool.acquire();
            if (result == null) {
                result = this.factory.mo304create();
                if (Log.isLoggable(FactoryPools.TAG, 2)) {
                    Log.v(FactoryPools.TAG, "Created new " + result.getClass());
                }
            }
            if (result instanceof Poolable) {
                ((Poolable) result).getVerifier().setRecycled(false);
            }
            return result;
        }

        @Override // android.support.p000v4.util.Pools.Pool
        public boolean release(@NonNull T instance) {
            if (instance instanceof Poolable) {
                ((Poolable) instance).getVerifier().setRecycled(true);
            }
            this.resetter.reset(instance);
            return this.pool.release(instance);
        }
    }
}