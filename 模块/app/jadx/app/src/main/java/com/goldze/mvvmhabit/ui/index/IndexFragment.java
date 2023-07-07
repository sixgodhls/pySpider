package com.goldze.mvvmhabit.ui.index;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.app.AppViewModelFactory;
import com.goldze.mvvmhabit.databinding.FragmentIndexBinding;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

/* loaded from: classes.dex */
public class IndexFragment extends BaseFragment<FragmentIndexBinding, IndexViewModel> {
    @Override // me.goldze.mvvmhabit.base.BaseFragment
    public int initContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return R.layout.fragment_index;
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

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // me.goldze.mvvmhabit.base.BaseFragment
    /* renamed from: initViewModel */
    public IndexViewModel mo245initViewModel() {
        return (IndexViewModel) ViewModelProviders.of(this, AppViewModelFactory.getInstance(getActivity().getApplication())).get(IndexViewModel.class);
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment, me.goldze.mvvmhabit.base.IBaseView
    public void initData() {
        ((FragmentIndexBinding) this.binding).setAdapter(new BindingRecyclerViewAdapter());
        ((IndexViewModel) this.viewModel).requestNetWork();
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment, me.goldze.mvvmhabit.base.IBaseView
    public void initViewObservable() {
        ((IndexViewModel) this.viewModel).uc.finishRefreshing.observe(this, new Observer() { // from class: com.goldze.mvvmhabit.ui.index.IndexFragment.1
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Object obj) {
                ((FragmentIndexBinding) IndexFragment.this.binding).twinklingRefreshLayout.finishRefreshing();
            }
        });
        ((IndexViewModel) this.viewModel).uc.finishLoadMore.observe(this, new Observer() { // from class: com.goldze.mvvmhabit.ui.index.IndexFragment.2
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Object obj) {
                ((FragmentIndexBinding) IndexFragment.this.binding).twinklingRefreshLayout.finishLoadmore();
            }
        });
    }
}
