package com.goldze.mvvmhabit.p004ui.index;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.goldze.mvvmhabit.C0690R;
import com.goldze.mvvmhabit.app.AppViewModelFactory;
import com.goldze.mvvmhabit.databinding.FragmentIndexBinding;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

/* renamed from: com.goldze.mvvmhabit.ui.index.IndexFragment */
/* loaded from: classes.dex */
public class IndexFragment extends BaseFragment<FragmentIndexBinding, IndexViewModel> {
    @Override // me.goldze.mvvmhabit.base.BaseFragment
    public int initContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return C0690R.layout.fragment_index;
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment
    public int initVariableId() {
        return 2;
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment, me.goldze.mvvmhabit.base.IBaseView
    public void initParam() {
        super.initParam();
        getActivity().setRequestedOrientation(-1);
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment
    /* renamed from: initViewModel */
    public IndexViewModel mo307initViewModel() {
        return (IndexViewModel) ViewModelProviders.m60of(this, AppViewModelFactory.getInstance(getActivity().getApplication())).get(IndexViewModel.class);
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment, me.goldze.mvvmhabit.base.IBaseView
    public void initData() {
        ((FragmentIndexBinding) this.binding).setAdapter(new BindingRecyclerViewAdapter());
        ((IndexViewModel) this.viewModel).requestNetWork();
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment, me.goldze.mvvmhabit.base.IBaseView
    public void initViewObservable() {
        ((IndexViewModel) this.viewModel).f67uc.finishRefreshing.observe(this, new Observer() { // from class: com.goldze.mvvmhabit.ui.index.IndexFragment.1
            {
                IndexFragment.this = this;
            }

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Object obj) {
                ((FragmentIndexBinding) IndexFragment.this.binding).twinklingRefreshLayout.finishRefreshing();
            }
        });
        ((IndexViewModel) this.viewModel).f67uc.finishLoadMore.observe(this, new Observer() { // from class: com.goldze.mvvmhabit.ui.index.IndexFragment.2
            {
                IndexFragment.this = this;
            }

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Object obj) {
                ((FragmentIndexBinding) IndexFragment.this.binding).twinklingRefreshLayout.finishLoadmore();
            }
        });
    }
}