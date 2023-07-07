package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import java.security.MessageDigest;

/* loaded from: classes.dex */
final class DataCacheKey implements Key {
    private final Key signature;
    private final Key sourceKey;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataCacheKey(Key sourceKey, Key signature) {
        this.sourceKey = sourceKey;
        this.signature = signature;
    }

    Key getSourceKey() {
        return this.sourceKey;
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object o) {
        if (o instanceof DataCacheKey) {
            DataCacheKey other = (DataCacheKey) o;
            return this.sourceKey.equals(other.sourceKey) && this.signature.equals(other.signature);
        }
        return false;
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        int result = this.sourceKey.hashCode();
        return (result * 31) + this.signature.hashCode();
    }

    public String toString() {
        return "DataCacheKey{sourceKey=" + this.sourceKey + ", signature=" + this.signature + '}';
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        this.sourceKey.updateDiskCacheKey(messageDigest);
        this.signature.updateDiskCacheKey(messageDigest);
    }
}