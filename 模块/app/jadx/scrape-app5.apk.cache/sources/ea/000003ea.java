package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AutoValue_AdapterViewItemSelectionEvent extends AdapterViewItemSelectionEvent {

    /* renamed from: id */
    private final long f71id;
    private final int position;
    private final View selectedView;
    private final AdapterView<?> view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_AdapterViewItemSelectionEvent(AdapterView<?> view, View selectedView, int position, long id) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        if (selectedView == null) {
            throw new NullPointerException("Null selectedView");
        }
        this.selectedView = selectedView;
        this.position = position;
        this.f71id = id;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewSelectionEvent
    @NonNull
    public AdapterView<?> view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemSelectionEvent
    @NonNull
    public View selectedView() {
        return this.selectedView;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemSelectionEvent
    public int position() {
        return this.position;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemSelectionEvent
    /* renamed from: id */
    public long mo41id() {
        return this.f71id;
    }

    public String toString() {
        return "AdapterViewItemSelectionEvent{view=" + this.view + ", selectedView=" + this.selectedView + ", position=" + this.position + ", id=" + this.f71id + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AdapterViewItemSelectionEvent)) {
            return false;
        }
        AdapterViewItemSelectionEvent that = (AdapterViewItemSelectionEvent) o;
        return this.view.equals(that.view()) && this.selectedView.equals(that.selectedView()) && this.position == that.position() && this.f71id == that.mo41id();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        long j = this.f71id;
        return ((((((h ^ this.view.hashCode()) * 1000003) ^ this.selectedView.hashCode()) * 1000003) ^ this.position) * 1000003) ^ ((int) (j ^ (j >>> 32)));
    }
}