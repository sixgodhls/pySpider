package com.goldze.mvvmhabit.p004ui.detail;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import com.goldze.mvvmhabit.entity.MovieEntity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/* renamed from: com.goldze.mvvmhabit.ui.detail.DetailViewModel */
/* loaded from: classes.dex */
public class DetailViewModel extends BaseViewModel {
    public ObservableField<MovieEntity> entity = new ObservableField<>();

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDemoEntity(MovieEntity movieEntity) {
        this.entity.set(movieEntity);
    }

    @Override // me.goldze.mvvmhabit.base.BaseViewModel, me.goldze.mvvmhabit.base.IBaseViewModel
    public void onDestroy() {
        super.onDestroy();
        this.entity = null;
    }
}