package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Util;

/* loaded from: classes.dex */
public final class UnitBitmapDecoder implements ResourceDecoder<Bitmap, Bitmap> {
    @Override // com.bumptech.glide.load.ResourceDecoder
    public boolean handles(@NonNull Bitmap source, @NonNull Options options) {
        return true;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<Bitmap> decode(@NonNull Bitmap source, int width, int height, @NonNull Options options) {
        return new NonOwnedBitmapResource(source);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class NonOwnedBitmapResource implements Resource<Bitmap> {
        private final Bitmap bitmap;

        NonOwnedBitmapResource(@NonNull Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override // com.bumptech.glide.load.engine.Resource
        @NonNull
        public Class<Bitmap> getResourceClass() {
            return Bitmap.class;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.bumptech.glide.load.engine.Resource
        @NonNull
        /* renamed from: get */
        public Bitmap mo239get() {
            return this.bitmap;
        }

        @Override // com.bumptech.glide.load.engine.Resource
        public int getSize() {
            return Util.getBitmapByteSize(this.bitmap);
        }

        @Override // com.bumptech.glide.load.engine.Resource
        public void recycle() {
        }
    }
}
