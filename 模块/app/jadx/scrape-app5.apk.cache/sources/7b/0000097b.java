package retrofit2.adapter.rxjava2;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Response;

/* loaded from: classes.dex */
final class BodyObservable<T> extends Observable<T> {
    private final Observable<Response<T>> upstream;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BodyObservable(Observable<Response<T>> upstream) {
        this.upstream = upstream;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super T> observer) {
        this.upstream.subscribe(new BodyObserver(observer));
    }

    /* loaded from: classes.dex */
    private static class BodyObserver<R> implements Observer<Response<R>> {
        private final Observer<? super R> observer;
        private boolean terminated;

        @Override // io.reactivex.Observer
        public /* bridge */ /* synthetic */ void onNext(Object obj) {
            onNext((Response) ((Response) obj));
        }

        BodyObserver(Observer<? super R> observer) {
            this.observer = observer;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable disposable) {
            this.observer.onSubscribe(disposable);
        }

        public void onNext(Response<R> response) {
            if (response.isSuccessful()) {
                this.observer.onNext(response.body());
                return;
            }
            this.terminated = true;
            Throwable t = new HttpException(response);
            try {
                this.observer.onError(t);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                RxJavaPlugins.onError(new CompositeException(t, inner));
            }
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            if (!this.terminated) {
                this.observer.onComplete();
            }
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable throwable) {
            if (!this.terminated) {
                this.observer.onError(throwable);
                return;
            }
            Throwable broken = new AssertionError("This should never happen! Report as a bug with the full stacktrace.");
            broken.initCause(throwable);
            RxJavaPlugins.onError(broken);
        }
    }
}