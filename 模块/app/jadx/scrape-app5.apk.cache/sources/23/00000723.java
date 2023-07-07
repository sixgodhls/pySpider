package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.http.Streaming;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class BuiltInConverters extends Converter.Factory {
    @Override // retrofit2.Converter.Factory
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == ResponseBody.class) {
            if (Utils.isAnnotationPresent(annotations, Streaming.class)) {
                return StreamingResponseBodyConverter.INSTANCE;
            }
            return BufferingResponseBodyConverter.INSTANCE;
        } else if (type == Void.class) {
            return VoidResponseBodyConverter.INSTANCE;
        } else {
            return null;
        }
    }

    @Override // retrofit2.Converter.Factory
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (RequestBody.class.isAssignableFrom(Utils.getRawType(type))) {
            return RequestBodyConverter.INSTANCE;
        }
        return null;
    }

    /* loaded from: classes.dex */
    static final class VoidResponseBodyConverter implements Converter<ResponseBody, Void> {
        static final VoidResponseBodyConverter INSTANCE = new VoidResponseBodyConverter();

        VoidResponseBodyConverter() {
        }

        @Override // retrofit2.Converter
        public Void convert(ResponseBody value) {
            value.close();
            return null;
        }
    }

    /* loaded from: classes.dex */
    static final class RequestBodyConverter implements Converter<RequestBody, RequestBody> {
        static final RequestBodyConverter INSTANCE = new RequestBodyConverter();

        RequestBodyConverter() {
        }

        @Override // retrofit2.Converter
        public RequestBody convert(RequestBody value) {
            return value;
        }
    }

    /* loaded from: classes.dex */
    static final class StreamingResponseBodyConverter implements Converter<ResponseBody, ResponseBody> {
        static final StreamingResponseBodyConverter INSTANCE = new StreamingResponseBodyConverter();

        StreamingResponseBodyConverter() {
        }

        @Override // retrofit2.Converter
        public ResponseBody convert(ResponseBody value) {
            return value;
        }
    }

    /* loaded from: classes.dex */
    static final class BufferingResponseBodyConverter implements Converter<ResponseBody, ResponseBody> {
        static final BufferingResponseBodyConverter INSTANCE = new BufferingResponseBodyConverter();

        BufferingResponseBodyConverter() {
        }

        @Override // retrofit2.Converter
        public ResponseBody convert(ResponseBody value) throws IOException {
            try {
                return Utils.buffer(value);
            } finally {
                value.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ToStringConverter implements Converter<Object, String> {
        static final ToStringConverter INSTANCE = new ToStringConverter();

        ToStringConverter() {
        }

        @Override // retrofit2.Converter
        public String convert(Object value) {
            return value.toString();
        }
    }
}