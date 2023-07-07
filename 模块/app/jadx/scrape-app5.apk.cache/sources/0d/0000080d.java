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
import android.support.p000v4.app.DialogFragment;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentActivity;
import android.support.p003v7.app.AppCompatActivity;
import android.support.v13.app.ActivityCompat;
import android.view.View;
import android.webkit.MimeTypeMap;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.commons.C0592R;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class FileChooserDialog extends DialogFragment implements MaterialDialog.ListCallback {
    private static final String DEFAULT_TAG = "[MD_FILE_SELECTOR]";
    private FileCallback callback;
    private boolean canGoUp = true;
    private File[] parentContents;
    private File parentFolder;

    /* loaded from: classes.dex */
    public interface FileCallback {
        void onFileChooserDismissed(@NonNull FileChooserDialog fileChooserDialog);

        void onFileSelection(@NonNull FileChooserDialog fileChooserDialog, @NonNull File file);
    }

    CharSequence[] getContentsArray() {
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

    File[] listFiles(@Nullable String mimeType, @Nullable String[] extensions) {
        File[] contents = this.parentFolder.listFiles();
        List<File> results = new ArrayList<>();
        if (contents == null) {
            return null;
        }
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        for (File fi : contents) {
            if (fi.isDirectory()) {
                results.add(fi);
            } else if (extensions != null) {
                boolean found = false;
                int length = extensions.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    String ext = extensions[i];
                    if (!fi.getName().toLowerCase().endsWith(ext.toLowerCase())) {
                        i++;
                    } else {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    results.add(fi);
                }
            } else if (mimeType != null && fileIsMimeType(fi, mimeType, mimeTypeMap)) {
                results.add(fi);
            }
        }
        Collections.sort(results, new FileSorter());
        return (File[]) results.toArray(new File[results.size()]);
    }

    boolean fileIsMimeType(File file, String mimeType, MimeTypeMap mimeTypeMap) {
        int fileTypeDelimiter;
        if (mimeType == null || mimeType.equals("*/*")) {
            return true;
        }
        String filename = file.toURI().toString();
        int dotPos = filename.lastIndexOf(46);
        if (dotPos == -1) {
            return false;
        }
        String fileExtension = filename.substring(dotPos + 1);
        if (fileExtension.endsWith("json")) {
            return mimeType.startsWith("application/json");
        }
        String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
        if (fileType == null) {
            return false;
        }
        if (fileType.equals(mimeType)) {
            return true;
        }
        int mimeTypeDelimiter = mimeType.lastIndexOf(47);
        if (mimeTypeDelimiter == -1) {
            return false;
        }
        String mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter);
        String mimeTypeSubtype = mimeType.substring(mimeTypeDelimiter + 1);
        if (!mimeTypeSubtype.equals("*") || (fileTypeDelimiter = fileType.lastIndexOf(47)) == -1) {
            return false;
        }
        String fileTypeMainType = fileType.substring(0, fileTypeDelimiter);
        return fileTypeMainType.equals(mimeTypeMainType);
    }

    @Override // android.support.p000v4.app.DialogFragment
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return new MaterialDialog.Builder(getActivity()).title(C0592R.string.md_error_label).content(C0592R.string.md_storage_perm_error).positiveText(17039370).build();
        }
        if (getArguments() == null || !getArguments().containsKey("builder")) {
            throw new IllegalStateException("You must create a FileChooserDialog using the Builder.");
        }
        if (!getArguments().containsKey("current_path")) {
            getArguments().putString("current_path", getBuilder().initialPath);
        }
        this.parentFolder = new File(getArguments().getString("current_path"));
        checkIfCanGoUp();
        this.parentContents = listFiles(getBuilder().mimeType, getBuilder().extensions);
        return new MaterialDialog.Builder(getActivity()).title(this.parentFolder.getAbsolutePath()).typeface(getBuilder().mediumFont, getBuilder().regularFont).items(getContentsArray()).itemsCallback(this).onNegative(new MaterialDialog.SingleButtonCallback() { // from class: com.afollestad.materialdialogs.folderselector.FileChooserDialog.1
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).autoDismiss(false).negativeText(getBuilder().cancelButton).build();
    }

    @Override // android.support.p000v4.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        FileCallback fileCallback = this.callback;
        if (fileCallback != null) {
            fileCallback.onFileChooserDismissed(this);
        }
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
        if (this.parentFolder.isFile()) {
            this.callback.onFileSelection(this, this.parentFolder);
            dismiss();
            return;
        }
        this.parentContents = listFiles(getBuilder().mimeType, getBuilder().extensions);
        MaterialDialog dialog = (MaterialDialog) getDialog();
        dialog.setTitle(this.parentFolder.getAbsolutePath());
        getArguments().putString("current_path", this.parentFolder.getAbsolutePath());
        dialog.setItems(getContentsArray());
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

    @Override // android.support.p000v4.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callback = (FileCallback) activity;
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
    public String getInitialPath() {
        return getBuilder().initialPath;
    }

    @NonNull
    private Builder getBuilder() {
        return (Builder) getArguments().getSerializable("builder");
    }

    /* loaded from: classes.dex */
    public static class Builder implements Serializable {
        @NonNull
        final transient AppCompatActivity context;
        String[] extensions;
        @Nullable
        String mediumFont;
        @Nullable
        String regularFont;
        String tag;
        @StringRes
        int cancelButton = 17039360;
        String initialPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String mimeType = null;
        String goUpLabel = "...";

        public <ActivityType extends AppCompatActivity & FileCallback> Builder(@NonNull ActivityType context) {
            this.context = context;
        }

        @NonNull
        public Builder typeface(@Nullable String medium, @Nullable String regular) {
            this.mediumFont = medium;
            this.regularFont = regular;
            return this;
        }

        @NonNull
        public Builder cancelButton(@StringRes int text) {
            this.cancelButton = text;
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
        public Builder mimeType(@Nullable String type) {
            this.mimeType = type;
            return this;
        }

        @NonNull
        public Builder extensionsFilter(@Nullable String... extensions) {
            this.extensions = extensions;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            if (tag == null) {
                tag = FileChooserDialog.DEFAULT_TAG;
            }
            this.tag = tag;
            return this;
        }

        @NonNull
        public Builder goUpLabel(String text) {
            this.goUpLabel = text;
            return this;
        }

        @NonNull
        public FileChooserDialog build() {
            FileChooserDialog dialog = new FileChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        public FileChooserDialog show() {
            FileChooserDialog dialog = build();
            dialog.show(this.context);
            return dialog;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FileSorter implements Comparator<File> {
        private FileSorter() {
        }

        @Override // java.util.Comparator
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            }
            if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            }
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}