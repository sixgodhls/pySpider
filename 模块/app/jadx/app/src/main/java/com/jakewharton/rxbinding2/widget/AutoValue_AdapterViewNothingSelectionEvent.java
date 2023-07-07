package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.widget.AdapterView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AutoValue_AdapterViewNothingSelectionEvent extends AdapterViewNothingSelectionEvent {
    private final AdapterView<?> view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_AdapterViewNothingSelectionEvent(AdapterView<?> view) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.widget.AdapterViewSelectionEvent
    @NonNull
    public AdapterView<?> view() {
        return this.view;
    }

    public String toString() {
        return "AdapterViewNothingSelectionEvent{view=" + this.view + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AdapterViewNothingSelectionEvent) {
            AdapterViewNothingSelectionEvent that = (AdapterViewNothingSelectionEvent) o;
            return this.view.equals(that.view());
        }
        return false;
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return h ^ this.view.hashCode();
    }
}
