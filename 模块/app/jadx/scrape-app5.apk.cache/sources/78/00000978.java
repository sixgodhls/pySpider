package retrofit2.adapter.rxjava2;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Response;

/* loaded from: classes.dex */
final class ResultObservable<T> extends Observable<Result<T>> {
    private final Observable<Response<T>> upstream;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultObservable(Observable<Response<T>> upstream) {
        this.upstream = upstream;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Result<T>> observer) {
        this.upstream.subscribe(new ResultObserver(observer));
    }

    /* loaded from: classes.dex */
    private static class ResultObserver<R> implements Observer<Response<R>> {
        private final Observer<? super Result<R>> observer;

        @Override // io.reactivex.Observer
        public /* bridge */ /* synthetic */ void onNext(Object obj) {
            onNext((Response) ((Response) obj));
        }

        ResultObserver(Observer<? super Result<R>> observer) {
            this.observer = observer;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable disposable) {
            this.observer.onSubscribe(disposable);
        }

        public void onNext(Response<R> response) {
            this.observer.onNext(Result.response(response));
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable throwable) {
            try {
                this.observer.onNext(Result.error(throwable));
                this.observer.onComplete();
            } catch (Throwable t) {
                try {
                    this.observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            this.observer.onComplete();
        }
    }
}