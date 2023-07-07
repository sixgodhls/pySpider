package android.support.p000v4.p002os;

import android.os.Parcel;

@Deprecated
/* renamed from: android.support.v4.os.ParcelableCompatCreatorCallbacks */
/* loaded from: classes.dex */
public interface ParcelableCompatCreatorCallbacks<T> {
    T createFromParcel(Parcel parcel, ClassLoader classLoader);

    T[] newArray(int i);
}