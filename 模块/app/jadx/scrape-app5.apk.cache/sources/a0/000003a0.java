package me.zhanghai.android.materialprogressbar;

import android.graphics.Canvas;
import android.graphics.Paint;

/* loaded from: classes.dex */
class SingleCircularProgressDrawable extends BaseSingleCircularProgressDrawable implements ShowBackgroundDrawable {
    private static final int LEVEL_MAX = 10000;
    private static final float START_ANGLE_MAX_DYNAMIC = 360.0f;
    private static final float START_ANGLE_MAX_NORMAL = 0.0f;
    private static final float SWEEP_ANGLE_MAX = 360.0f;
    private boolean mShowBackground;
    private final float mStartAngleMax;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SingleCircularProgressDrawable(int style) {
        switch (style) {
            case 0:
                this.mStartAngleMax = 0.0f;
                return;
            case 1:
                this.mStartAngleMax = 360.0f;
                return;
            default:
                throw new IllegalArgumentException("Invalid value for style");
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override // me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable
    public boolean getShowBackground() {
        return this.mShowBackground;
    }

    @Override // me.zhanghai.android.materialprogressbar.ShowBackgroundDrawable
    public void setShowBackground(boolean show) {
        if (this.mShowBackground != show) {
            this.mShowBackground = show;
            invalidateSelf();
        }
    }

    @Override // me.zhanghai.android.materialprogressbar.BaseSingleCircularProgressDrawable
    protected void onDrawRing(Canvas canvas, Paint paint) {
        int level = getLevel();
        if (level == 0) {
            return;
        }
        float ratio = level / 10000.0f;
        float startAngle = this.mStartAngleMax * ratio;
        float sweepAngle = 360.0f * ratio;
        drawRing(canvas, paint, startAngle, sweepAngle);
        if (this.mShowBackground) {
            drawRing(canvas, paint, startAngle, sweepAngle);
        }
    }
}