package me.zhanghai.android.materialprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* loaded from: classes.dex */
public class CircularProgressDrawable extends BaseProgressLayerDrawable<SingleCircularProgressDrawable, CircularProgressBackgroundDrawable> {
    @Override // me.zhanghai.android.materialprogressbar.BaseProgressLayerDrawable, me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable
    public /* bridge */ /* synthetic */ boolean getShowBackground() {
        return super.getShowBackground();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseProgressLayerDrawable, me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable
    public /* bridge */ /* synthetic */ boolean getUseIntrinsicPadding() {
        return super.getUseIntrinsicPadding();
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseProgressLayerDrawable, me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable
    public /* bridge */ /* synthetic */ void setShowBackground(boolean z) {
        super.setShowBackground(z);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseProgressLayerDrawable, android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    @SuppressLint({"NewApi"})
    public /* bridge */ /* synthetic */ void setTint(@ColorInt int i) {
        super.setTint(i);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseProgressLayerDrawable, android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    @SuppressLint({"NewApi"})
    public /* bridge */ /* synthetic */ void setTintList(@Nullable ColorStateList colorStateList) {
        super.setTintList(colorStateList);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseProgressLayerDrawable, android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    @SuppressLint({"NewApi"})
    public /* bridge */ /* synthetic */ void setTintMode(@NonNull PorterDuff.Mode mode) {
        super.setTintMode(mode);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseProgressLayerDrawable, me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable
    public /* bridge */ /* synthetic */ void setUseIntrinsicPadding(boolean z) {
        super.setUseIntrinsicPadding(z);
    }

    public CircularProgressDrawable(int style, Context context) {
        super(new Drawable[]{new CircularProgressBackgroundDrawable(), new SingleCircularProgressDrawable(style), new SingleCircularProgressDrawable(style)}, context);
    }
}
