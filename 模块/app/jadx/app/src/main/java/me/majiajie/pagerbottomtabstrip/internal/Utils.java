package me.majiajie.pagerbottomtabstrip.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

/* loaded from: classes.dex */
public class Utils {
    public static Drawable tint(Drawable drawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        wrappedDrawable.mutate();
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static Drawable newDrawable(Drawable drawable) {
        Drawable.ConstantState constantState = drawable.getConstantState();
        return constantState != null ? constantState.newDrawable() : drawable;
    }

    public static int getColorPrimary(Context context) {
        Resources res = context.getResources();
        int attrRes = res.getIdentifier("colorPrimary", "attr", context.getPackageName());
        if (attrRes == 0) {
            return -16738680;
        }
        return ContextCompat.getColor(context, getResourceId(context, attrRes));
    }

    private static int getResourceId(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.resourceId;
    }
}
