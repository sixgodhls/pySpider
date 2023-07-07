package android.databinding.adapters;

import android.annotation.TargetApi;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.annotation.RestrictTo;
import android.widget.Switch;

@BindingMethods({@BindingMethod(attribute = "android:thumb", method = "setThumbDrawable", type = Switch.class), @BindingMethod(attribute = "android:track", method = "setTrackDrawable", type = Switch.class)})
@TargetApi(14)
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class SwitchBindingAdapter {
    @BindingAdapter({"android:switchTextAppearance"})
    public static void setSwitchTextAppearance(Switch view, int value) {
        view.setSwitchTextAppearance(null, value);
    }
}
