package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/* loaded from: classes.dex */
class ThumbnailStreamOpener {
    private static final FileService DEFAULT_SERVICE = new FileService();
    private static final String TAG = "ThumbStreamOpener";
    private final ArrayPool byteArrayPool;
    private final ContentResolver contentResolver;
    private final List<ImageHeaderParser> parsers;
    private final ThumbnailQuery query;
    private final FileService service;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThumbnailStreamOpener(List<ImageHeaderParser> parsers, ThumbnailQuery query, ArrayPool byteArrayPool, ContentResolver contentResolver) {
        this(parsers, DEFAULT_SERVICE, query, byteArrayPool, contentResolver);
    }

    ThumbnailStreamOpener(List<ImageHeaderParser> parsers, FileService service, ThumbnailQuery query, ArrayPool byteArrayPool, ContentResolver contentResolver) {
        this.service = service;
        this.query = query;
        this.byteArrayPool = byteArrayPool;
        this.contentResolver = contentResolver;
        this.parsers = parsers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getOrientation(Uri uri) {
        InputStream is = null;
        try {
            try {
                is = this.contentResolver.openInputStream(uri);
                return ImageHeaderParserUtils.getOrientation(this.parsers, is, this.byteArrayPool);
            } finally {
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (IOException | NullPointerException e2) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Failed to open uri: " + uri, e2);
            }
            if (is == null) {
                return -1;
            }
            try {
                is.close();
                return -1;
            } catch (IOException e3) {
                return -1;
            }
        }
    }

    public InputStream open(Uri uri) throws FileNotFoundException {
        String path = getPath(uri);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = this.service.get(path);
        if (!isValid(file)) {
            return null;
        }
        Uri thumbnailUri = Uri.fromFile(file);
        try {
            return this.contentResolver.openInputStream(thumbnailUri);
        } catch (NullPointerException e) {
            throw ((FileNotFoundException) new FileNotFoundException("NPE opening uri: " + uri + " -> " + thumbnailUri).initCause(e));
        }
    }

    @Nullable
    private String getPath(@NonNull Uri uri) {
        Cursor cursor = this.query.query(uri);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    private boolean isValid(File file) {
        return this.service.exists(file) && 0 < this.service.length(file);
    }
}
