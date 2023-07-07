package android.support.p000v4.print;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* renamed from: android.support.v4.print.PrintHelper */
/* loaded from: classes.dex */
public final class PrintHelper {
    @SuppressLint({"InlinedApi"})
    public static final int COLOR_MODE_COLOR = 2;
    @SuppressLint({"InlinedApi"})
    public static final int COLOR_MODE_MONOCHROME = 1;
    static final boolean IS_MIN_MARGINS_HANDLING_CORRECT;
    private static final String LOG_TAG = "PrintHelper";
    private static final int MAX_PRINT_SIZE = 3500;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int ORIENTATION_PORTRAIT = 2;
    static final boolean PRINT_ACTIVITY_RESPECTS_ORIENTATION;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    final Context mContext;
    BitmapFactory.Options mDecodeOptions = null;
    final Object mLock = new Object();
    int mScaleMode = 2;
    int mColorMode = 2;
    int mOrientation = 1;

    /* renamed from: android.support.v4.print.PrintHelper$OnPrintFinishCallback */
    /* loaded from: classes.dex */
    public interface OnPrintFinishCallback {
        void onFinish();
    }

    static {
        boolean z = false;
        PRINT_ACTIVITY_RESPECTS_ORIENTATION = Build.VERSION.SDK_INT < 20 || Build.VERSION.SDK_INT > 23;
        if (Build.VERSION.SDK_INT != 23) {
            z = true;
        }
        IS_MIN_MARGINS_HANDLING_CORRECT = z;
    }

    public static boolean systemSupportsPrint() {
        return Build.VERSION.SDK_INT >= 19;
    }

    public PrintHelper(@NonNull Context context) {
        this.mContext = context;
    }

    public void setScaleMode(int scaleMode) {
        this.mScaleMode = scaleMode;
    }

    public int getScaleMode() {
        return this.mScaleMode;
    }

    public void setColorMode(int colorMode) {
        this.mColorMode = colorMode;
    }

