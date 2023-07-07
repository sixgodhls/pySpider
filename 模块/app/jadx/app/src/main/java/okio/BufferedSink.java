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
    BufferedSink mo350emitCompleteSegments() throws IOException;

    @Override // okio.Sink, java.io.Flushable
    void flush() throws IOException;

    OutputStream outputStream();

    /* renamed from: write */
    BufferedSink mo351write(ByteString byteString) throws IOException;

    BufferedSink write(Source source, long j) throws IOException;

    /* renamed from: write */
    BufferedSink mo352write(byte[] bArr) throws IOException;

    /* renamed from: write */
    BufferedSink mo353write(byte[] bArr, int i, int i2) throws IOException;

    long writeAll(Source source) throws IOException;

    /* renamed from: writeByte */
    BufferedSink mo354writeByte(int i) throws IOException;

    /* renamed from: writeDecimalLong */
    BufferedSink mo355writeDecimalLong(long j) throws IOException;

    /* renamed from: writeHexadecimalUnsignedLong */
    BufferedSink mo356writeHexadecimalUnsignedLong(long j) throws IOException;

    /* renamed from: writeInt */
    BufferedSink mo357writeInt(int i) throws IOException;

    /* renamed from: writeIntLe */
    BufferedSink mo358writeIntLe(int i) throws IOException;

    /* renamed from: writeLong */
    BufferedSink mo359writeLong(long j) throws IOException;

    /* renamed from: writeLongLe */
    BufferedSink mo360writeLongLe(long j) throws IOException;

    /* renamed from: writeShort */
    BufferedSink mo361writeShort(int i) throws IOException;

    /* renamed from: writeShortLe */
    BufferedSink mo362writeShortLe(int i) throws IOException;

    /* renamed from: writeString */
    BufferedSink mo363writeString(String str, int i, int i2, Charset charset) throws IOException;

    /* renamed from: writeString */
    BufferedSink mo364writeString(String str, Charset charset) throws IOException;

    /* renamed from: writeUtf8 */
    BufferedSink mo365writeUtf8(String str) throws IOException;

    /* renamed from: writeUtf8 */
    BufferedSink mo366writeUtf8(String str, int i, int i2) throws IOException;

    /* renamed from: writeUtf8CodePoint */
    BufferedSink mo367writeUtf8CodePoint(int i) throws IOException;
}
