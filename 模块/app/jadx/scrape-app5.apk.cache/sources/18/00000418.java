package android.support.design.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.C0091R;
import android.support.p003v7.widget.TintTypedArray;
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
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, C0091R.styleable.TabItem);
        this.text = a.getText(C0091R.styleable.TabItem_android_text);
        this.icon = a.getDrawable(C0091R.styleable.TabItem_android_icon);
        this.customLayout = a.getResourceId(C0091R.styleable.TabItem_android_layout, 0);
        a.recycle();
    }
}