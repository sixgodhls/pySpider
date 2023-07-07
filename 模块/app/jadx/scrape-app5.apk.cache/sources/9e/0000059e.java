package android.support.p003v7.view.menu;

import android.content.Context;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.p000v4.internal.view.SupportMenuItem;
import android.support.p000v4.view.ActionProvider;
import android.support.p003v7.view.menu.MenuItemWrapperICS;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.View;

/* JADX INFO: Access modifiers changed from: package-private */
@RequiresApi(16)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* renamed from: android.support.v7.view.menu.MenuItemWrapperJB */
/* loaded from: classes.dex */
public class MenuItemWrapperJB extends MenuItemWrapperICS {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItemWrapperJB(Context context, SupportMenuItem object) {
        super(context, object);
    }

    @Override // android.support.p003v7.view.menu.MenuItemWrapperICS
    MenuItemWrapperICS.ActionProviderWrapper createActionProviderWrapper(ActionProvider provider) {
        return new ActionProviderWrapperJB(this.mContext, provider);
    }

    /* renamed from: android.support.v7.view.menu.MenuItemWrapperJB$ActionProviderWrapperJB */
    /* loaded from: classes.dex */
    class ActionProviderWrapperJB extends MenuItemWrapperICS.ActionProviderWrapper implements ActionProvider.VisibilityListener {
        ActionProvider.VisibilityListener mListener;

        public ActionProviderWrapperJB(Context context, android.view.ActionProvider inner) {
            super(context, inner);
        }

        @Override // android.support.p000v4.view.ActionProvider
        public View onCreateActionView(MenuItem forItem) {
            return this.mInner.onCreateActionView(forItem);
        }

        @Override // android.support.p000v4.view.ActionProvider
        public boolean overridesItemVisibility() {
            return this.mInner.overridesItemVisibility();
        }

        @Override // android.support.p000v4.view.ActionProvider
        public boolean isVisible() {
            return this.mInner.isVisible();
        }

        @Override // android.support.p000v4.view.ActionProvider
        public void refreshVisibility() {
            this.mInner.refreshVisibility();
        }

        @Override // android.support.p000v4.view.ActionProvider
        public void setVisibilityListener(ActionProvider.VisibilityListener listener) {
            this.mListener = listener;
            this.mInner.setVisibilityListener(listener != null ? this : null);
        }

        @Override // android.view.ActionProvider.VisibilityListener
        public void onActionProviderVisibilityChanged(boolean isVisible) {
            ActionProvider.VisibilityListener visibilityListener = this.mListener;
            if (visibilityListener != null) {
                visibilityListener.onActionProviderVisibilityChanged(isVisible);
            }
        }
    }
}