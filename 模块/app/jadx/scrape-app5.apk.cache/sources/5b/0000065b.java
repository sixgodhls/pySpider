package android.support.p000v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AbstractC0065Px;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.view.AbsSavedState;
import android.support.p000v4.view.AccessibilityDelegateCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.p000v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;

/* renamed from: android.support.v4.widget.SlidingPaneLayout */
/* loaded from: classes.dex */
public class SlidingPaneLayout extends ViewGroup {
    private static final int DEFAULT_FADE_COLOR = -858993460;
    private static final int DEFAULT_OVERHANG_SIZE = 32;
    private static final int MIN_FLING_VELOCITY = 400;
    private static final String TAG = "SlidingPaneLayout";
    private boolean mCanSlide;
    private int mCoveredFadeColor;
    private boolean mDisplayListReflectionLoaded;
    final ViewDragHelper mDragHelper;
    private boolean mFirstLayout;
    private Method mGetDisplayList;
    private float mInitialMotionX;
    private float mInitialMotionY;
    boolean mIsUnableToDrag;
    private final int mOverhangSize;
    private PanelSlideListener mPanelSlideListener;
    private int mParallaxBy;
    private float mParallaxOffset;
    final ArrayList<DisableLayerRunnable> mPostedRunnables;
    boolean mPreservedOpenState;
    private Field mRecreateDisplayList;
    private Drawable mShadowDrawableLeft;
    private Drawable mShadowDrawableRight;
    float mSlideOffset;
    int mSlideRange;
    View mSlideableView;
    private int mSliderFadeColor;
    private final Rect mTmpRect;

    /* renamed from: android.support.v4.widget.SlidingPaneLayout$PanelSlideListener */
    /* loaded from: classes.dex */
    public interface PanelSlideListener {
        void onPanelClosed(@NonNull View view);

        void onPanelOpened(@NonNull View view);

        void onPanelSlide(@NonNull View view, float f);
    }

    /* renamed from: android.support.v4.widget.SlidingPaneLayout$SimplePanelSlideListener */
    /* loaded from: classes.dex */
    public static class SimplePanelSlideListener implements PanelSlideListener {
        @Override // android.support.p000v4.widget.SlidingPaneLayout.PanelSlideListener
        public void onPanelSlide(View panel, float slideOffset) {
        }

        @Override // android.support.p000v4.widget.SlidingPaneLayout.PanelSlideListener
        public void onPanelOpened(View panel) {
        }

        @Override // android.support.p000v4.widget.SlidingPaneLayout.PanelSlideListener
        public void onPanelClosed(View panel) {
        }
    }

    public SlidingPaneLayout(@NonNull Context context) {
        this(context, null);
    }

