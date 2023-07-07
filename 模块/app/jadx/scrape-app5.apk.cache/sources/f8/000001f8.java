package me.goldze.mvvmhabit.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

/* loaded from: classes.dex */
public final class Utils {
    @SuppressLint({"StaticFieldLeak"})
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(@NonNull Context context2) {
        context = context2.getApplicationContext();
    }

    public static Context getContext() {
        Context context2 = context;
        if (context2 != null) {
            return context2;
        }
        throw new NullPointerException("should be initialized in application");
    }
}