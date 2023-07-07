package me.zhanghai.android.materialprogressbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;
import me.zhanghai.android.materialprogressbar.internal.DrawableCompat;

/* loaded from: classes.dex */
public class MaterialProgressBar extends ProgressBar {
    public static final int DETERMINATE_CIRCULAR_PROGRESS_STYLE_DYNAMIC = 1;
    public static final int DETERMINATE_CIRCULAR_PROGRESS_STYLE_NORMAL = 0;
    public static final int PROGRESS_STYLE_CIRCULAR = 0;
    public static final int PROGRESS_STYLE_HORIZONTAL = 1;
    private static final String TAG = MaterialProgressBar.class.getSimpleName();
    private int mProgressStyle;
    private boolean mSuperInitialized = true;
    private TintInfo mProgressTintInfo = new TintInfo();

    public MaterialProgressBar(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Context context = getContext();
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.MaterialProgressBar, defStyleAttr, defStyleRes);
        this.mProgressStyle = a.getInt(R.styleable.MaterialProgressBar_mpb_progressStyle, 0);
        boolean setBothDrawables = a.getBoolean(R.styleable.MaterialProgressBar_mpb_setBothDrawables, false);
        boolean useIntrinsicPadding = a.getBoolean(R.styleable.MaterialProgressBar_mpb_useIntrinsicPadding, true);
        boolean showProgressBackground = a.getBoolean(R.styleable.MaterialProgressBar_mpb_showProgressBackground, this.mProgressStyle == 1);
        int determinateCircularProgressStyle = a.getInt(R.styleable.MaterialProgressBar_mpb_determinateCircularProgressStyle, 0);
        if (a.hasValue(R.styleable.MaterialProgressBar_mpb_progressTint)) {
            this.mProgressTintInfo.mProgressTint = a.getColorStateList(R.styleable.MaterialProgressBar_mpb_progressTint);
            this.mProgressTintInfo.mHasProgressTint = true;
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_mpb_progressTintMode)) {
            this.mProgressTintInfo.mProgressTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MaterialProgressBar_mpb_progressTintMode, -1), null);
            this.mProgressTintInfo.mHasProgressTintMode = true;
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_mpb_secondaryProgressTint)) {
            this.mProgressTintInfo.mSecondaryProgressTint = a.getColorStateList(R.styleable.MaterialProgressBar_mpb_secondaryProgressTint);
            this.mProgressTintInfo.mHasSecondaryProgressTint = true;
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_mpb_secondaryProgressTintMode)) {
            this.mProgressTintInfo.mSecondaryProgressTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MaterialProgressBar_mpb_secondaryProgressTintMode, -1), null);
            this.mProgressTintInfo.mHasSecondaryProgressTintMode = true;
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_mpb_progressBackgroundTint)) {
            this.mProgressTintInfo.mProgressBackgroundTint = a.getColorStateList(R.styleable.MaterialProgressBar_mpb_progressBackgroundTint);
            this.mProgressTintInfo.mHasProgressBackgroundTint = true;
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_mpb_progressBackgroundTintMode)) {
            this.mProgressTintInfo.mProgressBackgroundTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MaterialProgressBar_mpb_progressBackgroundTintMode, -1), null);
            this.mProgressTintInfo.mHasProgressBackgroundTintMode = true;
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_mpb_indeterminateTint)) {
            this.mProgressTintInfo.mIndeterminateTint = a.getColorStateList(R.styleable.MaterialProgressBar_mpb_indeterminateTint);
            this.mProgressTintInfo.mHasIndeterminateTint = true;
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_mpb_indeterminateTintMode)) {
            this.mProgressTintInfo.mIndeterminateTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MaterialProgressBar_mpb_indeterminateTintMode, -1), null);
            this.mProgressTintInfo.mHasIndeterminateTintMode = true;
        }
        a.recycle();
        switch (this.mProgressStyle) {
            case 0:
                if ((isIndeterminate() || setBothDrawables) && !isInEditMode()) {
                    setIndeterminateDrawable(new IndeterminateCircularProgressDrawable(context));
                }
                if (!isIndeterminate() || setBothDrawables) {
                    setProgressDrawable(new CircularProgressDrawable(determinateCircularProgressStyle, context));
                    break;
                }
                break;
            case 1:
                if ((isIndeterminate() || setBothDrawables) && !isInEditMode()) {
                    setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));
                }
                if (!isIndeterminate() || setBothDrawables) {
                    setProgressDrawable(new HorizontalProgressDrawable(context));
                    break;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown progress style: " + this.mProgressStyle);
        }
        setUseIntrinsicPadding(useIntrinsicPadding);
        setShowProgressBackground(showProgressBackground);
    }

    @Override // android.widget.ProgressBar
    public synchronized void setIndeterminate(boolean indeterminate) {
        super.setIndeterminate(indeterminate);
        if (this.mSuperInitialized && !(getCurrentDrawable() instanceof MaterialProgressDrawable)) {
            Log.w(TAG, "Current drawable is not a MaterialProgressDrawable, you may want to set app:mpb_setBothDrawables");
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        fixCanvasScalingWhenHardwareAccelerated();
    }

    private void fixCanvasScalingWhenHardwareAccelerated() {
        if (Build.VERSION.SDK_INT < 18 && isHardwareAccelerated() && getLayerType() != 1) {
            setLayerType(1, null);
        }
    }

    public int getProgressStyle() {
        return this.mProgressStyle;
    }

    @Override // android.widget.ProgressBar
    public Drawable getCurrentDrawable() {
        return isIndeterminate() ? getIndeterminateDrawable() : getProgressDrawable();
    }

    public boolean getUseIntrinsicPadding() {
        Drawable drawable = getCurrentDrawable();
        if (drawable instanceof IntrinsicPaddingDrawable) {
            return ((IntrinsicPaddingDrawable) drawable).getUseIntrinsicPadding();
        }
        throw new IllegalStateException("Drawable does not implement IntrinsicPaddingDrawable");
    }

    public void setUseIntrinsicPadding(boolean useIntrinsicPadding) {
        Drawable drawable = getCurrentDrawable();
        if (drawable instanceof IntrinsicPaddingDrawable) {
            ((IntrinsicPaddingDrawable) drawable).setUseIntrinsicPadding(useIntrinsicPadding);
        }
        Drawable indeterminateDrawable = getIndeterminateDrawable();
        if (indeterminateDrawable instanceof IntrinsicPaddingDrawable) {
            ((IntrinsicPaddingDrawable) indeterminateDrawable).setUseIntrinsicPadding(useIntrinsicPadding);
        }
    }

    public boolean getShowProgressBackground() {
        Drawable drawable = getCurrentDrawable();
        if (drawable instanceof ShowBackgroundDrawable) {
            return ((ShowBackgroundDrawable) drawable).getShowBackground();
        }
        return false;
    }

    public void setShowProgressBackground(boolean show) {
        Drawable drawable = getCurrentDrawable();
        if (drawable instanceof ShowBackgroundDrawable) {
            ((ShowBackgroundDrawable) drawable).setShowBackground(show);
        }
        Drawable indeterminateDrawable = getIndeterminateDrawable();
        if (indeterminateDrawable instanceof ShowBackgroundDrawable) {
            ((ShowBackgroundDrawable) indeterminateDrawable).setShowBackground(show);
        }
    }

    @Override // android.widget.ProgressBar
    public void setProgressDrawable(Drawable d) {
        super.setProgressDrawable(d);
        if (this.mProgressTintInfo != null) {
            applyProgressTints();
        }
    }

    @Override // android.widget.ProgressBar
    public void setIndeterminateDrawable(Drawable d) {
        super.setIndeterminateDrawable(d);
        if (this.mProgressTintInfo != null) {
            applyIndeterminateTint();
        }
    }

    @Override // android.widget.ProgressBar
    @Nullable
    public ColorStateList getProgressTintList() {
        return this.mProgressTintInfo.mProgressTint;
    }

    @Override // android.widget.ProgressBar
    public void setProgressTintList(@Nullable ColorStateList tint) {
        TintInfo tintInfo = this.mProgressTintInfo;
        tintInfo.mProgressTint = tint;
        tintInfo.mHasProgressTint = true;
        applyPrimaryProgressTint();
    }

    @Override // android.widget.ProgressBar
    @Nullable
    public PorterDuff.Mode getProgressTintMode() {
        return this.mProgressTintInfo.mProgressTintMode;
    }

    @Override // android.widget.ProgressBar
    public void setProgressTintMode(@Nullable PorterDuff.Mode tintMode) {
        TintInfo tintInfo = this.mProgressTintInfo;
        tintInfo.mProgressTintMode = tintMode;
        tintInfo.mHasProgressTintMode = true;
        applyPrimaryProgressTint();
    }

    @Override // android.widget.ProgressBar
    @Nullable
    public ColorStateList getSecondaryProgressTintList() {
        return this.mProgressTintInfo.mSecondaryProgressTint;
    }

    @Override // android.widget.ProgressBar
    public void setSecondaryProgressTintList(@Nullable ColorStateList tint) {
        TintInfo tintInfo = this.mProgressTintInfo;
        tintInfo.mSecondaryProgressTint = tint;
        tintInfo.mHasSecondaryProgressTint = true;
        applySecondaryProgressTint();
    }

    @Override // android.widget.ProgressBar
    @Nullable
    public PorterDuff.Mode getSecondaryProgressTintMode() {
        return this.mProgressTintInfo.mSecondaryProgressTintMode;
    }

    @Override // android.widget.ProgressBar
    public void setSecondaryProgressTintMode(@Nullable PorterDuff.Mode tintMode) {
        TintInfo tintInfo = this.mProgressTintInfo;
        tintInfo.mSecondaryProgressTintMode = tintMode;
        tintInfo.mHasSecondaryProgressTintMode = true;
        applySecondaryProgressTint();
    }

    @Override // android.widget.ProgressBar
    @Nullable
    public ColorStateList getProgressBackgroundTintList() {
        return this.mProgressTintInfo.mProgressBackgroundTint;
    }

    @Override // android.widget.ProgressBar
    public void setProgressBackgroundTintList(@Nullable ColorStateList tint) {
        TintInfo tintInfo = this.mProgressTintInfo;
        tintInfo.mProgressBackgroundTint = tint;
        tintInfo.mHasProgressBackgroundTint = true;
        applyProgressBackgroundTint();
    }

    @Override // android.widget.ProgressBar
    @Nullable
    public PorterDuff.Mode getProgressBackgroundTintMode() {
        return this.mProgressTintInfo.mProgressBackgroundTintMode;
    }

    @Override // android.widget.ProgressBar
    public void setProgressBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        TintInfo tintInfo = this.mProgressTintInfo;
        tintInfo.mProgressBackgroundTintMode = tintMode;
        tintInfo.mHasProgressBackgroundTintMode = true;
        applyProgressBackgroundTint();
    }

    @Override // android.widget.ProgressBar
    @Nullable
    public ColorStateList getIndeterminateTintList() {
        return this.mProgressTintInfo.mIndeterminateTint;
    }

    @Override // android.widget.ProgressBar
    public void setIndeterminateTintList(@Nullable ColorStateList tint) {
        TintInfo tintInfo = this.mProgressTintInfo;
        tintInfo.mIndeterminateTint = tint;
        tintInfo.mHasIndeterminateTint = true;
        applyIndeterminateTint();
    }

    @Override // android.widget.ProgressBar
    @Nullable
    public PorterDuff.Mode getIndeterminateTintMode() {
        return this.mProgressTintInfo.mIndeterminateTintMode;
    }

    @Override // android.widget.ProgressBar
    public void setIndeterminateTintMode(@Nullable PorterDuff.Mode tintMode) {
        TintInfo tintInfo = this.mProgressTintInfo;
        tintInfo.mIndeterminateTintMode = tintMode;
        tintInfo.mHasIndeterminateTintMode = true;
        applyIndeterminateTint();
    }

    private void applyProgressTints() {
        if (getProgressDrawable() == null) {
            return;
        }
        applyPrimaryProgressTint();
        applyProgressBackgroundTint();
        applySecondaryProgressTint();
    }

    private void applyPrimaryProgressTint() {
        Drawable target;
        if (getProgressDrawable() == null) {
            return;
        }
        if ((this.mProgressTintInfo.mHasProgressTint || this.mProgressTintInfo.mHasProgressTintMode) && (target = getTintTargetFromProgressDrawable(16908301, true)) != null) {
            applyTintForDrawable(target, this.mProgressTintInfo.mProgressTint, this.mProgressTintInfo.mHasProgressTint, this.mProgressTintInfo.mProgressTintMode, this.mProgressTintInfo.mHasProgressTintMode);
        }
    }

    private void applySecondaryProgressTint() {
        Drawable target;
        if (getProgressDrawable() == null) {
            return;
        }
        if ((this.mProgressTintInfo.mHasSecondaryProgressTint || this.mProgressTintInfo.mHasSecondaryProgressTintMode) && (target = getTintTargetFromProgressDrawable(16908303, false)) != null) {
            applyTintForDrawable(target, this.mProgressTintInfo.mSecondaryProgressTint, this.mProgressTintInfo.mHasSecondaryProgressTint, this.mProgressTintInfo.mSecondaryProgressTintMode, this.mProgressTintInfo.mHasSecondaryProgressTintMode);
        }
    }

    private void applyProgressBackgroundTint() {
        Drawable target;
        if (getProgressDrawable() == null) {
            return;
        }
        if ((this.mProgressTintInfo.mHasProgressBackgroundTint || this.mProgressTintInfo.mHasProgressBackgroundTintMode) && (target = getTintTargetFromProgressDrawable(16908288, false)) != null) {
            applyTintForDrawable(target, this.mProgressTintInfo.mProgressBackgroundTint, this.mProgressTintInfo.mHasProgressBackgroundTint, this.mProgressTintInfo.mProgressBackgroundTintMode, this.mProgressTintInfo.mHasProgressBackgroundTintMode);
        }
    }

    private Drawable getTintTargetFromProgressDrawable(int layerId, boolean shouldFallback) {
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable == null) {
            return null;
        }
        progressDrawable.mutate();
        Drawable layerDrawable = null;
        if (progressDrawable instanceof LayerDrawable) {
            layerDrawable = ((LayerDrawable) progressDrawable).findDrawableByLayerId(layerId);
        }
        if (layerDrawable == null && shouldFallback) {
            return progressDrawable;
        }
        return layerDrawable;
    }

    private void applyIndeterminateTint() {
        Drawable indeterminateDrawable = getIndeterminateDrawable();
        if (indeterminateDrawable == null) {
            return;
        }
        if (this.mProgressTintInfo.mHasIndeterminateTint || this.mProgressTintInfo.mHasIndeterminateTintMode) {
            indeterminateDrawable.mutate();
            applyTintForDrawable(indeterminateDrawable, this.mProgressTintInfo.mIndeterminateTint, this.mProgressTintInfo.mHasIndeterminateTint, this.mProgressTintInfo.mIndeterminateTintMode, this.mProgressTintInfo.mHasIndeterminateTintMode);
        }
    }

    @SuppressLint({"NewApi"})
    private void applyTintForDrawable(Drawable drawable, ColorStateList tint, boolean hasTint, PorterDuff.Mode tintMode, boolean hasTintMode) {
        if (hasTint || hasTintMode) {
            if (hasTint) {
                if (drawable instanceof TintableDrawable) {
                    ((TintableDrawable) drawable).setTintList(tint);
                } else {
                    Log.w(TAG, "Drawable did not implement TintableDrawable, it won't be tinted below Lollipop");
                    if (Build.VERSION.SDK_INT >= 21) {
                        drawable.setTintList(tint);
                    }
                }
            }
            if (hasTintMode) {
                if (drawable instanceof TintableDrawable) {
                    ((TintableDrawable) drawable).setTintMode(tintMode);
                } else {
                    Log.w(TAG, "Drawable did not implement TintableDrawable, it won't be tinted below Lollipop");
                    if (Build.VERSION.SDK_INT >= 21) {
                        drawable.setTintMode(tintMode);
                    }
                }
            }
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TintInfo {
        public boolean mHasIndeterminateTint;
        public boolean mHasIndeterminateTintMode;
        public boolean mHasProgressBackgroundTint;
        public boolean mHasProgressBackgroundTintMode;
        public boolean mHasProgressTint;
        public boolean mHasProgressTintMode;
        public boolean mHasSecondaryProgressTint;
        public boolean mHasSecondaryProgressTintMode;
        public ColorStateList mIndeterminateTint;
        public PorterDuff.Mode mIndeterminateTintMode;
        public ColorStateList mProgressBackgroundTint;
        public PorterDuff.Mode mProgressBackgroundTintMode;
        public ColorStateList mProgressTint;
        public PorterDuff.Mode mProgressTintMode;
        public ColorStateList mSecondaryProgressTint;
        public PorterDuff.Mode mSecondaryProgressTintMode;

        private TintInfo() {
        }
    }
}
