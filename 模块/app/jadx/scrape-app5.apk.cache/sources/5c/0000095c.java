package me.goldze.mvvmhabit.utils.compression;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.p000v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Luban {
    private static String DEFAULT_DISK_CACHE_DIR = "smartcity_disk_cache";
    private static final int FIRST_GEAR = 1;
    private static volatile Luban INSTANCE = null;
    private static final String TAG = "smartcity";
    public static final int THIRD_GEAR = 3;
    private OnCompressListener compressListener;
    private String filename;
    private final File mCacheDir;
    private String mFile;
    private List<String> mListFile = new ArrayList();
    private int gear = 3;

    private Luban(File cacheDir) {
        this.mCacheDir = cacheDir;
    }

    private static synchronized File getPhotoCacheDir(Context context) {
        File photoCacheDir;
        synchronized (Luban.class) {
            photoCacheDir = getPhotoCacheDir(context, DEFAULT_DISK_CACHE_DIR);
        }
        return photoCacheDir;
    }

    private static File getPhotoCacheDir(Context context, String cacheName) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                return null;
            }
            File noMedia = new File(cacheDir + "/.nomedia");
            if (!noMedia.mkdirs() && (!noMedia.exists() || !noMedia.isDirectory())) {
                return null;
            }
            return result;
        }
        if (Log.isLoggable(TAG, 6)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }

    public static Luban get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Luban(getPhotoCacheDir(context));
        }
        return INSTANCE;
    }

    public Luban launch() {
        Preconditions.checkNotNull(this.mFile, "the image file cannot be null, please call .load() before this method!");
        OnCompressListener onCompressListener = this.compressListener;
        if (onCompressListener != null) {
            onCompressListener.onStart();
        }
        int i = this.gear;
        if (i == 1) {
            Observable.just(this.mFile).map(new Function<String, File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.4
                @Override // io.reactivex.functions.Function
                /* renamed from: apply  reason: avoid collision after fix types in other method */
                public File mo401apply(String s) throws Exception {
                    File file = new File(s);
                    return Luban.this.firstCompress(file);
                }
            }).subscribeOn(Schedulers.m27io()).observeOn(AndroidSchedulers.mainThread()).doOnError(new Consumer<Throwable>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.3
                @Override // io.reactivex.functions.Consumer
                public void accept(Throwable throwable) throws Exception {
                    if (Luban.this.compressListener != null) {
                        Luban.this.compressListener.onError(throwable);
                    }
                }
            }).onErrorResumeNext(Observable.empty()).filter(new Predicate<File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.2
                @Override // io.reactivex.functions.Predicate
                public boolean test(File file) throws Exception {
                    return file != null;
                }
            }).subscribe(new Consumer<File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.1
                @Override // io.reactivex.functions.Consumer
                public void accept(File file) throws Exception {
                    if (Luban.this.compressListener != null) {
                        Luban.this.compressListener.onSuccess(file);
                    }
                }
            });
        } else if (i == 3) {
            Observable.just(this.mFile).map(new Function<String, File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.8
                @Override // io.reactivex.functions.Function
                /* renamed from: apply  reason: avoid collision after fix types in other method */
                public File mo401apply(String s) throws Exception {
                    File file = new File(s);
                    return Luban.this.thirdCompress(file);
                }
            }).subscribeOn(Schedulers.m27io()).observeOn(AndroidSchedulers.mainThread()).doOnError(new Consumer<Throwable>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.7
                @Override // io.reactivex.functions.Consumer
                public void accept(Throwable throwable) throws Exception {
                    if (Luban.this.compressListener != null) {
                        Luban.this.compressListener.onError(throwable);
                    }
                }
            }).onErrorResumeNext(Observable.empty()).filter(new Predicate<File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.6
                @Override // io.reactivex.functions.Predicate
                public boolean test(File file) throws Exception {
                    return file != null;
                }
            }).subscribe(new Consumer<File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.5
                @Override // io.reactivex.functions.Consumer
                public void accept(File file) throws Exception {
                    if (Luban.this.compressListener != null) {
                        Luban.this.compressListener.onSuccess(file);
                    }
                }
            });
        }
        return this;
    }

    public Luban load(String file) {
        this.mFile = file;
        return this;
    }

    public Luban load(List<String> listFile) {
        this.mListFile = listFile;
        return this;
    }

    public Luban setCompressListener(OnCompressListener listener) {
        this.compressListener = listener;
        return this;
    }

    public Luban putGear(int gear) {
        this.gear = gear;
        return this;
    }

    public Luban setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public Observable<File> asObservable() {
        int i = this.gear;
        if (i == 1) {
            return Observable.just(this.mFile).map(new Function<String, File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.9
                @Override // io.reactivex.functions.Function
                /* renamed from: apply  reason: avoid collision after fix types in other method */
                public File mo401apply(String s) throws Exception {
                    if (TextUtils.isEmpty(s) || s.contains("http")) {
                        return null;
                    }
                    File file = new File(s);
                    if (!file.exists()) {
                        return null;
                    }
                    return Luban.this.firstCompress(file);
                }
            });
        }
        if (i == 3) {
            return Observable.just(this.mFile).map(new Function<String, File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.10
                @Override // io.reactivex.functions.Function
                /* renamed from: apply  reason: avoid collision after fix types in other method */
                public File mo401apply(String s) throws Exception {
                    if (TextUtils.isEmpty(s) || s.contains("http")) {
                        return null;
                    }
                    File file = new File(s);
                    if (!file.exists()) {
                        return null;
                    }
                    return Luban.this.thirdCompress(file);
                }
            });
        }
        return Observable.empty();
    }

    public Observable<File> asListObservable() {
        int i = this.gear;
        if (i == 1) {
            return Observable.fromIterable(this.mListFile).map(new Function<String, File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.11
                @Override // io.reactivex.functions.Function
                /* renamed from: apply  reason: avoid collision after fix types in other method */
                public File mo401apply(String s) throws Exception {
                    if (TextUtils.isEmpty(s)) {
                        return null;
                    }
                    File file = new File(s);
                    if (!file.exists()) {
                        return null;
                    }
                    return Luban.this.firstCompress(file);
                }
            });
        }
        if (i == 3) {
            return Observable.fromIterable(this.mListFile).map(new Function<String, File>() { // from class: me.goldze.mvvmhabit.utils.compression.Luban.12
                @Override // io.reactivex.functions.Function
                /* renamed from: apply  reason: avoid collision after fix types in other method */
                public File mo401apply(String s) throws Exception {
                    if (TextUtils.isEmpty(s)) {
                        return null;
                    }
                    File file = new File(s);
                    if (!file.exists()) {
                        return null;
                    }
                    return Luban.this.thirdCompress(file);
                }
            });
        }
        return Observable.empty();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public File thirdCompress(@NonNull File file) {
        int thumbW;
        double size;
        int thumbH;
        StringBuilder sb = new StringBuilder();
        sb.append(this.mCacheDir.getAbsolutePath());
        sb.append(File.separator);
        sb.append(TextUtils.isEmpty(this.filename) ? Long.valueOf(System.currentTimeMillis()) : this.filename);
        sb.append(".jpg");
        String thumb = sb.toString();
        String filePath = file.getAbsolutePath();
        int angle = getImageSpinAngle(filePath);
        int width = getImageSize(filePath)[0];
        int height = getImageSize(filePath)[1];
        int thumbW2 = width % 2 == 1 ? width + 1 : width;
        int thumbH2 = height % 2 == 1 ? height + 1 : height;
        int width2 = thumbW2 > thumbH2 ? thumbH2 : thumbW2;
        int height2 = thumbW2 > thumbH2 ? thumbW2 : thumbH2;
        double d = width2;
        double d2 = height2;
        Double.isNaN(d);
        Double.isNaN(d2);
        double scale = d / d2;
        double size2 = 100.0d;
        if (scale <= 1.0d && scale > 0.5625d) {
            double d3 = 60.0d;
            int thumbW3 = thumbW2;
            if (height2 >= 1664) {
                if (height2 < 1664 || height2 >= 4990) {
                    if (height2 >= 4990 && height2 < 10240) {
                        int thumbW4 = width2 / 4;
                        int thumbH3 = height2 / 4;
                        double d4 = thumbW4 * thumbH3;
                        double pow = Math.pow(2560.0d, 2.0d);
                        Double.isNaN(d4);
                        double size3 = (d4 / pow) * 300.0d;
                        if (size3 >= 100.0d) {
                            size2 = size3;
                        }
                        thumbW = thumbW4;
                        size = size2;
                        thumbH = thumbH3;
                    } else {
                        int multiple = height2 / 1280 == 0 ? 1 : height2 / 1280;
                        int thumbW5 = width2 / multiple;
                        int thumbH4 = height2 / multiple;
                        double d5 = thumbW5 * thumbH4;
                        thumbH = thumbH4;
                        double pow2 = Math.pow(2560.0d, 2.0d);
                        Double.isNaN(d5);
                        double size4 = (d5 / pow2) * 300.0d;
                        if (size4 >= 100.0d) {
                            size2 = size4;
                        }
                        thumbW = thumbW5;
                        size = size2;
                    }
                } else {
                    int thumbW6 = width2 / 2;
                    int thumbH5 = height2 / 2;
                    double d6 = thumbW6 * thumbH5;
                    double pow3 = Math.pow(2495.0d, 2.0d);
                    Double.isNaN(d6);
                    double size5 = (d6 / pow3) * 300.0d;
                    if (size5 >= 60.0d) {
                        d3 = size5;
                    }
                    double size6 = d3;
                    thumbW = thumbW6;
                    thumbH = thumbH5;
                    size = size6;
                }
            } else if (file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID < 150) {
                return file;
            } else {
                double d7 = width2 * height2;
                double pow4 = Math.pow(1664.0d, 2.0d);
                Double.isNaN(d7);
                double size7 = (d7 / pow4) * 150.0d;
                if (size7 >= 60.0d) {
                    d3 = size7;
                }
                double size8 = d3;
                thumbH = thumbH2;
                thumbW = thumbW3;
                size = size8;
            }
        } else if (scale <= 0.5625d && scale > 0.5d) {
            if (height2 < 1280 && file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID < 200) {
                return file;
            }
            int multiple2 = height2 / 1280 == 0 ? 1 : height2 / 1280;
            int thumbW7 = width2 / multiple2;
            int thumbH6 = height2 / multiple2;
            double d8 = thumbW7 * thumbH6;
            Double.isNaN(d8);
            double size9 = (d8 / 3686400.0d) * 400.0d;
            if (size9 >= 100.0d) {
                size2 = size9;
            }
            thumbW = thumbW7;
            thumbH = thumbH6;
            size = size2;
        } else {
            double d9 = height2;
            Double.isNaN(d9);
            int multiple3 = (int) Math.ceil(d9 / (1280.0d / scale));
            int thumbW8 = width2 / multiple3;
            int thumbH7 = height2 / multiple3;
            double d10 = thumbW8 * thumbH7;
            Double.isNaN(d10);
            double size10 = 500.0d * (d10 / ((1280.0d / scale) * 1280.0d));
            if (size10 >= 100.0d) {
                size2 = size10;
            }
            thumbW = thumbW8;
            size = size2;
            thumbH = thumbH7;
        }
        return compress(filePath, thumb, thumbW, thumbH, angle, (long) size);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public File firstCompress(@NonNull File file) {
        long size;
        int width;
        int height;
        int i;
        int i2;
        int i3;
        int i4;
        String filePath = file.getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(this.mCacheDir.getAbsolutePath());
        sb.append(File.separator);
        sb.append(TextUtils.isEmpty(this.filename) ? Long.valueOf(System.currentTimeMillis()) : this.filename);
        sb.append(".jpg");
        String thumbFilePath = sb.toString();
        long size2 = 0;
        long maxSize = file.length() / 5;
        int angle = getImageSpinAngle(filePath);
        int[] imgSize = getImageSize(filePath);
        int width2 = 0;
        int height2 = 0;
        if (imgSize[0] <= imgSize[1]) {
            double d = imgSize[0];
            double d2 = imgSize[1];
            Double.isNaN(d);
            Double.isNaN(d2);
            double scale = d / d2;
            if (scale <= 1.0d && scale > 0.5625d) {
                if (imgSize[0] > 1280) {
                    i4 = 1280;
                } else {
                    i4 = imgSize[0];
                }
                width2 = i4;
                height2 = (imgSize[1] * width2) / imgSize[0];
                size2 = 60;
            } else if (scale <= 0.5625d) {
                if (imgSize[1] > 720) {
                    i3 = 720;
                } else {
                    i3 = imgSize[1];
                }
                height2 = i3;
                width2 = (imgSize[0] * height2) / imgSize[1];
                size2 = maxSize;
            }
            size = size2;
            width = width2;
            height = height2;
        } else {
            double d3 = imgSize[1];
            double d4 = imgSize[0];
            Double.isNaN(d3);
            Double.isNaN(d4);
            double scale2 = d3 / d4;
            if (scale2 <= 1.0d && scale2 > 0.5625d) {
                if (imgSize[1] > 1280) {
                    i2 = 1280;
                } else {
                    i2 = imgSize[1];
                }
                int height3 = i2;
                int width3 = (imgSize[0] * height3) / imgSize[1];
                long size3 = 60;
                size = size3;
                height = height3;
                width = width3;
            } else if (scale2 <= 0.5625d) {
                if (imgSize[0] > 720) {
                    i = 720;
                } else {
                    i = imgSize[0];
                }
                int width4 = i;
                int height4 = (imgSize[1] * width4) / imgSize[0];
                size = maxSize;
                width = width4;
                height = height4;
            } else {
                size = 0;
                width = 0;
                height = 0;
            }
        }
        return compress(filePath, thumbFilePath, width, height, angle, size);
    }

    public int[] getImageSize(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);
        int[] res = {options.outWidth, options.outHeight};
        return res;
    }

    private Bitmap compress(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;
        if (outH > height || outW > width) {
            int halfH = outH / 2;
            int halfW = outW / 2;
            while (halfH / inSampleSize > height && halfW / inSampleSize > width) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        int heightRatio = (int) Math.ceil(options.outHeight / height);
        int widthRatio = (int) Math.ceil(options.outWidth / width);
        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    private int getImageSpinAngle(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt("Orientation", 1);
            if (orientation == 3) {
                return 180;
            }
            if (orientation == 6) {
                return 90;
            }
            if (orientation != 8) {
                return 0;
            }
            return 270;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private File compress(String largeImagePath, String thumbFilePath, int width, int height, int angle, long size) {
        Bitmap thbBitmap = compress(largeImagePath, width, height);
        return saveImage(thumbFilePath, rotatingImage(angle, thbBitmap), size);
    }

    private static Bitmap rotatingImage(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private File saveImage(String filePath, Bitmap bitmap, long size) {
        Preconditions.checkNotNull(bitmap, "smartcitybitmap cannot be null");
        File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));
        if (result.exists() || result.mkdirs()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int options = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            while (stream.toByteArray().length / 1024 > size && options > 6) {
                stream.reset();
                options -= 6;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
            }
            try {
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(stream.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new File(filePath);
        }
        return null;
    }
}