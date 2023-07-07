package com.bumptech.glide;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.AbsListView;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.util.List;
import java.util.Queue;

/* loaded from: classes.dex */
public class ListPreloader<T> implements AbsListView.OnScrollListener {
    private int lastEnd;
    private int lastStart;
    private final int maxPreload;
    private final PreloadSizeProvider<T> preloadDimensionProvider;
    private final PreloadModelProvider<T> preloadModelProvider;
    private final PreloadTargetQueue preloadTargetQueue;
    private final RequestManager requestManager;
    private int totalItemCount;
    private int lastFirstVisible = -1;
    private boolean isIncreasing = true;

    /* loaded from: classes.dex */
    public interface PreloadModelProvider<U> {
        @NonNull
        List<U> getPreloadItems(int i);

        @Nullable
        RequestBuilder<?> getPreloadRequestBuilder(@NonNull U u);
    }

    /* loaded from: classes.dex */
    public interface PreloadSizeProvider<T> {
        @Nullable
        int[] getPreloadSize(@NonNull T t, int i, int i2);
    }

    public ListPreloader(@NonNull RequestManager requestManager, @NonNull PreloadModelProvider<T> preloadModelProvider, @NonNull PreloadSizeProvider<T> preloadDimensionProvider, int maxPreload) {
        this.requestManager = requestManager;
        this.preloadModelProvider = preloadModelProvider;
        this.preloadDimensionProvider = preloadDimensionProvider;
        this.maxPreload = maxPreload;
        this.preloadTargetQueue = new PreloadTargetQueue(maxPreload + 1);
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView absListView, int firstVisible, int visibleCount, int totalCount) {
        this.totalItemCount = totalCount;
        int i = this.lastFirstVisible;
        if (firstVisible > i) {
            preload(firstVisible + visibleCount, true);
        } else if (firstVisible < i) {
            preload(firstVisible, false);
        }
        this.lastFirstVisible = firstVisible;
    }

    private void preload(int start, boolean increasing) {
        if (this.isIncreasing != increasing) {
            this.isIncreasing = increasing;
            cancelAll();
        }
        preload(start, (increasing ? this.maxPreload : -this.maxPreload) + start);
    }

    private void preload(int from, int to) {
        int start;
        int end;
        if (from < to) {
            start = Math.max(this.lastEnd, from);
            end = to;
        } else {
            start = to;
            end = Math.min(this.lastStart, from);
        }
        int end2 = Math.min(this.totalItemCount, end);
        int start2 = Math.min(this.totalItemCount, Math.max(0, start));
        if (from < to) {
            for (int i = start2; i < end2; i++) {
                preloadAdapterPosition(this.preloadModelProvider.getPreloadItems(i), i, true);
            }
        } else {
            for (int i2 = end2 - 1; i2 >= start2; i2--) {
                preloadAdapterPosition(this.preloadModelProvider.getPreloadItems(i2), i2, false);
            }
        }
        this.lastStart = start2;
        this.lastEnd = end2;
    }

    private void preloadAdapterPosition(List<T> items, int position, boolean isIncreasing) {
        int numItems = items.size();
        if (isIncreasing) {
            for (int i = 0; i < numItems; i++) {
                preloadItem(items.get(i), position, i);
            }
            return;
        }
        for (int i2 = numItems - 1; i2 >= 0; i2--) {
            preloadItem(items.get(i2), position, i2);
        }
    }

    private void preloadItem(@Nullable T item, int position, int perItemPosition) {
        int[] dimensions;
        RequestBuilder<Object> preloadRequestBuilder;
        if (item == null || (dimensions = this.preloadDimensionProvider.getPreloadSize(item, position, perItemPosition)) == null || (preloadRequestBuilder = this.preloadModelProvider.getPreloadRequestBuilder(item)) == null) {
            return;
        }
        preloadRequestBuilder.into((RequestBuilder<Object>) this.preloadTargetQueue.next(dimensions[0], dimensions[1]));
    }

    private void cancelAll() {
        for (int i = 0; i < this.maxPreload; i++) {
            this.requestManager.clear(this.preloadTargetQueue.next(0, 0));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class PreloadTargetQueue {
        private final Queue<PreloadTarget> queue;

        PreloadTargetQueue(int size) {
            this.queue = Util.createQueue(size);
            for (int i = 0; i < size; i++) {
                this.queue.offer(new PreloadTarget());
            }
        }

        public PreloadTarget next(int width, int height) {
            PreloadTarget result = this.queue.poll();
            this.queue.offer(result);
            result.photoWidth = width;
            result.photoHeight = height;
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class PreloadTarget extends BaseTarget<Object> {
        int photoHeight;
        int photoWidth;

        PreloadTarget() {
        }

        @Override // com.bumptech.glide.request.target.Target
        public void onResourceReady(@NonNull Object resource, @Nullable Transition<? super Object> transition) {
        }

        @Override // com.bumptech.glide.request.target.Target
        public void getSize(@NonNull SizeReadyCallback cb) {
            cb.onSizeReady(this.photoWidth, this.photoHeight);
        }

        @Override // com.bumptech.glide.request.target.Target
        public void removeCallback(@NonNull SizeReadyCallback cb) {
        }
    }
}