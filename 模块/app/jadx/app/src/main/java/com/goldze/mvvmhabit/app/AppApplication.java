package com.goldze.mvvmhabit.app;

import com.goldze.mvvmhabit.BuildConfig;
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.ui.MainActivity;
import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;

/* loaded from: classes.dex */
public class AppApplication extends BaseApplication {
    @Override // me.goldze.mvvmhabit.base.BaseApplication, android.app.Application
    public void onCreate() {
        super.onCreate();
        KLog.init(BuildConfig.DEBUG);
        initCrash();
    }

    private void initCrash() {
        CaocConfig.Builder.create().backgroundMode(0).enabled(true).showErrorDetails(true).showRestartButton(true).trackActivities(true).minTimeBetweenCrashesMs(2000).errorDrawable(Integer.valueOf((int) R.mipmap.ic_launcher)).restartActivity(MainActivity.class).apply();
    }
}
