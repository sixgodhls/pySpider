package com.trello.rxlifecycle2.components;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/* loaded from: classes.dex */
public abstract class RxPreferenceFragment extends PreferenceFragment implements LifecycleProvider<FragmentEvent> {
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Override // com.trello.rxlifecycle2.LifecycleProvider
    @CheckResult
    @NonNull
    public final Observable<FragmentEvent> lifecycle() {
        return this.lifecycleSubject.hide();
    }

    @Override // com.trello.rxlifecycle2.LifecycleProvider
    @CheckResult
    @NonNull
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(this.lifecycleSubject, event);
    }

    @Override // com.trello.rxlifecycle2.LifecycleProvider
    @CheckResult
    @NonNull
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(this.lifecycleSubject);
    }

    @Override // android.app.Fragment
    @CallSuper
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    @CallSuper
    public void onStart() {
        super.onStart();
        this.lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override // android.app.Fragment
    @CallSuper
    public void onResume() {
        super.onResume();
        this.lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override // android.app.Fragment
    @CallSuper
    public void onPause() {
        this.lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    @CallSuper
    public void onStop() {
        this.lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    @CallSuper
    public void onDestroyView() {
        this.lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    @CallSuper
    public void onDestroy() {
        this.lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override // android.app.Fragment
    @CallSuper
    public void onDetach() {
        this.lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }
}
