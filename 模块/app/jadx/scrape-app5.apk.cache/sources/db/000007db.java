package android.support.design.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.C0091R;
import android.support.design.internal.ThemeEnforcement;
import android.support.design.widget.CoordinatorLayout;
import android.support.p000v4.util.ObjectsCompat;
import android.support.p000v4.view.AbsSavedState;
import android.support.p000v4.view.NestedScrollingChild;
import android.support.p000v4.view.OnApplyWindowInsetsListener;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@CoordinatorLayout.DefaultBehavior(Behavior.class)
/* loaded from: classes.dex */
public class AppBarLayout extends LinearLayout {
    private static final int INVALID_SCROLL_RANGE = -1;
    static final int PENDING_ACTION_ANIMATE_ENABLED = 4;
    static final int PENDING_ACTION_COLLAPSED = 2;
    static final int PENDING_ACTION_EXPANDED = 1;
    static final int PENDING_ACTION_FORCE = 8;
    static final int PENDING_ACTION_NONE = 0;
    private int downPreScrollRange;
    private int downScrollRange;
    private boolean haveChildWithInterpolator;
    private WindowInsetsCompat lastInsets;
    private boolean liftOnScroll;
    private boolean liftable;
    private boolean liftableOverride;
    private boolean lifted;
    private List<BaseOnOffsetChangedListener> listeners;
    private int pendingAction;
    private int[] tmpStatesArray;
    private int totalScrollRange;

    /* loaded from: classes.dex */
    public interface BaseOnOffsetChangedListener<T extends AppBarLayout> {
        void onOffsetChanged(T t, int i);
    }

    /* loaded from: classes.dex */
    public interface OnOffsetChangedListener extends BaseOnOffsetChangedListener<AppBarLayout> {
        @Override // android.support.design.widget.AppBarLayout.BaseOnOffsetChangedListener
        void onOffsetChanged(AppBarLayout appBarLayout, int i);
    }

    public AppBarLayout(Context context) {
        this(context, null);
    }

