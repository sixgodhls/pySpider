package me.tatarka.bindingcollectionadapter2;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes.dex */
public class BindingViewPagerAdapter<T> extends PagerAdapter implements BindingCollectionAdapter<T> {
    private final WeakReferenceOnListChangedCallback<T> callback = new WeakReferenceOnListChangedCallback<>(this);
    private LayoutInflater inflater;
    private ItemBinding<T> itemBinding;
    private List<T> items;
    private PageTitles<T> pageTitles;

    /* loaded from: classes.dex */
    public interface PageTitles<T> {
        CharSequence getPageTitle(int i, T t);
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

    public void setPageTitles(@Nullable PageTitles<T> pageTitles) {
        this.pageTitles = pageTitles;
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        List<T> list = this.items;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // android.support.v4.view.PagerAdapter
    public CharSequence getPageTitle(int position) {
        PageTitles<T> pageTitles = this.pageTitles;
        if (pageTitles == null) {
            return null;
        }
        return pageTitles.getPageTitle(position, this.items.get(position));
    }

    @Override // android.support.v4.view.PagerAdapter
    public Object instantiateItem(ViewGroup container, int position) {
        if (this.inflater == null) {
            this.inflater = LayoutInflater.from(container.getContext());
        }
        T item = this.items.get(position);
        this.itemBinding.onItemBind(position, item);
        ViewDataBinding binding = onCreateBinding(this.inflater, this.itemBinding.layoutRes(), container);
        onBindBinding(binding, this.itemBinding.variableId(), this.itemBinding.layoutRes(), position, item);
        container.addView(binding.getRoot());
        binding.getRoot().setTag(item);
        return binding.getRoot();
    }

    @Override // android.support.v4.view.PagerAdapter
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override // android.support.v4.view.PagerAdapter
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getItemPosition(Object object) {
        Object tag = ((View) object).getTag();
        if (this.items != null) {
            for (int i = 0; i < this.items.size(); i++) {
                if (tag == this.items.get(i)) {
                    return i;
                }
            }
            return -2;
        }
        return -2;
    }

    /* loaded from: classes.dex */
    private static class WeakReferenceOnListChangedCallback<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {
        final WeakReference<BindingViewPagerAdapter<T>> adapterRef;

        WeakReferenceOnListChangedCallback(BindingViewPagerAdapter<T> adapter) {
            this.adapterRef = new WeakReference<>(adapter);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onChanged(ObservableList sender) {
            BindingViewPagerAdapter<T> adapter = this.adapterRef.get();
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
