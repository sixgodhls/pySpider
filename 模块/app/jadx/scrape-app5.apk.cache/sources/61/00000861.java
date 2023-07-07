package com.bumptech.glide.request;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.util.Pools;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.List;

/* loaded from: classes.dex */
public final class SingleRequest<R> implements Request, SizeReadyCallback, ResourceCallback, FactoryPools.Poolable {
    private static final String GLIDE_TAG = "Glide";
    private TransitionFactory<? super R> animationFactory;
    private Context context;
    private Engine engine;
    private Drawable errorDrawable;
    private Drawable fallbackDrawable;
    private GlideContext glideContext;
    private int height;
    private boolean isCallingCallbacks;
    private Engine.LoadStatus loadStatus;
    @Nullable
    private Object model;
    private int overrideHeight;
    private int overrideWidth;
    private Drawable placeholderDrawable;
    private Priority priority;
    private RequestCoordinator requestCoordinator;
    @Nullable
    private List<RequestListener<R>> requestListeners;
    private RequestOptions requestOptions;
    private Resource<R> resource;
    private long startTime;
    private final StateVerifier stateVerifier;
    private Status status;
    @Nullable
    private final String tag;
    private Target<R> target;
    @Nullable
    private RequestListener<R> targetListener;
    private Class<R> transcodeClass;
    private int width;
    private static final Pools.Pool<SingleRequest<?>> POOL = FactoryPools.simple(150, new FactoryPools.Factory<SingleRequest<?>>() { // from class: com.bumptech.glide.request.SingleRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.bumptech.glide.util.pool.FactoryPools.Factory
        /* renamed from: create */
        public SingleRequest<?> mo304create() {
            return new SingleRequest<>();
        }
    });
    private static final String TAG = "Request";
    private static final boolean IS_VERBOSE_LOGGABLE = Log.isLoggable(TAG, 2);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Status {
        PENDING,
        RUNNING,
        WAITING_FOR_SIZE,
        COMPLETE,
        FAILED,
        CLEARED
    }

    public static <R> SingleRequest<R> obtain(Context context, GlideContext glideContext, Object model, Class<R> transcodeClass, RequestOptions requestOptions, int overrideWidth, int overrideHeight, Priority priority, Target<R> target, RequestListener<R> targetListener, @Nullable List<RequestListener<R>> requestListeners, RequestCoordinator requestCoordinator, Engine engine, TransitionFactory<? super R> animationFactory) {
        SingleRequest<?> acquire = POOL.acquire();
        if (acquire == null) {
            acquire = new SingleRequest();
        }
        acquire.init(context, glideContext, model, transcodeClass, requestOptions, overrideWidth, overrideHeight, priority, target, targetListener, requestListeners, requestCoordinator, engine, animationFactory);
        return acquire;
    }

    SingleRequest() {
        this.tag = IS_VERBOSE_LOGGABLE ? String.valueOf(super.hashCode()) : null;
        this.stateVerifier = StateVerifier.newInstance();
    }

    private void init(Context context, GlideContext glideContext, Object model, Class<R> transcodeClass, RequestOptions requestOptions, int overrideWidth, int overrideHeight, Priority priority, Target<R> target, RequestListener<R> targetListener, @Nullable List<RequestListener<R>> requestListeners, RequestCoordinator requestCoordinator, Engine engine, TransitionFactory<? super R> animationFactory) {
        this.context = context;
        this.glideContext = glideContext;
        this.model = model;
        this.transcodeClass = transcodeClass;
        this.requestOptions = requestOptions;
        this.overrideWidth = overrideWidth;
        this.overrideHeight = overrideHeight;
        this.priority = priority;
        this.target = target;
        this.targetListener = targetListener;
        this.requestListeners = requestListeners;
        this.requestCoordinator = requestCoordinator;
        this.engine = engine;
        this.animationFactory = animationFactory;
        this.status = Status.PENDING;
    }

