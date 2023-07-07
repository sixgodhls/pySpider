package android.support.design.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.design.R;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.drawable.DrawableWrapper;

/* loaded from: classes.dex */
public class ShadowDrawableWrapper extends DrawableWrapper {
    static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    static final float SHADOW_BOTTOM_SCALE = 1.0f;
    static final float SHADOW_HORIZ_SCALE = 0.5f;
    static final float SHADOW_MULTIPLIER = 1.5f;
    static final float SHADOW_TOP_SCALE = 0.25f;
    float cornerRadius;
    Path cornerShadowPath;
    float maxShadowSize;
    float rawMaxShadowSize;
    float rawShadowSize;
    private float rotation;
    private final int shadowEndColor;
    private final int shadowMiddleColor;
    float shadowSize;
    private final int shadowStartColor;
    private boolean dirty = true;
    private boolean addPaddingForCorners = true;
    private boolean printedShadowClipWarning = false;
    final Paint cornerShadowPaint = new Paint(5);
    final RectF contentBounds = new RectF();
    final Paint edgeShadowPaint = new Paint(this.cornerShadowPaint);

    public ShadowDrawableWrapper(Context context, Drawable content, float radius, float shadowSize, float maxShadowSize) {
        super(content);
        this.shadowStartColor = ContextCompat.getColor(context, R.color.design_fab_shadow_start_color);
        this.shadowMiddleColor = ContextCompat.getColor(context, R.color.design_fab_shadow_mid_color);
        this.shadowEndColor = ContextCompat.getColor(context, R.color.design_fab_shadow_end_color);
        this.cornerShadowPaint.setStyle(Paint.Style.FILL);
        this.cornerRadius = Math.round(radius);
        this.edgeShadowPaint.setAntiAlias(false);
        setShadowSize(shadowSize, maxShadowSize);
    }

    private static int toEven(float value) {
        int i = Math.round(value);
        return i % 2 == 1 ? i - 1 : i;
    }

    public void setAddPaddingForCorners(boolean addPaddingForCorners) {
        this.addPaddingForCorners = addPaddingForCorners;
        invalidateSelf();
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        this.cornerShadowPaint.setAlpha(alpha);
        this.edgeShadowPaint.setAlpha(alpha);
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        this.dirty = true;
    }

    public void setShadowSize(float shadowSize, float maxShadowSize) {
        if (shadowSize < 0.0f || maxShadowSize < 0.0f) {
            throw new IllegalArgumentException("invalid shadow size");
        }
        float shadowSize2 = toEven(shadowSize);
        float maxShadowSize2 = toEven(maxShadowSize);
        if (shadowSize2 > maxShadowSize2) {
            shadowSize2 = maxShadowSize2;
            if (!this.printedShadowClipWarning) {
                this.printedShadowClipWarning = true;
            }
        }
        if (this.rawShadowSize == shadowSize2 && this.rawMaxShadowSize == maxShadowSize2) {
            return;
        }
        this.rawShadowSize = shadowSize2;
        this.rawMaxShadowSize = maxShadowSize2;
        this.shadowSize = Math.round(SHADOW_MULTIPLIER * shadowSize2);
        this.maxShadowSize = maxShadowSize2;
        this.dirty = true;
        invalidateSelf();
    }

    public void setShadowSize(float size) {
        setShadowSize(size, this.rawMaxShadowSize);
    }

    public float getShadowSize() {
        return this.rawShadowSize;
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        int vOffset = (int) Math.ceil(calculateVerticalPadding(this.rawMaxShadowSize, this.cornerRadius, this.addPaddingForCorners));
        int hOffset = (int) Math.ceil(calculateHorizontalPadding(this.rawMaxShadowSize, this.cornerRadius, this.addPaddingForCorners));
        padding.set(hOffset, vOffset, hOffset, vOffset);
        return true;
    }

