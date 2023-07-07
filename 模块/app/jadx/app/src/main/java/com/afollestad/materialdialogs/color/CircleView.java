package com.afollestad.materialdialogs.color;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.afollestad.materialdialogs.util.DialogUtils;

/* loaded from: classes.dex */
public class CircleView extends FrameLayout {
    private final int borderWidthLarge;
    private final int borderWidthSmall;
    private final Paint innerPaint;
    private final Paint outerPaint;
    private boolean selected;
    private final Paint whitePaint;

    public CircleView(Context context) {
        this(context, null, 0);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources r = getResources();
        this.borderWidthSmall = (int) TypedValue.applyDimension(1, 3.0f, r.getDisplayMetrics());
        this.borderWidthLarge = (int) TypedValue.applyDimension(1, 5.0f, r.getDisplayMetrics());
        this.whitePaint = new Paint();
        this.whitePaint.setAntiAlias(true);
        this.whitePaint.setColor(-1);
        this.innerPaint = new Paint();
        this.innerPaint.setAntiAlias(true);
        this.outerPaint = new Paint();
        this.outerPaint.setAntiAlias(true);
        update(-12303292);
        setWillNotDraw(false);
    }

    @ColorInt
    private static int translucentColor(int color) {
        int alpha = Math.round(Color.alpha(color) * 0.7f);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0d, to = 2.0d) float by) {
        if (by == 1.0f) {
            return color;
        }
        Color.colorToHSV(color, hsv);
        float[] hsv = {0.0f, 0.0f, hsv[2] * by};
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    public static int shiftColorDown(@ColorInt int color) {
        return shiftColor(color, 0.9f);
    }

    @ColorInt
    public static int shiftColorUp(@ColorInt int color) {
        return shiftColor(color, 1.1f);
    }

    private void update(@ColorInt int color) {
        this.innerPaint.setColor(color);
        this.outerPaint.setColor(shiftColorDown(color));
        Drawable selector = createSelector(color);
        if (Build.VERSION.SDK_INT >= 21) {
            int[][] states = {new int[]{16842919}};
            int[] colors = {shiftColorUp(color)};
            ColorStateList rippleColors = new ColorStateList(states, colors);
            setForeground(new RippleDrawable(rippleColors, selector, null));
            return;
        }
        setForeground(selector);
    }

    @Override // android.view.View
    public void setBackgroundColor(@ColorInt int color) {
        update(color);
        requestLayout();
        invalidate();
    }

    @Override // android.view.View
    public void setBackgroundResource(@ColorRes int color) {
        setBackgroundColor(DialogUtils.getColor(getContext(), color));
    }

    @Override // android.view.View
    @Deprecated
    public void setBackground(Drawable background) {
        throw new IllegalStateException("Cannot use setBackground() on CircleView.");
    }

    @Override // android.view.View
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        throw new IllegalStateException("Cannot use setBackgroundDrawable() on CircleView.");
    }

    @Override // android.view.View
    @Deprecated
    public void setActivated(boolean activated) {
        throw new IllegalStateException("Cannot use setActivated() on CircleView.");
    }

    @Override // android.view.View
    public void setSelected(boolean selected) {
        this.selected = selected;
        requestLayout();
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int outerRadius = getMeasuredWidth() / 2;
        if (this.selected) {
            int whiteRadius = outerRadius - this.borderWidthLarge;
            int innerRadius = whiteRadius - this.borderWidthSmall;
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, outerRadius, this.outerPaint);
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, whiteRadius, this.whitePaint);
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, innerRadius, this.innerPaint);
            return;
        }
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, outerRadius, this.innerPaint);
    }

    private Drawable createSelector(int color) {
        ShapeDrawable darkerCircle = new ShapeDrawable(new OvalShape());
        darkerCircle.getPaint().setColor(translucentColor(shiftColorUp(color)));
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, darkerCircle);
        return stateListDrawable;
    }

    public void showHint(int color) {
        int[] screenPos = new int[2];
        Rect displayFrame = new Rect();
        getLocationOnScreen(screenPos);
        getWindowVisibleDisplayFrame(displayFrame);
        Context context = getContext();
        int width = getWidth();
        int height = getHeight();
        int midy = screenPos[1] + (height / 2);
        int referenceX = screenPos[0] + (width / 2);
        if (ViewCompat.getLayoutDirection(this) == 0) {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            referenceX = screenWidth - referenceX;
        }
        Toast cheatSheet = Toast.makeText(context, String.format("#%06X", Integer.valueOf(16777215 & color)), 0);
        if (midy >= displayFrame.height()) {
            cheatSheet.setGravity(81, 0, height);
        } else {
            cheatSheet.setGravity(8388661, referenceX, (screenPos[1] + height) - displayFrame.top);
        }
        cheatSheet.show();
    }
}
