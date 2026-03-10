package info.teksol.mc.util;

import java.util.Set;
import java.util.stream.IntStream;

public class Utf8Utils {
    public static final int SAFE_START = 93;
    public static final int SURROGATE_START = 0xD800;
    public static final int SURROGATE_END = 0xE000;
    public static final int UTF16_END = 0x10000;
    public static final int MAX_SAFE_RANGE = SURROGATE_START - SAFE_START;

    public static final Set<Integer> INVALID_CHARS = Set.of(0, (int) '\r', (int) '"', (int) '\\');

    public static boolean canEncode(int character) {
        return character >= 0 && character < UTF16_END && (character < SURROGATE_START || character >= SURROGATE_END) && !INVALID_CHARS.contains(character);
    }

    public static String encode(IntStream values) {
        StringBuilder sbr = new StringBuilder();
        values.forEach(i -> {
            if (i == 10) {
                sbr.append("\\n");
            } else {
                sbr.appendCodePoint(i);
            }
        });
        return sbr.toString();
    }

    public static int utf8Length(String s) {
        int len = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c != 0 && c <= 0x7F) {
                len += 1;
            } else if (c <= 0x7FF) {
                len += 2;
            } else {
                len += 3;
            }
        }

        return len;
    }

    public static int utf8Length(char[] chars) {
        int len = 0;

        for (char c : chars) {
            if (c != 0 && c <= 0x7F) {
                len += 1;
            } else if (c <= 0x7FF) {
                len += 2;
            } else {
                len += 3;
            }
        }

        return len;
    }

    public static String encode(int[] values) {
        return encode(IntStream.of(values));
    }
}
