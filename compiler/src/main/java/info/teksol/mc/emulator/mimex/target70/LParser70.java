package info.teksol.mc.emulator.mimex.target70;

import info.teksol.mc.emulator.mimex.EmulatorErrorHandler;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.target60.LParser60;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public class LParser70 extends LParser60 {

    public LParser70(EmulatorErrorHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged) {
        super(errorHandler, metadata, strings, code, privileged);
    }

    @Override
    protected Map<String, String> tokenChanges() {
        return Map.of(
                "@configure", "@config",
                "configure", "config"
        );
    }
}
