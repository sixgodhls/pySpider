package me.tatarka.bindingcollectionadapter2.itembindings;

import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;
import me.tatarka.bindingcollectionadapter2.itembindings.ItemBindingModel;

/* loaded from: classes.dex */
public class OnItemBindModel<T extends ItemBindingModel> implements OnItemBind<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // me.tatarka.bindingcollectionadapter2.OnItemBind
    public /* bridge */ /* synthetic */ void onItemBind(ItemBinding itemBinding, int i, Object obj) {
        onItemBind(itemBinding, i, (int) ((ItemBindingModel) obj));
    }

    public void onItemBind(ItemBinding itemBinding, int position, T item) {
        item.onItemBind(itemBinding);
    }
}