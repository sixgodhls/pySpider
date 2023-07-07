package com.bumptech.glide.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentActivity;
import android.support.p000v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class RequestManagerRetriever implements Handler.Callback {
    private static final RequestManagerFactory DEFAULT_FACTORY = new RequestManagerFactory() { // from class: com.bumptech.glide.manager.RequestManagerRetriever.1
        @Override // com.bumptech.glide.manager.RequestManagerRetriever.RequestManagerFactory
        @NonNull
        public RequestManager build(@NonNull Glide glide, @NonNull Lifecycle lifecycle, @NonNull RequestManagerTreeNode requestManagerTreeNode, @NonNull Context context) {
            return new RequestManager(glide, lifecycle, requestManagerTreeNode, context);
        }
    };
    private static final String FRAGMENT_INDEX_KEY = "key";
    @VisibleForTesting
    static final String FRAGMENT_TAG = "com.bumptech.glide.manager";
    private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;
    private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;
    private static final String TAG = "RMRetriever";
    private volatile RequestManager applicationManager;
    private final RequestManagerFactory factory;
    private final Handler handler;
    @VisibleForTesting
    final Map<FragmentManager, RequestManagerFragment> pendingRequestManagerFragments = new HashMap();
    @VisibleForTesting
    final Map<android.support.p000v4.app.FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments = new HashMap();
    private final ArrayMap<View, Fragment> tempViewToSupportFragment = new ArrayMap<>();
    private final ArrayMap<View, android.app.Fragment> tempViewToFragment = new ArrayMap<>();
    private final Bundle tempBundle = new Bundle();

    /* loaded from: classes.dex */
    public interface RequestManagerFactory {
        @NonNull
        RequestManager build(@NonNull Glide glide, @NonNull Lifecycle lifecycle, @NonNull RequestManagerTreeNode requestManagerTreeNode, @NonNull Context context);
    }

    public RequestManagerRetriever(@Nullable RequestManagerFactory factory) {
        this.factory = factory != null ? factory : DEFAULT_FACTORY;
        this.handler = new Handler(Looper.getMainLooper(), this);
    }

    @NonNull
    private RequestManager getApplicationManager(@NonNull Context context) {
        if (this.applicationManager == null) {
            synchronized (this) {
                if (this.applicationManager == null) {
                    Glide glide = Glide.get(context.getApplicationContext());
                    this.applicationManager = this.factory.build(glide, new ApplicationLifecycle(), new EmptyRequestManagerTreeNode(), context.getApplicationContext());
                }
            }
        }
        return this.applicationManager;
    }

    @NonNull
    public RequestManager get(@NonNull Context context) {
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        }
        if (Util.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return get((FragmentActivity) context);
            }
            if (context instanceof Activity) {
                return get((Activity) context);
            }
            if (context instanceof ContextWrapper) {
                return get(((ContextWrapper) context).getBaseContext());
            }
        }
        return getApplicationManager(context);
    }

    @NonNull
    public RequestManager get(@NonNull FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        }
        assertNotDestroyed(activity);
        android.support.p000v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        return supportFragmentGet(activity, fm, null, isActivityVisible(activity));
    }

    @NonNull
    public RequestManager get(@NonNull Fragment fragment) {
        Preconditions.checkNotNull(fragment.getActivity(), "You cannot start a load on a fragment before it is attached or after it is destroyed");
        if (Util.isOnBackgroundThread()) {
            return get(fragment.getActivity().getApplicationContext());
        }
        android.support.p000v4.app.FragmentManager fm = fragment.getChildFragmentManager();
        return supportFragmentGet(fragment.getActivity(), fm, fragment, fragment.isVisible());
    }

    @NonNull
    public RequestManager get(@NonNull Activity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        }
        assertNotDestroyed(activity);
        FragmentManager fm = activity.getFragmentManager();
        return fragmentGet(activity, fm, null, isActivityVisible(activity));
    }

    @NonNull
    public RequestManager get(@NonNull View view) {
        if (Util.isOnBackgroundThread()) {
            return get(view.getContext().getApplicationContext());
        }
        Preconditions.checkNotNull(view);
        Preconditions.checkNotNull(view.getContext(), "Unable to obtain a request manager for a view without a Context");
        Activity activity = findActivity(view.getContext());
        if (activity == null) {
            return get(view.getContext().getApplicationContext());
        }
        if (activity instanceof FragmentActivity) {
            Fragment fragment = findSupportFragment(view, (FragmentActivity) activity);
            return fragment != null ? get(fragment) : get(activity);
        }
        android.app.Fragment fragment2 = findFragment(view, activity);
        if (fragment2 == null) {
            return get(activity);
        }
        return get(fragment2);
    }

    private static void findAllSupportFragmentsWithViews(@Nullable Collection<Fragment> topLevelFragments, @NonNull Map<View, Fragment> result) {
        if (topLevelFragments == null) {
            return;
        }
        for (Fragment fragment : topLevelFragments) {
            if (fragment != null && fragment.getView() != null) {
                result.put(fragment.getView(), fragment);
                findAllSupportFragmentsWithViews(fragment.getChildFragmentManager().getFragments(), result);
            }
        }
    }

    @Nullable
    private Fragment findSupportFragment(@NonNull View target, @NonNull FragmentActivity activity) {
        this.tempViewToSupportFragment.clear();
        findAllSupportFragmentsWithViews(activity.getSupportFragmentManager().getFragments(), this.tempViewToSupportFragment);
        Fragment result = null;
        View activityRoot = activity.findViewById(16908290);
        for (View current = target; !current.equals(activityRoot); current = (View) current.getParent()) {
            Fragment result2 = this.tempViewToSupportFragment.get(current);
            result = result2;
            if (result != null || !(current.getParent() instanceof View)) {
                break;
            }
        }
        this.tempViewToSupportFragment.clear();
        return result;
    }

    @Nullable
    @Deprecated
    private android.app.Fragment findFragment(@NonNull View target, @NonNull Activity activity) {
        this.tempViewToFragment.clear();
        findAllFragmentsWithViews(activity.getFragmentManager(), this.tempViewToFragment);
        android.app.Fragment result = null;
        View activityRoot = activity.findViewById(16908290);
        for (View current = target; !current.equals(activityRoot); current = (View) current.getParent()) {
            android.app.Fragment result2 = this.tempViewToFragment.get(current);
            result = result2;
            if (result != null || !(current.getParent() instanceof View)) {
                break;
            }
        }
        this.tempViewToFragment.clear();
        return result;
    }

    @TargetApi(26)
    @Deprecated
    private void findAllFragmentsWithViews(@NonNull FragmentManager fragmentManager, @NonNull ArrayMap<View, android.app.Fragment> result) {
        if (Build.VERSION.SDK_INT >= 26) {
            for (android.app.Fragment fragment : fragmentManager.getFragments()) {
                if (fragment.getView() != null) {
                    result.put(fragment.getView(), fragment);
                    findAllFragmentsWithViews(fragment.getChildFragmentManager(), result);
                }
            }
            return;
        }
        findAllFragmentsWithViewsPreO(fragmentManager, result);
    }

    @Deprecated
    private void findAllFragmentsWithViewsPreO(@NonNull FragmentManager fragmentManager, @NonNull ArrayMap<View, android.app.Fragment> result) {
        int index = 0;
        while (true) {
            int index2 = index + 1;
            this.tempBundle.putInt(FRAGMENT_INDEX_KEY, index);
            android.app.Fragment fragment = null;
            try {
                fragment = fragmentManager.getFragment(this.tempBundle, FRAGMENT_INDEX_KEY);
            } catch (Exception e) {
            }
            if (fragment != null) {
                if (fragment.getView() != null) {
                    result.put(fragment.getView(), fragment);
                    if (Build.VERSION.SDK_INT >= 17) {
                        findAllFragmentsWithViews(fragment.getChildFragmentManager(), result);
                    }
                }
                index = index2;
            } else {
                return;
            }
        }
    }

    @Nullable
    private Activity findActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    @TargetApi(17)
    private static void assertNotDestroyed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }

    @NonNull
    @TargetApi(17)
    @Deprecated
    public RequestManager get(@NonNull android.app.Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
        }
        if (Util.isOnBackgroundThread() || Build.VERSION.SDK_INT < 17) {
            return get(fragment.getActivity().getApplicationContext());
        }
        FragmentManager fm = fragment.getChildFragmentManager();
        return fragmentGet(fragment.getActivity(), fm, fragment, fragment.isVisible());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NonNull
    @Deprecated
    public RequestManagerFragment getRequestManagerFragment(Activity activity) {
        return getRequestManagerFragment(activity.getFragmentManager(), null, isActivityVisible(activity));
    }

    @NonNull
    private RequestManagerFragment getRequestManagerFragment(@NonNull FragmentManager fm, @Nullable android.app.Fragment parentHint, boolean isParentVisible) {
        RequestManagerFragment current = (RequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null && (current = this.pendingRequestManagerFragments.get(fm)) == null) {
            current = new RequestManagerFragment();
            current.setParentFragmentHint(parentHint);
            if (isParentVisible) {
                current.getGlideLifecycle().onStart();
            }
            this.pendingRequestManagerFragments.put(fm, current);
            fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
            this.handler.obtainMessage(1, fm).sendToTarget();
        }
        return current;
    }

    @NonNull
    @Deprecated
    private RequestManager fragmentGet(@NonNull Context context, @NonNull FragmentManager fm, @Nullable android.app.Fragment parentHint, boolean isParentVisible) {
        RequestManagerFragment current = getRequestManagerFragment(fm, parentHint, isParentVisible);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            Glide glide = Glide.get(context);
            RequestManager requestManager2 = this.factory.build(glide, current.getGlideLifecycle(), current.getRequestManagerTreeNode(), context);
            current.setRequestManager(requestManager2);
            return requestManager2;
        }
        return requestManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NonNull
    public SupportRequestManagerFragment getSupportRequestManagerFragment(FragmentActivity activity) {
        return getSupportRequestManagerFragment(activity.getSupportFragmentManager(), null, isActivityVisible(activity));
    }

    private static boolean isActivityVisible(Activity activity) {
        return !activity.isFinishing();
    }

    @NonNull
    private SupportRequestManagerFragment getSupportRequestManagerFragment(@NonNull android.support.p000v4.app.FragmentManager fm, @Nullable Fragment parentHint, boolean isParentVisible) {
        SupportRequestManagerFragment current = (SupportRequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null && (current = this.pendingSupportRequestManagerFragments.get(fm)) == null) {
            current = new SupportRequestManagerFragment();
            current.setParentFragmentHint(parentHint);
            if (isParentVisible) {
                current.getGlideLifecycle().onStart();
            }
            this.pendingSupportRequestManagerFragments.put(fm, current);
            fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
            this.handler.obtainMessage(2, fm).sendToTarget();
        }
        return current;
    }

    @NonNull
    private RequestManager supportFragmentGet(@NonNull Context context, @NonNull android.support.p000v4.app.FragmentManager fm, @Nullable Fragment parentHint, boolean isParentVisible) {
        SupportRequestManagerFragment current = getSupportRequestManagerFragment(fm, parentHint, isParentVisible);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            Glide glide = Glide.get(context);
            RequestManager requestManager2 = this.factory.build(glide, current.getGlideLifecycle(), current.getRequestManagerTreeNode(), context);
            current.setRequestManager(requestManager2);
            return requestManager2;
        }
        return requestManager;
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        boolean handled = true;
        Object removed = null;
        Object key = null;
        switch (message.what) {
            case 1:
                Object fm = (FragmentManager) message.obj;
                key = fm;
                removed = this.pendingRequestManagerFragments.remove(fm);
                break;
            case 2:
                Object supportFm = (android.support.p000v4.app.FragmentManager) message.obj;
                key = supportFm;
                removed = this.pendingSupportRequestManagerFragments.remove(supportFm);
                break;
            default:
                handled = false;
                break;
        }
        if (handled && removed == null && Log.isLoggable(TAG, 5)) {
            Log.w(TAG, "Failed to remove expected request manager fragment, manager: " + key);
        }
        return handled;
    }
}