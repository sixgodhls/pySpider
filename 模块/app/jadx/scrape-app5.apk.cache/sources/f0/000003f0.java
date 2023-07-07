package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.widget.SeekBar;

/* loaded from: classes.dex */
final class AutoValue_SeekBarStartChangeEvent extends SeekBarStartChangeEvent {
    private final SeekBar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_SeekBarStartChangeEvent(SeekBar view) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.widget.SeekBarChangeEvent
    @NonNull
    public SeekBar view() {
        return this.view;
    }

    public String toString() {
        return "SeekBarStartChangeEvent{view=" + this.view + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SeekBarStartChangeEvent) {
            SeekBarStartChangeEvent that = (SeekBarStartChangeEvent) o;
            return this.view.equals(that.view());
        }
        return false;
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return h ^ this.view.hashCode();
    }
}