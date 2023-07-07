package me.goldze.mvvmhabit.binding.viewadapter.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.p003v7.widget.RecyclerView;
import android.view.View;

/* loaded from: classes.dex */
public class DividerLine extends RecyclerView.ItemDecoration {
    private static final int DEFAULT_DIVIDER_SIZE = 1;
    private Drawable dividerDrawable;
    private int dividerSize;
    private Context mContext;
    private LineDrawMode mMode;
    private static final String TAG = DividerLine.class.getCanonicalName();
    private static final int[] ATTRS = {16843284};

    /* loaded from: classes.dex */
    public enum LineDrawMode {
        HORIZONTAL,
        VERTICAL,
        BOTH
    }

    public DividerLine(Context context) {
        this.mMode = null;
        this.mContext = context;
        TypedArray attrArray = context.obtainStyledAttributes(ATTRS);
        this.dividerDrawable = attrArray.getDrawable(0);
        attrArray.recycle();
    }

    public DividerLine(Context context, LineDrawMode mode) {
        this(context);
        this.mMode = mode;
    }

    public DividerLine(Context context, int dividerSize, LineDrawMode mode) {
        this(context, mode);
        this.dividerSize = dividerSize;
    }

    public int getDividerSize() {
        return this.dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    public LineDrawMode getMode() {
        return this.mMode;
    }

    public void setMode(LineDrawMode mode) {
        this.mMode = mode;
    }

    @Override // android.support.p003v7.widget.RecyclerView.ItemDecoration
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (getMode() == null) {
            throw new IllegalStateException("assign LineDrawMode,please!");
        }
        switch (getMode()) {
            case VERTICAL:
                drawVertical(c, parent, state);
                return;
            case HORIZONTAL:
                drawHorizontal(c, parent, state);
                return;
            case BOTH:
                drawHorizontal(c, parent, state);
                drawVertical(c, parent, state);
                return;
            default:
                return;
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getTop() - params.topMargin;
            int bottom = child.getBottom() + params.bottomMargin;
            int left = child.getRight() + params.rightMargin;
            int right = (getDividerSize() == 0 ? dip2px(this.mContext, 1.0f) : getDividerSize()) + left;
            this.dividerDrawable.setBounds(left, top, right, bottom);
            this.dividerDrawable.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft() - params.leftMargin;
            int top = child.getBottom() + params.topMargin;
            int right = child.getRight() - params.rightMargin;
            int bottom = (getDividerSize() == 0 ? dip2px(this.mContext, 1.0f) : getDividerSize()) + top;
            this.dividerDrawable.setBounds(left, top, right, bottom);
            this.dividerDrawable.draw(c);
        }
    }

    @Override // android.support.p003v7.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * scale) + 0.5f);
    }
}