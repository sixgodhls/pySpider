package me.goldze.mvvmhabit.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import java.lang.ref.WeakReference;
import me.goldze.mvvmhabit.R;

/* loaded from: classes.dex */
public class ContainerActivity extends RxAppCompatActivity {
    public static final String BUNDLE = "bundle";
    public static final String FRAGMENT = "fragment";
    private static final String FRAGMENT_TAG = "content_fragment_tag";
    protected WeakReference<Fragment> mFragment;

    @Override // com.trello.rxlifecycle2.components.support.RxAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(32);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = fm.getFragment(savedInstanceState, FRAGMENT_TAG);
        }
        if (fragment == null) {
            fragment = initFromIntent(getIntent());
        }
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.content, fragment);
        trans.commitAllowingStateLoss();
        this.mFragment = new WeakReference<>(fragment);
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_TAG, this.mFragment.get());
    }

    protected Fragment initFromIntent(Intent data) {
        if (data == null) {
            throw new RuntimeException("you must provide a page info to display");
        }
        try {
            String fragmentName = data.getStringExtra(FRAGMENT);
            if (fragmentName == null || "".equals(fragmentName)) {
                throw new IllegalArgumentException("can not find page fragmentName");
            }
            Class<?> fragmentClass = Class.forName(fragmentName);
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            Bundle args = data.getBundleExtra(BUNDLE);
            if (args != null) {
                fragment.setArguments(args);
            }
            return fragment;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("fragment initialization failed!");
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            throw new RuntimeException("fragment initialization failed!");
        } catch (InstantiationException e3) {
            e3.printStackTrace();
            throw new RuntimeException("fragment initialization failed!");
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment instanceof BaseFragment) {
            if (!((BaseFragment) fragment).isBackPressed()) {
                super.onBackPressed();
                return;
            }
            return;
        }
        super.onBackPressed();
    }
}
