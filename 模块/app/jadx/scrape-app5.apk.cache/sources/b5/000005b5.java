package android.support.design.card;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.design.C0091R;
import android.support.design.internal.ThemeEnforcement;
import android.support.p003v7.widget.CardView;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class MaterialCardView extends CardView {
    private final MaterialCardViewHelper cardViewHelper;

    public MaterialCardView(Context context) {
        this(context, null);
    }

    public MaterialCardView(Context context, AttributeSet attrs) {
        this(context, attrs, C0091R.attr.materialCardViewStyle);
    }

    public MaterialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = ThemeEnforcement.obtainStyledAttributes(context, attrs, C0091R.styleable.MaterialCardView, defStyleAttr, C0091R.style.Widget_MaterialComponents_CardView, new int[0]);
        this.cardViewHelper = new MaterialCardViewHelper(this);
        this.cardViewHelper.loadFromAttributes(attributes);
        attributes.recycle();
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        this.cardViewHelper.setStrokeColor(strokeColor);
    }

    @ColorInt
    public int getStrokeColor() {
        return this.cardViewHelper.getStrokeColor();
    }

    public void setStrokeWidth(@Dimension int strokeWidth) {
        this.cardViewHelper.setStrokeWidth(strokeWidth);
    }

    @Dimension
    public int getStrokeWidth() {
        return this.cardViewHelper.getStrokeWidth();
    }

    @Override // android.support.p003v7.widget.CardView
    public void setRadius(float radius) {
        super.setRadius(radius);
        this.cardViewHelper.updateForeground();
    }
}