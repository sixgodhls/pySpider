package com.bumptech.glide.load;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.util.ArrayMap;
import android.support.p000v4.util.SimpleArrayMap;
import com.bumptech.glide.util.CachedHashCodeArrayMap;
import java.security.MessageDigest;

/* loaded from: classes.dex */
public final class Options implements Key {
    private final ArrayMap<Option<?>, Object> values = new CachedHashCodeArrayMap();

    public void putAll(@NonNull Options other) {
        this.values.putAll((SimpleArrayMap<? extends Option<?>, ? extends Object>) other.values);
    }

    @NonNull
    public <T> Options set(@NonNull Option<T> option, @NonNull T value) {
        this.values.put(option, value);
        return this;
    }

    @Nullable
    public <T> T get(@NonNull Option<T> option) {
        return this.values.containsKey(option) ? (T) this.values.get(option) : option.getDefaultValue();
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object o) {
        if (o instanceof Options) {
            Options other = (Options) o;
            return this.values.equals(other.values);
        }
        return false;
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        for (int i = 0; i < this.values.size(); i++) {
            Option<?> key = this.values.keyAt(i);
            Object value = this.values.valueAt(i);
            updateDiskCacheKey(key, value, messageDigest);
        }
    }

    public String toString() {
        return "Options{values=" + this.values + '}';
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <T> void updateDiskCacheKey(@NonNull Option<T> option, @NonNull Object value, @NonNull MessageDigest md) {
        option.update(value, md);
    }
}