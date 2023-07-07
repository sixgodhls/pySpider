package okhttp3.internal.http2;

import android.support.p003v7.widget.ActivityChooserView;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.internal.Util;
import okhttp3.internal.http2.Hpack;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class Http2Reader implements Closeable {
    static final Logger logger = Logger.getLogger(Http2.class.getName());
    private final boolean client;
    private final ContinuationSource continuation;
    final Hpack.Reader hpackReader;
    private final BufferedSource source;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Handler {
        void ackSettings();

        void alternateService(int i, String str, ByteString byteString, String str2, int i2, long j);

        void data(boolean z, int i, BufferedSource bufferedSource, int i2) throws IOException;

        void goAway(int i, ErrorCode errorCode, ByteString byteString);

        void headers(boolean z, int i, int i2, List<Header> list);

        void ping(boolean z, int i, int i2);

        void priority(int i, int i2, int i3, boolean z);

        void pushPromise(int i, int i2, List<Header> list) throws IOException;

        void rstStream(int i, ErrorCode errorCode);

        void settings(boolean z, Settings settings);

        void windowUpdate(int i, long j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2Reader(BufferedSource source, boolean client) {
        this.source = source;
        this.client = client;
        this.continuation = new ContinuationSource(this.source);
        this.hpackReader = new Hpack.Reader(4096, this.continuation);
    }

    public void readConnectionPreface(Handler handler) throws IOException {
        if (this.client) {
            if (!nextFrame(true, handler)) {
                throw Http2.ioException("Required SETTINGS preface not received", new Object[0]);
            }
            return;
        }
        ByteString connectionPreface = this.source.readByteString(Http2.CONNECTION_PREFACE.size());
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(Util.format("<< CONNECTION %s", connectionPreface.hex()));
        }
        if (!Http2.CONNECTION_PREFACE.equals(connectionPreface)) {
            throw Http2.ioException("Expected a connection header but was %s", connectionPreface.utf8());
        }
    }

    public boolean nextFrame(boolean requireSettings, Handler handler) throws IOException {
        try {
            this.source.require(9L);
            int length = readMedium(this.source);
            if (length < 0 || length > 16384) {
                throw Http2.ioException("FRAME_SIZE_ERROR: %s", Integer.valueOf(length));
            }
            byte type = (byte) (this.source.readByte() & 255);
            if (requireSettings && type != 4) {
                throw Http2.ioException("Expected a SETTINGS frame but was %s", Byte.valueOf(type));
            }
            byte flags = (byte) (this.source.readByte() & 255);
            int streamId = this.source.readInt() & ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(Http2.frameLog(true, streamId, length, type, flags));
            }
            switch (type) {
                case 0:
                    readData(handler, length, flags, streamId);
                    break;
                case 1:
                    readHeaders(handler, length, flags, streamId);
                    break;
                case 2:
                    readPriority(handler, length, flags, streamId);
                    break;
                case 3:
                    readRstStream(handler, length, flags, streamId);
                    break;
                case 4:
                    readSettings(handler, length, flags, streamId);
                    break;
                case 5:
                    readPushPromise(handler, length, flags, streamId);
                    break;
                case 6:
                    readPing(handler, length, flags, streamId);
                    break;
                case 7:
                    readGoAway(handler, length, flags, streamId);
                    break;
                case 8:
                    readWindowUpdate(handler, length, flags, streamId);
                    break;
                default:
                    this.source.skip(length);
                    break;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void readHeaders(Handler handler, int length, byte flags, int streamId) throws IOException {
        short padding = 0;
        if (streamId == 0) {
            throw Http2.ioException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0", new Object[0]);
        }
        boolean endStream = (flags & 1) != 0;
        if ((flags & 8) != 0) {
            padding = (short) (this.source.readByte() & 255);
        }
        if ((flags & 32) != 0) {
            readPriority(handler, streamId);
            length -= 5;
        }
        List<Header> headerBlock = readHeaderBlock(lengthWithoutPadding(length, flags, padding), padding, flags, streamId);
        handler.headers(endStream, streamId, -1, headerBlock);
    }

    private List<Header> readHeaderBlock(int length, short padding, byte flags, int streamId) throws IOException {
        ContinuationSource continuationSource = this.continuation;
        continuationSource.left = length;
        continuationSource.length = length;
        continuationSource.padding = padding;
        continuationSource.flags = flags;
        continuationSource.streamId = streamId;
        this.hpackReader.readHeaders();
        return this.hpackReader.getAndResetHeaderList();
    }

    private void readData(Handler handler, int length, byte flags, int streamId) throws IOException {
        short padding = 0;
        if (streamId == 0) {
            throw Http2.ioException("PROTOCOL_ERROR: TYPE_DATA streamId == 0", new Object[0]);
        }
        boolean gzipped = true;
        boolean inFinished = (flags & 1) != 0;
        if ((flags & 32) == 0) {
            gzipped = false;
        }
        if (gzipped) {
            throw Http2.ioException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA", new Object[0]);
        }
        if ((flags & 8) != 0) {
            padding = (short) (this.source.readByte() & 255);
        }
        handler.data(inFinished, streamId, this.source, lengthWithoutPadding(length, flags, padding));
        this.source.skip(padding);
    }

    private void readPriority(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (length == 5) {
            if (streamId == 0) {
                throw Http2.ioException("TYPE_PRIORITY streamId == 0", new Object[0]);
            }
            readPriority(handler, streamId);
            return;
        }
        throw Http2.ioException("TYPE_PRIORITY length: %d != 5", Integer.valueOf(length));
    }

    private void readPriority(Handler handler, int streamId) throws IOException {
        int w1 = this.source.readInt();
        boolean exclusive = (Integer.MIN_VALUE & w1) != 0;
        int streamDependency = Integer.MAX_VALUE & w1;
        int weight = (this.source.readByte() & 255) + 1;
        handler.priority(streamId, streamDependency, weight, exclusive);
    }

    private void readRstStream(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (length == 4) {
            if (streamId == 0) {
                throw Http2.ioException("TYPE_RST_STREAM streamId == 0", new Object[0]);
            }
            int errorCodeInt = this.source.readInt();
            ErrorCode errorCode = ErrorCode.fromHttp2(errorCodeInt);
            if (errorCode == null) {
                throw Http2.ioException("TYPE_RST_STREAM unexpected error code: %d", Integer.valueOf(errorCodeInt));
            }
            handler.rstStream(streamId, errorCode);
            return;
        }
        throw Http2.ioException("TYPE_RST_STREAM length: %d != 4", Integer.valueOf(length));
    }

    private void readSettings(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (streamId != 0) {
            throw Http2.ioException("TYPE_SETTINGS streamId != 0", new Object[0]);
        }
        if ((flags & 1) != 0) {
            if (length != 0) {
                throw Http2.ioException("FRAME_SIZE_ERROR ack frame should be empty!", new Object[0]);
            }
            handler.ackSettings();
        } else if (length % 6 != 0) {
            throw Http2.ioException("TYPE_SETTINGS length %% 6 != 0: %s", Integer.valueOf(length));
        } else {
            Settings settings = new Settings();
            for (int i = 0; i < length; i += 6) {
                int id = this.source.readShort() & 65535;
                int value = this.source.readInt();
                switch (id) {
                    case 2:
                        if (value != 0 && value != 1) {
                            throw Http2.ioException("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1", new Object[0]);
                        }
                        break;
                    case 3:
                        id = 4;
                        break;
                    case 4:
                        id = 7;
                        if (value < 0) {
                            throw Http2.ioException("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1", new Object[0]);
                        }
                        break;
                    case 5:
                        if (value < 16384 || value > 16777215) {
                            throw Http2.ioException("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: %s", Integer.valueOf(value));
                        }
                        break;
                        break;
                }
                settings.set(id, value);
            }
            handler.settings(false, settings);
        }
    }

    private void readPushPromise(Handler handler, int length, byte flags, int streamId) throws IOException {
        short padding = 0;
        if (streamId == 0) {
            throw Http2.ioException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0", new Object[0]);
        }
        if ((flags & 8) != 0) {
            padding = (short) (this.source.readByte() & 255);
        }
        int promisedStreamId = this.source.readInt() & ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        List<Header> headerBlock = readHeaderBlock(lengthWithoutPadding(length - 4, flags, padding), padding, flags, streamId);
        handler.pushPromise(streamId, promisedStreamId, headerBlock);
    }

    private void readPing(Handler handler, int length, byte flags, int streamId) throws IOException {
        boolean ack = false;
        if (length == 8) {
            if (streamId != 0) {
                throw Http2.ioException("TYPE_PING streamId != 0", new Object[0]);
            }
            int payload1 = this.source.readInt();
            int payload2 = this.source.readInt();
            if ((flags & 1) != 0) {
                ack = true;
            }
            handler.ping(ack, payload1, payload2);
            return;
        }
        throw Http2.ioException("TYPE_PING length != 8: %s", Integer.valueOf(length));
    }

    private void readGoAway(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (length >= 8) {
            if (streamId != 0) {
                throw Http2.ioException("TYPE_GOAWAY streamId != 0", new Object[0]);
            }
            int lastStreamId = this.source.readInt();
            int errorCodeInt = this.source.readInt();
            int opaqueDataLength = length - 8;
            ErrorCode errorCode = ErrorCode.fromHttp2(errorCodeInt);
            if (errorCode == null) {
                throw Http2.ioException("TYPE_GOAWAY unexpected error code: %d", Integer.valueOf(errorCodeInt));
            }
            ByteString debugData = ByteString.EMPTY;
            if (opaqueDataLength > 0) {
                debugData = this.source.readByteString(opaqueDataLength);
            }
            handler.goAway(lastStreamId, errorCode, debugData);
            return;
        }
        throw Http2.ioException("TYPE_GOAWAY length < 8: %s", Integer.valueOf(length));
    }

    private void readWindowUpdate(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (length == 4) {
            long increment = this.source.readInt() & 2147483647L;
            if (increment == 0) {
                throw Http2.ioException("windowSizeIncrement was 0", Long.valueOf(increment));
            }
            handler.windowUpdate(streamId, increment);
            return;
        }
        throw Http2.ioException("TYPE_WINDOW_UPDATE length !=4: %s", Integer.valueOf(length));
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.source.close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ContinuationSource implements Source {
        byte flags;
        int left;
        int length;
        short padding;
        private final BufferedSource source;
        int streamId;

        ContinuationSource(BufferedSource source) {
            this.source = source;
        }

        @Override // okio.Source
        public long read(Buffer sink, long byteCount) throws IOException {
            while (true) {
                int i = this.left;
                if (i == 0) {
                    this.source.skip(this.padding);
                    this.padding = (short) 0;
                    if ((this.flags & 4) != 0) {
                        return -1L;
                    }
                    readContinuationHeader();
                } else {
                    long read = this.source.read(sink, Math.min(byteCount, i));
                    if (read == -1) {
                        return -1L;
                    }
                    this.left = (int) (this.left - read);
                    return read;
                }
            }
        }

        @Override // okio.Source
        public Timeout timeout() {
            return this.source.timeout();
        }

        @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }

        private void readContinuationHeader() throws IOException {
            int previousStreamId = this.streamId;
            int readMedium = Http2Reader.readMedium(this.source);
            this.left = readMedium;
            this.length = readMedium;
            byte type = (byte) (this.source.readByte() & 255);
            this.flags = (byte) (this.source.readByte() & 255);
            if (Http2Reader.logger.isLoggable(Level.FINE)) {
                Http2Reader.logger.fine(Http2.frameLog(true, this.streamId, this.length, type, this.flags));
            }
            this.streamId = this.source.readInt() & ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            if (type == 9) {
                if (this.streamId != previousStreamId) {
                    throw Http2.ioException("TYPE_CONTINUATION streamId changed", new Object[0]);
                }
                return;
            }
            throw Http2.ioException("%s != TYPE_CONTINUATION", Byte.valueOf(type));
        }
    }

    static int readMedium(BufferedSource source) throws IOException {
        return ((source.readByte() & 255) << 16) | ((source.readByte() & 255) << 8) | (source.readByte() & 255);
    }

    static int lengthWithoutPadding(int length, byte flags, short padding) throws IOException {
        if ((flags & 8) != 0) {
            length--;
        }
        if (padding <= length) {
            return (short) (length - padding);
        }
        throw Http2.ioException("PROTOCOL_ERROR padding %s > remaining length %s", Short.valueOf(padding), Integer.valueOf(length));
    }
}