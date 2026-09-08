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
}
