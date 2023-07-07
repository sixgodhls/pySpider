package android.support.v4.app;

import android.arch.lifecycle.ViewModelStore;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class FragmentState implements Parcelable {
    public static final Parcelable.Creator<FragmentState> CREATOR = new Parcelable.Creator<FragmentState>() { // from class: android.support.v4.app.FragmentState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        /* renamed from: createFromParcel */
        public FragmentState mo91createFromParcel(Parcel in) {
            return new FragmentState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        /* renamed from: newArray */
        public FragmentState[] mo92newArray(int size) {
            return new FragmentState[size];
        }
    };
    final Bundle mArguments;
    final String mClassName;
    final int mContainerId;
    final boolean mDetached;
    final int mFragmentId;
    final boolean mFromLayout;
    final boolean mHidden;
    final int mIndex;
    Fragment mInstance;
    final boolean mRetainInstance;
    Bundle mSavedFragmentState;
    final String mTag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentState(Fragment frag) {
        this.mClassName = frag.getClass().getName();
        this.mIndex = frag.mIndex;
        this.mFromLayout = frag.mFromLayout;
        this.mFragmentId = frag.mFragmentId;
        this.mContainerId = frag.mContainerId;
        this.mTag = frag.mTag;
        this.mRetainInstance = frag.mRetainInstance;
        this.mDetached = frag.mDetached;
        this.mArguments = frag.mArguments;
        this.mHidden = frag.mHidden;
    }

    FragmentState(Parcel in) {
        this.mClassName = in.readString();
        this.mIndex = in.readInt();
        boolean z = true;
        this.mFromLayout = in.readInt() != 0;
        this.mFragmentId = in.readInt();
        this.mContainerId = in.readInt();
        this.mTag = in.readString();
        this.mRetainInstance = in.readInt() != 0;
        this.mDetached = in.readInt() != 0;
        this.mArguments = in.readBundle();
        this.mHidden = in.readInt() == 0 ? false : z;
        this.mSavedFragmentState = in.readBundle();
    }

    public Fragment instantiate(FragmentHostCallback host, FragmentContainer container, Fragment parent, FragmentManagerNonConfig childNonConfig, ViewModelStore viewModelStore) {
        if (this.mInstance == null) {
            Context context = host.getContext();
            Bundle bundle = this.mArguments;
            if (bundle != null) {
                bundle.setClassLoader(context.getClassLoader());
            }
            if (container != null) {
                this.mInstance = container.instantiate(context, this.mClassName, this.mArguments);
            } else {
                this.mInstance = Fragment.instantiate(context, this.mClassName, this.mArguments);
            }
            Bundle bundle2 = this.mSavedFragmentState;
            if (bundle2 != null) {
                bundle2.setClassLoader(context.getClassLoader());
                this.mInstance.mSavedFragmentState = this.mSavedFragmentState;
            }
            this.mInstance.setIndex(this.mIndex, parent);
            Fragment fragment = this.mInstance;
            fragment.mFromLayout = this.mFromLayout;
            fragment.mRestored = true;
            fragment.mFragmentId = this.mFragmentId;
            fragment.mContainerId = this.mContainerId;
            fragment.mTag = this.mTag;
            fragment.mRetainInstance = this.mRetainInstance;
            fragment.mDetached = this.mDetached;
            fragment.mHidden = this.mHidden;
            fragment.mFragmentManager = host.mFragmentManager;
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Instantiated fragment " + this.mInstance);
            }
        }
        Fragment fragment2 = this.mInstance;
        fragment2.mChildNonConfig = childNonConfig;
        fragment2.mViewModelStore = viewModelStore;
        return fragment2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mClassName);
        dest.writeInt(this.mIndex);
        dest.writeInt(this.mFromLayout ? 1 : 0);
        dest.writeInt(this.mFragmentId);
        dest.writeInt(this.mContainerId);
        dest.writeString(this.mTag);
        dest.writeInt(this.mRetainInstance ? 1 : 0);
        dest.writeInt(this.mDetached ? 1 : 0);
        dest.writeBundle(this.mArguments);
        dest.writeInt(this.mHidden ? 1 : 0);
        dest.writeBundle(this.mSavedFragmentState);
    }
}
