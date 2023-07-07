package me.goldze.mvvmhabit.crash;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import me.goldze.mvvmhabit.R;

/* loaded from: classes.dex */
public final class DefaultErrorActivity extends AppCompatActivity {
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    @SuppressLint({"PrivateResource"})
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypedArray a = obtainStyledAttributes(R.styleable.AppCompatTheme);
        if (!a.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
        }
        a.recycle();
        setContentView(R.layout.customactivityoncrash_default_error_activity);
        Button restartButton = (Button) findViewById(R.id.customactivityoncrash_error_activity_restart_button);
        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            restartButton.setText(R.string.customactivityoncrash_error_activity_restart_app);
            restartButton.setOnClickListener(new View.OnClickListener() { // from class: me.goldze.mvvmhabit.crash.DefaultErrorActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    CustomActivityOnCrash.restartApplication(DefaultErrorActivity.this, config);
                }
            });
        } else {
            restartButton.setOnClickListener(new View.OnClickListener() { // from class: me.goldze.mvvmhabit.crash.DefaultErrorActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    CustomActivityOnCrash.closeApplication(DefaultErrorActivity.this, config);
                }
            });
        }
        Button moreInfoButton = (Button) findViewById(R.id.customactivityoncrash_error_activity_more_info_button);
        if (config.isShowErrorDetails()) {
            moreInfoButton.setOnClickListener(new View.OnClickListener() { // from class: me.goldze.mvvmhabit.crash.DefaultErrorActivity.3
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    AlertDialog.Builder title = new AlertDialog.Builder(DefaultErrorActivity.this).setTitle(R.string.customactivityoncrash_error_activity_error_details_title);
                    DefaultErrorActivity defaultErrorActivity = DefaultErrorActivity.this;
                    AlertDialog dialog = title.setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(defaultErrorActivity, defaultErrorActivity.getIntent())).setPositiveButton(R.string.customactivityoncrash_error_activity_error_details_close, (DialogInterface.OnClickListener) null).setNeutralButton(R.string.customactivityoncrash_error_activity_error_details_copy, new DialogInterface.OnClickListener() { // from class: me.goldze.mvvmhabit.crash.DefaultErrorActivity.3.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog2, int which) {
                            DefaultErrorActivity.this.copyErrorToClipboard();
                            Toast.makeText(DefaultErrorActivity.this, R.string.customactivityoncrash_error_activity_error_details_copied, 0).show();
                        }
                    }).show();
                    TextView textView = (TextView) dialog.findViewById(16908299);
                    textView.setTextSize(0, DefaultErrorActivity.this.getResources().getDimension(R.dimen.customactivityoncrash_error_activity_error_details_text_size));
                }
            });
        } else {
            moreInfoButton.setVisibility(8);
        }
        Integer defaultErrorActivityDrawableId = config.getErrorDrawable();
        ImageView errorImageView = (ImageView) findViewById(R.id.customactivityoncrash_error_activity_image);
        if (defaultErrorActivityDrawableId != null) {
            errorImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), defaultErrorActivityDrawableId.intValue(), getTheme()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void copyErrorToClipboard() {
        String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent());
        ClipboardManager clipboard = (ClipboardManager) getSystemService("clipboard");
        ClipData clip = ClipData.newPlainText(getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label), errorInformation);
        clipboard.setPrimaryClip(clip);
    }
}
