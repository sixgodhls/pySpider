package me.zhanghai.android.materialprogressbar;

/* loaded from: classes.dex */
abstract class BaseProgressDrawable extends BasePaintDrawable implements IntrinsicPaddingDrawable {
    protected boolean mUseIntrinsicPadding = true;

    @Override // me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable
    public boolean getUseIntrinsicPadding() {
        return this.mUseIntrinsicPadding;
    }

    @Override // me.zhanghai.android.materialprogressbar.IntrinsicPaddingDrawable
    public void setUseIntrinsicPadding(boolean useIntrinsicPadding) {
        if (this.mUseIntrinsicPadding != useIntrinsicPadding) {
            this.mUseIntrinsicPadding = useIntrinsicPadding;
            invalidateSelf();
        }
    }
}