package android.support.design.widget;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.widget.ImageButton;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class VisibilityAwareImageButton extends ImageButton {
    private int userSetVisibility;

    public VisibilityAwareImageButton(Context context) {
        this(context, null);
    }

    public VisibilityAwareImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisibilityAwareImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.userSetVisibility = getVisibility();
    }

    @Override // android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        internalSetVisibility(visibility, true);
    }

    public final void internalSetVisibility(int visibility, boolean fromUser) {
        super.setVisibility(visibility);
        if (fromUser) {
            this.userSetVisibility = visibility;
        }
    }

    public final int getUserSetVisibility() {
        return this.userSetVisibility;
    }
}
