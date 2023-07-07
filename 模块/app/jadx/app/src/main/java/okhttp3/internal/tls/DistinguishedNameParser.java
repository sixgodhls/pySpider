package okhttp3.internal.tls;

import javax.security.auth.x500.X500Principal;

/* loaded from: classes.dex */
final class DistinguishedNameParser {
    private int beg;
    private char[] chars;
    private int cur;
    private final String dn;
    private int end;
    private final int length;
    private int pos;

    DistinguishedNameParser(X500Principal principal) {
        this.dn = principal.getName("RFC2253");
        this.length = this.dn.length();
    }

    private String nextAT() {
        while (true) {
            int i = this.pos;
            if (i >= this.length || this.chars[i] != ' ') {
                break;
            }
            this.pos = i + 1;
        }
        int i2 = this.pos;
        if (i2 == this.length) {
            return null;
        }
        this.beg = i2;
        this.pos = i2 + 1;
        while (true) {
            int i3 = this.pos;
            if (i3 >= this.length) {
                break;
            }
            char[] cArr = this.chars;
            if (cArr[i3] == '=' || cArr[i3] == ' ') {
                break;
            }
            this.pos = i3 + 1;
        }
        int i4 = this.pos;
        if (i4 >= this.length) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
        }
        this.end = i4;
        if (this.chars[i4] == ' ') {
            while (true) {
                int i5 = this.pos;
                if (i5 >= this.length) {
                    break;
                }
                char[] cArr2 = this.chars;
                if (cArr2[i5] == '=' || cArr2[i5] != ' ') {
                    break;
                }
                this.pos = i5 + 1;
            }
            char[] cArr3 = this.chars;
            int i6 = this.pos;
            if (cArr3[i6] != '=' || i6 == this.length) {
                throw new IllegalStateException("Unexpected end of DN: " + this.dn);
            }
        }
        this.pos++;
        while (true) {
            int i7 = this.pos;
            if (i7 >= this.length || this.chars[i7] != ' ') {
                break;
            }
            this.pos = i7 + 1;
        }
        int i8 = this.end;
        int i9 = this.beg;
        if (i8 - i9 > 4) {
            char[] cArr4 = this.chars;
            if (cArr4[i9 + 3] == '.' && (cArr4[i9] == 'O' || cArr4[i9] == 'o')) {
                char[] cArr5 = this.chars;
                int i10 = this.beg;
                if (cArr5[i10 + 1] == 'I' || cArr5[i10 + 1] == 'i') {
                    char[] cArr6 = this.chars;
                    int i11 = this.beg;
                    if (cArr6[i11 + 2] == 'D' || cArr6[i11 + 2] == 'd') {
                        this.beg += 4;
                    }
                }
            }
        }
        char[] cArr7 = this.chars;
        int i12 = this.beg;
        return new String(cArr7, i12, this.end - i12);
    }

    private String quotedAV() {
        this.pos++;
        this.beg = this.pos;
        this.end = this.beg;
        while (true) {
            int i = this.pos;
            if (i == this.length) {
                throw new IllegalStateException("Unexpected end of DN: " + this.dn);
            }
            char[] cArr = this.chars;
            if (cArr[i] == '\"') {
                this.pos = i + 1;
                while (true) {
                    int i2 = this.pos;
                    if (i2 >= this.length || this.chars[i2] != ' ') {
                        break;
                    }
                    this.pos = i2 + 1;
                }
                char[] cArr2 = this.chars;
                int i3 = this.beg;
                return new String(cArr2, i3, this.end - i3);
            }
            if (cArr[i] == '\\') {
                cArr[this.end] = getEscaped();
            } else {
                cArr[this.end] = cArr[i];
            }
            this.pos++;
            this.end++;
        }
    }

    private String hexAV() {
        int i = this.pos;
        if (i + 4 >= this.length) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
        }
        this.beg = i;
        this.pos = i + 1;
        while (true) {
            int i2 = this.pos;
            if (i2 == this.length) {
                break;
            }
            char[] cArr = this.chars;
            if (cArr[i2] == '+' || cArr[i2] == ',' || cArr[i2] == ';') {
                break;
            } else if (cArr[i2] == ' ') {
                this.end = i2;
                this.pos = i2 + 1;
                while (true) {
                    int i3 = this.pos;
                    if (i3 >= this.length || this.chars[i3] != ' ') {
                        break;
                    }
                    this.pos = i3 + 1;
                }
            } else {
                if (cArr[i2] >= 'A' && cArr[i2] <= 'F') {
                    cArr[i2] = (char) (cArr[i2] + ' ');
                }
                this.pos++;
            }
        }
        this.end = this.pos;
        int i4 = this.end;
        int i5 = this.beg;
        int hexLen = i4 - i5;
        if (hexLen < 5 || (hexLen & 1) == 0) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
        }
        byte[] encoded = new byte[hexLen / 2];
        int p = i5 + 1;
        for (int i6 = 0; i6 < encoded.length; i6++) {
            encoded[i6] = (byte) getByte(p);
            p += 2;
        }
        return new String(this.chars, this.beg, hexLen);
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x00a6, code lost:
        return new java.lang.String(r1, r2, r6.cur - r2);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String escapedAV() {
        /*
            r6 = this;
            int r0 = r6.pos
            r6.beg = r0
            r6.end = r0
        L6:
            int r0 = r6.pos
            int r1 = r6.length
            if (r0 < r1) goto L19
            java.lang.String r0 = new java.lang.String
            char[] r1 = r6.chars
            int r2 = r6.beg
            int r3 = r6.end
            int r3 = r3 - r2
            r0.<init>(r1, r2, r3)
            return r0
        L19:
            char[] r1 = r6.chars
            char r2 = r1[r0]
            r3 = 59
            r4 = 32
            if (r2 == r4) goto L5b
            if (r2 == r3) goto L4e
            r3 = 92
            if (r2 == r3) goto L3b
            switch(r2) {
                case 43: goto L4e;
                case 44: goto L4e;
                default: goto L2c;
            }
        L2c:
            int r2 = r6.end
            int r3 = r2 + 1
            r6.end = r3
            char r3 = r1[r0]
            r1[r2] = r3
            int r0 = r0 + 1
            r6.pos = r0
            goto L6
        L3b:
            int r0 = r6.end
            int r2 = r0 + 1
            r6.end = r2
            char r2 = r6.getEscaped()
            r1[r0] = r2
            int r0 = r6.pos
            int r0 = r0 + 1
            r6.pos = r0
            goto L6
        L4e:
            java.lang.String r0 = new java.lang.String
            char[] r1 = r6.chars
            int r2 = r6.beg
            int r3 = r6.end
            int r3 = r3 - r2
            r0.<init>(r1, r2, r3)
            return r0
        L5b:
            int r2 = r6.end
            r6.cur = r2
            int r0 = r0 + 1
            r6.pos = r0
            int r0 = r2 + 1
            r6.end = r0
            r1[r2] = r4
        L69:
            int r0 = r6.pos
            int r1 = r6.length
            if (r0 >= r1) goto L82
            char[] r1 = r6.chars
            char r2 = r1[r0]
            if (r2 != r4) goto L82
            int r2 = r6.end
            int r5 = r2 + 1
            r6.end = r5
            r1[r2] = r4
            int r0 = r0 + 1
            r6.pos = r0
            goto L69
        L82:
            int r0 = r6.pos
            int r1 = r6.length
            if (r0 == r1) goto L9a
            char[] r1 = r6.chars
            char r2 = r1[r0]
            r4 = 44
            if (r2 == r4) goto L9a
            char r2 = r1[r0]
            r4 = 43
            if (r2 == r4) goto L9a
            char r0 = r1[r0]
            if (r0 != r3) goto L6
        L9a:
            java.lang.String r0 = new java.lang.String
            char[] r1 = r6.chars
            int r2 = r6.beg
            int r3 = r6.cur
            int r3 = r3 - r2
            r0.<init>(r1, r2, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.tls.DistinguishedNameParser.escapedAV():java.lang.String");
    }

    private char getEscaped() {
        this.pos++;
        int i = this.pos;
        if (i == this.length) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
        }
        char c = this.chars[i];
        if (c != ' ' && c != '%' && c != '\\' && c != '_') {
            switch (c) {
                case '\"':
                case '#':
                    break;
                default:
                    switch (c) {
                        case '*':
                        case '+':
                        case ',':
                            break;
                        default:
                            switch (c) {
                                case ';':
                                case '<':
                                case '=':
                                case '>':
                                    break;
                                default:
                                    return getUTF8();
                            }
                    }
            }
        }
        return this.chars[this.pos];
    }

    private char getUTF8() {
        int count;
        int res;
        int res2 = getByte(this.pos);
        this.pos++;
        if (res2 < 128) {
            return (char) res2;
        }
        if (res2 < 192 || res2 > 247) {
            return '?';
        }
        if (res2 <= 223) {
            count = 1;
            res = res2 & 31;
        } else if (res2 <= 239) {
            count = 2;
            res = res2 & 15;
        } else {
            count = 3;
            res = res2 & 7;
        }
        for (int i = 0; i < count; i++) {
            this.pos++;
            int i2 = this.pos;
            if (i2 == this.length || this.chars[i2] != '\\') {
                return '?';
            }
            this.pos = i2 + 1;
            int b = getByte(this.pos);
            this.pos++;
            if ((b & 192) != 128) {
                return '?';
            }
            res = (res << 6) + (b & 63);
        }
        return (char) res;
    }

    private int getByte(int position) {
        int b1;
        int b2;
        if (position + 1 >= this.length) {
            throw new IllegalStateException("Malformed DN: " + this.dn);
        }
        char c = this.chars[position];
        if (c >= '0' && c <= '9') {
            b1 = c - '0';
        } else if (c >= 'a' && c <= 'f') {
            b1 = c - 'W';
        } else if (c >= 'A' && c <= 'F') {
            b1 = c - '7';
        } else {
            throw new IllegalStateException("Malformed DN: " + this.dn);
        }
        char c2 = this.chars[position + 1];
        if (c2 >= '0' && c2 <= '9') {
            b2 = c2 - '0';
        } else if (c2 >= 'a' && c2 <= 'f') {
            b2 = c2 - 'W';
        } else if (c2 >= 'A' && c2 <= 'F') {
            b2 = c2 - '7';
        } else {
            throw new IllegalStateException("Malformed DN: " + this.dn);
        }
        return (b1 << 4) + b2;
    }

    public String findMostSpecific(String attributeType) {
        this.pos = 0;
        this.beg = 0;
        this.end = 0;
        this.cur = 0;
        this.chars = this.dn.toCharArray();
        String attType = nextAT();
        if (attType == null) {
            return null;
        }
        do {
            String attValue = "";
            int i = this.pos;
            if (i == this.length) {
                return null;
            }
            switch (this.chars[i]) {
                case '\"':
                    attValue = quotedAV();
                    break;
                case '#':
                    attValue = hexAV();
                    break;
                case '+':
                case ',':
                case ';':
                    break;
                default:
                    attValue = escapedAV();
                    break;
            }
            if (attributeType.equalsIgnoreCase(attType)) {
                return attValue;
            }
            int i2 = this.pos;
            if (i2 >= this.length) {
                return null;
            }
            char[] cArr = this.chars;
            if (cArr[i2] != ',' && cArr[i2] != ';' && cArr[i2] != '+') {
                throw new IllegalStateException("Malformed DN: " + this.dn);
            }
            this.pos++;
            attType = nextAT();
        } while (attType != null);
        throw new IllegalStateException("Malformed DN: " + this.dn);
    }
}
