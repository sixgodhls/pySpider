package okio;

import android.support.p000v4.media.session.PlaybackStateCompat;
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
        mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public BufferedSink mo413write(ByteString byteString) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo413write(byteString);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8 */
    public BufferedSink mo427writeUtf8(String string) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo427writeUtf8(string);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8 */
    public BufferedSink mo428writeUtf8(String string, int beginIndex, int endIndex) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo428writeUtf8(string, beginIndex, endIndex);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8CodePoint */
    public BufferedSink mo429writeUtf8CodePoint(int codePoint) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo429writeUtf8CodePoint(codePoint);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeString */
    public BufferedSink mo426writeString(String string, Charset charset) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo426writeString(string, charset);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeString */
    public BufferedSink mo425writeString(String string, int beginIndex, int endIndex, Charset charset) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo425writeString(string, beginIndex, endIndex, charset);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public BufferedSink mo414write(byte[] source) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo414write(source);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public BufferedSink mo415write(byte[] source, int offset, int byteCount) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo415write(source, offset, byteCount);
        return mo412emitCompleteSegments();
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer source) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        int result = this.buffer.write(source);
        mo412emitCompleteSegments();
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
                mo412emitCompleteSegments();
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
            mo412emitCompleteSegments();
        }
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeByte */
    public BufferedSink mo416writeByte(int b) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo416writeByte(b);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeShort */
    public BufferedSink mo423writeShort(int s) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo423writeShort(s);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeShortLe */
    public BufferedSink mo424writeShortLe(int s) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo424writeShortLe(s);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeInt */
    public BufferedSink mo419writeInt(int i) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo419writeInt(i);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeIntLe */
    public BufferedSink mo420writeIntLe(int i) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo420writeIntLe(i);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeLong */
    public BufferedSink mo421writeLong(long v) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo421writeLong(v);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeLongLe */
    public BufferedSink mo422writeLongLe(long v) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo422writeLongLe(v);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeDecimalLong */
    public BufferedSink mo417writeDecimalLong(long v) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo417writeDecimalLong(v);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: writeHexadecimalUnsignedLong */
    public BufferedSink mo418writeHexadecimalUnsignedLong(long v) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.buffer.mo418writeHexadecimalUnsignedLong(v);
        return mo412emitCompleteSegments();
    }

    @Override // okio.BufferedSink
    /* renamed from: emitCompleteSegments */
    public BufferedSink mo412emitCompleteSegments() throws IOException {
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
            {
                RealBufferedSink.this = this;
            }

            @Override // java.io.OutputStream
            public void write(int b) throws IOException {
                if (RealBufferedSink.this.closed) {
                    throw new IOException("closed");
                }
                RealBufferedSink.this.buffer.mo416writeByte((int) ((byte) b));
                RealBufferedSink.this.mo412emitCompleteSegments();
            }

            @Override // java.io.OutputStream
            public void write(byte[] data, int offset, int byteCount) throws IOException {
                if (RealBufferedSink.this.closed) {
                    throw new IOException("closed");
                }
                RealBufferedSink.this.buffer.mo415write(data, offset, byteCount);
                RealBufferedSink.this.mo412emitCompleteSegments();
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