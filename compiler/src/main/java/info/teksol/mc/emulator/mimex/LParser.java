package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.mimex.target60.LParser60;
import info.teksol.mc.emulator.mimex.target70.LParser70;
import info.teksol.mc.emulator.mimex.target80.LParser80;
import info.teksol.mc.emulator.mimex.target81.LParser81;
import info.teksol.mc.emulator.mimex.target82.LParser82;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface LParser {
    LParser includeComments();
    LParser includeLabels();

    List<LStatement> parse();

    boolean isError();

    static LParser create(ParserMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged, boolean enforceInstructionLimit) {
        return switch (metadata.getProcessorVersion()) {
            case V6         -> new LParser60(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
            case V7, V7A    -> new LParser70(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
            case V8A        -> new LParser80(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
            case V8B        -> new LParser81(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
            case V8C, MAX   -> new LParser82(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
        };
    }
}
