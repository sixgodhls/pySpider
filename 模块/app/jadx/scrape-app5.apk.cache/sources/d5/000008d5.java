package com.goldze.mvvmhabit.data.source.http.service;

import com.goldze.mvvmhabit.data.source.HttpResponse;
import com.goldze.mvvmhabit.entity.MovieEntity;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/* loaded from: classes.dex */
public interface MovieApiService {
    public static final String indexPath = "/api/movie";

    @GET(indexPath)
    Observable<HttpResponse<MovieEntity>> index(@Query("offset") int i, @Query("limit") int i2, @Query("token") String str);
}