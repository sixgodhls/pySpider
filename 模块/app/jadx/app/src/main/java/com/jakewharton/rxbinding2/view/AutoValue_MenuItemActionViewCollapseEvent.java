package com.jakewharton.rxbinding2.view;

import android.support.annotation.NonNull;
import android.view.MenuItem;

/* loaded from: classes.dex */
final class AutoValue_MenuItemActionViewCollapseEvent extends MenuItemActionViewCollapseEvent {
    private final MenuItem menuItem;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_MenuItemActionViewCollapseEvent(MenuItem menuItem) {
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
        return "MenuItemActionViewCollapseEvent{menuItem=" + this.menuItem + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MenuItemActionViewCollapseEvent) {
            MenuItemActionViewCollapseEvent that = (MenuItemActionViewCollapseEvent) o;
            return this.menuItem.equals(that.menuItem());
        }
        return false;
    }

    public int hashCode() {
        int h = 1 * 1000003;
        return h ^ this.menuItem.hashCode();
    }
}
