package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.mindcode.logic.mimex.LVariable;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public abstract class LGlobalVarsBase implements LGlobalVars {
    protected final Map<String, LVar> vars = new HashMap<>();

    public LGlobalVarsBase(MindustryMetadata metadata) {
        createContentVariables(metadata);

        metadata.getAllLVars().stream()
                .filter(LVariable::isEmulatedVariable)
                .forEach(this::put);

        update("@mapw", 200);
        update("@maph", 300);
    }

    protected abstract void createContentVariables(MindustryMetadata metadata);

    @Override
    public @Nullable LVar get(String symbol, boolean privileged) {
        LVar var = vars.get(symbol);
        return var != null && var.privileged && !privileged ? vars.get("null") : var;
    }

    protected LVar getExisting(String symbol) {
        return Objects.requireNonNull(vars.get(symbol));
    }

    private void put(LVariable variable) {
        LVar existingVar = vars.get(variable.name());
        if (existingVar != null) { //don't overwrite existing vars (see #6910)
            throw new IllegalArgumentException("Failed to add global logic variable '" + variable.name() + "', as it already exists.");
        }

        LVar var = LVar.createGlobal(variable.name(), variable.privileged());
        var.constant = true;
        if (!variable.object()) {
            var.isobj = false;
            var.numval = variable.numericValue();
        } else {
            var.isobj = true;
            var.objval = null;
        }

        vars.put(variable.name(), var);
    }

    protected LVar put(String name, @Nullable Object value) {
        LVar existingVar = vars.get(name);
        if (existingVar != null) { //don't overwrite existing vars (see #6910)
            throw new IllegalArgumentException("Failed to add global logic variable '" + name + "', as it already exists.");
        }

        LVar var = LVar.createConst(name);
        var.isobj = true;
        var.objval = value;
        vars.put(name, var);

        return var;
    }

    protected void update(String name, double value) {
        LVar existingVar = vars.get(name);
        if (existingVar == null) {
            put(name, value);
        } else {
            existingVar.numval = value;
        }
    }

    protected LVar put(String name, double value) {
        LVar existingVar = vars.get(name);
        if (existingVar != null) { //don't overwrite existing vars (see #6910)
            throw new IllegalArgumentException("Failed to add global logic variable '" + name + "', as it already exists.");
        }

        LVar var = LVar.createConst(name);
        var.isobj = false;
        var.numval = value;
        vars.put(name, var);

        return var;
    }
}
