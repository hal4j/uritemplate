package com.github.hal4j.uritemplate;

import static java.lang.Character.toChars;

public class PercentEncoder {
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    /**
     * Unreserved characters according to RFC 3986 section 2.3
     */
    public static final String UNRESERVED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~";

    public static final String GEN_DELIMS  = ":/?#[]@";
    public static final String SUB_DELIMS  = "!$&'()*+,;=";
    /**
     * Reserved characters according to RFC 3986
     */
    public static final String RESERVED = GEN_DELIMS + SUB_DELIMS;

    /**
     * Encoder of literals according to RFC 6570 section 3.1
     */
    public static PercentEncoder LITERAL = new PercentEncoder(RESERVED + UNRESERVED);

    public static PercentEncoder DEFAULT = new PercentEncoder(UNRESERVED);

    private final boolean[] reserved;

    private PercentEncoder(String allowed) {
        int max = 0;
        for (int i = 0; i < allowed.length(); i++) {
            char c = allowed.charAt(i);
            if (max < c) max = c;
        }
        reserved = new boolean[max + 1];
        for (int i = 0; i < allowed.length(); i++) {
            char c = allowed.charAt(i);
            reserved[c] = true;
        }
    }

    public String encode(String s) {
        // scan the string to check if encoding is really necessary: if not, just return the original string,
        // saving the time on code point processing
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= reserved.length || !reserved[c]) return encodeStartingFrom(s, i);
        }
        return s;
    }

    private String encodeStartingFrom(String s, int index) {
        StringBuilder sb = new StringBuilder();
        s.codePoints().mapToObj(this::encode).forEach(sb::append);
        return sb.toString();
    }

    private char[] encode(int cp) {
        if ((cp < reserved.length) && reserved[cp]) return toChars(cp);
        char[] buf;
        if (cp <= 0x7F) {  // 1 byte UTF-8 characters
            buf = new char[3];
            buf[0] = '%';
            buf[2] = HEX[cp & 0xF];
            buf[1] = HEX[cp >>> 4];
        } else if (cp <= 0x7ff) { // 2 byte UTF-8 characters
            buf = new char[6];
            buf[3] = '%'; buf[5] = HEX[cp & 0xF]; cp >>>= 4; buf[4] = HEX[0x8 | (cp & 0x3)];

            cp >>>= 2;
            buf[0] = '%'; buf[2] = HEX[cp & 0xF]; cp >>>= 4; buf[1] = HEX[0xC | cp];
        } else if (cp <= 0xffff) { // 3 byte UTF-8 characters
            buf = new char[9];
            buf[6] = '%'; buf[8] = HEX[cp & 0xF]; cp >>>= 4; buf[7] = HEX[0x8 | (cp & 0x3)];

            cp >>>= 2;
            buf[3] = '%'; buf[5] = HEX[cp & 0xF]; cp >>>= 4; buf[4] = HEX[0x8 | (cp & 0x3)];

            cp >>>= 2;
            buf[0] = '%'; buf[1] = 'E'; buf[2] = HEX[cp];
        } else if (cp <= 0x10ffff) { // 4 byte UTF-8 characters
            buf = new char[12];
            buf[9] = '%'; buf[11] = HEX[cp & 0xF]; cp >>>= 4; buf[10] = HEX[0x8 | (cp & 0x3)];

            cp >>>= 2;
            buf[6] = '%'; buf[8] = HEX[cp & 0xF]; cp >>>= 4; buf[7] = HEX[0x8 | (cp & 0x3)];

            cp >>>= 2;
            buf[3] = '%'; buf[5] = HEX[cp & 0xF]; cp >>>= 4; buf[4] = HEX[0x8 | (cp & 0x3)];

            cp >>>= 2;
            buf[0] = '%'; buf[1] = 'F'; buf[2] = HEX[cp & 0x7];
        } else {
            // should be unreachable unless there's a bug in standard library
            throw new UnsupportedOperationException("Cannot encode code point " + cp);
        }
        return buf;
    }

}
