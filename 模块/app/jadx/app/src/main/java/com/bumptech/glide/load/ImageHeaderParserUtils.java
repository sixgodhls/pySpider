package com.bumptech.glide.load;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

/* loaded from: classes.dex */
public final class ImageHeaderParserUtils {
    private static final int MARK_POSITION = 5242880;

    private ImageHeaderParserUtils() {
    }

    @NonNull
    public static ImageHeaderParser.ImageType getType(@NonNull List<ImageHeaderParser> parsers, @Nullable InputStream is, @NonNull ArrayPool byteArrayPool) throws IOException {
        if (is == null) {
            return ImageHeaderParser.ImageType.UNKNOWN;
        }
        if (!is.markSupported()) {
            is = new RecyclableBufferedInputStream(is, byteArrayPool);
        }
        is.mark(MARK_POSITION);
        int size = parsers.size();
        for (int i = 0; i < size; i++) {
            ImageHeaderParser parser = parsers.get(i);
            try {
                ImageHeaderParser.ImageType type = parser.getType(is);
                if (type != ImageHeaderParser.ImageType.UNKNOWN) {
                    return type;
                }
                is.reset();
            } finally {
                is.reset();
            }
        }
        return ImageHeaderParser.ImageType.UNKNOWN;
    }

    @NonNull
    public static ImageHeaderParser.ImageType getType(@NonNull List<ImageHeaderParser> parsers, @Nullable ByteBuffer buffer) throws IOException {
        if (buffer == null) {
            return ImageHeaderParser.ImageType.UNKNOWN;
        }
        int size = parsers.size();
        for (int i = 0; i < size; i++) {
            ImageHeaderParser parser = parsers.get(i);
            ImageHeaderParser.ImageType type = parser.getType(buffer);
            if (type != ImageHeaderParser.ImageType.UNKNOWN) {
                return type;
            }
        }
        return ImageHeaderParser.ImageType.UNKNOWN;
    }

    public static int getOrientation(@NonNull List<ImageHeaderParser> parsers, @Nullable InputStream is, @NonNull ArrayPool byteArrayPool) throws IOException {
        if (is == null) {
            return -1;
        }
        if (!is.markSupported()) {
            is = new RecyclableBufferedInputStream(is, byteArrayPool);
        }
        is.mark(MARK_POSITION);
        int size = parsers.size();
        for (int i = 0; i < size; i++) {
            ImageHeaderParser parser = parsers.get(i);
            try {
                int orientation = parser.getOrientation(is, byteArrayPool);
                if (orientation != -1) {
                    return orientation;
                }
                is.reset();
            } finally {
                is.reset();
            }
        }
        return -1;
    }
}
