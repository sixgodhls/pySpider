package android.support.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public class ChangeScroll extends Transition {
    private static final String PROPNAME_SCROLL_X = "android:changeScroll:x";
    private static final String PROPNAME_SCROLL_Y = "android:changeScroll:y";
    private static final String[] PROPERTIES = {PROPNAME_SCROLL_X, PROPNAME_SCROLL_Y};

    public ChangeScroll() {
    }

    public ChangeScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.support.transition.Transition
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.support.transition.Transition
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.support.transition.Transition
    @Nullable
    public String[] getTransitionProperties() {
        return PROPERTIES;
    }

    private void captureValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_SCROLL_X, Integer.valueOf(transitionValues.view.getScrollX()));
        transitionValues.values.put(PROPNAME_SCROLL_Y, Integer.valueOf(transitionValues.view.getScrollY()));
    }

    @Override // android.support.transition.Transition
    @Nullable
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        View view = endValues.view;
        int startX = ((Integer) startValues.values.get(PROPNAME_SCROLL_X)).intValue();
        int endX = ((Integer) endValues.values.get(PROPNAME_SCROLL_X)).intValue();
        int startY = ((Integer) startValues.values.get(PROPNAME_SCROLL_Y)).intValue();
        int endY = ((Integer) endValues.values.get(PROPNAME_SCROLL_Y)).intValue();
        Animator scrollXAnimator = null;
        Animator scrollYAnimator = null;
        if (startX != endX) {
            view.setScrollX(startX);
            scrollXAnimator = ObjectAnimator.ofInt(view, "scrollX", startX, endX);
        }
        if (startY != endY) {
            view.setScrollY(startY);
            scrollYAnimator = ObjectAnimator.ofInt(view, "scrollY", startY, endY);
        }
        return TransitionUtils.mergeAnimators(scrollXAnimator, scrollYAnimator);
    }
}