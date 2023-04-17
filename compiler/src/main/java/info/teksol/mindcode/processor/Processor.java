package info.teksol.mindcode.processor;

import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.Opcode;

import java.util.*;
import java.util.regex.Pattern;

import static info.teksol.mindcode.processor.ProcessorFlag.*;

/**
 * Mindustry Processor emulator.
 */
public class Processor {
    private final Set<ProcessorFlag> flags;
    private final Map<String, Variable> variables = new TreeMap<>();
    private final List<MindustryObject> blocks = new ArrayList<>();
    private final List<String> textBuffer = new ArrayList<>();
    private final Variable counter = IntVariable.newIntValue(false,"@counter", 0);
    private int steps = 0;

    public Processor() {
        flags = EnumSet.allOf(ProcessorFlag.class);

        variables.put("@counter", counter);
        variables.put("null", DoubleVariable.newNullValue(true, "null"));
        variables.put("true", IntVariable.newBooleanValue(true, "true", true));
        variables.put("false", IntVariable.newBooleanValue(true, "false", false));
        variables.put("0", IntVariable.newIntValue(true, "0", 0));
        variables.put("1", IntVariable.newIntValue(true, "1", 1));
    }

    public void addBlock(MindustryObject block) {
        blocks.removeIf(b -> b.getName().equals(block.getName()));
        blocks.add(block);
        variables.put(block.getName(), DoubleVariable.newObjectValue(true, block.getName(), block));
    }

    public List<String> getTextBuffer() {
        return textBuffer;
    }

    public int getSteps() {
        return steps;
    }

    public boolean getFlag(ProcessorFlag flag) {
        return flags.contains(flag);
    }

    public void setFlag(ProcessorFlag flag, boolean value) {
        if (value) {
            flags.add(flag);
        } else {
            flags.remove(flag);
        }
    }

    public void run(List<LogicInstruction> program, int stepLimit) {
        if (!getFlag(STOP_PROCESSOR_OPTIONAL) && program.stream().noneMatch(ix -> ix instanceof StopInstruction)) {
            throw new ExecutionException(STOP_PROCESSOR_OPTIONAL, "A stop instruction not present in given program.");            
        }

        steps = 0;
        textBuffer.clear();
        counter.setIntValue(0);
        variables.put("@links", IntVariable.newIntValue(true, "@links", blocks.size()));

        while (steps < stepLimit) {
            try {
                int index = counter.getIntValue();
                if (index == program.size()) {
                    index = 0;
                    if (getFlag(ProcessorFlag.STOP_ON_PROGRAM_END)) {
                        break;
                    }
                }
                if (index < 0 || index > program.size()) {
                    counter.setIntValue(0);
                    throw new ExecutionException(ERR_INVALID_COUNTER, "Value of @counter (" + index + ") outside valid range (0 to " + program.size() + ")");
                }

                LogicInstruction instruction = program.get(index);

                if (false) {
                    if (instruction instanceof PrintInstruction ix) {
                        Variable var = getExistingVariable(ix.getValue());
                        System.out.printf("Step: %d, counter: %d, instruction: %s *** Print %s%n", steps, index, instruction, var.toString());

                    } else {
                        System.out.printf("Step: %d, counter: %d, instruction: %s%n", steps, index, instruction);
                    }
                }

                steps++;
                counter.setIntValue(index + 1);
                if (!execute(instruction)) {
                    break;
                }
            } catch (ExecutionException ex) {
                if (getFlag(ex.getFlag())) {
                    throw ex;
                }
            }
        }

        if (steps >= stepLimit) {
            throw new ExecutionException(ERR_EXECUTION_LIMIT_EXCEEDED, "Execution step limit of " + stepLimit + " exceeded");
        }
    }

    private boolean execute(LogicInstruction instruction) {
        return switch(instruction) {
            case EndInstruction ix      -> { counter.setIntValue(0); yield !getFlag(ProcessorFlag.STOP_ON_END_INSTRUCTION); }
            case JumpInstruction ix     -> executeJump(ix);
            case OpInstruction ix       -> executeOp(ix);
            case PackColorInstruction ix-> executePackColor(ix);
            case PrintInstruction ix    -> executePrint(ix);
            case ReadInstruction ix     -> executeRead(ix);
            case SetInstruction ix      -> executeSet(ix);
            case StopInstruction ix     -> false;
            case WriteInstruction ix    -> executeWrite(ix);
            default -> throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Unsupported opcode " + instruction.getOpcode());
        };
    }

    private boolean executeSet(SetInstruction ix) {
        Variable target = getOrCreateVariable(ix.getResult());
        Variable value = getExistingVariable(ix.getValue());
        target.assign(value);
        return true;
    }

    private boolean executeOp(OpInstruction ix) {
        Variable target = getOrCreateVariable(ix.getResult());
        Variable a = getExistingVariable(ix.getFirstOperand());
        Variable b = getExistingVariable(ix.hasSecondOperand() ? ix.getSecondOperand() : "0");
        Operation op = ExpressionEvaluator.getOperation(ix.getOperation());
        if (op == null) {
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid op operation " + ix.getOperation());
        }
        op.execute(target, a, b);
        return true;
    }

