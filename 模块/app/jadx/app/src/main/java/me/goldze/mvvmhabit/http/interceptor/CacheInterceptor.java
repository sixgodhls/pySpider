package me.goldze.mvvmhabit.http.interceptor;

import android.content.Context;
import java.io.IOException;
import me.goldze.mvvmhabit.http.NetworkUtil;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/* loaded from: classes.dex */
public class CacheInterceptor implements Interceptor {
    private Context context;

    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        if (NetworkUtil.isNetworkAvailable(this.context)) {
            Response response = chain.proceed(request);
            Response.Builder removeHeader = response.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control");
            return removeHeader.header("Cache-Control", "public, max-age=60").build();
        }
        Response response2 = chain.proceed(request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build());
        Response.Builder removeHeader2 = response2.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control");
        return removeHeader2.header("Cache-Control", "public, only-if-cached, max-stale=259200").build();
    }
}
