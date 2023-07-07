package com.lcodecore.tkrefreshlayout.header.bezierlayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class WaveView extends View {
    private int headHeight;
    Paint paint;
    Path path;
    private int waveHeight;

    public WaveView(Context context) {
        this(context, null, 0);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setColor(-14736346);
        this.paint.setAntiAlias(true);
    }

    public int getHeadHeight() {
        return this.headHeight;
    }

    public void setHeadHeight(int headHeight) {
        this.headHeight = headHeight;
    }

    public int getWaveHeight() {
        return this.waveHeight;
    }

    public void setWaveHeight(int waveHeight) {
        this.waveHeight = waveHeight;
    }

    public void setWaveColor(@ColorInt int color) {
        Paint paint = this.paint;
        if (paint != null) {
            paint.setColor(color);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.path.reset();
        this.path.lineTo(0.0f, this.headHeight);
        this.path.quadTo(getMeasuredWidth() / 2, this.headHeight + this.waveHeight, getMeasuredWidth(), this.headHeight);
        this.path.lineTo(getMeasuredWidth(), 0.0f);
        canvas.drawPath(this.path, this.paint);
    }

    static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
