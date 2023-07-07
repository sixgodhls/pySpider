package com.afollestad.materialdialogs.color;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.commons.R;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/* loaded from: classes.dex */
public class ColorChooserDialog extends DialogFragment implements View.OnClickListener, View.OnLongClickListener {
    public static final String TAG_ACCENT = "[MD_COLOR_CHOOSER]";
    public static final String TAG_CUSTOM = "[MD_COLOR_CHOOSER]";
    public static final String TAG_PRIMARY = "[MD_COLOR_CHOOSER]";
    private ColorCallback callback;
    private int circleSize;
    private View colorChooserCustomFrame;
    @Nullable
    private int[][] colorsSub;
    private int[] colorsTop;
    private EditText customColorHex;
    private View customColorIndicator;
    private SeekBar.OnSeekBarChangeListener customColorRgbListener;
    private TextWatcher customColorTextWatcher;
    private SeekBar customSeekA;
    private TextView customSeekAValue;
    private SeekBar customSeekB;
    private TextView customSeekBValue;
    private SeekBar customSeekG;
    private TextView customSeekGValue;
    private SeekBar customSeekR;
    private TextView customSeekRValue;
    private GridView grid;
    private int selectedCustomColor;

    /* loaded from: classes.dex */
    public interface ColorCallback {
        void onColorChooserDismissed(@NonNull ColorChooserDialog colorChooserDialog);

        void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ColorChooserTag {
    }

