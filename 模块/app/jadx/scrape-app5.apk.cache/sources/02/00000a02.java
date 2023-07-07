package retrofit2.adapter.rxjava2;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

/* loaded from: classes.dex */
public final class RxJava2CallAdapterFactory extends CallAdapter.Factory {
    private final boolean isAsync;
    @Nullable
    private final Scheduler scheduler;

    public static RxJava2CallAdapterFactory create() {
        return new RxJava2CallAdapterFactory(null, false);
    }

    public static RxJava2CallAdapterFactory createAsync() {
        return new RxJava2CallAdapterFactory(null, true);
    }

    public static RxJava2CallAdapterFactory createWithScheduler(Scheduler scheduler) {
        if (scheduler == null) {
            throw new NullPointerException("scheduler == null");
        }
        return new RxJava2CallAdapterFactory(scheduler, false);
    }

    private RxJava2CallAdapterFactory(@Nullable Scheduler scheduler, boolean isAsync) {
        this.scheduler = scheduler;
        this.isAsync = isAsync;
    }

    @Override // retrofit2.CallAdapter.Factory
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Type responseType;
        boolean isResult;
        boolean isBody;
        String name;
        Class<?> rawType = getRawType(returnType);
        if (rawType == Completable.class) {
            return new RxJava2CallAdapter(Void.class, this.scheduler, this.isAsync, false, true, false, false, false, true);
        }
        boolean isMaybe = true;
        boolean isFlowable = rawType == Flowable.class;
        boolean isSingle = rawType == Single.class;
        if (rawType != Maybe.class) {
            isMaybe = false;
        }
        if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            if (isFlowable) {
                name = "Flowable";
            } else if (isSingle) {
                name = "Single";
            } else {
                name = isMaybe ? "Maybe" : "Observable";
            }
            throw new IllegalStateException(name + " return type must be parameterized as " + name + "<Foo> or " + name + "<? extends Foo>");
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            isResult = false;
            isBody = false;
        } else if (rawObservableType == Result.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Result must be parameterized as Result<Foo> or Result<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            isResult = true;
            isBody = false;
        } else {
            responseType = observableType;
            isResult = false;
            isBody = true;
        }
        return new RxJava2CallAdapter(responseType, this.scheduler, this.isAsync, isResult, isBody, isFlowable, isSingle, isMaybe, false);
    }
}