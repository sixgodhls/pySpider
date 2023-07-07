package me.goldze.mvvmhabit.binding.viewadapter.recyclerview;

import android.databinding.BindingAdapter;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LineManagers;

/* loaded from: classes.dex */
public class ViewAdapter {
    @BindingAdapter({"lineManager"})
    public static void setLineManager(RecyclerView recyclerView, LineManagers.LineManagerFactory lineManagerFactory) {
        recyclerView.addItemDecoration(lineManagerFactory.create(recyclerView));
    }

    @BindingAdapter(requireAll = false, value = {"onScrollChangeCommand", "onScrollStateChangedCommand"})
    public static void onScrollChangeCommand(RecyclerView recyclerView, final BindingCommand<ScrollDataWrapper> onScrollChangeCommand, final BindingCommand<Integer> onScrollStateChangedCommand) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.recyclerview.ViewAdapter.1
            private int state;

            @Override // android.support.p003v7.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView2, int dx, int dy) {
                super.onScrolled(recyclerView2, dx, dy);
                BindingCommand bindingCommand = BindingCommand.this;
                if (bindingCommand != null) {
                    bindingCommand.execute(new ScrollDataWrapper(dx, dy, this.state));
                }
            }

            @Override // android.support.p003v7.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView2, int newState) {
                super.onScrollStateChanged(recyclerView2, newState);
                this.state = newState;
                BindingCommand bindingCommand = onScrollStateChangedCommand;
                if (bindingCommand != null) {
                    bindingCommand.execute(Integer.valueOf(newState));
                }
            }
        });
    }

    @BindingAdapter({"onLoadMoreCommand"})
    public static void onLoadMoreCommand(RecyclerView recyclerView, BindingCommand<Integer> onLoadMoreCommand) {
        RecyclerView.OnScrollListener listener = new OnScrollListener(onLoadMoreCommand);
        recyclerView.addOnScrollListener(listener);
    }

    @BindingAdapter({"itemAnimator"})
    public static void setItemAnimator(RecyclerView recyclerView, RecyclerView.ItemAnimator animator) {
        recyclerView.setItemAnimator(animator);
    }

    /* loaded from: classes.dex */
    public static class OnScrollListener extends RecyclerView.OnScrollListener {
        private PublishSubject<Integer> methodInvoke = PublishSubject.create();
        private BindingCommand<Integer> onLoadMoreCommand;

        public OnScrollListener(final BindingCommand<Integer> onLoadMoreCommand) {
            this.onLoadMoreCommand = onLoadMoreCommand;
            this.methodInvoke.throttleFirst(1L, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() { // from class: me.goldze.mvvmhabit.binding.viewadapter.recyclerview.ViewAdapter.OnScrollListener.1
                @Override // io.reactivex.functions.Consumer
                public void accept(Integer integer) throws Exception {
                    onLoadMoreCommand.execute(integer);
                }
            });
        }

        @Override // android.support.p003v7.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
            if (visibleItemCount + pastVisiblesItems >= totalItemCount && this.onLoadMoreCommand != null) {
                this.methodInvoke.onNext(Integer.valueOf(recyclerView.getAdapter().getItemCount()));
            }
        }

        @Override // android.support.p003v7.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    }

    /* loaded from: classes.dex */
    public static class ScrollDataWrapper {
        public float scrollX;
        public float scrollY;
        public int state;

        public ScrollDataWrapper(float scrollX, float scrollY, int state) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.state = state;
        }
    }
}