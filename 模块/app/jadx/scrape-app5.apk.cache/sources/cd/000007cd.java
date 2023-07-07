package me.goldze.mvvmhabit.http.interceptor.logging;

import android.text.TextUtils;
import java.io.IOException;
import java.util.List;
import me.goldze.mvvmhabit.http.interceptor.logging.LoggingInterceptor;
import okhttp3.FormBody;
import okhttp3.Request;
import okio.Buffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class Printer {
    private static final String BODY_TAG = "Body:";
    private static final String CENTER_LINE = "├ ";
    private static final String CORNER_BOTTOM = "└ ";
    private static final String CORNER_UP = "┌ ";
    private static final String DEFAULT_LINE = "│ ";
    private static final String END_LINE = "└───────────────────────────────────────────────────────────────────────────────────────";
    private static final String HEADERS_TAG = "Headers:";
    private static final int JSON_INDENT = 3;
    private static final String METHOD_TAG = "Method: @";

    /* renamed from: N */
    private static final String f207N = "\n";
    private static final String[] OMITTED_REQUEST;
    private static final String[] OMITTED_RESPONSE;
    private static final String RECEIVED_TAG = "Received in: ";
    private static final String REQUEST_UP_LINE = "┌────── Request ────────────────────────────────────────────────────────────────────────";
    private static final String RESPONSE_UP_LINE = "┌────── Response ───────────────────────────────────────────────────────────────────────";
    private static final String STATUS_CODE_TAG = "Status Code: ";

    /* renamed from: T */
    private static final String f208T = "\t";
    private static final String URL_TAG = "URL: ";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR;

    static {
        String str = LINE_SEPARATOR;
        OMITTED_RESPONSE = new String[]{str, "Omitted response body"};
        OMITTED_REQUEST = new String[]{str, "Omitted request body"};
    }

    protected Printer() {
        throw new UnsupportedOperationException();
    }

    private static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || f207N.equals(line) || f208T.equals(line) || TextUtils.isEmpty(line.trim());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void printJsonRequest(LoggingInterceptor.Builder builder, Request request) {
        String requestBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyToString(request);
        String tag = builder.getTag(true);
        if (builder.getLogger() == null) {
            C0985I.log(builder.getType(), tag, REQUEST_UP_LINE);
        }
        logLines(builder.getType(), tag, new String[]{URL_TAG + request.url()}, builder.getLogger(), false);
        logLines(builder.getType(), tag, getRequest(request, builder.getLevel()), builder.getLogger(), true);
        if (request.body() instanceof FormBody) {
            StringBuilder formBody = new StringBuilder();
            FormBody body = (FormBody) request.body();
            if (body != null && body.size() != 0) {
                for (int i = 0; i < body.size(); i++) {
                    formBody.append(body.encodedName(i) + "=" + body.encodedValue(i) + "&");
                }
                int i2 = formBody.length();
                formBody.delete(i2 - 1, formBody.length());
                logLines(builder.getType(), tag, new String[]{formBody.toString()}, builder.getLogger(), true);
            }
        }
        if (builder.getLevel() == Level.BASIC || builder.getLevel() == Level.BODY) {
            logLines(builder.getType(), tag, requestBody.split(LINE_SEPARATOR), builder.getLogger(), true);
        }
        if (builder.getLogger() == null) {
            C0985I.log(builder.getType(), tag, END_LINE);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void printJsonResponse(LoggingInterceptor.Builder builder, long chainMs, boolean isSuccessful, int code, String headers, String bodyString, List<String> segments) {
        String responseBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + getJsonString(bodyString);
        String tag = builder.getTag(false);
        if (builder.getLogger() == null) {
            C0985I.log(builder.getType(), tag, RESPONSE_UP_LINE);
        }
        logLines(builder.getType(), tag, getResponse(headers, chainMs, code, isSuccessful, builder.getLevel(), segments), builder.getLogger(), true);
        if (builder.getLevel() == Level.BASIC || builder.getLevel() == Level.BODY) {
            logLines(builder.getType(), tag, responseBody.split(LINE_SEPARATOR), builder.getLogger(), true);
        }
        if (builder.getLogger() == null) {
            C0985I.log(builder.getType(), tag, END_LINE);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void printFileRequest(LoggingInterceptor.Builder builder, Request request) {
        String tag = builder.getTag(true);
        if (builder.getLogger() == null) {
            C0985I.log(builder.getType(), tag, REQUEST_UP_LINE);
        }
        int type = builder.getType();
        logLines(type, tag, new String[]{URL_TAG + request.url()}, builder.getLogger(), false);
        logLines(builder.getType(), tag, getRequest(request, builder.getLevel()), builder.getLogger(), true);
        if (request.body() instanceof FormBody) {
            StringBuilder formBody = new StringBuilder();
            FormBody body = (FormBody) request.body();
            if (body != null && body.size() != 0) {
                for (int i = 0; i < body.size(); i++) {
                    formBody.append(body.encodedName(i) + "=" + body.encodedValue(i) + "&");
                }
                int i2 = formBody.length();
                formBody.delete(i2 - 1, formBody.length());
                logLines(builder.getType(), tag, new String[]{formBody.toString()}, builder.getLogger(), true);
            }
        }
        if (builder.getLevel() == Level.BASIC || builder.getLevel() == Level.BODY) {
            logLines(builder.getType(), tag, OMITTED_REQUEST, builder.getLogger(), true);
        }
        if (builder.getLogger() == null) {
            C0985I.log(builder.getType(), tag, END_LINE);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void printFileResponse(LoggingInterceptor.Builder builder, long chainMs, boolean isSuccessful, int code, String headers, List<String> segments) {
        String tag = builder.getTag(false);
        if (builder.getLogger() == null) {
            C0985I.log(builder.getType(), tag, RESPONSE_UP_LINE);
        }
        logLines(builder.getType(), tag, getResponse(headers, chainMs, code, isSuccessful, builder.getLevel(), segments), builder.getLogger(), true);
        logLines(builder.getType(), tag, OMITTED_RESPONSE, builder.getLogger(), true);
        if (builder.getLogger() == null) {
            C0985I.log(builder.getType(), tag, END_LINE);
        }
    }

    private static String[] getRequest(Request request, Level level) {
        String str;
        String header = request.headers().toString();
        boolean loggableHeader = level == Level.HEADERS || level == Level.BASIC;
        StringBuilder sb = new StringBuilder();
        sb.append(METHOD_TAG);
        sb.append(request.method());
        sb.append(DOUBLE_SEPARATOR);
        if (!isEmpty(header) && loggableHeader) {
            str = HEADERS_TAG + LINE_SEPARATOR + dotHeaders(header);
        } else {
            str = "";
        }
        sb.append(str);
        String message = sb.toString();
        return message.split(LINE_SEPARATOR);
    }

    private static String[] getResponse(String header, long tookMs, int code, boolean isSuccessful, Level level, List<String> segments) {
        String str;
        String str2;
        boolean loggableHeader = level == Level.HEADERS || level == Level.BASIC;
        String segmentString = slashSegments(segments);
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(segmentString)) {
            str = segmentString + " - ";
        } else {
            str = "";
        }
        sb.append(str);
        sb.append("is success : ");
        sb.append(isSuccessful);
        sb.append(" - ");
        sb.append(RECEIVED_TAG);
        sb.append(tookMs);
        sb.append("ms");
        sb.append(DOUBLE_SEPARATOR);
        sb.append(STATUS_CODE_TAG);
        sb.append(code);
        sb.append(DOUBLE_SEPARATOR);
        if (isEmpty(header)) {
            str2 = "";
        } else if (loggableHeader) {
            str2 = HEADERS_TAG + LINE_SEPARATOR + dotHeaders(header);
        } else {
            str2 = "";
        }
        sb.append(str2);
        String message = sb.toString();
        return message.split(LINE_SEPARATOR);
    }

    private static String slashSegments(List<String> segments) {
        StringBuilder segmentString = new StringBuilder();
        for (String segment : segments) {
            segmentString.append("/");
            segmentString.append(segment);
        }
        return segmentString.toString();
    }

    private static String dotHeaders(String header) {
        String tag;
        String[] headers = header.split(LINE_SEPARATOR);
        StringBuilder builder = new StringBuilder();
        if (headers.length > 1) {
            for (int i = 0; i < headers.length; i++) {
                if (i == 0) {
                    tag = CORNER_UP;
                } else if (i == headers.length - 1) {
                    tag = CORNER_BOTTOM;
                } else {
                    tag = CENTER_LINE;
                }
                builder.append(tag);
                builder.append(headers[i]);
                builder.append(f207N);
            }
        } else {
            for (String item : headers) {
                builder.append("─ ");
                builder.append(item);
                builder.append(f207N);
            }
        }
        return builder.toString();
    }

    private static void logLines(int type, String tag, String[] lines, Logger logger, boolean withLineSize) {
        for (String line : lines) {
            int lineLength = line.length();
            int MAX_LONG_SIZE = withLineSize ? 110 : lineLength;
            for (int i = 0; i <= lineLength / MAX_LONG_SIZE; i++) {
                int start = i * MAX_LONG_SIZE;
                int end = (i + 1) * MAX_LONG_SIZE;
                int end2 = end > line.length() ? line.length() : end;
                if (logger == null) {
                    C0985I.log(type, tag, DEFAULT_LINE + line.substring(start, end2));
                } else {
                    logger.log(type, tag, line.substring(start, end2));
                }
            }
        }
    }

    private static String bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            if (copy.body() == null) {
                return "";
            }
            copy.body().writeTo(buffer);
            return getJsonString(buffer.readUtf8());
        } catch (IOException e) {
            return "{\"err\": \"" + e.getMessage() + "\"}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getJsonString(String msg) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(3);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(3);
            } else {
                message = msg;
            }
            return message;
        } catch (JSONException e) {
            return msg;
        }
    }
}