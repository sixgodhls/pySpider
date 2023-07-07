package com.bumptech.glide.signature;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.load.Key;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/* loaded from: classes.dex */
public final class ApplicationVersionSignature {
    private static final ConcurrentMap<String, Key> PACKAGE_NAME_TO_KEY = new ConcurrentHashMap();
    private static final String TAG = "AppVersionSignature";

    @NonNull
    public static Key obtain(@NonNull Context context) {
        String packageName = context.getPackageName();
        Key result = PACKAGE_NAME_TO_KEY.get(packageName);
        if (result == null) {
            Key toAdd = obtainVersionSignature(context);
            Key result2 = PACKAGE_NAME_TO_KEY.putIfAbsent(packageName, toAdd);
            if (result2 == null) {
                return toAdd;
            }
            return result2;
        }
        return result;
    }

    @VisibleForTesting
    static void reset() {
        PACKAGE_NAME_TO_KEY.clear();
    }

    @NonNull
    private static Key obtainVersionSignature(@NonNull Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        String versionCode = getVersionCode(packageInfo);
        return new ObjectKey(versionCode);
    }

    @NonNull
    private static String getVersionCode(@Nullable PackageInfo packageInfo) {
        if (packageInfo != null) {
            String versionCode = String.valueOf(packageInfo.versionCode);
            return versionCode;
        }
        String versionCode2 = UUID.randomUUID().toString();
        return versionCode2;
    }

    @Nullable
    private static PackageInfo getPackageInfo(@NonNull Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Cannot resolve info for" + context.getPackageName(), e);
            return null;
        }
    }

    private ApplicationVersionSignature() {
    }
}