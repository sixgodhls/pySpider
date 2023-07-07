package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.os.Process;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class TypefaceCompatUtil {
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";

    private TypefaceCompatUtil() {
    }

    @Nullable
    public static File getTempFile(Context context) {
        String prefix = CACHE_FILE_PREFIX + Process.myPid() + "-" + Process.myTid() + "-";
        for (int i = 0; i < 100; i++) {
            File file = new File(context.getCacheDir(), prefix + i);
            if (file.createNewFile()) {
                return file;
            }
        }
        return null;
    }

    @RequiresApi(19)
    @Nullable
    private static ByteBuffer mmap(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileChannel channel = fis.getChannel();
            long size = channel.size();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, size);
            fis.close();
            return map;
        } catch (IOException e) {
            return null;
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Found unreachable blocks
        	at jadx.core.dex.visitors.blocks.DominatorTree.sortBlocks(DominatorTree.java:40)
        	at jadx.core.dex.visitors.blocks.DominatorTree.compute(DominatorTree.java:25)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.computeDominators(BlockProcessor.java:203)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:46)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:40)
        */
    @android.support.annotation.RequiresApi(19)
    @android.support.annotation.Nullable
    public static java.nio.ByteBuffer mmap(android.content.Context r11, android.os.CancellationSignal r12, android.net.Uri r13) {
        /*
            android.content.ContentResolver r0 = r11.getContentResolver()
            r1 = 0
            java.lang.String r2 = "r"
            android.os.ParcelFileDescriptor r2 = r0.openFileDescriptor(r13, r2, r12)     // Catch: java.io.IOException -> L7f
            if (r2 != 0) goto L14
        Le:
            if (r2 == 0) goto L13
            r2.close()     // Catch: java.io.IOException -> L7f
        L13:
            return r1
        L14:
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L60
            java.io.FileDescriptor r4 = r2.getFileDescriptor()     // Catch: java.lang.Throwable -> L60
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L60
            java.nio.channels.FileChannel r4 = r3.getChannel()     // Catch: java.lang.Throwable -> L40
            long r8 = r4.size()     // Catch: java.lang.Throwable -> L40
            java.nio.channels.FileChannel$MapMode r5 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch: java.lang.Throwable -> L40
            r6 = 0
            java.nio.MappedByteBuffer r5 = r4.map(r5, r6, r8)     // Catch: java.lang.Throwable -> L40
            r3.close()     // Catch: java.lang.Throwable -> L60
            if (r2 == 0) goto L3a
        L37:
            r2.close()     // Catch: java.io.IOException -> L7f
        L3a:
            return r5
        L3d:
            r4 = move-exception
            r5 = r1
            goto L47
        L40:
            r4 = move-exception
            throw r4     // Catch: java.lang.Throwable -> L43
        L43:
            r5 = move-exception
            r10 = r5
            r5 = r4
            r4 = r10
        L47:
            if (r5 == 0) goto L56
        L4c:
            r3.close()     // Catch: java.lang.Throwable -> L50
            goto L5a
        L50:
            r6 = move-exception
            r5.addSuppressed(r6)     // Catch: java.lang.Throwable -> L60
            goto L5a
        L56:
            r3.close()     // Catch: java.lang.Throwable -> L60
        L5a:
            throw r4     // Catch: java.lang.Throwable -> L60
        L5d:
            r3 = move-exception
            r4 = r1
            goto L67
        L60:
            r3 = move-exception
            throw r3     // Catch: java.lang.Throwable -> L63
        L63:
            r4 = move-exception
            r10 = r4
            r4 = r3
            r3 = r10
        L67:
            if (r2 == 0) goto L7c
        L6b:
            if (r4 == 0) goto L78
        L6e:
            r2.close()     // Catch: java.lang.Throwable -> L72
            goto L7c
        L72:
            r5 = move-exception
            r4.addSuppressed(r5)     // Catch: java.io.IOException -> L7f
            goto L7c
        L78:
            r2.close()     // Catch: java.io.IOException -> L7f
        L7c:
            throw r3     // Catch: java.io.IOException -> L7f
        L7f:
            r2 = move-exception
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(android.content.Context, android.os.CancellationSignal, android.net.Uri):java.nio.ByteBuffer");
    }

    @RequiresApi(19)
    @Nullable
    public static ByteBuffer copyToDirectBuffer(Context context, Resources res, int id) {
        File tmpFile = getTempFile(context);
        ByteBuffer byteBuffer = null;
        if (tmpFile == null) {
            return null;
        }
        try {
            if (copyToFile(tmpFile, res, id)) {
                byteBuffer = mmap(tmpFile);
            }
            return byteBuffer;
        } finally {
            tmpFile.delete();
        }
    }

    public static boolean copyToFile(File file, InputStream is) {
        FileOutputStream os = null;
        StrictMode.ThreadPolicy old = StrictMode.allowThreadDiskWrites();
        try {
            os = new FileOutputStream(file, false);
            byte[] buffer = new byte[1024];
            while (true) {
                int readLen = is.read(buffer);
                if (readLen == -1) {
                    return true;
                }
                os.write(buffer, 0, readLen);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error copying resource contents to temp file: " + e.getMessage());
            return false;
        } finally {
            closeQuietly(os);
            StrictMode.setThreadPolicy(old);
        }
    }

    public static boolean copyToFile(File file, Resources res, int id) {
        InputStream is = null;
        try {
            is = res.openRawResource(id);
            return copyToFile(file, is);
        } finally {
            closeQuietly(is);
        }
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }
}
