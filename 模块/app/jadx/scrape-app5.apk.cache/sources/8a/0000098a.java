package retrofit2.adapter.rxjava2;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes.dex */
final class CallEnqueueObservable<T> extends Observable<Response<T>> {
    private final Call<T> originalCall;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallEnqueueObservable(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Response<T>> observer) {
        Call<T> call = this.originalCall.mo432clone();
        CallCallback<T> callback = new CallCallback<>(call, observer);
        observer.onSubscribe(callback);
        call.enqueue(callback);
    }

    /* loaded from: classes.dex */
    private static final class CallCallback<T> implements Disposable, Callback<T> {
        private final Call<?> call;
        private volatile boolean disposed;
        private final Observer<? super Response<T>> observer;
        boolean terminated = false;

        CallCallback(Call<?> call, Observer<? super Response<T>> observer) {
            this.call = call;
            this.observer = observer;
        }

        @Override // retrofit2.Callback
        public void onResponse(Call<T> call, Response<T> response) {
            if (this.disposed) {
                return;
            }
            try {
                this.observer.onNext(response);
                if (!this.disposed) {
                    this.terminated = true;
                    this.observer.onComplete();
                }
            } catch (Throwable t) {
                if (this.terminated) {
                    RxJavaPlugins.onError(t);
                } else if (!this.disposed) {
                    try {
                        this.observer.onError(t);
                    } catch (Throwable inner) {
                        Exceptions.throwIfFatal(inner);
                        RxJavaPlugins.onError(new CompositeException(t, inner));
                    }
                }
            }
        }

        @Override // retrofit2.Callback
        public void onFailure(Call<T> call, Throwable t) {
            if (call.isCanceled()) {
                return;
            }
            try {
                this.observer.onError(t);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                RxJavaPlugins.onError(new CompositeException(t, inner));
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.disposed = true;
            this.call.cancel();
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.disposed;
        }
    }
}