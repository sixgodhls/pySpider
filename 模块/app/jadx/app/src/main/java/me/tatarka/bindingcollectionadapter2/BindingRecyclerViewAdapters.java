package me.tatarka.bindingcollectionadapter2;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.LayoutManagers;

/* loaded from: classes.dex */
public class BindingRecyclerViewAdapters {
    @BindingAdapter(requireAll = false, value = {"itemBinding", "items", "adapter", "itemIds", "viewHolder"})
    public static <T> void setAdapter(RecyclerView recyclerView, ItemBinding<T> itemBinding, List<T> items, BindingRecyclerViewAdapter<T> adapter, BindingRecyclerViewAdapter.ItemIds<? super T> itemIds, BindingRecyclerViewAdapter.ViewHolderFactory viewHolderFactory) {
        if (itemBinding == null) {
            throw new IllegalArgumentException("itemBinding must not be null");
        }
        BindingRecyclerViewAdapter<T> bindingRecyclerViewAdapter = (BindingRecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            if (bindingRecyclerViewAdapter == null) {
                adapter = new BindingRecyclerViewAdapter<>();
            } else {
                adapter = bindingRecyclerViewAdapter;
            }
        }
        adapter.setItemBinding(itemBinding);
        adapter.setItems(items);
        adapter.setItemIds(itemIds);
        adapter.setViewHolderFactory(viewHolderFactory);
        if (bindingRecyclerViewAdapter != adapter) {
            recyclerView.setAdapter(adapter);
        }
    }

    @BindingAdapter({"layoutManager"})
    public static void setLayoutManager(RecyclerView recyclerView, LayoutManagers.LayoutManagerFactory layoutManagerFactory) {
        recyclerView.setLayoutManager(layoutManagerFactory.create(recyclerView));
    }
}
