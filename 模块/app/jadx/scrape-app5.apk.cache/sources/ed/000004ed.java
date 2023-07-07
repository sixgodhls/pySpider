package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.annotation.RestrictTo;
import android.support.p003v7.widget.CardView;

@BindingMethods({@BindingMethod(attribute = "cardCornerRadius", method = "setRadius", type = CardView.class), @BindingMethod(attribute = "cardMaxElevation", method = "setMaxCardElevation", type = CardView.class), @BindingMethod(attribute = "cardPreventCornerOverlap", method = "setPreventCornerOverlap", type = CardView.class), @BindingMethod(attribute = "cardUseCompatPadding", method = "setUseCompatPadding", type = CardView.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class CardViewBindingAdapter {
    @BindingAdapter({"contentPadding"})
    public static void setContentPadding(CardView view, int padding) {
        view.setContentPadding(padding, padding, padding, padding);
    }

    @BindingAdapter({"contentPaddingLeft"})
    public static void setContentPaddingLeft(CardView view, int left) {
        int top = view.getContentPaddingTop();
        int right = view.getContentPaddingRight();
        int bottom = view.getContentPaddingBottom();
        view.setContentPadding(left, top, right, bottom);
    }

    @BindingAdapter({"contentPaddingTop"})
    public static void setContentPaddingTop(CardView view, int top) {
        int left = view.getContentPaddingLeft();
        int right = view.getContentPaddingRight();
        int bottom = view.getContentPaddingBottom();
        view.setContentPadding(left, top, right, bottom);
    }

    @BindingAdapter({"contentPaddingRight"})
    public static void setContentPaddingRight(CardView view, int right) {
        int left = view.getContentPaddingLeft();
        int top = view.getContentPaddingTop();
        int bottom = view.getContentPaddingBottom();
        view.setContentPadding(left, top, right, bottom);
    }

    @BindingAdapter({"contentPaddingBottom"})
    public static void setContentPaddingBottom(CardView view, int bottom) {
        int left = view.getContentPaddingLeft();
        int top = view.getContentPaddingTop();
        int right = view.getContentPaddingRight();
        view.setContentPadding(left, top, right, bottom);
    }
}