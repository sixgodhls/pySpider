package android.support.design.internal;

import android.graphics.PorterDuff;
import android.support.annotation.RestrictTo;
import android.support.p000v4.view.ViewCompat;
import android.view.View;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class ViewUtils {
    public static PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        if (value != 3) {
            if (value == 5) {
                return PorterDuff.Mode.SRC_IN;
            }
            if (value == 9) {
                return PorterDuff.Mode.SRC_ATOP;
            }
            switch (value) {
                case 14:
                    return PorterDuff.Mode.MULTIPLY;
                case 15:
                    return PorterDuff.Mode.SCREEN;
                case 16:
                    return PorterDuff.Mode.ADD;
                default:
                    return defaultMode;
            }
        }
        return PorterDuff.Mode.SRC_OVER;
    }

    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == 1;
    }
}