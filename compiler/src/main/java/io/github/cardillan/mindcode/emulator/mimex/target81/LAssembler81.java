package io.github.cardillan.mindcode.emulator.mimex.target81;

import io.github.cardillan.mindcode.emulator.mimex.EmulatorMessageHandler;
import io.github.cardillan.mindcode.emulator.mimex.LGlobalVars;
import io.github.cardillan.mindcode.emulator.mimex.LStrings;
import io.github.cardillan.mindcode.emulator.mimex.target80.LAssembler80;
import io.github.cardillan.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LAssembler81 extends LAssembler80 {

    public LAssembler81(EmulatorMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars,
            boolean privileged) {
        super(errorHandler, metadata, strings, globalVars, privileged);
    }
}
