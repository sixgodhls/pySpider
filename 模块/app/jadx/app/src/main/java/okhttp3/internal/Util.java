package okhttp3.internal;

import android.support.v4.media.session.PlaybackStateCompat;
import com.bumptech.glide.load.Key;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.IDN;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;

/* loaded from: classes.dex */
public final class Util {
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final ResponseBody EMPTY_RESPONSE = ResponseBody.create((MediaType) null, EMPTY_BYTE_ARRAY);
    public static final RequestBody EMPTY_REQUEST = RequestBody.create((MediaType) null, EMPTY_BYTE_ARRAY);
    private static final ByteString UTF_8_BOM = ByteString.decodeHex("efbbbf");
    private static final ByteString UTF_16_BE_BOM = ByteString.decodeHex("feff");
    private static final ByteString UTF_16_LE_BOM = ByteString.decodeHex("fffe");
    private static final ByteString UTF_32_BE_BOM = ByteString.decodeHex("0000ffff");
    private static final ByteString UTF_32_LE_BOM = ByteString.decodeHex("ffff0000");
    public static final Charset UTF_8 = Charset.forName(Key.STRING_CHARSET_NAME);
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    private static final Charset UTF_16_BE = Charset.forName("UTF-16BE");
    private static final Charset UTF_16_LE = Charset.forName("UTF-16LE");
    private static final Charset UTF_32_BE = Charset.forName("UTF-32BE");
    private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");
    public static final TimeZone UTC = TimeZone.getTimeZone("GMT");
    public static final Comparator<String> NATURAL_ORDER = new Comparator<String>() { // from class: okhttp3.internal.Util.1
        @Override // java.util.Comparator
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    };
    private static final Pattern VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");

    private Util() {
    }

