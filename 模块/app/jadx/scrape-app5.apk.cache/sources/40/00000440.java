package com.afollestad.materialdialogs.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

/* loaded from: classes.dex */
public class DialogUtils {
    @ColorInt
    public static int getDisabledColor(Context context) {
        int primaryColor = resolveColor(context, 16842806);
        int disabledColor = isColorDark(primaryColor) ? ViewCompat.MEASURED_STATE_MASK : -1;
        return adjustAlpha(disabledColor, 0.3f);
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @ColorInt
    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    @ColorInt
    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList resolveActionTextColorStateList(Context context, @AttrRes int colorAttr, ColorStateList fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{colorAttr});
        try {
            TypedValue value = a.peekValue(0);
            if (value == null) {
                return fallback;
            }
            if (value.type >= 28 && value.type <= 31) {
                return getActionTextStateList(context, value.data);
            }
            ColorStateList stateList = a.getColorStateList(0);
            return stateList != null ? stateList : fallback;
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList getActionTextColorStateList(Context context, @ColorRes int colorId) {
        TypedValue value = new TypedValue();
        context.getResources().getValue(colorId, value, true);
        if (value.type >= 28 && value.type <= 31) {
            return getActionTextStateList(context, value.data);
        }
        if (Build.VERSION.SDK_INT <= 22) {
            return context.getResources().getColorStateList(colorId);
        }
        return context.getColorStateList(colorId);
    }

    @ColorInt
    public static int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    public static String resolveString(Context context, @AttrRes int attr) {
        TypedValue v = new TypedValue();
        context.getTheme().resolveAttribute(attr, v, true);
        return (String) v.string;
    }

    private static int gravityEnumToAttrInt(GravityEnum value) {
        switch (value) {
            case CENTER:
                return 1;
            case END:
                return 2;
            default:
                return 0;
        }
    }

    public static GravityEnum resolveGravityEnum(Context context, @AttrRes int attr, GravityEnum defaultGravity) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            switch (a.getInt(0, gravityEnumToAttrInt(defaultGravity))) {
                case 1:
                    return GravityEnum.CENTER;
                case 2:
                    return GravityEnum.END;
                default:
                    return GravityEnum.START;
            }
        } finally {
            a.recycle();
        }
    }

    public static Drawable resolveDrawable(Context context, @AttrRes int attr) {
        return resolveDrawable(context, attr, null);
    }

    private static Drawable resolveDrawable(Context context, @AttrRes int attr, Drawable fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            Drawable d = a.getDrawable(0);
            if (d == null && fallback != null) {
                d = fallback;
            }
            return d;
        } finally {
            a.recycle();
        }
    }

    public static int resolveDimension(Context context, @AttrRes int attr) {
        return resolveDimension(context, attr, -1);
    }

    private static int resolveDimension(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getDimensionPixelSize(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static boolean resolveBoolean(Context context, @AttrRes int attr, boolean fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getBoolean(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static boolean resolveBoolean(Context context, @AttrRes int attr) {
        return resolveBoolean(context, attr, false);
    }

    public static boolean isColorDark(@ColorInt int color) {
        double red = Color.red(color);
        Double.isNaN(red);
        double green = Color.green(color);
        Double.isNaN(green);
        double d = (red * 0.299d) + (green * 0.587d);
        double blue = Color.blue(color);
        Double.isNaN(blue);
        double darkness = 1.0d - ((d + (blue * 0.114d)) / 255.0d);
        return darkness >= 0.5d;
    }

    public static void setBackgroundCompat(View view, Drawable d) {
        if (Build.VERSION.SDK_INT < 16) {
            view.setBackgroundDrawable(d);
        } else {
            view.setBackground(d);
        }
    }

    public static void showKeyboard(@NonNull DialogInterface di, @NonNull final MaterialDialog.Builder builder) {
        final MaterialDialog dialog = (MaterialDialog) di;
        if (dialog.getInputEditText() == null) {
            return;
        }
        dialog.getInputEditText().post(new Runnable() { // from class: com.afollestad.materialdialogs.util.DialogUtils.1
            @Override // java.lang.Runnable
            public void run() {
                MaterialDialog.this.getInputEditText().requestFocus();
                InputMethodManager imm = (InputMethodManager) builder.getContext().getSystemService("input_method");
                if (imm != null) {
                    imm.showSoftInput(MaterialDialog.this.getInputEditText(), 1);
                }
            }
        });
    }

    public static void hideKeyboard(@NonNull DialogInterface di, @NonNull MaterialDialog.Builder builder) {
        InputMethodManager imm;
        MaterialDialog dialog = (MaterialDialog) di;
        if (dialog.getInputEditText() != null && (imm = (InputMethodManager) builder.getContext().getSystemService("input_method")) != null) {
            View currentFocus = dialog.getCurrentFocus();
            IBinder windowToken = currentFocus != null ? currentFocus.getWindowToken() : dialog.getView().getWindowToken();
            if (windowToken != null) {
                imm.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    public static ColorStateList getActionTextStateList(Context context, int newPrimaryColor) {
        int fallBackButtonColor = resolveColor(context, 16842806);
        if (newPrimaryColor == 0) {
            newPrimaryColor = fallBackButtonColor;
        }
        int[][] states = {new int[]{-16842910}, new int[0]};
        int[] colors = {adjustAlpha(newPrimaryColor, 0.4f), newPrimaryColor};
        return new ColorStateList(states, colors);
    }

    public static int[] getColorArray(@NonNull Context context, @ArrayRes int array) {
        if (array == 0) {
            return null;
        }
        TypedArray ta = context.getResources().obtainTypedArray(array);
        int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        return colors;
    }

    public static <T> boolean isIn(@NonNull T find, @Nullable T[] ary) {
        if (ary == null || ary.length == 0) {
            return false;
        }
        for (T item : ary) {
            if (item.equals(find)) {
                return true;
            }
        }
        return false;
    }
}