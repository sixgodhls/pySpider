package com.goldze.mvvmhabit.p004ui;

import android.app.Application;
import android.support.annotation.NonNull;
import com.goldze.mvvmhabit.p004ui.index.IndexFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* renamed from: com.goldze.mvvmhabit.ui.MainViewModel */
/* loaded from: classes.dex */
public class MainViewModel extends BaseViewModel {
    public BindingCommand itemClick = new BindingCommand(new BindingAction() { // from class: com.goldze.mvvmhabit.ui.MainViewModel.1
        @Override // me.goldze.mvvmhabit.binding.command.BindingAction
        public void call() {
            MainViewModel.this.startContainerActivity(IndexFragment.class.getCanonicalName());
        }
    });

    public MainViewModel(@NonNull Application application) {
        super(application);
    }
}