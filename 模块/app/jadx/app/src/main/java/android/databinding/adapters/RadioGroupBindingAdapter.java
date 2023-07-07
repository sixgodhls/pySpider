package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.annotation.RestrictTo;
import android.widget.RadioGroup;

@InverseBindingMethods({@InverseBindingMethod(attribute = "android:checkedButton", method = "getCheckedRadioButtonId", type = RadioGroup.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class RadioGroupBindingAdapter {
    @BindingAdapter({"android:checkedButton"})
    public static void setCheckedButton(RadioGroup view, int id) {
        if (id != view.getCheckedRadioButtonId()) {
            view.check(id);
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onCheckedChanged", "android:checkedButtonAttrChanged"})
    public static void setListeners(RadioGroup view, final RadioGroup.OnCheckedChangeListener listener, final InverseBindingListener attrChange) {
        if (attrChange == null) {
            view.setOnCheckedChangeListener(listener);
        } else {
            view.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: android.databinding.adapters.RadioGroupBindingAdapter.1
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = listener;
                    if (onCheckedChangeListener != null) {
                        onCheckedChangeListener.onCheckedChanged(group, checkedId);
                    }
                    attrChange.onChange();
                }
            });
        }
    }
}
