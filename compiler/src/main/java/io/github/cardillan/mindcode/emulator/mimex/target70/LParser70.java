package io.github.cardillan.mindcode.emulator.mimex.target70;

import io.github.cardillan.mindcode.emulator.mimex.LStrings;
import io.github.cardillan.mindcode.emulator.mimex.ParserMessageHandler;
import io.github.cardillan.mindcode.emulator.mimex.target60.LParser60;
import io.github.cardillan.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public class LParser70 extends LParser60 {

    public LParser70(ParserMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged, boolean enforceInstructionLimit) {
        super(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
    }

    @Override
    protected Map<String, String> tokenChanges() {
        return Map.of(
                "@configure", "@config",
                "configure", "config"
        );
    }
}
