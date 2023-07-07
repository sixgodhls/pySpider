package android.support.design.card;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.RestrictTo;
import android.support.design.C0091R;

/* JADX INFO: Access modifiers changed from: package-private */
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class MaterialCardViewHelper {
    private static final int DEFAULT_STROKE_VALUE = -1;
    private final MaterialCardView materialCardView;
    private int strokeColor;
    private int strokeWidth;

    public MaterialCardViewHelper(MaterialCardView card) {
        this.materialCardView = card;
    }

    public void loadFromAttributes(TypedArray attributes) {
        this.strokeColor = attributes.getColor(C0091R.styleable.MaterialCardView_strokeColor, -1);
        this.strokeWidth = attributes.getDimensionPixelSize(C0091R.styleable.MaterialCardView_strokeWidth, 0);
        updateForeground();
        adjustContentPadding();
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        this.strokeColor = strokeColor;
        updateForeground();
    }

    @ColorInt
    public int getStrokeColor() {
        return this.strokeColor;
    }

    public void setStrokeWidth(@Dimension int strokeWidth) {
        this.strokeWidth = strokeWidth;
        updateForeground();
        adjustContentPadding();
    }

    @Dimension
    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public void updateForeground() {
        this.materialCardView.setForeground(createForegroundDrawable());
    }

    private Drawable createForegroundDrawable() {
        GradientDrawable fgDrawable = new GradientDrawable();
        fgDrawable.setCornerRadius(this.materialCardView.getRadius());
        int i = this.strokeColor;
        if (i != -1) {
            fgDrawable.setStroke(this.strokeWidth, i);
        }
        return fgDrawable;
    }

    private void adjustContentPadding() {
        int contentPaddingLeft = this.materialCardView.getContentPaddingLeft() + this.strokeWidth;
        int contentPaddingTop = this.materialCardView.getContentPaddingTop() + this.strokeWidth;
        int contentPaddingRight = this.materialCardView.getContentPaddingRight() + this.strokeWidth;
        int contentPaddingBottom = this.materialCardView.getContentPaddingBottom() + this.strokeWidth;
        this.materialCardView.setContentPadding(contentPaddingLeft, contentPaddingTop, contentPaddingRight, contentPaddingBottom);
    }
}