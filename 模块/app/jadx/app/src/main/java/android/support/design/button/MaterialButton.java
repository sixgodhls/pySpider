package android.support.design.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.design.internal.ThemeEnforcement;
import android.support.design.internal.ViewUtils;
import android.support.design.resources.MaterialResources;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public class MaterialButton extends AppCompatButton {
    public static final int ICON_GRAVITY_START = 1;
    public static final int ICON_GRAVITY_TEXT_START = 2;
    private static final String LOG_TAG = "MaterialButton";
    private Drawable icon;
    private int iconGravity;
    @Px
    private int iconLeft;
    @Px
    private int iconPadding;
    @Px
    private int iconSize;
    private ColorStateList iconTint;
    private PorterDuff.Mode iconTintMode;
    @Nullable
    private final MaterialButtonHelper materialButtonHelper;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface IconGravity {
    }

    public MaterialButton(Context context) {
        this(context, null);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialButtonStyle);
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = ThemeEnforcement.obtainStyledAttributes(context, attrs, R.styleable.MaterialButton, defStyleAttr, R.style.Widget_MaterialComponents_Button, new int[0]);
        this.iconPadding = attributes.getDimensionPixelSize(R.styleable.MaterialButton_iconPadding, 0);
        this.iconTintMode = ViewUtils.parseTintMode(attributes.getInt(R.styleable.MaterialButton_iconTintMode, -1), PorterDuff.Mode.SRC_IN);
        this.iconTint = MaterialResources.getColorStateList(getContext(), attributes, R.styleable.MaterialButton_iconTint);
        this.icon = MaterialResources.getDrawable(getContext(), attributes, R.styleable.MaterialButton_icon);
        this.iconGravity = attributes.getInteger(R.styleable.MaterialButton_iconGravity, 1);
        this.iconSize = attributes.getDimensionPixelSize(R.styleable.MaterialButton_iconSize, 0);
        this.materialButtonHelper = new MaterialButtonHelper(this);
        this.materialButtonHelper.loadFromAttributes(attributes);
        attributes.recycle();
        setCompoundDrawablePadding(this.iconPadding);
        updateIcon();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT < 21 && isUsingOriginalBackground()) {
            this.materialButtonHelper.drawStroke(canvas);
        }
    }

    @Override // android.support.v7.widget.AppCompatButton, android.support.v4.view.TintableBackgroundView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        if (isUsingOriginalBackground()) {
            this.materialButtonHelper.setSupportBackgroundTintList(tint);
        } else if (this.materialButtonHelper != null) {
            super.setSupportBackgroundTintList(tint);
        }
    }

    @Override // android.support.v7.widget.AppCompatButton, android.support.v4.view.TintableBackgroundView
    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public ColorStateList getSupportBackgroundTintList() {
        if (isUsingOriginalBackground()) {
            return this.materialButtonHelper.getSupportBackgroundTintList();
        }
        return super.getSupportBackgroundTintList();
    }

    @Override // android.support.v7.widget.AppCompatButton, android.support.v4.view.TintableBackgroundView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (isUsingOriginalBackground()) {
            this.materialButtonHelper.setSupportBackgroundTintMode(tintMode);
        } else if (this.materialButtonHelper != null) {
            super.setSupportBackgroundTintMode(tintMode);
        }
    }

    @Override // android.support.v7.widget.AppCompatButton, android.support.v4.view.TintableBackgroundView
    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        if (isUsingOriginalBackground()) {
            return this.materialButtonHelper.getSupportBackgroundTintMode();
        }
        return super.getSupportBackgroundTintMode();
    }

    @Override // android.view.View
    public void setBackgroundTintList(@Nullable ColorStateList tintList) {
        setSupportBackgroundTintList(tintList);
    }

    @Override // android.view.View
    @Nullable
    public ColorStateList getBackgroundTintList() {
        return getSupportBackgroundTintList();
    }

    @Override // android.view.View
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        setSupportBackgroundTintMode(tintMode);
    }

    @Override // android.view.View
    @Nullable
    public PorterDuff.Mode getBackgroundTintMode() {
        return getSupportBackgroundTintMode();
    }

    @Override // android.view.View
    public void setBackgroundColor(@ColorInt int color) {
        if (isUsingOriginalBackground()) {
            this.materialButtonHelper.setBackgroundColor(color);
        } else {
            super.setBackgroundColor(color);
        }
    }

    @Override // android.view.View
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Override // android.support.v7.widget.AppCompatButton, android.view.View
    public void setBackgroundResource(@DrawableRes int backgroundResourceId) {
        Drawable background = null;
        if (backgroundResourceId != 0) {
            background = AppCompatResources.getDrawable(getContext(), backgroundResourceId);
        }
        setBackgroundDrawable(background);
    }

    @Override // android.support.v7.widget.AppCompatButton, android.view.View
    public void setBackgroundDrawable(Drawable background) {
        if (isUsingOriginalBackground()) {
            if (background != getBackground()) {
                Log.i(LOG_TAG, "Setting a custom background is not supported.");
                this.materialButtonHelper.setBackgroundOverwritten();
                super.setBackgroundDrawable(background);
                return;
            }
            getBackground().setState(background.getState());
            return;
        }
        super.setBackgroundDrawable(background);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.AppCompatButton, android.widget.TextView, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        MaterialButtonHelper materialButtonHelper;
        super.onLayout(changed, left, top, right, bottom);
        if (Build.VERSION.SDK_INT == 21 && (materialButtonHelper = this.materialButtonHelper) != null) {
            materialButtonHelper.updateMaskBounds(bottom - top, right - left);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.icon == null || this.iconGravity != 2) {
            return;
        }
        Paint textPaint = getPaint();
        int textWidth = (int) textPaint.measureText(getText().toString());
        int localIconSize = this.iconSize;
        if (localIconSize == 0) {
            localIconSize = this.icon.getIntrinsicWidth();
        }
        int newIconLeft = (((((getMeasuredWidth() - textWidth) - ViewCompat.getPaddingEnd(this)) - localIconSize) - this.iconPadding) - ViewCompat.getPaddingStart(this)) / 2;
        if (isLayoutRTL()) {
            newIconLeft = -newIconLeft;
        }
        if (this.iconLeft != newIconLeft) {
            this.iconLeft = newIconLeft;
            updateIcon();
        }
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(this) == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInternalBackground(Drawable background) {
        super.setBackgroundDrawable(background);
    }

    public void setIconPadding(@Px int iconPadding) {
        if (this.iconPadding != iconPadding) {
            this.iconPadding = iconPadding;
            setCompoundDrawablePadding(iconPadding);
        }
    }

    @Px
    public int getIconPadding() {
        return this.iconPadding;
    }

    public void setIconSize(@Px int iconSize) {
        if (iconSize < 0) {
            throw new IllegalArgumentException("iconSize cannot be less than 0");
        }
        if (this.iconSize != iconSize) {
            this.iconSize = iconSize;
            updateIcon();
        }
    }

    @Px
    public int getIconSize() {
        return this.iconSize;
    }

    public void setIcon(Drawable icon) {
        if (this.icon != icon) {
            this.icon = icon;
            updateIcon();
        }
    }

    public void setIconResource(@DrawableRes int iconResourceId) {
        Drawable icon = null;
        if (iconResourceId != 0) {
            icon = AppCompatResources.getDrawable(getContext(), iconResourceId);
        }
        setIcon(icon);
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIconTint(@Nullable ColorStateList iconTint) {
        if (this.iconTint != iconTint) {
            this.iconTint = iconTint;
            updateIcon();
        }
    }

    public void setIconTintResource(@ColorRes int iconTintResourceId) {
        setIconTint(AppCompatResources.getColorStateList(getContext(), iconTintResourceId));
    }

    public ColorStateList getIconTint() {
        return this.iconTint;
    }

    public void setIconTintMode(PorterDuff.Mode iconTintMode) {
        if (this.iconTintMode != iconTintMode) {
            this.iconTintMode = iconTintMode;
            updateIcon();
        }
    }

    public PorterDuff.Mode getIconTintMode() {
        return this.iconTintMode;
    }

    private void updateIcon() {
        Drawable drawable = this.icon;
        if (drawable != null) {
            this.icon = drawable.mutate();
            DrawableCompat.setTintList(this.icon, this.iconTint);
            PorterDuff.Mode mode = this.iconTintMode;
            if (mode != null) {
                DrawableCompat.setTintMode(this.icon, mode);
            }
            int width = this.iconSize;
            if (width == 0) {
                width = this.icon.getIntrinsicWidth();
            }
            int height = this.iconSize;
            if (height == 0) {
                height = this.icon.getIntrinsicHeight();
            }
            Drawable drawable2 = this.icon;
            int i = this.iconLeft;
            drawable2.setBounds(i, 0, i + width, height);
        }
        TextViewCompat.setCompoundDrawablesRelative(this, this.icon, null, null, null);
    }

    public void setRippleColor(@Nullable ColorStateList rippleColor) {
        if (isUsingOriginalBackground()) {
            this.materialButtonHelper.setRippleColor(rippleColor);
        }
    }

    public void setRippleColorResource(@ColorRes int rippleColorResourceId) {
        if (isUsingOriginalBackground()) {
            setRippleColor(AppCompatResources.getColorStateList(getContext(), rippleColorResourceId));
        }
    }

    public ColorStateList getRippleColor() {
        if (isUsingOriginalBackground()) {
            return this.materialButtonHelper.getRippleColor();
        }
        return null;
    }

    public void setStrokeColor(@Nullable ColorStateList strokeColor) {
        if (isUsingOriginalBackground()) {
            this.materialButtonHelper.setStrokeColor(strokeColor);
        }
    }

    public void setStrokeColorResource(@ColorRes int strokeColorResourceId) {
        if (isUsingOriginalBackground()) {
            setStrokeColor(AppCompatResources.getColorStateList(getContext(), strokeColorResourceId));
        }
    }

    public ColorStateList getStrokeColor() {
        if (isUsingOriginalBackground()) {
            return this.materialButtonHelper.getStrokeColor();
        }
        return null;
    }

    public void setStrokeWidth(@Px int strokeWidth) {
        if (isUsingOriginalBackground()) {
            this.materialButtonHelper.setStrokeWidth(strokeWidth);
        }
    }

    public void setStrokeWidthResource(@DimenRes int strokeWidthResourceId) {
        if (isUsingOriginalBackground()) {
            setStrokeWidth(getResources().getDimensionPixelSize(strokeWidthResourceId));
        }
    }

    @Px
    public int getStrokeWidth() {
        if (isUsingOriginalBackground()) {
            return this.materialButtonHelper.getStrokeWidth();
        }
        return 0;
    }

    public void setCornerRadius(@Px int cornerRadius) {
        if (isUsingOriginalBackground()) {
            this.materialButtonHelper.setCornerRadius(cornerRadius);
        }
    }

    public void setCornerRadiusResource(@DimenRes int cornerRadiusResourceId) {
        if (isUsingOriginalBackground()) {
            setCornerRadius(getResources().getDimensionPixelSize(cornerRadiusResourceId));
        }
    }

    @Px
    public int getCornerRadius() {
        if (isUsingOriginalBackground()) {
            return this.materialButtonHelper.getCornerRadius();
        }
        return 0;
    }

    public int getIconGravity() {
        return this.iconGravity;
    }

    public void setIconGravity(int iconGravity) {
        this.iconGravity = iconGravity;
    }

    private boolean isUsingOriginalBackground() {
        MaterialButtonHelper materialButtonHelper = this.materialButtonHelper;
        return materialButtonHelper != null && !materialButtonHelper.isBackgroundOverwritten();
    }
}
