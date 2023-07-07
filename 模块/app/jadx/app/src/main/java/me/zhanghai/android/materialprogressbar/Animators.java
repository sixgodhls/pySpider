package me.zhanghai.android.materialprogressbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import me.zhanghai.android.materialprogressbar.Interpolators;
import me.zhanghai.android.materialprogressbar.internal.ObjectAnimatorCompat;

/* loaded from: classes.dex */
class Animators {
    private static final Path PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X = new Path();
    private static final Path PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X = new Path();
    private static final Path PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X = new Path();
    private static final Path PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X = new Path();

    private Animators() {
    }

    static {
        PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X.moveTo(-522.6f, 0.0f);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X.rCubicTo(48.89972f, 0.0f, 166.02657f, 0.0f, 301.2173f, 0.0f);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X.rCubicTo(197.58128f, 0.0f, 420.9827f, 0.0f, 420.9827f, 0.0f);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X.moveTo(0.0f, 0.1f);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X.lineTo(1.0f, 0.8268492f);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X.lineTo(2.0f, 0.1f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.moveTo(-197.6f, 0.0f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.rCubicTo(14.28182f, 0.0f, 85.07782f, 0.0f, 135.54689f, 0.0f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.rCubicTo(54.26191f, 0.0f, 90.42461f, 0.0f, 168.24332f, 0.0f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.rCubicTo(144.72154f, 0.0f, 316.40982f, 0.0f, 316.40982f, 0.0f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.moveTo(0.0f, 0.1f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.lineTo(1.0f, 0.5713795f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.lineTo(2.0f, 0.90995026f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.lineTo(3.0f, 0.1f);
    }

    public static Animator createIndeterminateHorizontalRect1(Object target) {
        ObjectAnimator translateXAnimator = ObjectAnimatorCompat.ofFloat(target, "translateX", (String) null, PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X);
        translateXAnimator.setDuration(2000L);
        translateXAnimator.setInterpolator(Interpolators.INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X.INSTANCE);
        translateXAnimator.setRepeatCount(-1);
        ObjectAnimator scaleXAnimator = ObjectAnimatorCompat.ofFloat(target, (String) null, "scaleX", PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X);
        scaleXAnimator.setDuration(2000L);
        scaleXAnimator.setInterpolator(Interpolators.INDETERMINATE_HORIZONTAL_RECT1_SCALE_X.INSTANCE);
        scaleXAnimator.setRepeatCount(-1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateXAnimator, scaleXAnimator);
        return animatorSet;
    }

    public static Animator createIndeterminateHorizontalRect2(Object target) {
        ObjectAnimator translateXAnimator = ObjectAnimatorCompat.ofFloat(target, "translateX", (String) null, PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X);
        translateXAnimator.setDuration(2000L);
        translateXAnimator.setInterpolator(Interpolators.INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.INSTANCE);
        translateXAnimator.setRepeatCount(-1);
        ObjectAnimator scaleXAnimator = ObjectAnimatorCompat.ofFloat(target, (String) null, "scaleX", PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X);
        scaleXAnimator.setDuration(2000L);
        scaleXAnimator.setInterpolator(Interpolators.INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.INSTANCE);
        scaleXAnimator.setRepeatCount(-1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateXAnimator, scaleXAnimator);
        return animatorSet;
    }

    public static Animator createIndeterminate(Object target) {
        ObjectAnimator trimPathStartAnimator = ObjectAnimator.ofFloat(target, "trimPathStart", 0.0f, 0.75f);
        trimPathStartAnimator.setDuration(1333L);
        trimPathStartAnimator.setInterpolator(Interpolators.TRIM_PATH_START.INSTANCE);
        trimPathStartAnimator.setRepeatCount(-1);
        ObjectAnimator trimPathEndAnimator = ObjectAnimator.ofFloat(target, "trimPathEnd", 0.0f, 0.75f);
        trimPathEndAnimator.setDuration(1333L);
        trimPathEndAnimator.setInterpolator(Interpolators.TRIM_PATH_END.INSTANCE);
        trimPathEndAnimator.setRepeatCount(-1);
        ObjectAnimator trimPathOffsetAnimator = ObjectAnimator.ofFloat(target, "trimPathOffset", 0.0f, 0.25f);
        trimPathOffsetAnimator.setDuration(1333L);
        trimPathOffsetAnimator.setInterpolator(Interpolators.LINEAR.INSTANCE);
        trimPathOffsetAnimator.setRepeatCount(-1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(trimPathStartAnimator, trimPathEndAnimator, trimPathOffsetAnimator);
        return animatorSet;
    }

    public static Animator createIndeterminateRotation(Object target) {
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(target, "rotation", 0.0f, 720.0f);
        rotationAnimator.setDuration(6665L);
        rotationAnimator.setInterpolator(Interpolators.LINEAR.INSTANCE);
        rotationAnimator.setRepeatCount(-1);
        return rotationAnimator;
    }
}
