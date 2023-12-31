package android.databinding;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CallbackRegistry<C, T, A> implements Cloneable {
    private static final String TAG = "CallbackRegistry";
    private List<C> mCallbacks = new ArrayList();
    private long mFirst64Removed = 0;
    private int mNotificationLevel;
    private final NotifierCallback<C, T, A> mNotifier;
    private long[] mRemainderRemoved;

    /* loaded from: classes.dex */
    public static abstract class NotifierCallback<C, T, A> {
        public abstract void onNotifyCallback(C c, T t, int i, A a);
    }

    public CallbackRegistry(NotifierCallback<C, T, A> notifier) {
        this.mNotifier = notifier;
    }

    public synchronized void notifyCallbacks(T sender, int arg, A arg2) {
        this.mNotificationLevel++;
        notifyRecurse(sender, arg, arg2);
        this.mNotificationLevel--;
        if (this.mNotificationLevel == 0) {
            if (this.mRemainderRemoved != null) {
                for (int i = this.mRemainderRemoved.length - 1; i >= 0; i--) {
                    long removedBits = this.mRemainderRemoved[i];
                    if (removedBits != 0) {
                        removeRemovedCallbacks((i + 1) * 64, removedBits);
                        this.mRemainderRemoved[i] = 0;
                    }
                }
            }
            if (this.mFirst64Removed != 0) {
                removeRemovedCallbacks(0, this.mFirst64Removed);
                this.mFirst64Removed = 0L;
            }
        }
    }

    private void notifyFirst64(T sender, int arg, A arg2) {
        int maxNotified = Math.min(64, this.mCallbacks.size());
        notifyCallbacks(sender, arg, arg2, 0, maxNotified, this.mFirst64Removed);
    }

    private void notifyRecurse(T sender, int arg, A arg2) {
        int callbackCount = this.mCallbacks.size();
        long[] jArr = this.mRemainderRemoved;
        int remainderIndex = jArr == null ? -1 : jArr.length - 1;
        notifyRemainder(sender, arg, arg2, remainderIndex);
        int startCallbackIndex = (remainderIndex + 2) * 64;
        notifyCallbacks(sender, arg, arg2, startCallbackIndex, callbackCount, 0L);
    }

    private void notifyRemainder(T sender, int arg, A arg2, int remainderIndex) {
        if (remainderIndex < 0) {
            notifyFirst64(sender, arg, arg2);
            return;
        }
        long bits = this.mRemainderRemoved[remainderIndex];
        int startIndex = (remainderIndex + 1) * 64;
        int endIndex = Math.min(this.mCallbacks.size(), startIndex + 64);
        notifyRemainder(sender, arg, arg2, remainderIndex - 1);
        notifyCallbacks(sender, arg, arg2, startIndex, endIndex, bits);
    }

    private void notifyCallbacks(T sender, int arg, A arg2, int startIndex, int endIndex, long bits) {
        long bitMask = 1;
        for (int i = startIndex; i < endIndex; i++) {
            if ((bits & bitMask) == 0) {
                this.mNotifier.onNotifyCallback(this.mCallbacks.get(i), sender, arg, arg2);
            }
            bitMask <<= 1;
        }
    }

    public synchronized void add(C callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        int index = this.mCallbacks.lastIndexOf(callback);
        if (index < 0 || isRemoved(index)) {
            this.mCallbacks.add(callback);
        }
    }

    private boolean isRemoved(int index) {
        int maskIndex;
        if (index < 64) {
            long bitMask = 1 << index;
            return (this.mFirst64Removed & bitMask) != 0;
        }
        long[] jArr = this.mRemainderRemoved;
        if (jArr == null || (maskIndex = (index / 64) - 1) >= jArr.length) {
            return false;
        }
        long bits = jArr[maskIndex];
        long bitMask2 = 1 << (index % 64);
        return (bits & bitMask2) != 0;
    }

    private void removeRemovedCallbacks(int startIndex, long removed) {
        int endIndex = startIndex + 64;
        long bitMask = Long.MIN_VALUE;
        for (int i = endIndex - 1; i >= startIndex; i--) {
            if ((removed & bitMask) != 0) {
                this.mCallbacks.remove(i);
            }
            bitMask >>>= 1;
        }
    }

    public synchronized void remove(C callback) {
        if (this.mNotificationLevel == 0) {
            this.mCallbacks.remove(callback);
        } else {
            int index = this.mCallbacks.lastIndexOf(callback);
            if (index >= 0) {
                setRemovalBit(index);
            }
        }
    }

    private void setRemovalBit(int index) {
        if (index < 64) {
            long bitMask = 1 << index;
            this.mFirst64Removed |= bitMask;
            return;
        }
        int remainderIndex = (index / 64) - 1;
        long[] jArr = this.mRemainderRemoved;
        if (jArr == null) {
            this.mRemainderRemoved = new long[this.mCallbacks.size() / 64];
        } else if (jArr.length <= remainderIndex) {
            long[] newRemainders = new long[this.mCallbacks.size() / 64];
            long[] jArr2 = this.mRemainderRemoved;
            System.arraycopy(jArr2, 0, newRemainders, 0, jArr2.length);
            this.mRemainderRemoved = newRemainders;
        }
        long bitMask2 = 1 << (index % 64);
        long[] jArr3 = this.mRemainderRemoved;
        jArr3[remainderIndex] = jArr3[remainderIndex] | bitMask2;
    }

    public synchronized ArrayList<C> copyCallbacks() {
        ArrayList<C> callbacks;
        callbacks = new ArrayList<>(this.mCallbacks.size());
        int numListeners = this.mCallbacks.size();
        for (int i = 0; i < numListeners; i++) {
            if (!isRemoved(i)) {
                callbacks.add(this.mCallbacks.get(i));
            }
        }
        return callbacks;
    }

    public synchronized void copyCallbacks(List<C> callbacks) {
        callbacks.clear();
        int numListeners = this.mCallbacks.size();
        for (int i = 0; i < numListeners; i++) {
            if (!isRemoved(i)) {
                callbacks.add(this.mCallbacks.get(i));
            }
        }
    }

    public synchronized boolean isEmpty() {
        if (this.mCallbacks.isEmpty()) {
            return true;
        }
        if (this.mNotificationLevel == 0) {
            return false;
        }
        int numListeners = this.mCallbacks.size();
        for (int i = 0; i < numListeners; i++) {
            if (!isRemoved(i)) {
                return false;
            }
        }
        return true;
    }

    public synchronized void clear() {
        if (this.mNotificationLevel == 0) {
            this.mCallbacks.clear();
        } else if (!this.mCallbacks.isEmpty()) {
            for (int i = this.mCallbacks.size() - 1; i >= 0; i--) {
                setRemovalBit(i);
            }
        }
    }

    /* renamed from: clone */
    public synchronized CallbackRegistry<C, T, A> m2clone() {
        CallbackRegistry<C, T, A> clone;
        clone = null;
        try {
            clone = (CallbackRegistry) super.clone();
            clone.mFirst64Removed = 0L;
            clone.mRemainderRemoved = null;
            clone.mNotificationLevel = 0;
            clone.mCallbacks = new ArrayList();
            int numListeners = this.mCallbacks.size();
            for (int i = 0; i < numListeners; i++) {
                if (!isRemoved(i)) {
                    clone.mCallbacks.add(this.mCallbacks.get(i));
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
