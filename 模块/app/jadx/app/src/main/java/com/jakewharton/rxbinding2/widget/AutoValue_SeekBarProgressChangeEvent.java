package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.widget.SeekBar;

/* loaded from: classes.dex */
final class AutoValue_SeekBarProgressChangeEvent extends SeekBarProgressChangeEvent {
    private final boolean fromUser;
    private final int progress;
    private final SeekBar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_SeekBarProgressChangeEvent(SeekBar view, int progress, boolean fromUser) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        this.progress = progress;
        this.fromUser = fromUser;
    }

    @Override // com.jakewharton.rxbinding2.widget.SeekBarChangeEvent
    @NonNull
    public SeekBar view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.SeekBarProgressChangeEvent
    public int progress() {
        return this.progress;
    }

    @Override // com.jakewharton.rxbinding2.widget.SeekBarProgressChangeEvent
    public boolean fromUser() {
        return this.fromUser;
    }

    public String toString() {
        return "SeekBarProgressChangeEvent{view=" + this.view + ", progress=" + this.progress + ", fromUser=" + this.fromUser + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SeekBarProgressChangeEvent)) {
            return false;
        }
        SeekBarProgressChangeEvent that = (SeekBarProgressChangeEvent) o;
        return this.view.equals(that.view()) && this.progress == that.progress() && this.fromUser == that.fromUser();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((((h ^ this.view.hashCode()) * 1000003) ^ this.progress) * 1000003) ^ (this.fromUser ? 1231 : 1237);
    }
}
