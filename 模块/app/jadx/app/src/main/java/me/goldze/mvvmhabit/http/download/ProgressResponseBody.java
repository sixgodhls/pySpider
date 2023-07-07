package me.goldze.mvvmhabit.http.download;

import java.io.IOException;
import me.goldze.mvvmhabit.bus.RxBus;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/* loaded from: classes.dex */
public class ProgressResponseBody extends ResponseBody {
    private BufferedSource bufferedSource;
    private ResponseBody responseBody;
    private String tag;

    public ProgressResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    public ProgressResponseBody(ResponseBody responseBody, String tag) {
        this.responseBody = responseBody;
        this.tag = tag;
    }

    @Override // okhttp3.ResponseBody
    public MediaType contentType() {
        return this.responseBody.contentType();
    }

    @Override // okhttp3.ResponseBody
    public long contentLength() {
        return this.responseBody.contentLength();
    }

    @Override // okhttp3.ResponseBody
    public BufferedSource source() {
        if (this.bufferedSource == null) {
            this.bufferedSource = Okio.buffer(source(this.responseBody.source()));
        }
        return this.bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) { // from class: me.goldze.mvvmhabit.http.download.ProgressResponseBody.1
            long bytesReaded = 0;

            @Override // okio.ForwardingSource, okio.Source
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                this.bytesReaded += bytesRead == -1 ? 0L : bytesRead;
                RxBus.getDefault().post(new DownLoadStateBean(ProgressResponseBody.this.contentLength(), this.bytesReaded, ProgressResponseBody.this.tag));
                return bytesRead;
            }
        };
    }
}
