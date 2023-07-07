package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.p000v4.view.GravityCompat;

/* loaded from: classes.dex */
public enum GravityEnum {
    START,
    CENTER,
    END;
    
    private static final boolean HAS_RTL;

    static {
        boolean z = false;
        if (Build.VERSION.SDK_INT >= 17) {
            z = true;
        }
        HAS_RTL = z;
    }

    @SuppressLint({"RtlHardcoded"})
    public int getGravityInt() {
        switch (this) {
            case START:
                if (!HAS_RTL) {
                    return 3;
                }
                return GravityCompat.START;
            case CENTER:
                return 1;
            case END:
                if (!HAS_RTL) {
                    return 5;
                }
                return GravityCompat.END;
            default:
                throw new IllegalStateException("Invalid gravity constant");
        }
    }

    @TargetApi(17)
    public int getTextAlignment() {
        switch (this) {
            case CENTER:
                return 4;
            case END:
                return 6;
            default:
                return 5;
        }
    }
}