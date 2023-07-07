package okhttp3.internal.http2;

import android.support.v4.internal.view.SupportMenu;
import android.support.v7.widget.ActivityChooserView;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Protocol;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.Util;
import okhttp3.internal.http2.Http2Reader;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

/* loaded from: classes.dex */
public final class Http2Connection implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
    private static final ExecutorService listenerExecutor = new ThreadPoolExecutor(0, (int) ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp Http2Connection", true));
    private boolean awaitingPong;
    long bytesLeftInWriteWindow;
    final boolean client;
    final String hostname;
    int lastGoodStreamId;
    final Listener listener;
    int nextStreamId;
    private final ExecutorService pushExecutor;
    final PushObserver pushObserver;
    final ReaderRunnable readerRunnable;
    boolean shutdown;
    final Socket socket;
    final Http2Writer writer;
    private final ScheduledExecutorService writerExecutor;
    final Map<Integer, Http2Stream> streams = new LinkedHashMap();
    long unacknowledgedBytesRead = 0;
    Settings okHttpSettings = new Settings();
    final Settings peerSettings = new Settings();
    boolean receivedInitialPeerSettings = false;
    final Set<Integer> currentPushRequests = new LinkedHashSet();

    Http2Connection(Builder builder) {
        this.pushObserver = builder.pushObserver;
        this.client = builder.client;
        this.listener = builder.listener;
        this.nextStreamId = builder.client ? 1 : 2;
        if (builder.client) {
            this.nextStreamId += 2;
        }
        if (builder.client) {
            this.okHttpSettings.set(7, 16777216);
        }
        this.hostname = builder.hostname;
        this.writerExecutor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(Util.format("OkHttp %s Writer", this.hostname), false));
        if (builder.pingIntervalMillis != 0) {
            this.writerExecutor.scheduleAtFixedRate(new PingRunnable(false, 0, 0), builder.pingIntervalMillis, builder.pingIntervalMillis, TimeUnit.MILLISECONDS);
        }
        this.pushExecutor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory(Util.format("OkHttp %s Push Observer", this.hostname), true));
        this.peerSettings.set(7, SupportMenu.USER_MASK);
        this.peerSettings.set(5, 16384);
        this.bytesLeftInWriteWindow = this.peerSettings.getInitialWindowSize();
        this.socket = builder.socket;
        this.writer = new Http2Writer(builder.sink, this.client);
        this.readerRunnable = new ReaderRunnable(new Http2Reader(builder.source, this.client));
    }

    public Protocol getProtocol() {
        return Protocol.HTTP_2;
    }

    public synchronized int openStreamCount() {
        return this.streams.size();
    }

    synchronized Http2Stream getStream(int id) {
        return this.streams.get(Integer.valueOf(id));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized Http2Stream removeStream(int streamId) {
        Http2Stream stream;
        stream = this.streams.remove(Integer.valueOf(streamId));
        notifyAll();
        return stream;
    }

    public synchronized int maxConcurrentStreams() {
        return this.peerSettings.getMaxConcurrentStreams(ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public Http2Stream pushStream(int associatedStreamId, List<Header> requestHeaders, boolean out) throws IOException {
        if (this.client) {
            throw new IllegalStateException("Client cannot push requests.");
        }
        return newStream(associatedStreamId, requestHeaders, out);
    }

    public Http2Stream newStream(List<Header> requestHeaders, boolean out) throws IOException {
        return newStream(0, requestHeaders, out);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0067 A[Catch: all -> 0x00b7, TryCatch #1 {, blocks: (B:4:0x000b, B:23:0x007c, B:24:0x0093, B:29:0x0084, B:31:0x008b, B:32:0x00a3, B:33:0x00ad, B:5:0x000d, B:7:0x0016, B:8:0x001f, B:10:0x0026, B:12:0x0045, B:14:0x004f, B:18:0x005d, B:20:0x0067, B:21:0x0076, B:35:0x00af, B:36:0x00b6), top: B:3:0x000b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private okhttp3.internal.http2.Http2Stream newStream(int r12, java.util.List<okhttp3.internal.http2.Header> r13, boolean r14) throws java.io.IOException {
        /*
            r11 = this;
            r0 = r14 ^ 1
            r7 = 0
            okhttp3.internal.http2.Http2Writer r8 = r11.writer
            monitor-enter(r8)
            monitor-enter(r11)     // Catch: java.lang.Throwable -> Lbc
            int r1 = r11.nextStreamId     // Catch: java.lang.Throwable -> Lb7
            r2 = 1073741823(0x3fffffff, float:1.9999999)
            if (r1 <= r2) goto L1e
        L16:
            okhttp3.internal.http2.ErrorCode r1 = okhttp3.internal.http2.ErrorCode.REFUSED_STREAM     // Catch: java.lang.Throwable -> Lb7
            r11.shutdown(r1)     // Catch: java.lang.Throwable -> Lb7
            goto L1f
        L1e:
        L1f:
            boolean r1 = r11.shutdown     // Catch: java.lang.Throwable -> Lb7
            if (r1 != 0) goto Lae
        L26:
            int r1 = r11.nextStreamId     // Catch: java.lang.Throwable -> Lb7
            r9 = r1
            int r1 = r11.nextStreamId     // Catch: java.lang.Throwable -> Lb7
            int r1 = r1 + 2
            r11.nextStreamId = r1     // Catch: java.lang.Throwable -> Lb7
            okhttp3.internal.http2.Http2Stream r10 = new okhttp3.internal.http2.Http2Stream     // Catch: java.lang.Throwable -> Lb7
            r1 = r10
            r2 = r9
            r3 = r11
            r4 = r0
            r5 = r7
            r6 = r13
            r1.<init>(r2, r3, r4, r5, r6)     // Catch: java.lang.Throwable -> Lb7
            r1 = r10
            if (r14 == 0) goto L5a
        L45:
            long r2 = r11.bytesLeftInWriteWindow     // Catch: java.lang.Throwable -> Lb7
            r4 = 0
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 == 0) goto L5a
        L4f:
            long r2 = r1.bytesLeftInWriteWindow     // Catch: java.lang.Throwable -> Lb7
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L57
            goto L5a
        L57:
            r2 = 0
            goto L5d
        L5a:
            r2 = 1
        L5d:
            boolean r3 = r1.isOpen()     // Catch: java.lang.Throwable -> Lb7
            if (r3 == 0) goto L74
        L67:
            java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r3 = r11.streams     // Catch: java.lang.Throwable -> Lb7
            java.lang.Integer r4 = java.lang.Integer.valueOf(r9)     // Catch: java.lang.Throwable -> Lb7
            r3.put(r4, r1)     // Catch: java.lang.Throwable -> Lb7
            goto L75
        L74:
        L75:
            monitor-exit(r11)     // Catch: java.lang.Throwable -> Lb7
            if (r12 != 0) goto L84
        L7c:
            okhttp3.internal.http2.Http2Writer r3 = r11.writer     // Catch: java.lang.Throwable -> Lbc
            r3.synStream(r0, r9, r12, r13)     // Catch: java.lang.Throwable -> Lbc
            goto L92
        L84:
            boolean r3 = r11.client     // Catch: java.lang.Throwable -> Lbc
            if (r3 != 0) goto La2
        L8b:
            okhttp3.internal.http2.Http2Writer r3 = r11.writer     // Catch: java.lang.Throwable -> Lbc
            r3.pushPromise(r12, r9, r13)     // Catch: java.lang.Throwable -> Lbc
        L92:
            monitor-exit(r8)     // Catch: java.lang.Throwable -> Lbc
            if (r2 == 0) goto L9f
        L99:
            okhttp3.internal.http2.Http2Writer r3 = r11.writer
            r3.flush()
            goto La0
        L9f:
        La0:
            return r1
        La2:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException     // Catch: java.lang.Throwable -> Lbc
            java.lang.String r4 = "client streams shouldn't have associated stream IDs"
            r3.<init>(r4)     // Catch: java.lang.Throwable -> Lbc
            throw r3     // Catch: java.lang.Throwable -> Lbc
        Lae:
            okhttp3.internal.http2.ConnectionShutdownException r1 = new okhttp3.internal.http2.ConnectionShutdownException     // Catch: java.lang.Throwable -> Lb7
            r1.<init>()     // Catch: java.lang.Throwable -> Lb7
            throw r1     // Catch: java.lang.Throwable -> Lb7
        Lb7:
            r1 = move-exception
            monitor-exit(r11)     // Catch: java.lang.Throwable -> Lb7
            throw r1     // Catch: java.lang.Throwable -> Lbc
        Lbc:
            r1 = move-exception
            monitor-exit(r8)     // Catch: java.lang.Throwable -> Lbc
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.newStream(int, java.util.List, boolean):okhttp3.internal.http2.Http2Stream");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeSynReply(int streamId, boolean outFinished, List<Header> alternating) throws IOException {
        this.writer.synReply(outFinished, streamId, alternating);
    }

    public void writeData(int streamId, boolean outFinished, Buffer buffer, long byteCount) throws IOException {
        int toWrite;
        if (byteCount == 0) {
            this.writer.data(outFinished, streamId, buffer, 0);
            return;
        }
        while (byteCount > 0) {
            synchronized (this) {
                while (this.bytesLeftInWriteWindow <= 0) {
                    try {
                        if (!this.streams.containsKey(Integer.valueOf(streamId))) {
                            throw new IOException("stream closed");
                        }
                        wait();
                    } catch (InterruptedException e) {
                        throw new InterruptedIOException();
                    }
                }
                toWrite = Math.min((int) Math.min(byteCount, this.bytesLeftInWriteWindow), this.writer.maxDataLength());
                this.bytesLeftInWriteWindow -= toWrite;
            }
            byteCount -= toWrite;
            this.writer.data(outFinished && byteCount == 0, streamId, buffer, toWrite);
        }
    }

    void addBytesToWriteWindow(long delta) {
        this.bytesLeftInWriteWindow += delta;
        if (delta > 0) {
            notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeSynResetLater(final int streamId, final ErrorCode errorCode) {
        try {
            this.writerExecutor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[]{this.hostname, Integer.valueOf(streamId)}) { // from class: okhttp3.internal.http2.Http2Connection.1
                @Override // okhttp3.internal.NamedRunnable
                public void execute() {
                    try {
                        Http2Connection.this.writeSynReset(streamId, errorCode);
                    } catch (IOException e) {
                        Http2Connection.this.failConnection();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeSynReset(int streamId, ErrorCode statusCode) throws IOException {
        this.writer.rstStream(streamId, statusCode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeWindowUpdateLater(final int streamId, final long unacknowledgedBytesRead) {
        try {
            this.writerExecutor.execute(new NamedRunnable("OkHttp Window Update %s stream %d", new Object[]{this.hostname, Integer.valueOf(streamId)}) { // from class: okhttp3.internal.http2.Http2Connection.2
                @Override // okhttp3.internal.NamedRunnable
                public void execute() {
                    try {
                        Http2Connection.this.writer.windowUpdate(streamId, unacknowledgedBytesRead);
                    } catch (IOException e) {
                        Http2Connection.this.failConnection();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
        }
    }

    /* loaded from: classes.dex */
    final class PingRunnable extends NamedRunnable {
        final int payload1;
        final int payload2;
        final boolean reply;

        PingRunnable(boolean reply, int payload1, int payload2) {
            super("OkHttp %s ping %08x%08x", Http2Connection.this.hostname, Integer.valueOf(payload1), Integer.valueOf(payload2));
            this.reply = reply;
            this.payload1 = payload1;
            this.payload2 = payload2;
        }

        @Override // okhttp3.internal.NamedRunnable
        public void execute() {
            Http2Connection.this.writePing(this.reply, this.payload1, this.payload2);
        }
    }

    void writePing(boolean reply, int payload1, int payload2) {
        boolean failedDueToMissingPong;
        if (!reply) {
            synchronized (this) {
                failedDueToMissingPong = this.awaitingPong;
                this.awaitingPong = true;
            }
            if (failedDueToMissingPong) {
                failConnection();
                return;
            }
        }
        try {
            this.writer.ping(reply, payload1, payload2);
        } catch (IOException e) {
            failConnection();
        }
    }

    void writePingAndAwaitPong() throws IOException, InterruptedException {
        writePing(false, 1330343787, -257978967);
        awaitPong();
    }

    synchronized void awaitPong() throws IOException, InterruptedException {
        while (this.awaitingPong) {
            wait();
        }
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void shutdown(ErrorCode statusCode) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (this.shutdown) {
                    return;
                }
                this.shutdown = true;
                int lastGoodStreamId = this.lastGoodStreamId;
                this.writer.goAway(lastGoodStreamId, statusCode, Util.EMPTY_BYTE_ARRAY);
            }
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
    }

    void close(ErrorCode connectionCode, ErrorCode streamCode) throws IOException {
        IOException thrown = null;
        try {
            shutdown(connectionCode);
        } catch (IOException e) {
            thrown = e;
        }
        Http2Stream[] streamsToClose = null;
        synchronized (this) {
            if (!this.streams.isEmpty()) {
                streamsToClose = (Http2Stream[]) this.streams.values().toArray(new Http2Stream[this.streams.size()]);
                this.streams.clear();
            }
        }
        if (streamsToClose != null) {
            for (Http2Stream stream : streamsToClose) {
                try {
                    stream.close(streamCode);
                } catch (IOException e2) {
                    if (thrown != null) {
                        thrown = e2;
                    }
                }
            }
        }
        try {
            this.writer.close();
        } catch (IOException e3) {
            if (thrown == null) {
                thrown = e3;
            }
        }
        try {
            this.socket.close();
        } catch (IOException e4) {
            thrown = e4;
        }
        this.writerExecutor.shutdown();
        this.pushExecutor.shutdown();
        if (thrown != null) {
            throw thrown;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void failConnection() {
        try {
            close(ErrorCode.PROTOCOL_ERROR, ErrorCode.PROTOCOL_ERROR);
        } catch (IOException e) {
        }
    }

    public void start() throws IOException {
        start(true);
    }

    void start(boolean sendConnectionPreface) throws IOException {
        if (sendConnectionPreface) {
            this.writer.connectionPreface();
            this.writer.settings(this.okHttpSettings);
            int windowSize = this.okHttpSettings.getInitialWindowSize();
            if (windowSize != 65535) {
                this.writer.windowUpdate(0, windowSize - SupportMenu.USER_MASK);
            }
        }
        new Thread(this.readerRunnable).start();
    }

    public void setSettings(Settings settings) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (this.shutdown) {
                    throw new ConnectionShutdownException();
                }
                this.okHttpSettings.merge(settings);
            }
            this.writer.settings(settings);
        }
    }

    public synchronized boolean isShutdown() {
        return this.shutdown;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        boolean client;
        String hostname;
        int pingIntervalMillis;
        BufferedSink sink;
        Socket socket;
        BufferedSource source;
        Listener listener = Listener.REFUSE_INCOMING_STREAMS;
        PushObserver pushObserver = PushObserver.CANCEL;

        public Builder(boolean client) {
            this.client = client;
        }

        public Builder socket(Socket socket) throws IOException {
            return socket(socket, ((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName(), Okio.buffer(Okio.source(socket)), Okio.buffer(Okio.sink(socket)));
        }

        public Builder socket(Socket socket, String hostname, BufferedSource source, BufferedSink sink) {
            this.socket = socket;
            this.hostname = hostname;
            this.source = source;
            this.sink = sink;
            return this;
        }

        public Builder listener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public Builder pushObserver(PushObserver pushObserver) {
            this.pushObserver = pushObserver;
            return this;
        }

        public Builder pingIntervalMillis(int pingIntervalMillis) {
            this.pingIntervalMillis = pingIntervalMillis;
            return this;
        }

        public Http2Connection build() {
            return new Http2Connection(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ReaderRunnable extends NamedRunnable implements Http2Reader.Handler {
        final Http2Reader reader;

        ReaderRunnable(Http2Reader reader) {
            super("OkHttp %s", Http2Connection.this.hostname);
            this.reader = reader;
        }

        @Override // okhttp3.internal.NamedRunnable
        protected void execute() {
            ErrorCode connectionErrorCode = ErrorCode.INTERNAL_ERROR;
            ErrorCode streamErrorCode = ErrorCode.INTERNAL_ERROR;
            try {
                try {
                    this.reader.readConnectionPreface(this);
                    while (this.reader.nextFrame(false, this)) {
                    }
                    connectionErrorCode = ErrorCode.NO_ERROR;
                    streamErrorCode = ErrorCode.CANCEL;
                    try {
                        Http2Connection.this.close(connectionErrorCode, streamErrorCode);
                    } catch (IOException e) {
                    }
                } catch (IOException e2) {
                    connectionErrorCode = ErrorCode.PROTOCOL_ERROR;
                    streamErrorCode = ErrorCode.PROTOCOL_ERROR;
                    try {
                        Http2Connection.this.close(connectionErrorCode, streamErrorCode);
                    } catch (IOException e3) {
                    }
                }
                Util.closeQuietly(this.reader);
            } catch (Throwable th) {
                try {
                    Http2Connection.this.close(connectionErrorCode, streamErrorCode);
                } catch (IOException e4) {
                }
                Util.closeQuietly(this.reader);
                throw th;
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void data(boolean inFinished, int streamId, BufferedSource source, int length) throws IOException {
            if (Http2Connection.this.pushedStream(streamId)) {
                Http2Connection.this.pushDataLater(streamId, source, length, inFinished);
                return;
            }
            Http2Stream dataStream = Http2Connection.this.getStream(streamId);
            if (dataStream == null) {
                Http2Connection.this.writeSynResetLater(streamId, ErrorCode.PROTOCOL_ERROR);
                source.skip(length);
                return;
            }
            dataStream.receiveData(source, length);
            if (inFinished) {
                dataStream.receiveFin();
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void headers(boolean inFinished, int streamId, int associatedStreamId, List<Header> headerBlock) {
            if (Http2Connection.this.pushedStream(streamId)) {
                Http2Connection.this.pushHeadersLater(streamId, headerBlock, inFinished);
                return;
            }
            synchronized (Http2Connection.this) {
                Http2Stream stream = Http2Connection.this.getStream(streamId);
                if (stream == null) {
                    if (Http2Connection.this.shutdown) {
                        return;
                    }
                    if (streamId <= Http2Connection.this.lastGoodStreamId) {
                        return;
                    }
                    if (streamId % 2 == Http2Connection.this.nextStreamId % 2) {
                        return;
                    }
                    final Http2Stream newStream = new Http2Stream(streamId, Http2Connection.this, false, inFinished, headerBlock);
                    Http2Connection.this.lastGoodStreamId = streamId;
                    Http2Connection.this.streams.put(Integer.valueOf(streamId), newStream);
                    Http2Connection.listenerExecutor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[]{Http2Connection.this.hostname, Integer.valueOf(streamId)}) { // from class: okhttp3.internal.http2.Http2Connection.ReaderRunnable.1
                        @Override // okhttp3.internal.NamedRunnable
                        public void execute() {
                            try {
                                Http2Connection.this.listener.onStream(newStream);
                            } catch (IOException e) {
                                Platform platform = Platform.get();
                                platform.log(4, "Http2Connection.Listener failure for " + Http2Connection.this.hostname, e);
                                try {
                                    newStream.close(ErrorCode.PROTOCOL_ERROR);
                                } catch (IOException e2) {
                                }
                            }
                        }
                    });
                    return;
                }
                stream.receiveHeaders(headerBlock);
                if (inFinished) {
                    stream.receiveFin();
                }
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void rstStream(int streamId, ErrorCode errorCode) {
            if (Http2Connection.this.pushedStream(streamId)) {
                Http2Connection.this.pushResetLater(streamId, errorCode);
                return;
            }
            Http2Stream rstStream = Http2Connection.this.removeStream(streamId);
            if (rstStream != null) {
                rstStream.receiveRstStream(errorCode);
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void settings(boolean clearPrevious, Settings newSettings) {
            int i;
            long delta = 0;
            Http2Stream[] streamsToNotify = null;
            synchronized (Http2Connection.this) {
                int priorWriteWindowSize = Http2Connection.this.peerSettings.getInitialWindowSize();
                if (clearPrevious) {
                    Http2Connection.this.peerSettings.clear();
                }
                Http2Connection.this.peerSettings.merge(newSettings);
                applyAndAckSettings(newSettings);
                int peerInitialWindowSize = Http2Connection.this.peerSettings.getInitialWindowSize();
                if (peerInitialWindowSize != -1 && peerInitialWindowSize != priorWriteWindowSize) {
                    delta = peerInitialWindowSize - priorWriteWindowSize;
                    if (!Http2Connection.this.receivedInitialPeerSettings) {
                        Http2Connection.this.addBytesToWriteWindow(delta);
                        Http2Connection.this.receivedInitialPeerSettings = true;
                    }
                    if (!Http2Connection.this.streams.isEmpty()) {
                        streamsToNotify = (Http2Stream[]) Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                    }
                }
                Http2Connection.listenerExecutor.execute(new NamedRunnable("OkHttp %s settings", Http2Connection.this.hostname) { // from class: okhttp3.internal.http2.Http2Connection.ReaderRunnable.2
                    @Override // okhttp3.internal.NamedRunnable
                    public void execute() {
                        Http2Connection.this.listener.onSettings(Http2Connection.this);
                    }
                });
            }
            if (streamsToNotify != null && delta != 0) {
                for (Http2Stream stream : streamsToNotify) {
                    synchronized (stream) {
                        stream.addBytesToWriteWindow(delta);
                    }
                }
            }
        }

        private void applyAndAckSettings(final Settings peerSettings) {
            try {
                Http2Connection.this.writerExecutor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[]{Http2Connection.this.hostname}) { // from class: okhttp3.internal.http2.Http2Connection.ReaderRunnable.3
                    @Override // okhttp3.internal.NamedRunnable
                    public void execute() {
                        try {
                            Http2Connection.this.writer.applyAndAckSettings(peerSettings);
                        } catch (IOException e) {
                            Http2Connection.this.failConnection();
                        }
                    }
                });
            } catch (RejectedExecutionException e) {
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void ackSettings() {
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void ping(boolean reply, int payload1, int payload2) {
            if (!reply) {
                try {
                    Http2Connection.this.writerExecutor.execute(new PingRunnable(true, payload1, payload2));
                    return;
                } catch (RejectedExecutionException e) {
                    return;
                }
            }
            synchronized (Http2Connection.this) {
                Http2Connection.this.awaitingPong = false;
                Http2Connection.this.notifyAll();
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void goAway(int lastGoodStreamId, ErrorCode errorCode, ByteString debugData) {
            Http2Stream[] streamsCopy;
            debugData.size();
            synchronized (Http2Connection.this) {
                streamsCopy = (Http2Stream[]) Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                Http2Connection.this.shutdown = true;
            }
            for (Http2Stream http2Stream : streamsCopy) {
                if (http2Stream.getId() > lastGoodStreamId && http2Stream.isLocallyInitiated()) {
                    http2Stream.receiveRstStream(ErrorCode.REFUSED_STREAM);
                    Http2Connection.this.removeStream(http2Stream.getId());
                }
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void windowUpdate(int streamId, long windowSizeIncrement) {
            if (streamId == 0) {
                synchronized (Http2Connection.this) {
                    Http2Connection.this.bytesLeftInWriteWindow += windowSizeIncrement;
                    Http2Connection.this.notifyAll();
                }
                return;
            }
            Http2Stream stream = Http2Connection.this.getStream(streamId);
            if (stream != null) {
                synchronized (stream) {
                    stream.addBytesToWriteWindow(windowSizeIncrement);
                }
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void priority(int streamId, int streamDependency, int weight, boolean exclusive) {
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void pushPromise(int streamId, int promisedStreamId, List<Header> requestHeaders) {
            Http2Connection.this.pushRequestLater(promisedStreamId, requestHeaders);
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void alternateService(int streamId, String origin, ByteString protocol, String host, int port, long maxAge) {
        }
    }

    boolean pushedStream(int streamId) {
        return streamId != 0 && (streamId & 1) == 0;
    }

    void pushRequestLater(final int streamId, final List<Header> requestHeaders) {
        synchronized (this) {
            if (this.currentPushRequests.contains(Integer.valueOf(streamId))) {
                writeSynResetLater(streamId, ErrorCode.PROTOCOL_ERROR);
                return;
            }
            this.currentPushRequests.add(Integer.valueOf(streamId));
            try {
                this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Request[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) { // from class: okhttp3.internal.http2.Http2Connection.3
                    @Override // okhttp3.internal.NamedRunnable
                    public void execute() {
                        boolean cancel = Http2Connection.this.pushObserver.onRequest(streamId, requestHeaders);
                        if (cancel) {
                            try {
                                Http2Connection.this.writer.rstStream(streamId, ErrorCode.CANCEL);
                                synchronized (Http2Connection.this) {
                                    Http2Connection.this.currentPushRequests.remove(Integer.valueOf(streamId));
                                }
                            } catch (IOException e) {
                            }
                        }
                    }
                });
            } catch (RejectedExecutionException e) {
            }
        }
    }

    void pushHeadersLater(final int streamId, final List<Header> requestHeaders, final boolean inFinished) {
        try {
            this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) { // from class: okhttp3.internal.http2.Http2Connection.4
                @Override // okhttp3.internal.NamedRunnable
                public void execute() {
                    boolean cancel = Http2Connection.this.pushObserver.onHeaders(streamId, requestHeaders, inFinished);
                    if (cancel) {
                        try {
                            Http2Connection.this.writer.rstStream(streamId, ErrorCode.CANCEL);
                        } catch (IOException e) {
                            return;
                        }
                    }
                    if (cancel || inFinished) {
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(streamId));
                        }
                    }
                }
            });
        } catch (RejectedExecutionException e) {
        }
    }

    void pushDataLater(final int streamId, BufferedSource source, final int byteCount, final boolean inFinished) throws IOException {
        final Buffer buffer = new Buffer();
        source.require(byteCount);
        source.read(buffer, byteCount);
        if (buffer.size() != byteCount) {
            throw new IOException(buffer.size() + " != " + byteCount);
        }
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Data[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) { // from class: okhttp3.internal.http2.Http2Connection.5
            @Override // okhttp3.internal.NamedRunnable
            public void execute() {
                try {
                    boolean cancel = Http2Connection.this.pushObserver.onData(streamId, buffer, byteCount, inFinished);
                    if (cancel) {
                        Http2Connection.this.writer.rstStream(streamId, ErrorCode.CANCEL);
                    }
                    if (cancel || inFinished) {
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(streamId));
                        }
                    }
                } catch (IOException e) {
                }
            }
        });
    }

    void pushResetLater(final int streamId, final ErrorCode errorCode) {
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) { // from class: okhttp3.internal.http2.Http2Connection.6
            @Override // okhttp3.internal.NamedRunnable
            public void execute() {
                Http2Connection.this.pushObserver.onReset(streamId, errorCode);
                synchronized (Http2Connection.this) {
                    Http2Connection.this.currentPushRequests.remove(Integer.valueOf(streamId));
                }
            }
        });
    }

    /* loaded from: classes.dex */
    public static abstract class Listener {
        public static final Listener REFUSE_INCOMING_STREAMS = new Listener() { // from class: okhttp3.internal.http2.Http2Connection.Listener.1
            @Override // okhttp3.internal.http2.Http2Connection.Listener
            public void onStream(Http2Stream stream) throws IOException {
                stream.close(ErrorCode.REFUSED_STREAM);
            }
        };

        public abstract void onStream(Http2Stream http2Stream) throws IOException;

        public void onSettings(Http2Connection connection) {
        }
    }
}