    @Nullable
    public static ColorChooserDialog findVisible(@NonNull AppCompatActivity context, String tag) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null && (frag instanceof ColorChooserDialog)) {
            return (ColorChooserDialog) frag;
        }
        return null;
    }

    private void generateColors() {
        Builder builder = getBuilder();
        if (builder.colorsTop != null) {
            this.colorsTop = builder.colorsTop;
            this.colorsSub = builder.colorsSub;
        } else if (builder.accentMode) {
            this.colorsTop = ColorPalette.ACCENT_COLORS;
            this.colorsSub = ColorPalette.ACCENT_COLORS_SUB;
        } else {
            this.colorsTop = ColorPalette.PRIMARY_COLORS;
            this.colorsSub = ColorPalette.PRIMARY_COLORS_SUB;
        }
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("top_index", topIndex());
        outState.putBoolean("in_sub", isInSub());
        outState.putInt("sub_index", subIndex());
        View view = this.colorChooserCustomFrame;
        outState.putBoolean("in_custom", view != null && view.getVisibility() == 0);
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ColorCallback)) {
            throw new IllegalStateException("ColorChooserDialog needs to be shown from an Activity implementing ColorCallback.");
        }
        this.callback = (ColorCallback) activity;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInSub() {
        return getArguments().getBoolean("in_sub", false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void isInSub(boolean value) {
        getArguments().putBoolean("in_sub", value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int topIndex() {
        return getArguments().getInt("top_index", -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void topIndex(int value) {
        if (value > -1) {
            findSubIndexForColor(value, this.colorsTop[value]);
        }
        getArguments().putInt("top_index", value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int subIndex() {
        if (this.colorsSub == null) {
            return -1;
        }
        return getArguments().getInt("sub_index", -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void subIndex(int value) {
        if (this.colorsSub == null) {
            return;
        }
        getArguments().putInt("sub_index", value);
    }

    @StringRes
    public int getTitle() {
        int title;
        Builder builder = getBuilder();
        if (isInSub()) {
            title = builder.titleSub;
        } else {
            title = builder.title;
        }
        if (title == 0) {
            int title2 = builder.title;
            return title2;
        }
        return title;
    }

    public String tag() {
        Builder builder = getBuilder();
        if (builder.tag != null) {
            return builder.tag;
        }
        return super.getTag();
    }

    public boolean isAccentMode() {
        return getBuilder().accentMode;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.getTag() != null) {
            String[] tag = ((String) v.getTag()).split(":");
            int index = Integer.parseInt(tag[0]);
            MaterialDialog dialog = (MaterialDialog) getDialog();
            Builder builder = getBuilder();
            if (isInSub()) {
                subIndex(index);
            } else {
                topIndex(index);
                int[][] iArr = this.colorsSub;
                if (iArr != null && index < iArr.length) {
                    dialog.setActionButton(DialogAction.NEGATIVE, builder.backBtn);
                    isInSub(true);
                }
            }
            if (builder.allowUserCustom) {
                this.selectedCustomColor = getSelectedColor();
            }
            invalidateDynamicButtonColors();
            invalidate();
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (v.getTag() != null) {
            String[] tag = ((String) v.getTag()).split(":");
            int color = Integer.parseInt(tag[1]);
            ((CircleView) v).showHint(color);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateDynamicButtonColors() {
        MaterialDialog dialog = (MaterialDialog) getDialog();
        if (dialog == null) {
            return;
        }
        Builder builder = getBuilder();
        if (builder.dynamicButtonColor) {
            int selectedColor = getSelectedColor();
            if (Color.alpha(selectedColor) < 64 || (Color.red(selectedColor) > 247 && Color.green(selectedColor) > 247 && Color.blue(selectedColor) > 247)) {
                selectedColor = Color.parseColor("#DEDEDE");
            }
            if (getBuilder().dynamicButtonColor) {
                dialog.getActionButton(DialogAction.POSITIVE).setTextColor(selectedColor);
                dialog.getActionButton(DialogAction.NEGATIVE).setTextColor(selectedColor);
                dialog.getActionButton(DialogAction.NEUTRAL).setTextColor(selectedColor);
            }
            if (this.customSeekR != null) {
                if (this.customSeekA.getVisibility() == 0) {
                    MDTintHelper.setTint(this.customSeekA, selectedColor);
                }
                MDTintHelper.setTint(this.customSeekR, selectedColor);
                MDTintHelper.setTint(this.customSeekG, selectedColor);
                MDTintHelper.setTint(this.customSeekB, selectedColor);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @ColorInt
    public int getSelectedColor() {
        View view = this.colorChooserCustomFrame;
        if (view != null && view.getVisibility() == 0) {
            return this.selectedCustomColor;
        }
        int color = 0;
        if (subIndex() > -1) {
            color = this.colorsSub[topIndex()][subIndex()];
        } else if (topIndex() > -1) {
            color = this.colorsTop[topIndex()];
        }
        if (color == 0) {
            int fallback = 0;
            if (Build.VERSION.SDK_INT >= 21) {
                fallback = DialogUtils.resolveColor(getActivity(), 16843829);
            }
            int color2 = DialogUtils.resolveColor(getActivity(), R.attr.colorAccent, fallback);
            return color2;
        }
        return color;
    }

    private void findSubIndexForColor(int topIndex, int color) {
        int[][] iArr = this.colorsSub;
        if (iArr == null || iArr.length - 1 < topIndex) {
            return;
        }
        int[] subColors = iArr[topIndex];
        for (int subIndex = 0; subIndex < subColors.length; subIndex++) {
            if (subColors[subIndex] == color) {
                subIndex(subIndex);
                return;
            }
        }
    }

    @Override // android.support.v4.app.DialogFragment
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int preselectColor;
        if (getArguments() == null || !getArguments().containsKey("builder")) {
            throw new IllegalStateException("ColorChooserDialog should be created using its Builder interface.");
        }
        generateColors();
        boolean foundPreselectColor = false;
        if (savedInstanceState != null) {
            foundPreselectColor = !savedInstanceState.getBoolean("in_custom", false);
            preselectColor = getSelectedColor();
        } else if (getBuilder().setPreselectionColor) {
            preselectColor = getBuilder().preselectColor;
            if (preselectColor != 0) {
                int topIndex = 0;
                while (true) {
                    int[] iArr = this.colorsTop;
                    if (topIndex >= iArr.length) {
                        break;
                    } else if (iArr[topIndex] == preselectColor) {
                        foundPreselectColor = true;
                        topIndex(topIndex);
                        if (getBuilder().accentMode) {
                            subIndex(2);
                        } else if (this.colorsSub != null) {
                            findSubIndexForColor(topIndex, preselectColor);
                        } else {
                            subIndex(5);
                        }
                    } else {
                        if (this.colorsSub != null) {
                            int subIndex = 0;
                            while (true) {
                                int[][] iArr2 = this.colorsSub;
                                if (subIndex >= iArr2[topIndex].length) {
                                    break;
                                } else if (iArr2[topIndex][subIndex] != preselectColor) {
                                    subIndex++;
                                } else {
                                    foundPreselectColor = true;
                                    topIndex(topIndex);
                                    subIndex(subIndex);
                                    break;
                                }
                            }
                            if (foundPreselectColor) {
                                break;
                            }
                        }
                        topIndex++;
                    }
                }
            }
        } else {
            preselectColor = ViewCompat.MEASURED_STATE_MASK;
            foundPreselectColor = true;
        }
        this.circleSize = getResources().getDimensionPixelSize(R.dimen.md_colorchooser_circlesize);
        Builder builder = getBuilder();
        MaterialDialog.Builder bd = new MaterialDialog.Builder(getActivity()).title(getTitle()).autoDismiss(false).customView(R.layout.md_dialog_colorchooser, false).negativeText(builder.cancelBtn).positiveText(builder.doneBtn).neutralText(builder.allowUserCustom ? builder.customBtn : 0).typeface(builder.mediumFont, builder.regularFont).onPositive(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.color.ColorChooserDialog.4
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                ColorCallback colorCallback = ColorChooserDialog.this.callback;
                ColorChooserDialog colorChooserDialog = ColorChooserDialog.this;
                colorCallback.onColorSelection(colorChooserDialog, colorChooserDialog.getSelectedColor());
                ColorChooserDialog.this.dismiss();
            }
        }).onNegative(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.color.ColorChooserDialog.3
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (ColorChooserDialog.this.isInSub()) {
                    dialog.setActionButton(DialogAction.NEGATIVE, ColorChooserDialog.this.getBuilder().cancelBtn);
                    ColorChooserDialog.this.isInSub(false);
                    ColorChooserDialog.this.subIndex(-1);
                    ColorChooserDialog.this.invalidate();
                    return;
                }
                dialog.cancel();
            }
        }).onNeutral(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.color.ColorChooserDialog.2
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                ColorChooserDialog.this.toggleCustom(dialog);
            }
        }).showListener(new DialogInterface.OnShowListener() { // from class: com.afollestad.materialdialogs.color.ColorChooserDialog.1
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface dialog) {
                ColorChooserDialog.this.invalidateDynamicButtonColors();
            }
        });
        if (builder.theme != null) {
            bd.theme(builder.theme);
        }
        MaterialDialog dialog = bd.build();
        View v = dialog.getCustomView();
        this.grid = (GridView) v.findViewById(R.id.md_grid);
        if (builder.allowUserCustom) {
            this.selectedCustomColor = preselectColor;
            this.colorChooserCustomFrame = v.findViewById(R.id.md_colorChooserCustomFrame);
            this.customColorHex = (EditText) v.findViewById(R.id.md_hexInput);
            this.customColorIndicator = v.findViewById(R.id.md_colorIndicator);
            this.customSeekA = (SeekBar) v.findViewById(R.id.md_colorA);
            this.customSeekAValue = (TextView) v.findViewById(R.id.md_colorAValue);
            this.customSeekR = (SeekBar) v.findViewById(R.id.md_colorR);
            this.customSeekRValue = (TextView) v.findViewById(R.id.md_colorRValue);
            this.customSeekG = (SeekBar) v.findViewById(R.id.md_colorG);
            this.customSeekGValue = (TextView) v.findViewById(R.id.md_colorGValue);
            this.customSeekB = (SeekBar) v.findViewById(R.id.md_colorB);
            this.customSeekBValue = (TextView) v.findViewById(R.id.md_colorBValue);
            if (!builder.allowUserCustomAlpha) {
                v.findViewById(R.id.md_colorALabel).setVisibility(8);
                this.customSeekA.setVisibility(8);
                this.customSeekAValue.setVisibility(8);
                this.customColorHex.setHint("2196F3");
                this.customColorHex.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            } else {
                this.customColorHex.setHint("FF2196F3");
                this.customColorHex.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            }
            if (!foundPreselectColor) {
                toggleCustom(dialog);
            }
        }
        invalidate();
        return dialog;
    }

    @Override // android.support.v4.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ColorCallback colorCallback = this.callback;
        if (colorCallback != null) {
            colorCallback.onColorChooserDismissed(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleCustom(MaterialDialog dialog) {
        if (dialog == null) {
            dialog = (MaterialDialog) getDialog();
        }
        if (this.grid.getVisibility() == 0) {
            dialog.setTitle(getBuilder().customBtn);
            dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().presetsBtn);
            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().cancelBtn);
            this.grid.setVisibility(4);
            this.colorChooserCustomFrame.setVisibility(0);
            this.customColorTextWatcher = new TextWatcher() { // from class: com.afollestad.materialdialogs.color.ColorChooserDialog.5
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        ColorChooserDialog colorChooserDialog = ColorChooserDialog.this;
                        colorChooserDialog.selectedCustomColor = Color.parseColor("#" + s.toString());
                    } catch (IllegalArgumentException e) {
                        ColorChooserDialog.this.selectedCustomColor = ViewCompat.MEASURED_STATE_MASK;
                    }
                    ColorChooserDialog.this.customColorIndicator.setBackgroundColor(ColorChooserDialog.this.selectedCustomColor);
                    if (ColorChooserDialog.this.customSeekA.getVisibility() == 0) {
                        int alpha = Color.alpha(ColorChooserDialog.this.selectedCustomColor);
                        ColorChooserDialog.this.customSeekA.setProgress(alpha);
                        ColorChooserDialog.this.customSeekAValue.setText(String.format(Locale.US, "%d", Integer.valueOf(alpha)));
                    }
                    if (ColorChooserDialog.this.customSeekA.getVisibility() == 0) {
                        ColorChooserDialog.this.customSeekA.setProgress(Color.alpha(ColorChooserDialog.this.selectedCustomColor));
                    }
                    int red = Color.red(ColorChooserDialog.this.selectedCustomColor);
                    ColorChooserDialog.this.customSeekR.setProgress(red);
                    int green = Color.green(ColorChooserDialog.this.selectedCustomColor);
                    ColorChooserDialog.this.customSeekG.setProgress(green);
                    int blue = Color.blue(ColorChooserDialog.this.selectedCustomColor);
                    ColorChooserDialog.this.customSeekB.setProgress(blue);
                    ColorChooserDialog.this.isInSub(false);
                    ColorChooserDialog.this.topIndex(-1);
                    ColorChooserDialog.this.subIndex(-1);
                    ColorChooserDialog.this.invalidateDynamicButtonColors();
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                }
            };
            this.customColorHex.addTextChangedListener(this.customColorTextWatcher);
            this.customColorRgbListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.afollestad.materialdialogs.color.ColorChooserDialog.6
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                @SuppressLint({"DefaultLocale"})
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        if (ColorChooserDialog.this.getBuilder().allowUserCustomAlpha) {
                            int color = Color.argb(ColorChooserDialog.this.customSeekA.getProgress(), ColorChooserDialog.this.customSeekR.getProgress(), ColorChooserDialog.this.customSeekG.getProgress(), ColorChooserDialog.this.customSeekB.getProgress());
                            ColorChooserDialog.this.customColorHex.setText(String.format("%08X", Integer.valueOf(color)));
                        } else {
                            int color2 = Color.rgb(ColorChooserDialog.this.customSeekR.getProgress(), ColorChooserDialog.this.customSeekG.getProgress(), ColorChooserDialog.this.customSeekB.getProgress());
                            ColorChooserDialog.this.customColorHex.setText(String.format("%06X", Integer.valueOf(16777215 & color2)));
                        }
                    }
                    ColorChooserDialog.this.customSeekAValue.setText(String.format("%d", Integer.valueOf(ColorChooserDialog.this.customSeekA.getProgress())));
                    ColorChooserDialog.this.customSeekRValue.setText(String.format("%d", Integer.valueOf(ColorChooserDialog.this.customSeekR.getProgress())));
                    ColorChooserDialog.this.customSeekGValue.setText(String.format("%d", Integer.valueOf(ColorChooserDialog.this.customSeekG.getProgress())));
                    ColorChooserDialog.this.customSeekBValue.setText(String.format("%d", Integer.valueOf(ColorChooserDialog.this.customSeekB.getProgress())));
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };
            this.customSeekR.setOnSeekBarChangeListener(this.customColorRgbListener);
            this.customSeekG.setOnSeekBarChangeListener(this.customColorRgbListener);
            this.customSeekB.setOnSeekBarChangeListener(this.customColorRgbListener);
            if (this.customSeekA.getVisibility() == 0) {
                this.customSeekA.setOnSeekBarChangeListener(this.customColorRgbListener);
                this.customColorHex.setText(String.format("%08X", Integer.valueOf(this.selectedCustomColor)));
                return;
            }
            this.customColorHex.setText(String.format("%06X", Integer.valueOf(16777215 & this.selectedCustomColor)));
            return;
        }
        dialog.setTitle(getBuilder().title);
        dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().customBtn);
        if (isInSub()) {
            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().backBtn);
        } else {
            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().cancelBtn);
        }
        this.grid.setVisibility(0);
        this.colorChooserCustomFrame.setVisibility(8);
        this.customColorHex.removeTextChangedListener(this.customColorTextWatcher);
        this.customColorTextWatcher = null;
        this.customSeekR.setOnSeekBarChangeListener(null);
        this.customSeekG.setOnSeekBarChangeListener(null);
        this.customSeekB.setOnSeekBarChangeListener(null);
        this.customColorRgbListener = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidate() {
        if (this.grid.getAdapter() == null) {
            this.grid.setAdapter((ListAdapter) new ColorGridAdapter());
            this.grid.setSelector(ResourcesCompat.getDrawable(getResources(), R.drawable.md_transparent, null));
        } else {
            ((BaseAdapter) this.grid.getAdapter()).notifyDataSetChanged();
        }
        if (getDialog() != null) {
            getDialog().setTitle(getTitle());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Builder getBuilder() {
        if (getArguments() == null || !getArguments().containsKey("builder")) {
            return null;
        }
        return (Builder) getArguments().getSerializable("builder");
    }

    private void dismissIfNecessary(AppCompatActivity context, String tag) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null) {
            ((DialogFragment) frag).dismiss();
            context.getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }
    }

    @NonNull
    public ColorChooserDialog show(AppCompatActivity context) {
        String tag;
        Builder builder = getBuilder();
        if (builder.colorsTop != null) {
            tag = "[MD_COLOR_CHOOSER]";
        } else if (builder.accentMode) {
            tag = "[MD_COLOR_CHOOSER]";
        } else {
            tag = "[MD_COLOR_CHOOSER]";
        }
        dismissIfNecessary(context, tag);
        show(context.getSupportFragmentManager(), tag);
        return this;
    }

    /* loaded from: classes.dex */
    public static class Builder implements Serializable {
        @Nullable
        int[][] colorsSub;
        @Nullable
        int[] colorsTop;
        @NonNull
        final transient AppCompatActivity context;
        @Nullable
        String mediumFont;
        @ColorInt
        int preselectColor;
        @Nullable
        String regularFont;
        @Nullable
        String tag;
        @Nullable
        Theme theme;
        @StringRes
        final int title;
        @StringRes
        int titleSub;
        @StringRes
        int doneBtn = R.string.md_done_label;
        @StringRes
        int backBtn = R.string.md_back_label;
        @StringRes
        int cancelBtn = R.string.md_cancel_label;
        @StringRes
        int customBtn = R.string.md_custom_label;
        @StringRes
        int presetsBtn = R.string.md_presets_label;
        boolean accentMode = false;
        boolean dynamicButtonColor = true;
        boolean allowUserCustom = true;
        boolean allowUserCustomAlpha = true;
        boolean setPreselectionColor = false;

        public <ActivityType extends AppCompatActivity & ColorCallback> Builder(@NonNull ActivityType context, @StringRes int title) {
            this.context = context;
            this.title = title;
        }

        @NonNull
        public Builder typeface(@Nullable String medium, @Nullable String regular) {
            this.mediumFont = medium;
            this.regularFont = regular;
            return this;
        }

        @NonNull
        public Builder titleSub(@StringRes int titleSub) {
            this.titleSub = titleSub;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        @NonNull
        public Builder theme(@NonNull Theme theme) {
            this.theme = theme;
            return this;
        }

        @NonNull
        public Builder preselect(@ColorInt int preselect) {
            this.preselectColor = preselect;
            this.setPreselectionColor = true;
            return this;
        }

        @NonNull
        public Builder accentMode(boolean accentMode) {
            this.accentMode = accentMode;
            return this;
        }

        @NonNull
        public Builder doneButton(@StringRes int text) {
            this.doneBtn = text;
            return this;
        }

        @NonNull
        public Builder backButton(@StringRes int text) {
            this.backBtn = text;
            return this;
        }

        @NonNull
        public Builder cancelButton(@StringRes int text) {
            this.cancelBtn = text;
            return this;
        }

        @NonNull
        public Builder customButton(@StringRes int text) {
            this.customBtn = text;
            return this;
        }

        @NonNull
        public Builder presetsButton(@StringRes int text) {
            this.presetsBtn = text;
            return this;
        }

        @NonNull
        public Builder dynamicButtonColor(boolean enabled) {
            this.dynamicButtonColor = enabled;
            return this;
        }

        @NonNull
        public Builder customColors(@NonNull int[] topLevel, @Nullable int[][] subLevel) {
            this.colorsTop = topLevel;
            this.colorsSub = subLevel;
            return this;
        }

        @NonNull
        public Builder customColors(@ArrayRes int topLevel, @Nullable int[][] subLevel) {
            this.colorsTop = DialogUtils.getColorArray(this.context, topLevel);
            this.colorsSub = subLevel;
            return this;
        }

        @NonNull
        public Builder allowUserColorInput(boolean allow) {
            this.allowUserCustom = allow;
            return this;
        }

        @NonNull
        public Builder allowUserColorInputAlpha(boolean allow) {
            this.allowUserCustomAlpha = allow;
            return this;
        }

        @NonNull
        public ColorChooserDialog build() {
            ColorChooserDialog dialog = new ColorChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        public ColorChooserDialog show() {
            ColorChooserDialog dialog = build();
            dialog.show(this.context);
            return dialog;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ColorGridAdapter extends BaseAdapter {
        ColorGridAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return ColorChooserDialog.this.isInSub() ? ColorChooserDialog.this.colorsSub[ColorChooserDialog.this.topIndex()].length : ColorChooserDialog.this.colorsTop.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return ColorChooserDialog.this.isInSub() ? Integer.valueOf(ColorChooserDialog.this.colorsSub[ColorChooserDialog.this.topIndex()][position]) : Integer.valueOf(ColorChooserDialog.this.colorsTop[position]);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        @SuppressLint({"DefaultLocale"})
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new CircleView(ColorChooserDialog.this.getContext());
                convertView.setLayoutParams(new AbsListView.LayoutParams(ColorChooserDialog.this.circleSize, ColorChooserDialog.this.circleSize));
            }
            CircleView child = (CircleView) convertView;
            int color = ColorChooserDialog.this.isInSub() ? ColorChooserDialog.this.colorsSub[ColorChooserDialog.this.topIndex()][position] : ColorChooserDialog.this.colorsTop[position];
            child.setBackgroundColor(color);
            if (ColorChooserDialog.this.isInSub()) {
                child.setSelected(ColorChooserDialog.this.subIndex() == position);
            } else {
                child.setSelected(ColorChooserDialog.this.topIndex() == position);
            }
            child.setTag(String.format("%d:%d", Integer.valueOf(position), Integer.valueOf(color)));
            child.setOnClickListener(ColorChooserDialog.this);
            child.setOnLongClickListener(ColorChooserDialog.this);
            return convertView;
        }
    }
}
