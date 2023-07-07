package android.support.p000v4.media;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.p000v4.media.MediaSessionManager;

@RequiresApi(21)
/* renamed from: android.support.v4.media.MediaSessionManagerImplApi21 */
/* loaded from: classes.dex */
class MediaSessionManagerImplApi21 extends MediaSessionManagerImplBase {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaSessionManagerImplApi21(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // android.support.p000v4.media.MediaSessionManagerImplBase, android.support.p000v4.media.MediaSessionManager.MediaSessionManagerImpl
    public boolean isTrustedForMediaControl(@NonNull MediaSessionManager.RemoteUserInfoImpl userInfo) {
        return hasMediaControlPermission(userInfo) || super.isTrustedForMediaControl(userInfo);
    }

    private boolean hasMediaControlPermission(@NonNull MediaSessionManager.RemoteUserInfoImpl userInfo) {
        return getContext().checkPermission("android.permission.MEDIA_CONTENT_CONTROL", userInfo.getPid(), userInfo.getUid()) == 0;
    }
}