package android.support.v7.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.os.TraceCompat;
import android.support.v4.util.Preconditions;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v7.recyclerview.R;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.AdapterHelper;
import android.support.v7.widget.ChildHelper;
import android.support.v7.widget.GapWorker;
import android.support.v7.widget.ViewBoundsCheck;
import android.support.v7.widget.ViewInfoStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.OverScroller;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;

/* loaded from: classes.dex */
public class RecyclerView extends ViewGroup implements ScrollingView, NestedScrollingChild2 {
    static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC;
    static final boolean ALLOW_THREAD_GAP_WORK;
    static final boolean DEBUG = false;
    static final int DEFAULT_ORIENTATION = 1;
    static final boolean DISPATCH_TEMP_DETACH = false;
    private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION;
    static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
    static final long FOREVER_NS = Long.MAX_VALUE;
    public static final int HORIZONTAL = 0;
    private static final boolean IGNORE_DETACHED_FOCUSED_CHILD;
    private static final int INVALID_POINTER = -1;
    public static final int INVALID_TYPE = -1;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
    static final int MAX_SCROLL_DURATION = 2000;
    public static final long NO_ID = -1;
    public static final int NO_POSITION = -1;
    static final boolean POST_UPDATES_ON_ANIMATION;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    static final String TAG = "RecyclerView";
    public static final int TOUCH_SLOP_DEFAULT = 0;
    public static final int TOUCH_SLOP_PAGING = 1;
    static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
    static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
    private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
    static final String TRACE_NESTED_PREFETCH_TAG = "RV Nested Prefetch";
    private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
    private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
    static final String TRACE_PREFETCH_TAG = "RV Prefetch";
    static final String TRACE_SCROLL_TAG = "RV Scroll";
    static final boolean VERBOSE_TRACING = false;
    public static final int VERTICAL = 1;
    static final Interpolator sQuinticInterpolator;
    RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
    private final AccessibilityManager mAccessibilityManager;
    private OnItemTouchListener mActiveOnItemTouchListener;
    Adapter mAdapter;
    AdapterHelper mAdapterHelper;
    boolean mAdapterUpdateDuringMeasure;
    private EdgeEffect mBottomGlow;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback;
    ChildHelper mChildHelper;
    boolean mClipToPadding;
    boolean mDataSetHasChangedAfterLayout;
    boolean mDispatchItemsChangedEvent;
    private int mDispatchScrollCounter;
    private int mEatenAccessibilityChangeFlags;
    @NonNull
    private EdgeEffectFactory mEdgeEffectFactory;
    boolean mEnableFastScroller;
    @VisibleForTesting
    boolean mFirstLayoutComplete;
    GapWorker mGapWorker;
    boolean mHasFixedSize;
    private boolean mIgnoreMotionEventTillDown;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private int mInterceptRequestLayoutDepth;
    boolean mIsAttached;
    ItemAnimator mItemAnimator;
    private ItemAnimator.ItemAnimatorListener mItemAnimatorListener;
    private Runnable mItemAnimatorRunner;
    final ArrayList<ItemDecoration> mItemDecorations;
    boolean mItemsAddedOrRemoved;
    boolean mItemsChanged;
    private int mLastTouchX;
    private int mLastTouchY;
    @VisibleForTesting
    LayoutManager mLayout;
    boolean mLayoutFrozen;
    private int mLayoutOrScrollCounter;
    boolean mLayoutWasDefered;
    private EdgeEffect mLeftGlow;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private final int[] mMinMaxLayoutPositions;
    private final int[] mNestedOffsets;
    private final RecyclerViewDataObserver mObserver;
    private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
    private OnFlingListener mOnFlingListener;
    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners;
    @VisibleForTesting
    final List<ViewHolder> mPendingAccessibilityImportanceChange;
    private SavedState mPendingSavedState;
    boolean mPostedAnimatorRunner;
    GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
    private boolean mPreserveFocusAfterLayout;
    final Recycler mRecycler;
    RecyclerListener mRecyclerListener;
    private EdgeEffect mRightGlow;
    private float mScaledHorizontalScrollFactor;
    private float mScaledVerticalScrollFactor;
    final int[] mScrollConsumed;
    private OnScrollListener mScrollListener;
    private List<OnScrollListener> mScrollListeners;
    private final int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    final int[] mScrollStepConsumed;
    private NestedScrollingChildHelper mScrollingChildHelper;
    final State mState;
    final Rect mTempRect;
    private final Rect mTempRect2;
    final RectF mTempRectF;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    final Runnable mUpdateChildViewsRunnable;
    private VelocityTracker mVelocityTracker;
    final ViewFlinger mViewFlinger;
    private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
    final ViewInfoStore mViewInfoStore;
    private static final int[] NESTED_SCROLLING_ATTRS = {16843830};
    private static final int[] CLIP_TO_PADDING_ATTR = {16842987};

    /* loaded from: classes.dex */
    public interface ChildDrawingOrderCallback {
        int onGetChildDrawingOrder(int i, int i2);
    }

    /* loaded from: classes.dex */
    public interface OnChildAttachStateChangeListener {
        void onChildViewAttachedToWindow(@NonNull View view);

        void onChildViewDetachedFromWindow(@NonNull View view);
    }

    /* loaded from: classes.dex */
    public static abstract class OnFlingListener {
        public abstract boolean onFling(int i, int i2);
    }

    /* loaded from: classes.dex */
    public interface OnItemTouchListener {
        boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent);

        void onRequestDisallowInterceptTouchEvent(boolean z);

