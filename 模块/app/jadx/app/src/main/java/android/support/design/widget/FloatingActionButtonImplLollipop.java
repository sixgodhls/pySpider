package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.ripple.RippleUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(21)
/* loaded from: classes.dex */
class FloatingActionButtonImplLollipop extends FloatingActionButtonImpl {
    private InsetDrawable insetDrawable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatingActionButtonImplLollipop(VisibilityAwareImageButton view, ShadowViewDelegate shadowViewDelegate) {
        super(view, shadowViewDelegate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void setBackgroundDrawable(ColorStateList backgroundTint, PorterDuff.Mode backgroundTintMode, ColorStateList rippleColor, int borderWidth) {
        Drawable rippleContent;
        this.shapeDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.shapeDrawable, backgroundTint);
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(this.shapeDrawable, backgroundTintMode);
        }
        if (borderWidth > 0) {
            this.borderDrawable = createBorderDrawable(borderWidth, backgroundTint);
            rippleContent = new LayerDrawable(new Drawable[]{this.borderDrawable, this.shapeDrawable});
        } else {
            this.borderDrawable = null;
            rippleContent = this.shapeDrawable;
        }
        this.rippleDrawable = new RippleDrawable(RippleUtils.convertToRippleDrawableColor(rippleColor), rippleContent, null);
        this.contentBackground = this.rippleDrawable;
        this.shadowViewDelegate.setBackgroundDrawable(this.rippleDrawable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void setRippleColor(ColorStateList rippleColor) {
        if (this.rippleDrawable instanceof RippleDrawable) {
            ((RippleDrawable) this.rippleDrawable).setColor(RippleUtils.convertToRippleDrawableColor(rippleColor));
        } else {
            super.setRippleColor(rippleColor);
        }
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    void onElevationsChanged(float elevation, float hoveredFocusedTranslationZ, float pressedTranslationZ) {
        if (Build.VERSION.SDK_INT == 21) {
            this.view.refreshDrawableState();
        } else {
            android.animation.StateListAnimator stateListAnimator = new android.animation.StateListAnimator();
            stateListAnimator.addState(PRESSED_ENABLED_STATE_SET, createElevationAnimator(elevation, pressedTranslationZ));
            stateListAnimator.addState(HOVERED_FOCUSED_ENABLED_STATE_SET, createElevationAnimator(elevation, hoveredFocusedTranslationZ));
            stateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, createElevationAnimator(elevation, hoveredFocusedTranslationZ));
            stateListAnimator.addState(HOVERED_ENABLED_STATE_SET, createElevationAnimator(elevation, hoveredFocusedTranslationZ));
            AnimatorSet set = new AnimatorSet();
            List<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(this.view, "elevation", elevation).setDuration(0L));
            if (Build.VERSION.SDK_INT >= 22 && Build.VERSION.SDK_INT <= 24) {
                animators.add(ObjectAnimator.ofFloat(this.view, View.TRANSLATION_Z, this.view.getTranslationZ()).setDuration(100L));
            }
            animators.add(ObjectAnimator.ofFloat(this.view, View.TRANSLATION_Z, 0.0f).setDuration(100L));
            set.playSequentially((Animator[]) animators.toArray(new Animator[0]));
            set.setInterpolator(ELEVATION_ANIM_INTERPOLATOR);
            stateListAnimator.addState(ENABLED_STATE_SET, set);
            stateListAnimator.addState(EMPTY_STATE_SET, createElevationAnimator(0.0f, 0.0f));
            this.view.setStateListAnimator(stateListAnimator);
        }
        if (this.shadowViewDelegate.isCompatPaddingEnabled()) {
            updatePadding();
        }
    }

    @NonNull
    private Animator createElevationAnimator(float elevation, float translationZ) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(this.view, "elevation", elevation).setDuration(0L)).with(ObjectAnimator.ofFloat(this.view, View.TRANSLATION_Z, translationZ).setDuration(100L));
        set.setInterpolator(ELEVATION_ANIM_INTERPOLATOR);
        return set;
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    public float getElevation() {
        return this.view.getElevation();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void onCompatShadowChanged() {
        updatePadding();
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    void onPaddingUpdated(Rect padding) {
        if (this.shadowViewDelegate.isCompatPaddingEnabled()) {
            this.insetDrawable = new InsetDrawable(this.rippleDrawable, padding.left, padding.top, padding.right, padding.bottom);
            this.shadowViewDelegate.setBackgroundDrawable(this.insetDrawable);
            return;
        }
        this.shadowViewDelegate.setBackgroundDrawable(this.rippleDrawable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void onDrawableStateChanged(int[] state) {
        if (Build.VERSION.SDK_INT == 21) {
            if (this.view.isEnabled()) {
                this.view.setElevation(this.elevation);
                if (this.view.isPressed()) {
                    this.view.setTranslationZ(this.pressedTranslationZ);
                    return;
                } else if (this.view.isFocused() || this.view.isHovered()) {
                    this.view.setTranslationZ(this.hoveredFocusedTranslationZ);
                    return;
                } else {
                    this.view.setTranslationZ(0.0f);
                    return;
                }
            }
            this.view.setElevation(0.0f);
            this.view.setTranslationZ(0.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void jumpDrawableToCurrentState() {
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    boolean requirePreDrawListener() {
        return false;
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawableLollipop();
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    GradientDrawable newGradientDrawableForShape() {
        return new AlwaysStatefulGradientDrawable();
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    void getPadding(Rect rect) {
        if (this.shadowViewDelegate.isCompatPaddingEnabled()) {
            float radius = this.shadowViewDelegate.getRadius();
            float maxShadowSize = getElevation() + this.pressedTranslationZ;
            int hPadding = (int) Math.ceil(ShadowDrawableWrapper.calculateHorizontalPadding(maxShadowSize, radius, false));
            int vPadding = (int) Math.ceil(ShadowDrawableWrapper.calculateVerticalPadding(maxShadowSize, radius, false));
            rect.set(hPadding, vPadding, hPadding, vPadding);
            return;
        }
        rect.set(0, 0, 0, 0);
    }

    /* loaded from: classes.dex */
    static class AlwaysStatefulGradientDrawable extends GradientDrawable {
        AlwaysStatefulGradientDrawable() {
        }

        @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
        public boolean isStateful() {
            return true;
        }
    }
}
