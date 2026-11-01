package io.github.cardillan.mc.emulator.mimex.target60;

import io.github.cardillan.mc.emulator.mimex.LParserBase;
import io.github.cardillan.mc.emulator.mimex.LStrings;
import io.github.cardillan.mc.emulator.mimex.ParserMessageHandler;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LParser60 extends LParserBase {

    public LParser60(ParserMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged, boolean enforceInstructionLimit) {
        super(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
    }
}
