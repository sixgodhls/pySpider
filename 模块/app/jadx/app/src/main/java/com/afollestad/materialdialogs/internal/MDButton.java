package com.afollestad.materialdialogs.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.R;
import com.afollestad.materialdialogs.util.DialogUtils;

@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes.dex */
public class MDButton extends TextView {
    private Drawable defaultBackground;
    private boolean stacked = false;
    private Drawable stackedBackground;
    private int stackedEndPadding;
    private GravityEnum stackedGravity;

    public MDButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MDButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.stackedEndPadding = context.getResources().getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
        this.stackedGravity = GravityEnum.END;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStacked(boolean stacked, boolean force) {
        if (this.stacked != stacked || force) {
            setGravity(stacked ? this.stackedGravity.getGravityInt() | 16 : 17);
            if (Build.VERSION.SDK_INT >= 17) {
                setTextAlignment(stacked ? this.stackedGravity.getTextAlignment() : 4);
            }
            DialogUtils.setBackgroundCompat(this, stacked ? this.stackedBackground : this.defaultBackground);
            if (stacked) {
                setPadding(this.stackedEndPadding, getPaddingTop(), this.stackedEndPadding, getPaddingBottom());
            }
            this.stacked = stacked;
        }
    }

    public void setStackedGravity(GravityEnum gravity) {
        this.stackedGravity = gravity;
    }

    public void setStackedSelector(Drawable d) {
        this.stackedBackground = d;
        if (this.stacked) {
            setStacked(true, true);
        }
    }

    public void setDefaultSelector(Drawable d) {
        this.defaultBackground = d;
        if (!this.stacked) {
            setStacked(false, true);
        }
    }

    public void setAllCapsCompat(boolean allCaps) {
        if (Build.VERSION.SDK_INT >= 14) {
            setAllCaps(allCaps);
        } else if (allCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        } else {
            setTransformationMethod(null);
        }
    }
}
