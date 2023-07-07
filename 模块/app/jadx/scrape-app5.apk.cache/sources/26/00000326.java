package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import com.google.auto.value.AutoValue;

@AutoValue
/* loaded from: classes.dex */
public abstract class AdapterViewItemClickEvent {
    @NonNull
    public abstract View clickedView();

    /* renamed from: id */
    public abstract long mo43id();

    public abstract int position();

    @NonNull
    public abstract AdapterView<?> view();

    @CheckResult
    @NonNull
    public static AdapterViewItemClickEvent create(@NonNull AdapterView<?> view, @NonNull View clickedView, int position, long id) {
        return new AutoValue_AdapterViewItemClickEvent(view, clickedView, position, id);
    }
}