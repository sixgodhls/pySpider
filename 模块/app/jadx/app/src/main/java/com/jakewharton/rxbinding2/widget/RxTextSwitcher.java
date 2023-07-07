package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.TextSwitcher;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

/* loaded from: classes.dex */
public final class RxTextSwitcher {
    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> text(@NonNull final TextSwitcher view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() { // from class: com.jakewharton.rxbinding2.widget.RxTextSwitcher.1
            @Override // io.reactivex.functions.Consumer
            public void accept(CharSequence text) {
                view.setText(text);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> currentText(@NonNull final TextSwitcher view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() { // from class: com.jakewharton.rxbinding2.widget.RxTextSwitcher.2
            @Override // io.reactivex.functions.Consumer
            public void accept(CharSequence textRes) {
                view.setCurrentText(textRes);
            }
        };
    }

    private RxTextSwitcher() {
        throw new AssertionError("No instances.");
    }
}
