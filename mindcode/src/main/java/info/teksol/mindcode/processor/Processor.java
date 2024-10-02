package info.teksol.mindcode.processor;

import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.processor.graphics.GraphicsBuffer;

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
    private final Variable counter = IntVariable.newIntValue(false,"@counter", 0);
    private int steps = 0;
    private int instructions = 0;
    private final BitSet coverage = new BitSet();

    private static final int TEXT_BUFFER_LIMIT = 10000;
    private static final int GRAPHICS_BUFFER_LIMIT = 256;
    private TextBuffer textBuffer;
    private GraphicsBuffer graphicsBuffer;

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

    public List<String> getPrintOutput() {
        return textBuffer.getPrintOutput();
    }

    public String getTextBuffer() {
        return textBuffer.getTextBuffer();
    }

    public int getSteps() {
        return steps;
    }

    public int getInstructions() {
        return instructions;
    }

    public BitSet getCoverage() {
        return coverage;
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

    public void run(List<LogicInstruction> program, int stepLimit) throws ExecutionException {
        if (!getFlag(STOP_PROCESSOR_OPTIONAL) && program.stream().noneMatch(StopInstruction.class::isInstance)) {
            throw new ExecutionException(STOP_PROCESSOR_OPTIONAL, "A stop instruction not present in given program.");            
        }

        if (program.isEmpty()) {
            throw new ExecutionException(ERR_INVALID_COUNTER, "No program to run.");
        }

        steps = 0;
        textBuffer = new TextBuffer(TEXT_BUFFER_LIMIT);
        graphicsBuffer = new GraphicsBuffer(GRAPHICS_BUFFER_LIMIT);

        counter.setIntValue(0);
        variables.put("@links", IntVariable.newIntValue(true, "@links", blocks.size()));
        instructions = program.size();

        int index = -1;
        LogicInstruction instruction = null;
        while (steps < stepLimit) {
            try {
                index = counter.getIntValue();
                if (index == program.size()) {
                    index = 0;
                    if (getFlag(ProcessorFlag.STOP_ON_PROGRAM_END)) {
                        break;
                    }
                }
                coverage.set(index);
                instruction = program.get(index);

                if (false) {
                    System.out.printf("Step: %d, counter: %d, instruction: %s%n", steps, index, instruction);
                    instruction.getArgs().stream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .distinct()
                            .map(a -> a.toMlog() + ": " + (variables.containsKey(a.toMlog())
                                    ? getExistingVariable(a).toString()
                                    : " [uninitialized]"))
                            .forEach(v -> System.out.printf("   variable %s%n", v));
                }

                steps++;
                counter.setIntValue(index + 1);
                if (!execute(instruction)) {
                    break;
                }

                // Report possible wrong @counter assignments at the errant instruction
                int newIndex = counter.getIntValue();
                if (newIndex < 0 || newIndex > program.size()) {
                    counter.setIntValue(0);
                    throw new ExecutionException(ERR_INVALID_COUNTER,
                            "Value of '@counter' (%d) outside valid range (0 to %d).".formatted(newIndex, program.size()));
                }
            } catch (ExecutionException ex) {
                if (getFlag(ex.getFlag())) {
                    ex.setInstructionIndex(index);
                    ex.setInstruction(instruction);
                    throw ex;
                }
            }
        }

        if (steps >= stepLimit) {
            throw new ExecutionException(ERR_EXECUTION_LIMIT_EXCEEDED, "Execution step limit of %,d exceeded.".formatted(stepLimit));
        }
    }

    private boolean execute(LogicInstruction instruction) {
        return switch(instruction.getOpcode()) {
            case DRAW       -> executeDraw((DrawInstruction) instruction);
            case DRAWFLUSH  -> executeDrawflush((DrawflushInstruction) instruction);
            case END        -> { counter.setIntValue(0); yield !getFlag(ProcessorFlag.STOP_ON_END_INSTRUCTION); }
            case FORMAT     -> executeFormat((FormatInstruction) instruction); 
            case JUMP       -> executeJump((JumpInstruction) instruction);
            case OP         -> executeOp((OpInstruction) instruction);
            case PACKCOLOR  -> executePackColor((PackColorInstruction) instruction);
            case PRINT      -> executePrint((PrintInstruction) instruction);
            case PRINTFLUSH -> { textBuffer.printflush(); yield true; }
            case READ       -> executeRead((ReadInstruction) instruction);
            case SET        -> executeSet((SetInstruction) instruction);
            case STOP       -> false;
            case WAIT       -> throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Instruction not supported by Mindcode emulator.");
            case WRITE      -> executeWrite((WriteInstruction) instruction);
            default         -> {
                if (instruction.getOutputs() == 0) yield true;
                throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Instruction not supported by Mindcode emulator.");
            }
        };
    }

    private boolean executeDraw(DrawInstruction ix) {
        graphicsBuffer.draw(ix);
        return true;
    }

    private boolean executeDrawflush(DrawflushInstruction ix) {
        Variable display = getExistingVariable(ix.getDisplay());
        display.getExistingObject().drawflush(graphicsBuffer);
        return true;
    }

    private boolean executeSet(SetInstruction ix) {
        Variable target = getOrCreateVariable(ix.getArg(0));
        Variable value = getExistingVariable(ix.getArg(1));
        target.assign(value);
        return true;
    }

    private boolean executeOp(OpInstruction ix) {
        Variable target = getOrCreateVariable(ix.getArg(1));
        Variable a = getExistingVariable(ix.getX());
        Variable b = getExistingVariable(ix.hasSecondOperand() ? ix.getY() : LogicNumber.ZERO);
        OperationEval op = ExpressionEvaluator.getOperation(ix.getOperation());
        if (op == null) {
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid op operation '" + ix.getOperation() + "'.");
        }
        op.execute(target, a, b);
        return true;
    }

    private boolean executeJump(JumpInstruction ix) {
        int address = Integer.parseInt(ix.getTarget().toMlog());
        if (ix.isUnconditional()) {
            counter.setIntValue(address);
        } else {
            Variable a = getExistingVariable(ix.getX());
            Variable b = getExistingVariable(ix.getY());
            ConditionEval conditionEval = CONDITIONS.get(ix.getCondition());
            if (conditionEval == null) {
                throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid jump condition '" + ix.getCondition() + "'.");
            }
            if (conditionEval.evaluate(a, b)) {
                counter.setIntValue(address);
            }
        }

        return true;
    }

    private boolean executePackColor(PackColorInstruction ix) {
        if (ix.getArg(0) == LogicNull.NULL) {
            return true;
        }

        Variable target = getOrCreateVariable(ix.getResult());
        Variable r = getExistingVariable(ix.getR());
        Variable g = getExistingVariable(ix.getG());
        Variable b = getExistingVariable(ix.getB());
        Variable a = getExistingVariable(ix.getA());
        ExpressionEvaluator.evaluatePackColor(target, r, g, b, a);
        return true;
    }

    private boolean executePrint(PrintInstruction ix) {
        Variable var = getExistingVariable(ix.getValue());
        textBuffer.print(var.toString());
        return true;
    }

    private boolean executeFormat(FormatInstruction ix) {
        Variable var = getExistingVariable(ix.getValue());
        if (!textBuffer.format(var.toString())) {
            throw new ExecutionException(ERR_INVALID_FORMAT, "No valid formatting placeholder found in the text buffer.");
        }
        return true;
    }
    
    private boolean executeRead(ReadInstruction ix) {
        Variable target = getOrCreateVariable(ix.getResult());
        Variable block = getExistingVariable(ix.getMemory());
        Variable index = getExistingVariable(ix.getIndex());
        target.setDoubleValue(block.getExistingObject().read(index.getIntValue()));
        return true;
    }

    private boolean executeWrite(WriteInstruction ix) {
        Variable source = getExistingVariable(ix.getArg(0));
        Variable block = getExistingVariable(ix.getMemory());
        Variable index = getExistingVariable(ix.getIndex());
        block.getExistingObject().write(index.getIntValue(), source.getDoubleValue());
        return true;
    }

    private Variable getOrCreateVariable(LogicArgument value) {
        return variables.computeIfAbsent(value.toMlog(), this::createVariable);
    }

    private Variable getExistingVariable(LogicArgument value) {
        return variables.computeIfAbsent(value.toMlog(), this::createConstant);
    }

    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("^[_a-zA-Z][-a-zA-Z_0-9]*$");

    private DoubleVariable createVariable(String value) {
        if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
            return DoubleVariable.newNullValue(false, value);
        } else {
            throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid identifier '" + value + "'.");
        }
    }

    private Variable createConstant(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            String repl = value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\\"", "'").replace("\\\\", "\\");
            return DoubleVariable.newStringValue(true, value, repl);
        }
        if (value.startsWith("@")) {
            // For now, we'll emulate these as strings
            return DoubleVariable.newStringValue(true, value, value);
        }
        try {
            // TODO This code is duplicated in NumericLiteral. Will be removed from here once typed arguments are implemented.
            return value.startsWith("0x") ? DoubleVariable.newLongValue(true, value, Long.decode(value)) :
                    value.startsWith("0b") ? DoubleVariable.newLongValue(true, value, Long.parseLong(value, 2, value.length(), 2)) :
                    DoubleVariable.newDoubleValue(true, value, Double.parseDouble(value));
        } catch (NumberFormatException ex) {
            if (VARIABLE_NAME_PATTERN.matcher(value).matches()) {
                if (getFlag(ERR_UNINITIALIZED_VAR)) {
                    throw new ExecutionException(ERR_UNINITIALIZED_VAR, "Uninitialized variable '" + value + "'.");
                } else {
                    return createVariable(value);
                }
            } else {
                throw new ExecutionException(ERR_INVALID_IDENTIFIER, "Invalid number or identifier '" + value + "'.");
            }
        }
    }

    private static final Map<Condition, ConditionEval> CONDITIONS = createConditionsMap();

    private interface ConditionEval {
        boolean evaluate(Variable a, Variable b);
    }

    private static Map<Condition, ConditionEval> createConditionsMap() {
        Map<Condition, ConditionEval> map = new HashMap<>();
        map.put(Condition.EQUAL,           ExpressionEvaluator::equals);
        map.put(Condition.NOT_EQUAL,       (a,  b) -> !ExpressionEvaluator.equals(a, b));
        map.put(Condition.LESS_THAN,       (a,  b) -> a.getDoubleValue() <  b.getDoubleValue());
        map.put(Condition.LESS_THAN_EQ,    (a,  b) -> a.getDoubleValue() <= b.getDoubleValue());
        map.put(Condition.GREATER_THAN,    (a,  b) -> a.getDoubleValue() >  b.getDoubleValue());
        map.put(Condition.GREATER_THAN_EQ, (a,  b) -> a.getDoubleValue() >= b.getDoubleValue());
        map.put(Condition.STRICT_EQUAL,    (a,  b) -> a.isObject() == b.isObject() && ExpressionEvaluator.equals(a, b));
        map.put(Condition.ALWAYS,          (a,  b) -> true);
        return map;
    }
}
