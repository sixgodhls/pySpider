package me.goldze.mvvmhabit.utils;

import android.annotation.TargetApi;
import android.os.Environment;
import android.os.StatFs;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public final class SDCardUtils {
    private SDCardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isSDCardEnable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static String getSDCardPath() {
        Closeable[] closeableArr;
        if (!isSDCardEnable()) {
            return null;
        }
        Runtime run = Runtime.getRuntime();
        BufferedReader bufferedReader = null;
        try {
            try {
                Process p = run.exec("cat /proc/mounts");
                bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
                while (true) {
                    String lineStr = bufferedReader.readLine();
                    if (lineStr != null) {
                        if (lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {
                            String[] strArray = lineStr.split(" ");
                            if (strArray.length >= 5) {
                                String str = strArray[1].replace("/.android_secure", "") + File.separator;
                                CloseUtils.closeIO(bufferedReader);
                                return str;
                            }
                        }
                        if (p.waitFor() != 0 && p.exitValue() == 1) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                closeableArr = new Closeable[]{bufferedReader};
            } catch (Exception e) {
                e.printStackTrace();
                closeableArr = new Closeable[]{bufferedReader};
            }
            CloseUtils.closeIO(closeableArr);
            return Environment.getExternalStorageDirectory().getPath() + File.separator;
        } catch (Throwable th) {
            CloseUtils.closeIO(bufferedReader);
            throw th;
        }
    }

    public static String getDataPath() {
        if (!isSDCardEnable()) {
            return null;
        }
        return Environment.getExternalStorageDirectory().getPath() + File.separator + "data" + File.separator;
    }

    @TargetApi(18)
    public static String getFreeSpace() {
        if (!isSDCardEnable()) {
            return null;
        }
        StatFs stat = new StatFs(getSDCardPath());
        long availableBlocks = stat.getAvailableBlocksLong();
        long blockSize = stat.getBlockSizeLong();
        return ConvertUtils.byte2FitMemorySize(availableBlocks * blockSize);
    }

    @TargetApi(18)
    public static String getSDCardInfo() {
        if (!isSDCardEnable()) {
            return null;
        }
        SDCardInfo sd = new SDCardInfo();
        sd.isExist = true;
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        sd.totalBlocks = sf.getBlockCountLong();
        sd.blockByteSize = sf.getBlockSizeLong();
        sd.availableBlocks = sf.getAvailableBlocksLong();
        sd.availableBytes = sf.getAvailableBytes();
        sd.freeBlocks = sf.getFreeBlocksLong();
        sd.freeBytes = sf.getFreeBytes();
        sd.totalBytes = sf.getTotalBytes();
        return sd.toString();
    }

    /* loaded from: classes.dex */
    public static class SDCardInfo {
        long availableBlocks;
        long availableBytes;
        long blockByteSize;
        long freeBlocks;
        long freeBytes;
        boolean isExist;
        long totalBlocks;
        long totalBytes;

        public String toString() {
            return "isExist=" + this.isExist + "\ntotalBlocks=" + this.totalBlocks + "\nfreeBlocks=" + this.freeBlocks + "\navailableBlocks=" + this.availableBlocks + "\nblockByteSize=" + this.blockByteSize + "\ntotalBytes=" + this.totalBytes + "\nfreeBytes=" + this.freeBytes + "\navailableBytes=" + this.availableBytes;
        }
    }
}