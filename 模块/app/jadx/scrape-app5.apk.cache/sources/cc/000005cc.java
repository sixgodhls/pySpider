package me.goldze.mvvmhabit.http.cookie;

import java.util.List;
import me.goldze.mvvmhabit.http.cookie.store.CookieStore;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/* loaded from: classes.dex */
public class CookieJarImpl implements CookieJar {
    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null!");
        }
        this.cookieStore = cookieStore;
    }

    @Override // okhttp3.CookieJar
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.cookieStore.saveCookie(url, cookies);
    }

    @Override // okhttp3.CookieJar
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return this.cookieStore.loadCookie(url);
    }

    public CookieStore getCookieStore() {
        return this.cookieStore;
    }
}