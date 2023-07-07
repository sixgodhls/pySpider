package me.majiajie.pagerbottomtabstrip.item;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/* loaded from: classes.dex */
public abstract class BaseTabItem extends FrameLayout {
    public abstract String getTitle();

    public abstract void setChecked(boolean z);

    public abstract void setHasMessage(boolean z);

    public abstract void setMessageNumber(int i);

    public BaseTabItem(@NonNull Context context) {
        super(context);
    }

    public BaseTabItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseTabItem(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onRepeat() {
    }
}
