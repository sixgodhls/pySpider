package me.tatarka.bindingcollectionadapter2.collections;

import android.databinding.ListChangeRegistry;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class MergeObservableList<T> extends AbstractList<T> implements ObservableList<T> {
    private final ArrayList<List<? extends T>> lists = new ArrayList<>();
    private final MergeObservableList<T>.ListChangeCallback callback = new ListChangeCallback();
    private final ListChangeRegistry listeners = new ListChangeRegistry();

    @Override // android.databinding.ObservableList
    public void addOnListChangedCallback(ObservableList.OnListChangedCallback<? extends ObservableList<T>> listener) {
        this.listeners.add(listener);
    }

    @Override // android.databinding.ObservableList
    public void removeOnListChangedCallback(ObservableList.OnListChangedCallback<? extends ObservableList<T>> listener) {
        this.listeners.remove(listener);
    }

    public MergeObservableList<T> insertItem(T object) {
        this.lists.add(Collections.singletonList(object));
        this.modCount++;
        this.listeners.notifyInserted(this, size() - 1, 1);
        return this;
    }

    public MergeObservableList<T> insertList(@NonNull ObservableList<? extends T> list) {
        list.addOnListChangedCallback(this.callback);
        int oldSize = size();
        this.lists.add(list);
        this.modCount++;
        if (!list.isEmpty()) {
            this.listeners.notifyInserted(this, oldSize, list.size());
        }
        return this;
    }

    public boolean removeItem(T object) {
        int size = 0;
        int listsSize = this.lists.size();
        for (int i = 0; i < listsSize; i++) {
            List<? extends T> list = this.lists.get(i);
            if (!(list instanceof ObservableList)) {
                Object item = list.get(0);
                if (object == null) {
                    if (item == null) {
                        this.lists.remove(i);
                        this.modCount++;
                        this.listeners.notifyRemoved(this, size, 1);
                        return true;
                    }
                } else if (object.equals(item)) {
                    this.lists.remove(i);
                    this.modCount++;
                    this.listeners.notifyRemoved(this, size, 1);
                    return true;
                }
            }
            size += list.size();
        }
        return false;
    }

    public boolean removeList(ObservableList<? extends T> listToRemove) {
        int size = 0;
        int listsSize = this.lists.size();
        for (int i = 0; i < listsSize; i++) {
            List<? extends T> list = this.lists.get(i);
            if (list == listToRemove) {
                listToRemove.removeOnListChangedCallback(this.callback);
                this.lists.remove(i);
                this.modCount++;
                this.listeners.notifyRemoved(this, size, list.size());
                return true;
            }
            size += list.size();
        }
        return false;
    }

    public void removeAll() {
        int size = size();
        if (size == 0) {
            return;
        }
        int listSize = this.lists.size();
        for (int i = 0; i < listSize; i++) {
            List<? extends T> list = this.lists.get(i);
            if (list instanceof ObservableList) {
                ((ObservableList) list).removeOnListChangedCallback(this.callback);
            }
        }
        this.lists.clear();
        this.modCount++;
        this.listeners.notifyRemoved(this, 0, size);
    }

    public int mergeToBackingIndex(ObservableList<? extends T> backingList, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        int size = 0;
        int listsSize = this.lists.size();
        for (int i = 0; i < listsSize; i++) {
            List<? extends T> list = this.lists.get(i);
            if (backingList == list) {
                if (index < list.size()) {
                    return size + index;
                }
                throw new IndexOutOfBoundsException();
            }
            size += list.size();
        }
        throw new IllegalArgumentException();
    }

    public int backingIndexToMerge(ObservableList<? extends T> backingList, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        int size = 0;
        int listsSize = this.lists.size();
        for (int i = 0; i < listsSize; i++) {
            List<? extends T> list = this.lists.get(i);
            if (backingList == list) {
                if (index - size < list.size()) {
                    return index - size;
                }
                throw new IndexOutOfBoundsException();
            }
            size += list.size();
        }
        throw new IllegalArgumentException();
    }

    @Override // java.util.AbstractList, java.util.List
    public T get(int location) {
        if (location < 0) {
            throw new IndexOutOfBoundsException();
        }
        int size = 0;
        int listsSize = this.lists.size();
        for (int i = 0; i < listsSize; i++) {
            List<? extends T> list = this.lists.get(i);
            if (location - size < list.size()) {
                return list.get(location - size);
            }
            size += list.size();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        int size = 0;
        int listsSize = this.lists.size();
        for (int i = 0; i < listsSize; i++) {
            List<? extends T> list = this.lists.get(i);
            size += list.size();
        }
        return size;
    }

    /* loaded from: classes.dex */
    class ListChangeCallback extends ObservableList.OnListChangedCallback {
        ListChangeCallback() {
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onChanged(ObservableList sender) {
            MergeObservableList.this.modCount++;
            MergeObservableList.this.listeners.notifyChanged(MergeObservableList.this);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            int size = 0;
            int listsSize = MergeObservableList.this.lists.size();
            for (int i = 0; i < listsSize; i++) {
                List list = (List) MergeObservableList.this.lists.get(i);
                if (list == sender) {
                    MergeObservableList.this.listeners.notifyChanged(MergeObservableList.this, size + positionStart, itemCount);
                    return;
                }
                size += list.size();
            }
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            MergeObservableList.this.modCount++;
            int size = 0;
            int listsSize = MergeObservableList.this.lists.size();
            for (int i = 0; i < listsSize; i++) {
                List list = (List) MergeObservableList.this.lists.get(i);
                if (list == sender) {
                    MergeObservableList.this.listeners.notifyInserted(MergeObservableList.this, size + positionStart, itemCount);
                    return;
                }
                size += list.size();
            }
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            int size = 0;
            int listsSize = MergeObservableList.this.lists.size();
            for (int i = 0; i < listsSize; i++) {
                List list = (List) MergeObservableList.this.lists.get(i);
                if (list == sender) {
                    MergeObservableList.this.listeners.notifyMoved(MergeObservableList.this, size + fromPosition, size + toPosition, itemCount);
                    return;
                }
                size += list.size();
            }
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            MergeObservableList.this.modCount++;
            int size = 0;
            int listsSize = MergeObservableList.this.lists.size();
            for (int i = 0; i < listsSize; i++) {
                List list = (List) MergeObservableList.this.lists.get(i);
                if (list == sender) {
                    MergeObservableList.this.listeners.notifyRemoved(MergeObservableList.this, size + positionStart, itemCount);
                    return;
                }
                size += list.size();
            }
        }
    }
}