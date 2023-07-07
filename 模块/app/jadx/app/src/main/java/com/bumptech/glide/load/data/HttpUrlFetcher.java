package com.bumptech.glide.load.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.bumptech.glide.util.LogTime;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/* loaded from: classes.dex */
public class HttpUrlFetcher implements DataFetcher<InputStream> {
    @VisibleForTesting
    static final HttpUrlConnectionFactory DEFAULT_CONNECTION_FACTORY = new DefaultHttpUrlConnectionFactory();
    private static final int INVALID_STATUS_CODE = -1;
    private static final int MAXIMUM_REDIRECTS = 5;
    private static final String TAG = "HttpUrlFetcher";
    private final HttpUrlConnectionFactory connectionFactory;
    private final GlideUrl glideUrl;
    private volatile boolean isCancelled;
    private InputStream stream;
    private final int timeout;
    private HttpURLConnection urlConnection;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface HttpUrlConnectionFactory {
        HttpURLConnection build(URL url) throws IOException;
    }

    public HttpUrlFetcher(GlideUrl glideUrl, int timeout) {
        this(glideUrl, timeout, DEFAULT_CONNECTION_FACTORY);
    }

    @VisibleForTesting
    HttpUrlFetcher(GlideUrl glideUrl, int timeout, HttpUrlConnectionFactory connectionFactory) {
        this.glideUrl = glideUrl;
        this.timeout = timeout;
        this.connectionFactory = connectionFactory;
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public void loadData(@NonNull Priority priority, @NonNull DataFetcher.DataCallback<? super InputStream> callback) {
        String str;
        StringBuilder sb;
        long startTime = LogTime.getLogTime();
        try {
            try {
                InputStream result = loadDataWithRedirects(this.glideUrl.toURL(), 0, null, this.glideUrl.getHeaders());
                callback.onDataReady(result);
            } catch (IOException e) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Failed to load data for url", e);
                }
                callback.onLoadFailed(e);
                if (!Log.isLoggable(TAG, 2)) {
                    return;
                }
                str = TAG;
                sb = new StringBuilder();
            }
            if (!Log.isLoggable(TAG, 2)) {
                return;
            }
            str = TAG;
            sb = new StringBuilder();
            sb.append("Finished http url fetcher fetch in ");
            sb.append(LogTime.getElapsedMillis(startTime));
            Log.v(str, sb.toString());
        } catch (Throwable th) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Finished http url fetcher fetch in " + LogTime.getElapsedMillis(startTime));
            }
            throw th;
        }
    }

    private InputStream loadDataWithRedirects(URL url, int redirects, URL lastUrl, Map<String, String> headers) throws IOException {
        if (redirects >= 5) {
            throw new HttpException("Too many (> 5) redirects!");
        }
        if (lastUrl != null) {
            try {
                if (url.toURI().equals(lastUrl.toURI())) {
                    throw new HttpException("In re-direct loop");
                }
            } catch (URISyntaxException e) {
            }
        }
        this.urlConnection = this.connectionFactory.build(url);
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            this.urlConnection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
        }
        this.urlConnection.setConnectTimeout(this.timeout);
        this.urlConnection.setReadTimeout(this.timeout);
        this.urlConnection.setUseCaches(false);
        this.urlConnection.setDoInput(true);
        this.urlConnection.setInstanceFollowRedirects(false);
        this.urlConnection.connect();
        this.stream = this.urlConnection.getInputStream();
        if (this.isCancelled) {
            return null;
        }
        int statusCode = this.urlConnection.getResponseCode();
        if (isHttpOk(statusCode)) {
            return getStreamForSuccessfulRequest(this.urlConnection);
        }
        if (isHttpRedirect(statusCode)) {
            String redirectUrlString = this.urlConnection.getHeaderField("Location");
            if (TextUtils.isEmpty(redirectUrlString)) {
                throw new HttpException("Received empty or null redirect url");
            }
            URL redirectUrl = new URL(url, redirectUrlString);
            cleanup();
            return loadDataWithRedirects(redirectUrl, redirects + 1, url, headers);
        } else if (statusCode == -1) {
            throw new HttpException(statusCode);
        } else {
            throw new HttpException(this.urlConnection.getResponseMessage(), statusCode);
        }
    }

    private static boolean isHttpOk(int statusCode) {
        return statusCode / 100 == 2;
    }

    private static boolean isHttpRedirect(int statusCode) {
        return statusCode / 100 == 3;
    }

    private InputStream getStreamForSuccessfulRequest(HttpURLConnection urlConnection) throws IOException {
        if (TextUtils.isEmpty(urlConnection.getContentEncoding())) {
            int contentLength = urlConnection.getContentLength();
            this.stream = ContentLengthInputStream.obtain(urlConnection.getInputStream(), contentLength);
        } else {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Got non empty content encoding: " + urlConnection.getContentEncoding());
            }
            this.stream = urlConnection.getInputStream();
        }
        return this.stream;
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public void cleanup() {
        InputStream inputStream = this.stream;
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        HttpURLConnection httpURLConnection = this.urlConnection;
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        this.urlConnection = null;
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public void cancel() {
        this.isCancelled = true;
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    @NonNull
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    @NonNull
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }

    /* loaded from: classes.dex */
    private static class DefaultHttpUrlConnectionFactory implements HttpUrlConnectionFactory {
        DefaultHttpUrlConnectionFactory() {
        }

        @Override // com.bumptech.glide.load.data.HttpUrlFetcher.HttpUrlConnectionFactory
        public HttpURLConnection build(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    }
}