    @Override // com.bumptech.glide.util.pool.FactoryPools.Poolable
    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }

    @Override // com.bumptech.glide.request.Request
    public void recycle() {
        assertNotCallingCallbacks();
        this.context = null;
        this.glideContext = null;
        this.model = null;
        this.transcodeClass = null;
        this.requestOptions = null;
        this.overrideWidth = -1;
        this.overrideHeight = -1;
        this.target = null;
        this.requestListeners = null;
        this.targetListener = null;
        this.requestCoordinator = null;
        this.animationFactory = null;
        this.loadStatus = null;
        this.errorDrawable = null;
        this.placeholderDrawable = null;
        this.fallbackDrawable = null;
        this.width = -1;
        this.height = -1;
        POOL.release(this);
    }

    @Override // com.bumptech.glide.request.Request
    public void begin() {
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        this.startTime = LogTime.getLogTime();
        if (this.model == null) {
            if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
                this.width = this.overrideWidth;
                this.height = this.overrideHeight;
            }
            int logLevel = getFallbackDrawable() == null ? 5 : 3;
            onLoadFailed(new GlideException("Received null model"), logLevel);
        } else if (this.status == Status.RUNNING) {
            throw new IllegalArgumentException("Cannot restart a running request");
        } else {
            if (this.status == Status.COMPLETE) {
                onResourceReady(this.resource, DataSource.MEMORY_CACHE);
                return;
            }
            this.status = Status.WAITING_FOR_SIZE;
            if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
                onSizeReady(this.overrideWidth, this.overrideHeight);
            } else {
                this.target.getSize(this);
            }
            if ((this.status == Status.RUNNING || this.status == Status.WAITING_FOR_SIZE) && canNotifyStatusChanged()) {
                this.target.onLoadStarted(getPlaceholderDrawable());
            }
            if (IS_VERBOSE_LOGGABLE) {
                logV("finished run method in " + LogTime.getElapsedMillis(this.startTime));
            }
        }
    }

    private void cancel() {
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        this.target.removeCallback(this);
        Engine.LoadStatus loadStatus = this.loadStatus;
        if (loadStatus != null) {
            loadStatus.cancel();
            this.loadStatus = null;
        }
    }

    private void assertNotCallingCallbacks() {
        if (this.isCallingCallbacks) {
            throw new IllegalStateException("You can't start or clear loads in RequestListener or Target callbacks. If you're trying to start a fallback request when a load fails, use RequestBuilder#error(RequestBuilder). Otherwise consider posting your into() or clear() calls to the main thread using a Handler instead.");
        }
    }

    @Override // com.bumptech.glide.request.Request
    public void clear() {
        Util.assertMainThread();
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        if (this.status == Status.CLEARED) {
            return;
        }
        cancel();
        Resource<R> resource = this.resource;
        if (resource != null) {
            releaseResource(resource);
        }
        if (canNotifyCleared()) {
            this.target.onLoadCleared(getPlaceholderDrawable());
        }
        this.status = Status.CLEARED;
    }

    private void releaseResource(Resource<?> resource) {
        this.engine.release(resource);
        this.resource = null;
    }

    @Override // com.bumptech.glide.request.Request
    public boolean isRunning() {
        return this.status == Status.RUNNING || this.status == Status.WAITING_FOR_SIZE;
    }

    @Override // com.bumptech.glide.request.Request
    public boolean isComplete() {
        return this.status == Status.COMPLETE;
    }

    @Override // com.bumptech.glide.request.Request
    public boolean isResourceSet() {
        return isComplete();
    }

    @Override // com.bumptech.glide.request.Request
    public boolean isCleared() {
        return this.status == Status.CLEARED;
    }

    @Override // com.bumptech.glide.request.Request
    public boolean isFailed() {
        return this.status == Status.FAILED;
    }

    private Drawable getErrorDrawable() {
        if (this.errorDrawable == null) {
            this.errorDrawable = this.requestOptions.getErrorPlaceholder();
            if (this.errorDrawable == null && this.requestOptions.getErrorId() > 0) {
                this.errorDrawable = loadDrawable(this.requestOptions.getErrorId());
            }
        }
        return this.errorDrawable;
    }

    private Drawable getPlaceholderDrawable() {
        if (this.placeholderDrawable == null) {
            this.placeholderDrawable = this.requestOptions.getPlaceholderDrawable();
            if (this.placeholderDrawable == null && this.requestOptions.getPlaceholderId() > 0) {
                this.placeholderDrawable = loadDrawable(this.requestOptions.getPlaceholderId());
            }
        }
        return this.placeholderDrawable;
    }

    private Drawable getFallbackDrawable() {
        if (this.fallbackDrawable == null) {
            this.fallbackDrawable = this.requestOptions.getFallbackDrawable();
            if (this.fallbackDrawable == null && this.requestOptions.getFallbackId() > 0) {
                this.fallbackDrawable = loadDrawable(this.requestOptions.getFallbackId());
            }
        }
        return this.fallbackDrawable;
    }

    private Drawable loadDrawable(@DrawableRes int resourceId) {
        Resources.Theme theme = this.requestOptions.getTheme() != null ? this.requestOptions.getTheme() : this.context.getTheme();
        return DrawableDecoderCompat.getDrawable(this.glideContext, resourceId, theme);
    }

    private void setErrorPlaceholder() {
        if (!canNotifyStatusChanged()) {
            return;
        }
        Drawable error = null;
        if (this.model == null) {
            error = getFallbackDrawable();
        }
        if (error == null) {
            error = getErrorDrawable();
        }
        if (error == null) {
            error = getPlaceholderDrawable();
        }
        this.target.onLoadFailed(error);
    }

    @Override // com.bumptech.glide.request.target.SizeReadyCallback
    public void onSizeReady(int width, int height) {
        this.stateVerifier.throwIfRecycled();
        if (IS_VERBOSE_LOGGABLE) {
            logV("Got onSizeReady in " + LogTime.getElapsedMillis(this.startTime));
        }
        if (this.status == Status.WAITING_FOR_SIZE) {
            this.status = Status.RUNNING;
            float sizeMultiplier = this.requestOptions.getSizeMultiplier();
            this.width = maybeApplySizeMultiplier(width, sizeMultiplier);
            this.height = maybeApplySizeMultiplier(height, sizeMultiplier);
            if (IS_VERBOSE_LOGGABLE) {
                logV("finished setup for calling load in " + LogTime.getElapsedMillis(this.startTime));
            }
            this.loadStatus = this.engine.load(this.glideContext, this.model, this.requestOptions.getSignature(), this.width, this.height, this.requestOptions.getResourceClass(), this.transcodeClass, this.priority, this.requestOptions.getDiskCacheStrategy(), this.requestOptions.getTransformations(), this.requestOptions.isTransformationRequired(), this.requestOptions.isScaleOnlyOrNoTransform(), this.requestOptions.getOptions(), this.requestOptions.isMemoryCacheable(), this.requestOptions.getUseUnlimitedSourceGeneratorsPool(), this.requestOptions.getUseAnimationPool(), this.requestOptions.getOnlyRetrieveFromCache(), this);
            if (this.status != Status.RUNNING) {
                this.loadStatus = null;
            }
            if (IS_VERBOSE_LOGGABLE) {
                logV("finished onSizeReady in " + LogTime.getElapsedMillis(this.startTime));
            }
        }
    }

    private static int maybeApplySizeMultiplier(int size, float sizeMultiplier) {
        return size == Integer.MIN_VALUE ? size : Math.round(size * sizeMultiplier);
    }

    private boolean canSetResource() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        return requestCoordinator == null || requestCoordinator.canSetImage(this);
    }

    private boolean canNotifyCleared() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        return requestCoordinator == null || requestCoordinator.canNotifyCleared(this);
    }

    private boolean canNotifyStatusChanged() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        return requestCoordinator == null || requestCoordinator.canNotifyStatusChanged(this);
    }

    private boolean isFirstReadyResource() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        return requestCoordinator == null || !requestCoordinator.isAnyResourceSet();
    }

    private void notifyLoadSuccess() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        if (requestCoordinator != null) {
            requestCoordinator.onRequestSuccess(this);
        }
    }

    private void notifyLoadFailed() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        if (requestCoordinator != null) {
            requestCoordinator.onRequestFailed(this);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.request.ResourceCallback
    public void onResourceReady(Resource<?> resource, DataSource dataSource) {
        this.stateVerifier.throwIfRecycled();
        this.loadStatus = null;
        if (resource == null) {
            GlideException exception = new GlideException("Expected to receive a Resource<R> with an object of " + this.transcodeClass + " inside, but instead got null.");
            onLoadFailed(exception);
            return;
        }
        Object received = resource.mo301get();
        if (received == null || !this.transcodeClass.isAssignableFrom(received.getClass())) {
            releaseResource(resource);
            StringBuilder sb = new StringBuilder();
            sb.append("Expected to receive an object of ");
            sb.append(this.transcodeClass);
            sb.append(" but instead got ");
            sb.append(received != null ? received.getClass() : "");
            sb.append("{");
            sb.append(received);
            sb.append("} inside Resource{");
            sb.append(resource);
            sb.append("}.");
            sb.append(received != null ? "" : " To indicate failure return a null Resource object, rather than a Resource object containing null data.");
            GlideException exception2 = new GlideException(sb.toString());
            onLoadFailed(exception2);
        } else if (!canSetResource()) {
            releaseResource(resource);
            this.status = Status.COMPLETE;
        } else {
            onResourceReady(resource, received, dataSource);
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:10:0x007e */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void onResourceReady(com.bumptech.glide.load.engine.Resource<R> r12, R r13, com.bumptech.glide.load.DataSource r14) {
        /*
            r11 = this;
            boolean r6 = r11.isFirstReadyResource()
            com.bumptech.glide.request.SingleRequest$Status r0 = com.bumptech.glide.request.SingleRequest.Status.COMPLETE
            r11.status = r0
            r11.resource = r12
            com.bumptech.glide.GlideContext r0 = r11.glideContext
            int r0 = r0.getLogLevel()
            r1 = 3
            if (r0 > r1) goto L6a
            java.lang.String r0 = "Glide"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Finished loading "
            r1.append(r2)
            java.lang.Class r2 = r13.getClass()
            java.lang.String r2 = r2.getSimpleName()
            r1.append(r2)
            java.lang.String r2 = " from "
            r1.append(r2)
            r1.append(r14)
            java.lang.String r2 = " for "
            r1.append(r2)
            java.lang.Object r2 = r11.model
            r1.append(r2)
            java.lang.String r2 = " with size ["
            r1.append(r2)
            int r2 = r11.width
            r1.append(r2)
            java.lang.String r2 = "x"
            r1.append(r2)
            int r2 = r11.height
            r1.append(r2)
            java.lang.String r2 = "] in "
            r1.append(r2)
            long r2 = r11.startTime
            double r2 = com.bumptech.glide.util.LogTime.getElapsedMillis(r2)
            r1.append(r2)
            java.lang.String r2 = " ms"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
        L6a:
            r7 = 1
            r11.isCallingCallbacks = r7
            r0 = 0
            r8 = 0
            java.util.List<com.bumptech.glide.request.RequestListener<R>> r1 = r11.requestListeners     // Catch: java.lang.Throwable -> Lbf
            if (r1 == 0) goto L93
            java.util.List<com.bumptech.glide.request.RequestListener<R>> r1 = r11.requestListeners     // Catch: java.lang.Throwable -> Lbf
            java.util.Iterator r9 = r1.iterator()     // Catch: java.lang.Throwable -> Lbf
            r10 = r0
        L7a:
            boolean r0 = r9.hasNext()     // Catch: java.lang.Throwable -> Lbf
            if (r0 == 0) goto L94
            java.lang.Object r0 = r9.next()     // Catch: java.lang.Throwable -> Lbf
            com.bumptech.glide.request.RequestListener r0 = (com.bumptech.glide.request.RequestListener) r0     // Catch: java.lang.Throwable -> Lbf
            java.lang.Object r2 = r11.model     // Catch: java.lang.Throwable -> Lbf
            com.bumptech.glide.request.target.Target<R> r3 = r11.target     // Catch: java.lang.Throwable -> Lbf
            r1 = r13
            r4 = r14
            r5 = r6
            boolean r1 = r0.onResourceReady(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> Lbf
            r10 = r10 | r1
            goto L7a
        L93:
            r10 = r0
        L94:
            com.bumptech.glide.request.RequestListener<R> r0 = r11.targetListener     // Catch: java.lang.Throwable -> Lbf
            if (r0 == 0) goto La8
            com.bumptech.glide.request.RequestListener<R> r0 = r11.targetListener     // Catch: java.lang.Throwable -> Lbf
            java.lang.Object r2 = r11.model     // Catch: java.lang.Throwable -> Lbf
            com.bumptech.glide.request.target.Target<R> r3 = r11.target     // Catch: java.lang.Throwable -> Lbf
            r1 = r13
            r4 = r14
            r5 = r6
            boolean r0 = r0.onResourceReady(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> Lbf
            if (r0 == 0) goto La8
            goto La9
        La8:
            r7 = 0
        La9:
            r0 = r10 | r7
            if (r0 != 0) goto Lb8
            com.bumptech.glide.request.transition.TransitionFactory<? super R> r1 = r11.animationFactory     // Catch: java.lang.Throwable -> Lbf
            com.bumptech.glide.request.transition.Transition r1 = r1.build(r14, r6)     // Catch: java.lang.Throwable -> Lbf
            com.bumptech.glide.request.target.Target<R> r2 = r11.target     // Catch: java.lang.Throwable -> Lbf
            r2.onResourceReady(r13, r1)     // Catch: java.lang.Throwable -> Lbf
        Lb8:
            r11.isCallingCallbacks = r8
            r11.notifyLoadSuccess()
            return
        Lbf:
            r0 = move-exception
            r11.isCallingCallbacks = r8
            throw r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.SingleRequest.onResourceReady(com.bumptech.glide.load.engine.Resource, java.lang.Object, com.bumptech.glide.load.DataSource):void");
    }

    @Override // com.bumptech.glide.request.ResourceCallback
    public void onLoadFailed(GlideException e) {
        onLoadFailed(e, 5);
    }

    private void onLoadFailed(GlideException e, int maxLogLevel) {
        this.stateVerifier.throwIfRecycled();
        int logLevel = this.glideContext.getLogLevel();
        if (logLevel <= maxLogLevel) {
            Log.w(GLIDE_TAG, "Load failed for " + this.model + " with size [" + this.width + "x" + this.height + "]", e);
            if (logLevel <= 4) {
                e.logRootCauses(GLIDE_TAG);
            }
        }
        this.loadStatus = null;
        this.status = Status.FAILED;
        boolean z = true;
        this.isCallingCallbacks = true;
        boolean anyListenerHandledUpdatingTarget = false;
        try {
            if (this.requestListeners != null) {
                for (RequestListener<R> listener : this.requestListeners) {
                    anyListenerHandledUpdatingTarget |= listener.onLoadFailed(e, this.model, this.target, isFirstReadyResource());
                }
            }
            if (this.targetListener == null || !this.targetListener.onLoadFailed(e, this.model, this.target, isFirstReadyResource())) {
                z = false;
            }
            if (!(z | anyListenerHandledUpdatingTarget)) {
                setErrorPlaceholder();
            }
            this.isCallingCallbacks = false;
            notifyLoadFailed();
        } catch (Throwable th) {
            this.isCallingCallbacks = false;
            throw th;
        }
    }

    @Override // com.bumptech.glide.request.Request
    public boolean isEquivalentTo(Request o) {
        if (o instanceof SingleRequest) {
            SingleRequest<?> that = (SingleRequest) o;
            return this.overrideWidth == that.overrideWidth && this.overrideHeight == that.overrideHeight && Util.bothModelsNullEquivalentOrEquals(this.model, that.model) && this.transcodeClass.equals(that.transcodeClass) && this.requestOptions.equals(that.requestOptions) && this.priority == that.priority && listenerCountEquals(this, that);
        }
        return false;
    }

    private static boolean listenerCountEquals(SingleRequest<?> first, SingleRequest<?> second) {
        List<RequestListener<?>> list = ((SingleRequest) first).requestListeners;
        int firstListenerCount = list == null ? 0 : list.size();
        List<RequestListener<?>> list2 = ((SingleRequest) second).requestListeners;
        int secondListenerCount = list2 == null ? 0 : list2.size();
        return firstListenerCount == secondListenerCount;
    }

    private void logV(String message) {
        Log.v(TAG, message + " this: " + this.tag);
    }
}