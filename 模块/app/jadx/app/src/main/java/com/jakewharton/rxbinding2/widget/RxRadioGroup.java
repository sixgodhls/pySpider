package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.RadioGroup;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

/* loaded from: classes.dex */
public final class RxRadioGroup {
    @CheckResult
    @NonNull
    public static InitialValueObservable<Integer> checkedChanges(@NonNull RadioGroup view) {
        Preconditions.checkNotNull(view, "view == null");
        return new RadioGroupCheckedChangeObservable(view);
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> checked(@NonNull final RadioGroup view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxRadioGroup.1
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer value) {
                if (value.intValue() == -1) {
                    view.clearCheck();
                } else {
                    view.check(value.intValue());
                }
            }
        };
    }

    private RxRadioGroup() {
        throw new AssertionError("No instances.");
    }
}
