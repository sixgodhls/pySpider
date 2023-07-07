package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v4.internal.view.SupportSubMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* JADX INFO: Access modifiers changed from: package-private */
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class SubMenuWrapperICS extends MenuWrapperICS implements SubMenu {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SubMenuWrapperICS(Context context, SupportSubMenu subMenu) {
        super(context, subMenu);
    }

    @Override // android.support.v7.view.menu.BaseWrapper
    /* renamed from: getWrappedObject */
    public SupportSubMenu mo156getWrappedObject() {
        return (SupportSubMenu) this.mWrappedObject;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(int titleRes) {
        mo156getWrappedObject().setHeaderTitle(titleRes);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(CharSequence title) {
        mo156getWrappedObject().setHeaderTitle(title);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(int iconRes) {
        mo156getWrappedObject().setHeaderIcon(iconRes);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(Drawable icon) {
        mo156getWrappedObject().setHeaderIcon(icon);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderView(View view) {
        mo156getWrappedObject().setHeaderView(view);
        return this;
    }

    @Override // android.view.SubMenu
    public void clearHeader() {
        mo156getWrappedObject().clearHeader();
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(int iconRes) {
        mo156getWrappedObject().setIcon(iconRes);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(Drawable icon) {
        mo156getWrappedObject().setIcon(icon);
        return this;
    }

    @Override // android.view.SubMenu
    public MenuItem getItem() {
        return getMenuItemWrapper(mo156getWrappedObject().getItem());
    }
}
