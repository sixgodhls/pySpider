package me.majiajie.pagerbottomtabstrip.internal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.p000v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;
import me.majiajie.pagerbottomtabstrip.C1028R;
import me.majiajie.pagerbottomtabstrip.ItemController;
import me.majiajie.pagerbottomtabstrip.item.MaterialItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/* loaded from: classes.dex */
public class MaterialItemLayout extends ViewGroup implements ItemController {
    private final int ANIM_TIME;
    private final int DEFAULT_SELECTED;
    private final int MATERIAL_BOTTOM_NAVIGATION_ACTIVE_ITEM_MAX_WIDTH;
    private final int MATERIAL_BOTTOM_NAVIGATION_ITEM_HEIGHT;
    private final int MATERIAL_BOTTOM_NAVIGATION_ITEM_MAX_WIDTH;
    private final int MATERIAL_BOTTOM_NAVIGATION_ITEM_MIN_WIDTH;
    private boolean mChangeBackgroundMode;
    private List<Integer> mColors;
    private boolean mHideTitle;
    private Interpolator mInterpolator;
    private int mItemTotalWidth;
    private List<MaterialItemView> mItems;
    private float mLastUpX;
    private float mLastUpY;
    private List<OnTabItemSelectedListener> mListeners;
    private int mOldSelected;
    private List<Oval> mOvals;
    private Paint mPaint;
    private int mSelected;
    private int[] mTempChildWidths;
    private RectF mTempRectF;

    public MaterialItemLayout(Context context) {
        this(context, null);
    }

