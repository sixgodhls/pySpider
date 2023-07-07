package com.lcodecore.tkrefreshlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.lcodecore.tkrefreshlayout.footer.BallPulseView;
import com.lcodecore.tkrefreshlayout.header.GoogleDotView;
import com.lcodecore.tkrefreshlayout.processor.AnimProcessor;
import com.lcodecore.tkrefreshlayout.processor.IDecorator;
import com.lcodecore.tkrefreshlayout.processor.OverScrollDecorator;
import com.lcodecore.tkrefreshlayout.processor.RefreshProcessor;
import com.lcodecore.tkrefreshlayout.utils.DensityUtil;
import java.lang.reflect.Constructor;

/* loaded from: classes.dex */
public class TwinklingRefreshLayout extends RelativeLayout implements PullListener, NestedScrollingChild {
    protected boolean autoLoadMore;
    private CoContext cp;
    private IDecorator decorator;
    protected boolean enableKeepIView;
    protected boolean enableLoadmore;
    protected boolean enableOverScroll;
    protected boolean enableRefresh;
    protected boolean floatRefresh;
    protected boolean isLoadingMore;
    protected boolean isLoadingVisible;
    protected boolean isOverScrollBottomShow;
    protected boolean isOverScrollTopShow;
    protected boolean isPureScrollModeOn;
    protected boolean isRefreshVisible;
    protected boolean isRefreshing;
    private OnGestureListener listener;
    private int mActivePointerId;
    private boolean mAlwaysInTapRegion;
    private float mBottomHeight;
    private FrameLayout mBottomLayout;
    private IBottomView mBottomView;
    private final NestedScrollingChildHelper mChildHelper;
    private View mChildView;
    private MotionEvent mCurrentDownEvent;
    private float mDownFocusX;
    private float mDownFocusY;
    private int mExHeadHeight;
    private FrameLayout mExtraHeadLayout;
    protected float mHeadHeight;
    protected FrameLayout mHeadLayout;
    private IHeaderView mHeadView;
    private boolean mIsBeingDragged;
    private float mLastFocusX;
    private float mLastFocusY;
    private int mLastTouchX;
    private int mLastTouchY;
    protected float mMaxBottomHeight;
    protected float mMaxHeadHeight;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private final int[] mNestedOffsets;
    protected float mOverScrollHeight;
    private final int[] mScrollConsumed;
    private final int[] mScrollOffset;
    private final int mTouchSlop;
    private int mTouchSlopSquare;
    private VelocityTracker mVelocityTracker;
    private PullListener pullListener;
    private RefreshListenerAdapter refreshListener;
    protected boolean showLoadingWhenOverScroll;
    protected boolean showRefreshingWhenOverScroll;
    private float vx;
    private float vy;
    private static String HEADER_CLASS_NAME = "";
    private static String FOOTER_CLASS_NAME = "";

