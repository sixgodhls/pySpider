package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.adapters.AdapterViewBindingAdapter;
import android.support.annotation.RestrictTo;
import android.widget.AutoCompleteTextView;

@BindingMethods({@BindingMethod(attribute = "android:completionThreshold", method = "setThreshold", type = AutoCompleteTextView.class), @BindingMethod(attribute = "android:popupBackground", method = "setDropDownBackgroundDrawable", type = AutoCompleteTextView.class), @BindingMethod(attribute = "android:onDismiss", method = "setOnDismissListener", type = AutoCompleteTextView.class), @BindingMethod(attribute = "android:onItemClick", method = "setOnItemClickListener", type = AutoCompleteTextView.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class AutoCompleteTextViewBindingAdapter {

    /* loaded from: classes.dex */
    public interface FixText {
        CharSequence fixText(CharSequence charSequence);
    }

    /* loaded from: classes.dex */
    public interface IsValid {
        boolean isValid(CharSequence charSequence);
    }

    @BindingAdapter(requireAll = false, value = {"android:fixText", "android:isValid"})
    public static void setValidator(AutoCompleteTextView view, final FixText fixText, final IsValid isValid) {
        if (fixText == null && isValid == null) {
            view.setValidator(null);
        } else {
            view.setValidator(new AutoCompleteTextView.Validator() { // from class: android.databinding.adapters.AutoCompleteTextViewBindingAdapter.1
                @Override // android.widget.AutoCompleteTextView.Validator
                public boolean isValid(CharSequence text) {
                    IsValid isValid2 = IsValid.this;
                    if (isValid2 != null) {
                        return isValid2.isValid(text);
                    }
                    return true;
                }

                @Override // android.widget.AutoCompleteTextView.Validator
                public CharSequence fixText(CharSequence invalidText) {
                    FixText fixText2 = fixText;
                    if (fixText2 != null) {
                        return fixText2.fixText(invalidText);
                    }
                    return invalidText;
                }
            });
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onItemSelected", "android:onNothingSelected"})
    public static void setOnItemSelectedListener(AutoCompleteTextView view, AdapterViewBindingAdapter.OnItemSelected selected, AdapterViewBindingAdapter.OnNothingSelected nothingSelected) {
        if (selected == null && nothingSelected == null) {
            view.setOnItemSelectedListener(null);
        } else {
            view.setOnItemSelectedListener(new AdapterViewBindingAdapter.OnItemSelectedComponentListener(selected, nothingSelected, null));
        }
    }
}
