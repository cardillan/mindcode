package info.teksol.mc.util;

import java.util.Set;

public class UtfUtils {
    public static final int SAFE_START = 93;
    public static final int SURROGATE_START = 0xD800;
    public static final int SURROGATE_END = 0xE000;
    public static final int UTF16_END = 0x10000;
    public static final int MAX_SAFE_RANGE = SURROGATE_START - SAFE_START;

    public static final Set<Integer> INVALID_CHARS = Set.of(0, (int) '\r', (int) '"', (int) '\\');

    public static boolean canEncode(int character) {
        return character >= 0 && character < UTF16_END;
    }

    public static String escape(EscapeClass escapeClass, String string) {
        StringBuilder sbr = new StringBuilder();
        for (int i = 0; i < string.length(); i++) escape(sbr, escapeClass, string.charAt(i));
        return sbr.toString();
    }

    public static String escape(EscapeClass escapeClass, int[] values) {
        StringBuilder sbr = new StringBuilder();
        for (int value : values) escape(sbr, escapeClass, value);
        return sbr.toString();
    }

    public static void escape(StringBuilder sbr, EscapeClass escapeClass, int value) {
        if (escapeClass.matches((char)value)) {
            if (escapeClass == EscapeClass.ALL) {
                sbr.append("\\u").append(String.format("%04x", value));
            } else switch (value) {
                case '\n'   -> sbr.append("\\n");
                case '\\'   -> sbr.append("\\\\");
                case '"'    -> sbr.append("\\\"");
                default     -> sbr.append("\\u").append(String.format("%04x", value));
            }
        } else {
            sbr.append((char) value);
        }
    }

    /// Decodes \n, \", \\ and uXXXX escape sequences in a string literal's contents (quotes already stripped).
    public static String unescape(String s) {
        if (s.indexOf('\\') == -1) return s;

        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                if (next == 'n') {
                    out.append('\n');
                    i++;
                    continue;
                } else if (next == '"' || next == '\\') {
                    out.append(next);
                    i++;
                    continue;
                } else if (next == 'u' && i + 5 < s.length()) {
                    // Our parser doesn't necessarily refuse invalid escape sequences
                    // We need to handle them gracefully
                    int value = 0;
                    for (int j = i + 2; j < i + 6; j++) {
                        // if any of the digits is invalid, value becomes and stays negative
                        value = value << 4 | Character.digit(s.charAt(j), 16);
                    }
                    if (value < 0) {
                        out.append('\\');
                    } else {
                        out.append((char) value);
                        i += 5;
                    }
                    continue;
                }
            }
            out.append(c);
        }
        return out.toString();
    }

    public static boolean canEncodeLegacy(int character) {
        return character >= 0 && character < UTF16_END && (character < SURROGATE_START || character >= SURROGATE_END) && !INVALID_CHARS.contains(character);
    }

    public static String legacyRecode(String s) {
        int start = s.indexOf('\\');
        if (start == -1) return s;

        StringBuilder sb = new StringBuilder(s.length());
        sb.append(s, 0, start);

        for (int pos = start; pos < s.length(); pos++) {
            if (s.charAt(pos) == '\\' && pos + 1 < s.length()) {
                pos++;
                switch (s.charAt(pos)) {
                    case 'n' -> sb.append("\\n");
                    case '"' -> sb.append("''");
                    default -> sb.append(s.charAt(pos));
                }
            } else {
                sb.append(s.charAt(pos));
            }
        }

        return sb.toString();
    }

    public static int utf16size(int c) {
        //see ByteBufferOutput.writeUTF()
        return c != 0 && c < 0x80 ? 1 : c < 0x800 ? 2 : 3;
    }

    /// Returns the number of bytes required to encode the string in UTF-8 after resolving escape sequences
    /// For string containing illegal escapes, the result is undefined
    public static int utf8EncodedLength(String s) {
        int len = 0;

        for (int pos = 0; pos < s.length(); pos++) {
            char c = s.charAt(pos);
            if (c == '\\' && pos + 1 < s.length()) {
                if (s.charAt(pos + 1) == 'u' && pos + 5 < s.length() ) {
                    int value = 0;
                    for (int j = pos + 2; j <= pos + 5; j++) {
                        value = value << 4 | Character.digit(s.charAt(j), 16);
                    }
                    pos += 5;
                    len += utf16size(value);
                } else {
                    len++;
                    pos++;
                }
            } else {
                len += utf16size(c);
            }
        }

        return len;
    }

    public static int utf8Length(String s) {
        int len = 0;
        for (int i = 0; i < s.length(); i++) {
            len += utf16size(s.charAt(i));
        }
        return len;
    }

    public static int utf8Length(char[] chars) {
        int len = 0;
        for (char c : chars) {
            len += utf16size(c);
        }
        return len;
    }
}
