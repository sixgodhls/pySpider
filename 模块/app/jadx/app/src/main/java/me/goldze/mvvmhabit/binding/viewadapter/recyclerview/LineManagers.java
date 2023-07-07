package me.goldze.mvvmhabit.binding.viewadapter.recyclerview;

import android.support.v7.widget.RecyclerView;
import me.goldze.mvvmhabit.binding.viewadapter.recyclerview.DividerLine;

/* loaded from: classes.dex */
public class LineManagers {

    /* loaded from: classes.dex */
    public interface LineManagerFactory {
        RecyclerView.ItemDecoration create(RecyclerView recyclerView);
    }

    protected LineManagers() {
    }

    public static LineManagerFactory both() {
        return new LineManagerFactory() { // from class: me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LineManagers.1
            @Override // me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LineManagers.LineManagerFactory
            public RecyclerView.ItemDecoration create(RecyclerView recyclerView) {
                return new DividerLine(recyclerView.getContext(), DividerLine.LineDrawMode.BOTH);
            }
        };
    }

    public static LineManagerFactory horizontal() {
        return new LineManagerFactory() { // from class: me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LineManagers.2
            @Override // me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LineManagers.LineManagerFactory
            public RecyclerView.ItemDecoration create(RecyclerView recyclerView) {
                return new DividerLine(recyclerView.getContext(), DividerLine.LineDrawMode.HORIZONTAL);
            }
        };
    }

    public static LineManagerFactory vertical() {
        return new LineManagerFactory() { // from class: me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LineManagers.3
            @Override // me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LineManagers.LineManagerFactory
            public RecyclerView.ItemDecoration create(RecyclerView recyclerView) {
                return new DividerLine(recyclerView.getContext(), DividerLine.LineDrawMode.VERTICAL);
            }
        };
    }
}
