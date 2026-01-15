package info.teksol.mc.emulator.mimex.target80;

import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.emulator.mimex.EmulatorMessageHandler;
import info.teksol.mc.emulator.mimex.LAssemblerBase;
import info.teksol.mc.emulator.mimex.LGlobalVars;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.mimex.NamedColor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LAssembler80 extends LAssemblerBase {
    private static final long invalidNumNegative = Long.MIN_VALUE;
    private static final long invalidNumPositive = Long.MAX_VALUE;

    public LAssembler80(EmulatorMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars,
            boolean privileged) {
        super(errorHandler, metadata, strings, globalVars, privileged);
    }

    @Override
    protected double parseDouble(String symbol) {
        //parse hex/binary syntax
        if (symbol.startsWith("0b")) return parseLong(false, symbol, 2, 2, symbol.length());
        if (symbol.startsWith("+0b")) return parseLong(false, symbol, 2, 3, symbol.length());
        if (symbol.startsWith("-0b")) return parseLong(true, symbol, 2, 3, symbol.length());
        if (symbol.startsWith("0x")) return parseLong(false, symbol, 16, 2, symbol.length());
        if (symbol.startsWith("+0x")) return parseLong(false, symbol, 16, 3, symbol.length());
        if (symbol.startsWith("-0x")) return parseLong(true, symbol, 16, 3, symbol.length());
        if (symbol.startsWith("%[") && symbol.endsWith("]") && symbol.length() > 3) return parseNamedColor(symbol);
        if (symbol.startsWith("%") && (symbol.length() == 7 || symbol.length() == 9)) return parseColor(symbol);
        return strings.parseDouble(symbol, Double.NaN);
    }

    protected double parseLong(boolean negative, String s, int radix, int start, int end) {
        long usedInvalidNum = negative ? invalidNumPositive : invalidNumNegative;
        long l = strings.parseLong(s, radix, start, end, usedInvalidNum);
        return l == usedInvalidNum ? Double.NaN : negative ? -l : l;
    }

    private double parseColor(String symbol) {
        int r = strings.parseInt(symbol, 16, 0, 1, 3);
        int g = strings.parseInt(symbol, 16, 0, 3, 5);
        int b = strings.parseInt(symbol, 16, 0, 5, 7);
        int a = symbol.length() == 9 ? strings.parseInt(symbol, 16, 0, 7, 9) : 255;
        return Color.toDoubleBitsClamped(r, g, b, a);
    }

    protected double parseNamedColor(String symbol) {
        NamedColor color = metadata.getColorByName(symbol.substring(2, symbol.length() - 1));
        if (color == null && errorHandler.error(ExecutionFlag.ERR_UNKNOWN_COLOR, "Unrecognized color literal: %s", symbol)) {
            error = true;
        }
        return color == null ? Double.NaN : color.color();
    }
}
