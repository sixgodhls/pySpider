package com.afollestad.materialdialogs;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDAdapter;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDRootLayout;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import me.zhanghai.android.materialprogressbar.HorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateCircularProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DialogInit {
    DialogInit() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @StyleRes
    public static int getTheme(@NonNull MaterialDialog.Builder builder) {
        boolean darkTheme = DialogUtils.resolveBoolean(builder.context, R.attr.md_dark_theme, builder.theme == Theme.DARK);
        builder.theme = darkTheme ? Theme.DARK : Theme.LIGHT;
        return darkTheme ? R.style.MD_Dark : R.style.MD_Light;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @LayoutRes
    public static int getInflateLayout(MaterialDialog.Builder builder) {
        if (builder.customView != null) {
            return R.layout.md_dialog_custom;
        }
        if (builder.items != null || builder.adapter != null) {
            if (builder.checkBoxPrompt != null) {
                return R.layout.md_dialog_list_check;
            }
            return R.layout.md_dialog_list;
        } else if (builder.progress > -2) {
            return R.layout.md_dialog_progress;
        } else {
            if (builder.indeterminateProgress) {
                if (builder.indeterminateIsHorizontalProgress) {
                    return R.layout.md_dialog_progress_indeterminate_horizontal;
                }
                return R.layout.md_dialog_progress_indeterminate;
            } else if (builder.inputCallback != null) {
                if (builder.checkBoxPrompt != null) {
                    return R.layout.md_dialog_input_check;
                }
                return R.layout.md_dialog_input;
            } else if (builder.checkBoxPrompt != null) {
                return R.layout.md_dialog_basic_check;
            } else {
                return R.layout.md_dialog_basic;
            }
        }
    }

    @UiThread
    public static void init(MaterialDialog dialog) {
        boolean textAllCaps;
        int maxIconSize;
        int i;
        MaterialDialog.Builder builder = dialog.builder;
        dialog.setCancelable(builder.cancelable);
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        if (builder.backgroundColor == 0) {
            builder.backgroundColor = DialogUtils.resolveColor(builder.context, R.attr.md_background_color, DialogUtils.resolveColor(dialog.getContext(), R.attr.colorBackgroundFloating));
        }
        if (builder.backgroundColor != 0) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(builder.context.getResources().getDimension(R.dimen.md_bg_corner_radius));
            drawable.setColor(builder.backgroundColor);
            dialog.getWindow().setBackgroundDrawable(drawable);
        }
        if (!builder.positiveColorSet) {
            builder.positiveColor = DialogUtils.resolveActionTextColorStateList(builder.context, R.attr.md_positive_color, builder.positiveColor);
        }
        if (!builder.neutralColorSet) {
            builder.neutralColor = DialogUtils.resolveActionTextColorStateList(builder.context, R.attr.md_neutral_color, builder.neutralColor);
        }
        if (!builder.negativeColorSet) {
            builder.negativeColor = DialogUtils.resolveActionTextColorStateList(builder.context, R.attr.md_negative_color, builder.negativeColor);
        }
        if (!builder.widgetColorSet) {
            builder.widgetColor = DialogUtils.resolveColor(builder.context, R.attr.md_widget_color, builder.widgetColor);
        }
        if (!builder.titleColorSet) {
            int titleColorFallback = DialogUtils.resolveColor(dialog.getContext(), 16842806);
            builder.titleColor = DialogUtils.resolveColor(builder.context, R.attr.md_title_color, titleColorFallback);
        }
        if (!builder.contentColorSet) {
            int contentColorFallback = DialogUtils.resolveColor(dialog.getContext(), 16842808);
            builder.contentColor = DialogUtils.resolveColor(builder.context, R.attr.md_content_color, contentColorFallback);
        }
        if (!builder.itemColorSet) {
            builder.itemColor = DialogUtils.resolveColor(builder.context, R.attr.md_item_color, builder.contentColor);
        }
        dialog.title = (TextView) dialog.view.findViewById(R.id.md_title);
        dialog.icon = (ImageView) dialog.view.findViewById(R.id.md_icon);
        dialog.titleFrame = dialog.view.findViewById(R.id.md_titleFrame);
        dialog.content = (TextView) dialog.view.findViewById(R.id.md_content);
        dialog.recyclerView = (RecyclerView) dialog.view.findViewById(R.id.md_contentRecyclerView);
        dialog.checkBoxPrompt = (CheckBox) dialog.view.findViewById(R.id.md_promptCheckbox);
        dialog.positiveButton = (MDButton) dialog.view.findViewById(R.id.md_buttonDefaultPositive);
        dialog.neutralButton = (MDButton) dialog.view.findViewById(R.id.md_buttonDefaultNeutral);
        dialog.negativeButton = (MDButton) dialog.view.findViewById(R.id.md_buttonDefaultNegative);
        if (builder.inputCallback != null && builder.positiveText == null) {
            builder.positiveText = builder.context.getText(17039370);
        }
        dialog.positiveButton.setVisibility(builder.positiveText != null ? 0 : 8);
        dialog.neutralButton.setVisibility(builder.neutralText != null ? 0 : 8);
        dialog.negativeButton.setVisibility(builder.negativeText != null ? 0 : 8);
        dialog.positiveButton.setFocusable(true);
        dialog.neutralButton.setFocusable(true);
        dialog.negativeButton.setFocusable(true);
        if (builder.positiveFocus) {
            dialog.positiveButton.requestFocus();
        }
        if (builder.neutralFocus) {
            dialog.neutralButton.requestFocus();
        }
        if (builder.negativeFocus) {
            dialog.negativeButton.requestFocus();
        }
        if (builder.icon != null) {
            dialog.icon.setVisibility(0);
            dialog.icon.setImageDrawable(builder.icon);
        } else {
            Drawable d = DialogUtils.resolveDrawable(builder.context, R.attr.md_icon);
            if (d != null) {
                dialog.icon.setVisibility(0);
                dialog.icon.setImageDrawable(d);
            } else {
                dialog.icon.setVisibility(8);
            }
        }
        int maxIconSize2 = builder.maxIconSize;
        if (maxIconSize2 == -1) {
            maxIconSize2 = DialogUtils.resolveDimension(builder.context, R.attr.md_icon_max_size);
        }
        if (builder.limitIconToDefaultSize || DialogUtils.resolveBoolean(builder.context, R.attr.md_icon_limit_icon_to_default_size)) {
            maxIconSize2 = builder.context.getResources().getDimensionPixelSize(R.dimen.md_icon_max_size);
        }
        if (maxIconSize2 > -1) {
            dialog.icon.setAdjustViewBounds(true);
            dialog.icon.setMaxHeight(maxIconSize2);
            dialog.icon.setMaxWidth(maxIconSize2);
            dialog.icon.requestLayout();
        }
        if (!builder.dividerColorSet) {
            int dividerFallback = DialogUtils.resolveColor(dialog.getContext(), R.attr.md_divider);
            builder.dividerColor = DialogUtils.resolveColor(builder.context, R.attr.md_divider_color, dividerFallback);
        }
        dialog.view.setDividerColor(builder.dividerColor);
        if (dialog.title != null) {
            dialog.setTypeface(dialog.title, builder.mediumFont);
            dialog.title.setTextColor(builder.titleColor);
            dialog.title.setGravity(builder.titleGravity.getGravityInt());
            if (Build.VERSION.SDK_INT >= 17) {
                dialog.title.setTextAlignment(builder.titleGravity.getTextAlignment());
            }
            if (builder.title == null) {
                dialog.titleFrame.setVisibility(8);
            } else {
                dialog.title.setText(builder.title);
                dialog.titleFrame.setVisibility(0);
            }
        }
        if (dialog.content != null) {
            dialog.content.setMovementMethod(new LinkMovementMethod());
            dialog.setTypeface(dialog.content, builder.regularFont);
            dialog.content.setLineSpacing(0.0f, builder.contentLineSpacingMultiplier);
            if (builder.linkColor == null) {
                dialog.content.setLinkTextColor(DialogUtils.resolveColor(dialog.getContext(), 16842806));
            } else {
                dialog.content.setLinkTextColor(builder.linkColor);
            }
            dialog.content.setTextColor(builder.contentColor);
            dialog.content.setGravity(builder.contentGravity.getGravityInt());
            if (Build.VERSION.SDK_INT >= 17) {
                dialog.content.setTextAlignment(builder.contentGravity.getTextAlignment());
            }
            if (builder.content != null) {
                dialog.content.setText(builder.content);
                dialog.content.setVisibility(0);
            } else {
                dialog.content.setVisibility(8);
            }
        }
        if (dialog.checkBoxPrompt != null) {
            dialog.checkBoxPrompt.setText(builder.checkBoxPrompt);
            dialog.checkBoxPrompt.setChecked(builder.checkBoxPromptInitiallyChecked);
            dialog.checkBoxPrompt.setOnCheckedChangeListener(builder.checkBoxPromptListener);
            dialog.setTypeface(dialog.checkBoxPrompt, builder.regularFont);
            dialog.checkBoxPrompt.setTextColor(builder.contentColor);
            MDTintHelper.setTint(dialog.checkBoxPrompt, builder.widgetColor);
        }
        dialog.view.setButtonGravity(builder.buttonsGravity);
        dialog.view.setButtonStackedGravity(builder.btnStackedGravity);
        dialog.view.setStackingBehavior(builder.stackingBehavior);
        if (Build.VERSION.SDK_INT >= 14) {
            textAllCaps = DialogUtils.resolveBoolean(builder.context, 16843660, true);
            if (textAllCaps) {
                textAllCaps = DialogUtils.resolveBoolean(builder.context, R.attr.textAllCaps, true);
            }
        } else {
            textAllCaps = DialogUtils.resolveBoolean(builder.context, R.attr.textAllCaps, true);
        }
        MDButton positiveTextView = dialog.positiveButton;
        dialog.setTypeface(positiveTextView, builder.mediumFont);
        positiveTextView.setAllCapsCompat(textAllCaps);
        positiveTextView.setText(builder.positiveText);
        positiveTextView.setTextColor(builder.positiveColor);
        dialog.positiveButton.setStackedSelector(dialog.getButtonSelector(DialogAction.POSITIVE, true));
        dialog.positiveButton.setDefaultSelector(dialog.getButtonSelector(DialogAction.POSITIVE, false));
        dialog.positiveButton.setTag(DialogAction.POSITIVE);
        dialog.positiveButton.setOnClickListener(dialog);
        dialog.positiveButton.setVisibility(0);
        MDButton negativeTextView = dialog.negativeButton;
        dialog.setTypeface(negativeTextView, builder.mediumFont);
        negativeTextView.setAllCapsCompat(textAllCaps);
        negativeTextView.setText(builder.negativeText);
        negativeTextView.setTextColor(builder.negativeColor);
        dialog.negativeButton.setStackedSelector(dialog.getButtonSelector(DialogAction.NEGATIVE, true));
        dialog.negativeButton.setDefaultSelector(dialog.getButtonSelector(DialogAction.NEGATIVE, false));
        dialog.negativeButton.setTag(DialogAction.NEGATIVE);
        dialog.negativeButton.setOnClickListener(dialog);
        dialog.negativeButton.setVisibility(0);
        MDButton neutralTextView = dialog.neutralButton;
        dialog.setTypeface(neutralTextView, builder.mediumFont);
        neutralTextView.setAllCapsCompat(textAllCaps);
        neutralTextView.setText(builder.neutralText);
        neutralTextView.setTextColor(builder.neutralColor);
        dialog.neutralButton.setStackedSelector(dialog.getButtonSelector(DialogAction.NEUTRAL, true));
        dialog.neutralButton.setDefaultSelector(dialog.getButtonSelector(DialogAction.NEUTRAL, false));
        dialog.neutralButton.setTag(DialogAction.NEUTRAL);
        dialog.neutralButton.setOnClickListener(dialog);
        dialog.neutralButton.setVisibility(0);
        if (builder.listCallbackMultiChoice != null) {
            dialog.selectedIndicesList = new ArrayList();
        }
        if (dialog.recyclerView != null) {
            if (builder.adapter == null) {
                if (builder.listCallbackSingleChoice != null) {
                    dialog.listType = MaterialDialog.ListType.SINGLE;
                } else if (builder.listCallbackMultiChoice != null) {
                    dialog.listType = MaterialDialog.ListType.MULTI;
                    if (builder.selectedIndices != null) {
                        dialog.selectedIndicesList = new ArrayList(Arrays.asList(builder.selectedIndices));
                        builder.selectedIndices = null;
                    }
                } else {
                    dialog.listType = MaterialDialog.ListType.REGULAR;
                }
                builder.adapter = new DefaultRvAdapter(dialog, MaterialDialog.ListType.getLayoutForType(dialog.listType));
            } else if (builder.adapter instanceof MDAdapter) {
                ((MDAdapter) builder.adapter).setDialog(dialog);
            }
        }
        setupProgressDialog(dialog);
        setupInputDialog(dialog);
        if (builder.customView != null) {
            ((MDRootLayout) dialog.view.findViewById(R.id.md_root)).noTitleNoPadding();
            FrameLayout frame = (FrameLayout) dialog.view.findViewById(R.id.md_customViewFrame);
            dialog.customViewFrame = frame;
            View innerView = builder.customView;
            if (innerView.getParent() != null) {
                ((ViewGroup) innerView.getParent()).removeView(innerView);
            }
            if (builder.wrapCustomViewInScroll) {
                Resources r = dialog.getContext().getResources();
                int framePadding = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
                ScrollView sv = new ScrollView(dialog.getContext());
                int paddingTop = r.getDimensionPixelSize(R.dimen.md_content_padding_top);
                int paddingBottom = r.getDimensionPixelSize(R.dimen.md_content_padding_bottom);
                sv.setClipToPadding(false);
                if (innerView instanceof EditText) {
                    sv.setPadding(framePadding, paddingTop, framePadding, paddingBottom);
                } else {
                    sv.setPadding(0, paddingTop, 0, paddingBottom);
                    innerView.setPadding(framePadding, 0, framePadding, 0);
                }
                maxIconSize = -2;
                i = -1;
                sv.addView(innerView, new FrameLayout.LayoutParams(-1, -2));
                innerView = sv;
            } else {
                maxIconSize = -2;
                i = -1;
            }
            frame.addView(innerView, new ViewGroup.LayoutParams(i, maxIconSize));
        }
        if (builder.showListener != null) {
            dialog.setOnShowListener(builder.showListener);
        }
        if (builder.cancelListener != null) {
            dialog.setOnCancelListener(builder.cancelListener);
        }
        if (builder.dismissListener != null) {
            dialog.setOnDismissListener(builder.dismissListener);
        }
        if (builder.keyListener != null) {
            dialog.setOnKeyListener(builder.keyListener);
        }
        dialog.setOnShowListenerInternal();
        dialog.invalidateList();
        dialog.setViewInternal(dialog.view);
        dialog.checkIfListInitScroll();
        WindowManager wm = dialog.getWindow().getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int windowWidth = size.x;
        int windowHeight = size.y;
        int windowVerticalPadding = builder.context.getResources().getDimensionPixelSize(R.dimen.md_dialog_vertical_margin);
        int windowHorizontalPadding = builder.context.getResources().getDimensionPixelSize(R.dimen.md_dialog_horizontal_margin);
        int maxWidth = builder.context.getResources().getDimensionPixelSize(R.dimen.md_dialog_max_width);
        int calculatedWidth = windowWidth - (windowHorizontalPadding * 2);
        dialog.view.setMaxHeight(windowHeight - (windowVerticalPadding * 2));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = Math.min(maxWidth, calculatedWidth);
        dialog.getWindow().setAttributes(lp);
    }

    private static void fixCanvasScalingWhenHardwareAccelerated(ProgressBar pb) {
        if (Build.VERSION.SDK_INT < 18 && pb.isHardwareAccelerated() && pb.getLayerType() != 1) {
            pb.setLayerType(1, null);
        }
    }

    private static void setupProgressDialog(MaterialDialog dialog) {
        MaterialDialog.Builder builder = dialog.builder;
        if (builder.indeterminateProgress || builder.progress > -2) {
            dialog.progressBar = (ProgressBar) dialog.view.findViewById(16908301);
            if (dialog.progressBar == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 14) {
                if (builder.indeterminateProgress) {
                    if (builder.indeterminateIsHorizontalProgress) {
                        IndeterminateHorizontalProgressDrawable d = new IndeterminateHorizontalProgressDrawable(builder.getContext());
                        d.setTint(builder.widgetColor);
                        dialog.progressBar.setProgressDrawable(d);
                        dialog.progressBar.setIndeterminateDrawable(d);
                    } else {
                        IndeterminateCircularProgressDrawable d2 = new IndeterminateCircularProgressDrawable(builder.getContext());
                        d2.setTint(builder.widgetColor);
                        dialog.progressBar.setProgressDrawable(d2);
                        dialog.progressBar.setIndeterminateDrawable(d2);
                    }
                } else {
                    HorizontalProgressDrawable d3 = new HorizontalProgressDrawable(builder.getContext());
                    d3.setTint(builder.widgetColor);
                    dialog.progressBar.setProgressDrawable(d3);
                    dialog.progressBar.setIndeterminateDrawable(d3);
                }
            } else {
                MDTintHelper.setTint(dialog.progressBar, builder.widgetColor);
            }
            if (!builder.indeterminateProgress || builder.indeterminateIsHorizontalProgress) {
                dialog.progressBar.setIndeterminate(builder.indeterminateProgress && builder.indeterminateIsHorizontalProgress);
                dialog.progressBar.setProgress(0);
                dialog.progressBar.setMax(builder.progressMax);
                dialog.progressLabel = (TextView) dialog.view.findViewById(R.id.md_label);
                if (dialog.progressLabel != null) {
                    dialog.progressLabel.setTextColor(builder.contentColor);
                    dialog.setTypeface(dialog.progressLabel, builder.mediumFont);
                    dialog.progressLabel.setText(builder.progressPercentFormat.format(0L));
                }
                dialog.progressMinMax = (TextView) dialog.view.findViewById(R.id.md_minMax);
                if (dialog.progressMinMax != null) {
                    dialog.progressMinMax.setTextColor(builder.contentColor);
                    dialog.setTypeface(dialog.progressMinMax, builder.regularFont);
                    if (builder.showMinMax) {
                        dialog.progressMinMax.setVisibility(0);
                        dialog.progressMinMax.setText(String.format(builder.progressNumberFormat, 0, Integer.valueOf(builder.progressMax)));
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) dialog.progressBar.getLayoutParams();
                        lp.leftMargin = 0;
                        lp.rightMargin = 0;
                    } else {
                        dialog.progressMinMax.setVisibility(8);
                    }
                } else {
                    builder.showMinMax = false;
                }
            }
        }
        if (dialog.progressBar != null) {
            fixCanvasScalingWhenHardwareAccelerated(dialog.progressBar);
        }
    }

    private static void setupInputDialog(MaterialDialog dialog) {
        MaterialDialog.Builder builder = dialog.builder;
        dialog.input = (EditText) dialog.view.findViewById(16908297);
        if (dialog.input == null) {
            return;
        }
        dialog.setTypeface(dialog.input, builder.regularFont);
        if (builder.inputPrefill != null) {
            dialog.input.setText(builder.inputPrefill);
        }
        dialog.setInternalInputCallback();
        dialog.input.setHint(builder.inputHint);
        dialog.input.setSingleLine();
        dialog.input.setTextColor(builder.contentColor);
        dialog.input.setHintTextColor(DialogUtils.adjustAlpha(builder.contentColor, 0.3f));
        MDTintHelper.setTint(dialog.input, dialog.builder.widgetColor);
        if (builder.inputType != -1) {
            dialog.input.setInputType(builder.inputType);
            if (builder.inputType != 144 && (builder.inputType & 128) == 128) {
                dialog.input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
        dialog.inputMinMax = (TextView) dialog.view.findViewById(R.id.md_minMax);
        if (builder.inputMinLength > 0 || builder.inputMaxLength > -1) {
            dialog.invalidateInputMinMaxIndicator(dialog.input.getText().toString().length(), !builder.inputAllowEmpty);
            return;
        }
        dialog.inputMinMax.setVisibility(8);
        dialog.inputMinMax = null;
    }
}
