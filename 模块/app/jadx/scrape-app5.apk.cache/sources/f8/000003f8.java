package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.SeekBar;
import com.google.auto.value.AutoValue;

@AutoValue
/* loaded from: classes.dex */
public abstract class SeekBarProgressChangeEvent extends SeekBarChangeEvent {
    public abstract boolean fromUser();

    public abstract int progress();

    @CheckResult
    @NonNull
    public static SeekBarProgressChangeEvent create(@NonNull SeekBar view, int progress, boolean fromUser) {
        return new AutoValue_SeekBarProgressChangeEvent(view, progress, fromUser);
    }
}