package me.tatarka.bindingcollectionadapter2;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes.dex */
public class BindingListViewAdapter<T> extends BaseAdapter implements BindingCollectionAdapter<T> {
    @NonNull
    private final WeakReferenceOnListChangedCallback<T> callback = new WeakReferenceOnListChangedCallback<>(this);
    private int dropDownItemLayout;
    private LayoutInflater inflater;
    private ItemBinding<T> itemBinding;
    private ItemIds<? super T> itemIds;
    private ItemIsEnabled<? super T> itemIsEnabled;
    private final int itemTypeCount;
    private List<T> items;
    private int[] layouts;

    /* loaded from: classes.dex */
    public interface ItemIds<T> {
        long getItemId(int i, T t);
    }

    /* loaded from: classes.dex */
    public interface ItemIsEnabled<T> {
        boolean isEnabled(int i, T t);
    }

    public BindingListViewAdapter(int itemTypeCount) {
        this.itemTypeCount = itemTypeCount;
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public void setItemBinding(ItemBinding<T> itemBinding) {
        this.itemBinding = itemBinding;
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public ItemBinding<T> getItemBinding() {
        return this.itemBinding;
    }

    public void setDropDownItemLayout(int layoutRes) {
        this.dropDownItemLayout = layoutRes;
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public void setItems(@Nullable List<T> items) {
        List<T> list = this.items;
        if (list == items) {
            return;
        }
        if (list instanceof ObservableList) {
            ((ObservableList) list).removeOnListChangedCallback(this.callback);
        }
        if (items instanceof ObservableList) {
            ((ObservableList) items).addOnListChangedCallback(this.callback);
        }
        this.items = items;
        notifyDataSetChanged();
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public T getAdapterItem(int position) {
        return this.items.get(position);
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, int layoutRes, ViewGroup viewGroup) {
        return DataBindingUtil.inflate(inflater, layoutRes, viewGroup, false);
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public void onBindBinding(ViewDataBinding binding, int variableId, int layoutRes, int position, T item) {
        if (this.itemBinding.bind(binding, item)) {
            binding.executePendingBindings();
        }
    }

    public void setItemIds(@Nullable ItemIds<? super T> itemIds) {
        this.itemIds = itemIds;
    }

    public void setItemIsEnabled(@Nullable ItemIsEnabled<? super T> itemIsEnabled) {
        this.itemIsEnabled = itemIsEnabled;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        List<T> list = this.items;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // android.widget.Adapter
    public T getItem(int position) {
        return this.items.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        ItemIds<? super T> itemIds = this.itemIds;
        return itemIds == null ? position : itemIds.getItemId(position, (T) this.items.get(position));
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int position) {
        ItemIsEnabled<? super T> itemIsEnabled = this.itemIsEnabled;
        return itemIsEnabled == null || itemIsEnabled.isEnabled(position, (T) this.items.get(position));
    }

    @Override // android.widget.Adapter
    public final View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewDataBinding binding;
        if (this.inflater == null) {
            this.inflater = LayoutInflater.from(parent.getContext());
        }
        int viewType = getItemViewType(position);
        int layoutRes = this.layouts[viewType];
        if (convertView == null) {
            binding = onCreateBinding(this.inflater, layoutRes, parent);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        T item = this.items.get(position);
        onBindBinding(binding, this.itemBinding.variableId(), layoutRes, position, item);
        return binding.getRoot();
    }

    @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public final View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding binding;
        if (this.inflater == null) {
            this.inflater = LayoutInflater.from(parent.getContext());
        }
        if (this.dropDownItemLayout == 0) {
            return super.getDropDownView(position, convertView, parent);
        }
        int layoutRes = this.dropDownItemLayout;
        if (convertView == null) {
            binding = onCreateBinding(this.inflater, layoutRes, parent);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        T item = this.items.get(position);
        onBindBinding(binding, this.itemBinding.variableId(), layoutRes, position, item);
        return binding.getRoot();
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        ensureLayoutsInit();
        T item = this.items.get(position);
        this.itemBinding.onItemBind(position, item);
        int firstEmpty = 0;
        int i = 0;
        while (true) {
            int[] iArr = this.layouts;
            if (i < iArr.length) {
                int layoutRes = this.itemBinding.layoutRes();
                int[] iArr2 = this.layouts;
                if (layoutRes == iArr2[i]) {
                    return i;
                }
                if (iArr2[i] == 0) {
                    firstEmpty = i;
                }
                i++;
            } else {
                iArr[firstEmpty] = this.itemBinding.layoutRes();
                return firstEmpty;
            }
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return this.itemIds != null;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return ensureLayoutsInit();
    }

    private int ensureLayoutsInit() {
        int count = this.itemTypeCount;
        if (this.layouts == null) {
            this.layouts = new int[count];
        }
        return count;
    }

    /* loaded from: classes.dex */
    private static class WeakReferenceOnListChangedCallback<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {
        final WeakReference<BindingListViewAdapter<T>> adapterRef;

        WeakReferenceOnListChangedCallback(BindingListViewAdapter<T> adapter) {
            this.adapterRef = new WeakReference<>(adapter);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onChanged(ObservableList sender) {
            BindingListViewAdapter<T> adapter = this.adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyDataSetChanged();
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            onChanged(sender);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }
    }
}