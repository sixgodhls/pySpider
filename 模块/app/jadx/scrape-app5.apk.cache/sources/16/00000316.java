package com.jakewharton.rxbinding2.widget;

import android.support.annotation.NonNull;
import android.widget.RatingBar;

/* loaded from: classes.dex */
final class AutoValue_RatingBarChangeEvent extends RatingBarChangeEvent {
    private final boolean fromUser;
    private final float rating;
    private final RatingBar view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_RatingBarChangeEvent(RatingBar view, float rating, boolean fromUser) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        this.rating = rating;
        this.fromUser = fromUser;
    }

    @Override // com.jakewharton.rxbinding2.widget.RatingBarChangeEvent
    @NonNull
    public RatingBar view() {
        return this.view;
    }

    @Override // com.jakewharton.rxbinding2.widget.RatingBarChangeEvent
    public float rating() {
        return this.rating;
    }

    @Override // com.jakewharton.rxbinding2.widget.RatingBarChangeEvent
    public boolean fromUser() {
        return this.fromUser;
    }

    public String toString() {
        return "RatingBarChangeEvent{view=" + this.view + ", rating=" + this.rating + ", fromUser=" + this.fromUser + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RatingBarChangeEvent)) {
            return false;
        }
        RatingBarChangeEvent that = (RatingBarChangeEvent) o;
        return this.view.equals(that.view()) && Float.floatToIntBits(this.rating) == Float.floatToIntBits(that.rating()) && this.fromUser == that.fromUser();
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return ((((h ^ this.view.hashCode()) * 1000003) ^ Float.floatToIntBits(this.rating)) * 1000003) ^ (this.fromUser ? 1231 : 1237);
    }
}