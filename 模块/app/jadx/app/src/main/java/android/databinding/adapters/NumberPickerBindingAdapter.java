package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.annotation.RestrictTo;
import android.widget.NumberPicker;

@BindingMethods({@BindingMethod(attribute = "android:format", method = "setFormatter", type = NumberPicker.class), @BindingMethod(attribute = "android:onScrollStateChange", method = "setOnScrollListener", type = NumberPicker.class)})
@InverseBindingMethods({@InverseBindingMethod(attribute = "android:value", type = NumberPicker.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class NumberPickerBindingAdapter {
    @BindingAdapter({"android:value"})
    public static void setValue(NumberPicker view, int value) {
        if (view.getValue() != value) {
            view.setValue(value);
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onValueChange", "android:valueAttrChanged"})
    public static void setListeners(NumberPicker view, final NumberPicker.OnValueChangeListener listener, final InverseBindingListener attrChange) {
        if (attrChange == null) {
            view.setOnValueChangedListener(listener);
        } else {
            view.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: android.databinding.adapters.NumberPickerBindingAdapter.1
                @Override // android.widget.NumberPicker.OnValueChangeListener
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    NumberPicker.OnValueChangeListener onValueChangeListener = listener;
                    if (onValueChangeListener != null) {
                        onValueChangeListener.onValueChange(picker, oldVal, newVal);
                    }
                    attrChange.onChange();
                }
            });
        }
    }
}
