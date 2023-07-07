package android.support.p003v7.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.p003v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* renamed from: android.support.v7.util.DiffUtil */
/* loaded from: classes.dex */
public class DiffUtil {
    private static final Comparator<Snake> SNAKE_COMPARATOR = new Comparator<Snake>() { // from class: android.support.v7.util.DiffUtil.1
        @Override // java.util.Comparator
        public int compare(Snake o1, Snake o2) {
            int cmpX = o1.f30x - o2.f30x;
            return cmpX == 0 ? o1.f31y - o2.f31y : cmpX;
        }
    };

    private DiffUtil() {
    }

    @NonNull
    public static DiffResult calculateDiff(@NonNull Callback cb) {
        return calculateDiff(cb, true);
    }

    @NonNull
    public static DiffResult calculateDiff(@NonNull Callback cb, boolean detectMoves) {
        int oldSize = cb.getOldListSize();
        int newSize = cb.getNewListSize();
        List<Snake> snakes = new ArrayList<>();
        List<Range> stack = new ArrayList<>();
        stack.add(new Range(0, oldSize, 0, newSize));
        int max = oldSize + newSize + Math.abs(oldSize - newSize);
        int[] forward = new int[max * 2];
        int[] backward = new int[max * 2];
        List<Range> rangePool = new ArrayList<>();
        while (!stack.isEmpty()) {
            Range range = stack.remove(stack.size() - 1);
            Snake snake = diffPartial(cb, range.oldListStart, range.oldListEnd, range.newListStart, range.newListEnd, forward, backward, max);
            if (snake != null) {
                if (snake.size > 0) {
                    snakes.add(snake);
                }
                snake.f30x += range.oldListStart;
                snake.f31y += range.newListStart;
                Range left = rangePool.isEmpty() ? new Range() : rangePool.remove(rangePool.size() - 1);
                left.oldListStart = range.oldListStart;
                left.newListStart = range.newListStart;
                if (snake.reverse) {
                    left.oldListEnd = snake.f30x;
                    left.newListEnd = snake.f31y;
                } else if (snake.removal) {
                    left.oldListEnd = snake.f30x - 1;
                    left.newListEnd = snake.f31y;
                } else {
                    left.oldListEnd = snake.f30x;
                    left.newListEnd = snake.f31y - 1;
                }
                stack.add(left);
                if (!snake.reverse) {
                    range.oldListStart = snake.f30x + snake.size;
                    range.newListStart = snake.f31y + snake.size;
                } else if (snake.removal) {
                    range.oldListStart = snake.f30x + snake.size + 1;
                    range.newListStart = snake.f31y + snake.size;
                } else {
                    range.oldListStart = snake.f30x + snake.size;
                    range.newListStart = snake.f31y + snake.size + 1;
                }
                stack.add(range);
            } else {
                rangePool.add(range);
            }
        }
        Collections.sort(snakes, SNAKE_COMPARATOR);
        return new DiffResult(cb, snakes, forward, backward, detectMoves);
    }

