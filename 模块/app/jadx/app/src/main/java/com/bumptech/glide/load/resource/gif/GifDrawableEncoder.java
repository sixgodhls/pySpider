package com.bumptech.glide.load.resource.gif;

import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class GifDrawableEncoder implements ResourceEncoder<GifDrawable> {
    private static final String TAG = "GifEncoder";

    @Override // com.bumptech.glide.load.ResourceEncoder
    @NonNull
    public EncodeStrategy getEncodeStrategy(@NonNull Options options) {
        return EncodeStrategy.SOURCE;
    }

    @Override // com.bumptech.glide.load.Encoder
    public boolean encode(@NonNull Resource<GifDrawable> data, @NonNull File file, @NonNull Options options) {
        GifDrawable drawable = data.mo239get();
        try {
            ByteBufferUtil.toFile(drawable.getBuffer(), file);
            return true;
        } catch (IOException e) {
            if (!Log.isLoggable(TAG, 5)) {
                return false;
            }
            Log.w(TAG, "Failed to encode GIF drawable data", e);
            return false;
        }
    }
}
