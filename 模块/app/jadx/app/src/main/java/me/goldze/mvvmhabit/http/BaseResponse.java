package me.goldze.mvvmhabit.http;

/* loaded from: classes.dex */
public class BaseResponse<T> {
    private int code;
    private String message;
    private T result;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isOk() {
        return this.code == 0;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
