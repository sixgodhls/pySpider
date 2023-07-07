package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.widget.TextView;

/* loaded from: classes.dex */
final class AutoValue_TextViewBeforeTextChangeEvent extends TextViewBeforeTextChangeEvent {
    private final int after;
    private final int count;
    private final int start;
    private final CharSequence text;
    private final TextView view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_TextViewBeforeTextChangeEvent(TextView view, CharSequence text, int start, int count, int after) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        if (text == null) {
            throw new NullPointerException("Null text");
        }
        this.text = text;
        this.start = start;
        this.count = count;
        this.after = after;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewBeforeTextChangeEvent
    @NonNull
    public TextView view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewBeforeTextChangeEvent
    @NonNull
    public CharSequence text() {
        return this.text;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewBeforeTextChangeEvent
    public int start() {
        return this.start;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewBeforeTextChangeEvent
    public int count() {
        return this.count;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewBeforeTextChangeEvent
    public int after() {
        return this.after;
    }

    public String toString() {
        return "TextViewBeforeTextChangeEvent{view=" + this.view + ", text=" + ((Object) this.text) + ", start=" + this.start + ", count=" + this.count + ", after=" + this.after + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextViewBeforeTextChangeEvent)) {
            return false;
        }
        TextViewBeforeTextChangeEvent that = (TextViewBeforeTextChangeEvent) o;
        return this.view.equals(that.view()) && this.text.equals(that.text()) && this.start == that.start() && this.count == that.count() && this.after == that.after();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((((((((h ^ this.view.hashCode()) * 1000003) ^ this.text.hashCode()) * 1000003) ^ this.start) * 1000003) ^ this.count) * 1000003) ^ this.after;
    }
}
