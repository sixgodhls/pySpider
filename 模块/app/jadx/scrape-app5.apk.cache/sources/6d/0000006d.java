package android.support.design.drawable;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class DrawableUtils {
    private DrawableUtils() {
    }

    @Nullable
    public static PorterDuffColorFilter updateTintFilter(Drawable drawable, @Nullable ColorStateList tint, @Nullable PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }
        int color = tint.getColorForState(drawable.getState(), 0);
        return new PorterDuffColorFilter(color, tintMode);
    }
}