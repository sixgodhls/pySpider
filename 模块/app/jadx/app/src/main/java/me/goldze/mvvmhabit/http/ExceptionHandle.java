package me.goldze.mvvmhabit.http;

import android.net.ParseException;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import retrofit2.HttpException;

/* loaded from: classes.dex */
public class ExceptionHandle {
    private static final int FORBIDDEN = 403;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int UNAUTHORIZED = 401;

    public static ResponseThrowable handleException(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ResponseThrowable ex = new ResponseThrowable(e, 1003);
            int code = httpException.code();
            if (code == UNAUTHORIZED) {
                ex.message = "操作未授权";
            } else if (code == REQUEST_TIMEOUT) {
                ex.message = "服务器执行超时";
            } else if (code == INTERNAL_SERVER_ERROR) {
                ex.message = "服务器内部错误";
            } else if (code != SERVICE_UNAVAILABLE) {
                switch (code) {
                    case FORBIDDEN /* 403 */:
                        ex.message = "请求被拒绝";
                        break;
                    case NOT_FOUND /* 404 */:
                        ex.message = "资源不存在";
                        break;
                    default:
                        ex.message = "网络错误";
                        break;
                }
            } else {
                ex.message = "服务器不可用";
            }
            return ex;
        } else if ((e instanceof JsonParseException) || (e instanceof JSONException) || (e instanceof ParseException) || (e instanceof MalformedJsonException)) {
            ResponseThrowable ex2 = new ResponseThrowable(e, 1001);
            ex2.message = "解析错误";
            return ex2;
        } else if (e instanceof ConnectException) {
            ResponseThrowable ex3 = new ResponseThrowable(e, 1002);
            ex3.message = "连接失败";
            return ex3;
        } else if (e instanceof SSLException) {
            ResponseThrowable ex4 = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex4.message = "证书验证失败";
            return ex4;
        } else if (e instanceof ConnectTimeoutException) {
            ResponseThrowable ex5 = new ResponseThrowable(e, 1006);
            ex5.message = "连接超时";
            return ex5;
        } else if (e instanceof SocketTimeoutException) {
            ResponseThrowable ex6 = new ResponseThrowable(e, 1006);
            ex6.message = "连接超时";
            return ex6;
        } else if (e instanceof UnknownHostException) {
            ResponseThrowable ex7 = new ResponseThrowable(e, 1006);
            ex7.message = "主机地址未知";
            return ex7;
        } else {
            ResponseThrowable ex8 = new ResponseThrowable(e, 1000);
            ex8.message = "未知错误";
            return ex8;
        }
    }

    /* loaded from: classes.dex */
    class ERROR {
        public static final int HTTP_ERROR = 1003;
        public static final int NETWORD_ERROR = 1002;
        public static final int PARSE_ERROR = 1001;
        public static final int SSL_ERROR = 1005;
        public static final int TIMEOUT_ERROR = 1006;
        public static final int UNKNOWN = 1000;

        ERROR() {
        }
    }
}