    public AppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.totalScrollRange = -1;
        this.downPreScrollRange = -1;
        this.downScrollRange = -1;
        this.pendingAction = 0;
        setOrientation(1);
        if (Build.VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setBoundsViewOutlineProvider(this);
            ViewUtilsLollipop.setStateListAnimatorFromAttrs(this, attrs, 0, C0091R.style.Widget_Design_AppBarLayout);
        }
        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context, attrs, C0091R.styleable.AppBarLayout, 0, C0091R.style.Widget_Design_AppBarLayout, new int[0]);
        ViewCompat.setBackground(this, a.getDrawable(C0091R.styleable.AppBarLayout_android_background));
        if (a.hasValue(C0091R.styleable.AppBarLayout_expanded)) {
            setExpanded(a.getBoolean(C0091R.styleable.AppBarLayout_expanded, false), false, false);
        }
        if (Build.VERSION.SDK_INT >= 21 && a.hasValue(C0091R.styleable.AppBarLayout_elevation)) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, a.getDimensionPixelSize(C0091R.styleable.AppBarLayout_elevation, 0));
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (a.hasValue(C0091R.styleable.AppBarLayout_android_keyboardNavigationCluster)) {
                setKeyboardNavigationCluster(a.getBoolean(C0091R.styleable.AppBarLayout_android_keyboardNavigationCluster, false));
            }
            if (a.hasValue(C0091R.styleable.AppBarLayout_android_touchscreenBlocksFocus)) {
                setTouchscreenBlocksFocus(a.getBoolean(C0091R.styleable.AppBarLayout_android_touchscreenBlocksFocus, false));
            }
        }
        this.liftOnScroll = a.getBoolean(C0091R.styleable.AppBarLayout_liftOnScroll, false);
        a.recycle();
        ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() { // from class: android.support.design.widget.AppBarLayout.1
            @Override // android.support.p000v4.view.OnApplyWindowInsetsListener
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                return AppBarLayout.this.onWindowInsetChanged(insets);
            }
        });
    }

    public void addOnOffsetChangedListener(BaseOnOffsetChangedListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        if (listener != null && !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void addOnOffsetChangedListener(OnOffsetChangedListener listener) {
        addOnOffsetChangedListener((BaseOnOffsetChangedListener) listener);
    }

    public void removeOnOffsetChangedListener(BaseOnOffsetChangedListener listener) {
        List<BaseOnOffsetChangedListener> list = this.listeners;
        if (list != null && listener != null) {
            list.remove(listener);
        }
    }

    public void removeOnOffsetChangedListener(OnOffsetChangedListener listener) {
        removeOnOffsetChangedListener((BaseOnOffsetChangedListener) listener);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidateScrollRanges();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        invalidateScrollRanges();
        boolean z = false;
        this.haveChildWithInterpolator = false;
        int i = 0;
        int z2 = getChildCount();
        while (true) {
            if (i >= z2) {
                break;
            }
            View child = getChildAt(i);
            LayoutParams childLp = (LayoutParams) child.getLayoutParams();
            Interpolator interpolator = childLp.getScrollInterpolator();
            if (interpolator == null) {
                i++;
            } else {
                this.haveChildWithInterpolator = true;
                break;
            }
        }
        if (!this.liftableOverride) {
            if (this.liftOnScroll || hasCollapsibleChild()) {
                z = true;
            }
            setLiftableState(z);
        }
    }

    private boolean hasCollapsibleChild() {
        int z = getChildCount();
        for (int i = 0; i < z; i++) {
            if (((LayoutParams) getChildAt(i).getLayoutParams()).isCollapsible()) {
                return true;
            }
        }
        return false;
    }

    private void invalidateScrollRanges() {
        this.totalScrollRange = -1;
        this.downPreScrollRange = -1;
        this.downScrollRange = -1;
    }

    @Override // android.widget.LinearLayout
    public void setOrientation(int orientation) {
        if (orientation != 1) {
            throw new IllegalArgumentException("AppBarLayout is always vertical and does not support horizontal orientation");
        }
        super.setOrientation(orientation);
    }

    public void setExpanded(boolean expanded) {
        setExpanded(expanded, ViewCompat.isLaidOut(this));
    }

    public void setExpanded(boolean expanded, boolean animate) {
        setExpanded(expanded, animate, true);
    }

    private void setExpanded(boolean expanded, boolean animate, boolean force) {
        int i = 0;
        int i2 = (expanded ? 1 : 2) | (animate ? 4 : 0);
        if (force) {
            i = 8;
        }
        this.pendingAction = i2 | i;
        requestLayout();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    /* renamed from: generateDefaultLayoutParams */
    public LayoutParams mo96generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    /* renamed from: generateLayoutParams */
    public LayoutParams mo99generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    /* renamed from: generateLayoutParams */
    public LayoutParams mo100generateLayoutParams(ViewGroup.LayoutParams p) {
        if (Build.VERSION.SDK_INT >= 19 && (p instanceof LinearLayout.LayoutParams)) {
            return new LayoutParams((LinearLayout.LayoutParams) p);
        }
        if (p instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    boolean hasChildWithInterpolator() {
        return this.haveChildWithInterpolator;
    }

    public final int getTotalScrollRange() {
        int i = this.totalScrollRange;
        if (i != -1) {
            return i;
        }
        int range = 0;
        int i2 = 0;
        int z = getChildCount();
        while (true) {
            if (i2 >= z) {
                break;
            }
            View child = getChildAt(i2);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int flags = lp.scrollFlags;
            if ((flags & 1) == 0) {
                break;
            }
            range += lp.topMargin + childHeight + lp.bottomMargin;
            if ((flags & 2) == 0) {
                i2++;
            } else {
                range -= ViewCompat.getMinimumHeight(child);
                break;
            }
        }
        int max = Math.max(0, range - getTopInset());
        this.totalScrollRange = max;
        return max;
    }

    boolean hasScrollableChildren() {
        return getTotalScrollRange() != 0;
    }

    int getUpNestedPreScrollRange() {
        return getTotalScrollRange();
    }

    int getDownNestedPreScrollRange() {
        int i = this.downPreScrollRange;
        if (i != -1) {
            return i;
        }
        int range = 0;
        for (int i2 = getChildCount() - 1; i2 >= 0; i2--) {
            View child = getChildAt(i2);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int flags = lp.scrollFlags;
            if ((flags & 5) == 5) {
                int range2 = range + lp.topMargin + lp.bottomMargin;
                if ((flags & 8) != 0) {
                    range = range2 + ViewCompat.getMinimumHeight(child);
                } else if ((flags & 2) != 0) {
                    range = range2 + (childHeight - ViewCompat.getMinimumHeight(child));
                } else {
                    range = range2 + (childHeight - getTopInset());
                }
            } else if (range > 0) {
                break;
            }
        }
        int max = Math.max(0, range);
        this.downPreScrollRange = max;
        return max;
    }

    int getDownNestedScrollRange() {
        int i = this.downScrollRange;
        if (i != -1) {
            return i;
        }
        int range = 0;
        int i2 = 0;
        int z = getChildCount();
        while (true) {
            if (i2 >= z) {
                break;
            }
            View child = getChildAt(i2);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int childHeight2 = childHeight + lp.topMargin + lp.bottomMargin;
            int flags = lp.scrollFlags;
            if ((flags & 1) == 0) {
                break;
            }
            range += childHeight2;
            if ((flags & 2) == 0) {
                i2++;
            } else {
                range -= ViewCompat.getMinimumHeight(child) + getTopInset();
                break;
            }
        }
        int max = Math.max(0, range);
        this.downScrollRange = max;
        return max;
    }

    void dispatchOffsetUpdates(int offset) {
        List<BaseOnOffsetChangedListener> list = this.listeners;
        if (list != null) {
            int z = list.size();
            for (int i = 0; i < z; i++) {
                BaseOnOffsetChangedListener listener = this.listeners.get(i);
                if (listener != null) {
                    listener.onOffsetChanged(this, offset);
                }
            }
        }
    }

    public final int getMinimumHeightForVisibleOverlappingContent() {
        int topInset = getTopInset();
        int minHeight = ViewCompat.getMinimumHeight(this);
        if (minHeight != 0) {
            return (minHeight * 2) + topInset;
        }
        int childCount = getChildCount();
        int lastChildMinHeight = childCount >= 1 ? ViewCompat.getMinimumHeight(getChildAt(childCount - 1)) : 0;
        if (lastChildMinHeight != 0) {
            return (lastChildMinHeight * 2) + topInset;
        }
        return getHeight() / 3;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected int[] onCreateDrawableState(int extraSpace) {
        if (this.tmpStatesArray == null) {
            this.tmpStatesArray = new int[4];
        }
        int[] extraStates = this.tmpStatesArray;
        int[] states = super.onCreateDrawableState(extraStates.length + extraSpace);
        extraStates[0] = this.liftable ? C0091R.attr.state_liftable : -C0091R.attr.state_liftable;
        extraStates[1] = (!this.liftable || !this.lifted) ? -C0091R.attr.state_lifted : C0091R.attr.state_lifted;
        extraStates[2] = this.liftable ? C0091R.attr.state_collapsible : -C0091R.attr.state_collapsible;
        extraStates[3] = (!this.liftable || !this.lifted) ? -C0091R.attr.state_collapsed : C0091R.attr.state_collapsed;
        return mergeDrawableStates(states, extraStates);
    }

    public boolean setLiftable(boolean liftable) {
        this.liftableOverride = true;
        return setLiftableState(liftable);
    }

    private boolean setLiftableState(boolean liftable) {
        if (this.liftable != liftable) {
            this.liftable = liftable;
            refreshDrawableState();
            return true;
        }
        return false;
    }

    public boolean setLifted(boolean lifted) {
        return setLiftedState(lifted);
    }

    boolean setLiftedState(boolean lifted) {
        if (this.lifted != lifted) {
            this.lifted = lifted;
            refreshDrawableState();
            return true;
        }
        return false;
    }

    public void setLiftOnScroll(boolean liftOnScroll) {
        this.liftOnScroll = liftOnScroll;
    }

    public boolean isLiftOnScroll() {
        return this.liftOnScroll;
    }

    @Deprecated
    public void setTargetElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, elevation);
        }
    }

    @Deprecated
    public float getTargetElevation() {
        return 0.0f;
    }

    int getPendingAction() {
        return this.pendingAction;
    }

    void resetPendingAction() {
        this.pendingAction = 0;
    }

    @VisibleForTesting
    final int getTopInset() {
        WindowInsetsCompat windowInsetsCompat = this.lastInsets;
        if (windowInsetsCompat != null) {
            return windowInsetsCompat.getSystemWindowInsetTop();
        }
        return 0;
    }

    WindowInsetsCompat onWindowInsetChanged(WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            newInsets = insets;
        }
        if (!ObjectsCompat.equals(this.lastInsets, newInsets)) {
            this.lastInsets = newInsets;
            invalidateScrollRanges();
        }
        return insets;
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends LinearLayout.LayoutParams {
        static final int COLLAPSIBLE_FLAGS = 10;
        static final int FLAG_QUICK_RETURN = 5;
        static final int FLAG_SNAP = 17;
        public static final int SCROLL_FLAG_ENTER_ALWAYS = 4;
        public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 8;
        public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 2;
        public static final int SCROLL_FLAG_SCROLL = 1;
        public static final int SCROLL_FLAG_SNAP = 16;
        public static final int SCROLL_FLAG_SNAP_MARGINS = 32;
        int scrollFlags;
        Interpolator scrollInterpolator;

        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        /* loaded from: classes.dex */
        public @interface ScrollFlags {
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.scrollFlags = 1;
            TypedArray a = c.obtainStyledAttributes(attrs, C0091R.styleable.AppBarLayout_Layout);
            this.scrollFlags = a.getInt(C0091R.styleable.AppBarLayout_Layout_layout_scrollFlags, 0);
            if (a.hasValue(C0091R.styleable.AppBarLayout_Layout_layout_scrollInterpolator)) {
                int resId = a.getResourceId(C0091R.styleable.AppBarLayout_Layout_layout_scrollInterpolator, 0);
                this.scrollInterpolator = AnimationUtils.loadInterpolator(c, resId);
            }
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.scrollFlags = 1;
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
            this.scrollFlags = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            this.scrollFlags = 1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.scrollFlags = 1;
        }

        @RequiresApi(19)
        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
            this.scrollFlags = 1;
        }

        @RequiresApi(19)
        public LayoutParams(LayoutParams source) {
            super((LinearLayout.LayoutParams) source);
            this.scrollFlags = 1;
            this.scrollFlags = source.scrollFlags;
            this.scrollInterpolator = source.scrollInterpolator;
        }

        public void setScrollFlags(int flags) {
            this.scrollFlags = flags;
        }

        public int getScrollFlags() {
            return this.scrollFlags;
        }

        public void setScrollInterpolator(Interpolator interpolator) {
            this.scrollInterpolator = interpolator;
        }

        public Interpolator getScrollInterpolator() {
            return this.scrollInterpolator;
        }

        boolean isCollapsible() {
            int i = this.scrollFlags;
            return (i & 1) == 1 && (i & 10) != 0;
        }
    }

    /* loaded from: classes.dex */
    public static class Behavior extends BaseBehavior<AppBarLayout> {

        /* loaded from: classes.dex */
        public static abstract class DragCallback extends BaseBehavior.BaseDragCallback<AppBarLayout> {
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ int getLeftAndRightOffset() {
            return super.getLeftAndRightOffset();
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ boolean onLayoutChild(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, int i) {
            return super.onLayoutChild(coordinatorLayout, (CoordinatorLayout) appBarLayout, i);
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ boolean onMeasureChild(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, int i, int i2, int i3, int i4) {
            return super.onMeasureChild(coordinatorLayout, (CoordinatorLayout) appBarLayout, i, i2, i3, i4);
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view, int i, int i2, int[] iArr, int i3) {
            super.onNestedPreScroll(coordinatorLayout, (CoordinatorLayout) appBarLayout, view, i, i2, iArr, i3);
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view, int i, int i2, int i3, int i4, int i5) {
            super.onNestedScroll(coordinatorLayout, (CoordinatorLayout) appBarLayout, view, i, i2, i3, i4, i5);
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ void onRestoreInstanceState(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, Parcelable parcelable) {
            super.onRestoreInstanceState(coordinatorLayout, (CoordinatorLayout) appBarLayout, parcelable);
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ Parcelable onSaveInstanceState(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout) {
            return super.onSaveInstanceState(coordinatorLayout, (CoordinatorLayout) appBarLayout);
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view, View view2, int i, int i2) {
            return super.onStartNestedScroll(coordinatorLayout, (CoordinatorLayout) appBarLayout, view, view2, i, i2);
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View view, int i) {
            super.onStopNestedScroll(coordinatorLayout, (CoordinatorLayout) appBarLayout, view, i);
        }

        @Override // android.support.design.widget.AppBarLayout.BaseBehavior
        public /* bridge */ /* synthetic */ void setDragCallback(@Nullable BaseBehavior.BaseDragCallback baseDragCallback) {
            super.setDragCallback(baseDragCallback);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ boolean setLeftAndRightOffset(int i) {
            return super.setLeftAndRightOffset(i);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class BaseBehavior<T extends AppBarLayout> extends HeaderBehavior<T> {
        private static final int INVALID_POSITION = -1;
        private static final int MAX_OFFSET_ANIMATION_DURATION = 600;
        private WeakReference<View> lastNestedScrollingChildRef;
        private int lastStartedType;
        private ValueAnimator offsetAnimator;
        private int offsetDelta;
        private int offsetToChildIndexOnLayout = -1;
        private boolean offsetToChildIndexOnLayoutIsMinHeight;
        private float offsetToChildIndexOnLayoutPerc;
        private BaseDragCallback onDragCallback;

        /* loaded from: classes.dex */
        public static abstract class BaseDragCallback<T extends AppBarLayout> {
            public abstract boolean canDrag(@NonNull T t);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.HeaderBehavior
        /* bridge */ /* synthetic */ boolean canDragView(View view) {
            return canDragView((BaseBehavior<T>) ((AppBarLayout) view));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.HeaderBehavior
        /* bridge */ /* synthetic */ int getMaxDragOffset(View view) {
            return getMaxDragOffset((BaseBehavior<T>) ((AppBarLayout) view));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.HeaderBehavior
        /* bridge */ /* synthetic */ int getScrollRangeForDragFling(View view) {
            return getScrollRangeForDragFling((BaseBehavior<T>) ((AppBarLayout) view));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.HeaderBehavior
        /* bridge */ /* synthetic */ void onFlingFinished(CoordinatorLayout coordinatorLayout, View view) {
            onFlingFinished(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.ViewOffsetBehavior, android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
            return onLayoutChild(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view), i);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ boolean onMeasureChild(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3, int i4) {
            return onMeasureChild(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view), i, i2, i3, i4);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View view, View view2, int i, int i2, int[] iArr, int i3) {
            onNestedPreScroll(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view), view2, i, i2, iArr, i3);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ void onNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2, int i, int i2, int i3, int i4, int i5) {
            onNestedScroll(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view), view2, i, i2, i3, i4, i5);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ void onRestoreInstanceState(CoordinatorLayout coordinatorLayout, View view, Parcelable parcelable) {
            onRestoreInstanceState(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view), parcelable);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ Parcelable onSaveInstanceState(CoordinatorLayout coordinatorLayout, View view) {
            return onSaveInstanceState(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2, View view3, int i, int i2) {
            return onStartNestedScroll(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view), view2, view3, i, i2);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2, int i) {
            onStopNestedScroll(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view), view2, i);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.design.widget.HeaderBehavior
        /* bridge */ /* synthetic */ int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3) {
            return setHeaderTopBottomOffset(coordinatorLayout, (CoordinatorLayout) ((AppBarLayout) view), i, i2, i3);
        }

        public BaseBehavior() {
        }

        public BaseBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public boolean onStartNestedScroll(CoordinatorLayout parent, T child, View directTargetChild, View target, int nestedScrollAxes, int type) {
            ValueAnimator valueAnimator;
            boolean started = (nestedScrollAxes & 2) != 0 && (child.isLiftOnScroll() || canScrollChildren(parent, child, directTargetChild));
            if (started && (valueAnimator = this.offsetAnimator) != null) {
                valueAnimator.cancel();
            }
            this.lastNestedScrollingChildRef = null;
            this.lastStartedType = type;
            return started;
        }

        private boolean canScrollChildren(CoordinatorLayout parent, T child, View directTargetChild) {
            return child.hasScrollableChildren() && parent.getHeight() - directTargetChild.getHeight() <= child.getHeight();
        }

        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, T child, View target, int dx, int dy, int[] consumed, int type) {
            int min;
            int max;
            if (dy != 0) {
                if (dy < 0) {
                    int min2 = -child.getTotalScrollRange();
                    min = min2;
                    max = child.getDownNestedPreScrollRange() + min2;
                } else {
                    int min3 = child.getUpNestedPreScrollRange();
                    min = -min3;
                    max = 0;
                }
                if (min != max) {
                    consumed[1] = scroll(coordinatorLayout, child, dy, min, max);
                    stopNestedScrollIfNeeded(dy, child, target, type);
                }
            }
        }

        public void onNestedScroll(CoordinatorLayout coordinatorLayout, T child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
            if (dyUnconsumed < 0) {
                scroll(coordinatorLayout, child, dyUnconsumed, -child.getDownNestedScrollRange(), 0);
                stopNestedScrollIfNeeded(dyUnconsumed, child, target, type);
            }
            if (child.isLiftOnScroll()) {
                child.setLiftedState(target.getScrollY() > 0);
            }
        }

        private void stopNestedScrollIfNeeded(int dy, T child, View target, int type) {
            if (type == 1) {
                int curOffset = getTopBottomOffsetForScrollingSibling();
                if ((dy < 0 && curOffset == 0) || (dy > 0 && curOffset == (-child.getDownNestedScrollRange()))) {
                    ViewCompat.stopNestedScroll(target, 1);
                }
            }
        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, T abl, View target, int type) {
            if (this.lastStartedType == 0 || type == 1) {
                snapToChildIfNeeded(coordinatorLayout, abl);
            }
            this.lastNestedScrollingChildRef = new WeakReference<>(target);
        }

        public void setDragCallback(@Nullable BaseDragCallback callback) {
            this.onDragCallback = callback;
        }

        private void animateOffsetTo(CoordinatorLayout coordinatorLayout, T child, int offset, float velocity) {
            int duration;
            int distance = Math.abs(getTopBottomOffsetForScrollingSibling() - offset);
            float velocity2 = Math.abs(velocity);
            if (velocity2 > 0.0f) {
                duration = Math.round((distance / velocity2) * 1000.0f) * 3;
            } else {
                float distanceRatio = distance / child.getHeight();
                duration = (int) ((1.0f + distanceRatio) * 150.0f);
            }
            animateOffsetWithDuration(coordinatorLayout, child, offset, duration);
        }

        private void animateOffsetWithDuration(final CoordinatorLayout coordinatorLayout, final T child, int offset, int duration) {
            int currentOffset = getTopBottomOffsetForScrollingSibling();
            if (currentOffset == offset) {
                ValueAnimator valueAnimator = this.offsetAnimator;
                if (valueAnimator != null && valueAnimator.isRunning()) {
                    this.offsetAnimator.cancel();
                    return;
                }
                return;
            }
            ValueAnimator valueAnimator2 = this.offsetAnimator;
            if (valueAnimator2 == null) {
                this.offsetAnimator = new ValueAnimator();
                this.offsetAnimator.setInterpolator(android.support.design.animation.AnimationUtils.DECELERATE_INTERPOLATOR);
                this.offsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.support.design.widget.AppBarLayout.BaseBehavior.1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animator) {
                        BaseBehavior.this.setHeaderTopBottomOffset(coordinatorLayout, child, ((Integer) animator.getAnimatedValue()).intValue());
                    }
                });
            } else {
                valueAnimator2.cancel();
            }
            this.offsetAnimator.setDuration(Math.min(duration, (int) MAX_OFFSET_ANIMATION_DURATION));
            this.offsetAnimator.setIntValues(currentOffset, offset);
            this.offsetAnimator.start();
        }

        private int getChildIndexOnOffset(T abl, int offset) {
            int count = abl.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = abl.getChildAt(i);
                int top = child.getTop();
                int bottom = child.getBottom();
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (checkFlag(lp.getScrollFlags(), 32)) {
                    top -= lp.topMargin;
                    bottom += lp.bottomMargin;
                }
                if (top <= (-offset) && bottom >= (-offset)) {
                    return i;
                }
            }
            return -1;
        }

        private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, T abl) {
            int offset = getTopBottomOffsetForScrollingSibling();
            int offsetChildIndex = getChildIndexOnOffset(abl, offset);
            if (offsetChildIndex >= 0) {
                View offsetChild = abl.getChildAt(offsetChildIndex);
                LayoutParams lp = (LayoutParams) offsetChild.getLayoutParams();
                int flags = lp.getScrollFlags();
                if ((flags & 17) == 17) {
                    int snapTop = -offsetChild.getTop();
                    int snapBottom = -offsetChild.getBottom();
                    if (offsetChildIndex == abl.getChildCount() - 1) {
                        snapBottom += abl.getTopInset();
                    }
                    if (checkFlag(flags, 2)) {
                        snapBottom += ViewCompat.getMinimumHeight(offsetChild);
                    } else if (checkFlag(flags, 5)) {
                        int seam = ViewCompat.getMinimumHeight(offsetChild) + snapBottom;
                        if (offset < seam) {
                            snapTop = seam;
                        } else {
                            snapBottom = seam;
                        }
                    }
                    if (checkFlag(flags, 32)) {
                        snapTop += lp.topMargin;
                        snapBottom -= lp.bottomMargin;
                    }
                    int newOffset = offset < (snapBottom + snapTop) / 2 ? snapBottom : snapTop;
                    animateOffsetTo(coordinatorLayout, abl, android.support.p000v4.math.MathUtils.clamp(newOffset, -abl.getTotalScrollRange(), 0), 0.0f);
                }
            }
        }

        private static boolean checkFlag(int flags, int check) {
            return (flags & check) == check;
        }

        public boolean onMeasureChild(CoordinatorLayout parent, T child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            if (lp.height == -2) {
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, View.MeasureSpec.makeMeasureSpec(0, 0), heightUsed);
                return true;
            }
            return super.onMeasureChild(parent, (CoordinatorLayout) child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        }

        public boolean onLayoutChild(CoordinatorLayout parent, T abl, int layoutDirection) {
            int offset;
            boolean handled = super.onLayoutChild(parent, (CoordinatorLayout) abl, layoutDirection);
            int pendingAction = abl.getPendingAction();
            int i = this.offsetToChildIndexOnLayout;
            if (i >= 0 && (pendingAction & 8) == 0) {
                View child = abl.getChildAt(i);
                int offset2 = -child.getBottom();
                if (this.offsetToChildIndexOnLayoutIsMinHeight) {
                    offset = offset2 + ViewCompat.getMinimumHeight(child) + abl.getTopInset();
                } else {
                    offset = offset2 + Math.round(child.getHeight() * this.offsetToChildIndexOnLayoutPerc);
                }
                setHeaderTopBottomOffset(parent, abl, offset);
            } else if (pendingAction != 0) {
                boolean animate = (pendingAction & 4) != 0;
                if ((pendingAction & 2) != 0) {
                    int offset3 = -abl.getUpNestedPreScrollRange();
                    if (animate) {
                        animateOffsetTo(parent, abl, offset3, 0.0f);
                    } else {
                        setHeaderTopBottomOffset(parent, abl, offset3);
                    }
                } else if ((pendingAction & 1) != 0) {
                    if (animate) {
                        animateOffsetTo(parent, abl, 0, 0.0f);
                    } else {
                        setHeaderTopBottomOffset(parent, abl, 0);
                    }
                }
            }
            abl.resetPendingAction();
            this.offsetToChildIndexOnLayout = -1;
            setTopAndBottomOffset(android.support.p000v4.math.MathUtils.clamp(getTopAndBottomOffset(), -abl.getTotalScrollRange(), 0));
            updateAppBarLayoutDrawableState(parent, abl, getTopAndBottomOffset(), 0, true);
            abl.dispatchOffsetUpdates(getTopAndBottomOffset());
            return handled;
        }

        boolean canDragView(T view) {
            BaseDragCallback baseDragCallback = this.onDragCallback;
            if (baseDragCallback != null) {
                return baseDragCallback.canDrag(view);
            }
            WeakReference<View> weakReference = this.lastNestedScrollingChildRef;
            if (weakReference == null) {
                return true;
            }
            View scrollingView = weakReference.get();
            return scrollingView != null && scrollingView.isShown() && !scrollingView.canScrollVertically(-1);
        }

        void onFlingFinished(CoordinatorLayout parent, T layout) {
            snapToChildIfNeeded(parent, layout);
        }

        int getMaxDragOffset(T view) {
            return -view.getDownNestedScrollRange();
        }

        int getScrollRangeForDragFling(T view) {
            return view.getTotalScrollRange();
        }

        int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, T appBarLayout, int newOffset, int minOffset, int maxOffset) {
            int curOffset = getTopBottomOffsetForScrollingSibling();
            int consumed = 0;
            if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
                int newOffset2 = android.support.p000v4.math.MathUtils.clamp(newOffset, minOffset, maxOffset);
                if (curOffset != newOffset2) {
                    int interpolatedOffset = appBarLayout.hasChildWithInterpolator() ? interpolateOffset(appBarLayout, newOffset2) : newOffset2;
                    boolean offsetChanged = setTopAndBottomOffset(interpolatedOffset);
                    consumed = curOffset - newOffset2;
                    this.offsetDelta = newOffset2 - interpolatedOffset;
                    if (!offsetChanged && appBarLayout.hasChildWithInterpolator()) {
                        coordinatorLayout.dispatchDependentViewsChanged(appBarLayout);
                    }
                    appBarLayout.dispatchOffsetUpdates(getTopAndBottomOffset());
                    updateAppBarLayoutDrawableState(coordinatorLayout, appBarLayout, newOffset2, newOffset2 < curOffset ? -1 : 1, false);
                }
            } else {
                this.offsetDelta = 0;
            }
            return consumed;
        }

        @VisibleForTesting
        boolean isOffsetAnimatorRunning() {
            ValueAnimator valueAnimator = this.offsetAnimator;
            return valueAnimator != null && valueAnimator.isRunning();
        }

        private int interpolateOffset(T layout, int offset) {
            int absOffset = Math.abs(offset);
            int i = 0;
            int z = layout.getChildCount();
            while (true) {
                if (i >= z) {
                    break;
                }
                View child = layout.getChildAt(i);
                LayoutParams childLp = (LayoutParams) child.getLayoutParams();
                Interpolator interpolator = childLp.getScrollInterpolator();
                if (absOffset < child.getTop() || absOffset > child.getBottom()) {
                    i++;
                } else if (interpolator != null) {
                    int childScrollableHeight = 0;
                    int flags = childLp.getScrollFlags();
                    if ((flags & 1) != 0) {
                        childScrollableHeight = 0 + child.getHeight() + childLp.topMargin + childLp.bottomMargin;
                        if ((flags & 2) != 0) {
                            childScrollableHeight -= ViewCompat.getMinimumHeight(child);
                        }
                    }
                    if (ViewCompat.getFitsSystemWindows(child)) {
                        childScrollableHeight -= layout.getTopInset();
                    }
                    if (childScrollableHeight > 0) {
                        int offsetForView = absOffset - child.getTop();
                        int interpolatedDiff = Math.round(childScrollableHeight * interpolator.getInterpolation(offsetForView / childScrollableHeight));
                        return Integer.signum(offset) * (child.getTop() + interpolatedDiff);
                    }
                }
            }
            return offset;
        }

        private void updateAppBarLayoutDrawableState(CoordinatorLayout parent, T layout, int offset, int direction, boolean forceJump) {
            View scrollingChild;
            View child = getAppBarChildOnOffset(layout, offset);
            if (child != null) {
                LayoutParams childLp = (LayoutParams) child.getLayoutParams();
                int flags = childLp.getScrollFlags();
                boolean lifted = false;
                boolean z = false;
                if ((flags & 1) != 0) {
                    int minHeight = ViewCompat.getMinimumHeight(child);
                    if (direction > 0 && (flags & 12) != 0) {
                        lifted = (-offset) >= (child.getBottom() - minHeight) - layout.getTopInset();
                    } else if ((flags & 2) != 0) {
                        lifted = (-offset) >= (child.getBottom() - minHeight) - layout.getTopInset();
                    }
                }
                if (layout.isLiftOnScroll() && (scrollingChild = findFirstScrollingChild(parent)) != null) {
                    if (scrollingChild.getScrollY() > 0) {
                        z = true;
                    }
                    lifted = z;
                }
                boolean changed = layout.setLiftedState(lifted);
                if (Build.VERSION.SDK_INT >= 11) {
                    if (forceJump || (changed && shouldJumpElevationState(parent, layout))) {
                        layout.jumpDrawablesToCurrentState();
                    }
                }
            }
        }

        private boolean shouldJumpElevationState(CoordinatorLayout parent, T layout) {
            List<View> dependencies = parent.getDependents(layout);
            int size = dependencies.size();
            for (int i = 0; i < size; i++) {
                View dependency = dependencies.get(i);
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
                CoordinatorLayout.Behavior behavior = lp.getBehavior();
                if (behavior instanceof ScrollingViewBehavior) {
                    return ((ScrollingViewBehavior) behavior).getOverlayTop() != 0;
                }
            }
            return false;
        }

        private static View getAppBarChildOnOffset(AppBarLayout layout, int offset) {
            int absOffset = Math.abs(offset);
            int z = layout.getChildCount();
            for (int i = 0; i < z; i++) {
                View child = layout.getChildAt(i);
                if (absOffset >= child.getTop() && absOffset <= child.getBottom()) {
                    return child;
                }
            }
            return null;
        }

        @Nullable
        private View findFirstScrollingChild(CoordinatorLayout parent) {
            int z = parent.getChildCount();
            for (int i = 0; i < z; i++) {
                View child = parent.getChildAt(i);
                if (child instanceof NestedScrollingChild) {
                    return child;
                }
            }
            return null;
        }

        @Override // android.support.design.widget.HeaderBehavior
        int getTopBottomOffsetForScrollingSibling() {
            return getTopAndBottomOffset() + this.offsetDelta;
        }

        public Parcelable onSaveInstanceState(CoordinatorLayout parent, T abl) {
            Parcelable superState = super.onSaveInstanceState(parent, (CoordinatorLayout) abl);
            int offset = getTopAndBottomOffset();
            int count = abl.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = abl.getChildAt(i);
                int visBottom = child.getBottom() + offset;
                if (child.getTop() + offset <= 0 && visBottom >= 0) {
                    SavedState ss = new SavedState(superState);
                    ss.firstVisibleChildIndex = i;
                    ss.firstVisibleChildAtMinimumHeight = visBottom == ViewCompat.getMinimumHeight(child) + abl.getTopInset();
                    ss.firstVisibleChildPercentageShown = visBottom / child.getHeight();
                    return ss;
                }
            }
            return superState;
        }

        public void onRestoreInstanceState(CoordinatorLayout parent, T appBarLayout, Parcelable state) {
            if (state instanceof SavedState) {
                SavedState ss = (SavedState) state;
                super.onRestoreInstanceState(parent, (CoordinatorLayout) appBarLayout, ss.getSuperState());
                this.offsetToChildIndexOnLayout = ss.firstVisibleChildIndex;
                this.offsetToChildIndexOnLayoutPerc = ss.firstVisibleChildPercentageShown;
                this.offsetToChildIndexOnLayoutIsMinHeight = ss.firstVisibleChildAtMinimumHeight;
                return;
            }
            super.onRestoreInstanceState(parent, (CoordinatorLayout) appBarLayout, state);
            this.offsetToChildIndexOnLayout = -1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: classes.dex */
        public static class SavedState extends AbsSavedState {
            public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.design.widget.AppBarLayout.BaseBehavior.SavedState.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.os.Parcelable.ClassLoaderCreator
                /* renamed from: createFromParcel */
                public SavedState mo102createFromParcel(Parcel source, ClassLoader loader) {
                    return new SavedState(source, loader);
                }

                @Override // android.os.Parcelable.Creator
                /* renamed from: createFromParcel */
                public SavedState mo101createFromParcel(Parcel source) {
                    return new SavedState(source, null);
                }

                @Override // android.os.Parcelable.Creator
                /* renamed from: newArray */
                public SavedState[] mo103newArray(int size) {
                    return new SavedState[size];
                }
            };
            boolean firstVisibleChildAtMinimumHeight;
            int firstVisibleChildIndex;
            float firstVisibleChildPercentageShown;

            public SavedState(Parcel source, ClassLoader loader) {
                super(source, loader);
                this.firstVisibleChildIndex = source.readInt();
                this.firstVisibleChildPercentageShown = source.readFloat();
                this.firstVisibleChildAtMinimumHeight = source.readByte() != 0;
            }

            public SavedState(Parcelable superState) {
                super(superState);
            }

            @Override // android.support.p000v4.view.AbsSavedState, android.os.Parcelable
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(this.firstVisibleChildIndex);
                dest.writeFloat(this.firstVisibleChildPercentageShown);
                dest.writeByte(this.firstVisibleChildAtMinimumHeight ? (byte) 1 : (byte) 0);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ScrollingViewBehavior extends HeaderScrollingViewBehavior {
        @Override // android.support.design.widget.HeaderScrollingViewBehavior
        /* renamed from: findFirstDependency  reason: collision with other method in class */
        /* bridge */ /* synthetic */ View mo104findFirstDependency(List list) {
            return mo104findFirstDependency((List<View>) list);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ int getLeftAndRightOffset() {
            return super.getLeftAndRightOffset();
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        @Override // android.support.design.widget.ViewOffsetBehavior, android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
            return super.onLayoutChild(coordinatorLayout, view, i);
        }

        @Override // android.support.design.widget.HeaderScrollingViewBehavior, android.support.design.widget.CoordinatorLayout.Behavior
        public /* bridge */ /* synthetic */ boolean onMeasureChild(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3, int i4) {
            return super.onMeasureChild(coordinatorLayout, view, i, i2, i3, i4);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ boolean setLeftAndRightOffset(int i) {
            return super.setLeftAndRightOffset(i);
        }

        @Override // android.support.design.widget.ViewOffsetBehavior
        public /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public ScrollingViewBehavior() {
        }

        public ScrollingViewBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, C0091R.styleable.ScrollingViewBehavior_Layout);
            setOverlayTop(a.getDimensionPixelSize(C0091R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
            a.recycle();
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return dependency instanceof AppBarLayout;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            offsetChildAsNeeded(child, dependency);
            updateLiftedStateIfNeeded(child, dependency);
            return false;
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onRequestChildRectangleOnScreen(CoordinatorLayout parent, View child, Rect rectangle, boolean immediate) {
            AppBarLayout header = mo104findFirstDependency(parent.getDependencies(child));
            if (header != null) {
                rectangle.offset(child.getLeft(), child.getTop());
                Rect parentRect = this.tempRect1;
                parentRect.set(0, 0, parent.getWidth(), parent.getHeight());
                if (!parentRect.contains(rectangle)) {
                    header.setExpanded(false, !immediate);
                    return true;
                }
            }
            return false;
        }

        private void offsetChildAsNeeded(View child, View dependency) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
            if (behavior instanceof BaseBehavior) {
                BaseBehavior ablBehavior = (BaseBehavior) behavior;
                ViewCompat.offsetTopAndBottom(child, (((dependency.getBottom() - child.getTop()) + ablBehavior.offsetDelta) + getVerticalLayoutGap()) - getOverlapPixelsForOffset(dependency));
            }
        }

        @Override // android.support.design.widget.HeaderScrollingViewBehavior
        float getOverlapRatioForOffset(View header) {
            int availScrollRange;
            if (header instanceof AppBarLayout) {
                AppBarLayout abl = (AppBarLayout) header;
                int totalScrollRange = abl.getTotalScrollRange();
                int preScrollDown = abl.getDownNestedPreScrollRange();
                int offset = getAppBarLayoutOffset(abl);
                if ((preScrollDown == 0 || totalScrollRange + offset > preScrollDown) && (availScrollRange = totalScrollRange - preScrollDown) != 0) {
                    return (offset / availScrollRange) + 1.0f;
                }
            }
            return 0.0f;
        }

        private static int getAppBarLayoutOffset(AppBarLayout abl) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) abl.getLayoutParams()).getBehavior();
            if (behavior instanceof BaseBehavior) {
                return ((BaseBehavior) behavior).getTopBottomOffsetForScrollingSibling();
            }
            return 0;
        }

        @Override // android.support.design.widget.HeaderScrollingViewBehavior
        /* renamed from: findFirstDependency */
        AppBarLayout mo104findFirstDependency(List<View> views) {
            int z = views.size();
            for (int i = 0; i < z; i++) {
                View view = views.get(i);
                if (view instanceof AppBarLayout) {
                    return (AppBarLayout) view;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.support.design.widget.HeaderScrollingViewBehavior
        public int getScrollRange(View v) {
            if (v instanceof AppBarLayout) {
                return ((AppBarLayout) v).getTotalScrollRange();
            }
            return super.getScrollRange(v);
        }

        private void updateLiftedStateIfNeeded(View child, View dependency) {
            if (dependency instanceof AppBarLayout) {
                AppBarLayout appBarLayout = (AppBarLayout) dependency;
                if (appBarLayout.isLiftOnScroll()) {
                    appBarLayout.setLiftedState(child.getScrollY() > 0);
                }
            }
        }
    }
}