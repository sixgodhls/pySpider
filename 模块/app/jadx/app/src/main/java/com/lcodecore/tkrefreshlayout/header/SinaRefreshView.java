package com.lcodecore.tkrefreshlayout.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.lcodecore.tkrefreshlayout.R;

/* loaded from: classes.dex */
public class SinaRefreshView extends FrameLayout implements IHeaderView {
    private ImageView loadingView;
    private String pullDownStr;
    private ImageView refreshArrow;
    private TextView refreshTextView;
    private String refreshingStr;
    private String releaseRefreshStr;

    public SinaRefreshView(Context context) {
        this(context, null);
    }

    public SinaRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SinaRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.pullDownStr = "下拉刷新";
        this.releaseRefreshStr = "释放刷新";
        this.refreshingStr = "正在刷新";
        init();
    }

    private void init() {
        View rootView = View.inflate(getContext(), R.layout.view_sinaheader, null);
        this.refreshArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
        this.refreshTextView = (TextView) rootView.findViewById(R.id.tv);
        this.loadingView = (ImageView) rootView.findViewById(R.id.iv_loading);
        addView(rootView);
    }

    public void setArrowResource(@DrawableRes int resId) {
        this.refreshArrow.setImageResource(resId);
    }

    public void setTextColor(@ColorInt int color) {
        this.refreshTextView.setTextColor(color);
    }

    public void setPullDownStr(String pullDownStr1) {
        this.pullDownStr = pullDownStr1;
    }

    public void setReleaseRefreshStr(String releaseRefreshStr1) {
        this.releaseRefreshStr = releaseRefreshStr1;
    }

    public void setRefreshingStr(String refreshingStr1) {
        this.refreshingStr = refreshingStr1;
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public View getView() {
        return this;
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1.0f) {
            this.refreshTextView.setText(this.pullDownStr);
        }
        if (fraction > 1.0f) {
            this.refreshTextView.setText(this.releaseRefreshStr);
        }
        this.refreshArrow.setRotation(((fraction * headHeight) / maxHeadHeight) * 180.0f);
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1.0f) {
            this.refreshTextView.setText(this.pullDownStr);
            this.refreshArrow.setRotation(((fraction * headHeight) / maxHeadHeight) * 180.0f);
            if (this.refreshArrow.getVisibility() == 8) {
                this.refreshArrow.setVisibility(0);
                this.loadingView.setVisibility(8);
            }
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void startAnim(float maxHeadHeight, float headHeight) {
        this.refreshTextView.setText(this.refreshingStr);
        this.refreshArrow.setVisibility(8);
        this.loadingView.setVisibility(0);
        ((AnimationDrawable) this.loadingView.getDrawable()).start();
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void onFinish(OnAnimEndListener listener) {
        listener.onAnimEnd();
    }

    @Override // com.lcodecore.tkrefreshlayout.IHeaderView
    public void reset() {
        this.refreshArrow.setVisibility(0);
        this.loadingView.setVisibility(8);
        this.refreshTextView.setText(this.pullDownStr);
    }
}
