package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ByteArrayLoader<Data> implements ModelLoader<byte[], Data> {
    private final Converter<Data> converter;

    /* loaded from: classes.dex */
    public interface Converter<Data> {
        /* renamed from: convert */
        Data mo230convert(byte[] bArr);

        Class<Data> getDataClass();
    }

    public ByteArrayLoader(Converter<Data> converter) {
        this.converter = converter;
    }

    @Override // com.bumptech.glide.load.model.ModelLoader
    public ModelLoader.LoadData<Data> buildLoadData(@NonNull byte[] model, int width, int height, @NonNull Options options) {
        return new ModelLoader.LoadData<>(new ObjectKey(model), new Fetcher(model, this.converter));
    }

    @Override // com.bumptech.glide.load.model.ModelLoader
    public boolean handles(@NonNull byte[] model) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Fetcher<Data> implements DataFetcher<Data> {
        private final Converter<Data> converter;
        private final byte[] model;

        Fetcher(byte[] model, Converter<Data> converter) {
            this.model = model;
            this.converter = converter;
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public void loadData(@NonNull Priority priority, @NonNull DataFetcher.DataCallback<? super Data> callback) {
            callback.onDataReady((Data) this.converter.mo230convert(this.model));
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public void cleanup() {
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public void cancel() {
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        @NonNull
        public Class<Data> getDataClass() {
            return this.converter.getDataClass();
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        @NonNull
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    /* loaded from: classes.dex */
    public static class ByteBufferFactory implements ModelLoaderFactory<byte[], ByteBuffer> {
        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        @NonNull
        public ModelLoader<byte[], ByteBuffer> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ByteArrayLoader(new Converter<ByteBuffer>() { // from class: com.bumptech.glide.load.model.ByteArrayLoader.ByteBufferFactory.1
                @Override // com.bumptech.glide.load.model.ByteArrayLoader.Converter
                /* renamed from: convert  reason: avoid collision after fix types in other method */
                public ByteBuffer mo230convert(byte[] model) {
                    return ByteBuffer.wrap(model);
                }

                @Override // com.bumptech.glide.load.model.ByteArrayLoader.Converter
                public Class<ByteBuffer> getDataClass() {
                    return ByteBuffer.class;
                }
            });
        }

        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        public void teardown() {
        }
    }

    /* loaded from: classes.dex */
    public static class StreamFactory implements ModelLoaderFactory<byte[], InputStream> {
        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        @NonNull
        public ModelLoader<byte[], InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ByteArrayLoader(new Converter<InputStream>() { // from class: com.bumptech.glide.load.model.ByteArrayLoader.StreamFactory.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // com.bumptech.glide.load.model.ByteArrayLoader.Converter
                /* renamed from: convert */
                public InputStream mo230convert(byte[] model) {
                    return new ByteArrayInputStream(model);
                }

                @Override // com.bumptech.glide.load.model.ByteArrayLoader.Converter
                public Class<InputStream> getDataClass() {
                    return InputStream.class;
                }
            });
        }

        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        public void teardown() {
        }
    }
}
