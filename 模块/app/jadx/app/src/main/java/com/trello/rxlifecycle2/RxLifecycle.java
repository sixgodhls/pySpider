package com.trello.rxlifecycle2;

import com.trello.rxlifecycle2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class RxLifecycle {
    private RxLifecycle() {
        throw new AssertionError("No instances");
    }

    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
        Preconditions.checkNotNull(lifecycle, "lifecycle == null");
        Preconditions.checkNotNull(event, "event == null");
        return bind(takeUntilEvent(lifecycle, event));
    }

    private static <R> Observable<R> takeUntilEvent(Observable<R> lifecycle, final R event) {
        return lifecycle.filter(new Predicate<R>() { // from class: com.trello.rxlifecycle2.RxLifecycle.1
            @Override // io.reactivex.functions.Predicate
            public boolean test(R lifecycleEvent) throws Exception {
                return lifecycleEvent.equals(event);
            }
        });
    }

    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bind(@Nonnull Observable<R> lifecycle) {
        return new LifecycleTransformer<>(lifecycle);
    }

    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bind(@Nonnull Observable<R> lifecycle, @Nonnull Function<R, R> correspondingEvents) {
        Preconditions.checkNotNull(lifecycle, "lifecycle == null");
        Preconditions.checkNotNull(correspondingEvents, "correspondingEvents == null");
        return bind(takeUntilCorrespondingEvent(lifecycle.share(), correspondingEvents));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <R> Observable<Boolean> takeUntilCorrespondingEvent(Observable<R> lifecycle, Function<R, R> correspondingEvents) {
        return Observable.combineLatest(lifecycle.take(1L).map(correspondingEvents), lifecycle.skip(1L), new BiFunction<R, R, Boolean>() { // from class: com.trello.rxlifecycle2.RxLifecycle.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // io.reactivex.functions.BiFunction
            /* renamed from: apply */
            public Boolean mo312apply(R bindUntilEvent, R lifecycleEvent) throws Exception {
                return Boolean.valueOf(lifecycleEvent.equals(bindUntilEvent));
            }
        }).onErrorReturn(Functions.RESUME_FUNCTION).filter(Functions.SHOULD_COMPLETE);
    }
}
