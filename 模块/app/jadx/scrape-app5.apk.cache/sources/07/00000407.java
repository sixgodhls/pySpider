package android.support.design.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AnimatorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.p000v4.util.SimpleArrayMap;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MotionSpec {
    private static final String TAG = "MotionSpec";
    private final SimpleArrayMap<String, MotionTiming> timings = new SimpleArrayMap<>();

    public boolean hasTiming(String name) {
        return this.timings.get(name) != null;
    }

    public MotionTiming getTiming(String name) {
        if (!hasTiming(name)) {
            throw new IllegalArgumentException();
        }
        return this.timings.get(name);
    }

    public void setTiming(String name, @Nullable MotionTiming timing) {
        this.timings.put(name, timing);
    }

    public long getTotalDuration() {
        long duration = 0;
        int count = this.timings.size();
        for (int i = 0; i < count; i++) {
            MotionTiming timing = this.timings.valueAt(i);
            duration = Math.max(duration, timing.getDelay() + timing.getDuration());
        }
        return duration;
    }

    @Nullable
    public static MotionSpec createFromAttribute(Context context, TypedArray attributes, @StyleableRes int index) {
        int resourceId;
        if (attributes.hasValue(index) && (resourceId = attributes.getResourceId(index, 0)) != 0) {
            return createFromResource(context, resourceId);
        }
        return null;
    }

    @Nullable
    public static MotionSpec createFromResource(Context context, @AnimatorRes int id) {
        try {
            Animator animator = AnimatorInflater.loadAnimator(context, id);
            if (animator instanceof AnimatorSet) {
                AnimatorSet set = (AnimatorSet) animator;
                return createSpecFromAnimators(set.getChildAnimations());
            } else if (animator == null) {
                return null;
            } else {
                List<Animator> animators = new ArrayList<>();
                animators.add(animator);
                return createSpecFromAnimators(animators);
            }
        } catch (Exception e) {
            Log.w(TAG, "Can't load animation resource ID #0x" + Integer.toHexString(id), e);
            return null;
        }
    }

    private static MotionSpec createSpecFromAnimators(List<Animator> animators) {
        MotionSpec spec = new MotionSpec();
        int count = animators.size();
        for (int i = 0; i < count; i++) {
            addTimingFromAnimator(spec, animators.get(i));
        }
        return spec;
    }

    private static void addTimingFromAnimator(MotionSpec spec, Animator animator) {
        if (animator instanceof ObjectAnimator) {
            ObjectAnimator anim = (ObjectAnimator) animator;
            spec.setTiming(anim.getPropertyName(), MotionTiming.createFromAnimator(anim));
            return;
        }
        throw new IllegalArgumentException("Animator must be an ObjectAnimator: " + animator);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MotionSpec that = (MotionSpec) o;
        return this.timings.equals(that.timings);
    }

    public int hashCode() {
        return this.timings.hashCode();
    }

    public String toString() {
        return '\n' + getClass().getName() + '{' + Integer.toHexString(System.identityHashCode(this)) + " timings: " + this.timings + "}\n";
    }
}