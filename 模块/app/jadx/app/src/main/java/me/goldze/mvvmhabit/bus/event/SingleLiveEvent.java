package me.goldze.mvvmhabit.bus.event;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public class SingleLiveEvent<T> extends MutableLiveData<T> {
    private static final String TAG = "SingleLiveEvent";
    private final AtomicBoolean mPending = new AtomicBoolean(false);

    @Override // android.arch.lifecycle.LiveData
    @MainThread
    public void observe(LifecycleOwner owner, final Observer<T> observer) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.");
        }
        super.observe(owner, new Observer<T>() { // from class: me.goldze.mvvmhabit.bus.event.SingleLiveEvent.1
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable T t) {
                if (SingleLiveEvent.this.mPending.compareAndSet(true, false)) {
                    observer.onChanged(t);
                }
            }
        });
    }

    @Override // android.arch.lifecycle.MutableLiveData, android.arch.lifecycle.LiveData
    @MainThread
    public void setValue(@Nullable T t) {
        this.mPending.set(true);
        super.setValue(t);
    }

    @MainThread
    public void call() {
        setValue(null);
    }
}
