package com.goldze.mvvmhabit.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.goldze.mvvmhabit.data.MainRepository;
import com.goldze.mvvmhabit.p004ui.index.IndexViewModel;

/* loaded from: classes.dex */
public class AppViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile AppViewModelFactory INSTANCE;
    private final Application mApplication;
    private final MainRepository mRepository;

    public static AppViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AppViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppViewModelFactory(application, Injection.provideDemoRepository());
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private AppViewModelFactory(Application application, MainRepository mainRepository) {
        this.mApplication = application;
        this.mRepository = mainRepository;
    }

    @Override // android.arch.lifecycle.ViewModelProvider.NewInstanceFactory, android.arch.lifecycle.ViewModelProvider.Factory
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> cls) {
        if (cls.isAssignableFrom(IndexViewModel.class)) {
            return new IndexViewModel(this.mApplication, this.mRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + cls.getName());
    }
}