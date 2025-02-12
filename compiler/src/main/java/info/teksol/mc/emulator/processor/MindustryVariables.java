package info.teksol.mc.emulator.processor;

import info.teksol.mc.emulator.MindustryVariable;
import info.teksol.mc.emulator.blocks.MindustryBlock;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.*;
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
    public final MindustryVariable counter = MindustryVariable.createCounter();
    public final MindustryVariable null_ = MindustryVariable.createNull();

    private final MindustryVariable unusedVariable = MindustryVariable.createVar("0");

    private final Map<String, MindustryVariable> variables = new HashMap<>();

    private static final float PI = 3.1415927f;
    private static final float E = 2.7182818f;
    private static final float RAD_DEG = 180f / PI;
    private static final float DEG_RAD = PI / 180;

    public MindustryVariables(Processor processor, InstructionProcessor instructionProcessor) {
        this.processor = processor;
        this.instructionProcessor = instructionProcessor;
        variables.put("@counter", counter);
        variables.put("@unit", null_);
        variables.put("@pi", MindustryVariable.createConst("@pi", PI));
        variables.put("@e", MindustryVariable.createConst("@e", E));
        variables.put("@degToRad", MindustryVariable.createConst("@degToRad", RAD_DEG));
        variables.put("@radToDeg", MindustryVariable.createConst("@radToDeg", DEG_RAD));
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
            MindustryContent content = MindustryContents.get(value);
            return content == null
                    ? MindustryVariable.createUnregisteredContent(value)
                    : MindustryVariable.createConstObject(content);
        } if (value.startsWith("%")) {
            return MindustryVariable.createConst(value, Color.parseColor(value));
        } else {
            try {
                // TODO This code is duplicated in NumericLiteral. Will be removed from here once typed arguments are implemented.
                return value.startsWith("0x") ? MindustryVariable.createConst(value, Long.decode(value)) :
                        value.startsWith("0b") ? MindustryVariable.createConst(value, Long.parseLong(value, 2, value.length(), 2)) :
                                MindustryVariable.createConst(value, instructionProcessor.parseNumber(value));
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
