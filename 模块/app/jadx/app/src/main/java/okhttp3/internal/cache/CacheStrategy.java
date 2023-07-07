package okhttp3.internal.cache;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Internal;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.http.HttpHeaders;

/* loaded from: classes.dex */
public final class CacheStrategy {
    @Nullable
    public final Response cacheResponse;
    @Nullable
    public final Request networkRequest;

    CacheStrategy(Request networkRequest, Response cacheResponse) {
        this.networkRequest = networkRequest;
        this.cacheResponse = cacheResponse;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x002e, code lost:
        if (r3.cacheControl().isPrivate() == false) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean isCacheable(okhttp3.Response r3, okhttp3.Request r4) {
        /*
            int r0 = r3.code()
            r1 = 0
            switch(r0) {
                case 200: goto L31;
                case 203: goto L31;
                case 204: goto L31;
                case 300: goto L31;
                case 301: goto L31;
                case 302: goto L9;
                case 307: goto L9;
                case 308: goto L31;
                case 404: goto L31;
                case 405: goto L31;
                case 410: goto L31;
                case 414: goto L31;
                case 501: goto L31;
                default: goto L8;
            }
        L8:
            goto L48
        L9:
            java.lang.String r0 = "Expires"
            java.lang.String r0 = r3.header(r0)
            if (r0 != 0) goto L32
            okhttp3.CacheControl r0 = r3.cacheControl()
            int r0 = r0.maxAgeSeconds()
            r2 = -1
            if (r0 != r2) goto L32
            okhttp3.CacheControl r0 = r3.cacheControl()
            boolean r0 = r0.isPublic()
            if (r0 != 0) goto L32
            okhttp3.CacheControl r0 = r3.cacheControl()
            boolean r0 = r0.isPrivate()
            if (r0 == 0) goto L48
            goto L32
        L31:
        L32:
            okhttp3.CacheControl r0 = r3.cacheControl()
            boolean r0 = r0.noStore()
            if (r0 != 0) goto L47
            okhttp3.CacheControl r0 = r4.cacheControl()
            boolean r0 = r0.noStore()
            if (r0 != 0) goto L47
            r1 = 1
        L47:
            return r1
        L48:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.CacheStrategy.isCacheable(okhttp3.Response, okhttp3.Request):boolean");
    }

    /* loaded from: classes.dex */
    public static class Factory {
        private int ageSeconds;
        final Response cacheResponse;
        private String etag;
        private Date expires;
        private Date lastModified;
        private String lastModifiedString;
        final long nowMillis;
        private long receivedResponseMillis;
        final Request request;
        private long sentRequestMillis;
        private Date servedDate;
        private String servedDateString;

        public Factory(long nowMillis, Request request, Response cacheResponse) {
            this.ageSeconds = -1;
            this.nowMillis = nowMillis;
            this.request = request;
            this.cacheResponse = cacheResponse;
            if (cacheResponse != null) {
                this.sentRequestMillis = cacheResponse.sentRequestAtMillis();
                this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
                Headers headers = cacheResponse.headers();
                int size = headers.size();
                for (int i = 0; i < size; i++) {
                    String fieldName = headers.name(i);
                    String value = headers.value(i);
                    if ("Date".equalsIgnoreCase(fieldName)) {
                        this.servedDate = HttpDate.parse(value);
                        this.servedDateString = value;
                    } else if ("Expires".equalsIgnoreCase(fieldName)) {
                        this.expires = HttpDate.parse(value);
                    } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
                        this.lastModified = HttpDate.parse(value);
                        this.lastModifiedString = value;
                    } else if ("ETag".equalsIgnoreCase(fieldName)) {
                        this.etag = value;
                    } else if ("Age".equalsIgnoreCase(fieldName)) {
                        this.ageSeconds = HttpHeaders.parseSeconds(value, -1);
                    }
                }
            }
        }

        public CacheStrategy get() {
            CacheStrategy candidate = getCandidate();
            if (candidate.networkRequest != null && this.request.cacheControl().onlyIfCached()) {
                return new CacheStrategy(null, null);
            }
            return candidate;
        }

        private CacheStrategy getCandidate() {
            Response response;
            String conditionName;
            String conditionValue;
            if (this.cacheResponse == null) {
                return new CacheStrategy(this.request, null);
            }
            if (!this.request.isHttps() || this.cacheResponse.handshake() != null) {
                if (!CacheStrategy.isCacheable(this.cacheResponse, this.request)) {
                    return new CacheStrategy(this.request, null);
                }
                CacheControl requestCaching = this.request.cacheControl();
                if (requestCaching.noCache()) {
                    response = null;
                } else if (!hasConditions(this.request)) {
                    CacheControl responseCaching = this.cacheResponse.cacheControl();
                    if (responseCaching.immutable()) {
                        return new CacheStrategy(null, this.cacheResponse);
                    }
                    long ageMillis = cacheResponseAge();
                    long freshMillis = computeFreshnessLifetime();
                    if (requestCaching.maxAgeSeconds() != -1) {
                        freshMillis = Math.min(freshMillis, TimeUnit.SECONDS.toMillis(requestCaching.maxAgeSeconds()));
                    }
                    long minFreshMillis = 0;
                    if (requestCaching.minFreshSeconds() != -1) {
                        minFreshMillis = TimeUnit.SECONDS.toMillis(requestCaching.minFreshSeconds());
                    }
                    long maxStaleMillis = 0;
                    if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {
                        maxStaleMillis = TimeUnit.SECONDS.toMillis(requestCaching.maxStaleSeconds());
                    }
                    if (!responseCaching.noCache() && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {
                        Response.Builder builder = this.cacheResponse.newBuilder();
                        if (ageMillis + minFreshMillis >= freshMillis) {
                            builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
                        }
                        if (ageMillis > 86400000 && isFreshnessLifetimeHeuristic()) {
                            builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
                        }
                        return new CacheStrategy(null, builder.build());
                    }
                    if (this.etag != null) {
                        conditionName = "If-None-Match";
                        conditionValue = this.etag;
                    } else if (this.lastModified != null) {
                        conditionName = "If-Modified-Since";
                        conditionValue = this.lastModifiedString;
                    } else if (this.servedDate != null) {
                        conditionName = "If-Modified-Since";
                        conditionValue = this.servedDateString;
                    } else {
                        return new CacheStrategy(this.request, null);
                    }
                    Headers.Builder conditionalRequestHeaders = this.request.headers().newBuilder();
                    Internal.instance.addLenient(conditionalRequestHeaders, conditionName, conditionValue);
                    Request conditionalRequest = this.request.newBuilder().headers(conditionalRequestHeaders.build()).build();
                    return new CacheStrategy(conditionalRequest, this.cacheResponse);
                } else {
                    response = null;
                }
                return new CacheStrategy(this.request, response);
            }
            return new CacheStrategy(this.request, null);
        }

        private long computeFreshnessLifetime() {
            long servedMillis;
            long servedMillis2;
            CacheControl responseCaching = this.cacheResponse.cacheControl();
            if (responseCaching.maxAgeSeconds() != -1) {
                return TimeUnit.SECONDS.toMillis(responseCaching.maxAgeSeconds());
            }
            if (this.expires != null) {
                Date date = this.servedDate;
                if (date != null) {
                    servedMillis2 = date.getTime();
                } else {
                    servedMillis2 = this.receivedResponseMillis;
                }
                long delta = this.expires.getTime() - servedMillis2;
                if (delta <= 0) {
                    return 0L;
                }
                return delta;
            } else if (this.lastModified == null || this.cacheResponse.request().url().query() != null) {
                return 0L;
            } else {
                Date date2 = this.servedDate;
                if (date2 != null) {
                    servedMillis = date2.getTime();
                } else {
                    servedMillis = this.sentRequestMillis;
                }
                long delta2 = servedMillis - this.lastModified.getTime();
                if (delta2 <= 0) {
                    return 0L;
                }
                return delta2 / 10;
            }
        }

        private long cacheResponseAge() {
            long receivedAge;
            Date date = this.servedDate;
            long j = 0;
            if (date != null) {
                j = Math.max(0L, this.receivedResponseMillis - date.getTime());
            }
            long apparentReceivedAge = j;
            if (this.ageSeconds != -1) {
                receivedAge = Math.max(apparentReceivedAge, TimeUnit.SECONDS.toMillis(this.ageSeconds));
            } else {
                receivedAge = apparentReceivedAge;
            }
            long j2 = this.receivedResponseMillis;
            long responseDuration = j2 - this.sentRequestMillis;
            long residentDuration = this.nowMillis - j2;
            return receivedAge + responseDuration + residentDuration;
        }

        private boolean isFreshnessLifetimeHeuristic() {
            return this.cacheResponse.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
        }

        private static boolean hasConditions(Request request) {
            return (request.header("If-Modified-Since") == null && request.header("If-None-Match") == null) ? false : true;
        }
    }
}
