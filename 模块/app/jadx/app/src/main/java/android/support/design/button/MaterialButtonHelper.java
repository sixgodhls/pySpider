package android.support.design.button;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.design.internal.ViewUtils;
import android.support.design.resources.MaterialResources;
import android.support.design.ripple.RippleUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;

/* JADX INFO: Access modifiers changed from: package-private */
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class MaterialButtonHelper {
    private static final float CORNER_RADIUS_ADJUSTMENT = 1.0E-5f;
    private static final int DEFAULT_BACKGROUND_COLOR = -1;
    private static final boolean IS_LOLLIPOP;
    @Nullable
    private GradientDrawable backgroundDrawableLollipop;
    @Nullable
    private ColorStateList backgroundTint;
    @Nullable
    private PorterDuff.Mode backgroundTintMode;
    @Nullable
    private GradientDrawable colorableBackgroundDrawableCompat;
    private int cornerRadius;
    private int insetBottom;
    private int insetLeft;
    private int insetRight;
    private int insetTop;
    @Nullable
    private GradientDrawable maskDrawableLollipop;
    private final MaterialButton materialButton;
    @Nullable
    private ColorStateList rippleColor;
    @Nullable
    private GradientDrawable rippleDrawableCompat;
    @Nullable
    private ColorStateList strokeColor;
    @Nullable
    private GradientDrawable strokeDrawableLollipop;
    private int strokeWidth;
    @Nullable
    private Drawable tintableBackgroundDrawableCompat;
    @Nullable
    private Drawable tintableRippleDrawableCompat;
    private final Paint buttonStrokePaint = new Paint(1);
    private final Rect bounds = new Rect();
    private final RectF rectF = new RectF();
    private boolean backgroundOverwritten = false;

    static {
        IS_LOLLIPOP = Build.VERSION.SDK_INT >= 21;
    }

    public MaterialButtonHelper(MaterialButton button) {
        this.materialButton = button;
    }

    public void loadFromAttributes(TypedArray attributes) {
        int i = 0;
        this.insetLeft = attributes.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetLeft, 0);
        this.insetRight = attributes.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetRight, 0);
        this.insetTop = attributes.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetTop, 0);
        this.insetBottom = attributes.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetBottom, 0);
        this.cornerRadius = attributes.getDimensionPixelSize(R.styleable.MaterialButton_cornerRadius, 0);
        this.strokeWidth = attributes.getDimensionPixelSize(R.styleable.MaterialButton_strokeWidth, 0);
        this.backgroundTintMode = ViewUtils.parseTintMode(attributes.getInt(R.styleable.MaterialButton_backgroundTintMode, -1), PorterDuff.Mode.SRC_IN);
        this.backgroundTint = MaterialResources.getColorStateList(this.materialButton.getContext(), attributes, R.styleable.MaterialButton_backgroundTint);
        this.strokeColor = MaterialResources.getColorStateList(this.materialButton.getContext(), attributes, R.styleable.MaterialButton_strokeColor);
        this.rippleColor = MaterialResources.getColorStateList(this.materialButton.getContext(), attributes, R.styleable.MaterialButton_rippleColor);
        this.buttonStrokePaint.setStyle(Paint.Style.STROKE);
        this.buttonStrokePaint.setStrokeWidth(this.strokeWidth);
        Paint paint = this.buttonStrokePaint;
        ColorStateList colorStateList = this.strokeColor;
        if (colorStateList != null) {
            i = colorStateList.getColorForState(this.materialButton.getDrawableState(), 0);
        }
        paint.setColor(i);
        int paddingStart = ViewCompat.getPaddingStart(this.materialButton);
        int paddingTop = this.materialButton.getPaddingTop();
        int paddingEnd = ViewCompat.getPaddingEnd(this.materialButton);
        int paddingBottom = this.materialButton.getPaddingBottom();
        this.materialButton.setInternalBackground(IS_LOLLIPOP ? createBackgroundLollipop() : createBackgroundCompat());
        ViewCompat.setPaddingRelative(this.materialButton, this.insetLeft + paddingStart, this.insetTop + paddingTop, this.insetRight + paddingEnd, this.insetBottom + paddingBottom);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBackgroundOverwritten() {
        this.backgroundOverwritten = true;
        this.materialButton.setSupportBackgroundTintList(this.backgroundTint);
        this.materialButton.setSupportBackgroundTintMode(this.backgroundTintMode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isBackgroundOverwritten() {
        return this.backgroundOverwritten;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void drawStroke(@Nullable Canvas canvas) {
        if (canvas != null && this.strokeColor != null && this.strokeWidth > 0) {
            this.bounds.set(this.materialButton.getBackground().getBounds());
            this.rectF.set(this.bounds.left + (this.strokeWidth / 2.0f) + this.insetLeft, this.bounds.top + (this.strokeWidth / 2.0f) + this.insetTop, (this.bounds.right - (this.strokeWidth / 2.0f)) - this.insetRight, (this.bounds.bottom - (this.strokeWidth / 2.0f)) - this.insetBottom);
            float strokeCornerRadius = this.cornerRadius - (this.strokeWidth / 2.0f);
            canvas.drawRoundRect(this.rectF, strokeCornerRadius, strokeCornerRadius, this.buttonStrokePaint);
        }
    }

    private Drawable createBackgroundCompat() {
        this.colorableBackgroundDrawableCompat = new GradientDrawable();
        this.colorableBackgroundDrawableCompat.setCornerRadius(this.cornerRadius + CORNER_RADIUS_ADJUSTMENT);
        this.colorableBackgroundDrawableCompat.setColor(-1);
        this.tintableBackgroundDrawableCompat = DrawableCompat.wrap(this.colorableBackgroundDrawableCompat);
        DrawableCompat.setTintList(this.tintableBackgroundDrawableCompat, this.backgroundTint);
        PorterDuff.Mode mode = this.backgroundTintMode;
        if (mode != null) {
            DrawableCompat.setTintMode(this.tintableBackgroundDrawableCompat, mode);
        }
        this.rippleDrawableCompat = new GradientDrawable();
        this.rippleDrawableCompat.setCornerRadius(this.cornerRadius + CORNER_RADIUS_ADJUSTMENT);
        this.rippleDrawableCompat.setColor(-1);
        this.tintableRippleDrawableCompat = DrawableCompat.wrap(this.rippleDrawableCompat);
        DrawableCompat.setTintList(this.tintableRippleDrawableCompat, this.rippleColor);
        return wrapDrawableWithInset(new LayerDrawable(new Drawable[]{this.tintableBackgroundDrawableCompat, this.tintableRippleDrawableCompat}));
    }

    private InsetDrawable wrapDrawableWithInset(Drawable drawable) {
        return new InsetDrawable(drawable, this.insetLeft, this.insetTop, this.insetRight, this.insetBottom);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSupportBackgroundTintList(@Nullable ColorStateList tintList) {
        if (this.backgroundTint != tintList) {
            this.backgroundTint = tintList;
            if (IS_LOLLIPOP) {
                updateTintAndTintModeLollipop();
                return;
            }
            Drawable drawable = this.tintableBackgroundDrawableCompat;
            if (drawable != null) {
                DrawableCompat.setTintList(drawable, this.backgroundTint);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ColorStateList getSupportBackgroundTintList() {
        return this.backgroundTint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        PorterDuff.Mode mode2;
        if (this.backgroundTintMode != mode) {
            this.backgroundTintMode = mode;
            if (IS_LOLLIPOP) {
                updateTintAndTintModeLollipop();
                return;
            }
            Drawable drawable = this.tintableBackgroundDrawableCompat;
            if (drawable != null && (mode2 = this.backgroundTintMode) != null) {
                DrawableCompat.setTintMode(drawable, mode2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return this.backgroundTintMode;
    }

    private void updateTintAndTintModeLollipop() {
        GradientDrawable gradientDrawable = this.backgroundDrawableLollipop;
        if (gradientDrawable != null) {
            DrawableCompat.setTintList(gradientDrawable, this.backgroundTint);
            PorterDuff.Mode mode = this.backgroundTintMode;
            if (mode != null) {
                DrawableCompat.setTintMode(this.backgroundDrawableLollipop, mode);
            }
        }
    }

    @TargetApi(21)
    private Drawable createBackgroundLollipop() {
        this.backgroundDrawableLollipop = new GradientDrawable();
        this.backgroundDrawableLollipop.setCornerRadius(this.cornerRadius + CORNER_RADIUS_ADJUSTMENT);
        this.backgroundDrawableLollipop.setColor(-1);
        updateTintAndTintModeLollipop();
        this.strokeDrawableLollipop = new GradientDrawable();
        this.strokeDrawableLollipop.setCornerRadius(this.cornerRadius + CORNER_RADIUS_ADJUSTMENT);
        this.strokeDrawableLollipop.setColor(0);
        this.strokeDrawableLollipop.setStroke(this.strokeWidth, this.strokeColor);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{this.backgroundDrawableLollipop, this.strokeDrawableLollipop});
        InsetDrawable bgInsetDrawable = wrapDrawableWithInset(layerDrawable);
        this.maskDrawableLollipop = new GradientDrawable();
        this.maskDrawableLollipop.setCornerRadius(this.cornerRadius + CORNER_RADIUS_ADJUSTMENT);
        this.maskDrawableLollipop.setColor(-1);
        return new MaterialButtonBackgroundDrawable(RippleUtils.convertToRippleDrawableColor(this.rippleColor), bgInsetDrawable, this.maskDrawableLollipop);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateMaskBounds(int height, int width) {
        GradientDrawable gradientDrawable = this.maskDrawableLollipop;
        if (gradientDrawable != null) {
            gradientDrawable.setBounds(this.insetLeft, this.insetTop, width - this.insetRight, height - this.insetBottom);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBackgroundColor(int color) {
        GradientDrawable gradientDrawable;
        GradientDrawable gradientDrawable2;
        if (IS_LOLLIPOP && (gradientDrawable2 = this.backgroundDrawableLollipop) != null) {
            gradientDrawable2.setColor(color);
        } else if (!IS_LOLLIPOP && (gradientDrawable = this.colorableBackgroundDrawableCompat) != null) {
            gradientDrawable.setColor(color);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRippleColor(@Nullable ColorStateList rippleColor) {
        Drawable drawable;
        if (this.rippleColor != rippleColor) {
            this.rippleColor = rippleColor;
            if (IS_LOLLIPOP && (this.materialButton.getBackground() instanceof RippleDrawable)) {
                ((RippleDrawable) this.materialButton.getBackground()).setColor(rippleColor);
            } else if (!IS_LOLLIPOP && (drawable = this.tintableRippleDrawableCompat) != null) {
                DrawableCompat.setTintList(drawable, rippleColor);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public ColorStateList getRippleColor() {
        return this.rippleColor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStrokeColor(@Nullable ColorStateList strokeColor) {
        if (this.strokeColor != strokeColor) {
            this.strokeColor = strokeColor;
            Paint paint = this.buttonStrokePaint;
            int i = 0;
            if (strokeColor != null) {
                i = strokeColor.getColorForState(this.materialButton.getDrawableState(), 0);
            }
            paint.setColor(i);
            updateStroke();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public ColorStateList getStrokeColor() {
        return this.strokeColor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStrokeWidth(int strokeWidth) {
        if (this.strokeWidth != strokeWidth) {
            this.strokeWidth = strokeWidth;
            this.buttonStrokePaint.setStrokeWidth(strokeWidth);
            updateStroke();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    private void updateStroke() {
        if (IS_LOLLIPOP && this.strokeDrawableLollipop != null) {
            this.materialButton.setInternalBackground(createBackgroundLollipop());
        } else if (!IS_LOLLIPOP) {
            this.materialButton.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCornerRadius(int cornerRadius) {
        GradientDrawable gradientDrawable;
        if (this.cornerRadius != cornerRadius) {
            this.cornerRadius = cornerRadius;
            if (IS_LOLLIPOP && this.backgroundDrawableLollipop != null && this.strokeDrawableLollipop != null && this.maskDrawableLollipop != null) {
                if (Build.VERSION.SDK_INT == 21) {
                    unwrapBackgroundDrawable().setCornerRadius(cornerRadius + CORNER_RADIUS_ADJUSTMENT);
                    unwrapStrokeDrawable().setCornerRadius(cornerRadius + CORNER_RADIUS_ADJUSTMENT);
                }
                this.backgroundDrawableLollipop.setCornerRadius(cornerRadius + CORNER_RADIUS_ADJUSTMENT);
                this.strokeDrawableLollipop.setCornerRadius(cornerRadius + CORNER_RADIUS_ADJUSTMENT);
                this.maskDrawableLollipop.setCornerRadius(cornerRadius + CORNER_RADIUS_ADJUSTMENT);
            } else if (!IS_LOLLIPOP && (gradientDrawable = this.colorableBackgroundDrawableCompat) != null && this.rippleDrawableCompat != null) {
                gradientDrawable.setCornerRadius(cornerRadius + CORNER_RADIUS_ADJUSTMENT);
                this.rippleDrawableCompat.setCornerRadius(cornerRadius + CORNER_RADIUS_ADJUSTMENT);
                this.materialButton.invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCornerRadius() {
        return this.cornerRadius;
    }

    @Nullable
    private GradientDrawable unwrapStrokeDrawable() {
        if (IS_LOLLIPOP && this.materialButton.getBackground() != null) {
            RippleDrawable background = (RippleDrawable) this.materialButton.getBackground();
            InsetDrawable insetDrawable = (InsetDrawable) background.getDrawable(0);
            LayerDrawable layerDrawable = (LayerDrawable) insetDrawable.getDrawable();
            return (GradientDrawable) layerDrawable.getDrawable(1);
        }
        return null;
    }

    @Nullable
    private GradientDrawable unwrapBackgroundDrawable() {
        if (IS_LOLLIPOP && this.materialButton.getBackground() != null) {
            RippleDrawable background = (RippleDrawable) this.materialButton.getBackground();
            InsetDrawable insetDrawable = (InsetDrawable) background.getDrawable(0);
            LayerDrawable layerDrawable = (LayerDrawable) insetDrawable.getDrawable();
            return (GradientDrawable) layerDrawable.getDrawable(0);
        }
        return null;
    }
}
