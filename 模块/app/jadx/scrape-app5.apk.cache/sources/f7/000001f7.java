package me.goldze.mvvmhabit.http.interceptor.logging;

/* renamed from: me.goldze.mvvmhabit.http.interceptor.logging.I */
/* loaded from: classes.dex */
class C0985I {
    protected C0985I() {
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