package android.support.v4.media;

import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.ActivityChooserView;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class MediaBrowserCompatUtils {
    public static boolean areSameOptions(Bundle options1, Bundle options2) {
        if (options1 == options2) {
            return true;
        }
        return options1 == null ? options2.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) == -1 && options2.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) == -1 : options2 == null ? options1.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) == -1 && options1.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) == -1 : options1.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) == options2.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) && options1.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) == options2.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1);
    }

    public static boolean hasDuplicatedItems(Bundle options1, Bundle options2) {
        int startIndex1;
        int endIndex1;
        int startIndex2;
        int endIndex2;
        int page1 = options1 == null ? -1 : options1.getInt(MediaBrowserCompat.EXTRA_PAGE, -1);
        int page2 = options2 == null ? -1 : options2.getInt(MediaBrowserCompat.EXTRA_PAGE, -1);
        int pageSize1 = options1 == null ? -1 : options1.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1);
        int pageSize2 = options2 == null ? -1 : options2.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1);
        if (page1 == -1 || pageSize1 == -1) {
            startIndex1 = 0;
            endIndex1 = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        } else {
            startIndex1 = pageSize1 * page1;
            endIndex1 = (startIndex1 + pageSize1) - 1;
        }
        if (page2 == -1 || pageSize2 == -1) {
            startIndex2 = 0;
            endIndex2 = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        } else {
            startIndex2 = pageSize2 * page2;
            endIndex2 = (startIndex2 + pageSize2) - 1;
        }
        return endIndex1 >= startIndex2 && endIndex2 >= startIndex1;
    }

    private MediaBrowserCompatUtils() {
    }
}
