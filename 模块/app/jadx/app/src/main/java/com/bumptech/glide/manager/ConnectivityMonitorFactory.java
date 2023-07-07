package com.bumptech.glide.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bumptech.glide.manager.ConnectivityMonitor;

/* loaded from: classes.dex */
public interface ConnectivityMonitorFactory {
    @NonNull
    ConnectivityMonitor build(@NonNull Context context, @NonNull ConnectivityMonitor.ConnectivityListener connectivityListener);
}