    public MaterialItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.DEFAULT_SELECTED = 0;
        this.mListeners = new ArrayList();
        this.mSelected = -1;
        this.mOldSelected = -1;
        this.ANIM_TIME = 300;
        Resources res = getResources();
        this.MATERIAL_BOTTOM_NAVIGATION_ACTIVE_ITEM_MAX_WIDTH = res.getDimensionPixelSize(C1028R.dimen.material_bottom_navigation_active_item_max_width);
        this.MATERIAL_BOTTOM_NAVIGATION_ITEM_MAX_WIDTH = res.getDimensionPixelSize(C1028R.dimen.material_bottom_navigation_item_max_width);
        this.MATERIAL_BOTTOM_NAVIGATION_ITEM_MIN_WIDTH = res.getDimensionPixelSize(C1028R.dimen.material_bottom_navigation_item_min_width);
        this.MATERIAL_BOTTOM_NAVIGATION_ITEM_HEIGHT = res.getDimensionPixelSize(C1028R.dimen.material_bottom_navigation_height);
        this.mTempChildWidths = new int[5];
    }

    public void initialize(List<MaterialItemView> items, List<Integer> checkedColors, int mode) {
        this.mItems = items;
        if ((mode & 2) > 0) {
            this.mChangeBackgroundMode = true;
            this.mOvals = new ArrayList();
            this.mColors = checkedColors;
            this.mInterpolator = new AccelerateDecelerateInterpolator();
            this.mTempRectF = new RectF();
            this.mPaint = new Paint();
            setBackgroundColor(this.mColors.get(0).intValue());
        } else {
            for (int i = 0; i < this.mItems.size(); i++) {
                MaterialItemView v = this.mItems.get(i);
                if (Build.VERSION.SDK_INT >= 21) {
                    v.setBackground(new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{(16777215 & checkedColors.get(i).intValue()) | 1442840576}), null, null));
                } else {
                    v.setBackgroundResource(C1028R.C1029drawable.material_item_background);
                }
            }
        }
        int i2 = mode & 1;
        if (i2 > 0) {
            this.mHideTitle = true;
            for (MaterialItemView v2 : this.mItems) {
                v2.setHideTitle(true);
            }
        }
        int n = this.mItems.size();
        for (int i3 = 0; i3 < n; i3++) {
            MaterialItemView v3 = this.mItems.get(i3);
            v3.setChecked(false);
            addView(v3);
            final int finali = i3;
            v3.setOnClickListener(new View.OnClickListener() { // from class: me.majiajie.pagerbottomtabstrip.internal.MaterialItemLayout.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v4) {
                    MaterialItemLayout materialItemLayout = MaterialItemLayout.this;
                    materialItemLayout.setSelect(finali, materialItemLayout.mLastUpX, MaterialItemLayout.this.mLastUpY);
                }
            });
        }
        this.mSelected = 0;
        this.mItems.get(0).setChecked(true);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        List<MaterialItemView> list = this.mItems;
        if (list == null || list.size() <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int count = getChildCount();
        int heightSpec = View.MeasureSpec.makeMeasureSpec(this.MATERIAL_BOTTOM_NAVIGATION_ITEM_HEIGHT, MemoryConstants.f216GB);
        if (this.mHideTitle) {
            int inactiveCount = count - 1;
            int activeMaxAvailable = width - (this.MATERIAL_BOTTOM_NAVIGATION_ITEM_MIN_WIDTH * inactiveCount);
            int activeWidth = Math.min(activeMaxAvailable, this.MATERIAL_BOTTOM_NAVIGATION_ACTIVE_ITEM_MAX_WIDTH);
            int inactiveMaxAvailable = (width - activeWidth) / inactiveCount;
            int inactiveWidth = Math.min(inactiveMaxAvailable, this.MATERIAL_BOTTOM_NAVIGATION_ITEM_MAX_WIDTH);
            for (int i = 0; i < count; i++) {
                int i2 = this.mSelected;
                if (i == i2) {
                    this.mTempChildWidths[i] = (int) (((activeWidth - inactiveWidth) * this.mItems.get(i2).getAnimValue()) + inactiveWidth);
                } else if (i == this.mOldSelected) {
                    this.mTempChildWidths[i] = (int) (activeWidth - ((activeWidth - inactiveWidth) * this.mItems.get(i2).getAnimValue()));
                } else {
                    this.mTempChildWidths[i] = inactiveWidth;
                }
            }
        } else {
            int maxAvailable = width / (count == 0 ? 1 : count);
            int childWidth = Math.min(maxAvailable, this.MATERIAL_BOTTOM_NAVIGATION_ACTIVE_ITEM_MAX_WIDTH);
            for (int i3 = 0; i3 < count; i3++) {
                this.mTempChildWidths[i3] = childWidth;
            }
        }
        this.mItemTotalWidth = 0;
        for (int i4 = 0; i4 < count; i4++) {
            View child = getChildAt(i4);
            if (child.getVisibility() != 8) {
                child.measure(View.MeasureSpec.makeMeasureSpec(this.mTempChildWidths[i4], MemoryConstants.f216GB), heightSpec);
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.width = child.getMeasuredWidth();
                this.mItemTotalWidth += child.getMeasuredWidth();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        int width = right - left;
        int height = bottom - top;
        int padding_top = getPaddingTop();
        int padding_bottom = getPaddingBottom();
        int used = 0;
        int i = this.mItemTotalWidth;
        if (i > 0 && i < width) {
            used = (width - i) / 2;
        }
        for (int i2 = 0; i2 < count; i2++) {
            View child = getChildAt(i2);
            if (child.getVisibility() != 8) {
                if (ViewCompat.getLayoutDirection(this) == 1) {
                    child.layout((width - used) - child.getMeasuredWidth(), padding_top, width - used, height - padding_bottom);
                } else {
                    child.layout(used, padding_top, child.getMeasuredWidth() + used, height - padding_bottom);
                }
                used += child.getMeasuredWidth();
            }
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mChangeBackgroundMode) {
            int width = getWidth();
            int height = getHeight();
            Iterator<Oval> iterator = this.mOvals.iterator();
            while (iterator.hasNext()) {
                Oval oval = iterator.next();
                this.mPaint.setColor(oval.color);
                if (oval.f219r < oval.maxR) {
                    this.mTempRectF.set(oval.getLeft(), oval.getTop(), oval.getRight(), oval.getBottom());
                    canvas.drawOval(this.mTempRectF, this.mPaint);
                } else {
                    setBackgroundColor(oval.color);
                    canvas.drawRect(0.0f, 0.0f, width, height, this.mPaint);
                    iterator.remove();
                }
                invalidate();
            }
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 1) {
            this.mLastUpX = ev.getX();
            this.mLastUpY = ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public void setSelect(int index) {
        if (index >= this.mItems.size() || index < 0) {
            return;
        }
        View v = this.mItems.get(index);
        setSelect(index, v.getX() + (v.getWidth() / 2.0f), v.getY() + (v.getHeight() / 2.0f));
    }

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public void setMessageNumber(int index, int number) {
        this.mItems.get(index).setMessageNumber(number);
    }

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public void setHasMessage(int index, boolean hasMessage) {
        this.mItems.get(index).setHasMessage(hasMessage);
    }

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public void addTabItemSelectedListener(OnTabItemSelectedListener listener) {
        this.mListeners.add(listener);
    }

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public int getSelected() {
        return this.mSelected;
    }

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public int getItemCount() {
        return this.mItems.size();
    }

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public String getItemTitle(int index) {
        return this.mItems.get(index).getTitle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSelect(int index, float x, float y) {
        int i = this.mSelected;
        if (index == i) {
            for (OnTabItemSelectedListener listener : this.mListeners) {
                listener.onRepeat(this.mSelected);
            }
            return;
        }
        this.mOldSelected = i;
        this.mSelected = index;
        if (this.mChangeBackgroundMode) {
            addOvalColor(this.mColors.get(this.mSelected).intValue(), x, y);
        }
        int i2 = this.mOldSelected;
        if (i2 >= 0) {
            this.mItems.get(i2).setChecked(false);
        }
        this.mItems.get(this.mSelected).setChecked(true);
        for (OnTabItemSelectedListener listener2 : this.mListeners) {
            listener2.onSelected(this.mSelected, this.mOldSelected);
        }
    }

    private void addOvalColor(int color, float x, float y) {
        final Oval oval = new Oval(color, 2.0f, x, y);
        oval.maxR = getR(x, y);
        this.mOvals.add(oval);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(oval.f219r, oval.maxR);
        valueAnimator.setInterpolator(this.mInterpolator);
        valueAnimator.setDuration(300L);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: me.majiajie.pagerbottomtabstrip.internal.MaterialItemLayout.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                oval.f219r = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: me.majiajie.pagerbottomtabstrip.internal.MaterialItemLayout.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                MaterialItemLayout.this.invalidate();
            }
        });
        valueAnimator.start();
    }

    private float getR(float x, float y) {
        int width = getWidth();
        int height = getHeight();
        double r1_square = (x * x) + (y * y);
        double r2_square = ((width - x) * (width - x)) + (y * y);
        double r3_square = ((width - x) * (width - x)) + ((height - y) * (height - y));
        double r4_square = (x * x) + ((height - y) * (height - y));
        return (float) Math.sqrt(Math.max(Math.max(r1_square, r2_square), Math.max(r3_square, r4_square)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Oval {
        int color;
        float maxR;

        /* renamed from: r */
        float f219r;

        /* renamed from: x */
        float f220x;

        /* renamed from: y */
        float f221y;

        Oval(int color, float r, float x, float y) {
            this.color = color;
            this.f219r = r;
            this.f220x = x;
            this.f221y = y;
        }

        float getLeft() {
            return this.f220x - this.f219r;
        }

        float getTop() {
            return this.f221y - this.f219r;
        }

        float getRight() {
            return this.f220x + this.f219r;
        }

        float getBottom() {
            return this.f221y + this.f219r;
        }
    }
}