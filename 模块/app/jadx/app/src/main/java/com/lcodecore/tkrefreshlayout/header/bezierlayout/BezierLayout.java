package com.lcodecore.tkrefreshlayout.header.bezierlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.lcodecore.tkrefreshlayout.R;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.RippleView;

/* loaded from: classes.dex */
public class BezierLayout extends FrameLayout implements IHeaderView {
    private ValueAnimator circleAnimator;
    View headView;
    RoundDotView r1;
    RoundProgressView r2;
    RippleView rippleView;
    private ValueAnimator waveAnimator;
    WaveView waveView;

    public BezierLayout(Context context) {
        this(context, null);
    }

    public BezierLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.headView = LayoutInflater.from(getContext()).inflate(R.layout.view_bezier, (ViewGroup) null);
        this.waveView = (WaveView) this.headView.findViewById(R.id.draweeView);
        this.rippleView = (RippleView) this.headView.findViewById(R.id.ripple);
        this.r1 = (RoundDotView) this.headView.findViewById(R.id.round1);
        this.r2 = (RoundProgressView) this.headView.findViewById(R.id.round2);
        this.r2.setVisibility(8);
        addView(this.headView);
    }

    public void setWaveColor(@ColorInt int color) {
        this.waveView.setWaveColor(color);
    }

    public void setRippleColor(@ColorInt int color) {
        this.rippleView.setRippleColor(color);
    }

    public float limitValue(float a, float b) {
        float min = Math.min(a, b);
        float max = Math.max(a, b);
        float valve = 0.0f > min ? 0.0f : min;
        return valve < max ? valve : max;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ValueAnimator valueAnimator = this.waveAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.circleAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public View getView() {
        return this;
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (this.rippleView.getVisibility() == 0) {
            this.rippleView.setVisibility(8);
        }
        this.waveView.setHeadHeight((int) (limitValue(1.0f, fraction) * headHeight));
        this.waveView.setWaveHeight((int) (Math.max(0.0f, fraction - 1.0f) * maxHeadHeight));
        this.waveView.invalidate();
        this.r1.setCir_x((int) (limitValue(1.0f, fraction) * 30.0f));
        this.r1.setVisibility(0);
        this.r1.invalidate();
        this.r2.setVisibility(8);
        this.r2.animate().scaleX(0.1f);
        this.r2.animate().scaleY(0.1f);
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        this.waveView.setHeadHeight((int) (limitValue(1.0f, fraction) * headHeight));
        this.waveView.setWaveHeight((int) (Math.max(0.0f, fraction - 1.0f) * maxHeadHeight));
        this.waveView.invalidate();
        this.r1.setCir_x((int) (limitValue(1.0f, fraction) * 30.0f));
        this.r1.invalidate();
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void startAnim(float maxHeadHeight, float headHeight) {
        this.waveView.setHeadHeight((int) headHeight);
        this.waveAnimator = ValueAnimator.ofInt(this.waveView.getWaveHeight(), 0, -300, 0, -100, 0);
        this.waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                BezierLayout.this.waveView.setWaveHeight(((Integer) animation.getAnimatedValue()).intValue() / 2);
                BezierLayout.this.waveView.invalidate();
            }
        });
        this.waveAnimator.setInterpolator(new DecelerateInterpolator());
        this.waveAnimator.setDuration(800L);
        this.waveAnimator.start();
        this.circleAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.circleAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                BezierLayout.this.r1.setVisibility(8);
                BezierLayout.this.r2.setVisibility(0);
                BezierLayout.this.r2.animate().scaleX(1.0f);
                BezierLayout.this.r2.animate().scaleY(1.0f);
                BezierLayout.this.r2.postDelayed(new Runnable() { // from class: com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        BezierLayout.this.r2.startAnim();
                    }
                }, 200L);
            }
        });
        this.circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) animation.getAnimatedValue()).floatValue();
                BezierLayout.this.r1.setCir_x((int) ((-value) * 40.0f));
                BezierLayout.this.r1.invalidate();
            }
        });
        this.circleAnimator.setInterpolator(new DecelerateInterpolator());
        this.circleAnimator.setDuration(300L);
        this.circleAnimator.start();
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onFinish(final OnAnimEndListener animEndListener) {
        this.r2.stopAnim();
        this.r2.animate().scaleX(0.0f);
        this.r2.animate().scaleY(0.0f);
        this.rippleView.setRippleEndListener(new RippleView.OnRippleEndListener() { // from class: com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout.4
            @Override // com.lcodecore.tkrefreshlayout.header.bezierlayout.RippleView.OnRippleEndListener
            public void onRippleEnd() {
                animEndListener.onAnimEnd();
            }
        });
        this.rippleView.startReveal();
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void reset() {
        ValueAnimator valueAnimator = this.waveAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.waveAnimator.cancel();
        }
        this.waveView.setWaveHeight(0);
        ValueAnimator valueAnimator2 = this.circleAnimator;
        if (valueAnimator2 != null && valueAnimator2.isRunning()) {
            this.circleAnimator.cancel();
        }
        this.r1.setVisibility(0);
        this.r2.stopAnim();
        this.r2.setScaleX(0.0f);
        this.r2.setScaleY(0.0f);
        this.r2.setVisibility(8);
        this.rippleView.stopAnim();
        this.rippleView.setVisibility(8);
    }
}
