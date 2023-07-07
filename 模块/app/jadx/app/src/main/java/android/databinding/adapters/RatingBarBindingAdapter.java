package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.annotation.RestrictTo;
import android.widget.RatingBar;

@InverseBindingMethods({@InverseBindingMethod(attribute = "android:rating", type = RatingBar.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class RatingBarBindingAdapter {
    @BindingAdapter({"android:rating"})
    public static void setRating(RatingBar view, float rating) {
        if (view.getRating() != rating) {
            view.setRating(rating);
        }
    }

    @BindingAdapter(requireAll = false, value = {"android:onRatingChanged", "android:ratingAttrChanged"})
    public static void setListeners(RatingBar view, final RatingBar.OnRatingBarChangeListener listener, final InverseBindingListener ratingChange) {
        if (ratingChange == null) {
            view.setOnRatingBarChangeListener(listener);
        } else {
            view.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() { // from class: android.databinding.adapters.RatingBarBindingAdapter.1
                @Override // android.widget.RatingBar.OnRatingBarChangeListener
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    RatingBar.OnRatingBarChangeListener onRatingBarChangeListener = listener;
                    if (onRatingBarChangeListener != null) {
                        onRatingBarChangeListener.onRatingChanged(ratingBar, rating, fromUser);
                    }
                    ratingChange.onChange();
                }
            });
        }
    }
}
