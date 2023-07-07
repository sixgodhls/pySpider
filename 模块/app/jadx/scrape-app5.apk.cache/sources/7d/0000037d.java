package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.Option;

/* loaded from: classes.dex */
public abstract class DownsampleStrategy {
    public static final DownsampleStrategy FIT_CENTER = new FitCenter();
    public static final DownsampleStrategy CENTER_OUTSIDE = new CenterOutside();
    public static final DownsampleStrategy AT_LEAST = new AtLeast();
    public static final DownsampleStrategy AT_MOST = new AtMost();
    public static final DownsampleStrategy CENTER_INSIDE = new CenterInside();
    public static final DownsampleStrategy NONE = new None();
    public static final DownsampleStrategy DEFAULT = CENTER_OUTSIDE;
    public static final Option<DownsampleStrategy> OPTION = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.DownsampleStrategy", DEFAULT);

    /* loaded from: classes.dex */
    public enum SampleSizeRounding {
        MEMORY,
        QUALITY
    }

    public abstract SampleSizeRounding getSampleSizeRounding(int i, int i2, int i3, int i4);

    public abstract float getScaleFactor(int i, int i2, int i3, int i4);

    /* loaded from: classes.dex */
    private static class FitCenter extends DownsampleStrategy {
        FitCenter() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            float widthPercentage = requestedWidth / sourceWidth;
            float heightPercentage = requestedHeight / sourceHeight;
            return Math.min(widthPercentage, heightPercentage);
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    /* loaded from: classes.dex */
    private static class CenterOutside extends DownsampleStrategy {
        CenterOutside() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            float widthPercentage = requestedWidth / sourceWidth;
            float heightPercentage = requestedHeight / sourceHeight;
            return Math.max(widthPercentage, heightPercentage);
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    /* loaded from: classes.dex */
    private static class AtLeast extends DownsampleStrategy {
        AtLeast() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            int minIntegerFactor = Math.min(sourceHeight / requestedHeight, sourceWidth / requestedWidth);
            if (minIntegerFactor == 0) {
                return 1.0f;
            }
            return 1.0f / Integer.highestOneBit(minIntegerFactor);
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    /* loaded from: classes.dex */
    private static class AtMost extends DownsampleStrategy {
        AtMost() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            int maxIntegerFactor = (int) Math.ceil(Math.max(sourceHeight / requestedHeight, sourceWidth / requestedWidth));
            int i = 1;
            int lesserOrEqualSampleSize = Math.max(1, Integer.highestOneBit(maxIntegerFactor));
            if (lesserOrEqualSampleSize >= maxIntegerFactor) {
                i = 0;
            }
            int greaterOrEqualSampleSize = lesserOrEqualSampleSize << i;
            return 1.0f / greaterOrEqualSampleSize;
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.MEMORY;
        }
    }

    /* loaded from: classes.dex */
    private static class None extends DownsampleStrategy {
        None() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return 1.0f;
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    /* loaded from: classes.dex */
    private static class CenterInside extends DownsampleStrategy {
        CenterInside() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return Math.min(1.0f, FIT_CENTER.getScaleFactor(sourceWidth, sourceHeight, requestedWidth, requestedHeight));
        }

        @Override // com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }
}