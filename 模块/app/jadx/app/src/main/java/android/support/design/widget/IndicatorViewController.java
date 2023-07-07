package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.R;
import android.support.design.animation.AnimationUtils;
import android.support.design.animation.AnimatorSetCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.Space;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class IndicatorViewController {
    private static final int CAPTION_OPACITY_FADE_ANIMATION_DURATION = 167;
    private static final int CAPTION_STATE_ERROR = 1;
    private static final int CAPTION_STATE_HELPER_TEXT = 2;
    private static final int CAPTION_STATE_NONE = 0;
    private static final int CAPTION_TRANSLATE_Y_ANIMATION_DURATION = 217;
    static final int COUNTER_INDEX = 2;
    static final int ERROR_INDEX = 0;
    static final int HELPER_INDEX = 1;
    @Nullable
    private Animator captionAnimator;
    private FrameLayout captionArea;
    private int captionDisplayed;
    private int captionToShow;
    private final float captionTranslationYPx;
    private int captionViewsAdded;
    private final Context context;
    private boolean errorEnabled;
    private CharSequence errorText;
    private int errorTextAppearance;
    private TextView errorView;
    private CharSequence helperText;
    private boolean helperTextEnabled;
    private int helperTextTextAppearance;
    private TextView helperTextView;
    private LinearLayout indicatorArea;
    private int indicatorsAdded;
    private final TextInputLayout textInputView;
    private Typeface typeface;

    public IndicatorViewController(TextInputLayout textInputView) {
        this.context = textInputView.getContext();
        this.textInputView = textInputView;
        this.captionTranslationYPx = this.context.getResources().getDimensionPixelSize(R.dimen.design_textinput_caption_translate_y);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showHelper(CharSequence helperText) {
        cancelCaptionAnimator();
        this.helperText = helperText;
        this.helperTextView.setText(helperText);
        if (this.captionDisplayed != 2) {
            this.captionToShow = 2;
        }
        updateCaptionViewsVisibility(this.captionDisplayed, this.captionToShow, shouldAnimateCaptionView(this.helperTextView, helperText));
    }

    void hideHelperText() {
        cancelCaptionAnimator();
        if (this.captionDisplayed == 2) {
            this.captionToShow = 0;
        }
        updateCaptionViewsVisibility(this.captionDisplayed, this.captionToShow, shouldAnimateCaptionView(this.helperTextView, null));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showError(CharSequence errorText) {
        cancelCaptionAnimator();
        this.errorText = errorText;
        this.errorView.setText(errorText);
        if (this.captionDisplayed != 1) {
            this.captionToShow = 1;
        }
        updateCaptionViewsVisibility(this.captionDisplayed, this.captionToShow, shouldAnimateCaptionView(this.errorView, errorText));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideError() {
        this.errorText = null;
        cancelCaptionAnimator();
        if (this.captionDisplayed == 1) {
            if (this.helperTextEnabled && !TextUtils.isEmpty(this.helperText)) {
                this.captionToShow = 2;
            } else {
                this.captionToShow = 0;
            }
        }
        updateCaptionViewsVisibility(this.captionDisplayed, this.captionToShow, shouldAnimateCaptionView(this.errorView, null));
    }

    private boolean shouldAnimateCaptionView(TextView captionView, @Nullable CharSequence captionText) {
        return ViewCompat.isLaidOut(this.textInputView) && this.textInputView.isEnabled() && (this.captionToShow != this.captionDisplayed || captionView == null || !TextUtils.equals(captionView.getText(), captionText));
    }

    private void updateCaptionViewsVisibility(final int captionToHide, final int captionToShow, boolean animate) {
        if (animate) {
            AnimatorSet captionAnimator = new AnimatorSet();
            this.captionAnimator = captionAnimator;
            List<Animator> captionAnimatorList = new ArrayList<>();
            createCaptionAnimators(captionAnimatorList, this.helperTextEnabled, this.helperTextView, 2, captionToHide, captionToShow);
            createCaptionAnimators(captionAnimatorList, this.errorEnabled, this.errorView, 1, captionToHide, captionToShow);
            AnimatorSetCompat.playTogether(captionAnimator, captionAnimatorList);
            final TextView captionViewToHide = getCaptionViewFromDisplayState(captionToHide);
            final TextView captionViewToShow = getCaptionViewFromDisplayState(captionToShow);
            captionAnimator.addListener(new AnimatorListenerAdapter() { // from class: android.support.design.widget.IndicatorViewController.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    IndicatorViewController.this.captionDisplayed = captionToShow;
                    IndicatorViewController.this.captionAnimator = null;
                    TextView textView = captionViewToHide;
                    if (textView != null) {
                        textView.setVisibility(4);
                        if (captionToHide == 1 && IndicatorViewController.this.errorView != null) {
                            IndicatorViewController.this.errorView.setText((CharSequence) null);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    TextView textView = captionViewToShow;
                    if (textView != null) {
                        textView.setVisibility(0);
                    }
                }
            });
            captionAnimator.start();
        } else {
            setCaptionViewVisibilities(captionToHide, captionToShow);
        }
        this.textInputView.updateEditTextBackground();
        this.textInputView.updateLabelState(animate);
        this.textInputView.updateTextInputBoxState();
    }

    private void setCaptionViewVisibilities(int captionToHide, int captionToShow) {
        TextView captionViewDisplayed;
        TextView captionViewToShow;
        if (captionToHide == captionToShow) {
            return;
        }
        if (captionToShow != 0 && (captionViewToShow = getCaptionViewFromDisplayState(captionToShow)) != null) {
            captionViewToShow.setVisibility(0);
            captionViewToShow.setAlpha(1.0f);
        }
        if (captionToHide != 0 && (captionViewDisplayed = getCaptionViewFromDisplayState(captionToHide)) != null) {
            captionViewDisplayed.setVisibility(4);
            if (captionToHide == 1) {
                captionViewDisplayed.setText((CharSequence) null);
            }
        }
        this.captionDisplayed = captionToShow;
    }

    private void createCaptionAnimators(List<Animator> captionAnimatorList, boolean captionEnabled, TextView captionView, int captionState, int captionToHide, int captionToShow) {
        if (captionView == null || !captionEnabled) {
            return;
        }
        if (captionState == captionToShow || captionState == captionToHide) {
            captionAnimatorList.add(createCaptionOpacityAnimator(captionView, captionToShow == captionState));
            if (captionToShow == captionState) {
                captionAnimatorList.add(createCaptionTranslationYAnimator(captionView));
            }
        }
    }

    private ObjectAnimator createCaptionOpacityAnimator(TextView captionView, boolean display) {
        float endValue = display ? 1.0f : 0.0f;
        ObjectAnimator opacityAnimator = ObjectAnimator.ofFloat(captionView, View.ALPHA, endValue);
        opacityAnimator.setDuration(167L);
        opacityAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        return opacityAnimator;
    }

    private ObjectAnimator createCaptionTranslationYAnimator(TextView captionView) {
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(captionView, View.TRANSLATION_Y, -this.captionTranslationYPx, 0.0f);
        translationYAnimator.setDuration(217L);
        translationYAnimator.setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
        return translationYAnimator;
    }

    void cancelCaptionAnimator() {
        Animator animator = this.captionAnimator;
        if (animator != null) {
            animator.cancel();
        }
    }

    boolean isCaptionView(int index) {
        return index == 0 || index == 1;
    }

    @Nullable
    private TextView getCaptionViewFromDisplayState(int captionDisplayState) {
        switch (captionDisplayState) {
            case 1:
                return this.errorView;
            case 2:
                return this.helperTextView;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void adjustIndicatorPadding() {
        if (canAdjustIndicatorPadding()) {
            ViewCompat.setPaddingRelative(this.indicatorArea, ViewCompat.getPaddingStart(this.textInputView.getEditText()), 0, ViewCompat.getPaddingEnd(this.textInputView.getEditText()), 0);
        }
    }

    private boolean canAdjustIndicatorPadding() {
        return (this.indicatorArea == null || this.textInputView.getEditText() == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addIndicator(TextView indicator, int index) {
        if (this.indicatorArea == null && this.captionArea == null) {
            this.indicatorArea = new LinearLayout(this.context);
            this.indicatorArea.setOrientation(0);
            this.textInputView.addView(this.indicatorArea, -1, -2);
            this.captionArea = new FrameLayout(this.context);
            this.indicatorArea.addView(this.captionArea, -1, new FrameLayout.LayoutParams(-2, -2));
            Space spacer = new Space(this.context);
            ViewGroup.LayoutParams spacerLp = new LinearLayout.LayoutParams(0, 0, 1.0f);
            this.indicatorArea.addView(spacer, spacerLp);
            if (this.textInputView.getEditText() != null) {
                adjustIndicatorPadding();
            }
        }
        if (isCaptionView(index)) {
            this.captionArea.setVisibility(0);
            this.captionArea.addView(indicator);
            this.captionViewsAdded++;
        } else {
            this.indicatorArea.addView(indicator, index);
        }
        this.indicatorArea.setVisibility(0);
        this.indicatorsAdded++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeIndicator(TextView indicator, int index) {
        FrameLayout frameLayout;
        if (this.indicatorArea == null) {
            return;
        }
        if (isCaptionView(index) && (frameLayout = this.captionArea) != null) {
            this.captionViewsAdded--;
            setViewGroupGoneIfEmpty(frameLayout, this.captionViewsAdded);
            this.captionArea.removeView(indicator);
        } else {
            this.indicatorArea.removeView(indicator);
        }
        this.indicatorsAdded--;
        setViewGroupGoneIfEmpty(this.indicatorArea, this.indicatorsAdded);
    }

    private void setViewGroupGoneIfEmpty(ViewGroup viewGroup, int indicatorsAdded) {
        if (indicatorsAdded == 0) {
            viewGroup.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setErrorEnabled(boolean enabled) {
        if (this.errorEnabled == enabled) {
            return;
        }
        cancelCaptionAnimator();
        if (enabled) {
            this.errorView = new AppCompatTextView(this.context);
            this.errorView.setId(R.id.textinput_error);
            Typeface typeface = this.typeface;
            if (typeface != null) {
                this.errorView.setTypeface(typeface);
            }
            setErrorTextAppearance(this.errorTextAppearance);
            this.errorView.setVisibility(4);
            ViewCompat.setAccessibilityLiveRegion(this.errorView, 1);
            addIndicator(this.errorView, 0);
        } else {
            hideError();
            removeIndicator(this.errorView, 0);
            this.errorView = null;
            this.textInputView.updateEditTextBackground();
            this.textInputView.updateTextInputBoxState();
        }
        this.errorEnabled = enabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isErrorEnabled() {
        return this.errorEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHelperTextEnabled() {
        return this.helperTextEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHelperTextEnabled(boolean enabled) {
        if (this.helperTextEnabled == enabled) {
            return;
        }
        cancelCaptionAnimator();
        if (enabled) {
            this.helperTextView = new AppCompatTextView(this.context);
            this.helperTextView.setId(R.id.textinput_helper_text);
            Typeface typeface = this.typeface;
            if (typeface != null) {
                this.helperTextView.setTypeface(typeface);
            }
            this.helperTextView.setVisibility(4);
            ViewCompat.setAccessibilityLiveRegion(this.helperTextView, 1);
            setHelperTextAppearance(this.helperTextTextAppearance);
            addIndicator(this.helperTextView, 1);
        } else {
            hideHelperText();
            removeIndicator(this.helperTextView, 1);
            this.helperTextView = null;
            this.textInputView.updateEditTextBackground();
            this.textInputView.updateTextInputBoxState();
        }
        this.helperTextEnabled = enabled;
    }

    boolean errorIsDisplayed() {
        return isCaptionStateError(this.captionDisplayed);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean errorShouldBeShown() {
        return isCaptionStateError(this.captionToShow);
    }

    private boolean isCaptionStateError(int captionState) {
        return captionState == 1 && this.errorView != null && !TextUtils.isEmpty(this.errorText);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean helperTextIsDisplayed() {
        return isCaptionStateHelperText(this.captionDisplayed);
    }

    boolean helperTextShouldBeShown() {
        return isCaptionStateHelperText(this.captionToShow);
    }

    private boolean isCaptionStateHelperText(int captionState) {
        return captionState == 2 && this.helperTextView != null && !TextUtils.isEmpty(this.helperText);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getErrorText() {
        return this.errorText;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getHelperText() {
        return this.helperText;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTypefaces(Typeface typeface) {
        if (typeface != this.typeface) {
            this.typeface = typeface;
            setTextViewTypeface(this.errorView, typeface);
            setTextViewTypeface(this.helperTextView, typeface);
        }
    }

    private void setTextViewTypeface(@Nullable TextView captionView, Typeface typeface) {
        if (captionView != null) {
            captionView.setTypeface(typeface);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @ColorInt
    public int getErrorViewCurrentTextColor() {
        TextView textView = this.errorView;
        if (textView != null) {
            return textView.getCurrentTextColor();
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public ColorStateList getErrorViewTextColors() {
        TextView textView = this.errorView;
        if (textView != null) {
            return textView.getTextColors();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setErrorViewTextColor(@Nullable ColorStateList textColors) {
        TextView textView = this.errorView;
        if (textView != null) {
            textView.setTextColor(textColors);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setErrorTextAppearance(@StyleRes int resId) {
        this.errorTextAppearance = resId;
        TextView textView = this.errorView;
        if (textView != null) {
            this.textInputView.setTextAppearanceCompatWithErrorFallback(textView, resId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @ColorInt
    public int getHelperTextViewCurrentTextColor() {
        TextView textView = this.helperTextView;
        if (textView != null) {
            return textView.getCurrentTextColor();
        }
        return -1;
    }

    @Nullable
    ColorStateList getHelperTextViewColors() {
        TextView textView = this.helperTextView;
        if (textView != null) {
            return textView.getTextColors();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHelperTextViewTextColor(@Nullable ColorStateList textColors) {
        TextView textView = this.helperTextView;
        if (textView != null) {
            textView.setTextColor(textColors);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHelperTextAppearance(@StyleRes int resId) {
        this.helperTextTextAppearance = resId;
        TextView textView = this.helperTextView;
        if (textView != null) {
            TextViewCompat.setTextAppearance(textView, resId);
        }
    }
}
