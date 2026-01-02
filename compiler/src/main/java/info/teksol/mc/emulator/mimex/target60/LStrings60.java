package info.teksol.mc.emulator.mimex.target60;

import info.teksol.mc.emulator.mimex.LStrings;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LStrings60 implements LStrings {

    @Override
    public boolean canParseInt(String s) {
        return parseInt(s) != Integer.MIN_VALUE;
    }

    @Override
    public boolean canParsePositiveInt(String s) {
        int p = parseInt(s);
        return p >= 0;
    }

    /// Returns Integer.MIN_VALUE if parsing failed.
    @Override
    public int parseInt(String s) {
        return parseInt(s, Integer.MIN_VALUE);
    }

    @Override
    public int parseInt(String s, int defaultValue) {
        return parseInt(s, 10, defaultValue);
    }

    @Override
    public int parseInt(String s, int radix, int defaultValue) {
        boolean negative = false;
        int i = 0, len = s.length(), limit = -2147483647;
        if (len <= 0) {
            return defaultValue;
        } else {
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    negative = true;
                    limit = -2147483648;
                } else if (firstChar != '+') {
                    return defaultValue;
                }

                if (len == 1) return defaultValue;

                ++i;
            }

            int digit, result;
            for (result = 0; i < len; result -= digit) {
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0) {
                    return defaultValue;
                }

                result *= radix;
                if (result < limit + digit) {
                    return defaultValue;
                }
            }

            return negative ? result : -result;
        }
    }

    @Override
    public int parseInt(String s, int radix, int defaultValue, int start, int end) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long parseLong(String s, int radix, int start, int end, long defaultValue) {
        boolean negative = false;
        int i = start, len = end - start;
        long limit = -9223372036854775807L;
        if (len <= 0) {
            return defaultValue;
        } else {
            char firstChar = s.charAt(i);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    negative = true;
                    limit = -9223372036854775808L;
                } else if (firstChar != '+') {
                    return defaultValue;
                }

                if (len == 1) return defaultValue;

                ++i;
            }

            long result;
            int digit;
            for (result = 0L; i < end; result -= digit) {
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0) {
                    return defaultValue;
                }

                result *= radix;
                if (result < limit + (long) digit) {
                    return defaultValue;
                }
            }

            return negative ? result : -result;
        }
    }

    /**
     * Faster double parser that doesn't throw exceptions.
     */
    @Override
    public double parseDouble(String value, double defaultValue) {
        int len = value.length();
        if (len == 0) return defaultValue;

        int start = 0, end = len;
        char last = value.charAt(len - 1), first = value.charAt(0);
        if (last == 'F' || last == 'f' || last == '.') {
            end--;
        }
        if (first == '+') {
            start = 1;
        }

        int dot = -1, e = -1;
        for (int i = start; i < end; i++) {
            char c = value.charAt(i);
            if (c == '.') dot = i;
            if (c == 'e' || c == 'E') e = i;
        }

        if (dot != -1 && dot < end) {
            //negation as first character
            if (dot == 1 && first == '-') {
                long dec = parseLong(value, 10, dot + 1, end, Long.MIN_VALUE);
                if (dec < 0) return defaultValue;
                return -dec / Math.pow(10, (end - dot - 1));
            }
            long whole = start == dot ? 0 : parseLong(value, 10, start, dot, Long.MIN_VALUE);
            if (whole == Long.MIN_VALUE) return defaultValue;
            long dec = parseLong(value, 10, dot + 1, end, Long.MIN_VALUE);
            if (dec < 0) return defaultValue;
            return whole + Math.copySign(dec / Math.pow(10, (end - dot - 1)), whole);
        }

        //check scientific notation
        if (e != -1) {
            long whole = parseLong(value, 10, start, e, Long.MIN_VALUE);
            if (whole == Long.MIN_VALUE) return defaultValue;
            long power = parseLong(value, 10, e + 1, end, Long.MIN_VALUE);
            if (power == Long.MIN_VALUE) return defaultValue;
            return whole * pow(10, power);
        }

        //parse as standard integer
        long out = parseLong(value, 10, start, end, Long.MIN_VALUE);
        return out == Long.MIN_VALUE ? defaultValue : out;
    }

    private float pow(float a, float b) {
        return (float) Math.pow(a, b);
    }
}