    public int getColorMode() {
        return this.mColorMode;
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public int getOrientation() {
        if (Build.VERSION.SDK_INT >= 19 && this.mOrientation == 0) {
            return 1;
        }
        return this.mOrientation;
    }

    public void printBitmap(@NonNull String jobName, @NonNull Bitmap bitmap) {
        printBitmap(jobName, bitmap, (OnPrintFinishCallback) null);
    }

    public void printBitmap(@NonNull String jobName, @NonNull Bitmap bitmap, @Nullable OnPrintFinishCallback callback) {
        PrintAttributes.MediaSize mediaSize;
        if (Build.VERSION.SDK_INT < 19 || bitmap == null) {
            return;
        }
        PrintManager printManager = (PrintManager) this.mContext.getSystemService("print");
        if (isPortrait(bitmap)) {
            mediaSize = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;
        } else {
            mediaSize = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE;
        }
        PrintAttributes attr = new PrintAttributes.Builder().setMediaSize(mediaSize).setColorMode(this.mColorMode).build();
        printManager.print(jobName, new PrintBitmapAdapter(jobName, this.mScaleMode, bitmap, callback), attr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @RequiresApi(19)
    /* renamed from: android.support.v4.print.PrintHelper$PrintBitmapAdapter */
    /* loaded from: classes.dex */
    public class PrintBitmapAdapter extends PrintDocumentAdapter {
        private PrintAttributes mAttributes;
        private final Bitmap mBitmap;
        private final OnPrintFinishCallback mCallback;
        private final int mFittingMode;
        private final String mJobName;

        PrintBitmapAdapter(String jobName, int fittingMode, Bitmap bitmap, OnPrintFinishCallback callback) {
            this.mJobName = jobName;
            this.mFittingMode = fittingMode;
            this.mBitmap = bitmap;
            this.mCallback = callback;
        }

        @Override // android.print.PrintDocumentAdapter
        public void onLayout(PrintAttributes oldPrintAttributes, PrintAttributes newPrintAttributes, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback layoutResultCallback, Bundle bundle) {
            this.mAttributes = newPrintAttributes;
            PrintDocumentInfo info = new PrintDocumentInfo.Builder(this.mJobName).setContentType(1).setPageCount(1).build();
            boolean changed = true ^ newPrintAttributes.equals(oldPrintAttributes);
            layoutResultCallback.onLayoutFinished(info, changed);
        }

        @Override // android.print.PrintDocumentAdapter
        public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor fileDescriptor, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
            PrintHelper.this.writeBitmap(this.mAttributes, this.mFittingMode, this.mBitmap, fileDescriptor, cancellationSignal, writeResultCallback);
        }

        @Override // android.print.PrintDocumentAdapter
        public void onFinish() {
            OnPrintFinishCallback onPrintFinishCallback = this.mCallback;
            if (onPrintFinishCallback != null) {
                onPrintFinishCallback.onFinish();
            }
        }
    }

    public void printBitmap(@NonNull String jobName, @NonNull Uri imageFile) throws FileNotFoundException {
        printBitmap(jobName, imageFile, (OnPrintFinishCallback) null);
    }

    public void printBitmap(@NonNull String jobName, @NonNull Uri imageFile, @Nullable OnPrintFinishCallback callback) throws FileNotFoundException {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        PrintDocumentAdapter printDocumentAdapter = new PrintUriAdapter(jobName, imageFile, callback, this.mScaleMode);
        PrintManager printManager = (PrintManager) this.mContext.getSystemService("print");
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(this.mColorMode);
        int i = this.mOrientation;
        if (i == 1 || i == 0) {
            builder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
        } else if (i == 2) {
            builder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
        }
        PrintAttributes attr = builder.build();
        printManager.print(jobName, printDocumentAdapter, attr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @RequiresApi(19)
    /* renamed from: android.support.v4.print.PrintHelper$PrintUriAdapter */
    /* loaded from: classes.dex */
    public class PrintUriAdapter extends PrintDocumentAdapter {
        PrintAttributes mAttributes;
        Bitmap mBitmap = null;
        final OnPrintFinishCallback mCallback;
        final int mFittingMode;
        final Uri mImageFile;
        final String mJobName;
        AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;

        PrintUriAdapter(String jobName, Uri imageFile, OnPrintFinishCallback callback, int fittingMode) {
            this.mJobName = jobName;
            this.mImageFile = imageFile;
            this.mCallback = callback;
            this.mFittingMode = fittingMode;
        }

        /* JADX WARN: Type inference failed for: r0v3, types: [android.support.v4.print.PrintHelper$PrintUriAdapter$1] */
        @Override // android.print.PrintDocumentAdapter
        public void onLayout(final PrintAttributes oldPrintAttributes, final PrintAttributes newPrintAttributes, final CancellationSignal cancellationSignal, final PrintDocumentAdapter.LayoutResultCallback layoutResultCallback, Bundle bundle) {
            synchronized (this) {
                this.mAttributes = newPrintAttributes;
            }
            if (cancellationSignal.isCanceled()) {
                layoutResultCallback.onLayoutCancelled();
            } else if (this.mBitmap != null) {
                PrintDocumentInfo info = new PrintDocumentInfo.Builder(this.mJobName).setContentType(1).setPageCount(1).build();
                boolean changed = true ^ newPrintAttributes.equals(oldPrintAttributes);
                layoutResultCallback.onLayoutFinished(info, changed);
            } else {
                this.mLoadBitmap = new AsyncTask<Uri, Boolean, Bitmap>() { // from class: android.support.v4.print.PrintHelper.PrintUriAdapter.1
                    @Override // android.os.AsyncTask
                    protected void onPreExecute() {
                        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: android.support.v4.print.PrintHelper.PrintUriAdapter.1.1
                            @Override // android.os.CancellationSignal.OnCancelListener
                            public void onCancel() {
                                PrintUriAdapter.this.cancelLoad();
                                cancel(false);
                            }
                        });
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Bitmap doInBackground(Uri... uris) {
                        try {
                            return PrintHelper.this.loadConstrainedBitmap(PrintUriAdapter.this.mImageFile);
                        } catch (FileNotFoundException e) {
                            return null;
                        }
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0049 -> B:19:0x004a). Please submit an issue!!! */
                    @Override // android.os.AsyncTask
                    public void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute((AsyncTaskC03671) bitmap);
                        if (bitmap != null && (!PrintHelper.PRINT_ACTIVITY_RESPECTS_ORIENTATION || PrintHelper.this.mOrientation == 0)) {
                            synchronized (this) {
                                try {
                                    PrintAttributes.MediaSize mediaSize = PrintUriAdapter.this.mAttributes.getMediaSize();
                                    try {
                                        if (mediaSize != null && mediaSize.isPortrait() != PrintHelper.isPortrait(bitmap)) {
                                            Matrix rotation = new Matrix();
                                            rotation.postRotate(90.0f);
                                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotation, true);
                                        }
                                    } catch (Throwable th) {
                                        th = th;
                                        throw th;
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                }
                            }
                        }
                        PrintUriAdapter printUriAdapter = PrintUriAdapter.this;
                        printUriAdapter.mBitmap = bitmap;
                        if (bitmap == null) {
                            layoutResultCallback.onLayoutFailed(null);
                        } else {
                            PrintDocumentInfo info2 = new PrintDocumentInfo.Builder(printUriAdapter.mJobName).setContentType(1).setPageCount(1).build();
                            boolean changed2 = true ^ newPrintAttributes.equals(oldPrintAttributes);
                            layoutResultCallback.onLayoutFinished(info2, changed2);
                        }
                        PrintUriAdapter.this.mLoadBitmap = null;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public void onCancelled(Bitmap result) {
                        layoutResultCallback.onLayoutCancelled();
                        PrintUriAdapter.this.mLoadBitmap = null;
                    }
                }.execute(new Uri[0]);
            }
        }

        void cancelLoad() {
            synchronized (PrintHelper.this.mLock) {
                if (PrintHelper.this.mDecodeOptions != null) {
                    if (Build.VERSION.SDK_INT < 24) {
                        PrintHelper.this.mDecodeOptions.requestCancelDecode();
                    }
                    PrintHelper.this.mDecodeOptions = null;
                }
            }
        }

        @Override // android.print.PrintDocumentAdapter
        public void onFinish() {
            super.onFinish();
            cancelLoad();
            AsyncTask<Uri, Boolean, Bitmap> asyncTask = this.mLoadBitmap;
            if (asyncTask != null) {
                asyncTask.cancel(true);
            }
            OnPrintFinishCallback onPrintFinishCallback = this.mCallback;
            if (onPrintFinishCallback != null) {
                onPrintFinishCallback.onFinish();
            }
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.mBitmap = null;
            }
        }

        @Override // android.print.PrintDocumentAdapter
        public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor fileDescriptor, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
            PrintHelper.this.writeBitmap(this.mAttributes, this.mFittingMode, this.mBitmap, fileDescriptor, cancellationSignal, writeResultCallback);
        }
    }

    static boolean isPortrait(Bitmap bitmap) {
        return bitmap.getWidth() <= bitmap.getHeight();
    }

    @RequiresApi(19)
    private static PrintAttributes.Builder copyAttributes(PrintAttributes other) {
        PrintAttributes.Builder b = new PrintAttributes.Builder().setMediaSize(other.getMediaSize()).setResolution(other.getResolution()).setMinMargins(other.getMinMargins());
        if (other.getColorMode() != 0) {
            b.setColorMode(other.getColorMode());
        }
        if (Build.VERSION.SDK_INT >= 23 && other.getDuplexMode() != 0) {
            b.setDuplexMode(other.getDuplexMode());
        }
        return b;
    }

    static Matrix getMatrix(int imageWidth, int imageHeight, RectF content, int fittingMode) {
        float scale;
        Matrix matrix = new Matrix();
        float scale2 = content.width() / imageWidth;
        if (fittingMode == 2) {
            scale = Math.max(scale2, content.height() / imageHeight);
        } else {
            scale = Math.min(scale2, content.height() / imageHeight);
        }
        matrix.postScale(scale, scale);
        float translateX = (content.width() - (imageWidth * scale)) / 2.0f;
        float translateY = (content.height() - (imageHeight * scale)) / 2.0f;
        matrix.postTranslate(translateX, translateY);
        return matrix;
    }

    /* JADX WARN: Type inference failed for: r11v0, types: [android.support.v4.print.PrintHelper$1] */
    @RequiresApi(19)
    void writeBitmap(final PrintAttributes attributes, final int fittingMode, final Bitmap bitmap, final ParcelFileDescriptor fileDescriptor, final CancellationSignal cancellationSignal, final PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
        PrintAttributes pdfAttributes;
        if (IS_MIN_MARGINS_HANDLING_CORRECT) {
            pdfAttributes = attributes;
        } else {
            pdfAttributes = copyAttributes(attributes).setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0)).build();
        }
        final PrintAttributes printAttributes = pdfAttributes;
        new AsyncTask<Void, Void, Throwable>() { // from class: android.support.v4.print.PrintHelper.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Throwable doInBackground(Void... params) {
                RectF contentRect;
                try {
                    if (cancellationSignal.isCanceled()) {
                        return null;
                    }
                    PrintedPdfDocument pdfDocument = new PrintedPdfDocument(PrintHelper.this.mContext, printAttributes);
                    Bitmap maybeGrayscale = PrintHelper.convertBitmapForColorMode(bitmap, printAttributes.getColorMode());
                    if (cancellationSignal.isCanceled()) {
                        return null;
                    }
                    PdfDocument.Page page = pdfDocument.startPage(1);
                    if (PrintHelper.IS_MIN_MARGINS_HANDLING_CORRECT) {
                        contentRect = new RectF(page.getInfo().getContentRect());
                    } else {
                        PrintedPdfDocument dummyDocument = new PrintedPdfDocument(PrintHelper.this.mContext, attributes);
                        PdfDocument.Page dummyPage = dummyDocument.startPage(1);
                        RectF contentRect2 = new RectF(dummyPage.getInfo().getContentRect());
                        dummyDocument.finishPage(dummyPage);
                        dummyDocument.close();
                        contentRect = contentRect2;
                    }
                    Matrix matrix = PrintHelper.getMatrix(maybeGrayscale.getWidth(), maybeGrayscale.getHeight(), contentRect, fittingMode);
                    if (!PrintHelper.IS_MIN_MARGINS_HANDLING_CORRECT) {
                        matrix.postTranslate(contentRect.left, contentRect.top);
                        page.getCanvas().clipRect(contentRect);
                    }
                    page.getCanvas().drawBitmap(maybeGrayscale, matrix, null);
                    pdfDocument.finishPage(page);
                    if (!cancellationSignal.isCanceled()) {
                        pdfDocument.writeTo(new FileOutputStream(fileDescriptor.getFileDescriptor()));
                        pdfDocument.close();
                        if (fileDescriptor != null) {
                            try {
                                fileDescriptor.close();
                            } catch (IOException e) {
                            }
                        }
                        if (maybeGrayscale != bitmap) {
                            maybeGrayscale.recycle();
                        }
                        return null;
                    }
                    pdfDocument.close();
                    if (fileDescriptor != null) {
                        try {
                            fileDescriptor.close();
                        } catch (IOException e2) {
                        }
                    }
                    if (maybeGrayscale != bitmap) {
                        maybeGrayscale.recycle();
                    }
                    return null;
                } catch (Throwable t) {
                    return t;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Throwable throwable) {
                if (cancellationSignal.isCanceled()) {
                    writeResultCallback.onWriteCancelled();
                } else if (throwable == null) {
                    writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                } else {
                    Log.e(PrintHelper.LOG_TAG, "Error writing printed content", throwable);
                    writeResultCallback.onWriteFailed(null);
                }
            }
        }.execute(new Void[0]);
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:51:0x0066 -> B:48:0x0067). Please submit an issue!!! */
    Bitmap loadConstrainedBitmap(Uri uri) throws FileNotFoundException {
        if (uri == null || this.mContext == null) {
            throw new IllegalArgumentException("bad argument to getScaledBitmap");
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        loadBitmap(uri, opt);
        int w = opt.outWidth;
        int h = opt.outHeight;
        if (w <= 0 || h <= 0) {
            return null;
        }
        int imageSide = Math.max(w, h);
        int sampleSize = 1;
        while (imageSide > MAX_PRINT_SIZE) {
            imageSide >>>= 1;
            sampleSize <<= 1;
        }
        if (sampleSize <= 0 || Math.min(w, h) / sampleSize <= 0) {
            return null;
        }
        synchronized (this.mLock) {
            try {
                this.mDecodeOptions = new BitmapFactory.Options();
                this.mDecodeOptions.inMutable = true;
                this.mDecodeOptions.inSampleSize = sampleSize;
                BitmapFactory.Options decodeOptions = this.mDecodeOptions;
                try {
                    try {
                        Bitmap loadBitmap = loadBitmap(uri, decodeOptions);
                        synchronized (this.mLock) {
                            this.mDecodeOptions = null;
                        }
                        return loadBitmap;
                    } catch (Throwable th) {
                        synchronized (this.mLock) {
                            this.mDecodeOptions = null;
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    private Bitmap loadBitmap(Uri uri, BitmapFactory.Options o) throws FileNotFoundException {
        Context context;
        if (uri == null || (context = this.mContext) == null) {
            throw new IllegalArgumentException("bad argument to loadBitmap");
        }
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is, null, o);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException t) {
                    Log.w(LOG_TAG, "close fail ", t);
                }
            }
        }
    }

    static Bitmap convertBitmapForColorMode(Bitmap original, int colorMode) {
        if (colorMode != 1) {
            return original;
        }
        Bitmap grayscale = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayscale);
        Paint p = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0f);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        p.setColorFilter(f);
        c.drawBitmap(original, 0.0f, 0.0f, p);
        c.setBitmap(null);
        return grayscale;
    }
}