        void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent);
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public @interface Orientation {
    }

    /* loaded from: classes.dex */
    public interface RecyclerListener {
        void onViewRecycled(@NonNull ViewHolder viewHolder);
    }

    /* loaded from: classes.dex */
    public static abstract class ViewCacheExtension {
        @Nullable
        public abstract View getViewForPositionAndType(@NonNull Recycler recycler, int i, int i2);
    }

    static {
        FORCE_INVALIDATE_DISPLAY_LIST = Build.VERSION.SDK_INT == 18 || Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20;
        ALLOW_SIZE_IN_UNSPECIFIED_SPEC = Build.VERSION.SDK_INT >= 23;
        POST_UPDATES_ON_ANIMATION = Build.VERSION.SDK_INT >= 16;
        ALLOW_THREAD_GAP_WORK = Build.VERSION.SDK_INT >= 21;
        FORCE_ABS_FOCUS_SEARCH_DIRECTION = Build.VERSION.SDK_INT <= 15;
        IGNORE_DETACHED_FOCUSED_CHILD = Build.VERSION.SDK_INT <= 15;
        LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[]{Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE};
        sQuinticInterpolator = new Interpolator() { // from class: android.support.v7.widget.RecyclerView.3
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float t) {
                float t2 = t - 1.0f;
                return (t2 * t2 * t2 * t2 * t2) + 1.0f;
            }
        };
    }

    public RecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mObserver = new RecyclerViewDataObserver();
        this.mRecycler = new Recycler();
        this.mViewInfoStore = new ViewInfoStore();
        this.mUpdateChildViewsRunnable = new Runnable() { // from class: android.support.v7.widget.RecyclerView.1
            @Override // java.lang.Runnable
            public void run() {
                if (!RecyclerView.this.mFirstLayoutComplete || RecyclerView.this.isLayoutRequested()) {
                    return;
                }
                if (!RecyclerView.this.mIsAttached) {
                    RecyclerView.this.requestLayout();
                } else if (RecyclerView.this.mLayoutFrozen) {
                    RecyclerView.this.mLayoutWasDefered = true;
                } else {
                    RecyclerView.this.consumePendingUpdateOperations();
                }
            }
        };
        this.mTempRect = new Rect();
        this.mTempRect2 = new Rect();
        this.mTempRectF = new RectF();
        this.mItemDecorations = new ArrayList<>();
        this.mOnItemTouchListeners = new ArrayList<>();
        this.mInterceptRequestLayoutDepth = 0;
        this.mDataSetHasChangedAfterLayout = false;
        this.mDispatchItemsChangedEvent = false;
        this.mLayoutOrScrollCounter = 0;
        this.mDispatchScrollCounter = 0;
        this.mEdgeEffectFactory = new EdgeEffectFactory();
        this.mItemAnimator = new DefaultItemAnimator();
        this.mScrollState = 0;
        this.mScrollPointerId = -1;
        this.mScaledHorizontalScrollFactor = Float.MIN_VALUE;
        this.mScaledVerticalScrollFactor = Float.MIN_VALUE;
        this.mPreserveFocusAfterLayout = true;
        this.mViewFlinger = new ViewFlinger();
        this.mPrefetchRegistry = ALLOW_THREAD_GAP_WORK ? new GapWorker.LayoutPrefetchRegistryImpl() : null;
        this.mState = new State();
        this.mItemsAddedOrRemoved = false;
        this.mItemsChanged = false;
        this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
        this.mPostedAnimatorRunner = false;
        this.mMinMaxLayoutPositions = new int[2];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mNestedOffsets = new int[2];
        this.mScrollStepConsumed = new int[2];
        this.mPendingAccessibilityImportanceChange = new ArrayList();
        this.mItemAnimatorRunner = new Runnable() { // from class: android.support.v7.widget.RecyclerView.2
            @Override // java.lang.Runnable
            public void run() {
                if (RecyclerView.this.mItemAnimator != null) {
                    RecyclerView.this.mItemAnimator.runPendingAnimations();
                }
                RecyclerView.this.mPostedAnimatorRunner = false;
            }
        };
        this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback() { // from class: android.support.v7.widget.RecyclerView.4
            @Override // android.support.v7.widget.ViewInfoStore.ProcessCallback
            public void processDisappeared(ViewHolder viewHolder, @NonNull ItemAnimator.ItemHolderInfo info, @Nullable ItemAnimator.ItemHolderInfo postInfo) {
                RecyclerView.this.mRecycler.unscrapView(viewHolder);
                RecyclerView.this.animateDisappearance(viewHolder, info, postInfo);
            }

            @Override // android.support.v7.widget.ViewInfoStore.ProcessCallback
            public void processAppeared(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo preInfo, ItemAnimator.ItemHolderInfo info) {
                RecyclerView.this.animateAppearance(viewHolder, preInfo, info);
            }

            @Override // android.support.v7.widget.ViewInfoStore.ProcessCallback
            public void processPersistent(ViewHolder viewHolder, @NonNull ItemAnimator.ItemHolderInfo preInfo, @NonNull ItemAnimator.ItemHolderInfo postInfo) {
                viewHolder.setIsRecyclable(false);
                if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
                    if (RecyclerView.this.mItemAnimator.animateChange(viewHolder, viewHolder, preInfo, postInfo)) {
                        RecyclerView.this.postAnimationRunner();
                    }
                } else if (RecyclerView.this.mItemAnimator.animatePersistence(viewHolder, preInfo, postInfo)) {
                    RecyclerView.this.postAnimationRunner();
                }
            }

            @Override // android.support.v7.widget.ViewInfoStore.ProcessCallback
            public void unused(ViewHolder viewHolder) {
                RecyclerView.this.mLayout.removeAndRecycleView(viewHolder.itemView, RecyclerView.this.mRecycler);
            }
        };
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, CLIP_TO_PADDING_ATTR, defStyle, 0);
            this.mClipToPadding = a.getBoolean(0, true);
            a.recycle();
        } else {
            this.mClipToPadding = true;
        }
        setScrollContainer(true);
        setFocusableInTouchMode(true);
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.mTouchSlop = vc.getScaledTouchSlop();
        this.mScaledHorizontalScrollFactor = ViewConfigurationCompat.getScaledHorizontalScrollFactor(vc, context);
        this.mScaledVerticalScrollFactor = ViewConfigurationCompat.getScaledVerticalScrollFactor(vc, context);
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        setWillNotDraw(getOverScrollMode() == 2);
        this.mItemAnimator.setListener(this.mItemAnimatorListener);
        initAdapterManager();
        initChildrenHelper();
        initAutofill();
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
        boolean nestedScrollingEnabled = true;
        if (attrs != null) {
            TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.RecyclerView, defStyle, 0);
            String layoutManagerName = a2.getString(R.styleable.RecyclerView_layoutManager);
            int descendantFocusability = a2.getInt(R.styleable.RecyclerView_android_descendantFocusability, -1);
            if (descendantFocusability == -1) {
                setDescendantFocusability(262144);
            }
            this.mEnableFastScroller = a2.getBoolean(R.styleable.RecyclerView_fastScrollEnabled, false);
            if (this.mEnableFastScroller) {
                StateListDrawable verticalThumbDrawable = (StateListDrawable) a2.getDrawable(R.styleable.RecyclerView_fastScrollVerticalThumbDrawable);
                Drawable verticalTrackDrawable = a2.getDrawable(R.styleable.RecyclerView_fastScrollVerticalTrackDrawable);
                StateListDrawable horizontalThumbDrawable = (StateListDrawable) a2.getDrawable(R.styleable.RecyclerView_fastScrollHorizontalThumbDrawable);
                Drawable horizontalTrackDrawable = a2.getDrawable(R.styleable.RecyclerView_fastScrollHorizontalTrackDrawable);
                initFastScroller(verticalThumbDrawable, verticalTrackDrawable, horizontalThumbDrawable, horizontalTrackDrawable);
            }
            a2.recycle();
            createLayoutManager(context, layoutManagerName, attrs, defStyle, 0);
            if (Build.VERSION.SDK_INT >= 21) {
                TypedArray a3 = context.obtainStyledAttributes(attrs, NESTED_SCROLLING_ATTRS, defStyle, 0);
                boolean nestedScrollingEnabled2 = a3.getBoolean(0, true);
                a3.recycle();
                nestedScrollingEnabled = nestedScrollingEnabled2;
            }
        } else {
            setDescendantFocusability(262144);
        }
        setNestedScrollingEnabled(nestedScrollingEnabled);
    }

    String exceptionLabel() {
        return " " + super.toString() + ", adapter:" + this.mAdapter + ", layout:" + this.mLayout + ", context:" + getContext();
    }

    @SuppressLint({"InlinedApi"})
    private void initAutofill() {
        if (ViewCompat.getImportantForAutofill(this) == 0) {
            ViewCompat.setImportantForAutofill(this, 8);
        }
    }

    @Nullable
    public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public void setAccessibilityDelegateCompat(@Nullable RecyclerViewAccessibilityDelegate accessibilityDelegate) {
        this.mAccessibilityDelegate = accessibilityDelegate;
        ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityDelegate);
    }

    private void createLayoutManager(Context context, String className, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        ClassLoader classLoader;
        Constructor constructor;
        if (className != null) {
            String className2 = className.trim();
            if (!className2.isEmpty()) {
                String className3 = getFullClassName(context, className2);
                try {
                    if (isInEditMode()) {
                        classLoader = getClass().getClassLoader();
                    } else {
                        classLoader = context.getClassLoader();
                    }
                    Class<? extends U> asSubclass = classLoader.loadClass(className3).asSubclass(LayoutManager.class);
                    Object[] constructorArgs = null;
                    try {
                        Constructor constructor2 = asSubclass.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
                        constructorArgs = new Object[]{context, attrs, Integer.valueOf(defStyleAttr), Integer.valueOf(defStyleRes)};
                        constructor = constructor2;
                    } catch (NoSuchMethodException e) {
                        try {
                            constructor = asSubclass.getConstructor(new Class[0]);
                        } catch (NoSuchMethodException e1) {
                            e1.initCause(e);
                            throw new IllegalStateException(attrs.getPositionDescription() + ": Error creating LayoutManager " + className3, e1);
                        }
                    }
                    constructor.setAccessible(true);
                    setLayoutManager((LayoutManager) constructor.newInstance(constructorArgs));
                } catch (ClassCastException e2) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Class is not a LayoutManager " + className3, e2);
                } catch (ClassNotFoundException e3) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Unable to find LayoutManager " + className3, e3);
                } catch (IllegalAccessException e4) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Cannot access non-public constructor " + className3, e4);
                } catch (InstantiationException e5) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Could not instantiate the LayoutManager: " + className3, e5);
                } catch (InvocationTargetException e6) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Could not instantiate the LayoutManager: " + className3, e6);
                }
            }
        }
    }

    private String getFullClassName(Context context, String className) {
        if (className.charAt(0) == '.') {
            return context.getPackageName() + className;
        } else if (className.contains(".")) {
            return className;
        } else {
            return RecyclerView.class.getPackage().getName() + '.' + className;
        }
    }

    private void initChildrenHelper() {
        this.mChildHelper = new ChildHelper(new ChildHelper.Callback() { // from class: android.support.v7.widget.RecyclerView.5
            @Override // android.support.v7.widget.ChildHelper.Callback
            public int getChildCount() {
                return RecyclerView.this.getChildCount();
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void addView(View child, int index) {
                RecyclerView.this.addView(child, index);
                RecyclerView.this.dispatchChildAttached(child);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public int indexOfChild(View view) {
                return RecyclerView.this.indexOfChild(view);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void removeViewAt(int index) {
                View child = RecyclerView.this.getChildAt(index);
                if (child != null) {
                    RecyclerView.this.dispatchChildDetached(child);
                    child.clearAnimation();
                }
                RecyclerView.this.removeViewAt(index);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public View getChildAt(int offset) {
                return RecyclerView.this.getChildAt(offset);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void removeAllViews() {
                int count = getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    RecyclerView.this.dispatchChildDetached(child);
                    child.clearAnimation();
                }
                RecyclerView.this.removeAllViews();
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public ViewHolder getChildViewHolder(View view) {
                return RecyclerView.getChildViewHolderInt(view);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void attachViewToParent(View child, int index, ViewGroup.LayoutParams layoutParams) {
                ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
                if (vh != null) {
                    if (!vh.isTmpDetached() && !vh.shouldIgnore()) {
                        throw new IllegalArgumentException("Called attach on a child which is not detached: " + vh + RecyclerView.this.exceptionLabel());
                    }
                    vh.clearTmpDetachFlag();
                }
                RecyclerView.this.attachViewToParent(child, index, layoutParams);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void detachViewFromParent(int offset) {
                ViewHolder vh;
                View view = getChildAt(offset);
                if (view != null && (vh = RecyclerView.getChildViewHolderInt(view)) != null) {
                    if (vh.isTmpDetached() && !vh.shouldIgnore()) {
                        throw new IllegalArgumentException("called detach on an already detached child " + vh + RecyclerView.this.exceptionLabel());
                    }
                    vh.addFlags(256);
                }
                RecyclerView.this.detachViewFromParent(offset);
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void onEnteredHiddenState(View child) {
                ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
                if (vh != null) {
                    vh.onEnteredHiddenState(RecyclerView.this);
                }
            }

            @Override // android.support.v7.widget.ChildHelper.Callback
            public void onLeftHiddenState(View child) {
                ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
                if (vh != null) {
                    vh.onLeftHiddenState(RecyclerView.this);
                }
            }
        });
    }

    void initAdapterManager() {
        this.mAdapterHelper = new AdapterHelper(new AdapterHelper.Callback() { // from class: android.support.v7.widget.RecyclerView.6
            @Override // android.support.v7.widget.AdapterHelper.Callback
            public ViewHolder findViewHolder(int position) {
                ViewHolder vh = RecyclerView.this.findViewHolderForPosition(position, true);
                if (vh == null || RecyclerView.this.mChildHelper.isHidden(vh.itemView)) {
                    return null;
                }
                return vh;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void offsetPositionsForRemovingInvisible(int start, int count) {
                RecyclerView.this.offsetPositionRecordsForRemove(start, count, true);
                RecyclerView recyclerView = RecyclerView.this;
                recyclerView.mItemsAddedOrRemoved = true;
                recyclerView.mState.mDeletedInvisibleItemCountSincePreviousLayout += count;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void offsetPositionsForRemovingLaidOutOrNewView(int positionStart, int itemCount) {
                RecyclerView.this.offsetPositionRecordsForRemove(positionStart, itemCount, false);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void markViewHoldersUpdated(int positionStart, int itemCount, Object payload) {
                RecyclerView.this.viewRangeUpdate(positionStart, itemCount, payload);
                RecyclerView.this.mItemsChanged = true;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void onDispatchFirstPass(AdapterHelper.UpdateOp op) {
                dispatchUpdate(op);
            }

            void dispatchUpdate(AdapterHelper.UpdateOp op) {
                int i = op.cmd;
                if (i == 4) {
                    RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, op.positionStart, op.itemCount, op.payload);
                } else if (i != 8) {
                    switch (i) {
                        case 1:
                            RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, op.positionStart, op.itemCount);
                            return;
                        case 2:
                            RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, op.positionStart, op.itemCount);
                            return;
                        default:
                            return;
                    }
                } else {
                    RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, op.positionStart, op.itemCount, 1);
                }
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void onDispatchSecondPass(AdapterHelper.UpdateOp op) {
                dispatchUpdate(op);
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void offsetPositionsForAdd(int positionStart, int itemCount) {
                RecyclerView.this.offsetPositionRecordsForInsert(positionStart, itemCount);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }

            @Override // android.support.v7.widget.AdapterHelper.Callback
            public void offsetPositionsForMove(int from, int to) {
                RecyclerView.this.offsetPositionRecordsForMove(from, to);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
        });
    }

    public void setHasFixedSize(boolean hasFixedSize) {
        this.mHasFixedSize = hasFixedSize;
    }

    public boolean hasFixedSize() {
        return this.mHasFixedSize;
    }

    @Override // android.view.ViewGroup
    public void setClipToPadding(boolean clipToPadding) {
        if (clipToPadding != this.mClipToPadding) {
            invalidateGlows();
        }
        this.mClipToPadding = clipToPadding;
        super.setClipToPadding(clipToPadding);
        if (this.mFirstLayoutComplete) {
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup
    public boolean getClipToPadding() {
        return this.mClipToPadding;
    }

    public void setScrollingTouchSlop(int slopConstant) {
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        switch (slopConstant) {
            case 0:
                break;
            case 1:
                this.mTouchSlop = vc.getScaledPagingTouchSlop();
                return;
            default:
                Log.w(TAG, "setScrollingTouchSlop(): bad argument constant " + slopConstant + "; using default value");
                break;
        }
        this.mTouchSlop = vc.getScaledTouchSlop();
    }

    public void swapAdapter(@Nullable Adapter adapter, boolean removeAndRecycleExistingViews) {
        setLayoutFrozen(false);
        setAdapterInternal(adapter, true, removeAndRecycleExistingViews);
        processDataSetCompletelyChanged(true);
        requestLayout();
    }

    public void setAdapter(@Nullable Adapter adapter) {
        setLayoutFrozen(false);
        setAdapterInternal(adapter, false, true);
        processDataSetCompletelyChanged(false);
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeAndRecycleViews() {
        ItemAnimator itemAnimator = this.mItemAnimator;
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.removeAndRecycleAllViews(this.mRecycler);
            this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        }
        this.mRecycler.clear();
    }

    private void setAdapterInternal(@Nullable Adapter adapter, boolean compatibleWithPrevious, boolean removeAndRecycleViews) {
        Adapter adapter2 = this.mAdapter;
        if (adapter2 != null) {
            adapter2.unregisterAdapterDataObserver(this.mObserver);
            this.mAdapter.onDetachedFromRecyclerView(this);
        }
        if (!compatibleWithPrevious || removeAndRecycleViews) {
            removeAndRecycleViews();
        }
        this.mAdapterHelper.reset();
        Adapter oldAdapter = this.mAdapter;
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mObserver);
            adapter.onAttachedToRecyclerView(this);
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.onAdapterChanged(oldAdapter, this.mAdapter);
        }
        this.mRecycler.onAdapterChanged(oldAdapter, this.mAdapter, compatibleWithPrevious);
        this.mState.mStructureChanged = true;
    }

    @Nullable
    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public void setRecyclerListener(@Nullable RecyclerListener listener) {
        this.mRecyclerListener = listener;
    }

    @Override // android.view.View
    public int getBaseline() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            return layoutManager.getBaseline();
        }
        return super.getBaseline();
    }

    public void addOnChildAttachStateChangeListener(@NonNull OnChildAttachStateChangeListener listener) {
        if (this.mOnChildAttachStateListeners == null) {
            this.mOnChildAttachStateListeners = new ArrayList();
        }
        this.mOnChildAttachStateListeners.add(listener);
    }

    public void removeOnChildAttachStateChangeListener(@NonNull OnChildAttachStateChangeListener listener) {
        List<OnChildAttachStateChangeListener> list = this.mOnChildAttachStateListeners;
        if (list == null) {
            return;
        }
        list.remove(listener);
    }

    public void clearOnChildAttachStateChangeListeners() {
        List<OnChildAttachStateChangeListener> list = this.mOnChildAttachStateListeners;
        if (list != null) {
            list.clear();
        }
    }

    public void setLayoutManager(@Nullable LayoutManager layout) {
        if (layout == this.mLayout) {
            return;
        }
        stopScroll();
        if (this.mLayout != null) {
            ItemAnimator itemAnimator = this.mItemAnimator;
            if (itemAnimator != null) {
                itemAnimator.endAnimations();
            }
            this.mLayout.removeAndRecycleAllViews(this.mRecycler);
            this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
            this.mRecycler.clear();
            if (this.mIsAttached) {
                this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
            }
            this.mLayout.setRecyclerView(null);
            this.mLayout = null;
        } else {
            this.mRecycler.clear();
        }
        this.mChildHelper.removeAllViewsUnfiltered();
        this.mLayout = layout;
        if (layout != null) {
            if (layout.mRecyclerView != null) {
                throw new IllegalArgumentException("LayoutManager " + layout + " is already attached to a RecyclerView:" + layout.mRecyclerView.exceptionLabel());
            }
            this.mLayout.setRecyclerView(this);
            if (this.mIsAttached) {
                this.mLayout.dispatchAttachedToWindow(this);
            }
        }
        this.mRecycler.updateViewCacheSize();
        requestLayout();
    }

    public void setOnFlingListener(@Nullable OnFlingListener onFlingListener) {
        this.mOnFlingListener = onFlingListener;
    }

    @Nullable
    public OnFlingListener getOnFlingListener() {
        return this.mOnFlingListener;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null) {
            state.copyFrom(savedState);
        } else {
            LayoutManager layoutManager = this.mLayout;
            if (layoutManager != null) {
                state.mLayoutState = layoutManager.onSaveInstanceState();
            } else {
                state.mLayoutState = null;
            }
        }
        return state;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        this.mPendingSavedState = (SavedState) state;
        super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
        if (this.mLayout != null && this.mPendingSavedState.mLayoutState != null) {
            this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    private void addAnimatingView(ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        boolean alreadyParented = view.getParent() == this;
        this.mRecycler.unscrapView(getChildViewHolder(view));
        if (viewHolder.isTmpDetached()) {
            this.mChildHelper.attachViewToParent(view, -1, view.getLayoutParams(), true);
        } else if (!alreadyParented) {
            this.mChildHelper.addView(view, true);
        } else {
            this.mChildHelper.hide(view);
        }
    }

    boolean removeAnimatingView(View view) {
        startInterceptRequestLayout();
        boolean removed = this.mChildHelper.removeViewIfHidden(view);
        if (removed) {
            ViewHolder viewHolder = getChildViewHolderInt(view);
            this.mRecycler.unscrapView(viewHolder);
            this.mRecycler.recycleViewHolderInternal(viewHolder);
        }
        stopInterceptRequestLayout(!removed);
        return removed;
    }

    @Nullable
    public LayoutManager getLayoutManager() {
        return this.mLayout;
    }

    @NonNull
    public RecycledViewPool getRecycledViewPool() {
        return this.mRecycler.getRecycledViewPool();
    }

    public void setRecycledViewPool(@Nullable RecycledViewPool pool) {
        this.mRecycler.setRecycledViewPool(pool);
    }

    public void setViewCacheExtension(@Nullable ViewCacheExtension extension) {
        this.mRecycler.setViewCacheExtension(extension);
    }

    public void setItemViewCacheSize(int size) {
        this.mRecycler.setViewCacheSize(size);
    }

    public int getScrollState() {
        return this.mScrollState;
    }

    void setScrollState(int state) {
        if (state == this.mScrollState) {
            return;
        }
        this.mScrollState = state;
        if (state != 2) {
            stopScrollersInternal();
        }
        dispatchOnScrollStateChanged(state);
    }

    public void addItemDecoration(@NonNull ItemDecoration decor, int index) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
        }
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(false);
        }
        if (index < 0) {
            this.mItemDecorations.add(decor);
        } else {
            this.mItemDecorations.add(index, decor);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void addItemDecoration(@NonNull ItemDecoration decor) {
        addItemDecoration(decor, -1);
    }

    @NonNull
    public ItemDecoration getItemDecorationAt(int index) {
        int size = getItemDecorationCount();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index + " is an invalid index for size " + size);
        }
        return this.mItemDecorations.get(index);
    }

    public int getItemDecorationCount() {
        return this.mItemDecorations.size();
    }

    public void removeItemDecorationAt(int index) {
        int size = getItemDecorationCount();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index + " is an invalid index for size " + size);
        }
        removeItemDecoration(getItemDecorationAt(index));
    }

    public void removeItemDecoration(@NonNull ItemDecoration decor) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
        }
        this.mItemDecorations.remove(decor);
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(getOverScrollMode() == 2);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void setChildDrawingOrderCallback(@Nullable ChildDrawingOrderCallback childDrawingOrderCallback) {
        if (childDrawingOrderCallback == this.mChildDrawingOrderCallback) {
            return;
        }
        this.mChildDrawingOrderCallback = childDrawingOrderCallback;
        setChildrenDrawingOrderEnabled(this.mChildDrawingOrderCallback != null);
    }

    @Deprecated
    public void setOnScrollListener(@Nullable OnScrollListener listener) {
        this.mScrollListener = listener;
    }

    public void addOnScrollListener(@NonNull OnScrollListener listener) {
        if (this.mScrollListeners == null) {
            this.mScrollListeners = new ArrayList();
        }
        this.mScrollListeners.add(listener);
    }

    public void removeOnScrollListener(@NonNull OnScrollListener listener) {
        List<OnScrollListener> list = this.mScrollListeners;
        if (list != null) {
            list.remove(listener);
        }
    }

    public void clearOnScrollListeners() {
        List<OnScrollListener> list = this.mScrollListeners;
        if (list != null) {
            list.clear();
        }
    }

    public void scrollToPosition(int position) {
        if (this.mLayoutFrozen) {
            return;
        }
        stopScroll();
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.e(TAG, "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        layoutManager.scrollToPosition(position);
        awakenScrollBars();
    }

    void jumpToPositionForSmoothScroller(int position) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            return;
        }
        layoutManager.scrollToPosition(position);
        awakenScrollBars();
    }

    public void smoothScrollToPosition(int position) {
        if (this.mLayoutFrozen) {
            return;
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else {
            layoutManager.smoothScrollToPosition(this, this.mState, position);
        }
    }

    @Override // android.view.View
    public void scrollTo(int x, int y) {
        Log.w(TAG, "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
    }

    @Override // android.view.View
    public void scrollBy(int x, int y) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.e(TAG, "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (this.mLayoutFrozen) {
        } else {
            boolean canScrollHorizontal = layoutManager.canScrollHorizontally();
            boolean canScrollVertical = this.mLayout.canScrollVertically();
            if (canScrollHorizontal || canScrollVertical) {
                int i = 0;
                int i2 = canScrollHorizontal ? x : 0;
                if (canScrollVertical) {
                    i = y;
                }
                scrollByInternal(i2, i, null);
            }
        }
    }

    void scrollStep(int dx, int dy, @Nullable int[] consumed) {
        startInterceptRequestLayout();
        onEnterLayoutOrScroll();
        TraceCompat.beginSection(TRACE_SCROLL_TAG);
        fillRemainingScrollValues(this.mState);
        int consumedX = 0;
        int consumedY = 0;
        if (dx != 0) {
            consumedX = this.mLayout.scrollHorizontallyBy(dx, this.mRecycler, this.mState);
        }
        if (dy != 0) {
            consumedY = this.mLayout.scrollVerticallyBy(dy, this.mRecycler, this.mState);
        }
        TraceCompat.endSection();
        repositionShadowingViews();
        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);
        if (consumed != null) {
            consumed[0] = consumedX;
            consumed[1] = consumedY;
        }
    }

    void consumePendingUpdateOperations() {
        if (!this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout) {
            TraceCompat.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
            dispatchLayout();
            TraceCompat.endSection();
        } else if (!this.mAdapterHelper.hasPendingUpdates()) {
        } else {
            if (this.mAdapterHelper.hasAnyUpdateTypes(4) && !this.mAdapterHelper.hasAnyUpdateTypes(11)) {
                TraceCompat.beginSection(TRACE_HANDLE_ADAPTER_UPDATES_TAG);
                startInterceptRequestLayout();
                onEnterLayoutOrScroll();
                this.mAdapterHelper.preProcess();
                if (!this.mLayoutWasDefered) {
                    if (hasUpdatedView()) {
                        dispatchLayout();
                    } else {
                        this.mAdapterHelper.consumePostponedUpdates();
                    }
                }
                stopInterceptRequestLayout(true);
                onExitLayoutOrScroll();
                TraceCompat.endSection();
            } else if (this.mAdapterHelper.hasPendingUpdates()) {
                TraceCompat.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
                dispatchLayout();
                TraceCompat.endSection();
            }
        }
    }

    private boolean hasUpdatedView() {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (holder != null && !holder.shouldIgnore() && holder.isUpdated()) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x009f, code lost:
        if (r3 != 0) goto L24;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    boolean scrollByInternal(int r19, int r20, android.view.MotionEvent r21) {
        /*
            r18 = this;
            r7 = r18
            r8 = r19
            r9 = r20
            r10 = r21
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r18.consumePendingUpdateOperations()
            android.support.v7.widget.RecyclerView$Adapter r4 = r7.mAdapter
            r11 = 1
            r12 = 0
            if (r4 == 0) goto L29
            int[] r4 = r7.mScrollStepConsumed
            r7.scrollStep(r8, r9, r4)
            int[] r4 = r7.mScrollStepConsumed
            r2 = r4[r12]
            r3 = r4[r11]
            int r0 = r8 - r2
            int r1 = r9 - r3
            r13 = r0
            r14 = r1
            r15 = r2
            r6 = r3
            goto L2d
        L29:
            r13 = r0
            r14 = r1
            r15 = r2
            r6 = r3
        L2d:
            java.util.ArrayList<android.support.v7.widget.RecyclerView$ItemDecoration> r0 = r7.mItemDecorations
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L38
            r18.invalidate()
        L38:
            int[] r5 = r7.mScrollOffset
            r16 = 0
            r0 = r18
            r1 = r15
            r2 = r6
            r3 = r13
            r4 = r14
            r17 = r6
            r6 = r16
            boolean r0 = r0.dispatchNestedScroll(r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L7a
            int r0 = r7.mLastTouchX
            int[] r1 = r7.mScrollOffset
            r2 = r1[r12]
            int r0 = r0 - r2
            r7.mLastTouchX = r0
            int r0 = r7.mLastTouchY
            r2 = r1[r11]
            int r0 = r0 - r2
            r7.mLastTouchY = r0
            if (r10 == 0) goto L67
            r0 = r1[r12]
            float r0 = (float) r0
            r1 = r1[r11]
            float r1 = (float) r1
            r10.offsetLocation(r0, r1)
        L67:
            int[] r0 = r7.mNestedOffsets
            r1 = r0[r12]
            int[] r2 = r7.mScrollOffset
            r3 = r2[r12]
            int r1 = r1 + r3
            r0[r12] = r1
            r1 = r0[r11]
            r2 = r2[r11]
            int r1 = r1 + r2
            r0[r11] = r1
            goto L9b
        L7a:
            int r0 = r18.getOverScrollMode()
            r1 = 2
            if (r0 == r1) goto L9b
            if (r10 == 0) goto L98
            r0 = 8194(0x2002, float:1.1482E-41)
            boolean r0 = android.support.v4.view.MotionEventCompat.isFromSource(r10, r0)
            if (r0 != 0) goto L98
            float r0 = r21.getX()
            float r1 = (float) r13
            float r2 = r21.getY()
            float r3 = (float) r14
            r7.pullGlows(r0, r1, r2, r3)
        L98:
            r18.considerReleasingGlowsOnScroll(r19, r20)
        L9b:
            if (r15 != 0) goto La2
            r3 = r17
            if (r3 == 0) goto La7
            goto La4
        La2:
            r3 = r17
        La4:
            r7.dispatchOnScrolled(r15, r3)
        La7:
            boolean r0 = r18.awakenScrollBars()
            if (r0 != 0) goto Lb0
            r18.invalidate()
        Lb0:
            if (r15 != 0) goto Lb4
            if (r3 == 0) goto Lb5
        Lb4:
            r12 = 1
        Lb5:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.scrollByInternal(int, int, android.view.MotionEvent):boolean");
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeHorizontalScrollOffset() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollOffset(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeHorizontalScrollExtent() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollExtent(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeHorizontalScrollRange() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollRange(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeVerticalScrollOffset() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollOffset(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeVerticalScrollExtent() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollExtent(this.mState);
        }
        return 0;
    }

    @Override // android.view.View, android.support.v4.view.ScrollingView
    public int computeVerticalScrollRange() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollRange(this.mState);
        }
        return 0;
    }

    void startInterceptRequestLayout() {
        this.mInterceptRequestLayoutDepth++;
        if (this.mInterceptRequestLayoutDepth == 1 && !this.mLayoutFrozen) {
            this.mLayoutWasDefered = false;
        }
    }

    void stopInterceptRequestLayout(boolean performLayoutChildren) {
        if (this.mInterceptRequestLayoutDepth < 1) {
            this.mInterceptRequestLayoutDepth = 1;
        }
        if (!performLayoutChildren && !this.mLayoutFrozen) {
            this.mLayoutWasDefered = false;
        }
        if (this.mInterceptRequestLayoutDepth == 1) {
            if (performLayoutChildren && this.mLayoutWasDefered && !this.mLayoutFrozen && this.mLayout != null && this.mAdapter != null) {
                dispatchLayout();
            }
            if (!this.mLayoutFrozen) {
                this.mLayoutWasDefered = false;
            }
        }
        this.mInterceptRequestLayoutDepth--;
    }

    public void setLayoutFrozen(boolean frozen) {
        if (frozen != this.mLayoutFrozen) {
            assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
            if (!frozen) {
                this.mLayoutFrozen = false;
                if (this.mLayoutWasDefered && this.mLayout != null && this.mAdapter != null) {
                    requestLayout();
                }
                this.mLayoutWasDefered = false;
                return;
            }
            long now = SystemClock.uptimeMillis();
            MotionEvent cancelEvent = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
            onTouchEvent(cancelEvent);
            this.mLayoutFrozen = true;
            this.mIgnoreMotionEventTillDown = true;
            stopScroll();
        }
    }

    public boolean isLayoutFrozen() {
        return this.mLayoutFrozen;
    }

    public void smoothScrollBy(@Px int dx, @Px int dy) {
        smoothScrollBy(dx, dy, null);
    }

    public void smoothScrollBy(@Px int dx, @Px int dy, @Nullable Interpolator interpolator) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (this.mLayoutFrozen) {
        } else {
            if (!layoutManager.canScrollHorizontally()) {
                dx = 0;
            }
            if (!this.mLayout.canScrollVertically()) {
                dy = 0;
            }
            if (dx != 0 || dy != 0) {
                this.mViewFlinger.smoothScrollBy(dx, dy, interpolator);
            }
        }
    }

    public boolean fling(int velocityX, int velocityY) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.e(TAG, "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return false;
        } else if (this.mLayoutFrozen) {
            return false;
        } else {
            boolean canScrollHorizontal = layoutManager.canScrollHorizontally();
            boolean canScrollVertical = this.mLayout.canScrollVertically();
            if (!canScrollHorizontal || Math.abs(velocityX) < this.mMinFlingVelocity) {
                velocityX = 0;
            }
            if (!canScrollVertical || Math.abs(velocityY) < this.mMinFlingVelocity) {
                velocityY = 0;
            }
            if ((velocityX != 0 || velocityY != 0) && !dispatchNestedPreFling(velocityX, velocityY)) {
                boolean canScroll = canScrollHorizontal || canScrollVertical;
                dispatchNestedFling(velocityX, velocityY, canScroll);
                OnFlingListener onFlingListener = this.mOnFlingListener;
                if (onFlingListener != null && onFlingListener.onFling(velocityX, velocityY)) {
                    return true;
                }
                if (canScroll) {
                    int nestedScrollAxis = 0;
                    if (canScrollHorizontal) {
                        nestedScrollAxis = 0 | 1;
                    }
                    if (canScrollVertical) {
                        nestedScrollAxis |= 2;
                    }
                    startNestedScroll(nestedScrollAxis, 1);
                    int i = this.mMaxFlingVelocity;
                    int velocityX2 = Math.max(-i, Math.min(velocityX, i));
                    int i2 = this.mMaxFlingVelocity;
                    this.mViewFlinger.fling(velocityX2, Math.max(-i2, Math.min(velocityY, i2)));
                    return true;
                }
            }
            return false;
        }
    }

    public void stopScroll() {
        setScrollState(0);
        stopScrollersInternal();
    }

    private void stopScrollersInternal() {
        this.mViewFlinger.stop();
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.stopSmoothScroller();
        }
    }

    public int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }

    public int getMaxFlingVelocity() {
        return this.mMaxFlingVelocity;
    }

    private void pullGlows(float x, float overscrollX, float y, float overscrollY) {
        boolean invalidate = false;
        if (overscrollX < 0.0f) {
            ensureLeftGlow();
            EdgeEffectCompat.onPull(this.mLeftGlow, (-overscrollX) / getWidth(), 1.0f - (y / getHeight()));
            invalidate = true;
        } else if (overscrollX > 0.0f) {
            ensureRightGlow();
            EdgeEffectCompat.onPull(this.mRightGlow, overscrollX / getWidth(), y / getHeight());
            invalidate = true;
        }
        if (overscrollY < 0.0f) {
            ensureTopGlow();
            EdgeEffectCompat.onPull(this.mTopGlow, (-overscrollY) / getHeight(), x / getWidth());
            invalidate = true;
        } else if (overscrollY > 0.0f) {
            ensureBottomGlow();
            EdgeEffectCompat.onPull(this.mBottomGlow, overscrollY / getHeight(), 1.0f - (x / getWidth()));
            invalidate = true;
        }
        if (invalidate || overscrollX != 0.0f || overscrollY != 0.0f) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void releaseGlows() {
        boolean needsInvalidate = false;
        EdgeEffect edgeEffect = this.mLeftGlow;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            needsInvalidate = this.mLeftGlow.isFinished();
        }
        EdgeEffect edgeEffect2 = this.mTopGlow;
        if (edgeEffect2 != null) {
            edgeEffect2.onRelease();
            needsInvalidate |= this.mTopGlow.isFinished();
        }
        EdgeEffect edgeEffect3 = this.mRightGlow;
        if (edgeEffect3 != null) {
            edgeEffect3.onRelease();
            needsInvalidate |= this.mRightGlow.isFinished();
        }
        EdgeEffect edgeEffect4 = this.mBottomGlow;
        if (edgeEffect4 != null) {
            edgeEffect4.onRelease();
            needsInvalidate |= this.mBottomGlow.isFinished();
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    void considerReleasingGlowsOnScroll(int dx, int dy) {
        boolean needsInvalidate = false;
        EdgeEffect edgeEffect = this.mLeftGlow;
        if (edgeEffect != null && !edgeEffect.isFinished() && dx > 0) {
            this.mLeftGlow.onRelease();
            needsInvalidate = this.mLeftGlow.isFinished();
        }
        EdgeEffect edgeEffect2 = this.mRightGlow;
        if (edgeEffect2 != null && !edgeEffect2.isFinished() && dx < 0) {
            this.mRightGlow.onRelease();
            needsInvalidate |= this.mRightGlow.isFinished();
        }
        EdgeEffect edgeEffect3 = this.mTopGlow;
        if (edgeEffect3 != null && !edgeEffect3.isFinished() && dy > 0) {
            this.mTopGlow.onRelease();
            needsInvalidate |= this.mTopGlow.isFinished();
        }
        EdgeEffect edgeEffect4 = this.mBottomGlow;
        if (edgeEffect4 != null && !edgeEffect4.isFinished() && dy < 0) {
            this.mBottomGlow.onRelease();
            needsInvalidate |= this.mBottomGlow.isFinished();
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    void absorbGlows(int velocityX, int velocityY) {
        if (velocityX < 0) {
            ensureLeftGlow();
            this.mLeftGlow.onAbsorb(-velocityX);
        } else if (velocityX > 0) {
            ensureRightGlow();
            this.mRightGlow.onAbsorb(velocityX);
        }
        if (velocityY < 0) {
            ensureTopGlow();
            this.mTopGlow.onAbsorb(-velocityY);
        } else if (velocityY > 0) {
            ensureBottomGlow();
            this.mBottomGlow.onAbsorb(velocityY);
        }
        if (velocityX != 0 || velocityY != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    void ensureLeftGlow() {
        if (this.mLeftGlow != null) {
            return;
        }
        this.mLeftGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 0);
        if (this.mClipToPadding) {
            this.mLeftGlow.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        } else {
            this.mLeftGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
        }
    }

    void ensureRightGlow() {
        if (this.mRightGlow != null) {
            return;
        }
        this.mRightGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 2);
        if (this.mClipToPadding) {
            this.mRightGlow.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        } else {
            this.mRightGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
        }
    }

    void ensureTopGlow() {
        if (this.mTopGlow != null) {
            return;
        }
        this.mTopGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 1);
        if (this.mClipToPadding) {
            this.mTopGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
        } else {
            this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    void ensureBottomGlow() {
        if (this.mBottomGlow != null) {
            return;
        }
        this.mBottomGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 3);
        if (this.mClipToPadding) {
            this.mBottomGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
        } else {
            this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }

    public void setEdgeEffectFactory(@NonNull EdgeEffectFactory edgeEffectFactory) {
        Preconditions.checkNotNull(edgeEffectFactory);
        this.mEdgeEffectFactory = edgeEffectFactory;
        invalidateGlows();
    }

    @NonNull
    public EdgeEffectFactory getEdgeEffectFactory() {
        return this.mEdgeEffectFactory;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public View focusSearch(View focused, int direction) {
        View result;
        View result2 = this.mLayout.onInterceptFocusSearch(focused, direction);
        if (result2 != null) {
            return result2;
        }
        boolean z = true;
        boolean canRunFocusFailure = this.mAdapter != null && this.mLayout != null && !isComputingLayout() && !this.mLayoutFrozen;
        FocusFinder ff = FocusFinder.getInstance();
        if (canRunFocusFailure && (direction == 2 || direction == 1)) {
            boolean needsFocusFailureLayout = false;
            if (this.mLayout.canScrollVertically()) {
                int absDir = direction == 2 ? 130 : 33;
                View found = ff.findNextFocus(this, focused, absDir);
                needsFocusFailureLayout = found == null;
                if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    direction = absDir;
                }
            }
            if (!needsFocusFailureLayout && this.mLayout.canScrollHorizontally()) {
                boolean rtl = this.mLayout.getLayoutDirection() == 1;
                int absDir2 = (direction == 2) ^ rtl ? 66 : 17;
                View found2 = ff.findNextFocus(this, focused, absDir2);
                if (found2 != null) {
                    z = false;
                }
                needsFocusFailureLayout = z;
                if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    direction = absDir2;
                }
            }
            if (needsFocusFailureLayout) {
                consumePendingUpdateOperations();
                View focusedItemView = findContainingItemView(focused);
                if (focusedItemView == null) {
                    return null;
                }
                startInterceptRequestLayout();
                this.mLayout.onFocusSearchFailed(focused, direction, this.mRecycler, this.mState);
                stopInterceptRequestLayout(false);
            }
            result = ff.findNextFocus(this, focused, direction);
        } else {
            result = ff.findNextFocus(this, focused, direction);
            if (result == null && canRunFocusFailure) {
                consumePendingUpdateOperations();
                View focusedItemView2 = findContainingItemView(focused);
                if (focusedItemView2 == null) {
                    return null;
                }
                startInterceptRequestLayout();
                result = this.mLayout.onFocusSearchFailed(focused, direction, this.mRecycler, this.mState);
                stopInterceptRequestLayout(false);
            }
        }
        if (result == null || result.hasFocusable()) {
            return isPreferredNextFocus(focused, result, direction) ? result : super.focusSearch(focused, direction);
        } else if (getFocusedChild() == null) {
            return super.focusSearch(focused, direction);
        } else {
            requestChildOnScreen(result, null);
            return focused;
        }
    }

    private boolean isPreferredNextFocus(View focused, View next, int direction) {
        if (next == null || next == this || findContainingItemView(next) == null) {
            return false;
        }
        if (focused != null && findContainingItemView(focused) != null) {
            this.mTempRect.set(0, 0, focused.getWidth(), focused.getHeight());
            this.mTempRect2.set(0, 0, next.getWidth(), next.getHeight());
            offsetDescendantRectToMyCoords(focused, this.mTempRect);
            offsetDescendantRectToMyCoords(next, this.mTempRect2);
            int rtl = this.mLayout.getLayoutDirection() == 1 ? -1 : 1;
            int rightness = 0;
            if ((this.mTempRect.left < this.mTempRect2.left || this.mTempRect.right <= this.mTempRect2.left) && this.mTempRect.right < this.mTempRect2.right) {
                rightness = 1;
            } else if ((this.mTempRect.right > this.mTempRect2.right || this.mTempRect.left >= this.mTempRect2.right) && this.mTempRect.left > this.mTempRect2.left) {
                rightness = -1;
            }
            int downness = 0;
            if ((this.mTempRect.top < this.mTempRect2.top || this.mTempRect.bottom <= this.mTempRect2.top) && this.mTempRect.bottom < this.mTempRect2.bottom) {
                downness = 1;
            } else if ((this.mTempRect.bottom > this.mTempRect2.bottom || this.mTempRect.top >= this.mTempRect2.bottom) && this.mTempRect.top > this.mTempRect2.top) {
                downness = -1;
            }
            if (direction == 17) {
                return rightness < 0;
            } else if (direction == 33) {
                return downness < 0;
            } else if (direction == 66) {
                return rightness > 0;
            } else if (direction == 130) {
                return downness > 0;
            } else {
                switch (direction) {
                    case 1:
                        return downness < 0 || (downness == 0 && rightness * rtl <= 0);
                    case 2:
                        return downness > 0 || (downness == 0 && rightness * rtl >= 0);
                    default:
                        throw new IllegalArgumentException("Invalid direction: " + direction + exceptionLabel());
                }
            }
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        if (!this.mLayout.onRequestChildFocus(this, this.mState, child, focused) && focused != null) {
            requestChildOnScreen(child, focused);
        }
        super.requestChildFocus(child, focused);
    }

    private void requestChildOnScreen(@NonNull View child, @Nullable View focused) {
        View rectView = focused != null ? focused : child;
        this.mTempRect.set(0, 0, rectView.getWidth(), rectView.getHeight());
        ViewGroup.LayoutParams focusedLayoutParams = rectView.getLayoutParams();
        if (focusedLayoutParams instanceof LayoutParams) {
            LayoutParams lp = (LayoutParams) focusedLayoutParams;
            if (!lp.mInsetsDirty) {
                Rect insets = lp.mDecorInsets;
                this.mTempRect.left -= insets.left;
                this.mTempRect.right += insets.right;
                this.mTempRect.top -= insets.top;
                this.mTempRect.bottom += insets.bottom;
            }
        }
        if (focused != null) {
            offsetDescendantRectToMyCoords(focused, this.mTempRect);
            offsetRectIntoDescendantCoords(child, this.mTempRect);
        }
        this.mLayout.requestChildRectangleOnScreen(this, child, this.mTempRect, !this.mFirstLayoutComplete, focused == null);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        return this.mLayout.requestChildRectangleOnScreen(this, child, rect, immediate);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null || !layoutManager.onAddFocusables(this, views, direction, focusableMode)) {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (isComputingLayout()) {
            return false;
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLayoutOrScrollCounter = 0;
        boolean z = true;
        this.mIsAttached = true;
        if (!this.mFirstLayoutComplete || isLayoutRequested()) {
            z = false;
        }
        this.mFirstLayoutComplete = z;
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.dispatchAttachedToWindow(this);
        }
        this.mPostedAnimatorRunner = false;
        if (ALLOW_THREAD_GAP_WORK) {
            this.mGapWorker = GapWorker.sGapWorker.get();
            if (this.mGapWorker == null) {
                this.mGapWorker = new GapWorker();
                Display display = ViewCompat.getDisplay(this);
                float refreshRate = 60.0f;
                if (!isInEditMode() && display != null) {
                    float displayRefreshRate = display.getRefreshRate();
                    if (displayRefreshRate >= 30.0f) {
                        refreshRate = displayRefreshRate;
                    }
                }
                this.mGapWorker.mFrameIntervalNs = 1.0E9f / refreshRate;
                GapWorker.sGapWorker.set(this.mGapWorker);
            }
            this.mGapWorker.add(this);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        GapWorker gapWorker;
        super.onDetachedFromWindow();
        ItemAnimator itemAnimator = this.mItemAnimator;
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
        }
        stopScroll();
        this.mIsAttached = false;
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.dispatchDetachedFromWindow(this, this.mRecycler);
        }
        this.mPendingAccessibilityImportanceChange.clear();
        removeCallbacks(this.mItemAnimatorRunner);
        this.mViewInfoStore.onDetach();
        if (ALLOW_THREAD_GAP_WORK && (gapWorker = this.mGapWorker) != null) {
            gapWorker.remove(this);
            this.mGapWorker = null;
        }
    }

    @Override // android.view.View
    public boolean isAttachedToWindow() {
        return this.mIsAttached;
    }

    void assertInLayoutOrScroll(String message) {
        if (!isComputingLayout()) {
            if (message == null) {
                throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling" + exceptionLabel());
            }
            throw new IllegalStateException(message + exceptionLabel());
        }
    }

    void assertNotInLayoutOrScroll(String message) {
        if (isComputingLayout()) {
            if (message == null) {
                throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling" + exceptionLabel());
            }
            throw new IllegalStateException(message);
        } else if (this.mDispatchScrollCounter > 0) {
            Log.w(TAG, "Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data. Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.", new IllegalStateException("" + exceptionLabel()));
        }
    }

    public void addOnItemTouchListener(@NonNull OnItemTouchListener listener) {
        this.mOnItemTouchListeners.add(listener);
    }

    public void removeOnItemTouchListener(@NonNull OnItemTouchListener listener) {
        this.mOnItemTouchListeners.remove(listener);
        if (this.mActiveOnItemTouchListener == listener) {
            this.mActiveOnItemTouchListener = null;
        }
    }

    private boolean dispatchOnItemTouchIntercept(MotionEvent e) {
        int action = e.getAction();
        if (action == 3 || action == 0) {
            this.mActiveOnItemTouchListener = null;
        }
        int listenerCount = this.mOnItemTouchListeners.size();
        for (int i = 0; i < listenerCount; i++) {
            OnItemTouchListener listener = this.mOnItemTouchListeners.get(i);
            if (listener.onInterceptTouchEvent(this, e) && action != 3) {
                this.mActiveOnItemTouchListener = listener;
                return true;
            }
        }
        return false;
    }

    private boolean dispatchOnItemTouch(MotionEvent e) {
        int action = e.getAction();
        OnItemTouchListener onItemTouchListener = this.mActiveOnItemTouchListener;
        if (onItemTouchListener != null) {
            if (action == 0) {
                this.mActiveOnItemTouchListener = null;
            } else {
                onItemTouchListener.onTouchEvent(this, e);
                if (action == 3 || action == 1) {
                    this.mActiveOnItemTouchListener = null;
                }
                return true;
            }
        }
        if (action != 0) {
            int listenerCount = this.mOnItemTouchListeners.size();
            for (int i = 0; i < listenerCount; i++) {
                OnItemTouchListener listener = this.mOnItemTouchListeners.get(i);
                if (listener.onInterceptTouchEvent(this, e)) {
                    this.mActiveOnItemTouchListener = listener;
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.mLayoutFrozen) {
            return false;
        }
        if (dispatchOnItemTouchIntercept(e)) {
            cancelTouch();
            return true;
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            return false;
        }
        boolean canScrollHorizontally = layoutManager.canScrollHorizontally();
        boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(e);
        int action = e.getActionMasked();
        int actionIndex = e.getActionIndex();
        switch (action) {
            case 0:
                if (this.mIgnoreMotionEventTillDown) {
                    this.mIgnoreMotionEventTillDown = false;
                }
                this.mScrollPointerId = e.getPointerId(0);
                int x = (int) (e.getX() + 0.5f);
                this.mLastTouchX = x;
                this.mInitialTouchX = x;
                int y = (int) (e.getY() + 0.5f);
                this.mLastTouchY = y;
                this.mInitialTouchY = y;
                if (this.mScrollState == 2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                }
                int[] iArr = this.mNestedOffsets;
                iArr[1] = 0;
                iArr[0] = 0;
                int nestedScrollAxis = 0;
                if (canScrollHorizontally) {
                    nestedScrollAxis = 0 | 1;
                }
                if (canScrollVertically) {
                    nestedScrollAxis |= 2;
                }
                startNestedScroll(nestedScrollAxis, 0);
                break;
            case 1:
                this.mVelocityTracker.clear();
                stopNestedScroll(0);
                break;
            case 2:
                int index = e.findPointerIndex(this.mScrollPointerId);
                if (index >= 0) {
                    int x2 = (int) (e.getX(index) + 0.5f);
                    int y2 = (int) (e.getY(index) + 0.5f);
                    if (this.mScrollState != 1) {
                        int dx = x2 - this.mInitialTouchX;
                        int dy = y2 - this.mInitialTouchY;
                        boolean startScroll = false;
                        if (canScrollHorizontally && Math.abs(dx) > this.mTouchSlop) {
                            this.mLastTouchX = x2;
                            startScroll = true;
                        }
                        if (canScrollVertically && Math.abs(dy) > this.mTouchSlop) {
                            this.mLastTouchY = y2;
                            startScroll = true;
                        }
                        if (startScroll) {
                            setScrollState(1);
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                break;
            case 3:
                cancelTouch();
                break;
            case 5:
                this.mScrollPointerId = e.getPointerId(actionIndex);
                int x3 = (int) (e.getX(actionIndex) + 0.5f);
                this.mLastTouchX = x3;
                this.mInitialTouchX = x3;
                int y3 = (int) (e.getY(actionIndex) + 0.5f);
                this.mLastTouchY = y3;
                this.mInitialTouchY = y3;
                break;
            case 6:
                onPointerUp(e);
                break;
        }
        int nestedScrollAxis2 = this.mScrollState;
        return nestedScrollAxis2 == 1;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        int listenerCount = this.mOnItemTouchListeners.size();
        for (int i = 0; i < listenerCount; i++) {
            OnItemTouchListener listener = this.mOnItemTouchListeners.get(i);
            listener.onRequestDisallowInterceptTouchEvent(disallowIntercept);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent e) {
        int dx;
        int dy;
        int i = 0;
        if (this.mLayoutFrozen || this.mIgnoreMotionEventTillDown) {
            return false;
        }
        if (dispatchOnItemTouch(e)) {
            cancelTouch();
            return true;
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            return false;
        }
        boolean canScrollHorizontally = layoutManager.canScrollHorizontally();
        boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        boolean eventAddedToVelocityTracker = false;
        MotionEvent vtev = MotionEvent.obtain(e);
        int action = e.getActionMasked();
        int actionIndex = e.getActionIndex();
        if (action == 0) {
            int[] iArr = this.mNestedOffsets;
            iArr[1] = 0;
            iArr[0] = 0;
        }
        int[] iArr2 = this.mNestedOffsets;
        vtev.offsetLocation(iArr2[0], iArr2[1]);
        switch (action) {
            case 0:
                this.mScrollPointerId = e.getPointerId(0);
                int x = (int) (e.getX() + 0.5f);
                this.mLastTouchX = x;
                this.mInitialTouchX = x;
                int y = (int) (e.getY() + 0.5f);
                this.mLastTouchY = y;
                this.mInitialTouchY = y;
                int nestedScrollAxis = 0;
                if (canScrollHorizontally) {
                    nestedScrollAxis = 0 | 1;
                }
                if (canScrollVertically) {
                    nestedScrollAxis |= 2;
                }
                startNestedScroll(nestedScrollAxis, 0);
                break;
            case 1:
                this.mVelocityTracker.addMovement(vtev);
                eventAddedToVelocityTracker = true;
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
                float xvel = canScrollHorizontally ? -this.mVelocityTracker.getXVelocity(this.mScrollPointerId) : 0.0f;
                float yvel = canScrollVertically ? -this.mVelocityTracker.getYVelocity(this.mScrollPointerId) : 0.0f;
                if ((xvel == 0.0f && yvel == 0.0f) || !fling((int) xvel, (int) yvel)) {
                    setScrollState(0);
                }
                resetTouch();
                break;
            case 2:
                int index = e.findPointerIndex(this.mScrollPointerId);
                if (index < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                int x2 = (int) (e.getX(index) + 0.5f);
                int y2 = (int) (e.getY(index) + 0.5f);
                int dx2 = this.mLastTouchX - x2;
                int dy2 = this.mLastTouchY - y2;
                if (dispatchNestedPreScroll(dx2, dy2, this.mScrollConsumed, this.mScrollOffset, 0)) {
                    int[] iArr3 = this.mScrollConsumed;
                    dx2 -= iArr3[0];
                    dy2 -= iArr3[1];
                    int[] iArr4 = this.mScrollOffset;
                    vtev.offsetLocation(iArr4[0], iArr4[1]);
                    int[] iArr5 = this.mNestedOffsets;
                    int i2 = iArr5[0];
                    int[] iArr6 = this.mScrollOffset;
                    iArr5[0] = i2 + iArr6[0];
                    iArr5[1] = iArr5[1] + iArr6[1];
                }
                if (this.mScrollState == 1) {
                    dx = dx2;
                    dy = dy2;
                } else {
                    boolean startScroll = false;
                    if (canScrollHorizontally) {
                        int abs = Math.abs(dx2);
                        int i3 = this.mTouchSlop;
                        if (abs > i3) {
                            if (dx2 > 0) {
                                dx2 -= i3;
                            } else {
                                dx2 += i3;
                            }
                            startScroll = true;
                        }
                    }
                    if (canScrollVertically) {
                        int abs2 = Math.abs(dy2);
                        int i4 = this.mTouchSlop;
                        if (abs2 > i4) {
                            if (dy2 > 0) {
                                dy2 -= i4;
                            } else {
                                dy2 += i4;
                            }
                            startScroll = true;
                        }
                    }
                    if (startScroll) {
                        setScrollState(1);
                    }
                    dx = dx2;
                    dy = dy2;
                }
                if (this.mScrollState == 1) {
                    int[] iArr7 = this.mScrollOffset;
                    this.mLastTouchX = x2 - iArr7[0];
                    this.mLastTouchY = y2 - iArr7[1];
                    int i5 = canScrollHorizontally ? dx : 0;
                    if (canScrollVertically) {
                        i = dy;
                    }
                    if (scrollByInternal(i5, i, vtev)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (this.mGapWorker != null && (dx != 0 || dy != 0)) {
                        this.mGapWorker.postFromTraversal(this, dx, dy);
                        break;
                    }
                }
                break;
            case 3:
                cancelTouch();
                break;
            case 5:
                this.mScrollPointerId = e.getPointerId(actionIndex);
                int x3 = (int) (e.getX(actionIndex) + 0.5f);
                this.mLastTouchX = x3;
                this.mInitialTouchX = x3;
                int y3 = (int) (e.getY(actionIndex) + 0.5f);
                this.mLastTouchY = y3;
                this.mInitialTouchY = y3;
                break;
            case 6:
                onPointerUp(e);
                break;
        }
        if (!eventAddedToVelocityTracker) {
            this.mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        return true;
    }

    private void resetTouch() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
        stopNestedScroll(0);
        releaseGlows();
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(0);
    }

    private void onPointerUp(MotionEvent e) {
        int actionIndex = e.getActionIndex();
        if (e.getPointerId(actionIndex) == this.mScrollPointerId) {
            int newIndex = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = e.getPointerId(newIndex);
            int x = (int) (e.getX(newIndex) + 0.5f);
            this.mLastTouchX = x;
            this.mInitialTouchX = x;
            int y = (int) (e.getY(newIndex) + 0.5f);
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
        }
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        float vScroll;
        float hScroll;
        if (this.mLayout != null && !this.mLayoutFrozen && event.getAction() == 8) {
            if ((event.getSource() & 2) != 0) {
                if (this.mLayout.canScrollVertically()) {
                    vScroll = -event.getAxisValue(9);
                } else {
                    vScroll = 0.0f;
                }
                if (this.mLayout.canScrollHorizontally()) {
                    hScroll = event.getAxisValue(10);
                } else {
                    hScroll = 0.0f;
                }
            } else if ((event.getSource() & 4194304) != 0) {
                float axisScroll = event.getAxisValue(26);
                if (this.mLayout.canScrollVertically()) {
                    float vScroll2 = -axisScroll;
                    vScroll = vScroll2;
                    hScroll = 0.0f;
                } else if (this.mLayout.canScrollHorizontally()) {
                    vScroll = 0.0f;
                    hScroll = axisScroll;
                } else {
                    hScroll = 0.0f;
                    vScroll = 0.0f;
                }
            } else {
                vScroll = 0.0f;
                hScroll = 0.0f;
            }
            if (vScroll != 0.0f || hScroll != 0.0f) {
                scrollByInternal((int) (this.mScaledHorizontalScrollFactor * hScroll), (int) (this.mScaledVerticalScrollFactor * vScroll), event);
            }
        }
        return false;
    }

    @Override // android.view.View
    protected void onMeasure(int widthSpec, int heightSpec) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            defaultOnMeasure(widthSpec, heightSpec);
            return;
        }
        boolean measureSpecModeIsExactly = false;
        if (layoutManager.isAutoMeasureEnabled()) {
            int widthMode = View.MeasureSpec.getMode(widthSpec);
            int heightMode = View.MeasureSpec.getMode(heightSpec);
            this.mLayout.onMeasure(this.mRecycler, this.mState, widthSpec, heightSpec);
            if (widthMode == 1073741824 && heightMode == 1073741824) {
                measureSpecModeIsExactly = true;
            }
            if (measureSpecModeIsExactly || this.mAdapter == null) {
                return;
            }
            if (this.mState.mLayoutStep == 1) {
                dispatchLayoutStep1();
            }
            this.mLayout.setMeasureSpecs(widthSpec, heightSpec);
            this.mState.mIsMeasuring = true;
            dispatchLayoutStep2();
            this.mLayout.setMeasuredDimensionFromChildren(widthSpec, heightSpec);
            if (this.mLayout.shouldMeasureTwice()) {
                this.mLayout.setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MemoryConstants.GB), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MemoryConstants.GB));
                this.mState.mIsMeasuring = true;
                dispatchLayoutStep2();
                this.mLayout.setMeasuredDimensionFromChildren(widthSpec, heightSpec);
            }
        } else if (this.mHasFixedSize) {
            this.mLayout.onMeasure(this.mRecycler, this.mState, widthSpec, heightSpec);
        } else {
            if (this.mAdapterUpdateDuringMeasure) {
                startInterceptRequestLayout();
                onEnterLayoutOrScroll();
                processAdapterUpdatesAndSetAnimationFlags();
                onExitLayoutOrScroll();
                if (this.mState.mRunPredictiveAnimations) {
                    this.mState.mInPreLayout = true;
                } else {
                    this.mAdapterHelper.consumeUpdatesInOnePass();
                    this.mState.mInPreLayout = false;
                }
                this.mAdapterUpdateDuringMeasure = false;
                stopInterceptRequestLayout(false);
            } else if (this.mState.mRunPredictiveAnimations) {
                setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
                return;
            }
            Adapter adapter = this.mAdapter;
            if (adapter != null) {
                this.mState.mItemCount = adapter.getItemCount();
            } else {
                this.mState.mItemCount = 0;
            }
            startInterceptRequestLayout();
            this.mLayout.onMeasure(this.mRecycler, this.mState, widthSpec, heightSpec);
            stopInterceptRequestLayout(false);
            this.mState.mInPreLayout = false;
        }
    }

    void defaultOnMeasure(int widthSpec, int heightSpec) {
        int width = LayoutManager.chooseSize(widthSpec, getPaddingLeft() + getPaddingRight(), ViewCompat.getMinimumWidth(this));
        int height = LayoutManager.chooseSize(heightSpec, getPaddingTop() + getPaddingBottom(), ViewCompat.getMinimumHeight(this));
        setMeasuredDimension(width, height);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            invalidateGlows();
        }
    }

    public void setItemAnimator(@Nullable ItemAnimator animator) {
        ItemAnimator itemAnimator = this.mItemAnimator;
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
            this.mItemAnimator.setListener(null);
        }
        this.mItemAnimator = animator;
        ItemAnimator itemAnimator2 = this.mItemAnimator;
        if (itemAnimator2 != null) {
            itemAnimator2.setListener(this.mItemAnimatorListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onEnterLayoutOrScroll() {
        this.mLayoutOrScrollCounter++;
    }

    void onExitLayoutOrScroll() {
        onExitLayoutOrScroll(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onExitLayoutOrScroll(boolean enableChangeEvents) {
        this.mLayoutOrScrollCounter--;
        if (this.mLayoutOrScrollCounter < 1) {
            this.mLayoutOrScrollCounter = 0;
            if (enableChangeEvents) {
                dispatchContentChangedIfNecessary();
                dispatchPendingImportantForAccessibilityChanges();
            }
        }
    }

    boolean isAccessibilityEnabled() {
        AccessibilityManager accessibilityManager = this.mAccessibilityManager;
        return accessibilityManager != null && accessibilityManager.isEnabled();
    }

    private void dispatchContentChangedIfNecessary() {
        int flags = this.mEatenAccessibilityChangeFlags;
        this.mEatenAccessibilityChangeFlags = 0;
        if (flags != 0 && isAccessibilityEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setEventType(2048);
            AccessibilityEventCompat.setContentChangeTypes(event, flags);
            sendAccessibilityEventUnchecked(event);
        }
    }

    public boolean isComputingLayout() {
        return this.mLayoutOrScrollCounter > 0;
    }

    boolean shouldDeferAccessibilityEvent(AccessibilityEvent event) {
        if (isComputingLayout()) {
            int type = 0;
            if (event != null) {
                type = AccessibilityEventCompat.getContentChangeTypes(event);
            }
            if (type == 0) {
                type = 0;
            }
            this.mEatenAccessibilityChangeFlags |= type;
            return true;
        }
        return false;
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        if (shouldDeferAccessibilityEvent(event)) {
            return;
        }
        super.sendAccessibilityEventUnchecked(event);
    }

    @Nullable
    public ItemAnimator getItemAnimator() {
        return this.mItemAnimator;
    }

    void postAnimationRunner() {
        if (!this.mPostedAnimatorRunner && this.mIsAttached) {
            ViewCompat.postOnAnimation(this, this.mItemAnimatorRunner);
            this.mPostedAnimatorRunner = true;
        }
    }

    private boolean predictiveItemAnimationsEnabled() {
        return this.mItemAnimator != null && this.mLayout.supportsPredictiveItemAnimations();
    }

    private void processAdapterUpdatesAndSetAnimationFlags() {
        if (this.mDataSetHasChangedAfterLayout) {
            this.mAdapterHelper.reset();
            if (this.mDispatchItemsChangedEvent) {
                this.mLayout.onItemsChanged(this);
            }
        }
        if (predictiveItemAnimationsEnabled()) {
            this.mAdapterHelper.preProcess();
        } else {
            this.mAdapterHelper.consumeUpdatesInOnePass();
        }
        boolean z = false;
        boolean animationTypeSupported = this.mItemsAddedOrRemoved || this.mItemsChanged;
        this.mState.mRunSimpleAnimations = this.mFirstLayoutComplete && this.mItemAnimator != null && (this.mDataSetHasChangedAfterLayout || animationTypeSupported || this.mLayout.mRequestedSimpleAnimations) && (!this.mDataSetHasChangedAfterLayout || this.mAdapter.hasStableIds());
        State state = this.mState;
        if (state.mRunSimpleAnimations && animationTypeSupported && !this.mDataSetHasChangedAfterLayout && predictiveItemAnimationsEnabled()) {
            z = true;
        }
        state.mRunPredictiveAnimations = z;
    }

    void dispatchLayout() {
        if (this.mAdapter == null) {
            Log.e(TAG, "No adapter attached; skipping layout");
        } else if (this.mLayout == null) {
            Log.e(TAG, "No layout manager attached; skipping layout");
        } else {
            State state = this.mState;
            state.mIsMeasuring = false;
            if (state.mLayoutStep == 1) {
                dispatchLayoutStep1();
                this.mLayout.setExactMeasureSpecsFrom(this);
                dispatchLayoutStep2();
            } else if (this.mAdapterHelper.hasUpdates() || this.mLayout.getWidth() != getWidth() || this.mLayout.getHeight() != getHeight()) {
                this.mLayout.setExactMeasureSpecsFrom(this);
                dispatchLayoutStep2();
            } else {
                this.mLayout.setExactMeasureSpecsFrom(this);
            }
            dispatchLayoutStep3();
        }
    }

    private void saveFocusInfo() {
        int adapterPosition;
        View child = null;
        if (this.mPreserveFocusAfterLayout && hasFocus() && this.mAdapter != null) {
            child = getFocusedChild();
        }
        ViewHolder focusedVh = child == null ? null : findContainingViewHolder(child);
        if (focusedVh == null) {
            resetFocusInfo();
            return;
        }
        this.mState.mFocusedItemId = this.mAdapter.hasStableIds() ? focusedVh.getItemId() : -1L;
        State state = this.mState;
        if (this.mDataSetHasChangedAfterLayout) {
            adapterPosition = -1;
        } else {
            adapterPosition = focusedVh.isRemoved() ? focusedVh.mOldPosition : focusedVh.getAdapterPosition();
        }
        state.mFocusedItemPosition = adapterPosition;
        this.mState.mFocusedSubChildId = getDeepestFocusedViewWithId(focusedVh.itemView);
    }

    private void resetFocusInfo() {
        State state = this.mState;
        state.mFocusedItemId = -1L;
        state.mFocusedItemPosition = -1;
        state.mFocusedSubChildId = -1;
    }

    @Nullable
    private View findNextViewToFocus() {
        ViewHolder nextFocus;
        int startFocusSearchIndex = this.mState.mFocusedItemPosition != -1 ? this.mState.mFocusedItemPosition : 0;
        int itemCount = this.mState.getItemCount();
        for (int i = startFocusSearchIndex; i < itemCount; i++) {
            ViewHolder nextFocus2 = findViewHolderForAdapterPosition(i);
            if (nextFocus2 == null) {
                break;
            } else if (nextFocus2.itemView.hasFocusable()) {
                return nextFocus2.itemView;
            }
        }
        int limit = Math.min(itemCount, startFocusSearchIndex);
        for (int i2 = limit - 1; i2 >= 0 && (nextFocus = findViewHolderForAdapterPosition(i2)) != null; i2--) {
            if (nextFocus.itemView.hasFocusable()) {
                return nextFocus.itemView;
            }
        }
        return null;
    }

    private void recoverFocusFromState() {
        View child;
        if (this.mPreserveFocusAfterLayout && this.mAdapter != null && hasFocus() && getDescendantFocusability() != 393216) {
            if (getDescendantFocusability() == 131072 && isFocused()) {
                return;
            }
            if (!isFocused()) {
                View focusedChild = getFocusedChild();
                if (IGNORE_DETACHED_FOCUSED_CHILD && (focusedChild.getParent() == null || !focusedChild.hasFocus())) {
                    if (this.mChildHelper.getChildCount() == 0) {
                        requestFocus();
                        return;
                    }
                } else if (!this.mChildHelper.isHidden(focusedChild)) {
                    return;
                }
            }
            ViewHolder focusTarget = null;
            if (this.mState.mFocusedItemId != -1 && this.mAdapter.hasStableIds()) {
                focusTarget = findViewHolderForItemId(this.mState.mFocusedItemId);
            }
            View viewToFocus = null;
            if (focusTarget == null || this.mChildHelper.isHidden(focusTarget.itemView) || !focusTarget.itemView.hasFocusable()) {
                if (this.mChildHelper.getChildCount() > 0) {
                    viewToFocus = findNextViewToFocus();
                }
            } else {
                viewToFocus = focusTarget.itemView;
            }
            if (viewToFocus != null) {
                if (this.mState.mFocusedSubChildId != -1 && (child = viewToFocus.findViewById(this.mState.mFocusedSubChildId)) != null && child.isFocusable()) {
                    viewToFocus = child;
                }
                viewToFocus.requestFocus();
            }
        }
    }

    private int getDeepestFocusedViewWithId(View view) {
        int lastKnownId = view.getId();
        while (!view.isFocused() && (view instanceof ViewGroup) && view.hasFocus()) {
            view = ((ViewGroup) view).getFocusedChild();
            int id = view.getId();
            if (id != -1) {
                lastKnownId = view.getId();
            }
        }
        return lastKnownId;
    }

    final void fillRemainingScrollValues(State state) {
        if (getScrollState() == 2) {
            OverScroller scroller = this.mViewFlinger.mScroller;
            state.mRemainingScrollHorizontal = scroller.getFinalX() - scroller.getCurrX();
            state.mRemainingScrollVertical = scroller.getFinalY() - scroller.getCurrY();
            return;
        }
        state.mRemainingScrollHorizontal = 0;
        state.mRemainingScrollVertical = 0;
    }

    private void dispatchLayoutStep1() {
        boolean z = true;
        this.mState.assertLayoutStep(1);
        fillRemainingScrollValues(this.mState);
        this.mState.mIsMeasuring = false;
        startInterceptRequestLayout();
        this.mViewInfoStore.clear();
        onEnterLayoutOrScroll();
        processAdapterUpdatesAndSetAnimationFlags();
        saveFocusInfo();
        State state = this.mState;
        if (!state.mRunSimpleAnimations || !this.mItemsChanged) {
            z = false;
        }
        state.mTrackOldChangeHolders = z;
        this.mItemsChanged = false;
        this.mItemsAddedOrRemoved = false;
        State state2 = this.mState;
        state2.mInPreLayout = state2.mRunPredictiveAnimations;
        this.mState.mItemCount = this.mAdapter.getItemCount();
        findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        if (this.mState.mRunSimpleAnimations) {
            int count = this.mChildHelper.getChildCount();
            for (int i = 0; i < count; i++) {
                ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!holder.shouldIgnore() && (!holder.isInvalid() || this.mAdapter.hasStableIds())) {
                    this.mViewInfoStore.addToPreLayout(holder, this.mItemAnimator.recordPreLayoutInformation(this.mState, holder, ItemAnimator.buildAdapterChangeFlagsForAnimations(holder), holder.getUnmodifiedPayloads()));
                    if (this.mState.mTrackOldChangeHolders && holder.isUpdated() && !holder.isRemoved() && !holder.shouldIgnore() && !holder.isInvalid()) {
                        long key = getChangedHolderKey(holder);
                        this.mViewInfoStore.addToOldChangeHolders(key, holder);
                    }
                }
            }
        }
        if (this.mState.mRunPredictiveAnimations) {
            saveOldPositions();
            boolean didStructureChange = this.mState.mStructureChanged;
            State state3 = this.mState;
            state3.mStructureChanged = false;
            this.mLayout.onLayoutChildren(this.mRecycler, state3);
            this.mState.mStructureChanged = didStructureChange;
            for (int i2 = 0; i2 < this.mChildHelper.getChildCount(); i2++) {
                View child = this.mChildHelper.getChildAt(i2);
                ViewHolder viewHolder = getChildViewHolderInt(child);
                if (!viewHolder.shouldIgnore() && !this.mViewInfoStore.isInPreLayout(viewHolder)) {
                    int flags = ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder);
                    boolean wasHidden = viewHolder.hasAnyOfTheFlags(8192);
                    if (!wasHidden) {
                        flags |= 4096;
                    }
                    ItemAnimator.ItemHolderInfo animationInfo = this.mItemAnimator.recordPreLayoutInformation(this.mState, viewHolder, flags, viewHolder.getUnmodifiedPayloads());
                    if (wasHidden) {
                        recordAnimationInfoIfBouncedHiddenView(viewHolder, animationInfo);
                    } else {
                        this.mViewInfoStore.addToAppearedInPreLayoutHolders(viewHolder, animationInfo);
                    }
                }
            }
            clearOldPositions();
        } else {
            clearOldPositions();
        }
        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);
        this.mState.mLayoutStep = 2;
    }

    private void dispatchLayoutStep2() {
        startInterceptRequestLayout();
        onEnterLayoutOrScroll();
        this.mState.assertLayoutStep(6);
        this.mAdapterHelper.consumeUpdatesInOnePass();
        this.mState.mItemCount = this.mAdapter.getItemCount();
        State state = this.mState;
        state.mDeletedInvisibleItemCountSincePreviousLayout = 0;
        state.mInPreLayout = false;
        this.mLayout.onLayoutChildren(this.mRecycler, state);
        State state2 = this.mState;
        state2.mStructureChanged = false;
        this.mPendingSavedState = null;
        state2.mRunSimpleAnimations = state2.mRunSimpleAnimations && this.mItemAnimator != null;
        this.mState.mLayoutStep = 4;
        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);
    }

    private void dispatchLayoutStep3() {
        this.mState.assertLayoutStep(4);
        startInterceptRequestLayout();
        onEnterLayoutOrScroll();
        State state = this.mState;
        state.mLayoutStep = 1;
        if (state.mRunSimpleAnimations) {
            for (int i = this.mChildHelper.getChildCount() - 1; i >= 0; i--) {
                ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!holder.shouldIgnore()) {
                    long key = getChangedHolderKey(holder);
                    ItemAnimator.ItemHolderInfo animationInfo = this.mItemAnimator.recordPostLayoutInformation(this.mState, holder);
                    ViewHolder oldChangeViewHolder = this.mViewInfoStore.getFromOldChangeHolders(key);
                    if (oldChangeViewHolder != null && !oldChangeViewHolder.shouldIgnore()) {
                        boolean oldDisappearing = this.mViewInfoStore.isDisappearing(oldChangeViewHolder);
                        boolean newDisappearing = this.mViewInfoStore.isDisappearing(holder);
                        if (oldDisappearing && oldChangeViewHolder == holder) {
                            this.mViewInfoStore.addToPostLayout(holder, animationInfo);
                        } else {
                            ItemAnimator.ItemHolderInfo preInfo = this.mViewInfoStore.popFromPreLayout(oldChangeViewHolder);
                            this.mViewInfoStore.addToPostLayout(holder, animationInfo);
                            ItemAnimator.ItemHolderInfo postInfo = this.mViewInfoStore.popFromPostLayout(holder);
                            if (preInfo == null) {
                                handleMissingPreInfoForChangeError(key, holder, oldChangeViewHolder);
                            } else {
                                animateChange(oldChangeViewHolder, holder, preInfo, postInfo, oldDisappearing, newDisappearing);
                            }
                        }
                    } else {
                        this.mViewInfoStore.addToPostLayout(holder, animationInfo);
                    }
                }
            }
            this.mViewInfoStore.process(this.mViewInfoProcessCallback);
        }
        this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        State state2 = this.mState;
        state2.mPreviousLayoutItemCount = state2.mItemCount;
        this.mDataSetHasChangedAfterLayout = false;
        this.mDispatchItemsChangedEvent = false;
        State state3 = this.mState;
        state3.mRunSimpleAnimations = false;
        state3.mRunPredictiveAnimations = false;
        this.mLayout.mRequestedSimpleAnimations = false;
        if (this.mRecycler.mChangedScrap != null) {
            this.mRecycler.mChangedScrap.clear();
        }
        if (this.mLayout.mPrefetchMaxObservedInInitialPrefetch) {
            LayoutManager layoutManager = this.mLayout;
            layoutManager.mPrefetchMaxCountObserved = 0;
            layoutManager.mPrefetchMaxObservedInInitialPrefetch = false;
            this.mRecycler.updateViewCacheSize();
        }
        this.mLayout.onLayoutCompleted(this.mState);
        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);
        this.mViewInfoStore.clear();
        int[] iArr = this.mMinMaxLayoutPositions;
        if (didChildRangeChange(iArr[0], iArr[1])) {
            dispatchOnScrolled(0, 0);
        }
        recoverFocusFromState();
        resetFocusInfo();
    }

    private void handleMissingPreInfoForChangeError(long key, ViewHolder holder, ViewHolder oldChangeViewHolder) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = this.mChildHelper.getChildAt(i);
            ViewHolder other = getChildViewHolderInt(view);
            if (other != holder) {
                long otherKey = getChangedHolderKey(other);
                if (otherKey == key) {
                    Adapter adapter = this.mAdapter;
                    if (adapter != null && adapter.hasStableIds()) {
                        throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + other + " \n View Holder 2:" + holder + exceptionLabel());
                    }
                    throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + other + " \n View Holder 2:" + holder + exceptionLabel());
                }
            }
        }
        Log.e(TAG, "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + oldChangeViewHolder + " cannot be found but it is necessary for " + holder + exceptionLabel());
    }

    void recordAnimationInfoIfBouncedHiddenView(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo animationInfo) {
        viewHolder.setFlags(0, 8192);
        if (this.mState.mTrackOldChangeHolders && viewHolder.isUpdated() && !viewHolder.isRemoved() && !viewHolder.shouldIgnore()) {
            long key = getChangedHolderKey(viewHolder);
            this.mViewInfoStore.addToOldChangeHolders(key, viewHolder);
        }
        this.mViewInfoStore.addToPreLayout(viewHolder, animationInfo);
    }

    private void findMinMaxChildLayoutPositions(int[] into) {
        int count = this.mChildHelper.getChildCount();
        if (count == 0) {
            into[0] = -1;
            into[1] = -1;
            return;
        }
        int minPositionPreLayout = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int maxPositionPreLayout = Integer.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (!holder.shouldIgnore()) {
                int pos = holder.getLayoutPosition();
                if (pos < minPositionPreLayout) {
                    minPositionPreLayout = pos;
                }
                if (pos > maxPositionPreLayout) {
                    maxPositionPreLayout = pos;
                }
            }
        }
        into[0] = minPositionPreLayout;
        into[1] = maxPositionPreLayout;
    }

    private boolean didChildRangeChange(int minPositionPreLayout, int maxPositionPreLayout) {
        findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        int[] iArr = this.mMinMaxLayoutPositions;
        return (iArr[0] == minPositionPreLayout && iArr[1] == maxPositionPreLayout) ? false : true;
    }

    @Override // android.view.ViewGroup
    protected void removeDetachedView(View child, boolean animate) {
        ViewHolder vh = getChildViewHolderInt(child);
        if (vh != null) {
            if (vh.isTmpDetached()) {
                vh.clearTmpDetachFlag();
            } else if (!vh.shouldIgnore()) {
                throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + vh + exceptionLabel());
            }
        }
        child.clearAnimation();
        dispatchChildDetached(child);
        super.removeDetachedView(child, animate);
    }

    long getChangedHolderKey(ViewHolder holder) {
        return this.mAdapter.hasStableIds() ? holder.getItemId() : holder.mPosition;
    }

    void animateAppearance(@NonNull ViewHolder itemHolder, @Nullable ItemAnimator.ItemHolderInfo preLayoutInfo, @NonNull ItemAnimator.ItemHolderInfo postLayoutInfo) {
        itemHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateAppearance(itemHolder, preLayoutInfo, postLayoutInfo)) {
            postAnimationRunner();
        }
    }

    void animateDisappearance(@NonNull ViewHolder holder, @NonNull ItemAnimator.ItemHolderInfo preLayoutInfo, @Nullable ItemAnimator.ItemHolderInfo postLayoutInfo) {
        addAnimatingView(holder);
        holder.setIsRecyclable(false);
        if (this.mItemAnimator.animateDisappearance(holder, preLayoutInfo, postLayoutInfo)) {
            postAnimationRunner();
        }
    }

    private void animateChange(@NonNull ViewHolder oldHolder, @NonNull ViewHolder newHolder, @NonNull ItemAnimator.ItemHolderInfo preInfo, @NonNull ItemAnimator.ItemHolderInfo postInfo, boolean oldHolderDisappearing, boolean newHolderDisappearing) {
        oldHolder.setIsRecyclable(false);
        if (oldHolderDisappearing) {
            addAnimatingView(oldHolder);
        }
        if (oldHolder != newHolder) {
            if (newHolderDisappearing) {
                addAnimatingView(newHolder);
            }
            oldHolder.mShadowedHolder = newHolder;
            addAnimatingView(oldHolder);
            this.mRecycler.unscrapView(oldHolder);
            newHolder.setIsRecyclable(false);
            newHolder.mShadowingHolder = oldHolder;
        }
        if (this.mItemAnimator.animateChange(oldHolder, newHolder, preInfo, postInfo)) {
            postAnimationRunner();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        TraceCompat.beginSection(TRACE_ON_LAYOUT_TAG);
        dispatchLayout();
        TraceCompat.endSection();
        this.mFirstLayoutComplete = true;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.mInterceptRequestLayoutDepth == 0 && !this.mLayoutFrozen) {
            super.requestLayout();
        } else {
            this.mLayoutWasDefered = true;
        }
    }

    void markItemDecorInsetsDirty() {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mChildHelper.getUnfilteredChildAt(i);
            ((LayoutParams) child.getLayoutParams()).mInsetsDirty = true;
        }
        this.mRecycler.markItemDecorInsetsDirty();
    }

    @Override // android.view.View
    public void draw(Canvas c) {
        super.draw(c);
        int count = this.mItemDecorations.size();
        for (int i = 0; i < count; i++) {
            this.mItemDecorations.get(i).onDrawOver(c, this, this.mState);
        }
        boolean needsInvalidate = false;
        EdgeEffect edgeEffect = this.mLeftGlow;
        boolean z = true;
        if (edgeEffect != null && !edgeEffect.isFinished()) {
            int restore = c.save();
            int padding = this.mClipToPadding ? getPaddingBottom() : 0;
            c.rotate(270.0f);
            c.translate((-getHeight()) + padding, 0.0f);
            EdgeEffect edgeEffect2 = this.mLeftGlow;
            needsInvalidate = edgeEffect2 != null && edgeEffect2.draw(c);
            c.restoreToCount(restore);
        }
        EdgeEffect edgeEffect3 = this.mTopGlow;
        if (edgeEffect3 != null && !edgeEffect3.isFinished()) {
            int restore2 = c.save();
            if (this.mClipToPadding) {
                c.translate(getPaddingLeft(), getPaddingTop());
            }
            EdgeEffect edgeEffect4 = this.mTopGlow;
            needsInvalidate |= edgeEffect4 != null && edgeEffect4.draw(c);
            c.restoreToCount(restore2);
        }
        EdgeEffect edgeEffect5 = this.mRightGlow;
        if (edgeEffect5 != null && !edgeEffect5.isFinished()) {
            int restore3 = c.save();
            int width = getWidth();
            int padding2 = this.mClipToPadding ? getPaddingTop() : 0;
            c.rotate(90.0f);
            c.translate(-padding2, -width);
            EdgeEffect edgeEffect6 = this.mRightGlow;
            needsInvalidate |= edgeEffect6 != null && edgeEffect6.draw(c);
            c.restoreToCount(restore3);
        }
        EdgeEffect edgeEffect7 = this.mBottomGlow;
        if (edgeEffect7 != null && !edgeEffect7.isFinished()) {
            int restore4 = c.save();
            c.rotate(180.0f);
            if (this.mClipToPadding) {
                c.translate((-getWidth()) + getPaddingRight(), (-getHeight()) + getPaddingBottom());
            } else {
                c.translate(-getWidth(), -getHeight());
            }
            EdgeEffect edgeEffect8 = this.mBottomGlow;
            if (edgeEffect8 == null || !edgeEffect8.draw(c)) {
                z = false;
            }
            needsInvalidate |= z;
            c.restoreToCount(restore4);
        }
        if (!needsInvalidate && this.mItemAnimator != null && this.mItemDecorations.size() > 0 && this.mItemAnimator.isRunning()) {
            needsInvalidate = true;
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas c) {
        super.onDraw(c);
        int count = this.mItemDecorations.size();
        for (int i = 0; i < count; i++) {
            this.mItemDecorations.get(i).onDraw(c, this, this.mState);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return (p instanceof LayoutParams) && this.mLayout.checkLayoutParams((LayoutParams) p);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager" + exceptionLabel());
        }
        return layoutManager.generateDefaultLayoutParams();
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager" + exceptionLabel());
        }
        return layoutManager.generateLayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager" + exceptionLabel());
        }
        return layoutManager.generateLayoutParams(p);
    }

    public boolean isAnimating() {
        ItemAnimator itemAnimator = this.mItemAnimator;
        return itemAnimator != null && itemAnimator.isRunning();
    }

    void saveOldPositions() {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!holder.shouldIgnore()) {
                holder.saveOldPosition();
            }
        }
    }

    void clearOldPositions() {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!holder.shouldIgnore()) {
                holder.clearOldPosition();
            }
        }
        this.mRecycler.clearOldPositions();
    }

    void offsetPositionRecordsForMove(int from, int to) {
        int start;
        int end;
        int inBetweenOffset;
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        if (from < to) {
            start = from;
            end = to;
            inBetweenOffset = -1;
        } else {
            start = to;
            end = from;
            inBetweenOffset = 1;
        }
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && holder.mPosition >= start && holder.mPosition <= end) {
                if (holder.mPosition == from) {
                    holder.offsetPosition(to - from, false);
                } else {
                    holder.offsetPosition(inBetweenOffset, false);
                }
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForMove(from, to);
        requestLayout();
    }

    void offsetPositionRecordsForInsert(int positionStart, int itemCount) {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.shouldIgnore() && holder.mPosition >= positionStart) {
                holder.offsetPosition(itemCount, false);
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForInsert(positionStart, itemCount);
        requestLayout();
    }

    void offsetPositionRecordsForRemove(int positionStart, int itemCount, boolean applyToPreLayout) {
        int positionEnd = positionStart + itemCount;
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.shouldIgnore()) {
                if (holder.mPosition >= positionEnd) {
                    holder.offsetPosition(-itemCount, applyToPreLayout);
                    this.mState.mStructureChanged = true;
                } else if (holder.mPosition >= positionStart) {
                    holder.flagRemovedAndOffsetPosition(positionStart - 1, -itemCount, applyToPreLayout);
                    this.mState.mStructureChanged = true;
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForRemove(positionStart, itemCount, applyToPreLayout);
        requestLayout();
    }

    void viewRangeUpdate(int positionStart, int itemCount, Object payload) {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        int positionEnd = positionStart + itemCount;
        for (int i = 0; i < childCount; i++) {
            View child = this.mChildHelper.getUnfilteredChildAt(i);
            ViewHolder holder = getChildViewHolderInt(child);
            if (holder != null && !holder.shouldIgnore() && holder.mPosition >= positionStart && holder.mPosition < positionEnd) {
                holder.addFlags(2);
                holder.addChangePayload(payload);
                ((LayoutParams) child.getLayoutParams()).mInsetsDirty = true;
            }
        }
        this.mRecycler.viewRangeUpdate(positionStart, itemCount);
    }

    boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
        ItemAnimator itemAnimator = this.mItemAnimator;
        return itemAnimator == null || itemAnimator.canReuseUpdatedViewHolder(viewHolder, viewHolder.getUnmodifiedPayloads());
    }

    void processDataSetCompletelyChanged(boolean dispatchItemsChanged) {
        this.mDispatchItemsChangedEvent |= dispatchItemsChanged;
        this.mDataSetHasChangedAfterLayout = true;
        markKnownViewsInvalid();
    }

    void markKnownViewsInvalid() {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.shouldIgnore()) {
                holder.addFlags(6);
            }
        }
        markItemDecorInsetsDirty();
        this.mRecycler.markKnownViewsInvalid();
    }

    public void invalidateItemDecorations() {
        if (this.mItemDecorations.size() == 0) {
            return;
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public boolean getPreserveFocusAfterLayout() {
        return this.mPreserveFocusAfterLayout;
    }

    public void setPreserveFocusAfterLayout(boolean preserveFocusAfterLayout) {
        this.mPreserveFocusAfterLayout = preserveFocusAfterLayout;
    }

    public ViewHolder getChildViewHolder(@NonNull View child) {
        ViewParent parent = child.getParent();
        if (parent != null && parent != this) {
            throw new IllegalArgumentException("View " + child + " is not a direct child of " + this);
        }
        return getChildViewHolderInt(child);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:?, code lost:
        return r3;
     */
    @android.support.annotation.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.view.View findContainingItemView(@android.support.annotation.NonNull android.view.View r3) {
        /*
            r2 = this;
            android.view.ViewParent r0 = r3.getParent()
        L4:
            if (r0 == 0) goto L14
            if (r0 == r2) goto L14
            boolean r1 = r0 instanceof android.view.View
            if (r1 == 0) goto L14
            r3 = r0
            android.view.View r3 = (android.view.View) r3
            android.view.ViewParent r0 = r3.getParent()
            goto L4
        L14:
            if (r0 != r2) goto L18
            r1 = r3
            goto L19
        L18:
            r1 = 0
        L19:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.findContainingItemView(android.view.View):android.view.View");
    }

    @Nullable
    public ViewHolder findContainingViewHolder(@NonNull View view) {
        View itemView = findContainingItemView(view);
        if (itemView == null) {
            return null;
        }
        return getChildViewHolder(itemView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ViewHolder getChildViewHolderInt(View child) {
        if (child == null) {
            return null;
        }
        return ((LayoutParams) child.getLayoutParams()).mViewHolder;
    }

    @Deprecated
    public int getChildPosition(@NonNull View child) {
        return getChildAdapterPosition(child);
    }

    public int getChildAdapterPosition(@NonNull View child) {
        ViewHolder holder = getChildViewHolderInt(child);
        if (holder != null) {
            return holder.getAdapterPosition();
        }
        return -1;
    }

    public int getChildLayoutPosition(@NonNull View child) {
        ViewHolder holder = getChildViewHolderInt(child);
        if (holder != null) {
            return holder.getLayoutPosition();
        }
        return -1;
    }

    public long getChildItemId(@NonNull View child) {
        ViewHolder holder;
        Adapter adapter = this.mAdapter;
        if (adapter == null || !adapter.hasStableIds() || (holder = getChildViewHolderInt(child)) == null) {
            return -1L;
        }
        return holder.getItemId();
    }

    @Nullable
    @Deprecated
    public ViewHolder findViewHolderForPosition(int position) {
        return findViewHolderForPosition(position, false);
    }

    @Nullable
    public ViewHolder findViewHolderForLayoutPosition(int position) {
        return findViewHolderForPosition(position, false);
    }

    @Nullable
    public ViewHolder findViewHolderForAdapterPosition(int position) {
        if (this.mDataSetHasChangedAfterLayout) {
            return null;
        }
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        ViewHolder hidden = null;
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.isRemoved() && getAdapterPositionFor(holder) == position) {
                if (this.mChildHelper.isHidden(holder.itemView)) {
                    hidden = holder;
                } else {
                    return holder;
                }
            }
        }
        return hidden;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0036 A[SYNTHETIC] */
    @android.support.annotation.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    android.support.v7.widget.RecyclerView.ViewHolder findViewHolderForPosition(int r7, boolean r8) {
        /*
            r6 = this;
            android.support.v7.widget.ChildHelper r0 = r6.mChildHelper
            int r0 = r0.getUnfilteredChildCount()
            r1 = 0
            r2 = 0
        L8:
            if (r2 >= r0) goto L3a
            android.support.v7.widget.ChildHelper r3 = r6.mChildHelper
            android.view.View r3 = r3.getUnfilteredChildAt(r2)
            android.support.v7.widget.RecyclerView$ViewHolder r3 = getChildViewHolderInt(r3)
            if (r3 == 0) goto L37
            boolean r4 = r3.isRemoved()
            if (r4 != 0) goto L37
            if (r8 == 0) goto L23
            int r4 = r3.mPosition
            if (r4 == r7) goto L2a
            goto L37
        L23:
            int r4 = r3.getLayoutPosition()
            if (r4 == r7) goto L2a
            goto L37
        L2a:
            android.support.v7.widget.ChildHelper r4 = r6.mChildHelper
            android.view.View r5 = r3.itemView
            boolean r4 = r4.isHidden(r5)
            if (r4 == 0) goto L36
            r1 = r3
            goto L37
        L36:
            return r3
        L37:
            int r2 = r2 + 1
            goto L8
        L3a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.findViewHolderForPosition(int, boolean):android.support.v7.widget.RecyclerView$ViewHolder");
    }

    public ViewHolder findViewHolderForItemId(long id) {
        Adapter adapter = this.mAdapter;
        if (adapter == null || !adapter.hasStableIds()) {
            return null;
        }
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        ViewHolder hidden = null;
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.isRemoved() && holder.getItemId() == id) {
                if (this.mChildHelper.isHidden(holder.itemView)) {
                    hidden = holder;
                } else {
                    return holder;
                }
            }
        }
        return hidden;
    }

    @Nullable
    public View findChildViewUnder(float x, float y) {
        int count = this.mChildHelper.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = this.mChildHelper.getChildAt(i);
            float translationX = child.getTranslationX();
            float translationY = child.getTranslationY();
            if (x >= child.getLeft() + translationX && x <= child.getRight() + translationX && y >= child.getTop() + translationY && y <= child.getBottom() + translationY) {
                return child;
            }
        }
        return null;
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public void offsetChildrenVertical(@Px int dy) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mChildHelper.getChildAt(i).offsetTopAndBottom(dy);
        }
    }

    public void onChildAttachedToWindow(@NonNull View child) {
    }

    public void onChildDetachedFromWindow(@NonNull View child) {
    }

    public void offsetChildrenHorizontal(@Px int dx) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mChildHelper.getChildAt(i).offsetLeftAndRight(dx);
        }
    }

    public void getDecoratedBoundsWithMargins(@NonNull View view, @NonNull Rect outBounds) {
        getDecoratedBoundsWithMarginsInt(view, outBounds);
    }

    static void getDecoratedBoundsWithMarginsInt(View view, Rect outBounds) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        Rect insets = lp.mDecorInsets;
        outBounds.set((view.getLeft() - insets.left) - lp.leftMargin, (view.getTop() - insets.top) - lp.topMargin, view.getRight() + insets.right + lp.rightMargin, view.getBottom() + insets.bottom + lp.bottomMargin);
    }

    Rect getItemDecorInsetsForChild(View child) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (!lp.mInsetsDirty) {
            return lp.mDecorInsets;
        }
        if (this.mState.isPreLayout() && (lp.isItemChanged() || lp.isViewInvalid())) {
            return lp.mDecorInsets;
        }
        Rect insets = lp.mDecorInsets;
        insets.set(0, 0, 0, 0);
        int decorCount = this.mItemDecorations.size();
        for (int i = 0; i < decorCount; i++) {
            this.mTempRect.set(0, 0, 0, 0);
            this.mItemDecorations.get(i).getItemOffsets(this.mTempRect, child, this, this.mState);
            insets.left += this.mTempRect.left;
            insets.top += this.mTempRect.top;
            insets.right += this.mTempRect.right;
            insets.bottom += this.mTempRect.bottom;
        }
        lp.mInsetsDirty = false;
        return insets;
    }

    public void onScrolled(@Px int dx, @Px int dy) {
    }

    void dispatchOnScrolled(int hresult, int vresult) {
        this.mDispatchScrollCounter++;
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(hresult, vresult);
        OnScrollListener onScrollListener = this.mScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrolled(this, hresult, vresult);
        }
        List<OnScrollListener> list = this.mScrollListeners;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                this.mScrollListeners.get(i).onScrolled(this, hresult, vresult);
            }
        }
        int i2 = this.mDispatchScrollCounter;
        this.mDispatchScrollCounter = i2 - 1;
    }

    public void onScrollStateChanged(int state) {
    }

    void dispatchOnScrollStateChanged(int state) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.onScrollStateChanged(state);
        }
        onScrollStateChanged(state);
        OnScrollListener onScrollListener = this.mScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(this, state);
        }
        List<OnScrollListener> list = this.mScrollListeners;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                this.mScrollListeners.get(i).onScrollStateChanged(this, state);
            }
        }
    }

    public boolean hasPendingAdapterUpdates() {
        return !this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout || this.mAdapterHelper.hasPendingUpdates();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ViewFlinger implements Runnable {
        private int mLastFlingX;
        private int mLastFlingY;
        OverScroller mScroller;
        Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
        private boolean mEatRunOnAnimationRequest = false;
        private boolean mReSchedulePostAnimationCallback = false;

        ViewFlinger() {
            this.mScroller = new OverScroller(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
        }

        @Override // java.lang.Runnable
        public void run() {
            int adapterSize;
            int overscrollX;
            int overscrollY;
            int velX;
            int velY;
            if (RecyclerView.this.mLayout == null) {
                stop();
                return;
            }
            disableRunOnAnimationRequests();
            RecyclerView.this.consumePendingUpdateOperations();
            OverScroller scroller = this.mScroller;
            SmoothScroller smoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
            if (scroller.computeScrollOffset()) {
                int[] scrollConsumed = RecyclerView.this.mScrollConsumed;
                int x = scroller.getCurrX();
                int y = scroller.getCurrY();
                int dx = x - this.mLastFlingX;
                int dy = y - this.mLastFlingY;
                int hresult = 0;
                this.mLastFlingX = x;
                this.mLastFlingY = y;
                if (RecyclerView.this.dispatchNestedPreScroll(dx, dy, scrollConsumed, null, 1)) {
                    dx -= scrollConsumed[0];
                    dy -= scrollConsumed[1];
                }
                if (RecyclerView.this.mAdapter == null) {
                    adapterSize = 0;
                    overscrollX = 0;
                    overscrollY = 0;
                } else {
                    RecyclerView recyclerView = RecyclerView.this;
                    recyclerView.scrollStep(dx, dy, recyclerView.mScrollStepConsumed);
                    hresult = RecyclerView.this.mScrollStepConsumed[0];
                    int vresult = RecyclerView.this.mScrollStepConsumed[1];
                    int overscrollX2 = dx - hresult;
                    int overscrollY2 = dy - vresult;
                    if (smoothScroller != null && !smoothScroller.isPendingInitialRun() && smoothScroller.isRunning()) {
                        int adapterSize2 = RecyclerView.this.mState.getItemCount();
                        if (adapterSize2 == 0) {
                            smoothScroller.stop();
                        } else if (smoothScroller.getTargetPosition() >= adapterSize2) {
                            smoothScroller.setTargetPosition(adapterSize2 - 1);
                            smoothScroller.onAnimation(dx - overscrollX2, dy - overscrollY2);
                        } else {
                            smoothScroller.onAnimation(dx - overscrollX2, dy - overscrollY2);
                        }
                    }
                    adapterSize = vresult;
                    overscrollX = overscrollX2;
                    overscrollY = overscrollY2;
                }
                if (!RecyclerView.this.mItemDecorations.isEmpty()) {
                    RecyclerView.this.invalidate();
                }
                if (RecyclerView.this.getOverScrollMode() != 2) {
                    RecyclerView.this.considerReleasingGlowsOnScroll(dx, dy);
                }
                if (!RecyclerView.this.dispatchNestedScroll(hresult, adapterSize, overscrollX, overscrollY, null, 1) && (overscrollX != 0 || overscrollY != 0)) {
                    int vel = (int) scroller.getCurrVelocity();
                    if (overscrollX == x) {
                        velX = 0;
                    } else {
                        velX = overscrollX < 0 ? -vel : overscrollX > 0 ? vel : 0;
                    }
                    if (overscrollY == y) {
                        velY = 0;
                    } else {
                        velY = overscrollY < 0 ? -vel : overscrollY > 0 ? vel : 0;
                    }
                    if (RecyclerView.this.getOverScrollMode() != 2) {
                        RecyclerView.this.absorbGlows(velX, velY);
                    }
                    if ((velX != 0 || overscrollX == x || scroller.getFinalX() == 0) && (velY != 0 || overscrollY == y || scroller.getFinalY() == 0)) {
                        scroller.abortAnimation();
                    }
                }
                if (hresult != 0 || adapterSize != 0) {
                    RecyclerView.this.dispatchOnScrolled(hresult, adapterSize);
                }
                if (!RecyclerView.this.awakenScrollBars()) {
                    RecyclerView.this.invalidate();
                }
                boolean fullyConsumedVertical = dy != 0 && RecyclerView.this.mLayout.canScrollVertically() && adapterSize == dy;
                boolean fullyConsumedHorizontal = dx != 0 && RecyclerView.this.mLayout.canScrollHorizontally() && hresult == dx;
                boolean fullyConsumedAny = (dx == 0 && dy == 0) || fullyConsumedHorizontal || fullyConsumedVertical;
                if (scroller.isFinished() || (!fullyConsumedAny && !RecyclerView.this.hasNestedScrollingParent(1))) {
                    RecyclerView.this.setScrollState(0);
                    if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                        RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
                    }
                    RecyclerView.this.stopNestedScroll(1);
                } else {
                    postOnAnimation();
                    if (RecyclerView.this.mGapWorker != null) {
                        RecyclerView.this.mGapWorker.postFromTraversal(RecyclerView.this, dx, dy);
                    }
                }
            }
            if (smoothScroller != null) {
                if (smoothScroller.isPendingInitialRun()) {
                    smoothScroller.onAnimation(0, 0);
                }
                if (!this.mReSchedulePostAnimationCallback) {
                    smoothScroller.stop();
                }
            }
            enableRunOnAnimationRequests();
        }

        private void disableRunOnAnimationRequests() {
            this.mReSchedulePostAnimationCallback = false;
            this.mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            this.mEatRunOnAnimationRequest = false;
            if (this.mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (this.mEatRunOnAnimationRequest) {
                this.mReSchedulePostAnimationCallback = true;
                return;
            }
            RecyclerView.this.removeCallbacks(this);
            ViewCompat.postOnAnimation(RecyclerView.this, this);
        }

        public void fling(int velocityX, int velocityY) {
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.fling(0, 0, velocityX, velocityY, Integer.MIN_VALUE, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, Integer.MIN_VALUE, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
            postOnAnimation();
        }

        public void smoothScrollBy(int dx, int dy) {
            smoothScrollBy(dx, dy, 0, 0);
        }

        public void smoothScrollBy(int dx, int dy, int vx, int vy) {
            smoothScrollBy(dx, dy, computeScrollDuration(dx, dy, vx, vy));
        }

        private float distanceInfluenceForSnapDuration(float f) {
            return (float) Math.sin((f - 0.5f) * 0.47123894f);
        }

        private int computeScrollDuration(int dx, int dy, int vx, int vy) {
            int duration;
            int absDx = Math.abs(dx);
            int absDy = Math.abs(dy);
            boolean horizontal = absDx > absDy;
            int velocity = (int) Math.sqrt((vx * vx) + (vy * vy));
            int delta = (int) Math.sqrt((dx * dx) + (dy * dy));
            int containerSize = horizontal ? RecyclerView.this.getWidth() : RecyclerView.this.getHeight();
            int halfContainerSize = containerSize / 2;
            float distanceRatio = Math.min(1.0f, (delta * 1.0f) / containerSize);
            float distance = halfContainerSize + (halfContainerSize * distanceInfluenceForSnapDuration(distanceRatio));
            if (velocity > 0) {
                duration = Math.round(Math.abs(distance / velocity) * 1000.0f) * 4;
            } else {
                float absDelta = horizontal ? absDx : absDy;
                duration = (int) (((absDelta / containerSize) + 1.0f) * 300.0f);
            }
            return Math.min(duration, (int) RecyclerView.MAX_SCROLL_DURATION);
        }

        public void smoothScrollBy(int dx, int dy, int duration) {
            smoothScrollBy(dx, dy, duration, RecyclerView.sQuinticInterpolator);
        }

        public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
            smoothScrollBy(dx, dy, computeScrollDuration(dx, dy, 0, 0), interpolator == null ? RecyclerView.sQuinticInterpolator : interpolator);
        }

        public void smoothScrollBy(int dx, int dy, int duration, Interpolator interpolator) {
            if (this.mInterpolator != interpolator) {
                this.mInterpolator = interpolator;
                this.mScroller = new OverScroller(RecyclerView.this.getContext(), interpolator);
            }
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.startScroll(0, 0, dx, dy, duration);
            if (Build.VERSION.SDK_INT < 23) {
                this.mScroller.computeScrollOffset();
            }
            postOnAnimation();
        }

        public void stop() {
            RecyclerView.this.removeCallbacks(this);
            this.mScroller.abortAnimation();
        }
    }

    void repositionShadowingViews() {
        int count = this.mChildHelper.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = this.mChildHelper.getChildAt(i);
            ViewHolder holder = getChildViewHolder(view);
            if (holder != null && holder.mShadowingHolder != null) {
                View shadowingView = holder.mShadowingHolder.itemView;
                int left = view.getLeft();
                int top = view.getTop();
                if (left != shadowingView.getLeft() || top != shadowingView.getTop()) {
                    shadowingView.layout(left, top, shadowingView.getWidth() + left, shadowingView.getHeight() + top);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecyclerViewDataObserver extends AdapterDataObserver {
        RecyclerViewDataObserver() {
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            RecyclerView.this.mState.mStructureChanged = true;
            RecyclerView.this.processDataSetCompletelyChanged(true);
            if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
                RecyclerView.this.requestLayout();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(positionStart, itemCount, payload)) {
                triggerUpdateProcessor();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int positionStart, int itemCount) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(positionStart, itemCount)) {
                triggerUpdateProcessor();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(positionStart, itemCount)) {
                triggerUpdateProcessor();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(fromPosition, toPosition, itemCount)) {
                triggerUpdateProcessor();
            }
        }

        void triggerUpdateProcessor() {
            if (RecyclerView.POST_UPDATES_ON_ANIMATION && RecyclerView.this.mHasFixedSize && RecyclerView.this.mIsAttached) {
                RecyclerView recyclerView = RecyclerView.this;
                ViewCompat.postOnAnimation(recyclerView, recyclerView.mUpdateChildViewsRunnable);
                return;
            }
            RecyclerView recyclerView2 = RecyclerView.this;
            recyclerView2.mAdapterUpdateDuringMeasure = true;
            recyclerView2.requestLayout();
        }
    }

    /* loaded from: classes.dex */
    public static class EdgeEffectFactory {
        public static final int DIRECTION_BOTTOM = 3;
        public static final int DIRECTION_LEFT = 0;
        public static final int DIRECTION_RIGHT = 2;
        public static final int DIRECTION_TOP = 1;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface EdgeDirection {
        }

        @NonNull
        protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
            return new EdgeEffect(view.getContext());
        }
    }

    /* loaded from: classes.dex */
    public static class RecycledViewPool {
        private static final int DEFAULT_MAX_SCRAP = 5;
        SparseArray<ScrapData> mScrap = new SparseArray<>();
        private int mAttachCount = 0;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static class ScrapData {
            final ArrayList<ViewHolder> mScrapHeap = new ArrayList<>();
            int mMaxScrap = 5;
            long mCreateRunningAverageNs = 0;
            long mBindRunningAverageNs = 0;

            ScrapData() {
            }
        }

        public void clear() {
            for (int i = 0; i < this.mScrap.size(); i++) {
                ScrapData data = this.mScrap.valueAt(i);
                data.mScrapHeap.clear();
            }
        }

        public void setMaxRecycledViews(int viewType, int max) {
            ScrapData scrapData = getScrapDataForType(viewType);
            scrapData.mMaxScrap = max;
            ArrayList<ViewHolder> scrapHeap = scrapData.mScrapHeap;
            while (scrapHeap.size() > max) {
                scrapHeap.remove(scrapHeap.size() - 1);
            }
        }

        public int getRecycledViewCount(int viewType) {
            return getScrapDataForType(viewType).mScrapHeap.size();
        }

        @Nullable
        public ViewHolder getRecycledView(int viewType) {
            ScrapData scrapData = this.mScrap.get(viewType);
            if (scrapData != null && !scrapData.mScrapHeap.isEmpty()) {
                ArrayList<ViewHolder> scrapHeap = scrapData.mScrapHeap;
                return scrapHeap.remove(scrapHeap.size() - 1);
            }
            return null;
        }

        int size() {
            int count = 0;
            for (int i = 0; i < this.mScrap.size(); i++) {
                ArrayList<ViewHolder> viewHolders = this.mScrap.valueAt(i).mScrapHeap;
                if (viewHolders != null) {
                    count += viewHolders.size();
                }
            }
            return count;
        }

        public void putRecycledView(ViewHolder scrap) {
            int viewType = scrap.getItemViewType();
            ArrayList<ViewHolder> scrapHeap = getScrapDataForType(viewType).mScrapHeap;
            if (this.mScrap.get(viewType).mMaxScrap <= scrapHeap.size()) {
                return;
            }
            scrap.resetInternal();
            scrapHeap.add(scrap);
        }

        long runningAverage(long oldAverage, long newValue) {
            if (oldAverage == 0) {
                return newValue;
            }
            return ((oldAverage / 4) * 3) + (newValue / 4);
        }

        void factorInCreateTime(int viewType, long createTimeNs) {
            ScrapData scrapData = getScrapDataForType(viewType);
            scrapData.mCreateRunningAverageNs = runningAverage(scrapData.mCreateRunningAverageNs, createTimeNs);
        }

        void factorInBindTime(int viewType, long bindTimeNs) {
            ScrapData scrapData = getScrapDataForType(viewType);
            scrapData.mBindRunningAverageNs = runningAverage(scrapData.mBindRunningAverageNs, bindTimeNs);
        }

        boolean willCreateInTime(int viewType, long approxCurrentNs, long deadlineNs) {
            long expectedDurationNs = getScrapDataForType(viewType).mCreateRunningAverageNs;
            return expectedDurationNs == 0 || approxCurrentNs + expectedDurationNs < deadlineNs;
        }

        boolean willBindInTime(int viewType, long approxCurrentNs, long deadlineNs) {
            long expectedDurationNs = getScrapDataForType(viewType).mBindRunningAverageNs;
            return expectedDurationNs == 0 || approxCurrentNs + expectedDurationNs < deadlineNs;
        }

        void attach() {
            this.mAttachCount++;
        }

        void detach() {
            this.mAttachCount--;
        }

        void onAdapterChanged(Adapter oldAdapter, Adapter newAdapter, boolean compatibleWithPrevious) {
            if (oldAdapter != null) {
                detach();
            }
            if (!compatibleWithPrevious && this.mAttachCount == 0) {
                clear();
            }
            if (newAdapter != null) {
                attach();
            }
        }

        private ScrapData getScrapDataForType(int viewType) {
            ScrapData scrapData = this.mScrap.get(viewType);
            if (scrapData == null) {
                ScrapData scrapData2 = new ScrapData();
                this.mScrap.put(viewType, scrapData2);
                return scrapData2;
            }
            return scrapData;
        }
    }

    @Nullable
    static RecyclerView findNestedRecyclerView(@NonNull View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        if (view instanceof RecyclerView) {
            return (RecyclerView) view;
        }
        ViewGroup parent = (ViewGroup) view;
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            RecyclerView descendant = findNestedRecyclerView(child);
            if (descendant != null) {
                return descendant;
            }
        }
        return null;
    }

    static void clearNestedRecyclerViewIfNotNested(@NonNull ViewHolder holder) {
        if (holder.mNestedRecyclerView != null) {
            RecyclerView item = holder.mNestedRecyclerView.get();
            while (item != null) {
                if (item == holder.itemView) {
                    return;
                }
                ViewParent parent = item.getParent();
                if (parent instanceof View) {
                    item = (View) parent;
                } else {
                    item = null;
                }
            }
            holder.mNestedRecyclerView = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getNanoTime() {
        if (ALLOW_THREAD_GAP_WORK) {
            return System.nanoTime();
        }
        return 0L;
    }

    /* loaded from: classes.dex */
    public final class Recycler {
        static final int DEFAULT_CACHE_SIZE = 2;
        RecycledViewPool mRecyclerPool;
        private ViewCacheExtension mViewCacheExtension;
        final ArrayList<ViewHolder> mAttachedScrap = new ArrayList<>();
        ArrayList<ViewHolder> mChangedScrap = null;
        final ArrayList<ViewHolder> mCachedViews = new ArrayList<>();
        private final List<ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
        private int mRequestedCacheMax = 2;
        int mViewCacheMax = 2;

        public Recycler() {
        }

        public void clear() {
            this.mAttachedScrap.clear();
            recycleAndClearCachedViews();
        }

        public void setViewCacheSize(int viewCount) {
            this.mRequestedCacheMax = viewCount;
            updateViewCacheSize();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void updateViewCacheSize() {
            int extraCache = RecyclerView.this.mLayout != null ? RecyclerView.this.mLayout.mPrefetchMaxCountObserved : 0;
            this.mViewCacheMax = this.mRequestedCacheMax + extraCache;
            for (int i = this.mCachedViews.size() - 1; i >= 0 && this.mCachedViews.size() > this.mViewCacheMax; i--) {
                recycleCachedViewAt(i);
            }
        }

        @NonNull
        public List<ViewHolder> getScrapList() {
            return this.mUnmodifiableAttachedScrap;
        }

        boolean validateViewHolderForOffsetPosition(ViewHolder holder) {
            if (holder.isRemoved()) {
                return RecyclerView.this.mState.isPreLayout();
            }
            if (holder.mPosition < 0 || holder.mPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + holder + RecyclerView.this.exceptionLabel());
            }
            if (!RecyclerView.this.mState.isPreLayout()) {
                int type = RecyclerView.this.mAdapter.getItemViewType(holder.mPosition);
                if (type != holder.getItemViewType()) {
                    return false;
                }
            }
            return !RecyclerView.this.mAdapter.hasStableIds() || holder.getItemId() == RecyclerView.this.mAdapter.getItemId(holder.mPosition);
        }

        private boolean tryBindViewHolderByDeadline(@NonNull ViewHolder holder, int offsetPosition, int position, long deadlineNs) {
            holder.mOwnerRecyclerView = RecyclerView.this;
            int viewType = holder.getItemViewType();
            long startBindNs = RecyclerView.this.getNanoTime();
            if (deadlineNs != RecyclerView.FOREVER_NS && !this.mRecyclerPool.willBindInTime(viewType, startBindNs, deadlineNs)) {
                return false;
            }
            RecyclerView.this.mAdapter.bindViewHolder(holder, offsetPosition);
            long endBindNs = RecyclerView.this.getNanoTime();
            this.mRecyclerPool.factorInBindTime(holder.getItemViewType(), endBindNs - startBindNs);
            attachAccessibilityDelegateOnBind(holder);
            if (RecyclerView.this.mState.isPreLayout()) {
                holder.mPreLayoutPosition = position;
                return true;
            }
            return true;
        }

        public void bindViewToPosition(@NonNull View view, int position) {
            LayoutParams rvLayoutParams;
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view);
            if (holder == null) {
                throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter" + RecyclerView.this.exceptionLabel());
            }
            int offsetPosition = RecyclerView.this.mAdapterHelper.findPositionOffset(position);
            if (offsetPosition < 0 || offsetPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + position + "(offset:" + offsetPosition + ").state:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
            }
            tryBindViewHolderByDeadline(holder, offsetPosition, position, RecyclerView.FOREVER_NS);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp == null) {
                rvLayoutParams = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                holder.itemView.setLayoutParams(rvLayoutParams);
            } else if (!RecyclerView.this.checkLayoutParams(lp)) {
                rvLayoutParams = (LayoutParams) RecyclerView.this.generateLayoutParams(lp);
                holder.itemView.setLayoutParams(rvLayoutParams);
            } else {
                rvLayoutParams = (LayoutParams) lp;
            }
            boolean z = true;
            rvLayoutParams.mInsetsDirty = true;
            rvLayoutParams.mViewHolder = holder;
            if (holder.itemView.getParent() != null) {
                z = false;
            }
            rvLayoutParams.mPendingInvalidate = z;
        }

        public int convertPreLayoutPositionToPostLayout(int position) {
            if (position < 0 || position >= RecyclerView.this.mState.getItemCount()) {
                throw new IndexOutOfBoundsException("invalid position " + position + ". State item count is " + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
            } else if (!RecyclerView.this.mState.isPreLayout()) {
                return position;
            } else {
                return RecyclerView.this.mAdapterHelper.findPositionOffset(position);
            }
        }

        @NonNull
        public View getViewForPosition(int position) {
            return getViewForPosition(position, false);
        }

        View getViewForPosition(int position, boolean dryRun) {
            return tryGetViewHolderForPositionByDeadline(position, dryRun, RecyclerView.FOREVER_NS).itemView;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Nullable
        public ViewHolder tryGetViewHolderForPositionByDeadline(int position, boolean dryRun, long deadlineNs) {
            boolean fromScrapOrHiddenOrCache;
            ViewHolder holder;
            LayoutParams rvLayoutParams;
            RecyclerView innerView;
            ViewCacheExtension viewCacheExtension;
            View view;
            if (position < 0 || position >= RecyclerView.this.mState.getItemCount()) {
                throw new IndexOutOfBoundsException("Invalid item position " + position + "(" + position + "). Item count:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
            }
            boolean fromScrapOrHiddenOrCache2 = false;
            ViewHolder holder2 = null;
            boolean z = true;
            if (RecyclerView.this.mState.isPreLayout()) {
                holder2 = getChangedScrapViewForPosition(position);
                fromScrapOrHiddenOrCache2 = holder2 != null;
            }
            if (holder2 == null && (holder2 = getScrapOrHiddenOrCachedHolderForPosition(position, dryRun)) != null) {
                if (!validateViewHolderForOffsetPosition(holder2)) {
                    if (!dryRun) {
                        holder2.addFlags(4);
                        if (holder2.isScrap()) {
                            RecyclerView.this.removeDetachedView(holder2.itemView, false);
                            holder2.unScrap();
                        } else if (holder2.wasReturnedFromScrap()) {
                            holder2.clearReturnedFromScrapFlag();
                        }
                        recycleViewHolderInternal(holder2);
                    }
                    holder2 = null;
                } else {
                    fromScrapOrHiddenOrCache2 = true;
                }
            }
            if (holder2 != null) {
                fromScrapOrHiddenOrCache = fromScrapOrHiddenOrCache2;
                holder = holder2;
            } else {
                int offsetPosition = RecyclerView.this.mAdapterHelper.findPositionOffset(position);
                if (offsetPosition < 0 || offsetPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                    throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + position + "(offset:" + offsetPosition + ").state:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
                }
                int type = RecyclerView.this.mAdapter.getItemViewType(offsetPosition);
                if (RecyclerView.this.mAdapter.hasStableIds() && (holder2 = getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(offsetPosition), type, dryRun)) != null) {
                    holder2.mPosition = offsetPosition;
                    fromScrapOrHiddenOrCache2 = true;
                }
                if (holder2 == null && (viewCacheExtension = this.mViewCacheExtension) != null && (view = viewCacheExtension.getViewForPositionAndType(this, position, type)) != null) {
                    holder2 = RecyclerView.this.getChildViewHolder(view);
                    if (holder2 == null) {
                        throw new IllegalArgumentException("getViewForPositionAndType returned a view which does not have a ViewHolder" + RecyclerView.this.exceptionLabel());
                    } else if (holder2.shouldIgnore()) {
                        throw new IllegalArgumentException("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view." + RecyclerView.this.exceptionLabel());
                    }
                }
                if (holder2 == null && (holder2 = getRecycledViewPool().getRecycledView(type)) != null) {
                    holder2.resetInternal();
                    if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST) {
                        invalidateDisplayListInt(holder2);
                    }
                }
                if (holder2 != null) {
                    fromScrapOrHiddenOrCache = fromScrapOrHiddenOrCache2;
                    holder = holder2;
                } else {
                    long start = RecyclerView.this.getNanoTime();
                    if (deadlineNs == RecyclerView.FOREVER_NS || this.mRecyclerPool.willCreateInTime(type, start, deadlineNs)) {
                        ViewHolder holder3 = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, type);
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK && (innerView = RecyclerView.findNestedRecyclerView(holder3.itemView)) != null) {
                            holder3.mNestedRecyclerView = new WeakReference<>(innerView);
                        }
                        long end = RecyclerView.this.getNanoTime();
                        this.mRecyclerPool.factorInCreateTime(type, end - start);
                        fromScrapOrHiddenOrCache = fromScrapOrHiddenOrCache2;
                        holder = holder3;
                    } else {
                        return null;
                    }
                }
            }
            if (fromScrapOrHiddenOrCache && !RecyclerView.this.mState.isPreLayout() && holder.hasAnyOfTheFlags(8192)) {
                holder.setFlags(0, 8192);
                if (RecyclerView.this.mState.mRunSimpleAnimations) {
                    int changeFlags = ItemAnimator.buildAdapterChangeFlagsForAnimations(holder);
                    ItemAnimator.ItemHolderInfo info = RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, holder, changeFlags | 4096, holder.getUnmodifiedPayloads());
                    RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(holder, info);
                }
            }
            boolean bound = false;
            if (RecyclerView.this.mState.isPreLayout() && holder.isBound()) {
                holder.mPreLayoutPosition = position;
            } else if (!holder.isBound() || holder.needsUpdate() || holder.isInvalid()) {
                bound = tryBindViewHolderByDeadline(holder, RecyclerView.this.mAdapterHelper.findPositionOffset(position), position, deadlineNs);
            }
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp == null) {
                rvLayoutParams = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                holder.itemView.setLayoutParams(rvLayoutParams);
            } else if (!RecyclerView.this.checkLayoutParams(lp)) {
                rvLayoutParams = (LayoutParams) RecyclerView.this.generateLayoutParams(lp);
                holder.itemView.setLayoutParams(rvLayoutParams);
            } else {
                rvLayoutParams = (LayoutParams) lp;
            }
            rvLayoutParams.mViewHolder = holder;
            if (!fromScrapOrHiddenOrCache || !bound) {
                z = false;
            }
            rvLayoutParams.mPendingInvalidate = z;
            return holder;
        }

        private void attachAccessibilityDelegateOnBind(ViewHolder holder) {
            if (RecyclerView.this.isAccessibilityEnabled()) {
                View itemView = holder.itemView;
                if (ViewCompat.getImportantForAccessibility(itemView) == 0) {
                    ViewCompat.setImportantForAccessibility(itemView, 1);
                }
                if (!ViewCompat.hasAccessibilityDelegate(itemView)) {
                    holder.addFlags(16384);
                    ViewCompat.setAccessibilityDelegate(itemView, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
                }
            }
        }

        private void invalidateDisplayListInt(ViewHolder holder) {
            if (holder.itemView instanceof ViewGroup) {
                invalidateDisplayListInt((ViewGroup) holder.itemView, false);
            }
        }

        private void invalidateDisplayListInt(ViewGroup viewGroup, boolean invalidateThis) {
            for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof ViewGroup) {
                    invalidateDisplayListInt((ViewGroup) view, true);
                }
            }
            if (!invalidateThis) {
                return;
            }
            if (viewGroup.getVisibility() == 4) {
                viewGroup.setVisibility(0);
                viewGroup.setVisibility(4);
                return;
            }
            int visibility = viewGroup.getVisibility();
            viewGroup.setVisibility(4);
            viewGroup.setVisibility(visibility);
        }

        public void recycleView(@NonNull View view) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view);
            if (holder.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(view, false);
            }
            if (holder.isScrap()) {
                holder.unScrap();
            } else if (holder.wasReturnedFromScrap()) {
                holder.clearReturnedFromScrapFlag();
            }
            recycleViewHolderInternal(holder);
        }

        void recycleViewInternal(View view) {
            recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(view));
        }

        void recycleAndClearCachedViews() {
            int count = this.mCachedViews.size();
            for (int i = count - 1; i >= 0; i--) {
                recycleCachedViewAt(i);
            }
            this.mCachedViews.clear();
            if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
            }
        }

        void recycleCachedViewAt(int cachedViewIndex) {
            ViewHolder viewHolder = this.mCachedViews.get(cachedViewIndex);
            addViewHolderToRecycledViewPool(viewHolder, true);
            this.mCachedViews.remove(cachedViewIndex);
        }

        void recycleViewHolderInternal(ViewHolder holder) {
            boolean z = false;
            if (holder.isScrap() || holder.itemView.getParent() != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Scrapped or attached views may not be recycled. isScrap:");
                sb.append(holder.isScrap());
                sb.append(" isAttached:");
                if (holder.itemView.getParent() != null) {
                    z = true;
                }
                sb.append(z);
                sb.append(RecyclerView.this.exceptionLabel());
                throw new IllegalArgumentException(sb.toString());
            } else if (holder.isTmpDetached()) {
                throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + holder + RecyclerView.this.exceptionLabel());
            } else if (holder.shouldIgnore()) {
                throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle." + RecyclerView.this.exceptionLabel());
            } else {
                boolean transientStatePreventsRecycling = holder.doesTransientStatePreventRecycling();
                boolean forceRecycle = RecyclerView.this.mAdapter != null && transientStatePreventsRecycling && RecyclerView.this.mAdapter.onFailedToRecycleView(holder);
                boolean cached = false;
                boolean recycled = false;
                if (forceRecycle || holder.isRecyclable()) {
                    if (this.mViewCacheMax > 0 && !holder.hasAnyOfTheFlags(526)) {
                        int cachedViewSize = this.mCachedViews.size();
                        if (cachedViewSize >= this.mViewCacheMax && cachedViewSize > 0) {
                            recycleCachedViewAt(0);
                            cachedViewSize--;
                        }
                        int targetCacheIndex = cachedViewSize;
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK && cachedViewSize > 0 && !RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(holder.mPosition)) {
                            int cacheIndex = cachedViewSize - 1;
                            while (cacheIndex >= 0) {
                                int cachedPos = this.mCachedViews.get(cacheIndex).mPosition;
                                if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(cachedPos)) {
                                    break;
                                }
                                cacheIndex--;
                            }
                            targetCacheIndex = cacheIndex + 1;
                        }
                        this.mCachedViews.add(targetCacheIndex, holder);
                        cached = true;
                    }
                    if (!cached) {
                        addViewHolderToRecycledViewPool(holder, true);
                        recycled = true;
                    }
                }
                RecyclerView.this.mViewInfoStore.removeViewHolder(holder);
                if (!cached && !recycled && transientStatePreventsRecycling) {
                    holder.mOwnerRecyclerView = null;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addViewHolderToRecycledViewPool(@NonNull ViewHolder holder, boolean dispatchRecycled) {
            RecyclerView.clearNestedRecyclerViewIfNotNested(holder);
            if (holder.hasAnyOfTheFlags(16384)) {
                holder.setFlags(0, 16384);
                ViewCompat.setAccessibilityDelegate(holder.itemView, null);
            }
            if (dispatchRecycled) {
                dispatchViewRecycled(holder);
            }
            holder.mOwnerRecyclerView = null;
            getRecycledViewPool().putRecycledView(holder);
        }

        void quickRecycleScrapView(View view) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view);
            holder.mScrapContainer = null;
            holder.mInChangeScrap = false;
            holder.clearReturnedFromScrapFlag();
            recycleViewHolderInternal(holder);
        }

        void scrapView(View view) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view);
            if (holder.hasAnyOfTheFlags(12) || !holder.isUpdated() || RecyclerView.this.canReuseUpdatedViewHolder(holder)) {
                if (holder.isInvalid() && !holder.isRemoved() && !RecyclerView.this.mAdapter.hasStableIds()) {
                    throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool." + RecyclerView.this.exceptionLabel());
                }
                holder.setScrapContainer(this, false);
                this.mAttachedScrap.add(holder);
                return;
            }
            if (this.mChangedScrap == null) {
                this.mChangedScrap = new ArrayList<>();
            }
            holder.setScrapContainer(this, true);
            this.mChangedScrap.add(holder);
        }

        void unscrapView(ViewHolder holder) {
            if (holder.mInChangeScrap) {
                this.mChangedScrap.remove(holder);
            } else {
                this.mAttachedScrap.remove(holder);
            }
            holder.mScrapContainer = null;
            holder.mInChangeScrap = false;
            holder.clearReturnedFromScrapFlag();
        }

        int getScrapCount() {
            return this.mAttachedScrap.size();
        }

        View getScrapViewAt(int index) {
            return this.mAttachedScrap.get(index).itemView;
        }

        void clearScrap() {
            this.mAttachedScrap.clear();
            ArrayList<ViewHolder> arrayList = this.mChangedScrap;
            if (arrayList != null) {
                arrayList.clear();
            }
        }

        ViewHolder getChangedScrapViewForPosition(int position) {
            int changedScrapSize;
            int offsetPosition;
            ArrayList<ViewHolder> arrayList = this.mChangedScrap;
            if (arrayList == null || (changedScrapSize = arrayList.size()) == 0) {
                return null;
            }
            for (int i = 0; i < changedScrapSize; i++) {
                ViewHolder holder = this.mChangedScrap.get(i);
                if (!holder.wasReturnedFromScrap() && holder.getLayoutPosition() == position) {
                    holder.addFlags(32);
                    return holder;
                }
            }
            if (RecyclerView.this.mAdapter.hasStableIds() && (offsetPosition = RecyclerView.this.mAdapterHelper.findPositionOffset(position)) > 0 && offsetPosition < RecyclerView.this.mAdapter.getItemCount()) {
                long id = RecyclerView.this.mAdapter.getItemId(offsetPosition);
                for (int i2 = 0; i2 < changedScrapSize; i2++) {
                    ViewHolder holder2 = this.mChangedScrap.get(i2);
                    if (!holder2.wasReturnedFromScrap() && holder2.getItemId() == id) {
                        holder2.addFlags(32);
                        return holder2;
                    }
                }
            }
            return null;
        }

        ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int position, boolean dryRun) {
            View view;
            int scrapCount = this.mAttachedScrap.size();
            for (int i = 0; i < scrapCount; i++) {
                ViewHolder holder = this.mAttachedScrap.get(i);
                if (!holder.wasReturnedFromScrap() && holder.getLayoutPosition() == position && !holder.isInvalid() && (RecyclerView.this.mState.mInPreLayout || !holder.isRemoved())) {
                    holder.addFlags(32);
                    return holder;
                }
            }
            if (!dryRun && (view = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(position)) != null) {
                ViewHolder vh = RecyclerView.getChildViewHolderInt(view);
                RecyclerView.this.mChildHelper.unhide(view);
                int layoutIndex = RecyclerView.this.mChildHelper.indexOfChild(view);
                if (layoutIndex == -1) {
                    throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + vh + RecyclerView.this.exceptionLabel());
                }
                RecyclerView.this.mChildHelper.detachViewFromParent(layoutIndex);
                scrapView(view);
                vh.addFlags(8224);
                return vh;
            }
            int cacheSize = this.mCachedViews.size();
            for (int i2 = 0; i2 < cacheSize; i2++) {
                ViewHolder holder2 = this.mCachedViews.get(i2);
                if (!holder2.isInvalid() && holder2.getLayoutPosition() == position) {
                    if (!dryRun) {
                        this.mCachedViews.remove(i2);
                    }
                    return holder2;
                }
            }
            return null;
        }

        ViewHolder getScrapOrCachedViewForId(long id, int type, boolean dryRun) {
            int count = this.mAttachedScrap.size();
            for (int i = count - 1; i >= 0; i--) {
                ViewHolder holder = this.mAttachedScrap.get(i);
                if (holder.getItemId() == id && !holder.wasReturnedFromScrap()) {
                    if (type == holder.getItemViewType()) {
                        holder.addFlags(32);
                        if (holder.isRemoved() && !RecyclerView.this.mState.isPreLayout()) {
                            holder.setFlags(2, 14);
                        }
                        return holder;
                    } else if (!dryRun) {
                        this.mAttachedScrap.remove(i);
                        RecyclerView.this.removeDetachedView(holder.itemView, false);
                        quickRecycleScrapView(holder.itemView);
                    }
                }
            }
            int cacheSize = this.mCachedViews.size();
            for (int i2 = cacheSize - 1; i2 >= 0; i2--) {
                ViewHolder holder2 = this.mCachedViews.get(i2);
                if (holder2.getItemId() == id) {
                    if (type == holder2.getItemViewType()) {
                        if (!dryRun) {
                            this.mCachedViews.remove(i2);
                        }
                        return holder2;
                    } else if (!dryRun) {
                        recycleCachedViewAt(i2);
                        return null;
                    }
                }
            }
            return null;
        }

        void dispatchViewRecycled(@NonNull ViewHolder holder) {
            if (RecyclerView.this.mRecyclerListener != null) {
                RecyclerView.this.mRecyclerListener.onViewRecycled(holder);
            }
            if (RecyclerView.this.mAdapter != null) {
                RecyclerView.this.mAdapter.onViewRecycled(holder);
            }
            if (RecyclerView.this.mState != null) {
                RecyclerView.this.mViewInfoStore.removeViewHolder(holder);
            }
        }

        void onAdapterChanged(Adapter oldAdapter, Adapter newAdapter, boolean compatibleWithPrevious) {
            clear();
            getRecycledViewPool().onAdapterChanged(oldAdapter, newAdapter, compatibleWithPrevious);
        }

        void offsetPositionRecordsForMove(int from, int to) {
            int start;
            int end;
            int inBetweenOffset;
            if (from < to) {
                start = from;
                end = to;
                inBetweenOffset = -1;
            } else {
                start = to;
                end = from;
                inBetweenOffset = 1;
            }
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null && holder.mPosition >= start && holder.mPosition <= end) {
                    if (holder.mPosition == from) {
                        holder.offsetPosition(to - from, false);
                    } else {
                        holder.offsetPosition(inBetweenOffset, false);
                    }
                }
            }
        }

        void offsetPositionRecordsForInsert(int insertedAt, int count) {
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null && holder.mPosition >= insertedAt) {
                    holder.offsetPosition(count, true);
                }
            }
        }

        void offsetPositionRecordsForRemove(int removedFrom, int count, boolean applyToPreLayout) {
            int removedEnd = removedFrom + count;
            int cachedCount = this.mCachedViews.size();
            for (int i = cachedCount - 1; i >= 0; i--) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null) {
                    if (holder.mPosition >= removedEnd) {
                        holder.offsetPosition(-count, applyToPreLayout);
                    } else if (holder.mPosition >= removedFrom) {
                        holder.addFlags(8);
                        recycleCachedViewAt(i);
                    }
                }
            }
        }

        void setViewCacheExtension(ViewCacheExtension extension) {
            this.mViewCacheExtension = extension;
        }

        void setRecycledViewPool(RecycledViewPool pool) {
            RecycledViewPool recycledViewPool = this.mRecyclerPool;
            if (recycledViewPool != null) {
                recycledViewPool.detach();
            }
            this.mRecyclerPool = pool;
            if (this.mRecyclerPool != null && RecyclerView.this.getAdapter() != null) {
                this.mRecyclerPool.attach();
            }
        }

        RecycledViewPool getRecycledViewPool() {
            if (this.mRecyclerPool == null) {
                this.mRecyclerPool = new RecycledViewPool();
            }
            return this.mRecyclerPool;
        }

        void viewRangeUpdate(int positionStart, int itemCount) {
            int pos;
            int positionEnd = positionStart + itemCount;
            int cachedCount = this.mCachedViews.size();
            for (int i = cachedCount - 1; i >= 0; i--) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null && (pos = holder.mPosition) >= positionStart && pos < positionEnd) {
                    holder.addFlags(2);
                    recycleCachedViewAt(i);
                }
            }
        }

        void markKnownViewsInvalid() {
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null) {
                    holder.addFlags(6);
                    holder.addChangePayload(null);
                }
            }
            if (RecyclerView.this.mAdapter == null || !RecyclerView.this.mAdapter.hasStableIds()) {
                recycleAndClearCachedViews();
            }
        }

        void clearOldPositions() {
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                holder.clearOldPosition();
            }
            int scrapCount = this.mAttachedScrap.size();
            for (int i2 = 0; i2 < scrapCount; i2++) {
                this.mAttachedScrap.get(i2).clearOldPosition();
            }
            ArrayList<ViewHolder> arrayList = this.mChangedScrap;
            if (arrayList != null) {
                int changedScrapCount = arrayList.size();
                for (int i3 = 0; i3 < changedScrapCount; i3++) {
                    this.mChangedScrap.get(i3).clearOldPosition();
                }
            }
        }

        void markItemDecorInsetsDirty() {
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                LayoutParams layoutParams = (LayoutParams) holder.itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.mInsetsDirty = true;
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Adapter<VH extends ViewHolder> {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();
        private boolean mHasStableIds = false;

        public abstract int getItemCount();

        public abstract void onBindViewHolder(@NonNull VH vh, int i);

        @NonNull
        /* renamed from: onCreateViewHolder */
        public abstract VH mo195onCreateViewHolder(@NonNull ViewGroup viewGroup, int i);

        public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
            onBindViewHolder(holder, position);
        }

        @NonNull
        public final VH createViewHolder(@NonNull ViewGroup parent, int viewType) {
            try {
                TraceCompat.beginSection(RecyclerView.TRACE_CREATE_VIEW_TAG);
                VH holder = mo195onCreateViewHolder(parent, viewType);
                if (holder.itemView.getParent() != null) {
                    throw new IllegalStateException("ViewHolder views must not be attached when created. Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)");
                }
                holder.mItemViewType = viewType;
                return holder;
            } finally {
                TraceCompat.endSection();
            }
        }

        public final void bindViewHolder(@NonNull VH holder, int position) {
            holder.mPosition = position;
            if (hasStableIds()) {
                holder.mItemId = getItemId(position);
            }
            holder.setFlags(1, 519);
            TraceCompat.beginSection(RecyclerView.TRACE_BIND_VIEW_TAG);
            onBindViewHolder(holder, position, holder.getUnmodifiedPayloads());
            holder.clearPayload();
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof LayoutParams) {
                ((LayoutParams) layoutParams).mInsetsDirty = true;
            }
            TraceCompat.endSection();
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public void setHasStableIds(boolean hasStableIds) {
            if (hasObservers()) {
                throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
            }
            this.mHasStableIds = hasStableIds;
        }

        public long getItemId(int position) {
            return -1L;
        }

        public final boolean hasStableIds() {
            return this.mHasStableIds;
        }

        public void onViewRecycled(@NonNull VH holder) {
        }

        public boolean onFailedToRecycleView(@NonNull VH holder) {
            return false;
        }

        public void onViewAttachedToWindow(@NonNull VH holder) {
        }

        public void onViewDetachedFromWindow(@NonNull VH holder) {
        }

        public final boolean hasObservers() {
            return this.mObservable.hasObservers();
        }

        public void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            this.mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            this.mObservable.unregisterObserver(observer);
        }

        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        }

        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        }

        public final void notifyDataSetChanged() {
            this.mObservable.notifyChanged();
        }

        public final void notifyItemChanged(int position) {
            this.mObservable.notifyItemRangeChanged(position, 1);
        }

        public final void notifyItemChanged(int position, @Nullable Object payload) {
            this.mObservable.notifyItemRangeChanged(position, 1, payload);
        }

        public final void notifyItemRangeChanged(int positionStart, int itemCount) {
            this.mObservable.notifyItemRangeChanged(positionStart, itemCount);
        }

        public final void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            this.mObservable.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        public final void notifyItemInserted(int position) {
            this.mObservable.notifyItemRangeInserted(position, 1);
        }

        public final void notifyItemMoved(int fromPosition, int toPosition) {
            this.mObservable.notifyItemMoved(fromPosition, toPosition);
        }

        public final void notifyItemRangeInserted(int positionStart, int itemCount) {
            this.mObservable.notifyItemRangeInserted(positionStart, itemCount);
        }

        public final void notifyItemRemoved(int position) {
            this.mObservable.notifyItemRangeRemoved(position, 1);
        }

        public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
            this.mObservable.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    void dispatchChildDetached(View child) {
        ViewHolder viewHolder = getChildViewHolderInt(child);
        onChildDetachedFromWindow(child);
        Adapter adapter = this.mAdapter;
        if (adapter != null && viewHolder != null) {
            adapter.onViewDetachedFromWindow(viewHolder);
        }
        List<OnChildAttachStateChangeListener> list = this.mOnChildAttachStateListeners;
        if (list != null) {
            int cnt = list.size();
            for (int i = cnt - 1; i >= 0; i--) {
                this.mOnChildAttachStateListeners.get(i).onChildViewDetachedFromWindow(child);
            }
        }
    }

    void dispatchChildAttached(View child) {
        ViewHolder viewHolder = getChildViewHolderInt(child);
        onChildAttachedToWindow(child);
        Adapter adapter = this.mAdapter;
        if (adapter != null && viewHolder != null) {
            adapter.onViewAttachedToWindow(viewHolder);
        }
        List<OnChildAttachStateChangeListener> list = this.mOnChildAttachStateListeners;
        if (list != null) {
            int cnt = list.size();
            for (int i = cnt - 1; i >= 0; i--) {
                this.mOnChildAttachStateListeners.get(i).onChildViewAttachedToWindow(child);
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class LayoutManager {
        ChildHelper mChildHelper;
        private int mHeight;
        private int mHeightMode;
        int mPrefetchMaxCountObserved;
        boolean mPrefetchMaxObservedInInitialPrefetch;
        RecyclerView mRecyclerView;
        @Nullable
        SmoothScroller mSmoothScroller;
        private int mWidth;
        private int mWidthMode;
        private final ViewBoundsCheck.Callback mHorizontalBoundCheckCallback = new ViewBoundsCheck.Callback() { // from class: android.support.v7.widget.RecyclerView.LayoutManager.1
            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getChildCount() {
                return LayoutManager.this.getChildCount();
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public View getParent() {
                return LayoutManager.this.mRecyclerView;
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public View getChildAt(int index) {
                return LayoutManager.this.getChildAt(index);
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getParentStart() {
                return LayoutManager.this.getPaddingLeft();
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getParentEnd() {
                return LayoutManager.this.getWidth() - LayoutManager.this.getPaddingRight();
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getChildStart(View view) {
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                return LayoutManager.this.getDecoratedLeft(view) - params.leftMargin;
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getChildEnd(View view) {
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                return LayoutManager.this.getDecoratedRight(view) + params.rightMargin;
            }
        };
        private final ViewBoundsCheck.Callback mVerticalBoundCheckCallback = new ViewBoundsCheck.Callback() { // from class: android.support.v7.widget.RecyclerView.LayoutManager.2
            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getChildCount() {
                return LayoutManager.this.getChildCount();
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public View getParent() {
                return LayoutManager.this.mRecyclerView;
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public View getChildAt(int index) {
                return LayoutManager.this.getChildAt(index);
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getParentStart() {
                return LayoutManager.this.getPaddingTop();
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getParentEnd() {
                return LayoutManager.this.getHeight() - LayoutManager.this.getPaddingBottom();
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getChildStart(View view) {
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                return LayoutManager.this.getDecoratedTop(view) - params.topMargin;
            }

            @Override // android.support.v7.widget.ViewBoundsCheck.Callback
            public int getChildEnd(View view) {
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                return LayoutManager.this.getDecoratedBottom(view) + params.bottomMargin;
            }
        };
        ViewBoundsCheck mHorizontalBoundCheck = new ViewBoundsCheck(this.mHorizontalBoundCheckCallback);
        ViewBoundsCheck mVerticalBoundCheck = new ViewBoundsCheck(this.mVerticalBoundCheckCallback);
        boolean mRequestedSimpleAnimations = false;
        boolean mIsAttachedToWindow = false;
        boolean mAutoMeasure = false;
        private boolean mMeasurementCacheEnabled = true;
        private boolean mItemPrefetchEnabled = true;

        /* loaded from: classes.dex */
        public interface LayoutPrefetchRegistry {
            void addPosition(int i, int i2);
        }

        /* loaded from: classes.dex */
        public static class Properties {
            public int orientation;
            public boolean reverseLayout;
            public int spanCount;
            public boolean stackFromEnd;
        }

        public abstract LayoutParams generateDefaultLayoutParams();

        void setRecyclerView(RecyclerView recyclerView) {
            if (recyclerView == null) {
                this.mRecyclerView = null;
                this.mChildHelper = null;
                this.mWidth = 0;
                this.mHeight = 0;
            } else {
                this.mRecyclerView = recyclerView;
                this.mChildHelper = recyclerView.mChildHelper;
                this.mWidth = recyclerView.getWidth();
                this.mHeight = recyclerView.getHeight();
            }
            this.mWidthMode = MemoryConstants.GB;
            this.mHeightMode = MemoryConstants.GB;
        }

        void setMeasureSpecs(int wSpec, int hSpec) {
            this.mWidth = View.MeasureSpec.getSize(wSpec);
            this.mWidthMode = View.MeasureSpec.getMode(wSpec);
            if (this.mWidthMode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mWidth = 0;
            }
            this.mHeight = View.MeasureSpec.getSize(hSpec);
            this.mHeightMode = View.MeasureSpec.getMode(hSpec);
            if (this.mHeightMode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mHeight = 0;
            }
        }

        void setMeasuredDimensionFromChildren(int widthSpec, int heightSpec) {
            int count = getChildCount();
            if (count == 0) {
                this.mRecyclerView.defaultOnMeasure(widthSpec, heightSpec);
                return;
            }
            int minX = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            int minY = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                Rect bounds = this.mRecyclerView.mTempRect;
                getDecoratedBoundsWithMargins(child, bounds);
                if (bounds.left < minX) {
                    minX = bounds.left;
                }
                if (bounds.right > maxX) {
                    maxX = bounds.right;
                }
                if (bounds.top < minY) {
                    minY = bounds.top;
                }
                if (bounds.bottom > maxY) {
                    maxY = bounds.bottom;
                }
            }
            this.mRecyclerView.mTempRect.set(minX, minY, maxX, maxY);
            setMeasuredDimension(this.mRecyclerView.mTempRect, widthSpec, heightSpec);
        }

        public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
            int usedWidth = childrenBounds.width() + getPaddingLeft() + getPaddingRight();
            int usedHeight = childrenBounds.height() + getPaddingTop() + getPaddingBottom();
            int width = chooseSize(wSpec, usedWidth, getMinimumWidth());
            int height = chooseSize(hSpec, usedHeight, getMinimumHeight());
            setMeasuredDimension(width, height);
        }

        public void requestLayout() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.requestLayout();
            }
        }

        public void assertInLayoutOrScroll(String message) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.assertInLayoutOrScroll(message);
            }
        }

        public static int chooseSize(int spec, int desired, int min) {
            int mode = View.MeasureSpec.getMode(spec);
            int size = View.MeasureSpec.getSize(spec);
            if (mode != Integer.MIN_VALUE) {
                if (mode == 1073741824) {
                    return size;
                }
                return Math.max(desired, min);
            }
            return Math.min(size, Math.max(desired, min));
        }

        public void assertNotInLayoutOrScroll(String message) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.assertNotInLayoutOrScroll(message);
            }
        }

        @Deprecated
        public void setAutoMeasureEnabled(boolean enabled) {
            this.mAutoMeasure = enabled;
        }

        public boolean isAutoMeasureEnabled() {
            return this.mAutoMeasure;
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public final void setItemPrefetchEnabled(boolean enabled) {
            if (enabled != this.mItemPrefetchEnabled) {
                this.mItemPrefetchEnabled = enabled;
                this.mPrefetchMaxCountObserved = 0;
                RecyclerView recyclerView = this.mRecyclerView;
                if (recyclerView != null) {
                    recyclerView.mRecycler.updateViewCacheSize();
                }
            }
        }

        public final boolean isItemPrefetchEnabled() {
            return this.mItemPrefetchEnabled;
        }

        public void collectAdjacentPrefetchPositions(int dx, int dy, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }

        public void collectInitialPrefetchPositions(int adapterItemCount, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }

        void dispatchAttachedToWindow(RecyclerView view) {
            this.mIsAttachedToWindow = true;
            onAttachedToWindow(view);
        }

        void dispatchDetachedFromWindow(RecyclerView view, Recycler recycler) {
            this.mIsAttachedToWindow = false;
            onDetachedFromWindow(view, recycler);
        }

        public boolean isAttachedToWindow() {
            return this.mIsAttachedToWindow;
        }

        public void postOnAnimation(Runnable action) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                ViewCompat.postOnAnimation(recyclerView, action);
            }
        }

        public boolean removeCallbacks(Runnable action) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.removeCallbacks(action);
            }
            return false;
        }

        @CallSuper
        public void onAttachedToWindow(RecyclerView view) {
        }

        @Deprecated
        public void onDetachedFromWindow(RecyclerView view) {
        }

        @CallSuper
        public void onDetachedFromWindow(RecyclerView view, Recycler recycler) {
            onDetachedFromWindow(view);
        }

        public boolean getClipToPadding() {
            RecyclerView recyclerView = this.mRecyclerView;
            return recyclerView != null && recyclerView.mClipToPadding;
        }

        public void onLayoutChildren(Recycler recycler, State state) {
            Log.e(RecyclerView.TAG, "You must override onLayoutChildren(Recycler recycler, State state) ");
        }

        public void onLayoutCompleted(State state) {
        }

        public boolean checkLayoutParams(LayoutParams lp) {
            return lp != null;
        }

        public LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
            if (lp instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) lp);
            }
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
            }
            return new LayoutParams(lp);
        }

        public LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
            return new LayoutParams(c, attrs);
        }

        public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
            return 0;
        }

        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
            return 0;
        }

        public boolean canScrollHorizontally() {
            return false;
        }

        public boolean canScrollVertically() {
            return false;
        }

        public void scrollToPosition(int position) {
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
            Log.e(RecyclerView.TAG, "You must override smoothScrollToPosition to support smooth scrolling");
        }

        public void startSmoothScroll(SmoothScroller smoothScroller) {
            SmoothScroller smoothScroller2 = this.mSmoothScroller;
            if (smoothScroller2 != null && smoothScroller != smoothScroller2 && smoothScroller2.isRunning()) {
                this.mSmoothScroller.stop();
            }
            this.mSmoothScroller = smoothScroller;
            this.mSmoothScroller.start(this.mRecyclerView, this);
        }

        public boolean isSmoothScrolling() {
            SmoothScroller smoothScroller = this.mSmoothScroller;
            return smoothScroller != null && smoothScroller.isRunning();
        }

        public int getLayoutDirection() {
            return ViewCompat.getLayoutDirection(this.mRecyclerView);
        }

        public void endAnimation(View view) {
            if (this.mRecyclerView.mItemAnimator != null) {
                this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(view));
            }
        }

        public void addDisappearingView(View child) {
            addDisappearingView(child, -1);
        }

        public void addDisappearingView(View child, int index) {
            addViewInt(child, index, true);
        }

        public void addView(View child) {
            addView(child, -1);
        }

        public void addView(View child, int index) {
            addViewInt(child, index, false);
        }

        private void addViewInt(View child, int index, boolean disappearing) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(child);
            if (disappearing || holder.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(holder);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(holder);
            }
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (holder.wasReturnedFromScrap() || holder.isScrap()) {
                if (holder.isScrap()) {
                    holder.unScrap();
                } else {
                    holder.clearReturnedFromScrapFlag();
                }
                this.mChildHelper.attachViewToParent(child, index, child.getLayoutParams(), false);
            } else if (child.getParent() == this.mRecyclerView) {
                int currentIndex = this.mChildHelper.indexOfChild(child);
                if (index == -1) {
                    index = this.mChildHelper.getChildCount();
                }
                if (currentIndex == -1) {
                    throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(child) + this.mRecyclerView.exceptionLabel());
                } else if (currentIndex != index) {
                    this.mRecyclerView.mLayout.moveView(currentIndex, index);
                }
            } else {
                this.mChildHelper.addView(child, index, false);
                lp.mInsetsDirty = true;
                SmoothScroller smoothScroller = this.mSmoothScroller;
                if (smoothScroller != null && smoothScroller.isRunning()) {
                    this.mSmoothScroller.onChildAttachedToWindow(child);
                }
            }
            if (lp.mPendingInvalidate) {
                holder.itemView.invalidate();
                lp.mPendingInvalidate = false;
            }
        }

        public void removeView(View child) {
            this.mChildHelper.removeView(child);
        }

        public void removeViewAt(int index) {
            View child = getChildAt(index);
            if (child != null) {
                this.mChildHelper.removeViewAt(index);
            }
        }

        public void removeAllViews() {
            int childCount = getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                this.mChildHelper.removeViewAt(i);
            }
        }

        public int getBaseline() {
            return -1;
        }

        public int getPosition(@NonNull View view) {
            return ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        }

        public int getItemViewType(@NonNull View view) {
            return RecyclerView.getChildViewHolderInt(view).getItemViewType();
        }

        @Nullable
        public View findContainingItemView(@NonNull View view) {
            View found;
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || (found = recyclerView.findContainingItemView(view)) == null || this.mChildHelper.isHidden(found)) {
                return null;
            }
            return found;
        }

        @Nullable
        public View findViewByPosition(int position) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
                if (vh != null && vh.getLayoutPosition() == position && !vh.shouldIgnore() && (this.mRecyclerView.mState.isPreLayout() || !vh.isRemoved())) {
                    return child;
                }
            }
            return null;
        }

        public void detachView(@NonNull View child) {
            int ind = this.mChildHelper.indexOfChild(child);
            if (ind >= 0) {
                detachViewInternal(ind, child);
            }
        }

        public void detachViewAt(int index) {
            detachViewInternal(index, getChildAt(index));
        }

        private void detachViewInternal(int index, @NonNull View view) {
            this.mChildHelper.detachViewFromParent(index);
        }

        public void attachView(@NonNull View child, int index, LayoutParams lp) {
            ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
            if (vh.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(vh);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(vh);
            }
            this.mChildHelper.attachViewToParent(child, index, lp, vh.isRemoved());
        }

        public void attachView(@NonNull View child, int index) {
            attachView(child, index, (LayoutParams) child.getLayoutParams());
        }

        public void attachView(@NonNull View child) {
            attachView(child, -1);
        }

        public void removeDetachedView(@NonNull View child) {
            this.mRecyclerView.removeDetachedView(child, false);
        }

        public void moveView(int fromIndex, int toIndex) {
            View view = getChildAt(fromIndex);
            if (view == null) {
                throw new IllegalArgumentException("Cannot move a child from non-existing index:" + fromIndex + this.mRecyclerView.toString());
            }
            detachViewAt(fromIndex);
            attachView(view, toIndex);
        }

        public void detachAndScrapView(@NonNull View child, @NonNull Recycler recycler) {
            int index = this.mChildHelper.indexOfChild(child);
            scrapOrRecycleView(recycler, index, child);
        }

        public void detachAndScrapViewAt(int index, @NonNull Recycler recycler) {
            View child = getChildAt(index);
            scrapOrRecycleView(recycler, index, child);
        }

        public void removeAndRecycleView(@NonNull View child, @NonNull Recycler recycler) {
            removeView(child);
            recycler.recycleView(child);
        }

        public void removeAndRecycleViewAt(int index, @NonNull Recycler recycler) {
            View view = getChildAt(index);
            removeViewAt(index);
            recycler.recycleView(view);
        }

        public int getChildCount() {
            ChildHelper childHelper = this.mChildHelper;
            if (childHelper != null) {
                return childHelper.getChildCount();
            }
            return 0;
        }

        @Nullable
        public View getChildAt(int index) {
            ChildHelper childHelper = this.mChildHelper;
            if (childHelper != null) {
                return childHelper.getChildAt(index);
            }
            return null;
        }

        public int getWidthMode() {
            return this.mWidthMode;
        }

        public int getHeightMode() {
            return this.mHeightMode;
        }

        @Px
        public int getWidth() {
            return this.mWidth;
        }

        @Px
        public int getHeight() {
            return this.mHeight;
        }

        @Px
        public int getPaddingLeft() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingLeft();
            }
            return 0;
        }

        @Px
        public int getPaddingTop() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingTop();
            }
            return 0;
        }

        @Px
        public int getPaddingRight() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingRight();
            }
            return 0;
        }

        @Px
        public int getPaddingBottom() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingBottom();
            }
            return 0;
        }

        @Px
        public int getPaddingStart() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return ViewCompat.getPaddingStart(recyclerView);
            }
            return 0;
        }

        @Px
        public int getPaddingEnd() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return ViewCompat.getPaddingEnd(recyclerView);
            }
            return 0;
        }

        public boolean isFocused() {
            RecyclerView recyclerView = this.mRecyclerView;
            return recyclerView != null && recyclerView.isFocused();
        }

        public boolean hasFocus() {
            RecyclerView recyclerView = this.mRecyclerView;
            return recyclerView != null && recyclerView.hasFocus();
        }

        @Nullable
        public View getFocusedChild() {
            View focused;
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || (focused = recyclerView.getFocusedChild()) == null || this.mChildHelper.isHidden(focused)) {
                return null;
            }
            return focused;
        }

        public int getItemCount() {
            RecyclerView recyclerView = this.mRecyclerView;
            Adapter a = recyclerView != null ? recyclerView.getAdapter() : null;
            if (a != null) {
                return a.getItemCount();
            }
            return 0;
        }

        public void offsetChildrenHorizontal(@Px int dx) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.offsetChildrenHorizontal(dx);
            }
        }

        public void offsetChildrenVertical(@Px int dy) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.offsetChildrenVertical(dy);
            }
        }

        public void ignoreView(@NonNull View view) {
            ViewParent parent = view.getParent();
            RecyclerView recyclerView = this.mRecyclerView;
            if (parent != recyclerView || recyclerView.indexOfChild(view) == -1) {
                throw new IllegalArgumentException("View should be fully attached to be ignored" + this.mRecyclerView.exceptionLabel());
            }
            ViewHolder vh = RecyclerView.getChildViewHolderInt(view);
            vh.addFlags(128);
            this.mRecyclerView.mViewInfoStore.removeViewHolder(vh);
        }

        public void stopIgnoringView(@NonNull View view) {
            ViewHolder vh = RecyclerView.getChildViewHolderInt(view);
            vh.stopIgnoring();
            vh.resetInternal();
            vh.addFlags(4);
        }

        public void detachAndScrapAttachedViews(@NonNull Recycler recycler) {
            int childCount = getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                View v = getChildAt(i);
                scrapOrRecycleView(recycler, i, v);
            }
        }

        private void scrapOrRecycleView(Recycler recycler, int index, View view) {
            ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(view);
            if (viewHolder.shouldIgnore()) {
                return;
            }
            if (viewHolder.isInvalid() && !viewHolder.isRemoved() && !this.mRecyclerView.mAdapter.hasStableIds()) {
                removeViewAt(index);
                recycler.recycleViewHolderInternal(viewHolder);
                return;
            }
            detachViewAt(index);
            recycler.scrapView(view);
            this.mRecyclerView.mViewInfoStore.onViewDetached(viewHolder);
        }

        void removeAndRecycleScrapInt(Recycler recycler) {
            int scrapCount = recycler.getScrapCount();
            for (int i = scrapCount - 1; i >= 0; i--) {
                View scrap = recycler.getScrapViewAt(i);
                ViewHolder vh = RecyclerView.getChildViewHolderInt(scrap);
                if (!vh.shouldIgnore()) {
                    vh.setIsRecyclable(false);
                    if (vh.isTmpDetached()) {
                        this.mRecyclerView.removeDetachedView(scrap, false);
                    }
                    if (this.mRecyclerView.mItemAnimator != null) {
                        this.mRecyclerView.mItemAnimator.endAnimation(vh);
                    }
                    vh.setIsRecyclable(true);
                    recycler.quickRecycleScrapView(scrap);
                }
            }
            recycler.clearScrap();
            if (scrapCount > 0) {
                this.mRecyclerView.invalidate();
            }
        }

        public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Rect insets = this.mRecyclerView.getItemDecorInsetsForChild(child);
            int widthUsed2 = widthUsed + insets.left + insets.right;
            int heightUsed2 = heightUsed + insets.top + insets.bottom;
            int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + widthUsed2, lp.width, canScrollHorizontally());
            int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + heightUsed2, lp.height, canScrollVertically());
            if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
                child.measure(widthSpec, heightSpec);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean shouldReMeasureChild(View child, int widthSpec, int heightSpec, LayoutParams lp) {
            return !this.mMeasurementCacheEnabled || !isMeasurementUpToDate(child.getMeasuredWidth(), widthSpec, lp.width) || !isMeasurementUpToDate(child.getMeasuredHeight(), heightSpec, lp.height);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean shouldMeasureChild(View child, int widthSpec, int heightSpec, LayoutParams lp) {
            return child.isLayoutRequested() || !this.mMeasurementCacheEnabled || !isMeasurementUpToDate(child.getWidth(), widthSpec, lp.width) || !isMeasurementUpToDate(child.getHeight(), heightSpec, lp.height);
        }

        public boolean isMeasurementCacheEnabled() {
            return this.mMeasurementCacheEnabled;
        }

        public void setMeasurementCacheEnabled(boolean measurementCacheEnabled) {
            this.mMeasurementCacheEnabled = measurementCacheEnabled;
        }

        private static boolean isMeasurementUpToDate(int childSize, int spec, int dimension) {
            int specMode = View.MeasureSpec.getMode(spec);
            int specSize = View.MeasureSpec.getSize(spec);
            if (dimension > 0 && childSize != dimension) {
                return false;
            }
            if (specMode == Integer.MIN_VALUE) {
                return specSize >= childSize;
            } else if (specMode == 0) {
                return true;
            } else {
                return specMode == 1073741824 && specSize == childSize;
            }
        }

        public void measureChildWithMargins(@NonNull View child, int widthUsed, int heightUsed) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Rect insets = this.mRecyclerView.getItemDecorInsetsForChild(child);
            int widthUsed2 = widthUsed + insets.left + insets.right;
            int heightUsed2 = heightUsed + insets.top + insets.bottom;
            int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed2, lp.width, canScrollHorizontally());
            int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin + heightUsed2, lp.height, canScrollVertically());
            if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
                child.measure(widthSpec, heightSpec);
            }
        }

        @Deprecated
        public static int getChildMeasureSpec(int parentSize, int padding, int childDimension, boolean canScroll) {
            int size = Math.max(0, parentSize - padding);
            int resultSize = 0;
            int resultMode = 0;
            if (canScroll) {
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = MemoryConstants.GB;
                } else {
                    resultSize = 0;
                    resultMode = 0;
                }
            } else if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MemoryConstants.GB;
            } else if (childDimension == -1) {
                resultSize = size;
                resultMode = MemoryConstants.GB;
            } else if (childDimension == -2) {
                resultSize = size;
                resultMode = Integer.MIN_VALUE;
            }
            return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode);
        }

        /* JADX WARN: Code restructure failed: missing block: B:11:0x001d, code lost:
            if (r8 != 1073741824) goto L5;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public static int getChildMeasureSpec(int r7, int r8, int r9, int r10, boolean r11) {
            /*
                int r0 = r7 - r9
                r1 = 0
                int r0 = java.lang.Math.max(r1, r0)
                r1 = 0
                r2 = 0
                r3 = 1073741824(0x40000000, float:2.0)
                r4 = -2147483648(0xffffffff80000000, float:-0.0)
                r5 = -2
                r6 = -1
                if (r11 == 0) goto L2c
                if (r10 < 0) goto L17
                r1 = r10
                r2 = 1073741824(0x40000000, float:2.0)
                goto L43
            L17:
                if (r10 != r6) goto L27
                if (r8 == r4) goto L23
                if (r8 == 0) goto L20
                if (r8 == r3) goto L23
                goto L26
            L20:
                r1 = 0
                r2 = 0
                goto L26
            L23:
                r1 = r0
                r2 = r8
            L26:
                goto L43
            L27:
                if (r10 != r5) goto L43
                r1 = 0
                r2 = 0
                goto L43
            L2c:
                if (r10 < 0) goto L32
                r1 = r10
                r2 = 1073741824(0x40000000, float:2.0)
                goto L43
            L32:
                if (r10 != r6) goto L37
                r1 = r0
                r2 = r8
                goto L43
            L37:
                if (r10 != r5) goto L43
                r1 = r0
                if (r8 == r4) goto L41
                if (r8 != r3) goto L3f
                goto L41
            L3f:
                r2 = 0
                goto L43
            L41:
                r2 = -2147483648(0xffffffff80000000, float:-0.0)
            L43:
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.RecyclerView.LayoutManager.getChildMeasureSpec(int, int, int, int, boolean):int");
        }

        public int getDecoratedMeasuredWidth(@NonNull View child) {
            Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
            return child.getMeasuredWidth() + insets.left + insets.right;
        }

        public int getDecoratedMeasuredHeight(@NonNull View child) {
            Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
            return child.getMeasuredHeight() + insets.top + insets.bottom;
        }

        public void layoutDecorated(@NonNull View child, int left, int top, int right, int bottom) {
            Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
            child.layout(insets.left + left, insets.top + top, right - insets.right, bottom - insets.bottom);
        }

        public void layoutDecoratedWithMargins(@NonNull View child, int left, int top, int right, int bottom) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Rect insets = lp.mDecorInsets;
            child.layout(insets.left + left + lp.leftMargin, insets.top + top + lp.topMargin, (right - insets.right) - lp.rightMargin, (bottom - insets.bottom) - lp.bottomMargin);
        }

        public void getTransformedBoundingBox(@NonNull View child, boolean includeDecorInsets, @NonNull Rect out) {
            Matrix childMatrix;
            if (includeDecorInsets) {
                Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
                out.set(-insets.left, -insets.top, child.getWidth() + insets.right, child.getHeight() + insets.bottom);
            } else {
                out.set(0, 0, child.getWidth(), child.getHeight());
            }
            if (this.mRecyclerView != null && (childMatrix = child.getMatrix()) != null && !childMatrix.isIdentity()) {
                RectF tempRectF = this.mRecyclerView.mTempRectF;
                tempRectF.set(out);
                childMatrix.mapRect(tempRectF);
                out.set((int) Math.floor(tempRectF.left), (int) Math.floor(tempRectF.top), (int) Math.ceil(tempRectF.right), (int) Math.ceil(tempRectF.bottom));
            }
            out.offset(child.getLeft(), child.getTop());
        }

        public void getDecoratedBoundsWithMargins(@NonNull View view, @NonNull Rect outBounds) {
            RecyclerView.getDecoratedBoundsWithMarginsInt(view, outBounds);
        }

        public int getDecoratedLeft(@NonNull View child) {
            return child.getLeft() - getLeftDecorationWidth(child);
        }

        public int getDecoratedTop(@NonNull View child) {
            return child.getTop() - getTopDecorationHeight(child);
        }

        public int getDecoratedRight(@NonNull View child) {
            return child.getRight() + getRightDecorationWidth(child);
        }

        public int getDecoratedBottom(@NonNull View child) {
            return child.getBottom() + getBottomDecorationHeight(child);
        }

        public void calculateItemDecorationsForChild(@NonNull View child, @NonNull Rect outRect) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null) {
                outRect.set(0, 0, 0, 0);
                return;
            }
            Rect insets = recyclerView.getItemDecorInsetsForChild(child);
            outRect.set(insets);
        }

        public int getTopDecorationHeight(@NonNull View child) {
            return ((LayoutParams) child.getLayoutParams()).mDecorInsets.top;
        }

        public int getBottomDecorationHeight(@NonNull View child) {
            return ((LayoutParams) child.getLayoutParams()).mDecorInsets.bottom;
        }

        public int getLeftDecorationWidth(@NonNull View child) {
            return ((LayoutParams) child.getLayoutParams()).mDecorInsets.left;
        }

        public int getRightDecorationWidth(@NonNull View child) {
            return ((LayoutParams) child.getLayoutParams()).mDecorInsets.right;
        }

        @Nullable
        public View onFocusSearchFailed(@NonNull View focused, int direction, @NonNull Recycler recycler, @NonNull State state) {
            return null;
        }

        @Nullable
        public View onInterceptFocusSearch(@NonNull View focused, int direction) {
            return null;
        }

        private int[] getChildRectangleOnScreenScrollAmount(RecyclerView parent, View child, Rect rect, boolean immediate) {
            int dx;
            int[] out = new int[2];
            int parentLeft = getPaddingLeft();
            int parentTop = getPaddingTop();
            int parentRight = getWidth() - getPaddingRight();
            int parentBottom = getHeight() - getPaddingBottom();
            int childLeft = (child.getLeft() + rect.left) - child.getScrollX();
            int childTop = (child.getTop() + rect.top) - child.getScrollY();
            int childRight = rect.width() + childLeft;
            int childBottom = rect.height() + childTop;
            int offScreenLeft = Math.min(0, childLeft - parentLeft);
            int offScreenTop = Math.min(0, childTop - parentTop);
            int offScreenRight = Math.max(0, childRight - parentRight);
            int offScreenBottom = Math.max(0, childBottom - parentBottom);
            if (getLayoutDirection() == 1) {
                dx = offScreenRight != 0 ? offScreenRight : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                dx = offScreenLeft != 0 ? offScreenLeft : Math.min(childLeft - parentLeft, offScreenRight);
            }
            int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);
            out[0] = dx;
            out[1] = dy;
            return out;
        }

        public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate) {
            return requestChildRectangleOnScreen(parent, child, rect, immediate, false);
        }

        public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
            int[] scrollAmount = getChildRectangleOnScreenScrollAmount(parent, child, rect, immediate);
            int dx = scrollAmount[0];
            int dy = scrollAmount[1];
            if ((focusedChildVisible && !isFocusedChildVisibleAfterScrolling(parent, dx, dy)) || (dx == 0 && dy == 0)) {
                return false;
            }
            if (immediate) {
                parent.scrollBy(dx, dy);
            } else {
                parent.smoothScrollBy(dx, dy);
            }
            return true;
        }

        public boolean isViewPartiallyVisible(@NonNull View child, boolean completelyVisible, boolean acceptEndPointInclusion) {
            boolean isViewFullyVisible = this.mHorizontalBoundCheck.isViewWithinBoundFlags(child, 24579) && this.mVerticalBoundCheck.isViewWithinBoundFlags(child, 24579);
            if (completelyVisible) {
                return isViewFullyVisible;
            }
            return !isViewFullyVisible;
        }

        private boolean isFocusedChildVisibleAfterScrolling(RecyclerView parent, int dx, int dy) {
            View focusedChild = parent.getFocusedChild();
            if (focusedChild == null) {
                return false;
            }
            int parentLeft = getPaddingLeft();
            int parentTop = getPaddingTop();
            int parentRight = getWidth() - getPaddingRight();
            int parentBottom = getHeight() - getPaddingBottom();
            Rect bounds = this.mRecyclerView.mTempRect;
            getDecoratedBoundsWithMargins(focusedChild, bounds);
            if (bounds.left - dx >= parentRight || bounds.right - dx <= parentLeft || bounds.top - dy >= parentBottom || bounds.bottom - dy <= parentTop) {
                return false;
            }
            return true;
        }

        @Deprecated
        public boolean onRequestChildFocus(@NonNull RecyclerView parent, @NonNull View child, @Nullable View focused) {
            return isSmoothScrolling() || parent.isComputingLayout();
        }

        public boolean onRequestChildFocus(@NonNull RecyclerView parent, @NonNull State state, @NonNull View child, @Nullable View focused) {
            return onRequestChildFocus(parent, child, focused);
        }

        public void onAdapterChanged(@Nullable Adapter oldAdapter, @Nullable Adapter newAdapter) {
        }

        public boolean onAddFocusables(@NonNull RecyclerView recyclerView, @NonNull ArrayList<View> views, int direction, int focusableMode) {
            return false;
        }

        public void onItemsChanged(@NonNull RecyclerView recyclerView) {
        }

        public void onItemsAdded(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
        }

        public void onItemsRemoved(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
        }

        public void onItemsUpdated(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
        }

        public void onItemsUpdated(@NonNull RecyclerView recyclerView, int positionStart, int itemCount, @Nullable Object payload) {
            onItemsUpdated(recyclerView, positionStart, itemCount);
        }

        public void onItemsMoved(@NonNull RecyclerView recyclerView, int from, int to, int itemCount) {
        }

        public int computeHorizontalScrollExtent(@NonNull State state) {
            return 0;
        }

        public int computeHorizontalScrollOffset(@NonNull State state) {
            return 0;
        }

        public int computeHorizontalScrollRange(@NonNull State state) {
            return 0;
        }

        public int computeVerticalScrollExtent(@NonNull State state) {
            return 0;
        }

        public int computeVerticalScrollOffset(@NonNull State state) {
            return 0;
        }

        public int computeVerticalScrollRange(@NonNull State state) {
            return 0;
        }

        public void onMeasure(@NonNull Recycler recycler, @NonNull State state, int widthSpec, int heightSpec) {
            this.mRecyclerView.defaultOnMeasure(widthSpec, heightSpec);
        }

        public void setMeasuredDimension(int widthSize, int heightSize) {
            this.mRecyclerView.setMeasuredDimension(widthSize, heightSize);
        }

        @Px
        public int getMinimumWidth() {
            return ViewCompat.getMinimumWidth(this.mRecyclerView);
        }

        @Px
        public int getMinimumHeight() {
            return ViewCompat.getMinimumHeight(this.mRecyclerView);
        }

        @Nullable
        public Parcelable onSaveInstanceState() {
            return null;
        }

        public void onRestoreInstanceState(Parcelable state) {
        }

        void stopSmoothScroller() {
            SmoothScroller smoothScroller = this.mSmoothScroller;
            if (smoothScroller != null) {
                smoothScroller.stop();
            }
        }

        void onSmoothScrollerStopped(SmoothScroller smoothScroller) {
            if (this.mSmoothScroller == smoothScroller) {
                this.mSmoothScroller = null;
            }
        }

        public void onScrollStateChanged(int state) {
        }

        public void removeAndRecycleAllViews(@NonNull Recycler recycler) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View view = getChildAt(i);
                if (!RecyclerView.getChildViewHolderInt(view).shouldIgnore()) {
                    removeAndRecycleViewAt(i, recycler);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat info) {
            onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, info);
        }

        public void onInitializeAccessibilityNodeInfo(@NonNull Recycler recycler, @NonNull State state, @NonNull AccessibilityNodeInfoCompat info) {
            if (this.mRecyclerView.canScrollVertically(-1) || this.mRecyclerView.canScrollHorizontally(-1)) {
                info.addAction(8192);
                info.setScrollable(true);
            }
            if (this.mRecyclerView.canScrollVertically(1) || this.mRecyclerView.canScrollHorizontally(1)) {
                info.addAction(4096);
                info.setScrollable(true);
            }
            AccessibilityNodeInfoCompat.CollectionInfoCompat collectionInfo = AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(recycler, state), getColumnCountForAccessibility(recycler, state), isLayoutHierarchical(recycler, state), getSelectionModeForAccessibility(recycler, state));
            info.setCollectionInfo(collectionInfo);
        }

        public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
            onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, event);
        }

        public void onInitializeAccessibilityEvent(@NonNull Recycler recycler, @NonNull State state, @NonNull AccessibilityEvent event) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || event == null) {
                return;
            }
            boolean z = true;
            if (!recyclerView.canScrollVertically(1) && !this.mRecyclerView.canScrollVertically(-1) && !this.mRecyclerView.canScrollHorizontally(-1) && !this.mRecyclerView.canScrollHorizontally(1)) {
                z = false;
            }
            event.setScrollable(z);
            if (this.mRecyclerView.mAdapter != null) {
                event.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onInitializeAccessibilityNodeInfoForItem(View host, AccessibilityNodeInfoCompat info) {
            ViewHolder vh = RecyclerView.getChildViewHolderInt(host);
            if (vh != null && !vh.isRemoved() && !this.mChildHelper.isHidden(vh.itemView)) {
                onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, host, info);
            }
        }

        public void onInitializeAccessibilityNodeInfoForItem(@NonNull Recycler recycler, @NonNull State state, @NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
            int rowIndexGuess = canScrollVertically() ? getPosition(host) : 0;
            int columnIndexGuess = canScrollHorizontally() ? getPosition(host) : 0;
            AccessibilityNodeInfoCompat.CollectionItemInfoCompat itemInfo = AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(rowIndexGuess, 1, columnIndexGuess, 1, false, false);
            info.setCollectionItemInfo(itemInfo);
        }

        public void requestSimpleAnimationsInNextLayout() {
            this.mRequestedSimpleAnimations = true;
        }

        public int getSelectionModeForAccessibility(@NonNull Recycler recycler, @NonNull State state) {
            return 0;
        }

        public int getRowCountForAccessibility(@NonNull Recycler recycler, @NonNull State state) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || recyclerView.mAdapter == null || !canScrollVertically()) {
                return 1;
            }
            return this.mRecyclerView.mAdapter.getItemCount();
        }

        public int getColumnCountForAccessibility(@NonNull Recycler recycler, @NonNull State state) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || recyclerView.mAdapter == null || !canScrollHorizontally()) {
                return 1;
            }
            return this.mRecyclerView.mAdapter.getItemCount();
        }

        public boolean isLayoutHierarchical(@NonNull Recycler recycler, @NonNull State state) {
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean performAccessibilityAction(int action, @Nullable Bundle args) {
            return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, action, args);
        }

        public boolean performAccessibilityAction(@NonNull Recycler recycler, @NonNull State state, int action, @Nullable Bundle args) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null) {
                return false;
            }
            int vScroll = 0;
            int hScroll = 0;
            if (action == 4096) {
                if (recyclerView.canScrollVertically(1)) {
                    vScroll = (getHeight() - getPaddingTop()) - getPaddingBottom();
                }
                if (this.mRecyclerView.canScrollHorizontally(1)) {
                    hScroll = (getWidth() - getPaddingLeft()) - getPaddingRight();
                }
            } else if (action == 8192) {
                if (recyclerView.canScrollVertically(-1)) {
                    vScroll = -((getHeight() - getPaddingTop()) - getPaddingBottom());
                }
                if (this.mRecyclerView.canScrollHorizontally(-1)) {
                    hScroll = -((getWidth() - getPaddingLeft()) - getPaddingRight());
                }
            }
            if (vScroll == 0 && hScroll == 0) {
                return false;
            }
            this.mRecyclerView.smoothScrollBy(hScroll, vScroll);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean performAccessibilityActionForItem(@NonNull View view, int action, @Nullable Bundle args) {
            return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, action, args);
        }

        public boolean performAccessibilityActionForItem(@NonNull Recycler recycler, @NonNull State state, @NonNull View view, int action, @Nullable Bundle args) {
            return false;
        }

        public static Properties getProperties(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            Properties properties = new Properties();
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerView, defStyleAttr, defStyleRes);
            properties.orientation = a.getInt(R.styleable.RecyclerView_android_orientation, 1);
            properties.spanCount = a.getInt(R.styleable.RecyclerView_spanCount, 1);
            properties.reverseLayout = a.getBoolean(R.styleable.RecyclerView_reverseLayout, false);
            properties.stackFromEnd = a.getBoolean(R.styleable.RecyclerView_stackFromEnd, false);
            a.recycle();
            return properties;
        }

        void setExactMeasureSpecsFrom(RecyclerView recyclerView) {
            setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), MemoryConstants.GB), View.MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), MemoryConstants.GB));
        }

        boolean shouldMeasureTwice() {
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean hasFlexibleChildInBothOrientations() {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                if (lp.width < 0 && lp.height < 0) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ItemDecoration {
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull State state) {
            onDraw(c, parent);
        }

        @Deprecated
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent) {
        }

        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull State state) {
            onDrawOver(c, parent);
        }

        @Deprecated
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent) {
        }

        @Deprecated
        public void getItemOffsets(@NonNull Rect outRect, int itemPosition, @NonNull RecyclerView parent) {
            outRect.set(0, 0, 0, 0);
        }

        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
            getItemOffsets(outRect, ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition(), parent);
        }
    }

    /* loaded from: classes.dex */
    public static class SimpleOnItemTouchListener implements OnItemTouchListener {
        @Override // android.support.v7.widget.RecyclerView.OnItemTouchListener
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            return false;
        }

        @Override // android.support.v7.widget.RecyclerView.OnItemTouchListener
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        }

        @Override // android.support.v7.widget.RecyclerView.OnItemTouchListener
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class OnScrollListener {
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        }

        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ViewHolder {
        static final int FLAG_ADAPTER_FULLUPDATE = 1024;
        static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
        static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
        static final int FLAG_BOUND = 1;
        static final int FLAG_IGNORE = 128;
        static final int FLAG_INVALID = 4;
        static final int FLAG_MOVED = 2048;
        static final int FLAG_NOT_RECYCLABLE = 16;
        static final int FLAG_REMOVED = 8;
        static final int FLAG_RETURNED_FROM_SCRAP = 32;
        static final int FLAG_SET_A11Y_ITEM_DELEGATE = 16384;
        static final int FLAG_TMP_DETACHED = 256;
        static final int FLAG_UPDATE = 2;
        private static final List<Object> FULLUPDATE_PAYLOADS = Collections.emptyList();
        static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
        @NonNull
        public final View itemView;
        int mFlags;
        WeakReference<RecyclerView> mNestedRecyclerView;
        RecyclerView mOwnerRecyclerView;
        int mPosition = -1;
        int mOldPosition = -1;
        long mItemId = -1;
        int mItemViewType = -1;
        int mPreLayoutPosition = -1;
        ViewHolder mShadowedHolder = null;
        ViewHolder mShadowingHolder = null;
        List<Object> mPayloads = null;
        List<Object> mUnmodifiedPayloads = null;
        private int mIsRecyclableCount = 0;
        Recycler mScrapContainer = null;
        boolean mInChangeScrap = false;
        private int mWasImportantForAccessibilityBeforeHidden = 0;
        @VisibleForTesting
        int mPendingAccessibilityState = -1;

        public ViewHolder(@NonNull View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }

        void flagRemovedAndOffsetPosition(int mNewPosition, int offset, boolean applyToPreLayout) {
            addFlags(8);
            offsetPosition(offset, applyToPreLayout);
            this.mPosition = mNewPosition;
        }

        void offsetPosition(int offset, boolean applyToPreLayout) {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
            if (this.mPreLayoutPosition == -1) {
                this.mPreLayoutPosition = this.mPosition;
            }
            if (applyToPreLayout) {
                this.mPreLayoutPosition += offset;
            }
            this.mPosition += offset;
            if (this.itemView.getLayoutParams() != null) {
                ((LayoutParams) this.itemView.getLayoutParams()).mInsetsDirty = true;
            }
        }

        void clearOldPosition() {
            this.mOldPosition = -1;
            this.mPreLayoutPosition = -1;
        }

        void saveOldPosition() {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean shouldIgnore() {
            return (this.mFlags & 128) != 0;
        }

        @Deprecated
        public final int getPosition() {
            int i = this.mPreLayoutPosition;
            return i == -1 ? this.mPosition : i;
        }

        public final int getLayoutPosition() {
            int i = this.mPreLayoutPosition;
            return i == -1 ? this.mPosition : i;
        }

        public final int getAdapterPosition() {
            RecyclerView recyclerView = this.mOwnerRecyclerView;
            if (recyclerView == null) {
                return -1;
            }
            return recyclerView.getAdapterPositionFor(this);
        }

        public final int getOldPosition() {
            return this.mOldPosition;
        }

        public final long getItemId() {
            return this.mItemId;
        }

        public final int getItemViewType() {
            return this.mItemViewType;
        }

        boolean isScrap() {
            return this.mScrapContainer != null;
        }

        void unScrap() {
            this.mScrapContainer.unscrapView(this);
        }

        boolean wasReturnedFromScrap() {
            return (this.mFlags & 32) != 0;
        }

        void clearReturnedFromScrapFlag() {
            this.mFlags &= -33;
        }

        void clearTmpDetachFlag() {
            this.mFlags &= -257;
        }

        void stopIgnoring() {
            this.mFlags &= -129;
        }

        void setScrapContainer(Recycler recycler, boolean isChangeScrap) {
            this.mScrapContainer = recycler;
            this.mInChangeScrap = isChangeScrap;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isInvalid() {
            return (this.mFlags & 4) != 0;
        }

        boolean needsUpdate() {
            return (this.mFlags & 2) != 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isBound() {
            return (this.mFlags & 1) != 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isRemoved() {
            return (this.mFlags & 8) != 0;
        }

        boolean hasAnyOfTheFlags(int flags) {
            return (this.mFlags & flags) != 0;
        }

        boolean isTmpDetached() {
            return (this.mFlags & 256) != 0;
        }

        boolean isAdapterPositionUnknown() {
            return (this.mFlags & 512) != 0 || isInvalid();
        }

        void setFlags(int flags, int mask) {
            this.mFlags = (this.mFlags & (mask ^ (-1))) | (flags & mask);
        }

        void addFlags(int flags) {
            this.mFlags |= flags;
        }

        void addChangePayload(Object payload) {
            if (payload != null) {
                if ((1024 & this.mFlags) == 0) {
                    createPayloadsIfNeeded();
                    this.mPayloads.add(payload);
                    return;
                }
                return;
            }
            addFlags(1024);
        }

        private void createPayloadsIfNeeded() {
            if (this.mPayloads == null) {
                this.mPayloads = new ArrayList();
                this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
            }
        }

        void clearPayload() {
            List<Object> list = this.mPayloads;
            if (list != null) {
                list.clear();
            }
            this.mFlags &= -1025;
        }

        List<Object> getUnmodifiedPayloads() {
            if ((this.mFlags & 1024) == 0) {
                List<Object> list = this.mPayloads;
                if (list == null || list.size() == 0) {
                    return FULLUPDATE_PAYLOADS;
                }
                return this.mUnmodifiedPayloads;
            }
            return FULLUPDATE_PAYLOADS;
        }

        void resetInternal() {
            this.mFlags = 0;
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1L;
            this.mPreLayoutPosition = -1;
            this.mIsRecyclableCount = 0;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            clearPayload();
            this.mWasImportantForAccessibilityBeforeHidden = 0;
            this.mPendingAccessibilityState = -1;
            RecyclerView.clearNestedRecyclerViewIfNotNested(this);
        }

        void onEnteredHiddenState(RecyclerView parent) {
            int i = this.mPendingAccessibilityState;
            if (i != -1) {
                this.mWasImportantForAccessibilityBeforeHidden = i;
            } else {
                this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
            }
            parent.setChildImportantForAccessibilityInternal(this, 4);
        }

        void onLeftHiddenState(RecyclerView parent) {
            parent.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
            this.mWasImportantForAccessibilityBeforeHidden = 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
            if (isScrap()) {
                sb.append(" scrap ");
                sb.append(this.mInChangeScrap ? "[changeScrap]" : "[attachedScrap]");
            }
            if (isInvalid()) {
                sb.append(" invalid");
            }
            if (!isBound()) {
                sb.append(" unbound");
            }
            if (needsUpdate()) {
                sb.append(" update");
            }
            if (isRemoved()) {
                sb.append(" removed");
            }
            if (shouldIgnore()) {
                sb.append(" ignored");
            }
            if (isTmpDetached()) {
                sb.append(" tmpDetached");
            }
            if (!isRecyclable()) {
                sb.append(" not recyclable(" + this.mIsRecyclableCount + ")");
            }
            if (isAdapterPositionUnknown()) {
                sb.append(" undefined adapter position");
            }
            if (this.itemView.getParent() == null) {
                sb.append(" no parent");
            }
            sb.append("}");
            return sb.toString();
        }

        public final void setIsRecyclable(boolean recyclable) {
            this.mIsRecyclableCount = recyclable ? this.mIsRecyclableCount - 1 : this.mIsRecyclableCount + 1;
            int i = this.mIsRecyclableCount;
            if (i < 0) {
                this.mIsRecyclableCount = 0;
                Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
            } else if (!recyclable && i == 1) {
                this.mFlags |= 16;
            } else if (recyclable && this.mIsRecyclableCount == 0) {
                this.mFlags &= -17;
            }
        }

        public final boolean isRecyclable() {
            return (this.mFlags & 16) == 0 && !ViewCompat.hasTransientState(this.itemView);
        }

        boolean shouldBeKeptAsChild() {
            return (this.mFlags & 16) != 0;
        }

        boolean doesTransientStatePreventRecycling() {
            return (this.mFlags & 16) == 0 && ViewCompat.hasTransientState(this.itemView);
        }

        boolean isUpdated() {
            return (this.mFlags & 2) != 0;
        }
    }

    @VisibleForTesting
    boolean setChildImportantForAccessibilityInternal(ViewHolder viewHolder, int importantForAccessibility) {
        if (isComputingLayout()) {
            viewHolder.mPendingAccessibilityState = importantForAccessibility;
            this.mPendingAccessibilityImportanceChange.add(viewHolder);
            return false;
        }
        ViewCompat.setImportantForAccessibility(viewHolder.itemView, importantForAccessibility);
        return true;
    }

    void dispatchPendingImportantForAccessibilityChanges() {
        int state;
        for (int i = this.mPendingAccessibilityImportanceChange.size() - 1; i >= 0; i--) {
            ViewHolder viewHolder = this.mPendingAccessibilityImportanceChange.get(i);
            if (viewHolder.itemView.getParent() == this && !viewHolder.shouldIgnore() && (state = viewHolder.mPendingAccessibilityState) != -1) {
                ViewCompat.setImportantForAccessibility(viewHolder.itemView, state);
                viewHolder.mPendingAccessibilityState = -1;
            }
        }
        this.mPendingAccessibilityImportanceChange.clear();
    }

    int getAdapterPositionFor(ViewHolder viewHolder) {
        if (viewHolder.hasAnyOfTheFlags(524) || !viewHolder.isBound()) {
            return -1;
        }
        return this.mAdapterHelper.applyPendingUpdatesToPosition(viewHolder.mPosition);
    }

    @VisibleForTesting
    void initFastScroller(StateListDrawable verticalThumbDrawable, Drawable verticalTrackDrawable, StateListDrawable horizontalThumbDrawable, Drawable horizontalTrackDrawable) {
        if (verticalThumbDrawable == null || verticalTrackDrawable == null || horizontalThumbDrawable == null || horizontalTrackDrawable == null) {
            throw new IllegalArgumentException("Trying to set fast scroller without both required drawables." + exceptionLabel());
        }
        Resources resources = getContext().getResources();
        new FastScroller(this, verticalThumbDrawable, verticalTrackDrawable, horizontalThumbDrawable, horizontalTrackDrawable, resources.getDimensionPixelSize(R.dimen.fastscroll_default_thickness), resources.getDimensionPixelSize(R.dimen.fastscroll_minimum_range), resources.getDimensionPixelOffset(R.dimen.fastscroll_margin));
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public void setNestedScrollingEnabled(boolean enabled) {
        getScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean isNestedScrollingEnabled() {
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean startNestedScroll(int axes) {
        return getScrollingChildHelper().startNestedScroll(axes);
    }

    @Override // android.support.v4.view.NestedScrollingChild2
    public boolean startNestedScroll(int axes, int type) {
        return getScrollingChildHelper().startNestedScroll(axes, type);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public void stopNestedScroll() {
        getScrollingChildHelper().stopNestedScroll();
    }

    @Override // android.support.v4.view.NestedScrollingChild2
    public void stopNestedScroll(int type) {
        getScrollingChildHelper().stopNestedScroll(type);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent();
    }

    @Override // android.support.v4.view.NestedScrollingChild2
    public boolean hasNestedScrollingParent(int type) {
        return getScrollingChildHelper().hasNestedScrollingParent(type);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override // android.support.v4.view.NestedScrollingChild2
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override // android.support.v4.view.NestedScrollingChild2
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override // android.view.View, android.support.v4.view.NestedScrollingChild
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        final Rect mDecorInsets = new Rect();
        boolean mInsetsDirty = true;
        boolean mPendingInvalidate = false;
        ViewHolder mViewHolder;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.LayoutParams) source);
        }

        public boolean viewNeedsUpdate() {
            return this.mViewHolder.needsUpdate();
        }

        public boolean isViewInvalid() {
            return this.mViewHolder.isInvalid();
        }

        public boolean isItemRemoved() {
            return this.mViewHolder.isRemoved();
        }

        public boolean isItemChanged() {
            return this.mViewHolder.isUpdated();
        }

        @Deprecated
        public int getViewPosition() {
            return this.mViewHolder.getPosition();
        }

        public int getViewLayoutPosition() {
            return this.mViewHolder.getLayoutPosition();
        }

        public int getViewAdapterPosition() {
            return this.mViewHolder.getAdapterPosition();
        }
    }

    /* loaded from: classes.dex */
    public static abstract class AdapterDataObserver {
        public void onChanged() {
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
        }

        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            onItemRangeChanged(positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class SmoothScroller {
        private LayoutManager mLayoutManager;
        private boolean mPendingInitialRun;
        private RecyclerView mRecyclerView;
        private boolean mRunning;
        private boolean mStarted;
        private View mTargetView;
        private int mTargetPosition = -1;
        private final Action mRecyclingAction = new Action(0, 0);

        /* loaded from: classes.dex */
        public interface ScrollVectorProvider {
            @Nullable
            PointF computeScrollVectorForPosition(int i);
        }

        protected abstract void onSeekTargetStep(@Px int i, @Px int i2, @NonNull State state, @NonNull Action action);

        protected abstract void onStart();

        protected abstract void onStop();

        protected abstract void onTargetFound(@NonNull View view, @NonNull State state, @NonNull Action action);

        void start(RecyclerView recyclerView, LayoutManager layoutManager) {
            if (this.mStarted) {
                Log.w(RecyclerView.TAG, "An instance of " + getClass().getSimpleName() + " was started more than once. Each instance of" + getClass().getSimpleName() + " is intended to only be used once. You should create a new instance for each use.");
            }
            this.mRecyclerView = recyclerView;
            this.mLayoutManager = layoutManager;
            if (this.mTargetPosition == -1) {
                throw new IllegalArgumentException("Invalid target position");
            }
            this.mRecyclerView.mState.mTargetPosition = this.mTargetPosition;
            this.mRunning = true;
            this.mPendingInitialRun = true;
            this.mTargetView = findViewByPosition(getTargetPosition());
            onStart();
            this.mRecyclerView.mViewFlinger.postOnAnimation();
            this.mStarted = true;
        }

        public void setTargetPosition(int targetPosition) {
            this.mTargetPosition = targetPosition;
        }

        @Nullable
        public PointF computeScrollVectorForPosition(int targetPosition) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof ScrollVectorProvider) {
                return ((ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(targetPosition);
            }
            Log.w(RecyclerView.TAG, "You should override computeScrollVectorForPosition when the LayoutManager does not implement " + ScrollVectorProvider.class.getCanonicalName());
            return null;
        }

        @Nullable
        public LayoutManager getLayoutManager() {
            return this.mLayoutManager;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public final void stop() {
            if (!this.mRunning) {
                return;
            }
            this.mRunning = false;
            onStop();
            this.mRecyclerView.mState.mTargetPosition = -1;
            this.mTargetView = null;
            this.mTargetPosition = -1;
            this.mPendingInitialRun = false;
            this.mLayoutManager.onSmoothScrollerStopped(this);
            this.mLayoutManager = null;
            this.mRecyclerView = null;
        }

        public boolean isPendingInitialRun() {
            return this.mPendingInitialRun;
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        public int getTargetPosition() {
            return this.mTargetPosition;
        }

        void onAnimation(int dx, int dy) {
            PointF pointF;
            RecyclerView recyclerView = this.mRecyclerView;
            if (!this.mRunning || this.mTargetPosition == -1 || recyclerView == null) {
                stop();
            }
            if (this.mPendingInitialRun && this.mTargetView == null && this.mLayoutManager != null && (pointF = computeScrollVectorForPosition(this.mTargetPosition)) != null && (pointF.x != 0.0f || pointF.y != 0.0f)) {
                recyclerView.scrollStep((int) Math.signum(pointF.x), (int) Math.signum(pointF.y), null);
            }
            this.mPendingInitialRun = false;
            View view = this.mTargetView;
            if (view != null) {
                if (getChildPosition(view) == this.mTargetPosition) {
                    onTargetFound(this.mTargetView, recyclerView.mState, this.mRecyclingAction);
                    this.mRecyclingAction.runIfNecessary(recyclerView);
                    stop();
                } else {
                    Log.e(RecyclerView.TAG, "Passed over target position while smooth scrolling.");
                    this.mTargetView = null;
                }
            }
            if (this.mRunning) {
                onSeekTargetStep(dx, dy, recyclerView.mState, this.mRecyclingAction);
                boolean hadJumpTarget = this.mRecyclingAction.hasJumpTarget();
                this.mRecyclingAction.runIfNecessary(recyclerView);
                if (hadJumpTarget) {
                    if (this.mRunning) {
                        this.mPendingInitialRun = true;
                        recyclerView.mViewFlinger.postOnAnimation();
                        return;
                    }
                    stop();
                }
            }
        }

        public int getChildPosition(View view) {
            return this.mRecyclerView.getChildLayoutPosition(view);
        }

        public int getChildCount() {
            return this.mRecyclerView.mLayout.getChildCount();
        }

        public View findViewByPosition(int position) {
            return this.mRecyclerView.mLayout.findViewByPosition(position);
        }

        @Deprecated
        public void instantScrollToPosition(int position) {
            this.mRecyclerView.scrollToPosition(position);
        }

        protected void onChildAttachedToWindow(View child) {
            if (getChildPosition(child) == getTargetPosition()) {
                this.mTargetView = child;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void normalize(@NonNull PointF scrollVector) {
            float magnitude = (float) Math.sqrt((scrollVector.x * scrollVector.x) + (scrollVector.y * scrollVector.y));
            scrollVector.x /= magnitude;
            scrollVector.y /= magnitude;
        }

        /* loaded from: classes.dex */
        public static class Action {
            public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
            private boolean mChanged;
            private int mConsecutiveUpdates;
            private int mDuration;
            private int mDx;
            private int mDy;
            private Interpolator mInterpolator;
            private int mJumpToPosition;

            public Action(@Px int dx, @Px int dy) {
                this(dx, dy, Integer.MIN_VALUE, null);
            }

            public Action(@Px int dx, @Px int dy, int duration) {
                this(dx, dy, duration, null);
            }

            public Action(@Px int dx, @Px int dy, int duration, @Nullable Interpolator interpolator) {
                this.mJumpToPosition = -1;
                this.mChanged = false;
                this.mConsecutiveUpdates = 0;
                this.mDx = dx;
                this.mDy = dy;
                this.mDuration = duration;
                this.mInterpolator = interpolator;
            }

            public void jumpTo(int targetPosition) {
                this.mJumpToPosition = targetPosition;
            }

            boolean hasJumpTarget() {
                return this.mJumpToPosition >= 0;
            }

            void runIfNecessary(RecyclerView recyclerView) {
                if (this.mJumpToPosition >= 0) {
                    int position = this.mJumpToPosition;
                    this.mJumpToPosition = -1;
                    recyclerView.jumpToPositionForSmoothScroller(position);
                    this.mChanged = false;
                } else if (this.mChanged) {
                    validate();
                    if (this.mInterpolator == null) {
                        if (this.mDuration == Integer.MIN_VALUE) {
                            recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
                        } else {
                            recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
                        }
                    } else {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
                    }
                    this.mConsecutiveUpdates++;
                    if (this.mConsecutiveUpdates > 10) {
                        Log.e(RecyclerView.TAG, "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
                    }
                    this.mChanged = false;
                } else {
                    this.mConsecutiveUpdates = 0;
                }
            }

            private void validate() {
                if (this.mInterpolator != null && this.mDuration < 1) {
                    throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
                }
                if (this.mDuration < 1) {
                    throw new IllegalStateException("Scroll duration must be a positive number");
                }
            }

            @Px
            public int getDx() {
                return this.mDx;
            }

            public void setDx(@Px int dx) {
                this.mChanged = true;
                this.mDx = dx;
            }

            @Px
            public int getDy() {
                return this.mDy;
            }

            public void setDy(@Px int dy) {
                this.mChanged = true;
                this.mDy = dy;
            }

            public int getDuration() {
                return this.mDuration;
            }

            public void setDuration(int duration) {
                this.mChanged = true;
                this.mDuration = duration;
            }

            @Nullable
            public Interpolator getInterpolator() {
                return this.mInterpolator;
            }

            public void setInterpolator(@Nullable Interpolator interpolator) {
                this.mChanged = true;
                this.mInterpolator = interpolator;
            }

            public void update(@Px int dx, @Px int dy, int duration, @Nullable Interpolator interpolator) {
                this.mDx = dx;
                this.mDy = dy;
                this.mDuration = duration;
                this.mInterpolator = interpolator;
                this.mChanged = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        AdapterDataObservable() {
        }

        public boolean hasObservers() {
            return !this.mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onChanged();
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount, null);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onItemRangeRemoved(positionStart, itemCount);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onItemRangeMoved(fromPosition, toPosition, 1);
            }
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.v7.widget.RecyclerView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            /* renamed from: createFromParcel */
            public SavedState mo169createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override // android.os.Parcelable.Creator
            /* renamed from: createFromParcel */
            public SavedState mo168createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override // android.os.Parcelable.Creator
            /* renamed from: newArray */
            public SavedState[] mo170newArray(int size) {
                return new SavedState[size];
            }
        };
        Parcelable mLayoutState;

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.mLayoutState = in.readParcelable(loader != null ? loader : LayoutManager.class.getClassLoader());
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(this.mLayoutState, 0);
        }

        void copyFrom(SavedState other) {
            this.mLayoutState = other.mLayoutState;
        }
    }

    /* loaded from: classes.dex */
    public static class State {
        static final int STEP_ANIMATIONS = 4;
        static final int STEP_LAYOUT = 2;
        static final int STEP_START = 1;
        private SparseArray<Object> mData;
        long mFocusedItemId;
        int mFocusedItemPosition;
        int mFocusedSubChildId;
        int mRemainingScrollHorizontal;
        int mRemainingScrollVertical;
        int mTargetPosition = -1;
        int mPreviousLayoutItemCount = 0;
        int mDeletedInvisibleItemCountSincePreviousLayout = 0;
        int mLayoutStep = 1;
        int mItemCount = 0;
        boolean mStructureChanged = false;
        boolean mInPreLayout = false;
        boolean mTrackOldChangeHolders = false;
        boolean mIsMeasuring = false;
        boolean mRunSimpleAnimations = false;
        boolean mRunPredictiveAnimations = false;

        void assertLayoutStep(int accepted) {
            if ((this.mLayoutStep & accepted) == 0) {
                throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(accepted) + " but it is " + Integer.toBinaryString(this.mLayoutStep));
            }
        }

        State reset() {
            this.mTargetPosition = -1;
            SparseArray<Object> sparseArray = this.mData;
            if (sparseArray != null) {
                sparseArray.clear();
            }
            this.mItemCount = 0;
            this.mStructureChanged = false;
            this.mIsMeasuring = false;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void prepareForNestedPrefetch(Adapter adapter) {
            this.mLayoutStep = 1;
            this.mItemCount = adapter.getItemCount();
            this.mInPreLayout = false;
            this.mTrackOldChangeHolders = false;
            this.mIsMeasuring = false;
        }

        public boolean isMeasuring() {
            return this.mIsMeasuring;
        }

        public boolean isPreLayout() {
            return this.mInPreLayout;
        }

        public boolean willRunPredictiveAnimations() {
            return this.mRunPredictiveAnimations;
        }

        public boolean willRunSimpleAnimations() {
            return this.mRunSimpleAnimations;
        }

        public void remove(int resourceId) {
            SparseArray<Object> sparseArray = this.mData;
            if (sparseArray == null) {
                return;
            }
            sparseArray.remove(resourceId);
        }

        public <T> T get(int resourceId) {
            SparseArray<Object> sparseArray = this.mData;
            if (sparseArray == null) {
                return null;
            }
            return (T) sparseArray.get(resourceId);
        }

        public void put(int resourceId, Object data) {
            if (this.mData == null) {
                this.mData = new SparseArray<>();
            }
            this.mData.put(resourceId, data);
        }

        public int getTargetScrollPosition() {
            return this.mTargetPosition;
        }

        public boolean hasTargetScrollPosition() {
            return this.mTargetPosition != -1;
        }

        public boolean didStructureChange() {
            return this.mStructureChanged;
        }

        public int getItemCount() {
            return this.mInPreLayout ? this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout : this.mItemCount;
        }

        public int getRemainingScrollHorizontal() {
            return this.mRemainingScrollHorizontal;
        }

        public int getRemainingScrollVertical() {
            return this.mRemainingScrollVertical;
        }

        public String toString() {
            return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mIsMeasuring=" + this.mIsMeasuring + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
        }
    }

    /* loaded from: classes.dex */
    private class ItemAnimatorRestoreListener implements ItemAnimator.ItemAnimatorListener {
        ItemAnimatorRestoreListener() {
        }

        @Override // android.support.v7.widget.RecyclerView.ItemAnimator.ItemAnimatorListener
        public void onAnimationFinished(ViewHolder item) {
            item.setIsRecyclable(true);
            if (item.mShadowedHolder != null && item.mShadowingHolder == null) {
                item.mShadowedHolder = null;
            }
            item.mShadowingHolder = null;
            if (!item.shouldBeKeptAsChild() && !RecyclerView.this.removeAnimatingView(item.itemView) && item.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(item.itemView, false);
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ItemAnimator {
        public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        public static final int FLAG_CHANGED = 2;
        public static final int FLAG_INVALIDATED = 4;
        public static final int FLAG_MOVED = 2048;
        public static final int FLAG_REMOVED = 8;
        private ItemAnimatorListener mListener = null;
        private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList<>();
        private long mAddDuration = 120;
        private long mRemoveDuration = 120;
        private long mMoveDuration = 250;
        private long mChangeDuration = 250;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface AdapterChanges {
        }

        /* loaded from: classes.dex */
        public interface ItemAnimatorFinishedListener {
            void onAnimationsFinished();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public interface ItemAnimatorListener {
            void onAnimationFinished(@NonNull ViewHolder viewHolder);
        }

        public abstract boolean animateAppearance(@NonNull ViewHolder viewHolder, @Nullable ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateChange(@NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder2, @NonNull ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateDisappearance(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @Nullable ItemHolderInfo itemHolderInfo2);

        public abstract boolean animatePersistence(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public abstract void endAnimation(@NonNull ViewHolder viewHolder);

        public abstract void endAnimations();

        public abstract boolean isRunning();

        public abstract void runPendingAnimations();

        public long getMoveDuration() {
            return this.mMoveDuration;
        }

        public void setMoveDuration(long moveDuration) {
            this.mMoveDuration = moveDuration;
        }

        public long getAddDuration() {
            return this.mAddDuration;
        }

        public void setAddDuration(long addDuration) {
            this.mAddDuration = addDuration;
        }

        public long getRemoveDuration() {
            return this.mRemoveDuration;
        }

        public void setRemoveDuration(long removeDuration) {
            this.mRemoveDuration = removeDuration;
        }

        public long getChangeDuration() {
            return this.mChangeDuration;
        }

        public void setChangeDuration(long changeDuration) {
            this.mChangeDuration = changeDuration;
        }

        void setListener(ItemAnimatorListener listener) {
            this.mListener = listener;
        }

        @NonNull
        public ItemHolderInfo recordPreLayoutInformation(@NonNull State state, @NonNull ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        @NonNull
        public ItemHolderInfo recordPostLayoutInformation(@NonNull State state, @NonNull ViewHolder viewHolder) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        static int buildAdapterChangeFlagsForAnimations(ViewHolder viewHolder) {
            int flags = viewHolder.mFlags & 14;
            if (viewHolder.isInvalid()) {
                return 4;
            }
            if ((flags & 4) == 0) {
                int oldPos = viewHolder.getOldPosition();
                int pos = viewHolder.getAdapterPosition();
                if (oldPos != -1 && pos != -1 && oldPos != pos) {
                    return flags | 2048;
                }
                return flags;
            }
            return flags;
        }

        public final void dispatchAnimationFinished(@NonNull ViewHolder viewHolder) {
            onAnimationFinished(viewHolder);
            ItemAnimatorListener itemAnimatorListener = this.mListener;
            if (itemAnimatorListener != null) {
                itemAnimatorListener.onAnimationFinished(viewHolder);
            }
        }

        public void onAnimationFinished(@NonNull ViewHolder viewHolder) {
        }

        public final void dispatchAnimationStarted(@NonNull ViewHolder viewHolder) {
            onAnimationStarted(viewHolder);
        }

        public void onAnimationStarted(@NonNull ViewHolder viewHolder) {
        }

        public final boolean isRunning(@Nullable ItemAnimatorFinishedListener listener) {
            boolean running = isRunning();
            if (listener != null) {
                if (!running) {
                    listener.onAnimationsFinished();
                } else {
                    this.mFinishedListeners.add(listener);
                }
            }
            return running;
        }

        public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder viewHolder) {
            return true;
        }

        public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder viewHolder, @NonNull List<Object> payloads) {
            return canReuseUpdatedViewHolder(viewHolder);
        }

        public final void dispatchAnimationsFinished() {
            int count = this.mFinishedListeners.size();
            for (int i = 0; i < count; i++) {
                this.mFinishedListeners.get(i).onAnimationsFinished();
            }
            this.mFinishedListeners.clear();
        }

        @NonNull
        public ItemHolderInfo obtainHolderInfo() {
            return new ItemHolderInfo();
        }

        /* loaded from: classes.dex */
        public static class ItemHolderInfo {
            public int bottom;
            public int changeFlags;
            public int left;
            public int right;
            public int top;

            @NonNull
            public ItemHolderInfo setFrom(@NonNull ViewHolder holder) {
                return setFrom(holder, 0);
            }

            @NonNull
            public ItemHolderInfo setFrom(@NonNull ViewHolder holder, int flags) {
                View view = holder.itemView;
                this.left = view.getLeft();
                this.top = view.getTop();
                this.right = view.getRight();
                this.bottom = view.getBottom();
                return this;
            }
        }
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int childCount, int i) {
        ChildDrawingOrderCallback childDrawingOrderCallback = this.mChildDrawingOrderCallback;
        if (childDrawingOrderCallback == null) {
            return super.getChildDrawingOrder(childCount, i);
        }
        return childDrawingOrderCallback.onGetChildDrawingOrder(childCount, i);
    }

    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (this.mScrollingChildHelper == null) {
            this.mScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return this.mScrollingChildHelper;
    }
}
