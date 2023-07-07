package android.support.p003v7.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.p000v4.view.KeyEventDispatcher;
import android.support.p003v7.appcompat.C0452R;
import android.support.p003v7.view.ActionMode;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

/* renamed from: android.support.v7.app.AppCompatDialog */
/* loaded from: classes.dex */
public class AppCompatDialog extends Dialog implements AppCompatCallback {
    private AppCompatDelegate mDelegate;
    private final KeyEventDispatcher.Component mKeyDispatcher;

    public AppCompatDialog(Context context) {
        this(context, 0);
    }

    public AppCompatDialog(Context context, int theme) {
        super(context, getThemeResId(context, theme));
        this.mKeyDispatcher = new KeyEventDispatcher.Component() { // from class: android.support.v7.app.AppCompatDialog.1
            @Override // android.support.p000v4.view.KeyEventDispatcher.Component
            public boolean superDispatchKeyEvent(KeyEvent event) {
                return AppCompatDialog.this.superDispatchKeyEvent(event);
            }
        };
        getDelegate().onCreate(null);
        getDelegate().applyDayNight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AppCompatDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mKeyDispatcher = new KeyEventDispatcher.Component() { // from class: android.support.v7.app.AppCompatDialog.1
            @Override // android.support.p000v4.view.KeyEventDispatcher.Component
            public boolean superDispatchKeyEvent(KeyEvent event) {
                return AppCompatDialog.this.superDispatchKeyEvent(event);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    @Override // android.app.Dialog
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override // android.app.Dialog
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override // android.app.Dialog
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override // android.app.Dialog
    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        return (T) getDelegate().findViewById(id);
    }

    @Override // android.app.Dialog
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        getDelegate().setTitle(title);
    }

    @Override // android.app.Dialog
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        getDelegate().setTitle(getContext().getString(titleId));
    }

    @Override // android.app.Dialog
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override // android.app.Dialog
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    public boolean supportRequestWindowFeature(int featureId) {
        return getDelegate().requestWindowFeature(featureId);
    }

    @Override // android.app.Dialog
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    public AppCompatDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = AppCompatDelegate.create(this, this);
        }
        return this.mDelegate;
    }

    private static int getThemeResId(Context context, int themeId) {
        if (themeId == 0) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(C0452R.attr.dialogTheme, outValue, true);
            return outValue.resourceId;
        }
        return themeId;
    }

    @Override // android.support.p003v7.app.AppCompatCallback
    public void onSupportActionModeStarted(ActionMode mode) {
    }

    @Override // android.support.p003v7.app.AppCompatCallback
    public void onSupportActionModeFinished(ActionMode mode) {
    }

    @Override // android.support.p003v7.app.AppCompatCallback
    @Nullable
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    boolean superDispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        View decor = getWindow().getDecorView();
        return KeyEventDispatcher.dispatchKeyEvent(this.mKeyDispatcher, decor, this, event);
    }
}