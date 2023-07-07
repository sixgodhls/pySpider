package android.support.v7.widget;

import android.support.v4.util.Pools;
import android.support.v7.widget.OpReorderer;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AdapterHelper implements OpReorderer.Callback {
    private static final boolean DEBUG = false;
    static final int POSITION_TYPE_INVISIBLE = 0;
    static final int POSITION_TYPE_NEW_OR_LAID_OUT = 1;
    private static final String TAG = "AHT";
    final Callback mCallback;
    final boolean mDisableRecycler;
    private int mExistingUpdateTypes;
    Runnable mOnItemProcessedCallback;
    final OpReorderer mOpReorderer;
    final ArrayList<UpdateOp> mPendingUpdates;
    final ArrayList<UpdateOp> mPostponedList;
    private Pools.Pool<UpdateOp> mUpdateOpPool;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Callback {
        RecyclerView.ViewHolder findViewHolder(int i);

        void markViewHoldersUpdated(int i, int i2, Object obj);

        void offsetPositionsForAdd(int i, int i2);

        void offsetPositionsForMove(int i, int i2);

        void offsetPositionsForRemovingInvisible(int i, int i2);

        void offsetPositionsForRemovingLaidOutOrNewView(int i, int i2);

        void onDispatchFirstPass(UpdateOp updateOp);

        void onDispatchSecondPass(UpdateOp updateOp);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdapterHelper(Callback callback) {
        this(callback, false);
    }

    AdapterHelper(Callback callback, boolean disableRecycler) {
        this.mUpdateOpPool = new Pools.SimplePool(30);
        this.mPendingUpdates = new ArrayList<>();
        this.mPostponedList = new ArrayList<>();
        this.mExistingUpdateTypes = 0;
        this.mCallback = callback;
        this.mDisableRecycler = disableRecycler;
        this.mOpReorderer = new OpReorderer(this);
    }

    AdapterHelper addUpdateOp(UpdateOp... ops) {
        Collections.addAll(this.mPendingUpdates, ops);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reset() {
        recycleUpdateOpsAndClearList(this.mPendingUpdates);
        recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void preProcess() {
        this.mOpReorderer.reorderOps(this.mPendingUpdates);
        int count = this.mPendingUpdates.size();
        for (int i = 0; i < count; i++) {
            UpdateOp op = this.mPendingUpdates.get(i);
            int i2 = op.cmd;
            if (i2 == 4) {
                applyUpdate(op);
            } else if (i2 != 8) {
                switch (i2) {
                    case 1:
                        applyAdd(op);
                        break;
                    case 2:
                        applyRemove(op);
                        break;
                }
            } else {
                applyMove(op);
            }
            Runnable runnable = this.mOnItemProcessedCallback;
            if (runnable != null) {
                runnable.run();
            }
        }
        this.mPendingUpdates.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void consumePostponedUpdates() {
        int count = this.mPostponedList.size();
        for (int i = 0; i < count; i++) {
            this.mCallback.onDispatchSecondPass(this.mPostponedList.get(i));
        }
        recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }

    private void applyMove(UpdateOp op) {
        postponeAndUpdateViewHolders(op);
    }

    private void applyRemove(UpdateOp op) {
        int tmpStart = op.positionStart;
        int tmpCount = 0;
        int tmpEnd = op.positionStart + op.itemCount;
        int type = -1;
        int position = op.positionStart;
        while (position < tmpEnd) {
            boolean typeChanged = false;
            RecyclerView.ViewHolder vh = this.mCallback.findViewHolder(position);
            if (vh != null || canFindInPreLayout(position)) {
                if (type == 0) {
                    UpdateOp newOp = obtainUpdateOp(2, tmpStart, tmpCount, null);
                    dispatchAndUpdateViewHolders(newOp);
                    typeChanged = true;
                }
                type = 1;
            } else {
                if (type == 1) {
                    UpdateOp newOp2 = obtainUpdateOp(2, tmpStart, tmpCount, null);
                    postponeAndUpdateViewHolders(newOp2);
                    typeChanged = true;
                }
                type = 0;
            }
            if (typeChanged) {
                position -= tmpCount;
                tmpEnd -= tmpCount;
                tmpCount = 1;
            } else {
                tmpCount++;
            }
            position++;
        }
        int position2 = op.itemCount;
        if (tmpCount != position2) {
            recycleUpdateOp(op);
            op = obtainUpdateOp(2, tmpStart, tmpCount, null);
        }
        if (type == 0) {
            dispatchAndUpdateViewHolders(op);
        } else {
            postponeAndUpdateViewHolders(op);
        }
    }

    private void applyUpdate(UpdateOp op) {
        int tmpStart = op.positionStart;
        int tmpCount = 0;
        int tmpEnd = op.positionStart + op.itemCount;
        int type = -1;
        for (int position = op.positionStart; position < tmpEnd; position++) {
            RecyclerView.ViewHolder vh = this.mCallback.findViewHolder(position);
            if (vh != null || canFindInPreLayout(position)) {
                if (type == 0) {
                    UpdateOp newOp = obtainUpdateOp(4, tmpStart, tmpCount, op.payload);
                    dispatchAndUpdateViewHolders(newOp);
                    tmpCount = 0;
                    tmpStart = position;
                }
                type = 1;
            } else {
                if (type == 1) {
                    UpdateOp newOp2 = obtainUpdateOp(4, tmpStart, tmpCount, op.payload);
                    postponeAndUpdateViewHolders(newOp2);
                    tmpCount = 0;
                    tmpStart = position;
                }
                type = 0;
            }
            tmpCount++;
        }
        int position2 = op.itemCount;
        if (tmpCount != position2) {
            Object payload = op.payload;
            recycleUpdateOp(op);
            op = obtainUpdateOp(4, tmpStart, tmpCount, payload);
        }
        if (type == 0) {
            dispatchAndUpdateViewHolders(op);
        } else {
            postponeAndUpdateViewHolders(op);
        }
    }

    private void dispatchAndUpdateViewHolders(UpdateOp op) {
        int positionMultiplier;
        if (op.cmd == 1 || op.cmd == 8) {
            throw new IllegalArgumentException("should not dispatch add or move for pre layout");
        }
        int tmpStart = updatePositionWithPostponed(op.positionStart, op.cmd);
        int tmpCnt = 1;
        int offsetPositionForPartial = op.positionStart;
        int i = op.cmd;
        if (i == 2) {
            positionMultiplier = 0;
        } else if (i == 4) {
            positionMultiplier = 1;
        } else {
            throw new IllegalArgumentException("op should be remove or update." + op);
        }
        for (int p = 1; p < op.itemCount; p++) {
            int pos = op.positionStart + (positionMultiplier * p);
            int updatedPos = updatePositionWithPostponed(pos, op.cmd);
            boolean continuous = false;
            int i2 = op.cmd;
            boolean z = false;
            if (i2 == 2) {
                if (updatedPos == tmpStart) {
                    z = true;
                }
                continuous = z;
            } else if (i2 == 4) {
                if (updatedPos == tmpStart + 1) {
                    z = true;
                }
                continuous = z;
            }
            if (continuous) {
                tmpCnt++;
            } else {
                UpdateOp tmp = obtainUpdateOp(op.cmd, tmpStart, tmpCnt, op.payload);
                dispatchFirstPassAndUpdateViewHolders(tmp, offsetPositionForPartial);
                recycleUpdateOp(tmp);
                if (op.cmd == 4) {
                    offsetPositionForPartial += tmpCnt;
                }
                tmpStart = updatedPos;
                tmpCnt = 1;
            }
        }
        Object payload = op.payload;
        recycleUpdateOp(op);
        if (tmpCnt > 0) {
            UpdateOp tmp2 = obtainUpdateOp(op.cmd, tmpStart, tmpCnt, payload);
            dispatchFirstPassAndUpdateViewHolders(tmp2, offsetPositionForPartial);
            recycleUpdateOp(tmp2);
        }
    }

    void dispatchFirstPassAndUpdateViewHolders(UpdateOp op, int offsetStart) {
        this.mCallback.onDispatchFirstPass(op);
        int i = op.cmd;
        if (i == 2) {
            this.mCallback.offsetPositionsForRemovingInvisible(offsetStart, op.itemCount);
        } else if (i == 4) {
            this.mCallback.markViewHoldersUpdated(offsetStart, op.itemCount, op.payload);
        } else {
            throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass");
        }
    }

    private int updatePositionWithPostponed(int pos, int cmd) {
        int start;
        int end;
        int count = this.mPostponedList.size();
        for (int i = count - 1; i >= 0; i--) {
            UpdateOp postponed = this.mPostponedList.get(i);
            if (postponed.cmd == 8) {
                if (postponed.positionStart < postponed.itemCount) {
                    start = postponed.positionStart;
                    end = postponed.itemCount;
                } else {
                    start = postponed.itemCount;
                    end = postponed.positionStart;
                }
                if (pos >= start && pos <= end) {
                    if (start == postponed.positionStart) {
                        if (cmd == 1) {
                            postponed.itemCount++;
                        } else if (cmd == 2) {
                            postponed.itemCount--;
                        }
                        pos++;
                    } else {
                        if (cmd == 1) {
                            postponed.positionStart++;
                        } else if (cmd == 2) {
                            postponed.positionStart--;
                        }
                        pos--;
                    }
                } else if (pos < postponed.positionStart) {
                    if (cmd == 1) {
                        postponed.positionStart++;
                        postponed.itemCount++;
                    } else if (cmd == 2) {
                        postponed.positionStart--;
                        postponed.itemCount--;
                    }
                }
            } else if (postponed.positionStart <= pos) {
                if (postponed.cmd == 1) {
                    pos -= postponed.itemCount;
                } else if (postponed.cmd == 2) {
                    pos += postponed.itemCount;
                }
            } else if (cmd == 1) {
                postponed.positionStart++;
            } else if (cmd == 2) {
                postponed.positionStart--;
            }
        }
        for (int i2 = this.mPostponedList.size() - 1; i2 >= 0; i2--) {
            UpdateOp op = this.mPostponedList.get(i2);
            if (op.cmd == 8) {
                if (op.itemCount == op.positionStart || op.itemCount < 0) {
                    this.mPostponedList.remove(i2);
                    recycleUpdateOp(op);
                }
            } else if (op.itemCount <= 0) {
                this.mPostponedList.remove(i2);
                recycleUpdateOp(op);
            }
        }
        return pos;
    }

    private boolean canFindInPreLayout(int position) {
        int count = this.mPostponedList.size();
        for (int i = 0; i < count; i++) {
            UpdateOp op = this.mPostponedList.get(i);
            if (op.cmd == 8) {
                if (findPositionOffset(op.itemCount, i + 1) == position) {
                    return true;
                }
            } else if (op.cmd == 1) {
                int end = op.positionStart + op.itemCount;
                for (int pos = op.positionStart; pos < end; pos++) {
                    if (findPositionOffset(pos, i + 1) == position) {
                        return true;
                    }
                }
                continue;
            } else {
                continue;
            }
        }
        return false;
    }

    private void applyAdd(UpdateOp op) {
        postponeAndUpdateViewHolders(op);
    }

    private void postponeAndUpdateViewHolders(UpdateOp op) {
        this.mPostponedList.add(op);
        int i = op.cmd;
        if (i == 4) {
            this.mCallback.markViewHoldersUpdated(op.positionStart, op.itemCount, op.payload);
        } else if (i != 8) {
            switch (i) {
                case 1:
                    this.mCallback.offsetPositionsForAdd(op.positionStart, op.itemCount);
                    return;
                case 2:
                    this.mCallback.offsetPositionsForRemovingLaidOutOrNewView(op.positionStart, op.itemCount);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown update op type for " + op);
            }
        } else {
            this.mCallback.offsetPositionsForMove(op.positionStart, op.itemCount);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasPendingUpdates() {
        return this.mPendingUpdates.size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasAnyUpdateTypes(int updateTypes) {
        return (this.mExistingUpdateTypes & updateTypes) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int findPositionOffset(int position) {
        return findPositionOffset(position, 0);
    }

    int findPositionOffset(int position, int firstPostponedItem) {
        int count = this.mPostponedList.size();
        for (int i = firstPostponedItem; i < count; i++) {
            UpdateOp op = this.mPostponedList.get(i);
            if (op.cmd == 8) {
                if (op.positionStart == position) {
                    position = op.itemCount;
                } else {
                    if (op.positionStart < position) {
                        position--;
                    }
                    if (op.itemCount <= position) {
                        position++;
                    }
                }
            } else if (op.positionStart > position) {
                continue;
            } else if (op.cmd == 2) {
                if (position < op.positionStart + op.itemCount) {
                    return -1;
                }
                position -= op.itemCount;
            } else if (op.cmd == 1) {
                position += op.itemCount;
            }
        }
        return position;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        if (itemCount < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(4, positionStart, itemCount, payload));
        this.mExistingUpdateTypes |= 4;
        return this.mPendingUpdates.size() == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onItemRangeInserted(int positionStart, int itemCount) {
        if (itemCount < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(1, positionStart, itemCount, null));
        this.mExistingUpdateTypes |= 1;
        return this.mPendingUpdates.size() == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onItemRangeRemoved(int positionStart, int itemCount) {
        if (itemCount < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(2, positionStart, itemCount, null));
        this.mExistingUpdateTypes |= 2;
        return this.mPendingUpdates.size() == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onItemRangeMoved(int from, int to, int itemCount) {
        if (from == to) {
            return false;
        }
        if (itemCount != 1) {
            throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
        }
        this.mPendingUpdates.add(obtainUpdateOp(8, from, to, null));
        this.mExistingUpdateTypes |= 8;
        return this.mPendingUpdates.size() == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void consumeUpdatesInOnePass() {
        consumePostponedUpdates();
        int count = this.mPendingUpdates.size();
        for (int i = 0; i < count; i++) {
            UpdateOp op = this.mPendingUpdates.get(i);
            int i2 = op.cmd;
            if (i2 == 4) {
                this.mCallback.onDispatchSecondPass(op);
                this.mCallback.markViewHoldersUpdated(op.positionStart, op.itemCount, op.payload);
            } else if (i2 != 8) {
                switch (i2) {
                    case 1:
                        this.mCallback.onDispatchSecondPass(op);
                        this.mCallback.offsetPositionsForAdd(op.positionStart, op.itemCount);
                        break;
                    case 2:
                        this.mCallback.onDispatchSecondPass(op);
                        this.mCallback.offsetPositionsForRemovingInvisible(op.positionStart, op.itemCount);
                        break;
                }
            } else {
                this.mCallback.onDispatchSecondPass(op);
                this.mCallback.offsetPositionsForMove(op.positionStart, op.itemCount);
            }
            Runnable runnable = this.mOnItemProcessedCallback;
            if (runnable != null) {
                runnable.run();
            }
        }
        recycleUpdateOpsAndClearList(this.mPendingUpdates);
        this.mExistingUpdateTypes = 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x0047, code lost:
        continue;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int applyPendingUpdatesToPosition(int r6) {
        /*
            r5 = this;
            java.util.ArrayList<android.support.v7.widget.AdapterHelper$UpdateOp> r0 = r5.mPendingUpdates
            int r0 = r0.size()
            r1 = 0
        L7:
            if (r1 >= r0) goto L4a
            java.util.ArrayList<android.support.v7.widget.AdapterHelper$UpdateOp> r2 = r5.mPendingUpdates
            java.lang.Object r2 = r2.get(r1)
            android.support.v7.widget.AdapterHelper$UpdateOp r2 = (android.support.v7.widget.AdapterHelper.UpdateOp) r2
            int r3 = r2.cmd
            r4 = 8
            if (r3 == r4) goto L34
            switch(r3) {
                case 1: goto L2c;
                case 2: goto L1b;
                default: goto L1a;
            }
        L1a:
            goto L47
        L1b:
            int r3 = r2.positionStart
            if (r3 > r6) goto L47
            int r3 = r2.positionStart
            int r4 = r2.itemCount
            int r3 = r3 + r4
            if (r3 <= r6) goto L28
            r4 = -1
            return r4
        L28:
            int r4 = r2.itemCount
            int r6 = r6 - r4
            goto L47
        L2c:
            int r3 = r2.positionStart
            if (r3 > r6) goto L47
            int r3 = r2.itemCount
            int r6 = r6 + r3
            goto L47
        L34:
            int r3 = r2.positionStart
            if (r3 != r6) goto L3b
            int r6 = r2.itemCount
            goto L47
        L3b:
            int r3 = r2.positionStart
            if (r3 >= r6) goto L41
            int r6 = r6 + (-1)
        L41:
            int r3 = r2.itemCount
            if (r3 > r6) goto L47
            int r6 = r6 + 1
        L47:
            int r1 = r1 + 1
            goto L7
        L4a:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AdapterHelper.applyPendingUpdatesToPosition(int):int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasUpdates() {
        return !this.mPostponedList.isEmpty() && !this.mPendingUpdates.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class UpdateOp {
        static final int ADD = 1;
        static final int MOVE = 8;
        static final int POOL_SIZE = 30;
        static final int REMOVE = 2;
        static final int UPDATE = 4;
        int cmd;
        int itemCount;
        Object payload;
        int positionStart;

        UpdateOp(int cmd, int positionStart, int itemCount, Object payload) {
            this.cmd = cmd;
            this.positionStart = positionStart;
            this.itemCount = itemCount;
            this.payload = payload;
        }

        String cmdToString() {
            int i = this.cmd;
            if (i != 4) {
                if (i != 8) {
                    switch (i) {
                        case 1:
                            return "add";
                        case 2:
                            return "rm";
                        default:
                            return "??";
                    }
                }
                return "mv";
            }
            return "up";
        }

        public String toString() {
            return Integer.toHexString(System.identityHashCode(this)) + "[" + cmdToString() + ",s:" + this.positionStart + "c:" + this.itemCount + ",p:" + this.payload + "]";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            UpdateOp op = (UpdateOp) o;
            int i = this.cmd;
            if (i != op.cmd) {
                return false;
            }
            if (i == 8 && Math.abs(this.itemCount - this.positionStart) == 1 && this.itemCount == op.positionStart && this.positionStart == op.itemCount) {
                return true;
            }
            if (this.itemCount != op.itemCount || this.positionStart != op.positionStart) {
                return false;
            }
            Object obj = this.payload;
            if (obj != null) {
                if (!obj.equals(op.payload)) {
                    return false;
                }
            } else if (op.payload != null) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int result = this.cmd;
            return (((result * 31) + this.positionStart) * 31) + this.itemCount;
        }
    }

    @Override // android.support.v7.widget.OpReorderer.Callback
    public UpdateOp obtainUpdateOp(int cmd, int positionStart, int itemCount, Object payload) {
        UpdateOp op = this.mUpdateOpPool.acquire();
        if (op == null) {
            return new UpdateOp(cmd, positionStart, itemCount, payload);
        }
        op.cmd = cmd;
        op.positionStart = positionStart;
        op.itemCount = itemCount;
        op.payload = payload;
        return op;
    }

    @Override // android.support.v7.widget.OpReorderer.Callback
    public void recycleUpdateOp(UpdateOp op) {
        if (!this.mDisableRecycler) {
            op.payload = null;
            this.mUpdateOpPool.release(op);
        }
    }

    void recycleUpdateOpsAndClearList(List<UpdateOp> ops) {
        int count = ops.size();
        for (int i = 0; i < count; i++) {
            recycleUpdateOp(ops.get(i));
        }
        ops.clear();
    }
}
