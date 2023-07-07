package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

/* loaded from: classes.dex */
public final class RxProgressBar {
    @CheckResult
    @NonNull
    public static Consumer<? super Integer> incrementProgressBy(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxProgressBar.1
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer value) {
                view.incrementProgressBy(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> incrementSecondaryProgressBy(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxProgressBar.2
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer value) {
                view.incrementSecondaryProgressBy(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Boolean> indeterminate(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Boolean>() { // from class: com.jakewharton.rxbinding2.widget.RxProgressBar.3
            @Override // io.reactivex.functions.Consumer
            public void accept(Boolean value) {
                view.setIndeterminate(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> max(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxProgressBar.4
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer value) {
                view.setMax(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> progress(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxProgressBar.5
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer value) {
                view.setProgress(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> secondaryProgress(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxProgressBar.6
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer value) {
                view.setSecondaryProgress(value.intValue());
            }
        };
    }

    private RxProgressBar() {
        throw new AssertionError("No instances.");
    }
}
