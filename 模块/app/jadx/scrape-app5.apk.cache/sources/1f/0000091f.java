package com.jakewharton.rxbinding2.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.MenuItem;
import android.widget.Toolbar;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

@RequiresApi(21)
/* loaded from: classes.dex */
public final class RxToolbar {
    @CheckResult
    @NonNull
    public static Observable<MenuItem> itemClicks(@NonNull Toolbar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new ToolbarItemClickObservable(view);
    }

    @CheckResult
    @NonNull
    public static Observable<Object> navigationClicks(@NonNull Toolbar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new ToolbarNavigationClickObservable(view);
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> title(@NonNull final Toolbar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() { // from class: com.jakewharton.rxbinding2.widget.RxToolbar.1
            @Override // io.reactivex.functions.Consumer
            public void accept(CharSequence title) {
                view.setTitle(title);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> titleRes(@NonNull final Toolbar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxToolbar.2
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer titleRes) {
                view.setTitle(titleRes.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> subtitle(@NonNull final Toolbar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() { // from class: com.jakewharton.rxbinding2.widget.RxToolbar.3
            @Override // io.reactivex.functions.Consumer
            public void accept(CharSequence subtitle) {
                view.setSubtitle(subtitle);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> subtitleRes(@NonNull final Toolbar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() { // from class: com.jakewharton.rxbinding2.widget.RxToolbar.4
            @Override // io.reactivex.functions.Consumer
            public void accept(Integer subtitleRes) {
                view.setSubtitle(subtitleRes.intValue());
            }
        };
    }

    private RxToolbar() {
        throw new AssertionError("No instances.");
    }
}