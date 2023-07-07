package com.goldze.mvvmhabit.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.goldze.mvvmhabit.ui.MainViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.viewadapter.view.ViewAdapter;

/* loaded from: classes.dex */
public class ActivityMainBindingImpl extends ActivityMainBinding {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = null;
    private long mDirtyFlags;
    @NonNull
    private final LinearLayout mboundView0;
    @NonNull
    private final ImageView mboundView1;

    @Override // android.databinding.ViewDataBinding
    protected boolean onFieldChange(int i, Object obj, int i2) {
        return false;
    }

    public ActivityMainBindingImpl(@Nullable DataBindingComponent dataBindingComponent, @NonNull View view) {
        this(dataBindingComponent, view, mapBindings(dataBindingComponent, view, 2, sIncludes, sViewsWithIds));
    }

    private ActivityMainBindingImpl(DataBindingComponent dataBindingComponent, View view, Object[] objArr) {
        super(dataBindingComponent, view, 0);
        this.mDirtyFlags = -1L;
        this.mboundView0 = (LinearLayout) objArr[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (ImageView) objArr[1];
        this.mboundView1.setTag(null);
        setRootTag(view);
        invalidateAll();
    }

    @Override // android.databinding.ViewDataBinding
    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 2L;
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
        if (2 == i) {
            setViewModel((MainViewModel) obj);
            return true;
        }
        return false;
    }

    @Override // com.goldze.mvvmhabit.databinding.ActivityMainBinding
    public void setViewModel(@Nullable MainViewModel mainViewModel) {
        this.mViewModel = mainViewModel;
        synchronized (this) {
            this.mDirtyFlags |= 1;
        }
        notifyPropertyChanged(2);
        super.requestRebind();
    }

    @Override // android.databinding.ViewDataBinding
    protected void executeBindings() {
        long j;
        synchronized (this) {
            j = this.mDirtyFlags;
            this.mDirtyFlags = 0L;
        }
        BindingCommand bindingCommand = null;
        MainViewModel mainViewModel = this.mViewModel;
        long j2 = j & 3;
        if (j2 != 0 && mainViewModel != null) {
            bindingCommand = mainViewModel.itemClick;
        }
        if (j2 != 0) {
            ViewAdapter.onClickCommand(this.mboundView1, bindingCommand, false);
        }
    }
}
