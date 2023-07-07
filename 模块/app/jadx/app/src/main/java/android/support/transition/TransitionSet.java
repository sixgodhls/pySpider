package android.support.transition;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.transition.Transition;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class TransitionSet extends Transition {
    private static final int FLAG_CHANGE_EPICENTER = 8;
    private static final int FLAG_CHANGE_INTERPOLATOR = 1;
    private static final int FLAG_CHANGE_PATH_MOTION = 4;
    private static final int FLAG_CHANGE_PROPAGATION = 2;
    public static final int ORDERING_SEQUENTIAL = 1;
    public static final int ORDERING_TOGETHER = 0;
    int mCurrentListeners;
    private ArrayList<Transition> mTransitions = new ArrayList<>();
    private boolean mPlayTogether = true;
    boolean mStarted = false;
    private int mChangeFlags = 0;

    public TransitionSet() {
    }

    public TransitionSet(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.TRANSITION_SET);
        int ordering = TypedArrayUtils.getNamedInt(a, (XmlResourceParser) attrs, "transitionOrdering", 0, 0);
        setOrdering(ordering);
        a.recycle();
    }

    @NonNull
    public TransitionSet setOrdering(int ordering) {
        switch (ordering) {
            case 0:
                this.mPlayTogether = true;
                break;
            case 1:
                this.mPlayTogether = false;
                break;
            default:
                throw new AndroidRuntimeException("Invalid parameter for TransitionSet ordering: " + ordering);
        }
        return this;
    }

    public int getOrdering() {
        return !this.mPlayTogether ? 1 : 0;
    }

    @NonNull
    public TransitionSet addTransition(@NonNull Transition transition) {
        this.mTransitions.add(transition);
        transition.mParent = this;
        if (this.mDuration >= 0) {
            transition.mo79setDuration(this.mDuration);
        }
        if ((this.mChangeFlags & 1) != 0) {
            transition.mo80setInterpolator(getInterpolator());
        }
        if ((this.mChangeFlags & 2) != 0) {
            transition.setPropagation(getPropagation());
        }
        if ((this.mChangeFlags & 4) != 0) {
            transition.setPathMotion(getPathMotion());
        }
        if ((this.mChangeFlags & 8) != 0) {
            transition.setEpicenterCallback(getEpicenterCallback());
        }
        return this;
    }

    public int getTransitionCount() {
        return this.mTransitions.size();
    }

    public Transition getTransitionAt(int index) {
        if (index < 0 || index >= this.mTransitions.size()) {
            return null;
        }
        return this.mTransitions.get(index);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: setDuration  reason: collision with other method in class */
    public TransitionSet mo79setDuration(long duration) {
        super.mo79setDuration(duration);
        if (this.mDuration >= 0) {
            int numTransitions = this.mTransitions.size();
            for (int i = 0; i < numTransitions; i++) {
                this.mTransitions.get(i).mo79setDuration(duration);
            }
        }
        return this;
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: setStartDelay  reason: collision with other method in class */
    public TransitionSet mo82setStartDelay(long startDelay) {
        return (TransitionSet) super.mo82setStartDelay(startDelay);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: setInterpolator  reason: collision with other method in class */
    public TransitionSet mo80setInterpolator(@Nullable TimeInterpolator interpolator) {
        this.mChangeFlags |= 1;
        ArrayList<Transition> arrayList = this.mTransitions;
        if (arrayList != null) {
            int numTransitions = arrayList.size();
            for (int i = 0; i < numTransitions; i++) {
                this.mTransitions.get(i).mo80setInterpolator(interpolator);
            }
        }
        return (TransitionSet) super.mo80setInterpolator(interpolator);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: addTarget  reason: collision with other method in class */
    public TransitionSet mo70addTarget(@NonNull View target) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).mo70addTarget(target);
        }
        return (TransitionSet) super.mo70addTarget(target);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: addTarget  reason: collision with other method in class */
    public TransitionSet mo69addTarget(@IdRes int targetId) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).mo69addTarget(targetId);
        }
        return (TransitionSet) super.mo69addTarget(targetId);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: addTarget  reason: collision with other method in class */
    public TransitionSet mo72addTarget(@NonNull String targetName) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).mo72addTarget(targetName);
        }
        return (TransitionSet) super.mo72addTarget(targetName);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: addTarget  reason: collision with other method in class */
    public TransitionSet mo71addTarget(@NonNull Class targetType) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).mo71addTarget(targetType);
        }
        return (TransitionSet) super.mo71addTarget(targetType);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: addListener  reason: collision with other method in class */
    public TransitionSet mo68addListener(@NonNull Transition.TransitionListener listener) {
        return (TransitionSet) super.mo68addListener(listener);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: removeTarget  reason: collision with other method in class */
    public TransitionSet mo75removeTarget(@IdRes int targetId) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).mo75removeTarget(targetId);
        }
        return (TransitionSet) super.mo75removeTarget(targetId);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: removeTarget  reason: collision with other method in class */
    public TransitionSet mo76removeTarget(@NonNull View target) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).mo76removeTarget(target);
        }
        return (TransitionSet) super.mo76removeTarget(target);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: removeTarget  reason: collision with other method in class */
    public TransitionSet mo77removeTarget(@NonNull Class target) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).mo77removeTarget(target);
        }
        return (TransitionSet) super.mo77removeTarget(target);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: removeTarget  reason: collision with other method in class */
    public TransitionSet mo78removeTarget(@NonNull String target) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).mo78removeTarget(target);
        }
        return (TransitionSet) super.mo78removeTarget(target);
    }

    @Override // android.support.transition.Transition
    @NonNull
    public Transition excludeTarget(@NonNull View target, boolean exclude) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).excludeTarget(target, exclude);
        }
        return super.excludeTarget(target, exclude);
    }

    @Override // android.support.transition.Transition
    @NonNull
    public Transition excludeTarget(@NonNull String targetName, boolean exclude) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).excludeTarget(targetName, exclude);
        }
        return super.excludeTarget(targetName, exclude);
    }

    @Override // android.support.transition.Transition
    @NonNull
    public Transition excludeTarget(int targetId, boolean exclude) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).excludeTarget(targetId, exclude);
        }
        return super.excludeTarget(targetId, exclude);
    }

    @Override // android.support.transition.Transition
    @NonNull
    public Transition excludeTarget(@NonNull Class type, boolean exclude) {
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).excludeTarget(type, exclude);
        }
        return super.excludeTarget(type, exclude);
    }

    @Override // android.support.transition.Transition
    @NonNull
    /* renamed from: removeListener  reason: collision with other method in class */
    public TransitionSet mo74removeListener(@NonNull Transition.TransitionListener listener) {
        return (TransitionSet) super.mo74removeListener(listener);
    }

    @Override // android.support.transition.Transition
    public void setPathMotion(PathMotion pathMotion) {
        super.setPathMotion(pathMotion);
        this.mChangeFlags |= 4;
        for (int i = 0; i < this.mTransitions.size(); i++) {
            this.mTransitions.get(i).setPathMotion(pathMotion);
        }
    }

    @NonNull
    public TransitionSet removeTransition(@NonNull Transition transition) {
        this.mTransitions.remove(transition);
        transition.mParent = null;
        return this;
    }

    private void setupStartEndListeners() {
        TransitionSetListener listener = new TransitionSetListener(this);
        Iterator<Transition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            Transition childTransition = it.next();
            childTransition.mo68addListener(listener);
        }
        this.mCurrentListeners = this.mTransitions.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class TransitionSetListener extends TransitionListenerAdapter {
        TransitionSet mTransitionSet;

        TransitionSetListener(TransitionSet transitionSet) {
            this.mTransitionSet = transitionSet;
        }

        @Override // android.support.transition.TransitionListenerAdapter, android.support.transition.Transition.TransitionListener
        public void onTransitionStart(@NonNull Transition transition) {
            if (!this.mTransitionSet.mStarted) {
                this.mTransitionSet.start();
                this.mTransitionSet.mStarted = true;
            }
        }

        @Override // android.support.transition.TransitionListenerAdapter, android.support.transition.Transition.TransitionListener
        public void onTransitionEnd(@NonNull Transition transition) {
            TransitionSet transitionSet = this.mTransitionSet;
            transitionSet.mCurrentListeners--;
            if (this.mTransitionSet.mCurrentListeners == 0) {
                TransitionSet transitionSet2 = this.mTransitionSet;
                transitionSet2.mStarted = false;
                transitionSet2.end();
            }
            transition.mo74removeListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.transition.Transition
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void createAnimators(ViewGroup sceneRoot, TransitionValuesMaps startValues, TransitionValuesMaps endValues, ArrayList<TransitionValues> startValuesList, ArrayList<TransitionValues> endValuesList) {
        long startDelay = getStartDelay();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            Transition childTransition = this.mTransitions.get(i);
            if (startDelay > 0 && (this.mPlayTogether || i == 0)) {
                long childStartDelay = childTransition.getStartDelay();
                if (childStartDelay > 0) {
                    childTransition.mo82setStartDelay(startDelay + childStartDelay);
                } else {
                    childTransition.mo82setStartDelay(startDelay);
                }
            }
            childTransition.createAnimators(sceneRoot, startValues, endValues, startValuesList, endValuesList);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.transition.Transition
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void runAnimators() {
        if (this.mTransitions.isEmpty()) {
            start();
            end();
            return;
        }
        setupStartEndListeners();
        if (!this.mPlayTogether) {
            for (int i = 1; i < this.mTransitions.size(); i++) {
                Transition previousTransition = this.mTransitions.get(i - 1);
                final Transition nextTransition = this.mTransitions.get(i);
                previousTransition.mo68addListener(new TransitionListenerAdapter() { // from class: android.support.transition.TransitionSet.1
                    @Override // android.support.transition.TransitionListenerAdapter, android.support.transition.Transition.TransitionListener
                    public void onTransitionEnd(@NonNull Transition transition) {
                        nextTransition.runAnimators();
                        transition.mo74removeListener(this);
                    }
                });
            }
            Transition firstTransition = this.mTransitions.get(0);
            if (firstTransition != null) {
                firstTransition.runAnimators();
                return;
            }
            return;
        }
        Iterator<Transition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            Transition childTransition = it.next();
            childTransition.runAnimators();
        }
    }

    @Override // android.support.transition.Transition
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        if (isValidTarget(transitionValues.view)) {
            Iterator<Transition> it = this.mTransitions.iterator();
            while (it.hasNext()) {
                Transition childTransition = it.next();
                if (childTransition.isValidTarget(transitionValues.view)) {
                    childTransition.captureStartValues(transitionValues);
                    transitionValues.mTargetedTransitions.add(childTransition);
                }
            }
        }
    }

    @Override // android.support.transition.Transition
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        if (isValidTarget(transitionValues.view)) {
            Iterator<Transition> it = this.mTransitions.iterator();
            while (it.hasNext()) {
                Transition childTransition = it.next();
                if (childTransition.isValidTarget(transitionValues.view)) {
                    childTransition.captureEndValues(transitionValues);
                    transitionValues.mTargetedTransitions.add(childTransition);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.transition.Transition
    public void capturePropagationValues(TransitionValues transitionValues) {
        super.capturePropagationValues(transitionValues);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).capturePropagationValues(transitionValues);
        }
    }

    @Override // android.support.transition.Transition
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void pause(View sceneRoot) {
        super.pause(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).pause(sceneRoot);
        }
    }

    @Override // android.support.transition.Transition
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void resume(View sceneRoot) {
        super.resume(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).resume(sceneRoot);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.transition.Transition
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void cancel() {
        super.cancel();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.transition.Transition
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void forceToEnd(ViewGroup sceneRoot) {
        super.forceToEnd(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).forceToEnd(sceneRoot);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.transition.Transition
    /* renamed from: setSceneRoot  reason: collision with other method in class */
    public TransitionSet mo81setSceneRoot(ViewGroup sceneRoot) {
        super.mo81setSceneRoot(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).mo81setSceneRoot(sceneRoot);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.transition.Transition
    public void setCanRemoveViews(boolean canRemoveViews) {
        super.setCanRemoveViews(canRemoveViews);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).setCanRemoveViews(canRemoveViews);
        }
    }

    @Override // android.support.transition.Transition
    public void setPropagation(TransitionPropagation propagation) {
        super.setPropagation(propagation);
        this.mChangeFlags |= 2;
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).setPropagation(propagation);
        }
    }

    @Override // android.support.transition.Transition
    public void setEpicenterCallback(Transition.EpicenterCallback epicenterCallback) {
        super.setEpicenterCallback(epicenterCallback);
        this.mChangeFlags |= 8;
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).setEpicenterCallback(epicenterCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.transition.Transition
    public String toString(String indent) {
        String result = super.toString(indent);
        for (int i = 0; i < this.mTransitions.size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(result);
            sb.append("\n");
            sb.append(this.mTransitions.get(i).toString(indent + "  "));
            result = sb.toString();
        }
        return result;
    }

    @Override // android.support.transition.Transition
    /* renamed from: clone */
    public Transition mo73clone() {
        TransitionSet clone = (TransitionSet) super.m67clone();
        clone.mTransitions = new ArrayList<>();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            clone.addTransition(this.mTransitions.get(i).m67clone());
        }
        return clone;
    }
}