    public TwinklingRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public TwinklingRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwinklingRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mExHeadHeight = 0;
        this.isRefreshVisible = false;
        this.isLoadingVisible = false;
        this.isRefreshing = false;
        this.isLoadingMore = false;
        this.enableLoadmore = true;
        this.enableRefresh = true;
        this.isOverScrollTopShow = true;
        this.isOverScrollBottomShow = true;
        this.isPureScrollModeOn = false;
        this.autoLoadMore = false;
        this.floatRefresh = false;
        this.enableOverScroll = true;
        this.enableKeepIView = true;
        this.showRefreshingWhenOverScroll = true;
        this.showLoadingWhenOverScroll = true;
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.pullListener = this;
        this.mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
        this.mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
        int i = this.mTouchSlop;
        this.mTouchSlopSquare = i * i;
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mNestedOffsets = new int[2];
        this.mActivePointerId = -1;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TwinklingRefreshLayout, defStyleAttr, 0);
        try {
            this.mMaxHeadHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_max_head_height, DensityUtil.dp2px(context, 120.0f));
            this.mHeadHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_head_height, DensityUtil.dp2px(context, 80.0f));
            this.mMaxBottomHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_max_bottom_height, DensityUtil.dp2px(context, 120.0f));
            this.mBottomHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_bottom_height, DensityUtil.dp2px(context, 60.0f));
            this.mOverScrollHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_overscroll_height, (int) this.mHeadHeight);
            this.enableRefresh = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_enable_refresh, true);
            this.enableLoadmore = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_enable_loadmore, true);
            this.isPureScrollModeOn = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_pureScrollMode_on, false);
            this.isOverScrollTopShow = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_overscroll_top_show, true);
            this.isOverScrollBottomShow = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_overscroll_bottom_show, true);
            this.enableOverScroll = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_enable_overscroll, true);
            this.floatRefresh = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_floatRefresh, false);
            this.autoLoadMore = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_autoLoadMore, false);
            this.enableKeepIView = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_enable_keepIView, true);
            this.showRefreshingWhenOverScroll = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_showRefreshingWhenOverScroll, true);
            this.showLoadingWhenOverScroll = a.getBoolean(R.styleable.TwinklingRefreshLayout_tr_showLoadingWhenOverScroll, true);
            a.recycle();
            this.cp = new CoContext();
            addHeader();
            addFooter();
            setFloatRefresh(this.floatRefresh);
            setAutoLoadMore(this.autoLoadMore);
            setEnableRefresh(this.enableRefresh);
            setEnableLoadmore(this.enableLoadmore);
            this.mChildHelper = new NestedScrollingChildHelper(this);
            setNestedScrollingEnabled(true);
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    private void addHeader() {
        FrameLayout headViewLayout = new FrameLayout(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, 0);
        layoutParams.addRule(10);
        FrameLayout extraHeadLayout = new FrameLayout(getContext());
        extraHeadLayout.setId(R.id.ex_header);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, -2);
        addView(extraHeadLayout, layoutParams2);
        addView(headViewLayout, layoutParams);
        this.mExtraHeadLayout = extraHeadLayout;
        this.mHeadLayout = headViewLayout;
        if (this.mHeadView == null) {
            if (!TextUtils.isEmpty(HEADER_CLASS_NAME)) {
                try {
                    Class headClazz = Class.forName(HEADER_CLASS_NAME);
                    Constructor ct = headClazz.getDeclaredConstructor(Context.class);
                    setHeaderView((IHeaderView) ct.newInstance(getContext()));
                    return;
                } catch (Exception e) {
                    Log.e("TwinklingRefreshLayout:", "setDefaultHeader classname=" + e.getMessage());
                    setHeaderView(new GoogleDotView(getContext()));
                    return;
                }
            }
            setHeaderView(new GoogleDotView(getContext()));
        }
    }

    private void addFooter() {
        FrameLayout bottomViewLayout = new FrameLayout(getContext());
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, 0);
        layoutParams2.addRule(12);
        bottomViewLayout.setLayoutParams(layoutParams2);
        this.mBottomLayout = bottomViewLayout;
        addView(this.mBottomLayout);
        if (this.mBottomView == null) {
            if (!TextUtils.isEmpty(FOOTER_CLASS_NAME)) {
                try {
                    Class clazz = Class.forName(FOOTER_CLASS_NAME);
                    Constructor ct = clazz.getDeclaredConstructor(Context.class);
                    setBottomView((IBottomView) ct.newInstance(getContext()));
                    return;
                } catch (Exception e) {
                    Log.e("TwinklingRefreshLayout:", "setDefaultFooter classname=" + e.getMessage());
                    setBottomView(new BallPulseView(getContext()));
                    return;
                }
            }
            setBottomView(new BallPulseView(getContext()));
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mChildView = getChildAt(3);
        this.cp.init();
        CoContext coContext = this.cp;
        this.decorator = new OverScrollDecorator(coContext, new RefreshProcessor(coContext));
        initGestureDetector();
    }

    private void initGestureDetector() {
        this.listener = new OnGestureListener() { // from class: com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout.1
            @Override // com.lcodecore.tkrefreshlayout.OnGestureListener
            public void onDown(MotionEvent ev) {
                TwinklingRefreshLayout.this.decorator.onFingerDown(ev);
            }

            @Override // com.lcodecore.tkrefreshlayout.OnGestureListener
            public void onScroll(MotionEvent downEvent, MotionEvent currentEvent, float distanceX, float distanceY) {
                TwinklingRefreshLayout.this.decorator.onFingerScroll(downEvent, currentEvent, distanceX, distanceY, TwinklingRefreshLayout.this.vx, TwinklingRefreshLayout.this.vy);
            }

            @Override // com.lcodecore.tkrefreshlayout.OnGestureListener
            public void onUp(MotionEvent ev, boolean isFling) {
                TwinklingRefreshLayout.this.decorator.onFingerUp(ev, isFling);
            }

            @Override // com.lcodecore.tkrefreshlayout.OnGestureListener
            public void onFling(MotionEvent downEvent, MotionEvent upEvent, float velocityX, float velocityY) {
                TwinklingRefreshLayout.this.decorator.onFingerFling(downEvent, upEvent, velocityX, velocityY);
            }
        };
    }

    private void detectGesture(MotionEvent ev, OnGestureListener listener) {
        boolean pointerUp;
        int skipIndex;
        int upIndex;
        int action = ev.getAction();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        boolean pointerUp2 = (action & 255) == 6;
        int skipIndex2 = pointerUp2 ? ev.getActionIndex() : -1;
        float sumX = 0.0f;
        float sumY = 0.0f;
        int count = ev.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex2 != i) {
                sumX += ev.getX(i);
                sumY += ev.getY(i);
            }
        }
        int div = pointerUp2 ? count - 1 : count;
        float focusX = sumX / div;
        float focusY = sumY / div;
        switch (action & 255) {
            case 0:
                this.mLastFocusX = focusX;
                this.mDownFocusX = focusX;
                this.mLastFocusY = focusY;
                this.mDownFocusY = focusY;
                MotionEvent motionEvent = this.mCurrentDownEvent;
                if (motionEvent != null) {
                    motionEvent.recycle();
                }
                this.mCurrentDownEvent = MotionEvent.obtain(ev);
                this.mAlwaysInTapRegion = true;
                listener.onDown(ev);
                return;
            case 1:
                int pointerId = ev.getPointerId(0);
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                this.vy = this.mVelocityTracker.getYVelocity(pointerId);
                this.vx = this.mVelocityTracker.getXVelocity(pointerId);
                boolean isFling = false;
                if (Math.abs(this.vy) > this.mMinimumFlingVelocity || Math.abs(this.vx) > this.mMinimumFlingVelocity) {
                    listener.onFling(this.mCurrentDownEvent, ev, this.vx, this.vy);
                    isFling = true;
                }
                listener.onUp(ev, isFling);
                VelocityTracker velocityTracker = this.mVelocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.mVelocityTracker = null;
                    return;
                }
                return;
            case 2:
                float scrollX = this.mLastFocusX - focusX;
                float scrollY = this.mLastFocusY - focusY;
                if (this.mAlwaysInTapRegion) {
                    int deltaX = (int) (focusX - this.mDownFocusX);
                    int deltaY = (int) (focusY - this.mDownFocusY);
                    int distance = (deltaX * deltaX) + (deltaY * deltaY);
                    if (distance > this.mTouchSlopSquare) {
                        listener.onScroll(this.mCurrentDownEvent, ev, scrollX, scrollY);
                        this.mLastFocusX = focusX;
                        this.mLastFocusY = focusY;
                        this.mAlwaysInTapRegion = false;
                        return;
                    }
                    return;
                } else if (Math.abs(scrollX) >= 1.0f || Math.abs(scrollY) >= 1.0f) {
                    listener.onScroll(this.mCurrentDownEvent, ev, scrollX, scrollY);
                    this.mLastFocusX = focusX;
                    this.mLastFocusY = focusY;
                    return;
                } else {
                    return;
                }
            case 3:
                this.mAlwaysInTapRegion = false;
                VelocityTracker velocityTracker2 = this.mVelocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.mVelocityTracker = null;
                    return;
                }
                return;
            case 4:
            default:
                return;
            case 5:
                this.mLastFocusX = focusX;
                this.mDownFocusX = focusX;
                this.mLastFocusY = focusY;
                this.mDownFocusY = focusY;
                return;
            case 6:
                this.mLastFocusX = focusX;
                this.mDownFocusX = focusX;
                this.mLastFocusY = focusY;
                this.mDownFocusY = focusY;
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                int upIndex2 = ev.getActionIndex();
                int id1 = ev.getPointerId(upIndex2);
                float x1 = this.mVelocityTracker.getXVelocity(id1);
                float y1 = this.mVelocityTracker.getYVelocity(id1);
                int action2 = 0;
                while (action2 < count) {
                    if (action2 == upIndex2) {
                        pointerUp = pointerUp2;
                        skipIndex = skipIndex2;
                        upIndex = upIndex2;
                    } else {
                        pointerUp = pointerUp2;
                        int id2 = ev.getPointerId(action2);
                        skipIndex = skipIndex2;
                        float x = this.mVelocityTracker.getXVelocity(id2) * x1;
                        upIndex = upIndex2;
                        float y = this.mVelocityTracker.getYVelocity(id2) * y1;
                        float dot = x + y;
                        if (dot < 0.0f) {
                            this.mVelocityTracker.clear();
                            return;
                        }
                    }
                    action2++;
                    upIndex2 = upIndex;
                    pointerUp2 = pointerUp;
                    skipIndex2 = skipIndex;
                }
                return;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean consume = this.decorator.dispatchTouchEvent(ev);
        detectGesture(ev, this.listener);
        detectNestedScroll(ev);
        return consume;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = this.decorator.interceptTouchEvent(ev);
        return intercept || super.onInterceptTouchEvent(ev);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent e) {
        boolean consume = this.decorator.dealTouchEvent(e);
        return consume || super.onTouchEvent(e);
    }

    private boolean detectNestedScroll(MotionEvent e) {
        int dy;
        MotionEvent vtev = MotionEvent.obtain(e);
        int action = MotionEventCompat.getActionMasked(e);
        int actionIndex = MotionEventCompat.getActionIndex(e);
        if (action == 0) {
            int[] iArr = this.mNestedOffsets;
            iArr[1] = 0;
            iArr[0] = 0;
        }
        int[] iArr2 = this.mNestedOffsets;
        vtev.offsetLocation(iArr2[0], iArr2[1]);
        if (action != 5) {
            switch (action) {
                case 0:
                    this.mActivePointerId = e.getPointerId(0);
                    this.mLastTouchX = (int) e.getX();
                    this.mLastTouchY = (int) e.getY();
                    startNestedScroll(2);
                    break;
                case 1:
                case 3:
                    stopNestedScroll();
                    this.mIsBeingDragged = false;
                    this.mActivePointerId = -1;
                    break;
                case 2:
                    int index = e.findPointerIndex(this.mActivePointerId);
                    if (index < 0) {
                        Log.e("TwinklingRefreshLayout", "Error processing scroll; pointer index for id " + this.mActivePointerId + " not found. Did any MotionEvents get skipped?");
                        return false;
                    }
                    int x = (int) e.getX(index);
                    int y = (int) e.getY(index);
                    int dx = this.mLastTouchX - x;
                    int dy2 = this.mLastTouchY - y;
                    if (dispatchNestedPreScroll(dx, dy2, this.mScrollConsumed, this.mScrollOffset)) {
                        int[] iArr3 = this.mScrollConsumed;
                        int i = dx - iArr3[0];
                        dy2 -= iArr3[1];
                        int[] iArr4 = this.mScrollOffset;
                        vtev.offsetLocation(iArr4[0], iArr4[1]);
                        int[] iArr5 = this.mNestedOffsets;
                        int i2 = iArr5[0];
                        int[] iArr6 = this.mScrollOffset;
                        iArr5[0] = i2 + iArr6[0];
                        iArr5[1] = iArr5[1] + iArr6[1];
                    }
                    if (!this.mIsBeingDragged && Math.abs(dy2) > this.mTouchSlop) {
                        ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        this.mIsBeingDragged = true;
                        if (dy2 > 0) {
                            dy = dy2 - this.mTouchSlop;
                        } else {
                            dy = dy2 + this.mTouchSlop;
                        }
                    } else {
                        dy = dy2;
                    }
                    if (this.mIsBeingDragged) {
                        int[] iArr7 = this.mScrollOffset;
                        this.mLastTouchY = y - iArr7[1];
                        int unconsumedY = dy + 0;
                        if (dispatchNestedScroll(0, 0, 0, unconsumedY, iArr7)) {
                            int i3 = this.mLastTouchX;
                            int[] iArr8 = this.mScrollOffset;
                            this.mLastTouchX = i3 - iArr8[0];
                            this.mLastTouchY -= iArr8[1];
                            vtev.offsetLocation(iArr8[0], iArr8[1]);
                            int[] iArr9 = this.mNestedOffsets;
                            int i4 = iArr9[0];
                            int[] iArr10 = this.mScrollOffset;
                            iArr9[0] = i4 + iArr10[0];
                            iArr9[1] = iArr9[1] + iArr10[1];
                            break;
                        }
                    }
                    break;
            }
        } else {
            this.mActivePointerId = e.getPointerId(actionIndex);
            this.mLastTouchX = (int) e.getX(actionIndex);
            this.mLastTouchY = (int) e.getY(actionIndex);
        }
        vtev.recycle();
        return true;
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public void setNestedScrollingEnabled(boolean enabled) {
        this.mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean isNestedScrollingEnabled() {
        return this.mChildHelper.isNestedScrollingEnabled();
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean startNestedScroll(int axes) {
        return this.mChildHelper.startNestedScroll(axes);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public void stopNestedScroll() {
        this.mChildHelper.stopNestedScroll();
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean hasNestedScrollingParent() {
        return this.mChildHelper.hasNestedScrollingParent();
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return this.mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return this.mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return this.mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return this.mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public static void setDefaultHeader(String className) {
        HEADER_CLASS_NAME = className;
    }

    public static void setDefaultFooter(String className) {
        FOOTER_CLASS_NAME = className;
    }

    public void startRefresh() {
        this.cp.startRefresh();
    }

    public void startLoadMore() {
        this.cp.startLoadMore();
    }

    public void finishRefreshing() {
        this.cp.finishRefreshing();
    }

    public void finishLoadmore() {
        this.cp.finishLoadmore();
    }

    public void setTargetView(View targetView) {
        if (targetView != null) {
            this.mChildView = targetView;
        }
    }

    public void setDecorator(IDecorator decorator1) {
        if (decorator1 != null) {
            this.decorator = decorator1;
        }
    }

    public void setHeaderView(IHeaderView headerView) {
        if (headerView != null) {
            this.mHeadLayout.removeAllViewsInLayout();
            this.mHeadLayout.addView(headerView.getView());
            this.mHeadView = headerView;
        }
    }

    @Deprecated
    public void addFixedExHeader(View view) {
        FrameLayout frameLayout;
        if (view != null && (frameLayout = this.mExtraHeadLayout) != null) {
            frameLayout.addView(view);
            this.mExtraHeadLayout.bringToFront();
            if (this.floatRefresh) {
                this.mHeadLayout.bringToFront();
            }
            this.cp.onAddExHead();
            this.cp.setExHeadFixed();
        }
    }

    public View getExtraHeaderView() {
        return this.mExtraHeadLayout;
    }

    public void setBottomView(IBottomView bottomView) {
        if (bottomView != null) {
            this.mBottomLayout.removeAllViewsInLayout();
            this.mBottomLayout.addView(bottomView.getView());
            this.mBottomView = bottomView;
        }
    }

    public void setFloatRefresh(boolean ifOpenFloatRefreshMode) {
        this.floatRefresh = ifOpenFloatRefreshMode;
        if (!this.floatRefresh) {
            return;
        }
        post(new Runnable() { // from class: com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout.2
            @Override // java.lang.Runnable
            public void run() {
                if (TwinklingRefreshLayout.this.mHeadLayout != null) {
                    TwinklingRefreshLayout.this.mHeadLayout.bringToFront();
                }
            }
        });
    }

    public void setMaxHeadHeight(float maxHeightDp) {
        this.mMaxHeadHeight = DensityUtil.dp2px(getContext(), maxHeightDp);
    }

    public void setHeaderHeight(float headHeightDp) {
        this.mHeadHeight = DensityUtil.dp2px(getContext(), headHeightDp);
    }

    public void setMaxBottomHeight(float maxBottomHeight) {
        this.mMaxBottomHeight = DensityUtil.dp2px(getContext(), maxBottomHeight);
    }

    public void setBottomHeight(float bottomHeightDp) {
        this.mBottomHeight = DensityUtil.dp2px(getContext(), bottomHeightDp);
    }

    public void setEnableLoadmore(boolean enableLoadmore1) {
        this.enableLoadmore = enableLoadmore1;
        IBottomView iBottomView = this.mBottomView;
        if (iBottomView != null) {
            if (!this.enableLoadmore) {
                iBottomView.getView().setVisibility(8);
            } else {
                iBottomView.getView().setVisibility(0);
            }
        }
    }

    public void setEnableRefresh(boolean enableRefresh1) {
        this.enableRefresh = enableRefresh1;
        IHeaderView iHeaderView = this.mHeadView;
        if (iHeaderView != null) {
            if (!this.enableRefresh) {
                iHeaderView.getView().setVisibility(8);
            } else {
                iHeaderView.getView().setVisibility(0);
            }
        }
    }

    public void setOverScrollTopShow(boolean isOverScrollTopShow) {
        this.isOverScrollTopShow = isOverScrollTopShow;
    }

    public void setOverScrollBottomShow(boolean isOverScrollBottomShow) {
        this.isOverScrollBottomShow = isOverScrollBottomShow;
    }

    public void setOverScrollRefreshShow(boolean isOverScrollRefreshShow) {
        this.isOverScrollTopShow = isOverScrollRefreshShow;
        this.isOverScrollBottomShow = isOverScrollRefreshShow;
    }

    public void setEnableOverScroll(boolean enableOverScroll1) {
        this.enableOverScroll = enableOverScroll1;
    }

    public void setPureScrollModeOn() {
        this.isPureScrollModeOn = true;
        this.isOverScrollTopShow = false;
        this.isOverScrollBottomShow = false;
        setMaxHeadHeight(this.mOverScrollHeight);
        setHeaderHeight(this.mOverScrollHeight);
        setMaxBottomHeight(this.mOverScrollHeight);
        setBottomHeight(this.mOverScrollHeight);
    }

    public void setOverScrollHeight(float overScrollHeightDp) {
        this.mOverScrollHeight = DensityUtil.dp2px(getContext(), overScrollHeightDp);
    }

    public void setAutoLoadMore(boolean ifAutoLoadMore) {
        this.autoLoadMore = ifAutoLoadMore;
        if (!this.autoLoadMore) {
            return;
        }
        setEnableLoadmore(true);
    }

    public void showRefreshingWhenOverScroll(boolean ifShow) {
        this.showRefreshingWhenOverScroll = ifShow;
    }

    public void showLoadingWhenOverScroll(boolean ifShow) {
        this.showLoadingWhenOverScroll = ifShow;
    }

    public void setEnableKeepIView(boolean ifKeep) {
        this.enableKeepIView = ifKeep;
    }

    public void setOnRefreshListener(RefreshListenerAdapter refreshListener) {
        if (refreshListener != null) {
            this.refreshListener = refreshListener;
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onPullingDown(TwinklingRefreshLayout refreshLayout, float fraction) {
        RefreshListenerAdapter refreshListenerAdapter;
        this.mHeadView.onPullingDown(fraction, this.mMaxHeadHeight, this.mHeadHeight);
        if (this.enableRefresh && (refreshListenerAdapter = this.refreshListener) != null) {
            refreshListenerAdapter.onPullingDown(refreshLayout, fraction);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onPullingUp(TwinklingRefreshLayout refreshLayout, float fraction) {
        RefreshListenerAdapter refreshListenerAdapter;
        this.mBottomView.onPullingUp(fraction, this.mMaxHeadHeight, this.mHeadHeight);
        if (this.enableLoadmore && (refreshListenerAdapter = this.refreshListener) != null) {
            refreshListenerAdapter.onPullingUp(refreshLayout, fraction);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onPullDownReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
        RefreshListenerAdapter refreshListenerAdapter;
        this.mHeadView.onPullReleasing(fraction, this.mMaxHeadHeight, this.mHeadHeight);
        if (this.enableRefresh && (refreshListenerAdapter = this.refreshListener) != null) {
            refreshListenerAdapter.onPullDownReleasing(refreshLayout, fraction);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onPullUpReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
        RefreshListenerAdapter refreshListenerAdapter;
        this.mBottomView.onPullReleasing(fraction, this.mMaxBottomHeight, this.mBottomHeight);
        if (this.enableLoadmore && (refreshListenerAdapter = this.refreshListener) != null) {
            refreshListenerAdapter.onPullUpReleasing(refreshLayout, fraction);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onRefresh(TwinklingRefreshLayout refreshLayout) {
        this.mHeadView.startAnim(this.mMaxHeadHeight, this.mHeadHeight);
        RefreshListenerAdapter refreshListenerAdapter = this.refreshListener;
        if (refreshListenerAdapter != null) {
            refreshListenerAdapter.onRefresh(refreshLayout);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
        this.mBottomView.startAnim(this.mMaxBottomHeight, this.mBottomHeight);
        RefreshListenerAdapter refreshListenerAdapter = this.refreshListener;
        if (refreshListenerAdapter != null) {
            refreshListenerAdapter.onLoadMore(refreshLayout);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onFinishRefresh() {
        RefreshListenerAdapter refreshListenerAdapter = this.refreshListener;
        if (refreshListenerAdapter != null) {
            refreshListenerAdapter.onFinishRefresh();
        }
        if (this.cp.isEnableKeepIView() || this.cp.isRefreshing()) {
            this.mHeadView.onFinish(new OnAnimEndListener() { // from class: com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout.3
                @Override // com.lcodecore.tkrefreshlayout.OnAnimEndListener
                public void onAnimEnd() {
                    TwinklingRefreshLayout.this.cp.finishRefreshAfterAnim();
                }
            });
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onFinishLoadMore() {
        RefreshListenerAdapter refreshListenerAdapter = this.refreshListener;
        if (refreshListenerAdapter != null) {
            refreshListenerAdapter.onFinishLoadMore();
        }
        if (this.cp.isEnableKeepIView() || this.cp.isLoadingMore()) {
            this.mBottomView.onFinish();
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onRefreshCanceled() {
        RefreshListenerAdapter refreshListenerAdapter = this.refreshListener;
        if (refreshListenerAdapter != null) {
            refreshListenerAdapter.onRefreshCanceled();
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.PullListener
    public void onLoadmoreCanceled() {
        RefreshListenerAdapter refreshListenerAdapter = this.refreshListener;
        if (refreshListenerAdapter != null) {
            refreshListenerAdapter.onLoadmoreCanceled();
        }
    }

    /* loaded from: classes.dex */
    public class CoContext {
        private static final int EX_MODE_FIXED = 1;
        private static final int EX_MODE_NORMAL = 0;
        private static final int PULLING_BOTTOM_UP = 1;
        private static final int PULLING_TOP_DOWN = 0;
        private int state = 0;
        private int exHeadMode = 0;
        private boolean isExHeadLocked = true;
        private boolean prepareFinishRefresh = false;
        private boolean prepareFinishLoadMore = false;
        private AnimProcessor animProcessor = new AnimProcessor(this);

        public CoContext() {
        }

        public void init() {
            if (TwinklingRefreshLayout.this.isPureScrollModeOn) {
                TwinklingRefreshLayout.this.setOverScrollTopShow(false);
                TwinklingRefreshLayout.this.setOverScrollBottomShow(false);
                if (TwinklingRefreshLayout.this.mHeadLayout != null) {
                    TwinklingRefreshLayout.this.mHeadLayout.setVisibility(8);
                }
                if (TwinklingRefreshLayout.this.mBottomLayout != null) {
                    TwinklingRefreshLayout.this.mBottomLayout.setVisibility(8);
                }
            }
        }

        public AnimProcessor getAnimProcessor() {
            return this.animProcessor;
        }

        public boolean isEnableKeepIView() {
            return TwinklingRefreshLayout.this.enableKeepIView;
        }

        public boolean showRefreshingWhenOverScroll() {
            return TwinklingRefreshLayout.this.showRefreshingWhenOverScroll;
        }

        public boolean showLoadingWhenOverScroll() {
            return TwinklingRefreshLayout.this.showLoadingWhenOverScroll;
        }

        public float getMaxHeadHeight() {
            return TwinklingRefreshLayout.this.mMaxHeadHeight;
        }

        public int getHeadHeight() {
            return (int) TwinklingRefreshLayout.this.mHeadHeight;
        }

        public int getExtraHeadHeight() {
            return TwinklingRefreshLayout.this.mExtraHeadLayout.getHeight();
        }

        public int getMaxBottomHeight() {
            return (int) TwinklingRefreshLayout.this.mMaxBottomHeight;
        }

        public int getBottomHeight() {
            return (int) TwinklingRefreshLayout.this.mBottomHeight;
        }

        public int getOsHeight() {
            return (int) TwinklingRefreshLayout.this.mOverScrollHeight;
        }

        public View getTargetView() {
            return TwinklingRefreshLayout.this.mChildView;
        }

        public View getHeader() {
            return TwinklingRefreshLayout.this.mHeadLayout;
        }

        public View getFooter() {
            return TwinklingRefreshLayout.this.mBottomLayout;
        }

        public int getTouchSlop() {
            return TwinklingRefreshLayout.this.mTouchSlop;
        }

        public void resetHeaderView() {
            if (TwinklingRefreshLayout.this.mHeadView != null) {
                TwinklingRefreshLayout.this.mHeadView.reset();
            }
        }

        public void resetBottomView() {
            if (TwinklingRefreshLayout.this.mBottomView != null) {
                TwinklingRefreshLayout.this.mBottomView.reset();
            }
        }

        public View getExHead() {
            return TwinklingRefreshLayout.this.mExtraHeadLayout;
        }

        public void setExHeadNormal() {
            this.exHeadMode = 0;
        }

        public void setExHeadFixed() {
            this.exHeadMode = 1;
        }

        public boolean isExHeadNormal() {
            return this.exHeadMode == 0;
        }

        public boolean isExHeadFixed() {
            return this.exHeadMode == 1;
        }

        public boolean isExHeadLocked() {
            return this.isExHeadLocked;
        }

        public void onAddExHead() {
            this.isExHeadLocked = false;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) TwinklingRefreshLayout.this.mChildView.getLayoutParams();
            params.addRule(3, TwinklingRefreshLayout.this.mExtraHeadLayout.getId());
            TwinklingRefreshLayout.this.mChildView.setLayoutParams(params);
            TwinklingRefreshLayout.this.requestLayout();
        }

        public void startRefresh() {
            TwinklingRefreshLayout.this.post(new Runnable() { // from class: com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout.CoContext.1
                @Override // java.lang.Runnable
                public void run() {
                    CoContext.this.setStatePTD();
                    if (!TwinklingRefreshLayout.this.isPureScrollModeOn && TwinklingRefreshLayout.this.mChildView != null) {
                        CoContext.this.setRefreshing(true);
                        CoContext.this.animProcessor.animHeadToRefresh();
                    }
                }
            });
        }

        public void startLoadMore() {
            TwinklingRefreshLayout.this.post(new Runnable() { // from class: com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout.CoContext.2
                @Override // java.lang.Runnable
                public void run() {
                    CoContext.this.setStatePBU();
                    if (!TwinklingRefreshLayout.this.isPureScrollModeOn && TwinklingRefreshLayout.this.mChildView != null) {
                        CoContext.this.setLoadingMore(true);
                        CoContext.this.animProcessor.animBottomToLoad();
                    }
                }
            });
        }

        public void finishRefreshing() {
            onFinishRefresh();
        }

        public void finishRefreshAfterAnim() {
            if (TwinklingRefreshLayout.this.mChildView != null) {
                this.animProcessor.animHeadBack(true);
            }
        }

        public void finishLoadmore() {
            onFinishLoadMore();
            if (TwinklingRefreshLayout.this.mChildView != null) {
                this.animProcessor.animBottomBack(true);
            }
        }

        public boolean enableOverScroll() {
            return TwinklingRefreshLayout.this.enableOverScroll;
        }

        public boolean allowPullDown() {
            return TwinklingRefreshLayout.this.enableRefresh || TwinklingRefreshLayout.this.enableOverScroll;
        }

        public boolean allowPullUp() {
            return TwinklingRefreshLayout.this.enableLoadmore || TwinklingRefreshLayout.this.enableOverScroll;
        }

        public boolean enableRefresh() {
            return TwinklingRefreshLayout.this.enableRefresh;
        }

        public boolean enableLoadmore() {
            return TwinklingRefreshLayout.this.enableLoadmore;
        }

        public boolean allowOverScroll() {
            return !TwinklingRefreshLayout.this.isRefreshVisible && !TwinklingRefreshLayout.this.isLoadingVisible;
        }

        public boolean isRefreshVisible() {
            return TwinklingRefreshLayout.this.isRefreshVisible;
        }

        public boolean isLoadingVisible() {
            return TwinklingRefreshLayout.this.isLoadingVisible;
        }

        public void setRefreshVisible(boolean visible) {
            TwinklingRefreshLayout.this.isRefreshVisible = visible;
        }

        public void setLoadVisible(boolean visible) {
            TwinklingRefreshLayout.this.isLoadingVisible = visible;
        }

        public void setRefreshing(boolean refreshing) {
            TwinklingRefreshLayout.this.isRefreshing = refreshing;
        }

        public boolean isRefreshing() {
            return TwinklingRefreshLayout.this.isRefreshing;
        }

        public boolean isLoadingMore() {
            return TwinklingRefreshLayout.this.isLoadingMore;
        }

        public void setLoadingMore(boolean loadingMore) {
            TwinklingRefreshLayout.this.isLoadingMore = loadingMore;
        }

        public boolean isOpenFloatRefresh() {
            return TwinklingRefreshLayout.this.floatRefresh;
        }

        public boolean autoLoadMore() {
            return TwinklingRefreshLayout.this.autoLoadMore;
        }

        public boolean isPureScrollModeOn() {
            return TwinklingRefreshLayout.this.isPureScrollModeOn;
        }

        public boolean isOverScrollTopShow() {
            return TwinklingRefreshLayout.this.isOverScrollTopShow;
        }

        public boolean isOverScrollBottomShow() {
            return TwinklingRefreshLayout.this.isOverScrollBottomShow;
        }

        public void onPullingDown(float offsetY) {
            PullListener pullListener = TwinklingRefreshLayout.this.pullListener;
            TwinklingRefreshLayout twinklingRefreshLayout = TwinklingRefreshLayout.this;
            pullListener.onPullingDown(twinklingRefreshLayout, offsetY / twinklingRefreshLayout.mHeadHeight);
        }

        public void onPullingUp(float offsetY) {
            PullListener pullListener = TwinklingRefreshLayout.this.pullListener;
            TwinklingRefreshLayout twinklingRefreshLayout = TwinklingRefreshLayout.this;
            pullListener.onPullingUp(twinklingRefreshLayout, offsetY / twinklingRefreshLayout.mBottomHeight);
        }

        public void onRefresh() {
            TwinklingRefreshLayout.this.pullListener.onRefresh(TwinklingRefreshLayout.this);
        }

        public void onLoadMore() {
            TwinklingRefreshLayout.this.pullListener.onLoadMore(TwinklingRefreshLayout.this);
        }

        public void onFinishRefresh() {
            TwinklingRefreshLayout.this.pullListener.onFinishRefresh();
        }

        public void onFinishLoadMore() {
            TwinklingRefreshLayout.this.pullListener.onFinishLoadMore();
        }

        public void onPullDownReleasing(float offsetY) {
            PullListener pullListener = TwinklingRefreshLayout.this.pullListener;
            TwinklingRefreshLayout twinklingRefreshLayout = TwinklingRefreshLayout.this;
            pullListener.onPullDownReleasing(twinklingRefreshLayout, offsetY / twinklingRefreshLayout.mHeadHeight);
        }

        public void onPullUpReleasing(float offsetY) {
            PullListener pullListener = TwinklingRefreshLayout.this.pullListener;
            TwinklingRefreshLayout twinklingRefreshLayout = TwinklingRefreshLayout.this;
            pullListener.onPullUpReleasing(twinklingRefreshLayout, offsetY / twinklingRefreshLayout.mBottomHeight);
        }

        public boolean dispatchTouchEventSuper(MotionEvent ev) {
            return TwinklingRefreshLayout.super.dispatchTouchEvent(ev);
        }

        public void onRefreshCanceled() {
            TwinklingRefreshLayout.this.pullListener.onRefreshCanceled();
        }

        public void onLoadmoreCanceled() {
            TwinklingRefreshLayout.this.pullListener.onLoadmoreCanceled();
        }

        public void setStatePTD() {
            this.state = 0;
        }

        public void setStatePBU() {
            this.state = 1;
        }

        public boolean isStatePTD() {
            return this.state == 0;
        }

        public boolean isStatePBU() {
            return 1 == this.state;
        }

        public boolean isPrepareFinishRefresh() {
            return this.prepareFinishRefresh;
        }

        public boolean isPrepareFinishLoadMore() {
            return this.prepareFinishLoadMore;
        }

        public void setPrepareFinishRefresh(boolean prepareFinishRefresh) {
            this.prepareFinishRefresh = prepareFinishRefresh;
        }

        public void setPrepareFinishLoadMore(boolean prepareFinishLoadMore) {
            this.prepareFinishLoadMore = prepareFinishLoadMore;
        }
    }
}
