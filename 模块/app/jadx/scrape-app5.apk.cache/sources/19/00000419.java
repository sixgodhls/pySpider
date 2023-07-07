package android.support.p000v4.media;

import android.content.Context;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.p000v4.media.MediaSessionManagerImplApi28;
import android.support.p000v4.media.MediaSessionManagerImplBase;
import android.util.Log;

/* renamed from: android.support.v4.media.MediaSessionManager */
/* loaded from: classes.dex */
public final class MediaSessionManager {
    private static volatile MediaSessionManager sSessionManager;
    MediaSessionManagerImpl mImpl;
    static final String TAG = "MediaSessionManager";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final Object sLock = new Object();

    /* renamed from: android.support.v4.media.MediaSessionManager$MediaSessionManagerImpl */
    /* loaded from: classes.dex */
    interface MediaSessionManagerImpl {
        Context getContext();

        boolean isTrustedForMediaControl(RemoteUserInfoImpl remoteUserInfoImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.support.v4.media.MediaSessionManager$RemoteUserInfoImpl */
    /* loaded from: classes.dex */
    public interface RemoteUserInfoImpl {
        String getPackageName();

        int getPid();

        int getUid();
    }

    @NonNull
    public static MediaSessionManager getSessionManager(@NonNull Context context) {
        MediaSessionManager manager = sSessionManager;
        if (manager == null) {
            synchronized (sLock) {
                manager = sSessionManager;
                if (manager == null) {
                    sSessionManager = new MediaSessionManager(context.getApplicationContext());
                    manager = sSessionManager;
                }
            }
        }
        return manager;
    }

    private MediaSessionManager(Context context) {
        if (Build.VERSION.SDK_INT >= 28) {
            this.mImpl = new MediaSessionManagerImplApi28(context);
        } else if (Build.VERSION.SDK_INT >= 21) {
            this.mImpl = new MediaSessionManagerImplApi21(context);
        } else {
            this.mImpl = new MediaSessionManagerImplBase(context);
        }
    }

    public boolean isTrustedForMediaControl(@NonNull RemoteUserInfo userInfo) {
        if (userInfo == null) {
            throw new IllegalArgumentException("userInfo should not be null");
        }
        return this.mImpl.isTrustedForMediaControl(userInfo.mImpl);
    }

    Context getContext() {
        return this.mImpl.getContext();
    }

    /* renamed from: android.support.v4.media.MediaSessionManager$RemoteUserInfo */
    /* loaded from: classes.dex */
    public static final class RemoteUserInfo {
        public static final String LEGACY_CONTROLLER = "android.media.session.MediaController";
        RemoteUserInfoImpl mImpl;

        public RemoteUserInfo(@NonNull String packageName, int pid, int uid) {
            if (Build.VERSION.SDK_INT >= 28) {
                this.mImpl = new MediaSessionManagerImplApi28.RemoteUserInfoImplApi28(packageName, pid, uid);
            } else {
                this.mImpl = new MediaSessionManagerImplBase.RemoteUserInfoImplBase(packageName, pid, uid);
            }
        }

        @RequiresApi(28)
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public RemoteUserInfo(MediaSessionManager.RemoteUserInfo remoteUserInfo) {
            this.mImpl = new MediaSessionManagerImplApi28.RemoteUserInfoImplApi28(remoteUserInfo);
        }

        @NonNull
        public String getPackageName() {
            return this.mImpl.getPackageName();
        }

        public int getPid() {
            return this.mImpl.getPid();
        }

        public int getUid() {
            return this.mImpl.getUid();
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RemoteUserInfo)) {
                return false;
            }
            return this.mImpl.equals(((RemoteUserInfo) obj).mImpl);
        }

        public int hashCode() {
            return this.mImpl.hashCode();
        }
    }
}