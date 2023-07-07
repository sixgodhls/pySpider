package me.zhanghai.android.materialprogressbar;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class BaseDrawable extends Drawable implements TintableDrawable {
    protected ColorFilter mColorFilter;
    protected PorterDuffColorFilter mTintFilter;
    protected ColorStateList mTintList;
    protected int mAlpha = 255;
    protected PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;
    private DummyConstantState mConstantState = new DummyConstantState();

    protected abstract void onDraw(Canvas canvas, int i, int i2);

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.mAlpha != alpha) {
            this.mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    public void setTint(@ColorInt int tintColor) {
        setTintList(ColorStateList.valueOf(tintColor));
    }

    @Override // android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    public void setTintList(@Nullable ColorStateList tint) {
        this.mTintList = tint;
        if (updateTintFilter()) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable, me.zhanghai.android.materialprogressbar.TintableDrawable
    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {
        this.mTintMode = tintMode;
        if (updateTintFilter()) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        ColorStateList colorStateList = this.mTintList;
        return colorStateList != null && colorStateList.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        return updateTintFilter();
    }

    private boolean updateTintFilter() {
        ColorStateList colorStateList = this.mTintList;
        boolean z = true;
        if (colorStateList == null || this.mTintMode == null) {
            if (this.mTintFilter == null) {
                z = false;
            }
            boolean hadTintFilter = z;
            this.mTintFilter = null;
            return hadTintFilter;
        }
        int tintColor = colorStateList.getColorForState(getState(), 0);
        this.mTintFilter = new PorterDuffColorFilter(tintColor, this.mTintMode);
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.width() == 0 || bounds.height() == 0) {
            return;
        }
        int saveCount = canvas.save();
        canvas.translate(bounds.left, bounds.top);
        onDraw(canvas, bounds.width(), bounds.height());
        canvas.restoreToCount(saveCount);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ColorFilter getColorFilterForDrawing() {
        ColorFilter colorFilter = this.mColorFilter;
        return colorFilter != null ? colorFilter : this.mTintFilter;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mConstantState;
    }

    /* loaded from: classes.dex */
    private class DummyConstantState extends Drawable.ConstantState {
        private DummyConstantState() {
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        @NonNull
        public Drawable newDrawable() {
            return BaseDrawable.this;
        }
    }
}
