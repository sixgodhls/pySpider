package com.bumptech.glide.module;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bumptech.glide.GlideBuilder;

/* loaded from: classes.dex */
public abstract class AppGlideModule extends LibraryGlideModule implements AppliesOptions {
    public boolean isManifestParsingEnabled() {
        return true;
    }

    @Override // com.bumptech.glide.module.AppliesOptions
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    }
}
