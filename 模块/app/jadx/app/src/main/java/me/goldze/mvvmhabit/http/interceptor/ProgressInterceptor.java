package me.goldze.mvvmhabit.http.interceptor;

import java.io.IOException;
import me.goldze.mvvmhabit.http.download.ProgressResponseBody;
import okhttp3.Interceptor;
import okhttp3.Response;

/* loaded from: classes.dex */
public class ProgressInterceptor implements Interceptor {
    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body())).build();
    }
}
