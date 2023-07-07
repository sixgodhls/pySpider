package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.p003v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class MaterialListPreference extends ListPreference {
    private Context context;
    private MaterialDialog dialog;

    public MaterialListPreference(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        PrefUtil.setLayoutResource(context, this, attrs);
        if (Build.VERSION.SDK_INT <= 10) {
            setWidgetLayoutResource(0);
        }
    }

    @Override // android.preference.ListPreference
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        MaterialDialog materialDialog = this.dialog;
        if (materialDialog != null) {
            materialDialog.setItems(entries);
        }
    }

    @Override // android.preference.DialogPreference
    public Dialog getDialog() {
        return this.dialog;
    }

    public RecyclerView getRecyclerView() {
        if (getDialog() == null) {
            return null;
        }
        return ((MaterialDialog) getDialog()).getRecyclerView();
    }

    @Override // android.preference.DialogPreference
    protected void showDialog(Bundle state) {
        if (getEntries() == null || getEntryValues() == null) {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
        }
        int preselect = findIndexOfValue(getValue());
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this.context).title(getDialogTitle()).icon(getDialogIcon()).dismissListener(this).onAny(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.prefs.MaterialListPreference.2
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                switch (C06133.$SwitchMap$com$afollestad$materialdialogs$DialogAction[which.ordinal()]) {
                    case 1:
                        MaterialListPreference.this.onClick(dialog, -3);
                        return;
                    case 2:
                        MaterialListPreference.this.onClick(dialog, -2);
                        return;
                    default:
                        MaterialListPreference.this.onClick(dialog, -1);
                        return;
                }
            }
        }).negativeText(getNegativeButtonText()).items(getEntries()).autoDismiss(true).itemsCallbackSingleChoice(preselect, new MaterialDialog.ListCallbackSingleChoice() { // from class: com.afollestad.materialdialogs.prefs.MaterialListPreference.1
            @Override // com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                MaterialListPreference.this.onClick(null, -1);
                if (which >= 0 && MaterialListPreference.this.getEntryValues() != null) {
                    try {
                        Field clickedIndex = ListPreference.class.getDeclaredField("mClickedDialogEntryIndex");
                        clickedIndex.setAccessible(true);
                        clickedIndex.set(MaterialListPreference.this, Integer.valueOf(which));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }
        PrefUtil.registerOnActivityDestroyListener(this, this);
        this.dialog = builder.build();
        if (state != null) {
            this.dialog.onRestoreInstanceState(state);
        }
        onClick(this.dialog, -2);
        this.dialog.show();
    }

    /* renamed from: com.afollestad.materialdialogs.prefs.MaterialListPreference$3 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C06133 {
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
        MaterialDialog materialDialog = this.dialog;
        if (materialDialog != null && materialDialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    @Override // android.preference.ListPreference, android.preference.DialogPreference, android.preference.Preference
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

    @Override // android.preference.ListPreference, android.preference.DialogPreference, android.preference.Preference
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
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.afollestad.materialdialogs.prefs.MaterialListPreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            /* renamed from: createFromParcel */
            public SavedState mo253createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            /* renamed from: newArray */
            public SavedState[] mo254newArray(int size) {
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