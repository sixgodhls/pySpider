package me.goldze.mvvmhabit.http;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import me.goldze.mvvmhabit.http.download.DownLoadSubscriber;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.http.interceptor.ProgressInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/* loaded from: classes.dex */
public class DownLoadManager {
    private static DownLoadManager instance;
    private static Retrofit retrofit;

    /* loaded from: classes.dex */
    private interface ApiService {
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String str);
    }

    private DownLoadManager() {
        buildNetWork();
    }

    public static DownLoadManager getInstance() {
        if (instance == null) {
            instance = new DownLoadManager();
        }
        return instance;
    }

    public void load(String downUrl, final ProgressCallBack callBack) {
        ((ApiService) retrofit.create(ApiService.class)).download(downUrl).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).doOnNext(new Consumer<ResponseBody>() { // from class: me.goldze.mvvmhabit.http.DownLoadManager.1
            @Override // io.reactivex.functions.Consumer
            public void accept(ResponseBody responseBody) throws Exception {
                callBack.saveFile(responseBody);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new DownLoadSubscriber(callBack));
    }

    private void buildNetWork() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new ProgressInterceptor()).connectTimeout(20L, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder().client(okHttpClient).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(NetworkUtil.url).build();
    }
}
