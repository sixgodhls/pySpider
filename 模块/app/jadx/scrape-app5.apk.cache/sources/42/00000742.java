package com.goldze.mvvmhabit.p004ui;

import android.os.Bundle;
import com.goldze.mvvmhabit.C0690R;
import com.goldze.mvvmhabit.databinding.ActivityMainBinding;
import com.goldze.mvvmhabit.p004ui.index.IndexFragment;
import me.goldze.mvvmhabit.base.BaseActivity;

/* renamed from: com.goldze.mvvmhabit.ui.MainActivity */
/* loaded from: classes.dex */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    @Override // me.goldze.mvvmhabit.base.BaseActivity
    public int initContentView(Bundle bundle) {
        return C0690R.layout.activity_main;
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
    @Override // me.goldze.mvvmhabit.base.BaseActivity, com.trello.rxlifecycle2.components.support.RxAppCompatActivity, android.support.p003v7.app.AppCompatActivity, android.support.p000v4.app.FragmentActivity, android.support.p000v4.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startContainerActivity(IndexFragment.class.getCanonicalName());
    }
}