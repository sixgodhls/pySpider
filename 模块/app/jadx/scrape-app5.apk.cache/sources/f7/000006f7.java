package me.tatarka.bindingcollectionadapter2;

import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.List;

/* loaded from: classes.dex */
public interface BindingCollectionAdapter<T> {
    T getAdapterItem(int i);

    ItemBinding<T> getItemBinding();

    void onBindBinding(ViewDataBinding viewDataBinding, int i, int i2, int i3, T t);

    ViewDataBinding onCreateBinding(LayoutInflater layoutInflater, int i, ViewGroup viewGroup);

    void setItemBinding(ItemBinding<T> itemBinding);

    void setItems(@Nullable List<T> list);
}