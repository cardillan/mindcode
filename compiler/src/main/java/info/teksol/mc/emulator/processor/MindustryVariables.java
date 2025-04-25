package info.teksol.mc.emulator.processor;

import info.teksol.mc.emulator.MindustryVariable;
import info.teksol.mc.emulator.blocks.MindustryBlock;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.LVar;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_INVALID_IDENTIFIER;
import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_UNINITIALIZED_VAR;

@NullMarked
public class MindustryVariables {
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("^[-a-zA-Z0-9_.:*$]+$");

    private final Processor processor;
    private final InstructionProcessor instructionProcessor;
    private final MindustryMetadata metadata;
    public final MindustryVariable counter = MindustryVariable.createCounter();
    public final MindustryVariable null_ = MindustryVariable.createNull();

    private final MindustryVariable unusedVariable = MindustryVariable.createVar("0");

    private final Map<String, MindustryVariable> variables = new HashMap<>();

    public MindustryVariables(Processor processor, InstructionProcessor instructionProcessor) {
        this.processor = processor;
        this.instructionProcessor = instructionProcessor;
        this.metadata = instructionProcessor.getMetadata();

        variables.put("@counter", counter);
        variables.put("@unit", null_);
        variables.put("null", null_);
        variables.put("true", MindustryVariable.createConst("true", true));
        variables.put("false", MindustryVariable.createConst("false", false));
        variables.put("0", MindustryVariable.createConst("0", 0));
        variables.put("1", MindustryVariable.createConst("1", 1));

        metadata.getAllLVars().stream()
                .filter(LVar::isNumericConstant)
                .forEach(v -> variables.put(v.name(), MindustryVariable.createConst(v.name(), v.numericValue())));
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

    public List<MindustryVariable> getAllVariables() {
        return variables.values().stream()
                .filter(v -> !v.isMlogConstant())
                .sorted(Comparator.comparing(MindustryVariable::getName))
                .toList();
    }

    public MindustryVariable getOrCreateVariable(LogicArgument value) {
        return value == LogicVariable.unusedVariable()
                ? unusedVariable
                : variables.computeIfAbsent(value.toMlog(), this::createVariable);
    }

    public MindustryVariable getExistingVariable(LogicArgument value) {
        return value == LogicVariable.unusedVariable()
                ? unusedVariable
                : variables.computeIfAbsent(value.toMlog(), this::createConstant);
    }

    private MindustryVariable createVariable(String value) {
        if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
            return MindustryVariable.createVar(value);
        } else {
            throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid identifier '%s'.", value);
        }
    }

    private MindustryVariable createConstant(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            String name = value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\\"", "'").replace("\\\\", "\\");
            return MindustryVariable.createConstString(name);
        } else if (value.startsWith("@")) {
            MindustryContent content = metadata.getNamedContent(value);
            return content == null
                    ? MindustryVariable.createUnregisteredContent(value)
                    : MindustryVariable.createConstObject(content);
        } if (value.startsWith("%")) {
            return MindustryVariable.createConst(value, Color.parseColor(value));
        } else {
            try {
                boolean negative = value.startsWith("-");
                String number = negative ? value.substring(1) : value;
                if (number.startsWith("0x") || number.startsWith("0b")) {
                    long parsed = Long.parseUnsignedLong(number, 2, number.length(), number.startsWith("0x") ? 16 : 2);
                    return MindustryVariable.createConst(value, negative ? -parsed : parsed);
                } else {
                    return MindustryVariable.createConst(value, instructionProcessor.parseNumber(value));
                }
            } catch (NumberFormatException ex) {
                if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
                    if (processor.getFlag(ERR_UNINITIALIZED_VAR)) {
                        throw new ExecutionException(ERR_UNINITIALIZED_VAR, "Uninitialized variable '%s'.", value);
                    } else {
                        return createVariable(value);
                    }
                } else {
                    throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid number or identifier '%s'.", value);
                }
            }
        }
    }
}
