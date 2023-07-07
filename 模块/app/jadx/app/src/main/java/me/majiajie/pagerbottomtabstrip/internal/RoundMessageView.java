package me.majiajie.pagerbottomtabstrip.internal;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.Locale;
import me.majiajie.pagerbottomtabstrip.R;

/* loaded from: classes.dex */
public class RoundMessageView extends FrameLayout {
    private boolean mHasMessage;
    private int mMessageNumber;
    private final TextView mMessages;
    private final View mOval;

    public RoundMessageView(Context context) {
        this(context, null);
    }

    public RoundMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.round_message_view, (ViewGroup) this, true);
        this.mOval = findViewById(R.id.oval);
        this.mMessages = (TextView) findViewById(R.id.msg);
        this.mMessages.setTypeface(Typeface.DEFAULT_BOLD);
        this.mMessages.setTextSize(1, 10.0f);
    }

    public void setMessageNumber(int number) {
        this.mMessageNumber = number;
        if (this.mMessageNumber > 0) {
            this.mOval.setVisibility(4);
            this.mMessages.setVisibility(0);
            if (this.mMessageNumber < 10) {
                this.mMessages.setTextSize(1, 12.0f);
            } else {
                this.mMessages.setTextSize(1, 10.0f);
            }
            if (this.mMessageNumber <= 99) {
                this.mMessages.setText(String.format(Locale.ENGLISH, "%d", Integer.valueOf(this.mMessageNumber)));
                return;
            } else {
                this.mMessages.setText(String.format(Locale.ENGLISH, "%d+", 99));
                return;
            }
        }
        this.mMessages.setVisibility(4);
        if (this.mHasMessage) {
            this.mOval.setVisibility(0);
        }
    }

    public void setHasMessage(boolean hasMessage) {
        this.mHasMessage = hasMessage;
        int i = 4;
        if (!hasMessage) {
            this.mOval.setVisibility(4);
            return;
        }
        View view = this.mOval;
        if (this.mMessageNumber <= 0) {
            i = 0;
        }
        view.setVisibility(i);
    }

    public void tintMessageBackground(@ColorInt int color) {
        Drawable drawable = Utils.tint(ContextCompat.getDrawable(getContext(), R.drawable.round), color);
        ViewCompat.setBackground(this.mOval, drawable);
        ViewCompat.setBackground(this.mMessages, drawable);
    }

    public void setMessageNumberColor(@ColorInt int color) {
        this.mMessages.setTextColor(color);
    }

    public int getMessageNumber() {
        return this.mMessageNumber;
    }

    public boolean hasMessage() {
        return this.mHasMessage;
    }
}
