package com.bumptech.glide.load.engine.cache;

import android.content.Context;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import java.io.File;

/* loaded from: classes.dex */
public final class ExternalPreferredCacheDiskCacheFactory extends DiskLruCacheFactory {
    public ExternalPreferredCacheDiskCacheFactory(Context context) {
        this(context, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, 262144000L);
    }

    public ExternalPreferredCacheDiskCacheFactory(Context context, long diskCacheSize) {
        this(context, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, diskCacheSize);
    }

    public ExternalPreferredCacheDiskCacheFactory(final Context context, final String diskCacheName, long diskCacheSize) {
        super(new DiskLruCacheFactory.CacheDirectoryGetter() { // from class: com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory.1
            @Nullable
            private File getInternalCacheDirectory() {
                File cacheDirectory = context.getCacheDir();
                if (cacheDirectory == null) {
                    return null;
                }
                String str = diskCacheName;
                if (str != null) {
                    return new File(cacheDirectory, str);
                }
                return cacheDirectory;
            }

            @Override // com.bumptech.glide.load.engine.cache.DiskLruCacheFactory.CacheDirectoryGetter
            public File getCacheDirectory() {
                File internalCacheDirectory = getInternalCacheDirectory();
                if (internalCacheDirectory != null && internalCacheDirectory.exists()) {
                    return internalCacheDirectory;
                }
                File cacheDirectory = context.getExternalCacheDir();
                if (cacheDirectory == null || !cacheDirectory.canWrite()) {
                    return internalCacheDirectory;
                }
                String str = diskCacheName;
                if (str != null) {
                    return new File(cacheDirectory, str);
                }
                return cacheDirectory;
            }
        }, diskCacheSize);
    }
}
