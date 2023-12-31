package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.annotation.RestrictTo;
import android.widget.SeekBar;

@InverseBindingMethods({@InverseBindingMethod(attribute = "android:progress", type = SeekBar.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class SeekBarBindingAdapter {

    /* loaded from: classes.dex */
    public interface OnProgressChanged {
        void onProgressChanged(SeekBar seekBar, int i, boolean z);
    }

    /* loaded from: classes.dex */
    public interface OnStartTrackingTouch {
        void onStartTrackingTouch(SeekBar seekBar);
    }

    /* loaded from: classes.dex */
    public interface OnStopTrackingTouch {
        void onStopTrackingTouch(SeekBar seekBar);
    }

    @BindingAdapter({"android:progress"})
    public static void setProgress(SeekBar view, int progress) {
        if (progress != view.getProgress()) {
            view.setProgress(progress);
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onStartTrackingTouch", "android:onStopTrackingTouch", "android:onProgressChanged", "android:progressAttrChanged"})
    public static void setOnSeekBarChangeListener(SeekBar view, final OnStartTrackingTouch start, final OnStopTrackingTouch stop, final OnProgressChanged progressChanged, final InverseBindingListener attrChanged) {
        if (start == null && stop == null && progressChanged == null && attrChanged == null) {
            view.setOnSeekBarChangeListener(null);
        } else {
            view.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: android.databinding.adapters.SeekBarBindingAdapter.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    OnProgressChanged onProgressChanged = OnProgressChanged.this;
                    if (onProgressChanged != null) {
                        onProgressChanged.onProgressChanged(seekBar, progress, fromUser);
                    }
                    InverseBindingListener inverseBindingListener = attrChanged;
                    if (inverseBindingListener != null) {
                        inverseBindingListener.onChange();
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                    OnStartTrackingTouch onStartTrackingTouch = start;
                    if (onStartTrackingTouch != null) {
                        onStartTrackingTouch.onStartTrackingTouch(seekBar);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                    OnStopTrackingTouch onStopTrackingTouch = stop;
                    if (onStopTrackingTouch != null) {
                        onStopTrackingTouch.onStopTrackingTouch(seekBar);
                    }
                }
            });
        }
    }
}