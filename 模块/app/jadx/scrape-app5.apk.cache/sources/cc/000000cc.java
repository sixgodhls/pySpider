package android.support.p000v4.p002os;

/* renamed from: android.support.v4.os.OperationCanceledException */
/* loaded from: classes.dex */
public class OperationCanceledException extends RuntimeException {
    public OperationCanceledException() {
        this(null);
    }

    public OperationCanceledException(String message) {
        super(message != null ? message : "The operation has been canceled.");
    }
}