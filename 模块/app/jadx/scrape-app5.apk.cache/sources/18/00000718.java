package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.design.C0091R;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.support.p000v4.view.PointerIconCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.widget.TextViewCompat;
import android.support.p003v7.view.menu.MenuItemImpl;
import android.support.p003v7.view.menu.MenuView;
import android.support.p003v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class BottomNavigationItemView extends FrameLayout implements MenuView.ItemView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    public static final int INVALID_ITEM_POSITION = -1;
    private final int defaultMargin;
    private ImageView icon;
    private ColorStateList iconTint;
    private boolean isShifting;
    private MenuItemImpl itemData;
    private int itemPosition;
    private int labelVisibilityMode;
    private final TextView largeLabel;
    private float scaleDownFactor;
    private float scaleUpFactor;
    private float shiftAmount;
    private final TextView smallLabel;

    public BottomNavigationItemView(@NonNull Context context) {
        this(context, null);
    }

    public BottomNavigationItemView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.itemPosition = -1;
        Resources res = getResources();
        LayoutInflater.from(context).inflate(C0091R.layout.design_bottom_navigation_item, (ViewGroup) this, true);
        setBackgroundResource(C0091R.C0093drawable.design_bottom_navigation_item_background);
        this.defaultMargin = res.getDimensionPixelSize(C0091R.dimen.design_bottom_navigation_margin);
        this.icon = (ImageView) findViewById(C0091R.C0094id.icon);
        this.smallLabel = (TextView) findViewById(C0091R.C0094id.smallLabel);
        this.largeLabel = (TextView) findViewById(C0091R.C0094id.largeLabel);
        ViewCompat.setImportantForAccessibility(this.smallLabel, 2);
        ViewCompat.setImportantForAccessibility(this.largeLabel, 2);
        setFocusable(true);
        calculateTextScaleFactors(this.smallLabel.getTextSize(), this.largeLabel.getTextSize());
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public void initialize(MenuItemImpl itemData, int menuType) {
        this.itemData = itemData;
        setCheckable(itemData.isCheckable());
        setChecked(itemData.isChecked());
        setEnabled(itemData.isEnabled());
        setIcon(itemData.getIcon());
        setTitle(itemData.getTitle());
        setId(itemData.getItemId());
        if (!TextUtils.isEmpty(itemData.getContentDescription())) {
            setContentDescription(itemData.getContentDescription());
        }
        TooltipCompat.setTooltipText(this, itemData.getTooltipText());
        setVisibility(itemData.isVisible() ? 0 : 8);
    }

    public void setItemPosition(int position) {
        this.itemPosition = position;
    }

    public int getItemPosition() {
        return this.itemPosition;
    }

    public void setShifting(boolean shifting) {
        if (this.isShifting != shifting) {
            this.isShifting = shifting;
            boolean initialized = this.itemData != null;
            if (initialized) {
                setChecked(this.itemData.isChecked());
            }
        }
    }

    public void setLabelVisibilityMode(int mode) {
        if (this.labelVisibilityMode != mode) {
            this.labelVisibilityMode = mode;
            boolean initialized = this.itemData != null;
            if (initialized) {
                setChecked(this.itemData.isChecked());
            }
        }
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.itemData;
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public void setTitle(CharSequence title) {
        this.smallLabel.setText(title);
        this.largeLabel.setText(title);
        MenuItemImpl menuItemImpl = this.itemData;
        if (menuItemImpl == null || TextUtils.isEmpty(menuItemImpl.getContentDescription())) {
            setContentDescription(title);
        }
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public void setCheckable(boolean checkable) {
        refreshDrawableState();
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public void setChecked(boolean checked) {
        TextView textView = this.largeLabel;
        textView.setPivotX(textView.getWidth() / 2);
        TextView textView2 = this.largeLabel;
        textView2.setPivotY(textView2.getBaseline());
        TextView textView3 = this.smallLabel;
        textView3.setPivotX(textView3.getWidth() / 2);
        TextView textView4 = this.smallLabel;
        textView4.setPivotY(textView4.getBaseline());
        switch (this.labelVisibilityMode) {
            case -1:
                if (this.isShifting) {
                    if (checked) {
                        setViewLayoutParams(this.icon, this.defaultMargin, 49);
                        setViewValues(this.largeLabel, 1.0f, 1.0f, 0);
                    } else {
                        setViewLayoutParams(this.icon, this.defaultMargin, 17);
                        setViewValues(this.largeLabel, 0.5f, 0.5f, 4);
                    }
                    this.smallLabel.setVisibility(4);
                    break;
                } else if (checked) {
                    setViewLayoutParams(this.icon, (int) (this.defaultMargin + this.shiftAmount), 49);
                    setViewValues(this.largeLabel, 1.0f, 1.0f, 0);
                    TextView textView5 = this.smallLabel;
                    float f = this.scaleUpFactor;
                    setViewValues(textView5, f, f, 4);
                    break;
                } else {
                    setViewLayoutParams(this.icon, this.defaultMargin, 49);
                    TextView textView6 = this.largeLabel;
                    float f2 = this.scaleDownFactor;
                    setViewValues(textView6, f2, f2, 4);
                    setViewValues(this.smallLabel, 1.0f, 1.0f, 0);
                    break;
                }
            case 0:
                if (checked) {
                    setViewLayoutParams(this.icon, this.defaultMargin, 49);
                    setViewValues(this.largeLabel, 1.0f, 1.0f, 0);
                } else {
                    setViewLayoutParams(this.icon, this.defaultMargin, 17);
                    setViewValues(this.largeLabel, 0.5f, 0.5f, 4);
                }
                this.smallLabel.setVisibility(4);
                break;
            case 1:
                if (checked) {
                    setViewLayoutParams(this.icon, (int) (this.defaultMargin + this.shiftAmount), 49);
                    setViewValues(this.largeLabel, 1.0f, 1.0f, 0);
                    TextView textView7 = this.smallLabel;
                    float f3 = this.scaleUpFactor;
                    setViewValues(textView7, f3, f3, 4);
                    break;
                } else {
                    setViewLayoutParams(this.icon, this.defaultMargin, 49);
                    TextView textView8 = this.largeLabel;
                    float f4 = this.scaleDownFactor;
                    setViewValues(textView8, f4, f4, 4);
                    setViewValues(this.smallLabel, 1.0f, 1.0f, 0);
                    break;
                }
            case 2:
                setViewLayoutParams(this.icon, this.defaultMargin, 17);
                this.largeLabel.setVisibility(8);
                this.smallLabel.setVisibility(8);
                break;
        }
        refreshDrawableState();
        setSelected(checked);
    }

    private void setViewLayoutParams(@NonNull View view, int topMargin, int gravity) {
        FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        viewParams.topMargin = topMargin;
        viewParams.gravity = gravity;
        view.setLayoutParams(viewParams);
    }

    private void setViewValues(@NonNull View view, float scaleX, float scaleY, int visibility) {
        view.setScaleX(scaleX);
        view.setScaleY(scaleY);
        view.setVisibility(visibility);
    }

    @Override // android.view.View, android.support.p003v7.view.menu.MenuView.ItemView
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.smallLabel.setEnabled(enabled);
        this.largeLabel.setEnabled(enabled);
        this.icon.setEnabled(enabled);
        if (enabled) {
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), 1002));
        } else {
            ViewCompat.setPointerIcon(this, null);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        MenuItemImpl menuItemImpl = this.itemData;
        if (menuItemImpl != null && menuItemImpl.isCheckable() && this.itemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public void setShortcut(boolean showShortcut, char shortcutKey) {
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public void setIcon(Drawable iconDrawable) {
        if (iconDrawable != null) {
            Drawable.ConstantState state = iconDrawable.getConstantState();
            iconDrawable = DrawableCompat.wrap(state == null ? iconDrawable : state.newDrawable()).mutate();
            DrawableCompat.setTintList(iconDrawable, this.iconTint);
        }
        this.icon.setImageDrawable(iconDrawable);
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override // android.support.p003v7.view.menu.MenuView.ItemView
    public boolean showsIcon() {
        return true;
    }

    public void setIconTintList(ColorStateList tint) {
        this.iconTint = tint;
        MenuItemImpl menuItemImpl = this.itemData;
        if (menuItemImpl != null) {
            setIcon(menuItemImpl.getIcon());
        }
    }

    public void setIconSize(int iconSize) {
        FrameLayout.LayoutParams iconParams = (FrameLayout.LayoutParams) this.icon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        this.icon.setLayoutParams(iconParams);
    }

    public void setTextAppearanceInactive(@StyleRes int inactiveTextAppearance) {
        TextViewCompat.setTextAppearance(this.smallLabel, inactiveTextAppearance);
        calculateTextScaleFactors(this.smallLabel.getTextSize(), this.largeLabel.getTextSize());
    }

    public void setTextAppearanceActive(@StyleRes int activeTextAppearance) {
        TextViewCompat.setTextAppearance(this.largeLabel, activeTextAppearance);
        calculateTextScaleFactors(this.smallLabel.getTextSize(), this.largeLabel.getTextSize());
    }

    public void setTextColor(@Nullable ColorStateList color) {
        if (color != null) {
            this.smallLabel.setTextColor(color);
            this.largeLabel.setTextColor(color);
        }
    }

    private void calculateTextScaleFactors(float smallLabelSize, float largeLabelSize) {
        this.shiftAmount = smallLabelSize - largeLabelSize;
        this.scaleUpFactor = (largeLabelSize * 1.0f) / smallLabelSize;
        this.scaleDownFactor = (1.0f * smallLabelSize) / largeLabelSize;
    }

    public void setItemBackground(int background) {
        Drawable backgroundDrawable = background == 0 ? null : ContextCompat.getDrawable(getContext(), background);
        setItemBackground(backgroundDrawable);
    }

    public void setItemBackground(@Nullable Drawable background) {
        ViewCompat.setBackground(this, background);
    }
}