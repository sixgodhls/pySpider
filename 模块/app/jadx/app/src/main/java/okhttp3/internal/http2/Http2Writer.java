package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.internal.Util;
import okhttp3.internal.http2.Hpack;
import okio.Buffer;
import okio.BufferedSink;

/* loaded from: classes.dex */
final class Http2Writer implements Closeable {
    private static final Logger logger = Logger.getLogger(Http2.class.getName());
    private final boolean client;
    private boolean closed;
    private final Buffer hpackBuffer = new Buffer();
    final Hpack.Writer hpackWriter = new Hpack.Writer(this.hpackBuffer);
    private int maxFrameSize = 16384;
    private final BufferedSink sink;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2Writer(BufferedSink sink, boolean client) {
        this.sink = sink;
        this.client = client;
    }

    public synchronized void connectionPreface() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        if (!this.client) {
            return;
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(Util.format(">> CONNECTION %s", Http2.CONNECTION_PREFACE.hex()));
        }
        this.sink.mo352write(Http2.CONNECTION_PREFACE.toByteArray());
        this.sink.flush();
    }

    public synchronized void applyAndAckSettings(Settings peerSettings) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        this.maxFrameSize = peerSettings.getMaxFrameSize(this.maxFrameSize);
        if (peerSettings.getHeaderTableSize() != -1) {
            this.hpackWriter.setHeaderTableSizeSetting(peerSettings.getHeaderTableSize());
        }
        frameHeader(0, 0, (byte) 4, (byte) 1);
        this.sink.flush();
    }

    public synchronized void pushPromise(int streamId, int promisedStreamId, List<Header> requestHeaders) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        this.hpackWriter.writeHeaders(requestHeaders);
        long byteCount = this.hpackBuffer.size();
        byte flags = 4;
        int length = (int) Math.min(this.maxFrameSize - 4, byteCount);
        if (byteCount != length) {
            flags = 0;
        }
        frameHeader(streamId, length + 4, (byte) 5, flags);
        this.sink.mo357writeInt(Integer.MAX_VALUE & promisedStreamId);
        this.sink.write(this.hpackBuffer, length);
        if (byteCount > length) {
            writeContinuationFrames(streamId, byteCount - length);
        }
    }

    public synchronized void flush() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        this.sink.flush();
    }

    public synchronized void synStream(boolean outFinished, int streamId, int associatedStreamId, List<Header> headerBlock) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        headers(outFinished, streamId, headerBlock);
    }

    public synchronized void synReply(boolean outFinished, int streamId, List<Header> headerBlock) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        headers(outFinished, streamId, headerBlock);
    }

    public synchronized void headers(int streamId, List<Header> headerBlock) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        headers(false, streamId, headerBlock);
    }

    public synchronized void rstStream(int streamId, ErrorCode errorCode) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        if (errorCode.httpCode == -1) {
            throw new IllegalArgumentException();
        }
        frameHeader(streamId, 4, (byte) 3, (byte) 0);
        this.sink.mo357writeInt(errorCode.httpCode);
        this.sink.flush();
    }

    public int maxDataLength() {
        return this.maxFrameSize;
    }

    public synchronized void data(boolean outFinished, int streamId, Buffer source, int byteCount) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        byte flags = 0;
        if (outFinished) {
            flags = (byte) (0 | 1);
        }
        dataFrame(streamId, flags, source, byteCount);
    }

    void dataFrame(int streamId, byte flags, Buffer buffer, int byteCount) throws IOException {
        frameHeader(streamId, byteCount, (byte) 0, flags);
        if (byteCount > 0) {
            this.sink.write(buffer, byteCount);
        }
    }

    public synchronized void settings(Settings settings) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        int length = settings.size() * 6;
        frameHeader(0, length, (byte) 4, (byte) 0);
        for (int i = 0; i < 10; i++) {
            if (settings.isSet(i)) {
                int id = i;
                if (id == 4) {
                    id = 3;
                } else if (id == 7) {
                    id = 4;
                }
                this.sink.mo361writeShort(id);
                this.sink.mo357writeInt(settings.get(i));
            }
        }
        this.sink.flush();
    }

    public synchronized void ping(boolean ack, int payload1, int payload2) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        byte flags = ack ? (byte) 1 : (byte) 0;
        frameHeader(0, 8, (byte) 6, flags);
        this.sink.mo357writeInt(payload1);
        this.sink.mo357writeInt(payload2);
        this.sink.flush();
    }

    public synchronized void goAway(int lastGoodStreamId, ErrorCode errorCode, byte[] debugData) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        if (errorCode.httpCode == -1) {
            throw Http2.illegalArgument("errorCode.httpCode == -1", new Object[0]);
        }
        int length = debugData.length + 8;
        frameHeader(0, length, (byte) 7, (byte) 0);
        this.sink.mo357writeInt(lastGoodStreamId);
        this.sink.mo357writeInt(errorCode.httpCode);
        if (debugData.length > 0) {
            this.sink.mo352write(debugData);
        }
        this.sink.flush();
    }

    public synchronized void windowUpdate(int streamId, long windowSizeIncrement) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        if (windowSizeIncrement == 0 || windowSizeIncrement > 2147483647L) {
            throw Http2.illegalArgument("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: %s", Long.valueOf(windowSizeIncrement));
        }
        frameHeader(streamId, 4, (byte) 8, (byte) 0);
        this.sink.mo357writeInt((int) windowSizeIncrement);
        this.sink.flush();
    }

    public void frameHeader(int streamId, int length, byte type, byte flags) throws IOException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(Http2.frameLog(false, streamId, length, type, flags));
        }
        int i = this.maxFrameSize;
        if (length <= i) {
            if ((Integer.MIN_VALUE & streamId) != 0) {
                throw Http2.illegalArgument("reserved bit set: %s", Integer.valueOf(streamId));
            }
            writeMedium(this.sink, length);
            this.sink.mo354writeByte(type & 255);
            this.sink.mo354writeByte(flags & 255);
            this.sink.mo357writeInt(Integer.MAX_VALUE & streamId);
            return;
        }
        throw Http2.illegalArgument("FRAME_SIZE_ERROR length > %d: %d", Integer.valueOf(i), Integer.valueOf(length));
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        this.closed = true;
        this.sink.close();
    }

    private static void writeMedium(BufferedSink sink, int i) throws IOException {
        sink.mo354writeByte((i >>> 16) & 255);
        sink.mo354writeByte((i >>> 8) & 255);
        sink.mo354writeByte(i & 255);
    }

    private void writeContinuationFrames(int streamId, long byteCount) throws IOException {
        while (byteCount > 0) {
            int length = (int) Math.min(this.maxFrameSize, byteCount);
            byteCount -= length;
            frameHeader(streamId, length, (byte) 9, byteCount == 0 ? (byte) 4 : (byte) 0);
            this.sink.write(this.hpackBuffer, length);
        }
    }

    void headers(boolean outFinished, int streamId, List<Header> headerBlock) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        this.hpackWriter.writeHeaders(headerBlock);
        long byteCount = this.hpackBuffer.size();
        int length = (int) Math.min(this.maxFrameSize, byteCount);
        byte flags = byteCount == ((long) length) ? (byte) 4 : (byte) 0;
        if (outFinished) {
            flags = (byte) (flags | 1);
        }
        frameHeader(streamId, length, (byte) 1, flags);
        this.sink.write(this.hpackBuffer, length);
        if (byteCount > length) {
            writeContinuationFrames(streamId, byteCount - length);
        }
    }
}
