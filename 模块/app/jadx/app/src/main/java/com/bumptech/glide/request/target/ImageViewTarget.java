package com.bumptech.glide.request.target;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.request.transition.Transition;

/* loaded from: classes.dex */
public abstract class ImageViewTarget<Z> extends ViewTarget<ImageView, Z> implements Transition.ViewAdapter {
    @Nullable
    private Animatable animatable;

    protected abstract void setResource(@Nullable Z z);

    public ImageViewTarget(ImageView view) {
        super(view);
    }

    @Deprecated
    public ImageViewTarget(ImageView view, boolean waitForLayout) {
        super(view, waitForLayout);
    }

    @Override // com.bumptech.glide.request.transition.Transition.ViewAdapter
    @Nullable
    public Drawable getCurrentDrawable() {
        return ((ImageView) this.view).getDrawable();
    }

    @Override // com.bumptech.glide.request.transition.Transition.ViewAdapter
    public void setDrawable(Drawable drawable) {
        ((ImageView) this.view).setImageDrawable(drawable);
    }

    @Override // com.bumptech.glide.request.target.ViewTarget, com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
    public void onLoadStarted(@Nullable Drawable placeholder) {
        super.onLoadStarted(placeholder);
        setResourceInternal(null);
        setDrawable(placeholder);
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        setResourceInternal(null);
        setDrawable(errorDrawable);
    }

    @Override // com.bumptech.glide.request.target.ViewTarget, com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
    public void onLoadCleared(@Nullable Drawable placeholder) {
        super.onLoadCleared(placeholder);
        Animatable animatable = this.animatable;
        if (animatable != null) {
            animatable.stop();
        }
        setResourceInternal(null);
        setDrawable(placeholder);
    }

    @Override // com.bumptech.glide.request.target.Target
    public void onResourceReady(@NonNull Z resource, @Nullable Transition<? super Z> transition) {
        if (transition == null || !transition.transition(resource, this)) {
            setResourceInternal(resource);
        } else {
            maybeUpdateAnimatable(resource);
        }
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.manager.LifecycleListener
    public void onStart() {
        Animatable animatable = this.animatable;
        if (animatable != null) {
            animatable.start();
        }
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.manager.LifecycleListener
    public void onStop() {
        Animatable animatable = this.animatable;
        if (animatable != null) {
            animatable.stop();
        }
    }

    private void setResourceInternal(@Nullable Z resource) {
        setResource(resource);
        maybeUpdateAnimatable(resource);
    }

    private void maybeUpdateAnimatable(@Nullable Z resource) {
        if (resource instanceof Animatable) {
            this.animatable = (Animatable) resource;
            this.animatable.start();
            return;
        }
        this.animatable = null;
    }
}
