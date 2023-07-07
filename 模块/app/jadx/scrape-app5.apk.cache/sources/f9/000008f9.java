package me.goldze.mvvmhabit.binding.viewadapter.listview;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* loaded from: classes.dex */
public final class ViewAdapter {
    @BindingAdapter(requireAll = false, value = {"onScrollChangeCommand", "onScrollStateChangedCommand"})
    public static void onScrollChangeCommand(ListView listView, final BindingCommand<ListViewScrollDataWrapper> onScrollChangeCommand, final BindingCommand<Integer> onScrollStateChangedCommand) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.listview.ViewAdapter.1
            private int scrollState;

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
                BindingCommand bindingCommand = BindingCommand.this;
                if (bindingCommand != null) {
                    bindingCommand.execute(Integer.valueOf(scrollState));
                }
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                BindingCommand bindingCommand = onScrollChangeCommand;
                if (bindingCommand != null) {
                    bindingCommand.execute(new ListViewScrollDataWrapper(this.scrollState, firstVisibleItem, visibleItemCount, totalItemCount));
                }
            }
        });
    }

    @BindingAdapter(requireAll = false, value = {"onItemClickCommand"})
    public static void onItemClickCommand(ListView listView, final BindingCommand<Integer> onItemClickCommand) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.listview.ViewAdapter.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BindingCommand bindingCommand = BindingCommand.this;
                if (bindingCommand != null) {
                    bindingCommand.execute(Integer.valueOf(position));
                }
            }
        });
    }

    @BindingAdapter({"onLoadMoreCommand"})
    public static void onLoadMoreCommand(ListView listView, BindingCommand<Integer> onLoadMoreCommand) {
        listView.setOnScrollListener(new OnScrollListener(listView, onLoadMoreCommand));
    }

    /* loaded from: classes.dex */
    public static class OnScrollListener implements AbsListView.OnScrollListener {
        private ListView listView;
        private PublishSubject<Integer> methodInvoke = PublishSubject.create();
        private BindingCommand<Integer> onLoadMoreCommand;

        public OnScrollListener(ListView listView, final BindingCommand<Integer> onLoadMoreCommand) {
            this.onLoadMoreCommand = onLoadMoreCommand;
            this.listView = listView;
            this.methodInvoke.throttleFirst(1L, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() { // from class: me.goldze.mvvmhabit.binding.viewadapter.listview.ViewAdapter.OnScrollListener.1
                @Override // io.reactivex.functions.Consumer
                public void accept(Integer integer) throws Exception {
                    onLoadMoreCommand.execute(integer);
                }
            });
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount != 0 && totalItemCount != this.listView.getHeaderViewsCount() + this.listView.getFooterViewsCount() && this.onLoadMoreCommand != null) {
                this.methodInvoke.onNext(Integer.valueOf(totalItemCount));
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ListViewScrollDataWrapper {
        public int firstVisibleItem;
        public int scrollState;
        public int totalItemCount;
        public int visibleItemCount;

        public ListViewScrollDataWrapper(int scrollState, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            this.firstVisibleItem = firstVisibleItem;
            this.visibleItemCount = visibleItemCount;
            this.totalItemCount = totalItemCount;
            this.scrollState = scrollState;
        }
    }
}