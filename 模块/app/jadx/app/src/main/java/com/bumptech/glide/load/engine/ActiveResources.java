package com.bumptech.glide.load.engine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.EngineResource;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
final class ActiveResources {
    private static final int MSG_CLEAN_REF = 1;
    @Nullable
    private volatile DequeuedResourceCallback cb;
    @Nullable
    private Thread cleanReferenceQueueThread;
    private final boolean isActiveResourceRetentionAllowed;
    private volatile boolean isShutdown;
    private EngineResource.ResourceListener listener;
    @Nullable
    private ReferenceQueue<EngineResource<?>> resourceReferenceQueue;
    private final Handler mainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: com.bumptech.glide.load.engine.ActiveResources.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                ActiveResources.this.cleanupActiveReference((ResourceWeakReference) msg.obj);
                return true;
            }
            return false;
        }
    });
    @VisibleForTesting
    final Map<Key, ResourceWeakReference> activeEngineResources = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface DequeuedResourceCallback {
        void onResourceDequeued();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActiveResources(boolean isActiveResourceRetentionAllowed) {
        this.isActiveResourceRetentionAllowed = isActiveResourceRetentionAllowed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setListener(EngineResource.ResourceListener listener) {
        this.listener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void activate(Key key, EngineResource<?> resource) {
        ResourceWeakReference toPut = new ResourceWeakReference(key, resource, getReferenceQueue(), this.isActiveResourceRetentionAllowed);
        ResourceWeakReference removed = this.activeEngineResources.put(key, toPut);
        if (removed != null) {
            removed.reset();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void deactivate(Key key) {
        ResourceWeakReference removed = this.activeEngineResources.remove(key);
        if (removed != null) {
            removed.reset();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public EngineResource<?> get(Key key) {
        ResourceWeakReference activeRef = this.activeEngineResources.get(key);
        if (activeRef == null) {
            return null;
        }
        EngineResource<?> active = (EngineResource) activeRef.get();
        if (active == null) {
            cleanupActiveReference(activeRef);
        }
        return active;
    }

    void cleanupActiveReference(@NonNull ResourceWeakReference ref) {
        Util.assertMainThread();
        this.activeEngineResources.remove(ref.key);
        if (!ref.isCacheable || ref.resource == null) {
            return;
        }
        EngineResource<?> newResource = new EngineResource<>(ref.resource, true, false);
        newResource.setResourceListener(ref.key, this.listener);
        this.listener.onResourceReleased(ref.key, newResource);
    }

    private ReferenceQueue<EngineResource<?>> getReferenceQueue() {
        if (this.resourceReferenceQueue == null) {
            this.resourceReferenceQueue = new ReferenceQueue<>();
            this.cleanReferenceQueueThread = new Thread(new Runnable() { // from class: com.bumptech.glide.load.engine.ActiveResources.2
                @Override // java.lang.Runnable
                public void run() {
                    Process.setThreadPriority(10);
                    ActiveResources.this.cleanReferenceQueue();
                }
            }, "glide-active-resources");
            this.cleanReferenceQueueThread.start();
        }
        return this.resourceReferenceQueue;
    }

    void cleanReferenceQueue() {
        while (!this.isShutdown) {
            try {
                ResourceWeakReference ref = (ResourceWeakReference) this.resourceReferenceQueue.remove();
                this.mainHandler.obtainMessage(1, ref).sendToTarget();
                DequeuedResourceCallback current = this.cb;
                if (current != null) {
                    current.onResourceDequeued();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @VisibleForTesting
    void setDequeuedResourceCallback(DequeuedResourceCallback cb) {
        this.cb = cb;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public void shutdown() {
        this.isShutdown = true;
        Thread thread = this.cleanReferenceQueueThread;
        if (thread == null) {
            return;
        }
        thread.interrupt();
        try {
            this.cleanReferenceQueueThread.join(TimeUnit.SECONDS.toMillis(5L));
            if (this.cleanReferenceQueueThread.isAlive()) {
                throw new RuntimeException("Failed to join in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class ResourceWeakReference extends WeakReference<EngineResource<?>> {
        final boolean isCacheable;
        final Key key;
        @Nullable
        Resource<?> resource;

        ResourceWeakReference(@NonNull Key key, @NonNull EngineResource<?> referent, @NonNull ReferenceQueue<? super EngineResource<?>> queue, boolean isActiveResourceRetentionAllowed) {
            super(referent, queue);
            this.key = (Key) Preconditions.checkNotNull(key);
            this.resource = (!referent.isCacheable() || !isActiveResourceRetentionAllowed) ? null : (Resource) Preconditions.checkNotNull(referent.getResource());
            this.isCacheable = referent.isCacheable();
        }

        void reset() {
            this.resource = null;
            clear();
        }
    }
}
