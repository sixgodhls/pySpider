package android.support.design.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.design.C0091R;
import android.support.p000v4.view.MarginLayoutParamsCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.widget.ActivityChooserView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class FlowLayout extends ViewGroup {
    private int itemSpacing;
    private int lineSpacing;
    private boolean singleLine;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.singleLine = false;
        loadFromAttributes(context, attrs);
    }

    @TargetApi(21)
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.singleLine = false;
        loadFromAttributes(context, attrs);
    }

    private void loadFromAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, C0091R.styleable.FlowLayout, 0, 0);
        this.lineSpacing = array.getDimensionPixelSize(C0091R.styleable.FlowLayout_lineSpacing, 0);
        this.itemSpacing = array.getDimensionPixelSize(C0091R.styleable.FlowLayout_itemSpacing, 0);
        array.recycle();
    }

    protected int getLineSpacing() {
        return this.lineSpacing;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    protected int getItemSpacing() {
        return this.itemSpacing;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setItemSpacing(int itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    protected boolean isSingleLine() {
        return this.singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int maxWidth2 = (widthMode == Integer.MIN_VALUE || widthMode == 1073741824) ? width : ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childBottom = childTop;
        int maxChildRight = 0;
        int maxRight = maxWidth2 - getPaddingRight();
        int i = 0;
        while (i < getChildCount()) {
            View child = getChildAt(i);
            if (child.getVisibility() == 8) {
                maxWidth = maxWidth2;
            } else {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth = maxWidth2;
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                int leftMargin = 0;
                int rightMargin = 0;
                int childTop2 = childTop;
                if (lp instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLp = (ViewGroup.MarginLayoutParams) lp;
                    leftMargin = 0 + marginLp.leftMargin;
                    rightMargin = 0 + marginLp.rightMargin;
                }
                if (childLeft + leftMargin + child.getMeasuredWidth() > maxRight && !isSingleLine()) {
                    childLeft = getPaddingLeft();
                    childTop2 = this.lineSpacing + childBottom;
                }
                int childRight = childLeft + leftMargin + child.getMeasuredWidth();
                int childBottom2 = childTop2 + child.getMeasuredHeight();
                if (childRight > maxChildRight) {
                    maxChildRight = childRight;
                }
                childLeft += leftMargin + rightMargin + child.getMeasuredWidth() + this.itemSpacing;
                childBottom = childBottom2;
                childTop = childTop2;
            }
            i++;
            maxWidth2 = maxWidth;
        }
        int finalWidth = getMeasuredDimension(width, widthMode, maxChildRight);
        int finalHeight = getMeasuredDimension(height, heightMode, childBottom);
        setMeasuredDimension(finalWidth, finalHeight);
    }

    private static int getMeasuredDimension(int size, int mode, int childrenEdge) {
        if (mode != Integer.MIN_VALUE) {
            if (mode == 1073741824) {
                return size;
            }
            return childrenEdge;
        }
        return Math.min(childrenEdge, size);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean sizeChanged, int left, int top, int right, int bottom) {
        if (getChildCount() == 0) {
            return;
        }
        boolean z = true;
        if (ViewCompat.getLayoutDirection(this) != 1) {
            z = false;
        }
        boolean isRtl = z;
        int paddingStart = isRtl ? getPaddingRight() : getPaddingLeft();
        int paddingEnd = isRtl ? getPaddingLeft() : getPaddingRight();
        int childStart = paddingStart;
        int childTop = getPaddingTop();
        int childBottom = childTop;
        int maxChildEnd = (right - left) - paddingEnd;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                int startMargin = 0;
                int endMargin = 0;
                if (lp instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLp = (ViewGroup.MarginLayoutParams) lp;
                    startMargin = MarginLayoutParamsCompat.getMarginStart(marginLp);
                    endMargin = MarginLayoutParamsCompat.getMarginEnd(marginLp);
                }
                int childEnd = childStart + startMargin + child.getMeasuredWidth();
                if (!this.singleLine && childEnd > maxChildEnd) {
                    childStart = paddingStart;
                    childTop = childBottom + this.lineSpacing;
                }
                int childEnd2 = childStart + startMargin + child.getMeasuredWidth();
                int childEnd3 = child.getMeasuredHeight();
                int childBottom2 = childEnd3 + childTop;
                if (isRtl) {
                    child.layout(maxChildEnd - childEnd2, childTop, (maxChildEnd - childStart) - startMargin, childBottom2);
                } else {
                    child.layout(childStart + startMargin, childTop, childEnd2, childBottom2);
                }
                childStart += startMargin + endMargin + child.getMeasuredWidth() + this.itemSpacing;
                childBottom = childBottom2;
            }
        }
    }
}