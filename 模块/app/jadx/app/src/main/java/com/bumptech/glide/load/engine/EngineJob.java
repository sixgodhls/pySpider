package com.bumptech.glide.load.engine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pools;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DecodeJob;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
class EngineJob<R> implements DecodeJob.Callback<R>, FactoryPools.Poolable {
    private static final EngineResourceFactory DEFAULT_FACTORY = new EngineResourceFactory();
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper(), new MainThreadCallback());
    private static final int MSG_CANCELLED = 3;
    private static final int MSG_COMPLETE = 1;
    private static final int MSG_EXCEPTION = 2;
    private final GlideExecutor animationExecutor;
    private final List<ResourceCallback> cbs;
    private DataSource dataSource;
    private DecodeJob<R> decodeJob;
    private final GlideExecutor diskCacheExecutor;
    private EngineResource<?> engineResource;
    private final EngineResourceFactory engineResourceFactory;
    private GlideException exception;
    private boolean hasLoadFailed;
    private boolean hasResource;
    private List<ResourceCallback> ignoredCallbacks;
    private boolean isCacheable;
    private volatile boolean isCancelled;
    private Key key;
    private final EngineJobListener listener;
    private boolean onlyRetrieveFromCache;
    private final Pools.Pool<EngineJob<?>> pool;
    private Resource<?> resource;
    private final GlideExecutor sourceExecutor;
    private final GlideExecutor sourceUnlimitedExecutor;
    private final StateVerifier stateVerifier;
    private boolean useAnimationPool;
    private boolean useUnlimitedSourceGeneratorPool;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EngineJob(GlideExecutor diskCacheExecutor, GlideExecutor sourceExecutor, GlideExecutor sourceUnlimitedExecutor, GlideExecutor animationExecutor, EngineJobListener listener, Pools.Pool<EngineJob<?>> pool) {
        this(diskCacheExecutor, sourceExecutor, sourceUnlimitedExecutor, animationExecutor, listener, pool, DEFAULT_FACTORY);
    }

    @VisibleForTesting
    EngineJob(GlideExecutor diskCacheExecutor, GlideExecutor sourceExecutor, GlideExecutor sourceUnlimitedExecutor, GlideExecutor animationExecutor, EngineJobListener listener, Pools.Pool<EngineJob<?>> pool, EngineResourceFactory engineResourceFactory) {
        this.cbs = new ArrayList(2);
        this.stateVerifier = StateVerifier.newInstance();
        this.diskCacheExecutor = diskCacheExecutor;
        this.sourceExecutor = sourceExecutor;
        this.sourceUnlimitedExecutor = sourceUnlimitedExecutor;
        this.animationExecutor = animationExecutor;
        this.listener = listener;
        this.pool = pool;
        this.engineResourceFactory = engineResourceFactory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public EngineJob<R> init(Key key, boolean isCacheable, boolean useUnlimitedSourceGeneratorPool, boolean useAnimationPool, boolean onlyRetrieveFromCache) {
        this.key = key;
        this.isCacheable = isCacheable;
        this.useUnlimitedSourceGeneratorPool = useUnlimitedSourceGeneratorPool;
        this.useAnimationPool = useAnimationPool;
        this.onlyRetrieveFromCache = onlyRetrieveFromCache;
        return this;
    }

    public void start(DecodeJob<R> decodeJob) {
        GlideExecutor executor;
        this.decodeJob = decodeJob;
        if (decodeJob.willDecodeFromCache()) {
            executor = this.diskCacheExecutor;
        } else {
            executor = getActiveSourceExecutor();
        }
        executor.execute(decodeJob);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addCallback(ResourceCallback cb) {
        Util.assertMainThread();
        this.stateVerifier.throwIfRecycled();
        if (this.hasResource) {
            cb.onResourceReady(this.engineResource, this.dataSource);
        } else if (this.hasLoadFailed) {
            cb.onLoadFailed(this.exception);
        } else {
            this.cbs.add(cb);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeCallback(ResourceCallback cb) {
        Util.assertMainThread();
        this.stateVerifier.throwIfRecycled();
        if (this.hasResource || this.hasLoadFailed) {
            addIgnoredCallback(cb);
            return;
        }
        this.cbs.remove(cb);
        if (this.cbs.isEmpty()) {
            cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onlyRetrieveFromCache() {
        return this.onlyRetrieveFromCache;
    }

    private GlideExecutor getActiveSourceExecutor() {
        if (this.useUnlimitedSourceGeneratorPool) {
            return this.sourceUnlimitedExecutor;
        }
        return this.useAnimationPool ? this.animationExecutor : this.sourceExecutor;
    }

    private void addIgnoredCallback(ResourceCallback cb) {
        if (this.ignoredCallbacks == null) {
            this.ignoredCallbacks = new ArrayList(2);
        }
        if (!this.ignoredCallbacks.contains(cb)) {
            this.ignoredCallbacks.add(cb);
        }
    }

    private boolean isInIgnoredCallbacks(ResourceCallback cb) {
        List<ResourceCallback> list = this.ignoredCallbacks;
        return list != null && list.contains(cb);
    }

    void cancel() {
        if (this.hasLoadFailed || this.hasResource || this.isCancelled) {
            return;
        }
        this.isCancelled = true;
        this.decodeJob.cancel();
        this.listener.onEngineJobCancelled(this, this.key);
    }

    boolean isCancelled() {
        return this.isCancelled;
    }

    void handleResultOnMainThread() {
        this.stateVerifier.throwIfRecycled();
        if (this.isCancelled) {
            this.resource.recycle();
            release(false);
        } else if (this.cbs.isEmpty()) {
            throw new IllegalStateException("Received a resource without any callbacks to notify");
        } else {
            if (this.hasResource) {
                throw new IllegalStateException("Already have resource");
            }
            this.engineResource = this.engineResourceFactory.build(this.resource, this.isCacheable);
            this.hasResource = true;
            this.engineResource.acquire();
            this.listener.onEngineJobComplete(this, this.key, this.engineResource);
            int size = this.cbs.size();
            for (int i = 0; i < size; i++) {
                ResourceCallback cb = this.cbs.get(i);
                if (!isInIgnoredCallbacks(cb)) {
                    this.engineResource.acquire();
                    cb.onResourceReady(this.engineResource, this.dataSource);
                }
            }
            this.engineResource.release();
            release(false);
        }
    }

    void handleCancelledOnMainThread() {
        this.stateVerifier.throwIfRecycled();
        if (!this.isCancelled) {
            throw new IllegalStateException("Not cancelled");
        }
        this.listener.onEngineJobCancelled(this, this.key);
        release(false);
    }

    private void release(boolean isRemovedFromQueue) {
        Util.assertMainThread();
        this.cbs.clear();
        this.key = null;
        this.engineResource = null;
        this.resource = null;
        List<ResourceCallback> list = this.ignoredCallbacks;
        if (list != null) {
            list.clear();
        }
        this.hasLoadFailed = false;
        this.isCancelled = false;
        this.hasResource = false;
        this.decodeJob.release(isRemovedFromQueue);
        this.decodeJob = null;
        this.exception = null;
        this.dataSource = null;
        this.pool.release(this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.load.engine.DecodeJob.Callback
    public void onResourceReady(Resource<R> resource, DataSource dataSource) {
        this.resource = resource;
        this.dataSource = dataSource;
        MAIN_THREAD_HANDLER.obtainMessage(1, this).sendToTarget();
    }

    @Override // com.bumptech.glide.load.engine.DecodeJob.Callback
    public void onLoadFailed(GlideException e) {
        this.exception = e;
        MAIN_THREAD_HANDLER.obtainMessage(2, this).sendToTarget();
    }

    @Override // com.bumptech.glide.load.engine.DecodeJob.Callback
    public void reschedule(DecodeJob<?> job) {
        getActiveSourceExecutor().execute(job);
    }

    void handleExceptionOnMainThread() {
        this.stateVerifier.throwIfRecycled();
        if (this.isCancelled) {
            release(false);
        } else if (this.cbs.isEmpty()) {
            throw new IllegalStateException("Received an exception without any callbacks to notify");
        } else {
            if (this.hasLoadFailed) {
                throw new IllegalStateException("Already failed once");
            }
            this.hasLoadFailed = true;
            this.listener.onEngineJobComplete(this, this.key, null);
            for (ResourceCallback cb : this.cbs) {
                if (!isInIgnoredCallbacks(cb)) {
                    cb.onLoadFailed(this.exception);
                }
            }
            release(false);
        }
    }

    @Override // com.bumptech.glide.util.pool.FactoryPools.Poolable
    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class EngineResourceFactory {
        EngineResourceFactory() {
        }

        public <R> EngineResource<R> build(Resource<R> resource, boolean isMemoryCacheable) {
            return new EngineResource<>(resource, isMemoryCacheable, true);
        }
    }

    /* loaded from: classes.dex */
    private static class MainThreadCallback implements Handler.Callback {
        MainThreadCallback() {
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            EngineJob<?> job = (EngineJob) message.obj;
            switch (message.what) {
                case 1:
                    job.handleResultOnMainThread();
                    return true;
                case 2:
                    job.handleExceptionOnMainThread();
                    return true;
                case 3:
                    job.handleCancelledOnMainThread();
                    return true;
                default:
                    throw new IllegalStateException("Unrecognized message: " + message.what);
            }
        }
    }
}
