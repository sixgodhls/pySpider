package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AutoValue_AdapterViewItemLongClickEvent extends AdapterViewItemLongClickEvent {
    private final View clickedView;
    private final long id;
    private final int position;
    private final AdapterView<?> view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_AdapterViewItemLongClickEvent(AdapterView<?> view, View clickedView, int position, long id) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        if (clickedView == null) {
            throw new NullPointerException("Null clickedView");
        }
        this.clickedView = clickedView;
        this.position = position;
        this.id = id;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemLongClickEvent
    @NonNull
    public AdapterView<?> view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemLongClickEvent
    @NonNull
    public View clickedView() {
        return this.clickedView;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemLongClickEvent
    public int position() {
        return this.position;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemLongClickEvent
    public long id() {
        return this.id;
    }

    public String toString() {
        return "AdapterViewItemLongClickEvent{view=" + this.view + ", clickedView=" + this.clickedView + ", position=" + this.position + ", id=" + this.id + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AdapterViewItemLongClickEvent)) {
            return false;
        }
        AdapterViewItemLongClickEvent that = (AdapterViewItemLongClickEvent) o;
        return this.view.equals(that.view()) && this.clickedView.equals(that.clickedView()) && this.position == that.position() && this.id == that.id();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        long j = this.id;
        return ((((((h ^ this.view.hashCode()) * 1000003) ^ this.clickedView.hashCode()) * 1000003) ^ this.position) * 1000003) ^ ((int) (j ^ (j >>> 32)));
    }
}
