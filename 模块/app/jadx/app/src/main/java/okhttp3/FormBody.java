package okhttp3;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;

/* loaded from: classes.dex */
public final class FormBody extends RequestBody {
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");
    private final List<String> encodedNames;
    private final List<String> encodedValues;

    FormBody(List<String> encodedNames, List<String> encodedValues) {
        this.encodedNames = Util.immutableList(encodedNames);
        this.encodedValues = Util.immutableList(encodedValues);
    }

    public int size() {
        return this.encodedNames.size();
    }

    public String encodedName(int index) {
        return this.encodedNames.get(index);
    }

    public String name(int index) {
        return HttpUrl.percentDecode(encodedName(index), true);
    }

    public String encodedValue(int index) {
        return this.encodedValues.get(index);
    }

    public String value(int index) {
        return HttpUrl.percentDecode(encodedValue(index), true);
    }

    @Override // okhttp3.RequestBody
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    @Override // okhttp3.RequestBody
    public long contentLength() {
        return writeOrCountBytes(null, true);
    }

    @Override // okhttp3.RequestBody
    public void writeTo(BufferedSink sink) throws IOException {
        writeOrCountBytes(sink, false);
    }

    private long writeOrCountBytes(@Nullable BufferedSink sink, boolean countBytes) {
        Buffer buffer;
        if (countBytes) {
            buffer = new Buffer();
        } else {
            buffer = sink.buffer();
        }
        int size = this.encodedNames.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                buffer.mo354writeByte(38);
            }
            buffer.mo365writeUtf8(this.encodedNames.get(i));
            buffer.mo354writeByte(61);
            buffer.mo365writeUtf8(this.encodedValues.get(i));
        }
        if (!countBytes) {
            return 0L;
        }
        long byteCount = buffer.size();
        buffer.clear();
        return byteCount;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private final Charset charset;
        private final List<String> names;
        private final List<String> values;

        public Builder() {
            this(null);
        }

        public Builder(Charset charset) {
            this.names = new ArrayList();
            this.values = new ArrayList();
            this.charset = charset;
        }

        public Builder add(String name, String value) {
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            if (value == null) {
                throw new NullPointerException("value == null");
            }
            this.names.add(HttpUrl.canonicalize(name, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, true, this.charset));
            this.values.add(HttpUrl.canonicalize(value, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, true, this.charset));
            return this;
        }

        public Builder addEncoded(String name, String value) {
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            if (value == null) {
                throw new NullPointerException("value == null");
            }
            this.names.add(HttpUrl.canonicalize(name, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, true, this.charset));
            this.values.add(HttpUrl.canonicalize(value, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, true, this.charset));
            return this;
        }

        public FormBody build() {
            return new FormBody(this.names, this.values);
        }
    }
}