    private static Snake diffPartial(Callback cb, int startOld, int endOld, int startNew, int endNew, int[] forward, int[] backward, int kOffset) {
        int x;
        boolean removal;
        int oldSize;
        int x2;
        boolean removal2;
        int oldSize2 = endOld - startOld;
        int newSize = endNew - startNew;
        if (endOld - startOld >= 1 && endNew - startNew >= 1) {
            int delta = oldSize2 - newSize;
            int dLimit = ((oldSize2 + newSize) + 1) / 2;
            Arrays.fill(forward, (kOffset - dLimit) - 1, kOffset + dLimit + 1, 0);
            Arrays.fill(backward, ((kOffset - dLimit) - 1) + delta, kOffset + dLimit + 1 + delta, oldSize2);
            boolean checkInFwd = delta % 2 != 0;
            for (int d = 0; d <= dLimit; d++) {
                for (int k = -d; k <= d; k += 2) {
                    if (k == (-d) || (k != d && forward[(kOffset + k) - 1] < forward[kOffset + k + 1])) {
                        int x3 = kOffset + k;
                        x2 = forward[x3 + 1];
                        removal2 = false;
                    } else {
                        x2 = forward[(kOffset + k) - 1] + 1;
                        removal2 = true;
                    }
                    for (int y = x2 - k; x2 < oldSize2 && y < newSize && cb.areItemsTheSame(startOld + x2, startNew + y); y++) {
                        x2++;
                    }
                    forward[kOffset + k] = x2;
                    if (checkInFwd && k >= (delta - d) + 1 && k <= (delta + d) - 1 && forward[kOffset + k] >= backward[kOffset + k]) {
                        Snake outSnake = new Snake();
                        outSnake.f30x = backward[kOffset + k];
                        outSnake.f31y = outSnake.f30x - k;
                        outSnake.size = forward[kOffset + k] - backward[kOffset + k];
                        outSnake.removal = removal2;
                        outSnake.reverse = false;
                        return outSnake;
                    }
                }
                int k2 = -d;
                while (k2 <= d) {
                    int backwardK = k2 + delta;
                    if (backwardK == d + delta || (backwardK != (-d) + delta && backward[(kOffset + backwardK) - 1] < backward[kOffset + backwardK + 1])) {
                        int x4 = kOffset + backwardK;
                        x = backward[x4 - 1];
                        removal = false;
                    } else {
                        x = backward[(kOffset + backwardK) + 1] - 1;
                        removal = true;
                    }
                    int y2 = x - backwardK;
                    while (x > 0 && y2 > 0) {
                        oldSize = oldSize2;
                        if (!cb.areItemsTheSame((startOld + x) - 1, (startNew + y2) - 1)) {
                            break;
                        }
                        x--;
                        y2--;
                        oldSize2 = oldSize;
                    }
                    oldSize = oldSize2;
                    backward[kOffset + backwardK] = x;
                    if (checkInFwd || k2 + delta < (-d) || k2 + delta > d || forward[kOffset + backwardK] < backward[kOffset + backwardK]) {
                        k2 += 2;
                        oldSize2 = oldSize;
                    } else {
                        Snake outSnake2 = new Snake();
                        outSnake2.f30x = backward[kOffset + backwardK];
                        outSnake2.f31y = outSnake2.f30x - backwardK;
                        outSnake2.size = forward[kOffset + backwardK] - backward[kOffset + backwardK];
                        outSnake2.removal = removal;
                        outSnake2.reverse = true;
                        return outSnake2;
                    }
                }
            }
            throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
        }
        return null;
    }

    /* renamed from: android.support.v7.util.DiffUtil$Callback */
    /* loaded from: classes.dex */
    public static abstract class Callback {
        public abstract boolean areContentsTheSame(int i, int i2);

        public abstract boolean areItemsTheSame(int i, int i2);

        public abstract int getNewListSize();

        public abstract int getOldListSize();

