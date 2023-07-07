package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DataFetcherGenerator;
import com.bumptech.glide.load.model.ModelLoader;
import java.io.File;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ResourceCacheGenerator implements DataFetcherGenerator, DataFetcher.DataCallback<Object> {
    private File cacheFile;

    /* renamed from: cb */
    private final DataFetcherGenerator.FetcherReadyCallback f53cb;
    private ResourceCacheKey currentKey;
    private final DecodeHelper<?> helper;
    private volatile ModelLoader.LoadData<?> loadData;
    private int modelLoaderIndex;
    private List<ModelLoader<File, ?>> modelLoaders;
    private int resourceClassIndex = -1;
    private int sourceIdIndex;
    private Key sourceKey;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResourceCacheGenerator(DecodeHelper<?> helper, DataFetcherGenerator.FetcherReadyCallback cb) {
        this.helper = helper;
        this.f53cb = cb;
    }

    @Override // com.bumptech.glide.load.engine.DataFetcherGenerator
    public boolean startNext() {
        List<Key> sourceIds = this.helper.getCacheKeys();
        boolean z = false;
        if (sourceIds.isEmpty()) {
            return false;
        }
        List<Class<?>> resourceClasses = this.helper.getRegisteredResourceClasses();
        if (resourceClasses.isEmpty() && File.class.equals(this.helper.getTranscodeClass())) {
            return false;
        }
        while (true) {
            if (this.modelLoaders == null || !hasNextModelLoader()) {
                this.resourceClassIndex++;
                if (this.resourceClassIndex >= resourceClasses.size()) {
                    this.sourceIdIndex++;
                    if (this.sourceIdIndex >= sourceIds.size()) {
                        return z;
                    }
                    int i = z ? 1 : 0;
                    int i2 = z ? 1 : 0;
                    int i3 = z ? 1 : 0;
                    this.resourceClassIndex = i;
                }
                Key sourceId = sourceIds.get(this.sourceIdIndex);
                Class<?> resourceClass = resourceClasses.get(this.resourceClassIndex);
                this.currentKey = new ResourceCacheKey(this.helper.getArrayPool(), sourceId, this.helper.getSignature(), this.helper.getWidth(), this.helper.getHeight(), this.helper.getTransformation(resourceClass), resourceClass, this.helper.getOptions());
                this.cacheFile = this.helper.getDiskCache().get(this.currentKey);
                File file = this.cacheFile;
                if (file == null) {
                    z = false;
                } else {
                    this.sourceKey = sourceId;
                    this.modelLoaders = this.helper.getModelLoaders(file);
                    z = false;
                    this.modelLoaderIndex = 0;
                }
            } else {
                this.loadData = null;
                boolean started = false;
                while (!started && hasNextModelLoader()) {
                    List<ModelLoader<File, ?>> list = this.modelLoaders;
                    int i4 = this.modelLoaderIndex;
                    this.modelLoaderIndex = i4 + 1;
                    ModelLoader<File, ?> modelLoader = list.get(i4);
                    this.loadData = modelLoader.buildLoadData(this.cacheFile, this.helper.getWidth(), this.helper.getHeight(), this.helper.getOptions());
                    if (this.loadData != null && this.helper.hasLoadPath(this.loadData.fetcher.getDataClass())) {
                        started = true;
                        this.loadData.fetcher.loadData(this.helper.getPriority(), this);
                    }
                }
                return started;
            }
        }
    }

    private boolean hasNextModelLoader() {
        return this.modelLoaderIndex < this.modelLoaders.size();
    }

    @Override // com.bumptech.glide.load.engine.DataFetcherGenerator
    public void cancel() {
        ModelLoader.LoadData<?> local = this.loadData;
        if (local != null) {
            local.fetcher.cancel();
        }
    }

    @Override // com.bumptech.glide.load.data.DataFetcher.DataCallback
    public void onDataReady(Object data) {
        this.f53cb.onDataFetcherReady(this.sourceKey, data, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE, this.currentKey);
    }

    @Override // com.bumptech.glide.load.data.DataFetcher.DataCallback
    public void onLoadFailed(@NonNull Exception e) {
        this.f53cb.onDataFetcherFailed(this.currentKey, e, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE);
    }
}