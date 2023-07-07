package com.goldze.mvvmhabit.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.databinding.FragmentDetailBinding;
import com.goldze.mvvmhabit.entity.MovieEntity;
import me.goldze.mvvmhabit.base.BaseFragment;

/* loaded from: classes.dex */
public class DetailFragment extends BaseFragment<FragmentDetailBinding, DetailViewModel> {
    private MovieEntity entity;

    @Override // me.goldze.mvvmhabit.base.BaseFragment
    public int initContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return R.layout.fragment_detail;
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment
    public int initVariableId() {
        return 2;
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment, me.goldze.mvvmhabit.base.IBaseView
    public void initParam() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.entity = (MovieEntity) arguments.getParcelable("entity");
        }
    }

    @Override // me.goldze.mvvmhabit.base.BaseFragment, me.goldze.mvvmhabit.base.IBaseView
    public void initData() {
        ((DetailViewModel) this.viewModel).setDemoEntity(this.entity);
    }
}
