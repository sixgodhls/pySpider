package android.support.design.bottomappbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Dimension;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.design.R;
import android.support.design.animation.AnimationUtils;
import android.support.design.behavior.HideBottomViewOnScrollBehavior;
import android.support.design.internal.ThemeEnforcement;
import android.support.design.resources.MaterialResources;
import android.support.design.shape.MaterialShapeDrawable;
import android.support.design.shape.ShapePathModel;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BottomAppBar extends Toolbar implements CoordinatorLayout.AttachedBehavior {
    private static final long ANIMATION_DURATION = 300;
    public static final int FAB_ALIGNMENT_MODE_CENTER = 0;
    public static final int FAB_ALIGNMENT_MODE_END = 1;
    @Nullable
    private Animator attachAnimator;
    private int fabAlignmentMode;
    AnimatorListenerAdapter fabAnimationListener;
    private boolean fabAttached;
    private final int fabOffsetEndMode;
    private boolean hideOnScroll;
    private final MaterialShapeDrawable materialShapeDrawable;
    @Nullable
    private Animator menuAnimator;
    @Nullable
    private Animator modeAnimator;
    private final BottomAppBarTopEdgeTreatment topEdgeTreatment;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FabAlignmentMode {
    }

    public BottomAppBar(Context context) {
        this(context, null, 0);
    }

    public BottomAppBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.bottomAppBarStyle);
    }

    public BottomAppBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.fabAttached = true;
        this.fabAnimationListener = new AnimatorListenerAdapter() { // from class: android.support.design.bottomappbar.BottomAppBar.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                BottomAppBar bottomAppBar = BottomAppBar.this;
                bottomAppBar.maybeAnimateAttachChange(bottomAppBar.fabAttached);
                BottomAppBar bottomAppBar2 = BottomAppBar.this;
                bottomAppBar2.maybeAnimateMenuView(bottomAppBar2.fabAlignmentMode, BottomAppBar.this.fabAttached);
            }
        };
        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context, attrs, R.styleable.BottomAppBar, defStyleAttr, R.style.Widget_MaterialComponents_BottomAppBar, new int[0]);
        ColorStateList backgroundTint = MaterialResources.getColorStateList(context, a, R.styleable.BottomAppBar_backgroundTint);
        float fabCradleMargin = a.getDimensionPixelOffset(R.styleable.BottomAppBar_fabCradleMargin, 0);
        float fabCornerRadius = a.getDimensionPixelOffset(R.styleable.BottomAppBar_fabCradleRoundedCornerRadius, 0);
        float fabVerticalOffset = a.getDimensionPixelOffset(R.styleable.BottomAppBar_fabCradleVerticalOffset, 0);
        this.fabAlignmentMode = a.getInt(R.styleable.BottomAppBar_fabAlignmentMode, 0);
        this.hideOnScroll = a.getBoolean(R.styleable.BottomAppBar_hideOnScroll, false);
        a.recycle();
        this.fabOffsetEndMode = getResources().getDimensionPixelOffset(R.dimen.mtrl_bottomappbar_fabOffsetEndMode);
        this.topEdgeTreatment = new BottomAppBarTopEdgeTreatment(fabCradleMargin, fabCornerRadius, fabVerticalOffset);
        ShapePathModel appBarModel = new ShapePathModel();
        appBarModel.setTopEdge(this.topEdgeTreatment);
        this.materialShapeDrawable = new MaterialShapeDrawable(appBarModel);
        this.materialShapeDrawable.setShadowEnabled(true);
        this.materialShapeDrawable.setPaintStyle(Paint.Style.FILL);
        DrawableCompat.setTintList(this.materialShapeDrawable, backgroundTint);
        ViewCompat.setBackground(this, this.materialShapeDrawable);
    }

    public int getFabAlignmentMode() {
        return this.fabAlignmentMode;
    }

    public void setFabAlignmentMode(int fabAlignmentMode) {
        maybeAnimateModeChange(fabAlignmentMode);
        maybeAnimateMenuView(fabAlignmentMode, this.fabAttached);
        this.fabAlignmentMode = fabAlignmentMode;
    }

    public void setBackgroundTint(@Nullable ColorStateList backgroundTint) {
        DrawableCompat.setTintList(this.materialShapeDrawable, backgroundTint);
    }

    @Nullable
    public ColorStateList getBackgroundTint() {
        return this.materialShapeDrawable.getTintList();
    }

    public float getFabCradleMargin() {
        return this.topEdgeTreatment.getFabCradleMargin();
    }

    public void setFabCradleMargin(@Dimension float cradleMargin) {
        if (cradleMargin != getFabCradleMargin()) {
            this.topEdgeTreatment.setFabCradleMargin(cradleMargin);
            this.materialShapeDrawable.invalidateSelf();
        }
    }

    @Dimension
    public float getFabCradleRoundedCornerRadius() {
        return this.topEdgeTreatment.getFabCradleRoundedCornerRadius();
    }

    public void setFabCradleRoundedCornerRadius(@Dimension float roundedCornerRadius) {
        if (roundedCornerRadius != getFabCradleRoundedCornerRadius()) {
            this.topEdgeTreatment.setFabCradleRoundedCornerRadius(roundedCornerRadius);
            this.materialShapeDrawable.invalidateSelf();
        }
    }

    @Dimension
    public float getCradleVerticalOffset() {
        return this.topEdgeTreatment.getCradleVerticalOffset();
    }

    public void setCradleVerticalOffset(@Dimension float verticalOffset) {
        if (verticalOffset != getCradleVerticalOffset()) {
            this.topEdgeTreatment.setCradleVerticalOffset(verticalOffset);
            this.materialShapeDrawable.invalidateSelf();
        }
    }

    public boolean getHideOnScroll() {
        return this.hideOnScroll;
    }

    public void setHideOnScroll(boolean hide) {
        this.hideOnScroll = hide;
    }

    public void replaceMenu(@MenuRes int newMenu) {
        getMenu().clear();
        inflateMenu(newMenu);
    }

    void setFabDiameter(@Px int diameter) {
        if (diameter != this.topEdgeTreatment.getFabDiameter()) {
            this.topEdgeTreatment.setFabDiameter(diameter);
            this.materialShapeDrawable.invalidateSelf();
        }
    }

    private void maybeAnimateModeChange(int targetMode) {
        if (this.fabAlignmentMode == targetMode || !ViewCompat.isLaidOut(this)) {
            return;
        }
        Animator animator = this.modeAnimator;
        if (animator != null) {
            animator.cancel();
        }
        List<Animator> animators = new ArrayList<>();
        createCradleTranslationAnimation(targetMode, animators);
        createFabTranslationXAnimation(targetMode, animators);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        this.modeAnimator = set;
        this.modeAnimator.addListener(new AnimatorListenerAdapter() { // from class: android.support.design.bottomappbar.BottomAppBar.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                BottomAppBar.this.modeAnimator = null;
            }
        });
        this.modeAnimator.start();
    }

    private void createCradleTranslationAnimation(int targetMode, List<Animator> animators) {
        if (!this.fabAttached) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(this.topEdgeTreatment.getHorizontalOffset(), getFabTranslationX(targetMode));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.support.design.bottomappbar.BottomAppBar.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                BottomAppBar.this.topEdgeTreatment.setHorizontalOffset(((Float) animation.getAnimatedValue()).floatValue());
                BottomAppBar.this.materialShapeDrawable.invalidateSelf();
            }
        });
        animator.setDuration(ANIMATION_DURATION);
        animators.add(animator);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public FloatingActionButton findDependentFab() {
        if (!(getParent() instanceof CoordinatorLayout)) {
            return null;
        }
        List<View> dependents = ((CoordinatorLayout) getParent()).getDependents(this);
        for (View v : dependents) {
            if (v instanceof FloatingActionButton) {
                return (FloatingActionButton) v;
            }
        }
        return null;
    }

    private boolean isVisibleFab() {
        FloatingActionButton fab = findDependentFab();
        return fab != null && fab.isOrWillBeShown();
    }

    private void createFabTranslationXAnimation(int targetMode, List<Animator> animators) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(findDependentFab(), "translationX", getFabTranslationX(targetMode));
        animator.setDuration(ANIMATION_DURATION);
        animators.add(animator);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeAnimateMenuView(int targetMode, boolean newFabAttached) {
        if (!ViewCompat.isLaidOut(this)) {
            return;
        }
        Animator animator = this.menuAnimator;
        if (animator != null) {
            animator.cancel();
        }
        List<Animator> animators = new ArrayList<>();
        if (!isVisibleFab()) {
            targetMode = 0;
            newFabAttached = false;
        }
        createMenuViewTranslationAnimation(targetMode, newFabAttached, animators);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        this.menuAnimator = set;
        this.menuAnimator.addListener(new AnimatorListenerAdapter() { // from class: android.support.design.bottomappbar.BottomAppBar.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                BottomAppBar.this.menuAnimator = null;
            }
        });
        this.menuAnimator.start();
    }

    private void createMenuViewTranslationAnimation(final int targetMode, final boolean targetAttached, List<Animator> animators) {
        final ActionMenuView actionMenuView = getActionMenuView();
        if (actionMenuView == null) {
            return;
        }
        Animator fadeIn = ObjectAnimator.ofFloat(actionMenuView, "alpha", 1.0f);
        if ((this.fabAttached || (targetAttached && isVisibleFab())) && (this.fabAlignmentMode == 1 || targetMode == 1)) {
            Animator fadeOut = ObjectAnimator.ofFloat(actionMenuView, "alpha", 0.0f);
            fadeOut.addListener(new AnimatorListenerAdapter() { // from class: android.support.design.bottomappbar.BottomAppBar.4
                public boolean cancelled;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    this.cancelled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (!this.cancelled) {
                        BottomAppBar.this.translateActionMenuView(actionMenuView, targetMode, targetAttached);
                    }
                }
            });
            AnimatorSet set = new AnimatorSet();
            set.setDuration(150L);
            set.playSequentially(fadeOut, fadeIn);
            animators.add(set);
        } else if (actionMenuView.getAlpha() < 1.0f) {
            animators.add(fadeIn);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeAnimateAttachChange(boolean targetAttached) {
        if (!ViewCompat.isLaidOut(this)) {
            return;
        }
        Animator animator = this.attachAnimator;
        if (animator != null) {
            animator.cancel();
        }
        List<Animator> animators = new ArrayList<>();
        createCradleShapeAnimation(targetAttached && isVisibleFab(), animators);
        createFabTranslationYAnimation(targetAttached, animators);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        this.attachAnimator = set;
        this.attachAnimator.addListener(new AnimatorListenerAdapter() { // from class: android.support.design.bottomappbar.BottomAppBar.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                BottomAppBar.this.attachAnimator = null;
            }
        });
        this.attachAnimator.start();
    }

    private void createCradleShapeAnimation(boolean showCradle, List<Animator> animators) {
        if (showCradle) {
            this.topEdgeTreatment.setHorizontalOffset(getFabTranslationX());
        }
        float[] fArr = new float[2];
        fArr[0] = this.materialShapeDrawable.getInterpolation();
        fArr[1] = showCradle ? 1.0f : 0.0f;
        ValueAnimator animator = ValueAnimator.ofFloat(fArr);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.support.design.bottomappbar.BottomAppBar.6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                BottomAppBar.this.materialShapeDrawable.setInterpolation(((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        animator.setDuration(ANIMATION_DURATION);
        animators.add(animator);
    }

    private void createFabTranslationYAnimation(boolean targetAttached, List<Animator> animators) {
        FloatingActionButton fab = findDependentFab();
        if (fab == null) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(fab, "translationY", getFabTranslationY(targetAttached));
        animator.setDuration(ANIMATION_DURATION);
        animators.add(animator);
    }

    private float getFabTranslationY(boolean targetAttached) {
        FloatingActionButton fab = findDependentFab();
        if (fab == null) {
            return 0.0f;
        }
        Rect fabContentRect = new Rect();
        fab.getContentRect(fabContentRect);
        float fabHeight = fabContentRect.height();
        if (fabHeight == 0.0f) {
            fabHeight = fab.getMeasuredHeight();
        }
        float fabBottomShadow = fab.getHeight() - fabContentRect.bottom;
        float fabVerticalShadowPadding = fab.getHeight() - fabContentRect.height();
        float attached = (-getCradleVerticalOffset()) + (fabHeight / 2.0f) + fabBottomShadow;
        float detached = fabVerticalShadowPadding - fab.getPaddingBottom();
        return (-getMeasuredHeight()) + (targetAttached ? attached : detached);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getFabTranslationY() {
        return getFabTranslationY(this.fabAttached);
    }

    private int getFabTranslationX(int fabAlignmentMode) {
        int i = 1;
        boolean isRtl = ViewCompat.getLayoutDirection(this) == 1;
        if (fabAlignmentMode == 1) {
            int measuredWidth = (getMeasuredWidth() / 2) - this.fabOffsetEndMode;
            if (isRtl) {
                i = -1;
            }
            return measuredWidth * i;
        }
        return 0;
    }

    private float getFabTranslationX() {
        return getFabTranslationX(this.fabAlignmentMode);
    }

    @Nullable
    private ActionMenuView getActionMenuView() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof ActionMenuView) {
                return (ActionMenuView) view;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void translateActionMenuView(ActionMenuView actionMenuView, int fabAlignmentMode, boolean fabAttached) {
        int toolbarLeftContentEnd = 0;
        boolean isRtl = ViewCompat.getLayoutDirection(this) == 1;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            boolean isAlignedToStart = (view.getLayoutParams() instanceof Toolbar.LayoutParams) && (((Toolbar.LayoutParams) view.getLayoutParams()).gravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 8388611;
            if (isAlignedToStart) {
                toolbarLeftContentEnd = Math.max(toolbarLeftContentEnd, isRtl ? view.getLeft() : view.getRight());
            }
        }
        int end = isRtl ? actionMenuView.getRight() : actionMenuView.getLeft();
        int offset = toolbarLeftContentEnd - end;
        actionMenuView.setTranslationX((fabAlignmentMode != 1 || !fabAttached) ? 0.0f : offset);
    }

    private void cancelAnimations() {
        Animator animator = this.attachAnimator;
        if (animator != null) {
            animator.cancel();
        }
        Animator animator2 = this.menuAnimator;
        if (animator2 != null) {
            animator2.cancel();
        }
        Animator animator3 = this.modeAnimator;
        if (animator3 != null) {
            animator3.cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAnimationRunning() {
        Animator animator;
        Animator animator2;
        Animator animator3 = this.attachAnimator;
        return (animator3 != null && animator3.isRunning()) || ((animator = this.menuAnimator) != null && animator.isRunning()) || ((animator2 = this.modeAnimator) != null && animator2.isRunning());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.Toolbar, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        cancelAnimations();
        setCutoutState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCutoutState() {
        this.topEdgeTreatment.setHorizontalOffset(getFabTranslationX());
        FloatingActionButton fab = findDependentFab();
        this.materialShapeDrawable.setInterpolation((!this.fabAttached || !isVisibleFab()) ? 0.0f : 1.0f);
        if (fab != null) {
            fab.setTranslationY(getFabTranslationY());
            fab.setTranslationX(getFabTranslationX());
        }
        ActionMenuView actionMenuView = getActionMenuView();
        if (actionMenuView != null) {
            actionMenuView.setAlpha(1.0f);
            if (!isVisibleFab()) {
                translateActionMenuView(actionMenuView, 0, false);
            } else {
                translateActionMenuView(actionMenuView, this.fabAlignmentMode, this.fabAttached);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addFabAnimationListeners(@NonNull FloatingActionButton fab) {
        removeFabAnimationListeners(fab);
        fab.addOnHideAnimationListener(this.fabAnimationListener);
        fab.addOnShowAnimationListener(this.fabAnimationListener);
    }

    private void removeFabAnimationListeners(@NonNull FloatingActionButton fab) {
        fab.removeOnHideAnimationListener(this.fabAnimationListener);
        fab.removeOnShowAnimationListener(this.fabAnimationListener);
    }

    @Override // android.support.v7.widget.Toolbar
    public void setTitle(CharSequence title) {
    }

    @Override // android.support.v7.widget.Toolbar
    public void setSubtitle(CharSequence subtitle) {
    }

    @Override // android.support.design.widget.CoordinatorLayout.AttachedBehavior
    @NonNull
    public CoordinatorLayout.Behavior<BottomAppBar> getBehavior() {
        return new Behavior();
    }

    /* loaded from: classes.dex */
    public static class Behavior extends HideBottomViewOnScrollBehavior<BottomAppBar> {
        private final Rect fabContentRect = new Rect();

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        private boolean updateFabPositionAndVisibility(FloatingActionButton fab, BottomAppBar child) {
            CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            fabLayoutParams.anchorGravity = 17;
            child.addFabAnimationListeners(fab);
            return true;
        }

        @Override // android.support.design.behavior.HideBottomViewOnScrollBehavior, android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onLayoutChild(CoordinatorLayout parent, BottomAppBar child, int layoutDirection) {
            FloatingActionButton fab = child.findDependentFab();
            if (fab != null) {
                updateFabPositionAndVisibility(fab, child);
                fab.getMeasuredContentRect(this.fabContentRect);
                child.setFabDiameter(this.fabContentRect.height());
            }
            if (!child.isAnimationRunning()) {
                child.setCutoutState();
            }
            parent.onLayoutChild(child, layoutDirection);
            return super.onLayoutChild(parent, (CoordinatorLayout) child, layoutDirection);
        }

        @Override // android.support.design.widget.CoordinatorLayout.Behavior
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomAppBar child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
            return child.getHideOnScroll() && super.onStartNestedScroll(coordinatorLayout, (CoordinatorLayout) child, directTargetChild, target, axes, type);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.support.design.behavior.HideBottomViewOnScrollBehavior
        public void slideUp(BottomAppBar child) {
            super.slideUp((Behavior) child);
            FloatingActionButton fab = child.findDependentFab();
            if (fab != null) {
                fab.clearAnimation();
                fab.animate().translationY(child.getFabTranslationY()).setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR).setDuration(225L);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.support.design.behavior.HideBottomViewOnScrollBehavior
        public void slideDown(BottomAppBar child) {
            super.slideDown((Behavior) child);
            FloatingActionButton fab = child.findDependentFab();
            if (fab != null) {
                fab.getContentRect(this.fabContentRect);
                float fabShadowPadding = fab.getMeasuredHeight() - this.fabContentRect.height();
                fab.clearAnimation();
                fab.animate().translationY((-fab.getPaddingBottom()) + fabShadowPadding).setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR).setDuration(175L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.Toolbar, android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.fabAlignmentMode = this.fabAlignmentMode;
        savedState.fabAttached = this.fabAttached;
        return savedState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.Toolbar, android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.fabAlignmentMode = savedState.fabAlignmentMode;
        this.fabAttached = savedState.fabAttached;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.design.bottomappbar.BottomAppBar.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            /* renamed from: createFromParcel */
            public SavedState mo22createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override // android.os.Parcelable.Creator
            /* renamed from: createFromParcel */
            public SavedState mo21createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override // android.os.Parcelable.Creator
            /* renamed from: newArray */
            public SavedState[] mo23newArray(int size) {
                return new SavedState[size];
            }
        };
        int fabAlignmentMode;
        boolean fabAttached;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.fabAlignmentMode = in.readInt();
            this.fabAttached = in.readInt() != 0;
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.fabAlignmentMode);
            out.writeInt(this.fabAttached ? 1 : 0);
        }
    }
}
