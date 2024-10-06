package info.teksol.emulator.processor;

import info.teksol.emulator.MindustryObject;
import info.teksol.emulator.MindustryVariable;
import info.teksol.emulator.blocks.Memory;
import info.teksol.emulator.blocks.MindustryBlock;
import info.teksol.emulator.blocks.graphics.GraphicsBuffer;
import info.teksol.emulator.blocks.graphics.LogicDisplay;
import info.teksol.evaluator.ConditionEvaluator;
import info.teksol.evaluator.ExpressionEvaluator;
import info.teksol.evaluator.LogicCondition;
import info.teksol.evaluator.LogicOperation;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.mimex.*;

import java.util.*;
import java.util.function.Consumer;

import static info.teksol.emulator.processor.ProcessorFlag.*;

/**
 * Mindustry Processor emulator.
 */
public class Processor {
    private final Set<ProcessorFlag> flags;
    private final MindustryVariables variables;
    private final Map<String, MindustryBlock> blockMap = new LinkedHashMap<>();
    private List<MindustryBlock> blocks = List.of();
    private final MindustryVariable counter;
    private int steps = 0;
    private int instructions = 0;
    private final BitSet coverage = new BitSet();

    private static final int TEXT_BUFFER_LIMIT = 10000;
    private static final int GRAPHICS_BUFFER_LIMIT = 256;
    private TextBuffer textBuffer;
    private GraphicsBuffer graphicsBuffer;

    public Processor() {
        flags = EnumSet.allOf(ProcessorFlag.class);
        variables = new MindustryVariables(this);
        counter = variables.counter;
    }

