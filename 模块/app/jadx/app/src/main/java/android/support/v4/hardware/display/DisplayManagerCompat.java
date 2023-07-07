package android.support.v4.hardware.display;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.WindowManager;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class DisplayManagerCompat {
    public static final String DISPLAY_CATEGORY_PRESENTATION = "android.hardware.display.category.PRESENTATION";
    private static final WeakHashMap<Context, DisplayManagerCompat> sInstances = new WeakHashMap<>();
    private final Context mContext;

    private DisplayManagerCompat(Context context) {
        this.mContext = context;
    }

    @NonNull
    public static DisplayManagerCompat getInstance(@NonNull Context context) {
        DisplayManagerCompat instance;
        synchronized (sInstances) {
            instance = sInstances.get(context);
            if (instance == null) {
                instance = new DisplayManagerCompat(context);
                sInstances.put(context, instance);
            }
        }
        return instance;
    }

    @Nullable
    public Display getDisplay(int displayId) {
        if (Build.VERSION.SDK_INT >= 17) {
            return ((DisplayManager) this.mContext.getSystemService("display")).getDisplay(displayId);
        }
        Display display = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
        if (display.getDisplayId() == displayId) {
            return display;
        }
        return null;
    }

    @NonNull
    public Display[] getDisplays() {
        if (Build.VERSION.SDK_INT >= 17) {
            return ((DisplayManager) this.mContext.getSystemService("display")).getDisplays();
        }
        Display display = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
        return new Display[]{display};
    }

    @NonNull
    public Display[] getDisplays(@Nullable String category) {
        if (Build.VERSION.SDK_INT >= 17) {
            return ((DisplayManager) this.mContext.getSystemService("display")).getDisplays(category);
        }
        if (category == null) {
            return new Display[0];
        }
        Display display = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
        return new Display[]{display};
    }
}
