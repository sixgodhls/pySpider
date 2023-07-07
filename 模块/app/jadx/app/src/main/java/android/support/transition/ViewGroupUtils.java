package android.support.transition;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

/* loaded from: classes.dex */
class ViewGroupUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup group) {
        if (Build.VERSION.SDK_INT >= 18) {
            return new ViewGroupOverlayApi18(group);
        }
        return ViewGroupOverlayApi14.createFrom(group);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void suppressLayout(@NonNull ViewGroup group, boolean suppress) {
        if (Build.VERSION.SDK_INT >= 18) {
            ViewGroupUtilsApi18.suppressLayout(group, suppress);
        } else {
            ViewGroupUtilsApi14.suppressLayout(group, suppress);
        }
    }

    private ViewGroupUtils() {
    }
}
