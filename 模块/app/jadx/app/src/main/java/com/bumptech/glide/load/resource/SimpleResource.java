package com.bumptech.glide.load.resource;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;

/* loaded from: classes.dex */
public class SimpleResource<T> implements Resource<T> {
    protected final T data;

    public SimpleResource(@NonNull T data) {
        this.data = (T) Preconditions.checkNotNull(data);
    }

    @Override // com.bumptech.glide.load.engine.Resource
    @NonNull
    public Class<T> getResourceClass() {
        return (Class<T>) this.data.getClass();
    }

    @Override // com.bumptech.glide.load.engine.Resource
    @NonNull
    /* renamed from: get */
    public final T mo239get() {
        return this.data;
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final int getSize() {
        return 1;
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public void recycle() {
    }
}
