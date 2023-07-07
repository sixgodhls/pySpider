package android.support.p003v7.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.p000v4.internal.view.SupportSubMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* JADX INFO: Access modifiers changed from: package-private */
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* renamed from: android.support.v7.view.menu.SubMenuWrapperICS */
/* loaded from: classes.dex */
public class SubMenuWrapperICS extends MenuWrapperICS implements SubMenu {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SubMenuWrapperICS(Context context, SupportSubMenu subMenu) {
        super(context, subMenu);
    }

    @Override // android.support.p003v7.view.menu.BaseWrapper
    /* renamed from: getWrappedObject */
    public SupportSubMenu mo218getWrappedObject() {
        return (SupportSubMenu) this.mWrappedObject;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(int titleRes) {
        mo218getWrappedObject().setHeaderTitle(titleRes);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(CharSequence title) {
        mo218getWrappedObject().setHeaderTitle(title);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(int iconRes) {
        mo218getWrappedObject().setHeaderIcon(iconRes);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(Drawable icon) {
        mo218getWrappedObject().setHeaderIcon(icon);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderView(View view) {
        mo218getWrappedObject().setHeaderView(view);
        return this;
    }

    @Override // android.view.SubMenu
    public void clearHeader() {
        mo218getWrappedObject().clearHeader();
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(int iconRes) {
        mo218getWrappedObject().setIcon(iconRes);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(Drawable icon) {
        mo218getWrappedObject().setIcon(icon);
        return this;
    }

    @Override // android.view.SubMenu
    public MenuItem getItem() {
        return getMenuItemWrapper(mo218getWrappedObject().getItem());
    }
}