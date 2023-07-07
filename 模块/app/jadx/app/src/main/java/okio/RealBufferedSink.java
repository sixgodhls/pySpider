package okio;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RealBufferedSink implements BufferedSink {
    public final Buffer buffer = new Buffer();
    boolean closed;
    public final Sink sink;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RealBufferedSink(Sink sink) {
        if (sink == null) {
            throw new NullPointerException("sink == null");
        }
        this.sink = sink;
    }

    @Override // okio.BufferedSink
    public Buffer buffer() {
        return this.buffer;
    }

    @Override // okio.Sink
    public void write(Buffer source, long byteCount) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.write(source, byteCount);
        mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public BufferedSink mo351write(ByteString byteString) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo351write(byteString);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8 */
    public BufferedSink mo365writeUtf8(String string) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo365writeUtf8(string);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8 */
    public BufferedSink mo366writeUtf8(String string, int beginIndex, int endIndex) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo366writeUtf8(string, beginIndex, endIndex);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8CodePoint */
    public BufferedSink mo367writeUtf8CodePoint(int codePoint) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo367writeUtf8CodePoint(codePoint);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeString */
    public BufferedSink mo364writeString(String string, Charset charset) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo364writeString(string, charset);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeString */
    public BufferedSink mo363writeString(String string, int beginIndex, int endIndex, Charset charset) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo363writeString(string, beginIndex, endIndex, charset);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public BufferedSink mo352write(byte[] source) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo352write(source);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public BufferedSink mo353write(byte[] source, int offset, int byteCount) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo353write(source, offset, byteCount);
        return mo350emitCompleteSegments();
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer source) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        int result = this.buffer.write(source);
        mo350emitCompleteSegments();
        return result;
    }

    @Override // okio.BufferedSink
    public long writeAll(Source source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        long totalBytesRead = 0;
        while (true) {
            long readCount = source.read(this.buffer, PlaybackStateCompat.ACTION_PLAY_FROM_URI);
            if (readCount != -1) {
                totalBytesRead += readCount;
                mo350emitCompleteSegments();
            } else {
                return totalBytesRead;
            }
        }
    }

    @Override // okio.BufferedSink
    public BufferedSink write(Source source, long byteCount) throws IOException {
        while (byteCount > 0) {
            long read = source.read(this.buffer, byteCount);
            if (read == -1) {
                throw new EOFException();
            }
            byteCount -= read;
            mo350emitCompleteSegments();
        }
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeByte */
    public BufferedSink mo354writeByte(int b) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo354writeByte(b);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeShort */
    public BufferedSink mo361writeShort(int s) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo361writeShort(s);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeShortLe */
    public BufferedSink mo362writeShortLe(int s) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo362writeShortLe(s);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeInt */
    public BufferedSink mo357writeInt(int i) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo357writeInt(i);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeIntLe */
    public BufferedSink mo358writeIntLe(int i) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo358writeIntLe(i);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeLong */
    public BufferedSink mo359writeLong(long v) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo359writeLong(v);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeLongLe */
    public BufferedSink mo360writeLongLe(long v) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo360writeLongLe(v);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeDecimalLong */
    public BufferedSink mo355writeDecimalLong(long v) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo355writeDecimalLong(v);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeHexadecimalUnsignedLong */
    public BufferedSink mo356writeHexadecimalUnsignedLong(long v) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo356writeHexadecimalUnsignedLong(v);
        return mo350emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: emitCompleteSegments */
    public BufferedSink mo350emitCompleteSegments() throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        long byteCount = this.buffer.completeSegmentByteCount();
        if (byteCount > 0) {
            this.sink.write(this.buffer, byteCount);
        }
        return this;
    }

    @Override // okio.BufferedSink
    public BufferedSink emit() throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        long byteCount = this.buffer.size();
        if (byteCount > 0) {
            this.sink.write(this.buffer, byteCount);
        }
        return this;
    }

    @Override // okio.BufferedSink
    public OutputStream outputStream() {
        return new OutputStream() { // from class: okio.RealBufferedSink.1
            @Override // java.io.OutputStream
            public void write(int b) throws IOException {
                if (RealBufferedSink.this.closed) {
                    throw new IOException("closed");
                }
                RealBufferedSink.this.buffer.mo354writeByte((int) ((byte) b));
                RealBufferedSink.this.mo350emitCompleteSegments();
            }

            @Override // java.io.OutputStream
            public void write(byte[] data, int offset, int byteCount) throws IOException {
                if (RealBufferedSink.this.closed) {
                    throw new IOException("closed");
                }
                RealBufferedSink.this.buffer.mo353write(data, offset, byteCount);
                RealBufferedSink.this.mo350emitCompleteSegments();
            }

            @Override // java.io.OutputStream, java.io.Flushable
            public void flush() throws IOException {
                if (!RealBufferedSink.this.closed) {
                    RealBufferedSink.this.flush();
                }
            }

            @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                RealBufferedSink.this.close();
            }

            public String toString() {
                return RealBufferedSink.this + ".outputStream()";
            }
        };
    }

    @Override // okio.BufferedSink, okio.Sink, java.io.Flushable
    public void flush() throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        if (this.buffer.size > 0) {
            Sink sink = this.sink;
            Buffer buffer = this.buffer;
            sink.write(buffer, buffer.size);
        }
        this.sink.flush();
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return !this.closed;
    }

    @Override // okio.Sink, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        Throwable thrown = null;
        try {
            if (this.buffer.size > 0) {
                this.sink.write(this.buffer, this.buffer.size);
            }
        } catch (Throwable e) {
            thrown = e;
        }
        try {
            this.sink.close();
        } catch (Throwable e2) {
            if (thrown == null) {
                thrown = e2;
            }
        }
        this.closed = true;
        if (thrown != null) {
            Util.sneakyRethrow(thrown);
        }
    }

    @Override // okio.Sink
    public Timeout timeout() {
        return this.sink.timeout();
    }

    public String toString() {
        return "buffer(" + this.sink + ")";
    }
}
