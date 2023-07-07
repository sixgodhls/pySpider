package com.bumptech.glide.load.resource.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ContextThemeWrapper;

/* loaded from: classes.dex */
public final class DrawableDecoderCompat {
    private static volatile boolean shouldCallAppCompatResources = true;

    private DrawableDecoderCompat() {
    }

    public static Drawable getDrawable(Context ourContext, Context targetContext, @DrawableRes int id) {
        return getDrawable(ourContext, targetContext, id, null);
    }

    public static Drawable getDrawable(Context ourContext, @DrawableRes int id, @Nullable Resources.Theme theme) {
        return getDrawable(ourContext, ourContext, id, theme);
    }

    private static Drawable getDrawable(Context ourContext, Context targetContext, @DrawableRes int id, @Nullable Resources.Theme theme) {
        try {
            if (shouldCallAppCompatResources) {
                return loadDrawableV7(targetContext, id, theme);
            }
        } catch (Resources.NotFoundException e) {
        } catch (IllegalStateException e2) {
            if (ourContext.getPackageName().equals(targetContext.getPackageName())) {
                throw e2;
            }
            return ContextCompat.getDrawable(targetContext, id);
        } catch (NoClassDefFoundError e3) {
            shouldCallAppCompatResources = false;
        }
        return loadDrawableV4(targetContext, id, theme != null ? theme : targetContext.getTheme());
    }

    private static Drawable loadDrawableV7(Context context, @DrawableRes int id, @Nullable Resources.Theme theme) {
        Context resourceContext = theme != null ? new ContextThemeWrapper(context, theme) : context;
        return AppCompatResources.getDrawable(resourceContext, id);
    }

    private static Drawable loadDrawableV4(Context context, @DrawableRes int id, @Nullable Resources.Theme theme) {
        Resources resources = context.getResources();
        return ResourcesCompat.getDrawable(resources, id, theme);
    }
}
