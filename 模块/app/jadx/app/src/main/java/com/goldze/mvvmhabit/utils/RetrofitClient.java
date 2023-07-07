package com.goldze.mvvmhabit.utils;

import android.content.Context;
import android.text.TextUtils;
import com.goldze.mvvmhabit.BuildConfig;
import com.goldze.mvvmhabit.utils.HttpsUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import me.goldze.mvvmhabit.http.cookie.CookieJarImpl;
import me.goldze.mvvmhabit.http.cookie.store.PersistentCookieStore;
import me.goldze.mvvmhabit.http.interceptor.BaseInterceptor;
import me.goldze.mvvmhabit.http.interceptor.CacheInterceptor;
import me.goldze.mvvmhabit.http.interceptor.logging.Level;
import me.goldze.mvvmhabit.http.interceptor.logging.LoggingInterceptor;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.Utils;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/* loaded from: classes.dex */
public class RetrofitClient {
    private static final int CACHE_TIMEOUT = 10485760;
    private static final int DEFAULT_TIMEOUT = 20;
    public static String baseUrl = "https://app5.scrape.center";
    private static Context mContext = Utils.getContext();
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private Cache cache;
    private File httpCacheDirectory;

    /* loaded from: classes.dex */
    private static class SingletonHolder {
        private static RetrofitClient INSTANCE = new RetrofitClient();

        private SingletonHolder() {
        }
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private RetrofitClient() {
        this(baseUrl, null);
    }

    private RetrofitClient(String url, Map<String, String> headers) {
        this.cache = null;
        url = TextUtils.isEmpty(url) ? baseUrl : url;
        if (this.httpCacheDirectory == null) {
            this.httpCacheDirectory = new File(mContext.getCacheDir(), "goldze_cache");
        }
        try {
            if (this.cache == null) {
                this.cache = new Cache(this.httpCacheDirectory, 10485760L);
            }
        } catch (Exception e) {
            KLog.e("Could not create http cache", e);
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJarImpl(new PersistentCookieStore(mContext))).addInterceptor(new BaseInterceptor(headers)).addInterceptor(new CacheInterceptor(mContext)).sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).addInterceptor(new LoggingInterceptor.Builder().loggable(BuildConfig.DEBUG).setLevel(Level.BASIC).log(4).request("Request").response("HttpResponse").addHeader("log-header", "I am the log request header.").build()).connectTimeout(20L, TimeUnit.SECONDS).writeTimeout(20L, TimeUnit.SECONDS).connectionPool(new ConnectionPool(8, 15L, TimeUnit.SECONDS)).build();
        retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(url).build();
    }

    public <T> T create(Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return (T) retrofit.create(service);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T execute(Observable<T> observable, Observer<T> subscriber) {
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        return null;
    }
}
