package io.github.cardillan.mc.emulator.mimex;

import io.github.cardillan.mc.emulator.LVar;
import io.github.cardillan.mc.emulator.mimex.target60.LGlobalVars60;
import io.github.cardillan.mc.emulator.mimex.target70.LGlobalVars70;
import io.github.cardillan.mc.emulator.mimex.target80.LGlobalVars80;
import io.github.cardillan.mc.emulator.mimex.target81.LGlobalVars81;
import io.github.cardillan.mc.emulator.mimex.target82.LGlobalVars82;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LGlobalVars {

    @Nullable LVar get(String symbol, boolean privileged);

    void update(double tick);

    double getTime();

    static LGlobalVars create(MindustryMetadata metadata) {
        return switch (metadata.getProcessorVersion()) {
            case V6         -> new LGlobalVars60(metadata);
            case V7, V7A    -> new LGlobalVars70(metadata);
            case V8A        -> new LGlobalVars80(metadata);
            case V8B        -> new LGlobalVars81(metadata);
            case V8C, MAX   -> new LGlobalVars82(metadata);
        };
    }
}
