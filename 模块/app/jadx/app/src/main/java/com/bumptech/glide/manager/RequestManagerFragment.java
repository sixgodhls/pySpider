package com.bumptech.glide.manager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Deprecated
/* loaded from: classes.dex */
public class RequestManagerFragment extends Fragment {
    private static final String TAG = "RMFragment";
    private final Set<RequestManagerFragment> childRequestManagerFragments;
    private final ActivityFragmentLifecycle lifecycle;
    @Nullable
    private Fragment parentFragmentHint;
    @Nullable
    private RequestManager requestManager;
    private final RequestManagerTreeNode requestManagerTreeNode;
    @Nullable
    private RequestManagerFragment rootRequestManagerFragment;

    public RequestManagerFragment() {
        this(new ActivityFragmentLifecycle());
    }

    @VisibleForTesting
    @SuppressLint({"ValidFragment"})
    RequestManagerFragment(@NonNull ActivityFragmentLifecycle lifecycle) {
        this.requestManagerTreeNode = new FragmentRequestManagerTreeNode();
        this.childRequestManagerFragments = new HashSet();
        this.lifecycle = lifecycle;
    }

    public void setRequestManager(@Nullable RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NonNull
    public ActivityFragmentLifecycle getGlideLifecycle() {
        return this.lifecycle;
    }

    @Nullable
    public RequestManager getRequestManager() {
        return this.requestManager;
    }

    @NonNull
    public RequestManagerTreeNode getRequestManagerTreeNode() {
        return this.requestManagerTreeNode;
    }

    private void addChildRequestManagerFragment(RequestManagerFragment child) {
        this.childRequestManagerFragments.add(child);
    }

    private void removeChildRequestManagerFragment(RequestManagerFragment child) {
        this.childRequestManagerFragments.remove(child);
    }

    @NonNull
    @TargetApi(17)
    Set<RequestManagerFragment> getDescendantRequestManagerFragments() {
        if (equals(this.rootRequestManagerFragment)) {
            return Collections.unmodifiableSet(this.childRequestManagerFragments);
        }
        if (this.rootRequestManagerFragment == null || Build.VERSION.SDK_INT < 17) {
            return Collections.emptySet();
        }
        Set<RequestManagerFragment> descendants = new HashSet<>();
        for (RequestManagerFragment fragment : this.rootRequestManagerFragment.getDescendantRequestManagerFragments()) {
            if (isDescendant(fragment.getParentFragment())) {
                descendants.add(fragment);
            }
        }
        return Collections.unmodifiableSet(descendants);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setParentFragmentHint(@Nullable Fragment parentFragmentHint) {
        this.parentFragmentHint = parentFragmentHint;
        if (parentFragmentHint != null && parentFragmentHint.getActivity() != null) {
            registerFragmentWithRoot(parentFragmentHint.getActivity());
        }
    }

    @TargetApi(17)
    @Nullable
    private Fragment getParentFragmentUsingHint() {
        Fragment fragment;
        if (Build.VERSION.SDK_INT >= 17) {
            fragment = getParentFragment();
        } else {
            fragment = null;
        }
        return fragment != null ? fragment : this.parentFragmentHint;
    }

    @TargetApi(17)
    private boolean isDescendant(@NonNull Fragment fragment) {
        Fragment root = getParentFragment();
        while (true) {
            Fragment parentFragment = fragment.getParentFragment();
            if (parentFragment != null) {
                if (parentFragment.equals(root)) {
                    return true;
                }
                fragment = fragment.getParentFragment();
            } else {
                return false;
            }
        }
    }

    private void registerFragmentWithRoot(@NonNull Activity activity) {
        unregisterFragmentWithRoot();
        this.rootRequestManagerFragment = Glide.get(activity).getRequestManagerRetriever().getRequestManagerFragment(activity);
        if (!equals(this.rootRequestManagerFragment)) {
            this.rootRequestManagerFragment.addChildRequestManagerFragment(this);
        }
    }

    private void unregisterFragmentWithRoot() {
        RequestManagerFragment requestManagerFragment = this.rootRequestManagerFragment;
        if (requestManagerFragment != null) {
            requestManagerFragment.removeChildRequestManagerFragment(this);
            this.rootRequestManagerFragment = null;
        }
    }

    @Override // android.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            registerFragmentWithRoot(activity);
        } catch (IllegalStateException e) {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Unable to register fragment with root", e);
            }
        }
    }

    @Override // android.app.Fragment
    public void onDetach() {
        super.onDetach();
        unregisterFragmentWithRoot();
    }

    @Override // android.app.Fragment
    public void onStart() {
        super.onStart();
        this.lifecycle.onStart();
    }

    @Override // android.app.Fragment
    public void onStop() {
        super.onStop();
        this.lifecycle.onStop();
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.lifecycle.onDestroy();
        unregisterFragmentWithRoot();
    }

    @Override // android.app.Fragment
    public String toString() {
        return super.toString() + "{parent=" + getParentFragmentUsingHint() + "}";
    }

    /* loaded from: classes.dex */
    private class FragmentRequestManagerTreeNode implements RequestManagerTreeNode {
        FragmentRequestManagerTreeNode() {
        }

        @Override // com.bumptech.glide.manager.RequestManagerTreeNode
        @NonNull
        public Set<RequestManager> getDescendants() {
            Set<RequestManagerFragment> descendantFragments = RequestManagerFragment.this.getDescendantRequestManagerFragments();
            Set<RequestManager> descendants = new HashSet<>(descendantFragments.size());
            for (RequestManagerFragment fragment : descendantFragments) {
                if (fragment.getRequestManager() != null) {
                    descendants.add(fragment.getRequestManager());
                }
            }
            return descendants;
        }

        public String toString() {
            return super.toString() + "{fragment=" + RequestManagerFragment.this + "}";
        }
    }
}
