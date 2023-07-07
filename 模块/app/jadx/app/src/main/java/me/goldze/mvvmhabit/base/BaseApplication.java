package me.goldze.mvvmhabit.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import me.goldze.mvvmhabit.utils.Utils;

/* loaded from: classes.dex */
public class BaseApplication extends Application {
    private static Application sInstance;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        setApplication(this);
    }

    public static synchronized void setApplication(@NonNull Application application) {
        synchronized (BaseApplication.class) {
            sInstance = application;
            Utils.init(application);
            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() { // from class: me.goldze.mvvmhabit.base.BaseApplication.1
                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    AppManager.getAppManager().addActivity(activity);
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityStarted(Activity activity) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityResumed(Activity activity) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityPaused(Activity activity) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityStopped(Activity activity) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityDestroyed(Activity activity) {
                    AppManager.getAppManager().removeActivity(activity);
                }
            });
        }
    }

    public static Application getInstance() {
        Application application = sInstance;
        if (application == null) {
            throw new NullPointerException("please inherit BaseApplication or call setApplication.");
        }
        return application;
    }
}
