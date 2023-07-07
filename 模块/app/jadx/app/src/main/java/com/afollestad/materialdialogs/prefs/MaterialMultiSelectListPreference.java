package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@TargetApi(11)
/* loaded from: classes.dex */
public class MaterialMultiSelectListPreference extends MultiSelectListPreference {
    private Context context;
    private MaterialDialog mDialog;

    public MaterialMultiSelectListPreference(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override // android.preference.MultiSelectListPreference
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        MaterialDialog materialDialog = this.mDialog;
        if (materialDialog != null) {
            materialDialog.setItems(entries);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        PrefUtil.setLayoutResource(context, this, attrs);
        if (Build.VERSION.SDK_INT <= 10) {
            setWidgetLayoutResource(0);
        }
    }

    @Override // android.preference.DialogPreference
    public Dialog getDialog() {
        return this.mDialog;
    }

    @Override // android.preference.DialogPreference
    protected void showDialog(Bundle state) {
        List<Integer> indices = new ArrayList<>();
        for (String s : getValues()) {
            int index = findIndexOfValue(s);
            if (index >= 0) {
                indices.add(Integer.valueOf(findIndexOfValue(s)));
            }
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this.context).title(getDialogTitle()).icon(getDialogIcon()).negativeText(getNegativeButtonText()).positiveText(getPositiveButtonText()).onAny(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference.2
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                switch (AnonymousClass3.$SwitchMap$com$afollestad$materialdialogs$DialogAction[which.ordinal()]) {
                    case 1:
                        MaterialMultiSelectListPreference.this.onClick(dialog, -3);
                        return;
                    case 2:
                        MaterialMultiSelectListPreference.this.onClick(dialog, -2);
                        return;
                    default:
                        MaterialMultiSelectListPreference.this.onClick(dialog, -1);
                        return;
                }
            }
        }).items(getEntries()).itemsCallbackMultiChoice((Integer[]) indices.toArray(new Integer[indices.size()]), new MaterialDialog.ListCallbackMultiChoice() { // from class: com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference.1
            @Override // com.afollestad.materialdialogs.MaterialDialog.ListCallbackMultiChoice
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                MaterialMultiSelectListPreference.this.onClick(null, -1);
                dialog.dismiss();
                Set<String> values = new HashSet<>();
                for (Integer num : which) {
                    int i = num.intValue();
                    values.add(MaterialMultiSelectListPreference.this.getEntryValues()[i].toString());
                }
                if (MaterialMultiSelectListPreference.this.callChangeListener(values)) {
                    MaterialMultiSelectListPreference.this.setValues(values);
                    return true;
                }
                return true;
            }
        }).dismissListener(this);
        View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }
        PrefUtil.registerOnActivityDestroyListener(this, this);
        this.mDialog = builder.build();
        if (state != null) {
            this.mDialog.onRestoreInstanceState(state);
        }
        this.mDialog.show();
    }

    /* renamed from: com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference$3  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$afollestad$materialdialogs$DialogAction = new int[DialogAction.values().length];

        static {
            try {
                $SwitchMap$com$afollestad$materialdialogs$DialogAction[DialogAction.NEUTRAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$afollestad$materialdialogs$DialogAction[DialogAction.NEGATIVE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    @Override // android.preference.DialogPreference, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        PrefUtil.unregisterOnActivityDestroyListener(this, this);
    }

    @Override // android.preference.DialogPreference, android.preference.PreferenceManager.OnActivityDestroyListener
    public void onActivityDestroy() {
        super.onActivityDestroy();
        MaterialDialog materialDialog = this.mDialog;
        if (materialDialog != null && materialDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }

    @Override // android.preference.MultiSelectListPreference, android.preference.DialogPreference, android.preference.Preference
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Dialog dialog = getDialog();
        if (dialog == null || !dialog.isShowing()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = dialog.onSaveInstanceState();
        return myState;
    }

    @Override // android.preference.DialogPreference, android.preference.Preference
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            /* renamed from: createFromParcel */
            public SavedState mo193createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            /* renamed from: newArray */
            public SavedState[] mo194newArray(int size) {
                return new SavedState[size];
            }
        };
        Bundle dialogBundle;
        boolean isDialogShowing;

        SavedState(Parcel source) {
            super(source);
            this.isDialogShowing = source.readInt() != 1 ? false : true;
            this.dialogBundle = source.readBundle();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.isDialogShowing ? 1 : 0);
            dest.writeBundle(this.dialogBundle);
        }
    }
}
