package info.teksol.emulator.processor;

import info.teksol.emulator.MindustryVariable;
import info.teksol.emulator.blocks.MindustryBlock;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.mimex.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static info.teksol.emulator.processor.ProcessorFlag.ERR_INVALID_IDENTIFIER;
import static info.teksol.emulator.processor.ProcessorFlag.ERR_UNINITIALIZED_VAR;

public class MindustryVariables {
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("^[_a-zA-Z][-a-zA-Z_0-9]*$");

    private final Processor processor;
    public final MindustryVariable counter = MindustryVariable.createCounter();
    public final MindustryVariable null_ = MindustryVariable.createNull();

    private final Map<String, MindustryVariable> variables = new HashMap<>();

    public MindustryVariables(Processor processor) {
        this.processor = processor;
        variables.put("@counter", counter);
        variables.put("null", null_);
        variables.put("true", MindustryVariable.createConst("true", true));
        variables.put("false", MindustryVariable.createConst("false", false));
        variables.put("0", MindustryVariable.createConst("0", 0));
        variables.put("1", MindustryVariable.createConst("1", 1));

        variables.put("@blockCount", MindustryVariable.createConst("blockCount", BlockType.count()));
        variables.put("@unitCount", MindustryVariable.createConst("unitCount", Unit.count()));
        variables.put("@itemCount", MindustryVariable.createConst("itemCount", Item.count()));
        variables.put("@liquidCount", MindustryVariable.createConst("liquidCount", Liquid.count()));
    }

    public void addConstVariable(String name, int value) {
        variables.put(name, MindustryVariable.createConst(name, value));
    }

    public void addLinkedBlock(String name, MindustryBlock block) {
        variables.put(name, MindustryVariable.createConstObject(block));
    }

    public boolean containsVariable(String name) {
        return variables.containsKey(name);
    }

    public MindustryVariable getOrCreateVariable(LogicArgument value) {
        return variables.computeIfAbsent(value.toMlog(), this::createVariable);
    }

    public MindustryVariable getExistingVariable(LogicArgument value) {
        return variables.computeIfAbsent(value.toMlog(), this::createConstant);
    }

    private MindustryVariable createVariable(String value) {
        if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
            return MindustryVariable.createVar(null);
        } else {
            throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid identifier '" + value + "'.");
        }
    }

    private MindustryVariable createConstant(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            String name = value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\\"", "'").replace("\\\\", "\\");
            return MindustryVariable.createConstString(name);
        } else if (value.startsWith("@")) {
            MindustryContent content = MindustryContents.get(value);
            return content == null
                    ? MindustryVariable.createUnregisteredContent(value)
                    : MindustryVariable.createConstObject(content);
        } else {
            try {
                // TODO This code is duplicated in NumericLiteral. Will be removed from here once typed arguments are implemented.
                return value.startsWith("0x") ? MindustryVariable.createConst(value, Long.decode(value)) :
                        value.startsWith("0b") ? MindustryVariable.createConst(value, Long.parseLong(value, 2, value.length(), 2)) :
                                MindustryVariable.createConst(value, Double.parseDouble(value));
            } catch (NumberFormatException ex) {
                if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
                    if (processor.getFlag(ERR_UNINITIALIZED_VAR)) {
                        throw new ExecutionException(ERR_UNINITIALIZED_VAR, "Uninitialized variable '" + value + "'.");
                    } else {
                        return createVariable(value);
                    }
                } else {
                    throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid number or identifier '" + value + "'.");
                }
            }
        }
    }
}
