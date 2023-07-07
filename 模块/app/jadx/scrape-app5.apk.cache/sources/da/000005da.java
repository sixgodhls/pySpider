package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.p003v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ScrollView;
import com.afollestad.materialdialogs.C0582R;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.StackingBehavior;
import com.afollestad.materialdialogs.util.DialogUtils;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;

/* loaded from: classes.dex */
public class MDRootLayout extends ViewGroup {
    private static final int INDEX_NEGATIVE = 1;
    private static final int INDEX_NEUTRAL = 0;
    private static final int INDEX_POSITIVE = 2;
    private ViewTreeObserver.OnScrollChangedListener bottomOnScrollChangedListener;
    private int buttonBarHeight;
    private GravityEnum buttonGravity;
    private int buttonHorizontalEdgeMargin;
    private int buttonPaddingFull;
    private final MDButton[] buttons;
    private View content;
    private Paint dividerPaint;
    private int dividerWidth;
    private boolean drawBottomDivider;
    private boolean drawTopDivider;
    private boolean isStacked;
    private int maxHeight;
    private boolean noTitleNoPadding;
    private int noTitlePaddingFull;
    private boolean reducePaddingNoTitleNoButtons;
    private StackingBehavior stackBehavior;
    private View titleBar;
    private ViewTreeObserver.OnScrollChangedListener topOnScrollChangedListener;
    private boolean useFullPadding;

    public MDRootLayout(Context context) {
        super(context);
        this.buttons = new MDButton[3];
        this.drawTopDivider = false;
        this.drawBottomDivider = false;
        this.stackBehavior = StackingBehavior.ADAPTIVE;
        this.isStacked = false;
        this.useFullPadding = true;
        this.buttonGravity = GravityEnum.START;
        init(context, null, 0);
    }

