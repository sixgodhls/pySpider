package android.arch.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.os.Handler;
import android.support.annotation.NonNull;

/* loaded from: classes.dex */
public class ServiceLifecycleDispatcher {
    private final Handler mHandler = new Handler();
    private DispatchRunnable mLastDispatchRunnable;
    private final LifecycleRegistry mRegistry;

    public ServiceLifecycleDispatcher(@NonNull LifecycleOwner provider) {
        this.mRegistry = new LifecycleRegistry(provider);
    }

    private void postDispatchRunnable(Lifecycle.Event event) {
        DispatchRunnable dispatchRunnable = this.mLastDispatchRunnable;
        if (dispatchRunnable != null) {
            dispatchRunnable.run();
        }
        this.mLastDispatchRunnable = new DispatchRunnable(this.mRegistry, event);
        this.mHandler.postAtFrontOfQueue(this.mLastDispatchRunnable);
    }

    public void onServicePreSuperOnCreate() {
        postDispatchRunnable(Lifecycle.Event.ON_CREATE);
    }

    public void onServicePreSuperOnBind() {
        postDispatchRunnable(Lifecycle.Event.ON_START);
    }

    public void onServicePreSuperOnStart() {
        postDispatchRunnable(Lifecycle.Event.ON_START);
    }

    public void onServicePreSuperOnDestroy() {
        postDispatchRunnable(Lifecycle.Event.ON_STOP);
        postDispatchRunnable(Lifecycle.Event.ON_DESTROY);
    }

    public Lifecycle getLifecycle() {
        return this.mRegistry;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DispatchRunnable implements Runnable {
        final Lifecycle.Event mEvent;
        private final LifecycleRegistry mRegistry;
        private boolean mWasExecuted = false;

        DispatchRunnable(@NonNull LifecycleRegistry registry, Lifecycle.Event event) {
            this.mRegistry = registry;
            this.mEvent = event;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.mWasExecuted) {
                this.mRegistry.handleLifecycleEvent(this.mEvent);
                this.mWasExecuted = true;
            }
        }
    }
}
