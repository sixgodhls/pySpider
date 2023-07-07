package android.support.p000v4.media.session;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.p000v4.media.session.MediaSessionCompatApi21;

/* JADX INFO: Access modifiers changed from: package-private */
@RequiresApi(23)
/* renamed from: android.support.v4.media.session.MediaSessionCompatApi23 */
/* loaded from: classes.dex */
public class MediaSessionCompatApi23 {

    /* renamed from: android.support.v4.media.session.MediaSessionCompatApi23$Callback */
    /* loaded from: classes.dex */
    public interface Callback extends MediaSessionCompatApi21.Callback {
        void onPlayFromUri(Uri uri, Bundle bundle);
    }

    public static Object createCallback(Callback callback) {
        return new CallbackProxy(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.support.v4.media.session.MediaSessionCompatApi23$CallbackProxy */
    /* loaded from: classes.dex */
    public static class CallbackProxy<T extends Callback> extends MediaSessionCompatApi21.CallbackProxy<T> {
        public CallbackProxy(T callback) {
            super(callback);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onPlayFromUri(Uri uri, Bundle extras) {
            MediaSessionCompat.ensureClassLoader(extras);
            ((Callback) this.mCallback).onPlayFromUri(uri, extras);
        }
    }

    private MediaSessionCompatApi23() {
    }
}