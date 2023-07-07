package com.bumptech.glide;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.module.AppGlideModule;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class GeneratedAppGlideModule extends AppGlideModule {
    /* JADX INFO: Access modifiers changed from: package-private */
    @NonNull
    public abstract Set<Class<?>> getExcludedModuleClasses();

    GeneratedAppGlideModule() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public RequestManagerRetriever.RequestManagerFactory getRequestManagerFactory() {
        return null;
    }
}
