package com.lcodecore.tkrefreshlayout.processor;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.utils.ScrollingUtil;

/* loaded from: classes.dex */
public class OverScrollDecorator extends Decorator {
    private static final int ALL_DELAY_TIMES = 60;
    private static final int MSG_CONTINUE_COMPUTE_SCROLL = 1;
    private static final int MSG_START_COMPUTE_SCROLL = 0;
    private static final int MSG_STOP_COMPUTE_SCROLL = 2;
    private static final int OVER_SCROLL_MIN_VX = 3000;
    private float mVelocityY;
    private int cur_delay_times = 0;
    private boolean preventTopOverScroll = false;
    private boolean preventBottomOverScroll = false;
    private boolean checkOverScroll = false;
    private Handler mHandler = new Handler() { // from class: com.lcodecore.tkrefreshlayout.processor.OverScrollDecorator.1
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int mTouchSlop = OverScrollDecorator.this.cp.getTouchSlop();
            switch (msg.what) {
                case 0:
                    OverScrollDecorator.this.cur_delay_times = -1;
                    break;
                case 1:
                    break;
                case 2:
                    OverScrollDecorator.this.cur_delay_times = 60;
                    return;
                default:
                    return;
            }
            OverScrollDecorator.access$008(OverScrollDecorator.this);
            View mChildView = OverScrollDecorator.this.cp.getTargetView();
            if (OverScrollDecorator.this.cp.allowOverScroll()) {
                if (OverScrollDecorator.this.mVelocityY < 3000.0f) {
                    if (OverScrollDecorator.this.mVelocityY <= -3000.0f && ScrollingUtil.isViewToBottom(mChildView, mTouchSlop)) {
                        OverScrollDecorator.this.cp.getAnimProcessor().animOverScrollBottom(OverScrollDecorator.this.mVelocityY, OverScrollDecorator.this.cur_delay_times);
                        OverScrollDecorator.this.mVelocityY = 0.0f;
                        OverScrollDecorator.this.cur_delay_times = 60;
                    }
                } else if (ScrollingUtil.isViewToTop(mChildView, mTouchSlop)) {
                    OverScrollDecorator.this.cp.getAnimProcessor().animOverScrollTop(OverScrollDecorator.this.mVelocityY, OverScrollDecorator.this.cur_delay_times);
                    OverScrollDecorator.this.mVelocityY = 0.0f;
                    OverScrollDecorator.this.cur_delay_times = 60;
                }
            }
            if (OverScrollDecorator.this.cur_delay_times < 60) {
                OverScrollDecorator.this.mHandler.sendEmptyMessageDelayed(1, 10L);
            }
        }
    };

    static /* synthetic */ int access$008(OverScrollDecorator x0) {
        int i = x0.cur_delay_times;
        x0.cur_delay_times = i + 1;
        return i;
    }

    public OverScrollDecorator(TwinklingRefreshLayout.CoContext processor, IDecorator decorator1) {
        super(processor, decorator1);
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return this.decorator != null && this.decorator.dispatchTouchEvent(ev);
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public boolean interceptTouchEvent(MotionEvent ev) {
        return this.decorator != null && this.decorator.interceptTouchEvent(ev);
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public boolean dealTouchEvent(MotionEvent e) {
        return this.decorator != null && this.decorator.dealTouchEvent(e);
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerDown(MotionEvent ev) {
        if (this.decorator != null) {
            this.decorator.onFingerDown(ev);
        }
        this.preventTopOverScroll = ScrollingUtil.isViewToTop(this.cp.getTargetView(), this.cp.getTouchSlop());
        this.preventBottomOverScroll = ScrollingUtil.isViewToBottom(this.cp.getTargetView(), this.cp.getTouchSlop());
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerUp(MotionEvent ev, boolean isFling) {
        if (this.decorator != null) {
            this.decorator.onFingerUp(ev, this.checkOverScroll && isFling);
        }
        this.checkOverScroll = false;
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float velocityX, float velocityY) {
        if (this.decorator != null) {
            this.decorator.onFingerScroll(e1, e2, distanceX, distanceY, velocityX, velocityY);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IDecorator
    public void onFingerFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.decorator != null) {
            this.decorator.onFingerFling(e1, e2, velocityX, velocityY);
        }
        if (!this.cp.enableOverScroll()) {
            return;
        }
        int dy = (int) (e2.getY() - e1.getY());
        if (dy < (-this.cp.getTouchSlop()) && this.preventBottomOverScroll) {
            return;
        }
        if (dy > this.cp.getTouchSlop() && this.preventTopOverScroll) {
            return;
        }
        this.mVelocityY = velocityY;
        if (Math.abs(this.mVelocityY) >= 3000.0f) {
            this.mHandler.sendEmptyMessage(0);
            this.checkOverScroll = true;
            return;
        }
        this.mVelocityY = 0.0f;
        this.cur_delay_times = 60;
    }
}
