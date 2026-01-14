package info.teksol.mc.emulator.mimex.target60;

import info.teksol.mc.emulator.mimex.EmulatorErrorHandler;
import info.teksol.mc.emulator.mimex.LAssemblerBase;
import info.teksol.mc.emulator.mimex.LGlobalVars;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LAssembler60 extends LAssemblerBase {
    private static final int invalidNum = Integer.MIN_VALUE;

    public LAssembler60(EmulatorErrorHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars,
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
        return strings.parseDouble(symbol, invalidNum);
    }
}
