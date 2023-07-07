package android.support.design.theme;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.p003v7.app.AppCompatViewInflater;
import android.support.p003v7.widget.AppCompatButton;
import android.util.AttributeSet;

@Keep
/* loaded from: classes.dex */
public class MaterialComponentsViewInflater extends AppCompatViewInflater {
    @Override // android.support.p003v7.app.AppCompatViewInflater
    @NonNull
    protected AppCompatButton createButton(Context context, AttributeSet attrs) {
        return new MaterialButton(context, attrs);
    }
}