    public SlidingPaneLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingPaneLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSliderFadeColor = DEFAULT_FADE_COLOR;
        this.mFirstLayout = true;
        this.mTmpRect = new Rect();
        this.mPostedRunnables = new ArrayList<>();
        float density = context.getResources().getDisplayMetrics().density;
        this.mOverhangSize = (int) ((32.0f * density) + 0.5f);
        setWillNotDraw(false);
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
        ViewCompat.setImportantForAccessibility(this, 1);
        this.mDragHelper = ViewDragHelper.create(this, 0.5f, new DragHelperCallback());
        this.mDragHelper.setMinVelocity(400.0f * density);
    }

    public void setParallaxDistance(@AbstractC0065Px int parallaxBy) {
        this.mParallaxBy = parallaxBy;
        requestLayout();
    }

    @AbstractC0065Px
    public int getParallaxDistance() {
        return this.mParallaxBy;
    }

    public void setSliderFadeColor(@ColorInt int color) {
        this.mSliderFadeColor = color;
    }

    @ColorInt
    public int getSliderFadeColor() {
        return this.mSliderFadeColor;
    }

    public void setCoveredFadeColor(@ColorInt int color) {
        this.mCoveredFadeColor = color;
    }

    @ColorInt
    public int getCoveredFadeColor() {
        return this.mCoveredFadeColor;
    }

    public void setPanelSlideListener(@Nullable PanelSlideListener listener) {
        this.mPanelSlideListener = listener;
    }

    void dispatchOnPanelSlide(View panel) {
        PanelSlideListener panelSlideListener = this.mPanelSlideListener;
        if (panelSlideListener != null) {
            panelSlideListener.onPanelSlide(panel, this.mSlideOffset);
        }
    }

    void dispatchOnPanelOpened(View panel) {
        PanelSlideListener panelSlideListener = this.mPanelSlideListener;
        if (panelSlideListener != null) {
            panelSlideListener.onPanelOpened(panel);
        }
        sendAccessibilityEvent(32);
    }

    void dispatchOnPanelClosed(View panel) {
        PanelSlideListener panelSlideListener = this.mPanelSlideListener;
        if (panelSlideListener != null) {
            panelSlideListener.onPanelClosed(panel);
        }
        sendAccessibilityEvent(32);
    }

    void updateObscuredViewsVisibility(View panel) {
        int left;
        int bottom;
        int top;
        int right;
        boolean isLayoutRtl;
        int clampedChildRight;
        View view = panel;
        boolean isLayoutRtl2 = isLayoutRtlSupport();
        int startBound = isLayoutRtl2 ? getWidth() - getPaddingRight() : getPaddingLeft();
        int endBound = isLayoutRtl2 ? getPaddingLeft() : getWidth() - getPaddingRight();
        int topBound = getPaddingTop();
        int bottomBound = getHeight() - getPaddingBottom();
        if (view != null && viewIsOpaque(panel)) {
            left = panel.getLeft();
            right = panel.getRight();
            top = panel.getTop();
            bottom = panel.getBottom();
        } else {
            left = 0;
            bottom = 0;
            top = 0;
            right = 0;
        }
        int i = 0;
        int childCount = getChildCount();
        while (i < childCount) {
            View child = getChildAt(i);
            if (child != view) {
                if (child.getVisibility() == 8) {
                    isLayoutRtl = isLayoutRtl2;
                } else {
                    int clampedChildLeft = Math.max(isLayoutRtl2 ? endBound : startBound, child.getLeft());
                    int clampedChildTop = Math.max(topBound, child.getTop());
                    isLayoutRtl = isLayoutRtl2;
                    int clampedChildRight2 = Math.min(isLayoutRtl2 ? startBound : endBound, child.getRight());
                    int clampedChildBottom = Math.min(bottomBound, child.getBottom());
                    if (clampedChildLeft >= left && clampedChildTop >= top && clampedChildRight2 <= right && clampedChildBottom <= bottom) {
                        clampedChildRight = 4;
                    } else {
                        clampedChildRight = 0;
                    }
                    child.setVisibility(clampedChildRight);
                }
                i++;
                isLayoutRtl2 = isLayoutRtl;
                view = panel;
            } else {
                return;
            }
        }
    }

    void setAllChildrenVisible() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 4) {
                child.setVisibility(0);
            }
        }
    }

    private static boolean viewIsOpaque(View v) {
        Drawable bg;
        if (v.isOpaque()) {
            return true;
        }
        return Build.VERSION.SDK_INT < 18 && (bg = v.getBackground()) != null && bg.getOpacity() == -1;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
        int count = this.mPostedRunnables.size();
        for (int i = 0; i < count; i++) {
            DisableLayerRunnable dlr = this.mPostedRunnables.get(i);
            dlr.run();
        }
        this.mPostedRunnables.clear();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int childCount;
        int heightMode;
        int fixedPanelWidthLimit;
        int maxLayoutHeight;
        int childHeightSpec;
        int i2;
        int childHeightSpec2;
        int heightSize;
        int childWidthSpec;
        int childHeightSpec3;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != 1073741824) {
            if (isInEditMode()) {
                if (widthMode == Integer.MIN_VALUE) {
                    widthMode = MemoryConstants.f216GB;
                } else if (widthMode == 0) {
                    widthMode = MemoryConstants.f216GB;
                    widthSize = 300;
                }
            } else {
                throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
            }
        } else if (heightMode2 == 0) {
            if (isInEditMode()) {
                if (heightMode2 == 0) {
                    heightMode2 = Integer.MIN_VALUE;
                    heightSize2 = 300;
                }
            } else {
                throw new IllegalStateException("Height must not be UNSPECIFIED");
            }
        }
        int layoutHeight = 0;
        int maxLayoutHeight2 = 0;
        if (heightMode2 == Integer.MIN_VALUE) {
            maxLayoutHeight2 = (heightSize2 - getPaddingTop()) - getPaddingBottom();
        } else if (heightMode2 == 1073741824) {
            int paddingTop = (heightSize2 - getPaddingTop()) - getPaddingBottom();
            maxLayoutHeight2 = paddingTop;
            layoutHeight = paddingTop;
        }
        float weightSum = 0.0f;
        boolean canSlide = false;
        int widthAvailable = (widthSize - getPaddingLeft()) - getPaddingRight();
        int widthRemaining = widthAvailable;
        int childCount2 = getChildCount();
        if (childCount2 > 2) {
            Log.e(TAG, "onMeasure: More than two child views are not supported.");
        }
        this.mSlideableView = null;
        int i3 = 0;
        while (true) {
            i = 8;
            if (i3 >= childCount2) {
                break;
            }
            View child = getChildAt(i3);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int widthMode2 = widthMode;
            if (child.getVisibility() == 8) {
                lp.dimWhenOffset = false;
                heightSize = heightSize2;
            } else {
                if (lp.weight > 0.0f) {
                    weightSum += lp.weight;
                    if (lp.width == 0) {
                        heightSize = heightSize2;
                    }
                }
                int horizontalMargin = lp.leftMargin + lp.rightMargin;
                heightSize = heightSize2;
                if (lp.width == -2) {
                    childWidthSpec = View.MeasureSpec.makeMeasureSpec(widthAvailable - horizontalMargin, Integer.MIN_VALUE);
                } else if (lp.width == -1) {
                    childWidthSpec = View.MeasureSpec.makeMeasureSpec(widthAvailable - horizontalMargin, MemoryConstants.f216GB);
                } else {
                    childWidthSpec = View.MeasureSpec.makeMeasureSpec(lp.width, MemoryConstants.f216GB);
                }
                if (lp.height == -2) {
                    childHeightSpec3 = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight2, Integer.MIN_VALUE);
                } else if (lp.height == -1) {
                    childHeightSpec3 = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight2, MemoryConstants.f216GB);
                } else {
                    childHeightSpec3 = View.MeasureSpec.makeMeasureSpec(lp.height, MemoryConstants.f216GB);
                }
                child.measure(childWidthSpec, childHeightSpec3);
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                float weightSum2 = weightSum;
                if (heightMode2 == Integer.MIN_VALUE && childHeight > layoutHeight) {
                    layoutHeight = Math.min(childHeight, maxLayoutHeight2);
                }
                widthRemaining -= childWidth;
                boolean z = widthRemaining < 0;
                lp.slideable = z;
                boolean canSlide2 = z | canSlide;
                boolean canSlide3 = lp.slideable;
                if (canSlide3) {
                    this.mSlideableView = child;
                }
                canSlide = canSlide2;
                weightSum = weightSum2;
            }
            i3++;
            widthMode = widthMode2;
            heightSize2 = heightSize;
        }
        if (canSlide || weightSum > 0.0f) {
            int fixedPanelWidthLimit2 = widthAvailable - this.mOverhangSize;
            int i4 = 0;
            while (i4 < childCount2) {
                View child2 = getChildAt(i4);
                if (child2.getVisibility() == i) {
                    fixedPanelWidthLimit = fixedPanelWidthLimit2;
                    heightMode = heightMode2;
                    maxLayoutHeight = maxLayoutHeight2;
                    childCount = childCount2;
                } else {
                    LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                    if (child2.getVisibility() == i) {
                        fixedPanelWidthLimit = fixedPanelWidthLimit2;
                        heightMode = heightMode2;
                        maxLayoutHeight = maxLayoutHeight2;
                        childCount = childCount2;
                    } else {
                        boolean skippedFirstPass = lp2.width == 0 && lp2.weight > 0.0f;
                        int measuredWidth = skippedFirstPass ? 0 : child2.getMeasuredWidth();
                        if (!canSlide || child2 == this.mSlideableView) {
                            childCount = childCount2;
                            heightMode = heightMode2;
                            if (lp2.weight <= 0.0f) {
                                fixedPanelWidthLimit = fixedPanelWidthLimit2;
                                maxLayoutHeight = maxLayoutHeight2;
                            } else {
                                if (lp2.width == 0) {
                                    if (lp2.height == -2) {
                                        childHeightSpec = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight2, Integer.MIN_VALUE);
                                    } else if (lp2.height == -1) {
                                        childHeightSpec = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight2, MemoryConstants.f216GB);
                                    } else {
                                        childHeightSpec = View.MeasureSpec.makeMeasureSpec(lp2.height, MemoryConstants.f216GB);
                                    }
                                } else {
                                    childHeightSpec = View.MeasureSpec.makeMeasureSpec(child2.getMeasuredHeight(), MemoryConstants.f216GB);
                                }
                                if (canSlide) {
                                    int newWidth = widthAvailable - (lp2.leftMargin + lp2.rightMargin);
                                    fixedPanelWidthLimit = fixedPanelWidthLimit2;
                                    maxLayoutHeight = maxLayoutHeight2;
                                    int childWidthSpec2 = View.MeasureSpec.makeMeasureSpec(newWidth, MemoryConstants.f216GB);
                                    if (measuredWidth != newWidth) {
                                        child2.measure(childWidthSpec2, childHeightSpec);
                                    }
                                } else {
                                    fixedPanelWidthLimit = fixedPanelWidthLimit2;
                                    maxLayoutHeight = maxLayoutHeight2;
                                    int widthToDistribute = Math.max(0, widthRemaining);
                                    int addedWidth = (int) ((lp2.weight * widthToDistribute) / weightSum);
                                    int childWidthSpec3 = View.MeasureSpec.makeMeasureSpec(measuredWidth + addedWidth, MemoryConstants.f216GB);
                                    child2.measure(childWidthSpec3, childHeightSpec);
                                }
                            }
                        } else if (lp2.width >= 0) {
                            childCount = childCount2;
                            heightMode = heightMode2;
                            fixedPanelWidthLimit = fixedPanelWidthLimit2;
                            maxLayoutHeight = maxLayoutHeight2;
                        } else {
                            if (measuredWidth <= fixedPanelWidthLimit2) {
                                heightMode = heightMode2;
                                if (lp2.weight <= 0.0f) {
                                    fixedPanelWidthLimit = fixedPanelWidthLimit2;
                                    maxLayoutHeight = maxLayoutHeight2;
                                    childCount = childCount2;
                                }
                            } else {
                                heightMode = heightMode2;
                            }
                            if (skippedFirstPass) {
                                childCount = childCount2;
                                if (lp2.height == -2) {
                                    childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight2, Integer.MIN_VALUE);
                                    i2 = MemoryConstants.f216GB;
                                } else if (lp2.height == -1) {
                                    i2 = MemoryConstants.f216GB;
                                    childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(maxLayoutHeight2, MemoryConstants.f216GB);
                                } else {
                                    i2 = MemoryConstants.f216GB;
                                    childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(lp2.height, MemoryConstants.f216GB);
                                }
                            } else {
                                childCount = childCount2;
                                i2 = MemoryConstants.f216GB;
                                childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(child2.getMeasuredHeight(), MemoryConstants.f216GB);
                            }
                            int childWidthSpec4 = View.MeasureSpec.makeMeasureSpec(fixedPanelWidthLimit2, i2);
                            child2.measure(childWidthSpec4, childHeightSpec2);
                            fixedPanelWidthLimit = fixedPanelWidthLimit2;
                            maxLayoutHeight = maxLayoutHeight2;
                        }
                    }
                }
                i4++;
                heightMode2 = heightMode;
                childCount2 = childCount;
                fixedPanelWidthLimit2 = fixedPanelWidthLimit;
                maxLayoutHeight2 = maxLayoutHeight;
                i = 8;
            }
        }
        int fixedPanelWidthLimit3 = widthSize;
        int measuredHeight = getPaddingTop() + layoutHeight + getPaddingBottom();
        setMeasuredDimension(fixedPanelWidthLimit3, measuredHeight);
        this.mCanSlide = canSlide;
        if (this.mDragHelper.getViewDragState() != 0 && !canSlide) {
            this.mDragHelper.abort();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingStart;
        int i;
        int childLeft;
        int childRight;
        boolean isLayoutRtl = isLayoutRtlSupport();
        if (!isLayoutRtl) {
            this.mDragHelper.setEdgeTrackingEnabled(1);
        } else {
            this.mDragHelper.setEdgeTrackingEnabled(2);
        }
        int width = r - l;
        int paddingStart2 = isLayoutRtl ? getPaddingRight() : getPaddingLeft();
        int paddingEnd = isLayoutRtl ? getPaddingLeft() : getPaddingRight();
        int paddingTop = getPaddingTop();
        int childCount = getChildCount();
        int xStart = paddingStart2;
        int nextXStart = xStart;
        if (this.mFirstLayout) {
            this.mSlideOffset = (!this.mCanSlide || !this.mPreservedOpenState) ? 0.0f : 1.0f;
        }
        int i2 = 0;
        while (i2 < childCount) {
            View child = getChildAt(i2);
            if (child.getVisibility() == 8) {
                paddingStart = paddingStart2;
            } else {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth();
                int offset = 0;
                if (!lp.slideable) {
                    paddingStart = paddingStart2;
                    if (this.mCanSlide && (i = this.mParallaxBy) != 0) {
                        xStart = nextXStart;
                        offset = (int) ((1.0f - this.mSlideOffset) * i);
                    } else {
                        xStart = nextXStart;
                    }
                } else {
                    int margin = lp.leftMargin + lp.rightMargin;
                    int range = (Math.min(nextXStart, (width - paddingEnd) - this.mOverhangSize) - xStart) - margin;
                    this.mSlideRange = range;
                    int lpMargin = isLayoutRtl ? lp.rightMargin : lp.leftMargin;
                    paddingStart = paddingStart2;
                    int margin2 = width - paddingEnd;
                    lp.dimWhenOffset = ((xStart + lpMargin) + range) + (childWidth / 2) > margin2;
                    int pos = (int) (range * this.mSlideOffset);
                    xStart += pos + lpMargin;
                    this.mSlideOffset = pos / this.mSlideRange;
                }
                if (isLayoutRtl) {
                    childRight = (width - xStart) + offset;
                    childLeft = childRight - childWidth;
                } else {
                    childLeft = xStart - offset;
                    childRight = childLeft + childWidth;
                }
                int childBottom = child.getMeasuredHeight() + paddingTop;
                child.layout(childLeft, paddingTop, childRight, childBottom);
                nextXStart += child.getWidth();
            }
            i2++;
            paddingStart2 = paddingStart;
        }
        if (this.mFirstLayout) {
            if (this.mCanSlide) {
                if (this.mParallaxBy != 0) {
                    parallaxOtherViews(this.mSlideOffset);
                }
                if (((LayoutParams) this.mSlideableView.getLayoutParams()).dimWhenOffset) {
                    dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
                }
            } else {
                for (int i3 = 0; i3 < childCount; i3++) {
                    dimChildView(getChildAt(i3), 0.0f, this.mSliderFadeColor);
                }
            }
            updateObscuredViewsVisibility(this.mSlideableView);
        }
        this.mFirstLayout = false;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw) {
            this.mFirstLayout = true;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (!isInTouchMode() && !this.mCanSlide) {
            this.mPreservedOpenState = child == this.mSlideableView;
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        View secondChild;
        int action = ev.getActionMasked();
        if (!this.mCanSlide && action == 0 && getChildCount() > 1 && (secondChild = getChildAt(1)) != null) {
            this.mPreservedOpenState = !this.mDragHelper.isViewUnder(secondChild, (int) ev.getX(), (int) ev.getY());
        }
        if (!this.mCanSlide || (this.mIsUnableToDrag && action != 0)) {
            this.mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        } else if (action == 3 || action == 1) {
            this.mDragHelper.cancel();
            return false;
        } else {
            boolean interceptTap = false;
            if (action == 0) {
                this.mIsUnableToDrag = false;
                float x = ev.getX();
                float y = ev.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                if (this.mDragHelper.isViewUnder(this.mSlideableView, (int) x, (int) y) && isDimmed(this.mSlideableView)) {
                    interceptTap = true;
                }
            } else if (action == 2) {
                float x2 = ev.getX();
                float y2 = ev.getY();
                float adx = Math.abs(x2 - this.mInitialMotionX);
                float ady = Math.abs(y2 - this.mInitialMotionY);
                int slop = this.mDragHelper.getTouchSlop();
                if (adx > slop && ady > adx) {
                    this.mDragHelper.cancel();
                    this.mIsUnableToDrag = true;
                    return false;
                }
            }
            boolean interceptForDrag = this.mDragHelper.shouldInterceptTouchEvent(ev);
            return interceptForDrag || interceptTap;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.mCanSlide) {
            return super.onTouchEvent(ev);
        }
        this.mDragHelper.processTouchEvent(ev);
        switch (ev.getActionMasked()) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                break;
            case 1:
                if (isDimmed(this.mSlideableView)) {
                    float x2 = ev.getX();
                    float y2 = ev.getY();
                    float dx = x2 - this.mInitialMotionX;
                    float dy = y2 - this.mInitialMotionY;
                    int slop = this.mDragHelper.getTouchSlop();
                    if ((dx * dx) + (dy * dy) < slop * slop && this.mDragHelper.isViewUnder(this.mSlideableView, (int) x2, (int) y2)) {
                        closePane(this.mSlideableView, 0);
                        break;
                    }
                }
                break;
        }
        return true;
    }

    private boolean closePane(View pane, int initialVelocity) {
        if (this.mFirstLayout || smoothSlideTo(0.0f, initialVelocity)) {
            this.mPreservedOpenState = false;
            return true;
        }
        return false;
    }

    private boolean openPane(View pane, int initialVelocity) {
        if (this.mFirstLayout || smoothSlideTo(1.0f, initialVelocity)) {
            this.mPreservedOpenState = true;
            return true;
        }
        return false;
    }

    @Deprecated
    public void smoothSlideOpen() {
        openPane();
    }

    public boolean openPane() {
        return openPane(this.mSlideableView, 0);
    }

    @Deprecated
    public void smoothSlideClosed() {
        closePane();
    }

    public boolean closePane() {
        return closePane(this.mSlideableView, 0);
    }

    public boolean isOpen() {
        return !this.mCanSlide || this.mSlideOffset == 1.0f;
    }

    @Deprecated
    public boolean canSlide() {
        return this.mCanSlide;
    }

    public boolean isSlideable() {
        return this.mCanSlide;
    }

    void onPanelDragged(int newLeft) {
        if (this.mSlideableView == null) {
            this.mSlideOffset = 0.0f;
            return;
        }
        boolean isLayoutRtl = isLayoutRtlSupport();
        LayoutParams lp = (LayoutParams) this.mSlideableView.getLayoutParams();
        int childWidth = this.mSlideableView.getWidth();
        int newStart = isLayoutRtl ? (getWidth() - newLeft) - childWidth : newLeft;
        int paddingStart = isLayoutRtl ? getPaddingRight() : getPaddingLeft();
        int lpMargin = isLayoutRtl ? lp.rightMargin : lp.leftMargin;
        int startBound = paddingStart + lpMargin;
        this.mSlideOffset = (newStart - startBound) / this.mSlideRange;
        if (this.mParallaxBy != 0) {
            parallaxOtherViews(this.mSlideOffset);
        }
        if (lp.dimWhenOffset) {
            dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
        }
        dispatchOnPanelSlide(this.mSlideableView);
    }

    private void dimChildView(View v, float mag, int fadeColor) {
        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        if (mag > 0.0f && fadeColor != 0) {
            int baseAlpha = ((-16777216) & fadeColor) >>> 24;
            int imag = (int) (baseAlpha * mag);
            int color = (imag << 24) | (16777215 & fadeColor);
            if (lp.dimPaint == null) {
                lp.dimPaint = new Paint();
            }
            lp.dimPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER));
            if (v.getLayerType() != 2) {
                v.setLayerType(2, lp.dimPaint);
            }
            invalidateChildRegion(v);
        } else if (v.getLayerType() != 0) {
            if (lp.dimPaint != null) {
                lp.dimPaint.setColorFilter(null);
            }
            DisableLayerRunnable dlr = new DisableLayerRunnable(v);
            this.mPostedRunnables.add(dlr);
            ViewCompat.postOnAnimation(this, dlr);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int save = canvas.save();
        if (this.mCanSlide && !lp.slideable && this.mSlideableView != null) {
            canvas.getClipBounds(this.mTmpRect);
            if (isLayoutRtlSupport()) {
                Rect rect = this.mTmpRect;
                rect.left = Math.max(rect.left, this.mSlideableView.getRight());
            } else {
                Rect rect2 = this.mTmpRect;
                rect2.right = Math.min(rect2.right, this.mSlideableView.getLeft());
            }
            canvas.clipRect(this.mTmpRect);
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(save);
        return result;
    }

    void invalidateChildRegion(View v) {
        Field field;
        if (Build.VERSION.SDK_INT >= 17) {
            ViewCompat.setLayerPaint(v, ((LayoutParams) v.getLayoutParams()).dimPaint);
            return;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            if (!this.mDisplayListReflectionLoaded) {
                try {
                    this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", null);
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, "Couldn't fetch getDisplayList method; dimming won't work right.", e);
                }
                try {
                    this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
                    this.mRecreateDisplayList.setAccessible(true);
                } catch (NoSuchFieldException e2) {
                    Log.e(TAG, "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", e2);
                }
                this.mDisplayListReflectionLoaded = true;
            }
            if (this.mGetDisplayList == null || (field = this.mRecreateDisplayList) == null) {
                v.invalidate();
                return;
            }
            try {
                field.setBoolean(v, true);
                this.mGetDisplayList.invoke(v, null);
            } catch (Exception e3) {
                Log.e(TAG, "Error refreshing display list state", e3);
            }
        }
        ViewCompat.postInvalidateOnAnimation(this, v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
    }

    boolean smoothSlideTo(float slideOffset, int velocity) {
        int startBound;
        if (!this.mCanSlide) {
            return false;
        }
        boolean isLayoutRtl = isLayoutRtlSupport();
        LayoutParams lp = (LayoutParams) this.mSlideableView.getLayoutParams();
        if (isLayoutRtl) {
            int startBound2 = getPaddingRight() + lp.rightMargin;
            int childWidth = this.mSlideableView.getWidth();
            startBound = (int) (getWidth() - ((startBound2 + (this.mSlideRange * slideOffset)) + childWidth));
        } else {
            int x = getPaddingLeft();
            int startBound3 = x + lp.leftMargin;
            startBound = (int) (startBound3 + (this.mSlideRange * slideOffset));
        }
        ViewDragHelper viewDragHelper = this.mDragHelper;
        View view = this.mSlideableView;
        if (!viewDragHelper.smoothSlideViewTo(view, startBound, view.getTop())) {
            return false;
        }
        setAllChildrenVisible();
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mDragHelper.continueSettling(true)) {
            if (!this.mCanSlide) {
                this.mDragHelper.abort();
            } else {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    @Deprecated
    public void setShadowDrawable(Drawable d) {
        setShadowDrawableLeft(d);
    }

    public void setShadowDrawableLeft(@Nullable Drawable d) {
        this.mShadowDrawableLeft = d;
    }

    public void setShadowDrawableRight(@Nullable Drawable d) {
        this.mShadowDrawableRight = d;
    }

    @Deprecated
    public void setShadowResource(@DrawableRes int resId) {
        setShadowDrawable(getResources().getDrawable(resId));
    }

    public void setShadowResourceLeft(int resId) {
        setShadowDrawableLeft(ContextCompat.getDrawable(getContext(), resId));
    }

    public void setShadowResourceRight(int resId) {
        setShadowDrawableRight(ContextCompat.getDrawable(getContext(), resId));
    }

    @Override // android.view.View
    public void draw(Canvas c) {
        Drawable shadowDrawable;
        int right;
        int left;
        super.draw(c);
        boolean isLayoutRtl = isLayoutRtlSupport();
        if (isLayoutRtl) {
            shadowDrawable = this.mShadowDrawableRight;
        } else {
            shadowDrawable = this.mShadowDrawableLeft;
        }
        View shadowView = getChildCount() > 1 ? getChildAt(1) : null;
        if (shadowView == null || shadowDrawable == null) {
            return;
        }
        int top = shadowView.getTop();
        int bottom = shadowView.getBottom();
        int shadowWidth = shadowDrawable.getIntrinsicWidth();
        if (isLayoutRtlSupport()) {
            left = shadowView.getRight();
            right = left + shadowWidth;
        } else {
            right = shadowView.getLeft();
            left = right - shadowWidth;
        }
        shadowDrawable.setBounds(left, top, right, bottom);
        shadowDrawable.draw(c);
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0023  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parallaxOtherViews(float r12) {
        /*
            r11 = this;
            boolean r0 = r11.isLayoutRtlSupport()
            android.view.View r1 = r11.mSlideableView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.support.v4.widget.SlidingPaneLayout$LayoutParams r1 = (android.support.p000v4.widget.SlidingPaneLayout.LayoutParams) r1
            boolean r2 = r1.dimWhenOffset
            if (r2 == 0) goto L1b
            if (r0 == 0) goto L15
            int r2 = r1.rightMargin
            goto L17
        L15:
            int r2 = r1.leftMargin
        L17:
            if (r2 > 0) goto L1b
            r2 = 1
            goto L1c
        L1b:
            r2 = 0
        L1c:
            int r3 = r11.getChildCount()
            r4 = 0
        L21:
            if (r4 >= r3) goto L5e
            android.view.View r5 = r11.getChildAt(r4)
            android.view.View r6 = r11.mSlideableView
            if (r5 != r6) goto L2c
            goto L5b
        L2c:
            float r6 = r11.mParallaxOffset
            r7 = 1065353216(0x3f800000, float:1.0)
            float r6 = r7 - r6
            int r8 = r11.mParallaxBy
            float r9 = (float) r8
            float r6 = r6 * r9
            int r6 = (int) r6
            r11.mParallaxOffset = r12
            float r9 = r7 - r12
            float r8 = (float) r8
            float r9 = r9 * r8
            int r8 = (int) r9
            int r9 = r6 - r8
            if (r0 == 0) goto L46
            int r10 = -r9
            goto L47
        L46:
            r10 = r9
        L47:
            r5.offsetLeftAndRight(r10)
            if (r2 == 0) goto L5b
            if (r0 == 0) goto L52
            float r10 = r11.mParallaxOffset
            float r10 = r10 - r7
            goto L56
        L52:
            float r10 = r11.mParallaxOffset
            float r10 = r7 - r10
        L56:
            int r7 = r11.mCoveredFadeColor
            r11.dimChildView(r5, r10, r7)
        L5b:
            int r4 = r4 + 1
            goto L21
        L5e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p000v4.widget.SlidingPaneLayout.parallaxOtherViews(float):void");
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            int scrollX = v.getScrollX();
            int scrollY = v.getScrollY();
            int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() && y + scrollY >= child.getTop() && y + scrollY < child.getBottom() && canScroll(child, true, dx, (x + scrollX) - child.getLeft(), (y + scrollY) - child.getTop())) {
                    return true;
                }
            }
        }
        if (checkV) {
            if (v.canScrollHorizontally(isLayoutRtlSupport() ? dx : -dx)) {
                return true;
            }
        }
        return false;
    }

    boolean isDimmed(View child) {
        if (child == null) {
            return false;
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return this.mCanSlide && lp.dimWhenOffset && this.mSlideOffset > 0.0f;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof ViewGroup.MarginLayoutParams ? new LayoutParams((ViewGroup.MarginLayoutParams) p) : new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return (p instanceof LayoutParams) && super.checkLayoutParams(p);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.isOpen = isSlideable() ? isOpen() : this.mPreservedOpenState;
        return ss;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (ss.isOpen) {
            openPane();
        } else {
            closePane();
        }
        this.mPreservedOpenState = ss.isOpen;
    }

    /* renamed from: android.support.v4.widget.SlidingPaneLayout$DragHelperCallback */
    /* loaded from: classes.dex */
    private class DragHelperCallback extends ViewDragHelper.Callback {
        DragHelperCallback() {
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public boolean tryCaptureView(View child, int pointerId) {
            if (SlidingPaneLayout.this.mIsUnableToDrag) {
                return false;
            }
            return ((LayoutParams) child.getLayoutParams()).slideable;
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public void onViewDragStateChanged(int state) {
            if (SlidingPaneLayout.this.mDragHelper.getViewDragState() == 0) {
                if (SlidingPaneLayout.this.mSlideOffset == 0.0f) {
                    SlidingPaneLayout slidingPaneLayout = SlidingPaneLayout.this;
                    slidingPaneLayout.updateObscuredViewsVisibility(slidingPaneLayout.mSlideableView);
                    SlidingPaneLayout slidingPaneLayout2 = SlidingPaneLayout.this;
                    slidingPaneLayout2.dispatchOnPanelClosed(slidingPaneLayout2.mSlideableView);
                    SlidingPaneLayout.this.mPreservedOpenState = false;
                    return;
                }
                SlidingPaneLayout slidingPaneLayout3 = SlidingPaneLayout.this;
                slidingPaneLayout3.dispatchOnPanelOpened(slidingPaneLayout3.mSlideableView);
                SlidingPaneLayout.this.mPreservedOpenState = true;
            }
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public void onViewCaptured(View capturedChild, int activePointerId) {
            SlidingPaneLayout.this.setAllChildrenVisible();
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            SlidingPaneLayout.this.onPanelDragged(left);
            SlidingPaneLayout.this.invalidate();
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int left;
            LayoutParams lp = (LayoutParams) releasedChild.getLayoutParams();
            if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
                int startToRight = SlidingPaneLayout.this.getPaddingRight() + lp.rightMargin;
                if (xvel < 0.0f || (xvel == 0.0f && SlidingPaneLayout.this.mSlideOffset > 0.5f)) {
                    startToRight += SlidingPaneLayout.this.mSlideRange;
                }
                int childWidth = SlidingPaneLayout.this.mSlideableView.getWidth();
                left = (SlidingPaneLayout.this.getWidth() - startToRight) - childWidth;
            } else {
                int left2 = SlidingPaneLayout.this.getPaddingLeft() + lp.leftMargin;
                if (xvel > 0.0f || (xvel == 0.0f && SlidingPaneLayout.this.mSlideOffset > 0.5f)) {
                    left = left2 + SlidingPaneLayout.this.mSlideRange;
                } else {
                    left = left2;
                }
            }
            SlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            SlidingPaneLayout.this.invalidate();
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public int getViewHorizontalDragRange(View child) {
            return SlidingPaneLayout.this.mSlideRange;
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            LayoutParams lp = (LayoutParams) SlidingPaneLayout.this.mSlideableView.getLayoutParams();
            if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
                int startBound = SlidingPaneLayout.this.getWidth() - ((SlidingPaneLayout.this.getPaddingRight() + lp.rightMargin) + SlidingPaneLayout.this.mSlideableView.getWidth());
                int endBound = startBound - SlidingPaneLayout.this.mSlideRange;
                return Math.max(Math.min(left, startBound), endBound);
            }
            int startBound2 = SlidingPaneLayout.this.getPaddingLeft() + lp.leftMargin;
            int endBound2 = SlidingPaneLayout.this.mSlideRange + startBound2;
            return Math.min(Math.max(left, startBound2), endBound2);
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public int clampViewPositionVertical(View child, int top, int dy) {
            return child.getTop();
        }

        @Override // android.support.p000v4.widget.ViewDragHelper.Callback
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            SlidingPaneLayout.this.mDragHelper.captureChildView(SlidingPaneLayout.this.mSlideableView, pointerId);
        }
    }

    /* renamed from: android.support.v4.widget.SlidingPaneLayout$LayoutParams */
    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int[] ATTRS = {16843137};
        Paint dimPaint;
        boolean dimWhenOffset;
        boolean slideable;
        public float weight;

        public LayoutParams() {
            super(-1, -1);
            this.weight = 0.0f;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.weight = 0.0f;
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
            this.weight = 0.0f;
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
            this.weight = 0.0f;
        }

        public LayoutParams(@NonNull LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.weight = 0.0f;
            this.weight = source.weight;
        }

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            this.weight = 0.0f;
            TypedArray a = c.obtainStyledAttributes(attrs, ATTRS);
            this.weight = a.getFloat(0, 0.0f);
            a.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.support.v4.widget.SlidingPaneLayout$SavedState */
    /* loaded from: classes.dex */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.v4.widget.SlidingPaneLayout.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            /* renamed from: createFromParcel */
            public SavedState mo199createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, null);
            }

            @Override // android.os.Parcelable.Creator
            /* renamed from: createFromParcel */
            public SavedState mo198createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override // android.os.Parcelable.Creator
            /* renamed from: newArray */
            public SavedState[] mo200newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean isOpen;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.isOpen = in.readInt() != 0;
        }

        @Override // android.support.p000v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.isOpen ? 1 : 0);
        }
    }

    /* renamed from: android.support.v4.widget.SlidingPaneLayout$AccessibilityDelegate */
    /* loaded from: classes.dex */
    class AccessibilityDelegate extends AccessibilityDelegateCompat {
        private final Rect mTmpRect = new Rect();

        AccessibilityDelegate() {
        }

        @Override // android.support.p000v4.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            AccessibilityNodeInfoCompat superNode = AccessibilityNodeInfoCompat.obtain(info);
            super.onInitializeAccessibilityNodeInfo(host, superNode);
            copyNodeInfoNoChildren(info, superNode);
            superNode.recycle();
            info.setClassName(SlidingPaneLayout.class.getName());
            info.setSource(host);
            ViewParent parent = ViewCompat.getParentForAccessibility(host);
            if (parent instanceof View) {
                info.setParent((View) parent);
            }
            int childCount = SlidingPaneLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = SlidingPaneLayout.this.getChildAt(i);
                if (!filter(child) && child.getVisibility() == 0) {
                    ViewCompat.setImportantForAccessibility(child, 1);
                    info.addChild(child);
                }
            }
        }

        @Override // android.support.p000v4.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);
            event.setClassName(SlidingPaneLayout.class.getName());
        }

        @Override // android.support.p000v4.view.AccessibilityDelegateCompat
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            if (!filter(child)) {
                return super.onRequestSendAccessibilityEvent(host, child, event);
            }
            return false;
        }

        public boolean filter(View child) {
            return SlidingPaneLayout.this.isDimmed(child);
        }

        private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat dest, AccessibilityNodeInfoCompat src) {
            Rect rect = this.mTmpRect;
            src.getBoundsInParent(rect);
            dest.setBoundsInParent(rect);
            src.getBoundsInScreen(rect);
            dest.setBoundsInScreen(rect);
            dest.setVisibleToUser(src.isVisibleToUser());
            dest.setPackageName(src.getPackageName());
            dest.setClassName(src.getClassName());
            dest.setContentDescription(src.getContentDescription());
            dest.setEnabled(src.isEnabled());
            dest.setClickable(src.isClickable());
            dest.setFocusable(src.isFocusable());
            dest.setFocused(src.isFocused());
            dest.setAccessibilityFocused(src.isAccessibilityFocused());
            dest.setSelected(src.isSelected());
            dest.setLongClickable(src.isLongClickable());
            dest.addAction(src.getActions());
            dest.setMovementGranularities(src.getMovementGranularities());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.support.v4.widget.SlidingPaneLayout$DisableLayerRunnable */
    /* loaded from: classes.dex */
    public class DisableLayerRunnable implements Runnable {
        final View mChildView;

        DisableLayerRunnable(View childView) {
            this.mChildView = childView;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mChildView.getParent() == SlidingPaneLayout.this) {
                this.mChildView.setLayerType(0, null);
                SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
            }
            SlidingPaneLayout.this.mPostedRunnables.remove(this);
        }
    }

    boolean isLayoutRtlSupport() {
        return ViewCompat.getLayoutDirection(this) == 1;
    }
}