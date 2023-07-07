package com.bumptech.glide.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* loaded from: classes.dex */
public class MultiClassKey {
    private Class<?> first;
    private Class<?> second;
    private Class<?> third;

    public MultiClassKey() {
    }

    public MultiClassKey(@NonNull Class<?> first, @NonNull Class<?> second) {
        set(first, second);
    }

    public MultiClassKey(@NonNull Class<?> first, @NonNull Class<?> second, @Nullable Class<?> third) {
        set(first, second, third);
    }

    public void set(@NonNull Class<?> first, @NonNull Class<?> second) {
        set(first, second, null);
    }

    public void set(@NonNull Class<?> first, @NonNull Class<?> second, @Nullable Class<?> third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public String toString() {
        return "MultiClassKey{first=" + this.first + ", second=" + this.second + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultiClassKey that = (MultiClassKey) o;
        if (this.first.equals(that.first) && this.second.equals(that.second) && Util.bothNullOrEqual(this.third, that.third)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.first.hashCode();
        int result2 = ((result * 31) + this.second.hashCode()) * 31;
        Class<?> cls = this.third;
        return result2 + (cls != null ? cls.hashCode() : 0);
    }
}