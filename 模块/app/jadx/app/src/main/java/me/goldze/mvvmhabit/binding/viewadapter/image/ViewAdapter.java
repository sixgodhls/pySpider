package me.goldze.mvvmhabit.binding.viewadapter.image;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/* loaded from: classes.dex */
public final class ViewAdapter {
    @BindingAdapter(requireAll = false, value = {"url", "placeholderRes"})
    public static void setImageUri(ImageView imageView, String url, int placeholderRes) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext()).mo212load(url).apply(new RequestOptions().placeholder(placeholderRes)).into(imageView);
        }
    }
}
