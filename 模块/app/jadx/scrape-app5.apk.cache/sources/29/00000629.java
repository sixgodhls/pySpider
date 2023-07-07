package me.goldze.mvvmhabit.base;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.trello.rxlifecycle2.LifecycleProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/* loaded from: classes.dex */
public class BaseViewModel<M extends BaseModel> extends AndroidViewModel implements IBaseViewModel, Consumer<Disposable> {
    private WeakReference<LifecycleProvider> lifecycle;
    private CompositeDisposable mCompositeDisposable;
    protected M model;

    /* renamed from: uc */
    private BaseViewModel<M>.UIChangeLiveData f205uc;

    /* loaded from: classes.dex */
    public static final class ParameterField {
        public static String CLASS = "CLASS";
        public static String CANONICAL_NAME = "CANONICAL_NAME";
        public static String BUNDLE = "BUNDLE";
    }

    public BaseViewModel(@NonNull Application application) {
        this(application, null);
    }

    public BaseViewModel(@NonNull Application application, M model) {
        super(application);
        this.model = model;
        this.mCompositeDisposable = new CompositeDisposable();
    }

    protected void addSubscribe(Disposable disposable) {
        if (this.mCompositeDisposable == null) {
            this.mCompositeDisposable = new CompositeDisposable();
        }
        this.mCompositeDisposable.add(disposable);
    }

    public void injectLifecycleProvider(LifecycleProvider lifecycle) {
        this.lifecycle = new WeakReference<>(lifecycle);
    }

    public LifecycleProvider getLifecycleProvider() {
        return this.lifecycle.get();
    }

    public BaseViewModel<M>.UIChangeLiveData getUC() {
        if (this.f205uc == null) {
            this.f205uc = new UIChangeLiveData();
        }
        return this.f205uc;
    }

    public void showDialog() {
        showDialog("请稍后...");
    }

    public void showDialog(String title) {
        ((UIChangeLiveData) this.f205uc).showDialogEvent.postValue(title);
    }

    public void dismissDialog() {
        ((UIChangeLiveData) this.f205uc).dismissDialogEvent.call();
    }

    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    public void startActivity(Class<?> clz, Bundle bundle) {
        HashMap hashMap = new HashMap();
        hashMap.put(ParameterField.CLASS, clz);
        if (bundle != null) {
            hashMap.put(ParameterField.BUNDLE, bundle);
        }
        ((UIChangeLiveData) this.f205uc).startActivityEvent.postValue(hashMap);
    }

    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    public void startContainerActivity(String canonicalName, Bundle bundle) {
        HashMap hashMap = new HashMap();
        hashMap.put(ParameterField.CANONICAL_NAME, canonicalName);
        if (bundle != null) {
            hashMap.put(ParameterField.BUNDLE, bundle);
        }
        ((UIChangeLiveData) this.f205uc).startContainerActivityEvent.postValue(hashMap);
    }

    public void finish() {
        ((UIChangeLiveData) this.f205uc).finishEvent.call();
    }

    public void onBackPressed() {
        ((UIChangeLiveData) this.f205uc).onBackPressedEvent.call();
    }

    @Override // me.goldze.mvvmhabit.base.IBaseViewModel
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
    }

    @Override // me.goldze.mvvmhabit.base.IBaseViewModel
    public void onCreate() {
    }

    public void onDestroy() {
    }

    @Override // me.goldze.mvvmhabit.base.IBaseViewModel
    public void onStart() {
    }

    @Override // me.goldze.mvvmhabit.base.IBaseViewModel
    public void onStop() {
    }

    @Override // me.goldze.mvvmhabit.base.IBaseViewModel
    public void onResume() {
    }

    @Override // me.goldze.mvvmhabit.base.IBaseViewModel
    public void onPause() {
    }

    @Override // me.goldze.mvvmhabit.base.IBaseViewModel
    public void registerRxBus() {
    }

    @Override // me.goldze.mvvmhabit.base.IBaseViewModel
    public void removeRxBus() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.arch.lifecycle.ViewModel
    public void onCleared() {
        super.onCleared();
        M m = this.model;
        if (m != null) {
            m.onCleared();
        }
        CompositeDisposable compositeDisposable = this.mCompositeDisposable;
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    @Override // io.reactivex.functions.Consumer
    public void accept(Disposable disposable) throws Exception {
        addSubscribe(disposable);
    }

    /* loaded from: classes.dex */
    public final class UIChangeLiveData extends SingleLiveEvent {
        private SingleLiveEvent<Void> dismissDialogEvent;
        private SingleLiveEvent<Void> finishEvent;
        private SingleLiveEvent<Void> onBackPressedEvent;
        private SingleLiveEvent<String> showDialogEvent;
        private SingleLiveEvent<Map<String, Object>> startActivityEvent;
        private SingleLiveEvent<Map<String, Object>> startContainerActivityEvent;

        public UIChangeLiveData() {
        }

        public SingleLiveEvent<String> getShowDialogEvent() {
            SingleLiveEvent<String> createLiveData = createLiveData(this.showDialogEvent);
            this.showDialogEvent = createLiveData;
            return createLiveData;
        }

        public SingleLiveEvent<Void> getDismissDialogEvent() {
            SingleLiveEvent<Void> createLiveData = createLiveData(this.dismissDialogEvent);
            this.dismissDialogEvent = createLiveData;
            return createLiveData;
        }

        public SingleLiveEvent<Map<String, Object>> getStartActivityEvent() {
            SingleLiveEvent<Map<String, Object>> createLiveData = createLiveData(this.startActivityEvent);
            this.startActivityEvent = createLiveData;
            return createLiveData;
        }

        public SingleLiveEvent<Map<String, Object>> getStartContainerActivityEvent() {
            SingleLiveEvent<Map<String, Object>> createLiveData = createLiveData(this.startContainerActivityEvent);
            this.startContainerActivityEvent = createLiveData;
            return createLiveData;
        }

        public SingleLiveEvent<Void> getFinishEvent() {
            SingleLiveEvent<Void> createLiveData = createLiveData(this.finishEvent);
            this.finishEvent = createLiveData;
            return createLiveData;
        }

        public SingleLiveEvent<Void> getOnBackPressedEvent() {
            SingleLiveEvent<Void> createLiveData = createLiveData(this.onBackPressedEvent);
            this.onBackPressedEvent = createLiveData;
            return createLiveData;
        }

        private <T> SingleLiveEvent<T> createLiveData(SingleLiveEvent<T> liveData) {
            if (liveData == null) {
                return new SingleLiveEvent<>();
            }
            return liveData;
        }

        @Override // me.goldze.mvvmhabit.bus.event.SingleLiveEvent, android.arch.lifecycle.LiveData
        public void observe(LifecycleOwner owner, Observer observer) {
            super.observe(owner, observer);
        }
    }
}