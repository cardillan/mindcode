package info.teksol.mc.emulator.mimex.target81;

import info.teksol.mc.emulator.mimex.EmulatorErrorHandler;
import info.teksol.mc.emulator.mimex.LGlobalVars;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.target80.LAssembler80;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LAssembler81 extends LAssembler80 {
    private static final long invalidNumNegative = Long.MIN_VALUE;
    private static final long invalidNumPositive = Long.MAX_VALUE;

    public LAssembler81(EmulatorErrorHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars) {
        super(errorHandler, metadata, strings, globalVars);
    }
}
