package android.support.v7.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/* loaded from: classes.dex */
public class AppCompatActivity extends FragmentActivity implements AppCompatCallback, TaskStackBuilder.SupportParentable, ActionBarDrawerToggle.DelegateProvider {
    private AppCompatDelegate mDelegate;
    private Resources mResources;
    private int mThemeId = 0;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate delegate = getDelegate();
        delegate.installViewFactory();
        delegate.onCreate(savedInstanceState);
        if (delegate.applyDayNight() && this.mThemeId != 0) {
            if (Build.VERSION.SDK_INT >= 23) {
                onApplyThemeResource(getTheme(), this.mThemeId, false);
            } else {
                setTheme(this.mThemeId);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public void setTheme(@StyleRes int resid) {
        super.setTheme(resid);
        this.mThemeId = resid;
    }

    @Override // android.app.Activity
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Nullable
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override // android.app.Activity
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override // android.app.Activity
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override // android.app.Activity
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override // android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override // android.app.Activity
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
        if (this.mResources != null) {
            DisplayMetrics newMetrics = super.getResources().getDisplayMetrics();
            this.mResources.updateConfiguration(newConfig, newMetrics);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        getDelegate().onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override // android.app.Activity
    public <T extends View> T findViewById(@IdRes int id) {
        return (T) getDelegate().findViewById(id);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.Window.Callback
    public final boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (super.onMenuItemSelected(featureId, item)) {
            return true;
        }
        ActionBar ab = getSupportActionBar();
        if (item.getItemId() == 16908332 && ab != null && (ab.getDisplayOptions() & 4) != 0) {
            return onSupportNavigateUp();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    @Override // android.app.Activity
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    public boolean supportRequestWindowFeature(int featureId) {
        return getDelegate().requestWindowFeature(featureId);
    }

    @Override // android.support.v4.app.FragmentActivity
    public void supportInvalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    @Override // android.app.Activity
    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    @Override // android.support.v7.app.AppCompatCallback
    @CallSuper
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
    }

    @Override // android.support.v7.app.AppCompatCallback
    @CallSuper
    public void onSupportActionModeFinished(@NonNull ActionMode mode) {
    }

    @Override // android.support.v7.app.AppCompatCallback
    @Nullable
    public ActionMode onWindowStartingSupportActionMode(@NonNull ActionMode.Callback callback) {
        return null;
    }

    @Nullable
    public ActionMode startSupportActionMode(@NonNull ActionMode.Callback callback) {
        return getDelegate().startSupportActionMode(callback);
    }

    @Deprecated
    public void setSupportProgressBarVisibility(boolean visible) {
    }

    @Deprecated
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
    }

    @Deprecated
    public void setSupportProgressBarIndeterminate(boolean indeterminate) {
    }

    @Deprecated
    public void setSupportProgress(int progress) {
    }

    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        builder.addParentStack(this);
    }

    public void onPrepareSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
    }

    public boolean onSupportNavigateUp() {
        Intent upIntent = getSupportParentActivityIntent();
        if (upIntent != null) {
            if (supportShouldUpRecreateTask(upIntent)) {
                TaskStackBuilder b = TaskStackBuilder.create(this);
                onCreateSupportNavigateUpTaskStack(b);
                onPrepareSupportNavigateUpTaskStack(b);
                b.startActivities();
                try {
                    ActivityCompat.finishAffinity(this);
                    return true;
                } catch (IllegalStateException e) {
                    finish();
                    return true;
                }
            }
            supportNavigateUpTo(upIntent);
            return true;
        }
        return false;
    }

    @Override // android.support.v4.app.TaskStackBuilder.SupportParentable
    @Nullable
    public Intent getSupportParentActivityIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    public boolean supportShouldUpRecreateTask(@NonNull Intent targetIntent) {
        return NavUtils.shouldUpRecreateTask(this, targetIntent);
    }

    public void supportNavigateUpTo(@NonNull Intent upIntent) {
        NavUtils.navigateUpTo(this, upIntent);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        onSupportContentChanged();
    }

    @Deprecated
    public void onSupportContentChanged() {
    }

    @Override // android.support.v7.app.ActionBarDrawerToggle.DelegateProvider
    @Nullable
    public ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
        return getDelegate().getDrawerToggleDelegate();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getDelegate().onSaveInstanceState(outState);
    }

    @NonNull
    public AppCompatDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = AppCompatDelegate.create(this, this);
        }
        return this.mDelegate;
    }

    @Override // android.support.v4.app.SupportActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        ActionBar actionBar = getSupportActionBar();
        if (keyCode == 82 && actionBar != null && actionBar.onMenuKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        if (this.mResources == null && VectorEnabledTintResources.shouldBeUsed()) {
            this.mResources = new VectorEnabledTintResources(this, super.getResources());
        }
        Resources resources = this.mResources;
        return resources == null ? super.getResources() : resources;
    }

    private boolean performMenuItemShortcut(int keycode, KeyEvent event) {
        Window currentWindow;
        if (Build.VERSION.SDK_INT < 26 && !event.isCtrlPressed() && !KeyEvent.metaStateHasNoModifiers(event.getMetaState()) && event.getRepeatCount() == 0 && !KeyEvent.isModifierKey(event.getKeyCode()) && (currentWindow = getWindow()) != null && currentWindow.getDecorView() != null) {
            View decorView = currentWindow.getDecorView();
            if (decorView.dispatchKeyShortcutEvent(event)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (performMenuItemShortcut(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Activity
    public void openOptionsMenu() {
        ActionBar actionBar = getSupportActionBar();
        if (getWindow().hasFeature(0)) {
            if (actionBar == null || !actionBar.openOptionsMenu()) {
                super.openOptionsMenu();
            }
        }
    }

    @Override // android.app.Activity
    public void closeOptionsMenu() {
        ActionBar actionBar = getSupportActionBar();
        if (getWindow().hasFeature(0)) {
            if (actionBar == null || !actionBar.closeOptionsMenu()) {
                super.closeOptionsMenu();
            }
        }
    }
}
