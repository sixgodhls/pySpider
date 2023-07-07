package com.bumptech.glide.request.transition;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

/* loaded from: classes.dex */
public interface Transition<R> {

    /* loaded from: classes.dex */
    public interface ViewAdapter {
        @Nullable
        Drawable getCurrentDrawable();

        View getView();

        void setDrawable(Drawable drawable);
    }

    boolean transition(R r, ViewAdapter viewAdapter);
}
