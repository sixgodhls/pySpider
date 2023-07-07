package retrofit2.adapter.rxjava2;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import retrofit2.Call;
import retrofit2.CallAdapter;

/* loaded from: classes.dex */
final class RxJava2CallAdapter<R> implements CallAdapter<R, Object> {
    private final boolean isAsync;
    private final boolean isBody;
    private final boolean isCompletable;
    private final boolean isFlowable;
    private final boolean isMaybe;
    private final boolean isResult;
    private final boolean isSingle;
    private final Type responseType;
    @Nullable
    private final Scheduler scheduler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RxJava2CallAdapter(Type responseType, @Nullable Scheduler scheduler, boolean isAsync, boolean isResult, boolean isBody, boolean isFlowable, boolean isSingle, boolean isMaybe, boolean isCompletable) {
        this.responseType = responseType;
        this.scheduler = scheduler;
        this.isAsync = isAsync;
        this.isResult = isResult;
        this.isBody = isBody;
        this.isFlowable = isFlowable;
        this.isSingle = isSingle;
        this.isMaybe = isMaybe;
        this.isCompletable = isCompletable;
    }

    @Override // retrofit2.CallAdapter
    public Type responseType() {
        return this.responseType;
    }

    @Override // retrofit2.CallAdapter
    public Object adapt(Call<R> call) {
        Observable<?> responseObservable;
        Observable<?> observable;
        if (this.isAsync) {
            responseObservable = new CallEnqueueObservable<>(call);
        } else {
            responseObservable = new CallExecuteObservable<>(call);
        }
        if (this.isResult) {
            observable = new ResultObservable<>(responseObservable);
        } else if (this.isBody) {
            observable = new BodyObservable<>(responseObservable);
        } else {
            observable = responseObservable;
        }
        Scheduler scheduler = this.scheduler;
        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler);
        }
        if (this.isFlowable) {
            return observable.toFlowable(BackpressureStrategy.LATEST);
        }
        if (this.isSingle) {
            return observable.singleOrError();
        }
        if (this.isMaybe) {
            return observable.singleElement();
        }
        if (this.isCompletable) {
            return observable.ignoreElements();
        }
        return observable;
    }
}