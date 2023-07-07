package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.annotation.RestrictTo;
import android.widget.CompoundButton;

@BindingMethods({@BindingMethod(attribute = "android:buttonTint", method = "setButtonTintList", type = CompoundButton.class), @BindingMethod(attribute = "android:onCheckedChanged", method = "setOnCheckedChangeListener", type = CompoundButton.class)})
@InverseBindingMethods({@InverseBindingMethod(attribute = "android:checked", type = CompoundButton.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class CompoundButtonBindingAdapter {
    @BindingAdapter({"android:checked"})
    public static void setChecked(CompoundButton view, boolean checked) {
        if (view.isChecked() != checked) {
            view.setChecked(checked);
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onCheckedChanged", "android:checkedAttrChanged"})
    public static void setListeners(CompoundButton view, final CompoundButton.OnCheckedChangeListener listener, final InverseBindingListener attrChange) {
        if (attrChange == null) {
            view.setOnCheckedChangeListener(listener);
        } else {
            view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: android.databinding.adapters.CompoundButtonBindingAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = listener;
                    if (onCheckedChangeListener != null) {
                        onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
                    }
                    attrChange.onChange();
                }
            });
        }
    }
}
