package me.goldze.mvvmhabit.base;

import android.support.annotation.NonNull;
import me.goldze.mvvmhabit.base.BaseViewModel;

/* loaded from: classes.dex */
public class ItemViewModel<VM extends BaseViewModel> {
    protected VM viewModel;

    public ItemViewModel(@NonNull VM viewModel) {
        this.viewModel = viewModel;
    }
}