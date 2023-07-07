package me.goldze.mvvmhabit.utils;

/* loaded from: classes.dex */
public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isTrimEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isSpace(String s) {
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

    public static boolean equals(CharSequence a, CharSequence b) {
        int length;
        if (a == b) {
            return true;
        }
        if (a == null || b == null || (length = a.length()) != b.length()) {
            return false;
        }
        if ((a instanceof String) && (b instanceof String)) {
            return a.equals(b);
        }
        for (int i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    public static String null2Length0(String s) {
        return s == null ? "" : s;
    }

    public static int length(CharSequence s) {
        if (s == null) {
            return 0;
        }
        return s.length();
    }

    public static String upperFirstLetter(String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - ' ')) + s.substring(1);
    }

    public static String lowerFirstLetter(String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + ' ')) + s.substring(1);
    }

    public static String reverse(String s) {
        int len = length(s);
        if (len <= 1) {
            return s;
        }
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        for (int i = 0; i < mid; i++) {
            char c = chars[i];
            chars[i] = chars[(len - i) - 1];
            chars[(len - i) - 1] = c;
        }
        return new String(chars);
    }

    public static String toDBC(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    public static String toSBC(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = 12288;
            } else if ('!' <= chars[i] && chars[i] <= '~') {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }
}