package com.bumptech.glide.request;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.DrawableTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableTransformation;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.bumptech.glide.signature.EmptySignature;
import com.bumptech.glide.util.CachedHashCodeArrayMap;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.util.Map;

/* loaded from: classes.dex */
public class RequestOptions implements Cloneable {
    private static final int DISK_CACHE_STRATEGY = 4;
    private static final int ERROR_ID = 32;
    private static final int ERROR_PLACEHOLDER = 16;
    private static final int FALLBACK = 8192;
    private static final int FALLBACK_ID = 16384;
    private static final int IS_CACHEABLE = 256;
    private static final int ONLY_RETRIEVE_FROM_CACHE = 524288;
    private static final int OVERRIDE = 512;
    private static final int PLACEHOLDER = 64;
    private static final int PLACEHOLDER_ID = 128;
    private static final int PRIORITY = 8;
    private static final int RESOURCE_CLASS = 4096;
    private static final int SIGNATURE = 1024;
    private static final int SIZE_MULTIPLIER = 2;
    private static final int THEME = 32768;
    private static final int TRANSFORMATION = 2048;
    private static final int TRANSFORMATION_ALLOWED = 65536;
    private static final int TRANSFORMATION_REQUIRED = 131072;
    private static final int UNSET = -1;
    private static final int USE_ANIMATION_POOL = 1048576;
    private static final int USE_UNLIMITED_SOURCE_GENERATORS_POOL = 262144;
    @Nullable
    private static RequestOptions centerCropOptions;
    @Nullable
    private static RequestOptions centerInsideOptions;
    @Nullable
    private static RequestOptions circleCropOptions;
    @Nullable
    private static RequestOptions fitCenterOptions;
    @Nullable
    private static RequestOptions noAnimationOptions;
    @Nullable
    private static RequestOptions noTransformOptions;
    @Nullable
    private static RequestOptions skipMemoryCacheFalseOptions;
    @Nullable
    private static RequestOptions skipMemoryCacheTrueOptions;
    private int errorId;
    @Nullable
    private Drawable errorPlaceholder;
    @Nullable
    private Drawable fallbackDrawable;
    private int fallbackId;
    private int fields;
    private boolean isAutoCloneEnabled;
    private boolean isLocked;
    private boolean isTransformationRequired;
    private boolean onlyRetrieveFromCache;
    @Nullable
    private Drawable placeholderDrawable;
    private int placeholderId;
    @Nullable
    private Resources.Theme theme;
    private boolean useAnimationPool;
    private boolean useUnlimitedSourceGeneratorsPool;
    private float sizeMultiplier = 1.0f;
    @NonNull
    private DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.AUTOMATIC;
    @NonNull
    private Priority priority = Priority.NORMAL;
    private boolean isCacheable = true;
    private int overrideHeight = -1;
    private int overrideWidth = -1;
    @NonNull
    private Key signature = EmptySignature.obtain();
    private boolean isTransformationAllowed = true;
    @NonNull
    private Options options = new Options();
    @NonNull
    private Map<Class<?>, Transformation<?>> transformations = new CachedHashCodeArrayMap();
    @NonNull
    private Class<?> resourceClass = Object.class;
    private boolean isScaleOnlyOrNoTransform = true;

    @CheckResult
    @NonNull
    public static RequestOptions sizeMultiplierOf(@FloatRange(from = 0.0d, to = 1.0d) float sizeMultiplier) {
        return new RequestOptions().sizeMultiplier(sizeMultiplier);
    }

    @CheckResult
    @NonNull
    public static RequestOptions diskCacheStrategyOf(@NonNull DiskCacheStrategy diskCacheStrategy) {
        return new RequestOptions().diskCacheStrategy(diskCacheStrategy);
    }

    @CheckResult
    @NonNull
    public static RequestOptions priorityOf(@NonNull Priority priority) {
        return new RequestOptions().priority(priority);
    }

