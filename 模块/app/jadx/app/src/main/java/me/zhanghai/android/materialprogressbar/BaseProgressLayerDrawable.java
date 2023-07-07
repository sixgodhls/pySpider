package me.zhanghai.android.materialprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable;
import me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable;
import me.zhanghai.android.materialprogressbar.TintableDrawable;
import me.zhanghai.android.materialprogressbar.internal.ThemeUtils;

/* loaded from: classes.dex */
class BaseProgressLayerDrawable<ProgressDrawableType extends IntrinsicPaddingDrawable & ShowBackgroundDrawable & TintableDrawable, BackgroundDrawableType extends IntrinsicPaddingDrawable & ShowBackgroundDrawable & TintableDrawable> extends LayerDrawable implements IntrinsicPaddingDrawable, MaterialProgressDrawable, ShowBackgroundDrawable, TintableDrawable {
    private float mBackgroundAlpha;
    private BackgroundDrawableType mBackgroundDrawable = (BackgroundDrawableType) ((IntrinsicPaddingDrawable) getDrawable(0));
    private ProgressDrawableType mSecondaryProgressDrawable = (ProgressDrawableType) ((IntrinsicPaddingDrawable) getDrawable(1));
    private ProgressDrawableType mProgressDrawable = (ProgressDrawableType) ((IntrinsicPaddingDrawable) getDrawable(2));

    public BaseProgressLayerDrawable(Drawable[] layers, Context context) {
        super(layers);
        this.mBackgroundAlpha = ThemeUtils.getFloatFromAttrRes(16842803, 0.0f, context);
        setId(0, 16908288);
        setId(1, 16908303);
        setId(2, 16908301);
        int controlActivatedColor = ThemeUtils.getColorFromAttrRes(R.attr.colorControlActivated, ViewCompat.MEASURED_STATE_MASK, context);
        setTint(controlActivatedColor);
    }

    @Override // me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable
    public boolean getShowBackground() {
        return this.mBackgroundDrawable.getShowBackground();
    }

    @Override // me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable
    public void setShowBackground(boolean show) {
        if (this.mBackgroundDrawable.getShowBackground() != show) {
            this.mBackgroundDrawable.setShowBackground(show);
            this.mSecondaryProgressDrawable.setShowBackground(!show);
        }
    }

    @Override // me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable
    public boolean getUseIntrinsicPadding() {
        return this.mBackgroundDrawable.getUseIntrinsicPadding();
    }

    @Override // me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable
    public void setUseIntrinsicPadding(boolean useIntrinsicPadding) {
        this.mBackgroundDrawable.setUseIntrinsicPadding(useIntrinsicPadding);
        this.mSecondaryProgressDrawable.setUseIntrinsicPadding(useIntrinsicPadding);
        this.mProgressDrawable.setUseIntrinsicPadding(useIntrinsicPadding);
    }

    @Override // android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    @SuppressLint({"NewApi"})
    public void setTint(@ColorInt int tintColor) {
        int backgroundTintColor = ColorUtils.setAlphaComponent(tintColor, Math.round(Color.alpha(tintColor) * this.mBackgroundAlpha));
        this.mBackgroundDrawable.setTint(backgroundTintColor);
        this.mSecondaryProgressDrawable.setTint(backgroundTintColor);
        this.mProgressDrawable.setTint(tintColor);
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    @SuppressLint({"NewApi"})
    public void setTintList(@Nullable ColorStateList tint) {
        ColorStateList backgroundTint;
        if (tint != null) {
            if (!tint.isOpaque()) {
                Log.w(getClass().getSimpleName(), "setTintList() called with a non-opaque ColorStateList, its original alpha will be discarded");
            }
            backgroundTint = tint.withAlpha(Math.round(this.mBackgroundAlpha * 255.0f));
        } else {
            backgroundTint = null;
        }
        this.mBackgroundDrawable.setTintList(backgroundTint);
        this.mSecondaryProgressDrawable.setTintList(backgroundTint);
        this.mProgressDrawable.setTintList(tint);
    }

    @Override // android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    @SuppressLint({"NewApi"})
    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {
        this.mBackgroundDrawable.setTintMode(tintMode);
        this.mSecondaryProgressDrawable.setTintMode(tintMode);
        this.mProgressDrawable.setTintMode(tintMode);
    }
}
