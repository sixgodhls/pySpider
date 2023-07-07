package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.SearchView;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

/* loaded from: classes.dex */
public final class RxSearchView {
    @CheckResult
    @NonNull
    public static InitialValueObservable<SearchViewQueryTextEvent> queryTextChangeEvents(@NonNull SearchView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new SearchViewQueryTextChangeEventsObservable(view);
    }

    @CheckResult
    @NonNull
    public static InitialValueObservable<CharSequence> queryTextChanges(@NonNull SearchView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new SearchViewQueryTextChangesObservable(view);
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> query(@NonNull final SearchView view, final boolean submit) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() { // from class: com.jakewharton.rxbinding2.widget.RxSearchView.1
            @Override // io.reactivex.functions.Consumer
            public void accept(CharSequence text) {
                view.setQuery(text, submit);
            }
        };
    }

    private RxSearchView() {
        throw new AssertionError("No instances.");
    }
}
