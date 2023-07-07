package com.afollestad.materialdialogs.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.support.p003v7.widget.AppCompatEditText;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.C0582R;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.lang.reflect.Field;

@SuppressLint({"PrivateResource"})
/* loaded from: classes.dex */
public class MDTintHelper {
    public static void setTint(@NonNull RadioButton radioButton, @NonNull ColorStateList colors) {
        if (Build.VERSION.SDK_INT >= 21) {
            radioButton.setButtonTintList(colors);
            return;
        }
        Drawable radioDrawable = ContextCompat.getDrawable(radioButton.getContext(), C0582R.C0584drawable.abc_btn_radio_material);
        Drawable d = DrawableCompat.wrap(radioDrawable);
        DrawableCompat.setTintList(d, colors);
        radioButton.setButtonDrawable(d);
    }

    public static void setTint(@NonNull RadioButton radioButton, @ColorInt int color) {
        int disabledColor = DialogUtils.getDisabledColor(radioButton.getContext());
        ColorStateList sl = new ColorStateList(new int[][]{new int[]{16842910, -16842912}, new int[]{16842910, 16842912}, new int[]{-16842910, -16842912}, new int[]{-16842910, 16842912}}, new int[]{DialogUtils.resolveColor(radioButton.getContext(), C0582R.attr.colorControlNormal), color, disabledColor, disabledColor});
        setTint(radioButton, sl);
    }

    public static void setTint(@NonNull CheckBox box, @NonNull ColorStateList colors) {
        if (Build.VERSION.SDK_INT >= 21) {
            box.setButtonTintList(colors);
            return;
        }
        Drawable checkDrawable = ContextCompat.getDrawable(box.getContext(), C0582R.C0584drawable.abc_btn_check_material);
        Drawable drawable = DrawableCompat.wrap(checkDrawable);
        DrawableCompat.setTintList(drawable, colors);
        box.setButtonDrawable(drawable);
    }

    public static void setTint(@NonNull CheckBox box, @ColorInt int color) {
        int disabledColor = DialogUtils.getDisabledColor(box.getContext());
        ColorStateList sl = new ColorStateList(new int[][]{new int[]{16842910, -16842912}, new int[]{16842910, 16842912}, new int[]{-16842910, -16842912}, new int[]{-16842910, 16842912}}, new int[]{DialogUtils.resolveColor(box.getContext(), C0582R.attr.colorControlNormal), color, disabledColor, disabledColor});
        setTint(box, sl);
    }

    public static void setTint(@NonNull SeekBar seekBar, @ColorInt int color) {
        ColorStateList s1 = ColorStateList.valueOf(color);
        if (Build.VERSION.SDK_INT >= 21) {
            seekBar.setThumbTintList(s1);
            seekBar.setProgressTintList(s1);
        } else if (Build.VERSION.SDK_INT > 10) {
            Drawable progressDrawable = DrawableCompat.wrap(seekBar.getProgressDrawable());
            seekBar.setProgressDrawable(progressDrawable);
            DrawableCompat.setTintList(progressDrawable, s1);
            if (Build.VERSION.SDK_INT >= 16) {
                Drawable thumbDrawable = DrawableCompat.wrap(seekBar.getThumb());
                DrawableCompat.setTintList(thumbDrawable, s1);
                seekBar.setThumb(thumbDrawable);
            }
        } else {
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
            if (Build.VERSION.SDK_INT <= 10) {
                mode = PorterDuff.Mode.MULTIPLY;
            }
            if (seekBar.getIndeterminateDrawable() != null) {
                seekBar.getIndeterminateDrawable().setColorFilter(color, mode);
            }
            if (seekBar.getProgressDrawable() != null) {
                seekBar.getProgressDrawable().setColorFilter(color, mode);
            }
        }
    }

    public static void setTint(@NonNull ProgressBar progressBar, @ColorInt int color) {
        setTint(progressBar, color, false);
    }

    private static void setTint(@NonNull ProgressBar progressBar, @ColorInt int color, boolean skipIndeterminate) {
        ColorStateList sl = ColorStateList.valueOf(color);
        if (Build.VERSION.SDK_INT >= 21) {
            progressBar.setProgressTintList(sl);
            progressBar.setSecondaryProgressTintList(sl);
            if (!skipIndeterminate) {
                progressBar.setIndeterminateTintList(sl);
                return;
            }
            return;
        }
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        if (Build.VERSION.SDK_INT <= 10) {
            mode = PorterDuff.Mode.MULTIPLY;
        }
        if (!skipIndeterminate && progressBar.getIndeterminateDrawable() != null) {
            progressBar.getIndeterminateDrawable().setColorFilter(color, mode);
        }
        if (progressBar.getProgressDrawable() != null) {
            progressBar.getProgressDrawable().setColorFilter(color, mode);
        }
    }

    private static ColorStateList createEditTextColorStateList(@NonNull Context context, @ColorInt int color) {
        int[][] states = new int[3];
        int[] colors = new int[3];
        int[] iArr = new int[1];
        iArr[0] = -16842910;
        states[0] = iArr;
        colors[0] = DialogUtils.resolveColor(context, C0582R.attr.colorControlNormal);
        int i = 0 + 1;
        states[i] = new int[]{-16842919, -16842908};
        colors[i] = DialogUtils.resolveColor(context, C0582R.attr.colorControlNormal);
        int i2 = i + 1;
        states[i2] = new int[0];
        colors[i2] = color;
        return new ColorStateList(states, colors);
    }

    public static void setTint(@NonNull EditText editText, @ColorInt int color) {
        ColorStateList editTextColorStateList = createEditTextColorStateList(editText.getContext(), color);
        if (editText instanceof AppCompatEditText) {
            ((AppCompatEditText) editText).setSupportBackgroundTintList(editTextColorStateList);
        } else if (Build.VERSION.SDK_INT >= 21) {
            editText.setBackgroundTintList(editTextColorStateList);
        }
        setCursorTint(editText, color);
    }

    private static void setCursorTint(@NonNull EditText editText, @ColorInt int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = {ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes), ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes)};
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (NoSuchFieldException e1) {
            Log.d("MDTintHelper", "Device issue with cursor tinting: " + e1.getMessage());
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}