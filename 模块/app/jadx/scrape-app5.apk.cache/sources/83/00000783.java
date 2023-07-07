package android.support.design.internal;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.design.C0091R;
import android.support.p000v4.util.Pools;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.animation.FastOutSlowInInterpolator;
import android.support.p003v7.appcompat.C0452R;
import android.support.p003v7.content.res.AppCompatResources;
import android.support.p003v7.view.menu.MenuBuilder;
import android.support.p003v7.view.menu.MenuItemImpl;
import android.support.p003v7.view.menu.MenuView;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class BottomNavigationMenuView extends ViewGroup implements MenuView {
    private static final long ACTIVE_ANIMATION_DURATION_MS = 115;
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private final int activeItemMaxWidth;
    private final int activeItemMinWidth;
    private BottomNavigationItemView[] buttons;
    private final int inactiveItemMaxWidth;
    private final int inactiveItemMinWidth;
    private Drawable itemBackground;
    private int itemBackgroundRes;
    private final int itemHeight;
    private boolean itemHorizontalTranslationEnabled;
    @Dimension
    private int itemIconSize;
    private ColorStateList itemIconTint;
    private final Pools.Pool<BottomNavigationItemView> itemPool;
    @StyleRes
    private int itemTextAppearanceActive;
    @StyleRes
    private int itemTextAppearanceInactive;
    private final ColorStateList itemTextColorDefault;
    private ColorStateList itemTextColorFromUser;
    private int labelVisibilityMode;
    private MenuBuilder menu;
    private final View.OnClickListener onClickListener;
    private BottomNavigationPresenter presenter;
    private int selectedItemId;
    private int selectedItemPosition;
    private final TransitionSet set;
    private int[] tempChildWidths;

    public BottomNavigationMenuView(Context context) {
        this(context, null);
    }

    public BottomNavigationMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.itemPool = new Pools.SynchronizedPool(5);
        this.selectedItemId = 0;
        this.selectedItemPosition = 0;
        Resources res = getResources();
        this.inactiveItemMaxWidth = res.getDimensionPixelSize(C0091R.dimen.design_bottom_navigation_item_max_width);
        this.inactiveItemMinWidth = res.getDimensionPixelSize(C0091R.dimen.design_bottom_navigation_item_min_width);
        this.activeItemMaxWidth = res.getDimensionPixelSize(C0091R.dimen.design_bottom_navigation_active_item_max_width);
        this.activeItemMinWidth = res.getDimensionPixelSize(C0091R.dimen.design_bottom_navigation_active_item_min_width);
        this.itemHeight = res.getDimensionPixelSize(C0091R.dimen.design_bottom_navigation_height);
        this.itemTextColorDefault = createDefaultColorStateList(16842808);
        this.set = new AutoTransition();
        this.set.setOrdering(0);
        this.set.mo141setDuration(ACTIVE_ANIMATION_DURATION_MS);
        this.set.mo142setInterpolator((TimeInterpolator) new FastOutSlowInInterpolator());
        this.set.addTransition(new TextScale());
        this.onClickListener = new View.OnClickListener() { // from class: android.support.design.internal.BottomNavigationMenuView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) v;
                MenuItem item = itemView.getItemData();
                if (!BottomNavigationMenuView.this.menu.performItemAction(item, BottomNavigationMenuView.this.presenter, 0)) {
                    item.setChecked(true);
                }
            }
        };
        this.tempChildWidths = new int[5];
    }

    @Override // android.support.p003v7.view.menu.MenuView
    public void initialize(MenuBuilder menu) {
        this.menu = menu;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int visibleCount = this.menu.getVisibleItems().size();
        int totalCount = getChildCount();
        int heightSpec = View.MeasureSpec.makeMeasureSpec(this.itemHeight, MemoryConstants.f216GB);
        int i = 8;
        if (isShifting(this.labelVisibilityMode, visibleCount) && this.itemHorizontalTranslationEnabled) {
            View activeChild = getChildAt(this.selectedItemPosition);
            int activeItemWidth = this.activeItemMinWidth;
            if (activeChild.getVisibility() != 8) {
                activeChild.measure(View.MeasureSpec.makeMeasureSpec(this.activeItemMaxWidth, Integer.MIN_VALUE), heightSpec);
                activeItemWidth = Math.max(activeItemWidth, activeChild.getMeasuredWidth());
            }
            int inactiveCount = visibleCount - (activeChild.getVisibility() != 8 ? 1 : 0);
            int activeMaxAvailable = width - (this.inactiveItemMinWidth * inactiveCount);
            int activeWidth = Math.min(activeMaxAvailable, Math.min(activeItemWidth, this.activeItemMaxWidth));
            int inactiveMaxAvailable = (width - activeWidth) / (inactiveCount == 0 ? 1 : inactiveCount);
            int inactiveWidth = Math.min(inactiveMaxAvailable, this.inactiveItemMaxWidth);
            int extra = (width - activeWidth) - (inactiveWidth * inactiveCount);
            int i2 = 0;
            while (i2 < totalCount) {
                if (getChildAt(i2).getVisibility() != i) {
                    this.tempChildWidths[i2] = i2 == this.selectedItemPosition ? activeWidth : inactiveWidth;
                    if (extra > 0) {
                        int[] iArr = this.tempChildWidths;
                        iArr[i2] = iArr[i2] + 1;
                        extra--;
                    }
                } else {
                    this.tempChildWidths[i2] = 0;
                }
                i2++;
                i = 8;
            }
        } else {
            int maxAvailable = width / (visibleCount == 0 ? 1 : visibleCount);
            int childWidth = Math.min(maxAvailable, this.activeItemMaxWidth);
            int extra2 = width - (childWidth * visibleCount);
            for (int i3 = 0; i3 < totalCount; i3++) {
                if (getChildAt(i3).getVisibility() != 8) {
                    int[] iArr2 = this.tempChildWidths;
                    iArr2[i3] = childWidth;
                    if (extra2 > 0) {
                        iArr2[i3] = iArr2[i3] + 1;
                        extra2--;
                    }
                } else {
                    this.tempChildWidths[i3] = 0;
                }
            }
        }
        int totalWidth = 0;
        for (int i4 = 0; i4 < totalCount; i4++) {
            View child = getChildAt(i4);
            if (child.getVisibility() != 8) {
                child.measure(View.MeasureSpec.makeMeasureSpec(this.tempChildWidths[i4], MemoryConstants.f216GB), heightSpec);
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.width = child.getMeasuredWidth();
                totalWidth += child.getMeasuredWidth();
            }
        }
        setMeasuredDimension(View.resolveSizeAndState(totalWidth, View.MeasureSpec.makeMeasureSpec(totalWidth, MemoryConstants.f216GB), 0), View.resolveSizeAndState(this.itemHeight, heightSpec, 0));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        int width = right - left;
        int height = bottom - top;
        int used = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                if (ViewCompat.getLayoutDirection(this) == 1) {
                    child.layout((width - used) - child.getMeasuredWidth(), 0, width - used, height);
                } else {
                    child.layout(used, 0, child.getMeasuredWidth() + used, height);
                }
                used += child.getMeasuredWidth();
            }
        }
    }

    @Override // android.support.p003v7.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    public void setIconTintList(ColorStateList tint) {
        this.itemIconTint = tint;
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null) {
            for (BottomNavigationItemView item : bottomNavigationItemViewArr) {
                item.setIconTintList(tint);
            }
        }
    }

    @Nullable
    public ColorStateList getIconTintList() {
        return this.itemIconTint;
    }

    public void setItemIconSize(@Dimension int iconSize) {
        this.itemIconSize = iconSize;
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null) {
            for (BottomNavigationItemView item : bottomNavigationItemViewArr) {
                item.setIconSize(iconSize);
            }
        }
    }

    @Dimension
    public int getItemIconSize() {
        return this.itemIconSize;
    }

    public void setItemTextColor(ColorStateList color) {
        this.itemTextColorFromUser = color;
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null) {
            for (BottomNavigationItemView item : bottomNavigationItemViewArr) {
                item.setTextColor(color);
            }
        }
    }

    public ColorStateList getItemTextColor() {
        return this.itemTextColorFromUser;
    }

    public void setItemTextAppearanceInactive(@StyleRes int textAppearanceRes) {
        this.itemTextAppearanceInactive = textAppearanceRes;
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null) {
            for (BottomNavigationItemView item : bottomNavigationItemViewArr) {
                item.setTextAppearanceInactive(textAppearanceRes);
                ColorStateList colorStateList = this.itemTextColorFromUser;
                if (colorStateList != null) {
                    item.setTextColor(colorStateList);
                }
            }
        }
    }

    @StyleRes
    public int getItemTextAppearanceInactive() {
        return this.itemTextAppearanceInactive;
    }

    public void setItemTextAppearanceActive(@StyleRes int textAppearanceRes) {
        this.itemTextAppearanceActive = textAppearanceRes;
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null) {
            for (BottomNavigationItemView item : bottomNavigationItemViewArr) {
                item.setTextAppearanceActive(textAppearanceRes);
                ColorStateList colorStateList = this.itemTextColorFromUser;
                if (colorStateList != null) {
                    item.setTextColor(colorStateList);
                }
            }
        }
    }

    @StyleRes
    public int getItemTextAppearanceActive() {
        return this.itemTextAppearanceActive;
    }

    public void setItemBackgroundRes(int background) {
        this.itemBackgroundRes = background;
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null) {
            for (BottomNavigationItemView item : bottomNavigationItemViewArr) {
                item.setItemBackground(background);
            }
        }
    }

    @Deprecated
    public int getItemBackgroundRes() {
        return this.itemBackgroundRes;
    }

    public void setItemBackground(@Nullable Drawable background) {
        this.itemBackground = background;
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null) {
            for (BottomNavigationItemView item : bottomNavigationItemViewArr) {
                item.setItemBackground(background);
            }
        }
    }

    @Nullable
    public Drawable getItemBackground() {
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null && bottomNavigationItemViewArr.length > 0) {
            return bottomNavigationItemViewArr[0].getBackground();
        }
        return this.itemBackground;
    }

    public void setLabelVisibilityMode(int labelVisibilityMode) {
        this.labelVisibilityMode = labelVisibilityMode;
    }

    public int getLabelVisibilityMode() {
        return this.labelVisibilityMode;
    }

    public void setItemHorizontalTranslationEnabled(boolean itemHorizontalTranslationEnabled) {
        this.itemHorizontalTranslationEnabled = itemHorizontalTranslationEnabled;
    }

    public boolean isItemHorizontalTranslationEnabled() {
        return this.itemHorizontalTranslationEnabled;
    }

    public ColorStateList createDefaultColorStateList(int baseColorThemeAttr) {
        TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(baseColorThemeAttr, value, true)) {
            return null;
        }
        ColorStateList baseColor = AppCompatResources.getColorStateList(getContext(), value.resourceId);
        if (!getContext().getTheme().resolveAttribute(C0452R.attr.colorPrimary, value, true)) {
            return null;
        }
        int colorPrimary = value.data;
        int defaultColor = baseColor.getDefaultColor();
        return new ColorStateList(new int[][]{DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET}, new int[]{baseColor.getColorForState(DISABLED_STATE_SET, defaultColor), colorPrimary, defaultColor});
    }

    public void setPresenter(BottomNavigationPresenter presenter) {
        this.presenter = presenter;
    }

    public void buildMenuView() {
        removeAllViews();
        BottomNavigationItemView[] bottomNavigationItemViewArr = this.buttons;
        if (bottomNavigationItemViewArr != null) {
            for (BottomNavigationItemView item : bottomNavigationItemViewArr) {
                if (item != null) {
                    this.itemPool.release(item);
                }
            }
        }
        if (this.menu.size() == 0) {
            this.selectedItemId = 0;
            this.selectedItemPosition = 0;
            this.buttons = null;
            return;
        }
        this.buttons = new BottomNavigationItemView[this.menu.size()];
        boolean shifting = isShifting(this.labelVisibilityMode, this.menu.getVisibleItems().size());
        for (int i = 0; i < this.menu.size(); i++) {
            this.presenter.setUpdateSuspended(true);
            this.menu.getItem(i).setCheckable(true);
            this.presenter.setUpdateSuspended(false);
            BottomNavigationItemView child = getNewItem();
            this.buttons[i] = child;
            child.setIconTintList(this.itemIconTint);
            child.setIconSize(this.itemIconSize);
            child.setTextColor(this.itemTextColorDefault);
            child.setTextAppearanceInactive(this.itemTextAppearanceInactive);
            child.setTextAppearanceActive(this.itemTextAppearanceActive);
            child.setTextColor(this.itemTextColorFromUser);
            Drawable drawable = this.itemBackground;
            if (drawable != null) {
                child.setItemBackground(drawable);
            } else {
                child.setItemBackground(this.itemBackgroundRes);
            }
            child.setShifting(shifting);
            child.setLabelVisibilityMode(this.labelVisibilityMode);
            child.initialize((MenuItemImpl) this.menu.getItem(i), 0);
            child.setItemPosition(i);
            child.setOnClickListener(this.onClickListener);
            addView(child);
        }
        this.selectedItemPosition = Math.min(this.menu.size() - 1, this.selectedItemPosition);
        this.menu.getItem(this.selectedItemPosition).setChecked(true);
    }

    public void updateMenuView() {
        MenuBuilder menuBuilder = this.menu;
        if (menuBuilder == null || this.buttons == null) {
            return;
        }
        int menuSize = menuBuilder.size();
        if (menuSize != this.buttons.length) {
            buildMenuView();
            return;
        }
        int previousSelectedId = this.selectedItemId;
        for (int i = 0; i < menuSize; i++) {
            MenuItem item = this.menu.getItem(i);
            if (item.isChecked()) {
                this.selectedItemId = item.getItemId();
                this.selectedItemPosition = i;
            }
        }
        int i2 = this.selectedItemId;
        if (previousSelectedId != i2) {
            TransitionManager.beginDelayedTransition(this, this.set);
        }
        boolean shifting = isShifting(this.labelVisibilityMode, this.menu.getVisibleItems().size());
        for (int i3 = 0; i3 < menuSize; i3++) {
            this.presenter.setUpdateSuspended(true);
            this.buttons[i3].setLabelVisibilityMode(this.labelVisibilityMode);
            this.buttons[i3].setShifting(shifting);
            this.buttons[i3].initialize((MenuItemImpl) this.menu.getItem(i3), 0);
            this.presenter.setUpdateSuspended(false);
        }
    }

    private BottomNavigationItemView getNewItem() {
        BottomNavigationItemView item = this.itemPool.acquire();
        if (item == null) {
            return new BottomNavigationItemView(getContext());
        }
        return item;
    }

    public int getSelectedItemId() {
        return this.selectedItemId;
    }

    private boolean isShifting(int labelVisibilityMode, int childCount) {
        if (labelVisibilityMode == -1) {
            if (childCount > 3) {
                return true;
            }
        } else if (labelVisibilityMode == 0) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tryRestoreSelectedItemId(int itemId) {
        int size = this.menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = this.menu.getItem(i);
            if (itemId == item.getItemId()) {
                this.selectedItemId = itemId;
                this.selectedItemPosition = i;
                item.setChecked(true);
                return;
            }
        }
    }
}