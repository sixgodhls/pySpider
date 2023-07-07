package android.support.design.widget;

import android.support.v4.view.ViewCompat;
import android.view.View;

/* loaded from: classes.dex */
class ViewOffsetHelper {
    private int layoutLeft;
    private int layoutTop;
    private int offsetLeft;
    private int offsetTop;
    private final View view;

    public ViewOffsetHelper(View view) {
        this.view = view;
    }

    public void onViewLayout() {
        this.layoutTop = this.view.getTop();
        this.layoutLeft = this.view.getLeft();
        updateOffsets();
    }

    private void updateOffsets() {
        View view = this.view;
        ViewCompat.offsetTopAndBottom(view, this.offsetTop - (view.getTop() - this.layoutTop));
        View view2 = this.view;
        ViewCompat.offsetLeftAndRight(view2, this.offsetLeft - (view2.getLeft() - this.layoutLeft));
    }

    public boolean setTopAndBottomOffset(int offset) {
        if (this.offsetTop != offset) {
            this.offsetTop = offset;
            updateOffsets();
            return true;
        }
        return false;
    }

    public boolean setLeftAndRightOffset(int offset) {
        if (this.offsetLeft != offset) {
            this.offsetLeft = offset;
            updateOffsets();
            return true;
        }
        return false;
    }

    public int getTopAndBottomOffset() {
        return this.offsetTop;
    }

    public int getLeftAndRightOffset() {
        return this.offsetLeft;
    }

    public int getLayoutTop() {
        return this.layoutTop;
    }

    public int getLayoutLeft() {
        return this.layoutLeft;
    }
}
