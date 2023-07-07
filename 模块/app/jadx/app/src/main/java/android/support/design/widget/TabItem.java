package android.support.design.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.R;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class TabItem extends View {
    public final int customLayout;
    public final Drawable icon;
    public final CharSequence text;

    public TabItem(Context context) {
        this(context, null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.TabItem);
        this.text = a.getText(R.styleable.TabItem_android_text);
        this.icon = a.getDrawable(R.styleable.TabItem_android_icon);
        this.customLayout = a.getResourceId(R.styleable.TabItem_android_layout, 0);
        a.recycle();
    }
}
