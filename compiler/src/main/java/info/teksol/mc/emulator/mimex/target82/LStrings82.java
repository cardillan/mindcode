package info.teksol.mc.emulator.mimex.target82;

import info.teksol.mc.emulator.mimex.target70.LStrings70;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LStrings82 extends LStrings70 {

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

            long multmin = limit / radix;
            long result;
            int digit;
            for (result = 0L; i < end; result -= digit) {
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0 || result < multmin) {
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

    /// Faster double parser that doesn't throw exceptions.
    @Override
    public double parseDouble(String value, double defaultValue) {
        int len = value.length();
        if (len == 0) return defaultValue;

        int sign = 1;
        int start = 0, end = len;
        char last = value.charAt(len - 1), first = value.charAt(0);
        if (last == 'F' || last == 'f' || last == '.') {
            end--;
        }
        if (first == '+') {
            start = 1;
        }
        if (first == '-') {
            start = 1;
            sign = -1;
        }
        if (start >= end) return defaultValue;

        int dot = -1, e = -1;
        int dotCount = 0, eCount = 0;
        for (int i = start; i < end; i++) {
            char c = value.charAt(i);
            if (c == '.') {
                dot = i;
                dotCount++;
            }
            if (c == 'e' || c == 'E') {
                e = i;
                eCount++;
            }
        }
        if (dotCount > 1 || eCount > 1) return defaultValue;
        if (dot != -1 && e != -1 && dot > e) return defaultValue;

        int mantissaEnd = (e != -1) ? e : end;

        long exponent = 0;
        if (e != -1) {
            if (e + 1 >= end) return defaultValue;
            exponent = parseLong(value, 10, e + 1, end, Long.MIN_VALUE);
            if (exponent == Long.MIN_VALUE) return defaultValue;
        }

        if (dot != -1 && dot < end) {
            //negation as first character
            long whole = start == dot ? 0 : parseLong(value, 10, start, dot, Long.MIN_VALUE);
            if (whole < 0) return defaultValue;

            int decDigits = mantissaEnd - (dot + 1);
            if (decDigits == 0) {
                return whole * Math.pow(10, exponent) * sign;
            }

            //a long holds 18 decimal digits safely, and a double can't represent more precision than that anyway
            int used = Math.min(decDigits, 18);
            long dec = parseLong(value, 10, dot + 1, dot + 1 + used, Long.MIN_VALUE);
            if (dec < 0) return defaultValue;

            //truncated digits still have to be valid digits
            for (int i = dot + 1 + used; i < mantissaEnd; i++) {
                char c = value.charAt(i);
                if (c < '0' || c > '9') return defaultValue;
            }

            double pow = Math.pow(10, used);
            long p = (long) pow;
            double mantissa;
            if (whole <= (Long.MAX_VALUE - dec) / p) {
                //fits in a long, keeps the original (more accurate) path
                mantissa = (whole * p + dec) / pow;
            } else {
                mantissa = whole + dec / pow;
            }
            return mantissa * Math.pow(10, exponent) * sign;
        }

        //check scientific notation
        if (e != -1) {
            long whole = parseLong(value, 10, start, e, Long.MIN_VALUE);
            if (whole == Long.MIN_VALUE) return defaultValue;
            return whole * Math.pow(10, exponent) * sign;
        }

        //parse as standard integer
        long out = parseLong(value, 10, start, end, Long.MIN_VALUE);
        return out == Long.MIN_VALUE ? defaultValue : out * sign;
    }
}
