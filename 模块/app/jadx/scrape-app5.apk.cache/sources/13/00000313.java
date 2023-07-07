package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AutoValue_AdapterViewItemClickEvent extends AdapterViewItemClickEvent {
    private final View clickedView;

    /* renamed from: id */
    private final long f69id;
    private final int position;
    private final AdapterView<?> view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_AdapterViewItemClickEvent(AdapterView<?> view, View clickedView, int position, long id) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        if (clickedView == null) {
            throw new NullPointerException("Null clickedView");
        }
        this.clickedView = clickedView;
        this.position = position;
        this.f69id = id;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemClickEvent
    @NonNull
    public AdapterView<?> view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemClickEvent
    @NonNull
    public View clickedView() {
        return this.clickedView;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemClickEvent
    public int position() {
        return this.position;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewItemClickEvent
    /* renamed from: id */
    public long mo43id() {
        return this.f69id;
    }

    public String toString() {
        return "AdapterViewItemClickEvent{view=" + this.view + ", clickedView=" + this.clickedView + ", position=" + this.position + ", id=" + this.f69id + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AdapterViewItemClickEvent)) {
            return false;
        }
        AdapterViewItemClickEvent that = (AdapterViewItemClickEvent) o;
        return this.view.equals(that.view()) && this.clickedView.equals(that.clickedView()) && this.position == that.position() && this.f69id == that.mo43id();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        long j = this.f69id;
        return ((((((h ^ this.view.hashCode()) * 1000003) ^ this.clickedView.hashCode()) * 1000003) ^ this.position) * 1000003) ^ ((int) (j ^ (j >>> 32)));
    }
}