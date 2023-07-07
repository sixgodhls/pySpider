package android.support.design.card;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.design.R;
import android.support.design.internal.ThemeEnforcement;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class MaterialCardView extends CardView {
    private final MaterialCardViewHelper cardViewHelper;

    public MaterialCardView(Context context) {
        this(context, null);
    }

    public MaterialCardView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialCardViewStyle);
    }

    public MaterialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = ThemeEnforcement.obtainStyledAttributes(context, attrs, R.styleable.MaterialCardView, defStyleAttr, R.style.Widget_MaterialComponents_CardView, new int[0]);
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

    @Override // android.support.v7.widget.CardView
    public void setRadius(float radius) {
        super.setRadius(radius);
        this.cardViewHelper.updateForeground();
    }
}
