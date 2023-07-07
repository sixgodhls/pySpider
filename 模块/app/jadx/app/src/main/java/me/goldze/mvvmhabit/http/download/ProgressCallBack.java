package me.goldze.mvvmhabit.http.download;

import android.util.Log;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import okhttp3.ResponseBody;

/* loaded from: classes.dex */
public abstract class ProgressCallBack<T> {
    private String destFileDir;
    private String destFileName;
    private Disposable mSubscription;

    public abstract void onError(Throwable th);

    public abstract void onSuccess(T t);

    public abstract void progress(long j, long j2);

    public ProgressCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        subscribeLoadProgress();
    }

    public void onStart() {
    }

    public void onCompleted() {
    }

    public void saveFile(ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;
        try {
            try {
                try {
                    is = body.byteStream();
                    File dir = new File(this.destFileDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, this.destFileName);
                    fos = new FileOutputStream(file);
                    while (true) {
                        int len = is.read(buf);
                        if (len == -1) {
                            break;
                        }
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    unsubscribe();
                    if (is != null) {
                        is.close();
                    }
                    fos.close();
                } catch (Throwable th) {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            Log.e("saveFile", e.getMessage());
                            throw th;
                        }
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    throw th;
                }
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
                if (is != null) {
                    is.close();
                }
                if (fos == null) {
                    return;
                }
                fos.close();
            } catch (IOException e3) {
                e3.printStackTrace();
                if (is != null) {
                    is.close();
                }
                if (fos == null) {
                    return;
                }
                fos.close();
            }
        } catch (IOException e4) {
            Log.e("saveFile", e4.getMessage());
        }
    }

    public void subscribeLoadProgress() {
        this.mSubscription = RxBus.getDefault().toObservable(DownLoadStateBean.class).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<DownLoadStateBean>() { // from class: me.goldze.mvvmhabit.http.download.ProgressCallBack.1
            @Override // io.reactivex.functions.Consumer
            public void accept(DownLoadStateBean progressLoadBean) throws Exception {
                ProgressCallBack.this.progress(progressLoadBean.getBytesLoaded(), progressLoadBean.getTotal());
            }
        });
        RxSubscriptions.add(this.mSubscription);
    }

    public void unsubscribe() {
        RxSubscriptions.remove(this.mSubscription);
    }
}
