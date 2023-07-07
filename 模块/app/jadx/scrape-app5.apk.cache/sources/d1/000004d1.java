package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.util.Util;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@RequiresApi(19)
/* loaded from: classes.dex */
public class SizeConfigStrategy implements LruPoolStrategy {
    private static final Bitmap.Config[] ALPHA_8_IN_CONFIGS;
    private static final Bitmap.Config[] ARGB_4444_IN_CONFIGS;
    private static final Bitmap.Config[] ARGB_8888_IN_CONFIGS;
    private static final int MAX_SIZE_MULTIPLE = 8;
    private static final Bitmap.Config[] RGBA_F16_IN_CONFIGS;
    private static final Bitmap.Config[] RGB_565_IN_CONFIGS;
    private final KeyPool keyPool = new KeyPool();
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap<>();
    private final Map<Bitmap.Config, NavigableMap<Integer, Integer>> sortedSizes = new HashMap();

    static {
        Bitmap.Config[] result = {Bitmap.Config.ARGB_8888, null};
        if (Build.VERSION.SDK_INT >= 26) {
            result = (Bitmap.Config[]) Arrays.copyOf(result, result.length + 1);
            result[result.length - 1] = Bitmap.Config.RGBA_F16;
        }
        ARGB_8888_IN_CONFIGS = result;
        RGBA_F16_IN_CONFIGS = ARGB_8888_IN_CONFIGS;
        RGB_565_IN_CONFIGS = new Bitmap.Config[]{Bitmap.Config.RGB_565};
        ARGB_4444_IN_CONFIGS = new Bitmap.Config[]{Bitmap.Config.ARGB_4444};
        ALPHA_8_IN_CONFIGS = new Bitmap.Config[]{Bitmap.Config.ALPHA_8};
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public void put(Bitmap bitmap) {
        int size = Util.getBitmapByteSize(bitmap);
        Key key = this.keyPool.get(size, bitmap.getConfig());
        this.groupedMap.put(key, bitmap);
        NavigableMap<Integer, Integer> sizes = getSizesForConfig(bitmap.getConfig());
        Integer current = (Integer) sizes.get(Integer.valueOf(key.size));
        Integer valueOf = Integer.valueOf(key.size);
        int i = 1;
        if (current != null) {
            i = 1 + current.intValue();
        }
        sizes.put(valueOf, Integer.valueOf(i));
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    @Nullable
    public Bitmap get(int width, int height, Bitmap.Config config) {
        int size = Util.getBitmapByteSize(width, height, config);
        Key bestKey = findBestKey(size, config);
        Bitmap result = this.groupedMap.get(bestKey);
        if (result != null) {
            decrementBitmapOfSize(Integer.valueOf(bestKey.size), result);
            result.reconfigure(width, height, result.getConfig() != null ? result.getConfig() : Bitmap.Config.ARGB_8888);
        }
        return result;
    }

    private Key findBestKey(int size, Bitmap.Config config) {
        Bitmap.Config[] inConfigs;
        Key result = this.keyPool.get(size, config);
        for (Bitmap.Config possibleConfig : getInConfigs(config)) {
            NavigableMap<Integer, Integer> sizesForPossibleConfig = getSizesForConfig(possibleConfig);
            Integer possibleSize = sizesForPossibleConfig.ceilingKey(Integer.valueOf(size));
            if (possibleSize != null && possibleSize.intValue() <= size * 8) {
                if (possibleSize.intValue() == size) {
                    if (possibleConfig == null) {
                        if (config == null) {
                            return result;
                        }
                    } else if (possibleConfig.equals(config)) {
                        return result;
                    }
                }
                this.keyPool.offer(result);
                return this.keyPool.get(possibleSize.intValue(), possibleConfig);
            }
        }
        return result;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    @Nullable
    public Bitmap removeLast() {
        Bitmap removed = this.groupedMap.removeLast();
        if (removed != null) {
            int removedSize = Util.getBitmapByteSize(removed);
            decrementBitmapOfSize(Integer.valueOf(removedSize), removed);
        }
        return removed;
    }

    private void decrementBitmapOfSize(Integer size, Bitmap removed) {
        Bitmap.Config config = removed.getConfig();
        NavigableMap<Integer, Integer> sizes = getSizesForConfig(config);
        Integer current = (Integer) sizes.get(size);
        if (current == null) {
            throw new NullPointerException("Tried to decrement empty size, size: " + size + ", removed: " + logBitmap(removed) + ", this: " + this);
        } else if (current.intValue() == 1) {
            sizes.remove(size);
        } else {
            sizes.put(size, Integer.valueOf(current.intValue() - 1));
        }
    }

    private NavigableMap<Integer, Integer> getSizesForConfig(Bitmap.Config config) {
        NavigableMap<Integer, Integer> sizes = this.sortedSizes.get(config);
        if (sizes == null) {
            NavigableMap<Integer, Integer> sizes2 = new TreeMap<>();
            this.sortedSizes.put(config, sizes2);
            return sizes2;
        }
        return sizes;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public String logBitmap(Bitmap bitmap) {
        int size = Util.getBitmapByteSize(bitmap);
        return getBitmapString(size, bitmap.getConfig());
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public String logBitmap(int width, int height, Bitmap.Config config) {
        int size = Util.getBitmapByteSize(width, height, config);
        return getBitmapString(size, config);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public int getSize(Bitmap bitmap) {
        return Util.getBitmapByteSize(bitmap);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SizeConfigStrategy{groupedMap=");
        sb.append(this.groupedMap);
        StringBuilder sb2 = sb.append(", sortedSizes=(");
        for (Map.Entry<Bitmap.Config, NavigableMap<Integer, Integer>> entry : this.sortedSizes.entrySet()) {
            sb2.append(entry.getKey());
            sb2.append('[');
            sb2.append(entry.getValue());
            sb2.append("], ");
        }
        if (!this.sortedSizes.isEmpty()) {
            sb2.replace(sb2.length() - 2, sb2.length(), "");
        }
        sb2.append(")}");
        return sb2.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class KeyPool extends BaseKeyPool<Key> {
        KeyPool() {
        }

        public Key get(int size, Bitmap.Config config) {
            Key result = get();
            result.init(size, config);
            return result;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.bumptech.glide.load.engine.bitmap_recycle.BaseKeyPool
        /* renamed from: create  reason: collision with other method in class */
        public Key mo290create() {
            return new Key(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class Key implements Poolable {
        private Bitmap.Config config;
        private final KeyPool pool;
        int size;

        public Key(KeyPool pool) {
            this.pool = pool;
        }

        @VisibleForTesting
        Key(KeyPool pool, int size, Bitmap.Config config) {
            this(pool);
            init(size, config);
        }

        public void init(int size, Bitmap.Config config) {
            this.size = size;
            this.config = config;
        }

        @Override // com.bumptech.glide.load.engine.bitmap_recycle.Poolable
        public void offer() {
            this.pool.offer(this);
        }

        public String toString() {
            return SizeConfigStrategy.getBitmapString(this.size, this.config);
        }

        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key other = (Key) o;
                return this.size == other.size && Util.bothNullOrEqual(this.config, other.config);
            }
            return false;
        }

        public int hashCode() {
            int result = this.size;
            int i = result * 31;
            Bitmap.Config config = this.config;
            int result2 = i + (config != null ? config.hashCode() : 0);
            return result2;
        }
    }

    static String getBitmapString(int size, Bitmap.Config config) {
        return "[" + size + "](" + config + ")";
    }

    private static Bitmap.Config[] getInConfigs(Bitmap.Config requested) {
        if (Build.VERSION.SDK_INT >= 26 && Bitmap.Config.RGBA_F16.equals(requested)) {
            return RGBA_F16_IN_CONFIGS;
        }
        switch (C06551.$SwitchMap$android$graphics$Bitmap$Config[requested.ordinal()]) {
            case 1:
                return ARGB_8888_IN_CONFIGS;
            case 2:
                return RGB_565_IN_CONFIGS;
            case 3:
                return ARGB_4444_IN_CONFIGS;
            case 4:
                return ALPHA_8_IN_CONFIGS;
            default:
                return new Bitmap.Config[]{requested};
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.bumptech.glide.load.engine.bitmap_recycle.SizeConfigStrategy$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C06551 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config = new int[Bitmap.Config.values().length];

        static {
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_8888.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.RGB_565.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_4444.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ALPHA_8.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }
}