package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.design.R;
import android.support.design.snackbar.ContentViewCallback;
import android.support.design.widget.BaseTransientBottomBar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;

/* loaded from: classes.dex */
public final class Snackbar extends BaseTransientBottomBar<Snackbar> {
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    private static final int[] SNACKBAR_BUTTON_STYLE_ATTR = {R.attr.snackbarButtonStyle};
    private final AccessibilityManager accessibilityManager;
    @Nullable
    private BaseTransientBottomBar.BaseCallback<Snackbar> callback;
    private boolean hasAction;

    @IntRange(from = 1)
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public @interface Duration {
    }

    /* loaded from: classes.dex */
    public static class Callback extends BaseTransientBottomBar.BaseCallback<Snackbar> {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;

        @Override // android.support.design.widget.BaseTransientBottomBar.BaseCallback
        public void onShown(Snackbar sb) {
        }

        @Override // android.support.design.widget.BaseTransientBottomBar.BaseCallback
        public void onDismissed(Snackbar transientBottomBar, int event) {
        }
    }

    private Snackbar(ViewGroup parent, View content, ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
        this.accessibilityManager = (AccessibilityManager) parent.getContext().getSystemService("accessibility");
    }

    @Override // android.support.design.widget.BaseTransientBottomBar
    public void show() {
        super.show();
    }

    @Override // android.support.design.widget.BaseTransientBottomBar
    public void dismiss() {
        super.dismiss();
    }

    @Override // android.support.design.widget.BaseTransientBottomBar
    public boolean isShown() {
        return super.isShown();
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @NonNull CharSequence text, int duration) {
        ViewGroup parent = findSuitableParent(view);
        if (parent == null) {
            throw new IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.");
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SnackbarContentLayout content = (SnackbarContentLayout) inflater.inflate(hasSnackbarButtonStyleAttr(parent.getContext()) ? R.layout.mtrl_layout_snackbar_include : R.layout.design_layout_snackbar_include, parent, false);
        Snackbar snackbar = new Snackbar(parent, content, content);
        snackbar.setText(text);
        snackbar.setDuration(duration);
        return snackbar;
    }

    protected static boolean hasSnackbarButtonStyleAttr(Context context) {
        TypedArray a = context.obtainStyledAttributes(SNACKBAR_BUTTON_STYLE_ATTR);
        int snackbarButtonStyleResId = a.getResourceId(0, -1);
        a.recycle();
        return snackbarButtonStyleResId != -1;
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int resId, int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        while (!(view instanceof CoordinatorLayout)) {
            if (view instanceof FrameLayout) {
                if (view.getId() == 16908290) {
                    return (ViewGroup) view;
                }
                fallback = (ViewGroup) view;
            }
            if (view != null) {
                ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
                continue;
            }
            if (view == null) {
                return fallback;
            }
        }
        return (ViewGroup) view;
    }

    @NonNull
    public Snackbar setText(@NonNull CharSequence message) {
        SnackbarContentLayout contentLayout = (SnackbarContentLayout) this.view.getChildAt(0);
        TextView tv = contentLayout.getMessageView();
        tv.setText(message);
        return this;
    }

    @NonNull
    public Snackbar setText(@StringRes int resId) {
        return setText(getContext().getText(resId));
    }

    @NonNull
    public Snackbar setAction(@StringRes int resId, View.OnClickListener listener) {
        return setAction(getContext().getText(resId), listener);
    }

    @NonNull
    public Snackbar setAction(CharSequence text, final View.OnClickListener listener) {
        SnackbarContentLayout contentLayout = (SnackbarContentLayout) this.view.getChildAt(0);
        TextView tv = contentLayout.getActionView();
        if (TextUtils.isEmpty(text) || listener == null) {
            tv.setVisibility(8);
            tv.setOnClickListener(null);
            this.hasAction = false;
        } else {
            this.hasAction = true;
            tv.setVisibility(0);
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() { // from class: android.support.design.widget.Snackbar.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    listener.onClick(view);
                    Snackbar.this.dispatchDismiss(1);
                }
            });
        }
        return this;
    }

    @Override // android.support.design.widget.BaseTransientBottomBar
    public int getDuration() {
        if (!this.hasAction || !this.accessibilityManager.isTouchExplorationEnabled()) {
            return super.getDuration();
        }
        return -2;
    }

    @NonNull
    public Snackbar setActionTextColor(ColorStateList colors) {
        SnackbarContentLayout contentLayout = (SnackbarContentLayout) this.view.getChildAt(0);
        TextView tv = contentLayout.getActionView();
        tv.setTextColor(colors);
        return this;
    }

    @NonNull
    public Snackbar setActionTextColor(@ColorInt int color) {
        SnackbarContentLayout contentLayout = (SnackbarContentLayout) this.view.getChildAt(0);
        TextView tv = contentLayout.getActionView();
        tv.setTextColor(color);
        return this;
    }

    @NonNull
    @Deprecated
    public Snackbar setCallback(Callback callback) {
        BaseTransientBottomBar.BaseCallback<Snackbar> baseCallback = this.callback;
        if (baseCallback != null) {
            removeCallback(baseCallback);
        }
        if (callback != null) {
            addCallback(callback);
        }
        this.callback = callback;
        return this;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public static final class SnackbarLayout extends BaseTransientBottomBar.SnackbarBaseLayout {
        public SnackbarLayout(Context context) {
            super(context);
        }

        public SnackbarLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int childCount = getChildCount();
            int availableWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getLayoutParams().width == -1) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, MemoryConstants.GB), View.MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MemoryConstants.GB));
                }
            }
        }
    }
}
