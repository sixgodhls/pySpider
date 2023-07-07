package android.support.v4.media;

import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.media.MediaBrowserCompatApi21;
import android.support.v4.media.session.MediaSessionCompat;
import java.util.List;

@RequiresApi(26)
/* loaded from: classes.dex */
class MediaBrowserCompatApi26 {

    /* loaded from: classes.dex */
    interface SubscriptionCallback extends MediaBrowserCompatApi21.SubscriptionCallback {
        void onChildrenLoaded(@NonNull String str, List<?> list, @NonNull Bundle bundle);

        void onError(@NonNull String str, @NonNull Bundle bundle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object createSubscriptionCallback(SubscriptionCallback callback) {
        return new SubscriptionCallbackProxy(callback);
    }

    public static void subscribe(Object browserObj, String parentId, Bundle options, Object subscriptionCallbackObj) {
        ((MediaBrowser) browserObj).subscribe(parentId, options, (MediaBrowser.SubscriptionCallback) subscriptionCallbackObj);
    }

    public static void unsubscribe(Object browserObj, String parentId, Object subscriptionCallbackObj) {
        ((MediaBrowser) browserObj).unsubscribe(parentId, (MediaBrowser.SubscriptionCallback) subscriptionCallbackObj);
    }

    /* loaded from: classes.dex */
    static class SubscriptionCallbackProxy<T extends SubscriptionCallback> extends MediaBrowserCompatApi21.SubscriptionCallbackProxy<T> {
        SubscriptionCallbackProxy(T callback) {
            super(callback);
        }

        @Override // android.media.browse.MediaBrowser.SubscriptionCallback
        public void onChildrenLoaded(@NonNull String parentId, List<MediaBrowser.MediaItem> children, @NonNull Bundle options) {
            MediaSessionCompat.ensureClassLoader(options);
            ((SubscriptionCallback) this.mSubscriptionCallback).onChildrenLoaded(parentId, children, options);
        }

        @Override // android.media.browse.MediaBrowser.SubscriptionCallback
        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            MediaSessionCompat.ensureClassLoader(options);
            ((SubscriptionCallback) this.mSubscriptionCallback).onError(parentId, options);
        }
    }

    private MediaBrowserCompatApi26() {
    }
}
