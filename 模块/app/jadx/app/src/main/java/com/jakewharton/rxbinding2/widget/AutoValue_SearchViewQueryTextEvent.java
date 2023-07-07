package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.widget.SearchView;

/* loaded from: classes.dex */
final class AutoValue_SearchViewQueryTextEvent extends SearchViewQueryTextEvent {
    private final boolean isSubmitted;
    private final CharSequence queryText;
    private final SearchView view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_SearchViewQueryTextEvent(SearchView view, CharSequence queryText, boolean isSubmitted) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        if (queryText == null) {
            throw new NullPointerException("Null queryText");
        }
        this.queryText = queryText;
        this.isSubmitted = isSubmitted;
    }

    @Override // com.jakewharton.rxbinding2.widget.SearchViewQueryTextEvent
    @NonNull
    public SearchView view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.SearchViewQueryTextEvent
    @NonNull
    public CharSequence queryText() {
        return this.queryText;
    }

    @Override // com.jakewharton.rxbinding2.widget.SearchViewQueryTextEvent
    public boolean isSubmitted() {
        return this.isSubmitted;
    }

    public String toString() {
        return "SearchViewQueryTextEvent{view=" + this.view + ", queryText=" + ((Object) this.queryText) + ", isSubmitted=" + this.isSubmitted + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SearchViewQueryTextEvent)) {
            return false;
        }
        SearchViewQueryTextEvent that = (SearchViewQueryTextEvent) o;
        return this.view.equals(that.view()) && this.queryText.equals(that.queryText()) && this.isSubmitted == that.isSubmitted();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((((h ^ this.view.hashCode()) * 1000003) ^ this.queryText.hashCode()) * 1000003) ^ (this.isSubmitted ? 1231 : 1237);
    }
}
