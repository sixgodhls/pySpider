package android.arch.lifecycle;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/* loaded from: classes.dex */
public class ViewModelStores {
    private ViewModelStores() {
    }

    @NonNull
    @MainThread
    public static ViewModelStore of(@NonNull FragmentActivity activity) {
        if (activity instanceof ViewModelStoreOwner) {
            return activity.getViewModelStore();
        }
        return HolderFragment.holderFragmentFor(activity).getViewModelStore();
    }

    @NonNull
    @MainThread
    public static ViewModelStore of(@NonNull Fragment fragment) {
        if (fragment instanceof ViewModelStoreOwner) {
            return fragment.getViewModelStore();
        }
        return HolderFragment.holderFragmentFor(fragment).getViewModelStore();
    }
}
