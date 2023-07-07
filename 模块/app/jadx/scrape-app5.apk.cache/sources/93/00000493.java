package me.majiajie.pagerbottomtabstrip.item;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import me.majiajie.pagerbottomtabstrip.C1028R;
import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView;

/* loaded from: classes.dex */
public class NormalItemView extends BaseTabItem {
    private int mCheckedDrawable;
    private int mCheckedTextColor;
    private int mDefaultDrawable;
    private int mDefaultTextColor;
    private ImageView mIcon;
    private final RoundMessageView mMessages;
    private final TextView mTitle;

    public NormalItemView(Context context) {
        this(context, null);
    }

    public NormalItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NormalItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDefaultTextColor = 1442840576;
        this.mCheckedTextColor = 1442840576;
        LayoutInflater.from(context).inflate(C1028R.layout.item_normal, (ViewGroup) this, true);
        this.mIcon = (ImageView) findViewById(C1028R.C1030id.icon);
        this.mTitle = (TextView) findViewById(C1028R.C1030id.title);
        this.mMessages = (RoundMessageView) findViewById(C1028R.C1030id.messages);
    }

    public void initialize(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, String title) {
        this.mDefaultDrawable = drawableRes;
        this.mCheckedDrawable = checkedDrawableRes;
        this.mTitle.setText(title);
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public void setChecked(boolean checked) {
        if (checked) {
            this.mIcon.setImageResource(this.mCheckedDrawable);
            this.mTitle.setTextColor(this.mCheckedTextColor);
            return;
        }
        this.mIcon.setImageResource(this.mDefaultDrawable);
        this.mTitle.setTextColor(this.mDefaultTextColor);
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public void setMessageNumber(int number) {
        this.mMessages.setMessageNumber(number);
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public void setHasMessage(boolean hasMessage) {
        this.mMessages.setHasMessage(hasMessage);
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public String getTitle() {
        return this.mTitle.getText().toString();
    }

    public void setTextDefaultColor(@ColorInt int color) {
        this.mDefaultTextColor = color;
    }

    public void setTextCheckedColor(@ColorInt int color) {
        this.mCheckedTextColor = color;
    }
}