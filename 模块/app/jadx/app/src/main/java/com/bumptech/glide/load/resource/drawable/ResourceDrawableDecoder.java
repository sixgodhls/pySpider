package com.bumptech.glide.load.resource.drawable;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import java.util.List;

/* loaded from: classes.dex */
public class ResourceDrawableDecoder implements ResourceDecoder<Uri, Drawable> {
    private static final int ID_PATH_SEGMENTS = 1;
    private static final int NAME_PATH_SEGMENT_INDEX = 1;
    private static final int NAME_URI_PATH_SEGMENTS = 2;
    private static final int RESOURCE_ID_SEGMENT_INDEX = 0;
    private static final int TYPE_PATH_SEGMENT_INDEX = 0;
    private final Context context;

    public ResourceDrawableDecoder(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public boolean handles(@NonNull Uri source, @NonNull Options options) {
        return source.getScheme().equals("android.resource");
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    @Nullable
    public Resource<Drawable> decode(@NonNull Uri source, int width, int height, @NonNull Options options) {
        int resId = loadResourceIdFromUri(source);
        String packageName = source.getAuthority();
        Context targetContext = packageName.equals(this.context.getPackageName()) ? this.context : getContextForPackage(source, packageName);
        Drawable drawable = DrawableDecoderCompat.getDrawable(this.context, targetContext, resId);
        return NonOwnedDrawableResource.newInstance(drawable);
    }

    @NonNull
    private Context getContextForPackage(Uri source, String packageName) {
        try {
            return this.context.createPackageContext(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException("Failed to obtain context or unrecognized Uri format for: " + source, e);
        }
    }

    @DrawableRes
    private int loadResourceIdFromUri(Uri source) {
        List<String> segments = source.getPathSegments();
        Integer result = null;
        if (segments.size() == 2) {
            String packageName = source.getAuthority();
            String typeName = segments.get(0);
            String resourceName = segments.get(1);
            result = Integer.valueOf(this.context.getResources().getIdentifier(resourceName, typeName, packageName));
        } else if (segments.size() == 1) {
            try {
                result = Integer.valueOf(segments.get(0));
            } catch (NumberFormatException e) {
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("Unrecognized Uri format: " + source);
        } else if (result.intValue() == 0) {
            throw new IllegalArgumentException("Failed to obtain resource id for: " + source);
        } else {
            return result.intValue();
        }
    }
}
