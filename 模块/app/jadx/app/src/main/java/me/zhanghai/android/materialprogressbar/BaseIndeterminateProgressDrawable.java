package me.zhanghai.android.materialprogressbar;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.support.v4.view.ViewCompat;
import me.zhanghai.android.materialprogressbar.internal.ThemeUtils;

/* loaded from: classes.dex */
abstract class BaseIndeterminateProgressDrawable extends BaseProgressDrawable implements Animatable {
    protected Animator[] mAnimators;

    @SuppressLint({"NewApi"})
    public BaseIndeterminateProgressDrawable(Context context) {
        int controlActivatedColor = ThemeUtils.getColorFromAttrRes(R.attr.colorControlActivated, ViewCompat.MEASURED_STATE_MASK, context);
        setTint(controlActivatedColor);
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isStarted()) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        Animator[] animatorArr;
        if (isStarted()) {
            return;
        }
        for (Animator animator : this.mAnimators) {
            animator.start();
        }
        invalidateSelf();
    }

    private boolean isStarted() {
        Animator[] animatorArr;
        for (Animator animator : this.mAnimators) {
            if (animator.isStarted()) {
                return true;
            }
        }
        return false;
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        Animator[] animatorArr;
        for (Animator animator : this.mAnimators) {
            animator.end();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        Animator[] animatorArr;
        for (Animator animator : this.mAnimators) {
            if (animator.isRunning()) {
                return true;
            }
        }
        return false;
    }
}
