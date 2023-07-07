package android.support.design.widget;

import android.animation.TimeInterpolator;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.animation.AnimationUtils;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.widget.TintTypedArray;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public final class CollapsingTextHelper {
    private static final boolean DEBUG_DRAW = false;
    private static final Paint DEBUG_DRAW_PAINT;
    private static final boolean USE_SCALING_TEXTURE;
    private boolean boundsChanged;
    private float collapsedDrawX;
    private float collapsedDrawY;
    private int collapsedShadowColor;
    private float collapsedShadowDx;
    private float collapsedShadowDy;
    private float collapsedShadowRadius;
    private ColorStateList collapsedTextColor;
    private Typeface collapsedTypeface;
    private float currentDrawX;
    private float currentDrawY;
    private float currentTextSize;
    private Typeface currentTypeface;
    private boolean drawTitle;
    private float expandedDrawX;
    private float expandedDrawY;
    private float expandedFraction;
    private int expandedShadowColor;
    private float expandedShadowDx;
    private float expandedShadowDy;
    private float expandedShadowRadius;
    private ColorStateList expandedTextColor;
    private Bitmap expandedTitleTexture;
    private Typeface expandedTypeface;
    private boolean isRtl;
    private TimeInterpolator positionInterpolator;
    private float scale;
    private int[] state;
    private CharSequence text;
    private TimeInterpolator textSizeInterpolator;
    private CharSequence textToDraw;
    private float textureAscent;
    private float textureDescent;
    private Paint texturePaint;
    private boolean useTexture;
    private final View view;
    private int expandedTextGravity = 16;
    private int collapsedTextGravity = 16;
    private float expandedTextSize = 15.0f;
    private float collapsedTextSize = 15.0f;
    private final TextPaint textPaint = new TextPaint(129);
    private final TextPaint tmpPaint = new TextPaint(this.textPaint);
    private final Rect collapsedBounds = new Rect();
    private final Rect expandedBounds = new Rect();
    private final RectF currentBounds = new RectF();

    static {
        USE_SCALING_TEXTURE = Build.VERSION.SDK_INT < 18;
        DEBUG_DRAW_PAINT = null;
        Paint paint = DEBUG_DRAW_PAINT;
        if (paint != null) {
            paint.setAntiAlias(true);
            DEBUG_DRAW_PAINT.setColor(-65281);
        }
    }

    public CollapsingTextHelper(View view) {
        this.view = view;
    }

    public void setTextSizeInterpolator(TimeInterpolator interpolator) {
        this.textSizeInterpolator = interpolator;
        recalculate();
    }

    public void setPositionInterpolator(TimeInterpolator interpolator) {
        this.positionInterpolator = interpolator;
        recalculate();
    }

    public void setExpandedTextSize(float textSize) {
        if (this.expandedTextSize != textSize) {
            this.expandedTextSize = textSize;
            recalculate();
        }
    }

    public void setCollapsedTextSize(float textSize) {
        if (this.collapsedTextSize != textSize) {
            this.collapsedTextSize = textSize;
            recalculate();
        }
    }

    public void setCollapsedTextColor(ColorStateList textColor) {
        if (this.collapsedTextColor != textColor) {
            this.collapsedTextColor = textColor;
            recalculate();
        }
    }

    public void setExpandedTextColor(ColorStateList textColor) {
        if (this.expandedTextColor != textColor) {
            this.expandedTextColor = textColor;
            recalculate();
        }
    }

    public void setExpandedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(this.expandedBounds, left, top, right, bottom)) {
            this.expandedBounds.set(left, top, right, bottom);
            this.boundsChanged = true;
            onBoundsChanged();
        }
    }

    public void setCollapsedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(this.collapsedBounds, left, top, right, bottom)) {
            this.collapsedBounds.set(left, top, right, bottom);
            this.boundsChanged = true;
            onBoundsChanged();
        }
    }

    public float calculateCollapsedTextWidth() {
        if (this.text == null) {
            return 0.0f;
        }
        getTextPaintCollapsed(this.tmpPaint);
        TextPaint textPaint = this.tmpPaint;
        CharSequence charSequence = this.text;
        return textPaint.measureText(charSequence, 0, charSequence.length());
    }

    public float getCollapsedTextHeight() {
        getTextPaintCollapsed(this.tmpPaint);
        return -this.tmpPaint.ascent();
    }

    public void getCollapsedTextActualBounds(RectF bounds) {
        boolean isRtl = calculateIsRtl(this.text);
        bounds.left = !isRtl ? this.collapsedBounds.left : this.collapsedBounds.right - calculateCollapsedTextWidth();
        bounds.top = this.collapsedBounds.top;
        bounds.right = !isRtl ? bounds.left + calculateCollapsedTextWidth() : this.collapsedBounds.right;
        bounds.bottom = this.collapsedBounds.top + getCollapsedTextHeight();
    }

    private void getTextPaintCollapsed(TextPaint textPaint) {
        textPaint.setTextSize(this.collapsedTextSize);
        textPaint.setTypeface(this.collapsedTypeface);
    }

    void onBoundsChanged() {
        this.drawTitle = this.collapsedBounds.width() > 0 && this.collapsedBounds.height() > 0 && this.expandedBounds.width() > 0 && this.expandedBounds.height() > 0;
    }

    public void setExpandedTextGravity(int gravity) {
        if (this.expandedTextGravity != gravity) {
            this.expandedTextGravity = gravity;
            recalculate();
        }
    }

    public int getExpandedTextGravity() {
        return this.expandedTextGravity;
    }

    public void setCollapsedTextGravity(int gravity) {
        if (this.collapsedTextGravity != gravity) {
            this.collapsedTextGravity = gravity;
            recalculate();
        }
    }

    public int getCollapsedTextGravity() {
        return this.collapsedTextGravity;
    }

    public void setCollapsedTextAppearance(int resId) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this.view.getContext(), resId, R.styleable.TextAppearance);
        if (a.hasValue(R.styleable.TextAppearance_android_textColor)) {
            this.collapsedTextColor = a.getColorStateList(R.styleable.TextAppearance_android_textColor);
        }
        if (a.hasValue(R.styleable.TextAppearance_android_textSize)) {
            this.collapsedTextSize = a.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) this.collapsedTextSize);
        }
        this.collapsedShadowColor = a.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
        this.collapsedShadowDx = a.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.collapsedShadowDy = a.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.collapsedShadowRadius = a.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        a.recycle();
        if (Build.VERSION.SDK_INT >= 16) {
            this.collapsedTypeface = readFontFamilyTypeface(resId);
        }
        recalculate();
    }

    public void setExpandedTextAppearance(int resId) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this.view.getContext(), resId, R.styleable.TextAppearance);
        if (a.hasValue(R.styleable.TextAppearance_android_textColor)) {
            this.expandedTextColor = a.getColorStateList(R.styleable.TextAppearance_android_textColor);
        }
        if (a.hasValue(R.styleable.TextAppearance_android_textSize)) {
            this.expandedTextSize = a.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) this.expandedTextSize);
        }
        this.expandedShadowColor = a.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
        this.expandedShadowDx = a.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.expandedShadowDy = a.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.expandedShadowRadius = a.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        a.recycle();
        if (Build.VERSION.SDK_INT >= 16) {
            this.expandedTypeface = readFontFamilyTypeface(resId);
        }
        recalculate();
    }

    private Typeface readFontFamilyTypeface(int resId) {
        TypedArray a = this.view.getContext().obtainStyledAttributes(resId, new int[]{16843692});
        try {
            String family = a.getString(0);
            if (family != null) {
                return Typeface.create(family, 0);
            }
            a.recycle();
            return null;
        } finally {
            a.recycle();
        }
    }

    public void setCollapsedTypeface(Typeface typeface) {
        if (this.collapsedTypeface != typeface) {
            this.collapsedTypeface = typeface;
            recalculate();
        }
    }

    public void setExpandedTypeface(Typeface typeface) {
        if (this.expandedTypeface != typeface) {
            this.expandedTypeface = typeface;
            recalculate();
        }
    }

    public void setTypefaces(Typeface typeface) {
        this.expandedTypeface = typeface;
        this.collapsedTypeface = typeface;
        recalculate();
    }

    public Typeface getCollapsedTypeface() {
        Typeface typeface = this.collapsedTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    public Typeface getExpandedTypeface() {
        Typeface typeface = this.expandedTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    public void setExpansionFraction(float fraction) {
        float fraction2 = android.support.v4.math.MathUtils.clamp(fraction, 0.0f, 1.0f);
        if (fraction2 != this.expandedFraction) {
            this.expandedFraction = fraction2;
            calculateCurrentOffsets();
        }
    }

    public final boolean setState(int[] state) {
        this.state = state;
        if (isStateful()) {
            recalculate();
            return true;
        }
        return false;
    }

    public final boolean isStateful() {
        ColorStateList colorStateList;
        ColorStateList colorStateList2 = this.collapsedTextColor;
        return (colorStateList2 != null && colorStateList2.isStateful()) || ((colorStateList = this.expandedTextColor) != null && colorStateList.isStateful());
    }

    public float getExpansionFraction() {
        return this.expandedFraction;
    }

    public float getCollapsedTextSize() {
        return this.collapsedTextSize;
    }

    public float getExpandedTextSize() {
        return this.expandedTextSize;
    }

    private void calculateCurrentOffsets() {
        calculateOffsets(this.expandedFraction);
    }

    private void calculateOffsets(float fraction) {
        interpolateBounds(fraction);
        this.currentDrawX = lerp(this.expandedDrawX, this.collapsedDrawX, fraction, this.positionInterpolator);
        this.currentDrawY = lerp(this.expandedDrawY, this.collapsedDrawY, fraction, this.positionInterpolator);
        setInterpolatedTextSize(lerp(this.expandedTextSize, this.collapsedTextSize, fraction, this.textSizeInterpolator));
        if (this.collapsedTextColor != this.expandedTextColor) {
            this.textPaint.setColor(blendColors(getCurrentExpandedTextColor(), getCurrentCollapsedTextColor(), fraction));
        } else {
            this.textPaint.setColor(getCurrentCollapsedTextColor());
        }
        this.textPaint.setShadowLayer(lerp(this.expandedShadowRadius, this.collapsedShadowRadius, fraction, null), lerp(this.expandedShadowDx, this.collapsedShadowDx, fraction, null), lerp(this.expandedShadowDy, this.collapsedShadowDy, fraction, null), blendColors(this.expandedShadowColor, this.collapsedShadowColor, fraction));
        ViewCompat.postInvalidateOnAnimation(this.view);
    }

    @ColorInt
    private int getCurrentExpandedTextColor() {
        int[] iArr = this.state;
        if (iArr != null) {
            return this.expandedTextColor.getColorForState(iArr, 0);
        }
        return this.expandedTextColor.getDefaultColor();
    }

    @VisibleForTesting
    @ColorInt
    public int getCurrentCollapsedTextColor() {
        int[] iArr = this.state;
        if (iArr != null) {
            return this.collapsedTextColor.getColorForState(iArr, 0);
        }
        return this.collapsedTextColor.getDefaultColor();
    }

    private void calculateBaseOffsets() {
        float currentTextSize = this.currentTextSize;
        calculateUsingTextSize(this.collapsedTextSize);
        CharSequence charSequence = this.textToDraw;
        float f = 0.0f;
        float width = charSequence != null ? this.textPaint.measureText(charSequence, 0, charSequence.length()) : 0.0f;
        int collapsedAbsGravity = GravityCompat.getAbsoluteGravity(this.collapsedTextGravity, this.isRtl ? 1 : 0);
        int i = collapsedAbsGravity & 112;
        if (i == 48) {
            this.collapsedDrawY = this.collapsedBounds.top - this.textPaint.ascent();
        } else if (i == 80) {
            this.collapsedDrawY = this.collapsedBounds.bottom;
        } else {
            float textHeight = this.textPaint.descent() - this.textPaint.ascent();
            float textOffset = (textHeight / 2.0f) - this.textPaint.descent();
            this.collapsedDrawY = this.collapsedBounds.centerY() + textOffset;
        }
        int i2 = collapsedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i2 == 1) {
            this.collapsedDrawX = this.collapsedBounds.centerX() - (width / 2.0f);
        } else if (i2 == 5) {
            this.collapsedDrawX = this.collapsedBounds.right - width;
        } else {
            this.collapsedDrawX = this.collapsedBounds.left;
        }
        calculateUsingTextSize(this.expandedTextSize);
        CharSequence charSequence2 = this.textToDraw;
        if (charSequence2 != null) {
            f = this.textPaint.measureText(charSequence2, 0, charSequence2.length());
        }
        float width2 = f;
        int expandedAbsGravity = GravityCompat.getAbsoluteGravity(this.expandedTextGravity, this.isRtl ? 1 : 0);
        int i3 = expandedAbsGravity & 112;
        if (i3 == 48) {
            this.expandedDrawY = this.expandedBounds.top - this.textPaint.ascent();
        } else if (i3 == 80) {
            this.expandedDrawY = this.expandedBounds.bottom;
        } else {
            float textHeight2 = this.textPaint.descent() - this.textPaint.ascent();
            float textOffset2 = (textHeight2 / 2.0f) - this.textPaint.descent();
            this.expandedDrawY = this.expandedBounds.centerY() + textOffset2;
        }
        int i4 = expandedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i4 == 1) {
            this.expandedDrawX = this.expandedBounds.centerX() - (width2 / 2.0f);
        } else if (i4 == 5) {
            this.expandedDrawX = this.expandedBounds.right - width2;
        } else {
            this.expandedDrawX = this.expandedBounds.left;
        }
        clearTexture();
        setInterpolatedTextSize(currentTextSize);
    }

    private void interpolateBounds(float fraction) {
        this.currentBounds.left = lerp(this.expandedBounds.left, this.collapsedBounds.left, fraction, this.positionInterpolator);
        this.currentBounds.top = lerp(this.expandedDrawY, this.collapsedDrawY, fraction, this.positionInterpolator);
        this.currentBounds.right = lerp(this.expandedBounds.right, this.collapsedBounds.right, fraction, this.positionInterpolator);
        this.currentBounds.bottom = lerp(this.expandedBounds.bottom, this.collapsedBounds.bottom, fraction, this.positionInterpolator);
    }

    public void draw(Canvas canvas) {
        float ascent;
        float y;
        int saveCount = canvas.save();
        if (this.textToDraw != null && this.drawTitle) {
            float x = this.currentDrawX;
            float y2 = this.currentDrawY;
            boolean drawTexture = this.useTexture && this.expandedTitleTexture != null;
            if (drawTexture) {
                float f = this.textureAscent;
                float f2 = this.scale;
                float ascent2 = f * f2;
                float f3 = this.textureDescent * f2;
                ascent = ascent2;
            } else {
                float ascent3 = this.textPaint.ascent() * this.scale;
                float descent = this.scale * this.textPaint.descent();
                ascent = ascent3;
            }
            if (!drawTexture) {
                y = y2;
            } else {
                y = y2 + ascent;
            }
            float y3 = this.scale;
            if (y3 != 1.0f) {
                canvas.scale(y3, y3, x, y);
            }
            if (drawTexture) {
                canvas.drawBitmap(this.expandedTitleTexture, x, y, this.texturePaint);
            } else {
                CharSequence charSequence = this.textToDraw;
                canvas.drawText(charSequence, 0, charSequence.length(), x, y, this.textPaint);
            }
        }
        canvas.restoreToCount(saveCount);
    }

    private boolean calculateIsRtl(CharSequence text) {
        boolean z = true;
        if (ViewCompat.getLayoutDirection(this.view) != 1) {
            z = false;
        }
        boolean defaultIsRtl = z;
        return (defaultIsRtl ? TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL : TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR).isRtl(text, 0, text.length());
    }

    private void setInterpolatedTextSize(float textSize) {
        calculateUsingTextSize(textSize);
        this.useTexture = USE_SCALING_TEXTURE && this.scale != 1.0f;
        if (this.useTexture) {
            ensureExpandedTexture();
        }
        ViewCompat.postInvalidateOnAnimation(this.view);
    }

    private void calculateUsingTextSize(float textSize) {
        float newTextSize;
        float textSizeRatio;
        if (this.text == null) {
            return;
        }
        float collapsedWidth = this.collapsedBounds.width();
        float expandedWidth = this.expandedBounds.width();
        boolean updateDrawText = false;
        if (isClose(textSize, this.collapsedTextSize)) {
            newTextSize = this.collapsedTextSize;
            this.scale = 1.0f;
            Typeface typeface = this.currentTypeface;
            Typeface typeface2 = this.collapsedTypeface;
            if (typeface != typeface2) {
                this.currentTypeface = typeface2;
                updateDrawText = true;
            }
            textSizeRatio = collapsedWidth;
        } else {
            newTextSize = this.expandedTextSize;
            Typeface typeface3 = this.currentTypeface;
            Typeface typeface4 = this.expandedTypeface;
            if (typeface3 != typeface4) {
                this.currentTypeface = typeface4;
                updateDrawText = true;
            }
            if (isClose(textSize, this.expandedTextSize)) {
                this.scale = 1.0f;
            } else {
                this.scale = textSize / this.expandedTextSize;
            }
            float textSizeRatio2 = this.collapsedTextSize / this.expandedTextSize;
            float scaledDownWidth = expandedWidth * textSizeRatio2;
            if (scaledDownWidth > collapsedWidth) {
                textSizeRatio = Math.min(collapsedWidth / textSizeRatio2, expandedWidth);
            } else {
                textSizeRatio = expandedWidth;
            }
        }
        boolean z = true;
        if (textSizeRatio > 0.0f) {
            updateDrawText = this.currentTextSize != newTextSize || this.boundsChanged || updateDrawText;
            this.currentTextSize = newTextSize;
            this.boundsChanged = false;
        }
        if (this.textToDraw == null || updateDrawText) {
            this.textPaint.setTextSize(this.currentTextSize);
            this.textPaint.setTypeface(this.currentTypeface);
            TextPaint textPaint = this.textPaint;
            if (this.scale == 1.0f) {
                z = false;
            }
            textPaint.setLinearText(z);
            CharSequence title = TextUtils.ellipsize(this.text, this.textPaint, textSizeRatio, TextUtils.TruncateAt.END);
            if (!TextUtils.equals(title, this.textToDraw)) {
                this.textToDraw = title;
                this.isRtl = calculateIsRtl(this.textToDraw);
            }
        }
    }

    private void ensureExpandedTexture() {
        if (this.expandedTitleTexture != null || this.expandedBounds.isEmpty() || TextUtils.isEmpty(this.textToDraw)) {
            return;
        }
        calculateOffsets(0.0f);
        this.textureAscent = this.textPaint.ascent();
        this.textureDescent = this.textPaint.descent();
        TextPaint textPaint = this.textPaint;
        CharSequence charSequence = this.textToDraw;
        int w = Math.round(textPaint.measureText(charSequence, 0, charSequence.length()));
        int h = Math.round(this.textureDescent - this.textureAscent);
        if (w <= 0 || h <= 0) {
            return;
        }
        this.expandedTitleTexture = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(this.expandedTitleTexture);
        CharSequence charSequence2 = this.textToDraw;
        c.drawText(charSequence2, 0, charSequence2.length(), 0.0f, h - this.textPaint.descent(), this.textPaint);
        if (this.texturePaint == null) {
            this.texturePaint = new Paint(3);
        }
    }

    public void recalculate() {
        if (this.view.getHeight() > 0 && this.view.getWidth() > 0) {
            calculateBaseOffsets();
            calculateCurrentOffsets();
        }
    }

    public void setText(CharSequence text) {
        if (text == null || !text.equals(this.text)) {
            this.text = text;
            this.textToDraw = null;
            clearTexture();
            recalculate();
        }
    }

    public CharSequence getText() {
        return this.text;
    }

    private void clearTexture() {
        Bitmap bitmap = this.expandedTitleTexture;
        if (bitmap != null) {
            bitmap.recycle();
            this.expandedTitleTexture = null;
        }
    }

    private static boolean isClose(float value, float targetValue) {
        return Math.abs(value - targetValue) < 0.001f;
    }

    public ColorStateList getExpandedTextColor() {
        return this.expandedTextColor;
    }

    public ColorStateList getCollapsedTextColor() {
        return this.collapsedTextColor;
    }

    private static int blendColors(int color1, int color2, float ratio) {
        float inverseRatio = 1.0f - ratio;
        float a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio);
        float r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio);
        float g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio);
        float b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    private static float lerp(float startValue, float endValue, float fraction, TimeInterpolator interpolator) {
        if (interpolator != null) {
            fraction = interpolator.getInterpolation(fraction);
        }
        return AnimationUtils.lerp(startValue, endValue, fraction);
    }

    private static boolean rectEquals(Rect r, int left, int top, int right, int bottom) {
        return r.left == left && r.top == top && r.right == right && r.bottom == bottom;
    }
}
