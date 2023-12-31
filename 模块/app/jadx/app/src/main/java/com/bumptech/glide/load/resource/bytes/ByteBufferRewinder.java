package com.bumptech.glide.load.resource.bytes;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.data.DataRewinder;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ByteBufferRewinder implements DataRewinder<ByteBuffer> {
    private final ByteBuffer buffer;

    public ByteBufferRewinder(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override // com.bumptech.glide.load.data.DataRewinder
    @NonNull
    /* renamed from: rewindAndGet  reason: collision with other method in class */
    public ByteBuffer mo237rewindAndGet() {
        this.buffer.position(0);
        return this.buffer;
    }

    @Override // com.bumptech.glide.load.data.DataRewinder
    public void cleanup() {
    }

    /* loaded from: classes.dex */
    public static class Factory implements DataRewinder.Factory<ByteBuffer> {
        @Override // com.bumptech.glide.load.data.DataRewinder.Factory
        @NonNull
        public DataRewinder<ByteBuffer> build(ByteBuffer data) {
            return new ByteBufferRewinder(data);
        }

        @Override // com.bumptech.glide.load.data.DataRewinder.Factory
        @NonNull
        public Class<ByteBuffer> getDataClass() {
            return ByteBuffer.class;
        }
    }
}
