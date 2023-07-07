package me.goldze.mvvmhabit.base;

import android.arch.lifecycle.GeneratedAdapter;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MethodCallsLogger;

/* loaded from: classes.dex */
public class IBaseViewModel_LifecycleAdapter implements GeneratedAdapter {
    final IBaseViewModel mReceiver;

    IBaseViewModel_LifecycleAdapter(IBaseViewModel receiver) {
        this.mReceiver = receiver;
    }

    @Override // android.arch.lifecycle.GeneratedAdapter
    public void callMethods(LifecycleOwner owner, Lifecycle.Event event, boolean onAny, MethodCallsLogger logger) {
        boolean hasLogger = logger != null;
        if (onAny) {
            if (!hasLogger || logger.approveCall("onAny", 4)) {
                this.mReceiver.onAny(owner, event);
            }
        } else if (event == Lifecycle.Event.ON_CREATE) {
            if (!hasLogger || logger.approveCall("onCreate", 1)) {
                this.mReceiver.onCreate();
            }
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            if (!hasLogger || logger.approveCall("onDestroy", 1)) {
                this.mReceiver.onDestroy();
            }
        } else if (event == Lifecycle.Event.ON_START) {
            if (!hasLogger || logger.approveCall("onStart", 1)) {
                this.mReceiver.onStart();
            }
        } else if (event == Lifecycle.Event.ON_STOP) {
            if (!hasLogger || logger.approveCall("onStop", 1)) {
                this.mReceiver.onStop();
            }
        } else if (event == Lifecycle.Event.ON_RESUME) {
            if (!hasLogger || logger.approveCall("onResume", 1)) {
                this.mReceiver.onResume();
            }
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            if (!hasLogger || logger.approveCall("onPause", 1)) {
                this.mReceiver.onPause();
            }
        }
    }
}
