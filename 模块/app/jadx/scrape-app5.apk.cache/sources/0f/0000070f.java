package android.support.transition;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.p000v4.app.FragmentTransitionImpl;
import android.support.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class FragmentTransitionSupport extends FragmentTransitionImpl {
    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public boolean canHandle(Object transition) {
        return transition instanceof Transition;
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public Object cloneTransition(Object transition) {
        if (transition == null) {
            return null;
        }
        Transition copy = ((Transition) transition).m129clone();
        return copy;
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public Object wrapTransitionInSet(Object transition) {
        if (transition == null) {
            return null;
        }
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition((Transition) transition);
        return transitionSet;
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void setSharedElementTargets(Object transitionObj, View nonExistentView, ArrayList<View> sharedViews) {
        TransitionSet transition = (TransitionSet) transitionObj;
        List<View> views = transition.getTargets();
        views.clear();
        int count = sharedViews.size();
        for (int i = 0; i < count; i++) {
            View view = sharedViews.get(i);
            bfsAddViewChildren(views, view);
        }
        views.add(nonExistentView);
        sharedViews.add(nonExistentView);
        addTargets(transition, sharedViews);
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void setEpicenter(Object transitionObj, View view) {
        if (view != null) {
            Transition transition = (Transition) transitionObj;
            final Rect epicenter = new Rect();
            getBoundsOnScreen(view, epicenter);
            transition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.support.transition.FragmentTransitionSupport.1
                @Override // android.support.transition.Transition.EpicenterCallback
                public Rect onGetEpicenter(@NonNull Transition transition2) {
                    return epicenter;
                }
            });
        }
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void addTargets(Object transitionObj, ArrayList<View> views) {
        Transition transition = (Transition) transitionObj;
        if (transition == null) {
            return;
        }
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            int numTransitions = set.getTransitionCount();
            for (int i = 0; i < numTransitions; i++) {
                Transition child = set.getTransitionAt(i);
                addTargets(child, views);
            }
        } else if (!hasSimpleTarget(transition)) {
            List<View> targets = transition.getTargets();
            if (isNullOrEmpty(targets)) {
                int numViews = views.size();
                for (int i2 = 0; i2 < numViews; i2++) {
                    transition.mo132addTarget(views.get(i2));
                }
            }
        }
    }

    private static boolean hasSimpleTarget(Transition transition) {
        return !isNullOrEmpty(transition.getTargetIds()) || !isNullOrEmpty(transition.getTargetNames()) || !isNullOrEmpty(transition.getTargetTypes());
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public Object mergeTransitionsTogether(Object transition1, Object transition2, Object transition3) {
        TransitionSet transitionSet = new TransitionSet();
        if (transition1 != null) {
            transitionSet.addTransition((Transition) transition1);
        }
        if (transition2 != null) {
            transitionSet.addTransition((Transition) transition2);
        }
        if (transition3 != null) {
            transitionSet.addTransition((Transition) transition3);
        }
        return transitionSet;
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void scheduleHideFragmentView(Object exitTransitionObj, final View fragmentView, final ArrayList<View> exitingViews) {
        Transition exitTransition = (Transition) exitTransitionObj;
        exitTransition.mo130addListener(new Transition.TransitionListener() { // from class: android.support.transition.FragmentTransitionSupport.2
            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionStart(@NonNull Transition transition) {
            }

            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionEnd(@NonNull Transition transition) {
                transition.mo136removeListener(this);
                fragmentView.setVisibility(8);
                int numViews = exitingViews.size();
                for (int i = 0; i < numViews; i++) {
                    ((View) exitingViews.get(i)).setVisibility(0);
                }
            }

            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionCancel(@NonNull Transition transition) {
            }

            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionPause(@NonNull Transition transition) {
            }

            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionResume(@NonNull Transition transition) {
            }
        });
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public Object mergeTransitionsInSequence(Object exitTransitionObj, Object enterTransitionObj, Object sharedElementTransitionObj) {
        Transition staggered = null;
        Transition exitTransition = (Transition) exitTransitionObj;
        Transition enterTransition = (Transition) enterTransitionObj;
        Transition sharedElementTransition = (Transition) sharedElementTransitionObj;
        if (exitTransition != null && enterTransition != null) {
            staggered = new TransitionSet().addTransition(exitTransition).addTransition(enterTransition).setOrdering(1);
        } else if (exitTransition != null) {
            staggered = exitTransition;
        } else if (enterTransition != null) {
            staggered = enterTransition;
        }
        if (sharedElementTransition != null) {
            TransitionSet together = new TransitionSet();
            if (staggered != null) {
                together.addTransition(staggered);
            }
            together.addTransition(sharedElementTransition);
            return together;
        }
        return staggered;
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void beginDelayedTransition(ViewGroup sceneRoot, Object transition) {
        TransitionManager.beginDelayedTransition(sceneRoot, (Transition) transition);
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void scheduleRemoveTargets(Object overallTransitionObj, final Object enterTransition, final ArrayList<View> enteringViews, final Object exitTransition, final ArrayList<View> exitingViews, final Object sharedElementTransition, final ArrayList<View> sharedElementsIn) {
        Transition overallTransition = (Transition) overallTransitionObj;
        overallTransition.mo130addListener(new Transition.TransitionListener() { // from class: android.support.transition.FragmentTransitionSupport.3
            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionStart(@NonNull Transition transition) {
                Object obj = enterTransition;
                if (obj != null) {
                    FragmentTransitionSupport.this.replaceTargets(obj, enteringViews, null);
                }
                Object obj2 = exitTransition;
                if (obj2 != null) {
                    FragmentTransitionSupport.this.replaceTargets(obj2, exitingViews, null);
                }
                Object obj3 = sharedElementTransition;
                if (obj3 != null) {
                    FragmentTransitionSupport.this.replaceTargets(obj3, sharedElementsIn, null);
                }
            }

            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionEnd(@NonNull Transition transition) {
            }

            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionCancel(@NonNull Transition transition) {
            }

            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionPause(@NonNull Transition transition) {
            }

            @Override // android.support.transition.Transition.TransitionListener
            public void onTransitionResume(@NonNull Transition transition) {
            }
        });
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void swapSharedElementTargets(Object sharedElementTransitionObj, ArrayList<View> sharedElementsOut, ArrayList<View> sharedElementsIn) {
        TransitionSet sharedElementTransition = (TransitionSet) sharedElementTransitionObj;
        if (sharedElementTransition != null) {
            sharedElementTransition.getTargets().clear();
            sharedElementTransition.getTargets().addAll(sharedElementsIn);
            replaceTargets(sharedElementTransition, sharedElementsOut, sharedElementsIn);
        }
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void replaceTargets(Object transitionObj, ArrayList<View> oldTargets, ArrayList<View> newTargets) {
        Transition transition = (Transition) transitionObj;
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            int numTransitions = set.getTransitionCount();
            for (int i = 0; i < numTransitions; i++) {
                Transition child = set.getTransitionAt(i);
                replaceTargets(child, oldTargets, newTargets);
            }
        } else if (!hasSimpleTarget(transition)) {
            List<View> targets = transition.getTargets();
            if (targets.size() == oldTargets.size() && targets.containsAll(oldTargets)) {
                int targetCount = newTargets == null ? 0 : newTargets.size();
                for (int i2 = 0; i2 < targetCount; i2++) {
                    transition.mo132addTarget(newTargets.get(i2));
                }
                int i3 = oldTargets.size();
                for (int i4 = i3 - 1; i4 >= 0; i4--) {
                    transition.mo138removeTarget(oldTargets.get(i4));
                }
            }
        }
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void addTarget(Object transitionObj, View view) {
        if (transitionObj != null) {
            Transition transition = (Transition) transitionObj;
            transition.mo132addTarget(view);
        }
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void removeTarget(Object transitionObj, View view) {
        if (transitionObj != null) {
            Transition transition = (Transition) transitionObj;
            transition.mo138removeTarget(view);
        }
    }

    @Override // android.support.p000v4.app.FragmentTransitionImpl
    public void setEpicenter(Object transitionObj, final Rect epicenter) {
        if (transitionObj != null) {
            Transition transition = (Transition) transitionObj;
            transition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.support.transition.FragmentTransitionSupport.4
                @Override // android.support.transition.Transition.EpicenterCallback
                public Rect onGetEpicenter(@NonNull Transition transition2) {
                    Rect rect = epicenter;
                    if (rect == null || rect.isEmpty()) {
                        return null;
                    }
                    return epicenter;
                }
            });
        }
    }
}