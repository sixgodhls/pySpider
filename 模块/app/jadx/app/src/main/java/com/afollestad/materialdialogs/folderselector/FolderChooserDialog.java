package com.afollestad.materialdialogs.folderselector;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.commons.R;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class FolderChooserDialog extends DialogFragment implements MaterialDialog.ListCallback {
    private static final String DEFAULT_TAG = "[MD_FOLDER_SELECTOR]";
    private FolderCallback callback;
    private boolean canGoUp = false;
    private File[] parentContents;
    private File parentFolder;

    /* loaded from: classes.dex */
    public interface FolderCallback {
        void onFolderChooserDismissed(@NonNull FolderChooserDialog folderChooserDialog);

        void onFolderSelection(@NonNull FolderChooserDialog folderChooserDialog, @NonNull File file);
    }

    String[] getContentsArray() {
        File[] fileArr = this.parentContents;
        if (fileArr == null) {
            return this.canGoUp ? new String[]{getBuilder().goUpLabel} : new String[0];
        }
        int length = fileArr.length;
        boolean z = this.canGoUp;
        String[] results = new String[length + (z ? 1 : 0)];
        if (z) {
            results[0] = getBuilder().goUpLabel;
        }
        for (int i = 0; i < this.parentContents.length; i++) {
            results[this.canGoUp ? i + 1 : i] = this.parentContents[i].getName();
        }
        return results;
    }

    File[] listFiles() {
        File[] contents = this.parentFolder.listFiles();
        List<File> results = new ArrayList<>();
        if (contents == null) {
            return null;
        }
        for (File fi : contents) {
            if (fi.isDirectory()) {
                results.add(fi);
            }
        }
        Collections.sort(results, new FolderSorter());
        return (File[]) results.toArray(new File[results.size()]);
    }

    @Override // android.support.v4.app.DialogFragment
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return new MaterialDialog.Builder(getActivity()).title(R.string.md_error_label).content(R.string.md_storage_perm_error).positiveText(17039370).build();
        }
        if (getArguments() == null || !getArguments().containsKey("builder")) {
            throw new IllegalStateException("You must create a FolderChooserDialog using the Builder.");
        }
        if (!getArguments().containsKey("current_path")) {
            getArguments().putString("current_path", getBuilder().initialPath);
        }
        this.parentFolder = new File(getArguments().getString("current_path"));
        checkIfCanGoUp();
        this.parentContents = listFiles();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity()).typeface(getBuilder().mediumFont, getBuilder().regularFont).title(this.parentFolder.getAbsolutePath()).items(getContentsArray()).itemsCallback(this).onPositive(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.folderselector.FolderChooserDialog.2
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                FolderCallback folderCallback = FolderChooserDialog.this.callback;
                FolderChooserDialog folderChooserDialog = FolderChooserDialog.this;
                folderCallback.onFolderSelection(folderChooserDialog, folderChooserDialog.parentFolder);
            }
        }).onNegative(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.folderselector.FolderChooserDialog.1
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).autoDismiss(false).positiveText(getBuilder().chooseButton).negativeText(getBuilder().cancelButton);
        if (getBuilder().allowNewFolder) {
            builder.neutralText(getBuilder().newFolderButton);
            builder.onNeutral(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.folderselector.FolderChooserDialog.3
                @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    FolderChooserDialog.this.createNewFolder();
                }
            });
        }
        if ("/".equals(getBuilder().initialPath)) {
            this.canGoUp = false;
        }
        return builder.build();
    }

    @Override // android.support.v4.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        FolderCallback folderCallback = this.callback;
        if (folderCallback != null) {
            folderCallback.onFolderChooserDismissed(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createNewFolder() {
        new MaterialDialog.Builder(getActivity()).title(getBuilder().newFolderButton).input(0, 0, false, new MaterialDialog.InputCallback() { // from class: com.afollestad.materialdialogs.folderselector.FolderChooserDialog.4
            @Override // com.afollestad.materialdialogs.MaterialDialog.InputCallback
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                File newFi = new File(FolderChooserDialog.this.parentFolder, input.toString());
                if (newFi.mkdir()) {
                    FolderChooserDialog.this.reload();
                    return;
                }
                String msg = "Unable to create folder " + newFi.getAbsolutePath() + ", make sure you have the WRITE_EXTERNAL_STORAGE permission or root permissions.";
                Toast.makeText(FolderChooserDialog.this.getActivity(), msg, 1).show();
            }
        }).show();
    }

    @Override // com.afollestad.materialdialogs.MaterialDialog.ListCallback
    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence s) {
        boolean z = true;
        if (this.canGoUp && i == 0) {
            this.parentFolder = this.parentFolder.getParentFile();
            if (this.parentFolder.getAbsolutePath().equals("/storage/emulated")) {
                this.parentFolder = this.parentFolder.getParentFile();
            }
            if (this.parentFolder.getParent() == null) {
                z = false;
            }
            this.canGoUp = z;
        } else {
            this.parentFolder = this.parentContents[this.canGoUp ? i - 1 : i];
            this.canGoUp = true;
            if (this.parentFolder.getAbsolutePath().equals("/storage/emulated")) {
                this.parentFolder = Environment.getExternalStorageDirectory();
            }
        }
        reload();
    }

    private void checkIfCanGoUp() {
        try {
            boolean z = true;
            if (this.parentFolder.getPath().split("/").length <= 1) {
                z = false;
            }
            this.canGoUp = z;
        } catch (IndexOutOfBoundsException e) {
            this.canGoUp = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reload() {
        this.parentContents = listFiles();
        MaterialDialog dialog = (MaterialDialog) getDialog();
        dialog.setTitle(this.parentFolder.getAbsolutePath());
        getArguments().putString("current_path", this.parentFolder.getAbsolutePath());
        dialog.setItems(getContentsArray());
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callback = (FolderCallback) activity;
    }

    public void show(FragmentActivity context) {
        String tag = getBuilder().tag;
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null) {
            ((DialogFragment) frag).dismiss();
            context.getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }
        show(context.getSupportFragmentManager(), tag);
    }

    @NonNull
    private Builder getBuilder() {
        return (Builder) getArguments().getSerializable("builder");
    }

    /* loaded from: classes.dex */
    public static class Builder implements Serializable {
        boolean allowNewFolder;
        @NonNull
        final transient AppCompatActivity context;
        @Nullable
        String mediumFont;
        @StringRes
        int newFolderButton;
        @Nullable
        String regularFont;
        String tag;
        @StringRes
        int chooseButton = R.string.md_choose_label;
        @StringRes
        int cancelButton = 17039360;
        String goUpLabel = "...";
        String initialPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        public <ActivityType extends AppCompatActivity & FolderCallback> Builder(@NonNull ActivityType context) {
            this.context = context;
        }

        @NonNull
        public Builder typeface(@Nullable String medium, @Nullable String regular) {
            this.mediumFont = medium;
            this.regularFont = regular;
            return this;
        }

        @NonNull
        public Builder chooseButton(@StringRes int text) {
            this.chooseButton = text;
            return this;
        }

        @NonNull
        public Builder cancelButton(@StringRes int text) {
            this.cancelButton = text;
            return this;
        }

        @NonNull
        public Builder goUpLabel(String text) {
            this.goUpLabel = text;
            return this;
        }

        @NonNull
        public Builder allowNewFolder(boolean allow, @StringRes int buttonLabel) {
            this.allowNewFolder = allow;
            if (buttonLabel == 0) {
                buttonLabel = R.string.new_folder;
            }
            this.newFolderButton = buttonLabel;
            return this;
        }

        @NonNull
        public Builder initialPath(@Nullable String initialPath) {
            if (initialPath == null) {
                initialPath = File.separator;
            }
            this.initialPath = initialPath;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            if (tag == null) {
                tag = FolderChooserDialog.DEFAULT_TAG;
            }
            this.tag = tag;
            return this;
        }

        @NonNull
        public FolderChooserDialog build() {
            FolderChooserDialog dialog = new FolderChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        public FolderChooserDialog show() {
            FolderChooserDialog dialog = build();
            dialog.show(this.context);
            return dialog;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FolderSorter implements Comparator<File> {
        private FolderSorter() {
        }

        @Override // java.util.Comparator
        public int compare(File lhs, File rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}
