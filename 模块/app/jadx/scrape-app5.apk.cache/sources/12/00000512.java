package android.support.p003v7.widget;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.p000v4.graphics.drawable.WrappedDrawable;
import android.support.p003v7.graphics.drawable.DrawableWrapper;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* renamed from: android.support.v7.widget.DrawableUtils */
/* loaded from: classes.dex */
public class DrawableUtils {
    public static final Rect INSETS_NONE = new Rect();
    private static final String TAG = "DrawableUtils";
    private static final String VECTOR_DRAWABLE_CLAZZ_NAME = "android.graphics.drawable.VectorDrawable";
    private static Class<?> sInsetsClazz;

    static {
        if (Build.VERSION.SDK_INT >= 18) {
            try {
                sInsetsClazz = Class.forName("android.graphics.Insets");
            } catch (ClassNotFoundException e) {
            }
        }
    }

    private DrawableUtils() {
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0079  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x007a A[Catch: Exception -> 0x009b, TryCatch #0 {Exception -> 0x009b, blocks: (B:7:0x0004, B:9:0x001e, B:11:0x002d, B:21:0x0076, B:25:0x007a, B:27:0x0081, B:29:0x0088, B:31:0x008f, B:33:0x004d, B:36:0x0057, B:39:0x0061, B:42:0x006b), top: B:6:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0081 A[Catch: Exception -> 0x009b, TryCatch #0 {Exception -> 0x009b, blocks: (B:7:0x0004, B:9:0x001e, B:11:0x002d, B:21:0x0076, B:25:0x007a, B:27:0x0081, B:29:0x0088, B:31:0x008f, B:33:0x004d, B:36:0x0057, B:39:0x0061, B:42:0x006b), top: B:6:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0088 A[Catch: Exception -> 0x009b, TryCatch #0 {Exception -> 0x009b, blocks: (B:7:0x0004, B:9:0x001e, B:11:0x002d, B:21:0x0076, B:25:0x007a, B:27:0x0081, B:29:0x0088, B:31:0x008f, B:33:0x004d, B:36:0x0057, B:39:0x0061, B:42:0x006b), top: B:6:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x008f A[Catch: Exception -> 0x009b, TRY_LEAVE, TryCatch #0 {Exception -> 0x009b, blocks: (B:7:0x0004, B:9:0x001e, B:11:0x002d, B:21:0x0076, B:25:0x007a, B:27:0x0081, B:29:0x0088, B:31:0x008f, B:33:0x004d, B:36:0x0057, B:39:0x0061, B:42:0x006b), top: B:6:0x0004 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Rect getOpticalBounds(android.graphics.drawable.Drawable r12) {
        /*
            java.lang.Class<?> r0 = android.support.p003v7.widget.DrawableUtils.sInsetsClazz
            if (r0 == 0) goto La3
            android.graphics.drawable.Drawable r0 = android.support.p000v4.graphics.drawable.DrawableCompat.unwrap(r12)     // Catch: java.lang.Exception -> L9b
            r12 = r0
            java.lang.Class r0 = r12.getClass()     // Catch: java.lang.Exception -> L9b
            java.lang.String r1 = "getOpticalInsets"
            r2 = 0
            java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch: java.lang.Exception -> L9b
            java.lang.reflect.Method r0 = r0.getMethod(r1, r3)     // Catch: java.lang.Exception -> L9b
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch: java.lang.Exception -> L9b
            java.lang.Object r1 = r0.invoke(r12, r1)     // Catch: java.lang.Exception -> L9b
            if (r1 == 0) goto L9a
            android.graphics.Rect r3 = new android.graphics.Rect     // Catch: java.lang.Exception -> L9b
            r3.<init>()     // Catch: java.lang.Exception -> L9b
            java.lang.Class<?> r4 = android.support.p003v7.widget.DrawableUtils.sInsetsClazz     // Catch: java.lang.Exception -> L9b
            java.lang.reflect.Field[] r4 = r4.getFields()     // Catch: java.lang.Exception -> L9b
            int r5 = r4.length     // Catch: java.lang.Exception -> L9b
            r6 = 0
        L2b:
            if (r6 >= r5) goto L99
            r7 = r4[r6]     // Catch: java.lang.Exception -> L9b
            java.lang.String r8 = r7.getName()     // Catch: java.lang.Exception -> L9b
            r9 = -1
            int r10 = r8.hashCode()     // Catch: java.lang.Exception -> L9b
            r11 = -1383228885(0xffffffffad8d9a2b, float:-1.6098308E-11)
            if (r10 == r11) goto L6b
            r11 = 115029(0x1c155, float:1.6119E-40)
            if (r10 == r11) goto L61
            r11 = 3317767(0x32a007, float:4.649182E-39)
            if (r10 == r11) goto L57
            r11 = 108511772(0x677c21c, float:4.6598146E-35)
            if (r10 == r11) goto L4d
        L4c:
            goto L75
        L4d:
            java.lang.String r10 = "right"
            boolean r8 = r8.equals(r10)     // Catch: java.lang.Exception -> L9b
            if (r8 == 0) goto L4c
            r8 = 2
            goto L76
        L57:
            java.lang.String r10 = "left"
            boolean r8 = r8.equals(r10)     // Catch: java.lang.Exception -> L9b
            if (r8 == 0) goto L4c
            r8 = 0
            goto L76
        L61:
            java.lang.String r10 = "top"
            boolean r8 = r8.equals(r10)     // Catch: java.lang.Exception -> L9b
            if (r8 == 0) goto L4c
            r8 = 1
            goto L76
        L6b:
            java.lang.String r10 = "bottom"
            boolean r8 = r8.equals(r10)     // Catch: java.lang.Exception -> L9b
            if (r8 == 0) goto L4c
            r8 = 3
            goto L76
        L75:
            r8 = -1
        L76:
            switch(r8) {
                case 0: goto L8f;
                case 1: goto L88;
                case 2: goto L81;
                case 3: goto L7a;
                default: goto L79;
            }     // Catch: java.lang.Exception -> L9b
        L79:
            goto L96
        L7a:
            int r8 = r7.getInt(r1)     // Catch: java.lang.Exception -> L9b
            r3.bottom = r8     // Catch: java.lang.Exception -> L9b
            goto L96
        L81:
            int r8 = r7.getInt(r1)     // Catch: java.lang.Exception -> L9b
            r3.right = r8     // Catch: java.lang.Exception -> L9b
            goto L96
        L88:
            int r8 = r7.getInt(r1)     // Catch: java.lang.Exception -> L9b
            r3.top = r8     // Catch: java.lang.Exception -> L9b
            goto L96
        L8f:
            int r8 = r7.getInt(r1)     // Catch: java.lang.Exception -> L9b
            r3.left = r8     // Catch: java.lang.Exception -> L9b
        L96:
            int r6 = r6 + 1
            goto L2b
        L99:
            return r3
        L9a:
            goto La3
        L9b:
            r0 = move-exception
            java.lang.String r1 = "DrawableUtils"
            java.lang.String r2 = "Couldn't obtain the optical insets. Ignoring."
            android.util.Log.e(r1, r2)
        La3:
            android.graphics.Rect r0 = android.support.p003v7.widget.DrawableUtils.INSETS_NONE
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.DrawableUtils.getOpticalBounds(android.graphics.drawable.Drawable):android.graphics.Rect");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void fixDrawable(@NonNull Drawable drawable) {
        if (Build.VERSION.SDK_INT == 21 && VECTOR_DRAWABLE_CLAZZ_NAME.equals(drawable.getClass().getName())) {
            fixVectorDrawableTinting(drawable);
        }
    }

    public static boolean canSafelyMutateDrawable(@NonNull Drawable drawable) {
        Drawable[] children;
        if (Build.VERSION.SDK_INT >= 15 || !(drawable instanceof InsetDrawable)) {
            if (Build.VERSION.SDK_INT < 15 && (drawable instanceof GradientDrawable)) {
                return false;
            }
            if (Build.VERSION.SDK_INT < 17 && (drawable instanceof LayerDrawable)) {
                return false;
            }
            if (!(drawable instanceof DrawableContainer)) {
                if (drawable instanceof WrappedDrawable) {
                    return canSafelyMutateDrawable(((WrappedDrawable) drawable).getWrappedDrawable());
                }
                if (drawable instanceof DrawableWrapper) {
                    return canSafelyMutateDrawable(((DrawableWrapper) drawable).getWrappedDrawable());
                }
                if (drawable instanceof ScaleDrawable) {
                    return canSafelyMutateDrawable(((ScaleDrawable) drawable).getDrawable());
                }
                return true;
            }
            Drawable.ConstantState state = drawable.getConstantState();
            if (state instanceof DrawableContainer.DrawableContainerState) {
                DrawableContainer.DrawableContainerState containerState = (DrawableContainer.DrawableContainerState) state;
                for (Drawable child : containerState.getChildren()) {
                    if (!canSafelyMutateDrawable(child)) {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }
        return false;
    }

    private static void fixVectorDrawableTinting(Drawable drawable) {
        int[] originalState = drawable.getState();
        if (originalState == null || originalState.length == 0) {
            drawable.setState(ThemeUtils.CHECKED_STATE_SET);
        } else {
            drawable.setState(ThemeUtils.EMPTY_STATE_SET);
        }
        drawable.setState(originalState);
    }

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
}