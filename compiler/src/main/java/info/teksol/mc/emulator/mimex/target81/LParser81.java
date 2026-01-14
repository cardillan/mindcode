package info.teksol.mc.emulator.mimex.target81;

import info.teksol.mc.emulator.mimex.EmulatorErrorHandler;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.target80.LParser80;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LParser81 extends LParser80 {

    public LParser81(EmulatorErrorHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged) {
        super(errorHandler, metadata, strings, code, privileged);
    }

}
