package me.goldze.mvvmhabit.utils.compression;

import java.io.File;

/* loaded from: classes.dex */
public interface OnCompressListener {
    void onError(Throwable th);

    void onStart();

    void onSuccess(File file);
}
