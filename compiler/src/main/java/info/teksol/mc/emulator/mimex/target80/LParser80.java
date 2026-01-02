package info.teksol.mc.emulator.mimex.target80;

import info.teksol.mc.emulator.mimex.EmulatorErrorHandler;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.target70.LParser70;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LParser80 extends LParser70 {

    public LParser80(EmulatorErrorHandler errorHandler, MindustryMetadata metadata, LStrings strings, String text,
            boolean privileged) {
        super(errorHandler, metadata, strings, text, privileged);
    }

}
