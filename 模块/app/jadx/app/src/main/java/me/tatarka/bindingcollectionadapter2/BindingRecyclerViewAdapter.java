package me.tatarka.bindingcollectionadapter2;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes.dex */
public class BindingRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BindingCollectionAdapter<T> {
    private static final Object DATA_INVALIDATION = new Object();
    private final WeakReferenceOnListChangedCallback<T> callback = new WeakReferenceOnListChangedCallback<>(this);
    private LayoutInflater inflater;
    private ItemBinding<T> itemBinding;
    private ItemIds<? super T> itemIds;
    private List<T> items;
    @Nullable
    private RecyclerView recyclerView;
    private ViewHolderFactory viewHolderFactory;

    /* loaded from: classes.dex */
    public interface ItemIds<T> {
        long getItemId(int i, T t);
    }

    /* loaded from: classes.dex */
    public interface ViewHolderFactory {
        RecyclerView.ViewHolder createViewHolder(ViewDataBinding viewDataBinding);
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public void setItemBinding(ItemBinding<T> itemBinding) {
        this.itemBinding = itemBinding;
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public ItemBinding<T> getItemBinding() {
        return this.itemBinding;
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public void setItems(@Nullable List<T> items) {
        List<T> list = this.items;
        if (list == items) {
            return;
        }
        if (this.recyclerView != null) {
            if (list instanceof ObservableList) {
                ((ObservableList) list).removeOnListChangedCallback(this.callback);
            }
            if (items instanceof ObservableList) {
                ((ObservableList) items).addOnListChangedCallback(this.callback);
            }
        }
        this.items = items;
        notifyDataSetChanged();
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public T getAdapterItem(int position) {
        return this.items.get(position);
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup viewGroup) {
        return DataBindingUtil.inflate(inflater, layoutId, viewGroup, false);
    }

    @Override // me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
    public void onBindBinding(ViewDataBinding binding, int variableId, @LayoutRes int layoutRes, int position, T item) {
        if (this.itemBinding.bind(binding, item)) {
            binding.executePendingBindings();
        }
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        List<T> list;
        if (this.recyclerView == null && (list = this.items) != null && (list instanceof ObservableList)) {
            ((ObservableList) list).addOnListChangedCallback(this.callback);
        }
        this.recyclerView = recyclerView;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        List<T> list;
        if (this.recyclerView != null && (list = this.items) != null && (list instanceof ObservableList)) {
            ((ObservableList) list).removeOnListChangedCallback(this.callback);
        }
        this.recyclerView = null;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    /* renamed from: onCreateViewHolder */
    public final RecyclerView.ViewHolder mo195onCreateViewHolder(ViewGroup viewGroup, int layoutId) {
        if (this.inflater == null) {
            this.inflater = LayoutInflater.from(viewGroup.getContext());
        }
        ViewDataBinding binding = onCreateBinding(this.inflater, layoutId, viewGroup);
        final RecyclerView.ViewHolder holder = onCreateViewHolder(binding);
        binding.addOnRebindCallback(new OnRebindCallback() { // from class: me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter.1
            @Override // android.databinding.OnRebindCallback
            public boolean onPreBind(ViewDataBinding binding2) {
                return BindingRecyclerViewAdapter.this.recyclerView != null && BindingRecyclerViewAdapter.this.recyclerView.isComputingLayout();
            }

            @Override // android.databinding.OnRebindCallback
            public void onCanceled(ViewDataBinding binding2) {
                int position;
                if (BindingRecyclerViewAdapter.this.recyclerView != null && !BindingRecyclerViewAdapter.this.recyclerView.isComputingLayout() && (position = holder.getAdapterPosition()) != -1) {
                    BindingRecyclerViewAdapter.this.notifyItemChanged(position, BindingRecyclerViewAdapter.DATA_INVALIDATION);
                }
            }
        });
        return holder;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewDataBinding binding) {
        ViewHolderFactory viewHolderFactory = this.viewHolderFactory;
        if (viewHolderFactory != null) {
            return viewHolderFactory.createViewHolder(binding);
        }
        return new BindingViewHolder(binding);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BindingViewHolder extends RecyclerView.ViewHolder {
        public BindingViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
        }
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        T item = this.items.get(position);
        ViewDataBinding binding = DataBindingUtil.getBinding(viewHolder.itemView);
        onBindBinding(binding, this.itemBinding.variableId(), this.itemBinding.layoutRes(), position, item);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (isForDataBinding(payloads)) {
            ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
            binding.executePendingBindings();
            return;
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    private boolean isForDataBinding(List<Object> payloads) {
        if (payloads == null || payloads.size() == 0) {
            return false;
        }
        for (int i = 0; i < payloads.size(); i++) {
            Object obj = payloads.get(i);
            if (obj != DATA_INVALIDATION) {
                return false;
            }
        }
        return true;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        this.itemBinding.onItemBind(position, this.items.get(position));
        return this.itemBinding.layoutRes();
    }

    public void setItemIds(@Nullable ItemIds<? super T> itemIds) {
        if (this.itemIds != itemIds) {
            this.itemIds = itemIds;
            setHasStableIds(itemIds != null);
        }
    }

    public void setViewHolderFactory(@Nullable ViewHolderFactory factory) {
        this.viewHolderFactory = factory;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<T> list = this.items;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public long getItemId(int position) {
        ItemIds<? super T> itemIds = this.itemIds;
        return itemIds == null ? position : itemIds.getItemId(position, (T) this.items.get(position));
    }

    /* loaded from: classes.dex */
    private static class WeakReferenceOnListChangedCallback<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {
        final WeakReference<BindingRecyclerViewAdapter<T>> adapterRef;

        WeakReferenceOnListChangedCallback(BindingRecyclerViewAdapter<T> adapter) {
            this.adapterRef = new WeakReference<>(adapter);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onChanged(ObservableList sender) {
            BindingRecyclerViewAdapter<T> adapter = this.adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyDataSetChanged();
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            BindingRecyclerViewAdapter<T> adapter = this.adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            BindingRecyclerViewAdapter<T> adapter = this.adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            BindingRecyclerViewAdapter<T> adapter = this.adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            for (int i = 0; i < itemCount; i++) {
                adapter.notifyItemMoved(fromPosition + i, toPosition + i);
            }
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            BindingRecyclerViewAdapter<T> adapter = this.adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }
}
