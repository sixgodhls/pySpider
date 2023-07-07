package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.annotation.RestrictTo;
import android.widget.TabHost;

@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class TabHostBindingAdapter {
    @InverseBindingAdapter(attribute = "android:currentTab")
    public static int getCurrentTab(TabHost view) {
        return view.getCurrentTab();
    }

    @InverseBindingAdapter(attribute = "android:currentTab")
    public static String getCurrentTabTag(TabHost view) {
        return view.getCurrentTabTag();
    }

    @BindingAdapter({"android:currentTab"})
    public static void setCurrentTab(TabHost view, int tab) {
        if (view.getCurrentTab() != tab) {
            view.setCurrentTab(tab);
        }
    }

    @BindingAdapter({"android:currentTab"})
    public static void setCurrentTabTag(TabHost view, String tabTag) {
        if (view.getCurrentTabTag() != tabTag) {
            view.setCurrentTabByTag(tabTag);
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onTabChanged", "android:currentTabAttrChanged"})
    public static void setListeners(TabHost view, final TabHost.OnTabChangeListener listener, final InverseBindingListener attrChange) {
        if (attrChange == null) {
            view.setOnTabChangedListener(listener);
        } else {
            view.setOnTabChangedListener(new TabHost.OnTabChangeListener() { // from class: android.databinding.adapters.TabHostBindingAdapter.1
                @Override // android.widget.TabHost.OnTabChangeListener
                public void onTabChanged(String tabId) {
                    TabHost.OnTabChangeListener onTabChangeListener = listener;
                    if (onTabChangeListener != null) {
                        onTabChangeListener.onTabChanged(tabId);
                    }
                    attrChange.onChange();
                }
            });
        }
    }
}
