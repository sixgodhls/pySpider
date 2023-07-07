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
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.ui.index.IndexViewModel;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

/* loaded from: classes.dex */
public abstract class FragmentIndexBinding extends ViewDataBinding {
    @Bindable
    protected BindingRecyclerViewAdapter mAdapter;
    @Bindable
    protected IndexViewModel mViewModel;
    @NonNull
    public final TwinklingRefreshLayout twinklingRefreshLayout;

    public abstract void setAdapter(@Nullable BindingRecyclerViewAdapter bindingRecyclerViewAdapter);

    public abstract void setViewModel(@Nullable IndexViewModel indexViewModel);

    /* JADX INFO: Access modifiers changed from: protected */
    public FragmentIndexBinding(DataBindingComponent dataBindingComponent, View view, int i, TwinklingRefreshLayout twinklingRefreshLayout) {
        super(dataBindingComponent, view, i);
        this.twinklingRefreshLayout = twinklingRefreshLayout;
    }

    @Nullable
    public IndexViewModel getViewModel() {
        return this.mViewModel;
    }

    @Nullable
    public BindingRecyclerViewAdapter getAdapter() {
        return this.mAdapter;
    }

    @NonNull
    public static FragmentIndexBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z) {
        return inflate(layoutInflater, viewGroup, z, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentIndexBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z, @Nullable DataBindingComponent dataBindingComponent) {
        return (FragmentIndexBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_index, viewGroup, z, dataBindingComponent);
    }

    @NonNull
    public static FragmentIndexBinding inflate(@NonNull LayoutInflater layoutInflater) {
        return inflate(layoutInflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentIndexBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable DataBindingComponent dataBindingComponent) {
        return (FragmentIndexBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_index, null, false, dataBindingComponent);
    }

    public static FragmentIndexBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    public static FragmentIndexBinding bind(@NonNull View view, @Nullable DataBindingComponent dataBindingComponent) {
        return (FragmentIndexBinding) bind(dataBindingComponent, view, R.layout.fragment_index);
    }
}
