package me.goldze.mvvmhabit.bus.event;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/* loaded from: classes.dex */
public class SnackbarMessage extends SingleLiveEvent<Integer> {

    /* loaded from: classes.dex */
    public interface SnackbarObserver {
        void onNewMessage(@StringRes int i);
    }

    public void observe(LifecycleOwner owner, final SnackbarObserver observer) {
        super.observe(owner, new Observer<Integer>() { // from class: me.goldze.mvvmhabit.bus.event.SnackbarMessage.1
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Integer t) {
                if (t == null) {
                    return;
                }
                observer.onNewMessage(t.intValue());
            }
        });
    }
}