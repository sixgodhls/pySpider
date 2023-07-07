package com.bumptech.glide.load.engine.bitmap_recycle;

import android.support.annotation.Nullable;
import com.bumptech.glide.load.engine.bitmap_recycle.Poolable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
class GroupedLinkedMap<K extends Poolable, V> {
    private final LinkedEntry<K, V> head = new LinkedEntry<>();
    private final Map<K, LinkedEntry<K, V>> keyToEntry = new HashMap();

    public void put(K key, V value) {
        LinkedEntry<K, V> entry = this.keyToEntry.get(key);
        if (entry == null) {
            entry = new LinkedEntry<>(key);
            makeTail(entry);
            this.keyToEntry.put(key, entry);
        } else {
            key.offer();
        }
        entry.add(value);
    }

    @Nullable
    public V get(K key) {
        LinkedEntry<K, V> entry = this.keyToEntry.get(key);
        if (entry == null) {
            entry = new LinkedEntry<>(key);
            this.keyToEntry.put(key, entry);
        } else {
            key.offer();
        }
        makeHead(entry);
        return entry.removeLast();
    }

    @Nullable
    public V removeLast() {
        for (LinkedEntry linkedEntry = this.head.prev; !linkedEntry.equals(this.head); linkedEntry = linkedEntry.prev) {
            V removed = (V) linkedEntry.removeLast();
            if (removed != null) {
                return removed;
            }
            removeEntry(linkedEntry);
            this.keyToEntry.remove(linkedEntry.key);
            ((Poolable) linkedEntry.key).offer();
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("GroupedLinkedMap( ");
        boolean hadAtLeastOneItem = false;
        for (LinkedEntry linkedEntry = this.head.next; !linkedEntry.equals(this.head); linkedEntry = linkedEntry.next) {
            hadAtLeastOneItem = true;
            sb.append('{');
            sb.append(linkedEntry.key);
            sb.append(':');
            sb.append(linkedEntry.size());
            sb.append("}, ");
        }
        if (hadAtLeastOneItem) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(" )");
        return sb.toString();
    }

    private void makeHead(LinkedEntry<K, V> entry) {
        removeEntry(entry);
        LinkedEntry<K, V> linkedEntry = this.head;
        entry.prev = linkedEntry;
        entry.next = linkedEntry.next;
        updateEntry(entry);
    }

    private void makeTail(LinkedEntry<K, V> entry) {
        removeEntry(entry);
        entry.prev = this.head.prev;
        entry.next = this.head;
        updateEntry(entry);
    }

    private static <K, V> void updateEntry(LinkedEntry<K, V> entry) {
        entry.next.prev = entry;
        entry.prev.next = entry;
    }

    private static <K, V> void removeEntry(LinkedEntry<K, V> entry) {
        entry.prev.next = entry.next;
        entry.next.prev = entry.prev;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LinkedEntry<K, V> {
        final K key;
        LinkedEntry<K, V> next;
        LinkedEntry<K, V> prev;
        private List<V> values;

        LinkedEntry() {
            this(null);
        }

        LinkedEntry(K key) {
            this.prev = this;
            this.next = this;
            this.key = key;
        }

        @Nullable
        public V removeLast() {
            int valueSize = size();
            if (valueSize > 0) {
                return this.values.remove(valueSize - 1);
            }
            return null;
        }

        public int size() {
            List<V> list = this.values;
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        public void add(V value) {
            if (this.values == null) {
                this.values = new ArrayList();
            }
            this.values.add(value);
        }
    }
}
