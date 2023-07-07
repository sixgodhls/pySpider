package android.support.p000v4.content;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.util.DebugUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* renamed from: android.support.v4.content.Loader */
/* loaded from: classes.dex */
public class Loader<D> {
    Context mContext;
    int mId;
    OnLoadCompleteListener<D> mListener;
    OnLoadCanceledListener<D> mOnLoadCanceledListener;
    boolean mStarted = false;
    boolean mAbandoned = false;
    boolean mReset = true;
    boolean mContentChanged = false;
    boolean mProcessingChange = false;

    /* renamed from: android.support.v4.content.Loader$OnLoadCanceledListener */
    /* loaded from: classes.dex */
    public interface OnLoadCanceledListener<D> {
        void onLoadCanceled(@NonNull Loader<D> loader);
    }

    /* renamed from: android.support.v4.content.Loader$OnLoadCompleteListener */
    /* loaded from: classes.dex */
    public interface OnLoadCompleteListener<D> {
        void onLoadComplete(@NonNull Loader<D> loader, @Nullable D d);
    }

    /* renamed from: android.support.v4.content.Loader$ForceLoadContentObserver */
    /* loaded from: classes.dex */
    public final class ForceLoadContentObserver extends ContentObserver {
        public ForceLoadContentObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            Loader.this.onContentChanged();
        }
    }

    public Loader(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
    }

    @MainThread
    public void deliverResult(@Nullable D data) {
        OnLoadCompleteListener<D> onLoadCompleteListener = this.mListener;
        if (onLoadCompleteListener != null) {
            onLoadCompleteListener.onLoadComplete(this, data);
        }
    }

    @MainThread
    public void deliverCancellation() {
        OnLoadCanceledListener<D> onLoadCanceledListener = this.mOnLoadCanceledListener;
        if (onLoadCanceledListener != null) {
            onLoadCanceledListener.onLoadCanceled(this);
        }
    }

    @NonNull
    public Context getContext() {
        return this.mContext;
    }

    public int getId() {
        return this.mId;
    }

    @MainThread
    public void registerListener(int id, @NonNull OnLoadCompleteListener<D> listener) {
        if (this.mListener != null) {
            throw new IllegalStateException("There is already a listener registered");
        }
        this.mListener = listener;
        this.mId = id;
    }

    @MainThread
    public void unregisterListener(@NonNull OnLoadCompleteListener<D> listener) {
        OnLoadCompleteListener<D> onLoadCompleteListener = this.mListener;
        if (onLoadCompleteListener == null) {
            throw new IllegalStateException("No listener register");
        }
        if (onLoadCompleteListener != listener) {
            throw new IllegalArgumentException("Attempting to unregister the wrong listener");
        }
        this.mListener = null;
    }

    @MainThread
    public void registerOnLoadCanceledListener(@NonNull OnLoadCanceledListener<D> listener) {
        if (this.mOnLoadCanceledListener != null) {
            throw new IllegalStateException("There is already a listener registered");
        }
        this.mOnLoadCanceledListener = listener;
    }

    @MainThread
    public void unregisterOnLoadCanceledListener(@NonNull OnLoadCanceledListener<D> listener) {
        OnLoadCanceledListener<D> onLoadCanceledListener = this.mOnLoadCanceledListener;
        if (onLoadCanceledListener == null) {
            throw new IllegalStateException("No listener register");
        }
        if (onLoadCanceledListener != listener) {
            throw new IllegalArgumentException("Attempting to unregister the wrong listener");
        }
        this.mOnLoadCanceledListener = null;
    }

    public boolean isStarted() {
        return this.mStarted;
    }

    public boolean isAbandoned() {
        return this.mAbandoned;
    }

    public boolean isReset() {
        return this.mReset;
    }

    @MainThread
    public final void startLoading() {
        this.mStarted = true;
        this.mReset = false;
        this.mAbandoned = false;
        onStartLoading();
    }

    @MainThread
    protected void onStartLoading() {
    }

    @MainThread
    public boolean cancelLoad() {
        return onCancelLoad();
    }

    @MainThread
    protected boolean onCancelLoad() {
        return false;
    }

    @MainThread
    public void forceLoad() {
        onForceLoad();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @MainThread
    public void onForceLoad() {
    }

    @MainThread
    public void stopLoading() {
        this.mStarted = false;
        onStopLoading();
    }

    @MainThread
    protected void onStopLoading() {
    }

    @MainThread
    public void abandon() {
        this.mAbandoned = true;
        onAbandon();
    }

    @MainThread
    protected void onAbandon() {
    }

    @MainThread
    public void reset() {
        onReset();
        this.mReset = true;
        this.mStarted = false;
        this.mAbandoned = false;
        this.mContentChanged = false;
        this.mProcessingChange = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @MainThread
    public void onReset() {
    }

    public boolean takeContentChanged() {
        boolean res = this.mContentChanged;
        this.mContentChanged = false;
        this.mProcessingChange |= res;
        return res;
    }

    public void commitContentChanged() {
        this.mProcessingChange = false;
    }

    public void rollbackContentChanged() {
        if (this.mProcessingChange) {
            onContentChanged();
        }
    }

    @MainThread
    public void onContentChanged() {
        if (this.mStarted) {
            forceLoad();
        } else {
            this.mContentChanged = true;
        }
    }

    @NonNull
    public String dataToString(@Nullable D data) {
        StringBuilder sb = new StringBuilder(64);
        DebugUtils.buildShortClassTag(data, sb);
        sb.append("}");
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        DebugUtils.buildShortClassTag(this, sb);
        sb.append(" id=");
        sb.append(this.mId);
        sb.append("}");
        return sb.toString();
    }

    @Deprecated
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.print(prefix);
        writer.print("mId=");
        writer.print(this.mId);
        writer.print(" mListener=");
        writer.println(this.mListener);
        if (this.mStarted || this.mContentChanged || this.mProcessingChange) {
            writer.print(prefix);
            writer.print("mStarted=");
            writer.print(this.mStarted);
            writer.print(" mContentChanged=");
            writer.print(this.mContentChanged);
            writer.print(" mProcessingChange=");
            writer.println(this.mProcessingChange);
        }
        if (this.mAbandoned || this.mReset) {
            writer.print(prefix);
            writer.print("mAbandoned=");
            writer.print(this.mAbandoned);
            writer.print(" mReset=");
            writer.println(this.mReset);
        }
    }
}