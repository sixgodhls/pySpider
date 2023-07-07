package com.goldze.mvvmhabit.app;

import com.goldze.mvvmhabit.data.MainRepository;
import com.goldze.mvvmhabit.data.source.HttpDataSourceImpl;
import com.goldze.mvvmhabit.data.source.http.service.MovieApiService;
import com.goldze.mvvmhabit.utils.RetrofitClient;

/* loaded from: classes.dex */
public class Injection {
    public static MainRepository provideDemoRepository() {
        return MainRepository.getInstance(HttpDataSourceImpl.getInstance((MovieApiService) RetrofitClient.getInstance().create(MovieApiService.class)));
    }
}
