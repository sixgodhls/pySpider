package android.databinding;

import android.databinding.CallbackRegistry;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v4.util.Pools;

/* loaded from: classes.dex */
public class ListChangeRegistry extends CallbackRegistry<ObservableList.OnListChangedCallback, ObservableList, ListChanges> {
    private static final int ALL = 0;
    private static final int CHANGED = 1;
    private static final int INSERTED = 2;
    private static final int MOVED = 3;
    private static final int REMOVED = 4;
    private static final Pools.SynchronizedPool<ListChanges> sListChanges = new Pools.SynchronizedPool<>(10);
    private static final CallbackRegistry.NotifierCallback<ObservableList.OnListChangedCallback, ObservableList, ListChanges> NOTIFIER_CALLBACK = new CallbackRegistry.NotifierCallback<ObservableList.OnListChangedCallback, ObservableList, ListChanges>() { // from class: android.databinding.ListChangeRegistry.1
        @Override // android.databinding.CallbackRegistry.NotifierCallback
        public void onNotifyCallback(ObservableList.OnListChangedCallback callback, ObservableList sender, int notificationType, ListChanges listChanges) {
            switch (notificationType) {
                case 1:
                    callback.onItemRangeChanged(sender, listChanges.start, listChanges.count);
                    return;
                case 2:
                    callback.onItemRangeInserted(sender, listChanges.start, listChanges.count);
                    return;
                case 3:
                    callback.onItemRangeMoved(sender, listChanges.start, listChanges.to, listChanges.count);
                    return;
                case 4:
                    callback.onItemRangeRemoved(sender, listChanges.start, listChanges.count);
                    return;
                default:
                    callback.onChanged(sender);
                    return;
            }
        }
    };

    public void notifyChanged(@NonNull ObservableList list) {
        notifyCallbacks(list, 0, (ListChanges) null);
    }

    public void notifyChanged(@NonNull ObservableList list, int start, int count) {
        ListChanges listChanges = acquire(start, 0, count);
        notifyCallbacks(list, 1, listChanges);
    }

    public void notifyInserted(@NonNull ObservableList list, int start, int count) {
        ListChanges listChanges = acquire(start, 0, count);
        notifyCallbacks(list, 2, listChanges);
    }

    public void notifyMoved(@NonNull ObservableList list, int from, int to, int count) {
        ListChanges listChanges = acquire(from, to, count);
        notifyCallbacks(list, 3, listChanges);
    }

    public void notifyRemoved(@NonNull ObservableList list, int start, int count) {
        ListChanges listChanges = acquire(start, 0, count);
        notifyCallbacks(list, 4, listChanges);
    }

    private static ListChanges acquire(int start, int to, int count) {
        ListChanges listChanges = sListChanges.acquire();
        if (listChanges == null) {
            listChanges = new ListChanges();
        }
        listChanges.start = start;
        listChanges.to = to;
        listChanges.count = count;
        return listChanges;
    }

    @Override // android.databinding.CallbackRegistry
    public synchronized void notifyCallbacks(@NonNull ObservableList sender, int notificationType, ListChanges listChanges) {
        super.notifyCallbacks((ListChangeRegistry) sender, notificationType, (int) listChanges);
        if (listChanges != null) {
            sListChanges.release(listChanges);
        }
    }

    public ListChangeRegistry() {
        super(NOTIFIER_CALLBACK);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ListChanges {
        public int count;
        public int start;
        public int to;

        ListChanges() {
        }
    }
}
