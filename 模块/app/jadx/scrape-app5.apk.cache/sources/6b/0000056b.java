package me.goldze.mvvmhabit.http.cookie.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/* loaded from: classes.dex */
public class MemoryCookieStore implements CookieStore {
    private final HashMap<String, List<Cookie>> memoryCookies = new HashMap<>();

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public synchronized void saveCookie(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = this.memoryCookies.get(url.host());
        List<Cookie> needRemove = new ArrayList<>();
        for (Cookie newCookie : cookies) {
            for (Cookie oldCookie : oldCookies) {
                if (newCookie.name().equals(oldCookie.name())) {
                    needRemove.add(oldCookie);
                }
            }
        }
        oldCookies.removeAll(needRemove);
        oldCookies.addAll(cookies);
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public synchronized void saveCookie(HttpUrl url, Cookie cookie) {
        List<Cookie> cookies = this.memoryCookies.get(url.host());
        List<Cookie> needRemove = new ArrayList<>();
        for (Cookie item : cookies) {
            if (cookie.name().equals(item.name())) {
                needRemove.add(item);
            }
        }
        cookies.removeAll(needRemove);
        cookies.add(cookie);
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public synchronized List<Cookie> loadCookie(HttpUrl url) {
        List<Cookie> cookies;
        cookies = this.memoryCookies.get(url.host());
        if (cookies == null) {
            cookies = new ArrayList();
            this.memoryCookies.put(url.host(), cookies);
        }
        return cookies;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public synchronized List<Cookie> getAllCookie() {
        List<Cookie> cookies;
        cookies = new ArrayList<>();
        Set<String> httpUrls = this.memoryCookies.keySet();
        for (String url : httpUrls) {
            cookies.addAll(this.memoryCookies.get(url));
        }
        return cookies;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public List<Cookie> getCookie(HttpUrl url) {
        List<Cookie> cookies = new ArrayList<>();
        List<Cookie> urlCookies = this.memoryCookies.get(url.host());
        if (urlCookies != null) {
            cookies.addAll(urlCookies);
        }
        return cookies;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public synchronized boolean removeCookie(HttpUrl url, Cookie cookie) {
        boolean z;
        List<Cookie> cookies = this.memoryCookies.get(url.host());
        if (cookie != null) {
            if (cookies.remove(cookie)) {
                z = true;
            }
        }
        z = false;
        return z;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public synchronized boolean removeCookie(HttpUrl url) {
        return this.memoryCookies.remove(url.host()) != null;
    }

    @Override // me.goldze.mvvmhabit.http.cookie.store.CookieStore
    public synchronized boolean removeAllCookie() {
        this.memoryCookies.clear();
        return true;
    }
}