package info.teksol.mc.emulator.mimex.target82;

import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.ParserMessageHandler;
import info.teksol.mc.emulator.mimex.target81.LParser81;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LParser82 extends LParser81 {

    public LParser82(ParserMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged, boolean enforceInstructionLimit) {
        super(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
    }

    @Override
    protected void checkRead() {
        super.checkRead();

        if (tokens[0].equals("status")) {
            if (metadata.getStatusEffects().contains(tokens[1])) {
                tokens[1] = "@status-" + tokens[1];
            }
        }
    }
}
