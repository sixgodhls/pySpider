package com.bumptech.glide.load;

import android.support.annotation.NonNull;
import java.io.File;

/* loaded from: classes.dex */
public interface Encoder<T> {
    boolean encode(@NonNull T t, @NonNull File file, @NonNull Options options);
}
