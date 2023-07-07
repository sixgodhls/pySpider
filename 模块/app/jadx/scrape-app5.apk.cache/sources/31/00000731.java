package com.goldze.mvvmhabit.databinding;

import android.databinding.Bindable;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.goldze.mvvmhabit.C0690R;
import com.goldze.mvvmhabit.p004ui.MainViewModel;

/* loaded from: classes.dex */
public abstract class ActivityMainBinding extends ViewDataBinding {
    @Bindable
    protected MainViewModel mViewModel;

    public abstract void setViewModel(@Nullable MainViewModel mainViewModel);

    /* JADX INFO: Access modifiers changed from: protected */
    public ActivityMainBinding(DataBindingComponent dataBindingComponent, View view, int i) {
        super(dataBindingComponent, view, i);
    }

    @Nullable
    public MainViewModel getViewModel() {
        return this.mViewModel;
    }

    @NonNull
    public static ActivityMainBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z) {
        return inflate(layoutInflater, viewGroup, z, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ActivityMainBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z, @Nullable DataBindingComponent dataBindingComponent) {
        return (ActivityMainBinding) DataBindingUtil.inflate(layoutInflater, C0690R.layout.activity_main, viewGroup, z, dataBindingComponent);
    }

    @NonNull
    public static ActivityMainBinding inflate(@NonNull LayoutInflater layoutInflater) {
        return inflate(layoutInflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ActivityMainBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable DataBindingComponent dataBindingComponent) {
        return (ActivityMainBinding) DataBindingUtil.inflate(layoutInflater, C0690R.layout.activity_main, null, false, dataBindingComponent);
    }

    public static ActivityMainBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    public static ActivityMainBinding bind(@NonNull View view, @Nullable DataBindingComponent dataBindingComponent) {
        return (ActivityMainBinding) bind(dataBindingComponent, view, C0690R.layout.activity_main);
    }
}