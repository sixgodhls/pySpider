package com.bumptech.glide.load.engine;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.Pools;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.engine.DataFetcherGenerator;
import com.bumptech.glide.load.engine.DecodePath;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.GlideTrace;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
class DecodeJob<R> implements DataFetcherGenerator.FetcherReadyCallback, Runnable, Comparable<DecodeJob<?>>, FactoryPools.Poolable {
    private static final String TAG = "DecodeJob";
    private Callback<R> callback;
    private Key currentAttemptingKey;
    private Object currentData;
    private DataSource currentDataSource;
    private DataFetcher<?> currentFetcher;
    private volatile DataFetcherGenerator currentGenerator;
    private Key currentSourceKey;
    private Thread currentThread;
    private final DiskCacheProvider diskCacheProvider;
    private DiskCacheStrategy diskCacheStrategy;
    private GlideContext glideContext;
    private int height;
    private volatile boolean isCallbackNotified;
    private volatile boolean isCancelled;
    private EngineKey loadKey;
    private Object model;
    private boolean onlyRetrieveFromCache;
    private Options options;
    private int order;
    private final Pools.Pool<DecodeJob<?>> pool;
    private Priority priority;
    private RunReason runReason;
    private Key signature;
    private Stage stage;
    private long startFetchTime;
    private int width;
    private final DecodeHelper<R> decodeHelper = new DecodeHelper<>();
    private final List<Throwable> throwables = new ArrayList();
    private final StateVerifier stateVerifier = StateVerifier.newInstance();
    private final DeferredEncodeManager<?> deferredEncodeManager = new DeferredEncodeManager<>();
    private final ReleaseManager releaseManager = new ReleaseManager();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Callback<R> {
        void onLoadFailed(GlideException glideException);

        void onResourceReady(Resource<R> resource, DataSource dataSource);

        void reschedule(DecodeJob<?> decodeJob);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface DiskCacheProvider {
        DiskCache getDiskCache();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum RunReason {
        INITIALIZE,
        SWITCH_TO_SOURCE_SERVICE,
        DECODE_DATA
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Stage {
        INITIALIZE,
        RESOURCE_CACHE,
        DATA_CACHE,
        SOURCE,
        ENCODE,
        FINISHED
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DecodeJob(DiskCacheProvider diskCacheProvider, Pools.Pool<DecodeJob<?>> pool) {
        this.diskCacheProvider = diskCacheProvider;
        this.pool = pool;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DecodeJob<R> init(GlideContext glideContext, Object model, EngineKey loadKey, Key signature, int width, int height, Class<?> resourceClass, Class<R> transcodeClass, Priority priority, DiskCacheStrategy diskCacheStrategy, Map<Class<?>, Transformation<?>> transformations, boolean isTransformationRequired, boolean isScaleOnlyOrNoTransform, boolean onlyRetrieveFromCache, Options options, Callback<R> callback, int order) {
        this.decodeHelper.init(glideContext, model, signature, width, height, diskCacheStrategy, resourceClass, transcodeClass, priority, options, transformations, isTransformationRequired, isScaleOnlyOrNoTransform, this.diskCacheProvider);
        this.glideContext = glideContext;
        this.signature = signature;
        this.priority = priority;
        this.loadKey = loadKey;
        this.width = width;
        this.height = height;
        this.diskCacheStrategy = diskCacheStrategy;
        this.onlyRetrieveFromCache = onlyRetrieveFromCache;
        this.options = options;
        this.callback = callback;
        this.order = order;
        this.runReason = RunReason.INITIALIZE;
        this.model = model;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean willDecodeFromCache() {
        Stage firstStage = getNextStage(Stage.INITIALIZE);
        return firstStage == Stage.RESOURCE_CACHE || firstStage == Stage.DATA_CACHE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void release(boolean isRemovedFromQueue) {
        if (this.releaseManager.release(isRemovedFromQueue)) {
            releaseInternal();
        }
    }

    private void onEncodeComplete() {
        if (this.releaseManager.onEncodeComplete()) {
            releaseInternal();
        }
    }

    private void onLoadFailed() {
        if (this.releaseManager.onFailed()) {
            releaseInternal();
        }
    }

    private void releaseInternal() {
        this.releaseManager.reset();
        this.deferredEncodeManager.clear();
        this.decodeHelper.clear();
        this.isCallbackNotified = false;
        this.glideContext = null;
        this.signature = null;
        this.options = null;
        this.priority = null;
        this.loadKey = null;
        this.callback = null;
        this.stage = null;
        this.currentGenerator = null;
        this.currentThread = null;
        this.currentSourceKey = null;
        this.currentData = null;
        this.currentDataSource = null;
        this.currentFetcher = null;
        this.startFetchTime = 0L;
        this.isCancelled = false;
        this.model = null;
        this.throwables.clear();
        this.pool.release(this);
    }

    @Override // java.lang.Comparable
    public int compareTo(@NonNull DecodeJob<?> other) {
        int result = getPriority() - other.getPriority();
        if (result == 0) {
            return this.order - other.order;
        }
        return result;
    }

    private int getPriority() {
        return this.priority.ordinal();
    }

    public void cancel() {
        this.isCancelled = true;
        DataFetcherGenerator local = this.currentGenerator;
        if (local != null) {
            local.cancel();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x001c, code lost:
        if (r0 != null) goto L13;
     */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void run() {
        /*
            r5 = this;
            java.lang.String r0 = "DecodeJob#run(model=%s)"
            java.lang.Object r1 = r5.model
            com.bumptech.glide.util.pool.GlideTrace.beginSectionFormat(r0, r1)
            com.bumptech.glide.load.data.DataFetcher<?> r0 = r5.currentFetcher
            boolean r1 = r5.isCancelled     // Catch: java.lang.Throwable -> L27
            if (r1 == 0) goto L19
            r5.notifyFailed()     // Catch: java.lang.Throwable -> L27
            if (r0 == 0) goto L15
            r0.cleanup()
        L15:
            com.bumptech.glide.util.pool.GlideTrace.endSection()
            return
        L19:
            r5.runWrapped()     // Catch: java.lang.Throwable -> L27
            if (r0 == 0) goto L21
        L1e:
            r0.cleanup()
        L21:
            com.bumptech.glide.util.pool.GlideTrace.endSection()
            goto L68
        L25:
            r1 = move-exception
            goto L6b
        L27:
            r1 = move-exception
            java.lang.String r2 = "DecodeJob"
            r3 = 3
            boolean r2 = android.util.Log.isLoggable(r2, r3)     // Catch: java.lang.Throwable -> L25
            if (r2 == 0) goto L53
            java.lang.String r2 = "DecodeJob"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L25
            r3.<init>()     // Catch: java.lang.Throwable -> L25
            java.lang.String r4 = "DecodeJob threw unexpectedly, isCancelled: "
            r3.append(r4)     // Catch: java.lang.Throwable -> L25
            boolean r4 = r5.isCancelled     // Catch: java.lang.Throwable -> L25
            r3.append(r4)     // Catch: java.lang.Throwable -> L25
            java.lang.String r4 = ", stage: "
            r3.append(r4)     // Catch: java.lang.Throwable -> L25
            com.bumptech.glide.load.engine.DecodeJob$Stage r4 = r5.stage     // Catch: java.lang.Throwable -> L25
            r3.append(r4)     // Catch: java.lang.Throwable -> L25
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L25
            android.util.Log.d(r2, r3, r1)     // Catch: java.lang.Throwable -> L25
        L53:
            com.bumptech.glide.load.engine.DecodeJob$Stage r2 = r5.stage     // Catch: java.lang.Throwable -> L25
            com.bumptech.glide.load.engine.DecodeJob$Stage r3 = com.bumptech.glide.load.engine.DecodeJob.Stage.ENCODE     // Catch: java.lang.Throwable -> L25
            if (r2 == r3) goto L61
            java.util.List<java.lang.Throwable> r2 = r5.throwables     // Catch: java.lang.Throwable -> L25
            r2.add(r1)     // Catch: java.lang.Throwable -> L25
            r5.notifyFailed()     // Catch: java.lang.Throwable -> L25
        L61:
            boolean r2 = r5.isCancelled     // Catch: java.lang.Throwable -> L25
            if (r2 == 0) goto L69
            if (r0 == 0) goto L21
            goto L1e
        L68:
            return
        L69:
            throw r1     // Catch: java.lang.Throwable -> L25
        L6b:
            if (r0 == 0) goto L70
            r0.cleanup()
        L70:
            com.bumptech.glide.util.pool.GlideTrace.endSection()
            throw r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.DecodeJob.run():void");
    }

    private void runWrapped() {
        switch (this.runReason) {
            case INITIALIZE:
                this.stage = getNextStage(Stage.INITIALIZE);
                this.currentGenerator = getNextGenerator();
                runGenerators();
                return;
            case SWITCH_TO_SOURCE_SERVICE:
                runGenerators();
                return;
            case DECODE_DATA:
                decodeFromRetrievedData();
                return;
            default:
                throw new IllegalStateException("Unrecognized run reason: " + this.runReason);
        }
    }

    private DataFetcherGenerator getNextGenerator() {
        switch (this.stage) {
            case RESOURCE_CACHE:
                return new ResourceCacheGenerator(this.decodeHelper, this);
            case DATA_CACHE:
                return new DataCacheGenerator(this.decodeHelper, this);
            case SOURCE:
                return new SourceGenerator(this.decodeHelper, this);
            case FINISHED:
                return null;
            default:
                throw new IllegalStateException("Unrecognized stage: " + this.stage);
        }
    }

    private void runGenerators() {
        this.currentThread = Thread.currentThread();
        this.startFetchTime = LogTime.getLogTime();
        boolean isStarted = false;
        while (!this.isCancelled && this.currentGenerator != null) {
            boolean startNext = this.currentGenerator.startNext();
            isStarted = startNext;
            if (startNext) {
                break;
            }
            this.stage = getNextStage(this.stage);
            this.currentGenerator = getNextGenerator();
            if (this.stage == Stage.SOURCE) {
                reschedule();
                return;
            }
        }
        if ((this.stage == Stage.FINISHED || this.isCancelled) && !isStarted) {
            notifyFailed();
        }
    }

    private void notifyFailed() {
        setNotifiedOrThrow();
        GlideException e = new GlideException("Failed to load resource", new ArrayList(this.throwables));
        this.callback.onLoadFailed(e);
        onLoadFailed();
    }

    private void notifyComplete(Resource<R> resource, DataSource dataSource) {
        setNotifiedOrThrow();
        this.callback.onResourceReady(resource, dataSource);
    }

    private void setNotifiedOrThrow() {
        this.stateVerifier.throwIfRecycled();
        if (this.isCallbackNotified) {
            throw new IllegalStateException("Already notified");
        }
        this.isCallbackNotified = true;
    }

    private Stage getNextStage(Stage current) {
        switch (current) {
            case RESOURCE_CACHE:
                return this.diskCacheStrategy.decodeCachedData() ? Stage.DATA_CACHE : getNextStage(Stage.DATA_CACHE);
            case DATA_CACHE:
                return this.onlyRetrieveFromCache ? Stage.FINISHED : Stage.SOURCE;
            case SOURCE:
            case FINISHED:
                return Stage.FINISHED;
            case INITIALIZE:
                return this.diskCacheStrategy.decodeCachedResource() ? Stage.RESOURCE_CACHE : getNextStage(Stage.RESOURCE_CACHE);
            default:
                throw new IllegalArgumentException("Unrecognized stage: " + current);
        }
    }

    @Override // com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback
    public void reschedule() {
        this.runReason = RunReason.SWITCH_TO_SOURCE_SERVICE;
        this.callback.reschedule(this);
    }

    @Override // com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback
    public void onDataFetcherReady(Key sourceKey, Object data, DataFetcher<?> fetcher, DataSource dataSource, Key attemptedKey) {
        this.currentSourceKey = sourceKey;
        this.currentData = data;
        this.currentFetcher = fetcher;
        this.currentDataSource = dataSource;
        this.currentAttemptingKey = attemptedKey;
        if (Thread.currentThread() != this.currentThread) {
            this.runReason = RunReason.DECODE_DATA;
            this.callback.reschedule(this);
            return;
        }
        GlideTrace.beginSection("DecodeJob.decodeFromRetrievedData");
        try {
            decodeFromRetrievedData();
        } finally {
            GlideTrace.endSection();
        }
    }

    @Override // com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback
    public void onDataFetcherFailed(Key attemptedKey, Exception e, DataFetcher<?> fetcher, DataSource dataSource) {
        fetcher.cleanup();
        GlideException exception = new GlideException("Fetching data failed", e);
        exception.setLoggingDetails(attemptedKey, dataSource, fetcher.getDataClass());
        this.throwables.add(exception);
        if (Thread.currentThread() != this.currentThread) {
            this.runReason = RunReason.SWITCH_TO_SOURCE_SERVICE;
            this.callback.reschedule(this);
            return;
        }
        runGenerators();
    }

    private void decodeFromRetrievedData() {
        if (Log.isLoggable(TAG, 2)) {
            long j = this.startFetchTime;
            logWithTimeAndKey("Retrieved data", j, "data: " + this.currentData + ", cache key: " + this.currentSourceKey + ", fetcher: " + this.currentFetcher);
        }
        Resource<R> resource = null;
        try {
            resource = decodeFromData(this.currentFetcher, this.currentData, this.currentDataSource);
        } catch (GlideException e) {
            e.setLoggingDetails(this.currentAttemptingKey, this.currentDataSource);
            this.throwables.add(e);
        }
        if (resource != null) {
            notifyEncodeAndRelease(resource, this.currentDataSource);
        } else {
            runGenerators();
        }
    }

    private void notifyEncodeAndRelease(Resource<R> resource, DataSource dataSource) {
        if (resource instanceof Initializable) {
            ((Initializable) resource).initialize();
        }
        Resource<R> result = resource;
        LockedResource<R> lockedResource = null;
        if (this.deferredEncodeManager.hasResourceToEncode()) {
            lockedResource = LockedResource.obtain(resource);
            result = lockedResource;
        }
        notifyComplete(result, dataSource);
        this.stage = Stage.ENCODE;
        try {
            if (this.deferredEncodeManager.hasResourceToEncode()) {
                this.deferredEncodeManager.encode(this.diskCacheProvider, this.options);
            }
            onEncodeComplete();
        } finally {
            if (lockedResource != null) {
                lockedResource.unlock();
            }
        }
    }

    private <Data> Resource<R> decodeFromData(DataFetcher<?> fetcher, Data data, DataSource dataSource) throws GlideException {
        if (data != null) {
            try {
                long startTime = LogTime.getLogTime();
                Resource<R> result = decodeFromFetcher(data, dataSource);
                if (Log.isLoggable(TAG, 2)) {
                    logWithTimeAndKey("Decoded result " + result, startTime);
                }
                return result;
            } finally {
                fetcher.cleanup();
            }
        }
        return null;
    }

    private <Data> Resource<R> decodeFromFetcher(Data data, DataSource dataSource) throws GlideException {
        return runLoadPath(data, dataSource, (LoadPath<Data, ?, R>) this.decodeHelper.getLoadPath(data.getClass()));
    }

    @NonNull
    private Options getOptionsWithHardwareConfig(DataSource dataSource) {
        Options options = this.options;
        if (Build.VERSION.SDK_INT < 26) {
            return options;
        }
        boolean isHardwareConfigSafe = dataSource == DataSource.RESOURCE_DISK_CACHE || this.decodeHelper.isScaleOnlyOrNoTransform();
        Boolean isHardwareConfigAllowed = (Boolean) options.get(Downsampler.ALLOW_HARDWARE_CONFIG);
        if (isHardwareConfigAllowed != null && (!isHardwareConfigAllowed.booleanValue() || isHardwareConfigSafe)) {
            return options;
        }
        Options options2 = new Options();
        options2.putAll(this.options);
        options2.set(Downsampler.ALLOW_HARDWARE_CONFIG, Boolean.valueOf(isHardwareConfigSafe));
        return options2;
    }

    private <Data, ResourceType> Resource<R> runLoadPath(Data data, DataSource dataSource, LoadPath<Data, ResourceType, R> path) throws GlideException {
        Options options = getOptionsWithHardwareConfig(dataSource);
        DataRewinder<Data> rewinder = this.glideContext.getRegistry().getRewinder(data);
        try {
            return path.load(rewinder, options, this.width, this.height, new DecodeCallback(dataSource));
        } finally {
            rewinder.cleanup();
        }
    }

    private void logWithTimeAndKey(String message, long startTime) {
        logWithTimeAndKey(message, startTime, null);
    }

    private void logWithTimeAndKey(String message, long startTime, String extraArgs) {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        sb.append(" in ");
        sb.append(LogTime.getElapsedMillis(startTime));
        sb.append(", load key: ");
        sb.append(this.loadKey);
        if (extraArgs != null) {
            str = ", " + extraArgs;
        } else {
            str = "";
        }
        sb.append(str);
        sb.append(", thread: ");
        sb.append(Thread.currentThread().getName());
        Log.v(TAG, sb.toString());
    }

    @Override // com.bumptech.glide.util.pool.FactoryPools.Poolable
    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @NonNull
    <Z> Resource<Z> onResourceDecoded(DataSource dataSource, @NonNull Resource<Z> decoded) {
        Transformation<Z> appliedTransformation;
        Resource<Z> transformed;
        ResourceEncoder<Z> encoder;
        EncodeStrategy encodeStrategy;
        ResourceEncoder resourceEncoder;
        Key key;
        Class<?> cls = decoded.mo239get().getClass();
        if (dataSource == DataSource.RESOURCE_DISK_CACHE) {
            appliedTransformation = null;
            transformed = decoded;
        } else {
            Transformation<Z> appliedTransformation2 = this.decodeHelper.getTransformation(cls);
            Resource<Z> transformed2 = appliedTransformation2.transform(this.glideContext, decoded, this.width, this.height);
            appliedTransformation = appliedTransformation2;
            transformed = transformed2;
        }
        if (!decoded.equals(transformed)) {
            decoded.recycle();
        }
        if (this.decodeHelper.isResourceEncoderAvailable(transformed)) {
            ResourceEncoder<Z> encoder2 = this.decodeHelper.getResultEncoder(transformed);
            encoder = encoder2;
            encodeStrategy = encoder2.getEncodeStrategy(this.options);
        } else {
            encoder = null;
            encodeStrategy = EncodeStrategy.NONE;
        }
        Resource<Z> result = transformed;
        boolean isFromAlternateCacheKey = !this.decodeHelper.isSourceKey(this.currentSourceKey);
        if (this.diskCacheStrategy.isResourceCacheable(isFromAlternateCacheKey, dataSource, encodeStrategy)) {
            if (encoder == null) {
                throw new Registry.NoResultEncoderAvailableException(transformed.mo239get().getClass());
            }
            switch (encodeStrategy) {
                case SOURCE:
                    resourceEncoder = encoder;
                    key = new DataCacheKey(this.currentSourceKey, this.signature);
                    break;
                case TRANSFORMED:
                    resourceEncoder = encoder;
                    key = new ResourceCacheKey(this.decodeHelper.getArrayPool(), this.currentSourceKey, this.signature, this.width, this.height, appliedTransformation, cls, this.options);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown strategy: " + encodeStrategy);
            }
            LockedResource obtain = LockedResource.obtain(transformed);
            this.deferredEncodeManager.init(key, resourceEncoder, obtain);
            return obtain;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DecodeCallback<Z> implements DecodePath.DecodeCallback<Z> {
        private final DataSource dataSource;

        DecodeCallback(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override // com.bumptech.glide.load.engine.DecodePath.DecodeCallback
        @NonNull
        public Resource<Z> onResourceDecoded(@NonNull Resource<Z> decoded) {
            return DecodeJob.this.onResourceDecoded(this.dataSource, decoded);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ReleaseManager {
        private boolean isEncodeComplete;
        private boolean isFailed;
        private boolean isReleased;

        ReleaseManager() {
        }

        synchronized boolean release(boolean isRemovedFromQueue) {
            this.isReleased = true;
            return isComplete(isRemovedFromQueue);
        }

        synchronized boolean onEncodeComplete() {
            this.isEncodeComplete = true;
            return isComplete(false);
        }

        synchronized boolean onFailed() {
            this.isFailed = true;
            return isComplete(false);
        }

        synchronized void reset() {
            this.isEncodeComplete = false;
            this.isReleased = false;
            this.isFailed = false;
        }

        private boolean isComplete(boolean isRemovedFromQueue) {
            return (this.isFailed || isRemovedFromQueue || this.isEncodeComplete) && this.isReleased;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DeferredEncodeManager<Z> {
        private ResourceEncoder<Z> encoder;
        private Key key;
        private LockedResource<Z> toEncode;

        DeferredEncodeManager() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        <X> void init(Key key, ResourceEncoder<X> encoder, LockedResource<X> toEncode) {
            this.key = key;
            this.encoder = encoder;
            this.toEncode = toEncode;
        }

        void encode(DiskCacheProvider diskCacheProvider, Options options) {
            GlideTrace.beginSection("DecodeJob.encode");
            try {
                diskCacheProvider.getDiskCache().put(this.key, new DataCacheWriter(this.encoder, this.toEncode, options));
            } finally {
                this.toEncode.unlock();
                GlideTrace.endSection();
            }
        }

        boolean hasResourceToEncode() {
            return this.toEncode != null;
        }

        void clear() {
            this.key = null;
            this.encoder = null;
            this.toEncode = null;
        }
    }
}
