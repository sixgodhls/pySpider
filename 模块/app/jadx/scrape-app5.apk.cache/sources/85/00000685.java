package me.goldze.mvvmhabit.crash;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.PathInterpolatorCompat;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import me.goldze.mvvmhabit.crash.CustomActivityOnCrash;

/* loaded from: classes.dex */
public class CaocConfig implements Serializable {
    public static final int BACKGROUND_MODE_CRASH = 2;
    public static final int BACKGROUND_MODE_SHOW_CUSTOM = 1;
    public static final int BACKGROUND_MODE_SILENT = 0;
    private int backgroundMode = 1;
    private boolean enabled = true;
    private boolean showErrorDetails = true;
    private boolean showRestartButton = true;
    private boolean trackActivities = false;
    private int minTimeBetweenCrashesMs = PathInterpolatorCompat.MAX_NUM_POINTS;
    private Integer errorDrawable = null;
    private Class<? extends Activity> errorActivityClass = null;
    private Class<? extends Activity> restartActivityClass = null;
    private CustomActivityOnCrash.EventListener eventListener = null;

    public int getBackgroundMode() {
        return this.backgroundMode;
    }

    public void setBackgroundMode(int backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isShowErrorDetails() {
        return this.showErrorDetails;
    }

    public void setShowErrorDetails(boolean showErrorDetails) {
        this.showErrorDetails = showErrorDetails;
    }

    public boolean isShowRestartButton() {
        return this.showRestartButton;
    }

    public void setShowRestartButton(boolean showRestartButton) {
        this.showRestartButton = showRestartButton;
    }

    public boolean isTrackActivities() {
        return this.trackActivities;
    }

    public void setTrackActivities(boolean trackActivities) {
        this.trackActivities = trackActivities;
    }

    public int getMinTimeBetweenCrashesMs() {
        return this.minTimeBetweenCrashesMs;
    }

    public void setMinTimeBetweenCrashesMs(int minTimeBetweenCrashesMs) {
        this.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs;
    }

    @DrawableRes
    @Nullable
    public Integer getErrorDrawable() {
        return this.errorDrawable;
    }

    public void setErrorDrawable(@DrawableRes @Nullable Integer errorDrawable) {
        this.errorDrawable = errorDrawable;
    }

    @Nullable
    public Class<? extends Activity> getErrorActivityClass() {
        return this.errorActivityClass;
    }

    public void setErrorActivityClass(@Nullable Class<? extends Activity> errorActivityClass) {
        this.errorActivityClass = errorActivityClass;
    }

    @Nullable
    public Class<? extends Activity> getRestartActivityClass() {
        return this.restartActivityClass;
    }

    public void setRestartActivityClass(@Nullable Class<? extends Activity> restartActivityClass) {
        this.restartActivityClass = restartActivityClass;
    }

    @Nullable
    public CustomActivityOnCrash.EventListener getEventListener() {
        return this.eventListener;
    }

    public void setEventListener(@Nullable CustomActivityOnCrash.EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private CaocConfig config;

        @NonNull
        public static Builder create() {
            Builder builder = new Builder();
            CaocConfig currentConfig = CustomActivityOnCrash.getConfig();
            CaocConfig config = new CaocConfig();
            config.backgroundMode = currentConfig.backgroundMode;
            config.enabled = currentConfig.enabled;
            config.showErrorDetails = currentConfig.showErrorDetails;
            config.showRestartButton = currentConfig.showRestartButton;
            config.trackActivities = currentConfig.trackActivities;
            config.minTimeBetweenCrashesMs = currentConfig.minTimeBetweenCrashesMs;
            config.errorDrawable = currentConfig.errorDrawable;
            config.errorActivityClass = currentConfig.errorActivityClass;
            config.restartActivityClass = currentConfig.restartActivityClass;
            config.eventListener = currentConfig.eventListener;
            builder.config = config;
            return builder;
        }

        @NonNull
        public Builder backgroundMode(int backgroundMode) {
            this.config.backgroundMode = backgroundMode;
            return this;
        }

        @NonNull
        public Builder enabled(boolean enabled) {
            this.config.enabled = enabled;
            return this;
        }

        @NonNull
        public Builder showErrorDetails(boolean showErrorDetails) {
            this.config.showErrorDetails = showErrorDetails;
            return this;
        }

        @NonNull
        public Builder showRestartButton(boolean showRestartButton) {
            this.config.showRestartButton = showRestartButton;
            return this;
        }

        @NonNull
        public Builder trackActivities(boolean trackActivities) {
            this.config.trackActivities = trackActivities;
            return this;
        }

        @NonNull
        public Builder minTimeBetweenCrashesMs(int minTimeBetweenCrashesMs) {
            this.config.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs;
            return this;
        }

        @NonNull
        public Builder errorDrawable(@DrawableRes @Nullable Integer errorDrawable) {
            this.config.errorDrawable = errorDrawable;
            return this;
        }

        @NonNull
        public Builder errorActivity(@Nullable Class<? extends Activity> errorActivityClass) {
            this.config.errorActivityClass = errorActivityClass;
            return this;
        }

        @NonNull
        public Builder restartActivity(@Nullable Class<? extends Activity> restartActivityClass) {
            this.config.restartActivityClass = restartActivityClass;
            return this;
        }

        @NonNull
        public Builder eventListener(@Nullable CustomActivityOnCrash.EventListener eventListener) {
            if (eventListener == null || eventListener.getClass().getEnclosingClass() == null || Modifier.isStatic(eventListener.getClass().getModifiers())) {
                this.config.eventListener = eventListener;
                return this;
            }
            throw new IllegalArgumentException("The event listener cannot be an inner or anonymous class, because it will need to be serialized. Change it to a class of its own, or make it a static inner class.");
        }

        @NonNull
        public CaocConfig get() {
            return this.config;
        }

        public void apply() {
            CustomActivityOnCrash.setConfig(this.config);
        }
    }
}