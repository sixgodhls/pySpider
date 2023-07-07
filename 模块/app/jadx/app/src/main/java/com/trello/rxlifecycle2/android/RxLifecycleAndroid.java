package com.trello.rxlifecycle2.android;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.OutsideLifecycleException;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/* loaded from: classes.dex */
public class RxLifecycleAndroid {
    private static final Function<ActivityEvent, ActivityEvent> ACTIVITY_LIFECYCLE = new Function<ActivityEvent, ActivityEvent>() { // from class: com.trello.rxlifecycle2.android.RxLifecycleAndroid.1
        @Override // io.reactivex.functions.Function
        /* renamed from: apply  reason: avoid collision after fix types in other method */
        public ActivityEvent mo339apply(ActivityEvent lastEvent) throws Exception {
            switch (AnonymousClass3.$SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent[lastEvent.ordinal()]) {
                case 1:
                    return ActivityEvent.DESTROY;
                case 2:
                    return ActivityEvent.STOP;
                case 3:
                    return ActivityEvent.PAUSE;
                case 4:
                    return ActivityEvent.STOP;
                case 5:
                    return ActivityEvent.DESTROY;
                case 6:
                    throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                default:
                    throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
            }
        }
    };
    private static final Function<FragmentEvent, FragmentEvent> FRAGMENT_LIFECYCLE = new Function<FragmentEvent, FragmentEvent>() { // from class: com.trello.rxlifecycle2.android.RxLifecycleAndroid.2
        @Override // io.reactivex.functions.Function
        /* renamed from: apply  reason: avoid collision after fix types in other method */
        public FragmentEvent mo339apply(FragmentEvent lastEvent) throws Exception {
            switch (AnonymousClass3.$SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[lastEvent.ordinal()]) {
                case 1:
                    return FragmentEvent.DETACH;
                case 2:
                    return FragmentEvent.DESTROY;
                case 3:
                    return FragmentEvent.DESTROY_VIEW;
                case 4:
                    return FragmentEvent.STOP;
                case 5:
                    return FragmentEvent.PAUSE;
                case 6:
                    return FragmentEvent.STOP;
                case 7:
                    return FragmentEvent.DESTROY_VIEW;
                case 8:
                    return FragmentEvent.DESTROY;
                case 9:
                    return FragmentEvent.DETACH;
                case 10:
                    throw new OutsideLifecycleException("Cannot bind to Fragment lifecycle when outside of it.");
                default:
                    throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
            }
        }
    };

    private RxLifecycleAndroid() {
        throw new AssertionError("No instances");
    }

    @CheckResult
    @NonNull
    public static <T> LifecycleTransformer<T> bindActivity(@NonNull Observable<ActivityEvent> lifecycle) {
        return RxLifecycle.bind(lifecycle, ACTIVITY_LIFECYCLE);
    }

    @CheckResult
    @NonNull
    public static <T> LifecycleTransformer<T> bindFragment(@NonNull Observable<FragmentEvent> lifecycle) {
        return RxLifecycle.bind(lifecycle, FRAGMENT_LIFECYCLE);
    }

    @CheckResult
    @NonNull
    public static <T> LifecycleTransformer<T> bindView(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return RxLifecycle.bind(Observable.create(new ViewDetachesOnSubscribe(view)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.trello.rxlifecycle2.android.RxLifecycleAndroid$3  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent;
        static final /* synthetic */ int[] $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent = new int[FragmentEvent.values().length];

        static {
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.ATTACH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.CREATE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.CREATE_VIEW.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.START.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.RESUME.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.PAUSE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.STOP.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.DESTROY_VIEW.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.DESTROY.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$FragmentEvent[FragmentEvent.DETACH.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            $SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent = new int[ActivityEvent.values().length];
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent[ActivityEvent.CREATE.ordinal()] = 1;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent[ActivityEvent.START.ordinal()] = 2;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent[ActivityEvent.RESUME.ordinal()] = 3;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent[ActivityEvent.PAUSE.ordinal()] = 4;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent[ActivityEvent.STOP.ordinal()] = 5;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$trello$rxlifecycle2$android$ActivityEvent[ActivityEvent.DESTROY.ordinal()] = 6;
            } catch (NoSuchFieldError e16) {
            }
        }
    }
}
