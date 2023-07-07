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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.ui.detail.DetailViewModel;

/* loaded from: classes.dex */
public abstract class FragmentDetailBinding extends ViewDataBinding {
    @NonNull
    public final TextView categoriesKey;
    @NonNull
    public final TextView categoriesValue;
    @NonNull
    public final ImageView cover;
    @NonNull
    public final ScrollView detail;
    @NonNull
    public final TextView dramaKey;
    @NonNull
    public final TextView dramaValue;
    @Bindable
    protected DetailViewModel mViewModel;
    @NonNull
    public final TextView minuteKey;
    @NonNull
    public final TextView minuteValue;
    @NonNull
    public final TextView publishedAtKey;
    @NonNull
    public final TextView publishedAtValue;
    @NonNull
    public final TextView scoreKey;
    @NonNull
    public final TextView scoreValue;
    @NonNull
    public final TextView title;

    public abstract void setViewModel(@Nullable DetailViewModel detailViewModel);

    /* JADX INFO: Access modifiers changed from: protected */
    public FragmentDetailBinding(DataBindingComponent dataBindingComponent, View view, int i, TextView textView, TextView textView2, ImageView imageView, ScrollView scrollView, TextView textView3, TextView textView4, TextView textView5, TextView textView6, TextView textView7, TextView textView8, TextView textView9, TextView textView10, TextView textView11) {
        super(dataBindingComponent, view, i);
        this.categoriesKey = textView;
        this.categoriesValue = textView2;
        this.cover = imageView;
        this.detail = scrollView;
        this.dramaKey = textView3;
        this.dramaValue = textView4;
        this.minuteKey = textView5;
        this.minuteValue = textView6;
        this.publishedAtKey = textView7;
        this.publishedAtValue = textView8;
        this.scoreKey = textView9;
        this.scoreValue = textView10;
        this.title = textView11;
    }

    @Nullable
    public DetailViewModel getViewModel() {
        return this.mViewModel;
    }

    @NonNull
    public static FragmentDetailBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z) {
        return inflate(layoutInflater, viewGroup, z, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentDetailBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z, @Nullable DataBindingComponent dataBindingComponent) {
        return (FragmentDetailBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_detail, viewGroup, z, dataBindingComponent);
    }

    @NonNull
    public static FragmentDetailBinding inflate(@NonNull LayoutInflater layoutInflater) {
        return inflate(layoutInflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentDetailBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable DataBindingComponent dataBindingComponent) {
        return (FragmentDetailBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_detail, null, false, dataBindingComponent);
    }

    public static FragmentDetailBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    public static FragmentDetailBinding bind(@NonNull View view, @Nullable DataBindingComponent dataBindingComponent) {
        return (FragmentDetailBinding) bind(dataBindingComponent, view, R.layout.fragment_detail);
    }
}
