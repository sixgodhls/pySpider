package com.bumptech.glide.load.engine.executor;

import android.os.Build;

/* loaded from: classes.dex */
final class RuntimeCompat {
    private static final String CPU_LOCATION = "/sys/devices/system/cpu/";
    private static final String CPU_NAME_REGEX = "cpu[0-9]+";
    private static final String TAG = "GlideRuntimeCompat";

    private RuntimeCompat() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int availableProcessors() {
        int cpus = Runtime.getRuntime().availableProcessors();
        if (Build.VERSION.SDK_INT < 17) {
            return Math.max(getCoreCountPre17(), cpus);
        }
        return cpus;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0039  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0037  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int getCoreCountPre17() {
        /*
            r0 = 0
            android.os.StrictMode$ThreadPolicy r1 = android.os.StrictMode.allowThreadDiskReads()
            java.io.File r2 = new java.io.File     // Catch: java.lang.Throwable -> L1f
            java.lang.String r3 = "/sys/devices/system/cpu/"
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L1f
            java.lang.String r3 = "cpu[0-9]+"
            java.util.regex.Pattern r3 = java.util.regex.Pattern.compile(r3)     // Catch: java.lang.Throwable -> L1f
            com.bumptech.glide.load.engine.executor.RuntimeCompat$1 r4 = new com.bumptech.glide.load.engine.executor.RuntimeCompat$1     // Catch: java.lang.Throwable -> L1f
            r4.<init>()     // Catch: java.lang.Throwable -> L1f
            java.io.File[] r4 = r2.listFiles(r4)     // Catch: java.lang.Throwable -> L1f
            r0 = r4
            goto L30
        L1d:
            r2 = move-exception
            goto L3f
        L1f:
            r2 = move-exception
            java.lang.String r3 = "GlideRuntimeCompat"
            r4 = 6
            boolean r3 = android.util.Log.isLoggable(r3, r4)     // Catch: java.lang.Throwable -> L1d
            if (r3 == 0) goto L30
            java.lang.String r3 = "GlideRuntimeCompat"
            java.lang.String r4 = "Failed to calculate accurate cpu count"
            android.util.Log.e(r3, r4, r2)     // Catch: java.lang.Throwable -> L1d
        L30:
            android.os.StrictMode.setThreadPolicy(r1)
            r2 = 1
            if (r0 == 0) goto L39
            int r3 = r0.length
            goto L3a
        L39:
            r3 = 0
        L3a:
            int r2 = java.lang.Math.max(r2, r3)
            return r2
        L3f:
            android.os.StrictMode.setThreadPolicy(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.executor.RuntimeCompat.getCoreCountPre17():int");
    }
}
