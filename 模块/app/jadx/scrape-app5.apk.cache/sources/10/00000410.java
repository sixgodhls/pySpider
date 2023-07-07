package android.support.design.stateful;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.p000v4.util.SimpleArrayMap;
import android.support.p000v4.view.AbsSavedState;

/* loaded from: classes.dex */
public class ExtendableSavedState extends AbsSavedState {
    public static final Parcelable.Creator<ExtendableSavedState> CREATOR = new Parcelable.ClassLoaderCreator<ExtendableSavedState>() { // from class: android.support.design.stateful.ExtendableSavedState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.ClassLoaderCreator
        /* renamed from: createFromParcel */
        public ExtendableSavedState mo93createFromParcel(Parcel in, ClassLoader loader) {
            return new ExtendableSavedState(in, loader);
        }

        @Override // android.os.Parcelable.Creator
        /* renamed from: createFromParcel */
        public ExtendableSavedState mo92createFromParcel(Parcel in) {
            return new ExtendableSavedState(in, null);
        }

        @Override // android.os.Parcelable.Creator
        /* renamed from: newArray */
        public ExtendableSavedState[] mo94newArray(int size) {
            return new ExtendableSavedState[size];
        }
    };
    public final SimpleArrayMap<String, Bundle> extendableStates;

    public ExtendableSavedState(Parcelable superState) {
        super(superState);
        this.extendableStates = new SimpleArrayMap<>();
    }

    private ExtendableSavedState(Parcel in, ClassLoader loader) {
        super(in, loader);
        int size = in.readInt();
        String[] keys = new String[size];
        in.readStringArray(keys);
        Bundle[] states = new Bundle[size];
        in.readTypedArray(states, Bundle.CREATOR);
        this.extendableStates = new SimpleArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            this.extendableStates.put(keys[i], states[i]);
        }
    }

    @Override // android.support.p000v4.view.AbsSavedState, android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        int size = this.extendableStates.size();
        out.writeInt(size);
        String[] keys = new String[size];
        Bundle[] states = new Bundle[size];
        for (int i = 0; i < size; i++) {
            keys[i] = this.extendableStates.keyAt(i);
            states[i] = this.extendableStates.valueAt(i);
        }
        out.writeStringArray(keys);
        out.writeTypedArray(states, 0);
    }

    public String toString() {
        return "ExtendableSavedState{" + Integer.toHexString(System.identityHashCode(this)) + " states=" + this.extendableStates + "}";
    }
}