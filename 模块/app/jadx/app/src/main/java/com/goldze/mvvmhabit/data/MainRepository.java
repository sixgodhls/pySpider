package com.goldze.mvvmhabit.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.goldze.mvvmhabit.data.source.HttpDataSource;
import com.goldze.mvvmhabit.data.source.HttpResponse;
import com.goldze.mvvmhabit.entity.MovieEntity;
import io.reactivex.Observable;
import me.goldze.mvvmhabit.base.BaseModel;

/* loaded from: classes.dex */
public class MainRepository extends BaseModel implements HttpDataSource {
    private static volatile MainRepository INSTANCE;
    private final HttpDataSource mHttpDataSource;

    private MainRepository(@NonNull HttpDataSource httpDataSource) {
        this.mHttpDataSource = httpDataSource;
    }

    public static MainRepository getInstance(HttpDataSource httpDataSource) {
        if (INSTANCE == null) {
            synchronized (MainRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MainRepository(httpDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override // com.goldze.mvvmhabit.data.source.HttpDataSource
    public Observable<HttpResponse<MovieEntity>> index(int i, int i2) {
        return this.mHttpDataSource.index(i, i2);
    }
}
