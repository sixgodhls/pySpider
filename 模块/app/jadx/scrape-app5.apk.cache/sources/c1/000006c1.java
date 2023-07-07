package android.databinding;

import android.annotation.TargetApi;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.res.ColorStateList;
import android.databinding.CallbackRegistry;
import android.databinding.Observable;
import android.databinding.ObservableList;
import android.databinding.ObservableMap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import com.android.databinding.library.C0622R;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class ViewDataBinding extends BaseObservable {
    private static final CreateWeakListener CREATE_LIST_LISTENER;
    private static final CreateWeakListener CREATE_LIVE_DATA_LISTENER;
    private static final CreateWeakListener CREATE_MAP_LISTENER;
    private static final CreateWeakListener CREATE_PROPERTY_LISTENER;
    private static final int HALTED = 2;
    private static final int REBIND = 1;
    private static final CallbackRegistry.NotifierCallback<OnRebindCallback, ViewDataBinding, Void> REBIND_NOTIFIER;
    private static final int REBOUND = 3;
    private static final View.OnAttachStateChangeListener ROOT_REATTACHED_LISTENER;
    private static final boolean USE_CHOREOGRAPHER;
    private static final ReferenceQueue<ViewDataBinding> sReferenceQueue;
    protected final DataBindingComponent mBindingComponent;
    private Choreographer mChoreographer;
    private ViewDataBinding mContainingBinding;
    private final Choreographer.FrameCallback mFrameCallback;
    private boolean mInLiveDataRegisterObserver;
    private boolean mIsExecutingPendingBindings;
    private LifecycleOwner mLifecycleOwner;
    private WeakListener[] mLocalFieldObservers;
    private OnStartListener mOnStartListener;
    private CallbackRegistry<OnRebindCallback, ViewDataBinding, Void> mRebindCallbacks;
    private final View mRoot;
    private Handler mUIThreadHandler;
    static int SDK_INT = Build.VERSION.SDK_INT;
    public static final String BINDING_TAG_PREFIX = "binding_";
    private static final int BINDING_NUMBER_START = BINDING_TAG_PREFIX.length();
    private final Runnable mRebindRunnable = new Runnable() { // from class: android.databinding.ViewDataBinding.7
        {
            ViewDataBinding.this = this;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (this) {
                ViewDataBinding.this.mPendingRebind = false;
            }
            ViewDataBinding.processReferenceQueue();
            if (Build.VERSION.SDK_INT >= 19 && !ViewDataBinding.this.mRoot.isAttachedToWindow()) {
                ViewDataBinding.this.mRoot.removeOnAttachStateChangeListener(ViewDataBinding.ROOT_REATTACHED_LISTENER);
                ViewDataBinding.this.mRoot.addOnAttachStateChangeListener(ViewDataBinding.ROOT_REATTACHED_LISTENER);
                return;
            }
            ViewDataBinding.this.executePendingBindings();
        }
    };
    private boolean mPendingRebind = false;
    private boolean mRebindHalted = false;

    /* loaded from: classes.dex */
    public interface CreateWeakListener {
        WeakListener create(ViewDataBinding viewDataBinding, int i);
    }

    /* loaded from: classes.dex */
    public interface ObservableReference<T> {
        void addListener(T t);

        WeakListener<T> getListener();

        void removeListener(T t);

        void setLifecycleOwner(LifecycleOwner lifecycleOwner);
    }

    protected abstract void executeBindings();

    public abstract boolean hasPendingBindings();

    public abstract void invalidateAll();

    protected abstract boolean onFieldChange(int i, Object obj, int i2);

    public abstract boolean setVariable(int i, @Nullable Object obj);

    static {
        USE_CHOREOGRAPHER = SDK_INT >= 16;
        CREATE_PROPERTY_LISTENER = new CreateWeakListener() { // from class: android.databinding.ViewDataBinding.1
            @Override // android.databinding.ViewDataBinding.CreateWeakListener
            public WeakListener create(ViewDataBinding viewDataBinding, int localFieldId) {
                return new WeakPropertyListener(viewDataBinding, localFieldId).getListener();
            }
        };
        CREATE_LIST_LISTENER = new CreateWeakListener() { // from class: android.databinding.ViewDataBinding.2
            @Override // android.databinding.ViewDataBinding.CreateWeakListener
            public WeakListener create(ViewDataBinding viewDataBinding, int localFieldId) {
                return new WeakListListener(viewDataBinding, localFieldId).getListener();
            }
        };
        CREATE_MAP_LISTENER = new CreateWeakListener() { // from class: android.databinding.ViewDataBinding.3
            @Override // android.databinding.ViewDataBinding.CreateWeakListener
            public WeakListener create(ViewDataBinding viewDataBinding, int localFieldId) {
                return new WeakMapListener(viewDataBinding, localFieldId).getListener();
            }
        };
        CREATE_LIVE_DATA_LISTENER = new CreateWeakListener() { // from class: android.databinding.ViewDataBinding.4
            @Override // android.databinding.ViewDataBinding.CreateWeakListener
            public WeakListener create(ViewDataBinding viewDataBinding, int localFieldId) {
                return new LiveDataListener(viewDataBinding, localFieldId).getListener();
            }
        };
        REBIND_NOTIFIER = new CallbackRegistry.NotifierCallback<OnRebindCallback, ViewDataBinding, Void>() { // from class: android.databinding.ViewDataBinding.5
            @Override // android.databinding.CallbackRegistry.NotifierCallback
            public void onNotifyCallback(OnRebindCallback callback, ViewDataBinding sender, int mode, Void arg2) {
                switch (mode) {
                    case 1:
                        if (!callback.onPreBind(sender)) {
                            sender.mRebindHalted = true;
                            return;
                        }
                        return;
                    case 2:
                        callback.onCanceled(sender);
                        return;
                    case 3:
                        callback.onBound(sender);
                        return;
                    default:
                        return;
                }
            }
        };
        sReferenceQueue = new ReferenceQueue<>();
        if (Build.VERSION.SDK_INT < 19) {
            ROOT_REATTACHED_LISTENER = null;
        } else {
            ROOT_REATTACHED_LISTENER = new View.OnAttachStateChangeListener() { // from class: android.databinding.ViewDataBinding.6
                @Override // android.view.View.OnAttachStateChangeListener
                @TargetApi(19)
                public void onViewAttachedToWindow(View v) {
                    ViewDataBinding binding = ViewDataBinding.getBinding(v);
                    binding.mRebindRunnable.run();
                    v.removeOnAttachStateChangeListener(this);
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View v) {
                }
            };
        }
    }

    public ViewDataBinding(DataBindingComponent bindingComponent, View root, int localFieldCount) {
        this.mBindingComponent = bindingComponent;
        this.mLocalFieldObservers = new WeakListener[localFieldCount];
        this.mRoot = root;
        if (Looper.myLooper() == null) {
            throw new IllegalStateException("DataBinding must be created in view's UI Thread");
        }
        if (USE_CHOREOGRAPHER) {
            this.mChoreographer = Choreographer.getInstance();
            this.mFrameCallback = new Choreographer.FrameCallback() { // from class: android.databinding.ViewDataBinding.8
                {
                    ViewDataBinding.this = this;
                }

                @Override // android.view.Choreographer.FrameCallback
                public void doFrame(long frameTimeNanos) {
                    ViewDataBinding.this.mRebindRunnable.run();
                }
            };
            return;
        }
        this.mFrameCallback = null;
        this.mUIThreadHandler = new Handler(Looper.myLooper());
    }

    public void setRootTag(View view) {
        view.setTag(C0622R.C0625id.dataBinding, this);
    }

    protected void setRootTag(View[] views) {
        for (View view : views) {
            view.setTag(C0622R.C0625id.dataBinding, this);
        }
    }

    public static int getBuildSdkInt() {
        return SDK_INT;
    }

    @MainThread
    public void setLifecycleOwner(@Nullable LifecycleOwner lifecycleOwner) {
        WeakListener<?>[] weakListenerArr;
        LifecycleOwner lifecycleOwner2 = this.mLifecycleOwner;
        if (lifecycleOwner2 == lifecycleOwner) {
            return;
        }
        if (lifecycleOwner2 != null) {
            lifecycleOwner2.getLifecycle().removeObserver(this.mOnStartListener);
        }
        this.mLifecycleOwner = lifecycleOwner;
        if (lifecycleOwner != null) {
            if (this.mOnStartListener == null) {
                this.mOnStartListener = new OnStartListener();
            }
            lifecycleOwner.getLifecycle().addObserver(this.mOnStartListener);
        }
        for (WeakListener<?> weakListener : this.mLocalFieldObservers) {
            if (weakListener != null) {
                weakListener.setLifecycleOwner(lifecycleOwner);
            }
        }
    }

    @Nullable
    public LifecycleOwner getLifecycleOwner() {
        return this.mLifecycleOwner;
    }

    public void addOnRebindCallback(@NonNull OnRebindCallback listener) {
        if (this.mRebindCallbacks == null) {
            this.mRebindCallbacks = new CallbackRegistry<>(REBIND_NOTIFIER);
        }
        this.mRebindCallbacks.add(listener);
    }

    public void removeOnRebindCallback(@NonNull OnRebindCallback listener) {
        CallbackRegistry<OnRebindCallback, ViewDataBinding, Void> callbackRegistry = this.mRebindCallbacks;
        if (callbackRegistry != null) {
            callbackRegistry.remove(listener);
        }
    }

    public void executePendingBindings() {
        ViewDataBinding viewDataBinding = this.mContainingBinding;
        if (viewDataBinding == null) {
            executeBindingsInternal();
        } else {
            viewDataBinding.executePendingBindings();
        }
    }

    private void executeBindingsInternal() {
        if (this.mIsExecutingPendingBindings) {
            requestRebind();
        } else if (!hasPendingBindings()) {
        } else {
            this.mIsExecutingPendingBindings = true;
            this.mRebindHalted = false;
            CallbackRegistry<OnRebindCallback, ViewDataBinding, Void> callbackRegistry = this.mRebindCallbacks;
            if (callbackRegistry != null) {
                callbackRegistry.notifyCallbacks(this, 1, null);
                if (this.mRebindHalted) {
                    this.mRebindCallbacks.notifyCallbacks(this, 2, null);
                }
            }
            if (!this.mRebindHalted) {
                executeBindings();
                CallbackRegistry<OnRebindCallback, ViewDataBinding, Void> callbackRegistry2 = this.mRebindCallbacks;
                if (callbackRegistry2 != null) {
                    callbackRegistry2.notifyCallbacks(this, 3, null);
                }
            }
            this.mIsExecutingPendingBindings = false;
        }
    }

    protected static void executeBindingsOn(ViewDataBinding other) {
        other.executeBindingsInternal();
    }

    public void forceExecuteBindings() {
        executeBindings();
    }

    public void unbind() {
        WeakListener[] weakListenerArr;
        for (WeakListener weakListener : this.mLocalFieldObservers) {
            if (weakListener != null) {
                weakListener.unregister();
            }
        }
    }

    public static ViewDataBinding getBinding(View v) {
        if (v != null) {
            return (ViewDataBinding) v.getTag(C0622R.C0625id.dataBinding);
        }
        return null;
    }

    @NonNull
    public View getRoot() {
        return this.mRoot;
    }

    public void handleFieldChange(int mLocalFieldId, Object object, int fieldId) {
        if (this.mInLiveDataRegisterObserver) {
            return;
        }
        boolean result = onFieldChange(mLocalFieldId, object, fieldId);
        if (result) {
            requestRebind();
        }
    }

    protected boolean unregisterFrom(int localFieldId) {
        WeakListener listener = this.mLocalFieldObservers[localFieldId];
        if (listener != null) {
            return listener.unregister();
        }
        return false;
    }

    public void requestRebind() {
        ViewDataBinding viewDataBinding = this.mContainingBinding;
        if (viewDataBinding != null) {
            viewDataBinding.requestRebind();
            return;
        }
        LifecycleOwner owner = this.mLifecycleOwner;
        if (owner != null) {
            Lifecycle.State state = owner.getLifecycle().getCurrentState();
            if (!state.isAtLeast(Lifecycle.State.STARTED)) {
                return;
            }
        }
        synchronized (this) {
            if (this.mPendingRebind) {
                return;
            }
            this.mPendingRebind = true;
            if (USE_CHOREOGRAPHER) {
                this.mChoreographer.postFrameCallback(this.mFrameCallback);
            } else {
                this.mUIThreadHandler.post(this.mRebindRunnable);
            }
        }
    }

    protected Object getObservedField(int localFieldId) {
        WeakListener listener = this.mLocalFieldObservers[localFieldId];
        if (listener == null) {
            return null;
        }
        return listener.getTarget();
    }

    private boolean updateRegistration(int localFieldId, Object observable, CreateWeakListener listenerCreator) {
        if (observable == null) {
            return unregisterFrom(localFieldId);
        }
        WeakListener listener = this.mLocalFieldObservers[localFieldId];
        if (listener == null) {
            registerTo(localFieldId, observable, listenerCreator);
            return true;
        } else if (listener.getTarget() == observable) {
            return false;
        } else {
            unregisterFrom(localFieldId);
            registerTo(localFieldId, observable, listenerCreator);
            return true;
        }
    }

    public boolean updateRegistration(int localFieldId, Observable observable) {
        return updateRegistration(localFieldId, observable, CREATE_PROPERTY_LISTENER);
    }

    public boolean updateRegistration(int localFieldId, ObservableList observable) {
        return updateRegistration(localFieldId, observable, CREATE_LIST_LISTENER);
    }

    protected boolean updateRegistration(int localFieldId, ObservableMap observable) {
        return updateRegistration(localFieldId, observable, CREATE_MAP_LISTENER);
    }

    protected boolean updateLiveDataRegistration(int localFieldId, LiveData<?> observable) {
        this.mInLiveDataRegisterObserver = true;
        try {
            return updateRegistration(localFieldId, observable, CREATE_LIVE_DATA_LISTENER);
        } finally {
            this.mInLiveDataRegisterObserver = false;
        }
    }

    protected void ensureBindingComponentIsNotNull(Class<?> oneExample) {
        if (this.mBindingComponent == null) {
            String errorMessage = "Required DataBindingComponent is null in class " + getClass().getSimpleName() + ". A BindingAdapter in " + oneExample.getCanonicalName() + " is not static and requires an object to use, retrieved from the DataBindingComponent. If you don't use an inflation method taking a DataBindingComponent, use DataBindingUtil.setDefaultComponent or make all BindingAdapter methods static.";
            throw new IllegalStateException(errorMessage);
        }
    }

    protected void registerTo(int localFieldId, Object observable, CreateWeakListener listenerCreator) {
        if (observable == null) {
            return;
        }
        WeakListener listener = this.mLocalFieldObservers[localFieldId];
        if (listener == null) {
            listener = listenerCreator.create(this, localFieldId);
            this.mLocalFieldObservers[localFieldId] = listener;
            LifecycleOwner lifecycleOwner = this.mLifecycleOwner;
            if (lifecycleOwner != null) {
                listener.setLifecycleOwner(lifecycleOwner);
            }
        }
        listener.setTarget(observable);
    }

    public static ViewDataBinding bind(DataBindingComponent bindingComponent, View view, int layoutId) {
        return DataBindingUtil.bind(bindingComponent, view, layoutId);
    }

    public static Object[] mapBindings(DataBindingComponent bindingComponent, View root, int numBindings, IncludedLayouts includes, SparseIntArray viewsWithIds) {
        Object[] bindings = new Object[numBindings];
        mapBindings(bindingComponent, root, bindings, includes, viewsWithIds, true);
        return bindings;
    }

    protected static boolean parse(String str, boolean fallback) {
        if (str == null) {
            return fallback;
        }
        return Boolean.parseBoolean(str);
    }

    protected static byte parse(String str, byte fallback) {
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    protected static short parse(String str, short fallback) {
        try {
            return Short.parseShort(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    protected static int parse(String str, int fallback) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    protected static long parse(String str, long fallback) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    protected static float parse(String str, float fallback) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    protected static double parse(String str, double fallback) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    protected static char parse(String str, char fallback) {
        if (str == null || str.isEmpty()) {
            return fallback;
        }
        return str.charAt(0);
    }

    protected static int getColorFromResource(View view, int resourceId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return view.getContext().getColor(resourceId);
        }
        return view.getResources().getColor(resourceId);
    }

    protected static ColorStateList getColorStateListFromResource(View view, int resourceId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return view.getContext().getColorStateList(resourceId);
        }
        return view.getResources().getColorStateList(resourceId);
    }

    protected static Drawable getDrawableFromResource(View view, int resourceId) {
        if (Build.VERSION.SDK_INT >= 21) {
            return view.getContext().getDrawable(resourceId);
        }
        return view.getResources().getDrawable(resourceId);
    }

    protected static <T> T getFromArray(T[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return null;
        }
        return arr[index];
    }

    protected static <T> void setTo(T[] arr, int index, T value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static boolean getFromArray(boolean[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return false;
        }
        return arr[index];
    }

    protected static void setTo(boolean[] arr, int index, boolean value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static byte getFromArray(byte[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return (byte) 0;
        }
        return arr[index];
    }

    protected static void setTo(byte[] arr, int index, byte value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static short getFromArray(short[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return (short) 0;
        }
        return arr[index];
    }

    protected static void setTo(short[] arr, int index, short value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static char getFromArray(char[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return (char) 0;
        }
        return arr[index];
    }

    protected static void setTo(char[] arr, int index, char value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static int getFromArray(int[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return 0;
        }
        return arr[index];
    }

    protected static void setTo(int[] arr, int index, int value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static long getFromArray(long[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return 0L;
        }
        return arr[index];
    }

    protected static void setTo(long[] arr, int index, long value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static float getFromArray(float[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return 0.0f;
        }
        return arr[index];
    }

    protected static void setTo(float[] arr, int index, float value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static double getFromArray(double[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return 0.0d;
        }
        return arr[index];
    }

    protected static void setTo(double[] arr, int index, double value) {
        if (arr == null || index < 0 || index >= arr.length) {
            return;
        }
        arr[index] = value;
    }

    protected static <T> T getFromList(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    protected static <T> void setTo(List<T> list, int index, T value) {
        if (list == null || index < 0 || index >= list.size()) {
            return;
        }
        list.set(index, value);
    }

    protected static <T> T getFromList(SparseArray<T> list, int index) {
        if (list == null || index < 0) {
            return null;
        }
        return list.get(index);
    }

    protected static <T> void setTo(SparseArray<T> list, int index, T value) {
        if (list == null || index < 0 || index >= list.size()) {
            return;
        }
        list.put(index, value);
    }

    @TargetApi(16)
    protected static <T> T getFromList(LongSparseArray<T> list, int index) {
        if (list == null || index < 0) {
            return null;
        }
        return list.get(index);
    }

    @TargetApi(16)
    protected static <T> void setTo(LongSparseArray<T> list, int index, T value) {
        if (list == null || index < 0 || index >= list.size()) {
            return;
        }
        list.put(index, value);
    }

    protected static <T> T getFromList(android.support.p000v4.util.LongSparseArray<T> list, int index) {
        if (list == null || index < 0) {
            return null;
        }
        return list.get(index);
    }

    protected static <T> void setTo(android.support.p000v4.util.LongSparseArray<T> list, int index, T value) {
        if (list == null || index < 0 || index >= list.size()) {
            return;
        }
        list.put(index, value);
    }

    protected static boolean getFromList(SparseBooleanArray list, int index) {
        if (list == null || index < 0) {
            return false;
        }
        return list.get(index);
    }

    protected static void setTo(SparseBooleanArray list, int index, boolean value) {
        if (list == null || index < 0 || index >= list.size()) {
            return;
        }
        list.put(index, value);
    }

    protected static int getFromList(SparseIntArray list, int index) {
        if (list == null || index < 0) {
            return 0;
        }
        return list.get(index);
    }

    protected static void setTo(SparseIntArray list, int index, int value) {
        if (list == null || index < 0 || index >= list.size()) {
            return;
        }
        list.put(index, value);
    }

    @TargetApi(18)
    protected static long getFromList(SparseLongArray list, int index) {
        if (list == null || index < 0) {
            return 0L;
        }
        return list.get(index);
    }

    @TargetApi(18)
    protected static void setTo(SparseLongArray list, int index, long value) {
        if (list == null || index < 0 || index >= list.size()) {
            return;
        }
        list.put(index, value);
    }

    protected static <K, T> T getFrom(Map<K, T> map, K key) {
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    protected static <K, T> void setTo(Map<K, T> map, K key, T value) {
        if (map == null) {
            return;
        }
        map.put(key, value);
    }

    protected static void setBindingInverseListener(ViewDataBinding binder, InverseBindingListener oldListener, PropertyChangedInverseListener listener) {
        if (oldListener != listener) {
            if (oldListener != null) {
                binder.removeOnPropertyChangedCallback((PropertyChangedInverseListener) oldListener);
            }
            if (listener != null) {
                binder.addOnPropertyChangedCallback(listener);
            }
        }
    }

    protected static int safeUnbox(Integer boxed) {
        if (boxed == null) {
            return 0;
        }
        return boxed.intValue();
    }

    protected static long safeUnbox(Long boxed) {
        if (boxed == null) {
            return 0L;
        }
        return boxed.longValue();
    }

    protected static short safeUnbox(Short boxed) {
        if (boxed == null) {
            return (short) 0;
        }
        return boxed.shortValue();
    }

    protected static byte safeUnbox(Byte boxed) {
        if (boxed == null) {
            return (byte) 0;
        }
        return boxed.byteValue();
    }

    protected static char safeUnbox(Character boxed) {
        if (boxed == null) {
            return (char) 0;
        }
        return boxed.charValue();
    }

    protected static double safeUnbox(Double boxed) {
        if (boxed == null) {
            return 0.0d;
        }
        return boxed.doubleValue();
    }

    protected static float safeUnbox(Float boxed) {
        if (boxed == null) {
            return 0.0f;
        }
        return boxed.floatValue();
    }

    protected static boolean safeUnbox(Boolean boxed) {
        if (boxed == null) {
            return false;
        }
        return boxed.booleanValue();
    }

    protected void setContainedBinding(ViewDataBinding included) {
        if (included != null) {
            included.mContainingBinding = this;
        }
    }

    protected static Object[] mapBindings(DataBindingComponent bindingComponent, View[] roots, int numBindings, IncludedLayouts includes, SparseIntArray viewsWithIds) {
        Object[] bindings = new Object[numBindings];
        for (View view : roots) {
            mapBindings(bindingComponent, view, bindings, includes, viewsWithIds, true);
        }
        return bindings;
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x0138  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0149  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void mapBindings(android.databinding.DataBindingComponent r23, android.view.View r24, java.lang.Object[] r25, android.databinding.ViewDataBinding.IncludedLayouts r26, android.util.SparseIntArray r27, boolean r28) {
        /*
            Method dump skipped, instructions count: 347
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.databinding.ViewDataBinding.mapBindings(android.databinding.DataBindingComponent, android.view.View, java.lang.Object[], android.databinding.ViewDataBinding$IncludedLayouts, android.util.SparseIntArray, boolean):void");
    }

    private static int findIncludeIndex(String tag, int minInclude, IncludedLayouts included, int includedIndex) {
        int slashIndex = tag.indexOf(47);
        CharSequence layoutName = tag.subSequence(slashIndex + 1, tag.length() - 2);
        String[] layouts = included.layouts[includedIndex];
        int length = layouts.length;
        for (int i = minInclude; i < length; i++) {
            String layout = layouts[i];
            if (TextUtils.equals(layoutName, layout)) {
                return i;
            }
        }
        return -1;
    }

    private static int findLastMatching(ViewGroup viewGroup, int firstIncludedIndex) {
        View firstView = viewGroup.getChildAt(firstIncludedIndex);
        String firstViewTag = (String) firstView.getTag();
        String tagBase = firstViewTag.substring(0, firstViewTag.length() - 1);
        int tagSequenceIndex = tagBase.length();
        int count = viewGroup.getChildCount();
        int max = firstIncludedIndex;
        for (int i = firstIncludedIndex + 1; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            Object objTag = view.getTag();
            String tag = objTag instanceof String ? (String) view.getTag() : null;
            if (tag != null && tag.startsWith(tagBase)) {
                if (tag.length() == firstViewTag.length() && tag.charAt(tag.length() - 1) == '0') {
                    return max;
                }
                if (isNumeric(tag, tagSequenceIndex)) {
                    max = i;
                }
            }
        }
        return max;
    }

    private static boolean isNumeric(String tag, int startIndex) {
        int length = tag.length();
        if (length == startIndex) {
            return false;
        }
        for (int i = startIndex; i < length; i++) {
            if (!Character.isDigit(tag.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static int parseTagInt(String str, int startIndex) {
        int end = str.length();
        int val = 0;
        for (int i = startIndex; i < end; i++) {
            char c = str.charAt(i);
            val = (val * 10) + (c - '0');
        }
        return val;
    }

    public static void processReferenceQueue() {
        while (true) {
            Reference<? extends ViewDataBinding> ref = sReferenceQueue.poll();
            if (ref != null) {
                if (ref instanceof WeakListener) {
                    WeakListener listener = (WeakListener) ref;
                    listener.unregister();
                }
            } else {
                return;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class WeakListener<T> extends WeakReference<ViewDataBinding> {
        protected final int mLocalFieldId;
        private final ObservableReference<T> mObservable;
        private T mTarget;

        public WeakListener(ViewDataBinding binder, int localFieldId, ObservableReference<T> observable) {
            super(binder, ViewDataBinding.sReferenceQueue);
            this.mLocalFieldId = localFieldId;
            this.mObservable = observable;
        }

        public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
            this.mObservable.setLifecycleOwner(lifecycleOwner);
        }

        public void setTarget(T object) {
            unregister();
            this.mTarget = object;
            T t = this.mTarget;
            if (t != null) {
                this.mObservable.addListener(t);
            }
        }

        public boolean unregister() {
            boolean unregistered = false;
            T t = this.mTarget;
            if (t != null) {
                this.mObservable.removeListener(t);
                unregistered = true;
            }
            this.mTarget = null;
            return unregistered;
        }

        public T getTarget() {
            return this.mTarget;
        }

        protected ViewDataBinding getBinder() {
            ViewDataBinding binder = (ViewDataBinding) get();
            if (binder == null) {
                unregister();
            }
            return binder;
        }
    }

    /* loaded from: classes.dex */
    private static class WeakPropertyListener extends Observable.OnPropertyChangedCallback implements ObservableReference<Observable> {
        final WeakListener<Observable> mListener;

        public WeakPropertyListener(ViewDataBinding binder, int localFieldId) {
            this.mListener = new WeakListener<>(binder, localFieldId, this);
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public WeakListener<Observable> getListener() {
            return this.mListener;
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void addListener(Observable target) {
            target.addOnPropertyChangedCallback(this);
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void removeListener(Observable target) {
            target.removeOnPropertyChangedCallback(this);
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        }

        @Override // android.databinding.Observable.OnPropertyChangedCallback
        public void onPropertyChanged(Observable sender, int propertyId) {
            ViewDataBinding binder = this.mListener.getBinder();
            if (binder == null) {
                return;
            }
            Observable obj = this.mListener.getTarget();
            if (obj == sender) {
                binder.handleFieldChange(this.mListener.mLocalFieldId, sender, propertyId);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class WeakListListener extends ObservableList.OnListChangedCallback implements ObservableReference<ObservableList> {
        final WeakListener<ObservableList> mListener;

        public WeakListListener(ViewDataBinding binder, int localFieldId) {
            this.mListener = new WeakListener<>(binder, localFieldId, this);
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public WeakListener<ObservableList> getListener() {
            return this.mListener;
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void addListener(ObservableList target) {
            target.addOnListChangedCallback(this);
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void removeListener(ObservableList target) {
            target.removeOnListChangedCallback(this);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onChanged(ObservableList sender) {
            ObservableList target;
            ViewDataBinding binder = this.mListener.getBinder();
            if (binder != null && (target = this.mListener.getTarget()) == sender) {
                binder.handleFieldChange(this.mListener.mLocalFieldId, target, 0);
            }
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            onChanged(sender);
        }

        @Override // android.databinding.ObservableList.OnListChangedCallback
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }
    }

    /* loaded from: classes.dex */
    private static class WeakMapListener extends ObservableMap.OnMapChangedCallback implements ObservableReference<ObservableMap> {
        final WeakListener<ObservableMap> mListener;

        public WeakMapListener(ViewDataBinding binder, int localFieldId) {
            this.mListener = new WeakListener<>(binder, localFieldId, this);
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public WeakListener<ObservableMap> getListener() {
            return this.mListener;
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void addListener(ObservableMap target) {
            target.addOnMapChangedCallback(this);
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void removeListener(ObservableMap target) {
            target.removeOnMapChangedCallback(this);
        }

        @Override // android.databinding.ObservableMap.OnMapChangedCallback
        public void onMapChanged(ObservableMap sender, Object key) {
            ViewDataBinding binder = this.mListener.getBinder();
            if (binder != null && sender == this.mListener.getTarget()) {
                binder.handleFieldChange(this.mListener.mLocalFieldId, sender, 0);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class LiveDataListener implements Observer, ObservableReference<LiveData<?>> {
        LifecycleOwner mLifecycleOwner;
        final WeakListener<LiveData<?>> mListener;

        public LiveDataListener(ViewDataBinding binder, int localFieldId) {
            this.mListener = new WeakListener<>(binder, localFieldId, this);
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
            LiveData<?> liveData = this.mListener.getTarget();
            if (liveData != null) {
                if (this.mLifecycleOwner != null) {
                    liveData.removeObserver(this);
                }
                if (lifecycleOwner != null) {
                    liveData.observe(lifecycleOwner, this);
                }
            }
            this.mLifecycleOwner = lifecycleOwner;
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public WeakListener<LiveData<?>> getListener() {
            return this.mListener;
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void addListener(LiveData<?> target) {
            LifecycleOwner lifecycleOwner = this.mLifecycleOwner;
            if (lifecycleOwner != null) {
                target.observe(lifecycleOwner, this);
            }
        }

        @Override // android.databinding.ViewDataBinding.ObservableReference
        public void removeListener(LiveData<?> target) {
            target.removeObserver(this);
        }

        @Override // android.arch.lifecycle.Observer
        public void onChanged(@Nullable Object o) {
            ViewDataBinding binder = this.mListener.getBinder();
            binder.handleFieldChange(this.mListener.mLocalFieldId, this.mListener.getTarget(), 0);
        }
    }

    /* loaded from: classes.dex */
    public static class IncludedLayouts {
        public final int[][] indexes;
        public final int[][] layoutIds;
        public final String[][] layouts;

        public IncludedLayouts(int bindingCount) {
            this.layouts = new String[bindingCount];
            this.indexes = new int[bindingCount];
            this.layoutIds = new int[bindingCount];
        }

        public void setIncludes(int index, String[] layouts, int[] indexes, int[] layoutIds) {
            this.layouts[index] = layouts;
            this.indexes[index] = indexes;
            this.layoutIds[index] = layoutIds;
        }
    }

    /* loaded from: classes.dex */
    protected static abstract class PropertyChangedInverseListener extends Observable.OnPropertyChangedCallback implements InverseBindingListener {
        final int mPropertyId;

        public PropertyChangedInverseListener(int propertyId) {
            this.mPropertyId = propertyId;
        }

        @Override // android.databinding.Observable.OnPropertyChangedCallback
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == this.mPropertyId || propertyId == 0) {
                onChange();
            }
        }
    }

    /* loaded from: classes.dex */
    public class OnStartListener implements LifecycleObserver {
        private OnStartListener() {
            ViewDataBinding.this = this$0;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onStart() {
            ViewDataBinding.this.executePendingBindings();
        }
    }
}