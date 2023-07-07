package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
class MultiModelLoader<Model, Data> implements ModelLoader<Model, Data> {
    private final Pools.Pool<List<Throwable>> exceptionListPool;
    private final List<ModelLoader<Model, Data>> modelLoaders;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MultiModelLoader(@NonNull List<ModelLoader<Model, Data>> modelLoaders, @NonNull Pools.Pool<List<Throwable>> exceptionListPool) {
        this.modelLoaders = modelLoaders;
        this.exceptionListPool = exceptionListPool;
    }

    @Override // com.bumptech.glide.load.model.ModelLoader
    public ModelLoader.LoadData<Data> buildLoadData(@NonNull Model model, int width, int height, @NonNull Options options) {
        ModelLoader.LoadData<Data> loadData;
        Key sourceKey = null;
        int size = this.modelLoaders.size();
        List<DataFetcher<Data>> fetchers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ModelLoader<Model, Data> modelLoader = this.modelLoaders.get(i);
            if (modelLoader.handles(model) && (loadData = modelLoader.buildLoadData(model, width, height, options)) != null) {
                sourceKey = loadData.sourceKey;
                fetchers.add(loadData.fetcher);
            }
        }
        if (fetchers.isEmpty() || sourceKey == null) {
            return null;
        }
        return new ModelLoader.LoadData<>(sourceKey, new MultiFetcher(fetchers, this.exceptionListPool));
    }

    @Override // com.bumptech.glide.load.model.ModelLoader
    public boolean handles(@NonNull Model model) {
        for (ModelLoader<Model, Data> modelLoader : this.modelLoaders) {
            if (modelLoader.handles(model)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "MultiModelLoader{modelLoaders=" + Arrays.toString(this.modelLoaders.toArray()) + '}';
    }

    /* loaded from: classes.dex */
    static class MultiFetcher<Data> implements DataFetcher<Data>, DataFetcher.DataCallback<Data> {
        private DataFetcher.DataCallback<? super Data> callback;
        private int currentIndex = 0;
        @Nullable
        private List<Throwable> exceptions;
        private final List<DataFetcher<Data>> fetchers;
        private Priority priority;
        private final Pools.Pool<List<Throwable>> throwableListPool;

        MultiFetcher(@NonNull List<DataFetcher<Data>> fetchers, @NonNull Pools.Pool<List<Throwable>> throwableListPool) {
            this.throwableListPool = throwableListPool;
            Preconditions.checkNotEmpty(fetchers);
            this.fetchers = fetchers;
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public void loadData(@NonNull Priority priority, @NonNull DataFetcher.DataCallback<? super Data> callback) {
            this.priority = priority;
            this.callback = callback;
            this.exceptions = this.throwableListPool.acquire();
            this.fetchers.get(this.currentIndex).loadData(priority, this);
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public void cleanup() {
            List<Throwable> list = this.exceptions;
            if (list != null) {
                this.throwableListPool.release(list);
            }
            this.exceptions = null;
            for (DataFetcher<Data> fetcher : this.fetchers) {
                fetcher.cleanup();
            }
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public void cancel() {
            for (DataFetcher<Data> fetcher : this.fetchers) {
                fetcher.cancel();
            }
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        @NonNull
        public Class<Data> getDataClass() {
            return this.fetchers.get(0).getDataClass();
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        @NonNull
        public DataSource getDataSource() {
            return this.fetchers.get(0).getDataSource();
        }

        @Override // com.bumptech.glide.load.data.DataFetcher.DataCallback
        public void onDataReady(@Nullable Data data) {
            if (data != null) {
                this.callback.onDataReady(data);
            } else {
                startNextOrFail();
            }
        }

        @Override // com.bumptech.glide.load.data.DataFetcher.DataCallback
        public void onLoadFailed(@NonNull Exception e) {
            ((List) Preconditions.checkNotNull(this.exceptions)).add(e);
            startNextOrFail();
        }

        private void startNextOrFail() {
            if (this.currentIndex < this.fetchers.size() - 1) {
                this.currentIndex++;
                loadData(this.priority, this.callback);
                return;
            }
            Preconditions.checkNotNull(this.exceptions);
            this.callback.onLoadFailed(new GlideException("Fetch failed", new ArrayList(this.exceptions)));
        }
    }
}
