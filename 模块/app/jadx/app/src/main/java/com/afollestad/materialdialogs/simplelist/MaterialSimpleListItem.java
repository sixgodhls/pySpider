package com.afollestad.materialdialogs.simplelist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import com.afollestad.materialdialogs.util.DialogUtils;

/* loaded from: classes.dex */
public class MaterialSimpleListItem {
    private final Builder builder;

    private MaterialSimpleListItem(Builder builder) {
        this.builder = builder;
    }

    public Drawable getIcon() {
        return this.builder.icon;
    }

    public CharSequence getContent() {
        return this.builder.content;
    }

    public int getIconPadding() {
        return this.builder.iconPadding;
    }

    @ColorInt
    public int getBackgroundColor() {
        return this.builder.backgroundColor;
    }

    public long getId() {
        return this.builder.id;
    }

    @Nullable
    public Object getTag() {
        return this.builder.tag;
    }

    public String toString() {
        if (getContent() != null) {
            return getContent().toString();
        }
        return "(no content)";
    }

    /* loaded from: classes.dex */
    public static class Builder {
        int backgroundColor = Color.parseColor("#BCBCBC");
        protected CharSequence content;
        private final Context context;
        protected Drawable icon;
        int iconPadding;
        protected long id;
        Object tag;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder icon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(@DrawableRes int iconRes) {
            return icon(ContextCompat.getDrawable(this.context, iconRes));
        }

        public Builder iconPadding(@IntRange(from = 0, to = 2147483647L) int padding) {
            this.iconPadding = padding;
            return this;
        }

        public Builder iconPaddingDp(@IntRange(from = 0, to = 2147483647L) int paddingDp) {
            this.iconPadding = (int) TypedValue.applyDimension(1, paddingDp, this.context.getResources().getDisplayMetrics());
            return this;
        }

        public Builder iconPaddingRes(@DimenRes int paddingRes) {
            return iconPadding(this.context.getResources().getDimensionPixelSize(paddingRes));
        }

        public Builder content(CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            return content(this.context.getString(contentRes));
        }

        public Builder backgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder backgroundColorRes(@ColorRes int colorRes) {
            return backgroundColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder backgroundColorAttr(@AttrRes int colorAttr) {
            return backgroundColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder tag(@Nullable Object tag) {
            this.tag = tag;
            return this;
        }

        public MaterialSimpleListItem build() {
            return new MaterialSimpleListItem(this);
        }
    }
}
