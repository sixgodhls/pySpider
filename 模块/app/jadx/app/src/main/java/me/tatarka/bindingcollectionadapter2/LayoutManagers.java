package me.tatarka.bindingcollectionadapter2;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public class LayoutManagers {

    /* loaded from: classes.dex */
    public interface LayoutManagerFactory {
        RecyclerView.LayoutManager create(RecyclerView recyclerView);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Orientation {
    }

    protected LayoutManagers() {
    }

    public static LayoutManagerFactory linear() {
        return new LayoutManagerFactory() { // from class: me.tatarka.bindingcollectionadapter2.LayoutManagers.1
            @Override // me.tatarka.bindingcollectionadapter2.LayoutManagers.LayoutManagerFactory
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new LinearLayoutManager(recyclerView.getContext());
            }
        };
    }

    public static LayoutManagerFactory linear(final int orientation, final boolean reverseLayout) {
        return new LayoutManagerFactory() { // from class: me.tatarka.bindingcollectionadapter2.LayoutManagers.2
            @Override // me.tatarka.bindingcollectionadapter2.LayoutManagers.LayoutManagerFactory
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new LinearLayoutManager(recyclerView.getContext(), orientation, reverseLayout);
            }
        };
    }

    public static LayoutManagerFactory grid(final int spanCount) {
        return new LayoutManagerFactory() { // from class: me.tatarka.bindingcollectionadapter2.LayoutManagers.3
            @Override // me.tatarka.bindingcollectionadapter2.LayoutManagers.LayoutManagerFactory
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new GridLayoutManager(recyclerView.getContext(), spanCount);
            }
        };
    }

    public static LayoutManagerFactory grid(final int spanCount, final int orientation, final boolean reverseLayout) {
        return new LayoutManagerFactory() { // from class: me.tatarka.bindingcollectionadapter2.LayoutManagers.4
            @Override // me.tatarka.bindingcollectionadapter2.LayoutManagers.LayoutManagerFactory
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new GridLayoutManager(recyclerView.getContext(), spanCount, orientation, reverseLayout);
            }
        };
    }

    public static LayoutManagerFactory staggeredGrid(final int spanCount, final int orientation) {
        return new LayoutManagerFactory() { // from class: me.tatarka.bindingcollectionadapter2.LayoutManagers.5
            @Override // me.tatarka.bindingcollectionadapter2.LayoutManagers.LayoutManagerFactory
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new StaggeredGridLayoutManager(spanCount, orientation);
            }
        };
    }
}
