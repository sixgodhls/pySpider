package android.support.design.widget;

import android.content.Context;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.widget.ActivityChooserView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

/* loaded from: classes.dex */
abstract class HeaderBehavior<V extends View> extends ViewOffsetBehavior<V> {
    private static final int INVALID_POINTER = -1;
    private Runnable flingRunnable;
    private boolean isBeingDragged;
    private int lastMotionY;
    OverScroller scroller;
    private VelocityTracker velocityTracker;
    private int activePointerId = -1;
    private int touchSlop = -1;

    public HeaderBehavior() {
    }

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
        int pointerIndex;
        if (this.touchSlop < 0) {
            this.touchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }
        int action = ev.getAction();
        if (action != 2 || !this.isBeingDragged) {
            switch (ev.getActionMasked()) {
                case 0:
                    this.isBeingDragged = false;
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    if (canDragView(child) && parent.isPointInChildBounds(child, x, y)) {
                        this.lastMotionY = y;
                        this.activePointerId = ev.getPointerId(0);
                        ensureVelocityTracker();
                        break;
                    }
                    break;
                case 1:
                case 3:
                    this.isBeingDragged = false;
                    this.activePointerId = -1;
                    VelocityTracker velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                        break;
                    }
                    break;
                case 2:
                    int activePointerId = this.activePointerId;
                    if (activePointerId != -1 && (pointerIndex = ev.findPointerIndex(activePointerId)) != -1) {
                        int y2 = (int) ev.getY(pointerIndex);
                        int yDiff = Math.abs(y2 - this.lastMotionY);
                        if (yDiff > this.touchSlop) {
                            this.isBeingDragged = true;
                            this.lastMotionY = y2;
                            break;
                        }
                    }
                    break;
            }
            VelocityTracker velocityTracker2 = this.velocityTracker;
            if (velocityTracker2 != null) {
                velocityTracker2.addMovement(ev);
            }
            return this.isBeingDragged;
        }
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0080  */
    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.support.design.widget.CoordinatorLayout r12, V r13, android.view.MotionEvent r14) {
        /*
            r11 = this;
            int r0 = r11.touchSlop
            if (r0 >= 0) goto L12
            android.content.Context r0 = r12.getContext()
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r0)
            int r0 = r0.getScaledTouchSlop()
            r11.touchSlop = r0
        L12:
            int r0 = r14.getActionMasked()
            r1 = 1
            r2 = -1
            r3 = 0
            switch(r0) {
                case 0: goto L87;
                case 1: goto L55;
                case 2: goto L1e;
                case 3: goto L78;
                default: goto L1c;
            }
        L1c:
            goto Laa
        L1e:
            int r0 = r11.activePointerId
            int r0 = r14.findPointerIndex(r0)
            if (r0 != r2) goto L27
            return r3
        L27:
            float r2 = r14.getY(r0)
            int r2 = (int) r2
            int r3 = r11.lastMotionY
            int r3 = r3 - r2
            boolean r4 = r11.isBeingDragged
            if (r4 != 0) goto L42
            int r4 = java.lang.Math.abs(r3)
            int r5 = r11.touchSlop
            if (r4 <= r5) goto L42
            r11.isBeingDragged = r1
            if (r3 <= 0) goto L41
            int r3 = r3 - r5
            goto L42
        L41:
            int r3 = r3 + r5
        L42:
            boolean r4 = r11.isBeingDragged
            if (r4 == 0) goto Laa
            r11.lastMotionY = r2
            int r8 = r11.getMaxDragOffset(r13)
            r9 = 0
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r3
            r4.scroll(r5, r6, r7, r8, r9)
            goto Laa
        L55:
            android.view.VelocityTracker r0 = r11.velocityTracker
            if (r0 == 0) goto L78
            r0.addMovement(r14)
            android.view.VelocityTracker r0 = r11.velocityTracker
            r4 = 1000(0x3e8, float:1.401E-42)
            r0.computeCurrentVelocity(r4)
            android.view.VelocityTracker r0 = r11.velocityTracker
            int r4 = r11.activePointerId
            float r0 = r0.getYVelocity(r4)
            int r4 = r11.getScrollRangeForDragFling(r13)
            int r8 = -r4
            r9 = 0
            r5 = r11
            r6 = r12
            r7 = r13
            r10 = r0
            r5.fling(r6, r7, r8, r9, r10)
        L78:
            r11.isBeingDragged = r3
            r11.activePointerId = r2
            android.view.VelocityTracker r0 = r11.velocityTracker
            if (r0 == 0) goto Laa
            r0.recycle()
            r0 = 0
            r11.velocityTracker = r0
            goto Laa
        L87:
            float r0 = r14.getX()
            int r0 = (int) r0
            float r2 = r14.getY()
            int r2 = (int) r2
            boolean r4 = r12.isPointInChildBounds(r13, r0, r2)
            if (r4 == 0) goto La9
            boolean r4 = r11.canDragView(r13)
            if (r4 == 0) goto La9
            r11.lastMotionY = r2
            int r3 = r14.getPointerId(r3)
            r11.activePointerId = r3
            r11.ensureVelocityTracker()
            goto Laa
        La9:
            return r3
        Laa:
            android.view.VelocityTracker r0 = r11.velocityTracker
            if (r0 == 0) goto Lb1
            r0.addMovement(r14)
        Lb1:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.HeaderBehavior.onTouchEvent(android.support.design.widget.CoordinatorLayout, android.view.View, android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int setHeaderTopBottomOffset(CoordinatorLayout parent, V header, int newOffset) {
        return setHeaderTopBottomOffset(parent, header, newOffset, Integer.MIN_VALUE, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    int setHeaderTopBottomOffset(CoordinatorLayout parent, V header, int newOffset, int minOffset, int maxOffset) {
        int newOffset2;
        int curOffset = getTopAndBottomOffset();
        if (minOffset == 0 || curOffset < minOffset || curOffset > maxOffset || curOffset == (newOffset2 = android.support.p000v4.math.MathUtils.clamp(newOffset, minOffset, maxOffset))) {
            return 0;
        }
        setTopAndBottomOffset(newOffset2);
        int consumed = curOffset - newOffset2;
        return consumed;
    }

    int getTopBottomOffsetForScrollingSibling() {
        return getTopAndBottomOffset();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int scroll(CoordinatorLayout coordinatorLayout, V header, int dy, int minOffset, int maxOffset) {
        return setHeaderTopBottomOffset(coordinatorLayout, header, getTopBottomOffsetForScrollingSibling() - dy, minOffset, maxOffset);
    }

    final boolean fling(CoordinatorLayout coordinatorLayout, V layout, int minOffset, int maxOffset, float velocityY) {
        Runnable runnable = this.flingRunnable;
        if (runnable != null) {
            layout.removeCallbacks(runnable);
            this.flingRunnable = null;
        }
        if (this.scroller == null) {
            this.scroller = new OverScroller(layout.getContext());
        }
        this.scroller.fling(0, getTopAndBottomOffset(), 0, Math.round(velocityY), 0, 0, minOffset, maxOffset);
        if (this.scroller.computeScrollOffset()) {
            this.flingRunnable = new FlingRunnable(coordinatorLayout, layout);
            ViewCompat.postOnAnimation(layout, this.flingRunnable);
            return true;
        }
        onFlingFinished(coordinatorLayout, layout);
        return false;
    }

    void onFlingFinished(CoordinatorLayout parent, V layout) {
    }

    boolean canDragView(V view) {
        return false;
    }

    int getMaxDragOffset(V view) {
        return -view.getHeight();
    }

    int getScrollRangeForDragFling(V view) {
        return view.getHeight();
    }

    private void ensureVelocityTracker() {
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FlingRunnable implements Runnable {
        private final V layout;
        private final CoordinatorLayout parent;

        FlingRunnable(CoordinatorLayout parent, V layout) {
            this.parent = parent;
            this.layout = layout;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.layout != null && HeaderBehavior.this.scroller != null) {
                if (HeaderBehavior.this.scroller.computeScrollOffset()) {
                    HeaderBehavior headerBehavior = HeaderBehavior.this;
                    headerBehavior.setHeaderTopBottomOffset(this.parent, this.layout, headerBehavior.scroller.getCurrY());
                    ViewCompat.postOnAnimation(this.layout, this);
                    return;
                }
                HeaderBehavior.this.onFlingFinished(this.parent, this.layout);
            }
        }
    }
}