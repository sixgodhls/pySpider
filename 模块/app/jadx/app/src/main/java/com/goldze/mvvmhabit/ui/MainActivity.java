package com.goldze.mvvmhabit.ui;

import android.os.Bundle;
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.databinding.ActivityMainBinding;
import com.goldze.mvvmhabit.ui.index.IndexFragment;
import me.goldze.mvvmhabit.base.BaseActivity;

/* loaded from: classes.dex */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    @Override // me.goldze.mvvmhabit.base.BaseActivity
    public int initContentView(Bundle bundle) {
        return R.layout.activity_main;
    }

    @Override // me.goldze.mvvmhabit.base.BaseActivity
    public int initVariableId() {
        return 2;
    }

    @Override // me.goldze.mvvmhabit.base.BaseActivity, me.goldze.mvvmhabit.base.IBaseView
    public void initParam() {
        super.initParam();
        setRequestedOrientation(-1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // me.goldze.mvvmhabit.base.BaseActivity, com.trello.rxlifecycle2.components.support.RxAppCompatActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startContainerActivity(IndexFragment.class.getCanonicalName());
    }
}
