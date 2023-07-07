package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.DefaultRvAdapter;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDRootLayout;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.afollestad.materialdialogs.util.RippleHelper;
import com.afollestad.materialdialogs.util.TypefaceHelper;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class MaterialDialog extends DialogBase implements View.OnClickListener, DefaultRvAdapter.InternalListCallback {
    protected final Builder builder;
    CheckBox checkBoxPrompt;
    protected TextView content;
    FrameLayout customViewFrame;
    private final Handler handler = new Handler();
    protected ImageView icon;
    EditText input;
    TextView inputMinMax;
    ListType listType;
    MDButton negativeButton;
    MDButton neutralButton;
    MDButton positiveButton;
    ProgressBar progressBar;
    TextView progressLabel;
    TextView progressMinMax;
    RecyclerView recyclerView;
    List<Integer> selectedIndicesList;
    protected TextView title;
    View titleFrame;

    /* loaded from: classes.dex */
    public interface InputCallback {
        void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence);
    }

    /* loaded from: classes.dex */
    public interface ListCallback {
        void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence);
    }

    /* loaded from: classes.dex */
    public interface ListCallbackMultiChoice {
        boolean onSelection(MaterialDialog materialDialog, Integer[] numArr, CharSequence[] charSequenceArr);
    }

    /* loaded from: classes.dex */
    public interface ListCallbackSingleChoice {
        boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence);
    }

    /* loaded from: classes.dex */
    public interface ListLongCallback {
        boolean onLongSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence);
    }

    /* loaded from: classes.dex */
    public interface SingleButtonCallback {
        void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction);
    }

    @Override // com.afollestad.materialdialogs.DialogBase, android.app.Dialog
    public /* bridge */ /* synthetic */ View findViewById(int i) {
        return super.findViewById(i);
    }

    @Override // com.afollestad.materialdialogs.DialogBase, android.app.Dialog
    @Deprecated
    public /* bridge */ /* synthetic */ void setContentView(int i) throws IllegalAccessError {
        super.setContentView(i);
    }

    @Override // com.afollestad.materialdialogs.DialogBase, android.app.Dialog
    @Deprecated
    public /* bridge */ /* synthetic */ void setContentView(@NonNull View view) throws IllegalAccessError {
        super.setContentView(view);
    }

    @Override // com.afollestad.materialdialogs.DialogBase, android.app.Dialog
    @Deprecated
    public /* bridge */ /* synthetic */ void setContentView(@NonNull View view, ViewGroup.LayoutParams layoutParams) throws IllegalAccessError {
        super.setContentView(view, layoutParams);
    }

    @SuppressLint({"InflateParams"})
    protected MaterialDialog(Builder builder) {
        super(builder.context, DialogInit.getTheme(builder));
        this.builder = builder;
        LayoutInflater inflater = LayoutInflater.from(builder.context);
        this.view = (MDRootLayout) inflater.inflate(DialogInit.getInflateLayout(builder), (ViewGroup) null);
        DialogInit.init(this);
    }

    public final Builder getBuilder() {
        return this.builder;
    }

    public final void setTypeface(TextView target, Typeface t) {
        if (t == null) {
            return;
        }
        int flags = target.getPaintFlags() | 128;
        target.setPaintFlags(flags);
        target.setTypeface(t);
    }

    @Nullable
    public Object getTag() {
        return this.builder.tag;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void checkIfListInitScroll() {
        RecyclerView recyclerView = this.recyclerView;
        if (recyclerView == null) {
            return;
        }
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.afollestad.materialdialogs.MaterialDialog.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                int selectedIndex;
                if (Build.VERSION.SDK_INT < 16) {
                    MaterialDialog.this.recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    MaterialDialog.this.recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                if (MaterialDialog.this.listType == ListType.SINGLE || MaterialDialog.this.listType == ListType.MULTI) {
                    if (MaterialDialog.this.listType == ListType.SINGLE) {
                        if (MaterialDialog.this.builder.selectedIndex < 0) {
                            return;
                        }
                        selectedIndex = MaterialDialog.this.builder.selectedIndex;
                    } else if (MaterialDialog.this.selectedIndicesList == null || MaterialDialog.this.selectedIndicesList.size() == 0) {
                        return;
                    } else {
                        Collections.sort(MaterialDialog.this.selectedIndicesList);
                        selectedIndex = MaterialDialog.this.selectedIndicesList.get(0).intValue();
                    }
                    final int fSelectedIndex = selectedIndex;
                    MaterialDialog.this.recyclerView.post(new Runnable() { // from class: com.afollestad.materialdialogs.MaterialDialog.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MaterialDialog.this.recyclerView.requestFocus();
                            MaterialDialog.this.builder.layoutManager.scrollToPosition(fSelectedIndex);
                        }
                    });
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void invalidateList() {
        if (this.recyclerView == null) {
            return;
        }
        if ((this.builder.items == null || this.builder.items.size() == 0) && this.builder.adapter == null) {
            return;
        }
        if (this.builder.layoutManager == null) {
            this.builder.layoutManager = new LinearLayoutManager(getContext());
        }
        this.recyclerView.setLayoutManager(this.builder.layoutManager);
        this.recyclerView.setAdapter(this.builder.adapter);
        if (this.listType != null) {
            ((DefaultRvAdapter) this.builder.adapter).setCallback(this);
        }
    }

    @Override // com.afollestad.materialdialogs.DefaultRvAdapter.InternalListCallback
    public boolean onItemSelected(MaterialDialog dialog, View view, int position, CharSequence text, boolean longPress) {
        if (!view.isEnabled()) {
            return false;
        }
        ListType listType = this.listType;
        if (listType == null || listType == ListType.REGULAR) {
            if (this.builder.autoDismiss) {
                dismiss();
            }
            if (!longPress && this.builder.listCallback != null) {
                this.builder.listCallback.onSelection(this, view, position, this.builder.items.get(position));
            }
            if (longPress && this.builder.listLongCallback != null) {
                return this.builder.listLongCallback.onLongSelection(this, view, position, this.builder.items.get(position));
            }
        } else if (this.listType == ListType.MULTI) {
            CheckBox cb = (CheckBox) view.findViewById(R.id.md_control);
            if (!cb.isEnabled()) {
                return false;
            }
            boolean shouldBeChecked = !this.selectedIndicesList.contains(Integer.valueOf(position));
            if (shouldBeChecked) {
                this.selectedIndicesList.add(Integer.valueOf(position));
                if (this.builder.alwaysCallMultiChoiceCallback) {
                    if (sendMultiChoiceCallback()) {
                        cb.setChecked(true);
                    } else {
                        this.selectedIndicesList.remove(Integer.valueOf(position));
                    }
                } else {
                    cb.setChecked(true);
                }
            } else {
                this.selectedIndicesList.remove(Integer.valueOf(position));
                if (this.builder.alwaysCallMultiChoiceCallback) {
                    if (sendMultiChoiceCallback()) {
                        cb.setChecked(false);
                    } else {
                        this.selectedIndicesList.add(Integer.valueOf(position));
                    }
                } else {
                    cb.setChecked(false);
                }
            }
        } else if (this.listType == ListType.SINGLE) {
            RadioButton radio = (RadioButton) view.findViewById(R.id.md_control);
            if (!radio.isEnabled()) {
                return false;
            }
            boolean allowSelection = true;
            int oldSelected = this.builder.selectedIndex;
            if (this.builder.autoDismiss && this.builder.positiveText == null) {
                dismiss();
                allowSelection = false;
                this.builder.selectedIndex = position;
                sendSingleChoiceCallback(view);
            } else if (this.builder.alwaysCallSingleChoiceCallback) {
                this.builder.selectedIndex = position;
                allowSelection = sendSingleChoiceCallback(view);
                this.builder.selectedIndex = oldSelected;
            }
            if (allowSelection) {
                this.builder.selectedIndex = position;
                radio.setChecked(true);
                this.builder.adapter.notifyItemChanged(oldSelected);
                this.builder.adapter.notifyItemChanged(position);
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Drawable getListSelector() {
        if (this.builder.listSelector != 0) {
            return ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.listSelector, null);
        }
        Drawable d = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_list_selector);
        if (d != null) {
            return d;
        }
        return DialogUtils.resolveDrawable(getContext(), R.attr.md_list_selector);
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

    public boolean isPromptCheckBoxChecked() {
        CheckBox checkBox = this.checkBoxPrompt;
        return checkBox != null && checkBox.isChecked();
    }

    public void setPromptCheckBoxChecked(boolean checked) {
        CheckBox checkBox = this.checkBoxPrompt;
        if (checkBox != null) {
            checkBox.setChecked(checked);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Drawable getButtonSelector(DialogAction which, boolean isStacked) {
        if (isStacked) {
            if (this.builder.btnSelectorStacked != 0) {
                return ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.btnSelectorStacked, null);
            }
            Drawable d = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_btn_stacked_selector);
            if (d != null) {
                return d;
            }
            return DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_stacked_selector);
        }
        switch (which) {
            case NEUTRAL:
                if (this.builder.btnSelectorNeutral != 0) {
                    return ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.btnSelectorNeutral, null);
                }
                Drawable d2 = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_btn_neutral_selector);
                if (d2 != null) {
                    return d2;
                }
                Drawable d3 = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_neutral_selector);
                if (Build.VERSION.SDK_INT >= 21) {
                    RippleHelper.applyColor(d3, this.builder.buttonRippleColor);
                }
                return d3;
            case NEGATIVE:
                if (this.builder.btnSelectorNegative != 0) {
                    return ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.btnSelectorNegative, null);
                }
                Drawable d4 = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_btn_negative_selector);
                if (d4 != null) {
                    return d4;
                }
                Drawable d5 = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_negative_selector);
                if (Build.VERSION.SDK_INT >= 21) {
                    RippleHelper.applyColor(d5, this.builder.buttonRippleColor);
                }
                return d5;
            default:
                if (this.builder.btnSelectorPositive != 0) {
                    return ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.btnSelectorPositive, null);
                }
                Drawable d6 = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_btn_positive_selector);
                if (d6 != null) {
                    return d6;
                }
                Drawable d7 = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_positive_selector);
                if (Build.VERSION.SDK_INT >= 21) {
                    RippleHelper.applyColor(d7, this.builder.buttonRippleColor);
                }
                return d7;
        }
    }

    private boolean sendSingleChoiceCallback(View v) {
        if (this.builder.listCallbackSingleChoice == null) {
            return false;
        }
        CharSequence text = null;
        if (this.builder.selectedIndex >= 0 && this.builder.selectedIndex < this.builder.items.size()) {
            text = this.builder.items.get(this.builder.selectedIndex);
        }
        return this.builder.listCallbackSingleChoice.onSelection(this, v, this.builder.selectedIndex, text);
    }

    private boolean sendMultiChoiceCallback() {
        if (this.builder.listCallbackMultiChoice == null) {
            return false;
        }
        Collections.sort(this.selectedIndicesList);
        List<CharSequence> selectedTitles = new ArrayList<>();
        for (Integer i : this.selectedIndicesList) {
            if (i.intValue() >= 0 && i.intValue() <= this.builder.items.size() - 1) {
                selectedTitles.add(this.builder.items.get(i.intValue()));
            }
        }
        ListCallbackMultiChoice listCallbackMultiChoice = this.builder.listCallbackMultiChoice;
        List<Integer> list = this.selectedIndicesList;
        return listCallbackMultiChoice.onSelection(this, (Integer[]) list.toArray(new Integer[list.size()]), (CharSequence[]) selectedTitles.toArray(new CharSequence[selectedTitles.size()]));
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View v) {
        DialogAction tag = (DialogAction) v.getTag();
        switch (tag) {
            case NEUTRAL:
                if (this.builder.callback != null) {
                    this.builder.callback.onAny(this);
                    this.builder.callback.onNeutral(this);
                }
                if (this.builder.onNeutralCallback != null) {
                    this.builder.onNeutralCallback.onClick(this, tag);
                }
                if (this.builder.autoDismiss) {
                    dismiss();
                    break;
                }
                break;
            case NEGATIVE:
                if (this.builder.callback != null) {
                    this.builder.callback.onAny(this);
                    this.builder.callback.onNegative(this);
                }
                if (this.builder.onNegativeCallback != null) {
                    this.builder.onNegativeCallback.onClick(this, tag);
                }
                if (this.builder.autoDismiss) {
                    cancel();
                    break;
                }
                break;
            case POSITIVE:
                if (this.builder.callback != null) {
                    this.builder.callback.onAny(this);
                    this.builder.callback.onPositive(this);
                }
                if (this.builder.onPositiveCallback != null) {
                    this.builder.onPositiveCallback.onClick(this, tag);
                }
                if (!this.builder.alwaysCallSingleChoiceCallback) {
                    sendSingleChoiceCallback(v);
                }
                if (!this.builder.alwaysCallMultiChoiceCallback) {
                    sendMultiChoiceCallback();
                }
                if (this.builder.inputCallback != null && this.input != null && !this.builder.alwaysCallInputCallback) {
                    this.builder.inputCallback.onInput(this, this.input.getText());
                }
                if (this.builder.autoDismiss) {
                    dismiss();
                    break;
                }
                break;
        }
        if (this.builder.onAnyCallback != null) {
            this.builder.onAnyCallback.onClick(this, tag);
        }
    }

    @Override // android.app.Dialog
    @UiThread
    public void show() {
        try {
            super.show();
        } catch (WindowManager.BadTokenException e) {
            throw new DialogException("Bad window token, you cannot show a dialog before an Activity is created or after it's hidden.");
        }
    }

    public final MDButton getActionButton(@NonNull DialogAction which) {
        switch (which) {
            case NEUTRAL:
                return this.neutralButton;
            case NEGATIVE:
                return this.negativeButton;
            default:
                return this.positiveButton;
        }
    }

    public final View getView() {
        return this.view;
    }

    @Nullable
    public final EditText getInputEditText() {
        return this.input;
    }

    public final TextView getTitleView() {
        return this.title;
    }

    public ImageView getIconView() {
        return this.icon;
    }

    @Nullable
    public final TextView getContentView() {
        return this.content;
    }

    @Nullable
    public final View getCustomView() {
        return this.builder.customView;
    }

    @UiThread
    public final void setActionButton(@NonNull DialogAction which, CharSequence title) {
        int i = 8;
        switch (which) {
            case NEUTRAL:
                this.builder.neutralText = title;
                this.neutralButton.setText(title);
                MDButton mDButton = this.neutralButton;
                if (title != null) {
                    i = 0;
                }
                mDButton.setVisibility(i);
                return;
            case NEGATIVE:
                this.builder.negativeText = title;
                this.negativeButton.setText(title);
                MDButton mDButton2 = this.negativeButton;
                if (title != null) {
                    i = 0;
                }
                mDButton2.setVisibility(i);
                return;
            default:
                this.builder.positiveText = title;
                this.positiveButton.setText(title);
                MDButton mDButton3 = this.positiveButton;
                if (title != null) {
                    i = 0;
                }
                mDButton3.setVisibility(i);
                return;
        }
    }

    public final void setActionButton(DialogAction which, @StringRes int titleRes) {
        setActionButton(which, getContext().getText(titleRes));
    }

    public final boolean hasActionButtons() {
        return numberOfActionButtons() > 0;
    }

    public final int numberOfActionButtons() {
        int number = 0;
        if (this.builder.positiveText != null && this.positiveButton.getVisibility() == 0) {
            number = 0 + 1;
        }
        if (this.builder.neutralText != null && this.neutralButton.getVisibility() == 0) {
            number++;
        }
        if (this.builder.negativeText != null && this.negativeButton.getVisibility() == 0) {
            return number + 1;
        }
        return number;
    }

    @Override // android.app.Dialog
    @UiThread
    public final void setTitle(CharSequence newTitle) {
        this.title.setText(newTitle);
    }

    @Override // android.app.Dialog
    @UiThread
    public final void setTitle(@StringRes int newTitleRes) {
        setTitle(this.builder.context.getString(newTitleRes));
    }

    @UiThread
    public final void setTitle(@StringRes int newTitleRes, @Nullable Object... formatArgs) {
        setTitle(this.builder.context.getString(newTitleRes, formatArgs));
    }

    @UiThread
    public void setIcon(@DrawableRes int resId) {
        this.icon.setImageResource(resId);
        this.icon.setVisibility(resId != 0 ? 0 : 8);
    }

    @UiThread
    public void setIcon(Drawable d) {
        this.icon.setImageDrawable(d);
        this.icon.setVisibility(d != null ? 0 : 8);
    }

    @UiThread
    public void setIconAttribute(@AttrRes int attrId) {
        Drawable d = DialogUtils.resolveDrawable(this.builder.context, attrId);
        setIcon(d);
    }

    @UiThread
    public final void setContent(CharSequence newContent) {
        this.content.setText(newContent);
        this.content.setVisibility(TextUtils.isEmpty(newContent) ? 8 : 0);
    }

    @UiThread
    public final void setContent(@StringRes int newContentRes) {
        setContent(this.builder.context.getString(newContentRes));
    }

    @UiThread
    public final void setContent(@StringRes int newContentRes, @Nullable Object... formatArgs) {
        setContent(this.builder.context.getString(newContentRes, formatArgs));
    }

    @Nullable
    public final ArrayList<CharSequence> getItems() {
        return this.builder.items;
    }

    @UiThread
    public final void setItems(CharSequence... items) {
        if (this.builder.adapter == null) {
            throw new IllegalStateException("This MaterialDialog instance does not yet have an adapter set to it. You cannot use setItems().");
        }
        if (items != null) {
            this.builder.items = new ArrayList<>(items.length);
            Collections.addAll(this.builder.items, items);
        } else {
            this.builder.items = null;
        }
        if (!(this.builder.adapter instanceof DefaultRvAdapter)) {
            throw new IllegalStateException("When using a custom adapter, setItems() cannot be used. Set items through the adapter instead.");
        }
        notifyItemsChanged();
    }

    @UiThread
    public final void notifyItemInserted(@IntRange(from = 0, to = 2147483647L) int index) {
        this.builder.adapter.notifyItemInserted(index);
    }

    @UiThread
    public final void notifyItemChanged(@IntRange(from = 0, to = 2147483647L) int index) {
        this.builder.adapter.notifyItemChanged(index);
    }

    @UiThread
    public final void notifyItemsChanged() {
        this.builder.adapter.notifyDataSetChanged();
    }

    public final int getCurrentProgress() {
        ProgressBar progressBar = this.progressBar;
        if (progressBar == null) {
            return -1;
        }
        return progressBar.getProgress();
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    public final void incrementProgress(int by) {
        setProgress(getCurrentProgress() + by);
    }

    public final void setProgress(int progress) {
        if (this.builder.progress <= -2) {
            Log.w("MaterialDialog", "Calling setProgress(int) on an indeterminate progress dialog has no effect!");
            return;
        }
        this.progressBar.setProgress(progress);
        this.handler.post(new Runnable() { // from class: com.afollestad.materialdialogs.MaterialDialog.2
            @Override // java.lang.Runnable
            public void run() {
                if (MaterialDialog.this.progressLabel != null) {
                    MaterialDialog.this.progressLabel.setText(MaterialDialog.this.builder.progressPercentFormat.format(MaterialDialog.this.getCurrentProgress() / MaterialDialog.this.getMaxProgress()));
                }
                if (MaterialDialog.this.progressMinMax != null) {
                    MaterialDialog.this.progressMinMax.setText(String.format(MaterialDialog.this.builder.progressNumberFormat, Integer.valueOf(MaterialDialog.this.getCurrentProgress()), Integer.valueOf(MaterialDialog.this.getMaxProgress())));
                }
            }
        });
    }

    public final boolean isIndeterminateProgress() {
        return this.builder.indeterminateProgress;
    }

    public final int getMaxProgress() {
        ProgressBar progressBar = this.progressBar;
        if (progressBar == null) {
            return -1;
        }
        return progressBar.getMax();
    }

    public final void setMaxProgress(int max) {
        if (this.builder.progress <= -2) {
            throw new IllegalStateException("Cannot use setMaxProgress() on this dialog.");
        }
        this.progressBar.setMax(max);
    }

    public final void setProgressPercentFormat(NumberFormat format) {
        this.builder.progressPercentFormat = format;
        setProgress(getCurrentProgress());
    }

    public final void setProgressNumberFormat(String format) {
        this.builder.progressNumberFormat = format;
        setProgress(getCurrentProgress());
    }

    public final boolean isCancelled() {
        return !isShowing();
    }

    public int getSelectedIndex() {
        if (this.builder.listCallbackSingleChoice != null) {
            return this.builder.selectedIndex;
        }
        return -1;
    }

    @UiThread
    public void setSelectedIndex(int index) {
        Builder builder = this.builder;
        builder.selectedIndex = index;
        if (builder.adapter != null && (this.builder.adapter instanceof DefaultRvAdapter)) {
            this.builder.adapter.notifyDataSetChanged();
            return;
        }
        throw new IllegalStateException("You can only use setSelectedIndex() with the default adapter implementation.");
    }

    @Nullable
    public Integer[] getSelectedIndices() {
        if (this.builder.listCallbackMultiChoice != null) {
            List<Integer> list = this.selectedIndicesList;
            return (Integer[]) list.toArray(new Integer[list.size()]);
        }
        return null;
    }

    @UiThread
    public void setSelectedIndices(@NonNull Integer[] indices) {
        this.selectedIndicesList = new ArrayList(Arrays.asList(indices));
        if (this.builder.adapter != null && (this.builder.adapter instanceof DefaultRvAdapter)) {
            this.builder.adapter.notifyDataSetChanged();
            return;
        }
        throw new IllegalStateException("You can only use setSelectedIndices() with the default adapter implementation.");
    }

    public void clearSelectedIndices() {
        clearSelectedIndices(true);
    }

    public void clearSelectedIndices(boolean sendCallback) {
        ListType listType = this.listType;
        if (listType == null || listType != ListType.MULTI) {
            throw new IllegalStateException("You can only use clearSelectedIndices() with multi choice list dialogs.");
        }
        if (this.builder.adapter != null && (this.builder.adapter instanceof DefaultRvAdapter)) {
            List<Integer> list = this.selectedIndicesList;
            if (list != null) {
                list.clear();
            }
            this.builder.adapter.notifyDataSetChanged();
            if (sendCallback && this.builder.listCallbackMultiChoice != null) {
                sendMultiChoiceCallback();
                return;
            }
            return;
        }
        throw new IllegalStateException("You can only use clearSelectedIndices() with the default adapter implementation.");
    }

    public void selectAllIndices() {
        selectAllIndices(true);
    }

    public void selectAllIndices(boolean sendCallback) {
        ListType listType = this.listType;
        if (listType == null || listType != ListType.MULTI) {
            throw new IllegalStateException("You can only use selectAllIndices() with multi choice list dialogs.");
        }
        if (this.builder.adapter != null && (this.builder.adapter instanceof DefaultRvAdapter)) {
            if (this.selectedIndicesList == null) {
                this.selectedIndicesList = new ArrayList();
            }
            for (int i = 0; i < this.builder.adapter.getItemCount(); i++) {
                if (!this.selectedIndicesList.contains(Integer.valueOf(i))) {
                    this.selectedIndicesList.add(Integer.valueOf(i));
                }
            }
            this.builder.adapter.notifyDataSetChanged();
            if (sendCallback && this.builder.listCallbackMultiChoice != null) {
                sendMultiChoiceCallback();
                return;
            }
            return;
        }
        throw new IllegalStateException("You can only use selectAllIndices() with the default adapter implementation.");
    }

    @Override // com.afollestad.materialdialogs.DialogBase, android.content.DialogInterface.OnShowListener
    public final void onShow(DialogInterface dialog) {
        if (this.input != null) {
            DialogUtils.showKeyboard(this, this.builder);
            if (this.input.getText().length() > 0) {
                EditText editText = this.input;
                editText.setSelection(editText.getText().length());
            }
        }
        super.onShow(dialog);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInternalInputCallback() {
        EditText editText = this.input;
        if (editText == null) {
            return;
        }
        editText.addTextChangedListener(new TextWatcher() { // from class: com.afollestad.materialdialogs.MaterialDialog.3
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.toString().length();
                boolean emptyDisabled = false;
                if (!MaterialDialog.this.builder.inputAllowEmpty) {
                    boolean z = true;
                    emptyDisabled = length == 0;
                    View positiveAb = MaterialDialog.this.getActionButton(DialogAction.POSITIVE);
                    if (emptyDisabled) {
                        z = false;
                    }
                    positiveAb.setEnabled(z);
                }
                MaterialDialog.this.invalidateInputMinMaxIndicator(length, emptyDisabled);
                if (MaterialDialog.this.builder.alwaysCallInputCallback) {
                    MaterialDialog.this.builder.inputCallback.onInput(MaterialDialog.this, s);
                }
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateInputMinMaxIndicator(int currentLength, boolean emptyDisabled) {
        if (this.inputMinMax != null) {
            boolean z = true;
            if (this.builder.inputMaxLength > 0) {
                this.inputMinMax.setText(String.format(Locale.getDefault(), "%d/%d", Integer.valueOf(currentLength), Integer.valueOf(this.builder.inputMaxLength)));
                this.inputMinMax.setVisibility(0);
            } else {
                this.inputMinMax.setVisibility(8);
            }
            boolean isDisabled = (emptyDisabled && currentLength == 0) || (this.builder.inputMaxLength > 0 && currentLength > this.builder.inputMaxLength) || currentLength < this.builder.inputMinLength;
            int colorText = isDisabled ? this.builder.inputRangeErrorColor : this.builder.contentColor;
            int colorWidget = isDisabled ? this.builder.inputRangeErrorColor : this.builder.widgetColor;
            if (this.builder.inputMaxLength > 0) {
                this.inputMinMax.setTextColor(colorText);
            }
            MDTintHelper.setTint(this.input, colorWidget);
            View positiveAb = getActionButton(DialogAction.POSITIVE);
            if (isDisabled) {
                z = false;
            }
            positiveAb.setEnabled(z);
        }
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        if (this.input != null) {
            DialogUtils.hideKeyboard(this, this.builder);
        }
        super.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ListType {
        REGULAR,
        SINGLE,
        MULTI;

        public static int getLayoutForType(ListType type) {
            switch (type) {
                case REGULAR:
                    return R.layout.md_listitem;
                case SINGLE:
                    return R.layout.md_listitem_singlechoice;
                case MULTI:
                    return R.layout.md_listitem_multichoice;
                default:
                    throw new IllegalArgumentException("Not a valid list type");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DialogException extends WindowManager.BadTokenException {
        DialogException(String message) {
            super(message);
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        protected RecyclerView.Adapter<?> adapter;
        protected boolean alwaysCallInputCallback;
        protected int backgroundColor;
        @DrawableRes
        protected int btnSelectorNegative;
        @DrawableRes
        protected int btnSelectorNeutral;
        @DrawableRes
        protected int btnSelectorPositive;
        @DrawableRes
        protected int btnSelectorStacked;
        protected GravityEnum btnStackedGravity;
        protected int buttonRippleColor;
        protected GravityEnum buttonsGravity;
        protected ButtonCallback callback;
        protected DialogInterface.OnCancelListener cancelListener;
        protected CharSequence checkBoxPrompt;
        protected boolean checkBoxPromptInitiallyChecked;
        protected CompoundButton.OnCheckedChangeListener checkBoxPromptListener;
        protected ColorStateList choiceWidgetColor;
        protected CharSequence content;
        protected GravityEnum contentGravity;
        protected final Context context;
        protected View customView;
        protected DialogInterface.OnDismissListener dismissListener;
        protected int dividerColor;
        protected Drawable icon;
        protected boolean indeterminateIsHorizontalProgress;
        protected boolean indeterminateProgress;
        protected boolean inputAllowEmpty;
        protected InputCallback inputCallback;
        protected CharSequence inputHint;
        protected CharSequence inputPrefill;
        protected int itemColor;
        protected int[] itemIds;
        protected ArrayList<CharSequence> items;
        protected GravityEnum itemsGravity;
        protected DialogInterface.OnKeyListener keyListener;
        protected RecyclerView.LayoutManager layoutManager;
        protected boolean limitIconToDefaultSize;
        protected ColorStateList linkColor;
        protected ListCallback listCallback;
        protected ListCallbackMultiChoice listCallbackMultiChoice;
        protected ListCallbackSingleChoice listCallbackSingleChoice;
        protected ListLongCallback listLongCallback;
        @DrawableRes
        protected int listSelector;
        protected Typeface mediumFont;
        protected ColorStateList negativeColor;
        protected boolean negativeFocus;
        protected CharSequence negativeText;
        protected ColorStateList neutralColor;
        protected boolean neutralFocus;
        protected CharSequence neutralText;
        protected SingleButtonCallback onAnyCallback;
        protected SingleButtonCallback onNegativeCallback;
        protected SingleButtonCallback onNeutralCallback;
        protected SingleButtonCallback onPositiveCallback;
        protected ColorStateList positiveColor;
        protected boolean positiveFocus;
        protected CharSequence positiveText;
        protected String progressNumberFormat;
        protected NumberFormat progressPercentFormat;
        protected Typeface regularFont;
        protected DialogInterface.OnShowListener showListener;
        protected boolean showMinMax;
        protected StackingBehavior stackingBehavior;
        protected Object tag;
        protected Theme theme;
        protected CharSequence title;
        protected GravityEnum titleGravity;
        protected int widgetColor;
        protected boolean wrapCustomViewInScroll;
        protected int titleColor = -1;
        protected int contentColor = -1;
        protected boolean alwaysCallMultiChoiceCallback = false;
        protected boolean alwaysCallSingleChoiceCallback = false;
        protected boolean cancelable = true;
        protected boolean canceledOnTouchOutside = true;
        protected float contentLineSpacingMultiplier = 1.2f;
        protected int selectedIndex = -1;
        protected Integer[] selectedIndices = null;
        protected Integer[] disabledIndices = null;
        protected boolean autoDismiss = true;
        protected int maxIconSize = -1;
        protected int progress = -2;
        protected int progressMax = 0;
        protected int inputType = -1;
        protected int inputMinLength = -1;
        protected int inputMaxLength = -1;
        protected int inputRangeErrorColor = 0;
        protected boolean titleColorSet = false;
        protected boolean contentColorSet = false;
        protected boolean itemColorSet = false;
        protected boolean positiveColorSet = false;
        protected boolean neutralColorSet = false;
        protected boolean negativeColorSet = false;
        protected boolean widgetColorSet = false;
        protected boolean dividerColorSet = false;

        public Builder(@NonNull Context context) {
            this.titleGravity = GravityEnum.START;
            this.contentGravity = GravityEnum.START;
            this.btnStackedGravity = GravityEnum.END;
            this.itemsGravity = GravityEnum.START;
            this.buttonsGravity = GravityEnum.START;
            this.buttonRippleColor = 0;
            this.theme = Theme.LIGHT;
            this.context = context;
            int materialBlue = DialogUtils.getColor(context, R.color.md_material_blue_600);
            this.widgetColor = DialogUtils.resolveColor(context, R.attr.colorAccent, materialBlue);
            if (Build.VERSION.SDK_INT >= 21) {
                this.widgetColor = DialogUtils.resolveColor(context, 16843829, this.widgetColor);
            }
            this.positiveColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
            this.negativeColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
            this.neutralColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
            this.linkColor = DialogUtils.getActionTextStateList(context, DialogUtils.resolveColor(context, R.attr.md_link_color, this.widgetColor));
            int fallback = 0;
            this.buttonRippleColor = DialogUtils.resolveColor(context, R.attr.md_btn_ripple_color, DialogUtils.resolveColor(context, R.attr.colorControlHighlight, Build.VERSION.SDK_INT >= 21 ? DialogUtils.resolveColor(context, 16843820) : fallback));
            this.progressPercentFormat = NumberFormat.getPercentInstance();
            this.progressNumberFormat = "%1d/%2d";
            int primaryTextColor = DialogUtils.resolveColor(context, 16842806);
            this.theme = DialogUtils.isColorDark(primaryTextColor) ? Theme.LIGHT : Theme.DARK;
            checkSingleton();
            this.titleGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_title_gravity, this.titleGravity);
            this.contentGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_content_gravity, this.contentGravity);
            this.btnStackedGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_btnstacked_gravity, this.btnStackedGravity);
            this.itemsGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_items_gravity, this.itemsGravity);
            this.buttonsGravity = DialogUtils.resolveGravityEnum(context, R.attr.md_buttons_gravity, this.buttonsGravity);
            String mediumFont = DialogUtils.resolveString(context, R.attr.md_medium_font);
            String regularFont = DialogUtils.resolveString(context, R.attr.md_regular_font);
            try {
                typeface(mediumFont, regularFont);
            } catch (Throwable th) {
            }
            if (this.mediumFont == null) {
                try {
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.mediumFont = Typeface.create("sans-serif-medium", 0);
                    } else {
                        this.mediumFont = Typeface.create("sans-serif", 1);
                    }
                } catch (Throwable th2) {
                    this.mediumFont = Typeface.DEFAULT_BOLD;
                }
            }
            if (this.regularFont == null) {
                try {
                    this.regularFont = Typeface.create("sans-serif", 0);
                } catch (Throwable th3) {
                    this.regularFont = Typeface.SANS_SERIF;
                    if (this.regularFont == null) {
                        this.regularFont = Typeface.DEFAULT;
                    }
                }
            }
        }

        public final Context getContext() {
            return this.context;
        }

        public final int getItemColor() {
            return this.itemColor;
        }

        public final Typeface getRegularFont() {
            return this.regularFont;
        }

        private void checkSingleton() {
            if (ThemeSingleton.get(false) == null) {
                return;
            }
            ThemeSingleton s = ThemeSingleton.get();
            if (s.darkTheme) {
                this.theme = Theme.DARK;
            }
            if (s.titleColor != 0) {
                this.titleColor = s.titleColor;
            }
            if (s.contentColor != 0) {
                this.contentColor = s.contentColor;
            }
            if (s.positiveColor != null) {
                this.positiveColor = s.positiveColor;
            }
            if (s.neutralColor != null) {
                this.neutralColor = s.neutralColor;
            }
            if (s.negativeColor != null) {
                this.negativeColor = s.negativeColor;
            }
            if (s.itemColor != 0) {
                this.itemColor = s.itemColor;
            }
            if (s.icon != null) {
                this.icon = s.icon;
            }
            if (s.backgroundColor != 0) {
                this.backgroundColor = s.backgroundColor;
            }
            if (s.dividerColor != 0) {
                this.dividerColor = s.dividerColor;
            }
            if (s.btnSelectorStacked != 0) {
                this.btnSelectorStacked = s.btnSelectorStacked;
            }
            if (s.listSelector != 0) {
                this.listSelector = s.listSelector;
            }
            if (s.btnSelectorPositive != 0) {
                this.btnSelectorPositive = s.btnSelectorPositive;
            }
            if (s.btnSelectorNeutral != 0) {
                this.btnSelectorNeutral = s.btnSelectorNeutral;
            }
            if (s.btnSelectorNegative != 0) {
                this.btnSelectorNegative = s.btnSelectorNegative;
            }
            if (s.widgetColor != 0) {
                this.widgetColor = s.widgetColor;
            }
            if (s.linkColor != null) {
                this.linkColor = s.linkColor;
            }
            this.titleGravity = s.titleGravity;
            this.contentGravity = s.contentGravity;
            this.btnStackedGravity = s.btnStackedGravity;
            this.itemsGravity = s.itemsGravity;
            this.buttonsGravity = s.buttonsGravity;
        }

        public Builder title(@StringRes int titleRes) {
            title(this.context.getText(titleRes));
            return this;
        }

        public Builder title(@NonNull CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder titleGravity(@NonNull GravityEnum gravity) {
            this.titleGravity = gravity;
            return this;
        }

        public Builder buttonRippleColor(@ColorInt int color) {
            this.buttonRippleColor = color;
            return this;
        }

        public Builder buttonRippleColorRes(@ColorRes int colorRes) {
            return buttonRippleColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder buttonRippleColorAttr(@AttrRes int colorAttr) {
            return buttonRippleColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder titleColor(@ColorInt int color) {
            this.titleColor = color;
            this.titleColorSet = true;
            return this;
        }

        public Builder titleColorRes(@ColorRes int colorRes) {
            return titleColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder titleColorAttr(@AttrRes int colorAttr) {
            return titleColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder typeface(@Nullable Typeface medium, @Nullable Typeface regular) {
            this.mediumFont = medium;
            this.regularFont = regular;
            return this;
        }

        public Builder typeface(@Nullable String medium, @Nullable String regular) {
            if (medium != null && !medium.trim().isEmpty()) {
                this.mediumFont = TypefaceHelper.get(this.context, medium);
                if (this.mediumFont == null) {
                    throw new IllegalArgumentException("No font asset found for \"" + medium + "\"");
                }
            }
            if (regular != null && !regular.trim().isEmpty()) {
                this.regularFont = TypefaceHelper.get(this.context, regular);
                if (this.regularFont == null) {
                    throw new IllegalArgumentException("No font asset found for \"" + regular + "\"");
                }
            }
            return this;
        }

        public Builder icon(@NonNull Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder iconRes(@DrawableRes int icon) {
            this.icon = ResourcesCompat.getDrawable(this.context.getResources(), icon, null);
            return this;
        }

        public Builder iconAttr(@AttrRes int iconAttr) {
            this.icon = DialogUtils.resolveDrawable(this.context, iconAttr);
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            return content(contentRes, false);
        }

        public Builder content(@StringRes int contentRes, boolean html) {
            CharSequence text = this.context.getText(contentRes);
            if (html) {
                text = Html.fromHtml(text.toString().replace("\n", "<br/>"));
            }
            return content(text);
        }

        public Builder content(@NonNull CharSequence content) {
            if (this.customView != null) {
                throw new IllegalStateException("You cannot set content() when you're using a custom view.");
            }
            this.content = content;
            return this;
        }

        public Builder content(@StringRes int contentRes, Object... formatArgs) {
            String str = String.format(this.context.getString(contentRes), formatArgs).replace("\n", "<br/>");
            return content(Html.fromHtml(str));
        }

        public Builder contentColor(@ColorInt int color) {
            this.contentColor = color;
            this.contentColorSet = true;
            return this;
        }

        public Builder contentColorRes(@ColorRes int colorRes) {
            contentColor(DialogUtils.getColor(this.context, colorRes));
            return this;
        }

        public Builder contentColorAttr(@AttrRes int colorAttr) {
            contentColor(DialogUtils.resolveColor(this.context, colorAttr));
            return this;
        }

        public Builder contentGravity(@NonNull GravityEnum gravity) {
            this.contentGravity = gravity;
            return this;
        }

        public Builder contentLineSpacing(float multiplier) {
            this.contentLineSpacingMultiplier = multiplier;
            return this;
        }

        public Builder items(@NonNull Collection collection) {
            if (collection.size() > 0) {
                CharSequence[] array = new CharSequence[collection.size()];
                int i = 0;
                for (Object obj : collection) {
                    array[i] = obj.toString();
                    i++;
                }
                items(array);
            } else if (collection.size() == 0) {
                this.items = new ArrayList<>();
            }
            return this;
        }

        public Builder items(@ArrayRes int itemsRes) {
            items(this.context.getResources().getTextArray(itemsRes));
            return this;
        }

        public Builder items(@NonNull CharSequence... items) {
            if (this.customView != null) {
                throw new IllegalStateException("You cannot set items() when you're using a custom view.");
            }
            this.items = new ArrayList<>();
            Collections.addAll(this.items, items);
            return this;
        }

        public Builder itemsCallback(@NonNull ListCallback callback) {
            this.listCallback = callback;
            this.listCallbackSingleChoice = null;
            this.listCallbackMultiChoice = null;
            return this;
        }

        public Builder itemsLongCallback(@NonNull ListLongCallback callback) {
            this.listLongCallback = callback;
            this.listCallbackSingleChoice = null;
            this.listCallbackMultiChoice = null;
            return this;
        }

        public Builder itemsColor(@ColorInt int color) {
            this.itemColor = color;
            this.itemColorSet = true;
            return this;
        }

        public Builder itemsColorRes(@ColorRes int colorRes) {
            return itemsColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder itemsColorAttr(@AttrRes int colorAttr) {
            return itemsColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder itemsGravity(@NonNull GravityEnum gravity) {
            this.itemsGravity = gravity;
            return this;
        }

        public Builder itemsIds(@NonNull int[] idsArray) {
            this.itemIds = idsArray;
            return this;
        }

        public Builder itemsIds(@ArrayRes int idsArrayRes) {
            return itemsIds(this.context.getResources().getIntArray(idsArrayRes));
        }

        public Builder buttonsGravity(@NonNull GravityEnum gravity) {
            this.buttonsGravity = gravity;
            return this;
        }

        public Builder itemsCallbackSingleChoice(int selectedIndex, @NonNull ListCallbackSingleChoice callback) {
            this.selectedIndex = selectedIndex;
            this.listCallback = null;
            this.listCallbackSingleChoice = callback;
            this.listCallbackMultiChoice = null;
            return this;
        }

        public Builder alwaysCallSingleChoiceCallback() {
            this.alwaysCallSingleChoiceCallback = true;
            return this;
        }

        public Builder itemsCallbackMultiChoice(@Nullable Integer[] selectedIndices, @NonNull ListCallbackMultiChoice callback) {
            this.selectedIndices = selectedIndices;
            this.listCallback = null;
            this.listCallbackSingleChoice = null;
            this.listCallbackMultiChoice = callback;
            return this;
        }

        public Builder itemsDisabledIndices(@Nullable Integer... disabledIndices) {
            this.disabledIndices = disabledIndices;
            return this;
        }

        public Builder alwaysCallMultiChoiceCallback() {
            this.alwaysCallMultiChoiceCallback = true;
            return this;
        }

        public Builder positiveText(@StringRes int positiveRes) {
            if (positiveRes == 0) {
                return this;
            }
            positiveText(this.context.getText(positiveRes));
            return this;
        }

        public Builder positiveText(@NonNull CharSequence message) {
            this.positiveText = message;
            return this;
        }

        public Builder positiveColor(@ColorInt int color) {
            return positiveColor(DialogUtils.getActionTextStateList(this.context, color));
        }

        public Builder positiveColorRes(@ColorRes int colorRes) {
            return positiveColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
        }

        public Builder positiveColorAttr(@AttrRes int colorAttr) {
            return positiveColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
        }

        public Builder positiveColor(@NonNull ColorStateList colorStateList) {
            this.positiveColor = colorStateList;
            this.positiveColorSet = true;
            return this;
        }

        public Builder positiveFocus(boolean isFocusedDefault) {
            this.positiveFocus = isFocusedDefault;
            return this;
        }

        public Builder neutralText(@StringRes int neutralRes) {
            if (neutralRes == 0) {
                return this;
            }
            return neutralText(this.context.getText(neutralRes));
        }

        public Builder neutralText(@NonNull CharSequence message) {
            this.neutralText = message;
            return this;
        }

        public Builder negativeColor(@ColorInt int color) {
            return negativeColor(DialogUtils.getActionTextStateList(this.context, color));
        }

        public Builder negativeColorRes(@ColorRes int colorRes) {
            return negativeColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
        }

        public Builder negativeColorAttr(@AttrRes int colorAttr) {
            return negativeColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
        }

        public Builder negativeColor(@NonNull ColorStateList colorStateList) {
            this.negativeColor = colorStateList;
            this.negativeColorSet = true;
            return this;
        }

        public Builder negativeText(@StringRes int negativeRes) {
            if (negativeRes == 0) {
                return this;
            }
            return negativeText(this.context.getText(negativeRes));
        }

        public Builder negativeText(@NonNull CharSequence message) {
            this.negativeText = message;
            return this;
        }

        public Builder negativeFocus(boolean isFocusedDefault) {
            this.negativeFocus = isFocusedDefault;
            return this;
        }

        public Builder neutralColor(@ColorInt int color) {
            return neutralColor(DialogUtils.getActionTextStateList(this.context, color));
        }

        public Builder neutralColorRes(@ColorRes int colorRes) {
            return neutralColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
        }

        public Builder neutralColorAttr(@AttrRes int colorAttr) {
            return neutralColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
        }

        public Builder neutralColor(@NonNull ColorStateList colorStateList) {
            this.neutralColor = colorStateList;
            this.neutralColorSet = true;
            return this;
        }

        public Builder neutralFocus(boolean isFocusedDefault) {
            this.neutralFocus = isFocusedDefault;
            return this;
        }

        public Builder linkColor(@ColorInt int color) {
            return linkColor(DialogUtils.getActionTextStateList(this.context, color));
        }

        public Builder linkColorRes(@ColorRes int colorRes) {
            return linkColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
        }

        public Builder linkColorAttr(@AttrRes int colorAttr) {
            return linkColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
        }

        public Builder linkColor(@NonNull ColorStateList colorStateList) {
            this.linkColor = colorStateList;
            return this;
        }

        public Builder listSelector(@DrawableRes int selectorRes) {
            this.listSelector = selectorRes;
            return this;
        }

        public Builder btnSelectorStacked(@DrawableRes int selectorRes) {
            this.btnSelectorStacked = selectorRes;
            return this;
        }

        public Builder btnSelector(@DrawableRes int selectorRes) {
            this.btnSelectorPositive = selectorRes;
            this.btnSelectorNeutral = selectorRes;
            this.btnSelectorNegative = selectorRes;
            return this;
        }

        public Builder btnSelector(@DrawableRes int selectorRes, @NonNull DialogAction which) {
            switch (which) {
                case NEUTRAL:
                    this.btnSelectorNeutral = selectorRes;
                    break;
                case NEGATIVE:
                    this.btnSelectorNegative = selectorRes;
                    break;
                default:
                    this.btnSelectorPositive = selectorRes;
                    break;
            }
            return this;
        }

        public Builder btnStackedGravity(@NonNull GravityEnum gravity) {
            this.btnStackedGravity = gravity;
            return this;
        }

        public Builder checkBoxPrompt(@NonNull CharSequence prompt, boolean initiallyChecked, @Nullable CompoundButton.OnCheckedChangeListener checkListener) {
            this.checkBoxPrompt = prompt;
            this.checkBoxPromptInitiallyChecked = initiallyChecked;
            this.checkBoxPromptListener = checkListener;
            return this;
        }

        public Builder checkBoxPromptRes(@StringRes int prompt, boolean initiallyChecked, @Nullable CompoundButton.OnCheckedChangeListener checkListener) {
            return checkBoxPrompt(this.context.getResources().getText(prompt), initiallyChecked, checkListener);
        }

        public Builder customView(@LayoutRes int layoutRes, boolean wrapInScrollView) {
            LayoutInflater li = LayoutInflater.from(this.context);
            return customView(li.inflate(layoutRes, (ViewGroup) null), wrapInScrollView);
        }

        public Builder customView(@NonNull View view, boolean wrapInScrollView) {
            if (this.content != null) {
                throw new IllegalStateException("You cannot use customView() when you have content set.");
            }
            if (this.items != null) {
                throw new IllegalStateException("You cannot use customView() when you have items set.");
            }
            if (this.inputCallback != null) {
                throw new IllegalStateException("You cannot use customView() with an input dialog");
            }
            if (this.progress > -2 || this.indeterminateProgress) {
                throw new IllegalStateException("You cannot use customView() with a progress dialog");
            }
            if (view.getParent() != null && (view.getParent() instanceof ViewGroup)) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            this.customView = view;
            this.wrapCustomViewInScroll = wrapInScrollView;
            return this;
        }

        public Builder progress(boolean indeterminate, int max) {
            if (this.customView != null) {
                throw new IllegalStateException("You cannot set progress() when you're using a custom view.");
            }
            if (indeterminate) {
                this.indeterminateProgress = true;
                this.progress = -2;
            } else {
                this.indeterminateIsHorizontalProgress = false;
                this.indeterminateProgress = false;
                this.progress = -1;
                this.progressMax = max;
            }
            return this;
        }

        public Builder progress(boolean indeterminate, int max, boolean showMinMax) {
            this.showMinMax = showMinMax;
            return progress(indeterminate, max);
        }

        public Builder progressNumberFormat(@NonNull String format) {
            this.progressNumberFormat = format;
            return this;
        }

        public Builder progressPercentFormat(@NonNull NumberFormat format) {
            this.progressPercentFormat = format;
            return this;
        }

        public Builder progressIndeterminateStyle(boolean horizontal) {
            this.indeterminateIsHorizontalProgress = horizontal;
            return this;
        }

        public Builder widgetColor(@ColorInt int color) {
            this.widgetColor = color;
            this.widgetColorSet = true;
            return this;
        }

        public Builder widgetColorRes(@ColorRes int colorRes) {
            return widgetColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder widgetColorAttr(@AttrRes int colorAttr) {
            return widgetColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder choiceWidgetColor(@Nullable ColorStateList colorStateList) {
            this.choiceWidgetColor = colorStateList;
            return this;
        }

        public Builder dividerColor(@ColorInt int color) {
            this.dividerColor = color;
            this.dividerColorSet = true;
            return this;
        }

        public Builder dividerColorRes(@ColorRes int colorRes) {
            return dividerColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder dividerColorAttr(@AttrRes int colorAttr) {
            return dividerColor(DialogUtils.resolveColor(this.context, colorAttr));
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

        public Builder callback(@NonNull ButtonCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder onPositive(@NonNull SingleButtonCallback callback) {
            this.onPositiveCallback = callback;
            return this;
        }

        public Builder onNegative(@NonNull SingleButtonCallback callback) {
            this.onNegativeCallback = callback;
            return this;
        }

        public Builder onNeutral(@NonNull SingleButtonCallback callback) {
            this.onNeutralCallback = callback;
            return this;
        }

        public Builder onAny(@NonNull SingleButtonCallback callback) {
            this.onAnyCallback = callback;
            return this;
        }

        public Builder theme(@NonNull Theme theme) {
            this.theme = theme;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            this.canceledOnTouchOutside = cancelable;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder autoDismiss(boolean dismiss) {
            this.autoDismiss = dismiss;
            return this;
        }

        public Builder adapter(@NonNull RecyclerView.Adapter<?> adapter, @Nullable RecyclerView.LayoutManager layoutManager) {
            if (this.customView != null) {
                throw new IllegalStateException("You cannot set adapter() when you're using a custom view.");
            }
            if (layoutManager != null && !(layoutManager instanceof LinearLayoutManager) && !(layoutManager instanceof GridLayoutManager)) {
                throw new IllegalStateException("You can currently only use LinearLayoutManager and GridLayoutManager with this library.");
            }
            this.adapter = adapter;
            this.layoutManager = layoutManager;
            return this;
        }

        public Builder limitIconToDefaultSize() {
            this.limitIconToDefaultSize = true;
            return this;
        }

        public Builder maxIconSize(int maxIconSize) {
            this.maxIconSize = maxIconSize;
            return this;
        }

        public Builder maxIconSizeRes(@DimenRes int maxIconSizeRes) {
            return maxIconSize((int) this.context.getResources().getDimension(maxIconSizeRes));
        }

        public Builder showListener(@NonNull DialogInterface.OnShowListener listener) {
            this.showListener = listener;
            return this;
        }

        public Builder dismissListener(@NonNull DialogInterface.OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder cancelListener(@NonNull DialogInterface.OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        public Builder keyListener(@NonNull DialogInterface.OnKeyListener listener) {
            this.keyListener = listener;
            return this;
        }

        public Builder stackingBehavior(@NonNull StackingBehavior behavior) {
            this.stackingBehavior = behavior;
            return this;
        }

        public Builder input(@Nullable CharSequence hint, @Nullable CharSequence prefill, boolean allowEmptyInput, @NonNull InputCallback callback) {
            if (this.customView != null) {
                throw new IllegalStateException("You cannot set content() when you're using a custom view.");
            }
            this.inputCallback = callback;
            this.inputHint = hint;
            this.inputPrefill = prefill;
            this.inputAllowEmpty = allowEmptyInput;
            return this;
        }

        public Builder input(@Nullable CharSequence hint, @Nullable CharSequence prefill, @NonNull InputCallback callback) {
            return input(hint, prefill, true, callback);
        }

        public Builder input(@StringRes int hint, @StringRes int prefill, boolean allowEmptyInput, @NonNull InputCallback callback) {
            CharSequence text;
            CharSequence charSequence = null;
            if (hint == 0) {
                text = null;
            } else {
                text = this.context.getText(hint);
            }
            if (prefill != 0) {
                charSequence = this.context.getText(prefill);
            }
            return input(text, charSequence, allowEmptyInput, callback);
        }

        public Builder input(@StringRes int hint, @StringRes int prefill, @NonNull InputCallback callback) {
            return input(hint, prefill, true, callback);
        }

        public Builder inputType(int type) {
            this.inputType = type;
            return this;
        }

        public Builder inputRange(@IntRange(from = 0, to = 2147483647L) int minLength, @IntRange(from = -1, to = 2147483647L) int maxLength) {
            return inputRange(minLength, maxLength, 0);
        }

        public Builder inputRange(@IntRange(from = 0, to = 2147483647L) int minLength, @IntRange(from = -1, to = 2147483647L) int maxLength, @ColorInt int errorColor) {
            if (minLength < 0) {
                throw new IllegalArgumentException("Min length for input dialogs cannot be less than 0.");
            }
            this.inputMinLength = minLength;
            this.inputMaxLength = maxLength;
            if (errorColor == 0) {
                this.inputRangeErrorColor = DialogUtils.getColor(this.context, R.color.md_edittext_error);
            } else {
                this.inputRangeErrorColor = errorColor;
            }
            if (this.inputMinLength > 0) {
                this.inputAllowEmpty = false;
            }
            return this;
        }

        public Builder inputRangeRes(@IntRange(from = 0, to = 2147483647L) int minLength, @IntRange(from = -1, to = 2147483647L) int maxLength, @ColorRes int errorColor) {
            return inputRange(minLength, maxLength, DialogUtils.getColor(this.context, errorColor));
        }

        public Builder alwaysCallInputCallback() {
            this.alwaysCallInputCallback = true;
            return this;
        }

        public Builder tag(@Nullable Object tag) {
            this.tag = tag;
            return this;
        }

        @UiThread
        public MaterialDialog build() {
            return new MaterialDialog(this);
        }

        @UiThread
        public MaterialDialog show() {
            MaterialDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }

    @Deprecated
    /* loaded from: classes.dex */
    public static abstract class ButtonCallback {
        @Deprecated
        public void onAny(MaterialDialog dialog) {
        }

        @Deprecated
        public void onPositive(MaterialDialog dialog) {
        }

        @Deprecated
        public void onNegative(MaterialDialog dialog) {
        }

        @Deprecated
        public void onNeutral(MaterialDialog dialog) {
        }

        protected final Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public final boolean equals(Object o) {
            return super.equals(o);
        }

        protected final void finalize() throws Throwable {
            super.finalize();
        }

        public final int hashCode() {
            return super.hashCode();
        }

        public final String toString() {
            return super.toString();
        }
    }
}
