package me.zhanghai.android.materialprogressbar;

import android.animation.Animator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* loaded from: classes.dex */
public class IndeterminateCircularProgressDrawable extends BaseIndeterminateProgressDrawable implements MaterialProgressDrawable {
    private static final int PADDED_INTRINSIC_SIZE_DP = 48;
    private static final int PROGRESS_INTRINSIC_SIZE_DP = 42;
    private static final RectF RECT_BOUND = new RectF(-21.0f, -21.0f, 21.0f, 21.0f);
    private static final RectF RECT_PADDED_BOUND = new RectF(-24.0f, -24.0f, 24.0f, 24.0f);
    private static final RectF RECT_PROGRESS = new RectF(-19.0f, -19.0f, 19.0f, 19.0f);
    private int mPaddedIntrinsicSize;
    private int mProgressIntrinsicSize;
    private RingPathTransform mRingPathTransform = new RingPathTransform();
    private RingRotation mRingRotation = new RingRotation();

    @Override // me.zhanghai.android.materialprogressbar.BaseIndeterminateProgressDrawable, me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public /* bridge */ /* synthetic */ void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public /* bridge */ /* synthetic */ int getAlpha() {
        return super.getAlpha();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public /* bridge */ /* synthetic */ ColorFilter getColorFilter() {
        return super.getColorFilter();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public /* bridge */ /* synthetic */ Drawable.ConstantState getConstantState() {
        return super.getConstantState();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public /* bridge */ /* synthetic */ int getOpacity() {
        return super.getOpacity();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseProgressDrawable, me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable
    public /* bridge */ /* synthetic */ boolean getUseIntrinsicPadding() {
        return super.getUseIntrinsicPadding();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseIndeterminateProgressDrawable, android.graphics.drawable.Animatable
    public /* bridge */ /* synthetic */ boolean isRunning() {
        return super.isRunning();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public /* bridge */ /* synthetic */ boolean isStateful() {
        return super.isStateful();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public /* bridge */ /* synthetic */ void setAlpha(int i) {
        super.setAlpha(i);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public /* bridge */ /* synthetic */ void setColorFilter(@Nullable ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    public /* bridge */ /* synthetic */ void setTint(@ColorInt int i) {
        super.setTint(i);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    public /* bridge */ /* synthetic */ void setTintList(@Nullable ColorStateList colorStateList) {
        super.setTintList(colorStateList);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    public /* bridge */ /* synthetic */ void setTintMode(@NonNull PorterDuff.Mode mode) {
        super.setTintMode(mode);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseProgressDrawable, me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable
    public /* bridge */ /* synthetic */ void setUseIntrinsicPadding(boolean z) {
        super.setUseIntrinsicPadding(z);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseIndeterminateProgressDrawable, android.graphics.drawable.Animatable
    public /* bridge */ /* synthetic */ void start() {
        super.start();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseIndeterminateProgressDrawable, android.graphics.drawable.Animatable
    public /* bridge */ /* synthetic */ void stop() {
        super.stop();
    }

    public IndeterminateCircularProgressDrawable(Context context) {
        super(context);
        float density = context.getResources().getDisplayMetrics().density;
        this.mProgressIntrinsicSize = Math.round(42.0f * density);
        this.mPaddedIntrinsicSize = Math.round(48.0f * density);
        this.mAnimators = new Animator[]{Animators.createIndeterminate(this.mRingPathTransform), Animators.createIndeterminateRotation(this.mRingRotation)};
    }

    private int getIntrinsicSize() {
        return this.mUseIntrinsicPadding ? this.mPaddedIntrinsicSize : this.mProgressIntrinsicSize;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return getIntrinsicSize();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return getIntrinsicSize();
    }

    @Override // me.zhanghai.android.materialprogressbar.BasePaintDrawable
    protected void onPreparePaint(Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.0f);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeJoin(Paint.Join.MITER);
    }

    @Override // me.zhanghai.android.materialprogressbar.BasePaintDrawable
    protected void onDraw(Canvas canvas, int width, int height, Paint paint) {
        if (this.mUseIntrinsicPadding) {
            canvas.scale(width / RECT_PADDED_BOUND.width(), height / RECT_PADDED_BOUND.height());
            canvas.translate(RECT_PADDED_BOUND.width() / 2.0f, RECT_PADDED_BOUND.height() / 2.0f);
        } else {
            canvas.scale(width / RECT_BOUND.width(), height / RECT_BOUND.height());
            canvas.translate(RECT_BOUND.width() / 2.0f, RECT_BOUND.height() / 2.0f);
        }
        drawRing(canvas, paint);
    }

    private void drawRing(Canvas canvas, Paint paint) {
        int saveCount = canvas.save();
        canvas.rotate(this.mRingRotation.mRotation);
        float startAngle = ((this.mRingPathTransform.mTrimPathOffset + this.mRingPathTransform.mTrimPathStart) * 360.0f) - 90.0f;
        float sweepAngle = 360.0f * (this.mRingPathTransform.mTrimPathEnd - this.mRingPathTransform.mTrimPathStart);
        canvas.drawArc(RECT_PROGRESS, startAngle, sweepAngle, false, paint);
        canvas.restoreToCount(saveCount);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RingPathTransform {
        public float mTrimPathEnd;
        public float mTrimPathOffset;
        public float mTrimPathStart;

        private RingPathTransform() {
        }

        @Keep
        public void setTrimPathStart(float trimPathStart) {
            this.mTrimPathStart = trimPathStart;
        }

        @Keep
        public void setTrimPathEnd(float trimPathEnd) {
            this.mTrimPathEnd = trimPathEnd;
        }

        @Keep
        public void setTrimPathOffset(float trimPathOffset) {
            this.mTrimPathOffset = trimPathOffset;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RingRotation {
        private float mRotation;

        private RingRotation() {
        }

        @Keep
        public void setRotation(float rotation) {
            this.mRotation = rotation;
        }
    }
}
