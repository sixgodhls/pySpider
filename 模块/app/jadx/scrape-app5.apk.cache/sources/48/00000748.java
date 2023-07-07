package me.tatarka.bindingcollectionadapter2;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Looper;

/* loaded from: classes.dex */
public class Utils {
    private static final String TAG = "BCAdapters";

    Utils() {
    }

    public static void throwMissingVariable(ViewDataBinding binding, int bindingVariable, int layoutRes) {
        Context context = binding.getRoot().getContext();
        Resources resources = context.getResources();
        String layoutName = resources.getResourceName(layoutRes);
        String bindingVariableName = DataBindingUtil.convertBrIdToString(bindingVariable);
        throw new IllegalStateException("Could not bind variable '" + bindingVariableName + "' in layout '" + layoutName + "'");
    }

    public static void ensureChangeOnMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("You must only modify the ObservableList on the main thread.");
        }
    }

    static <T, A extends BindingCollectionAdapter<T>> A createClass(Class<? extends BindingCollectionAdapter> adapterClass, ItemBinding<T> itemBinding) {
        try {
            return (A) adapterClass.getConstructor(ItemBinding.class).newInstance(itemBinding);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }
}