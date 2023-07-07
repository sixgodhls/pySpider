package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.design.R;
import android.support.design.animation.AnimationUtils;
import android.support.design.internal.ThemeEnforcement;
import android.support.design.internal.ViewUtils;
import android.support.design.resources.MaterialResources;
import android.support.design.ripple.RippleUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pools;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.TooltipCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;

@ViewPager.DecorView
/* loaded from: classes.dex */
public class TabLayout extends HorizontalScrollView {
    private static final int ANIMATION_DURATION = 300;
    @Dimension(unit = 0)
    static final int DEFAULT_GAP_TEXT_ICON = 8;
    @Dimension(unit = 0)
    private static final int DEFAULT_HEIGHT = 48;
    @Dimension(unit = 0)
    private static final int DEFAULT_HEIGHT_WITH_TEXT_ICON = 72;
    @Dimension(unit = 0)
    static final int FIXED_WRAP_GUTTER_MIN = 16;
    public static final int GRAVITY_CENTER = 1;
    public static final int GRAVITY_FILL = 0;
    public static final int INDICATOR_GRAVITY_BOTTOM = 0;
    public static final int INDICATOR_GRAVITY_CENTER = 1;
    public static final int INDICATOR_GRAVITY_STRETCH = 3;
    public static final int INDICATOR_GRAVITY_TOP = 2;
    private static final int INVALID_WIDTH = -1;
    @Dimension(unit = 0)
    private static final int MIN_INDICATOR_WIDTH = 24;
    public static final int MODE_FIXED = 1;
    public static final int MODE_SCROLLABLE = 0;
    @Dimension(unit = 0)
    private static final int TAB_MIN_WIDTH_MARGIN = 56;
    private static final Pools.Pool<Tab> tabPool = new Pools.SynchronizedPool(16);
    private AdapterChangeListener adapterChangeListener;
    private int contentInsetStart;
    private BaseOnTabSelectedListener currentVpSelectedListener;
    boolean inlineLabel;
    int mode;
    private TabLayoutOnPageChangeListener pageChangeListener;
    private PagerAdapter pagerAdapter;
    private DataSetObserver pagerAdapterObserver;
    private final int requestedTabMaxWidth;
    private final int requestedTabMinWidth;
    private ValueAnimator scrollAnimator;
    private final int scrollableTabMinWidth;
    private BaseOnTabSelectedListener selectedListener;
    private final ArrayList<BaseOnTabSelectedListener> selectedListeners;
    private Tab selectedTab;
    private boolean setupViewPagerImplicitly;
    private final SlidingTabIndicator slidingTabIndicator;
    final int tabBackgroundResId;
    int tabGravity;
    ColorStateList tabIconTint;
    PorterDuff.Mode tabIconTintMode;
    int tabIndicatorAnimationDuration;
    boolean tabIndicatorFullWidth;
    int tabIndicatorGravity;
    int tabMaxWidth;
    int tabPaddingBottom;
    int tabPaddingEnd;
    int tabPaddingStart;
    int tabPaddingTop;
    ColorStateList tabRippleColorStateList;
    @Nullable
    Drawable tabSelectedIndicator;
    int tabTextAppearance;
    ColorStateList tabTextColors;
    float tabTextMultiLineSize;
    float tabTextSize;
    private final RectF tabViewContentBounds;
    private final Pools.Pool<TabView> tabViewPool;
    private final ArrayList<Tab> tabs;
    boolean unboundedRipple;
    ViewPager viewPager;

    /* loaded from: classes.dex */
    public interface BaseOnTabSelectedListener<T extends Tab> {
        void onTabReselected(T t);

        void onTabSelected(T t);

        void onTabUnselected(T t);
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public @interface Mode {
    }

    /* loaded from: classes.dex */
    public interface OnTabSelectedListener extends BaseOnTabSelectedListener<Tab> {
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public @interface TabGravity {
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public @interface TabIndicatorGravity {
    }

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.tabStyle);
    }

