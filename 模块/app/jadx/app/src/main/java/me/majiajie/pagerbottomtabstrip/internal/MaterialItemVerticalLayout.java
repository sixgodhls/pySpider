package me.majiajie.pagerbottomtabstrip.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import me.goldze.mvvmhabit.utils.constant.MemoryConstants;
import me.majiajie.pagerbottomtabstrip.ItemController;
import me.majiajie.pagerbottomtabstrip.R;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/* loaded from: classes.dex */
public class MaterialItemVerticalLayout extends ViewGroup implements ItemController {
    private final int NAVIGATION_ITEM_SIZE;
    private List<BaseTabItem> mItems;
    private List<OnTabItemSelectedListener> mListeners;
    private int mSelected;

    public MaterialItemVerticalLayout(Context context) {
        this(context, null);
    }

    public MaterialItemVerticalLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialItemVerticalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mListeners = new ArrayList();
        this.mSelected = -1;
        this.NAVIGATION_ITEM_SIZE = getResources().getDimensionPixelSize(R.dimen.material_bottom_navigation_height);
    }

    public void initialize(List<BaseTabItem> items) {
        this.mItems = items;
        int n = this.mItems.size();
        for (int i = 0; i < n; i++) {
            BaseTabItem v = this.mItems.get(i);
            v.setChecked(false);
            addView(v);
            final int finali = i;
            v.setOnClickListener(new View.OnClickListener() { // from class: me.majiajie.pagerbottomtabstrip.internal.MaterialItemVerticalLayout.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    MaterialItemVerticalLayout.this.setSelect(finali);
                }
            });
        }
        this.mSelected = 0;
        this.mItems.get(0).setChecked(true);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int n = getChildCount();
        int heightSpec = View.MeasureSpec.makeMeasureSpec(this.NAVIGATION_ITEM_SIZE, MemoryConstants.GB);
        int widthSpec = View.MeasureSpec.makeMeasureSpec(this.NAVIGATION_ITEM_SIZE, MemoryConstants.GB);
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                child.measure(widthSpec, heightSpec);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        int used = getPaddingTop();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                child.layout(0, used, child.getMeasuredWidth(), child.getMeasuredHeight() + used);
                used += child.getMeasuredHeight();
            }
        }
    }

    @Override // me.majiajie.pagerbottomtabstrip.ItemController
    public void setSelect(int index) {
        if (index == this.mSelected) {
            for (OnTabItemSelectedListener listener : this.mListeners) {
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
