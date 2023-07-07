package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.widget.AbsListView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AutoValue_AbsListViewScrollEvent extends AbsListViewScrollEvent {
    private final int firstVisibleItem;
    private final int scrollState;
    private final int totalItemCount;
    private final AbsListView view;
    private final int visibleItemCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_AbsListViewScrollEvent(AbsListView view, int scrollState, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        this.scrollState = scrollState;
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    @Override // com.jakewharton.rxbinding2.widget.AbsListViewScrollEvent
    @NonNull
    public AbsListView view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.AbsListViewScrollEvent
    public int scrollState() {
        return this.scrollState;
    }

    @Override // com.jakewharton.rxbinding2.widget.AbsListViewScrollEvent
    public int firstVisibleItem() {
        return this.firstVisibleItem;
    }

    @Override // com.jakewharton.rxbinding2.widget.AbsListViewScrollEvent
    public int visibleItemCount() {
        return this.visibleItemCount;
    }

    @Override // com.jakewharton.rxbinding2.widget.AbsListViewScrollEvent
    public int totalItemCount() {
        return this.totalItemCount;
    }

    public String toString() {
        return "AbsListViewScrollEvent{view=" + this.view + ", scrollState=" + this.scrollState + ", firstVisibleItem=" + this.firstVisibleItem + ", visibleItemCount=" + this.visibleItemCount + ", totalItemCount=" + this.totalItemCount + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AbsListViewScrollEvent)) {
            return false;
        }
        AbsListViewScrollEvent that = (AbsListViewScrollEvent) o;
        return this.view.equals(that.view()) && this.scrollState == that.scrollState() && this.firstVisibleItem == that.firstVisibleItem() && this.visibleItemCount == that.visibleItemCount() && this.totalItemCount == that.totalItemCount();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((((((((h ^ this.view.hashCode()) * 1000003) ^ this.scrollState) * 1000003) ^ this.firstVisibleItem) * 1000003) ^ this.visibleItemCount) * 1000003) ^ this.totalItemCount;
    }
}