    /* JADX WARN: Finally extract failed */
    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.tabs = new ArrayList<>();
        this.tabViewContentBounds = new RectF();
        this.tabMaxWidth = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.selectedListeners = new ArrayList<>();
        this.tabViewPool = new Pools.SimplePool(12);
        setHorizontalScrollBarEnabled(false);
        this.slidingTabIndicator = new SlidingTabIndicator(context);
        super.addView(this.slidingTabIndicator, 0, new FrameLayout.LayoutParams(-2, -1));
        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context, attrs, R.styleable.TabLayout, defStyleAttr, R.style.Widget_Design_TabLayout, R.styleable.TabLayout_tabTextAppearance);
        this.slidingTabIndicator.setSelectedIndicatorHeight(a.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorHeight, -1));
        this.slidingTabIndicator.setSelectedIndicatorColor(a.getColor(R.styleable.TabLayout_tabIndicatorColor, 0));
        setSelectedTabIndicator(MaterialResources.getDrawable(context, a, R.styleable.TabLayout_tabIndicator));
        setSelectedTabIndicatorGravity(a.getInt(R.styleable.TabLayout_tabIndicatorGravity, 0));
        setTabIndicatorFullWidth(a.getBoolean(R.styleable.TabLayout_tabIndicatorFullWidth, true));
        int dimensionPixelSize = a.getDimensionPixelSize(R.styleable.TabLayout_tabPadding, 0);
        this.tabPaddingBottom = dimensionPixelSize;
        this.tabPaddingEnd = dimensionPixelSize;
        this.tabPaddingTop = dimensionPixelSize;
        this.tabPaddingStart = dimensionPixelSize;
        this.tabPaddingStart = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingStart, this.tabPaddingStart);
        this.tabPaddingTop = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingTop, this.tabPaddingTop);
        this.tabPaddingEnd = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingEnd, this.tabPaddingEnd);
        this.tabPaddingBottom = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingBottom, this.tabPaddingBottom);
        this.tabTextAppearance = a.getResourceId(R.styleable.TabLayout_tabTextAppearance, R.style.TextAppearance_Design_Tab);
        TypedArray ta = context.obtainStyledAttributes(this.tabTextAppearance, android.support.v7.appcompat.R.styleable.TextAppearance);
        try {
            this.tabTextSize = ta.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.TextAppearance_android_textSize, 0);
            this.tabTextColors = MaterialResources.getColorStateList(context, ta, android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor);
            ta.recycle();
            if (a.hasValue(R.styleable.TabLayout_tabTextColor)) {
                this.tabTextColors = MaterialResources.getColorStateList(context, a, R.styleable.TabLayout_tabTextColor);
            }
            if (a.hasValue(R.styleable.TabLayout_tabSelectedTextColor)) {
                int selected = a.getColor(R.styleable.TabLayout_tabSelectedTextColor, 0);
                this.tabTextColors = createColorStateList(this.tabTextColors.getDefaultColor(), selected);
            }
            int selected2 = R.styleable.TabLayout_tabIconTint;
            this.tabIconTint = MaterialResources.getColorStateList(context, a, selected2);
            this.tabIconTintMode = ViewUtils.parseTintMode(a.getInt(R.styleable.TabLayout_tabIconTintMode, -1), null);
            this.tabRippleColorStateList = MaterialResources.getColorStateList(context, a, R.styleable.TabLayout_tabRippleColor);
            this.tabIndicatorAnimationDuration = a.getInt(R.styleable.TabLayout_tabIndicatorAnimationDuration, ANIMATION_DURATION);
            this.requestedTabMinWidth = a.getDimensionPixelSize(R.styleable.TabLayout_tabMinWidth, -1);
            this.requestedTabMaxWidth = a.getDimensionPixelSize(R.styleable.TabLayout_tabMaxWidth, -1);
            this.tabBackgroundResId = a.getResourceId(R.styleable.TabLayout_tabBackground, 0);
            this.contentInsetStart = a.getDimensionPixelSize(R.styleable.TabLayout_tabContentStart, 0);
            this.mode = a.getInt(R.styleable.TabLayout_tabMode, 1);
            this.tabGravity = a.getInt(R.styleable.TabLayout_tabGravity, 0);
            this.inlineLabel = a.getBoolean(R.styleable.TabLayout_tabInlineLabel, false);
            this.unboundedRipple = a.getBoolean(R.styleable.TabLayout_tabUnboundedRipple, false);
            a.recycle();
            Resources res = getResources();
            this.tabTextMultiLineSize = res.getDimensionPixelSize(R.dimen.design_tab_text_size_2line);
            this.scrollableTabMinWidth = res.getDimensionPixelSize(R.dimen.design_tab_scrollable_min_width);
            applyModeAndGravity();
        } catch (Throwable th) {
            ta.recycle();
            throw th;
        }
    }

    public void setSelectedTabIndicatorColor(@ColorInt int color) {
        this.slidingTabIndicator.setSelectedIndicatorColor(color);
    }

    @Deprecated
    public void setSelectedTabIndicatorHeight(int height) {
        this.slidingTabIndicator.setSelectedIndicatorHeight(height);
    }

    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        setScrollPosition(position, positionOffset, updateSelectedText, true);
    }

    void setScrollPosition(int position, float positionOffset, boolean updateSelectedText, boolean updateIndicatorPosition) {
        int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= this.slidingTabIndicator.getChildCount()) {
            return;
        }
        if (updateIndicatorPosition) {
            this.slidingTabIndicator.setIndicatorPositionFromTabPosition(position, positionOffset);
        }
        ValueAnimator valueAnimator = this.scrollAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.scrollAnimator.cancel();
        }
        scrollTo(calculateScrollXForTab(position, positionOffset), 0);
        if (updateSelectedText) {
            setSelectedTabView(roundedPosition);
        }
    }

    public void addTab(@NonNull Tab tab) {
        addTab(tab, this.tabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, int position) {
        addTab(tab, position, this.tabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, boolean setSelected) {
        addTab(tab, this.tabs.size(), setSelected);
    }

    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        if (tab.parent != this) {
            throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
        }
        configureTab(tab, position);
        addTabView(tab);
        if (setSelected) {
            tab.select();
        }
    }

    private void addTabFromItemView(@NonNull TabItem item) {
        Tab tab = newTab();
        if (item.text != null) {
            tab.setText(item.text);
        }
        if (item.icon != null) {
            tab.setIcon(item.icon);
        }
        if (item.customLayout != 0) {
            tab.setCustomView(item.customLayout);
        }
        if (!TextUtils.isEmpty(item.getContentDescription())) {
            tab.setContentDescription(item.getContentDescription());
        }
        addTab(tab);
    }

    @Deprecated
    public void setOnTabSelectedListener(@Nullable BaseOnTabSelectedListener listener) {
        BaseOnTabSelectedListener baseOnTabSelectedListener = this.selectedListener;
        if (baseOnTabSelectedListener != null) {
            removeOnTabSelectedListener(baseOnTabSelectedListener);
        }
        this.selectedListener = listener;
        if (listener != null) {
            addOnTabSelectedListener(listener);
        }
    }

    public void addOnTabSelectedListener(@NonNull BaseOnTabSelectedListener listener) {
        if (!this.selectedListeners.contains(listener)) {
            this.selectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(@NonNull BaseOnTabSelectedListener listener) {
        this.selectedListeners.remove(listener);
    }

    public void clearOnTabSelectedListeners() {
        this.selectedListeners.clear();
    }

    @NonNull
    public Tab newTab() {
        Tab tab = createTabFromPool();
        tab.parent = this;
        tab.view = createTabView(tab);
        return tab;
    }

    protected Tab createTabFromPool() {
        Tab tab = tabPool.acquire();
        if (tab == null) {
            return new Tab();
        }
        return tab;
    }

    protected boolean releaseFromTabPool(Tab tab) {
        return tabPool.release(tab);
    }

    public int getTabCount() {
        return this.tabs.size();
    }

    @Nullable
    public Tab getTabAt(int index) {
        if (index < 0 || index >= getTabCount()) {
            return null;
        }
        return this.tabs.get(index);
    }

    public int getSelectedTabPosition() {
        Tab tab = this.selectedTab;
        if (tab != null) {
            return tab.getPosition();
        }
        return -1;
    }

    public void removeTab(Tab tab) {
        if (tab.parent != this) {
            throw new IllegalArgumentException("Tab does not belong to this TabLayout.");
        }
        removeTabAt(tab.getPosition());
    }

    public void removeTabAt(int position) {
        Tab tab = this.selectedTab;
        int selectedTabPosition = tab != null ? tab.getPosition() : 0;
        removeTabViewAt(position);
        Tab removedTab = this.tabs.remove(position);
        if (removedTab != null) {
            removedTab.reset();
            releaseFromTabPool(removedTab);
        }
        int newTabCount = this.tabs.size();
        for (int i = position; i < newTabCount; i++) {
            this.tabs.get(i).setPosition(i);
        }
        if (selectedTabPosition == position) {
            selectTab(this.tabs.isEmpty() ? null : this.tabs.get(Math.max(0, position - 1)));
        }
    }

    public void removeAllTabs() {
        for (int i = this.slidingTabIndicator.getChildCount() - 1; i >= 0; i--) {
            removeTabViewAt(i);
        }
        Iterator<Tab> i2 = this.tabs.iterator();
        while (i2.hasNext()) {
            Tab tab = i2.next();
            i2.remove();
            tab.reset();
            releaseFromTabPool(tab);
        }
        this.selectedTab = null;
    }

    public void setTabMode(int mode) {
        if (mode != this.mode) {
            this.mode = mode;
            applyModeAndGravity();
        }
    }

    public int getTabMode() {
        return this.mode;
    }

    public void setTabGravity(int gravity) {
        if (this.tabGravity != gravity) {
            this.tabGravity = gravity;
            applyModeAndGravity();
        }
    }

    public int getTabGravity() {
        return this.tabGravity;
    }

    public void setSelectedTabIndicatorGravity(int indicatorGravity) {
        if (this.tabIndicatorGravity != indicatorGravity) {
            this.tabIndicatorGravity = indicatorGravity;
            ViewCompat.postInvalidateOnAnimation(this.slidingTabIndicator);
        }
    }

    public int getTabIndicatorGravity() {
        return this.tabIndicatorGravity;
    }

    public void setTabIndicatorFullWidth(boolean tabIndicatorFullWidth) {
        this.tabIndicatorFullWidth = tabIndicatorFullWidth;
        ViewCompat.postInvalidateOnAnimation(this.slidingTabIndicator);
    }

    public boolean isTabIndicatorFullWidth() {
        return this.tabIndicatorFullWidth;
    }

    public void setInlineLabel(boolean inline) {
        if (this.inlineLabel != inline) {
            this.inlineLabel = inline;
            for (int i = 0; i < this.slidingTabIndicator.getChildCount(); i++) {
                View child = this.slidingTabIndicator.getChildAt(i);
                if (child instanceof TabView) {
                    ((TabView) child).updateOrientation();
                }
            }
            applyModeAndGravity();
        }
    }

    public void setInlineLabelResource(@BoolRes int inlineResourceId) {
        setInlineLabel(getResources().getBoolean(inlineResourceId));
    }

    public boolean isInlineLabel() {
        return this.inlineLabel;
    }

    public void setUnboundedRipple(boolean unboundedRipple) {
        if (this.unboundedRipple != unboundedRipple) {
            this.unboundedRipple = unboundedRipple;
            for (int i = 0; i < this.slidingTabIndicator.getChildCount(); i++) {
                View child = this.slidingTabIndicator.getChildAt(i);
                if (child instanceof TabView) {
                    ((TabView) child).updateBackgroundDrawable(getContext());
                }
            }
        }
    }

    public void setUnboundedRippleResource(@BoolRes int unboundedRippleResourceId) {
        setUnboundedRipple(getResources().getBoolean(unboundedRippleResourceId));
    }

    public boolean hasUnboundedRipple() {
        return this.unboundedRipple;
    }

    public void setTabTextColors(@Nullable ColorStateList textColor) {
        if (this.tabTextColors != textColor) {
            this.tabTextColors = textColor;
            updateAllTabs();
        }
    }

    @Nullable
    public ColorStateList getTabTextColors() {
        return this.tabTextColors;
    }

    public void setTabTextColors(int normalColor, int selectedColor) {
        setTabTextColors(createColorStateList(normalColor, selectedColor));
    }

    public void setTabIconTint(@Nullable ColorStateList iconTint) {
        if (this.tabIconTint != iconTint) {
            this.tabIconTint = iconTint;
            updateAllTabs();
        }
    }

    public void setTabIconTintResource(@ColorRes int iconTintResourceId) {
        setTabIconTint(AppCompatResources.getColorStateList(getContext(), iconTintResourceId));
    }

    @Nullable
    public ColorStateList getTabIconTint() {
        return this.tabIconTint;
    }

    @Nullable
    public ColorStateList getTabRippleColor() {
        return this.tabRippleColorStateList;
    }

    public void setTabRippleColor(@Nullable ColorStateList color) {
        if (this.tabRippleColorStateList != color) {
            this.tabRippleColorStateList = color;
            for (int i = 0; i < this.slidingTabIndicator.getChildCount(); i++) {
                View child = this.slidingTabIndicator.getChildAt(i);
                if (child instanceof TabView) {
                    ((TabView) child).updateBackgroundDrawable(getContext());
                }
            }
        }
    }

    public void setTabRippleColorResource(@ColorRes int tabRippleColorResourceId) {
        setTabRippleColor(AppCompatResources.getColorStateList(getContext(), tabRippleColorResourceId));
    }

    @Nullable
    public Drawable getTabSelectedIndicator() {
        return this.tabSelectedIndicator;
    }

    public void setSelectedTabIndicator(@Nullable Drawable tabSelectedIndicator) {
        if (this.tabSelectedIndicator != tabSelectedIndicator) {
            this.tabSelectedIndicator = tabSelectedIndicator;
            ViewCompat.postInvalidateOnAnimation(this.slidingTabIndicator);
        }
    }

    public void setSelectedTabIndicator(@DrawableRes int tabSelectedIndicatorResourceId) {
        if (tabSelectedIndicatorResourceId != 0) {
            setSelectedTabIndicator(AppCompatResources.getDrawable(getContext(), tabSelectedIndicatorResourceId));
        } else {
            setSelectedTabIndicator((Drawable) null);
        }
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        setupWithViewPager(viewPager, true);
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        setupWithViewPager(viewPager, autoRefresh, false);
    }

    private void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh, boolean implicitSetup) {
        ViewPager viewPager2 = this.viewPager;
        if (viewPager2 != null) {
            TabLayoutOnPageChangeListener tabLayoutOnPageChangeListener = this.pageChangeListener;
            if (tabLayoutOnPageChangeListener != null) {
                viewPager2.removeOnPageChangeListener(tabLayoutOnPageChangeListener);
            }
            AdapterChangeListener adapterChangeListener = this.adapterChangeListener;
            if (adapterChangeListener != null) {
                this.viewPager.removeOnAdapterChangeListener(adapterChangeListener);
            }
        }
        BaseOnTabSelectedListener baseOnTabSelectedListener = this.currentVpSelectedListener;
        if (baseOnTabSelectedListener != null) {
            removeOnTabSelectedListener(baseOnTabSelectedListener);
            this.currentVpSelectedListener = null;
        }
        if (viewPager != null) {
            this.viewPager = viewPager;
            if (this.pageChangeListener == null) {
                this.pageChangeListener = new TabLayoutOnPageChangeListener(this);
            }
            this.pageChangeListener.reset();
            viewPager.addOnPageChangeListener(this.pageChangeListener);
            this.currentVpSelectedListener = new ViewPagerOnTabSelectedListener(viewPager);
            addOnTabSelectedListener(this.currentVpSelectedListener);
            PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                setPagerAdapter(adapter, autoRefresh);
            }
            if (this.adapterChangeListener == null) {
                this.adapterChangeListener = new AdapterChangeListener();
            }
            this.adapterChangeListener.setAutoRefresh(autoRefresh);
            viewPager.addOnAdapterChangeListener(this.adapterChangeListener);
            setScrollPosition(viewPager.getCurrentItem(), 0.0f, true);
        } else {
            this.viewPager = null;
            setPagerAdapter(null, false);
        }
        this.setupViewPagerImplicitly = implicitSetup;
    }

    @Deprecated
    public void setTabsFromPagerAdapter(@Nullable PagerAdapter adapter) {
        setPagerAdapter(adapter, false);
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return getTabScrollRange() > 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.viewPager == null) {
            ViewParent vp = getParent();
            if (vp instanceof ViewPager) {
                setupWithViewPager((ViewPager) vp, true, true);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.setupViewPagerImplicitly) {
            setupWithViewPager(null);
            this.setupViewPagerImplicitly = false;
        }
    }

    private int getTabScrollRange() {
        return Math.max(0, ((this.slidingTabIndicator.getWidth() - getWidth()) - getPaddingLeft()) - getPaddingRight());
    }

    void setPagerAdapter(@Nullable PagerAdapter adapter, boolean addObserver) {
        DataSetObserver dataSetObserver;
        PagerAdapter pagerAdapter = this.pagerAdapter;
        if (pagerAdapter != null && (dataSetObserver = this.pagerAdapterObserver) != null) {
            pagerAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.pagerAdapter = adapter;
        if (addObserver && adapter != null) {
            if (this.pagerAdapterObserver == null) {
                this.pagerAdapterObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(this.pagerAdapterObserver);
        }
        populateFromPagerAdapter();
    }

    void populateFromPagerAdapter() {
        int curItem;
        removeAllTabs();
        PagerAdapter pagerAdapter = this.pagerAdapter;
        if (pagerAdapter != null) {
            int adapterCount = pagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                addTab(newTab().setText(this.pagerAdapter.getPageTitle(i)), false);
            }
            ViewPager viewPager = this.viewPager;
            if (viewPager != null && adapterCount > 0 && (curItem = viewPager.getCurrentItem()) != getSelectedTabPosition() && curItem < getTabCount()) {
                selectTab(getTabAt(curItem));
            }
        }
    }

    private void updateAllTabs() {
        int z = this.tabs.size();
        for (int i = 0; i < z; i++) {
            this.tabs.get(i).updateView();
        }
    }

    private TabView createTabView(@NonNull Tab tab) {
        Pools.Pool<TabView> pool = this.tabViewPool;
        TabView tabView = pool != null ? pool.acquire() : null;
        if (tabView == null) {
            tabView = new TabView(getContext());
        }
        tabView.setTab(tab);
        tabView.setFocusable(true);
        tabView.setMinimumWidth(getTabMinWidth());
        if (TextUtils.isEmpty(tab.contentDesc)) {
            tabView.setContentDescription(tab.text);
        } else {
            tabView.setContentDescription(tab.contentDesc);
        }
        return tabView;
    }

    private void configureTab(Tab tab, int position) {
        tab.setPosition(position);
        this.tabs.add(position, tab);
        int count = this.tabs.size();
        for (int i = position + 1; i < count; i++) {
            this.tabs.get(i).setPosition(i);
        }
    }

    private void addTabView(Tab tab) {
        TabView tabView = tab.view;
        this.slidingTabIndicator.addView(tabView, tab.getPosition(), createLayoutParamsForTabs());
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public void addView(View child) {
        addViewInternal(child);
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public void addView(View child, int index) {
        addViewInternal(child);
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup, android.view.ViewManager
    public void addView(View child, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    private void addViewInternal(View child) {
        if (child instanceof TabItem) {
            addTabFromItemView((TabItem) child);
            return;
        }
        throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -1);
        updateTabViewLayoutParams(lp);
        return lp;
    }

    private void updateTabViewLayoutParams(LinearLayout.LayoutParams lp) {
        if (this.mode == 1 && this.tabGravity == 0) {
            lp.width = 0;
            lp.weight = 1.0f;
            return;
        }
        lp.width = -2;
        lp.weight = 0.0f;
    }

    @Dimension(unit = 1)
    int dpToPx(@Dimension(unit = 0) int dps) {
        return Math.round(getResources().getDisplayMetrics().density * dps);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < this.slidingTabIndicator.getChildCount(); i++) {
            View tabView = this.slidingTabIndicator.getChildAt(i);
            if (tabView instanceof TabView) {
                ((TabView) tabView).drawBackground(canvas);
            }
        }
        super.onDraw(canvas);
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int idealHeight = dpToPx(getDefaultHeight()) + getPaddingTop() + getPaddingBottom();
        int mode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (mode == Integer.MIN_VALUE) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.min(idealHeight, View.MeasureSpec.getSize(heightMeasureSpec)), MemoryConstants.GB);
        } else if (mode == 0) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(idealHeight, MemoryConstants.GB);
        }
        int specWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        if (View.MeasureSpec.getMode(widthMeasureSpec) != 0) {
            int i = this.requestedTabMaxWidth;
            if (i <= 0) {
                i = specWidth - dpToPx(56);
            }
            this.tabMaxWidth = i;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 1) {
            boolean z = false;
            View child = getChildAt(0);
            boolean remeasure = false;
            switch (this.mode) {
                case 0:
                    if (child.getMeasuredWidth() < getMeasuredWidth()) {
                        z = true;
                    }
                    remeasure = z;
                    break;
                case 1:
                    if (child.getMeasuredWidth() != getMeasuredWidth()) {
                        z = true;
                    }
                    remeasure = z;
                    break;
            }
            if (remeasure) {
                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), child.getLayoutParams().height);
                int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MemoryConstants.GB);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    private void removeTabViewAt(int position) {
        TabView view = (TabView) this.slidingTabIndicator.getChildAt(position);
        this.slidingTabIndicator.removeViewAt(position);
        if (view != null) {
            view.reset();
            this.tabViewPool.release(view);
        }
        requestLayout();
    }

    private void animateToTab(int newPosition) {
        if (newPosition == -1) {
            return;
        }
        if (getWindowToken() == null || !ViewCompat.isLaidOut(this) || this.slidingTabIndicator.childrenNeedLayout()) {
            setScrollPosition(newPosition, 0.0f, true);
            return;
        }
        int startScrollX = getScrollX();
        int targetScrollX = calculateScrollXForTab(newPosition, 0.0f);
        if (startScrollX != targetScrollX) {
            ensureScrollAnimator();
            this.scrollAnimator.setIntValues(startScrollX, targetScrollX);
            this.scrollAnimator.start();
        }
        this.slidingTabIndicator.animateIndicatorToPosition(newPosition, this.tabIndicatorAnimationDuration);
    }

    private void ensureScrollAnimator() {
        if (this.scrollAnimator == null) {
            this.scrollAnimator = new ValueAnimator();
            this.scrollAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            this.scrollAnimator.setDuration(this.tabIndicatorAnimationDuration);
            this.scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.support.design.widget.TabLayout.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animator) {
                    TabLayout.this.scrollTo(((Integer) animator.getAnimatedValue()).intValue(), 0);
                }
            });
        }
    }

    void setScrollAnimatorListener(Animator.AnimatorListener listener) {
        ensureScrollAnimator();
        this.scrollAnimator.addListener(listener);
    }

    private void setSelectedTabView(int position) {
        int tabCount = this.slidingTabIndicator.getChildCount();
        if (position < tabCount) {
            int i = 0;
            while (i < tabCount) {
                View child = this.slidingTabIndicator.getChildAt(i);
                boolean z = false;
                child.setSelected(i == position);
                if (i == position) {
                    z = true;
                }
                child.setActivated(z);
                i++;
            }
        }
    }

    void selectTab(Tab tab) {
        selectTab(tab, true);
    }

    void selectTab(Tab tab, boolean updateIndicator) {
        Tab currentTab = this.selectedTab;
        if (currentTab == tab) {
            if (currentTab != null) {
                dispatchTabReselected(tab);
                animateToTab(tab.getPosition());
                return;
            }
            return;
        }
        int newPosition = tab != null ? tab.getPosition() : -1;
        if (updateIndicator) {
            if ((currentTab == null || currentTab.getPosition() == -1) && newPosition != -1) {
                setScrollPosition(newPosition, 0.0f, true);
            } else {
                animateToTab(newPosition);
            }
            if (newPosition != -1) {
                setSelectedTabView(newPosition);
            }
        }
        this.selectedTab = tab;
        if (currentTab != null) {
            dispatchTabUnselected(currentTab);
        }
        if (tab != null) {
            dispatchTabSelected(tab);
        }
    }

    private void dispatchTabSelected(@NonNull Tab tab) {
        for (int i = this.selectedListeners.size() - 1; i >= 0; i--) {
            this.selectedListeners.get(i).onTabSelected(tab);
        }
    }

    private void dispatchTabUnselected(@NonNull Tab tab) {
        for (int i = this.selectedListeners.size() - 1; i >= 0; i--) {
            this.selectedListeners.get(i).onTabUnselected(tab);
        }
    }

    private void dispatchTabReselected(@NonNull Tab tab) {
        for (int i = this.selectedListeners.size() - 1; i >= 0; i--) {
            this.selectedListeners.get(i).onTabReselected(tab);
        }
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        int nextWidth = 0;
        if (this.mode == 0) {
            View selectedChild = this.slidingTabIndicator.getChildAt(position);
            View nextChild = position + 1 < this.slidingTabIndicator.getChildCount() ? this.slidingTabIndicator.getChildAt(position + 1) : null;
            int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
            if (nextChild != null) {
                nextWidth = nextChild.getWidth();
            }
            int scrollBase = (selectedChild.getLeft() + (selectedWidth / 2)) - (getWidth() / 2);
            int scrollOffset = (int) ((selectedWidth + nextWidth) * 0.5f * positionOffset);
            return ViewCompat.getLayoutDirection(this) == 0 ? scrollBase + scrollOffset : scrollBase - scrollOffset;
        }
        return 0;
    }

    private void applyModeAndGravity() {
        int paddingStart = 0;
        if (this.mode == 0) {
            paddingStart = Math.max(0, this.contentInsetStart - this.tabPaddingStart);
        }
        ViewCompat.setPaddingRelative(this.slidingTabIndicator, paddingStart, 0, 0, 0);
        switch (this.mode) {
            case 0:
                this.slidingTabIndicator.setGravity(GravityCompat.START);
                break;
            case 1:
                this.slidingTabIndicator.setGravity(1);
                break;
        }
        updateTabViews(true);
    }

    void updateTabViews(boolean requestLayout) {
        for (int i = 0; i < this.slidingTabIndicator.getChildCount(); i++) {
            View child = this.slidingTabIndicator.getChildAt(i);
            child.setMinimumWidth(getTabMinWidth());
            updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams());
            if (requestLayout) {
                child.requestLayout();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Tab {
        public static final int INVALID_POSITION = -1;
        private CharSequence contentDesc;
        private View customView;
        private Drawable icon;
        public TabLayout parent;
        private int position = -1;
        private Object tag;
        private CharSequence text;
        public TabView view;

        @Nullable
        public Object getTag() {
            return this.tag;
        }

        @NonNull
        public Tab setTag(@Nullable Object tag) {
            this.tag = tag;
            return this;
        }

        @Nullable
        public View getCustomView() {
            return this.customView;
        }

        @NonNull
        public Tab setCustomView(@Nullable View view) {
            this.customView = view;
            updateView();
            return this;
        }

        @NonNull
        public Tab setCustomView(@LayoutRes int resId) {
            LayoutInflater inflater = LayoutInflater.from(this.view.getContext());
            return setCustomView(inflater.inflate(resId, (ViewGroup) this.view, false));
        }

        @Nullable
        public Drawable getIcon() {
            return this.icon;
        }

        public int getPosition() {
            return this.position;
        }

        void setPosition(int position) {
            this.position = position;
        }

        @Nullable
        public CharSequence getText() {
            return this.text;
        }

        @NonNull
        public Tab setIcon(@Nullable Drawable icon) {
            this.icon = icon;
            updateView();
            return this;
        }

        @NonNull
        public Tab setIcon(@DrawableRes int resId) {
            TabLayout tabLayout = this.parent;
            if (tabLayout == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return setIcon(AppCompatResources.getDrawable(tabLayout.getContext(), resId));
        }

        @NonNull
        public Tab setText(@Nullable CharSequence text) {
            if (TextUtils.isEmpty(this.contentDesc) && !TextUtils.isEmpty(text)) {
                this.view.setContentDescription(text);
            }
            this.text = text;
            updateView();
            return this;
        }

        @NonNull
        public Tab setText(@StringRes int resId) {
            TabLayout tabLayout = this.parent;
            if (tabLayout == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return setText(tabLayout.getResources().getText(resId));
        }

        public void select() {
            TabLayout tabLayout = this.parent;
            if (tabLayout == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            tabLayout.selectTab(this);
        }

        public boolean isSelected() {
            TabLayout tabLayout = this.parent;
            if (tabLayout != null) {
                return tabLayout.getSelectedTabPosition() == this.position;
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        @NonNull
        public Tab setContentDescription(@StringRes int resId) {
            TabLayout tabLayout = this.parent;
            if (tabLayout == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return setContentDescription(tabLayout.getResources().getText(resId));
        }

        @NonNull
        public Tab setContentDescription(@Nullable CharSequence contentDesc) {
            this.contentDesc = contentDesc;
            updateView();
            return this;
        }

        @Nullable
        public CharSequence getContentDescription() {
            TabView tabView = this.view;
            if (tabView == null) {
                return null;
            }
            return tabView.getContentDescription();
        }

        void updateView() {
            TabView tabView = this.view;
            if (tabView != null) {
                tabView.update();
            }
        }

        void reset() {
            this.parent = null;
            this.view = null;
            this.tag = null;
            this.icon = null;
            this.text = null;
            this.contentDesc = null;
            this.position = -1;
            this.customView = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class TabView extends LinearLayout {
        @Nullable
        private Drawable baseBackgroundDrawable;
        private ImageView customIconView;
        private TextView customTextView;
        private View customView;
        private int defaultMaxLines = 2;
        private ImageView iconView;
        private Tab tab;
        private TextView textView;

        public TabView(Context context) {
            super(context);
            updateBackgroundDrawable(context);
            ViewCompat.setPaddingRelative(this, TabLayout.this.tabPaddingStart, TabLayout.this.tabPaddingTop, TabLayout.this.tabPaddingEnd, TabLayout.this.tabPaddingBottom);
            setGravity(17);
            setOrientation(!TabLayout.this.inlineLabel ? 1 : 0);
            setClickable(true);
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), 1002));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateBackgroundDrawable(Context context) {
            Drawable background;
            GradientDrawable gradientDrawable = null;
            if (TabLayout.this.tabBackgroundResId != 0) {
                this.baseBackgroundDrawable = AppCompatResources.getDrawable(context, TabLayout.this.tabBackgroundResId);
                Drawable drawable = this.baseBackgroundDrawable;
                if (drawable != null && drawable.isStateful()) {
                    this.baseBackgroundDrawable.setState(getDrawableState());
                }
            } else {
                this.baseBackgroundDrawable = null;
            }
            Drawable contentDrawable = new GradientDrawable();
            ((GradientDrawable) contentDrawable).setColor(0);
            if (TabLayout.this.tabRippleColorStateList != null) {
                GradientDrawable maskDrawable = new GradientDrawable();
                maskDrawable.setCornerRadius(1.0E-5f);
                maskDrawable.setColor(-1);
                ColorStateList rippleColor = RippleUtils.convertToRippleDrawableColor(TabLayout.this.tabRippleColorStateList);
                if (Build.VERSION.SDK_INT >= 21) {
                    GradientDrawable gradientDrawable2 = TabLayout.this.unboundedRipple ? null : contentDrawable;
                    if (!TabLayout.this.unboundedRipple) {
                        gradientDrawable = maskDrawable;
                    }
                    background = new RippleDrawable(rippleColor, gradientDrawable2, gradientDrawable);
                } else {
                    Drawable rippleDrawable = DrawableCompat.wrap(maskDrawable);
                    DrawableCompat.setTintList(rippleDrawable, rippleColor);
                    background = new LayerDrawable(new Drawable[]{contentDrawable, rippleDrawable});
                }
            } else {
                background = contentDrawable;
            }
            ViewCompat.setBackground(this, background);
            TabLayout.this.invalidate();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void drawBackground(Canvas canvas) {
            Drawable drawable = this.baseBackgroundDrawable;
            if (drawable != null) {
                drawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
                this.baseBackgroundDrawable.draw(canvas);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void drawableStateChanged() {
            super.drawableStateChanged();
            boolean changed = false;
            int[] state = getDrawableState();
            Drawable drawable = this.baseBackgroundDrawable;
            if (drawable != null && drawable.isStateful()) {
                changed = false | this.baseBackgroundDrawable.setState(state);
            }
            if (changed) {
                invalidate();
                TabLayout.this.invalidate();
            }
        }

        @Override // android.view.View
        public boolean performClick() {
            boolean handled = super.performClick();
            if (this.tab != null) {
                if (!handled) {
                    playSoundEffect(0);
                }
                this.tab.select();
                return true;
            }
            return handled;
        }

        @Override // android.view.View
        public void setSelected(boolean selected) {
            boolean changed = isSelected() != selected;
            super.setSelected(selected);
            if (changed && selected && Build.VERSION.SDK_INT < 16) {
                sendAccessibilityEvent(4);
            }
            TextView textView = this.textView;
            if (textView != null) {
                textView.setSelected(selected);
            }
            ImageView imageView = this.iconView;
            if (imageView != null) {
                imageView.setSelected(selected);
            }
            View view = this.customView;
            if (view != null) {
                view.setSelected(selected);
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(event);
            event.setClassName(ActionBar.Tab.class.getName());
        }

        @Override // android.view.View
        @TargetApi(14)
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(ActionBar.Tab.class.getName());
        }

        @Override // android.widget.LinearLayout, android.view.View
        public void onMeasure(int origWidthMeasureSpec, int origHeightMeasureSpec) {
            int widthMeasureSpec;
            Layout layout;
            int specWidthSize = View.MeasureSpec.getSize(origWidthMeasureSpec);
            int specWidthMode = View.MeasureSpec.getMode(origWidthMeasureSpec);
            int maxWidth = TabLayout.this.getTabMaxWidth();
            if (maxWidth > 0 && (specWidthMode == 0 || specWidthSize > maxWidth)) {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(TabLayout.this.tabMaxWidth, Integer.MIN_VALUE);
            } else {
                widthMeasureSpec = origWidthMeasureSpec;
            }
            super.onMeasure(widthMeasureSpec, origHeightMeasureSpec);
            if (this.textView != null) {
                float textSize = TabLayout.this.tabTextSize;
                int maxLines = this.defaultMaxLines;
                ImageView imageView = this.iconView;
                if (imageView != null && imageView.getVisibility() == 0) {
                    maxLines = 1;
                } else {
                    TextView textView = this.textView;
                    if (textView != null && textView.getLineCount() > 1) {
                        textSize = TabLayout.this.tabTextMultiLineSize;
                    }
                }
                float curTextSize = this.textView.getTextSize();
                int curLineCount = this.textView.getLineCount();
                int curMaxLines = TextViewCompat.getMaxLines(this.textView);
                if (textSize != curTextSize || (curMaxLines >= 0 && maxLines != curMaxLines)) {
                    boolean updateTextView = true;
                    if (TabLayout.this.mode == 1 && textSize > curTextSize && curLineCount == 1 && ((layout = this.textView.getLayout()) == null || approximateLineWidth(layout, 0, textSize) > (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight())) {
                        updateTextView = false;
                    }
                    if (updateTextView) {
                        this.textView.setTextSize(0, textSize);
                        this.textView.setMaxLines(maxLines);
                        super.onMeasure(widthMeasureSpec, origHeightMeasureSpec);
                    }
                }
            }
        }

        void setTab(@Nullable Tab tab) {
            if (tab != this.tab) {
                this.tab = tab;
                update();
            }
        }

        void reset() {
            setTab(null);
            setSelected(false);
        }

        final void update() {
            Tab tab = this.tab;
            Drawable icon = null;
            View custom = tab != null ? tab.getCustomView() : null;
            if (custom != null) {
                ViewParent customParent = custom.getParent();
                if (customParent != this) {
                    if (customParent != null) {
                        ((ViewGroup) customParent).removeView(custom);
                    }
                    addView(custom);
                }
                this.customView = custom;
                TextView textView = this.textView;
                if (textView != null) {
                    textView.setVisibility(8);
                }
                ImageView imageView = this.iconView;
                if (imageView != null) {
                    imageView.setVisibility(8);
                    this.iconView.setImageDrawable(null);
                }
                this.customTextView = (TextView) custom.findViewById(16908308);
                TextView textView2 = this.customTextView;
                if (textView2 != null) {
                    this.defaultMaxLines = TextViewCompat.getMaxLines(textView2);
                }
                this.customIconView = (ImageView) custom.findViewById(16908294);
            } else {
                View view = this.customView;
                if (view != null) {
                    removeView(view);
                    this.customView = null;
                }
                this.customTextView = null;
                this.customIconView = null;
            }
            boolean z = false;
            if (this.customView == null) {
                if (this.iconView == null) {
                    ImageView iconView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.design_layout_tab_icon, (ViewGroup) this, false);
                    addView(iconView, 0);
                    this.iconView = iconView;
                }
                if (tab != null && tab.getIcon() != null) {
                    icon = DrawableCompat.wrap(tab.getIcon()).mutate();
                }
                if (icon != null) {
                    DrawableCompat.setTintList(icon, TabLayout.this.tabIconTint);
                    if (TabLayout.this.tabIconTintMode != null) {
                        DrawableCompat.setTintMode(icon, TabLayout.this.tabIconTintMode);
                    }
                }
                if (this.textView == null) {
                    TextView textView3 = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.design_layout_tab_text, (ViewGroup) this, false);
                    addView(textView3);
                    this.textView = textView3;
                    this.defaultMaxLines = TextViewCompat.getMaxLines(this.textView);
                }
                TextViewCompat.setTextAppearance(this.textView, TabLayout.this.tabTextAppearance);
                if (TabLayout.this.tabTextColors != null) {
                    this.textView.setTextColor(TabLayout.this.tabTextColors);
                }
                updateTextAndIcon(this.textView, this.iconView);
            } else if (this.customTextView != null || this.customIconView != null) {
                updateTextAndIcon(this.customTextView, this.customIconView);
            }
            if (tab != null && !TextUtils.isEmpty(tab.contentDesc)) {
                setContentDescription(tab.contentDesc);
            }
            if (tab != null && tab.isSelected()) {
                z = true;
            }
            setSelected(z);
        }

        final void updateOrientation() {
            setOrientation(!TabLayout.this.inlineLabel ? 1 : 0);
            if (this.customTextView != null || this.customIconView != null) {
                updateTextAndIcon(this.customTextView, this.customIconView);
            } else {
                updateTextAndIcon(this.textView, this.iconView);
            }
        }

        private void updateTextAndIcon(@Nullable TextView textView, @Nullable ImageView iconView) {
            Tab tab = this.tab;
            CharSequence charSequence = null;
            Drawable icon = (tab == null || tab.getIcon() == null) ? null : DrawableCompat.wrap(this.tab.getIcon()).mutate();
            Tab tab2 = this.tab;
            CharSequence text = tab2 != null ? tab2.getText() : null;
            if (iconView != null) {
                if (icon != null) {
                    iconView.setImageDrawable(icon);
                    iconView.setVisibility(0);
                    setVisibility(0);
                } else {
                    iconView.setVisibility(8);
                    iconView.setImageDrawable(null);
                }
            }
            boolean hasText = !TextUtils.isEmpty(text);
            if (textView != null) {
                if (hasText) {
                    textView.setText(text);
                    textView.setVisibility(0);
                    setVisibility(0);
                } else {
                    textView.setVisibility(8);
                    textView.setText((CharSequence) null);
                }
            }
            if (iconView != null) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) iconView.getLayoutParams();
                int iconMargin = 0;
                if (hasText && iconView.getVisibility() == 0) {
                    iconMargin = TabLayout.this.dpToPx(8);
                }
                if (TabLayout.this.inlineLabel) {
                    if (iconMargin != MarginLayoutParamsCompat.getMarginEnd(lp)) {
                        MarginLayoutParamsCompat.setMarginEnd(lp, iconMargin);
                        lp.bottomMargin = 0;
                        iconView.setLayoutParams(lp);
                        iconView.requestLayout();
                    }
                } else if (iconMargin != lp.bottomMargin) {
                    lp.bottomMargin = iconMargin;
                    MarginLayoutParamsCompat.setMarginEnd(lp, 0);
                    iconView.setLayoutParams(lp);
                    iconView.requestLayout();
                }
            }
            Tab tab3 = this.tab;
            CharSequence contentDesc = tab3 != null ? tab3.contentDesc : null;
            if (!hasText) {
                charSequence = contentDesc;
            }
            TooltipCompat.setTooltipText(this, charSequence);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getContentWidth() {
            View[] viewArr;
            boolean initialized = false;
            int left = 0;
            int right = 0;
            for (View view : new View[]{this.textView, this.iconView, this.customView}) {
                if (view != null && view.getVisibility() == 0) {
                    left = initialized ? Math.min(left, view.getLeft()) : view.getLeft();
                    right = initialized ? Math.max(right, view.getRight()) : view.getRight();
                    initialized = true;
                }
            }
            return right - left;
        }

        public Tab getTab() {
            return this.tab;
        }

        private float approximateLineWidth(Layout layout, int line, float textSize) {
            return layout.getLineWidth(line) * (textSize / layout.getPaint().getTextSize());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SlidingTabIndicator extends LinearLayout {
        private ValueAnimator indicatorAnimator;
        private int selectedIndicatorHeight;
        float selectionOffset;
        int selectedPosition = -1;
        private int layoutDirection = -1;
        private int indicatorLeft = -1;
        private int indicatorRight = -1;
        private final Paint selectedIndicatorPaint = new Paint();
        private final GradientDrawable defaultSelectionIndicator = new GradientDrawable();

        SlidingTabIndicator(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        void setSelectedIndicatorColor(int color) {
            if (this.selectedIndicatorPaint.getColor() != color) {
                this.selectedIndicatorPaint.setColor(color);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        void setSelectedIndicatorHeight(int height) {
            if (this.selectedIndicatorHeight != height) {
                this.selectedIndicatorHeight = height;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        boolean childrenNeedLayout() {
            int z = getChildCount();
            for (int i = 0; i < z; i++) {
                View child = getChildAt(i);
                if (child.getWidth() <= 0) {
                    return true;
                }
            }
            return false;
        }

        void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
            ValueAnimator valueAnimator = this.indicatorAnimator;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
            }
            this.selectedPosition = position;
            this.selectionOffset = positionOffset;
            updateIndicatorPosition();
        }

        float getIndicatorPosition() {
            return this.selectedPosition + this.selectionOffset;
        }

        @Override // android.widget.LinearLayout, android.view.View
        public void onRtlPropertiesChanged(int layoutDirection) {
            super.onRtlPropertiesChanged(layoutDirection);
            if (Build.VERSION.SDK_INT < 23 && this.layoutDirection != layoutDirection) {
                requestLayout();
                this.layoutDirection = layoutDirection;
            }
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824 && TabLayout.this.mode == 1 && TabLayout.this.tabGravity == 1) {
                int count = getChildCount();
                int largestTabWidth = 0;
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() == 0) {
                        largestTabWidth = Math.max(largestTabWidth, child.getMeasuredWidth());
                    }
                }
                if (largestTabWidth <= 0) {
                    return;
                }
                int gutter = TabLayout.this.dpToPx(16);
                boolean remeasure = false;
                if (largestTabWidth * count <= getMeasuredWidth() - (gutter * 2)) {
                    for (int i2 = 0; i2 < count; i2++) {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getChildAt(i2).getLayoutParams();
                        if (lp.width != largestTabWidth || lp.weight != 0.0f) {
                            lp.width = largestTabWidth;
                            lp.weight = 0.0f;
                            remeasure = true;
                        }
                    }
                } else {
                    TabLayout tabLayout = TabLayout.this;
                    tabLayout.tabGravity = 0;
                    tabLayout.updateTabViews(false);
                    remeasure = true;
                }
                if (remeasure) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        }

        @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            ValueAnimator valueAnimator = this.indicatorAnimator;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
                long duration = this.indicatorAnimator.getDuration();
                animateIndicatorToPosition(this.selectedPosition, Math.round((1.0f - this.indicatorAnimator.getAnimatedFraction()) * ((float) duration)));
                return;
            }
            updateIndicatorPosition();
        }

        private void updateIndicatorPosition() {
            int left;
            int right;
            View selectedTitle = getChildAt(this.selectedPosition);
            if (selectedTitle != null && selectedTitle.getWidth() > 0) {
                left = selectedTitle.getLeft();
                right = selectedTitle.getRight();
                if (!TabLayout.this.tabIndicatorFullWidth && (selectedTitle instanceof TabView)) {
                    calculateTabViewContentBounds((TabView) selectedTitle, TabLayout.this.tabViewContentBounds);
                    left = (int) TabLayout.this.tabViewContentBounds.left;
                    right = (int) TabLayout.this.tabViewContentBounds.right;
                }
                if (this.selectionOffset > 0.0f && this.selectedPosition < getChildCount() - 1) {
                    View nextTitle = getChildAt(this.selectedPosition + 1);
                    int nextTitleLeft = nextTitle.getLeft();
                    int nextTitleRight = nextTitle.getRight();
                    if (!TabLayout.this.tabIndicatorFullWidth && (nextTitle instanceof TabView)) {
                        calculateTabViewContentBounds((TabView) nextTitle, TabLayout.this.tabViewContentBounds);
                        nextTitleLeft = (int) TabLayout.this.tabViewContentBounds.left;
                        nextTitleRight = (int) TabLayout.this.tabViewContentBounds.right;
                    }
                    float f = this.selectionOffset;
                    left = (int) ((nextTitleLeft * f) + ((1.0f - f) * left));
                    right = (int) ((nextTitleRight * f) + ((1.0f - f) * right));
                }
            } else {
                left = -1;
                right = -1;
            }
            setIndicatorPosition(left, right);
        }

        void setIndicatorPosition(int left, int right) {
            if (left != this.indicatorLeft || right != this.indicatorRight) {
                this.indicatorLeft = left;
                this.indicatorRight = right;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        void animateIndicatorToPosition(final int position, int duration) {
            int targetLeft;
            int targetRight;
            ValueAnimator valueAnimator = this.indicatorAnimator;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
            }
            View targetView = getChildAt(position);
            if (targetView == null) {
                updateIndicatorPosition();
                return;
            }
            int targetLeft2 = targetView.getLeft();
            int targetRight2 = targetView.getRight();
            if (!TabLayout.this.tabIndicatorFullWidth && (targetView instanceof TabView)) {
                calculateTabViewContentBounds((TabView) targetView, TabLayout.this.tabViewContentBounds);
                int targetLeft3 = (int) TabLayout.this.tabViewContentBounds.left;
                int targetRight3 = (int) TabLayout.this.tabViewContentBounds.right;
                targetLeft = targetLeft3;
                targetRight = targetRight3;
            } else {
                targetLeft = targetLeft2;
                targetRight = targetRight2;
            }
            final int finalTargetLeft = targetLeft;
            final int finalTargetRight = targetRight;
            final int startLeft = this.indicatorLeft;
            final int startRight = this.indicatorRight;
            if (startLeft != finalTargetLeft || startRight != finalTargetRight) {
                ValueAnimator animator = new ValueAnimator();
                this.indicatorAnimator = animator;
                animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                animator.setDuration(duration);
                animator.setFloatValues(0.0f, 1.0f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.support.design.widget.TabLayout.SlidingTabIndicator.1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animator2) {
                        float fraction = animator2.getAnimatedFraction();
                        SlidingTabIndicator.this.setIndicatorPosition(AnimationUtils.lerp(startLeft, finalTargetLeft, fraction), AnimationUtils.lerp(startRight, finalTargetRight, fraction));
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() { // from class: android.support.design.widget.TabLayout.SlidingTabIndicator.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        SlidingTabIndicator slidingTabIndicator = SlidingTabIndicator.this;
                        slidingTabIndicator.selectedPosition = position;
                        slidingTabIndicator.selectionOffset = 0.0f;
                    }
                });
                animator.start();
            }
        }

        private void calculateTabViewContentBounds(TabView tabView, RectF contentBounds) {
            int tabViewContentWidth = tabView.getContentWidth();
            if (tabViewContentWidth < TabLayout.this.dpToPx(24)) {
                tabViewContentWidth = TabLayout.this.dpToPx(24);
            }
            int tabViewCenter = (tabView.getLeft() + tabView.getRight()) / 2;
            int contentLeftBounds = tabViewCenter - (tabViewContentWidth / 2);
            int contentRightBounds = (tabViewContentWidth / 2) + tabViewCenter;
            contentBounds.set(contentLeftBounds, 0.0f, contentRightBounds, 0.0f);
        }

        @Override // android.view.View
        public void draw(Canvas canvas) {
            int indicatorHeight = 0;
            if (TabLayout.this.tabSelectedIndicator != null) {
                indicatorHeight = TabLayout.this.tabSelectedIndicator.getIntrinsicHeight();
            }
            if (this.selectedIndicatorHeight >= 0) {
                indicatorHeight = this.selectedIndicatorHeight;
            }
            int indicatorTop = 0;
            int indicatorBottom = 0;
            switch (TabLayout.this.tabIndicatorGravity) {
                case 0:
                    indicatorTop = getHeight() - indicatorHeight;
                    indicatorBottom = getHeight();
                    break;
                case 1:
                    indicatorTop = (getHeight() - indicatorHeight) / 2;
                    indicatorBottom = (getHeight() + indicatorHeight) / 2;
                    break;
                case 2:
                    indicatorTop = 0;
                    indicatorBottom = indicatorHeight;
                    break;
                case 3:
                    indicatorTop = 0;
                    indicatorBottom = getHeight();
                    break;
            }
            int i = this.indicatorLeft;
            if (i >= 0 && this.indicatorRight > i) {
                Drawable selectedIndicator = DrawableCompat.wrap(TabLayout.this.tabSelectedIndicator != null ? TabLayout.this.tabSelectedIndicator : this.defaultSelectionIndicator);
                selectedIndicator.setBounds(this.indicatorLeft, indicatorTop, this.indicatorRight, indicatorBottom);
                if (this.selectedIndicatorPaint != null) {
                    if (Build.VERSION.SDK_INT == 21) {
                        selectedIndicator.setColorFilter(this.selectedIndicatorPaint.getColor(), PorterDuff.Mode.SRC_IN);
                    } else {
                        DrawableCompat.setTint(selectedIndicator, this.selectedIndicatorPaint.getColor());
                    }
                }
                selectedIndicator.draw(canvas);
            }
            super.draw(canvas);
        }
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        int[][] states = new int[2];
        int[] colors = new int[2];
        states[0] = SELECTED_STATE_SET;
        colors[0] = selectedColor;
        int i = 0 + 1;
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        int i2 = i + 1;
        return new ColorStateList(states, colors);
    }

    @Dimension(unit = 0)
    private int getDefaultHeight() {
        boolean hasIconAndText = false;
        int i = 0;
        int count = this.tabs.size();
        while (true) {
            if (i < count) {
                Tab tab = this.tabs.get(i);
                if (tab == null || tab.getIcon() == null || TextUtils.isEmpty(tab.getText())) {
                    i++;
                } else {
                    hasIconAndText = true;
                    break;
                }
            } else {
                break;
            }
        }
        return (!hasIconAndText || this.inlineLabel) ? 48 : 72;
    }

    private int getTabMinWidth() {
        int i = this.requestedTabMinWidth;
        if (i != -1) {
            return i;
        }
        if (this.mode != 0) {
            return 0;
        }
        return this.scrollableTabMinWidth;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return generateDefaultLayoutParams();
    }

    int getTabMaxWidth() {
        return this.tabMaxWidth;
    }

    /* loaded from: classes.dex */
    public static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int previousScrollState;
        private int scrollState;
        private final WeakReference<TabLayout> tabLayoutRef;

        public TabLayoutOnPageChangeListener(TabLayout tabLayout) {
            this.tabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int state) {
            this.previousScrollState = this.scrollState;
            this.scrollState = state;
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            TabLayout tabLayout = this.tabLayoutRef.get();
            if (tabLayout != null) {
                boolean updateIndicator = false;
                boolean updateText = this.scrollState != 2 || this.previousScrollState == 1;
                if (this.scrollState != 2 || this.previousScrollState != 0) {
                    updateIndicator = true;
                }
                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator);
            }
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageSelected(int position) {
            TabLayout tabLayout = this.tabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position && position < tabLayout.getTabCount()) {
                int i = this.scrollState;
                boolean updateIndicator = i == 0 || (i == 2 && this.previousScrollState == 0);
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator);
            }
        }

        void reset() {
            this.scrollState = 0;
            this.previousScrollState = 0;
        }
    }

    /* loaded from: classes.dex */
    public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final ViewPager viewPager;

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        @Override // android.support.design.widget.TabLayout.BaseOnTabSelectedListener
        public void onTabSelected(Tab tab) {
            this.viewPager.setCurrentItem(tab.getPosition());
        }

        @Override // android.support.design.widget.TabLayout.BaseOnTabSelectedListener
        public void onTabUnselected(Tab tab) {
        }

        @Override // android.support.design.widget.TabLayout.BaseOnTabSelectedListener
        public void onTabReselected(Tab tab) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PagerAdapterObserver extends DataSetObserver {
        PagerAdapterObserver() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            TabLayout.this.populateFromPagerAdapter();
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            TabLayout.this.populateFromPagerAdapter();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {
        private boolean autoRefresh;

        AdapterChangeListener() {
        }

        @Override // android.support.v4.view.ViewPager.OnAdapterChangeListener
        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            if (TabLayout.this.viewPager == viewPager) {
                TabLayout.this.setPagerAdapter(newAdapter, this.autoRefresh);
            }
        }

        void setAutoRefresh(boolean autoRefresh) {
            this.autoRefresh = autoRefresh;
        }
    }
}
