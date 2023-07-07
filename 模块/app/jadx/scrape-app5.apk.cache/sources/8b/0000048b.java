package com.bumptech.glide.request.transition;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/* loaded from: classes.dex */
public class BitmapTransitionFactory extends BitmapContainerTransitionFactory<Bitmap> {
    public BitmapTransitionFactory(@NonNull TransitionFactory<Drawable> realFactory) {
        super(realFactory);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.bumptech.glide.request.transition.BitmapContainerTransitionFactory
    @NonNull
    public Bitmap getBitmap(@NonNull Bitmap current) {
        return current;
    }
}