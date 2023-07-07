package me.tatarka.bindingcollectionadapter2;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.support.v4.view.ViewPager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.WrapperListAdapter;
import java.util.List;
import me.tatarka.bindingcollectionadapter2.BindingListViewAdapter;
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;

/* loaded from: classes.dex */
public class BindingCollectionAdapters {
    @BindingAdapter(requireAll = false, value = {"itemBinding", "itemTypeCount", "items", "adapter", "itemDropDownLayout", "itemIds", "itemIsEnabled"})
    public static <T> void setAdapter(AdapterView adapterView, ItemBinding<T> itemBinding, Integer itemTypeCount, List items, BindingListViewAdapter<T> adapter, int itemDropDownLayout, BindingListViewAdapter.ItemIds<? super T> itemIds, BindingListViewAdapter.ItemIsEnabled<? super T> itemIsEnabled) {
        if (itemBinding == null) {
            throw new IllegalArgumentException("onItemBind must not be null");
        }
        BindingListViewAdapter<T> oldAdapter = (BindingListViewAdapter) unwrapAdapter(adapterView.getAdapter());
        if (adapter == null) {
            if (oldAdapter == null) {
                int count = itemTypeCount != null ? itemTypeCount.intValue() : 1;
                adapter = new BindingListViewAdapter<>(count);
            } else {
                adapter = oldAdapter;
            }
        }
        adapter.setItemBinding(itemBinding);
        adapter.setDropDownItemLayout(itemDropDownLayout);
        adapter.setItems(items);
        adapter.setItemIds(itemIds);
        adapter.setItemIsEnabled(itemIsEnabled);
        if (oldAdapter != adapter) {
            adapterView.setAdapter(adapter);
        }
    }

    private static Adapter unwrapAdapter(Adapter adapter) {
        return adapter instanceof WrapperListAdapter ? unwrapAdapter(((WrapperListAdapter) adapter).getWrappedAdapter()) : adapter;
    }

    @BindingAdapter(requireAll = false, value = {"itemBinding", "items", "adapter", "pageTitles"})
    public static <T> void setAdapter(ViewPager viewPager, ItemBinding<T> itemBinding, List items, BindingViewPagerAdapter<T> adapter, BindingViewPagerAdapter.PageTitles<T> pageTitles) {
        if (itemBinding == null) {
            throw new IllegalArgumentException("onItemBind must not be null");
        }
        BindingViewPagerAdapter<T> oldAdapter = (BindingViewPagerAdapter) viewPager.getAdapter();
        if (adapter == null) {
            if (oldAdapter == null) {
                adapter = new BindingViewPagerAdapter<>();
            } else {
                adapter = oldAdapter;
            }
        }
        adapter.setItemBinding(itemBinding);
        adapter.setItems(items);
        adapter.setPageTitles(pageTitles);
        if (oldAdapter != adapter) {
            viewPager.setAdapter(adapter);
        }
    }

    @BindingConversion
    public static <T> ItemBinding<T> toItemBinding(OnItemBind<T> onItemBind) {
        return ItemBinding.of(onItemBind);
    }
}
