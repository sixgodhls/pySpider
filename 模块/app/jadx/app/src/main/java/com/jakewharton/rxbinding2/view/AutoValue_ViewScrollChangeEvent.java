package com.jakewharton.rxbinding2.view;

import android.support.annotation.NonNull;
import android.view.View;

/* loaded from: classes.dex */
final class AutoValue_ViewScrollChangeEvent extends ViewScrollChangeEvent {
    private final int oldScrollX;
    private final int oldScrollY;
    private final int scrollX;
    private final int scrollY;
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_ViewScrollChangeEvent(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.oldScrollX = oldScrollX;
        this.oldScrollY = oldScrollY;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewScrollChangeEvent
    @NonNull
    public View view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewScrollChangeEvent
    public int scrollX() {
        return this.scrollX;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewScrollChangeEvent
    public int scrollY() {
        return this.scrollY;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewScrollChangeEvent
    public int oldScrollX() {
        return this.oldScrollX;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewScrollChangeEvent
    public int oldScrollY() {
        return this.oldScrollY;
    }

    public String toString() {
        return "ViewScrollChangeEvent{view=" + this.view + ", scrollX=" + this.scrollX + ", scrollY=" + this.scrollY + ", oldScrollX=" + this.oldScrollX + ", oldScrollY=" + this.oldScrollY + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewScrollChangeEvent)) {
            return false;
        }
        ViewScrollChangeEvent that = (ViewScrollChangeEvent) o;
        return this.view.equals(that.view()) && this.scrollX == that.scrollX() && this.scrollY == that.scrollY() && this.oldScrollX == that.oldScrollX() && this.oldScrollY == that.oldScrollY();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((((((((h ^ this.view.hashCode()) * 1000003) ^ this.scrollX) * 1000003) ^ this.scrollY) * 1000003) ^ this.oldScrollX) * 1000003) ^ this.oldScrollY;
    }
}
