package me.goldze.mvvmhabit.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import me.goldze.mvvmhabit.utils.constant.TimeConstants;

/* loaded from: classes.dex */
public final class ConvertUtils {
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private ConvertUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String bytes2HexString(byte[] bytes) {
        int len;
        if (bytes == null || (len = bytes.length) <= 0) {
            return null;
        }
        char[] ret = new char[len << 1];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int j2 = j + 1;
            char[] cArr = hexDigits;
            ret[j] = cArr[(bytes[i] >>> 4) & 15];
            j = j2 + 1;
            ret[j2] = cArr[bytes[i] & 15];
        }
        return new String(ret);
    }

    public static byte[] hexString2Bytes(String hexString) {
        if (isSpace(hexString)) {
            return null;
        }
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len++;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) ((hex2Dec(hexBytes[i]) << 4) | hex2Dec(hexBytes[i + 1]));
        }
        return ret;
    }

    private static int hex2Dec(char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        }
        if (hexChar >= 'A' && hexChar <= 'F') {
            return (hexChar - 'A') + 10;
        }
        throw new IllegalArgumentException();
    }

    public static byte[] chars2Bytes(char[] chars) {
        if (chars == null || chars.length <= 0) {
            return null;
        }
        int len = chars.length;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public static char[] bytes2Chars(byte[] bytes) {
        int len;
        if (bytes == null || (len = bytes.length) <= 0) {
            return null;
        }
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char) (bytes[i] & 255);
        }
        return chars;
    }

    public static long memorySize2Byte(long memorySize, int unit) {
        if (memorySize < 0) {
            return -1L;
        }
        return unit * memorySize;
    }

    public static double byte2MemorySize(long byteNum, int unit) {
        if (byteNum < 0) {
            return -1.0d;
        }
        double d = byteNum;
        double d2 = unit;
        Double.isNaN(d);
        Double.isNaN(d2);
        return d / d2;
    }

    @SuppressLint({"DefaultLocale"})
    public static String byte2FitMemorySize(long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        }
        if (byteNum < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            double d = byteNum;
            Double.isNaN(d);
            return String.format("%.3fB", Double.valueOf(d + 5.0E-4d));
        } else if (byteNum < PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
            double d2 = byteNum;
            Double.isNaN(d2);
            return String.format("%.3fKB", Double.valueOf((d2 / 1024.0d) + 5.0E-4d));
        } else if (byteNum < 1073741824) {
            double d3 = byteNum;
            Double.isNaN(d3);
            return String.format("%.3fMB", Double.valueOf((d3 / 1048576.0d) + 5.0E-4d));
        } else {
            double d4 = byteNum;
            Double.isNaN(d4);
            return String.format("%.3fGB", Double.valueOf((d4 / 1.073741824E9d) + 5.0E-4d));
        }
    }

    public static long timeSpan2Millis(long timeSpan, int unit) {
        return unit * timeSpan;
    }

    public static long millis2TimeSpan(long millis, int unit) {
        return millis / unit;
    }

    @SuppressLint({"DefaultLocale"})
    public static String millis2FitTimeSpan(long millis, int precision) {
        if (millis <= 0 || precision <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        int[] unitLen = {TimeConstants.DAY, TimeConstants.HOUR, TimeConstants.MIN, 1000, 1};
        int precision2 = Math.min(precision, 5);
        for (int i = 0; i < precision2; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                millis -= unitLen[i] * mode;
                sb.append(mode);
                sb.append(units[i]);
            }
        }
        return sb.toString();
    }

    public static String bytes2Bits(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            for (int j = 7; j >= 0; j--) {
                sb.append(((aByte >> j) & 1) == 0 ? '0' : '1');
            }
        }
        return sb.toString();
    }

    public static byte[] bits2Bytes(String bits) {
        int lenMod = bits.length() % 8;
        int byteLen = bits.length() / 8;
        if (lenMod != 0) {
            for (int i = lenMod; i < 8; i++) {
                bits = "0" + bits;
            }
            byteLen++;
        }
        byte[] bytes = new byte[byteLen];
        for (int i2 = 0; i2 < byteLen; i2++) {
            for (int j = 0; j < 8; j++) {
                bytes[i2] = (byte) (bytes[i2] << 1);
                bytes[i2] = (byte) (bytes[i2] | (bits.charAt((i2 * 8) + j) - '0'));
            }
        }
        return bytes;
    }

    public static ByteArrayOutputStream input2OutputStream(InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                while (true) {
                    int len = is.read(b, 0, 1024);
                    if (len == -1) {
                        CloseUtils.closeIO(is);
                        return os;
                    }
                    os.write(b, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
                CloseUtils.closeIO(is);
                return null;
            }
        } catch (Throwable th) {
            CloseUtils.closeIO(is);
            throw th;
        }
    }

    public ByteArrayInputStream output2InputStream(OutputStream out) {
        if (out == null) {
            return null;
        }
        return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
    }

    public static byte[] inputStream2Bytes(InputStream is) {
        if (is == null) {
            return null;
        }
        return input2OutputStream(is).toByteArray();
    }

    public static InputStream bytes2InputStream(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    public static byte[] outputStream2Bytes(OutputStream out) {
        if (out == null) {
            return null;
        }
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    public static OutputStream bytes2OutputStream(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            try {
                os = new ByteArrayOutputStream();
                os.write(bytes);
                CloseUtils.closeIO(os);
                return os;
            } catch (IOException e) {
                e.printStackTrace();
                CloseUtils.closeIO(os);
                return null;
            }
        } catch (Throwable th) {
            CloseUtils.closeIO(os);
            throw th;
        }
    }

    public static String inputStream2String(InputStream is, String charsetName) {
        if (is == null || isSpace(charsetName)) {
            return null;
        }
        try {
            return new String(inputStream2Bytes(is), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream string2InputStream(String string, String charsetName) {
        if (string == null || isSpace(charsetName)) {
            return null;
        }
        try {
            return new ByteArrayInputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String outputStream2String(OutputStream out, String charsetName) {
        if (out == null || isSpace(charsetName)) {
            return null;
        }
        try {
            return new String(outputStream2Bytes(out), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OutputStream string2OutputStream(String string, String charsetName) {
        if (string == null || isSpace(charsetName)) {
            return null;
        }
        try {
            return bytes2OutputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(Utils.getContext().getResources(), bitmap);
    }

    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format) {
        if (drawable == null) {
            return null;
        }
        return bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    public static Drawable bytes2Drawable(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return bitmap2Drawable(bytes2Bitmap(bytes));
    }

    public static Bitmap view2Bitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(-1);
        }
        view.draw(canvas);
        return ret;
    }

    public static int dp2px(float dpValue) {
        float scale = Utils.getContext().getResources().getDisplayMetrics().density;
        return (int) ((dpValue * scale) + 0.5f);
    }

    public static int px2dp(float pxValue) {
        float scale = Utils.getContext().getResources().getDisplayMetrics().density;
        return (int) ((pxValue / scale) + 0.5f);
    }

    public static int sp2px(float spValue) {
        float fontScale = Utils.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) ((spValue * fontScale) + 0.5f);
    }

    public static int px2sp(float pxValue) {
        float fontScale = Utils.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) ((pxValue / fontScale) + 0.5f);
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
