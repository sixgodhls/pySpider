package okhttp3.internal.http;

import android.support.v7.widget.ActivityChooserView;
import com.bumptech.glide.load.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Challenge;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

/* loaded from: classes.dex */
public final class HttpHeaders {
    private static final Pattern PARAMETER = Pattern.compile(" +([^ \"=]*)=(:?\"([^\"]*)\"|([^ \"=]*)) *(:?,|$)");
    private static final String QUOTED_STRING = "\"([^\"]*)\"";
    private static final String TOKEN = "([^ \"=]*)";

    private HttpHeaders() {
    }

    public static long contentLength(Response response) {
        return contentLength(response.headers());
    }

    public static long contentLength(Headers headers) {
        return stringToLong(headers.get("Content-Length"));
    }

    private static long stringToLong(String s) {
        if (s == null) {
            return -1L;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    public static boolean varyMatches(Response cachedResponse, Headers cachedRequest, Request newRequest) {
        for (String field : varyFields(cachedResponse)) {
            if (!Util.equal(cachedRequest.values(field), newRequest.headers(field))) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasVaryAll(Response response) {
        return hasVaryAll(response.headers());
    }

    public static boolean hasVaryAll(Headers responseHeaders) {
        return varyFields(responseHeaders).contains("*");
    }

    private static Set<String> varyFields(Response response) {
        return varyFields(response.headers());
    }

    public static Set<String> varyFields(Headers responseHeaders) {
        String[] split;
        Set<String> result = Collections.emptySet();
        int size = responseHeaders.size();
        for (int i = 0; i < size; i++) {
            if ("Vary".equalsIgnoreCase(responseHeaders.name(i))) {
                String value = responseHeaders.value(i);
                if (result.isEmpty()) {
                    result = new TreeSet(String.CASE_INSENSITIVE_ORDER);
                }
                for (String varyField : value.split(",")) {
                    result.add(varyField.trim());
                }
            }
        }
        return result;
    }

    public static Headers varyHeaders(Response response) {
        Headers requestHeaders = response.networkResponse().request().headers();
        Headers responseHeaders = response.headers();
        return varyHeaders(requestHeaders, responseHeaders);
    }

    public static Headers varyHeaders(Headers requestHeaders, Headers responseHeaders) {
        Set<String> varyFields = varyFields(responseHeaders);
        if (varyFields.isEmpty()) {
            return new Headers.Builder().build();
        }
        Headers.Builder result = new Headers.Builder();
        int size = requestHeaders.size();
        for (int i = 0; i < size; i++) {
            String fieldName = requestHeaders.name(i);
            if (varyFields.contains(fieldName)) {
                result.add(fieldName, requestHeaders.value(i));
            }
        }
        return result.build();
    }

    public static List<Challenge> parseChallenges(Headers responseHeaders, String challengeHeader) {
        List<String> authenticationHeaders;
        Iterator<String> it;
        List<Challenge> challenges = new ArrayList<>();
        List<String> authenticationHeaders2 = responseHeaders.values(challengeHeader);
        Iterator<String> it2 = authenticationHeaders2.iterator();
        while (it2.hasNext()) {
            String header = it2.next();
            int index = header.indexOf(32);
            if (index != -1) {
                String scheme = header.substring(0, index);
                Matcher matcher = PARAMETER.matcher(header);
                String realm = null;
                String charset = null;
                int i = index;
                while (true) {
                    if (!matcher.find(i)) {
                        authenticationHeaders = authenticationHeaders2;
                        it = it2;
                        break;
                    }
                    authenticationHeaders = authenticationHeaders2;
                    if (header.regionMatches(true, matcher.start(1), "realm", 0, 5)) {
                        realm = matcher.group(3);
                        it = it2;
                    } else {
                        it = it2;
                        if (header.regionMatches(true, matcher.start(1), "charset", 0, 7)) {
                            charset = matcher.group(3);
                        }
                    }
                    if (realm != null && charset != null) {
                        break;
                    }
                    i = matcher.end();
                    it2 = it;
                    authenticationHeaders2 = authenticationHeaders;
                }
                if (realm == null) {
                    it2 = it;
                    authenticationHeaders2 = authenticationHeaders;
                } else {
                    Challenge challenge = new Challenge(scheme, realm);
                    if (charset != null) {
                        if (!charset.equalsIgnoreCase(Key.STRING_CHARSET_NAME)) {
                            it2 = it;
                            authenticationHeaders2 = authenticationHeaders;
                        } else {
                            challenge = challenge.withCharset(Util.UTF_8);
                        }
                    }
                    challenges.add(challenge);
                    it2 = it;
                    authenticationHeaders2 = authenticationHeaders;
                }
            }
        }
        return challenges;
    }

    public static void receiveHeaders(CookieJar cookieJar, HttpUrl url, Headers headers) {
        if (cookieJar == CookieJar.NO_COOKIES) {
            return;
        }
        List<Cookie> cookies = Cookie.parseAll(url, headers);
        if (cookies.isEmpty()) {
            return;
        }
        cookieJar.saveFromResponse(url, cookies);
    }

    public static boolean hasBody(Response response) {
        if (response.request().method().equals("HEAD")) {
            return false;
        }
        int responseCode = response.code();
        return (((responseCode >= 100 && responseCode < 200) || responseCode == 204 || responseCode == 304) && contentLength(response) == -1 && !"chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) ? false : true;
    }

    public static int skipUntil(String input, int pos, String characters) {
        while (pos < input.length() && characters.indexOf(input.charAt(pos)) == -1) {
            pos++;
        }
        return pos;
    }

    public static int skipWhitespace(String input, int pos) {
        char c;
        while (pos < input.length() && ((c = input.charAt(pos)) == ' ' || c == '\t')) {
            pos++;
        }
        return pos;
    }

    public static int parseSeconds(String value, int defaultValue) {
        try {
            long seconds = Long.parseLong(value);
            if (seconds > 2147483647L) {
                return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            }
            if (seconds < 0) {
                return 0;
            }
            return (int) seconds;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