        @Nullable
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return null;
        }
    }

    /* renamed from: android.support.v7.util.DiffUtil$ItemCallback */
    /* loaded from: classes.dex */
    public static abstract class ItemCallback<T> {
        public abstract boolean areContentsTheSame(@NonNull T t, @NonNull T t2);

        public abstract boolean areItemsTheSame(@NonNull T t, @NonNull T t2);

        @Nullable
        public Object getChangePayload(@NonNull T oldItem, @NonNull T newItem) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.support.v7.util.DiffUtil$Snake */
    /* loaded from: classes.dex */
    public static class Snake {
        boolean removal;
        boolean reverse;
        int size;

        /* renamed from: x */
        int f30x;

        /* renamed from: y */
        int f31y;

        Snake() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.support.v7.util.DiffUtil$Range */
    /* loaded from: classes.dex */
    public static class Range {
        int newListEnd;
        int newListStart;
        int oldListEnd;
        int oldListStart;

        public Range() {
        }

        public Range(int oldListStart, int oldListEnd, int newListStart, int newListEnd) {
            this.oldListStart = oldListStart;
            this.oldListEnd = oldListEnd;
            this.newListStart = newListStart;
            this.newListEnd = newListEnd;
        }
    }

    /* renamed from: android.support.v7.util.DiffUtil$DiffResult */
    /* loaded from: classes.dex */
    public static class DiffResult {
        private static final int FLAG_CHANGED = 2;
        private static final int FLAG_IGNORE = 16;
        private static final int FLAG_MASK = 31;
        private static final int FLAG_MOVED_CHANGED = 4;
        private static final int FLAG_MOVED_NOT_CHANGED = 8;
        private static final int FLAG_NOT_CHANGED = 1;
        private static final int FLAG_OFFSET = 5;
        public static final int NO_POSITION = -1;
        private final Callback mCallback;
        private final boolean mDetectMoves;
        private final int[] mNewItemStatuses;
        private final int mNewListSize;
        private final int[] mOldItemStatuses;
        private final int mOldListSize;
        private final List<Snake> mSnakes;

        DiffResult(Callback callback, List<Snake> snakes, int[] oldItemStatuses, int[] newItemStatuses, boolean detectMoves) {
            this.mSnakes = snakes;
            this.mOldItemStatuses = oldItemStatuses;
            this.mNewItemStatuses = newItemStatuses;
            Arrays.fill(this.mOldItemStatuses, 0);
            Arrays.fill(this.mNewItemStatuses, 0);
            this.mCallback = callback;
            this.mOldListSize = callback.getOldListSize();
            this.mNewListSize = callback.getNewListSize();
            this.mDetectMoves = detectMoves;
            addRootSnake();
            findMatchingItems();
        }

        private void addRootSnake() {
            Snake firstSnake = this.mSnakes.isEmpty() ? null : this.mSnakes.get(0);
            if (firstSnake == null || firstSnake.f30x != 0 || firstSnake.f31y != 0) {
                Snake root = new Snake();
                root.f30x = 0;
                root.f31y = 0;
                root.removal = false;
                root.size = 0;
                root.reverse = false;
                this.mSnakes.add(0, root);
            }
        }

        private void findMatchingItems() {
            int posOld = this.mOldListSize;
            int posNew = this.mNewListSize;
            for (int i = this.mSnakes.size() - 1; i >= 0; i--) {
                Snake snake = this.mSnakes.get(i);
                int endX = snake.f30x + snake.size;
                int endY = snake.f31y + snake.size;
                if (this.mDetectMoves) {
                    while (posOld > endX) {
                        findAddition(posOld, posNew, i);
                        posOld--;
                    }
                    while (posNew > endY) {
                        findRemoval(posOld, posNew, i);
                        posNew--;
                    }
                }
                for (int j = 0; j < snake.size; j++) {
                    int oldItemPos = snake.f30x + j;
                    int newItemPos = snake.f31y + j;
                    boolean theSame = this.mCallback.areContentsTheSame(oldItemPos, newItemPos);
                    int changeFlag = theSame ? 1 : 2;
                    this.mOldItemStatuses[oldItemPos] = (newItemPos << 5) | changeFlag;
                    this.mNewItemStatuses[newItemPos] = (oldItemPos << 5) | changeFlag;
                }
                posOld = snake.f30x;
                posNew = snake.f31y;
            }
        }

        private void findAddition(int x, int y, int snakeIndex) {
            if (this.mOldItemStatuses[x - 1] != 0) {
                return;
            }
            findMatchingItem(x, y, snakeIndex, false);
        }

        private void findRemoval(int x, int y, int snakeIndex) {
            if (this.mNewItemStatuses[y - 1] != 0) {
                return;
            }
            findMatchingItem(x, y, snakeIndex, true);
        }

        public int convertOldPositionToNew(@IntRange(from = 0) int oldListPosition) {
            if (oldListPosition >= 0) {
                int[] iArr = this.mOldItemStatuses;
                if (oldListPosition < iArr.length) {
                    int status = iArr[oldListPosition];
                    if ((status & 31) == 0) {
                        return -1;
                    }
                    return status >> 5;
                }
            }
            throw new IndexOutOfBoundsException("Index out of bounds - passed position = " + oldListPosition + ", old list size = " + this.mOldItemStatuses.length);
        }

        public int convertNewPositionToOld(@IntRange(from = 0) int newListPosition) {
            if (newListPosition >= 0) {
                int[] iArr = this.mNewItemStatuses;
                if (newListPosition < iArr.length) {
                    int status = iArr[newListPosition];
                    if ((status & 31) == 0) {
                        return -1;
                    }
                    return status >> 5;
                }
            }
            throw new IndexOutOfBoundsException("Index out of bounds - passed position = " + newListPosition + ", new list size = " + this.mNewItemStatuses.length);
        }

        private boolean findMatchingItem(int x, int y, int snakeIndex, boolean removal) {
            int myItemPos;
            int curX;
            int curY;
            if (removal) {
                myItemPos = y - 1;
                curX = x;
                curY = y - 1;
            } else {
                myItemPos = x - 1;
                curX = x - 1;
                curY = y;
            }
            for (int i = snakeIndex; i >= 0; i--) {
                Snake snake = this.mSnakes.get(i);
                int endX = snake.f30x + snake.size;
                int endY = snake.f31y + snake.size;
                int changeFlag = 8;
                if (removal) {
                    for (int pos = curX - 1; pos >= endX; pos--) {
                        if (this.mCallback.areItemsTheSame(pos, myItemPos)) {
                            boolean theSame = this.mCallback.areContentsTheSame(pos, myItemPos);
                            if (!theSame) {
                                changeFlag = 4;
                            }
                            this.mNewItemStatuses[myItemPos] = (pos << 5) | 16;
                            this.mOldItemStatuses[pos] = (myItemPos << 5) | changeFlag;
                            return true;
                        }
                    }
                    continue;
                } else {
                    for (int pos2 = curY - 1; pos2 >= endY; pos2--) {
                        if (this.mCallback.areItemsTheSame(myItemPos, pos2)) {
                            boolean theSame2 = this.mCallback.areContentsTheSame(myItemPos, pos2);
                            if (!theSame2) {
                                changeFlag = 4;
                            }
                            this.mOldItemStatuses[x - 1] = (pos2 << 5) | 16;
                            this.mNewItemStatuses[pos2] = ((x - 1) << 5) | changeFlag;
                            return true;
                        }
                    }
                    continue;
                }
                curX = snake.f30x;
                curY = snake.f31y;
            }
            return false;
        }

        public void dispatchUpdatesTo(@NonNull RecyclerView.Adapter adapter) {
            dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
        }

        public void dispatchUpdatesTo(@NonNull ListUpdateCallback updateCallback) {
            BatchingListUpdateCallback batchingCallback;
            int endY;
            int snakeSize;
            if (updateCallback instanceof BatchingListUpdateCallback) {
                batchingCallback = (BatchingListUpdateCallback) updateCallback;
            } else {
                BatchingListUpdateCallback batchingCallback2 = new BatchingListUpdateCallback(updateCallback);
                batchingCallback = batchingCallback2;
            }
            List<PostponedUpdate> postponedUpdates = new ArrayList<>();
            int posOld = this.mOldListSize;
            int posNew = this.mNewListSize;
            int posOld2 = posOld;
            int posNew2 = posNew;
            for (int snakeIndex = this.mSnakes.size() - 1; snakeIndex >= 0; snakeIndex--) {
                Snake snake = this.mSnakes.get(snakeIndex);
                int snakeSize2 = snake.size;
                int endX = snake.f30x + snakeSize2;
                int endY2 = snake.f31y + snakeSize2;
                if (endX >= posOld2) {
                    endY = endY2;
                } else {
                    endY = endY2;
                    dispatchRemovals(postponedUpdates, batchingCallback, endX, posOld2 - endX, endX);
                }
                if (endY < posNew2) {
                    snakeSize = snakeSize2;
                    dispatchAdditions(postponedUpdates, batchingCallback, endX, posNew2 - endY, endY);
                } else {
                    snakeSize = snakeSize2;
                }
                for (int i = snakeSize - 1; i >= 0; i--) {
                    if ((this.mOldItemStatuses[snake.f30x + i] & 31) == 2) {
                        batchingCallback.onChanged(snake.f30x + i, 1, this.mCallback.getChangePayload(snake.f30x + i, snake.f31y + i));
                    }
                }
                posOld2 = snake.f30x;
                posNew2 = snake.f31y;
            }
            batchingCallback.dispatchLastEvent();
        }

        private static PostponedUpdate removePostponedUpdate(List<PostponedUpdate> updates, int pos, boolean removal) {
            for (int i = updates.size() - 1; i >= 0; i--) {
                PostponedUpdate update = updates.get(i);
                if (update.posInOwnerList == pos && update.removal == removal) {
                    updates.remove(i);
                    for (int j = i; j < updates.size(); j++) {
                        updates.get(j).currentPos += removal ? 1 : -1;
                    }
                    return update;
                }
            }
            return null;
        }

        private void dispatchAdditions(List<PostponedUpdate> postponedUpdates, ListUpdateCallback updateCallback, int start, int count, int globalIndex) {
            if (!this.mDetectMoves) {
                updateCallback.onInserted(start, count);
                return;
            }
            for (int i = count - 1; i >= 0; i--) {
                int status = this.mNewItemStatuses[globalIndex + i] & 31;
                if (status == 0) {
                    updateCallback.onInserted(start, 1);
                    for (PostponedUpdate update : postponedUpdates) {
                        update.currentPos++;
                    }
                } else if (status == 4 || status == 8) {
                    int pos = this.mNewItemStatuses[globalIndex + i] >> 5;
                    updateCallback.onMoved(removePostponedUpdate(postponedUpdates, pos, true).currentPos, start);
                    if (status == 4) {
                        updateCallback.onChanged(start, 1, this.mCallback.getChangePayload(pos, globalIndex + i));
                    }
                } else if (status == 16) {
                    postponedUpdates.add(new PostponedUpdate(globalIndex + i, start, false));
                } else {
                    throw new IllegalStateException("unknown flag for pos " + (globalIndex + i) + " " + Long.toBinaryString(status));
                }
            }
        }

        private void dispatchRemovals(List<PostponedUpdate> postponedUpdates, ListUpdateCallback updateCallback, int start, int count, int globalIndex) {
            if (!this.mDetectMoves) {
                updateCallback.onRemoved(start, count);
                return;
            }
            for (int i = count - 1; i >= 0; i--) {
                int status = this.mOldItemStatuses[globalIndex + i] & 31;
                if (status == 0) {
                    updateCallback.onRemoved(start + i, 1);
                    for (PostponedUpdate update : postponedUpdates) {
                        update.currentPos--;
                    }
                } else if (status == 4 || status == 8) {
                    int pos = this.mOldItemStatuses[globalIndex + i] >> 5;
                    PostponedUpdate update2 = removePostponedUpdate(postponedUpdates, pos, false);
                    updateCallback.onMoved(start + i, update2.currentPos - 1);
                    if (status == 4) {
                        updateCallback.onChanged(update2.currentPos - 1, 1, this.mCallback.getChangePayload(globalIndex + i, pos));
                    }
                } else if (status == 16) {
                    postponedUpdates.add(new PostponedUpdate(globalIndex + i, start + i, true));
                } else {
                    throw new IllegalStateException("unknown flag for pos " + (globalIndex + i) + " " + Long.toBinaryString(status));
                }
            }
        }

        @VisibleForTesting
        List<Snake> getSnakes() {
            return this.mSnakes;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.support.v7.util.DiffUtil$PostponedUpdate */
    /* loaded from: classes.dex */
    public static class PostponedUpdate {
        int currentPos;
        int posInOwnerList;
        boolean removal;

        public PostponedUpdate(int posInOwnerList, int currentPos, boolean removal) {
            this.posInOwnerList = posInOwnerList;
            this.currentPos = currentPos;
            this.removal = removal;
        }
    }
}