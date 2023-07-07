package me.goldze.mvvmhabit.crash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.Thread;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: classes.dex */
public final class CustomActivityOnCrash {
    private static final String CAOC_HANDLER_PACKAGE_NAME = "cat.ereza.customactivityoncrash";
    private static final String DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os";
    private static final String EXTRA_ACTIVITY_LOG = "cat.ereza.customactivityoncrash.EXTRA_ACTIVITY_LOG";
    private static final String EXTRA_CONFIG = "cat.ereza.customactivityoncrash.EXTRA_CONFIG";
    private static final String EXTRA_STACK_TRACE = "cat.ereza.customactivityoncrash.EXTRA_STACK_TRACE";
    private static final String INTENT_ACTION_ERROR_ACTIVITY = "cat.ereza.customactivityoncrash.ERROR";
    private static final String INTENT_ACTION_RESTART_ACTIVITY = "cat.ereza.customactivityoncrash.RESTART";
    private static final int MAX_ACTIVITIES_IN_LOG = 50;
    private static final int MAX_STACK_TRACE_SIZE = 131071;
    private static final String SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp";
    private static final String SHARED_PREFERENCES_FILE = "custom_activity_on_crash";
    private static final String TAG = "CustomActivityOnCrash";
    @SuppressLint({"StaticFieldLeak"})
    private static Application application;
    private static CaocConfig config = new CaocConfig();
    private static Deque<String> activityLog = new ArrayDeque(50);
    private static WeakReference<Activity> lastActivityCreated = new WeakReference<>(null);
    private static boolean isInBackground = true;

    /* loaded from: classes.dex */
    public interface EventListener extends Serializable {
        void onCloseAppFromErrorActivity();

        void onLaunchErrorActivity();

