package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.ws.WebSocketReader;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

/* loaded from: classes.dex */
public final class RealWebSocket implements WebSocket, WebSocketReader.FrameCallback {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000;
    private static final long MAX_QUEUE_SIZE = 16777216;
    private static final List<Protocol> ONLY_HTTP1 = Collections.singletonList(Protocol.HTTP_1_1);
    private boolean awaitingPong;
    private Call call;
    private ScheduledFuture<?> cancelFuture;
    private boolean enqueuedClose;
    private ScheduledExecutorService executor;
    private boolean failed;
    private final String key;
    final WebSocketListener listener;
    private final Request originalRequest;
    private final long pingIntervalMillis;
    private long queueSize;
    private final Random random;
    private WebSocketReader reader;
    private String receivedCloseReason;
    private int receivedPingCount;
    private int receivedPongCount;
    private int sentPingCount;
    private Streams streams;
    private WebSocketWriter writer;
    private final Runnable writerRunnable;
    private final ArrayDeque<ByteString> pongQueue = new ArrayDeque<>();
    private final ArrayDeque<Object> messageAndCloseQueue = new ArrayDeque<>();
    private int receivedCloseCode = -1;

    public RealWebSocket(Request request, WebSocketListener listener, Random random, long pingIntervalMillis) {
        if (!"GET".equals(request.method())) {
            throw new IllegalArgumentException("Request must be GET: " + request.method());
        }
        this.originalRequest = request;
        this.listener = listener;
        this.random = random;
        this.pingIntervalMillis = pingIntervalMillis;
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);
        this.key = ByteString.of(nonce).base64();
        this.writerRunnable = new Runnable() { // from class: okhttp3.internal.ws.RealWebSocket.1
            @Override // java.lang.Runnable
            public void run() {
                do {
                    try {
                    } catch (IOException e) {
                        RealWebSocket.this.failWebSocket(e, null);
                        return;
                    }
                } while (RealWebSocket.this.writeOneFrame());
            }
        };
    }

    @Override // okhttp3.WebSocket
    public Request request() {
        return this.originalRequest;
    }

    @Override // okhttp3.WebSocket
    public synchronized long queueSize() {
        return this.queueSize;
    }

    @Override // okhttp3.WebSocket
    public void cancel() {
        this.call.cancel();
    }

    public void connect(OkHttpClient client) {
        OkHttpClient client2 = client.newBuilder().eventListener(EventListener.NONE).protocols(ONLY_HTTP1).build();
        final Request request = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").build();
        this.call = Internal.instance.newWebSocketCall(client2, request);
        this.call.enqueue(new Callback() { // from class: okhttp3.internal.ws.RealWebSocket.2
            @Override // okhttp3.Callback
            public void onResponse(Call call, Response response) {
                try {
                    RealWebSocket.this.checkResponse(response);
                    StreamAllocation streamAllocation = Internal.instance.streamAllocation(call);
                    streamAllocation.noNewStreams();
                    Streams streams = streamAllocation.connection().newWebSocketStreams(streamAllocation);
                    try {
                        RealWebSocket.this.listener.onOpen(RealWebSocket.this, response);
                        String name = "OkHttp WebSocket " + request.url().redact();
                        RealWebSocket.this.initReaderAndWriter(name, streams);
                        streamAllocation.connection().socket().setSoTimeout(0);
                        RealWebSocket.this.loopReader();
                    } catch (Exception e) {
                        RealWebSocket.this.failWebSocket(e, null);
                    }
                } catch (ProtocolException e2) {
                    RealWebSocket.this.failWebSocket(e2, response);
                    Util.closeQuietly(response);
                }
            }

            @Override // okhttp3.Callback
            public void onFailure(Call call, IOException e) {
                RealWebSocket.this.failWebSocket(e, null);
            }
        });
    }

    void checkResponse(Response response) throws ProtocolException {
        if (response.code() != 101) {
            throw new ProtocolException("Expected HTTP 101 response but was '" + response.code() + " " + response.message() + "'");
        }
        String headerConnection = response.header("Connection");
        if (!"Upgrade".equalsIgnoreCase(headerConnection)) {
            throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '" + headerConnection + "'");
        }
        String headerUpgrade = response.header("Upgrade");
        if (!"websocket".equalsIgnoreCase(headerUpgrade)) {
            throw new ProtocolException("Expected 'Upgrade' header value 'websocket' but was '" + headerUpgrade + "'");
        }
        String headerAccept = response.header("Sec-WebSocket-Accept");
        String acceptExpected = ByteString.encodeUtf8(this.key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
        if (!acceptExpected.equals(headerAccept)) {
            throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '" + acceptExpected + "' but was '" + headerAccept + "'");
        }
    }

    public void initReaderAndWriter(String name, Streams streams) throws IOException {
        synchronized (this) {
            this.streams = streams;
            this.writer = new WebSocketWriter(streams.client, streams.sink, this.random);
            this.executor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(name, false));
            if (this.pingIntervalMillis != 0) {
                this.executor.scheduleAtFixedRate(new PingRunnable(), this.pingIntervalMillis, this.pingIntervalMillis, TimeUnit.MILLISECONDS);
            }
            if (!this.messageAndCloseQueue.isEmpty()) {
                runWriter();
            }
        }
        this.reader = new WebSocketReader(streams.client, streams.source, this);
    }

    public void loopReader() throws IOException {
        while (this.receivedCloseCode == -1) {
            this.reader.processNextFrame();
        }
    }

    boolean processNextFrame() throws IOException {
        try {
            this.reader.processNextFrame();
            return this.receivedCloseCode == -1;
        } catch (Exception e) {
            failWebSocket(e, null);
            return false;
        }
    }

    void awaitTermination(int timeout, TimeUnit timeUnit) throws InterruptedException {
        this.executor.awaitTermination(timeout, timeUnit);
    }

    void tearDown() throws InterruptedException {
        ScheduledFuture<?> scheduledFuture = this.cancelFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        this.executor.shutdown();
        this.executor.awaitTermination(10L, TimeUnit.SECONDS);
    }

    synchronized int sentPingCount() {
        return this.sentPingCount;
    }

    synchronized int receivedPingCount() {
        return this.receivedPingCount;
    }

    synchronized int receivedPongCount() {
        return this.receivedPongCount;
    }

    @Override // okhttp3.internal.ws.WebSocketReader.FrameCallback
    public void onReadMessage(String text) throws IOException {
        this.listener.onMessage(this, text);
    }

    @Override // okhttp3.internal.ws.WebSocketReader.FrameCallback
    public void onReadMessage(ByteString bytes) throws IOException {
        this.listener.onMessage(this, bytes);
    }

    @Override // okhttp3.internal.ws.WebSocketReader.FrameCallback
    public synchronized void onReadPing(ByteString payload) {
        if (!this.failed && (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty())) {
            this.pongQueue.add(payload);
            runWriter();
            this.receivedPingCount++;
        }
    }

    @Override // okhttp3.internal.ws.WebSocketReader.FrameCallback
    public synchronized void onReadPong(ByteString buffer) {
        this.receivedPongCount++;
        this.awaitingPong = false;
    }

    @Override // okhttp3.internal.ws.WebSocketReader.FrameCallback
    public void onReadClose(int code, String reason) {
        if (code == -1) {
            throw new IllegalArgumentException();
        }
        Streams toClose = null;
        synchronized (this) {
            if (this.receivedCloseCode != -1) {
                throw new IllegalStateException("already closed");
            }
            this.receivedCloseCode = code;
            this.receivedCloseReason = reason;
            if (this.enqueuedClose && this.messageAndCloseQueue.isEmpty()) {
                toClose = this.streams;
                this.streams = null;
                if (this.cancelFuture != null) {
                    this.cancelFuture.cancel(false);
                }
                this.executor.shutdown();
            }
        }
        try {
            this.listener.onClosing(this, code, reason);
            if (toClose != null) {
                this.listener.onClosed(this, code, reason);
            }
        } finally {
            Util.closeQuietly(toClose);
        }
    }

    @Override // okhttp3.WebSocket
    public boolean send(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }
        return send(ByteString.encodeUtf8(text), 1);
    }

    @Override // okhttp3.WebSocket
    public boolean send(ByteString bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        return send(bytes, 2);
    }

    private synchronized boolean send(ByteString data, int formatOpcode) {
        if (!this.failed && !this.enqueuedClose) {
            if (this.queueSize + data.size() > MAX_QUEUE_SIZE) {
                close(1001, null);
                return false;
            }
            this.queueSize += data.size();
            this.messageAndCloseQueue.add(new Message(formatOpcode, data));
            runWriter();
            return true;
        }
        return false;
    }

    synchronized boolean pong(ByteString payload) {
        if (!this.failed && (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty())) {
            this.pongQueue.add(payload);
            runWriter();
            return true;
        }
        return false;
    }

    @Override // okhttp3.WebSocket
    public boolean close(int code, String reason) {
        return close(code, reason, CANCEL_AFTER_CLOSE_MILLIS);
    }

    synchronized boolean close(int code, String reason, long cancelAfterCloseMillis) {
        WebSocketProtocol.validateCloseCode(code);
        ByteString reasonBytes = null;
        if (reason != null) {
            reasonBytes = ByteString.encodeUtf8(reason);
            if (reasonBytes.size() > 123) {
                throw new IllegalArgumentException("reason.size() > 123: " + reason);
            }
        }
        if (!this.failed && !this.enqueuedClose) {
            this.enqueuedClose = true;
            this.messageAndCloseQueue.add(new Close(code, reasonBytes, cancelAfterCloseMillis));
            runWriter();
            return true;
        }
        return false;
    }

    private void runWriter() {
        ScheduledExecutorService scheduledExecutorService = this.executor;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.execute(this.writerRunnable);
        }
    }

    boolean writeOneFrame() throws IOException {
        Object messageOrClose = null;
        int receivedCloseCode = -1;
        String receivedCloseReason = null;
        Streams streamsToClose = null;
        synchronized (this) {
            if (this.failed) {
                return false;
            }
            WebSocketWriter writer = this.writer;
            ByteString pong = this.pongQueue.poll();
            if (pong == null) {
                messageOrClose = this.messageAndCloseQueue.poll();
                if (messageOrClose instanceof Close) {
                    receivedCloseCode = this.receivedCloseCode;
                    receivedCloseReason = this.receivedCloseReason;
                    if (receivedCloseCode != -1) {
                        streamsToClose = this.streams;
                        this.streams = null;
                        this.executor.shutdown();
                    } else {
                        this.cancelFuture = this.executor.schedule(new CancelRunnable(), ((Close) messageOrClose).cancelAfterCloseMillis, TimeUnit.MILLISECONDS);
                    }
                } else if (messageOrClose == null) {
                    return false;
                }
            }
            try {
                if (pong != null) {
                    writer.writePong(pong);
                } else if (messageOrClose instanceof Message) {
                    ByteString data = ((Message) messageOrClose).data;
                    BufferedSink sink = Okio.buffer(writer.newMessageSink(((Message) messageOrClose).formatOpcode, data.size()));
                    sink.mo351write(data);
                    sink.close();
                    synchronized (this) {
                        this.queueSize -= data.size();
                    }
                } else if (messageOrClose instanceof Close) {
                    Close close = (Close) messageOrClose;
                    writer.writeClose(close.code, close.reason);
                    if (streamsToClose != null) {
                        this.listener.onClosed(this, receivedCloseCode, receivedCloseReason);
                    }
                } else {
                    throw new AssertionError();
                }
                return true;
            } finally {
                Util.closeQuietly(streamsToClose);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class PingRunnable implements Runnable {
        PingRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            RealWebSocket.this.writePingFrame();
        }
    }

    void writePingFrame() {
        synchronized (this) {
            if (this.failed) {
                return;
            }
            WebSocketWriter writer = this.writer;
            int failedPing = this.awaitingPong ? this.sentPingCount : -1;
            this.sentPingCount++;
            this.awaitingPong = true;
            if (failedPing != -1) {
                StringBuilder sb = new StringBuilder();
                sb.append("sent ping but didn't receive pong within ");
                sb.append(this.pingIntervalMillis);
                sb.append("ms (after ");
                sb.append(failedPing - 1);
                sb.append(" successful ping/pongs)");
                failWebSocket(new SocketTimeoutException(sb.toString()), null);
                return;
            }
            try {
                writer.writePing(ByteString.EMPTY);
            } catch (IOException e) {
                failWebSocket(e, null);
            }
        }
    }

    public void failWebSocket(Exception e, @Nullable Response response) {
        synchronized (this) {
            if (this.failed) {
                return;
            }
            this.failed = true;
            Streams streamsToClose = this.streams;
            this.streams = null;
            if (this.cancelFuture != null) {
                this.cancelFuture.cancel(false);
            }
            if (this.executor != null) {
                this.executor.shutdown();
            }
            try {
                this.listener.onFailure(this, e, response);
            } finally {
                Util.closeQuietly(streamsToClose);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Message {
        final ByteString data;
        final int formatOpcode;

        Message(int formatOpcode, ByteString data) {
            this.formatOpcode = formatOpcode;
            this.data = data;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Close {
        final long cancelAfterCloseMillis;
        final int code;
        final ByteString reason;

        Close(int code, ByteString reason, long cancelAfterCloseMillis) {
            this.code = code;
            this.reason = reason;
            this.cancelAfterCloseMillis = cancelAfterCloseMillis;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Streams implements Closeable {
        public final boolean client;
        public final BufferedSink sink;
        public final BufferedSource source;

        public Streams(boolean client, BufferedSource source, BufferedSink sink) {
            this.client = client;
            this.source = source;
            this.sink = sink;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class CancelRunnable implements Runnable {
        CancelRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            RealWebSocket.this.cancel();
        }
    }
}
