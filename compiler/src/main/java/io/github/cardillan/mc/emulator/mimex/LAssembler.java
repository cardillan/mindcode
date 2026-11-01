package io.github.cardillan.mc.emulator.mimex;

import io.github.cardillan.mc.emulator.LInstruction;
import io.github.cardillan.mc.emulator.LVar;
import io.github.cardillan.mc.emulator.mimex.target60.LAssembler60;
import io.github.cardillan.mc.emulator.mimex.target70.LAssembler70;
import io.github.cardillan.mc.emulator.mimex.target80.LAssembler80;
import io.github.cardillan.mc.emulator.mimex.target81.LAssembler81;
import io.github.cardillan.mc.emulator.mimex.target82.LAssembler82;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

// Creates variables
@NullMarked
public interface LAssembler {
    boolean isError();

    List<LInstruction> getInstructions();

    Map<String, LVar> getVars();

    LVar var(String symbol);

    static LAssembler create(EmulatorMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars,
            boolean privileged) {
        return switch (metadata.getProcessorVersion()) {
            case V6         -> new LAssembler60(errorHandler, metadata, strings, globalVars, privileged);
            case V7, V7A    -> new LAssembler70(errorHandler, metadata, strings, globalVars, privileged);
            case V8A        -> new LAssembler80(errorHandler, metadata, strings, globalVars, privileged);
            case V8B        -> new LAssembler81(errorHandler, metadata, strings, globalVars, privileged);
            case V8C, MAX   -> new LAssembler82(errorHandler, metadata, strings, globalVars, privileged);
        };
    }
}
