package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class GifFrameLoader {
    private final BitmapPool bitmapPool;
    private final List<FrameCallback> callbacks;
    private DelayTarget current;
    private Bitmap firstFrame;
    private final GifDecoder gifDecoder;
    private final Handler handler;
    private boolean isCleared;
    private boolean isLoadPending;
    private boolean isRunning;
    private DelayTarget next;
    @Nullable
    private OnEveryFrameListener onEveryFrameListener;
    private DelayTarget pendingTarget;
    private RequestBuilder<Bitmap> requestBuilder;
    final RequestManager requestManager;
    private boolean startFromFirstFrame;
    private Transformation<Bitmap> transformation;

    /* loaded from: classes.dex */
    public interface FrameCallback {
        void onFrameReady();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface OnEveryFrameListener {
        void onFrameReady();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GifFrameLoader(Glide glide, GifDecoder gifDecoder, int width, int height, Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this(glide.getBitmapPool(), Glide.with(glide.getContext()), gifDecoder, null, getRequestBuilder(Glide.with(glide.getContext()), width, height), transformation, firstFrame);
    }

    GifFrameLoader(BitmapPool bitmapPool, RequestManager requestManager, GifDecoder gifDecoder, Handler handler, RequestBuilder<Bitmap> requestBuilder, Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this.callbacks = new ArrayList();
        this.requestManager = requestManager;
        handler = handler == null ? new Handler(Looper.getMainLooper(), new FrameLoaderCallback()) : handler;
        this.bitmapPool = bitmapPool;
        this.handler = handler;
        this.requestBuilder = requestBuilder;
        this.gifDecoder = gifDecoder;
        setFrameTransformation(transformation, firstFrame);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFrameTransformation(Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this.transformation = (Transformation) Preconditions.checkNotNull(transformation);
        this.firstFrame = (Bitmap) Preconditions.checkNotNull(firstFrame);
        this.requestBuilder = this.requestBuilder.apply(new RequestOptions().transform(transformation));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Transformation<Bitmap> getFrameTransformation() {
        return this.transformation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap getFirstFrame() {
        return this.firstFrame;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void subscribe(FrameCallback frameCallback) {
        if (this.isCleared) {
            throw new IllegalStateException("Cannot subscribe to a cleared frame loader");
        }
        if (this.callbacks.contains(frameCallback)) {
            throw new IllegalStateException("Cannot subscribe twice in a row");
        }
        boolean start = this.callbacks.isEmpty();
        this.callbacks.add(frameCallback);
        if (start) {
            start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unsubscribe(FrameCallback frameCallback) {
        this.callbacks.remove(frameCallback);
        if (this.callbacks.isEmpty()) {
            stop();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getWidth() {
        return getCurrentFrame().getWidth();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHeight() {
        return getCurrentFrame().getHeight();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSize() {
        return this.gifDecoder.getByteSize() + getFrameSize();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCurrentIndex() {
        DelayTarget delayTarget = this.current;
        if (delayTarget != null) {
            return delayTarget.index;
        }
        return -1;
    }

    private int getFrameSize() {
        return Util.getBitmapByteSize(getCurrentFrame().getWidth(), getCurrentFrame().getHeight(), getCurrentFrame().getConfig());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteBuffer getBuffer() {
        return this.gifDecoder.getData().asReadOnlyBuffer();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFrameCount() {
        return this.gifDecoder.getFrameCount();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getLoopCount() {
        return this.gifDecoder.getTotalIterationCount();
    }

    private void start() {
        if (this.isRunning) {
            return;
        }
        this.isRunning = true;
        this.isCleared = false;
        loadNextFrame();
    }

    private void stop() {
        this.isRunning = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.callbacks.clear();
        recycleFirstFrame();
        stop();
        DelayTarget delayTarget = this.current;
        if (delayTarget != null) {
            this.requestManager.clear(delayTarget);
            this.current = null;
        }
        DelayTarget delayTarget2 = this.next;
        if (delayTarget2 != null) {
            this.requestManager.clear(delayTarget2);
            this.next = null;
        }
        DelayTarget delayTarget3 = this.pendingTarget;
        if (delayTarget3 != null) {
            this.requestManager.clear(delayTarget3);
            this.pendingTarget = null;
        }
        this.gifDecoder.clear();
        this.isCleared = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap getCurrentFrame() {
        DelayTarget delayTarget = this.current;
        return delayTarget != null ? delayTarget.getResource() : this.firstFrame;
    }

    private void loadNextFrame() {
        if (!this.isRunning || this.isLoadPending) {
            return;
        }
        if (this.startFromFirstFrame) {
            Preconditions.checkArgument(this.pendingTarget == null, "Pending target must be null when starting from the first frame");
            this.gifDecoder.resetFrameIndex();
            this.startFromFirstFrame = false;
        }
        if (this.pendingTarget != null) {
            DelayTarget temp = this.pendingTarget;
            this.pendingTarget = null;
            onFrameReady(temp);
            return;
        }
        this.isLoadPending = true;
        int delay = this.gifDecoder.getNextDelay();
        long targetTime = SystemClock.uptimeMillis() + delay;
        this.gifDecoder.advance();
        this.next = new DelayTarget(this.handler, this.gifDecoder.getCurrentFrameIndex(), targetTime);
        this.requestBuilder.apply(RequestOptions.signatureOf(getFrameSignature())).mo211load((Object) this.gifDecoder).into((RequestBuilder<Bitmap>) this.next);
    }

    private void recycleFirstFrame() {
        Bitmap bitmap = this.firstFrame;
        if (bitmap != null) {
            this.bitmapPool.put(bitmap);
            this.firstFrame = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNextStartFromFirstFrame() {
        Preconditions.checkArgument(!this.isRunning, "Can't restart a running animation");
        this.startFromFirstFrame = true;
        DelayTarget delayTarget = this.pendingTarget;
        if (delayTarget != null) {
            this.requestManager.clear(delayTarget);
            this.pendingTarget = null;
        }
    }

    @VisibleForTesting
    void setOnEveryFrameReadyListener(@Nullable OnEveryFrameListener onEveryFrameListener) {
        this.onEveryFrameListener = onEveryFrameListener;
    }

    @VisibleForTesting
    void onFrameReady(DelayTarget delayTarget) {
        OnEveryFrameListener onEveryFrameListener = this.onEveryFrameListener;
        if (onEveryFrameListener != null) {
            onEveryFrameListener.onFrameReady();
        }
        this.isLoadPending = false;
        if (this.isCleared) {
            this.handler.obtainMessage(2, delayTarget).sendToTarget();
        } else if (!this.isRunning) {
            this.pendingTarget = delayTarget;
        } else {
            if (delayTarget.getResource() != null) {
                recycleFirstFrame();
                DelayTarget previous = this.current;
                this.current = delayTarget;
                for (int i = this.callbacks.size() - 1; i >= 0; i--) {
                    FrameCallback cb = this.callbacks.get(i);
                    cb.onFrameReady();
                }
                if (previous != null) {
                    this.handler.obtainMessage(2, previous).sendToTarget();
                }
            }
            loadNextFrame();
        }
    }

    /* loaded from: classes.dex */
    private class FrameLoaderCallback implements Handler.Callback {
        static final int MSG_CLEAR = 2;
        static final int MSG_DELAY = 1;

        FrameLoaderCallback() {
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                DelayTarget target = (DelayTarget) msg.obj;
                GifFrameLoader.this.onFrameReady(target);
                return true;
            } else if (msg.what == 2) {
                DelayTarget target2 = (DelayTarget) msg.obj;
                GifFrameLoader.this.requestManager.clear(target2);
                return false;
            } else {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class DelayTarget extends SimpleTarget<Bitmap> {
        private final Handler handler;
        final int index;
        private Bitmap resource;
        private final long targetTime;

        @Override // com.bumptech.glide.request.target.Target
        public /* bridge */ /* synthetic */ void onResourceReady(@NonNull Object obj, @Nullable Transition transition) {
            onResourceReady((Bitmap) obj, (Transition<? super Bitmap>) transition);
        }

        DelayTarget(Handler handler, int index, long targetTime) {
            this.handler = handler;
            this.index = index;
            this.targetTime = targetTime;
        }

        Bitmap getResource() {
            return this.resource;
        }

        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            this.resource = resource;
            Message msg = this.handler.obtainMessage(1, this);
            this.handler.sendMessageAtTime(msg, this.targetTime);
        }
    }

    private static RequestBuilder<Bitmap> getRequestBuilder(RequestManager requestManager, int width, int height) {
        return requestManager.asBitmap().apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).useAnimationPool(true).skipMemoryCache(true).override(width, height));
    }

    private static Key getFrameSignature() {
        return new ObjectKey(Double.valueOf(Math.random()));
    }
}
