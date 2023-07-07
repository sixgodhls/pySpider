package me.tatarka.bindingcollectionadapter2.collections;

import android.databinding.ListChangeRegistry;
import android.databinding.ObservableList;
import android.support.annotation.MainThread;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class DiffObservableList<T> extends AbstractList<T> implements ObservableList<T> {
    private final Object LIST_LOCK;
    private final Callback<T> callback;
    private final boolean detectMoves;
    private List<T> list;
    private final DiffObservableList<T>.ObservableListUpdateCallback listCallback;
    private final ListChangeRegistry listeners;

    /* loaded from: classes.dex */
    public interface Callback<T> {
        boolean areContentsTheSame(T t, T t2);

        boolean areItemsTheSame(T t, T t2);
    }

    public DiffObservableList(Callback<T> callback) {
        this(callback, true);
    }

    public DiffObservableList(Callback<T> callback, boolean detectMoves) {
        this.LIST_LOCK = new Object();
        this.list = Collections.emptyList();
        this.listeners = new ListChangeRegistry();
        this.listCallback = new ObservableListUpdateCallback();
        this.callback = callback;
        this.detectMoves = detectMoves;
    }

    public DiffUtil.DiffResult calculateDiff(List<T> newItems) {
        ArrayList<T> frozenList;
        synchronized (this.LIST_LOCK) {
            frozenList = new ArrayList<>(this.list);
        }
        return doCalculateDiff(frozenList, newItems);
    }

    private DiffUtil.DiffResult doCalculateDiff(final List<T> oldItems, final List<T> newItems) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: me.tatarka.bindingcollectionadapter2.collections.DiffObservableList.1
            @Override // android.support.v7.util.DiffUtil.Callback
            public int getOldListSize() {
                return oldItems.size();
            }

            @Override // android.support.v7.util.DiffUtil.Callback
            public int getNewListSize() {
                List list = newItems;
                if (list != null) {
                    return list.size();
                }
                return 0;
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // android.support.v7.util.DiffUtil.Callback
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return DiffObservableList.this.callback.areItemsTheSame(oldItems.get(oldItemPosition), newItems.get(newItemPosition));
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // android.support.v7.util.DiffUtil.Callback
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return DiffObservableList.this.callback.areContentsTheSame(oldItems.get(oldItemPosition), newItems.get(newItemPosition));
            }
        }, this.detectMoves);
    }

    @MainThread
    public void update(List<T> newItems, DiffUtil.DiffResult diffResult) {
        synchronized (this.LIST_LOCK) {
            this.list = newItems;
        }
        diffResult.dispatchUpdatesTo(this.listCallback);
    }

    @MainThread
    public void update(List<T> newItems) {
        DiffUtil.DiffResult diffResult = doCalculateDiff(this.list, newItems);
        this.list = newItems;
        diffResult.dispatchUpdatesTo(this.listCallback);
    }

    @Override // android.databinding.ObservableList
    public void addOnListChangedCallback(ObservableList.OnListChangedCallback<? extends ObservableList<T>> listener) {
        this.listeners.add(listener);
    }

    @Override // android.databinding.ObservableList
    public void removeOnListChangedCallback(ObservableList.OnListChangedCallback<? extends ObservableList<T>> listener) {
        this.listeners.remove(listener);
    }

    @Override // java.util.AbstractList, java.util.List
    public T get(int i) {
        return this.list.get(i);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.list.size();
    }

    /* loaded from: classes.dex */
    class ObservableListUpdateCallback implements ListUpdateCallback {
        ObservableListUpdateCallback() {
        }

        @Override // android.support.v7.util.ListUpdateCallback
        public void onChanged(int position, int count, Object payload) {
            DiffObservableList.this.listeners.notifyChanged(DiffObservableList.this, position, count);
        }

        @Override // android.support.v7.util.ListUpdateCallback
        public void onInserted(int position, int count) {
            DiffObservableList.this.modCount++;
            DiffObservableList.this.listeners.notifyInserted(DiffObservableList.this, position, count);
        }

        @Override // android.support.v7.util.ListUpdateCallback
        public void onRemoved(int position, int count) {
            DiffObservableList.this.modCount++;
            DiffObservableList.this.listeners.notifyRemoved(DiffObservableList.this, position, count);
        }

        @Override // android.support.v7.util.ListUpdateCallback
        public void onMoved(int fromPosition, int toPosition) {
            DiffObservableList.this.listeners.notifyMoved(DiffObservableList.this, fromPosition, toPosition, 1);
        }
    }
}
