package android.support.p003v7.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.RequiresApi;
import android.support.p003v7.widget.RoundRectDrawableWithShadow;

@RequiresApi(17)
/* renamed from: android.support.v7.widget.CardViewApi17Impl */
/* loaded from: classes.dex */
class CardViewApi17Impl extends CardViewBaseImpl {
    @Override // android.support.p003v7.widget.CardViewBaseImpl, android.support.p003v7.widget.CardViewImpl
    public void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper = new RoundRectDrawableWithShadow.RoundRectHelper() { // from class: android.support.v7.widget.CardViewApi17Impl.1
            @Override // android.support.p003v7.widget.RoundRectDrawableWithShadow.RoundRectHelper
            public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius, Paint paint) {
                canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
            }
        };
    }
}