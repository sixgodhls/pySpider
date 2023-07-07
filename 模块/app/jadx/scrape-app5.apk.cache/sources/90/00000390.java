package com.lcodecore.tkrefreshlayout.footer;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.lcodecore.tkrefreshlayout.C0843R;
import com.lcodecore.tkrefreshlayout.IBottomView;
import com.lcodecore.tkrefreshlayout.utils.DensityUtil;

/* loaded from: classes.dex */
public class LoadingView extends ImageView implements IBottomView {
    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int size = DensityUtil.dp2px(context, 34.0f);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = 17;
        setLayoutParams(params);
        setImageResource(C0843R.C0845drawable.anim_loading_view);
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public View getView() {
        return this;
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void onPullingUp(float fraction, float maxHeadHeight, float headHeight) {
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void startAnim(float maxHeadHeight, float headHeight) {
        ((AnimationDrawable) getDrawable()).start();
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void onFinish() {
    }

    @Override // com.lcodecore.tkrefreshlayout.IBottomView
    public void reset() {
    }
}