package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.mimex.target60.LStrings60;
import info.teksol.mc.emulator.mimex.target70.LStrings70;
import info.teksol.mc.emulator.mimex.target80.LStrings80;
import info.teksol.mc.emulator.mimex.target81.LStrings81;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LStrings {
    boolean canParseInt(String s);

    boolean canParsePositiveInt(String s);

    int parseInt(String s);

    int parseInt(String s, int defaultValue);

    int parseInt(String s, int radix, int defaultValue);

    int parseInt(String s, int radix, int defaultValue, int start, int end);

    long parseLong(String s, int radix, int start, int end, long defaultValue);

    double parseDouble(String value, double defaultValue);

    static LStrings create(ProcessorVersion version) {
        return switch (version) {
            case V6         -> new LStrings60();
            case V7, V7A    -> new LStrings70();
            case V8A        -> new LStrings80();
            case V8B, MAX   -> new LStrings81();
        };
    }
}
