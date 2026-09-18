package info.teksol.mc.emulator.mimex.target82;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.EmulatorMessageHandler;
import info.teksol.mc.emulator.mimex.LGlobalVars;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.target81.LAssembler81;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.util.UtfUtils;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LAssembler82 extends LAssembler81 {

    public LAssembler82(EmulatorMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, LGlobalVars globalVars,
            boolean privileged) {
        super(errorHandler, metadata, strings, globalVars, privileged);
    }

    /// @param symbol the string literal, numeric literal, or variable name. Leading or trailing spaces are not allowed.
    /// @return a variable ID by name. This may be a constant variable referring to a number or object.
    @Override
    public LVar var(String symbol) {
        LVar global = globalVars.get(symbol, privileged);
        if (global != null) {
            return global;
        }

        //string case
        if(symbol.length() > 1 && symbol.charAt(0) == '\"' && symbol.charAt(symbol.length() - 1) == '\"'){
            return putConst("___" + symbol, UtfUtils.unescape(symbol.substring(1, symbol.length() - 1)));
        }

        double value = parseDouble(symbol);

        if (Double.isNaN(value)) {
            return putVar(symbol);
        } else {
            // This check is only present in target 8.1+ but has almost no effect on actual code
            if (Double.isInfinite(value)) value = 0.0;
            //this creates a hidden const variable with the specified value
            return putConst("___" + value, value);
        }
    }

    @Override
    public double parseDouble(String symbol){
        //fail fast for obvious non-numbers
        if(symbol.isEmpty() || !isNumStart(symbol.charAt(0))) return Double.NaN;

        //parse hex/binary syntax
        if(symbol.startsWith("0b")) return parseHexOrBin(false, symbol, true, 2);
        if(symbol.startsWith("+0b")) return parseHexOrBin(false, symbol, true, 3);
        if(symbol.startsWith("-0b")) return parseHexOrBin(true, symbol, true, 3);
        if(symbol.startsWith("0x")) return parseHexOrBin(false, symbol, false, 2);
        if(symbol.startsWith("+0x")) return parseHexOrBin(false, symbol, false, 3);
        if(symbol.startsWith("-0x")) return parseHexOrBin(true, symbol, false, 3);
        if(symbol.startsWith("%[") && symbol.endsWith("]") && symbol.length() > 3) return parseNamedColor(symbol);
        if(symbol.startsWith("%") && (symbol.length() == 7 || symbol.length() == 9)) return parseColor(symbol);

        return strings.parseDouble(symbol, Double.NaN);
    }

    boolean isNumStart(char c){
        //note that 'e10' isn't a valid number; '%ffffff' is. Hex numbers start with '0x'.
        return c >= '0' && c <= '9' || c == '.' || c == '-' || c == '+' || c == '%';
    }

    //parses *unsigned* hex or bin number, including negative ones (0xffffffffffffffff as -1)
    //detects overflow by input length and uses bit manipulation to avoid signed arithmetics
    double parseHexOrBin(boolean negative, String s, boolean binary, int offset){
        int end = s.length();
        if(offset >= end) return Double.NaN;

        int pos = offset;
        while(pos < end && s.charAt(pos) == '0') pos ++;    //skip leading zeros to avoid incorrect overflow detection

        int shift = binary ? 1 : 4;
        if(end - pos > 64 / shift) return Double.NaN;

        long acc = 0;
        int radix = 1 << shift;
        while(pos < end){
            int digit = Character.digit(s.charAt(pos), radix);
            if(digit < 0) return Double.NaN;
            acc = acc << shift | digit;
            pos ++;
        }
        return negative ? -acc : acc;
    }
}
