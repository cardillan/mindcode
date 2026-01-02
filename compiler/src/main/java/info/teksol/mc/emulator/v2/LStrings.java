package info.teksol.mc.emulator.v2;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LStrings {
    boolean canParseInt(String s);

    boolean canParsePositiveInt(String s);

    int parseInt(String s);

    int parseInt(String s, int defaultValue);

    int parseInt(String s, int radix, int defaultValue);

    long parseLong(String s, int radix, int start, int end, long defaultValue);

    double parseDouble(String value, double defaultValue);
}
