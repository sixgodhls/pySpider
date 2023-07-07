package com.jakewharton.rxbinding2.view;

import android.support.annotation.NonNull;
import android.view.MenuItem;

/* loaded from: classes.dex */
final class AutoValue_MenuItemActionViewExpandEvent extends MenuItemActionViewExpandEvent {
    private final MenuItem menuItem;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_MenuItemActionViewExpandEvent(MenuItem menuItem) {
        if (menuItem == null) {
            throw new NullPointerException("Null menuItem");
        }
        this.menuItem = menuItem;
    }

    @Override // com.jakewharton.rxbinding2.view.MenuItemActionViewEvent
    @NonNull
    public MenuItem menuItem() {
        return this.menuItem;
    }

    public String toString() {
        return "MenuItemActionViewExpandEvent{menuItem=" + this.menuItem + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MenuItemActionViewExpandEvent) {
            MenuItemActionViewExpandEvent that = (MenuItemActionViewExpandEvent) o;
            return this.menuItem.equals(that.menuItem());
        }
        return false;
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return h ^ this.menuItem.hashCode();
    }
}
