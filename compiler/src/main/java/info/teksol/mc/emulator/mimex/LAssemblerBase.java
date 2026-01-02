package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.LInstruction;
import info.teksol.mc.emulator.LVar;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public abstract class LAssemblerBase implements LAssembler {
    protected final EmulatorErrorHandler errorHandler;
    protected final MindustryMetadata metadata;
    protected final LStrings strings;
    protected final LGlobalVars globalVars;

    protected boolean error;
    private final List<LInstruction> instructions = new ArrayList<>();
    private final Map<String, LVar> vars = new LinkedHashMap<>();

    protected final LVar counter;
    protected final LVar unit;
    protected final LVar thisv;
    protected final LVar thisx;
    protected final LVar thisy;
    protected final LVar links;
    protected final LVar ipt;

    public LAssemblerBase(EmulatorErrorHandler errorHandler, MindustryMetadata metadata, LStrings strings,
            LGlobalVars globalVars) {
        this.errorHandler = errorHandler;
        this.metadata = metadata;
        this.strings = strings;
        this.globalVars = globalVars;

        // Create default variables
        counter = putVar("@counter");
        counter.setnum(0);
        unit = putConst("@unit", null);
        thisv = putConst("@this", null);
        thisx = putConst("@thisx", 0.0);
        thisy = putConst("@thisy", 0.0);
        links = putConst("@links", 0.0);
        ipt = putConst("@ipt", 0.0);
    }

    /// Attempts to parse the symbol as a double value, in the same way as the target Mindustry version
    ///
    /// @return the parsed value, or NaN when cannot be parsed
    protected abstract double parseDouble(String symbol);

    /// @return a variable ID by name. This may be a constant variable referring to a number or object.
    @Override
    public LVar var(String symbol) {
        LVar global = globalVars.get(symbol);
        if (global != null) {
            return global;
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
            // This check is only present in target 8.1+ but has almost no effect on actual code
            if (Double.isInfinite(value)) value = 0.0;
            //this creates a hidden const variable with the specified value
            return putConst("___" + value, value);
        }
    }

    /// Adds a constant value by name.
    protected LVar putConst(String name, @Nullable Object value) {
        LVar var = putVar(name);
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
    protected LVar putVar(String name) {
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

    @Override
    public Map<String, LVar> getVars() {
        return vars;
    }

    @Override
    public List<LInstruction> getInstructions() {
        return instructions;
    }

    @Override
    public boolean isError() {
        return error;
    }
}
