package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.TextView;

/* loaded from: classes.dex */
final class AutoValue_TextViewEditorActionEvent extends TextViewEditorActionEvent {
    private final int actionId;
    private final KeyEvent keyEvent;
    private final TextView view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_TextViewEditorActionEvent(TextView view, int actionId, @Nullable KeyEvent keyEvent) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        this.actionId = actionId;
        this.keyEvent = keyEvent;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewEditorActionEvent
    @NonNull
    public TextView view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewEditorActionEvent
    public int actionId() {
        return this.actionId;
    }

    @Override // com.jakewharton.rxbinding2.widget.TextViewEditorActionEvent
    @Nullable
    public KeyEvent keyEvent() {
        return this.keyEvent;
    }

    public String toString() {
        return "TextViewEditorActionEvent{view=" + this.view + ", actionId=" + this.actionId + ", keyEvent=" + this.keyEvent + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextViewEditorActionEvent)) {
            return false;
        }
        TextViewEditorActionEvent that = (TextViewEditorActionEvent) o;
        if (this.view.equals(that.view()) && this.actionId == that.actionId()) {
            KeyEvent keyEvent = this.keyEvent;
            if (keyEvent == null) {
                if (that.keyEvent() == null) {
                    return true;
                }
            } else if (keyEvent.equals(that.keyEvent())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h = 1 * 1000003;
        int h2 = (((h ^ this.view.hashCode()) * 1000003) ^ this.actionId) * 1000003;
        KeyEvent keyEvent = this.keyEvent;
        return h2 ^ (keyEvent == null ? 0 : keyEvent.hashCode());
    }
}
