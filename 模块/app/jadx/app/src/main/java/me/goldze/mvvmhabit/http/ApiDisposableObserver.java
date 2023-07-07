package me.goldze.mvvmhabit.http;

import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/* loaded from: classes.dex */
public abstract class ApiDisposableObserver<T> extends DisposableObserver<T> {

    /* loaded from: classes.dex */
    public static final class CodeRule {
        static final int CODE_200 = 200;
        static final int CODE_220 = 220;
        static final int CODE_300 = 300;
        static final int CODE_330 = 330;
        static final int CODE_500 = 500;
        static final int CODE_502 = 502;
        static final int CODE_503 = 503;
        static final int CODE_510 = 510;
        static final int CODE_530 = 530;
        static final int CODE_551 = 551;
    }

    public abstract void onResult(T t);

    @Override // io.reactivex.Observer
    public void onComplete() {
    }

    @Override // io.reactivex.Observer
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof ResponseThrowable) {
            ResponseThrowable rError = (ResponseThrowable) e;
            ToastUtils.showShort(rError.message);
            return;
        }
        ToastUtils.showShort("网络异常");
    }

    @Override // io.reactivex.observers.DisposableObserver
    public void onStart() {
        super.onStart();
        ToastUtils.showShort("http is start");
        if (!NetworkUtil.isNetworkAvailable(Utils.getContext())) {
            KLog.d("无网络，读取缓存数据");
            onComplete();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // io.reactivex.Observer
    public void onNext(Object o) {
        BaseResponse baseResponse = (BaseResponse) o;
        int code = baseResponse.getCode();
        if (code == 200) {
            onResult(baseResponse.getResult());
        } else if (code == 220) {
            onResult(baseResponse.getResult());
        } else if (code == 300) {
            KLog.e("请求失败");
            ToastUtils.showShort("错误代码:", Integer.valueOf(baseResponse.getCode()));
        } else if (code == 330) {
            ToastUtils.showShort(baseResponse.getMessage());
        } else if (code == 500) {
            ToastUtils.showShort("错误代码:", Integer.valueOf(baseResponse.getCode()));
        } else if (code == 510) {
            ToastUtils.showShort("token已过期，请重新登录");
            AppManager.getAppManager().finishAllActivity();
        } else if (code == 530) {
            ToastUtils.showShort("请先登录");
        } else if (code != 551) {
            switch (code) {
                case 502:
                    KLog.e("没有数据");
                    return;
                case 503:
                    KLog.e("参数为空");
                    return;
                default:
                    ToastUtils.showShort("错误代码:", Integer.valueOf(baseResponse.getCode()));
                    return;
            }
        } else {
            ToastUtils.showShort("错误代码:", Integer.valueOf(baseResponse.getCode()));
        }
    }
}
