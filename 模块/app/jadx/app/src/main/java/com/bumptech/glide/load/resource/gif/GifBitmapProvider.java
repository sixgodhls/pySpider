package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

/* loaded from: classes.dex */
public final class GifBitmapProvider implements GifDecoder.BitmapProvider {
    @Nullable
    private final ArrayPool arrayPool;
    private final BitmapPool bitmapPool;

    public GifBitmapProvider(BitmapPool bitmapPool) {
        this(bitmapPool, null);
    }

    public GifBitmapProvider(BitmapPool bitmapPool, @Nullable ArrayPool arrayPool) {
        this.bitmapPool = bitmapPool;
        this.arrayPool = arrayPool;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider
    @NonNull
    public Bitmap obtain(int width, int height, @NonNull Bitmap.Config config) {
        return this.bitmapPool.getDirty(width, height, config);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider
    public void release(@NonNull Bitmap bitmap) {
        this.bitmapPool.put(bitmap);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider
    @NonNull
    public byte[] obtainByteArray(int size) {
        ArrayPool arrayPool = this.arrayPool;
        if (arrayPool == null) {
            return new byte[size];
        }
        return (byte[]) arrayPool.get(size, byte[].class);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider
    public void release(@NonNull byte[] bytes) {
        ArrayPool arrayPool = this.arrayPool;
        if (arrayPool == null) {
            return;
        }
        arrayPool.put(bytes);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider
    @NonNull
    public int[] obtainIntArray(int size) {
        ArrayPool arrayPool = this.arrayPool;
        if (arrayPool == null) {
            return new int[size];
        }
        return (int[]) arrayPool.get(size, int[].class);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider
    public void release(@NonNull int[] array) {
        ArrayPool arrayPool = this.arrayPool;
        if (arrayPool == null) {
            return;
        }
        arrayPool.put(array);
    }
}
