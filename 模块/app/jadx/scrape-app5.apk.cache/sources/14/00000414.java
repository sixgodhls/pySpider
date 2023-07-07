package android.support.design.transformation;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.expandable.ExpandableWidget;
import android.support.design.widget.CoordinatorLayout;
import android.support.p000v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import java.util.List;

/* loaded from: classes.dex */
public abstract class ExpandableBehavior extends CoordinatorLayout.Behavior<View> {
    private static final int STATE_COLLAPSED = 2;
    private static final int STATE_EXPANDED = 1;
    private static final int STATE_UNINITIALIZED = 0;
    private int currentState = 0;

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public abstract boolean layoutDependsOn(CoordinatorLayout coordinatorLayout, View view, View view2);

    protected abstract boolean onExpandedStateChange(View view, View view2, boolean z, boolean z2);

    public ExpandableBehavior() {
    }

    public ExpandableBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    @CallSuper
    public boolean onLayoutChild(CoordinatorLayout parent, final View child, int layoutDirection) {
        final ExpandableWidget dep;
        if (!ViewCompat.isLaidOut(child) && (dep = findExpandableWidget(parent, child)) != null && didStateChange(dep.isExpanded())) {
            this.currentState = dep.isExpanded() ? 1 : 2;
            final int expectedState = this.currentState;
            child.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: android.support.design.transformation.ExpandableBehavior.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    child.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (ExpandableBehavior.this.currentState == expectedState) {
                        ExpandableBehavior expandableBehavior = ExpandableBehavior.this;
                        ExpandableWidget expandableWidget = dep;
                        expandableBehavior.onExpandedStateChange((View) expandableWidget, child, expandableWidget.isExpanded(), false);
                    }
                    return false;
                }
            });
            return false;
        }
        return false;
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    @CallSuper
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        ExpandableWidget dep = (ExpandableWidget) dependency;
        boolean expanded = dep.isExpanded();
        if (didStateChange(expanded)) {
            this.currentState = dep.isExpanded() ? 1 : 2;
            return onExpandedStateChange((View) dep, child, dep.isExpanded(), true);
        }
        return false;
    }

    @Nullable
    protected ExpandableWidget findExpandableWidget(CoordinatorLayout parent, View child) {
        List<View> dependencies = parent.getDependencies(child);
        int size = dependencies.size();
        for (int i = 0; i < size; i++) {
            View dependency = dependencies.get(i);
            if (layoutDependsOn(parent, child, dependency)) {
                return (ExpandableWidget) dependency;
            }
        }
        return null;
    }

    private boolean didStateChange(boolean expanded) {
        if (!expanded) {
            return this.currentState == 1;
        }
        int i = this.currentState;
        return i == 0 || i == 2;
    }

    public static <T extends ExpandableBehavior> T from(View view, Class<T> klass) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior<?> behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
        if (!(behavior instanceof ExpandableBehavior)) {
            throw new IllegalArgumentException("The view is not associated with ExpandableBehavior");
        }
        return klass.cast(behavior);
    }
}