package okio;

import java.io.IOException;
import java.util.zip.Inflater;

/* loaded from: classes.dex */
public final class InflaterSource implements Source {
    private int bufferBytesHeldByInflater;
    private boolean closed;
    private final Inflater inflater;
    private final BufferedSource source;

    public InflaterSource(Source source, Inflater inflater) {
        this(Okio.buffer(source), inflater);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InflaterSource(BufferedSource source, Inflater inflater) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (inflater == null) {
            throw new IllegalArgumentException("inflater == null");
        }
        this.source = source;
        this.inflater = inflater;
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0058, code lost:
        releaseInflatedBytes();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x005f, code lost:
        if (r1.pos != r1.limit) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0061, code lost:
        r9.head = r1.pop();
        okio.SegmentPool.recycle(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x006a, code lost:
        return -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:?, code lost:
        return -1;
     */
    @Override // okio.Source
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public long read(okio.Buffer r9, long r10) throws java.io.IOException {
        /*
            r8 = this;
            r0 = 0
            int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r2 < 0) goto L7c
            boolean r2 = r8.closed
            if (r2 != 0) goto L74
            int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r2 != 0) goto Lf
            return r0
        Lf:
            boolean r0 = r8.refill()
            r1 = 1
            okio.Segment r1 = r9.writableSegment(r1)     // Catch: java.util.zip.DataFormatException -> L6d
            int r2 = r1.limit     // Catch: java.util.zip.DataFormatException -> L6d
            int r2 = 8192 - r2
            long r2 = (long) r2     // Catch: java.util.zip.DataFormatException -> L6d
            long r2 = java.lang.Math.min(r10, r2)     // Catch: java.util.zip.DataFormatException -> L6d
            int r3 = (int) r2     // Catch: java.util.zip.DataFormatException -> L6d
            java.util.zip.Inflater r2 = r8.inflater     // Catch: java.util.zip.DataFormatException -> L6d
            byte[] r4 = r1.data     // Catch: java.util.zip.DataFormatException -> L6d
            int r5 = r1.limit     // Catch: java.util.zip.DataFormatException -> L6d
            int r2 = r2.inflate(r4, r5, r3)     // Catch: java.util.zip.DataFormatException -> L6d
            if (r2 <= 0) goto L3b
            int r4 = r1.limit     // Catch: java.util.zip.DataFormatException -> L6d
            int r4 = r4 + r2
            r1.limit = r4     // Catch: java.util.zip.DataFormatException -> L6d
            long r4 = r9.size     // Catch: java.util.zip.DataFormatException -> L6d
            long r6 = (long) r2     // Catch: java.util.zip.DataFormatException -> L6d
            long r4 = r4 + r6
            r9.size = r4     // Catch: java.util.zip.DataFormatException -> L6d
            long r4 = (long) r2     // Catch: java.util.zip.DataFormatException -> L6d
            return r4
        L3b:
            java.util.zip.Inflater r4 = r8.inflater     // Catch: java.util.zip.DataFormatException -> L6d
            boolean r4 = r4.finished()     // Catch: java.util.zip.DataFormatException -> L6d
            if (r4 != 0) goto L58
            java.util.zip.Inflater r4 = r8.inflater     // Catch: java.util.zip.DataFormatException -> L6d
            boolean r4 = r4.needsDictionary()     // Catch: java.util.zip.DataFormatException -> L6d
            if (r4 == 0) goto L4c
            goto L58
        L4c:
            if (r0 != 0) goto L50
        L4f:
            goto Lf
        L50:
            java.io.EOFException r4 = new java.io.EOFException     // Catch: java.util.zip.DataFormatException -> L6d
            java.lang.String r5 = "source exhausted prematurely"
            r4.<init>(r5)     // Catch: java.util.zip.DataFormatException -> L6d
            throw r4     // Catch: java.util.zip.DataFormatException -> L6d
        L58:
            r8.releaseInflatedBytes()     // Catch: java.util.zip.DataFormatException -> L6d
            int r4 = r1.pos     // Catch: java.util.zip.DataFormatException -> L6d
            int r5 = r1.limit     // Catch: java.util.zip.DataFormatException -> L6d
            if (r4 != r5) goto L6a
            okio.Segment r4 = r1.pop()     // Catch: java.util.zip.DataFormatException -> L6d
            r9.head = r4     // Catch: java.util.zip.DataFormatException -> L6d
            okio.SegmentPool.recycle(r1)     // Catch: java.util.zip.DataFormatException -> L6d
        L6a:
            r4 = -1
            return r4
        L6d:
            r1 = move-exception
            java.io.IOException r2 = new java.io.IOException
            r2.<init>(r1)
            throw r2
        L74:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "closed"
            r0.<init>(r1)
            throw r0
        L7c:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "byteCount < 0: "
            r1.append(r2)
            r1.append(r10)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.InflaterSource.read(okio.Buffer, long):long");
    }

    public boolean refill() throws IOException {
        if (!this.inflater.needsInput()) {
            return false;
        }
        releaseInflatedBytes();
        if (this.inflater.getRemaining() != 0) {
            throw new IllegalStateException("?");
        }
        if (this.source.exhausted()) {
            return true;
        }
        Segment head = this.source.buffer().head;
        this.bufferBytesHeldByInflater = head.limit - head.pos;
        this.inflater.setInput(head.data, head.pos, this.bufferBytesHeldByInflater);
        return false;
    }

    private void releaseInflatedBytes() throws IOException {
        int i = this.bufferBytesHeldByInflater;
        if (i == 0) {
            return;
        }
        int toRelease = i - this.inflater.getRemaining();
        this.bufferBytesHeldByInflater -= toRelease;
        this.source.skip(toRelease);
    }

    @Override // okio.Source
    public Timeout timeout() {
        return this.source.timeout();
    }

    @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.inflater.end();
        this.closed = true;
        this.source.close();
    }
}
