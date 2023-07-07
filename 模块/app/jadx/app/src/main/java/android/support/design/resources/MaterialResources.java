package android.support.design.resources;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleableRes;
import android.support.v7.content.res.AppCompatResources;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class MaterialResources {
    private MaterialResources() {
    }

    @Nullable
    public static ColorStateList getColorStateList(Context context, TypedArray attributes, @StyleableRes int index) {
        int resourceId;
        ColorStateList value;
        if (attributes.hasValue(index) && (resourceId = attributes.getResourceId(index, 0)) != 0 && (value = AppCompatResources.getColorStateList(context, resourceId)) != null) {
            return value;
        }
        return attributes.getColorStateList(index);
    }

    @Nullable
    public static Drawable getDrawable(Context context, TypedArray attributes, @StyleableRes int index) {
        int resourceId;
        Drawable value;
        if (attributes.hasValue(index) && (resourceId = attributes.getResourceId(index, 0)) != 0 && (value = AppCompatResources.getDrawable(context, resourceId)) != null) {
            return value;
        }
        return attributes.getDrawable(index);
    }

    @Nullable
    public static TextAppearance getTextAppearance(Context context, TypedArray attributes, @StyleableRes int index) {
        int resourceId;
        if (attributes.hasValue(index) && (resourceId = attributes.getResourceId(index, 0)) != 0) {
            return new TextAppearance(context, resourceId);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @StyleableRes
    public static int getIndexWithValue(TypedArray attributes, @StyleableRes int a, @StyleableRes int b) {
        if (attributes.hasValue(a)) {
            return a;
        }
        return b;
    }
}
