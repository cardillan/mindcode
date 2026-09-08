package info.teksol.mc.emulator.mimex.target82;

import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.ParserMessageHandler;
import info.teksol.mc.emulator.mimex.target81.LParser81;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import static info.teksol.mc.util.UtfUtils.utf16size;

@NullMarked
public class LParser82 extends LParser81 {

    public LParser82(ParserMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged, boolean enforceInstructionLimit) {
        super(errorHandler, metadata, strings, code, privileged, enforceInstructionLimit);

        //normalize CRLF and lone-CR line endings to LF in place; avoids extra allocations, and an extra \n is harmless
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\r') chars[i] = '\n';
        }
    }

    @Override
    protected String string() {
        int from = pos;
        int utflen = 0;

        while (++pos < chars.length) {
            char c = chars[pos];

            //skip over \n, \" and \\ escape sequences
            //this doesn't actually transform the sequences, as that would output invalid characters into Statement fields and break round-trip parsing
            if (c == '\\' && pos + 1 < chars.length && (chars[pos + 1] == 'n' || chars[pos + 1] == '"' || chars[pos + 1] == '\\')) {
                utflen += utf16size(chars[pos + 1]);
                pos++; //consume the escaped character too
                continue;
            }

            //uXXXX: validate 4 hex digits
            if (c == '\\' && pos + 1 < chars.length && chars[pos + 1] == 'u') {
                if (pos + 5 >= chars.length) {
                    error("Invalid \\u escape; expected 4 hex digits.");
                    continue;
                }
                int value = 0;
                for (int j = pos + 2; j <= pos + 5; j++) {
                    // if any of the digits is invalid, value becomes and stays negative
                    value = value << 4 | Character.digit(chars[j], 16);
                }
                if (value < 0) error("Invalid \\u escape; expected 4 hex digits.");
                utflen += utf16size(value);
                pos += 5; //consume u and the 4 hex digits
                continue;
            }

            if (c == '\n') {
                error("Missing closing quote \" before end of line.");
                pos--;
                break;
            } else if (c == '"') {
                break;
            }

            utflen += utf16size(c);
        }

        if (pos >= chars.length || chars[pos] != '"') error("Missing closing quote \" before end of file.");
        if (utflen > 65535) error("String value too long.");

        pos++; //move past the closing quote

        return new String(chars, from, pos - from);
    }

    @Override
    protected String token() {
        int from = pos;

        while (pos < chars.length) {
            char c = chars[pos];
            if (c == '\n' || c == ' ' || c == '#' || c == '\t' || c == ';' || c == '"') break;
            pos++;
        }

        return new String(chars, from, pos - from);
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
