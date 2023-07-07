package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public interface CallAdapter<R, T> {
    T adapt(Call<R> call);

    Type responseType();

    /* loaded from: classes.dex */
    public static abstract class Factory {
        @Nullable
        public abstract CallAdapter<?, ?> get(Type type, Annotation[] annotationArr, Retrofit retrofit);

        /* JADX INFO: Access modifiers changed from: protected */
        public static Type getParameterUpperBound(int index, ParameterizedType type) {
            return Utils.getParameterUpperBound(index, type);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
    }
}
