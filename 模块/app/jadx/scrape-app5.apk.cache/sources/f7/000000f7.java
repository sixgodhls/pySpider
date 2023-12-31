package android.support.p000v4.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* renamed from: android.support.v4.graphics.drawable.WrappedDrawable */
/* loaded from: classes.dex */
public interface WrappedDrawable {
    Drawable getWrappedDrawable();

    void setWrappedDrawable(Drawable drawable);
}