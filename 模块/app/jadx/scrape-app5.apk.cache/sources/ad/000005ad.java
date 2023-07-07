package me.goldze.mvvmhabit.http.cookie.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/* loaded from: classes.dex */
public class PersistentCookieStore implements CookieStore {
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    private static final String COOKIE_PREFS = "habit_cookie";
    private static final String LOG_TAG = "PersistentCookieStore";
    private final SharedPreferences cookiePrefs;
    private final HashMap<String, ConcurrentHashMap<String, Cookie>> cookies = new HashMap<>();

    public PersistentCookieStore(Context context) {
        Cookie decodedCookie;
        this.cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        Map<String, ?> prefsMap = this.cookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            if (entry.getValue() != null && !entry.getKey().startsWith(COOKIE_NAME_PREFIX)) {
                String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
                for (String name : cookieNames) {
                    String encodedCookie = this.cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                    if (encodedCookie != null && (decodedCookie = decodeCookie(encodedCookie)) != null) {
                        if (!this.cookies.containsKey(entry.getKey())) {
                            this.cookies.put(entry.getKey(), new ConcurrentHashMap<>());
                        }
                        this.cookies.get(entry.getKey()).put(name, decodedCookie);
                    }
                }
            }
        }
    }

    private String getCookieToken(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public List<Cookie> loadCookie(HttpUrl url) {
        ArrayList<Cookie> ret = new ArrayList<>();
        if (this.cookies.containsKey(url.host())) {
            Collection<Cookie> urlCookies = this.cookies.get(url.host()).values();
            for (Cookie cookie : urlCookies) {
                if (isCookieExpired(cookie)) {
                    removeCookie(url, cookie);
                } else {
                    ret.add(cookie);
                }
            }
        }
        return ret;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public void saveCookie(HttpUrl url, List<Cookie> urlCookies) {
        if (!this.cookies.containsKey(url.host())) {
            this.cookies.put(url.host(), new ConcurrentHashMap<>());
        }
        for (Cookie cookie : urlCookies) {
            if (isCookieExpired(cookie)) {
                removeCookie(url, cookie);
            } else {
                saveCookie(url, cookie, getCookieToken(cookie));
            }
        }
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public void saveCookie(HttpUrl url, Cookie cookie) {
        if (!this.cookies.containsKey(url.host())) {
            this.cookies.put(url.host(), new ConcurrentHashMap<>());
        }
        if (isCookieExpired(cookie)) {
            removeCookie(url, cookie);
        } else {
            saveCookie(url, cookie, getCookieToken(cookie));
        }
    }

    private void saveCookie(HttpUrl url, Cookie cookie, String name) {
        this.cookies.get(url.host()).put(name, cookie);
        SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
        prefsWriter.putString(url.host(), TextUtils.join(",", this.cookies.get(url.host()).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableHttpCookie(cookie)));
        prefsWriter.apply();
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public boolean removeCookie(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);
        if (this.cookies.containsKey(url.host()) && this.cookies.get(url.host()).containsKey(name)) {
            this.cookies.get(url.host()).remove(name);
            SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
            SharedPreferences sharedPreferences = this.cookiePrefs;
            if (sharedPreferences.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);
            }
            prefsWriter.putString(url.host(), TextUtils.join(",", this.cookies.get(url.host()).keySet()));
            prefsWriter.apply();
            return true;
        }
        return false;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public boolean removeCookie(HttpUrl url) {
        if (this.cookies.containsKey(url.host())) {
            Set<String> cookieNames = this.cookies.get(url.host()).keySet();
            SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
            for (String cookieName : cookieNames) {
                SharedPreferences sharedPreferences = this.cookiePrefs;
                if (sharedPreferences.contains(COOKIE_NAME_PREFIX + cookieName)) {
                    prefsWriter.remove(COOKIE_NAME_PREFIX + cookieName);
                }
            }
            prefsWriter.remove(url.host()).apply();
            this.cookies.remove(url.host());
            return true;
        }
        return false;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public boolean removeAllCookie() {
        SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
        prefsWriter.clear().apply();
        this.cookies.clear();
        return true;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public List<Cookie> getAllCookie() {
        List<Cookie> ret = new ArrayList<>();
        for (String key : this.cookies.keySet()) {
            ret.addAll(this.cookies.get(key).values());
        }
        return ret;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public List<Cookie> getCookie(HttpUrl url) {
        List<Cookie> ret = new ArrayList<>();
        Map<String, Cookie> mapCookie = this.cookies.get(url.host());
        if (mapCookie != null) {
            ret.addAll(mapCookie.values());
        }
        return ret;
    }

    private String encodeCookie(SerializableHttpCookie cookie) {
        if (cookie == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
            return byteArrayToHexString(os.toByteArray());
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e);
            return null;
        }
    }

    private Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Cookie cookie = ((SerializableHttpCookie) objectInputStream.readObject()).getCookie();
            return cookie;
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e);
            return null;
        } catch (ClassNotFoundException e2) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e2);
            return null;
        }
    }

    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 255;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    private byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}