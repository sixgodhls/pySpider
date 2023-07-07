package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import android.support.p000v4.util.Pools;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.StateVerifier;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class LockedResource<Z> implements Resource<Z>, FactoryPools.Poolable {
    private static final Pools.Pool<LockedResource<?>> POOL = FactoryPools.threadSafe(20, new FactoryPools.Factory<LockedResource<?>>() { // from class: com.bumptech.glide.load.engine.LockedResource.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.bumptech.glide.util.pool.FactoryPools.Factory
        /* renamed from: create */
        public LockedResource<?> mo304create() {
            return new LockedResource<>();
        }
    });
    private boolean isLocked;
    private boolean isRecycled;
    private final StateVerifier stateVerifier = StateVerifier.newInstance();
    private Resource<Z> toWrap;

    /* JADX INFO: Access modifiers changed from: package-private */
    @NonNull
    public static <Z> LockedResource<Z> obtain(Resource<Z> resource) {
        LockedResource<Z> result = (LockedResource) Preconditions.checkNotNull(POOL.acquire());
        result.init(resource);
        return result;
    }

    LockedResource() {
    }

    private void init(Resource<Z> toWrap) {
        this.isRecycled = false;
        this.isLocked = true;
        this.toWrap = toWrap;
    }

    private void release() {
        this.toWrap = null;
        POOL.release(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void unlock() {
        this.stateVerifier.throwIfRecycled();
        if (!this.isLocked) {
            throw new IllegalStateException("Already unlocked");
        }
        this.isLocked = false;
        if (this.isRecycled) {
            recycle();
        }
    }

    @Override // com.bumptech.glide.load.engine.Resource
    @NonNull
    public Class<Z> getResourceClass() {
        return this.toWrap.getResourceClass();
    }

    @Override // com.bumptech.glide.load.engine.Resource
    @NonNull
    /* renamed from: get */
    public Z mo301get() {
        return this.toWrap.mo301get();
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public int getSize() {
        return this.toWrap.getSize();
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public synchronized void recycle() {
        this.stateVerifier.throwIfRecycled();
        this.isRecycled = true;
        if (!this.isLocked) {
            this.toWrap.recycle();
            release();
        }
    }

    @Override // com.bumptech.glide.util.pool.FactoryPools.Poolable
    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }
}