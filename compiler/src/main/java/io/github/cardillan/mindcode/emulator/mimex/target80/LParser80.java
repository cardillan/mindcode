package io.github.cardillan.mindcode.emulator.mimex.target80;

import io.github.cardillan.mindcode.emulator.mimex.LStrings;
import io.github.cardillan.mindcode.emulator.mimex.ParserMessageHandler;
import io.github.cardillan.mindcode.emulator.mimex.target70.LParser70;
import io.github.cardillan.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LParser80 extends LParser70 {

    public LParser80(ParserMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged, boolean enforceInstructionLimit) {
        super(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
    }

}
