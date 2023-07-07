package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.RestrictTo;
import android.util.StateSet;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public final class StateListAnimator {
    private final ArrayList<Tuple> tuples = new ArrayList<>();
    private Tuple lastMatch = null;
    ValueAnimator runningAnimator = null;
    private final Animator.AnimatorListener animationListener = new AnimatorListenerAdapter() { // from class: android.support.design.widget.StateListAnimator.1
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (StateListAnimator.this.runningAnimator == animator) {
                StateListAnimator.this.runningAnimator = null;
            }
        }
    };

    public void addState(int[] specs, ValueAnimator animator) {
        Tuple tuple = new Tuple(specs, animator);
        animator.addListener(this.animationListener);
        this.tuples.add(tuple);
    }

    public void setState(int[] state) {
        Tuple match = null;
        int count = this.tuples.size();
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            Tuple tuple = this.tuples.get(i);
            if (!StateSet.stateSetMatches(tuple.specs, state)) {
                i++;
            } else {
                match = tuple;
                break;
            }
        }
        Tuple tuple2 = this.lastMatch;
        if (match == tuple2) {
            return;
        }
        if (tuple2 != null) {
            cancel();
        }
        this.lastMatch = match;
        if (match != null) {
            start(match);
        }
    }

    private void start(Tuple match) {
        this.runningAnimator = match.animator;
        this.runningAnimator.start();
    }

    private void cancel() {
        ValueAnimator valueAnimator = this.runningAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.runningAnimator = null;
        }
    }

    public void jumpToCurrentState() {
        ValueAnimator valueAnimator = this.runningAnimator;
        if (valueAnimator != null) {
            valueAnimator.end();
            this.runningAnimator = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Tuple {
        final ValueAnimator animator;
        final int[] specs;

        Tuple(int[] specs, ValueAnimator animator) {
            this.specs = specs;
            this.animator = animator;
        }
    }
}