    private boolean executeJump(JumpInstruction ix) {
        int address = Integer.parseInt(ix.getTarget());
        if (ix.isUnconditional()) {
            counter.setIntValue(address);
        } else {
            Variable a = getExistingVariable(ix.getFirstOperand());
            Variable b = getExistingVariable(ix.getSecondOperand());
            Condition condition = CONDITIONS.get(ix.getCondition());
            if (condition == null) {
                throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid jump condition " + ix.getCondition());
            }
            if (condition.evaluate(a, b)) {
                counter.setIntValue(address);
            }
        }

        return true;
    }

    private boolean executePackColor(PackColorInstruction ix) {
        Variable target = getOrCreateVariable(ix.getResult());
        Variable r = getExistingVariable(ix.getR());
        Variable g = getExistingVariable(ix.getG());
        Variable b = getExistingVariable(ix.getB());
        Variable a = getExistingVariable(ix.getA());
        double result = toDoubleBits(clamp(r.getDoubleValue()), clamp(g.getDoubleValue()), clamp(b.getDoubleValue()),
                clamp(a.getDoubleValue()));

        target.setDoubleValue(result);
        return true;
    }

    private static float clamp(double value){
        return Math.max(Math.min((float)value, 1f), 0f);
    }

    public static double toDoubleBits(float r, float g, float b, float a){
        return Double.longBitsToDouble(rgba8888(r, g, b, a) & 0x00000000_ffffffffL);
    }

    private static int rgba8888(float r, float g, float b, float a){
        return ((int)(r * 255) << 24) | ((int)(g * 255) << 16) | ((int)(b * 255) << 8) | (int)(a * 255);
    }

    private boolean executePrint(PrintInstruction ix) {
        Variable var = getExistingVariable(ix.getValue());
        textBuffer.add(var.toString());
        return true;
    }

    private boolean executeRead(ReadInstruction ix) {
        Variable target = getOrCreateVariable(ix.getValue());
        Variable block = getExistingVariable(ix.getMemory());
        Variable index = getExistingVariable(ix.getIndex());
        target.setDoubleValue(block.getExistingObject().read(index.getIntValue()));
        return true;
    }

    private boolean executeWrite(WriteInstruction ix) {
        Variable source = getExistingVariable(ix.getValue());
        Variable block = getExistingVariable(ix.getMemory());
        Variable index = getExistingVariable(ix.getIndex());
        block.getExistingObject().write(index.getIntValue(), source.getDoubleValue());
        return true;
    }

    private Variable getOrCreateVariable(String value) {
        return variables.computeIfAbsent(value, this::createVariable);
    }

    private Variable getExistingVariable(String value) {
        return variables.computeIfAbsent(value, this::createConstant);
    }

    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("^[_a-zA-Z][-a-zA-Z_0-9]*$");

    private DoubleVariable createVariable(String value) {
        if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
            return DoubleVariable.newNullValue(false, value);
        } else {
            throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid identifier " + value);
        }
    }

    private Variable createConstant(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            String repl = value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\\"", "'").replace("\\\\", "\\");
            return DoubleVariable.newStringValue(true, value, repl);
        }
        try {
            // This code is duplicated in NumericLiteral. Will be removed from here once typed arguments are implemented.
            return value.startsWith("0x") ? DoubleVariable.newLongValue(true, value, Long.decode(value)) :
                    value.startsWith("0b") ? DoubleVariable.newLongValue(true, value, Long.parseLong(value, 2, value.length(), 2)) :
                    DoubleVariable.newDoubleValue(true, value, Double.parseDouble(value));
        } catch (NumberFormatException ex) {
            if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
                throw new ExecutionException(ERR_UNINITIALIZED_VAR, "Uninitialized variable " + value);
            } else {
                throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid number or identifier " + value);
            }
        }
    }

    private static final Map<String, Condition> CONDITIONS = createConditionsMap();

    private interface Condition {
        boolean evaluate(Variable a, Variable b);
    }

    private static Map<String, Condition> createConditionsMap() {
        Map<String, Condition> map = new HashMap<>();
        map.put("always",       (a, b) -> true);
        map.put("equal",        (a, b) -> ExpressionEvaluator.equals(a, b));
        map.put("notEqual",     (a, b) -> !ExpressionEvaluator.equals(a, b));
        map.put("land",         (a, b) -> a.getDoubleValue() != 0 && b.getDoubleValue() != 0);
        map.put("lessThan",     (a, b) -> a.getDoubleValue() < b.getDoubleValue());
        map.put("lessThanEq",   (a, b) -> a.getDoubleValue() <= b.getDoubleValue());
        map.put("greaterThan",  (a, b) -> a.getDoubleValue() > b.getDoubleValue());
        map.put("greaterThanEq",(a, b) -> a.getDoubleValue() >= b.getDoubleValue());
        map.put("strictEqual",  (a, b) -> a.isObject() == b.isObject() && ExpressionEvaluator.equals(a, b));
        return map;
    }
}
