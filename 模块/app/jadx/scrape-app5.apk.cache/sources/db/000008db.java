package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.ErrorRequestCoordinator;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestCoordinator;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.ThumbnailRequestCoordinator;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.signature.ApplicationVersionSignature;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class RequestBuilder<TranscodeType> implements Cloneable, ModelTypes<RequestBuilder<TranscodeType>> {
    protected static final RequestOptions DOWNLOAD_ONLY_OPTIONS = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.LOW).skipMemoryCache(true);
    private final Context context;
    private final RequestOptions defaultRequestOptions;
    @Nullable
    private RequestBuilder<TranscodeType> errorBuilder;
    private final Glide glide;
    private final GlideContext glideContext;
    private boolean isDefaultTransitionOptionsSet;
    private boolean isModelSet;
    private boolean isThumbnailBuilt;
    @Nullable
    private Object model;
    @Nullable
    private List<RequestListener<TranscodeType>> requestListeners;
    private final RequestManager requestManager;
    @NonNull
    protected RequestOptions requestOptions;
    @Nullable
    private Float thumbSizeMultiplier;
    @Nullable
    private RequestBuilder<TranscodeType> thumbnailBuilder;
    private final Class<TranscodeType> transcodeClass;
    @NonNull
    private TransitionOptions<?, ? super TranscodeType> transitionOptions;

    public RequestBuilder(Glide glide, RequestManager requestManager, Class<TranscodeType> transcodeClass, Context context) {
        this.isDefaultTransitionOptionsSet = true;
        this.glide = glide;
        this.requestManager = requestManager;
        this.transcodeClass = transcodeClass;
        this.defaultRequestOptions = requestManager.getDefaultRequestOptions();
        this.context = context;
        this.transitionOptions = requestManager.getDefaultTransitionOptions(transcodeClass);
        this.requestOptions = this.defaultRequestOptions;
        this.glideContext = glide.getGlideContext();
    }

    protected RequestBuilder(Class<TranscodeType> transcodeClass, RequestBuilder<?> other) {
        this(other.glide, other.requestManager, transcodeClass, other.context);
        this.model = other.model;
        this.isModelSet = other.isModelSet;
        this.requestOptions = other.requestOptions;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> apply(@NonNull RequestOptions requestOptions) {
        Preconditions.checkNotNull(requestOptions);
        this.requestOptions = getMutableOptions().apply(requestOptions);
        return this;
    }

    @NonNull
    protected RequestOptions getMutableOptions() {
        RequestOptions requestOptions = this.defaultRequestOptions;
        RequestOptions requestOptions2 = this.requestOptions;
        if (requestOptions != requestOptions2) {
            return requestOptions2;
        }
        return requestOptions2.m302clone();
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> transition(@NonNull TransitionOptions<?, ? super TranscodeType> transitionOptions) {
        this.transitionOptions = (TransitionOptions) Preconditions.checkNotNull(transitionOptions);
        this.isDefaultTransitionOptionsSet = false;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> listener(@Nullable RequestListener<TranscodeType> requestListener) {
        this.requestListeners = null;
        return addListener(requestListener);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> addListener(@Nullable RequestListener<TranscodeType> requestListener) {
        if (requestListener != null) {
            if (this.requestListeners == null) {
                this.requestListeners = new ArrayList();
            }
            this.requestListeners.add(requestListener);
        }
        return this;
    }

    @NonNull
    public RequestBuilder<TranscodeType> error(@Nullable RequestBuilder<TranscodeType> errorBuilder) {
        this.errorBuilder = errorBuilder;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType> thumbnailRequest) {
        this.thumbnailBuilder = thumbnailRequest;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType>... thumbnails) {
        if (thumbnails == null || thumbnails.length == 0) {
            return thumbnail((RequestBuilder) null);
        }
        RequestBuilder<TranscodeType> previous = null;
        for (int i = thumbnails.length - 1; i >= 0; i--) {
            RequestBuilder<TranscodeType> current = thumbnails[i];
            if (current != null) {
                if (previous == null) {
                    previous = current;
                } else {
                    previous = current.thumbnail(previous);
                }
            }
        }
        return thumbnail(previous);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(float sizeMultiplier) {
        if (sizeMultiplier < 0.0f || sizeMultiplier > 1.0f) {
            throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
        }
        this.thumbSizeMultiplier = Float.valueOf(sizeMultiplier);
        return this;
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo273load(@Nullable Object model) {
        return loadGeneric(model);
    }

    @NonNull
    private RequestBuilder<TranscodeType> loadGeneric(@Nullable Object model) {
        this.model = model;
        this.isModelSet = true;
        return this;
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo268load(@Nullable Bitmap bitmap) {
        return loadGeneric(bitmap).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo269load(@Nullable Drawable drawable) {
        return loadGeneric(drawable).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo274load(@Nullable String string) {
        return loadGeneric(string);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo270load(@Nullable Uri uri) {
        return loadGeneric(uri);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo271load(@Nullable File file) {
        return loadGeneric(file);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo272load(@RawRes @DrawableRes @Nullable Integer resourceId) {
        return loadGeneric(resourceId).apply(RequestOptions.signatureOf(ApplicationVersionSignature.obtain(this.context)));
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @Deprecated
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo275load(@Nullable URL url) {
        return loadGeneric(url);
    }

    @Override // com.bumptech.glide.ModelTypes
    @CheckResult
    @NonNull
    /* renamed from: load */
    public RequestBuilder<TranscodeType> mo276load(@Nullable byte[] model) {
        RequestBuilder<TranscodeType> result = loadGeneric(model);
        if (!result.requestOptions.isDiskCacheStrategySet()) {
            result = result.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
        }
        if (!result.requestOptions.isSkipMemoryCacheSet()) {
            return result.apply(RequestOptions.skipMemoryCacheOf(true));
        }
        return result;
    }

    @CheckResult
    /* renamed from: clone */
    public RequestBuilder<TranscodeType> m258clone() {
        try {
            RequestBuilder<TranscodeType> result = (RequestBuilder) super.clone();
            result.requestOptions = result.requestOptions.m302clone();
            result.transitionOptions = (TransitionOptions<?, ? super TranscodeType>) result.transitionOptions.m277clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public <Y extends Target<TranscodeType>> Y into(@NonNull Y target) {
        return (Y) into((RequestBuilder<TranscodeType>) target, (RequestListener) null);
    }

    @NonNull
    <Y extends Target<TranscodeType>> Y into(@NonNull Y target, @Nullable RequestListener<TranscodeType> targetListener) {
        return (Y) into(target, targetListener, getMutableOptions());
    }

    private <Y extends Target<TranscodeType>> Y into(@NonNull Y target, @Nullable RequestListener<TranscodeType> targetListener, @NonNull RequestOptions options) {
        Util.assertMainThread();
        Preconditions.checkNotNull(target);
        if (!this.isModelSet) {
            throw new IllegalArgumentException("You must call #load() before calling #into()");
        }
        RequestOptions options2 = options.autoClone();
        Request request = buildRequest(target, targetListener, options2);
        Request previous = target.getRequest();
        if (request.isEquivalentTo(previous) && !isSkipMemoryCacheWithCompletePreviousRequest(options2, previous)) {
            request.recycle();
            if (!((Request) Preconditions.checkNotNull(previous)).isRunning()) {
                previous.begin();
            }
            return target;
        }
        this.requestManager.clear((Target<?>) target);
        target.setRequest(request);
        this.requestManager.track(target, request);
        return target;
    }

    private boolean isSkipMemoryCacheWithCompletePreviousRequest(RequestOptions options, Request previous) {
        return !options.isMemoryCacheable() && previous.isComplete();
    }

    @NonNull
    public ViewTarget<ImageView, TranscodeType> into(@NonNull ImageView view) {
        Util.assertMainThread();
        Preconditions.checkNotNull(view);
        RequestOptions requestOptions = this.requestOptions;
        if (!requestOptions.isTransformationSet() && requestOptions.isTransformationAllowed() && view.getScaleType() != null) {
            switch (C06362.$SwitchMap$android$widget$ImageView$ScaleType[view.getScaleType().ordinal()]) {
                case 1:
                    requestOptions = requestOptions.m302clone().optionalCenterCrop();
                    break;
                case 2:
                    requestOptions = requestOptions.m302clone().optionalCenterInside();
                    break;
                case 3:
                case 4:
                case 5:
                    requestOptions = requestOptions.m302clone().optionalFitCenter();
                    break;
                case 6:
                    requestOptions = requestOptions.m302clone().optionalCenterInside();
                    break;
            }
        }
        return (ViewTarget) into(this.glideContext.buildImageViewTarget(view, this.transcodeClass), null, requestOptions);
    }

    @Deprecated
    public FutureTarget<TranscodeType> into(int width, int height) {
        return submit(width, height);
    }

    @NonNull
    public FutureTarget<TranscodeType> submit() {
        return submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @NonNull
    public FutureTarget<TranscodeType> submit(int width, int height) {
        final RequestFutureTarget<TranscodeType> target = new RequestFutureTarget<>(this.glideContext.getMainHandler(), width, height);
        if (Util.isOnBackgroundThread()) {
            this.glideContext.getMainHandler().post(new Runnable() { // from class: com.bumptech.glide.RequestBuilder.1
                {
                    RequestBuilder.this = this;
                }

                @Override // java.lang.Runnable
                public void run() {
                    if (!target.isCancelled()) {
                        RequestBuilder requestBuilder = RequestBuilder.this;
                        RequestFutureTarget requestFutureTarget = target;
                        requestBuilder.into((RequestBuilder) requestFutureTarget, (RequestListener) requestFutureTarget);
                    }
                }
            });
        } else {
            into((RequestBuilder<TranscodeType>) target, (RequestListener) target);
        }
        return target;
    }

    @NonNull
    public Target<TranscodeType> preload(int width, int height) {
        PreloadTarget<TranscodeType> target = PreloadTarget.obtain(this.requestManager, width, height);
        return into((RequestBuilder<TranscodeType>) target);
    }

    @NonNull
    public Target<TranscodeType> preload() {
        return preload(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @CheckResult
    @Deprecated
    public <Y extends Target<File>> Y downloadOnly(@NonNull Y target) {
        return (Y) getDownloadOnlyRequest().into((RequestBuilder<File>) target);
    }

    @CheckResult
    @Deprecated
    public FutureTarget<File> downloadOnly(int width, int height) {
        return getDownloadOnlyRequest().submit(width, height);
    }

    @CheckResult
    @NonNull
    protected RequestBuilder<File> getDownloadOnlyRequest() {
        return new RequestBuilder(File.class, this).apply(DOWNLOAD_ONLY_OPTIONS);
    }

    /* renamed from: com.bumptech.glide.RequestBuilder$2 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C06362 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType;

        static {
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.LOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.HIGH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.IMMEDIATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            $SwitchMap$android$widget$ImageView$ScaleType = new int[ImageView.ScaleType.values().length];
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_CROP.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_INSIDE.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_START.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_XY.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER.ordinal()] = 7;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.MATRIX.ordinal()] = 8;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    @NonNull
    private Priority getThumbnailPriority(@NonNull Priority current) {
        switch (current) {
            case LOW:
                return Priority.NORMAL;
            case NORMAL:
                return Priority.HIGH;
            case HIGH:
            case IMMEDIATE:
                return Priority.IMMEDIATE;
            default:
                throw new IllegalArgumentException("unknown priority: " + this.requestOptions.getPriority());
        }
    }

    private Request buildRequest(Target<TranscodeType> target, @Nullable RequestListener<TranscodeType> targetListener, RequestOptions requestOptions) {
        return buildRequestRecursive(target, targetListener, null, this.transitionOptions, requestOptions.getPriority(), requestOptions.getOverrideWidth(), requestOptions.getOverrideHeight(), requestOptions);
    }

    private Request buildRequestRecursive(Target<TranscodeType> target, @Nullable RequestListener<TranscodeType> targetListener, @Nullable RequestCoordinator parentCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int overrideWidth, int overrideHeight, RequestOptions requestOptions) {
        ErrorRequestCoordinator errorRequestCoordinator;
        RequestCoordinator parentCoordinator2;
        int errorOverrideWidth;
        int errorOverrideHeight;
        if (this.errorBuilder == null) {
            errorRequestCoordinator = null;
            parentCoordinator2 = parentCoordinator;
        } else {
            RequestCoordinator errorRequestCoordinator2 = new ErrorRequestCoordinator(parentCoordinator);
            errorRequestCoordinator = errorRequestCoordinator2;
            parentCoordinator2 = errorRequestCoordinator2;
        }
        Request mainRequest = buildThumbnailRequestRecursive(target, targetListener, parentCoordinator2, transitionOptions, priority, overrideWidth, overrideHeight, requestOptions);
        if (errorRequestCoordinator == null) {
            return mainRequest;
        }
        int errorOverrideWidth2 = this.errorBuilder.requestOptions.getOverrideWidth();
        int errorOverrideHeight2 = this.errorBuilder.requestOptions.getOverrideHeight();
        if (Util.isValidDimensions(overrideWidth, overrideHeight) && !this.errorBuilder.requestOptions.isValidOverride()) {
            int errorOverrideWidth3 = requestOptions.getOverrideWidth();
            int errorOverrideHeight3 = requestOptions.getOverrideHeight();
            errorOverrideWidth = errorOverrideWidth3;
            errorOverrideHeight = errorOverrideHeight3;
        } else {
            errorOverrideWidth = errorOverrideWidth2;
            errorOverrideHeight = errorOverrideHeight2;
        }
        RequestBuilder<TranscodeType> requestBuilder = this.errorBuilder;
        Request errorRequest = requestBuilder.buildRequestRecursive(target, targetListener, errorRequestCoordinator, requestBuilder.transitionOptions, requestBuilder.requestOptions.getPriority(), errorOverrideWidth, errorOverrideHeight, this.errorBuilder.requestOptions);
        errorRequestCoordinator.setRequests(mainRequest, errorRequest);
        return errorRequestCoordinator;
    }

    private Request buildThumbnailRequestRecursive(Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, @Nullable RequestCoordinator parentCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int overrideWidth, int overrideHeight, RequestOptions requestOptions) {
        TransitionOptions<?, ? super TranscodeType> thumbTransitionOptions;
        Priority thumbnailPriority;
        int thumbOverrideWidth;
        int thumbOverrideHeight;
        RequestBuilder<TranscodeType> requestBuilder = this.thumbnailBuilder;
        if (requestBuilder != null) {
            if (this.isThumbnailBuilt) {
                throw new IllegalStateException("You cannot use a request as both the main request and a thumbnail, consider using clone() on the request(s) passed to thumbnail()");
            }
            TransitionOptions<?, ? super TranscodeType> thumbTransitionOptions2 = requestBuilder.transitionOptions;
            if (!requestBuilder.isDefaultTransitionOptionsSet) {
                thumbTransitionOptions = thumbTransitionOptions2;
            } else {
                thumbTransitionOptions = transitionOptions;
            }
            if (!this.thumbnailBuilder.requestOptions.isPrioritySet()) {
                thumbnailPriority = getThumbnailPriority(priority);
            } else {
                thumbnailPriority = this.thumbnailBuilder.requestOptions.getPriority();
            }
            Priority thumbPriority = thumbnailPriority;
            int thumbOverrideWidth2 = this.thumbnailBuilder.requestOptions.getOverrideWidth();
            int thumbOverrideHeight2 = this.thumbnailBuilder.requestOptions.getOverrideHeight();
            if (Util.isValidDimensions(overrideWidth, overrideHeight) && !this.thumbnailBuilder.requestOptions.isValidOverride()) {
                int thumbOverrideWidth3 = requestOptions.getOverrideWidth();
                int thumbOverrideHeight3 = requestOptions.getOverrideHeight();
                thumbOverrideWidth = thumbOverrideWidth3;
                thumbOverrideHeight = thumbOverrideHeight3;
            } else {
                thumbOverrideWidth = thumbOverrideWidth2;
                thumbOverrideHeight = thumbOverrideHeight2;
            }
            ThumbnailRequestCoordinator coordinator = new ThumbnailRequestCoordinator(parentCoordinator);
            Request fullRequest = obtainRequest(target, targetListener, requestOptions, coordinator, transitionOptions, priority, overrideWidth, overrideHeight);
            this.isThumbnailBuilt = true;
            RequestBuilder<TranscodeType> requestBuilder2 = this.thumbnailBuilder;
            Request thumbRequest = requestBuilder2.buildRequestRecursive(target, targetListener, coordinator, thumbTransitionOptions, thumbPriority, thumbOverrideWidth, thumbOverrideHeight, requestBuilder2.requestOptions);
            this.isThumbnailBuilt = false;
            coordinator.setRequests(fullRequest, thumbRequest);
            return coordinator;
        } else if (this.thumbSizeMultiplier != null) {
            ThumbnailRequestCoordinator coordinator2 = new ThumbnailRequestCoordinator(parentCoordinator);
            Request fullRequest2 = obtainRequest(target, targetListener, requestOptions, coordinator2, transitionOptions, priority, overrideWidth, overrideHeight);
            RequestOptions thumbnailOptions = requestOptions.m302clone().sizeMultiplier(this.thumbSizeMultiplier.floatValue());
            Request thumbnailRequest = obtainRequest(target, targetListener, thumbnailOptions, coordinator2, transitionOptions, getThumbnailPriority(priority), overrideWidth, overrideHeight);
            coordinator2.setRequests(fullRequest2, thumbnailRequest);
            return coordinator2;
        } else {
            return obtainRequest(target, targetListener, requestOptions, parentCoordinator, transitionOptions, priority, overrideWidth, overrideHeight);
        }
    }

    private Request obtainRequest(Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, RequestOptions requestOptions, RequestCoordinator requestCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int overrideWidth, int overrideHeight) {
        Context context = this.context;
        GlideContext glideContext = this.glideContext;
        return SingleRequest.obtain(context, glideContext, this.model, this.transcodeClass, requestOptions, overrideWidth, overrideHeight, priority, target, targetListener, this.requestListeners, requestCoordinator, glideContext.getEngine(), transitionOptions.getTransitionFactory());
    }
}