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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.ui.index.IndexItemViewModel;

/* loaded from: classes.dex */
public abstract class ItemBinding extends ViewDataBinding {
    @NonNull
    public final LinearLayout item;
    @Bindable
    protected IndexItemViewModel mViewModel;
    @NonNull
    public final TextView tvDescription;
    @NonNull
    public final TextView tvScore;
    @NonNull
    public final TextView tvTitle;

    public abstract void setViewModel(@Nullable IndexItemViewModel indexItemViewModel);

    /* JADX INFO: Access modifiers changed from: protected */
    public ItemBinding(DataBindingComponent dataBindingComponent, View view, int i, LinearLayout linearLayout, TextView textView, TextView textView2, TextView textView3) {
        super(dataBindingComponent, view, i);
        this.item = linearLayout;
        this.tvDescription = textView;
        this.tvScore = textView2;
        this.tvTitle = textView3;
    }

    @Nullable
    public IndexItemViewModel getViewModel() {
        return this.mViewModel;
    }

    @NonNull
    public static ItemBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z) {
        return inflate(layoutInflater, viewGroup, z, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ItemBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z, @Nullable DataBindingComponent dataBindingComponent) {
        return (ItemBinding) DataBindingUtil.inflate(layoutInflater, R.layout.item, viewGroup, z, dataBindingComponent);
    }

    @NonNull
    public static ItemBinding inflate(@NonNull LayoutInflater layoutInflater) {
        return inflate(layoutInflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ItemBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable DataBindingComponent dataBindingComponent) {
        return (ItemBinding) DataBindingUtil.inflate(layoutInflater, R.layout.item, null, false, dataBindingComponent);
    }

    public static ItemBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    public static ItemBinding bind(@NonNull View view, @Nullable DataBindingComponent dataBindingComponent) {
        return (ItemBinding) bind(dataBindingComponent, view, R.layout.item);
    }
}
