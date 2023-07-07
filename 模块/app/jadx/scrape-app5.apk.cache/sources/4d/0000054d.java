package me.goldze.mvvmhabit.http.interceptor.logging;

import okhttp3.internal.platform.Platform;

/* loaded from: classes.dex */
public interface Logger {
    public static final Logger DEFAULT = new Logger() { // from class: me.goldze.mvvmhabit.http.interceptor.logging.Logger.1
        @Override // me.goldze.mvvmhabit.http.interceptor.logging.Logger
        public void log(int level, String tag, String message) {
            Platform.get().log(level, message, null);
        }
    };

    void log(int i, String str, String str2);
}