package me.majiajie.pagerbottomtabstrip.item;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import me.majiajie.pagerbottomtabstrip.C1028R;
import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView;
import me.majiajie.pagerbottomtabstrip.internal.Utils;

/* loaded from: classes.dex */
public class MaterialItemView extends BaseTabItem {
    private ValueAnimator mAnimator;
    private float mAnimatorValue;
    private boolean mChecked;
    private int mCheckedColor;
    private Drawable mCheckedDrawable;
    private int mDefaultColor;
    private Drawable mDefaultDrawable;
    private boolean mHideTitle;
    private final ImageView mIcon;
    private boolean mIsMeasured;
    private final TextView mLabel;
    private final RoundMessageView mMessages;
    private final int mTopMargin;
    private final int mTopMarginHideTitle;
    private final float mTranslation;
    private final float mTranslationHideTitle;

    public MaterialItemView(@NonNull Context context) {
        this(context, null);
    }

    public MaterialItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialItemView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAnimatorValue = 1.0f;
        this.mIsMeasured = false;
        float scale = context.getResources().getDisplayMetrics().density;
        this.mTranslation = 2.0f * scale;
        this.mTranslationHideTitle = 10.0f * scale;
        this.mTopMargin = (int) (8.0f * scale);
        this.mTopMarginHideTitle = (int) (16.0f * scale);
        LayoutInflater.from(context).inflate(C1028R.layout.item_material, (ViewGroup) this, true);
        this.mIcon = (ImageView) findViewById(C1028R.C1030id.icon);
        this.mLabel = (TextView) findViewById(C1028R.C1030id.label);
        this.mMessages = (RoundMessageView) findViewById(C1028R.C1030id.messages);
    }

    public void initialization(String title, Drawable drawable, Drawable checkedDrawable, int color, int checkedColor) {
        this.mDefaultColor = color;
        this.mCheckedColor = checkedColor;
        this.mDefaultDrawable = Utils.tint(drawable, this.mDefaultColor);
        this.mCheckedDrawable = Utils.tint(checkedDrawable, this.mCheckedColor);
        this.mLabel.setText(title);
        this.mLabel.setTextColor(color);
        this.mIcon.setImageDrawable(this.mDefaultDrawable);
        this.mAnimator = ValueAnimator.ofFloat(1.0f);
        this.mAnimator.setDuration(115L);
        this.mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: me.majiajie.pagerbottomtabstrip.item.MaterialItemView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                MaterialItemView.this.mAnimatorValue = ((Float) animation.getAnimatedValue()).floatValue();
                if (MaterialItemView.this.mHideTitle) {
                    MaterialItemView.this.mIcon.setTranslationY((-MaterialItemView.this.mTranslationHideTitle) * MaterialItemView.this.mAnimatorValue);
                } else {
                    MaterialItemView.this.mIcon.setTranslationY((-MaterialItemView.this.mTranslation) * MaterialItemView.this.mAnimatorValue);
                }
                MaterialItemView.this.mLabel.setTextSize(2, (MaterialItemView.this.mAnimatorValue * 2.0f) + 12.0f);
            }
        });
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mIsMeasured = true;
    }

    @Override // me.majiajie.pagerbottomtabstrip.item.BaseTabItem
    public void setChecked(boolean checked) {
        if (this.mChecked == checked) {
            return;
        }
        this.mChecked = checked;
        if (this.mHideTitle) {
            this.mLabel.setVisibility(this.mChecked ? 0 : 4);
        }
        if (this.mIsMeasured) {
            if (this.mChecked) {
                this.mAnimator.start();
            } else {
                this.mAnimator.reverse();
            }
        } else if (this.mChecked) {
            if (this.mHideTitle) {
                this.mIcon.setTranslationY(-this.mTranslationHideTitle);
            } else {
                this.mIcon.setTranslationY(-this.mTranslation);
            }
            this.mLabel.setTextSize(2, 14.0f);
        } else {
            this.mIcon.setTranslationY(0.0f);
            this.mLabel.setTextSize(2, 12.0f);
        }
        if (this.mChecked) {
            this.mIcon.setImageDrawable(this.mCheckedDrawable);
            this.mLabel.setTextColor(this.mCheckedColor);
            return;
        }
        this.mIcon.setImageDrawable(this.mDefaultDrawable);
        this.mLabel.setTextColor(this.mDefaultColor);
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
        return this.mLabel.getText().toString();
    }

    public float getAnimValue() {
        return this.mAnimatorValue;
    }

    public void setHideTitle(boolean hideTitle) {
        this.mHideTitle = hideTitle;
        FrameLayout.LayoutParams iconParams = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
        if (this.mHideTitle) {
            iconParams.topMargin = this.mTopMarginHideTitle;
        } else {
            iconParams.topMargin = this.mTopMargin;
        }
        this.mLabel.setVisibility(this.mChecked ? 0 : 4);
        this.mIcon.setLayoutParams(iconParams);
    }

    public void setMessageBackgroundColor(@ColorInt int color) {
        this.mMessages.tintMessageBackground(color);
    }

    public void setMessageNumberColor(@ColorInt int color) {
        this.mMessages.setMessageNumberColor(color);
    }
}