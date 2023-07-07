package me.tatarka.bindingcollectionadapter2.itembindings;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

/* loaded from: classes.dex */
public class OnItemBindClass<T> implements OnItemBind<T> {
    private final List<Class<? extends T>> itemBindingClassList = new ArrayList(2);
    private final List<OnItemBind<? extends T>> itemBindingList = new ArrayList(2);

    public OnItemBindClass<T> map(@NonNull Class<? extends T> itemClass, int variableId, int layoutRes) {
        int index = this.itemBindingClassList.indexOf(itemClass);
        if (index >= 0) {
            this.itemBindingList.set(index, itemBind(variableId, layoutRes));
        } else {
            this.itemBindingClassList.add(itemClass);
            this.itemBindingList.add(itemBind(variableId, layoutRes));
        }
        return this;
    }

    public <E extends T> OnItemBindClass<T> map(@NonNull Class<E> itemClass, OnItemBind<E> onItemBind) {
        int index = this.itemBindingClassList.indexOf(itemClass);
        if (index >= 0) {
            this.itemBindingList.set(index, onItemBind);
        } else {
            this.itemBindingClassList.add(itemClass);
            this.itemBindingList.add(onItemBind);
        }
        return this;
    }

    public int itemTypeCount() {
        return this.itemBindingClassList.size();
    }

    @Override // me.tatarka.bindingcollectionadapter2.OnItemBind
    public void onItemBind(ItemBinding itemBinding, int position, T item) {
        for (int i = 0; i < this.itemBindingClassList.size(); i++) {
            Class<? extends T> key = this.itemBindingClassList.get(i);
            if (key.isInstance(item)) {
                OnItemBind itemBind = this.itemBindingList.get(i);
                itemBind.onItemBind(itemBinding, position, item);
                return;
            }
        }
        throw new IllegalArgumentException("Missing class for item " + item);
    }

    @NonNull
    private OnItemBind<T> itemBind(final int variableId, final int layoutRes) {
        return new OnItemBind<T>() { // from class: me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass.1
            @Override // me.tatarka.bindingcollectionadapter2.OnItemBind
            public void onItemBind(ItemBinding itemBinding, int position, T item) {
                itemBinding.set(variableId, layoutRes);
            }
        };
    }
}
