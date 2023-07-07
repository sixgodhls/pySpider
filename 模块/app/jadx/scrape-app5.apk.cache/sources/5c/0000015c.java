package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;

/* loaded from: classes.dex */
public interface Resource<Z> {
    @NonNull
    /* renamed from: get */
    Z mo301get();

    @NonNull
    Class<Z> getResourceClass();

    int getSize();

    void recycle();
}