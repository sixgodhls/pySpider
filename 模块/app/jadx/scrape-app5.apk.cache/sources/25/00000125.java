package android.support.transition;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/* loaded from: classes.dex */
interface ViewOverlayImpl {
    void add(@NonNull Drawable drawable);

    void clear();

    void remove(@NonNull Drawable drawable);
}