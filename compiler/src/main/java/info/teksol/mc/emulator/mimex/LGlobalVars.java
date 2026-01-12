package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.target60.LGlobalVars60;
import info.teksol.mc.emulator.mimex.target70.LGlobalVars70;
import info.teksol.mc.emulator.mimex.target80.LGlobalVars80;
import info.teksol.mc.emulator.mimex.target81.LGlobalVars81;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LGlobalVars {

    @Nullable LVar get(String symbol);

    void update(double tick);

    static LGlobalVars create(MindustryMetadata metadata, boolean privileged) {
        return switch (metadata.getProcessorVersion()) {
            case V6         -> new LGlobalVars60(metadata, privileged);
            case V7, V7A    -> new LGlobalVars70(metadata, privileged);
            case V8A        -> new LGlobalVars80(metadata, privileged);
            case V8B, MAX   -> new LGlobalVars81(metadata, privileged);
        };
    }
}
