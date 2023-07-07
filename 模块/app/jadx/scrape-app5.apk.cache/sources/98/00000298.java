package com.afollestad.materialdialogs.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.afollestad.materialdialogs.commons.C0592R;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
class PrefUtil {
    private PrefUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setLayoutResource(@NonNull Context context, @NonNull Preference preference, @Nullable AttributeSet attrs) {
        boolean foundLayout = false;
        if (attrs != null) {
            int i = 0;
            while (true) {
                if (i >= attrs.getAttributeCount()) {
                    break;
                }
                String namespace = ((XmlResourceParser) attrs).getAttributeNamespace(0);
                if (!namespace.equals("http://schemas.android.com/apk/res/android") || !attrs.getAttributeName(i).equals("layout")) {
                    i++;
                } else {
                    foundLayout = true;
                    break;
                }
            }
        }
        boolean useStockLayout = false;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, C0592R.styleable.Preference, 0, 0);
            try {
                useStockLayout = a.getBoolean(C0592R.styleable.Preference_useStockLayout, false);
            } finally {
                a.recycle();
            }
        }
        if (!foundLayout && !useStockLayout) {
            preference.setLayoutResource(C0592R.layout.md_preference_custom);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void registerOnActivityDestroyListener(@NonNull Preference preference, @NonNull PreferenceManager.OnActivityDestroyListener listener) {
        try {
            PreferenceManager pm = preference.getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod("registerOnActivityDestroyListener", PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, listener);
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unregisterOnActivityDestroyListener(@NonNull Preference preference, @NonNull PreferenceManager.OnActivityDestroyListener listener) {
        try {
            PreferenceManager pm = preference.getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod("unregisterOnActivityDestroyListener", PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, listener);
        } catch (Exception e) {
        }
    }
}