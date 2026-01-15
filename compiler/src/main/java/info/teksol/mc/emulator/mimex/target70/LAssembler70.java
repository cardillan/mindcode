package info.teksol.mc.emulator.mimex.target70;

import info.teksol.mc.emulator.mimex.EmulatorMessageHandler;
import info.teksol.mc.emulator.mimex.LAssemblerBase;
import info.teksol.mc.emulator.mimex.LGlobalVars;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LAssembler70 extends LAssemblerBase {
    private static final int invalidNum = Integer.MIN_VALUE;

    public LAssembler70(EmulatorMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars,
            boolean privileged) {
        super(errorHandler, metadata, strings, globalVars, privileged);
    }

    @Override
    protected double parseDouble(String symbol) {
        double d = parseDoubleInternal(symbol);
        return d == invalidNum ? Double.NaN : d;
    }

    private double parseDoubleInternal(String symbol) {
        //parse hex/binary syntax
        if (symbol.startsWith("0b")) return strings.parseLong(symbol, 2, 2, symbol.length(), invalidNum);
        if (symbol.startsWith("0x")) return strings.parseLong(symbol, 16, 2, symbol.length(), invalidNum);
        if (symbol.startsWith("%") && (symbol.length() == 7 || symbol.length() == 9)) return parseColor(symbol);
        return strings.parseDouble(symbol, invalidNum);
    }

    private double parseColor(String symbol){
        int r = strings.parseInt(symbol, 16, 0, 1, 3);
        int g = strings.parseInt(symbol, 16, 0, 3, 5);
        int b = strings.parseInt(symbol, 16, 0, 5, 7);
        int a = symbol.length() == 9 ? strings.parseInt(symbol, 16, 0, 7, 9) : 255;
        return Color.toDoubleBitsClamped(r, g, b, a);
    }
}
