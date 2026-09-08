package info.teksol.mc.emulator.mimex.target81;

import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.ParserMessageHandler;
import info.teksol.mc.emulator.mimex.target80.LParser80;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LParser81 extends LParser80 {

    public LParser81(ParserMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged, boolean enforceInstructionLimit) {
        super(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);
    }

    @Override
    protected String string() {
        int from = pos;
        int utflen = 0;

        while (++pos < chars.length) {
            var c = chars[pos];
            if (c == '\n') {
                error("Missing closing quote \" before end of line.");
            } else if (c == '"') {
                break;
            }

            // See ByteBufferOutput.writeUTF()
            utflen += c != 0 && c <= 0x7F ? 1 : c <= 0x7FF ? 2 : 3;
        }

        if (pos >= chars.length || chars[pos] != '"') error("Missing closing quote \" before end of file.");
        if (utflen > 65535) error("String value too long.");

        return new String(chars, from, ++pos - from);
    }
}
