package me.zhanghai.android.materialprogressbar;

import android.content.Context;
import android.graphics.Canvas;

/* loaded from: classes.dex */
class HorizontalProgressBackgroundDrawable extends BaseSingleHorizontalProgressDrawable implements ShowBackgroundDrawable {
    private boolean mShow = true;

    public HorizontalProgressBackgroundDrawable(Context context) {
        super(context);
    }

    @Override // me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable
    public boolean getShowBackground() {
        return this.mShow;
    }

    @Override // me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable
    public void setShowBackground(boolean show) {
        if (this.mShow != show) {
            this.mShow = show;
            invalidateSelf();
        }
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mShow) {
            super.draw(canvas);
        }
    }
}
