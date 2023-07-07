package android.support.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.p000v4.content.res.TypedArrayUtils;
import android.support.p000v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
public class Slide extends Visibility {
    private static final String PROPNAME_SCREEN_POSITION = "android:slide:screenPosition";
    private CalculateSlide mSlideCalculator = sCalculateBottom;
    private int mSlideEdge = 80;
    private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerate = new AccelerateInterpolator();
    private static final CalculateSlide sCalculateLeft = new CalculateSlideHorizontal() { // from class: android.support.transition.Slide.1
        @Override // android.support.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            return view.getTranslationX() - sceneRoot.getWidth();
        }
    };
    private static final CalculateSlide sCalculateStart = new CalculateSlideHorizontal() { // from class: android.support.transition.Slide.2
        @Override // android.support.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            boolean z = true;
            if (ViewCompat.getLayoutDirection(sceneRoot) != 1) {
                z = false;
            }
            boolean isRtl = z;
            if (isRtl) {
                float x = view.getTranslationX() + sceneRoot.getWidth();
                return x;
            }
            float x2 = view.getTranslationX() - sceneRoot.getWidth();
            return x2;
        }
    };
    private static final CalculateSlide sCalculateTop = new CalculateSlideVertical() { // from class: android.support.transition.Slide.3
        @Override // android.support.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view) {
            return view.getTranslationY() - sceneRoot.getHeight();
        }
    };
    private static final CalculateSlide sCalculateRight = new CalculateSlideHorizontal() { // from class: android.support.transition.Slide.4
        @Override // android.support.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            return view.getTranslationX() + sceneRoot.getWidth();
        }
    };
    private static final CalculateSlide sCalculateEnd = new CalculateSlideHorizontal() { // from class: android.support.transition.Slide.5
        @Override // android.support.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            boolean z = true;
            if (ViewCompat.getLayoutDirection(sceneRoot) != 1) {
                z = false;
            }
            boolean isRtl = z;
            if (isRtl) {
                float x = view.getTranslationX() - sceneRoot.getWidth();
                return x;
            }
            float x2 = view.getTranslationX() + sceneRoot.getWidth();
            return x2;
        }
    };
    private static final CalculateSlide sCalculateBottom = new CalculateSlideVertical() { // from class: android.support.transition.Slide.6
        @Override // android.support.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view) {
            return view.getTranslationY() + sceneRoot.getHeight();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface CalculateSlide {
        float getGoneX(ViewGroup viewGroup, View view);

        float getGoneY(ViewGroup viewGroup, View view);
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public @interface GravityFlag {
    }

    /* loaded from: classes.dex */
    private static abstract class CalculateSlideHorizontal implements CalculateSlide {
        private CalculateSlideHorizontal() {
        }

        @Override // android.support.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view) {
            return view.getTranslationY();
        }
    }

    /* loaded from: classes.dex */
    private static abstract class CalculateSlideVertical implements CalculateSlide {
        private CalculateSlideVertical() {
        }

        @Override // android.support.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            return view.getTranslationX();
        }
    }

    public Slide() {
        setSlideEdge(80);
    }

    public Slide(int slideEdge) {
        setSlideEdge(slideEdge);
    }

    public Slide(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.SLIDE);
        int edge = TypedArrayUtils.getNamedInt(a, (XmlPullParser) attrs, "slideEdge", 0, 80);
        a.recycle();
        setSlideEdge(edge);
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        transitionValues.values.put(PROPNAME_SCREEN_POSITION, position);
    }

    @Override // android.support.transition.Visibility, android.support.transition.Transition
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override // android.support.transition.Visibility, android.support.transition.Transition
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    public void setSlideEdge(int slideEdge) {
        if (slideEdge == 3) {
            this.mSlideCalculator = sCalculateLeft;
        } else if (slideEdge == 5) {
            this.mSlideCalculator = sCalculateRight;
        } else if (slideEdge == 48) {
            this.mSlideCalculator = sCalculateTop;
        } else if (slideEdge == 80) {
            this.mSlideCalculator = sCalculateBottom;
        } else if (slideEdge == 8388611) {
            this.mSlideCalculator = sCalculateStart;
        } else if (slideEdge == 8388613) {
            this.mSlideCalculator = sCalculateEnd;
        } else {
            throw new IllegalArgumentException("Invalid slide direction");
        }
        this.mSlideEdge = slideEdge;
        SidePropagation propagation = new SidePropagation();
        propagation.setSide(slideEdge);
        setPropagation(propagation);
    }

    public int getSlideEdge() {
        return this.mSlideEdge;
    }

    @Override // android.support.transition.Visibility
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (endValues == null) {
            return null;
        }
        int[] position = (int[]) endValues.values.get(PROPNAME_SCREEN_POSITION);
        float endX = view.getTranslationX();
        float endY = view.getTranslationY();
        float startX = this.mSlideCalculator.getGoneX(sceneRoot, view);
        float startY = this.mSlideCalculator.getGoneY(sceneRoot, view);
        return TranslationAnimationCreator.createAnimation(view, endValues, position[0], position[1], startX, startY, endX, endY, sDecelerate);
    }

    @Override // android.support.transition.Visibility
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null) {
            return null;
        }
        int[] position = (int[]) startValues.values.get(PROPNAME_SCREEN_POSITION);
        float startX = view.getTranslationX();
        float startY = view.getTranslationY();
        float endX = this.mSlideCalculator.getGoneX(sceneRoot, view);
        float endY = this.mSlideCalculator.getGoneY(sceneRoot, view);
        return TranslationAnimationCreator.createAnimation(view, startValues, position[0], position[1], startX, startY, endX, endY, sAccelerate);
    }
}