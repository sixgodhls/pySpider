package com.jakewharton.rxbinding2.view;

import android.support.annotation.NonNull;
import android.view.View;

/* loaded from: classes.dex */
final class AutoValue_ViewAttachAttachedEvent extends ViewAttachAttachedEvent {
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_ViewAttachAttachedEvent(View view) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.view.ViewAttachEvent
    @NonNull
    public View view() {
        return this.view;
    }

    public String toString() {
        return "ViewAttachAttachedEvent{view=" + this.view + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ViewAttachAttachedEvent) {
            ViewAttachAttachedEvent that = (ViewAttachAttachedEvent) o;
            return this.view.equals(that.view());
        }
        return false;
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return h ^ this.view.hashCode();
    }
}