    public MDRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.buttons = new MDButton[3];
        this.drawTopDivider = false;
        this.drawBottomDivider = false;
        this.stackBehavior = StackingBehavior.ADAPTIVE;
        this.isStacked = false;
        this.useFullPadding = true;
        this.buttonGravity = GravityEnum.START;
        init(context, attrs, 0);
    }

    @TargetApi(11)
    public MDRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.buttons = new MDButton[3];
        this.drawTopDivider = false;
        this.drawBottomDivider = false;
        this.stackBehavior = StackingBehavior.ADAPTIVE;
        this.isStacked = false;
        this.useFullPadding = true;
        this.buttonGravity = GravityEnum.START;
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public MDRootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.buttons = new MDButton[3];
        this.drawTopDivider = false;
        this.drawBottomDivider = false;
        this.stackBehavior = StackingBehavior.ADAPTIVE;
        this.isStacked = false;
        this.useFullPadding = true;
        this.buttonGravity = GravityEnum.START;
        init(context, attrs, defStyleAttr);
    }

    private static boolean isVisible(View v) {
        boolean visible = true;
        boolean visible2 = (v == null || v.getVisibility() == 8) ? false : true;
        if (visible2 && (v instanceof MDButton)) {
            if (((MDButton) v).getText().toString().trim().length() <= 0) {
                visible = false;
            }
            return visible;
        }
        return visible2;
    }

    public static boolean canRecyclerViewScroll(RecyclerView view) {
        return (view == null || view.getLayoutManager() == null || !view.getLayoutManager().canScrollVertically()) ? false : true;
    }

    private static boolean canScrollViewScroll(ScrollView sv) {
        if (sv.getChildCount() == 0) {
            return false;
        }
        int childHeight = sv.getChildAt(0).getMeasuredHeight();
        return (sv.getMeasuredHeight() - sv.getPaddingTop()) - sv.getPaddingBottom() < childHeight;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean canWebViewScroll(WebView view) {
        return ((float) view.getMeasuredHeight()) < ((float) view.getContentHeight()) * view.getScale();
    }

    private static boolean canAdapterViewScroll(AdapterView lv) {
        if (lv.getLastVisiblePosition() == -1) {
            return false;
        }
        boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
        boolean lastItemVisible = lv.getLastVisiblePosition() == lv.getCount() - 1;
        return !firstItemVisible || !lastItemVisible || lv.getChildCount() <= 0 || lv.getChildAt(0).getTop() < lv.getPaddingTop() || lv.getChildAt(lv.getChildCount() - 1).getBottom() > lv.getHeight() - lv.getPaddingBottom();
    }

    @Nullable
    private static View getBottomView(ViewGroup viewGroup) {
        if (viewGroup == null || viewGroup.getChildCount() == 0) {
            return null;
        }
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View child = viewGroup.getChildAt(i);
            if (child.getVisibility() == 0 && child.getBottom() == viewGroup.getMeasuredHeight()) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    private static View getTopView(ViewGroup viewGroup) {
        if (viewGroup == null || viewGroup.getChildCount() == 0) {
            return null;
        }
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View child = viewGroup.getChildAt(i);
            if (child.getVisibility() == 0 && child.getTop() == 0) {
                return child;
            }
        }
        return null;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Resources r = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, C0582R.styleable.MDRootLayout, defStyleAttr, 0);
        this.reducePaddingNoTitleNoButtons = a.getBoolean(C0582R.styleable.MDRootLayout_md_reduce_padding_no_title_no_buttons, true);
        a.recycle();
        this.noTitlePaddingFull = r.getDimensionPixelSize(C0582R.dimen.md_notitle_vertical_padding);
        this.buttonPaddingFull = r.getDimensionPixelSize(C0582R.dimen.md_button_frame_vertical_padding);
        this.buttonHorizontalEdgeMargin = r.getDimensionPixelSize(C0582R.dimen.md_button_padding_frame_side);
        this.buttonBarHeight = r.getDimensionPixelSize(C0582R.dimen.md_button_height);
        this.dividerPaint = new Paint();
        this.dividerWidth = r.getDimensionPixelSize(C0582R.dimen.md_divider_height);
        this.dividerPaint.setColor(DialogUtils.resolveColor(context, C0582R.attr.md_divider_color));
        setWillNotDraw(false);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void noTitleNoPadding() {
        this.noTitleNoPadding = true;
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getId() == C0582R.C0585id.md_titleFrame) {
                this.titleBar = v;
            } else if (v.getId() == C0582R.C0585id.md_buttonDefaultNeutral) {
                this.buttons[0] = (MDButton) v;
            } else if (v.getId() == C0582R.C0585id.md_buttonDefaultNegative) {
                this.buttons[1] = (MDButton) v;
            } else if (v.getId() == C0582R.C0585id.md_buttonDefaultPositive) {
                this.buttons[2] = (MDButton) v;
            } else {
                this.content = v;
            }
        }
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        MDButton[] mDButtonArr;
        boolean stacked;
        int fullPadding;
        MDButton[] mDButtonArr2;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (height > this.maxHeight) {
            height = this.maxHeight;
        }
        this.useFullPadding = true;
        boolean hasButtons = false;
        if (this.stackBehavior == StackingBehavior.ALWAYS) {
            stacked = true;
        } else if (this.stackBehavior == StackingBehavior.NEVER) {
            stacked = false;
        } else {
            int buttonsWidth = 0;
            boolean hasButtons2 = false;
            for (MDButton button : this.buttons) {
                if (button != null && isVisible(button)) {
                    button.setStacked(false, false);
                    measureChild(button, widthMeasureSpec, heightMeasureSpec);
                    buttonsWidth += button.getMeasuredWidth();
                    hasButtons2 = true;
                }
            }
            int buttonBarPadding = getContext().getResources().getDimensionPixelSize(C0582R.dimen.md_neutral_button_margin);
            int buttonFrameWidth = width - (buttonBarPadding * 2);
            stacked = buttonsWidth > buttonFrameWidth;
            hasButtons = hasButtons2;
        }
        int stackedHeight = 0;
        this.isStacked = stacked;
        if (stacked) {
            int stackedHeight2 = 0;
            boolean hasButtons3 = hasButtons;
            for (MDButton button2 : this.buttons) {
                if (button2 != null && isVisible(button2)) {
                    button2.setStacked(true, false);
                    measureChild(button2, widthMeasureSpec, heightMeasureSpec);
                    stackedHeight2 += button2.getMeasuredHeight();
                    hasButtons3 = true;
                }
            }
            hasButtons = hasButtons3;
            stackedHeight = stackedHeight2;
        }
        int availableHeight = height;
        int minPadding = 0;
        if (!hasButtons) {
            fullPadding = 0 + (this.buttonPaddingFull * 2);
        } else if (this.isStacked) {
            availableHeight -= stackedHeight;
            int i = this.buttonPaddingFull;
            fullPadding = 0 + (i * 2);
            minPadding = 0 + (i * 2);
        } else {
            availableHeight -= this.buttonBarHeight;
            fullPadding = 0 + (this.buttonPaddingFull * 2);
        }
        if (isVisible(this.titleBar)) {
            this.titleBar.measure(View.MeasureSpec.makeMeasureSpec(width, MemoryConstants.f216GB), 0);
            availableHeight -= this.titleBar.getMeasuredHeight();
        } else if (!this.noTitleNoPadding) {
            fullPadding += this.noTitlePaddingFull;
        }
        if (isVisible(this.content)) {
            this.content.measure(View.MeasureSpec.makeMeasureSpec(width, MemoryConstants.f216GB), View.MeasureSpec.makeMeasureSpec(availableHeight - minPadding, Integer.MIN_VALUE));
            if (this.content.getMeasuredHeight() <= availableHeight - fullPadding) {
                if (!this.reducePaddingNoTitleNoButtons || isVisible(this.titleBar) || hasButtons) {
                    this.useFullPadding = true;
                    availableHeight -= this.content.getMeasuredHeight() + fullPadding;
                } else {
                    this.useFullPadding = false;
                    availableHeight -= this.content.getMeasuredHeight() + minPadding;
                }
            } else {
                this.useFullPadding = false;
                availableHeight = 0;
            }
        }
        setMeasuredDimension(width, height - availableHeight);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        View view = this.content;
        if (view != null) {
            if (this.drawTopDivider) {
                int y = view.getTop();
                canvas.drawRect(0.0f, y - this.dividerWidth, getMeasuredWidth(), y, this.dividerPaint);
            }
            if (this.drawBottomDivider) {
                int y2 = this.content.getBottom();
                canvas.drawRect(0.0f, y2, getMeasuredWidth(), this.dividerWidth + y2, this.dividerPaint);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int bl;
        int br;
        int bl2;
        int br2;
        int br3;
        int bl3;
        MDButton[] mDButtonArr;
        int t2 = t;
        if (!isVisible(this.titleBar)) {
            if (!this.noTitleNoPadding && this.useFullPadding) {
                t2 += this.noTitlePaddingFull;
            }
        } else {
            int height = this.titleBar.getMeasuredHeight();
            this.titleBar.layout(l, t2, r, t2 + height);
            t2 += height;
        }
        if (isVisible(this.content)) {
            View view = this.content;
            view.layout(l, t2, r, view.getMeasuredHeight() + t2);
        }
        if (this.isStacked) {
            int b2 = b - this.buttonPaddingFull;
            for (MDButton mButton : this.buttons) {
                if (isVisible(mButton)) {
                    mButton.layout(l, b2 - mButton.getMeasuredHeight(), r, b2);
                    b2 -= mButton.getMeasuredHeight();
                }
            }
        } else {
            int barBottom = b;
            if (this.useFullPadding) {
                barBottom -= this.buttonPaddingFull;
            }
            int barTop = barBottom - this.buttonBarHeight;
            int offset = this.buttonHorizontalEdgeMargin;
            int neutralLeft = -1;
            int neutralRight = -1;
            if (isVisible(this.buttons[2])) {
                if (this.buttonGravity == GravityEnum.END) {
                    bl3 = l + offset;
                    br3 = this.buttons[2].getMeasuredWidth() + bl3;
                } else {
                    br3 = r - offset;
                    bl3 = br3 - this.buttons[2].getMeasuredWidth();
                    neutralRight = bl3;
                }
                this.buttons[2].layout(bl3, barTop, br3, barBottom);
                offset += this.buttons[2].getMeasuredWidth();
            }
            if (isVisible(this.buttons[1])) {
                if (this.buttonGravity == GravityEnum.END) {
                    bl2 = l + offset;
                    br2 = this.buttons[1].getMeasuredWidth() + bl2;
                } else if (this.buttonGravity == GravityEnum.START) {
                    br2 = r - offset;
                    bl2 = br2 - this.buttons[1].getMeasuredWidth();
                } else {
                    bl2 = this.buttonHorizontalEdgeMargin + l;
                    br2 = this.buttons[1].getMeasuredWidth() + bl2;
                    neutralLeft = br2;
                }
                this.buttons[1].layout(bl2, barTop, br2, barBottom);
            }
            if (isVisible(this.buttons[0])) {
                if (this.buttonGravity == GravityEnum.END) {
                    br = r - this.buttonHorizontalEdgeMargin;
                    bl = br - this.buttons[0].getMeasuredWidth();
                } else if (this.buttonGravity == GravityEnum.START) {
                    bl = l + this.buttonHorizontalEdgeMargin;
                    br = this.buttons[0].getMeasuredWidth() + bl;
                } else {
                    if (neutralLeft == -1 && neutralRight != -1) {
                        neutralLeft = neutralRight - this.buttons[0].getMeasuredWidth();
                    } else if (neutralRight == -1 && neutralLeft != -1) {
                        neutralRight = this.buttons[0].getMeasuredWidth() + neutralLeft;
                    } else if (neutralRight == -1) {
                        int neutralLeft2 = ((r - l) / 2) - (this.buttons[0].getMeasuredWidth() / 2);
                        neutralRight = this.buttons[0].getMeasuredWidth() + neutralLeft2;
                        neutralLeft = neutralLeft2;
                    }
                    bl = neutralLeft;
                    br = neutralRight;
                }
                this.buttons[0].layout(bl, barTop, br, barBottom);
            }
        }
        setUpDividersVisibility(this.content, true, true);
    }

    public void setStackingBehavior(StackingBehavior behavior) {
        this.stackBehavior = behavior;
        invalidate();
    }

    public void setDividerColor(int color) {
        this.dividerPaint.setColor(color);
        invalidate();
    }

    public void setButtonGravity(GravityEnum gravity) {
        this.buttonGravity = gravity;
        invertGravityIfNecessary();
    }

    private void invertGravityIfNecessary() {
        if (Build.VERSION.SDK_INT < 17) {
            return;
        }
        Configuration config = getResources().getConfiguration();
        if (config.getLayoutDirection() == 1) {
            switch (this.buttonGravity) {
                case START:
                    this.buttonGravity = GravityEnum.END;
                    return;
                case END:
                    this.buttonGravity = GravityEnum.START;
                    return;
                default:
                    return;
            }
        }
    }

    public void setButtonStackedGravity(GravityEnum gravity) {
        MDButton[] mDButtonArr;
        for (MDButton mButton : this.buttons) {
            if (mButton != null) {
                mButton.setStackedGravity(gravity);
            }
        }
    }

    private void setUpDividersVisibility(final View view, final boolean setForTop, final boolean setForBottom) {
        if (view == null) {
            return;
        }
        if (view instanceof ScrollView) {
            ScrollView sv = (ScrollView) view;
            if (canScrollViewScroll(sv)) {
                addScrollListener(sv, setForTop, setForBottom);
                return;
            }
            if (setForTop) {
                this.drawTopDivider = false;
            }
            if (setForBottom) {
                this.drawBottomDivider = false;
            }
        } else if (view instanceof AdapterView) {
            AdapterView sv2 = (AdapterView) view;
            if (canAdapterViewScroll(sv2)) {
                addScrollListener(sv2, setForTop, setForBottom);
                return;
            }
            if (setForTop) {
                this.drawTopDivider = false;
            }
            if (setForBottom) {
                this.drawBottomDivider = false;
            }
        } else if (view instanceof WebView) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.afollestad.materialdialogs.internal.MDRootLayout.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    if (view.getMeasuredHeight() != 0) {
                        if (MDRootLayout.canWebViewScroll((WebView) view)) {
                            MDRootLayout.this.addScrollListener((ViewGroup) view, setForTop, setForBottom);
                        } else {
                            if (setForTop) {
                                MDRootLayout.this.drawTopDivider = false;
                            }
                            if (setForBottom) {
                                MDRootLayout.this.drawBottomDivider = false;
                            }
                        }
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                    return true;
                }
            });
        } else if (view instanceof RecyclerView) {
            boolean canScroll = canRecyclerViewScroll((RecyclerView) view);
            if (setForTop) {
                this.drawTopDivider = canScroll;
            }
            if (setForBottom) {
                this.drawBottomDivider = canScroll;
            }
            if (canScroll) {
                addScrollListener((ViewGroup) view, setForTop, setForBottom);
            }
        } else if (view instanceof ViewGroup) {
            View topView = getTopView((ViewGroup) view);
            setUpDividersVisibility(topView, setForTop, setForBottom);
            View bottomView = getBottomView((ViewGroup) view);
            if (bottomView != topView) {
                setUpDividersVisibility(bottomView, false, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addScrollListener(final ViewGroup vg, final boolean setForTop, final boolean setForBottom) {
        if ((!setForBottom && this.topOnScrollChangedListener == null) || (setForBottom && this.bottomOnScrollChangedListener == null)) {
            if (vg instanceof RecyclerView) {
                RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() { // from class: com.afollestad.materialdialogs.internal.MDRootLayout.2
                    @Override // android.support.p003v7.widget.RecyclerView.OnScrollListener
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        boolean hasButtons = false;
                        MDButton[] mDButtonArr = MDRootLayout.this.buttons;
                        int length = mDButtonArr.length;
                        int i = 0;
                        while (true) {
                            if (i < length) {
                                MDButton button = mDButtonArr[i];
                                if (button == null || button.getVisibility() == 8) {
                                    i++;
                                } else {
                                    hasButtons = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        MDRootLayout.this.invalidateDividersForScrollingView(vg, setForTop, setForBottom, hasButtons);
                        MDRootLayout.this.invalidate();
                    }
                };
                ((RecyclerView) vg).addOnScrollListener(scrollListener);
                scrollListener.onScrolled((RecyclerView) vg, 0, 0);
                return;
            }
            ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: com.afollestad.materialdialogs.internal.MDRootLayout.3
                @Override // android.view.ViewTreeObserver.OnScrollChangedListener
                public void onScrollChanged() {
                    boolean hasButtons = false;
                    MDButton[] mDButtonArr = MDRootLayout.this.buttons;
                    int length = mDButtonArr.length;
                    int i = 0;
                    while (true) {
                        if (i < length) {
                            MDButton button = mDButtonArr[i];
                            if (button == null || button.getVisibility() == 8) {
                                i++;
                            } else {
                                hasButtons = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    ViewGroup viewGroup = vg;
                    if (viewGroup instanceof WebView) {
                        MDRootLayout.this.invalidateDividersForWebView((WebView) viewGroup, setForTop, setForBottom, hasButtons);
                    } else {
                        MDRootLayout.this.invalidateDividersForScrollingView(viewGroup, setForTop, setForBottom, hasButtons);
                    }
                    MDRootLayout.this.invalidate();
                }
            };
            if (!setForBottom) {
                this.topOnScrollChangedListener = onScrollChangedListener;
                vg.getViewTreeObserver().addOnScrollChangedListener(this.topOnScrollChangedListener);
            } else {
                this.bottomOnScrollChangedListener = onScrollChangedListener;
                vg.getViewTreeObserver().addOnScrollChangedListener(this.bottomOnScrollChangedListener);
            }
            onScrollChangedListener.onScrollChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateDividersForScrollingView(ViewGroup view, boolean setForTop, boolean setForBottom, boolean hasButtons) {
        boolean z = true;
        if (setForTop && view.getChildCount() > 0) {
            View view2 = this.titleBar;
            this.drawTopDivider = (view2 == null || view2.getVisibility() == 8 || view.getScrollY() + view.getPaddingTop() <= view.getChildAt(0).getTop()) ? false : true;
        }
        if (setForBottom && view.getChildCount() > 0) {
            if (!hasButtons || (view.getScrollY() + view.getHeight()) - view.getPaddingBottom() >= view.getChildAt(view.getChildCount() - 1).getBottom()) {
                z = false;
            }
            this.drawBottomDivider = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateDividersForWebView(WebView view, boolean setForTop, boolean setForBottom, boolean hasButtons) {
        boolean z = true;
        if (setForTop) {
            View view2 = this.titleBar;
            this.drawTopDivider = (view2 == null || view2.getVisibility() == 8 || view.getScrollY() + view.getPaddingTop() <= 0) ? false : true;
        }
        if (setForBottom) {
            if (!hasButtons || (view.getScrollY() + view.getMeasuredHeight()) - view.getPaddingBottom() >= view.getContentHeight() * view.getScale()) {
                z = false;
            }
            this.drawBottomDivider = z;
        }
    }
}