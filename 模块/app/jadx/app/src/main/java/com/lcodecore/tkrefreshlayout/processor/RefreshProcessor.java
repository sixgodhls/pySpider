package com.lcodecore.tkrefreshlayout.processor;

import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.utils.ScrollingUtil;

/* loaded from: classes.dex */
public class RefreshProcessor implements IDecorator {
    protected TwinklingRefreshLayout.CoContext cp;
    private MotionEvent mLastMoveEvent;
    private float mTouchX;
    private float mTouchY;
    private boolean intercepted = false;
    private boolean willAnimHead = false;
    private boolean willAnimBottom = false;
    private boolean downEventSent = false;

    public RefreshProcessor(TwinklingRefreshLayout.CoContext processor) {
        if (processor == null) {
            throw new NullPointerException("The coprocessor can not be null.");
        }
        this.cp = processor;
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.downEventSent = false;
                this.intercepted = false;
                this.mTouchX = ev.getX();
                this.mTouchY = ev.getY();
                if (this.cp.isEnableKeepIView()) {
                    if (!this.cp.isRefreshing()) {
                        this.cp.setPrepareFinishRefresh(false);
                    }
                    if (!this.cp.isLoadingMore()) {
                        this.cp.setPrepareFinishLoadMore(false);
                    }
                }
                this.cp.dispatchTouchEventSuper(ev);
                return true;
            case 1:
            case 3:
                if (this.intercepted) {
                    if (this.cp.isStatePTD()) {
                        this.willAnimHead = true;
                    } else if (this.cp.isStatePBU()) {
                        this.willAnimBottom = true;
                    }
                    this.intercepted = false;
                    return true;
                }
                break;
            case 2:
                this.mLastMoveEvent = ev;
                float dx = ev.getX() - this.mTouchX;
                float dy = ev.getY() - this.mTouchY;
                if (!this.intercepted && Math.abs(dx) <= Math.abs(dy) && Math.abs(dy) > this.cp.getTouchSlop()) {
                    if (dy > 0.0f && ScrollingUtil.isViewToTop(this.cp.getTargetView(), this.cp.getTouchSlop()) && this.cp.allowPullDown()) {
                        this.cp.setStatePTD();
                        this.mTouchX = ev.getX();
                        this.mTouchY = ev.getY();
                        sendCancelEvent();
                        this.intercepted = true;
                        return true;
                    } else if (dy < 0.0f && ScrollingUtil.isViewToBottom(this.cp.getTargetView(), this.cp.getTouchSlop()) && this.cp.allowPullUp()) {
                        this.cp.setStatePBU();
                        this.mTouchX = ev.getX();
                        this.mTouchY = ev.getY();
                        this.intercepted = true;
                        sendCancelEvent();
                        return true;
                    }
                }
                if (this.intercepted) {
                    if (this.cp.isRefreshVisible() || this.cp.isLoadingVisible()) {
                        return this.cp.dispatchTouchEventSuper(ev);
                    }
                    if (!this.cp.isPrepareFinishRefresh() && this.cp.isStatePTD()) {
                        if (dy < (-this.cp.getTouchSlop()) || !ScrollingUtil.isViewToTop(this.cp.getTargetView(), this.cp.getTouchSlop())) {
                            this.cp.dispatchTouchEventSuper(ev);
                        }
                        dy = Math.max(0.0f, Math.min(this.cp.getMaxHeadHeight() * 2.0f, dy));
                        this.cp.getAnimProcessor().scrollHeadByMove(dy);
                    } else if (!this.cp.isPrepareFinishLoadMore() && this.cp.isStatePBU()) {
                        if (dy > this.cp.getTouchSlop() || !ScrollingUtil.isViewToBottom(this.cp.getTargetView(), this.cp.getTouchSlop())) {
                            this.cp.dispatchTouchEventSuper(ev);
                        }
                        dy = Math.min(0.0f, Math.max((-this.cp.getMaxBottomHeight()) * 2, dy));
                        this.cp.getAnimProcessor().scrollBottomByMove(Math.abs(dy));
                    }
                    if (dy == 0.0f && !this.downEventSent) {
                        this.downEventSent = true;
                        sendDownEvent();
                    }
                    return true;
                }
                break;
        }
        return this.cp.dispatchTouchEventSuper(ev);
    }

    private void sendCancelEvent() {
        if (this.mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = this.mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), 3, last.getX(), last.getY(), last.getMetaState());
        this.cp.dispatchTouchEventSuper(e);
    }

    private void sendDownEvent() {
        MotionEvent last = this.mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), 0, last.getX(), last.getY(), last.getMetaState());
        this.cp.dispatchTouchEventSuper(e);
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public boolean interceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public boolean dealTouchEvent(MotionEvent e) {
        return false;
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerDown(MotionEvent ev) {
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerUp(MotionEvent ev, boolean isFling) {
        if (!isFling && this.willAnimHead) {
            this.cp.getAnimProcessor().dealPullDownRelease();
        }
        if (!isFling && this.willAnimBottom) {
            this.cp.getAnimProcessor().dealPullUpRelease();
        }
        this.willAnimHead = false;
        this.willAnimBottom = false;
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float velocityX, float velocityY) {
        int mTouchSlop = this.cp.getTouchSlop();
        if (this.cp.isRefreshVisible() && distanceY >= mTouchSlop && !this.cp.isOpenFloatRefresh()) {
            this.cp.getAnimProcessor().animHeadHideByVy((int) velocityY);
        }
        if (this.cp.isLoadingVisible() && distanceY <= (-mTouchSlop)) {
            this.cp.getAnimProcessor().animBottomHideByVy((int) velocityY);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    }
}
