package com.goldze.mvvmhabit.data.source;

import com.goldze.mvvmhabit.data.source.http.service.MovieApiService;
import com.goldze.mvvmhabit.entity.MovieEntity;
import com.goldze.mvvmhabit.utils.Encrypt;
import io.reactivex.Observable;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class HttpDataSourceImpl implements HttpDataSource {
    private static volatile HttpDataSourceImpl INSTANCE;
    private MovieApiService apiService;

    public static HttpDataSourceImpl getInstance(MovieApiService movieApiService) {
        if (INSTANCE == null) {
            synchronized (HttpDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDataSourceImpl(movieApiService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private HttpDataSourceImpl(MovieApiService movieApiService) {
        this.apiService = movieApiService;
    }

    @Override // com.goldze.mvvmhabit.data.source.HttpDataSource
    public Observable<HttpResponse<MovieEntity>> index(int i, int i2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(MovieApiService.indexPath);
        String encrypt = Encrypt.encrypt(arrayList);
        return this.apiService.index((i - 1) * i2, i2, encrypt);
    }
}
