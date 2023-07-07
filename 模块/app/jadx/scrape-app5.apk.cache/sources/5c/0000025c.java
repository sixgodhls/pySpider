package android.support.p000v4.media;

import android.os.Bundle;
import android.support.annotation.NonNull;
import androidx.versionedparcelable.VersionedParcelable;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: android.support.v4.media.AudioAttributesImpl */
/* loaded from: classes.dex */
public interface AudioAttributesImpl extends VersionedParcelable {
    Object getAudioAttributes();

    int getContentType();

    int getFlags();

    int getLegacyStreamType();

    int getRawLegacyStreamType();

    int getUsage();

    int getVolumeControlStream();

    @NonNull
    Bundle toBundle();
}