    public void addBlock(String name, MindustryBlock block) {
        blockMap.put(name, block);
        variables.addLinkedBlock(name, block);
        blocks= List.copyOf(blockMap.values());
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
        variables.addConstVariable("@links", blocks.size());
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
                            .map(a -> a.toMlog() + ": " + (variables.containsVariable(a.toMlog())
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
            case GETLINK    -> executeGetlink((GetlinkInstruction) instruction);
            case JUMP       -> executeJump((JumpInstruction) instruction);
            case LOOKUP     -> executeLookup((LookupInstruction) instruction);
            case OP         -> executeOp((OpInstruction) instruction);
            case PACKCOLOR  -> executePackColor((PackColorInstruction) instruction);
            case PRINT      -> executePrint((PrintInstruction) instruction);
            case PRINTFLUSH -> { textBuffer.printflush(); yield true; }
            case READ       -> executeRead((ReadInstruction) instruction);
            case SENSOR     -> executeSensor((SensorInstruction) instruction);
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

    @SuppressWarnings("unchecked")
    private <T extends MindustryObject> void blockOperation(LogicVariable value, Class<T> type, Consumer<T> operation) {
        MindustryVariable variable = getExistingVariable(value);
        if (variable.getObject() == null) {
            throw new ExecutionException(ERR_NOT_AN_OBJECT, "Variable '" + value.toMlog() + "' is not an object.");
        } else if (!type.isInstance(variable.getObject())) {
            throw new ExecutionException(ERR_UNSUPPORTED_BLOCK_OPERATION,
                    "Unsupported operation 'drawflush' on '" + value.toMlog() + "' (class " + variable.getObject().getClass().getSimpleName() + ").");
        } else {
            operation.accept((T) variable.getObject());
        }
    }

    private boolean executeDraw(DrawInstruction ix) {
        graphicsBuffer.draw(ix);
        return true;
    }

    private boolean executeDrawflush(DrawflushInstruction ix) {
        blockOperation(ix.getDisplay(), LogicDisplay.class, display -> display.drawflush(graphicsBuffer));
        return true;
    }

    private boolean executeGetlink(GetlinkInstruction ix) {
        MindustryVariable index = getExistingVariable(ix.getIndex());
        MindustryVariable result = getOrCreateVariable(ix.getResult());
        if (!index.isValidNumber()) {
            result.setNull();
            throw new ExecutionException(ERR_NOT_A_NUMBER, "Invalid numeric value in getlink index '%s'.", ix.getIndex().toMlog());
        }
        if (!index.isValidNumber() || index.getIntValue() < 0 || index.getIntValue() >= blockMap.size()) {
            result.setNull();
            throw new ExecutionException(ERR_INVALID_LINK, "Invalid link index %d.", index.getIntValue());
        }
        result.setObject(blocks.get(index.getIntValue()));
        return true;
    }

    private boolean executeFormat(FormatInstruction ix) {
        MindustryVariable var = getExistingVariable(ix.getValue());
        if (!textBuffer.format(var.print())) {
            throw new ExecutionException(ERR_INVALID_FORMAT, "No valid formatting placeholder found in the text buffer.");
        }
        return true;
    }

    private boolean executeJump(JumpInstruction ix) {
        int address = Integer.parseInt(ix.getTarget().toMlog());
        if (ix.isUnconditional()) {
            counter.setIntValue(address);
        } else {
            MindustryVariable a = getExistingVariable(ix.getX());
            MindustryVariable b = getExistingVariable(ix.getY());
            LogicCondition logicCondition = ConditionEvaluator.getCondition(ix.getCondition());
            if (logicCondition == null) {
                throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid jump condition '" + ix.getCondition() + "'.");
            }
            if (logicCondition.evaluate(a, b)) {
                counter.setIntValue(address);
            }
        }

        return true;
    }

    private boolean executeLookup(LookupInstruction ix) {
        LogicKeyword type = ix.getType();
        MindustryVariable index = getExistingVariable(ix.getIndex());
        MindustryContent object = switch (type.getKeyword()) {
            case "block"    -> BlockType.forId(index.getIntValue());
            case "unit"     -> Unit.forId(index.getIntValue());
            case "item"     -> Item.forId(index.getIntValue());
            case "liquid"   -> Liquid.forId(index.getIntValue());
            default         -> throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid lookup type '" + type.getKeyword() + "'.");
        };
        MindustryVariable result = getOrCreateVariable(ix.getResult());
        result.setObject(object);
        if (!index.isValidNumber()) {
            throw new ExecutionException(ERR_NOT_A_NUMBER, "Invalid numeric value in lookup index '%s'.", ix.getIndex().toMlog());
        }
        if (object == null || !index.isValidNumber()) {
            throw new ExecutionException(ERR_INVALID_CONTENT, "Invalid lookup index %d for type '%s'.", index.getIntValue(), type.getKeyword());
        }
        return true;
    }

    private boolean executeOp(OpInstruction ix) {
        MindustryVariable target = getOrCreateVariable(ix.getArg(1));
        MindustryVariable a = getExistingVariable(ix.getX());
        MindustryVariable b = getExistingVariable(ix.hasSecondOperand() ? ix.getY() : LogicNumber.ZERO);
        LogicOperation op = ExpressionEvaluator.getOperation(ix.getOperation());
        if (op == null) {
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid op operation '" + ix.getOperation() + "'.");
        }
        op.execute(target, a, b);
        return true;
    }

    private boolean executePackColor(PackColorInstruction ix) {
        if (ix.getArg(0) == LogicNull.NULL) {
            return true;
        }

        MindustryVariable target = getOrCreateVariable(ix.getResult());
        MindustryVariable r = getExistingVariable(ix.getR());
        MindustryVariable g = getExistingVariable(ix.getG());
        MindustryVariable b = getExistingVariable(ix.getB());
        MindustryVariable a = getExistingVariable(ix.getA());
        ExpressionEvaluator.evaluatePackColor(target, r, g, b, a);
        return true;
    }

    private boolean executePrint(PrintInstruction ix) {
        MindustryVariable var = getExistingVariable(ix.getValue());
        textBuffer.print(var.print());
        return true;
    }

    private boolean executeRead(ReadInstruction ix) {
        MindustryVariable target = getOrCreateVariable(ix.getResult());
        MindustryVariable index = getExistingVariable(ix.getIndex());
        blockOperation(ix.getMemory(), Memory.class,
                memory -> target.setDoubleValue(memory.read(index.getIntValue())));
        return true;
    }

    private boolean executeSensor(SensorInstruction ix) {
        MindustryVariable property = getExistingVariable(ix.getProperty());
        MindustryVariable object = getExistingVariable(ix.getObject());
        MindustryObject inner = object.getObject();
        MindustryVariable target = getOrCreateVariable(ix.getResult());
        switch (property.getExistingObject().name()) {
            case "type" -> {
                target.setObject(inner != null ? inner.type() : null);
                return true;
            }
            case "id" -> {
                if (inner != null && inner.id() >= 0) {
                    target.setLongValue(inner.id());
                } else {
                    target.setNull();
                }
                return true;
            }
            default -> {
                throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Instruction not supported by Mindcode emulator.");
            }
        }
    }

    private boolean executeSet(SetInstruction ix) {
        MindustryVariable target = getOrCreateVariable(ix.getArg(0));
        MindustryVariable value = getExistingVariable(ix.getArg(1));
        target.assign(value);
        return true;
    }

    private boolean executeWrite(WriteInstruction ix) {
        MindustryVariable source = getExistingVariable(ix.getArg(0));
        MindustryVariable index = getExistingVariable(ix.getIndex());
        blockOperation(ix.getMemory(), Memory.class,
                memory -> memory.write(index.getIntValue(), source.getDoubleValue()));
        return true;
    }

    private MindustryVariable getOrCreateVariable(LogicArgument value) {
        return variables.getOrCreateVariable(value);
    }

    private MindustryVariable getExistingVariable(LogicArgument value) {
        return variables.getExistingVariable(value);
    }
}
