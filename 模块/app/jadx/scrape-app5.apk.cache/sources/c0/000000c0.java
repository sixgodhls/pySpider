package android.support.p000v4.content.p001pm;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.annotation.NonNull;

/* renamed from: android.support.v4.content.pm.PackageInfoCompat */
/* loaded from: classes.dex */
public final class PackageInfoCompat {
    public static long getLongVersionCode(@NonNull PackageInfo info) {
        if (Build.VERSION.SDK_INT >= 28) {
            return info.getLongVersionCode();
        }
        return info.versionCode;
    }

    private PackageInfoCompat() {
    }
}