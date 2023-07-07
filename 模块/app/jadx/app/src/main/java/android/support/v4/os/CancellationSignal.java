package android.support.v4.os;

import android.os.Build;

/* loaded from: classes.dex */
public final class CancellationSignal {
    private boolean mCancelInProgress;
    private Object mCancellationSignalObj;
    private boolean mIsCanceled;
    private OnCancelListener mOnCancelListener;

    /* loaded from: classes.dex */
    public interface OnCancelListener {
        void onCancel();
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this) {
            z = this.mIsCanceled;
        }
        return z;
    }

    public void throwIfCanceled() {
        if (isCanceled()) {
            throw new OperationCanceledException();
        }
    }

    public void cancel() {
        synchronized (this) {
            try {
                try {
                    if (this.mIsCanceled) {
                        return;
                    }
                    this.mIsCanceled = true;
                    this.mCancelInProgress = true;
                    OnCancelListener listener = this.mOnCancelListener;
                    Object obj = this.mCancellationSignalObj;
                    if (listener != null) {
                        try {
                            listener.onCancel();
                        } catch (Throwable th) {
                            synchronized (this) {
                                this.mCancelInProgress = false;
                                notifyAll();
                                throw th;
                            }
                        }
                    }
                    if (obj != null && Build.VERSION.SDK_INT >= 16) {
                        ((android.os.CancellationSignal) obj).cancel();
                    }
                    synchronized (this) {
                        this.mCancelInProgress = false;
                        notifyAll();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    public void setOnCancelListener(OnCancelListener listener) {
        synchronized (this) {
            waitForCancelFinishedLocked();
            if (this.mOnCancelListener == listener) {
                return;
            }
            this.mOnCancelListener = listener;
            if (this.mIsCanceled && listener != null) {
                listener.onCancel();
            }
        }
    }

    public Object getCancellationSignalObject() {
        Object obj;
        if (Build.VERSION.SDK_INT < 16) {
            return null;
        }
        synchronized (this) {
            if (this.mCancellationSignalObj == null) {
                this.mCancellationSignalObj = new android.os.CancellationSignal();
                if (this.mIsCanceled) {
                    ((android.os.CancellationSignal) this.mCancellationSignalObj).cancel();
                }
            }
            obj = this.mCancellationSignalObj;
        }
        return obj;
    }

    private void waitForCancelFinishedLocked() {
        while (this.mCancelInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }
}
