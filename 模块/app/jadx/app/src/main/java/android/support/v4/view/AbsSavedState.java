package android.support.v4.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class AbsSavedState implements Parcelable {
    private final Parcelable mSuperState;
    public static final AbsSavedState EMPTY_STATE = new AbsSavedState() { // from class: android.support.v4.view.AbsSavedState.1
    };
    public static final Parcelable.Creator<AbsSavedState> CREATOR = new Parcelable.ClassLoaderCreator<AbsSavedState>() { // from class: android.support.v4.view.AbsSavedState.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.ClassLoaderCreator
        /* renamed from: createFromParcel */
        public AbsSavedState mo126createFromParcel(Parcel in, ClassLoader loader) {
            Parcelable superState = in.readParcelable(loader);
            if (superState != null) {
                throw new IllegalStateException("superState must be null");
            }
            return AbsSavedState.EMPTY_STATE;
        }

        @Override // android.os.Parcelable.Creator
        /* renamed from: createFromParcel */
        public AbsSavedState mo125createFromParcel(Parcel in) {
            return mo126createFromParcel(in, (ClassLoader) null);
        }

        @Override // android.os.Parcelable.Creator
        /* renamed from: newArray */
        public AbsSavedState[] mo127newArray(int size) {
            return new AbsSavedState[size];
        }
    };

    private AbsSavedState() {
        this.mSuperState = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbsSavedState(@NonNull Parcelable superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        this.mSuperState = superState != EMPTY_STATE ? superState : null;
    }

    protected AbsSavedState(@NonNull Parcel source) {
        this(source, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbsSavedState(@NonNull Parcel source, @Nullable ClassLoader loader) {
        Parcelable superState = source.readParcelable(loader);
        this.mSuperState = superState != null ? superState : EMPTY_STATE;
    }

    @Nullable
    public final Parcelable getSuperState() {
        return this.mSuperState;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mSuperState, flags);
    }
}
