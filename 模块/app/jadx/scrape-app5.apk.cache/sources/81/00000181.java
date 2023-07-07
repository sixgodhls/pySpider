package com.lcodecore.tkrefreshlayout.header.bezierlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class RippleView extends View {
    private OnRippleEndListener listener;
    private Paint mPaint;

    /* renamed from: r */
    private int f79r;

    /* renamed from: va */
    ValueAnimator f80va;

    /* loaded from: classes.dex */
    public interface OnRippleEndListener {
        void onRippleEnd();
    }

    public int getR() {
        return this.f79r;
    }

    public void setR(int r) {
        this.f79r = r;
    }

    public RippleView(Context context) {
        this(context, null, 0);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-1);
        this.mPaint.setStyle(Paint.Style.FILL);
    }

    public void setRippleColor(@ColorInt int color) {
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColor(color);
        }
    }

    public void startReveal() {
        setVisibility(0);
        if (this.f80va == null) {
            int bigRadius = (int) Math.sqrt(Math.pow(getHeight(), 2.0d) + Math.pow(getWidth(), 2.0d));
            this.f80va = ValueAnimator.ofInt(0, bigRadius / 2);
            this.f80va.setDuration(bigRadius);
            this.f80va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.header.bezierlayout.RippleView.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    RippleView.this.f79r = ((Integer) animation.getAnimatedValue()).intValue() * 2;
                    RippleView.this.invalidate();
                }
            });
            this.f80va.addListener(new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.header.bezierlayout.RippleView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (RippleView.this.listener != null) {
                        RippleView.this.listener.onRippleEnd();
                    }
                }
            });
        }
        this.f80va.start();
    }

    public void stopAnim() {
        ValueAnimator valueAnimator = this.f80va;
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            return;
        }
        this.f80va.cancel();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.f79r, this.mPaint);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ValueAnimator valueAnimator = this.f80va;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void setRippleEndListener(OnRippleEndListener listener) {
        this.listener = listener;
    }
}