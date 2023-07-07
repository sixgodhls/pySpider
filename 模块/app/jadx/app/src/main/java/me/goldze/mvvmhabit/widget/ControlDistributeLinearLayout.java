package me.goldze.mvvmhabit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import me.goldze.mvvmhabit.R;

/* loaded from: classes.dex */
public class ControlDistributeLinearLayout extends LinearLayout {
    private boolean isDistributeEvent;

    public ControlDistributeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isDistributeEvent = false;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ControlDistributeLinearLayout);
        this.isDistributeEvent = typedArray.getBoolean(R.styleable.ControlDistributeLinearLayout_distribute_event, false);
    }

    public ControlDistributeLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlDistributeLinearLayout(Context context) {
        this(context, null);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isDistributeEvent();
    }

    public boolean isDistributeEvent() {
        return this.isDistributeEvent;
    }

    public void setDistributeEvent(boolean distributeEvent) {
        this.isDistributeEvent = distributeEvent;
    }
}
