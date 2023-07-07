package com.lcodecore.tkrefreshlayout.processor;

import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.utils.ScrollingUtil;

/* loaded from: classes.dex */
public class RefreshProcessor implements IDecorator {

    /* renamed from: cp */
    protected TwinklingRefreshLayout.CoContext f86cp;
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
        this.f86cp = processor;
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.downEventSent = false;
                this.intercepted = false;
                this.mTouchX = ev.getX();
                this.mTouchY = ev.getY();
                if (this.f86cp.isEnableKeepIView()) {
                    if (!this.f86cp.isRefreshing()) {
                        this.f86cp.setPrepareFinishRefresh(false);
                    }
                    if (!this.f86cp.isLoadingMore()) {
                        this.f86cp.setPrepareFinishLoadMore(false);
                    }
                }
                this.f86cp.dispatchTouchEventSuper(ev);
                return true;
            case 1:
            case 3:
                if (this.intercepted) {
                    if (this.f86cp.isStatePTD()) {
                        this.willAnimHead = true;
                    } else if (this.f86cp.isStatePBU()) {
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
                if (!this.intercepted && Math.abs(dx) <= Math.abs(dy) && Math.abs(dy) > this.f86cp.getTouchSlop()) {
                    if (dy > 0.0f && ScrollingUtil.isViewToTop(this.f86cp.getTargetView(), this.f86cp.getTouchSlop()) && this.f86cp.allowPullDown()) {
                        this.f86cp.setStatePTD();
                        this.mTouchX = ev.getX();
                        this.mTouchY = ev.getY();
                        sendCancelEvent();
                        this.intercepted = true;
                        return true;
                    } else if (dy < 0.0f && ScrollingUtil.isViewToBottom(this.f86cp.getTargetView(), this.f86cp.getTouchSlop()) && this.f86cp.allowPullUp()) {
                        this.f86cp.setStatePBU();
                        this.mTouchX = ev.getX();
                        this.mTouchY = ev.getY();
                        this.intercepted = true;
                        sendCancelEvent();
                        return true;
                    }
                }
                if (this.intercepted) {
                    if (this.f86cp.isRefreshVisible() || this.f86cp.isLoadingVisible()) {
                        return this.f86cp.dispatchTouchEventSuper(ev);
                    }
                    if (!this.f86cp.isPrepareFinishRefresh() && this.f86cp.isStatePTD()) {
                        if (dy < (-this.f86cp.getTouchSlop()) || !ScrollingUtil.isViewToTop(this.f86cp.getTargetView(), this.f86cp.getTouchSlop())) {
                            this.f86cp.dispatchTouchEventSuper(ev);
                        }
                        dy = Math.max(0.0f, Math.min(this.f86cp.getMaxHeadHeight() * 2.0f, dy));
                        this.f86cp.getAnimProcessor().scrollHeadByMove(dy);
                    } else if (!this.f86cp.isPrepareFinishLoadMore() && this.f86cp.isStatePBU()) {
                        if (dy > this.f86cp.getTouchSlop() || !ScrollingUtil.isViewToBottom(this.f86cp.getTargetView(), this.f86cp.getTouchSlop())) {
                            this.f86cp.dispatchTouchEventSuper(ev);
                        }
                        dy = Math.min(0.0f, Math.max((-this.f86cp.getMaxBottomHeight()) * 2, dy));
                        this.f86cp.getAnimProcessor().scrollBottomByMove(Math.abs(dy));
                    }
                    if (dy == 0.0f && !this.downEventSent) {
                        this.downEventSent = true;
                        sendDownEvent();
                    }
                    return true;
                }
                break;
        }
        return this.f86cp.dispatchTouchEventSuper(ev);
    }

    private void sendCancelEvent() {
        if (this.mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = this.mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), 3, last.getX(), last.getY(), last.getMetaState());
        this.f86cp.dispatchTouchEventSuper(e);
    }

    private void sendDownEvent() {
        MotionEvent last = this.mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), 0, last.getX(), last.getY(), last.getMetaState());
        this.f86cp.dispatchTouchEventSuper(e);
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
            this.f86cp.getAnimProcessor().dealPullDownRelease();
        }
        if (!isFling && this.willAnimBottom) {
            this.f86cp.getAnimProcessor().dealPullUpRelease();
        }
        this.willAnimHead = false;
        this.willAnimBottom = false;
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float velocityX, float velocityY) {
        int mTouchSlop = this.f86cp.getTouchSlop();
        if (this.f86cp.isRefreshVisible() && distanceY >= mTouchSlop && !this.f86cp.isOpenFloatRefresh()) {
            this.f86cp.getAnimProcessor().animHeadHideByVy((int) velocityY);
        }
        if (this.f86cp.isLoadingVisible() && distanceY <= (-mTouchSlop)) {
            this.f86cp.getAnimProcessor().animBottomHideByVy((int) velocityY);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    }
}