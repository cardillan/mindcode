package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.LInstruction;
import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.target60.LAssembler60;
import info.teksol.mc.emulator.mimex.target70.LAssembler70;
import info.teksol.mc.emulator.mimex.target80.LAssembler80;
import info.teksol.mc.emulator.mimex.target81.LAssembler81;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
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
            case V8B, MAX   -> new LAssembler81(errorHandler, metadata, strings, globalVars, privileged);
        };
    }
}