    public static float calculateVerticalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
        if (addPaddingForCorners) {
            double d = SHADOW_MULTIPLIER * maxShadowSize;
            double d2 = 1.0d - COS_45;
            double d3 = cornerRadius;
            Double.isNaN(d3);
            Double.isNaN(d);
            return (float) (d + (d2 * d3));
        }
        return SHADOW_MULTIPLIER * maxShadowSize;
    }

    public static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
        if (addPaddingForCorners) {
            double d = maxShadowSize;
            double d2 = 1.0d - COS_45;
            double d3 = cornerRadius;
            Double.isNaN(d3);
            Double.isNaN(d);
            return (float) (d + (d2 * d3));
        }
        return maxShadowSize;
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public void setCornerRadius(float radius) {
        float radius2 = Math.round(radius);
        if (this.cornerRadius == radius2) {
            return;
        }
        this.cornerRadius = radius2;
        this.dirty = true;
        invalidateSelf();
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.dirty) {
            buildComponents(getBounds());
            this.dirty = false;
        }
        drawShadow(canvas);
        super.draw(canvas);
    }

    public final void setRotation(float rotation) {
        if (this.rotation != rotation) {
            this.rotation = rotation;
            invalidateSelf();
        }
    }

    private void drawShadow(Canvas canvas) {
        float shadowScaleBottom;
        float shadowScaleTop;
        float shadowScaleHorizontal;
        float shadowOffsetHorizontal;
        int saved;
        float shadowScaleHorizontal2;
        int rotateSaved = canvas.save();
        canvas.rotate(this.rotation, this.contentBounds.centerX(), this.contentBounds.centerY());
        float edgeShadowTop = (-this.cornerRadius) - this.shadowSize;
        float shadowOffset = this.cornerRadius;
        boolean z = true;
        boolean drawHorizontalEdges = this.contentBounds.width() - (shadowOffset * 2.0f) > 0.0f;
        if (this.contentBounds.height() - (shadowOffset * 2.0f) <= 0.0f) {
            z = false;
        }
        boolean drawVerticalEdges = z;
        float f = this.rawShadowSize;
        float shadowOffsetTop = f - (SHADOW_TOP_SCALE * f);
        float shadowOffsetHorizontal2 = f - (SHADOW_HORIZ_SCALE * f);
        float shadowOffsetBottom = f - (f * SHADOW_BOTTOM_SCALE);
        float shadowScaleHorizontal3 = shadowOffset / (shadowOffset + shadowOffsetHorizontal2);
        float shadowScaleTop2 = shadowOffset / (shadowOffset + shadowOffsetTop);
        float shadowScaleBottom2 = shadowOffset / (shadowOffset + shadowOffsetBottom);
        int saved2 = canvas.save();
        canvas.translate(this.contentBounds.left + shadowOffset, this.contentBounds.top + shadowOffset);
        canvas.scale(shadowScaleHorizontal3, shadowScaleTop2);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.scale(SHADOW_BOTTOM_SCALE / shadowScaleHorizontal3, SHADOW_BOTTOM_SCALE);
            float shadowOffsetHorizontal3 = this.contentBounds.width() - (shadowOffset * 2.0f);
            Paint paint = this.edgeShadowPaint;
            saved = saved2;
            shadowScaleBottom = shadowScaleBottom2;
            shadowScaleTop = shadowScaleTop2;
            shadowScaleHorizontal = shadowScaleHorizontal3;
            shadowOffsetHorizontal = SHADOW_BOTTOM_SCALE;
            canvas.drawRect(0.0f, edgeShadowTop, shadowOffsetHorizontal3, -this.cornerRadius, paint);
        } else {
            shadowScaleBottom = shadowScaleBottom2;
            shadowScaleTop = shadowScaleTop2;
            shadowScaleHorizontal = shadowScaleHorizontal3;
            shadowOffsetHorizontal = SHADOW_BOTTOM_SCALE;
            saved = saved2;
        }
        canvas.restoreToCount(saved);
        int saved3 = canvas.save();
        canvas.translate(this.contentBounds.right - shadowOffset, this.contentBounds.bottom - shadowOffset);
        float shadowScaleHorizontal4 = shadowScaleHorizontal;
        canvas.scale(shadowScaleHorizontal4, shadowScaleBottom);
        canvas.rotate(180.0f);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (!drawHorizontalEdges) {
            shadowScaleHorizontal2 = shadowScaleHorizontal4;
        } else {
            canvas.scale(shadowOffsetHorizontal / shadowScaleHorizontal4, shadowOffsetHorizontal);
            shadowScaleHorizontal2 = shadowScaleHorizontal4;
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.width() - (shadowOffset * 2.0f), (-this.cornerRadius) + this.shadowSize, this.edgeShadowPaint);
        }
        canvas.restoreToCount(saved3);
        int saved4 = canvas.save();
        canvas.translate(this.contentBounds.left + shadowOffset, this.contentBounds.bottom - shadowOffset);
        canvas.scale(shadowScaleHorizontal2, shadowScaleBottom);
        canvas.rotate(270.0f);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.scale(SHADOW_BOTTOM_SCALE / shadowScaleBottom, SHADOW_BOTTOM_SCALE);
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.height() - (shadowOffset * 2.0f), -this.cornerRadius, this.edgeShadowPaint);
        }
        canvas.restoreToCount(saved4);
        int saved5 = canvas.save();
        canvas.translate(this.contentBounds.right - shadowOffset, this.contentBounds.top + shadowOffset);
        float shadowScaleTop3 = shadowScaleTop;
        canvas.scale(shadowScaleHorizontal2, shadowScaleTop3);
        canvas.rotate(90.0f);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.scale(SHADOW_BOTTOM_SCALE / shadowScaleTop3, SHADOW_BOTTOM_SCALE);
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.height() - (2.0f * shadowOffset), -this.cornerRadius, this.edgeShadowPaint);
        }
        canvas.restoreToCount(saved5);
        canvas.restoreToCount(rotateSaved);
    }

    private void buildShadowCorners() {
        float f = this.cornerRadius;
        RectF innerBounds = new RectF(-f, -f, f, f);
        RectF outerBounds = new RectF(innerBounds);
        float f2 = this.shadowSize;
        outerBounds.inset(-f2, -f2);
        Path path = this.cornerShadowPath;
        if (path == null) {
            this.cornerShadowPath = new Path();
        } else {
            path.reset();
        }
        this.cornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
        this.cornerShadowPath.moveTo(-this.cornerRadius, 0.0f);
        this.cornerShadowPath.rLineTo(-this.shadowSize, 0.0f);
        this.cornerShadowPath.arcTo(outerBounds, 180.0f, 90.0f, false);
        this.cornerShadowPath.arcTo(innerBounds, 270.0f, -90.0f, false);
        this.cornerShadowPath.close();
        float shadowRadius = -outerBounds.top;
        if (shadowRadius > 0.0f) {
            float startRatio = this.cornerRadius / shadowRadius;
            float midRatio = startRatio + ((SHADOW_BOTTOM_SCALE - startRatio) / 2.0f);
            this.cornerShadowPaint.setShader(new RadialGradient(0.0f, 0.0f, shadowRadius, new int[]{0, this.shadowStartColor, this.shadowMiddleColor, this.shadowEndColor}, new float[]{0.0f, startRatio, midRatio, SHADOW_BOTTOM_SCALE}, Shader.TileMode.CLAMP));
        }
        this.edgeShadowPaint.setShader(new LinearGradient(0.0f, innerBounds.top, 0.0f, outerBounds.top, new int[]{this.shadowStartColor, this.shadowMiddleColor, this.shadowEndColor}, new float[]{0.0f, SHADOW_HORIZ_SCALE, SHADOW_BOTTOM_SCALE}, Shader.TileMode.CLAMP));
        this.edgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect bounds) {
        float verticalOffset = this.rawMaxShadowSize * SHADOW_MULTIPLIER;
        this.contentBounds.set(bounds.left + this.rawMaxShadowSize, bounds.top + verticalOffset, bounds.right - this.rawMaxShadowSize, bounds.bottom - verticalOffset);
        getWrappedDrawable().setBounds((int) this.contentBounds.left, (int) this.contentBounds.top, (int) this.contentBounds.right, (int) this.contentBounds.bottom);
        buildShadowCorners();
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setMaxShadowSize(float size) {
        setShadowSize(this.rawShadowSize, size);
    }

    public float getMaxShadowSize() {
        return this.rawMaxShadowSize;
    }

    public float getMinWidth() {
        float f = this.rawMaxShadowSize;
        float content = Math.max(f, this.cornerRadius + (f / 2.0f)) * 2.0f;
        return (this.rawMaxShadowSize * 2.0f) + content;
    }

    public float getMinHeight() {
        float f = this.rawMaxShadowSize;
        float content = Math.max(f, this.cornerRadius + ((f * SHADOW_MULTIPLIER) / 2.0f)) * 2.0f;
        return (this.rawMaxShadowSize * SHADOW_MULTIPLIER * 2.0f) + content;
    }
}
