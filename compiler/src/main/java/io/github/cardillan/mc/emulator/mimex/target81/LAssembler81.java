package io.github.cardillan.mc.emulator.mimex.target81;

import io.github.cardillan.mc.emulator.mimex.EmulatorMessageHandler;
import io.github.cardillan.mc.emulator.mimex.LGlobalVars;
import io.github.cardillan.mc.emulator.mimex.LStrings;
import io.github.cardillan.mc.emulator.mimex.target80.LAssembler80;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LAssembler81 extends LAssembler80 {

    public LAssembler81(EmulatorMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars,
            boolean privileged) {
        super(errorHandler, metadata, strings, globalVars, privileged);
    }
}
