package android.support.p000v4.app;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.content.Loader;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* renamed from: android.support.v4.app.LoaderManager */
/* loaded from: classes.dex */
public abstract class LoaderManager {

    /* renamed from: android.support.v4.app.LoaderManager$LoaderCallbacks */
    /* loaded from: classes.dex */
    public interface LoaderCallbacks<D> {
        @NonNull
        @MainThread
        Loader<D> onCreateLoader(int i, @Nullable Bundle bundle);

        @MainThread
        void onLoadFinished(@NonNull Loader<D> loader, D d);

        @MainThread
        void onLoaderReset(@NonNull Loader<D> loader);
    }

    @MainThread
    public abstract void destroyLoader(int i);

    @Deprecated
    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    @Nullable
    public abstract <D> Loader<D> getLoader(int i);

    @NonNull
    @MainThread
    public abstract <D> Loader<D> initLoader(int i, @Nullable Bundle bundle, @NonNull LoaderCallbacks<D> loaderCallbacks);

    public abstract void markForRedelivery();

    @NonNull
    @MainThread
    public abstract <D> Loader<D> restartLoader(int i, @Nullable Bundle bundle, @NonNull LoaderCallbacks<D> loaderCallbacks);

    @NonNull
    public static <T extends LifecycleOwner & ViewModelStoreOwner> LoaderManager getInstance(@NonNull T owner) {
        return new LoaderManagerImpl(owner, owner.getViewModelStore());
    }

    public static void enableDebugLogging(boolean enabled) {
        LoaderManagerImpl.DEBUG = enabled;
    }

    public boolean hasRunningLoaders() {
        return false;
    }
}