package com.goldze.mvvmhabit;

import android.databinding.DataBinderMapper;
import android.databinding.DataBindingComponent;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import com.goldze.mvvmhabit.databinding.ActivityMainBindingImpl;
import com.goldze.mvvmhabit.databinding.FragmentDetailBindingImpl;
import com.goldze.mvvmhabit.databinding.FragmentIndexBindingImpl;
import com.goldze.mvvmhabit.databinding.ItemBindingImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class DataBinderMapperImpl extends DataBinderMapper {
    private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(4);
    private static final int LAYOUT_ACTIVITYMAIN = 1;
    private static final int LAYOUT_FRAGMENTDETAIL = 2;
    private static final int LAYOUT_FRAGMENTINDEX = 3;
    private static final int LAYOUT_ITEM = 4;

    static {
        INTERNAL_LAYOUT_ID_LOOKUP.put(C0690R.layout.activity_main, 1);
        INTERNAL_LAYOUT_ID_LOOKUP.put(C0690R.layout.fragment_detail, 2);
        INTERNAL_LAYOUT_ID_LOOKUP.put(C0690R.layout.fragment_index, 3);
        INTERNAL_LAYOUT_ID_LOOKUP.put(C0690R.layout.item, 4);
    }

    @Override // android.databinding.DataBinderMapper
    public ViewDataBinding getDataBinder(DataBindingComponent dataBindingComponent, View view, int i) {
        int i2 = INTERNAL_LAYOUT_ID_LOOKUP.get(i);
        if (i2 > 0) {
            Object tag = view.getTag();
            if (tag == null) {
                throw new RuntimeException("view must have a tag");
            }
            switch (i2) {
                case 1:
                    if ("layout/activity_main_0".equals(tag)) {
                        return new ActivityMainBindingImpl(dataBindingComponent, view);
                    }
                    throw new IllegalArgumentException("The tag for activity_main is invalid. Received: " + tag);
                case 2:
                    if ("layout/fragment_detail_0".equals(tag)) {
                        return new FragmentDetailBindingImpl(dataBindingComponent, view);
                    }
                    throw new IllegalArgumentException("The tag for fragment_detail is invalid. Received: " + tag);
                case 3:
                    if ("layout/fragment_index_0".equals(tag)) {
                        return new FragmentIndexBindingImpl(dataBindingComponent, view);
                    }
                    throw new IllegalArgumentException("The tag for fragment_index is invalid. Received: " + tag);
                case 4:
                    if ("layout/item_0".equals(tag)) {
                        return new ItemBindingImpl(dataBindingComponent, view);
                    }
                    throw new IllegalArgumentException("The tag for item is invalid. Received: " + tag);
                default:
                    return null;
            }
        }
        return null;
    }

    @Override // android.databinding.DataBinderMapper
    public ViewDataBinding getDataBinder(DataBindingComponent dataBindingComponent, View[] viewArr, int i) {
        if (viewArr == null || viewArr.length == 0 || INTERNAL_LAYOUT_ID_LOOKUP.get(i) <= 0 || viewArr[0].getTag() != null) {
            return null;
        }
        throw new RuntimeException("view must have a tag");
    }

    @Override // android.databinding.DataBinderMapper
    public int getLayoutId(String str) {
        Integer num;
        if (str == null || (num = InnerLayoutIdLookup.sKeys.get(str)) == null) {
            return 0;
        }
        return num.intValue();
    }

    @Override // android.databinding.DataBinderMapper
    public String convertBrIdToString(int i) {
        return InnerBrLookup.sKeys.get(i);
    }

    @Override // android.databinding.DataBinderMapper
    public List<DataBinderMapper> collectDependencies() {
        ArrayList arrayList = new ArrayList(2);
        arrayList.add(new com.android.databinding.library.baseAdapters.DataBinderMapperImpl());
        arrayList.add(new me.goldze.mvvmhabit.DataBinderMapperImpl());
        return arrayList;
    }

    /* loaded from: classes.dex */
    private static class InnerBrLookup {
        static final SparseArray<String> sKeys = new SparseArray<>(4);

        private InnerBrLookup() {
        }

        static {
            sKeys.put(0, "_all");
            sKeys.put(1, "adapter");
            sKeys.put(2, "viewModel");
        }
    }

    /* loaded from: classes.dex */
    private static class InnerLayoutIdLookup {
        static final HashMap<String, Integer> sKeys = new HashMap<>(4);

        private InnerLayoutIdLookup() {
        }

        static {
            sKeys.put("layout/activity_main_0", Integer.valueOf((int) C0690R.layout.activity_main));
            sKeys.put("layout/fragment_detail_0", Integer.valueOf((int) C0690R.layout.fragment_detail));
            sKeys.put("layout/fragment_index_0", Integer.valueOf((int) C0690R.layout.fragment_index));
            sKeys.put("layout/item_0", Integer.valueOf((int) C0690R.layout.item));
        }
    }
}