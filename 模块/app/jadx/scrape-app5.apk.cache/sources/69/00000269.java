package android.support.p000v4.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* renamed from: android.support.v4.util.Pair */
/* loaded from: classes.dex */
public class Pair<F, S> {
    @Nullable
    public final F first;
    @Nullable
    public final S second;

    public Pair(@Nullable F first, @Nullable S second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair) o;
        return ObjectsCompat.equals(p.first, this.first) && ObjectsCompat.equals(p.second, this.second);
    }

    public int hashCode() {
        F f = this.first;
        int i = 0;
        int hashCode = f == null ? 0 : f.hashCode();
        S s = this.second;
        if (s != null) {
            i = s.hashCode();
        }
        return hashCode ^ i;
    }

    public String toString() {
        return "Pair{" + String.valueOf(this.first) + " " + String.valueOf(this.second) + "}";
    }

    @NonNull
    public static <A, B> Pair<A, B> create(@Nullable A a, @Nullable B b) {
        return new Pair<>(a, b);
    }
}