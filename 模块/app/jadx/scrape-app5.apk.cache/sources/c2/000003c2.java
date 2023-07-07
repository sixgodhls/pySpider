package android.support.p003v7.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.p003v7.appcompat.C0452R;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RatingBar;

/* renamed from: android.support.v7.widget.AppCompatRatingBar */
/* loaded from: classes.dex */
public class AppCompatRatingBar extends RatingBar {
    private final AppCompatProgressBarHelper mAppCompatProgressBarHelper;

    public AppCompatRatingBar(Context context) {
        this(context, null);
    }

    public AppCompatRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, C0452R.attr.ratingBarStyle);
    }

    public AppCompatRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAppCompatProgressBarHelper = new AppCompatProgressBarHelper(this);
        this.mAppCompatProgressBarHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override // android.widget.RatingBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Bitmap sampleTile = this.mAppCompatProgressBarHelper.getSampleTime();
        if (sampleTile != null) {
            int width = sampleTile.getWidth() * getNumStars();
            setMeasuredDimension(View.resolveSizeAndState(width, widthMeasureSpec, 0), getMeasuredHeight());
        }
    }
}