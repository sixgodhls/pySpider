package retrofit2.adapter.rxjava2;

import javax.annotation.Nullable;
import retrofit2.Response;

/* loaded from: classes.dex */
public final class Result<T> {
    @Nullable
    private final Throwable error;
    @Nullable
    private final Response<T> response;

    public static <T> Result<T> error(Throwable error) {
        if (error == null) {
            throw new NullPointerException("error == null");
        }
        return new Result<>(null, error);
    }

    public static <T> Result<T> response(Response<T> response) {
        if (response == null) {
            throw new NullPointerException("response == null");
        }
        return new Result<>(response, null);
    }

    private Result(@Nullable Response<T> response, @Nullable Throwable error) {
        this.response = response;
        this.error = error;
    }

    @Nullable
    public Response<T> response() {
        return this.response;
    }

    @Nullable
    public Throwable error() {
        return this.error;
    }

    public boolean isError() {
        return this.error != null;
    }
}
