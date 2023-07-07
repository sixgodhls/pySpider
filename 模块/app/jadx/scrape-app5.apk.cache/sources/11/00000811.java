package com.trello.rxlifecycle2.components.support;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p003v7.app.AppCompatActivity;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/* loaded from: classes.dex */
public abstract class RxAppCompatActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    @Override // com.trello.rxlifecycle2.LifecycleProvider
    @CheckResult
    @NonNull
    public final Observable<ActivityEvent> lifecycle() {
        return this.lifecycleSubject.hide();
    }

    @Override // com.trello.rxlifecycle2.LifecycleProvider
    @CheckResult
    @NonNull
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(this.lifecycleSubject, event);
    }

    @Override // com.trello.rxlifecycle2.LifecycleProvider
    @CheckResult
    @NonNull
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(this.lifecycleSubject);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.p003v7.app.AppCompatActivity, android.support.p000v4.app.FragmentActivity, android.support.p000v4.app.ComponentActivity, android.app.Activity
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lifecycleSubject.onNext(ActivityEvent.CREATE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.p003v7.app.AppCompatActivity, android.support.p000v4.app.FragmentActivity, android.app.Activity
    @CallSuper
    public void onStart() {
        super.onStart();
        this.lifecycleSubject.onNext(ActivityEvent.START);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.p000v4.app.FragmentActivity, android.app.Activity
    @CallSuper
    public void onResume() {
        super.onResume();
        this.lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.p000v4.app.FragmentActivity, android.app.Activity
    @CallSuper
    public void onPause() {
        this.lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.p003v7.app.AppCompatActivity, android.support.p000v4.app.FragmentActivity, android.app.Activity
    @CallSuper
    public void onStop() {
        this.lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.p003v7.app.AppCompatActivity, android.support.p000v4.app.FragmentActivity, android.app.Activity
    @CallSuper
    public void onDestroy() {
        this.lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }
}