package info.teksol.mc.emulator.v2;

import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public abstract class LAssemblerBase implements LAssembler {
    protected final LStrings strings;
    protected final MindustryMetadata metadata;

    private final Map<String, LVar> globalVars;
    private final Map<String, LVar> vars = new HashMap<>();

    private LInstruction[] instructions;

    public LAssemblerBase(LStrings strings, MindustryMetadata metadata) {
        this.strings = strings;
        this.metadata = metadata;
        this.globalVars = createGlobalVariables();
        createDefaultVariables();
    }

    /// Creates the constant variables for the target Mindustry version
    /// Constant variables aren't present in the processor's variable map
    protected abstract Map<String, LVar> createGlobalVariables();

    /// Creates the default processor variables for the target Mindustry version within this instance
    /// Default variables are present in the processor's variable map
    protected abstract void createDefaultVariables();

    /// Attempts to parse the symbol as a double value, in the same way as the target Mindustry version
    ///
    /// @return the parsed value, or NaN when cannot be parsed
    protected abstract double parseDouble(String symbol);

    // Note: this code is implemented in the LStatement class in Mindustry, which contains a specific

    /// @return a variable ID by name. This may be a constant variable referring to a number or object.
    @Override
    public LVar var(String symbol) {
        if (globalVars.containsKey(symbol)) {
            return globalVars.get(symbol);
        }

        // Not needed - kept for compliance
        symbol = symbol.trim();

        //string case
        if (!symbol.isEmpty() && symbol.charAt(0) == '\"' && symbol.charAt(symbol.length() - 1) == '\"') {
            return putConst("___" + symbol, symbol.substring(1, symbol.length() - 1).replace("\\n", "\n"));
        }

        // Not needed - kept for compliance
        //remove spaces for non-strings
        symbol = symbol.replace(' ', '_');

        double value = parseDouble(symbol);

        if (Double.isNaN(value)) {
            return putVar(symbol);
        } else {
            if (Double.isInfinite(value)) value = 0.0;
            //this creates a hidden const variable with the specified value
            return putConst("___" + value, value);
        }
    }

    public LVar putConst(String name, @Nullable Object value) {
        return putConst(vars, name, value);
    }

    public LVar putVar(String name) {
        return putVar(vars, name);
    }


    /// Adds a constant value by name.
    protected LVar putConst(Map<String, LVar> vars, String name, @Nullable Object value) {
        LVar var = putVar(vars, name);
        if (value instanceof Number number) {
            var.isobj = false;
            var.numval = number.doubleValue();
            var.objval = null;
        } else {
            var.isobj = true;
            var.objval = value;
        }
        var.constant = true;
        return var;
    }

    /// Registers a variable name mapping.
    protected LVar putVar(Map<String, LVar> vars, String name) {
        if (vars.containsKey(name)) {
            return vars.get(name);
        } else {
            //variables are null objects by default
            LVar var = new LVar(name);
            var.isobj = true;
            vars.put(name, var);
            return var;
        }
    }

    public LVar getVar(String name) {
        return vars.get(name);
    }
}
