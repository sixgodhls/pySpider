package me.goldze.mvvmhabit.binding.viewadapter.webview;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.webkit.WebView;
import com.bumptech.glide.load.Key;

/* loaded from: classes.dex */
public class ViewAdapter {
    @BindingAdapter({"render"})
    public static void loadHtml(WebView webView, String html) {
        if (!TextUtils.isEmpty(html)) {
            webView.loadDataWithBaseURL(null, html, "text/html", Key.STRING_CHARSET_NAME, null);
        }
    }
}