package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.CheckedTextView;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

/* loaded from: classes.dex */
public final class RxCheckedTextView {
    @CheckResult
    @NonNull
    public static Consumer<? super Boolean> check(@NonNull final CheckedTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Boolean>() { // from class: com.jakewharton.rxbinding2.widget.RxCheckedTextView.1
            @Override // io.reactivex.functions.Consumer
            public void accept(Boolean check) {
                view.setChecked(check.booleanValue());
            }
        };
    }

    private RxCheckedTextView() {
        throw new AssertionError("No instances.");
    }
}
