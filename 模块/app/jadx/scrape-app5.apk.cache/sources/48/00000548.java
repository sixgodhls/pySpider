package okio;

import android.support.p000v4.media.session.PlaybackStateCompat;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public final class Buffer implements BufferedSource, BufferedSink, Cloneable, ByteChannel {
    private static final byte[] DIGITS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    static final int REPLACEMENT_CHARACTER = 65533;
    @Nullable
    Segment head;
    long size;

    public long size() {
        return this.size;
    }

    @Override // okio.BufferedSource, okio.BufferedSink
    public Buffer buffer() {
        return this;
    }

    @Override // okio.BufferedSink
    public OutputStream outputStream() {
        return new OutputStream() { // from class: okio.Buffer.1
            {
                Buffer.this = this;
            }

            @Override // java.io.OutputStream
            public void write(int b) {
                Buffer.this.mo416writeByte((int) ((byte) b));
            }

            @Override // java.io.OutputStream
            public void write(byte[] data, int offset, int byteCount) {
                Buffer.this.mo415write(data, offset, byteCount);
            }

            @Override // java.io.OutputStream, java.io.Flushable
            public void flush() {
            }

            @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
            }

            public String toString() {
                return Buffer.this + ".outputStream()";
            }
        };
    }

    @Override // okio.BufferedSink
    /* renamed from: emitCompleteSegments */
    public Buffer mo412emitCompleteSegments() {
        return this;
    }

    @Override // okio.BufferedSink
    public BufferedSink emit() {
        return this;
    }

    @Override // okio.BufferedSource
    public boolean exhausted() {
        return this.size == 0;
    }

    @Override // okio.BufferedSource
    public void require(long byteCount) throws EOFException {
        if (this.size < byteCount) {
            throw new EOFException();
        }
    }

    @Override // okio.BufferedSource
    public boolean request(long byteCount) {
        return this.size >= byteCount;
    }

    @Override // okio.BufferedSource
    public InputStream inputStream() {
        return new InputStream() { // from class: okio.Buffer.2
            {
                Buffer.this = this;
            }

            @Override // java.io.InputStream
            public int read() {
                if (Buffer.this.size > 0) {
                    return Buffer.this.readByte() & 255;
                }
                return -1;
            }

            @Override // java.io.InputStream
            public int read(byte[] sink, int offset, int byteCount) {
                return Buffer.this.read(sink, offset, byteCount);
            }

            @Override // java.io.InputStream
            public int available() {
                return (int) Math.min(Buffer.this.size, 2147483647L);
            }

            @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
            }

            public String toString() {
                return Buffer.this + ".inputStream()";
            }
        };
    }

    public Buffer copyTo(OutputStream out) throws IOException {
        return copyTo(out, 0L, this.size);
    }

    public Buffer copyTo(OutputStream out, long offset, long byteCount) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, offset, byteCount);
        if (byteCount == 0) {
            return this;
        }
        Segment s = this.head;
        while (offset >= s.limit - s.pos) {
            offset -= s.limit - s.pos;
            s = s.next;
        }
        while (byteCount > 0) {
            int pos = (int) (s.pos + offset);
            int toCopy = (int) Math.min(s.limit - pos, byteCount);
            out.write(s.data, pos, toCopy);
            byteCount -= toCopy;
            offset = 0;
            s = s.next;
        }
        return this;
    }

    public Buffer copyTo(Buffer out, long offset, long byteCount) {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, offset, byteCount);
        if (byteCount == 0) {
            return this;
        }
        out.size += byteCount;
        Segment s = this.head;
        while (offset >= s.limit - s.pos) {
            offset -= s.limit - s.pos;
            s = s.next;
        }
        while (byteCount > 0) {
            Segment copy = s.sharedCopy();
            copy.pos = (int) (copy.pos + offset);
            copy.limit = Math.min(copy.pos + ((int) byteCount), copy.limit);
            Segment segment = out.head;
            if (segment == null) {
                copy.prev = copy;
                copy.next = copy;
                out.head = copy;
            } else {
                segment.prev.push(copy);
            }
            byteCount -= copy.limit - copy.pos;
            offset = 0;
            s = s.next;
        }
        return this;
    }

    public Buffer writeTo(OutputStream out) throws IOException {
        return writeTo(out, this.size);
    }

    public Buffer writeTo(OutputStream out, long byteCount) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, 0L, byteCount);
        Segment s = this.head;
        while (byteCount > 0) {
            int toCopy = (int) Math.min(byteCount, s.limit - s.pos);
            out.write(s.data, s.pos, toCopy);
            s.pos += toCopy;
            this.size -= toCopy;
            byteCount -= toCopy;
            if (s.pos == s.limit) {
                Segment toRecycle = s;
                Segment pop = toRecycle.pop();
                s = pop;
                this.head = pop;
                SegmentPool.recycle(toRecycle);
            }
        }
        return this;
    }

    public Buffer readFrom(InputStream in) throws IOException {
        readFrom(in, Long.MAX_VALUE, true);
        return this;
    }

    public Buffer readFrom(InputStream in, long byteCount) throws IOException {
        if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        }
        readFrom(in, byteCount, false);
        return this;
    }

    private void readFrom(InputStream in, long byteCount, boolean forever) throws IOException {
        if (in != null) {
            while (true) {
                if (byteCount > 0 || forever) {
                    Segment tail = writableSegment(1);
                    int maxToCopy = (int) Math.min(byteCount, 8192 - tail.limit);
                    int bytesRead = in.read(tail.data, tail.limit, maxToCopy);
                    if (bytesRead == -1) {
                        if (!forever) {
                            throw new EOFException();
                        }
                        return;
                    }
                    tail.limit += bytesRead;
                    this.size += bytesRead;
                    byteCount -= bytesRead;
                } else {
                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("in == null");
        }
    }

    public long completeSegmentByteCount() {
        long result = this.size;
        if (result == 0) {
            return 0L;
        }
        Segment tail = this.head.prev;
        if (tail.limit < 8192 && tail.owner) {
            return result - (tail.limit - tail.pos);
        }
        return result;
    }

    @Override // okio.BufferedSource
    public byte readByte() {
        if (this.size == 0) {
            throw new IllegalStateException("size == 0");
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        byte[] data = segment.data;
        int pos2 = pos + 1;
        byte b = data[pos];
        this.size--;
        if (pos2 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos2;
        }
        return b;
    }

    public byte getByte(long pos) {
        Util.checkOffsetAndCount(this.size, pos, 1L);
        long j = this.size;
        if (j - pos > pos) {
            Segment s = this.head;
            while (true) {
                int segmentByteCount = s.limit - s.pos;
                if (pos < segmentByteCount) {
                    return s.data[s.pos + ((int) pos)];
                }
                pos -= segmentByteCount;
                s = s.next;
            }
        } else {
            long pos2 = pos - j;
            Segment s2 = this.head.prev;
            while (true) {
                pos2 += s2.limit - s2.pos;
                if (pos2 >= 0) {
                    return s2.data[s2.pos + ((int) pos2)];
                }
                s2 = s2.prev;
            }
        }
    }

    @Override // okio.BufferedSource
    public short readShort() {
        if (this.size < 2) {
            throw new IllegalStateException("size < 2: " + this.size);
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        if (limit - pos < 2) {
            int s = ((readByte() & 255) << 8) | (readByte() & 255);
            return (short) s;
        }
        byte[] data = segment.data;
        int pos2 = pos + 1;
        int pos3 = pos2 + 1;
        int s2 = ((data[pos] & 255) << 8) | (data[pos2] & 255);
        this.size -= 2;
        if (pos3 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos3;
        }
        return (short) s2;
    }

    @Override // okio.BufferedSource
    public int readInt() {
        if (this.size < 4) {
            throw new IllegalStateException("size < 4: " + this.size);
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        if (limit - pos < 4) {
            return ((readByte() & 255) << 24) | ((readByte() & 255) << 16) | ((readByte() & 255) << 8) | (readByte() & 255);
        }
        byte[] data = segment.data;
        int pos2 = pos + 1;
        int pos3 = pos2 + 1;
        int i = ((data[pos] & 255) << 24) | ((data[pos2] & 255) << 16);
        int pos4 = pos3 + 1;
        int i2 = i | ((data[pos3] & 255) << 8);
        int pos5 = pos4 + 1;
        int i3 = i2 | (data[pos4] & 255);
        this.size -= 4;
        if (pos5 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos5;
        }
        return i3;
    }

    @Override // okio.BufferedSource
    public long readLong() {
        int pos;
        int pos2;
        int pos3;
        int pos4;
        int pos5;
        int pos6;
        int pos7;
        if (this.size < 8) {
            throw new IllegalStateException("size < 8: " + this.size);
        }
        Segment segment = this.head;
        int pos8 = segment.pos;
        int limit = segment.limit;
        if (limit - pos8 < 8) {
            return ((readInt() & 4294967295L) << 32) | (readInt() & 4294967295L);
        }
        byte[] data = segment.data;
        long j = ((data[pos8] & 255) << 56) | ((data[pos] & 255) << 48) | ((data[pos2] & 255) << 40) | ((data[pos3] & 255) << 32) | ((data[pos4] & 255) << 24) | ((data[pos5] & 255) << 16);
        int pos9 = pos8 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
        long v = ((data[pos6] & 255) << 8) | j | (data[pos7] & 255);
        this.size -= 8;
        if (pos9 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos9;
        }
        return v;
    }

    @Override // okio.BufferedSource
    public short readShortLe() {
        return Util.reverseBytesShort(readShort());
    }

    @Override // okio.BufferedSource
    public int readIntLe() {
        return Util.reverseBytesInt(readInt());
    }

    @Override // okio.BufferedSource
    public long readLongLe() {
        return Util.reverseBytesLong(readLong());
    }

    /* JADX WARN: Code restructure failed: missing block: B:44:0x00d9, code lost:
        r20.size -= r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00df, code lost:
        if (r4 == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:?, code lost:
        return r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x00e4, code lost:
        return -r1;
     */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00c2  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00cc  */
    @Override // okio.BufferedSource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public long readDecimalLong() {
        /*
            Method dump skipped, instructions count: 238
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readDecimalLong():long");
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0095  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00a3  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00a7 A[EDGE_INSN: B:39:0x00a7->B:37:0x00a7 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x009f  */
    @Override // okio.BufferedSource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public long readHexadecimalUnsignedLong() {
        /*
            r15 = this;
            long r0 = r15.size
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto Lae
            r0 = 0
            r4 = 0
            r5 = 0
        Lc:
            okio.Segment r6 = r15.head
            byte[] r7 = r6.data
            int r8 = r6.pos
            int r9 = r6.limit
        L14:
            if (r8 >= r9) goto L93
            r10 = r7[r8]
            r11 = 48
            if (r10 < r11) goto L23
            r11 = 57
            if (r10 > r11) goto L23
            int r11 = r10 + (-48)
            goto L3c
        L23:
            r11 = 97
            if (r10 < r11) goto L30
            r11 = 102(0x66, float:1.43E-43)
            if (r10 > r11) goto L30
            int r11 = r10 + (-97)
            int r11 = r11 + 10
            goto L3c
        L30:
            r11 = 65
            if (r10 < r11) goto L74
            r11 = 70
            if (r10 > r11) goto L74
            int r11 = r10 + (-65)
            int r11 = r11 + 10
        L3c:
            r12 = -1152921504606846976(0xf000000000000000, double:-3.105036184601418E231)
            long r12 = r12 & r0
            int r14 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r14 != 0) goto L4c
            r12 = 4
            long r0 = r0 << r12
            long r12 = (long) r11
            long r0 = r0 | r12
            int r8 = r8 + 1
            int r4 = r4 + 1
            goto L14
        L4c:
            okio.Buffer r2 = new okio.Buffer
            r2.<init>()
            okio.Buffer r2 = r2.mo418writeHexadecimalUnsignedLong(r0)
            okio.Buffer r2 = r2.mo416writeByte(r10)
            java.lang.NumberFormatException r3 = new java.lang.NumberFormatException
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Number too large: "
            r12.append(r13)
            java.lang.String r13 = r2.readUtf8()
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r3.<init>(r12)
            throw r3
        L74:
            if (r4 == 0) goto L78
            r5 = 1
            goto L93
        L78:
            java.lang.NumberFormatException r2 = new java.lang.NumberFormatException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r11 = "Expected leading [0-9a-fA-F] character but was 0x"
            r3.append(r11)
            java.lang.String r11 = java.lang.Integer.toHexString(r10)
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L93:
            if (r8 != r9) goto L9f
            okio.Segment r10 = r6.pop()
            r15.head = r10
            okio.SegmentPool.recycle(r6)
            goto La1
        L9f:
            r6.pos = r8
        La1:
            if (r5 != 0) goto La7
            okio.Segment r6 = r15.head
            if (r6 != 0) goto Lc
        La7:
            long r2 = r15.size
            long r6 = (long) r4
            long r2 = r2 - r6
            r15.size = r2
            return r0
        Lae:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "size == 0"
            r0.<init>(r1)
            throw r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readHexadecimalUnsignedLong():long");
    }

    @Override // okio.BufferedSource
    public ByteString readByteString() {
        return new ByteString(readByteArray());
    }

    @Override // okio.BufferedSource
    public ByteString readByteString(long byteCount) throws EOFException {
        return new ByteString(readByteArray(byteCount));
    }

    @Override // okio.BufferedSource
    public int select(Options options) {
        Segment s = this.head;
        if (s == null) {
            return options.indexOf(ByteString.EMPTY);
        }
        ByteString[] byteStrings = options.byteStrings;
        int listSize = byteStrings.length;
        for (int i = 0; i < listSize; i++) {
            ByteString b = byteStrings[i];
            if (this.size >= b.size() && rangeEquals(s, s.pos, b, 0, b.size())) {
                try {
                    skip(b.size());
                    return i;
                } catch (EOFException e) {
                    throw new AssertionError(e);
                }
            }
        }
        return -1;
    }

    public int selectPrefix(Options options) {
        Segment s = this.head;
        ByteString[] byteStrings = options.byteStrings;
        int listSize = byteStrings.length;
        for (int i = 0; i < listSize; i++) {
            ByteString b = byteStrings[i];
            int bytesLimit = (int) Math.min(this.size, b.size());
            if (bytesLimit == 0 || rangeEquals(s, s.pos, b, 0, bytesLimit)) {
                return i;
            }
        }
        return -1;
    }

    @Override // okio.BufferedSource
    public void readFully(Buffer sink, long byteCount) throws EOFException {
        long j = this.size;
        if (j < byteCount) {
            sink.write(this, j);
            throw new EOFException();
        } else {
            sink.write(this, byteCount);
        }
    }

    @Override // okio.BufferedSource
    public long readAll(Sink sink) throws IOException {
        long byteCount = this.size;
        if (byteCount > 0) {
            sink.write(this, byteCount);
        }
        return byteCount;
    }

    @Override // okio.BufferedSource
    public String readUtf8() {
        try {
            return readString(this.size, Util.UTF_8);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    @Override // okio.BufferedSource
    public String readUtf8(long byteCount) throws EOFException {
        return readString(byteCount, Util.UTF_8);
    }

    @Override // okio.BufferedSource
    public String readString(Charset charset) {
        try {
            return readString(this.size, charset);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    @Override // okio.BufferedSource
    public String readString(long byteCount, Charset charset) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0L, byteCount);
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        }
        if (byteCount > 2147483647L) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
        } else if (byteCount == 0) {
            return "";
        } else {
            Segment s = this.head;
            if (s.pos + byteCount > s.limit) {
                return new String(readByteArray(byteCount), charset);
            }
            String result = new String(s.data, s.pos, (int) byteCount, charset);
            s.pos = (int) (s.pos + byteCount);
            this.size -= byteCount;
            if (s.pos == s.limit) {
                this.head = s.pop();
                SegmentPool.recycle(s);
            }
            return result;
        }
    }

    @Override // okio.BufferedSource
    @Nullable
    public String readUtf8Line() throws EOFException {
        long newline = indexOf((byte) 10);
        if (newline == -1) {
            long j = this.size;
            if (j == 0) {
                return null;
            }
            return readUtf8(j);
        }
        return readUtf8Line(newline);
    }

    @Override // okio.BufferedSource
    public String readUtf8LineStrict() throws EOFException {
        return readUtf8LineStrict(Long.MAX_VALUE);
    }

    @Override // okio.BufferedSource
    public String readUtf8LineStrict(long limit) throws EOFException {
        if (limit < 0) {
            throw new IllegalArgumentException("limit < 0: " + limit);
        }
        long scanLength = Long.MAX_VALUE;
        if (limit != Long.MAX_VALUE) {
            scanLength = limit + 1;
        }
        long newline = indexOf((byte) 10, 0L, scanLength);
        if (newline != -1) {
            return readUtf8Line(newline);
        }
        if (scanLength < size() && getByte(scanLength - 1) == 13 && getByte(scanLength) == 10) {
            return readUtf8Line(scanLength);
        }
        Buffer data = new Buffer();
        copyTo(data, 0L, Math.min(32L, size()));
        throw new EOFException("\\n not found: limit=" + Math.min(size(), limit) + " content=" + data.readByteString().hex() + (char) 8230);
    }

    public String readUtf8Line(long newline) throws EOFException {
        if (newline > 0 && getByte(newline - 1) == 13) {
            String result = readUtf8(newline - 1);
            skip(2L);
            return result;
        }
        String result2 = readUtf8(newline);
        skip(1L);
        return result2;
    }

    @Override // okio.BufferedSource
    public int readUtf8CodePoint() throws EOFException {
        int codePoint;
        int byteCount;
        int min;
        if (this.size == 0) {
            throw new EOFException();
        }
        byte b0 = getByte(0L);
        if ((b0 & 128) == 0) {
            codePoint = b0 & Byte.MAX_VALUE;
            byteCount = 1;
            min = 0;
        } else {
            int codePoint2 = b0 & 224;
            if (codePoint2 == 192) {
                codePoint = b0 & 31;
                byteCount = 2;
                min = 128;
            } else {
                int codePoint3 = b0 & 240;
                if (codePoint3 == 224) {
                    codePoint = b0 & 15;
                    byteCount = 3;
                    min = 2048;
                } else {
                    int codePoint4 = b0 & 248;
                    if (codePoint4 == 240) {
                        codePoint = b0 & 7;
                        byteCount = 4;
                        min = 65536;
                    } else {
                        skip(1L);
                        return REPLACEMENT_CHARACTER;
                    }
                }
            }
        }
        if (this.size < byteCount) {
            throw new EOFException("size < " + byteCount + ": " + this.size + " (to read code point prefixed 0x" + Integer.toHexString(b0) + ")");
        }
        for (int i = 1; i < byteCount; i++) {
            byte b = getByte(i);
            if ((b & 192) == 128) {
                codePoint = (codePoint << 6) | (b & 63);
            } else {
                skip(i);
                return REPLACEMENT_CHARACTER;
            }
        }
        skip(byteCount);
        return codePoint > 1114111 ? REPLACEMENT_CHARACTER : ((codePoint < 55296 || codePoint > 57343) && codePoint >= min) ? codePoint : REPLACEMENT_CHARACTER;
    }

    @Override // okio.BufferedSource
    public byte[] readByteArray() {
        try {
            return readByteArray(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    @Override // okio.BufferedSource
    public byte[] readByteArray(long byteCount) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0L, byteCount);
        if (byteCount > 2147483647L) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
        }
        byte[] result = new byte[(int) byteCount];
        readFully(result);
        return result;
    }

    @Override // okio.BufferedSource
    public int read(byte[] sink) {
        return read(sink, 0, sink.length);
    }

    @Override // okio.BufferedSource
    public void readFully(byte[] sink) throws EOFException {
        int offset = 0;
        while (offset < sink.length) {
            int read = read(sink, offset, sink.length - offset);
            if (read == -1) {
                throw new EOFException();
            }
            offset += read;
        }
    }

    @Override // okio.BufferedSource
    public int read(byte[] sink, int offset, int byteCount) {
        Util.checkOffsetAndCount(sink.length, offset, byteCount);
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(byteCount, s.limit - s.pos);
        System.arraycopy(s.data, s.pos, sink, offset, toCopy);
        s.pos += toCopy;
        this.size -= toCopy;
        if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
        }
        return toCopy;
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer sink) throws IOException {
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(sink.remaining(), s.limit - s.pos);
        sink.put(s.data, s.pos, toCopy);
        s.pos += toCopy;
        this.size -= toCopy;
        if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
        }
        return toCopy;
    }

    public void clear() {
        try {
            skip(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    @Override // okio.BufferedSource
    public void skip(long byteCount) throws EOFException {
        Segment segment;
        while (byteCount > 0) {
            if (this.head == null) {
                throw new EOFException();
            }
            int toSkip = (int) Math.min(byteCount, segment.limit - this.head.pos);
            this.size -= toSkip;
            byteCount -= toSkip;
            this.head.pos += toSkip;
            if (this.head.pos == this.head.limit) {
                Segment toRecycle = this.head;
                this.head = toRecycle.pop();
                SegmentPool.recycle(toRecycle);
            }
        }
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public Buffer mo413write(ByteString byteString) {
        if (byteString == null) {
            throw new IllegalArgumentException("byteString == null");
        }
        byteString.write(this);
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8 */
    public Buffer mo427writeUtf8(String string) {
        return mo428writeUtf8(string, 0, string.length());
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8 */
    public Buffer mo428writeUtf8(String string, int beginIndex, int endIndex) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (beginIndex < 0) {
            throw new IllegalArgumentException("beginIndex < 0: " + beginIndex);
        } else if (endIndex < beginIndex) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
        } else if (endIndex > string.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
        } else {
            int i = beginIndex;
            while (i < endIndex) {
                int c = string.charAt(i);
                if (c < 128) {
                    Segment tail = writableSegment(1);
                    byte[] data = tail.data;
                    int segmentOffset = tail.limit - i;
                    int runLimit = Math.min(endIndex, 8192 - segmentOffset);
                    int i2 = i + 1;
                    data[i + segmentOffset] = (byte) c;
                    while (i2 < runLimit) {
                        int c2 = string.charAt(i2);
                        if (c2 >= 128) {
                            break;
                        }
                        data[i2 + segmentOffset] = (byte) c2;
                        i2++;
                    }
                    int runSize = (i2 + segmentOffset) - tail.limit;
                    tail.limit += runSize;
                    this.size += runSize;
                    i = i2;
                } else if (c < 2048) {
                    mo416writeByte((c >> 6) | 192);
                    mo416writeByte(128 | (c & 63));
                    i++;
                } else if (c < 55296 || c > 57343) {
                    mo416writeByte((c >> 12) | 224);
                    mo416writeByte(((c >> 6) & 63) | 128);
                    mo416writeByte(128 | (c & 63));
                    i++;
                } else {
                    int low = i + 1 < endIndex ? string.charAt(i + 1) : 0;
                    if (c > 56319 || low < 56320 || low > 57343) {
                        mo416writeByte(63);
                        i++;
                    } else {
                        int codePoint = ((((-55297) & c) << 10) | ((-56321) & low)) + 65536;
                        mo416writeByte((codePoint >> 18) | 240);
                        mo416writeByte(((codePoint >> 12) & 63) | 128);
                        mo416writeByte(((codePoint >> 6) & 63) | 128);
                        mo416writeByte(128 | (codePoint & 63));
                        i += 2;
                    }
                }
            }
            return this;
        }
    }

    @Override // okio.BufferedSink
    /* renamed from: writeUtf8CodePoint */
    public Buffer mo429writeUtf8CodePoint(int codePoint) {
        if (codePoint < 128) {
            mo416writeByte(codePoint);
        } else if (codePoint < 2048) {
            mo416writeByte((codePoint >> 6) | 192);
            mo416writeByte(128 | (codePoint & 63));
        } else if (codePoint < 65536) {
            if (codePoint >= 55296 && codePoint <= 57343) {
                mo416writeByte(63);
            } else {
                mo416writeByte((codePoint >> 12) | 224);
                mo416writeByte(((codePoint >> 6) & 63) | 128);
                mo416writeByte(128 | (codePoint & 63));
            }
        } else if (codePoint <= 1114111) {
            mo416writeByte((codePoint >> 18) | 240);
            mo416writeByte(((codePoint >> 12) & 63) | 128);
            mo416writeByte(((codePoint >> 6) & 63) | 128);
            mo416writeByte(128 | (codePoint & 63));
        } else {
            throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(codePoint));
        }
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeString */
    public Buffer mo426writeString(String string, Charset charset) {
        return mo425writeString(string, 0, string.length(), charset);
    }

    @Override // okio.BufferedSink
    /* renamed from: writeString */
    public Buffer mo425writeString(String string, int beginIndex, int endIndex, Charset charset) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (beginIndex < 0) {
            throw new IllegalAccessError("beginIndex < 0: " + beginIndex);
        } else if (endIndex < beginIndex) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
        } else if (endIndex > string.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
        } else if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else {
            if (charset.equals(Util.UTF_8)) {
                return mo428writeUtf8(string, beginIndex, endIndex);
            }
            byte[] data = string.substring(beginIndex, endIndex).getBytes(charset);
            return mo415write(data, 0, data.length);
        }
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public Buffer mo414write(byte[] source) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        return mo415write(source, 0, source.length);
    }

    @Override // okio.BufferedSink
    /* renamed from: write */
    public Buffer mo415write(byte[] source, int offset, int byteCount) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        Util.checkOffsetAndCount(source.length, offset, byteCount);
        int limit = offset + byteCount;
        while (offset < limit) {
            Segment tail = writableSegment(1);
            int toCopy = Math.min(limit - offset, 8192 - tail.limit);
            System.arraycopy(source, offset, tail.data, tail.limit, toCopy);
            offset += toCopy;
            tail.limit += toCopy;
        }
        this.size += byteCount;
        return this;
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        int byteCount = source.remaining();
        int remaining = byteCount;
        while (remaining > 0) {
            Segment tail = writableSegment(1);
            int toCopy = Math.min(remaining, 8192 - tail.limit);
            source.get(tail.data, tail.limit, toCopy);
            remaining -= toCopy;
            tail.limit += toCopy;
        }
        this.size += byteCount;
        return byteCount;
    }

    @Override // okio.BufferedSink
    public long writeAll(Source source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        long totalBytesRead = 0;
        while (true) {
            long readCount = source.read(this, PlaybackStateCompat.ACTION_PLAY_FROM_URI);
            if (readCount != -1) {
                totalBytesRead += readCount;
            } else {
                return totalBytesRead;
            }
        }
    }

    @Override // okio.BufferedSink
    public BufferedSink write(Source source, long byteCount) throws IOException {
        while (byteCount > 0) {
            long read = source.read(this, byteCount);
            if (read == -1) {
                throw new EOFException();
            }
            byteCount -= read;
        }
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeByte */
    public Buffer mo416writeByte(int b) {
        Segment tail = writableSegment(1);
        byte[] bArr = tail.data;
        int i = tail.limit;
        tail.limit = i + 1;
        bArr[i] = (byte) b;
        this.size++;
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeShort */
    public Buffer mo423writeShort(int s) {
        Segment tail = writableSegment(2);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((s >>> 8) & 255);
        data[limit2] = (byte) (s & 255);
        tail.limit = limit2 + 1;
        this.size += 2;
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeShortLe */
    public Buffer mo424writeShortLe(int s) {
        return mo423writeShort((int) Util.reverseBytesShort((short) s));
    }

    @Override // okio.BufferedSink
    /* renamed from: writeInt */
    public Buffer mo419writeInt(int i) {
        Segment tail = writableSegment(4);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((i >>> 24) & 255);
        int limit3 = limit2 + 1;
        data[limit2] = (byte) ((i >>> 16) & 255);
        int limit4 = limit3 + 1;
        data[limit3] = (byte) ((i >>> 8) & 255);
        data[limit4] = (byte) (i & 255);
        tail.limit = limit4 + 1;
        this.size += 4;
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeIntLe */
    public Buffer mo420writeIntLe(int i) {
        return mo419writeInt(Util.reverseBytesInt(i));
    }

    @Override // okio.BufferedSink
    /* renamed from: writeLong */
    public Buffer mo421writeLong(long v) {
        Segment tail = writableSegment(8);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((v >>> 56) & 255);
        int limit3 = limit2 + 1;
        data[limit2] = (byte) ((v >>> 48) & 255);
        int limit4 = limit3 + 1;
        data[limit3] = (byte) ((v >>> 40) & 255);
        int limit5 = limit4 + 1;
        data[limit4] = (byte) ((v >>> 32) & 255);
        int limit6 = limit5 + 1;
        data[limit5] = (byte) ((v >>> 24) & 255);
        int limit7 = limit6 + 1;
        data[limit6] = (byte) ((v >>> 16) & 255);
        int limit8 = limit7 + 1;
        data[limit7] = (byte) ((v >>> 8) & 255);
        data[limit8] = (byte) (v & 255);
        tail.limit = limit8 + 1;
        this.size += 8;
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeLongLe */
    public Buffer mo422writeLongLe(long v) {
        return mo421writeLong(Util.reverseBytesLong(v));
    }

    @Override // okio.BufferedSink
    /* renamed from: writeDecimalLong */
    public Buffer mo417writeDecimalLong(long v) {
        int width;
        if (v == 0) {
            return mo416writeByte(48);
        }
        boolean negative = false;
        if (v < 0) {
            v = -v;
            if (v < 0) {
                return mo427writeUtf8("-9223372036854775808");
            }
            negative = true;
        }
        if (v < 100000000) {
            if (v < 10000) {
                if (v < 100) {
                    width = v < 10 ? 1 : 2;
                } else {
                    width = v < 1000 ? 3 : 4;
                }
            } else if (v < 1000000) {
                width = v < 100000 ? 5 : 6;
            } else {
                width = v < 10000000 ? 7 : 8;
            }
        } else if (v < 1000000000000L) {
            if (v < 10000000000L) {
                width = v < 1000000000 ? 9 : 10;
            } else {
                width = v < 100000000000L ? 11 : 12;
            }
        } else if (v < 1000000000000000L) {
            if (v < 10000000000000L) {
                width = 13;
            } else {
                width = v < 100000000000000L ? 14 : 15;
            }
        } else if (v < 100000000000000000L) {
            width = v < 10000000000000000L ? 16 : 17;
        } else {
            width = v < 1000000000000000000L ? 18 : 19;
        }
        if (negative) {
            width++;
        }
        Segment tail = writableSegment(width);
        byte[] data = tail.data;
        int pos = tail.limit + width;
        while (v != 0) {
            int digit = (int) (v % 10);
            pos--;
            data[pos] = DIGITS[digit];
            v /= 10;
        }
        if (negative) {
            data[pos - 1] = 45;
        }
        tail.limit += width;
        this.size += width;
        return this;
    }

    @Override // okio.BufferedSink
    /* renamed from: writeHexadecimalUnsignedLong */
    public Buffer mo418writeHexadecimalUnsignedLong(long v) {
        if (v == 0) {
            return mo416writeByte(48);
        }
        int width = (Long.numberOfTrailingZeros(Long.highestOneBit(v)) / 4) + 1;
        Segment tail = writableSegment(width);
        byte[] data = tail.data;
        int start = tail.limit;
        for (int pos = (tail.limit + width) - 1; pos >= start; pos--) {
            data[pos] = DIGITS[(int) (15 & v)];
            v >>>= 4;
        }
        tail.limit += width;
        this.size += width;
        return this;
    }

    public Segment writableSegment(int minimumCapacity) {
        if (minimumCapacity < 1 || minimumCapacity > 8192) {
            throw new IllegalArgumentException();
        }
        Segment segment = this.head;
        if (segment == null) {
            this.head = SegmentPool.take();
            Segment segment2 = this.head;
            segment2.prev = segment2;
            segment2.next = segment2;
            return segment2;
        }
        Segment tail = segment.prev;
        if (tail.limit + minimumCapacity > 8192 || !tail.owner) {
            return tail.push(SegmentPool.take());
        }
        return tail;
    }

    @Override // okio.Sink
    public void write(Buffer source, long byteCount) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (source == this) {
            throw new IllegalArgumentException("source == this");
        }
        Util.checkOffsetAndCount(source.size, 0L, byteCount);
        while (byteCount > 0) {
            if (byteCount < source.head.limit - source.head.pos) {
                Segment segment = this.head;
                Segment tail = segment != null ? segment.prev : null;
                if (tail != null && tail.owner) {
                    if ((tail.limit + byteCount) - (tail.shared ? 0 : tail.pos) <= PlaybackStateCompat.ACTION_PLAY_FROM_URI) {
                        source.head.writeTo(tail, (int) byteCount);
                        source.size -= byteCount;
                        this.size += byteCount;
                        return;
                    }
                }
                source.head = source.head.split((int) byteCount);
            }
            Segment tail2 = source.head;
            long movedByteCount = tail2.limit - tail2.pos;
            source.head = tail2.pop();
            Segment segment2 = this.head;
            if (segment2 == null) {
                this.head = tail2;
                Segment segment3 = this.head;
                segment3.prev = segment3;
                segment3.next = segment3;
            } else {
                segment2.prev.push(tail2).compact();
            }
            source.size -= movedByteCount;
            this.size += movedByteCount;
            byteCount -= movedByteCount;
        }
    }

    @Override // okio.Source
    public long read(Buffer sink, long byteCount) {
        if (sink == null) {
            throw new IllegalArgumentException("sink == null");
        }
        if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        }
        long j = this.size;
        if (j == 0) {
            return -1L;
        }
        if (byteCount > j) {
            byteCount = this.size;
        }
        sink.write(this, byteCount);
        return byteCount;
    }

    @Override // okio.BufferedSource
    public long indexOf(byte b) {
        return indexOf(b, 0L, Long.MAX_VALUE);
    }

    @Override // okio.BufferedSource
    public long indexOf(byte b, long fromIndex) {
        return indexOf(b, fromIndex, Long.MAX_VALUE);
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x004b  */
    @Override // okio.BufferedSource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public long indexOf(byte r11, long r12, long r14) {
        /*
            r10 = this;
            r0 = 0
            int r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r2 < 0) goto L7c
            int r0 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r0 < 0) goto L7c
            long r0 = r10.size
            int r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r2 <= 0) goto L12
            long r14 = r10.size
        L12:
            r0 = -1
            int r2 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r2 != 0) goto L19
            return r0
        L19:
            okio.Segment r2 = r10.head
            if (r2 != 0) goto L1e
            return r0
        L1e:
            long r3 = r10.size
            long r3 = r3 - r12
            int r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r5 >= 0) goto L35
            long r3 = r10.size
        L27:
            int r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r5 <= 0) goto L47
            okio.Segment r2 = r2.prev
            int r5 = r2.limit
            int r6 = r2.pos
            int r5 = r5 - r6
            long r5 = (long) r5
            long r3 = r3 - r5
            goto L27
        L35:
            r3 = 0
        L37:
            int r5 = r2.limit
            int r6 = r2.pos
            int r5 = r5 - r6
            long r5 = (long) r5
            long r5 = r5 + r3
            r7 = r5
            int r9 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r9 >= 0) goto L47
            okio.Segment r2 = r2.next
            r3 = r7
            goto L37
        L47:
            int r5 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r5 >= 0) goto L7b
            byte[] r5 = r2.data
            int r6 = r2.limit
            long r6 = (long) r6
            int r8 = r2.pos
            long r8 = (long) r8
            long r8 = r8 + r14
            long r8 = r8 - r3
            long r6 = java.lang.Math.min(r6, r8)
            int r7 = (int) r6
            int r6 = r2.pos
            long r8 = (long) r6
            long r8 = r8 + r12
            long r8 = r8 - r3
            int r6 = (int) r8
        L60:
            if (r6 >= r7) goto L70
            r8 = r5[r6]
            if (r8 != r11) goto L6d
            int r0 = r2.pos
            int r0 = r6 - r0
            long r0 = (long) r0
            long r0 = r0 + r3
            return r0
        L6d:
            int r6 = r6 + 1
            goto L60
        L70:
            int r8 = r2.limit
            int r9 = r2.pos
            int r8 = r8 - r9
            long r8 = (long) r8
            long r3 = r3 + r8
            r12 = r3
            okio.Segment r2 = r2.next
            goto L47
        L7b:
            return r0
        L7c:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            long r3 = r10.size
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r1[r2] = r3
            java.lang.Long r2 = java.lang.Long.valueOf(r12)
            r3 = 1
            r1[r3] = r2
            r2 = 2
            java.lang.Long r3 = java.lang.Long.valueOf(r14)
            r1[r2] = r3
            java.lang.String r2 = "size=%s fromIndex=%s toIndex=%s"
            java.lang.String r1 = java.lang.String.format(r2, r1)
            r0.<init>(r1)
            throw r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.indexOf(byte, long, long):long");
    }

    @Override // okio.BufferedSource
    public long indexOf(ByteString bytes) throws IOException {
        return indexOf(bytes, 0L);
    }

    @Override // okio.BufferedSource
    public long indexOf(ByteString bytes, long fromIndex) throws IOException {
        long offset;
        int pos;
        int segmentLimit;
        byte[] data;
        Segment s;
        if (bytes.size() != 0) {
            if (fromIndex < 0) {
                throw new IllegalArgumentException("fromIndex < 0");
            }
            Segment s2 = this.head;
            if (s2 == null) {
                return -1L;
            }
            if (this.size - fromIndex < fromIndex) {
                offset = this.size;
                while (offset > fromIndex) {
                    s2 = s2.prev;
                    offset -= s2.limit - s2.pos;
                }
            } else {
                offset = 0;
                while (true) {
                    long nextOffset = (s2.limit - s2.pos) + offset;
                    if (nextOffset >= fromIndex) {
                        break;
                    }
                    s2 = s2.next;
                    offset = nextOffset;
                }
            }
            byte b0 = bytes.getByte(0);
            int bytesSize = bytes.size();
            long resultLimit = 1 + (this.size - bytesSize);
            long fromIndex2 = fromIndex;
            Segment s3 = s2;
            long offset2 = offset;
            while (offset2 < resultLimit) {
                byte[] data2 = s3.data;
                int segmentLimit2 = (int) Math.min(s3.limit, (s3.pos + resultLimit) - offset2);
                int pos2 = (int) ((s3.pos + fromIndex2) - offset2);
                while (pos2 < segmentLimit2) {
                    if (data2[pos2] == b0) {
                        int segmentLimit3 = pos2 + 1;
                        pos = pos2;
                        segmentLimit = segmentLimit2;
                        data = data2;
                        s = s3;
                        if (rangeEquals(s3, segmentLimit3, bytes, 1, bytesSize)) {
                            return (pos - s.pos) + offset2;
                        }
                    } else {
                        pos = pos2;
                        segmentLimit = segmentLimit2;
                        data = data2;
                        s = s3;
                    }
                    pos2 = pos + 1;
                    s3 = s;
                    segmentLimit2 = segmentLimit;
                    data2 = data;
                }
                Segment s4 = s3;
                offset2 += s4.limit - s4.pos;
                fromIndex2 = offset2;
                s3 = s4.next;
            }
            return -1L;
        }
        throw new IllegalArgumentException("bytes is empty");
    }

    @Override // okio.BufferedSource
    public long indexOfElement(ByteString targetBytes) {
        return indexOfElement(targetBytes, 0L);
    }

    @Override // okio.BufferedSource
    public long indexOfElement(ByteString targetBytes, long fromIndex) {
        long offset;
        Buffer buffer = this;
        if (fromIndex < 0) {
            throw new IllegalArgumentException("fromIndex < 0");
        }
        Segment s = buffer.head;
        if (s == null) {
            return -1L;
        }
        if (buffer.size - fromIndex < fromIndex) {
            offset = buffer.size;
            while (offset > fromIndex) {
                s = s.prev;
                offset -= s.limit - s.pos;
            }
        } else {
            offset = 0;
            while (true) {
                long nextOffset = (s.limit - s.pos) + offset;
                if (nextOffset >= fromIndex) {
                    break;
                }
                s = s.next;
                offset = nextOffset;
            }
        }
        int i = 0;
        if (targetBytes.size() == 2) {
            int b0 = targetBytes.getByte(0);
            int b1 = targetBytes.getByte(1);
            long fromIndex2 = fromIndex;
            while (offset < buffer.size) {
                byte[] data = s.data;
                int limit = s.limit;
                for (int pos = (int) ((s.pos + fromIndex2) - offset); pos < limit; pos++) {
                    int b = data[pos];
                    if (b == b0 || b == b1) {
                        return (pos - s.pos) + offset;
                    }
                }
                int limit2 = s.limit;
                offset += limit2 - s.pos;
                fromIndex2 = offset;
                s = s.next;
            }
            return -1L;
        }
        byte[] targetByteArray = targetBytes.internalArray();
        long fromIndex3 = fromIndex;
        while (offset < buffer.size) {
            byte[] data2 = s.data;
            int pos2 = (int) ((s.pos + fromIndex3) - offset);
            int limit3 = s.limit;
            while (pos2 < limit3) {
                byte b2 = data2[pos2];
                int length = targetByteArray.length;
                while (i < length) {
                    byte t = targetByteArray[i];
                    if (b2 == t) {
                        return (pos2 - s.pos) + offset;
                    }
                    i++;
                }
                pos2++;
                i = 0;
            }
            offset += s.limit - s.pos;
            fromIndex3 = offset;
            s = s.next;
            buffer = this;
            i = 0;
        }
        return -1L;
    }

    @Override // okio.BufferedSource
    public boolean rangeEquals(long offset, ByteString bytes) {
        return rangeEquals(offset, bytes, 0, bytes.size());
    }

    @Override // okio.BufferedSource
    public boolean rangeEquals(long offset, ByteString bytes, int bytesOffset, int byteCount) {
        if (offset < 0 || bytesOffset < 0 || byteCount < 0 || this.size - offset < byteCount || bytes.size() - bytesOffset < byteCount) {
            return false;
        }
        for (int i = 0; i < byteCount; i++) {
            if (getByte(i + offset) != bytes.getByte(bytesOffset + i)) {
                return false;
            }
        }
        return true;
    }

    private boolean rangeEquals(Segment segment, int segmentPos, ByteString bytes, int bytesOffset, int bytesLimit) {
        int segmentLimit = segment.limit;
        byte[] data = segment.data;
        for (int i = bytesOffset; i < bytesLimit; i++) {
            if (segmentPos == segmentLimit) {
                segment = segment.next;
                data = segment.data;
                segmentPos = segment.pos;
                segmentLimit = segment.limit;
            }
            if (data[segmentPos] != bytes.getByte(i)) {
                return false;
            }
            segmentPos++;
        }
        return true;
    }

    @Override // okio.BufferedSink, okio.Sink, java.io.Flushable
    public void flush() {
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return true;
    }

    @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    @Override // okio.Source
    public Timeout timeout() {
        return Timeout.NONE;
    }

    List<Integer> segmentSizes() {
        if (this.head == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        result.add(Integer.valueOf(this.head.limit - this.head.pos));
        for (Segment s = this.head.next; s != this.head; s = s.next) {
            result.add(Integer.valueOf(s.limit - s.pos));
        }
        return result;
    }

    public ByteString md5() {
        return digest("MD5");
    }

    public ByteString sha1() {
        return digest("SHA-1");
    }

    public ByteString sha256() {
        return digest("SHA-256");
    }

    public ByteString sha512() {
        return digest("SHA-512");
    }

    private ByteString digest(String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            if (this.head != null) {
                messageDigest.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
                for (Segment s = this.head.next; s != this.head; s = s.next) {
                    messageDigest.update(s.data, s.pos, s.limit - s.pos);
                }
            }
            return ByteString.m2of(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

    public ByteString hmacSha1(ByteString key) {
        return hmac("HmacSHA1", key);
    }

    public ByteString hmacSha256(ByteString key) {
        return hmac("HmacSHA256", key);
    }

    public ByteString hmacSha512(ByteString key) {
        return hmac("HmacSHA512", key);
    }

    private ByteString hmac(String algorithm, ByteString key) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
            if (this.head != null) {
                mac.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
                for (Segment s = this.head.next; s != this.head; s = s.next) {
                    mac.update(s.data, s.pos, s.limit - s.pos);
                }
            }
            return ByteString.m2of(mac.doFinal());
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new AssertionError();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Buffer)) {
            return false;
        }
        Buffer that = (Buffer) o;
        long j = this.size;
        if (j != that.size) {
            return false;
        }
        if (j == 0) {
            return true;
        }
        Segment sa = this.head;
        Segment sb = that.head;
        int posA = sa.pos;
        int posB = sb.pos;
        long pos = 0;
        while (pos < this.size) {
            long count = Math.min(sa.limit - posA, sb.limit - posB);
            int i = 0;
            while (i < count) {
                int posA2 = posA + 1;
                int posB2 = posB + 1;
                if (sa.data[posA] != sb.data[posB]) {
                    return false;
                }
                i++;
                posA = posA2;
                posB = posB2;
            }
            int i2 = sa.limit;
            if (posA == i2) {
                sa = sa.next;
                posA = sa.pos;
            }
            if (posB == sb.limit) {
                sb = sb.next;
                posB = sb.pos;
            }
            pos += count;
        }
        return true;
    }

    public int hashCode() {
        Segment s = this.head;
        if (s == null) {
            return 0;
        }
        int result = 1;
        do {
            int limit = s.limit;
            for (int pos = s.pos; pos < limit; pos++) {
                result = (result * 31) + s.data[pos];
            }
            s = s.next;
        } while (s != this.head);
        return result;
    }

    public String toString() {
        return snapshot().toString();
    }

    public Buffer clone() {
        Buffer result = new Buffer();
        if (this.size == 0) {
            return result;
        }
        result.head = this.head.sharedCopy();
        Segment segment = result.head;
        segment.prev = segment;
        segment.next = segment;
        for (Segment s = this.head.next; s != this.head; s = s.next) {
            result.head.prev.push(s.sharedCopy());
        }
        result.size = this.size;
        return result;
    }

    public ByteString snapshot() {
        long j = this.size;
        if (j > 2147483647L) {
            throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.size);
        }
        return snapshot((int) j);
    }

    public ByteString snapshot(int byteCount) {
        return byteCount == 0 ? ByteString.EMPTY : new SegmentedByteString(this, byteCount);
    }

    public UnsafeCursor readUnsafe() {
        return readUnsafe(new UnsafeCursor());
    }

    public UnsafeCursor readUnsafe(UnsafeCursor unsafeCursor) {
        if (unsafeCursor.buffer != null) {
            throw new IllegalStateException("already attached to a buffer");
        }
        unsafeCursor.buffer = this;
        unsafeCursor.readWrite = false;
        return unsafeCursor;
    }

    public UnsafeCursor readAndWriteUnsafe() {
        return readAndWriteUnsafe(new UnsafeCursor());
    }

    public UnsafeCursor readAndWriteUnsafe(UnsafeCursor unsafeCursor) {
        if (unsafeCursor.buffer != null) {
            throw new IllegalStateException("already attached to a buffer");
        }
        unsafeCursor.buffer = this;
        unsafeCursor.readWrite = true;
        return unsafeCursor;
    }

    /* loaded from: classes.dex */
    public static final class UnsafeCursor implements Closeable {
        public Buffer buffer;
        public byte[] data;
        public boolean readWrite;
        private Segment segment;
        public long offset = -1;
        public int start = -1;
        public int end = -1;

        public int next() {
            if (this.offset == this.buffer.size) {
                throw new IllegalStateException();
            }
            long j = this.offset;
            return j == -1 ? seek(0L) : seek(j + (this.end - this.start));
        }

        public int seek(long offset) {
            Segment next;
            long nextOffset;
            if (offset < -1 || offset > this.buffer.size) {
                throw new ArrayIndexOutOfBoundsException(String.format("offset=%s > size=%s", Long.valueOf(offset), Long.valueOf(this.buffer.size)));
            }
            if (offset == -1 || offset == this.buffer.size) {
                this.segment = null;
                this.offset = offset;
                this.data = null;
                this.start = -1;
                this.end = -1;
                return -1;
            }
            long min = 0;
            long max = this.buffer.size;
            Segment head = this.buffer.head;
            Segment tail = this.buffer.head;
            Segment segment = this.segment;
            if (segment != null) {
                long segmentOffset = this.offset - (this.start - segment.pos);
                if (segmentOffset > offset) {
                    max = segmentOffset;
                    tail = this.segment;
                } else {
                    min = segmentOffset;
                    head = this.segment;
                }
            }
            if (max - offset > offset - min) {
                next = head;
                nextOffset = min;
                while (offset >= (next.limit - next.pos) + nextOffset) {
                    nextOffset += next.limit - next.pos;
                    next = next.next;
                }
            } else {
                next = tail;
                nextOffset = max;
                while (nextOffset > offset) {
                    next = next.prev;
                    nextOffset -= next.limit - next.pos;
                }
            }
            if (this.readWrite && next.shared) {
                Segment unsharedNext = next.unsharedCopy();
                if (this.buffer.head == next) {
                    this.buffer.head = unsharedNext;
                }
                next = next.push(unsharedNext);
                next.prev.pop();
            }
            this.segment = next;
            this.offset = offset;
            this.data = next.data;
            this.start = next.pos + ((int) (offset - nextOffset));
            this.end = next.limit;
            return this.end - this.start;
        }

        public long resizeBuffer(long newSize) {
            Buffer buffer = this.buffer;
            if (buffer == null) {
                throw new IllegalStateException("not attached to a buffer");
            }
            if (!this.readWrite) {
                throw new IllegalStateException("resizeBuffer() only permitted for read/write buffers");
            }
            long oldSize = buffer.size;
            if (newSize <= oldSize) {
                if (newSize < 0) {
                    throw new IllegalArgumentException("newSize < 0: " + newSize);
                }
                long bytesToSubtract = oldSize - newSize;
                while (true) {
                    if (bytesToSubtract <= 0) {
                        break;
                    }
                    Segment tail = this.buffer.head.prev;
                    int tailSize = tail.limit - tail.pos;
                    if (tailSize <= bytesToSubtract) {
                        this.buffer.head = tail.pop();
                        SegmentPool.recycle(tail);
                        bytesToSubtract -= tailSize;
                    } else {
                        tail.limit = (int) (tail.limit - bytesToSubtract);
                        break;
                    }
                }
                this.segment = null;
                this.offset = newSize;
                this.data = null;
                this.start = -1;
                this.end = -1;
            } else if (newSize > oldSize) {
                boolean needsToSeek = true;
                long bytesToAdd = newSize - oldSize;
                while (bytesToAdd > 0) {
                    Segment tail2 = this.buffer.writableSegment(1);
                    int segmentBytesToAdd = (int) Math.min(bytesToAdd, 8192 - tail2.limit);
                    tail2.limit += segmentBytesToAdd;
                    bytesToAdd -= segmentBytesToAdd;
                    if (needsToSeek) {
                        this.segment = tail2;
                        this.offset = oldSize;
                        this.data = tail2.data;
                        this.start = tail2.limit - segmentBytesToAdd;
                        this.end = tail2.limit;
                        needsToSeek = false;
                    }
                }
            }
            this.buffer.size = newSize;
            return oldSize;
        }

        public long expandBuffer(int minByteCount) {
            if (minByteCount <= 0) {
                throw new IllegalArgumentException("minByteCount <= 0: " + minByteCount);
            } else if (minByteCount > 8192) {
                throw new IllegalArgumentException("minByteCount > Segment.SIZE: " + minByteCount);
            } else {
                Buffer buffer = this.buffer;
                if (buffer == null) {
                    throw new IllegalStateException("not attached to a buffer");
                }
                if (!this.readWrite) {
                    throw new IllegalStateException("expandBuffer() only permitted for read/write buffers");
                }
                long oldSize = buffer.size;
                Segment tail = this.buffer.writableSegment(minByteCount);
                int result = 8192 - tail.limit;
                tail.limit = 8192;
                this.buffer.size = result + oldSize;
                this.segment = tail;
                this.offset = oldSize;
                this.data = tail.data;
                this.start = 8192 - result;
                this.end = 8192;
                return result;
            }
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            if (this.buffer == null) {
                throw new IllegalStateException("not attached to a buffer");
            }
            this.buffer = null;
            this.segment = null;
            this.offset = -1L;
            this.data = null;
            this.start = -1;
            this.end = -1;
        }
    }
}