    public static void checkOffsetAndCount(long arrayLength, long offset, long count) {
        if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (AssertionError e) {
                if (!isAndroidGetsocknameError(e)) {
                    throw e;
                }
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e2) {
            }
        }
    }

    public static void closeQuietly(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static boolean discard(Source source, int timeout, TimeUnit timeUnit) {
        try {
            return skipAll(source, timeout, timeUnit);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean skipAll(Source source, int duration, TimeUnit timeUnit) throws IOException {
        long originalDuration;
        long now = System.nanoTime();
        if (source.timeout().hasDeadline()) {
            originalDuration = source.timeout().deadlineNanoTime() - now;
        } else {
            originalDuration = Long.MAX_VALUE;
        }
        source.timeout().deadlineNanoTime(Math.min(originalDuration, timeUnit.toNanos(duration)) + now);
        try {
            Buffer skipBuffer = new Buffer();
            while (source.read(skipBuffer, PlaybackStateCompat.ACTION_PLAY_FROM_URI) != -1) {
                skipBuffer.clear();
            }
            if (originalDuration == Long.MAX_VALUE) {
                source.timeout().clearDeadline();
            } else {
                source.timeout().deadlineNanoTime(now + originalDuration);
            }
            return true;
        } catch (InterruptedIOException e) {
            if (originalDuration == Long.MAX_VALUE) {
                source.timeout().clearDeadline();
            } else {
                source.timeout().deadlineNanoTime(now + originalDuration);
            }
            return false;
        } catch (Throwable th) {
            if (originalDuration == Long.MAX_VALUE) {
                source.timeout().clearDeadline();
            } else {
                source.timeout().deadlineNanoTime(now + originalDuration);
            }
            throw th;
        }
    }

    public static <T> List<T> immutableList(List<T> list) {
        return Collections.unmodifiableList(new ArrayList(list));
    }

    public static <T> List<T> immutableList(T... elements) {
        return Collections.unmodifiableList(Arrays.asList((Object[]) elements.clone()));
    }

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() { // from class: okhttp3.internal.Util.2
            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    public static String[] intersect(Comparator<? super String> comparator, String[] first, String[] second) {
        List<String> result = new ArrayList<>();
        for (String a : first) {
            int length = second.length;
            int i = 0;
            while (true) {
                if (i < length) {
                    String b = second[i];
                    if (comparator.compare(a, b) != 0) {
                        i++;
                    } else {
                        result.add(a);
                        break;
                    }
                }
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public static boolean nonEmptyIntersection(Comparator<String> comparator, String[] first, String[] second) {
        if (first == null || second == null || first.length == 0 || second.length == 0) {
            return false;
        }
        for (String a : first) {
            for (String b : second) {
                if (comparator.compare(a, b) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String hostHeader(HttpUrl url, boolean includeDefaultPort) {
        String host;
        if (url.host().contains(":")) {
            host = "[" + url.host() + "]";
        } else {
            host = url.host();
        }
        if (includeDefaultPort || url.port() != HttpUrl.defaultPort(url.scheme())) {
            return host + ":" + url.port();
        }
        return host;
    }

    public static boolean isAndroidGetsocknameError(AssertionError e) {
        return (e.getCause() == null || e.getMessage() == null || !e.getMessage().contains("getsockname failed")) ? false : true;
    }

    public static int indexOf(Comparator<String> comparator, String[] array, String value) {
        int size = array.length;
        for (int i = 0; i < size; i++) {
            if (comparator.compare(array[i], value) == 0) {
                return i;
            }
        }
        return -1;
    }

    public static String[] concat(String[] array, String value) {
        String[] result = new String[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[result.length - 1] = value;
        return result;
    }

    public static int skipLeadingAsciiWhitespace(String input, int pos, int limit) {
        for (int i = pos; i < limit; i++) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                default:
                    return i;
            }
        }
        return limit;
    }

    public static int skipTrailingAsciiWhitespace(String input, int pos, int limit) {
        for (int i = limit - 1; i >= pos; i--) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                default:
                    return i + 1;
            }
        }
        return pos;
    }

    public static String trimSubstring(String string, int pos, int limit) {
        int start = skipLeadingAsciiWhitespace(string, pos, limit);
        int end = skipTrailingAsciiWhitespace(string, start, limit);
        return string.substring(start, end);
    }

    public static int delimiterOffset(String input, int pos, int limit, String delimiters) {
        for (int i = pos; i < limit; i++) {
            if (delimiters.indexOf(input.charAt(i)) != -1) {
                return i;
            }
        }
        return limit;
    }

    public static int delimiterOffset(String input, int pos, int limit, char delimiter) {
        for (int i = pos; i < limit; i++) {
            if (input.charAt(i) == delimiter) {
                return i;
            }
        }
        return limit;
    }

    public static String canonicalizeHost(String host) {
        InetAddress inetAddress;
        if (host.contains(":")) {
            if (host.startsWith("[") && host.endsWith("]")) {
                inetAddress = decodeIpv6(host, 1, host.length() - 1);
            } else {
                inetAddress = decodeIpv6(host, 0, host.length());
            }
            if (inetAddress == null) {
                return null;
            }
            byte[] address = inetAddress.getAddress();
            if (address.length == 16) {
                return inet6AddressToAscii(address);
            }
            throw new AssertionError("Invalid IPv6 address: '" + host + "'");
        }
        try {
            String result = IDN.toASCII(host).toLowerCase(Locale.US);
            if (result.isEmpty()) {
                return null;
            }
            if (!containsInvalidHostnameAsciiCodes(result)) {
                return result;
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean containsInvalidHostnameAsciiCodes(String hostnameAscii) {
        for (int i = 0; i < hostnameAscii.length(); i++) {
            char c = hostnameAscii.charAt(i);
            if (c <= 31 || c >= 127 || " #%/:?@[\\]".indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    public static int indexOfControlOrNonAscii(String input) {
        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c <= 31 || c >= 127) {
                return i;
            }
        }
        return -1;
    }

    public static boolean verifyAsIpAddress(String host) {
        return VERIFY_AS_IP_ADDRESS.matcher(host).matches();
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }

    public static Charset bomAwareCharset(BufferedSource source, Charset charset) throws IOException {
        if (source.rangeEquals(0L, UTF_8_BOM)) {
            source.skip(UTF_8_BOM.size());
            return UTF_8;
        } else if (source.rangeEquals(0L, UTF_16_BE_BOM)) {
            source.skip(UTF_16_BE_BOM.size());
            return UTF_16_BE;
        } else if (source.rangeEquals(0L, UTF_16_LE_BOM)) {
            source.skip(UTF_16_LE_BOM.size());
            return UTF_16_LE;
        } else if (source.rangeEquals(0L, UTF_32_BE_BOM)) {
            source.skip(UTF_32_BE_BOM.size());
            return UTF_32_BE;
        } else if (source.rangeEquals(0L, UTF_32_LE_BOM)) {
            source.skip(UTF_32_LE_BOM.size());
            return UTF_32_LE;
        } else {
            return charset;
        }
    }

    public static int checkDuration(String name, long duration, TimeUnit unit) {
        if (duration < 0) {
            throw new IllegalArgumentException(name + " < 0");
        } else if (unit == null) {
            throw new NullPointerException("unit == null");
        } else {
            long millis = unit.toMillis(duration);
            if (millis > 2147483647L) {
                throw new IllegalArgumentException(name + " too large.");
            } else if (millis != 0 || duration <= 0) {
                return (int) millis;
            } else {
                throw new IllegalArgumentException(name + " too small.");
            }
        }
    }

    public static AssertionError assertionError(String message, Exception e) {
        AssertionError assertionError = new AssertionError(message);
        try {
            assertionError.initCause(e);
        } catch (IllegalStateException e2) {
        }
        return assertionError;
    }

    public static int decodeHexDigit(char c) {
        if (c < '0' || c > '9') {
            if (c >= 'a' && c <= 'f') {
                return (c - 'a') + 10;
            }
            if (c >= 'A' && c <= 'F') {
                return (c - 'A') + 10;
            }
            return -1;
        }
        return c - '0';
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x007b, code lost:
        return null;
     */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0050  */
    @javax.annotation.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.net.InetAddress decodeIpv6(java.lang.String r11, int r12, int r13) {
        /*
            r0 = 16
            byte[] r0 = new byte[r0]
            r1 = 0
            r2 = -1
            r3 = -1
            r4 = r12
        L8:
            r5 = -1
            r6 = 0
            r7 = 0
            if (r4 >= r13) goto L7c
            int r8 = r0.length
            if (r1 != r8) goto L11
            return r7
        L11:
            int r8 = r4 + 2
            if (r8 > r13) goto L29
            java.lang.String r8 = "::"
            r9 = 2
            boolean r8 = r11.regionMatches(r4, r8, r6, r9)
            if (r8 == 0) goto L29
            if (r2 == r5) goto L21
            return r7
        L21:
            int r4 = r4 + 2
            int r1 = r1 + 2
            r2 = r1
            if (r4 != r13) goto L4c
            goto L7c
        L29:
            if (r1 == 0) goto L4c
            java.lang.String r8 = ":"
            r9 = 1
            boolean r8 = r11.regionMatches(r4, r8, r6, r9)
            if (r8 == 0) goto L37
            int r4 = r4 + 1
            goto L4c
        L37:
            java.lang.String r8 = "."
            boolean r8 = r11.regionMatches(r4, r8, r6, r9)
            if (r8 == 0) goto L4b
            int r8 = r1 + (-2)
            boolean r8 = decodeIpv4Suffix(r11, r3, r13, r0, r8)
            if (r8 != 0) goto L48
            return r7
        L48:
            int r1 = r1 + 2
            goto L7c
        L4b:
            return r7
        L4c:
            r6 = 0
            r3 = r4
        L4e:
            if (r4 >= r13) goto L62
            char r8 = r11.charAt(r4)
            int r9 = decodeHexDigit(r8)
            if (r9 != r5) goto L5b
            goto L62
        L5b:
            int r10 = r6 << 4
            int r6 = r10 + r9
            int r4 = r4 + 1
            goto L4e
        L62:
            int r5 = r4 - r3
            if (r5 == 0) goto L7b
            r8 = 4
            if (r5 <= r8) goto L6a
            goto L7b
        L6a:
            int r7 = r1 + 1
            int r8 = r6 >>> 8
            r8 = r8 & 255(0xff, float:3.57E-43)
            byte r8 = (byte) r8
            r0[r1] = r8
            int r1 = r7 + 1
            r8 = r6 & 255(0xff, float:3.57E-43)
            byte r8 = (byte) r8
            r0[r7] = r8
            goto L8
        L7b:
            return r7
        L7c:
            int r4 = r0.length
            if (r1 == r4) goto L91
            if (r2 != r5) goto L82
            return r7
        L82:
            int r4 = r0.length
            int r5 = r1 - r2
            int r4 = r4 - r5
            int r5 = r1 - r2
            java.lang.System.arraycopy(r0, r2, r0, r4, r5)
            int r4 = r0.length
            int r4 = r4 - r1
            int r4 = r4 + r2
            java.util.Arrays.fill(r0, r2, r4, r6)
        L91:
            java.net.InetAddress r4 = java.net.InetAddress.getByAddress(r0)     // Catch: java.net.UnknownHostException -> L96
            return r4
        L96:
            r4 = move-exception
            java.lang.AssertionError r5 = new java.lang.AssertionError
            r5.<init>()
            throw r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.Util.decodeIpv6(java.lang.String, int, int):java.net.InetAddress");
    }

    private static boolean decodeIpv4Suffix(String input, int pos, int limit, byte[] address, int addressOffset) {
        int b = addressOffset;
        int i = pos;
        while (i < limit) {
            if (b == address.length) {
                return false;
            }
            if (b != addressOffset) {
                if (input.charAt(i) != '.') {
                    return false;
                }
                i++;
            }
            int value = 0;
            int groupOffset = i;
            while (i < limit) {
                char c = input.charAt(i);
                if (c < '0' || c > '9') {
                    break;
                } else if ((value == 0 && groupOffset != i) || ((value * 10) + c) - 48 > 255) {
                    return false;
                } else {
                    i++;
                }
            }
            int groupLength = i - groupOffset;
            if (groupLength == 0) {
                return false;
            }
            address[b] = (byte) value;
            b++;
        }
        int i2 = addressOffset + 4;
        return b == i2;
    }

    private static String inet6AddressToAscii(byte[] address) {
        int longestRunOffset = -1;
        int longestRunLength = 0;
        int i = 0;
        while (i < address.length) {
            int currentRunOffset = i;
            while (i < 16 && address[i] == 0 && address[i + 1] == 0) {
                i += 2;
            }
            int currentRunLength = i - currentRunOffset;
            if (currentRunLength > longestRunLength && currentRunLength >= 4) {
                longestRunOffset = currentRunOffset;
                longestRunLength = currentRunLength;
            }
            i += 2;
        }
        Buffer result = new Buffer();
        int i2 = 0;
        while (i2 < address.length) {
            if (i2 == longestRunOffset) {
                result.mo354writeByte(58);
                i2 += longestRunLength;
                if (i2 == 16) {
                    result.mo354writeByte(58);
                }
            } else {
                if (i2 > 0) {
                    result.mo354writeByte(58);
                }
                int group = ((address[i2] & 255) << 8) | (address[i2 + 1] & 255);
                result.mo356writeHexadecimalUnsignedLong(group);
                i2 += 2;
            }
        }
        return result.readUtf8();
    }
}
