package me.goldze.mvvmhabit.base;

import android.support.annotation.NonNull;
import me.goldze.mvvmhabit.base.BaseViewModel;

/* loaded from: classes.dex */
public class MultiItemViewModel<VM extends BaseViewModel> extends ItemViewModel<VM> {
    protected Object multiType;

    public Object getItemType() {
        return this.multiType;
    }

    public void multiItemType(@NonNull Object multiType) {
        this.multiType = multiType;
    }

    public MultiItemViewModel(@NonNull VM viewModel) {
        super(viewModel);
    }
}
