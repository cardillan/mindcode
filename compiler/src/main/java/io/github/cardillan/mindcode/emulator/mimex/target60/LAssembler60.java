package io.github.cardillan.mindcode.emulator.mimex.target60;

import io.github.cardillan.mindcode.emulator.mimex.EmulatorMessageHandler;
import io.github.cardillan.mindcode.emulator.mimex.LAssemblerBase;
import io.github.cardillan.mindcode.emulator.mimex.LGlobalVars;
import io.github.cardillan.mindcode.emulator.mimex.LStrings;
import io.github.cardillan.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LAssembler60 extends LAssemblerBase {
    private static final int invalidNum = Integer.MIN_VALUE;

    public LAssembler60(EmulatorMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars,
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
