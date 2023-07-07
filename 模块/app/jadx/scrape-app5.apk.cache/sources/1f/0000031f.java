package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.TextView;
import com.google.auto.value.AutoValue;

@AutoValue
/* loaded from: classes.dex */
public abstract class TextViewEditorActionEvent {
    public abstract int actionId();

    @Nullable
    public abstract KeyEvent keyEvent();

    @NonNull
    public abstract TextView view();

    @CheckResult
    @NonNull
    public static TextViewEditorActionEvent create(@NonNull TextView view, int actionId, @Nullable KeyEvent keyEvent) {
        return new AutoValue_TextViewEditorActionEvent(view, actionId, keyEvent);
    }
}