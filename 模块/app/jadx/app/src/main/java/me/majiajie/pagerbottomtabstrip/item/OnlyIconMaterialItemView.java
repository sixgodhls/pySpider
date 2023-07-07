package me.majiajie.pagerbottomtabstrip.item;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import me.majiajie.pagerbottomtabstrip.R;
import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView;
import me.majiajie.pagerbottomtabstrip.internal.Utils;

/* loaded from: classes.dex */
public class OnlyIconMaterialItemView extends BaseTabItem {
    private boolean mChecked;
    private int mCheckedColor;
    private Drawable mCheckedDrawable;
    private int mDefaultColor;
    private Drawable mDefaultDrawable;
    private final ImageView mIcon;
    private final RoundMessageView mMessages;
    private String mTitle;

    public OnlyIconMaterialItemView(@NonNull Context context) {
        this(context, null);
    }

    public OnlyIconMaterialItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OnlyIconMaterialItemView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_material_only_icon, (ViewGroup) this, true);
        this.mIcon = (ImageView) findViewById(R.id.icon);
        this.mMessages = (RoundMessageView) findViewById(R.id.messages);
    }

    public void initialization(String title, Drawable drawable, Drawable checkedDrawable, int color, int checkedColor) {
        this.mTitle = title;
        this.mDefaultColor = color;
        this.mCheckedColor = checkedColor;
        this.mDefaultDrawable = Utils.tint(drawable, this.mDefaultColor);
        this.mCheckedDrawable = Utils.tint(checkedDrawable, this.mCheckedColor);
        this.mIcon.setImageDrawable(this.mDefaultDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            setBackground(new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{(16777215 & checkedColor) | 1442840576}), null, null));
        } else {
            setBackgroundResource(R.drawable.material_item_background);
        }
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public void setChecked(boolean checked) {
        if (this.mChecked == checked) {
            return;
        }
        this.mChecked = checked;
        if (this.mChecked) {
            this.mIcon.setImageDrawable(this.mCheckedDrawable);
        } else {
            this.mIcon.setImageDrawable(this.mDefaultDrawable);
        }
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public void setMessageNumber(int number) {
        this.mMessages.setVisibility(0);
        this.mMessages.setMessageNumber(number);
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public void setHasMessage(boolean hasMessage) {
        this.mMessages.setVisibility(0);
        this.mMessages.setHasMessage(hasMessage);
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public String getTitle() {
        return this.mTitle;
    }

    public void setMessageBackgroundColor(@ColorInt int color) {
        this.mMessages.tintMessageBackground(color);
    }

    public void setMessageNumberColor(@ColorInt int color) {
        this.mMessages.setMessageNumberColor(color);
    }
}
