package com.jakewharton.rxbinding2.view;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
final class AutoValue_ViewGroupHierarchyChildViewAddEvent extends ViewGroupHierarchyChildViewAddEvent {
    private final View child;
    private final ViewGroup view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_ViewGroupHierarchyChildViewAddEvent(ViewGroup view, View child) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        if (child == null) {
            throw new NullPointerException("Null child");
        }
        this.child = child;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewGroupHierarchyChangeEvent
    @NonNull
    public ViewGroup view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewGroupHierarchyChangeEvent
    @NonNull
    public View child() {
        return this.child;
    }

    public String toString() {
        return "ViewGroupHierarchyChildViewAddEvent{view=" + this.view + ", child=" + this.child + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewGroupHierarchyChildViewAddEvent)) {
            return false;
        }
        ViewGroupHierarchyChildViewAddEvent that = (ViewGroupHierarchyChildViewAddEvent) o;
        return this.view.equals(that.view()) && this.child.equals(that.child());
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((h ^ this.view.hashCode()) * 1000003) ^ this.child.hashCode();
    }
}