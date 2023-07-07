package com.goldze.mvvmhabit.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;
import com.goldze.mvvmhabit.ui.index.IndexItemViewModel;
import com.goldze.mvvmhabit.ui.index.IndexViewModel;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LineManagers;
import me.goldze.mvvmhabit.binding.viewadapter.recyclerview.ViewAdapter;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapters;
import me.tatarka.bindingcollectionadapter2.LayoutManagers;

/* loaded from: classes.dex */
public class FragmentIndexBindingImpl extends FragmentIndexBinding {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = null;
    private long mDirtyFlags;
    @NonNull
    private final LinearLayout mboundView0;
    @NonNull
    private final RecyclerView mboundView2;

    public FragmentIndexBindingImpl(@Nullable DataBindingComponent dataBindingComponent, @NonNull View view) {
        this(dataBindingComponent, view, mapBindings(dataBindingComponent, view, 3, sIncludes, sViewsWithIds));
    }

    private FragmentIndexBindingImpl(DataBindingComponent dataBindingComponent, View view, Object[] objArr) {
        super(dataBindingComponent, view, 1, (TwinklingRefreshLayout) objArr[1]);
        this.mDirtyFlags = -1L;
        this.mboundView0 = (LinearLayout) objArr[0];
        this.mboundView0.setTag(null);
        this.mboundView2 = (RecyclerView) objArr[2];
        this.mboundView2.setTag(null);
        this.twinklingRefreshLayout.setTag(null);
        setRootTag(view);
        invalidateAll();
    }

    @Override // android.databinding.ViewDataBinding
    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 8L;
        }
        requestRebind();
    }

    @Override // android.databinding.ViewDataBinding
    public boolean hasPendingBindings() {
        synchronized (this) {
            return this.mDirtyFlags != 0;
        }
    }

    @Override // android.databinding.ViewDataBinding
    public boolean setVariable(int i, @Nullable Object obj) {
        if (1 == i) {
            setAdapter((BindingRecyclerViewAdapter) obj);
            return true;
        } else if (2 != i) {
            return false;
        } else {
            setViewModel((IndexViewModel) obj);
            return true;
        }
    }

    @Override // com.goldze.mvvmhabit.databinding.FragmentIndexBinding
    public void setAdapter(@Nullable BindingRecyclerViewAdapter bindingRecyclerViewAdapter) {
        this.mAdapter = bindingRecyclerViewAdapter;
        synchronized (this) {
            this.mDirtyFlags |= 2;
        }
        notifyPropertyChanged(1);
        super.requestRebind();
    }

    @Override // com.goldze.mvvmhabit.databinding.FragmentIndexBinding
    public void setViewModel(@Nullable IndexViewModel indexViewModel) {
        this.mViewModel = indexViewModel;
        synchronized (this) {
            this.mDirtyFlags |= 4;
        }
        notifyPropertyChanged(2);
        super.requestRebind();
    }

    @Override // android.databinding.ViewDataBinding
    protected boolean onFieldChange(int i, Object obj, int i2) {
        if (i != 0) {
            return false;
        }
        return onChangeViewModelObservableList((ObservableList) obj, i2);
    }

    private boolean onChangeViewModelObservableList(ObservableList<IndexItemViewModel> observableList, int i) {
        if (i == 0) {
            synchronized (this) {
                this.mDirtyFlags |= 1;
            }
            return true;
        }
        return false;
    }

    @Override // android.databinding.ViewDataBinding
    protected void executeBindings() {
        long j;
        BindingCommand bindingCommand;
        ObservableList<IndexItemViewModel> observableList;
        me.tatarka.bindingcollectionadapter2.ItemBinding<IndexItemViewModel> itemBinding;
        ObservableList<IndexItemViewModel> observableList2;
        synchronized (this) {
            j = this.mDirtyFlags;
            this.mDirtyFlags = 0L;
        }
        BindingRecyclerViewAdapter bindingRecyclerViewAdapter = this.mAdapter;
        IndexViewModel indexViewModel = this.mViewModel;
        long j2 = 15 & j;
        if (j2 != 0) {
            BindingCommand bindingCommand2 = ((j & 12) == 0 || indexViewModel == null) ? null : indexViewModel.onLoadMoreCommand;
            if (indexViewModel != null) {
                itemBinding = indexViewModel.itemBinding;
                observableList2 = indexViewModel.observableList;
            } else {
                observableList2 = null;
                itemBinding = null;
            }
            updateRegistration(0, observableList2);
            BindingCommand bindingCommand3 = bindingCommand2;
            observableList = observableList2;
            bindingCommand = bindingCommand3;
        } else {
            bindingCommand = null;
            observableList = null;
            itemBinding = null;
        }
        if ((8 & j) != 0) {
            BindingRecyclerViewAdapters.setLayoutManager(this.mboundView2, LayoutManagers.linear());
            ViewAdapter.setLineManager(this.mboundView2, LineManagers.horizontal());
        }
        if (j2 != 0) {
            BindingRecyclerViewAdapters.setAdapter(this.mboundView2, itemBinding, observableList, bindingRecyclerViewAdapter, null, null);
        }
        if ((j & 12) != 0) {
            com.goldze.mvvmhabit.adapter.ViewAdapter.onRefreshAndLoadMoreCommand(this.twinklingRefreshLayout, null, bindingCommand);
        }
    }
}
