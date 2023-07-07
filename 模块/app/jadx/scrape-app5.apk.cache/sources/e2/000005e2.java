package me.majiajie.pagerbottomtabstrip.internal;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.p000v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;
import me.majiajie.pagerbottomtabstrip.ItemController;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/* loaded from: classes.dex */
public class CustomItemLayout extends ViewGroup implements ItemController {
    private List<BaseTabItem> mItems;
    private List<OnTabItemSelectedListener> mListeners;
    private int mSelected;

    public CustomItemLayout(Context context) {
        this(context, null);
    }

    public CustomItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mListeners = new ArrayList();
        this.mSelected = -1;
        setLayoutTransition(new LayoutTransition());
    }

    public void initialize(List<BaseTabItem> items) {
        this.mItems = items;
        int n = this.mItems.size();
        for (int i = 0; i < n; i++) {
            BaseTabItem v = this.mItems.get(i);
            v.setChecked(false);
            addView(v);
            final int finali = i;
            v.setOnClickListener(new View.OnClickListener() { // from class: me.majiajie.pagerbottomtabstrip.internal.CustomItemLayout.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    CustomItemLayout.this.setSelect(finali);
                }
            });
        }
        this.mSelected = 0;
        this.mItems.get(0).setChecked(true);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int n = getChildCount();
        int visableChildCount = 0;
        for (int i = 0; i < n; i++) {
            if (getChildAt(i).getVisibility() != 8) {
                visableChildCount++;
            }
        }
        if (visableChildCount == 0) {
            return;
        }
        int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec) / visableChildCount, MemoryConstants.f216GB);
        int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.max(0, (View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom()) - getPaddingTop()), MemoryConstants.f216GB);
        for (int i2 = 0; i2 < n; i2++) {
            View child = getChildAt(i2);
            if (child.getVisibility() != 8) {
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
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
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
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

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public void setSelect(int index) {
        if (index == this.mSelected) {
            for (OnTabItemSelectedListener listener : this.mListeners) {
                this.mItems.get(this.mSelected).onRepeat();
                listener.onRepeat(this.mSelected);
            }
            return;
        }
        int oldSelected = this.mSelected;
        this.mSelected = index;
        if (oldSelected >= 0) {
            this.mItems.get(oldSelected).setChecked(false);
        }
        this.mItems.get(this.mSelected).setChecked(true);
        for (OnTabItemSelectedListener listener2 : this.mListeners) {
            listener2.onSelected(this.mSelected, oldSelected);
        }
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
}