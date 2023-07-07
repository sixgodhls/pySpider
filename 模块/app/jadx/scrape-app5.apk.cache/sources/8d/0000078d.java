package android.arch.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentActivity;

/* loaded from: classes.dex */
public class ViewModelProviders {
    private static Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

    private static Activity checkActivity(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Can't create ViewModelProvider for detached fragment");
        }
        return activity;
    }

    @NonNull
    @MainThread
    /* renamed from: of */
    public static ViewModelProvider m61of(@NonNull Fragment fragment) {
        return m60of(fragment, (ViewModelProvider.Factory) null);
    }

    @NonNull
    @MainThread
    /* renamed from: of */
    public static ViewModelProvider m59of(@NonNull FragmentActivity activity) {
        return m58of(activity, (ViewModelProvider.Factory) null);
    }

    @NonNull
    @MainThread
    /* renamed from: of */
    public static ViewModelProvider m60of(@NonNull Fragment fragment, @Nullable ViewModelProvider.Factory factory) {
        Application application = checkApplication(checkActivity(fragment));
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return new ViewModelProvider(ViewModelStores.m57of(fragment), factory);
    }

    @NonNull
    @MainThread
    /* renamed from: of */
    public static ViewModelProvider m58of(@NonNull FragmentActivity activity, @Nullable ViewModelProvider.Factory factory) {
        Application application = checkApplication(activity);
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return new ViewModelProvider(ViewModelStores.m56of(activity), factory);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public static class DefaultFactory extends ViewModelProvider.AndroidViewModelFactory {
        @Deprecated
        public DefaultFactory(@NonNull Application application) {
            super(application);
        }
    }
}