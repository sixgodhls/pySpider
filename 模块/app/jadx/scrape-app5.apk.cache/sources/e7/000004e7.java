package okio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public interface BufferedSink extends Sink, WritableByteChannel {
    Buffer buffer();

    BufferedSink emit() throws IOException;

    /* renamed from: emitCompleteSegments */
    BufferedSink mo412emitCompleteSegments() throws IOException;

    @Override // okio.Sink, java.io.Flushable
    void flush() throws IOException;

    OutputStream outputStream();

    /* renamed from: write */
    BufferedSink mo413write(ByteString byteString) throws IOException;

    BufferedSink write(Source source, long j) throws IOException;

    /* renamed from: write */
    BufferedSink mo414write(byte[] bArr) throws IOException;

    /* renamed from: write */
    BufferedSink mo415write(byte[] bArr, int i, int i2) throws IOException;

    long writeAll(Source source) throws IOException;

    /* renamed from: writeByte */
    BufferedSink mo416writeByte(int i) throws IOException;

    /* renamed from: writeDecimalLong */
    BufferedSink mo417writeDecimalLong(long j) throws IOException;

    /* renamed from: writeHexadecimalUnsignedLong */
    BufferedSink mo418writeHexadecimalUnsignedLong(long j) throws IOException;

    /* renamed from: writeInt */
    BufferedSink mo419writeInt(int i) throws IOException;

    /* renamed from: writeIntLe */
    BufferedSink mo420writeIntLe(int i) throws IOException;

    /* renamed from: writeLong */
    BufferedSink mo421writeLong(long j) throws IOException;

    /* renamed from: writeLongLe */
    BufferedSink mo422writeLongLe(long j) throws IOException;

    /* renamed from: writeShort */
    BufferedSink mo423writeShort(int i) throws IOException;

    /* renamed from: writeShortLe */
    BufferedSink mo424writeShortLe(int i) throws IOException;

    /* renamed from: writeString */
    BufferedSink mo425writeString(String str, int i, int i2, Charset charset) throws IOException;

    /* renamed from: writeString */
    BufferedSink mo426writeString(String str, Charset charset) throws IOException;

    /* renamed from: writeUtf8 */
    BufferedSink mo427writeUtf8(String str) throws IOException;

    /* renamed from: writeUtf8 */
    BufferedSink mo428writeUtf8(String str, int i, int i2) throws IOException;

    /* renamed from: writeUtf8CodePoint */
    BufferedSink mo429writeUtf8CodePoint(int i) throws IOException;
}