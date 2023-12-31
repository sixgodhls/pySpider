package android.databinding.adapters;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

@BindingMethods({@BindingMethod(attribute = "android:alwaysDrawnWithCache", method = "setAlwaysDrawnWithCacheEnabled", type = ViewGroup.class), @BindingMethod(attribute = "android:animationCache", method = "setAnimationCacheEnabled", type = ViewGroup.class), @BindingMethod(attribute = "android:splitMotionEvents", method = "setMotionEventSplittingEnabled", type = ViewGroup.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class ViewGroupBindingAdapter {

    /* loaded from: classes.dex */
    public interface OnAnimationEnd {
        void onAnimationEnd(Animation animation);
    }

    /* loaded from: classes.dex */
    public interface OnAnimationRepeat {
        void onAnimationRepeat(Animation animation);
    }

    /* loaded from: classes.dex */
    public interface OnAnimationStart {
        void onAnimationStart(Animation animation);
    }

    /* loaded from: classes.dex */
    public interface OnChildViewAdded {
        void onChildViewAdded(View view, View view2);
    }

    /* loaded from: classes.dex */
    public interface OnChildViewRemoved {
        void onChildViewRemoved(View view, View view2);
    }

    @BindingAdapter({"android:animateLayoutChanges"})
    @TargetApi(11)
    public static void setAnimateLayoutChanges(ViewGroup view, boolean animate) {
        if (animate) {
            view.setLayoutTransition(new LayoutTransition());
        } else {
            view.setLayoutTransition(null);
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onChildViewAdded", "android:onChildViewRemoved"})
    public static void setListener(ViewGroup view, final OnChildViewAdded added, final OnChildViewRemoved removed) {
        if (added == null && removed == null) {
            view.setOnHierarchyChangeListener(null);
        } else {
            view.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() { // from class: android.databinding.adapters.ViewGroupBindingAdapter.1
                @Override // android.view.ViewGroup.OnHierarchyChangeListener
                public void onChildViewAdded(View parent, View child) {
                    OnChildViewAdded onChildViewAdded = OnChildViewAdded.this;
                    if (onChildViewAdded != null) {
                        onChildViewAdded.onChildViewAdded(parent, child);
                    }
                }

                @Override // android.view.ViewGroup.OnHierarchyChangeListener
                public void onChildViewRemoved(View parent, View child) {
                    OnChildViewRemoved onChildViewRemoved = removed;
                    if (onChildViewRemoved != null) {
                        onChildViewRemoved.onChildViewRemoved(parent, child);
                    }
                }
            });
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onAnimationStart", "android:onAnimationEnd", "android:onAnimationRepeat"})
    public static void setListener(ViewGroup view, final OnAnimationStart start, final OnAnimationEnd end, final OnAnimationRepeat repeat) {
        if (start == null && end == null && repeat == null) {
            view.setLayoutAnimationListener(null);
        } else {
            view.setLayoutAnimationListener(new Animation.AnimationListener() { // from class: android.databinding.adapters.ViewGroupBindingAdapter.2
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                    OnAnimationStart onAnimationStart = OnAnimationStart.this;
                    if (onAnimationStart != null) {
                        onAnimationStart.onAnimationStart(animation);
                    }
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    OnAnimationEnd onAnimationEnd = end;
                    if (onAnimationEnd != null) {
                        onAnimationEnd.onAnimationEnd(animation);
                    }
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                    OnAnimationRepeat onAnimationRepeat = repeat;
                    if (onAnimationRepeat != null) {
                        onAnimationRepeat.onAnimationRepeat(animation);
                    }
                }
            });
        }
    }
}