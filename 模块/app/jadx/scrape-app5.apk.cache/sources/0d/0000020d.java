package me.goldze.mvvmhabit.utils;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class KLog {

    /* renamed from: A */
    private static final int f209A = 6;

    /* renamed from: D */
    private static final int f210D = 2;
    private static final String DEFAULT_MESSAGE = "execute";

    /* renamed from: E */
    private static final int f211E = 5;

    /* renamed from: I */
    private static final int f212I = 3;
    private static final int JSON = 7;
    private static final int JSON_INDENT = 4;

    /* renamed from: V */
    private static final int f213V = 1;

    /* renamed from: W */
    private static final int f214W = 4;
    private static boolean IS_SHOW_LOG = false;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void init(boolean isShowLog) {
        IS_SHOW_LOG = isShowLog;
    }

    /* renamed from: v */
    public static void m14v() {
        printLog(1, null, DEFAULT_MESSAGE);
    }

    /* renamed from: v */
    public static void m13v(Object msg) {
        printLog(1, null, msg);
    }

    /* renamed from: v */
    public static void m12v(String tag, String msg) {
        printLog(1, tag, msg);
    }

    /* renamed from: d */
    public static void m23d() {
        printLog(2, null, DEFAULT_MESSAGE);
    }

    /* renamed from: d */
    public static void m22d(Object msg) {
        printLog(2, null, msg);
    }

    /* renamed from: d */
    public static void m21d(String tag, Object msg) {
        printLog(2, tag, msg);
    }

    /* renamed from: i */
    public static void m17i() {
        printLog(3, null, DEFAULT_MESSAGE);
    }

    /* renamed from: i */
    public static void m16i(Object msg) {
        printLog(3, null, msg);
    }

    /* renamed from: i */
    public static void m15i(String tag, Object msg) {
        printLog(3, tag, msg);
    }

    /* renamed from: w */
    public static void m11w() {
        printLog(4, null, DEFAULT_MESSAGE);
    }

    /* renamed from: w */
    public static void m10w(Object msg) {
        printLog(4, null, msg);
    }

    /* renamed from: w */
    public static void m9w(String tag, Object msg) {
        printLog(4, tag, msg);
    }

    /* renamed from: e */
    public static void m20e() {
        printLog(5, null, DEFAULT_MESSAGE);
    }

    /* renamed from: e */
    public static void m19e(Object msg) {
        printLog(5, null, msg);
    }

    /* renamed from: e */
    public static void m18e(String tag, Object msg) {
        printLog(5, tag, msg);
    }

    /* renamed from: a */
    public static void m26a() {
        printLog(6, null, DEFAULT_MESSAGE);
    }

    /* renamed from: a */
    public static void m25a(Object msg) {
        printLog(6, null, msg);
    }

    /* renamed from: a */
    public static void m24a(String tag, Object msg) {
        printLog(6, tag, msg);
    }

    public static void json(String jsonFormat) {
        printLog(7, null, jsonFormat);
    }

    public static void json(String tag, String jsonFormat) {
        printLog(7, tag, jsonFormat);
    }

    private static void printLog(int type, String tagStr, Object objectMsg) {
        String msg;
        int chunkCount;
        if (!IS_SHOW_LOG) {
            return;
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[4].getFileName();
        String methodName = stackTrace[4].getMethodName();
        int lineNumber = stackTrace[4].getLineNumber();
        String tag = tagStr == null ? className : tagStr;
        String methodName2 = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (");
        stringBuilder.append(className);
        stringBuilder.append(":");
        stringBuilder.append(lineNumber);
        stringBuilder.append(")#");
        stringBuilder.append(methodName2);
        stringBuilder.append(" ] ");
        if (objectMsg == null) {
            msg = "Log with null Object";
        } else {
            msg = objectMsg.toString();
        }
        if (msg != null && type != 7) {
            stringBuilder.append(msg);
        }
        String logStr = stringBuilder.toString();
        switch (type) {
            case 1:
                Log.v(tag, logStr);
                return;
            case 2:
                Log.d(tag, logStr);
                return;
            case 3:
                Log.i(tag, logStr);
                return;
            case 4:
                Log.w(tag, logStr);
                return;
            case 5:
                Log.e(tag, logStr);
                return;
            case 6:
                Log.wtf(tag, logStr);
                return;
            case 7:
                if (TextUtils.isEmpty(msg)) {
                    Log.d(tag, "Empty or Null json content");
                    return;
                }
                String message = null;
                try {
                    if (msg.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(msg);
                        message = jsonObject.toString(4);
                    } else if (msg.startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(msg);
                        message = jsonArray.toString(4);
                    }
                    printLine(tag, true);
                    String message2 = logStr + LINE_SEPARATOR + message;
                    String[] lines = message2.split(LINE_SEPARATOR);
                    StringBuilder jsonContent = new StringBuilder();
                    int length = lines.length;
                    int i = 0;
                    while (i < length) {
                        String line = lines[i];
                        jsonContent.append("║ ");
                        jsonContent.append(line);
                        jsonContent.append(LINE_SEPARATOR);
                        i++;
                        message2 = message2;
                    }
                    String message3 = jsonContent.toString();
                    if (message3.length() > 3200) {
                        Log.w(tag, "jsonContent.length = " + jsonContent.toString().length());
                        int chunkCount2 = jsonContent.toString().length() / 3200;
                        int i2 = 0;
                        while (i2 <= chunkCount2) {
                            int max = (i2 + 1) * 3200;
                            if (max >= jsonContent.toString().length()) {
                                chunkCount = chunkCount2;
                                int chunkCount3 = i2 * 3200;
                                Log.w(tag, jsonContent.toString().substring(chunkCount3));
                            } else {
                                chunkCount = chunkCount2;
                                Log.w(tag, jsonContent.toString().substring(i2 * 3200, max));
                            }
                            i2++;
                            chunkCount2 = chunkCount;
                        }
                    } else {
                        Log.w(tag, jsonContent.toString());
                    }
                    printLine(tag, false);
                    return;
                } catch (JSONException e) {
                    m18e(tag, e.getCause().getMessage() + "\n" + msg);
                    return;
                }
            default:
                return;
        }
    }

    private static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.w(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.w(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }
}