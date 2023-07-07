package me.zhanghai.android.materialprogressbar;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* loaded from: classes.dex */
public interface TintableDrawable {
    void setTint(@ColorInt int i);

    void setTintList(@Nullable ColorStateList colorStateList);

    void setTintMode(@NonNull PorterDuff.Mode mode);
}