package okhttp3;

import android.support.v4.app.NotificationCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.connection.ConnectInterceptor;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import okhttp3.internal.platform.Platform;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RealCall implements Call {
    final OkHttpClient client;
    private EventListener eventListener;
    private boolean executed;
    final boolean forWebSocket;
    final Request originalRequest;
    final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;

    private RealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
        this.client = client;
        this.originalRequest = originalRequest;
        this.forWebSocket = forWebSocket;
        this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client, forWebSocket);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RealCall newRealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
        RealCall call = new RealCall(client, originalRequest, forWebSocket);
        call.eventListener = client.eventListenerFactory().create(call);
        return call;
    }

    @Override // okhttp3.Call
    public Request request() {
        return this.originalRequest;
    }

    @Override // okhttp3.Call
    public Response execute() throws IOException {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }
        captureCallStackTrace();
        this.eventListener.callStart(this);
        try {
            try {
                this.client.dispatcher().executed(this);
                Response result = getResponseWithInterceptorChain();
                if (result == null) {
                    throw new IOException("Canceled");
                }
                return result;
            } catch (IOException e) {
                this.eventListener.callFailed(this, e);
                throw e;
            }
        } finally {
            this.client.dispatcher().finished(this);
        }
    }

    private void captureCallStackTrace() {
        Object callStackTrace = Platform.get().getStackTraceForCloseable("response.body().close()");
        this.retryAndFollowUpInterceptor.setCallStackTrace(callStackTrace);
    }

    @Override // okhttp3.Call
    public void enqueue(Callback responseCallback) {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }
        captureCallStackTrace();
        this.eventListener.callStart(this);
        this.client.dispatcher().enqueue(new AsyncCall(responseCallback));
    }

    @Override // okhttp3.Call
    public void cancel() {
        this.retryAndFollowUpInterceptor.cancel();
    }

    @Override // okhttp3.Call
    public synchronized boolean isExecuted() {
        return this.executed;
    }

    @Override // okhttp3.Call
    public boolean isCanceled() {
        return this.retryAndFollowUpInterceptor.isCanceled();
    }

    @Override // okhttp3.Call
    /* renamed from: clone */
    public RealCall mo348clone() {
        return newRealCall(this.client, this.originalRequest, this.forWebSocket);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StreamAllocation streamAllocation() {
        return this.retryAndFollowUpInterceptor.streamAllocation();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class AsyncCall extends NamedRunnable {
        private final Callback responseCallback;

        AsyncCall(Callback responseCallback) {
            super("OkHttp %s", RealCall.this.redactedUrl());
            this.responseCallback = responseCallback;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String host() {
            return RealCall.this.originalRequest.url().host();
        }

        Request request() {
            return RealCall.this.originalRequest;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public RealCall get() {
            return RealCall.this;
        }

        @Override // okhttp3.internal.NamedRunnable
        protected void execute() {
            boolean signalledCallback = false;
            try {
                try {
                    Response response = RealCall.this.getResponseWithInterceptorChain();
                    if (RealCall.this.retryAndFollowUpInterceptor.isCanceled()) {
                        signalledCallback = true;
                        this.responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
                    } else {
                        signalledCallback = true;
                        this.responseCallback.onResponse(RealCall.this, response);
                    }
                } catch (IOException e) {
                    if (!signalledCallback) {
                        RealCall.this.eventListener.callFailed(RealCall.this, e);
                        this.responseCallback.onFailure(RealCall.this, e);
                    } else {
                        Platform platform = Platform.get();
                        platform.log(4, "Callback failure for " + RealCall.this.toLoggableString(), e);
                    }
                }
            } finally {
                RealCall.this.client.dispatcher().finished(this);
            }
        }
    }

    String toLoggableString() {
        StringBuilder sb = new StringBuilder();
        sb.append(isCanceled() ? "canceled " : "");
        sb.append(this.forWebSocket ? "web socket" : NotificationCompat.CATEGORY_CALL);
        sb.append(" to ");
        sb.append(redactedUrl());
        return sb.toString();
    }

    String redactedUrl() {
        return this.originalRequest.url().redact();
    }

    Response getResponseWithInterceptorChain() throws IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(this.client.interceptors());
        interceptors.add(this.retryAndFollowUpInterceptor);
        interceptors.add(new BridgeInterceptor(this.client.cookieJar()));
        interceptors.add(new CacheInterceptor(this.client.internalCache()));
        interceptors.add(new ConnectInterceptor(this.client));
        if (!this.forWebSocket) {
            interceptors.addAll(this.client.networkInterceptors());
        }
        interceptors.add(new CallServerInterceptor(this.forWebSocket));
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0, this.originalRequest, this, this.eventListener, this.client.connectTimeoutMillis(), this.client.readTimeoutMillis(), this.client.writeTimeoutMillis());
        return chain.proceed(this.originalRequest);
    }
}