        void onRestartAppFromErrorActivity();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static void install(@Nullable Context context) {
        try {
            if (context == null) {
                Log.e(TAG, "Install failed: context is null!");
                return;
            }
            final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
            if (oldHandler != null && oldHandler.getClass().getName().startsWith(CAOC_HANDLER_PACKAGE_NAME)) {
                Log.e(TAG, "CustomActivityOnCrash was already installed, doing nothing!");
            } else {
                if (oldHandler != null && !oldHandler.getClass().getName().startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
                    Log.e(TAG, "IMPORTANT WARNING! You already have an UncaughtExceptionHandler, are you sure this is correct? If you use a custom UncaughtExceptionHandler, you must initialize it AFTER CustomActivityOnCrash! Installing anyway, but your original handler will not be called.");
                }
                application = (Application) context.getApplicationContext();
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { // from class: me.goldze.mvvmhabit.crash.CustomActivityOnCrash.1
                    @Override // java.lang.Thread.UncaughtExceptionHandler
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
                        if (CustomActivityOnCrash.config.isEnabled()) {
                            Log.e(CustomActivityOnCrash.TAG, "App has crashed, executing CustomActivityOnCrash's UncaughtExceptionHandler", throwable);
                            if (!CustomActivityOnCrash.hasCrashedInTheLastSeconds(CustomActivityOnCrash.application)) {
                                CustomActivityOnCrash.setLastCrashTimestamp(CustomActivityOnCrash.application, new Date().getTime());
                                Class<? extends Activity> errorActivityClass = CustomActivityOnCrash.config.getErrorActivityClass();
                                if (errorActivityClass == null) {
                                    errorActivityClass = CustomActivityOnCrash.guessErrorActivityClass(CustomActivityOnCrash.application);
                                }
                                if (!CustomActivityOnCrash.isStackTraceLikelyConflictive(throwable, errorActivityClass)) {
                                    if (CustomActivityOnCrash.config.getBackgroundMode() != 1 && CustomActivityOnCrash.isInBackground) {
                                        if (CustomActivityOnCrash.config.getBackgroundMode() == 2 && (uncaughtExceptionHandler = oldHandler) != null) {
                                            uncaughtExceptionHandler.uncaughtException(thread, throwable);
                                            return;
                                        }
                                    } else {
                                        Intent intent = new Intent(CustomActivityOnCrash.application, errorActivityClass);
                                        StringWriter sw = new StringWriter();
                                        PrintWriter pw = new PrintWriter(sw);
                                        throwable.printStackTrace(pw);
                                        String stackTraceString = sw.toString();
                                        if (stackTraceString.length() > CustomActivityOnCrash.MAX_STACK_TRACE_SIZE) {
                                            stackTraceString = stackTraceString.substring(0, CustomActivityOnCrash.MAX_STACK_TRACE_SIZE - " [stack trace too large]".length()) + " [stack trace too large]";
                                        }
                                        intent.putExtra(CustomActivityOnCrash.EXTRA_STACK_TRACE, stackTraceString);
                                        if (CustomActivityOnCrash.config.isTrackActivities()) {
                                            String activityLogString = "";
                                            while (!CustomActivityOnCrash.activityLog.isEmpty()) {
                                                activityLogString = activityLogString + ((String) CustomActivityOnCrash.activityLog.poll());
                                            }
                                            intent.putExtra(CustomActivityOnCrash.EXTRA_ACTIVITY_LOG, activityLogString);
                                        }
                                        if (CustomActivityOnCrash.config.isShowRestartButton() && CustomActivityOnCrash.config.getRestartActivityClass() == null) {
                                            CustomActivityOnCrash.config.setRestartActivityClass(CustomActivityOnCrash.guessRestartActivityClass(CustomActivityOnCrash.application));
                                        }
                                        intent.putExtra(CustomActivityOnCrash.EXTRA_CONFIG, CustomActivityOnCrash.config);
                                        intent.setFlags(268468224);
                                        if (CustomActivityOnCrash.config.getEventListener() != null) {
                                            CustomActivityOnCrash.config.getEventListener().onLaunchErrorActivity();
                                        }
                                        CustomActivityOnCrash.application.startActivity(intent);
                                    }
                                } else {
                                    Log.e(CustomActivityOnCrash.TAG, "Your application class or your error activity have crashed, the custom activity will not be launched!");
                                    Thread.UncaughtExceptionHandler uncaughtExceptionHandler2 = oldHandler;
                                    if (uncaughtExceptionHandler2 != null) {
                                        uncaughtExceptionHandler2.uncaughtException(thread, throwable);
                                        return;
                                    }
                                }
                            } else {
                                Log.e(CustomActivityOnCrash.TAG, "App already crashed recently, not starting custom error activity because we could enter a restart loop. Are you sure that your app does not crash directly on init?", throwable);
                                Thread.UncaughtExceptionHandler uncaughtExceptionHandler3 = oldHandler;
                                if (uncaughtExceptionHandler3 != null) {
                                    uncaughtExceptionHandler3.uncaughtException(thread, throwable);
                                    return;
                                }
                            }
                            Activity lastActivity = (Activity) CustomActivityOnCrash.lastActivityCreated.get();
                            if (lastActivity != null) {
                                lastActivity.finish();
                                CustomActivityOnCrash.lastActivityCreated.clear();
                            }
                            CustomActivityOnCrash.killCurrentProcess();
                            return;
                        }
                        Thread.UncaughtExceptionHandler uncaughtExceptionHandler4 = oldHandler;
                        if (uncaughtExceptionHandler4 != null) {
                            uncaughtExceptionHandler4.uncaughtException(thread, throwable);
                        }
                    }
                });
                application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() { // from class: me.goldze.mvvmhabit.crash.CustomActivityOnCrash.2
                    int currentlyStartedActivities = 0;
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

                    @Override // android.app.Application.ActivityLifecycleCallbacks
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        if (activity.getClass() != CustomActivityOnCrash.config.getErrorActivityClass()) {
                            WeakReference unused = CustomActivityOnCrash.lastActivityCreated = new WeakReference(activity);
                        }
                        if (CustomActivityOnCrash.config.isTrackActivities()) {
                            Deque deque = CustomActivityOnCrash.activityLog;
                            deque.add(this.dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " created\n");
                        }
                    }

                    @Override // android.app.Application.ActivityLifecycleCallbacks
                    public void onActivityStarted(Activity activity) {
                        boolean z = true;
                        this.currentlyStartedActivities++;
                        if (this.currentlyStartedActivities != 0) {
                            z = false;
                        }
                        boolean unused = CustomActivityOnCrash.isInBackground = z;
                    }

                    @Override // android.app.Application.ActivityLifecycleCallbacks
                    public void onActivityResumed(Activity activity) {
                        if (CustomActivityOnCrash.config.isTrackActivities()) {
                            Deque deque = CustomActivityOnCrash.activityLog;
                            deque.add(this.dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " resumed\n");
                        }
                    }

                    @Override // android.app.Application.ActivityLifecycleCallbacks
                    public void onActivityPaused(Activity activity) {
                        if (CustomActivityOnCrash.config.isTrackActivities()) {
                            Deque deque = CustomActivityOnCrash.activityLog;
                            deque.add(this.dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " paused\n");
                        }
                    }

                    @Override // android.app.Application.ActivityLifecycleCallbacks
                    public void onActivityStopped(Activity activity) {
                        boolean z = true;
                        this.currentlyStartedActivities--;
                        if (this.currentlyStartedActivities != 0) {
                            z = false;
                        }
                        boolean unused = CustomActivityOnCrash.isInBackground = z;
                    }

                    @Override // android.app.Application.ActivityLifecycleCallbacks
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    }

