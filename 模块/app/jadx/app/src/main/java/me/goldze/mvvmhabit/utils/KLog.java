package me.goldze.mvvmhabit.utils;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class KLog {
    private static final int A = 6;
    private static final int D = 2;
    private static final String DEFAULT_MESSAGE = "execute";
    private static final int E = 5;
    private static final int I = 3;
    private static final int JSON = 7;
    private static final int JSON_INDENT = 4;
    private static final int V = 1;
    private static final int W = 4;
    private static boolean IS_SHOW_LOG = false;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void init(boolean isShowLog) {
        IS_SHOW_LOG = isShowLog;
    }

    public static void v() {
        printLog(1, null, DEFAULT_MESSAGE);
    }

    public static void v(Object msg) {
        printLog(1, null, msg);
    }

    public static void v(String tag, String msg) {
        printLog(1, tag, msg);
    }

    public static void d() {
        printLog(2, null, DEFAULT_MESSAGE);
    }

    public static void d(Object msg) {
        printLog(2, null, msg);
    }

    public static void d(String tag, Object msg) {
        printLog(2, tag, msg);
    }

    public static void i() {
        printLog(3, null, DEFAULT_MESSAGE);
    }

    public static void i(Object msg) {
        printLog(3, null, msg);
    }

    public static void i(String tag, Object msg) {
        printLog(3, tag, msg);
    }

    public static void w() {
        printLog(4, null, DEFAULT_MESSAGE);
    }

    public static void w(Object msg) {
        printLog(4, null, msg);
    }

    public static void w(String tag, Object msg) {
        printLog(4, tag, msg);
    }

    public static void e() {
        printLog(5, null, DEFAULT_MESSAGE);
    }

    public static void e(Object msg) {
        printLog(5, null, msg);
    }

    public static void e(String tag, Object msg) {
        printLog(5, tag, msg);
    }

    public static void a() {
        printLog(6, null, DEFAULT_MESSAGE);
    }

    public static void a(Object msg) {
        printLog(6, null, msg);
    }

    public static void a(String tag, Object msg) {
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
                    e(tag, e.getCause().getMessage() + "\n" + msg);
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
