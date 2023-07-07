package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.AutoCompleteTextView;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/* loaded from: classes.dex */
public final class RxAutoCompleteTextView {
    @CheckResult
    @NonNull
    public static Observable<AdapterViewItemClickEvent> itemClickEvents(@NonNull AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new AutoCompleteTextViewItemClickEventObservable(view);
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> completionHint(@NonNull final AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() { // from class: com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView.1
            @Override // io.reactivex.functions.Consumer
            public void accept(CharSequence completionHint) {
                view.setCompletionHint(completionHint);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> threshold(@NonNull final AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView.2
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer threshold) {
                view.setThreshold(threshold.intValue());
            }
        };
    }

    private RxAutoCompleteTextView() {
        throw new AssertionError("No instances.");
    }
}