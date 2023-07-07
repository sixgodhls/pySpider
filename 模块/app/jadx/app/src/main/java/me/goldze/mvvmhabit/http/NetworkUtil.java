package me.goldze.mvvmhabit.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.graphics.drawable.PathInterpolatorCompat;
import android.telephony.TelephonyManager;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/* loaded from: classes.dex */
public class NetworkUtil {
    public static String url = "http://www.baidu.com";
    public static int NET_CNNT_BAIDU_OK = 1;
    public static int NET_CNNT_BAIDU_TIMEOUT = 2;
    public static int NET_NOT_PREPARE = 3;
    public static int NET_ERROR = 4;
    private static int TIMEOUT = PathInterpolatorCompat.MAX_NUM_POINTS;

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info;
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
        if (manager == null || (info = manager.getActiveNetworkInfo()) == null || !info.isAvailable()) {
            return false;
        }
        return true;
    }

    public static String getLocalIpAddress() {
        String ret = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ret = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static int getNetState(Context context) {
        NetworkInfo networkinfo;
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivity != null && (networkinfo = connectivity.getActiveNetworkInfo()) != null) {
                if (networkinfo.isAvailable() && networkinfo.isConnected()) {
                    if (!connectionNetwork()) {
                        return NET_CNNT_BAIDU_TIMEOUT;
                    }
                    return NET_CNNT_BAIDU_OK;
                }
                return NET_NOT_PREPARE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NET_ERROR;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0025, code lost:
        if (r1 == null) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0019, code lost:
        if (r1 != null) goto L9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x002c, code lost:
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0027, code lost:
        r1.disconnect();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean connectionNetwork() {
        /*
            r0 = 0
            r1 = 0
            java.net.URL r2 = new java.net.URL     // Catch: java.lang.Throwable -> L1c java.io.IOException -> L24
            java.lang.String r3 = me.goldze.mvvmhabit.http.NetworkUtil.url     // Catch: java.lang.Throwable -> L1c java.io.IOException -> L24
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L1c java.io.IOException -> L24
            java.net.URLConnection r2 = r2.openConnection()     // Catch: java.lang.Throwable -> L1c java.io.IOException -> L24
            java.net.HttpURLConnection r2 = (java.net.HttpURLConnection) r2     // Catch: java.lang.Throwable -> L1c java.io.IOException -> L24
            r1 = r2
            int r2 = me.goldze.mvvmhabit.http.NetworkUtil.TIMEOUT     // Catch: java.lang.Throwable -> L1c java.io.IOException -> L24
            r1.setConnectTimeout(r2)     // Catch: java.lang.Throwable -> L1c java.io.IOException -> L24
            r1.connect()     // Catch: java.lang.Throwable -> L1c java.io.IOException -> L24
            r0 = 1
            if (r1 == 0) goto L2a
            goto L27
        L1c:
            r2 = move-exception
            if (r1 == 0) goto L22
            r1.disconnect()
        L22:
            r1 = 0
            throw r2
        L24:
            r2 = move-exception
            if (r1 == 0) goto L2a
        L27:
            r1.disconnect()
        L2a:
            r1 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: me.goldze.mvvmhabit.http.NetworkUtil.connectionNetwork():boolean");
    }

    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == 1;
    }

    public static boolean is2G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            return activeNetInfo.getSubtype() == 2 || activeNetInfo.getSubtype() == 1 || activeNetInfo.getSubtype() == 4;
        }
        return false;
    }

    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService("connectivity");
        TelephonyManager mgrTel = (TelephonyManager) context.getSystemService("phone");
        return (mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel.getNetworkType() == 3;
    }
}
