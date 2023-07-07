package com.bumptech.glide.manager;

import android.support.annotation.NonNull;

/* loaded from: classes.dex */
class ApplicationLifecycle implements Lifecycle {
    @Override // com.bumptech.glide.manager.Lifecycle
    public void addListener(@NonNull LifecycleListener listener) {
        listener.onStart();
    }

    @Override // com.bumptech.glide.manager.Lifecycle
    public void removeListener(@NonNull LifecycleListener listener) {
    }
}