    @CheckResult
    @NonNull
    public static RequestOptions placeholderOf(@Nullable Drawable placeholder) {
        return new RequestOptions().placeholder(placeholder);
    }

    @CheckResult
    @NonNull
    public static RequestOptions placeholderOf(@DrawableRes int placeholderId) {
        return new RequestOptions().placeholder(placeholderId);
    }

    @CheckResult
    @NonNull
    public static RequestOptions errorOf(@Nullable Drawable errorDrawable) {
        return new RequestOptions().error(errorDrawable);
    }

    @CheckResult
    @NonNull
    public static RequestOptions errorOf(@DrawableRes int errorId) {
        return new RequestOptions().error(errorId);
    }

    @CheckResult
    @NonNull
    public static RequestOptions skipMemoryCacheOf(boolean skipMemoryCache) {
        if (skipMemoryCache) {
            if (skipMemoryCacheTrueOptions == null) {
                skipMemoryCacheTrueOptions = new RequestOptions().skipMemoryCache(true).autoClone();
            }
            return skipMemoryCacheTrueOptions;
        }
        if (skipMemoryCacheFalseOptions == null) {
            skipMemoryCacheFalseOptions = new RequestOptions().skipMemoryCache(false).autoClone();
        }
        return skipMemoryCacheFalseOptions;
    }

    @CheckResult
    @NonNull
    public static RequestOptions overrideOf(@IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        return new RequestOptions().override(width, height);
    }

    @CheckResult
    @NonNull
    public static RequestOptions overrideOf(@IntRange(from = 0) int size) {
        return overrideOf(size, size);
    }

    @CheckResult
    @NonNull
    public static RequestOptions signatureOf(@NonNull Key signature) {
        return new RequestOptions().signature(signature);
    }

    @CheckResult
    @NonNull
    public static RequestOptions fitCenterTransform() {
        if (fitCenterOptions == null) {
            fitCenterOptions = new RequestOptions().fitCenter().autoClone();
        }
        return fitCenterOptions;
    }

    @CheckResult
    @NonNull
    public static RequestOptions centerInsideTransform() {
        if (centerInsideOptions == null) {
            centerInsideOptions = new RequestOptions().centerInside().autoClone();
        }
        return centerInsideOptions;
    }

    @CheckResult
    @NonNull
    public static RequestOptions centerCropTransform() {
        if (centerCropOptions == null) {
            centerCropOptions = new RequestOptions().centerCrop().autoClone();
        }
        return centerCropOptions;
    }

    @CheckResult
    @NonNull
    public static RequestOptions circleCropTransform() {
        if (circleCropOptions == null) {
            circleCropOptions = new RequestOptions().circleCrop().autoClone();
        }
        return circleCropOptions;
    }

    @CheckResult
    @NonNull
    public static RequestOptions bitmapTransform(@NonNull Transformation<Bitmap> transformation) {
        return new RequestOptions().transform(transformation);
    }

    @CheckResult
    @NonNull
    public static RequestOptions noTransformation() {
        if (noTransformOptions == null) {
            noTransformOptions = new RequestOptions().dontTransform().autoClone();
        }
        return noTransformOptions;
    }

    @CheckResult
    @NonNull
    public static <T> RequestOptions option(@NonNull Option<T> option, @NonNull T value) {
        return new RequestOptions().set(option, value);
    }

    @CheckResult
    @NonNull
    public static RequestOptions decodeTypeOf(@NonNull Class<?> resourceClass) {
        return new RequestOptions().decode(resourceClass);
    }

    @CheckResult
    @NonNull
    public static RequestOptions formatOf(@NonNull DecodeFormat format) {
        return new RequestOptions().format(format);
    }

    @CheckResult
    @NonNull
    public static RequestOptions frameOf(@IntRange(from = 0) long frameTimeMicros) {
        return new RequestOptions().frame(frameTimeMicros);
    }

