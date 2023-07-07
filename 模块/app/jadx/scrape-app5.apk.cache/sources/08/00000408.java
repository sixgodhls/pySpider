package me.tatarka.bindingcollectionadapter2;

import android.databinding.ViewDataBinding;
import android.util.SparseArray;

/* loaded from: classes.dex */
public final class ItemBinding<T> {
    private static final int LAYOUT_NONE = 0;
    private static final int VAR_INVALID = -1;
    public static final int VAR_NONE = 0;
    private SparseArray<Object> extraBindings;
    private int layoutRes;
    private final OnItemBind<T> onItemBind;
    private int variableId;

    /* renamed from: of */
    public static <T> ItemBinding<T> m8of(int variableId, int layoutRes) {
        return new ItemBinding(null).set(variableId, layoutRes);
    }

    /* renamed from: of */
    public static <T> ItemBinding<T> m7of(OnItemBind<T> onItemBind) {
        if (onItemBind == null) {
            throw new NullPointerException("onItemBind == null");
        }
        return new ItemBinding<>(onItemBind);
    }

    private ItemBinding(OnItemBind<T> onItemBind) {
        this.onItemBind = onItemBind;
    }

    public final ItemBinding<T> set(int variableId, int layoutRes) {
        this.variableId = variableId;
        this.layoutRes = layoutRes;
        return this;
    }

    public final ItemBinding<T> variableId(int variableId) {
        this.variableId = variableId;
        return this;
    }

    public final ItemBinding<T> layoutRes(int layoutRes) {
        this.layoutRes = layoutRes;
        return this;
    }

    public final ItemBinding<T> bindExtra(int variableId, Object value) {
        if (this.extraBindings == null) {
            this.extraBindings = new SparseArray<>(1);
        }
        this.extraBindings.put(variableId, value);
        return this;
    }

    public final ItemBinding<T> clearExtras() {
        SparseArray<Object> sparseArray = this.extraBindings;
        if (sparseArray != null) {
            sparseArray.clear();
        }
        return this;
    }

    public ItemBinding<T> removeExtra(int variableId) {
        SparseArray<Object> sparseArray = this.extraBindings;
        if (sparseArray != null) {
            sparseArray.remove(variableId);
        }
        return this;
    }

    public final int variableId() {
        return this.variableId;
    }

    public final int layoutRes() {
        return this.layoutRes;
    }

    public final Object extraBinding(int variableId) {
        SparseArray<Object> sparseArray = this.extraBindings;
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(variableId);
    }

    public void onItemBind(int position, T item) {
        OnItemBind<T> onItemBind = this.onItemBind;
        if (onItemBind != null) {
            this.variableId = -1;
            this.layoutRes = 0;
            onItemBind.onItemBind(this, position, item);
            if (this.variableId == -1) {
                throw new IllegalStateException("variableId not set in onItemBind()");
            }
            if (this.layoutRes == 0) {
                throw new IllegalStateException("layoutRes not set in onItemBind()");
            }
        }
    }

    public boolean bind(ViewDataBinding binding, T item) {
        int i = this.variableId;
        if (i == 0) {
            return false;
        }
        boolean result = binding.setVariable(i, item);
        if (!result) {
            Utils.throwMissingVariable(binding, this.variableId, this.layoutRes);
        }
        SparseArray<Object> sparseArray = this.extraBindings;
        if (sparseArray != null) {
            int size = sparseArray.size();
            for (int i2 = 0; i2 < size; i2++) {
                int variableId = this.extraBindings.keyAt(i2);
                Object value = this.extraBindings.valueAt(i2);
                if (variableId != 0) {
                    binding.setVariable(variableId, value);
                }
            }
            return true;
        }
        return true;
    }
}