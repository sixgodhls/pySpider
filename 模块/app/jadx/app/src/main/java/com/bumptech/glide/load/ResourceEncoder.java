package com.bumptech.glide.load;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.Resource;

/* loaded from: classes.dex */
public interface ResourceEncoder<T> extends Encoder<Resource<T>> {
    @NonNull
    EncodeStrategy getEncodeStrategy(@NonNull Options options);
}
