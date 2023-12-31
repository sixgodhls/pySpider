package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.annotation.RestrictTo;
import android.widget.AbsListView;

@BindingMethods({@BindingMethod(attribute = "android:listSelector", method = "setSelector", type = AbsListView.class), @BindingMethod(attribute = "android:scrollingCache", method = "setScrollingCacheEnabled", type = AbsListView.class), @BindingMethod(attribute = "android:smoothScrollbar", method = "setSmoothScrollbarEnabled", type = AbsListView.class), @BindingMethod(attribute = "android:onMovedToScrapHeap", method = "setRecyclerListener", type = AbsListView.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class AbsListViewBindingAdapter {

    /* loaded from: classes.dex */
    public interface OnScroll {
        void onScroll(AbsListView absListView, int i, int i2, int i3);
    }

    /* loaded from: classes.dex */
    public interface OnScrollStateChanged {
        void onScrollStateChanged(AbsListView absListView, int i);
    }

    @BindingAdapter(requireAll = false, value = {"android:onScroll", "android:onScrollStateChanged"})
    public static void setOnScroll(AbsListView view, final OnScroll scrollListener, final OnScrollStateChanged scrollStateListener) {
        view.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: android.databinding.adapters.AbsListViewBindingAdapter.1
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView view2, int scrollState) {
                OnScrollStateChanged onScrollStateChanged = OnScrollStateChanged.this;
                if (onScrollStateChanged != null) {
                    onScrollStateChanged.onScrollStateChanged(view2, scrollState);
                }
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView view2, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                OnScroll onScroll = scrollListener;
                if (onScroll != null) {
                    onScroll.onScroll(view2, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });
    }
}