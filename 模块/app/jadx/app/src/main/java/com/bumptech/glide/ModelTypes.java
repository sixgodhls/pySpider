package com.bumptech.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import java.io.File;
import java.net.URL;

/* loaded from: classes.dex */
interface ModelTypes<T> {
    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo206load(@Nullable Bitmap bitmap);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo207load(@Nullable Drawable drawable);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo208load(@Nullable Uri uri);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo209load(@Nullable File file);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo210load(@RawRes @DrawableRes @Nullable Integer num);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo211load(@Nullable Object obj);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo212load(@Nullable String str);

    @CheckResult
    @Deprecated
    /* renamed from: load */
    T mo213load(@Nullable URL url);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo214load(@Nullable byte[] bArr);
}
