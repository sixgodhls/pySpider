package io.reactivex.exceptions;

import io.reactivex.annotations.NonNull;

/* loaded from: classes.dex */
public final class OnErrorNotImplementedException extends RuntimeException {
    private static final long serialVersionUID = -6298857009889503852L;

    public OnErrorNotImplementedException(String message, @NonNull Throwable e) {
        super(message, e != null ? e : new NullPointerException());
    }

    public OnErrorNotImplementedException(@NonNull Throwable e) {
        this("The exception was not handled due to missing onError handler in the subscribe() method call. Further reading: https://github.com/ReactiveX/RxJava/wiki/Error-Handling | " + e, e);
    }
}
