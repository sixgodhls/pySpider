package me.goldze.mvvmhabit.http.interceptor.logging;

/* loaded from: classes.dex */
class I {
    protected I() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void log(int type, String tag, String msg) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(tag);
        if (type == 4) {
            logger.log(java.util.logging.Level.INFO, msg);
        } else {
            logger.log(java.util.logging.Level.WARNING, msg);
        }
    }
}
