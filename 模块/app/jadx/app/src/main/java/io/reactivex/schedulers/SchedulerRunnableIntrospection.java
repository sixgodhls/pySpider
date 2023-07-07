package io.reactivex.schedulers;

import io.reactivex.annotations.NonNull;

/* loaded from: classes.dex */
public interface SchedulerRunnableIntrospection {
    @NonNull
    Runnable getWrappedRunnable();
}
