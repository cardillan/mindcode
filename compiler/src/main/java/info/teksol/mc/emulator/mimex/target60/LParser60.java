package info.teksol.mc.emulator.mimex.target60;

import info.teksol.mc.emulator.mimex.EmulatorMessageHandler;
import info.teksol.mc.emulator.mimex.LParserBase;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LParser60 extends LParserBase {

    public LParser60(EmulatorMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged) {
        super(errorHandler, metadata, strings, code, privileged);
    }
}
