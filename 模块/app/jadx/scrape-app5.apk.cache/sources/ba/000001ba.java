package com.afollestad.materialdialogs.util;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.support.annotation.ColorInt;

@TargetApi(21)
/* loaded from: classes.dex */
public class RippleHelper {
    public static void applyColor(Drawable d, @ColorInt int color) {
        if (d instanceof RippleDrawable) {
            ((RippleDrawable) d).setColor(ColorStateList.valueOf(color));
        }
    }
}