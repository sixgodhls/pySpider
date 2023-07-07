package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.widget.TextView;

/* loaded from: classes.dex */
final class AutoValue_TextViewTextChangeEvent extends TextViewTextChangeEvent {
    private final int before;
    private final int count;
    private final int start;
    private final CharSequence text;
    private final TextView view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_TextViewTextChangeEvent(TextView view, CharSequence text, int start, int before, int count) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        if (text == null) {
            throw new NullPointerException("Null text");
        }
        this.text = text;
        this.start = start;
        this.before = before;
        this.count = count;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
    @NonNull
    public TextView view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
    @NonNull
    public CharSequence text() {
        return this.text;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
    public int start() {
        return this.start;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
    public int before() {
        return this.before;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
    public int count() {
        return this.count;
    }

    public String toString() {
        return "TextViewTextChangeEvent{view=" + this.view + ", text=" + ((Object) this.text) + ", start=" + this.start + ", before=" + this.before + ", count=" + this.count + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextViewTextChangeEvent)) {
            return false;
        }
        TextViewTextChangeEvent that = (TextViewTextChangeEvent) o;
        return this.view.equals(that.view()) && this.text.equals(that.text()) && this.start == that.start() && this.before == that.before() && this.count == that.count();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((((((((h ^ this.view.hashCode()) * 1000003) ^ this.text.hashCode()) * 1000003) ^ this.start) * 1000003) ^ this.before) * 1000003) ^ this.count;
    }
}
