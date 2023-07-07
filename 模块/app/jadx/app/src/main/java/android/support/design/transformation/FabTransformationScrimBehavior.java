package android.support.design.transformation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.animation.AnimatorSetCompat;
import android.support.design.animation.MotionTiming;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FabTransformationScrimBehavior extends ExpandableTransformationBehavior {
    public static final long COLLAPSE_DELAY = 0;
    public static final long COLLAPSE_DURATION = 150;
    public static final long EXPAND_DELAY = 75;
    public static final long EXPAND_DURATION = 150;
    private final MotionTiming expandTiming = new MotionTiming(75, 150);
    private final MotionTiming collapseTiming = new MotionTiming(0, 150);

    public FabTransformationScrimBehavior() {
    }

    public FabTransformationScrimBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.support.design.transformation.ExpandableBehavior, android.support.design.widget.CoordinatorLayout.Behavior
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof FloatingActionButton;
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        return super.onTouchEvent(parent, child, ev);
    }

    @Override // android.support.design.transformation.ExpandableTransformationBehavior
    @NonNull
    protected AnimatorSet onCreateExpandedStateChangeAnimation(View dependency, final View child, final boolean expanded, boolean isAnimating) {
        List<Animator> animations = new ArrayList<>();
        List<Animator.AnimatorListener> listeners = new ArrayList<>();
        createScrimAnimation(child, expanded, isAnimating, animations, listeners);
        AnimatorSet set = new AnimatorSet();
        AnimatorSetCompat.playTogether(set, animations);
        set.addListener(new AnimatorListenerAdapter() { // from class: android.support.design.transformation.FabTransformationScrimBehavior.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                if (expanded) {
                    child.setVisibility(0);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!expanded) {
                    child.setVisibility(4);
                }
            }
        });
        return set;
    }

    private void createScrimAnimation(View child, boolean expanded, boolean currentlyAnimating, List<Animator> animations, List<Animator.AnimatorListener> unusedListeners) {
        Animator animator;
        MotionTiming timing = expanded ? this.expandTiming : this.collapseTiming;
        if (expanded) {
            if (!currentlyAnimating) {
                child.setAlpha(0.0f);
            }
            animator = ObjectAnimator.ofFloat(child, View.ALPHA, 1.0f);
        } else {
            animator = ObjectAnimator.ofFloat(child, View.ALPHA, 0.0f);
        }
        timing.apply(animator);
        animations.add(animator);
    }
}
