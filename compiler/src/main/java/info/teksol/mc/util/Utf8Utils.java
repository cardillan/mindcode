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

    public static boolean canEncode(int value) {
        return value >= 0 && value < UTF16_END && (value < SURROGATE_START || value >= SURROGATE_END) && !INVALID_CHARS.contains(value);
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
}
