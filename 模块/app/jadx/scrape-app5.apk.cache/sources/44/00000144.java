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
    T mo268load(@Nullable Bitmap bitmap);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo269load(@Nullable Drawable drawable);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo270load(@Nullable Uri uri);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo271load(@Nullable File file);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo272load(@RawRes @DrawableRes @Nullable Integer num);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo273load(@Nullable Object obj);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo274load(@Nullable String str);

    @CheckResult
    @Deprecated
    /* renamed from: load */
    T mo275load(@Nullable URL url);

    @CheckResult
    @NonNull
    /* renamed from: load */
    T mo276load(@Nullable byte[] bArr);
}