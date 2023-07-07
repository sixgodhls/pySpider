package me.zhanghai.android.materialprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/* loaded from: classes.dex */
class SingleHorizontalProgressDrawable extends BaseSingleHorizontalProgressDrawable implements ShowBackgroundDrawable {
    private static final int LEVEL_MAX = 10000;
    private boolean mShowBackground;

    public SingleHorizontalProgressDrawable(Context context) {
        super(context);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // me.zhanghai.android.materialprogressbar.BaseSingleHorizontalProgressDrawable
    public void onDrawRect(Canvas canvas, Paint paint) {
        int level = getLevel();
        if (level == 0) {
            return;
        }
        int saveCount = canvas.save();
        canvas.scale(level / 10000.0f, 1.0f, RECT_BOUND.left, 0.0f);
        super.onDrawRect(canvas, paint);
        if (this.mShowBackground) {
            super.onDrawRect(canvas, paint);
        }
        canvas.restoreToCount(saveCount);
    }
}
