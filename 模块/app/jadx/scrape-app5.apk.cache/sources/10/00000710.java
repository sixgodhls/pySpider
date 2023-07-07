package android.support.design.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.C0091R;
import android.support.design.widget.BottomSheetBehavior;
import android.support.p000v4.view.AccessibilityDelegateCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.p003v7.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

/* loaded from: classes.dex */
public class BottomSheetDialog extends AppCompatDialog {
    private BottomSheetBehavior<FrameLayout> behavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    boolean cancelable;
    private boolean canceledOnTouchOutside;
    private boolean canceledOnTouchOutsideSet;

    public BottomSheetDialog(@NonNull Context context) {
        this(context, 0);
    }

    public BottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, getThemeResId(context, theme));
        this.cancelable = true;
        this.canceledOnTouchOutside = true;
        this.bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() { // from class: android.support.design.widget.BottomSheetDialog.4
            @Override // android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == 5) {
                    BottomSheetDialog.this.cancel();
                }
            }

            @Override // android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };
        supportRequestWindowFeature(1);
    }

    protected BottomSheetDialog(@NonNull Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.cancelable = true;
        this.canceledOnTouchOutside = true;
        this.bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() { // from class: android.support.design.widget.BottomSheetDialog.4
            @Override // android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == 5) {
                    BottomSheetDialog.this.cancel();
                }
            }

            @Override // android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };
        supportRequestWindowFeature(1);
        this.cancelable = cancelable;
    }

    @Override // android.support.p003v7.app.AppCompatDialog, android.app.Dialog
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(wrapInBottomSheet(layoutResId, null, null));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.p003v7.app.AppCompatDialog, android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                window.clearFlags(67108864);
                window.addFlags(Integer.MIN_VALUE);
            }
            window.setLayout(-1, -1);
        }
    }

    @Override // android.support.p003v7.app.AppCompatDialog, android.app.Dialog
    public void setContentView(View view) {
        super.setContentView(wrapInBottomSheet(0, view, null));
    }

    @Override // android.support.p003v7.app.AppCompatDialog, android.app.Dialog
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(wrapInBottomSheet(0, view, params));
    }

    @Override // android.app.Dialog
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (this.cancelable != cancelable) {
            this.cancelable = cancelable;
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = this.behavior;
            if (bottomSheetBehavior != null) {
                bottomSheetBehavior.setHideable(cancelable);
            }
        }
    }

    @Override // android.app.Dialog
    protected void onStart() {
        super.onStart();
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = this.behavior;
        if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == 5) {
            this.behavior.setState(4);
        }
    }

    @Override // android.app.Dialog
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !this.cancelable) {
            this.cancelable = true;
        }
        this.canceledOnTouchOutside = cancel;
        this.canceledOnTouchOutsideSet = true;
    }

    private View wrapInBottomSheet(int layoutResId, View view, ViewGroup.LayoutParams params) {
        FrameLayout container = (FrameLayout) View.inflate(getContext(), C0091R.layout.design_bottom_sheet_dialog, null);
        CoordinatorLayout coordinator = (CoordinatorLayout) container.findViewById(C0091R.C0094id.coordinator);
        if (layoutResId != 0 && view == null) {
            view = getLayoutInflater().inflate(layoutResId, (ViewGroup) coordinator, false);
        }
        FrameLayout bottomSheet = (FrameLayout) coordinator.findViewById(C0091R.C0094id.design_bottom_sheet);
        this.behavior = BottomSheetBehavior.from(bottomSheet);
        this.behavior.setBottomSheetCallback(this.bottomSheetCallback);
        this.behavior.setHideable(this.cancelable);
        if (params == null) {
            bottomSheet.addView(view);
        } else {
            bottomSheet.addView(view, params);
        }
        coordinator.findViewById(C0091R.C0094id.touch_outside).setOnClickListener(new View.OnClickListener() { // from class: android.support.design.widget.BottomSheetDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (BottomSheetDialog.this.cancelable && BottomSheetDialog.this.isShowing() && BottomSheetDialog.this.shouldWindowCloseOnTouchOutside()) {
                    BottomSheetDialog.this.cancel();
                }
            }
        });
        ViewCompat.setAccessibilityDelegate(bottomSheet, new AccessibilityDelegateCompat() { // from class: android.support.design.widget.BottomSheetDialog.2
            @Override // android.support.p000v4.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                if (BottomSheetDialog.this.cancelable) {
                    info.addAction(1048576);
                    info.setDismissable(true);
                    return;
                }
                info.setDismissable(false);
            }

            @Override // android.support.p000v4.view.AccessibilityDelegateCompat
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action == 1048576 && BottomSheetDialog.this.cancelable) {
                    BottomSheetDialog.this.cancel();
                    return true;
                }
                return super.performAccessibilityAction(host, action, args);
            }
        });
        bottomSheet.setOnTouchListener(new View.OnTouchListener() { // from class: android.support.design.widget.BottomSheetDialog.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent event) {
                return true;
            }
        });
        return container;
    }

    boolean shouldWindowCloseOnTouchOutside() {
        if (!this.canceledOnTouchOutsideSet) {
            TypedArray a = getContext().obtainStyledAttributes(new int[]{16843611});
            this.canceledOnTouchOutside = a.getBoolean(0, true);
            a.recycle();
            this.canceledOnTouchOutsideSet = true;
        }
        return this.canceledOnTouchOutside;
    }

    private static int getThemeResId(Context context, int themeId) {
        if (themeId == 0) {
            TypedValue outValue = new TypedValue();
            if (context.getTheme().resolveAttribute(C0091R.attr.bottomSheetDialogTheme, outValue, true)) {
                return outValue.resourceId;
            }
            return C0091R.style.Theme_Design_Light_BottomSheetDialog;
        }
        return themeId;
    }
}