package android.support.p003v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.p000v4.view.GravityCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.appcompat.C0452R;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;

/* renamed from: android.support.v7.widget.LinearLayoutCompat */
/* loaded from: classes.dex */
public class LinearLayoutCompat extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* renamed from: android.support.v7.widget.LinearLayoutCompat$DividerMode */
    /* loaded from: classes.dex */
    public @interface DividerMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* renamed from: android.support.v7.widget.LinearLayoutCompat$OrientationMode */
    /* loaded from: classes.dex */
    public @interface OrientationMode {
    }

    public LinearLayoutCompat(Context context) {
        this(context, null);
    }

    public LinearLayoutCompat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayoutCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, C0452R.styleable.LinearLayoutCompat, defStyleAttr, 0);
        int index = a.getInt(C0452R.styleable.LinearLayoutCompat_android_orientation, -1);
        if (index >= 0) {
            setOrientation(index);
        }
        int index2 = a.getInt(C0452R.styleable.LinearLayoutCompat_android_gravity, -1);
        if (index2 >= 0) {
            setGravity(index2);
        }
        boolean baselineAligned = a.getBoolean(C0452R.styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!baselineAligned) {
            setBaselineAligned(baselineAligned);
        }
        this.mWeightSum = a.getFloat(C0452R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = a.getInt(C0452R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = a.getBoolean(C0452R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
        setDividerDrawable(a.getDrawable(C0452R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = a.getInt(C0452R.styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = a.getDimensionPixelSize(C0452R.styleable.LinearLayoutCompat_dividerPadding, 0);
        a.recycle();
    }

    public void setShowDividers(int showDividers) {
        if (showDividers != this.mShowDividers) {
            requestLayout();
        }
        this.mShowDividers = showDividers;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public void setDividerDrawable(Drawable divider) {
        if (divider == this.mDivider) {
            return;
        }
        this.mDivider = divider;
        boolean z = false;
        if (divider != null) {
            this.mDividerWidth = divider.getIntrinsicWidth();
            this.mDividerHeight = divider.getIntrinsicHeight();
        } else {
            this.mDividerWidth = 0;
            this.mDividerHeight = 0;
        }
        if (divider == null) {
            z = true;
        }
        setWillNotDraw(z);
        requestLayout();
    }

    public void setDividerPadding(int padding) {
        this.mDividerPadding = padding;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.mOrientation == 1) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }

    void drawDividersVertical(Canvas canvas) {
        int bottom;
        int count = getVirtualChildCount();
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int top = (child.getTop() - lp.topMargin) - this.mDividerHeight;
                drawHorizontalDivider(canvas, top);
            }
        }
        if (hasDividerBeforeChildAt(count)) {
            View child2 = getVirtualChildAt(count - 1);
            if (child2 == null) {
                bottom = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
            } else {
                LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                bottom = child2.getBottom() + lp2.bottomMargin;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    void drawDividersHorizontal(Canvas canvas) {
        int position;
        int position2;
        int count = getVirtualChildCount();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (isLayoutRtl) {
                    position2 = child.getRight() + lp.rightMargin;
                } else {
                    position2 = (child.getLeft() - lp.leftMargin) - this.mDividerWidth;
                }
                drawVerticalDivider(canvas, position2);
            }
        }
        if (hasDividerBeforeChildAt(count)) {
            View child2 = getVirtualChildAt(count - 1);
            if (child2 == null) {
                if (isLayoutRtl) {
                    position = getPaddingLeft();
                } else {
                    position = (getWidth() - getPaddingRight()) - this.mDividerWidth;
                }
            } else {
                LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                if (isLayoutRtl) {
                    position = (child2.getLeft() - lp2.leftMargin) - this.mDividerWidth;
                } else {
                    position = child2.getRight() + lp2.rightMargin;
                }
            }
            drawVerticalDivider(canvas, position);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int top) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, top, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + top);
        this.mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int left) {
        this.mDivider.setBounds(left, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + left, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    public void setBaselineAligned(boolean baselineAligned) {
        this.mBaselineAligned = baselineAligned;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    public void setMeasureWithLargestChildEnabled(boolean enabled) {
        this.mUseLargestChild = enabled;
    }

    @Override // android.view.View
    public int getBaseline() {
        int majorGravity;
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        int childCount = getChildCount();
        int i = this.mBaselineAlignedChildIndex;
        if (childCount <= i) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        View child = getChildAt(i);
        int childBaseline = child.getBaseline();
        if (childBaseline == -1) {
            if (this.mBaselineAlignedChildIndex == 0) {
                return -1;
            }
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }
        int childTop = this.mBaselineChildTop;
        if (this.mOrientation == 1 && (majorGravity = this.mGravity & 112) != 48) {
            if (majorGravity == 16) {
                childTop += ((((getBottom() - getTop()) - getPaddingTop()) - getPaddingBottom()) - this.mTotalLength) / 2;
            } else if (majorGravity == 80) {
                childTop = ((getBottom() - getTop()) - getPaddingBottom()) - this.mTotalLength;
            }
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return lp.topMargin + childTop + childBaseline;
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    public void setBaselineAlignedChildIndex(int i) {
        if (i < 0 || i >= getChildCount()) {
            throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
        }
        this.mBaselineAlignedChildIndex = i;
    }

    View getVirtualChildAt(int index) {
        return getChildAt(index);
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    public void setWeightSum(float weightSum) {
        this.mWeightSum = Math.max(0.0f, weightSum);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mOrientation == 1) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return (this.mShowDividers & 1) != 0;
        } else if (childIndex == getChildCount()) {
            return (this.mShowDividers & 4) != 0;
        } else if ((this.mShowDividers & 2) == 0) {
            return false;
        } else {
            for (int i = childIndex - 1; i >= 0; i--) {
                if (getChildAt(i).getVisibility() != 8) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:145:0x045d  */
    /* JADX WARN: Removed duplicated region for block: B:147:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void measureVertical(int r38, int r39) {
        /*
            Method dump skipped, instructions count: 1121
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.LinearLayoutCompat.measureVertical(int, int):void");
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MemoryConstants.f216GB);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.width == -1) {
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:154:0x0540  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x0578  */
    /* JADX WARN: Removed duplicated region for block: B:194:0x062b  */
    /* JADX WARN: Removed duplicated region for block: B:197:0x0633  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void measureHorizontal(int r47, int r48) {
        /*
            Method dump skipped, instructions count: 1592
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.LinearLayoutCompat.measureHorizontal(int, int):void");
    }

    private void forceUniformHeight(int count, int widthMeasureSpec) {
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MemoryConstants.f216GB);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.height == -1) {
                    int oldWidth = lp.width;
                    lp.width = child.getMeasuredWidth();
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    int getChildrenSkipCount(View child, int index) {
        return 0;
    }

    int measureNullChild(int childIndex) {
        return 0;
    }

    void measureChildBeforeLayout(View child, int childIndex, int widthMeasureSpec, int totalWidth, int heightMeasureSpec, int totalHeight) {
        measureChildWithMargins(child, widthMeasureSpec, totalWidth, heightMeasureSpec, totalHeight);
    }

    int getLocationOffset(View child) {
        return 0;
    }

    int getNextLocationOffset(View child) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.mOrientation == 1) {
            layoutVertical(l, t, r, b);
        } else {
            layoutHorizontal(l, t, r, b);
        }
    }

    void layoutVertical(int left, int top, int right, int bottom) {
        int childTop;
        int paddingLeft;
        int gravity;
        int childLeft;
        int paddingLeft2 = getPaddingLeft();
        int width = right - left;
        int childRight = width - getPaddingRight();
        int childSpace = (width - paddingLeft2) - getPaddingRight();
        int count = getVirtualChildCount();
        int i = this.mGravity;
        int majorGravity = i & 112;
        int minorGravity = i & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (majorGravity == 16) {
            int childTop2 = getPaddingTop();
            childTop = childTop2 + (((bottom - top) - this.mTotalLength) / 2);
        } else {
            childTop = majorGravity != 80 ? getPaddingTop() : ((getPaddingTop() + bottom) - top) - this.mTotalLength;
        }
        int i2 = 0;
        while (i2 < count) {
            View child = getVirtualChildAt(i2);
            if (child == null) {
                childTop += measureNullChild(i2);
                paddingLeft = paddingLeft2;
            } else if (child.getVisibility() == 8) {
                paddingLeft = paddingLeft2;
            } else {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int gravity2 = lp.gravity;
                if (gravity2 >= 0) {
                    gravity = gravity2;
                } else {
                    gravity = minorGravity;
                }
                int layoutDirection = ViewCompat.getLayoutDirection(this);
                int absoluteGravity = GravityCompat.getAbsoluteGravity(gravity, layoutDirection);
                int i3 = absoluteGravity & 7;
                if (i3 == 1) {
                    int childLeft2 = childSpace - childWidth;
                    childLeft = (((childLeft2 / 2) + paddingLeft2) + lp.leftMargin) - lp.rightMargin;
                } else if (i3 == 5) {
                    int childLeft3 = (childRight - childWidth) - lp.rightMargin;
                    childLeft = childLeft3;
                } else {
                    childLeft = lp.leftMargin + paddingLeft2;
                }
                if (hasDividerBeforeChildAt(i2)) {
                    childTop += this.mDividerHeight;
                }
                int childTop3 = childTop + lp.topMargin;
                int childTop4 = getLocationOffset(child);
                int layoutDirection2 = childLeft;
                paddingLeft = paddingLeft2;
                setChildFrame(child, layoutDirection2, childTop3 + childTop4, childWidth, childHeight);
                int childTop5 = childTop3 + childHeight + lp.bottomMargin + getNextLocationOffset(child);
                i2 += getChildrenSkipCount(child, i2);
                childTop = childTop5;
            }
            i2++;
            paddingLeft2 = paddingLeft;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00d3  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0107  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00c9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void layoutHorizontal(int r34, int r35, int r36, int r37) {
        /*
            Method dump skipped, instructions count: 392
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.LinearLayoutCompat.layoutHorizontal(int, int, int, int):void");
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            requestLayout();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((8388615 & gravity) == 0) {
                gravity |= GravityCompat.START;
            }
            if ((gravity & 112) == 0) {
                gravity |= 48;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setHorizontalGravity(int horizontalGravity) {
        int gravity = horizontalGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int i = this.mGravity;
        if ((8388615 & i) != gravity) {
            this.mGravity = ((-8388616) & i) | gravity;
            requestLayout();
        }
    }

    public void setVerticalGravity(int verticalGravity) {
        int gravity = verticalGravity & 112;
        int i = this.mGravity;
        if ((i & 112) != gravity) {
            this.mGravity = (i & (-113)) | gravity;
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        int i = this.mOrientation;
        if (i == 0) {
            return new LayoutParams(-2, -2);
        }
        if (i == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(LinearLayoutCompat.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(LinearLayoutCompat.class.getName());
    }

    /* renamed from: android.support.v7.widget.LinearLayoutCompat$LayoutParams */
    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity;
        public float weight;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.gravity = -1;
            TypedArray a = c.obtainStyledAttributes(attrs, C0452R.styleable.LinearLayoutCompat_Layout);
            this.weight = a.getFloat(C0452R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = a.getInt(C0452R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.gravity = -1;
            this.weight = weight;
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            this.gravity = -1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.gravity = -1;
            this.weight = source.weight;
            this.gravity = source.gravity;
        }
    }
}