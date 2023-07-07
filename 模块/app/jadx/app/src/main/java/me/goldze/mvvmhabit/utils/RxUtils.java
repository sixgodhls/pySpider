package me.goldze.mvvmhabit.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ExceptionHandle;

/* loaded from: classes.dex */
public class RxUtils {
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull Context lifecycle) {
        if (lifecycle instanceof LifecycleProvider) {
            return ((LifecycleProvider) lifecycle).bindToLifecycle();
        }
        throw new IllegalArgumentException("context not the LifecycleProvider type");
    }

    public static LifecycleTransformer bindToLifecycle(@NonNull Fragment lifecycle) {
        if (lifecycle instanceof LifecycleProvider) {
            return ((LifecycleProvider) lifecycle).bindToLifecycle();
        }
        throw new IllegalArgumentException("fragment not the LifecycleProvider type");
    }

    public static LifecycleTransformer bindToLifecycle(@NonNull LifecycleProvider lifecycle) {
        return lifecycle.bindToLifecycle();
    }

    public static ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() { // from class: me.goldze.mvvmhabit.utils.RxUtils.1
            @Override // io.reactivex.ObservableTransformer
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static ObservableTransformer exceptionTransformer() {
        return new ObservableTransformer() { // from class: me.goldze.mvvmhabit.utils.RxUtils.2
            @Override // io.reactivex.ObservableTransformer
            public ObservableSource apply(Observable observable) {
                return observable.onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }

    /* loaded from: classes.dex */
    private static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        private HttpResponseFunc() {
        }

        @Override // io.reactivex.functions.Function
        /* renamed from: apply  reason: avoid collision after fix types in other method */
        public Observable<T> mo339apply(Throwable t) {
            return Observable.error(ExceptionHandle.handleException(t));
        }
    }

    /* loaded from: classes.dex */
    private static class HandleFuc<T> implements Function<BaseResponse<T>, T> {
        private HandleFuc() {
        }

        @Override // io.reactivex.functions.Function
        /* renamed from: apply */
        public /* bridge */ /* synthetic */ Object mo339apply(Object obj) throws Exception {
            return apply((BaseResponse) ((BaseResponse) obj));
        }

        public T apply(BaseResponse<T> response) {
            if (!response.isOk()) {
                StringBuilder sb = new StringBuilder();
                sb.append(response.getCode());
                sb.append("");
                sb.append(response.getMessage());
                throw new RuntimeException(!"".equals(sb.toString()) ? response.getMessage() : "");
            }
            return response.getResult();
        }
    }
}
