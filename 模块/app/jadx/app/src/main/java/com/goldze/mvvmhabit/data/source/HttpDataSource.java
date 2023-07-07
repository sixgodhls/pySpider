package com.goldze.mvvmhabit.data.source;

import com.goldze.mvvmhabit.entity.MovieEntity;
import io.reactivex.Observable;

/* loaded from: classes.dex */
public interface HttpDataSource {
    Observable<HttpResponse<MovieEntity>> index(int i, int i2);
}
