package retrofit2;

import java.io.IOException;
import javax.annotation.Nullable;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RequestBuilder {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String PATH_SEGMENT_ALWAYS_ENCODE_SET = " \"<>^`{}|\\?#";
    private final HttpUrl baseUrl;
    @Nullable
    private RequestBody body;
    @Nullable
    private MediaType contentType;
    @Nullable
    private FormBody.Builder formBuilder;
    private final boolean hasBody;
    private final String method;
    @Nullable
    private MultipartBody.Builder multipartBuilder;
    @Nullable
    private String relativeUrl;
    private final Request.Builder requestBuilder = new Request.Builder();
    @Nullable
    private HttpUrl.Builder urlBuilder;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RequestBuilder(String method, HttpUrl baseUrl, @Nullable String relativeUrl, @Nullable Headers headers, @Nullable MediaType contentType, boolean hasBody, boolean isFormEncoded, boolean isMultipart) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.relativeUrl = relativeUrl;
        this.contentType = contentType;
        this.hasBody = hasBody;
        if (headers != null) {
            this.requestBuilder.headers(headers);
        }
        if (isFormEncoded) {
            this.formBuilder = new FormBody.Builder();
        } else if (isMultipart) {
            this.multipartBuilder = new MultipartBody.Builder();
            this.multipartBuilder.setType(MultipartBody.FORM);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRelativeUrl(Object relativeUrl) {
        this.relativeUrl = relativeUrl.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addHeader(String name, String value) {
        if ("Content-Type".equalsIgnoreCase(name)) {
            MediaType type = MediaType.parse(value);
            if (type == null) {
                throw new IllegalArgumentException("Malformed content type: " + value);
            }
            this.contentType = type;
            return;
        }
        this.requestBuilder.addHeader(name, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPathParam(String name, String value, boolean encoded) {
        String str = this.relativeUrl;
        if (str == null) {
            throw new AssertionError();
        }
        this.relativeUrl = str.replace("{" + name + "}", canonicalizeForPath(value, encoded));
    }

    private static String canonicalizeForPath(String input, boolean alreadyEncoded) {
        int i = 0;
        int limit = input.length();
        while (i < limit) {
            int codePoint = input.codePointAt(i);
            if (codePoint >= 32 && codePoint < 127 && PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint) == -1 && (alreadyEncoded || (codePoint != 47 && codePoint != 37))) {
                i += Character.charCount(codePoint);
            } else {
                Buffer out = new Buffer();
                out.mo366writeUtf8(input, 0, i);
                canonicalizeForPath(out, input, i, limit, alreadyEncoded);
                return out.readUtf8();
            }
        }
        return input;
    }

    private static void canonicalizeForPath(Buffer out, String input, int pos, int limit, boolean alreadyEncoded) {
        Buffer utf8Buffer = null;
        int i = pos;
        while (i < limit) {
            int codePoint = input.codePointAt(i);
            if (!alreadyEncoded || (codePoint != 9 && codePoint != 10 && codePoint != 12 && codePoint != 13)) {
                if (codePoint < 32 || codePoint >= 127 || PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint) != -1 || (!alreadyEncoded && (codePoint == 47 || codePoint == 37))) {
                    if (utf8Buffer == null) {
                        utf8Buffer = new Buffer();
                    }
                    utf8Buffer.mo367writeUtf8CodePoint(codePoint);
                    while (!utf8Buffer.exhausted()) {
                        int b = utf8Buffer.readByte() & 255;
                        out.mo354writeByte(37);
                        out.mo354writeByte((int) HEX_DIGITS[(b >> 4) & 15]);
                        out.mo354writeByte((int) HEX_DIGITS[b & 15]);
                    }
                } else {
                    out.mo367writeUtf8CodePoint(codePoint);
                }
            }
            i += Character.charCount(codePoint);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addQueryParam(String name, @Nullable String value, boolean encoded) {
        String str = this.relativeUrl;
        if (str != null) {
            this.urlBuilder = this.baseUrl.newBuilder(str);
            if (this.urlBuilder == null) {
                throw new IllegalArgumentException("Malformed URL. Base: " + this.baseUrl + ", Relative: " + this.relativeUrl);
            }
            this.relativeUrl = null;
        }
        if (encoded) {
            this.urlBuilder.addEncodedQueryParameter(name, value);
        } else {
            this.urlBuilder.addQueryParameter(name, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addFormField(String name, String value, boolean encoded) {
        if (encoded) {
            this.formBuilder.addEncoded(name, value);
        } else {
            this.formBuilder.add(name, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPart(Headers headers, RequestBody body) {
        this.multipartBuilder.addPart(headers, body);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPart(MultipartBody.Part part) {
        this.multipartBuilder.addPart(part);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBody(RequestBody body) {
        this.body = body;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Request build() {
        HttpUrl url;
        HttpUrl.Builder urlBuilder = this.urlBuilder;
        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = this.baseUrl.resolve(this.relativeUrl);
            if (url == null) {
                throw new IllegalArgumentException("Malformed URL. Base: " + this.baseUrl + ", Relative: " + this.relativeUrl);
            }
        }
        RequestBody body = this.body;
        if (body == null) {
            FormBody.Builder builder = this.formBuilder;
            if (builder != null) {
                body = builder.build();
            } else {
                MultipartBody.Builder builder2 = this.multipartBuilder;
                if (builder2 != null) {
                    body = builder2.build();
                } else if (this.hasBody) {
                    body = RequestBody.create((MediaType) null, new byte[0]);
                }
            }
        }
        MediaType contentType = this.contentType;
        if (contentType != null) {
            if (body != null) {
                body = new ContentTypeOverridingRequestBody(body, contentType);
            } else {
                this.requestBuilder.addHeader("Content-Type", contentType.toString());
            }
        }
        return this.requestBuilder.url(url).method(this.method, body).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ContentTypeOverridingRequestBody extends RequestBody {
        private final MediaType contentType;
        private final RequestBody delegate;

        ContentTypeOverridingRequestBody(RequestBody delegate, MediaType contentType) {
            this.delegate = delegate;
            this.contentType = contentType;
        }

        @Override // okhttp3.RequestBody
        public MediaType contentType() {
            return this.contentType;
        }

        @Override // okhttp3.RequestBody
        public long contentLength() throws IOException {
            return this.delegate.contentLength();
        }

        @Override // okhttp3.RequestBody
        public void writeTo(BufferedSink sink) throws IOException {
            this.delegate.writeTo(sink);
        }
    }
}
