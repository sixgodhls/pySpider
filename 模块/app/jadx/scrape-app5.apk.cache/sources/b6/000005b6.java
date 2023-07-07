package me.goldze.mvvmhabit.binding.viewadapter.scrollview;

import android.databinding.BindingAdapter;
import android.support.p000v4.widget.NestedScrollView;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* loaded from: classes.dex */
public final class ViewAdapter {
    @BindingAdapter({"onScrollChangeCommand"})
    public static void onScrollChangeCommand(NestedScrollView nestedScrollView, final BindingCommand<NestScrollDataWrapper> onScrollChangeCommand) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.scrollview.ViewAdapter.1
            @Override // android.support.p000v4.widget.NestedScrollView.OnScrollChangeListener
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                BindingCommand bindingCommand = BindingCommand.this;
                if (bindingCommand != null) {
                    bindingCommand.execute(new NestScrollDataWrapper(scrollX, scrollY, oldScrollX, oldScrollY));
                }
            }
        });
    }

    @BindingAdapter({"onScrollChangeCommand"})
    public static void onScrollChangeCommand(final ScrollView scrollView, final BindingCommand<ScrollDataWrapper> onScrollChangeCommand) {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.scrollview.ViewAdapter.2
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                BindingCommand bindingCommand = BindingCommand.this;
                if (bindingCommand != null) {
                    bindingCommand.execute(new ScrollDataWrapper(scrollView.getScrollX(), scrollView.getScrollY()));
                }
            }
        });
    }

    /* loaded from: classes.dex */
    public static class ScrollDataWrapper {
        public float scrollX;
        public float scrollY;

        public ScrollDataWrapper(float scrollX, float scrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
        }
    }

    /* loaded from: classes.dex */
    public static class NestScrollDataWrapper {
        public int oldScrollX;
        public int oldScrollY;
        public int scrollX;
        public int scrollY;

        public NestScrollDataWrapper(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.oldScrollX = oldScrollX;
            this.oldScrollY = oldScrollY;
        }
    }
}