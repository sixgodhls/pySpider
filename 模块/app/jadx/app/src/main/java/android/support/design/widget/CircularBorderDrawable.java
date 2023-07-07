package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.ColorUtils;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class CircularBorderDrawable extends Drawable {
    private static final float DRAW_STROKE_WIDTH_MULTIPLE = 1.3333f;
    private ColorStateList borderTint;
    @Dimension
    float borderWidth;
    @ColorInt
    private int bottomInnerStrokeColor;
    @ColorInt
    private int bottomOuterStrokeColor;
    @ColorInt
    private int currentBorderTintColor;
    @FloatRange(from = 0.0d, to = 360.0d)
    private float rotation;
    @ColorInt
    private int topInnerStrokeColor;
    @ColorInt
    private int topOuterStrokeColor;
    final Rect rect = new Rect();
    final RectF rectF = new RectF();
    final CircularBorderState state = new CircularBorderState();
    private boolean invalidateShader = true;
    final Paint paint = new Paint(1);

    public CircularBorderDrawable() {
        this.paint.setStyle(Paint.Style.STROKE);
    }

    @Override // android.graphics.drawable.Drawable
    @Nullable
    public Drawable.ConstantState getConstantState() {
        return this.state;
    }

    public void setGradientColors(@ColorInt int topOuterStrokeColor, @ColorInt int topInnerStrokeColor, @ColorInt int bottomOuterStrokeColor, @ColorInt int bottomInnerStrokeColor) {
        this.topOuterStrokeColor = topOuterStrokeColor;
        this.topInnerStrokeColor = topInnerStrokeColor;
        this.bottomOuterStrokeColor = bottomOuterStrokeColor;
        this.bottomInnerStrokeColor = bottomInnerStrokeColor;
    }

    public void setBorderWidth(@Dimension float width) {
        if (this.borderWidth != width) {
            this.borderWidth = width;
            this.paint.setStrokeWidth(DRAW_STROKE_WIDTH_MULTIPLE * width);
            this.invalidateShader = true;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.invalidateShader) {
            this.paint.setShader(createGradientShader());
            this.invalidateShader = false;
        }
        float halfBorderWidth = this.paint.getStrokeWidth() / 2.0f;
        RectF rectF = this.rectF;
        copyBounds(this.rect);
        rectF.set(this.rect);
        rectF.left += halfBorderWidth;
        rectF.top += halfBorderWidth;
        rectF.right -= halfBorderWidth;
        rectF.bottom -= halfBorderWidth;
        canvas.save();
        canvas.rotate(this.rotation, rectF.centerX(), rectF.centerY());
        canvas.drawOval(rectF, this.paint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        int borderWidth = Math.round(this.borderWidth);
        padding.set(borderWidth, borderWidth, borderWidth, borderWidth);
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        this.paint.setAlpha(alpha);
        invalidateSelf();
    }

    public void setBorderTint(ColorStateList tint) {
        if (tint != null) {
            this.currentBorderTintColor = tint.getColorForState(getState(), this.currentBorderTintColor);
        }
        this.borderTint = tint;
        this.invalidateShader = true;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.borderWidth > 0.0f ? -3 : -2;
    }

    public final void setRotation(float rotation) {
        if (rotation != this.rotation) {
            this.rotation = rotation;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        this.invalidateShader = true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        ColorStateList colorStateList = this.borderTint;
        return (colorStateList != null && colorStateList.isStateful()) || super.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        int newColor;
        ColorStateList colorStateList = this.borderTint;
        if (colorStateList != null && (newColor = colorStateList.getColorForState(state, this.currentBorderTintColor)) != this.currentBorderTintColor) {
            this.invalidateShader = true;
            this.currentBorderTintColor = newColor;
        }
        if (this.invalidateShader) {
            invalidateSelf();
        }
        return this.invalidateShader;
    }

    private Shader createGradientShader() {
        Rect rect = this.rect;
        copyBounds(rect);
        float borderRatio = this.borderWidth / rect.height();
        int[] colors = {ColorUtils.compositeColors(this.topOuterStrokeColor, this.currentBorderTintColor), ColorUtils.compositeColors(this.topInnerStrokeColor, this.currentBorderTintColor), ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.topInnerStrokeColor, 0), this.currentBorderTintColor), ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.bottomInnerStrokeColor, 0), this.currentBorderTintColor), ColorUtils.compositeColors(this.bottomInnerStrokeColor, this.currentBorderTintColor), ColorUtils.compositeColors(this.bottomOuterStrokeColor, this.currentBorderTintColor)};
        float[] positions = {0.0f, borderRatio, 0.5f, 0.5f, 1.0f - borderRatio, 1.0f};
        return new LinearGradient(0.0f, rect.top, 0.0f, rect.bottom, colors, positions, Shader.TileMode.CLAMP);
    }

    /* loaded from: classes.dex */
    private class CircularBorderState extends Drawable.ConstantState {
        private CircularBorderState() {
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        @NonNull
        public Drawable newDrawable() {
            return CircularBorderDrawable.this;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }
    }
}
