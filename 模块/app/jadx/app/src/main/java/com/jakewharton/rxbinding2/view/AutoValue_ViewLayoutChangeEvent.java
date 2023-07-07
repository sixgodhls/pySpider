package com.jakewharton.rxbinding2.view;

import android.support.annotation.NonNull;
import android.view.View;

/* loaded from: classes.dex */
final class AutoValue_ViewLayoutChangeEvent extends ViewLayoutChangeEvent {
    private final int bottom;
    private final int left;
    private final int oldBottom;
    private final int oldLeft;
    private final int oldRight;
    private final int oldTop;
    private final int right;
    private final int top;
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_ViewLayoutChangeEvent(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.oldLeft = oldLeft;
        this.oldTop = oldTop;
        this.oldRight = oldRight;
        this.oldBottom = oldBottom;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    @NonNull
    public View view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    public int left() {
        return this.left;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    public int top() {
        return this.top;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    public int right() {
        return this.right;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    public int bottom() {
        return this.bottom;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    public int oldLeft() {
        return this.oldLeft;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    public int oldTop() {
        return this.oldTop;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    public int oldRight() {
        return this.oldRight;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewLayoutChangeEvent
    public int oldBottom() {
        return this.oldBottom;
    }

    public String toString() {
        return "ViewLayoutChangeEvent{view=" + this.view + ", left=" + this.left + ", top=" + this.top + ", right=" + this.right + ", bottom=" + this.bottom + ", oldLeft=" + this.oldLeft + ", oldTop=" + this.oldTop + ", oldRight=" + this.oldRight + ", oldBottom=" + this.oldBottom + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewLayoutChangeEvent)) {
            return false;
        }
        ViewLayoutChangeEvent that = (ViewLayoutChangeEvent) o;
        return this.view.equals(that.view()) && this.left == that.left() && this.top == that.top() && this.right == that.right() && this.bottom == that.bottom() && this.oldLeft == that.oldLeft() && this.oldTop == that.oldTop() && this.oldRight == that.oldRight() && this.oldBottom == that.oldBottom();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((((((((((((((((h ^ this.view.hashCode()) * 1000003) ^ this.left) * 1000003) ^ this.top) * 1000003) ^ this.right) * 1000003) ^ this.bottom) * 1000003) ^ this.oldLeft) * 1000003) ^ this.oldTop) * 1000003) ^ this.oldRight) * 1000003) ^ this.oldBottom;
    }
}