                    @Override // android.app.Application.ActivityLifecycleCallbacks
                    public void onActivityDestroyed(Activity activity) {
                        if (CustomActivityOnCrash.config.isTrackActivities()) {
                            Deque deque = CustomActivityOnCrash.activityLog;
                            deque.add(this.dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " destroyed\n");
                        }
                    }
                });
            }
            Log.i(TAG, "CustomActivityOnCrash has been installed.");
        } catch (Throwable t) {
            Log.e(TAG, "An unknown error occurred while installing CustomActivityOnCrash, it may not have been properly initialized. Please report this as a bug if needed.", t);
        }
    }

    @NonNull
    public static String getStackTraceFromIntent(@NonNull Intent intent) {
        return intent.getStringExtra(EXTRA_STACK_TRACE);
    }

    @NonNull
    public static CaocConfig getConfigFromIntent(@NonNull Intent intent) {
        return (CaocConfig) intent.getSerializableExtra(EXTRA_CONFIG);
    }

    @Nullable
    public static String getActivityLogFromIntent(@NonNull Intent intent) {
        return intent.getStringExtra(EXTRA_ACTIVITY_LOG);
    }

    @NonNull
    public static String getAllErrorDetailsFromIntent(@NonNull Context context, @NonNull Intent intent) {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String buildDateAsString = getBuildDateAsString(context, dateFormat);
        String versionName = getVersionName(context);
        String errorDetails = "Build version: " + versionName + " \n";
        if (buildDateAsString != null) {
            errorDetails = errorDetails + "Build date: " + buildDateAsString + " \n";
        }
        String errorDetails2 = (((errorDetails + "Current date: " + dateFormat.format(currentDate) + " \n") + "Device: " + getDeviceModelName() + " \n \n") + "Stack trace:  \n") + getStackTraceFromIntent(intent);
        String activityLog2 = getActivityLogFromIntent(intent);
        if (activityLog2 != null) {
            return (errorDetails2 + "\nUser actions: \n") + activityLog2;
        }
        return errorDetails2;
    }

    public static void restartApplicationWithIntent(@NonNull Activity activity, @NonNull Intent intent, @NonNull CaocConfig config2) {
        intent.addFlags(270565376);
        if (intent.getComponent() != null) {
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
        }
        if (config2.getEventListener() != null) {
            config2.getEventListener().onRestartAppFromErrorActivity();
        }
        activity.finish();
        activity.startActivity(intent);
        killCurrentProcess();
    }

    public static void restartApplication(@NonNull Activity activity, @NonNull CaocConfig config2) {
        Intent intent = new Intent(activity, config2.getRestartActivityClass());
        restartApplicationWithIntent(activity, intent, config2);
    }

    public static void closeApplication(@NonNull Activity activity, @NonNull CaocConfig config2) {
        if (config2.getEventListener() != null) {
            config2.getEventListener().onCloseAppFromErrorActivity();
        }
        activity.finish();
        killCurrentProcess();
    }

    @NonNull
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static CaocConfig getConfig() {
        return config;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static void setConfig(@NonNull CaocConfig config2) {
        config = config2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isStackTraceLikelyConflictive(@NonNull Throwable throwable, @NonNull Class<? extends Activity> activityClass) {
        Throwable cause;
        do {
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                if ((element.getClassName().equals("android.app.ActivityThread") && element.getMethodName().equals("handleBindApplication")) || element.getClassName().equals(activityClass.getName())) {
                    return true;
                }
            }
            cause = throwable.getCause();
            throwable = cause;
        } while (cause != null);
        return false;
    }

    @Nullable
    private static String getBuildDateAsString(@NonNull Context context, @NonNull DateFormat dateFormat) {
        long buildDate;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            buildDate = ze.getTime();
            zf.close();
        } catch (Exception e) {
            buildDate = 0;
        }
        if (buildDate > 312764400000L) {
            return dateFormat.format(new Date(buildDate));
        }
        return null;
    }

    @NonNull
    private static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    @NonNull
    private static String getDeviceModelName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    @NonNull
    private static String capitalize(@Nullable String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        }
        return Character.toUpperCase(first) + s.substring(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public static Class<? extends Activity> guessRestartActivityClass(@NonNull Context context) {
        Class<? extends Activity> resolvedActivityClass = getRestartActivityClassWithIntentFilter(context);
        if (resolvedActivityClass == null) {
            return getLauncherActivity(context);
        }
        return resolvedActivityClass;
    }

    @Nullable
    private static Class<? extends Activity> getRestartActivityClassWithIntentFilter(@NonNull Context context) {
        Intent searchedIntent = new Intent().setAction(INTENT_ACTION_RESTART_ACTIVITY).setPackage(context.getPackageName());
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(searchedIntent, 64);
        if (resolveInfos != null && resolveInfos.size() > 0) {
            ResolveInfo resolveInfo = resolveInfos.get(0);
            try {
                return Class.forName(resolveInfo.activityInfo.name);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Failed when resolving the restart activity class via intent filter, stack trace follows!", e);
                return null;
            }
        }
        return null;
    }

    @Nullable
    private static Class<? extends Activity> getLauncherActivity(@NonNull Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            try {
                return Class.forName(intent.getComponent().getClassName());
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Failed when resolving the restart activity class via getLaunchIntentForPackage, stack trace follows!", e);
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @NonNull
    public static Class<? extends Activity> guessErrorActivityClass(@NonNull Context context) {
        Class<? extends Activity> resolvedActivityClass = getErrorActivityClassWithIntentFilter(context);
        if (resolvedActivityClass == null) {
            return DefaultErrorActivity.class;
        }
        return resolvedActivityClass;
    }

    @Nullable
    private static Class<? extends Activity> getErrorActivityClassWithIntentFilter(@NonNull Context context) {
        Intent searchedIntent = new Intent().setAction(INTENT_ACTION_ERROR_ACTIVITY).setPackage(context.getPackageName());
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(searchedIntent, 64);
        if (resolveInfos != null && resolveInfos.size() > 0) {
            ResolveInfo resolveInfo = resolveInfos.get(0);
            try {
                return Class.forName(resolveInfo.activityInfo.name);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Failed when resolving the error activity class via intent filter, stack trace follows!", e);
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void killCurrentProcess() {
        Process.killProcess(Process.myPid());
        System.exit(10);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"ApplySharedPref"})
    public static void setLastCrashTimestamp(@NonNull Context context, long timestamp) {
        context.getSharedPreferences(SHARED_PREFERENCES_FILE, 0).edit().putLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp).commit();
    }

    private static long getLastCrashTimestamp(@NonNull Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, 0).getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, -1L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean hasCrashedInTheLastSeconds(@NonNull Context context) {
        long lastTimestamp = getLastCrashTimestamp(context);
        long currentTimestamp = new Date().getTime();
        return lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < ((long) config.getMinTimeBetweenCrashesMs());
    }
}
