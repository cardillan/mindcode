package info.teksol.mc.emulator.v2;

import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;

public class V6Parser extends LParserBase {

    public V6Parser(MindustryMetadata metadata, String text, boolean privileged) {
        super(new V6Strings(), metadata, text, privileged);
    }
}
