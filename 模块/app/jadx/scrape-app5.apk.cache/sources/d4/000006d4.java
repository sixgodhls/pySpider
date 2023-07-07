package com.lcodecore.tkrefreshlayout.header.progresslayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.p000v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;

/* loaded from: classes.dex */
public class ProgressLayout extends FrameLayout implements IHeaderView {
    private static final int CIRCLE_BG_LIGHT = -328966;
    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    public static final int DEFAULT = 1;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    public static final int LARGE = 0;
    private static final int MAX_ALPHA = 255;
    private static final float MAX_PROGRESS_ANGLE = 0.8f;
    private static final int STARTING_PROGRESS_ALPHA = 76;
    private int mCircleHeight;
    private CircleImageView mCircleView;
    private int mCircleWidth;
    private boolean mIsBeingDragged;
    private MaterialProgressDrawable mProgress;

    public ProgressLayout(Context context) {
        this(context, null);
    }

    public ProgressLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsBeingDragged = false;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        this.mCircleWidth = (int) (metrics.density * 40.0f);
        this.mCircleHeight = (int) (metrics.density * 40.0f);
        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }

    private void createProgressView() {
        this.mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, 20.0f);
        this.mProgress = new MaterialProgressDrawable(getContext(), this);
        this.mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        this.mCircleView.setImageDrawable(this.mProgress);
        this.mCircleView.setVisibility(8);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2, 17);
        this.mCircleView.setLayoutParams(params);
        addView(this.mCircleView);
    }

    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        setProgressBackgroundColorSchemeColor(getResources().getColor(colorRes));
    }

    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        this.mCircleView.setBackgroundColor(color);
        this.mProgress.setBackgroundColor(color);
    }

    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    public void setColorSchemeColors(int... colors) {
        this.mProgress.setColorSchemeColors(colors);
    }

    public void setSize(int size) {
        if (size != 0 && size != 1) {
            return;
        }
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (size == 0) {
            int i = (int) (metrics.density * 56.0f);
            this.mCircleWidth = i;
            this.mCircleHeight = i;
        } else {
            int i2 = (int) (metrics.density * 40.0f);
            this.mCircleWidth = i2;
            this.mCircleHeight = i2;
        }
        this.mCircleView.setImageDrawable(null);
        this.mProgress.updateSizes(size);
        this.mCircleView.setImageDrawable(this.mProgress);
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void reset() {
        this.mCircleView.clearAnimation();
        this.mProgress.stop();
        this.mCircleView.setVisibility(8);
        this.mCircleView.getBackground().setAlpha(255);
        this.mProgress.setAlpha(255);
        ViewCompat.setScaleX(this.mCircleView, 0.0f);
        ViewCompat.setScaleY(this.mCircleView, 0.0f);
        ViewCompat.setAlpha(this.mCircleView, 1.0f);
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public View getView() {
        return this;
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (!this.mIsBeingDragged) {
            this.mIsBeingDragged = true;
            this.mProgress.setAlpha(76);
        }
        if (this.mCircleView.getVisibility() != 0) {
            this.mCircleView.setVisibility(0);
        }
        if (fraction >= 1.0f) {
            ViewCompat.setScaleX(this.mCircleView, 1.0f);
            ViewCompat.setScaleY(this.mCircleView, 1.0f);
        } else {
            ViewCompat.setScaleX(this.mCircleView, fraction);
            ViewCompat.setScaleY(this.mCircleView, fraction);
        }
        if (fraction <= 1.0f) {
            this.mProgress.setAlpha((int) ((179.0f * fraction) + 76.0f));
        }
        double d = fraction;
        Double.isNaN(d);
        float adjustedPercent = (((float) Math.max(d - 0.4d, 0.0d)) * 5.0f) / 3.0f;
        float strokeStart = adjustedPercent * MAX_PROGRESS_ANGLE;
        this.mProgress.setStartEndTrim(0.0f, Math.min((float) MAX_PROGRESS_ANGLE, strokeStart));
        this.mProgress.setArrowScale(Math.min(1.0f, adjustedPercent));
        float rotation = ((0.4f * adjustedPercent) - 0.25f) * 0.5f;
        this.mProgress.setProgressRotation(rotation);
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        this.mIsBeingDragged = false;
        if (fraction >= 1.0f) {
            ViewCompat.setScaleX(this.mCircleView, 1.0f);
            ViewCompat.setScaleY(this.mCircleView, 1.0f);
            return;
        }
        ViewCompat.setScaleX(this.mCircleView, fraction);
        ViewCompat.setScaleY(this.mCircleView, fraction);
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void startAnim(float maxHeadHeight, float headHeight) {
        this.mCircleView.setVisibility(0);
        this.mCircleView.getBackground().setAlpha(255);
        this.mProgress.setAlpha(255);
        ViewCompat.setScaleX(this.mCircleView, 1.0f);
        ViewCompat.setScaleY(this.mCircleView, 1.0f);
        this.mProgress.setArrowScale(1.0f);
        this.mProgress.start();
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onFinish(final OnAnimEndListener animEndListener) {
        this.mCircleView.animate().scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ProgressLayout.this.reset();
                animEndListener.onAnimEnd();
            }
        }).start();
    }
}