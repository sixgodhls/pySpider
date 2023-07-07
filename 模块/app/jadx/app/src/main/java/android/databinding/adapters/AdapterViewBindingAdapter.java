package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

@BindingMethods({@BindingMethod(attribute = "android:onItemClick", method = "setOnItemClickListener", type = AdapterView.class), @BindingMethod(attribute = "android:onItemLongClick", method = "setOnItemLongClickListener", type = AdapterView.class)})
@InverseBindingMethods({@InverseBindingMethod(attribute = "android:selectedItemPosition", type = AdapterView.class), @InverseBindingMethod(attribute = "android:selection", event = "android:selectedItemPositionAttrChanged", method = "getSelectedItemPosition", type = AdapterView.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class AdapterViewBindingAdapter {

    /* loaded from: classes.dex */
    public interface OnItemSelected {
        void onItemSelected(AdapterView<?> adapterView, View view, int i, long j);
    }

    /* loaded from: classes.dex */
    public interface OnNothingSelected {
        void onNothingSelected(AdapterView<?> adapterView);
    }

    @BindingAdapter({"android:selectedItemPosition"})
    public static void setSelectedItemPosition(AdapterView view, int position) {
        if (view.getSelectedItemPosition() != position) {
            view.setSelection(position);
        }
    }

    @BindingAdapter({"android:selection"})
    public static void setSelection(AdapterView view, int position) {
        setSelectedItemPosition(view, position);
    }

    @BindingAdapter({"android:selectedItemPosition", "android:adapter"})
    public static void setSelectedItemPosition(AdapterView view, int position, Adapter adapter) {
        if (adapter != view.getAdapter()) {
            view.setAdapter(adapter);
            view.setSelection(position);
        } else if (view.getSelectedItemPosition() != position) {
            view.setSelection(position);
        }
    }

    @BindingAdapter({"android:selection", "android:adapter"})
    public static void setSelection(AdapterView view, int position, Adapter adapter) {
        setSelectedItemPosition(view, position, adapter);
    }

    @BindingAdapter(requireAll = false, value = {"android:onItemSelected", "android:onNothingSelected", "android:selectedItemPositionAttrChanged"})
    public static void setOnItemSelectedListener(AdapterView view, OnItemSelected selected, OnNothingSelected nothingSelected, InverseBindingListener attrChanged) {
        if (selected == null && nothingSelected == null && attrChanged == null) {
            view.setOnItemSelectedListener(null);
        } else {
            view.setOnItemSelectedListener(new OnItemSelectedComponentListener(selected, nothingSelected, attrChanged));
        }
    }

    /* loaded from: classes.dex */
    public static class OnItemSelectedComponentListener implements AdapterView.OnItemSelectedListener {
        private final InverseBindingListener mAttrChanged;
        private final OnNothingSelected mNothingSelected;
        private final OnItemSelected mSelected;

        public OnItemSelectedComponentListener(OnItemSelected selected, OnNothingSelected nothingSelected, InverseBindingListener attrChanged) {
            this.mSelected = selected;
            this.mNothingSelected = nothingSelected;
            this.mAttrChanged = attrChanged;
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            OnItemSelected onItemSelected = this.mSelected;
            if (onItemSelected != null) {
                onItemSelected.onItemSelected(parent, view, position, id);
            }
            InverseBindingListener inverseBindingListener = this.mAttrChanged;
            if (inverseBindingListener != null) {
                inverseBindingListener.onChange();
            }
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> parent) {
            OnNothingSelected onNothingSelected = this.mNothingSelected;
            if (onNothingSelected != null) {
                onNothingSelected.onNothingSelected(parent);
            }
            InverseBindingListener inverseBindingListener = this.mAttrChanged;
            if (inverseBindingListener != null) {
                inverseBindingListener.onChange();
            }
        }
    }
}
