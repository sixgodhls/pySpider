package io.reactivex;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.NotificationLite;

/* loaded from: classes.dex */
public final class Notification<T> {
    static final Notification<Object> COMPLETE = new Notification<>(null);
    final Object value;

    private Notification(Object value) {
        this.value = value;
    }

    public boolean isOnComplete() {
        return this.value == null;
    }

    public boolean isOnError() {
        return NotificationLite.isError(this.value);
    }

    public boolean isOnNext() {
        Object o = this.value;
        return o != null && !NotificationLite.isError(o);
    }

    @Nullable
    public T getValue() {
        Object o = this.value;
        if (o != null && !NotificationLite.isError(o)) {
            return (T) this.value;
        }
        return null;
    }

    @Nullable
    public Throwable getError() {
        Object o = this.value;
        if (NotificationLite.isError(o)) {
            return NotificationLite.getError(o);
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Notification) {
            Notification<?> n = (Notification) obj;
            return ObjectHelper.equals(this.value, n.value);
        }
        return false;
    }

    public int hashCode() {
        Object o = this.value;
        if (o != null) {
            return o.hashCode();
        }
        return 0;
    }

    public String toString() {
        Object o = this.value;
        if (o == null) {
            return "OnCompleteNotification";
        }
        if (NotificationLite.isError(o)) {
            return "OnErrorNotification[" + NotificationLite.getError(o) + "]";
        }
        return "OnNextNotification[" + this.value + "]";
    }

    @NonNull
    public static <T> Notification<T> createOnNext(@NonNull T value) {
        ObjectHelper.requireNonNull(value, "value is null");
        return new Notification<>(value);
    }

    @NonNull
    public static <T> Notification<T> createOnError(@NonNull Throwable error) {
        ObjectHelper.requireNonNull(error, "error is null");
        return new Notification<>(NotificationLite.error(error));
    }

    @NonNull
    public static <T> Notification<T> createOnComplete() {
        return (Notification<T>) COMPLETE;
    }
}