    @CheckResult
    @NonNull
    public static RequestOptions downsampleOf(@NonNull DownsampleStrategy strategy) {
        return new RequestOptions().downsample(strategy);
    }

    @CheckResult
    @NonNull
    public static RequestOptions timeoutOf(@IntRange(from = 0) int timeout) {
        return new RequestOptions().timeout(timeout);
    }

    @CheckResult
    @NonNull
    public static RequestOptions encodeQualityOf(@IntRange(from = 0, to = 100) int quality) {
        return new RequestOptions().encodeQuality(quality);
    }

    @CheckResult
    @NonNull
    public static RequestOptions encodeFormatOf(@NonNull Bitmap.CompressFormat format) {
        return new RequestOptions().encodeFormat(format);
    }

    @CheckResult
    @NonNull
    public static RequestOptions noAnimation() {
        if (noAnimationOptions == null) {
            noAnimationOptions = new RequestOptions().dontAnimate().autoClone();
        }
        return noAnimationOptions;
    }

    private static boolean isSet(int fields, int flag) {
        return (fields & flag) != 0;
    }

    @CheckResult
    @NonNull
    public RequestOptions sizeMultiplier(@FloatRange(from = 0.0d, to = 1.0d) float sizeMultiplier) {
        if (this.isAutoCloneEnabled) {
            return m240clone().sizeMultiplier(sizeMultiplier);
        }
        if (sizeMultiplier < 0.0f || sizeMultiplier > 1.0f) {
            throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
        }
        this.sizeMultiplier = sizeMultiplier;
        this.fields |= 2;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions useUnlimitedSourceGeneratorsPool(boolean flag) {
        if (this.isAutoCloneEnabled) {
            return m240clone().useUnlimitedSourceGeneratorsPool(flag);
        }
        this.useUnlimitedSourceGeneratorsPool = flag;
        this.fields |= 262144;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions useAnimationPool(boolean flag) {
        if (this.isAutoCloneEnabled) {
            return m240clone().useAnimationPool(flag);
        }
        this.useAnimationPool = flag;
        this.fields |= 1048576;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions onlyRetrieveFromCache(boolean flag) {
        if (this.isAutoCloneEnabled) {
            return m240clone().onlyRetrieveFromCache(flag);
        }
        this.onlyRetrieveFromCache = flag;
        this.fields |= 524288;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions diskCacheStrategy(@NonNull DiskCacheStrategy strategy) {
        if (this.isAutoCloneEnabled) {
            return m240clone().diskCacheStrategy(strategy);
        }
        this.diskCacheStrategy = (DiskCacheStrategy) Preconditions.checkNotNull(strategy);
        this.fields |= 4;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions priority(@NonNull Priority priority) {
        if (this.isAutoCloneEnabled) {
            return m240clone().priority(priority);
        }
        this.priority = (Priority) Preconditions.checkNotNull(priority);
        this.fields |= 8;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions placeholder(@Nullable Drawable drawable) {
        if (this.isAutoCloneEnabled) {
            return m240clone().placeholder(drawable);
        }
        this.placeholderDrawable = drawable;
        this.fields |= 64;
        this.placeholderId = 0;
        this.fields &= -129;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions placeholder(@DrawableRes int resourceId) {
        if (this.isAutoCloneEnabled) {
            return m240clone().placeholder(resourceId);
        }
        this.placeholderId = resourceId;
        this.fields |= 128;
        this.placeholderDrawable = null;
        this.fields &= -65;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions fallback(@Nullable Drawable drawable) {
        if (this.isAutoCloneEnabled) {
            return m240clone().fallback(drawable);
        }
        this.fallbackDrawable = drawable;
        this.fields |= 8192;
        this.fallbackId = 0;
        this.fields &= -16385;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions fallback(@DrawableRes int resourceId) {
        if (this.isAutoCloneEnabled) {
            return m240clone().fallback(resourceId);
        }
        this.fallbackId = resourceId;
        this.fields |= 16384;
        this.fallbackDrawable = null;
        this.fields &= -8193;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions error(@Nullable Drawable drawable) {
        if (this.isAutoCloneEnabled) {
            return m240clone().error(drawable);
        }
        this.errorPlaceholder = drawable;
        this.fields |= 16;
        this.errorId = 0;
        this.fields &= -33;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions error(@DrawableRes int resourceId) {
        if (this.isAutoCloneEnabled) {
            return m240clone().error(resourceId);
        }
        this.errorId = resourceId;
        this.fields |= 32;
        this.errorPlaceholder = null;
        this.fields &= -17;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions theme(@Nullable Resources.Theme theme) {
        if (this.isAutoCloneEnabled) {
            return m240clone().theme(theme);
        }
        this.theme = theme;
        this.fields |= 32768;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions skipMemoryCache(boolean skip) {
        if (this.isAutoCloneEnabled) {
            return m240clone().skipMemoryCache(true);
        }
        this.isCacheable = !skip;
        this.fields |= 256;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions override(int width, int height) {
        if (this.isAutoCloneEnabled) {
            return m240clone().override(width, height);
        }
        this.overrideWidth = width;
        this.overrideHeight = height;
        this.fields |= 512;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions override(int size) {
        return override(size, size);
    }

    @CheckResult
    @NonNull
    public RequestOptions signature(@NonNull Key signature) {
        if (this.isAutoCloneEnabled) {
            return m240clone().signature(signature);
        }
        this.signature = (Key) Preconditions.checkNotNull(signature);
        this.fields |= 1024;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    /* renamed from: clone */
    public RequestOptions m240clone() {
        try {
            RequestOptions result = (RequestOptions) super.clone();
            result.options = new Options();
            result.options.putAll(this.options);
            result.transformations = new CachedHashCodeArrayMap();
            result.transformations.putAll(this.transformations);
            result.isLocked = false;
            result.isAutoCloneEnabled = false;
            return result;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @CheckResult
    @NonNull
    public <T> RequestOptions set(@NonNull Option<T> option, @NonNull T value) {
        if (this.isAutoCloneEnabled) {
            return m240clone().set(option, value);
        }
        Preconditions.checkNotNull(option);
        Preconditions.checkNotNull(value);
        this.options.set(option, value);
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions decode(@NonNull Class<?> resourceClass) {
        if (this.isAutoCloneEnabled) {
            return m240clone().decode(resourceClass);
        }
        this.resourceClass = (Class) Preconditions.checkNotNull(resourceClass);
        this.fields |= 4096;
        return selfOrThrowIfLocked();
    }

    public final boolean isTransformationAllowed() {
        return this.isTransformationAllowed;
    }

    public final boolean isTransformationSet() {
        return isSet(2048);
    }

    public final boolean isLocked() {
        return this.isLocked;
    }

    @CheckResult
    @NonNull
    public RequestOptions encodeFormat(@NonNull Bitmap.CompressFormat format) {
        return set(BitmapEncoder.COMPRESSION_FORMAT, Preconditions.checkNotNull(format));
    }

    @CheckResult
    @NonNull
    public RequestOptions encodeQuality(@IntRange(from = 0, to = 100) int quality) {
        return set(BitmapEncoder.COMPRESSION_QUALITY, Integer.valueOf(quality));
    }

    @CheckResult
    @NonNull
    public RequestOptions frame(@IntRange(from = 0) long frameTimeMicros) {
        return set(VideoDecoder.TARGET_FRAME, Long.valueOf(frameTimeMicros));
    }

    @CheckResult
    @NonNull
    public RequestOptions format(@NonNull DecodeFormat format) {
        Preconditions.checkNotNull(format);
        return set(Downsampler.DECODE_FORMAT, format).set(GifOptions.DECODE_FORMAT, format);
    }

    @CheckResult
    @NonNull
    public RequestOptions disallowHardwareConfig() {
        return set(Downsampler.ALLOW_HARDWARE_CONFIG, false);
    }

    @CheckResult
    @NonNull
    public RequestOptions downsample(@NonNull DownsampleStrategy strategy) {
        return set(DownsampleStrategy.OPTION, Preconditions.checkNotNull(strategy));
    }

    @CheckResult
    @NonNull
    public RequestOptions timeout(@IntRange(from = 0) int timeoutMs) {
        return set(HttpGlideUrlLoader.TIMEOUT, Integer.valueOf(timeoutMs));
    }

    @CheckResult
    @NonNull
    public RequestOptions optionalCenterCrop() {
        return optionalTransform(DownsampleStrategy.CENTER_OUTSIDE, new CenterCrop());
    }

    @CheckResult
    @NonNull
    public RequestOptions centerCrop() {
        return transform(DownsampleStrategy.CENTER_OUTSIDE, new CenterCrop());
    }

    @CheckResult
    @NonNull
    public RequestOptions optionalFitCenter() {
        return optionalScaleOnlyTransform(DownsampleStrategy.FIT_CENTER, new FitCenter());
    }

    @CheckResult
    @NonNull
    public RequestOptions fitCenter() {
        return scaleOnlyTransform(DownsampleStrategy.FIT_CENTER, new FitCenter());
    }

    @CheckResult
    @NonNull
    public RequestOptions optionalCenterInside() {
        return optionalScaleOnlyTransform(DownsampleStrategy.CENTER_INSIDE, new CenterInside());
    }

    @CheckResult
    @NonNull
    public RequestOptions centerInside() {
        return scaleOnlyTransform(DownsampleStrategy.CENTER_INSIDE, new CenterInside());
    }

    @CheckResult
    @NonNull
    public RequestOptions optionalCircleCrop() {
        return optionalTransform(DownsampleStrategy.CENTER_OUTSIDE, new CircleCrop());
    }

    @CheckResult
    @NonNull
    public RequestOptions circleCrop() {
        return transform(DownsampleStrategy.CENTER_INSIDE, new CircleCrop());
    }

    @NonNull
    final RequestOptions optionalTransform(@NonNull DownsampleStrategy downsampleStrategy, @NonNull Transformation<Bitmap> transformation) {
        if (this.isAutoCloneEnabled) {
            return m240clone().optionalTransform(downsampleStrategy, transformation);
        }
        downsample(downsampleStrategy);
        return transform(transformation, false);
    }

    @CheckResult
    @NonNull
    final RequestOptions transform(@NonNull DownsampleStrategy downsampleStrategy, @NonNull Transformation<Bitmap> transformation) {
        if (this.isAutoCloneEnabled) {
            return m240clone().transform(downsampleStrategy, transformation);
        }
        downsample(downsampleStrategy);
        return transform(transformation);
    }

    @NonNull
    private RequestOptions scaleOnlyTransform(@NonNull DownsampleStrategy strategy, @NonNull Transformation<Bitmap> transformation) {
        return scaleOnlyTransform(strategy, transformation, true);
    }

    @NonNull
    private RequestOptions optionalScaleOnlyTransform(@NonNull DownsampleStrategy strategy, @NonNull Transformation<Bitmap> transformation) {
        return scaleOnlyTransform(strategy, transformation, false);
    }

    @NonNull
    private RequestOptions scaleOnlyTransform(@NonNull DownsampleStrategy strategy, @NonNull Transformation<Bitmap> transformation, boolean isTransformationRequired) {
        RequestOptions result = isTransformationRequired ? transform(strategy, transformation) : optionalTransform(strategy, transformation);
        result.isScaleOnlyOrNoTransform = true;
        return result;
    }

    @CheckResult
    @NonNull
    public RequestOptions transform(@NonNull Transformation<Bitmap> transformation) {
        return transform(transformation, true);
    }

    @CheckResult
    @NonNull
    public RequestOptions transforms(@NonNull Transformation<Bitmap>... transformations) {
        return transform((Transformation<Bitmap>) new MultiTransformation(transformations), true);
    }

    @CheckResult
    @NonNull
    public RequestOptions optionalTransform(@NonNull Transformation<Bitmap> transformation) {
        return transform(transformation, false);
    }

    @NonNull
    private RequestOptions transform(@NonNull Transformation<Bitmap> transformation, boolean isRequired) {
        if (this.isAutoCloneEnabled) {
            return m240clone().transform(transformation, isRequired);
        }
        DrawableTransformation drawableTransformation = new DrawableTransformation(transformation, isRequired);
        transform(Bitmap.class, transformation, isRequired);
        transform(Drawable.class, drawableTransformation, isRequired);
        transform(BitmapDrawable.class, drawableTransformation.asBitmapDrawable(), isRequired);
        transform(GifDrawable.class, new GifDrawableTransformation(transformation), isRequired);
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public <T> RequestOptions optionalTransform(@NonNull Class<T> resourceClass, @NonNull Transformation<T> transformation) {
        return transform(resourceClass, transformation, false);
    }

    @NonNull
    private <T> RequestOptions transform(@NonNull Class<T> resourceClass, @NonNull Transformation<T> transformation, boolean isRequired) {
        if (this.isAutoCloneEnabled) {
            return m240clone().transform(resourceClass, transformation, isRequired);
        }
        Preconditions.checkNotNull(resourceClass);
        Preconditions.checkNotNull(transformation);
        this.transformations.put(resourceClass, transformation);
        this.fields |= 2048;
        this.isTransformationAllowed = true;
        this.fields |= 65536;
        this.isScaleOnlyOrNoTransform = false;
        if (isRequired) {
            this.fields |= 131072;
            this.isTransformationRequired = true;
        }
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public <T> RequestOptions transform(@NonNull Class<T> resourceClass, @NonNull Transformation<T> transformation) {
        return transform(resourceClass, transformation, true);
    }

    @CheckResult
    @NonNull
    public RequestOptions dontTransform() {
        if (this.isAutoCloneEnabled) {
            return m240clone().dontTransform();
        }
        this.transformations.clear();
        this.fields &= -2049;
        this.isTransformationRequired = false;
        this.fields &= -131073;
        this.isTransformationAllowed = false;
        this.fields |= 65536;
        this.isScaleOnlyOrNoTransform = true;
        return selfOrThrowIfLocked();
    }

    @CheckResult
    @NonNull
    public RequestOptions dontAnimate() {
        return set(GifOptions.DISABLE_ANIMATION, true);
    }

    @CheckResult
    @NonNull
    public RequestOptions apply(@NonNull RequestOptions other) {
        if (this.isAutoCloneEnabled) {
            return m240clone().apply(other);
        }
        if (isSet(other.fields, 2)) {
            this.sizeMultiplier = other.sizeMultiplier;
        }
        if (isSet(other.fields, 262144)) {
            this.useUnlimitedSourceGeneratorsPool = other.useUnlimitedSourceGeneratorsPool;
        }
        if (isSet(other.fields, 1048576)) {
            this.useAnimationPool = other.useAnimationPool;
        }
        if (isSet(other.fields, 4)) {
            this.diskCacheStrategy = other.diskCacheStrategy;
        }
        if (isSet(other.fields, 8)) {
            this.priority = other.priority;
        }
        if (isSet(other.fields, 16)) {
            this.errorPlaceholder = other.errorPlaceholder;
            this.errorId = 0;
            this.fields &= -33;
        }
        if (isSet(other.fields, 32)) {
            this.errorId = other.errorId;
            this.errorPlaceholder = null;
            this.fields &= -17;
        }
        if (isSet(other.fields, 64)) {
            this.placeholderDrawable = other.placeholderDrawable;
            this.placeholderId = 0;
            this.fields &= -129;
        }
        if (isSet(other.fields, 128)) {
            this.placeholderId = other.placeholderId;
            this.placeholderDrawable = null;
            this.fields &= -65;
        }
        if (isSet(other.fields, 256)) {
            this.isCacheable = other.isCacheable;
        }
        if (isSet(other.fields, 512)) {
            this.overrideWidth = other.overrideWidth;
            this.overrideHeight = other.overrideHeight;
        }
        if (isSet(other.fields, 1024)) {
            this.signature = other.signature;
        }
        if (isSet(other.fields, 4096)) {
            this.resourceClass = other.resourceClass;
        }
        if (isSet(other.fields, 8192)) {
            this.fallbackDrawable = other.fallbackDrawable;
            this.fallbackId = 0;
            this.fields &= -16385;
        }
        if (isSet(other.fields, 16384)) {
            this.fallbackId = other.fallbackId;
            this.fallbackDrawable = null;
            this.fields &= -8193;
        }
        if (isSet(other.fields, 32768)) {
            this.theme = other.theme;
        }
        if (isSet(other.fields, 65536)) {
            this.isTransformationAllowed = other.isTransformationAllowed;
        }
        if (isSet(other.fields, 131072)) {
            this.isTransformationRequired = other.isTransformationRequired;
        }
        if (isSet(other.fields, 2048)) {
            this.transformations.putAll(other.transformations);
            this.isScaleOnlyOrNoTransform = other.isScaleOnlyOrNoTransform;
        }
        if (isSet(other.fields, 524288)) {
            this.onlyRetrieveFromCache = other.onlyRetrieveFromCache;
        }
        if (!this.isTransformationAllowed) {
            this.transformations.clear();
            this.fields &= -2049;
            this.isTransformationRequired = false;
            this.fields &= -131073;
            this.isScaleOnlyOrNoTransform = true;
        }
        this.fields |= other.fields;
        this.options.putAll(other.options);
        return selfOrThrowIfLocked();
    }

    public boolean equals(Object o) {
        if (o instanceof RequestOptions) {
            RequestOptions other = (RequestOptions) o;
            return Float.compare(other.sizeMultiplier, this.sizeMultiplier) == 0 && this.errorId == other.errorId && Util.bothNullOrEqual(this.errorPlaceholder, other.errorPlaceholder) && this.placeholderId == other.placeholderId && Util.bothNullOrEqual(this.placeholderDrawable, other.placeholderDrawable) && this.fallbackId == other.fallbackId && Util.bothNullOrEqual(this.fallbackDrawable, other.fallbackDrawable) && this.isCacheable == other.isCacheable && this.overrideHeight == other.overrideHeight && this.overrideWidth == other.overrideWidth && this.isTransformationRequired == other.isTransformationRequired && this.isTransformationAllowed == other.isTransformationAllowed && this.useUnlimitedSourceGeneratorsPool == other.useUnlimitedSourceGeneratorsPool && this.onlyRetrieveFromCache == other.onlyRetrieveFromCache && this.diskCacheStrategy.equals(other.diskCacheStrategy) && this.priority == other.priority && this.options.equals(other.options) && this.transformations.equals(other.transformations) && this.resourceClass.equals(other.resourceClass) && Util.bothNullOrEqual(this.signature, other.signature) && Util.bothNullOrEqual(this.theme, other.theme);
        }
        return false;
    }

    public int hashCode() {
        int hashCode = Util.hashCode(this.sizeMultiplier);
        return Util.hashCode(this.theme, Util.hashCode(this.signature, Util.hashCode(this.resourceClass, Util.hashCode(this.transformations, Util.hashCode(this.options, Util.hashCode(this.priority, Util.hashCode(this.diskCacheStrategy, Util.hashCode(this.onlyRetrieveFromCache, Util.hashCode(this.useUnlimitedSourceGeneratorsPool, Util.hashCode(this.isTransformationAllowed, Util.hashCode(this.isTransformationRequired, Util.hashCode(this.overrideWidth, Util.hashCode(this.overrideHeight, Util.hashCode(this.isCacheable, Util.hashCode(this.fallbackDrawable, Util.hashCode(this.fallbackId, Util.hashCode(this.placeholderDrawable, Util.hashCode(this.placeholderId, Util.hashCode(this.errorPlaceholder, Util.hashCode(this.errorId, hashCode))))))))))))))))))));
    }

    @NonNull
    public RequestOptions lock() {
        this.isLocked = true;
        return this;
    }

    @NonNull
    public RequestOptions autoClone() {
        if (this.isLocked && !this.isAutoCloneEnabled) {
            throw new IllegalStateException("You cannot auto lock an already locked options object, try clone() first");
        }
        this.isAutoCloneEnabled = true;
        return lock();
    }

    @NonNull
    private RequestOptions selfOrThrowIfLocked() {
        if (this.isLocked) {
            throw new IllegalStateException("You cannot modify locked RequestOptions, consider clone()");
        }
        return this;
    }

    protected boolean isAutoCloneEnabled() {
        return this.isAutoCloneEnabled;
    }

    public final boolean isDiskCacheStrategySet() {
        return isSet(4);
    }

    public final boolean isSkipMemoryCacheSet() {
        return isSet(256);
    }

    @NonNull
    public final Map<Class<?>, Transformation<?>> getTransformations() {
        return this.transformations;
    }

    public final boolean isTransformationRequired() {
        return this.isTransformationRequired;
    }

    @NonNull
    public final Options getOptions() {
        return this.options;
    }

    @NonNull
    public final Class<?> getResourceClass() {
        return this.resourceClass;
    }

    @NonNull
    public final DiskCacheStrategy getDiskCacheStrategy() {
        return this.diskCacheStrategy;
    }

    @Nullable
    public final Drawable getErrorPlaceholder() {
        return this.errorPlaceholder;
    }

    public final int getErrorId() {
        return this.errorId;
    }

    public final int getPlaceholderId() {
        return this.placeholderId;
    }

    @Nullable
    public final Drawable getPlaceholderDrawable() {
        return this.placeholderDrawable;
    }

    public final int getFallbackId() {
        return this.fallbackId;
    }

    @Nullable
    public final Drawable getFallbackDrawable() {
        return this.fallbackDrawable;
    }

    @Nullable
    public final Resources.Theme getTheme() {
        return this.theme;
    }

    public final boolean isMemoryCacheable() {
        return this.isCacheable;
    }

    @NonNull
    public final Key getSignature() {
        return this.signature;
    }

    public final boolean isPrioritySet() {
        return isSet(8);
    }

    @NonNull
    public final Priority getPriority() {
        return this.priority;
    }

    public final int getOverrideWidth() {
        return this.overrideWidth;
    }

    public final boolean isValidOverride() {
        return Util.isValidDimensions(this.overrideWidth, this.overrideHeight);
    }

    public final int getOverrideHeight() {
        return this.overrideHeight;
    }

    public final float getSizeMultiplier() {
        return this.sizeMultiplier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isScaleOnlyOrNoTransform() {
        return this.isScaleOnlyOrNoTransform;
    }

    private boolean isSet(int flag) {
        return isSet(this.fields, flag);
    }

    public final boolean getUseUnlimitedSourceGeneratorsPool() {
        return this.useUnlimitedSourceGeneratorsPool;
    }

    public final boolean getUseAnimationPool() {
        return this.useAnimationPool;
    }

    public final boolean getOnlyRetrieveFromCache() {
        return this.onlyRetrieveFromCache;
    }
}
