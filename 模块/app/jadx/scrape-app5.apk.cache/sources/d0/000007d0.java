package com.goldze.mvvmhabit.adapter;

import android.databinding.BindingAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* loaded from: classes.dex */
public class ViewAdapter {
    @BindingAdapter(requireAll = false, value = {"onRefreshCommand", "onLoadMoreCommand"})
    public static void onRefreshAndLoadMoreCommand(TwinklingRefreshLayout twinklingRefreshLayout, final BindingCommand bindingCommand, final BindingCommand bindingCommand2) {
        twinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() { // from class: com.goldze.mvvmhabit.adapter.ViewAdapter.1
            @Override // com.lcodecore.tkrefreshlayout.RefreshListenerAdapter, com.lcodecore.tkrefreshlayout.PullListener
            public void onRefresh(TwinklingRefreshLayout twinklingRefreshLayout2) {
                super.onRefresh(twinklingRefreshLayout2);
                BindingCommand bindingCommand3 = BindingCommand.this;
                if (bindingCommand3 != null) {
                    bindingCommand3.execute();
                }
            }

            @Override // com.lcodecore.tkrefreshlayout.RefreshListenerAdapter, com.lcodecore.tkrefreshlayout.PullListener
            public void onLoadMore(TwinklingRefreshLayout twinklingRefreshLayout2) {
                super.onLoadMore(twinklingRefreshLayout2);
                BindingCommand bindingCommand3 = bindingCommand2;
                if (bindingCommand3 != null) {
                    bindingCommand3.execute();
                }
            }
        });
    }
}