package me.goldze.mvvmhabit.binding.viewadapter.viewpager;

import android.databinding.BindingAdapter;
import android.support.p000v4.view.ViewPager;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* loaded from: classes.dex */
public class ViewAdapter {
    @BindingAdapter(requireAll = false, value = {"onPageScrolledCommand", "onPageSelectedCommand", "onPageScrollStateChangedCommand"})
    public static void onScrollChangeCommand(ViewPager viewPager, final BindingCommand<ViewPagerDataWrapper> onPageScrolledCommand, final BindingCommand<Integer> onPageSelectedCommand, final BindingCommand<Integer> onPageScrollStateChangedCommand) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.viewpager.ViewAdapter.1
            private int state;

            @Override // android.support.p000v4.view.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                BindingCommand bindingCommand = BindingCommand.this;
                if (bindingCommand != null) {
                    bindingCommand.execute(new ViewPagerDataWrapper(position, positionOffset, positionOffsetPixels, this.state));
                }
            }

            @Override // android.support.p000v4.view.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                BindingCommand bindingCommand = onPageSelectedCommand;
                if (bindingCommand != null) {
                    bindingCommand.execute(Integer.valueOf(position));
                }
            }

            @Override // android.support.p000v4.view.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
                this.state = state;
                BindingCommand bindingCommand = onPageScrollStateChangedCommand;
                if (bindingCommand != null) {
                    bindingCommand.execute(Integer.valueOf(state));
                }
            }
        });
    }

    /* loaded from: classes.dex */
    public static class ViewPagerDataWrapper {
        public float position;
        public float positionOffset;
        public int positionOffsetPixels;
        public int state;

        public ViewPagerDataWrapper(float position, float positionOffset, int positionOffsetPixels, int state) {
            this.positionOffset = positionOffset;
            this.position = position;
            this.positionOffsetPixels = positionOffsetPixels;
            this.state = state;
        }
    }
}