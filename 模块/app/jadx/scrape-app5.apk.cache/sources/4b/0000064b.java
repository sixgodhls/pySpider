package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.view.View;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.manager.ConnectivityMonitor;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.LifecycleListener;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.manager.RequestTracker;
import com.bumptech.glide.manager.TargetTracker;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;

/* loaded from: classes.dex */
public class RequestManager implements LifecycleListener, ModelTypes<RequestBuilder<Drawable>> {
    private static final RequestOptions DECODE_TYPE_BITMAP = RequestOptions.decodeTypeOf(Bitmap.class).lock();
    private static final RequestOptions DECODE_TYPE_GIF = RequestOptions.decodeTypeOf(GifDrawable.class).lock();
    private static final RequestOptions DOWNLOAD_ONLY_OPTIONS = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA).priority(Priority.LOW).skipMemoryCache(true);
    private final Runnable addSelfToLifecycle;
    private final ConnectivityMonitor connectivityMonitor;
    protected final Context context;
    protected final Glide glide;
    final Lifecycle lifecycle;
    private final Handler mainHandler;
    private RequestOptions requestOptions;
    private final RequestTracker requestTracker;
    private final TargetTracker targetTracker;
    private final RequestManagerTreeNode treeNode;

    public RequestManager(@NonNull Glide glide, @NonNull Lifecycle lifecycle, @NonNull RequestManagerTreeNode treeNode, @NonNull Context context) {
        this(glide, lifecycle, treeNode, new RequestTracker(), glide.getConnectivityMonitorFactory(), context);
    }

    RequestManager(Glide glide, Lifecycle lifecycle, RequestManagerTreeNode treeNode, RequestTracker requestTracker, ConnectivityMonitorFactory factory, Context context) {
        this.targetTracker = new TargetTracker();
        this.addSelfToLifecycle = new Runnable() { // from class: com.bumptech.glide.RequestManager.1
            {
                RequestManager.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                RequestManager.this.lifecycle.addListener(RequestManager.this);
            }
        };
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.glide = glide;
        this.lifecycle = lifecycle;
        this.treeNode = treeNode;
        this.requestTracker = requestTracker;
        this.context = context;
        this.connectivityMonitor = factory.build(context.getApplicationContext(), new RequestManagerConnectivityListener(requestTracker));
        if (Util.isOnBackgroundThread()) {
            this.mainHandler.post(this.addSelfToLifecycle);
        } else {
            lifecycle.addListener(this);
        }
        lifecycle.addListener(this.connectivityMonitor);
        setRequestOptions(glide.getGlideContext().getDefaultRequestOptions());
        glide.registerRequestManager(this);
    }

    protected void setRequestOptions(@NonNull RequestOptions toSet) {
        this.requestOptions = toSet.m302clone().autoClone();
    }

    private void updateRequestOptions(@NonNull RequestOptions toUpdate) {
        this.requestOptions = this.requestOptions.apply(toUpdate);
    }

    @NonNull
    public RequestManager applyDefaultRequestOptions(@NonNull RequestOptions requestOptions) {
        updateRequestOptions(requestOptions);
        return this;
    }

    @NonNull
    public RequestManager setDefaultRequestOptions(@NonNull RequestOptions requestOptions) {
        setRequestOptions(requestOptions);
        return this;
    }

    public boolean isPaused() {
        Util.assertMainThread();
        return this.requestTracker.isPaused();
    }

    public void pauseRequests() {
        Util.assertMainThread();
        this.requestTracker.pauseRequests();
    }

    public void pauseAllRequests() {
        Util.assertMainThread();
        this.requestTracker.pauseAllRequests();
    }

    public void pauseRequestsRecursive() {
        Util.assertMainThread();
        pauseRequests();
        for (RequestManager requestManager : this.treeNode.getDescendants()) {
            requestManager.pauseRequests();
        }
    }

    public void resumeRequests() {
        Util.assertMainThread();
        this.requestTracker.resumeRequests();
    }

    public void resumeRequestsRecursive() {
        Util.assertMainThread();
        resumeRequests();
        for (RequestManager requestManager : this.treeNode.getDescendants()) {
            requestManager.resumeRequests();
        }
    }

    @Override // com.bumptech.glide.manager.LifecycleListener
    public void onStart() {
        resumeRequests();
        this.targetTracker.onStart();
    }

    @Override // com.bumptech.glide.manager.LifecycleListener
    public void onStop() {
        pauseRequests();
        this.targetTracker.onStop();
    }

    @Override // com.bumptech.glide.manager.LifecycleListener
    public void onDestroy() {
        this.targetTracker.onDestroy();
        for (Target<?> target : this.targetTracker.getAll()) {
            clear(target);
        }
        this.targetTracker.clear();
        this.requestTracker.clearRequests();
        this.lifecycle.removeListener(this);
        this.lifecycle.removeListener(this.connectivityMonitor);
        this.mainHandler.removeCallbacks(this.addSelfToLifecycle);
        this.glide.unregisterRequestManager(this);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<Bitmap> asBitmap() {
        return m44as(Bitmap.class).apply(DECODE_TYPE_BITMAP);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<GifDrawable> asGif() {
        return m44as(GifDrawable.class).apply(DECODE_TYPE_GIF);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<Drawable> asDrawable() {
        return m44as(Drawable.class);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<Drawable> mo268load(@Nullable Bitmap bitmap) {
        return asDrawable().mo268load(bitmap);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<Drawable> mo269load(@Nullable Drawable drawable) {
        return asDrawable().mo269load(drawable);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<Drawable> mo274load(@Nullable String string) {
        return asDrawable().mo274load(string);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<Drawable> mo270load(@Nullable Uri uri) {
        return asDrawable().mo270load(uri);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<Drawable> mo271load(@Nullable File file) {
        return asDrawable().mo271load(file);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<Drawable> mo272load(@RawRes @DrawableRes @Nullable Integer resourceId) {
        return asDrawable().mo272load(resourceId);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @Deprecated
    /* renamed from: load */
    public RequestBuilder<Drawable> mo275load(@Nullable URL url) {
        return asDrawable().mo275load(url);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<Drawable> mo276load(@Nullable byte[] model) {
        return asDrawable().mo276load(model);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<Drawable> mo273load(@Nullable Object model) {
        return asDrawable().mo273load(model);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<File> downloadOnly() {
        return m44as(File.class).apply(DOWNLOAD_ONLY_OPTIONS);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<File> download(@Nullable Object model) {
        return downloadOnly().mo273load(model);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<File> asFile() {
        return m44as(File.class).apply(RequestOptions.skipMemoryCacheOf(true));
    }

    @CheckResult
    @NonNull
    /* renamed from: as */
    public <ResourceType> RequestBuilder<ResourceType> m44as(@NonNull Class<ResourceType> resourceClass) {
        return new RequestBuilder<>(this.glide, this, resourceClass, this.context);
    }

    public void clear(@NonNull View view) {
        clear(new ClearTarget(view));
    }

    public void clear(@Nullable final Target<?> target) {
        if (target == null) {
            return;
        }
        if (Util.isOnMainThread()) {
            untrackOrDelegate(target);
        } else {
            this.mainHandler.post(new Runnable() { // from class: com.bumptech.glide.RequestManager.2
                {
                    RequestManager.this = this;
                }

                @Override // java.lang.Runnable
                public void run() {
                    RequestManager.this.clear(target);
                }
            });
        }
    }

    private void untrackOrDelegate(@NonNull Target<?> target) {
        boolean isOwnedByUs = untrack(target);
        if (!isOwnedByUs && !this.glide.removeFromManagers(target) && target.getRequest() != null) {
            Request request = target.getRequest();
            target.setRequest(null);
            request.clear();
        }
    }

    public boolean untrack(@NonNull Target<?> target) {
        Request request = target.getRequest();
        if (request == null) {
            return true;
        }
        if (this.requestTracker.clearRemoveAndRecycle(request)) {
            this.targetTracker.untrack(target);
            target.setRequest(null);
            return true;
        }
        return false;
    }

    public void track(@NonNull Target<?> target, @NonNull Request request) {
        this.targetTracker.track(target);
        this.requestTracker.runRequest(request);
    }

    public RequestOptions getDefaultRequestOptions() {
        return this.requestOptions;
    }

    @NonNull
    public <T> TransitionOptions<?, T> getDefaultTransitionOptions(Class<T> transcodeClass) {
        return this.glide.getGlideContext().getDefaultTransitionOptions(transcodeClass);
    }

    public String toString() {
        return super.toString() + "{tracker=" + this.requestTracker + ", treeNode=" + this.treeNode + "}";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RequestManagerConnectivityListener implements ConnectivityMonitor.ConnectivityListener {
        private final RequestTracker requestTracker;

        RequestManagerConnectivityListener(@NonNull RequestTracker requestTracker) {
            this.requestTracker = requestTracker;
        }

        @Override // com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener
        public void onConnectivityChanged(boolean isConnected) {
            if (isConnected) {
                this.requestTracker.restartRequests();
            }
        }
    }

    /* loaded from: classes.dex */
    private static class ClearTarget extends ViewTarget<View, Object> {
        ClearTarget(@NonNull View view) {
            super(view);
        }

        @Override // com.bumptech.glide.request.target.Target
        public void onResourceReady(@NonNull Object resource, @Nullable Transition<? super Object> transition) {
        }
    }
}