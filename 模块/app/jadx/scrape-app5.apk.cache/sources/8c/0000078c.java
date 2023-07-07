package android.arch.lifecycle;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentActivity;

/* loaded from: classes.dex */
public class ViewModelStores {
    private ViewModelStores() {
    }

    @NonNull
    @MainThread
    /* renamed from: of */
    public static ViewModelStore m56of(@NonNull FragmentActivity activity) {
        if (activity instanceof ViewModelStoreOwner) {
            return activity.getViewModelStore();
        }
        return HolderFragment.holderFragmentFor(activity).getViewModelStore();
    }

    @NonNull
    @MainThread
    /* renamed from: of */
    public static ViewModelStore m57of(@NonNull Fragment fragment) {
        if (fragment instanceof ViewModelStoreOwner) {
            return fragment.getViewModelStore();
        }
        return HolderFragment.holderFragmentFor(fragment).getViewModelStore();
    }
}