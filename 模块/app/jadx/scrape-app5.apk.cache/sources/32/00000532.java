package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.security.MessageDigest;

/* loaded from: classes.dex */
public class CenterInside extends BitmapTransformation {

    /* renamed from: ID */
    private static final String f56ID = "com.bumptech.glide.load.resource.bitmap.CenterInside";
    private static final byte[] ID_BYTES = f56ID.getBytes(CHARSET);

    @Override // com.bumptech.glide.load.resource.bitmap.BitmapTransformation
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtils.centerInside(pool, toTransform, outWidth, outHeight);
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object o) {
        return o instanceof CenterInside;
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        return f56ID.hashCode();
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}