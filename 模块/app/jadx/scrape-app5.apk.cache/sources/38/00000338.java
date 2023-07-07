package com.lcodecore.tkrefreshlayout.footer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.lcodecore.tkrefreshlayout.IBottomView;
import com.lcodecore.tkrefreshlayout.utils.DensityUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class BallPulseView extends View implements IBottomView {
    public static final int DEFAULT_SIZE = 50;
    private int animatingColor;
    private float circleSpacing;
    private ArrayList<ValueAnimator> mAnimators;
    private Paint mPaint;
    private Map<ValueAnimator, ValueAnimator.AnimatorUpdateListener> mUpdateListeners;
    private int normalColor;
    private float[] scaleFloats;

    public BallPulseView(Context context) {
        this(context, null);
    }

    public BallPulseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BallPulseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.scaleFloats = new float[]{1.0f, 1.0f, 1.0f};
        this.mUpdateListeners = new HashMap();
        this.normalColor = -1118482;
        this.animatingColor = -1615546;
        int default_size = DensityUtil.dp2px(context, 50.0f);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(default_size, default_size, 17);
        setLayoutParams(params);
        this.circleSpacing = DensityUtil.dp2px(context, 4.0f);
        this.mPaint = new Paint();
        this.mPaint.setColor(-1);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);
    }

    public void setIndicatorColor(int color) {
        this.mPaint.setColor(color);
    }

    public void setNormalColor(@ColorInt int color) {
        this.normalColor = color;
    }

    public void setAnimatingColor(@ColorInt int color) {
        this.animatingColor = color;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float radius = (Math.min(getWidth(), getHeight()) - (this.circleSpacing * 2.0f)) / 6.0f;
        float x = (getWidth() / 2) - ((radius * 2.0f) + this.circleSpacing);
        float y = getHeight() / 2;
        for (int i = 0; i < 3; i++) {
            canvas.save();
            float translateX = (radius * 2.0f * i) + x + (this.circleSpacing * i);
            canvas.translate(translateX, y);
            float[] fArr = this.scaleFloats;
            canvas.scale(fArr[i], fArr[i]);
            canvas.drawCircle(0.0f, 0.0f, radius, this.mPaint);
            canvas.restore();
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAnimators != null) {
            for (int i = 0; i < this.mAnimators.size(); i++) {
                this.mAnimators.get(i).cancel();
            }
        }
    }

    public void startAnim() {
        if (this.mAnimators == null) {
            createAnimators();
        }
        if (this.mAnimators != null && !isStarted()) {
            for (int i = 0; i < this.mAnimators.size(); i++) {
                ValueAnimator animator = this.mAnimators.get(i);
                ValueAnimator.AnimatorUpdateListener updateListener = this.mUpdateListeners.get(animator);
                if (updateListener != null) {
                    animator.addUpdateListener(updateListener);
                }
                animator.start();
            }
            int i2 = this.animatingColor;
            setIndicatorColor(i2);
        }
    }

    public void stopAnim() {
        ArrayList<ValueAnimator> arrayList = this.mAnimators;
        if (arrayList != null) {
            Iterator<ValueAnimator> it = arrayList.iterator();
            while (it.hasNext()) {
                ValueAnimator animator = it.next();
                if (animator != null && animator.isStarted()) {
                    animator.removeAllUpdateListeners();
                    animator.end();
                }
            }
        }
        setIndicatorColor(this.normalColor);
    }

    private boolean isStarted() {
        Iterator<ValueAnimator> it = this.mAnimators.iterator();
        if (it.hasNext()) {
            ValueAnimator animator = it.next();
            return animator.isStarted();
        }
        return false;
    }

    private void createAnimators() {
        this.mAnimators = new ArrayList<>();
        int[] delays = {120, 240, 360};
        for (int i = 0; i < 3; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1.0f, 0.3f, 1.0f);
            scaleAnim.setDuration(750L);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            this.mUpdateListeners.put(scaleAnim, new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.footer.BallPulseView.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    BallPulseView.this.scaleFloats[index] = ((Float) animation.getAnimatedValue()).floatValue();
                    BallPulseView.this.postInvalidate();
                }
            });
            this.mAnimators.add(scaleAnim);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public View getView() {
        return this;
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void onPullingUp(float fraction, float maxHeadHeight, float headHeight) {
        stopAnim();
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void startAnim(float maxHeadHeight, float headHeight) {
        startAnim();
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        stopAnim();
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void onFinish() {
        stopAnim();
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void reset() {
        stopAnim();
    }
}