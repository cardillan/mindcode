package info.teksol.mc.emulator.processor;

import info.teksol.mc.emulator.MindustryObject;
import info.teksol.mc.emulator.MindustryString;
import info.teksol.mc.emulator.MindustryVariable;
import info.teksol.mc.emulator.blocks.Memory;
import info.teksol.mc.emulator.blocks.MessageBlock;
import info.teksol.mc.emulator.blocks.MindustryBlock;
import info.teksol.mc.emulator.blocks.graphics.GraphicsBuffer;
import info.teksol.mc.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mc.evaluator.ConditionEvaluator;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.evaluator.LogicCondition;
import info.teksol.mc.evaluator.LogicOperation;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Consumer;

import static info.teksol.mc.emulator.processor.ExecutionFlag.*;

/// Mindustry Processor emulator.
@NullMarked
public class Processor extends AbstractMessageEmitter {
    private final InstructionProcessor instructionProcessor;
    private final MindustryMetadata metadata;
    private final Set<ExecutionFlag> flags;
    private final MindustryVariables variables;
    private final Map<String, MindustryBlock> blockMap = new LinkedHashMap<>();
    private List<MindustryBlock> blocks = List.of();
    private final MindustryVariable counter;
    private int traceCount = 0;
    private int traceLimit = 0;
    private int steps = 0;
    private int instructions = 0;
    private final BitSet coverage = new BitSet();

    private static final int TEXT_OUTPUT_LIMIT = 10000;
    private static final int TEXT_BUFFER_LIMIT = 400;
    private static final int GRAPHICS_BUFFER_LIMIT = 256;
    private TextBuffer textBuffer = TextBuffer.EMPTY;
    private GraphicsBuffer graphicsBuffer = GraphicsBuffer.EMPTY;
    private List<Assertion> assertions = new ArrayList<>();

    public Processor(InstructionProcessor instructionProcessor, MessageConsumer messageConsumer,
            Set<ExecutionFlag> flags, int traceLimit) {
        super(messageConsumer);
        this.instructionProcessor = instructionProcessor;
        this.metadata = instructionProcessor.getMetadata();
        this.flags = EnumSet.copyOf(flags);
        variables = new MindustryVariables(this, instructionProcessor);
        counter = variables.counter;
        this.traceLimit = traceLimit;
    }

    public Processor(InstructionProcessor instructionProcessor, MessageConsumer messageConsumer, int traceLimit) {
        this(instructionProcessor, messageConsumer, ExecutionFlag.getDefaultFlags(), traceLimit);
    }

    public void addBlock(String name, MindustryBlock block) {
        blockMap.put(name, block);
        variables.addLinkedBlock(name, block);
        blocks= List.copyOf(blockMap.values());
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public List<String> getPrintOutput() {
        return textBuffer.getPrintOutput();
    }

    public TextBuffer getTextBuffer() {
        return Objects.requireNonNull(textBuffer);
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

    public boolean getFlag(ExecutionFlag flag) {
        return flags.contains(flag) && (flag != TRACE_EXECUTION || traceCount < traceLimit);
    }

    public void setFlag(ExecutionFlag flag, boolean value) {
        if (value) {
            flags.add(flag);
        } else {
            flags.remove(flag);
        }
    }

    @Override
    public void info(@PrintFormat String format, Object... args) {
        traceCount++;
        super.info(format, args);
    }

    public void run(List<LogicInstruction> program, int stepLimit) throws ExecutionException {
        steps = 0;
        textBuffer = new TextBuffer(TEXT_OUTPUT_LIMIT, TEXT_BUFFER_LIMIT,
                getFlag(ERR_TEXT_BUFFER_OVERFLOW));
        graphicsBuffer = new GraphicsBuffer(GRAPHICS_BUFFER_LIMIT);
        assertions = new ArrayList<>();

        counter.setIntValue(0);
        variables.addConstVariable("@links", blocks.size());
        instructions = program.size();

        if (instructions == 0) {
            textBuffer.append("No program to run.");
            return;
        }

        if (getFlag(TRACE_EXECUTION)) {
            info("%nProgram execution trace:");
        }

        int index = -1;
        LogicInstruction instruction = null;
        while (steps < stepLimit) {
            try {
                index = counter.getIntValue();
                if (index == program.size()) {
                    index = 0;
                    if (getFlag(ExecutionFlag.STOP_ON_PROGRAM_END)) {
                        break;
                    }
                }
                coverage.set(index);
                instruction = program.get(index);

                if (getFlag(TRACE_EXECUTION)) {
                    info("Step %d, instruction #%d: %s", steps + 1, index, LogicInstructionPrinter.toStringSimple(instruction));
                    instruction.inputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .distinct()
                            .map(a -> a.toMlog() + ": " + (variables.containsVariable(a.toMlog())
                                    ? getExistingVariable(a).printExact()
                                    : " [uninitialized]"))
                            .forEach(v -> info("   in  %s", v));
                }

                steps++;
                counter.setIntValue(index + 1);
                if (!execute(instruction)) {
                    break;
                }

                if (getFlag(TRACE_EXECUTION)) {
                    instruction.outputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .distinct()
                            .map(a -> a.toMlog() + ": " + (variables.containsVariable(a.toMlog())
                                    ? getExistingVariable(a).printExact()
                                    : " [uninitialized]"))
                            .forEach(v -> info("   out %s", v));
                }

                // Report possible wrong jump and @counter assignments at the errant instruction
                if (!counter.isInteger()) {
                    throw new ExecutionException(ERR_INVALID_COUNTER, "Non-integer value of of '@counter' (%s) .", counter.printExact());
                }

                int newIndex = counter.getIntValue();
                if (newIndex < 0 || newIndex > program.size()) {
                    counter.setIntValue(0);
                    throw new ExecutionException(ERR_INVALID_COUNTER,
                            "Value of '@counter' (%d) outside valid range (0 to %d).", newIndex, program.size());
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
            throw new ExecutionException(ERR_EXECUTION_LIMIT_EXCEEDED, "Execution step limit of %,d exceeded.", stepLimit);
        }
    }

    private boolean execute(LogicInstruction instruction) {
        return switch(instruction.getOpcode()) {
            case ASSERT_EQUALS  -> executeAssertEquals(instruction);
            case ASSERT_BOUNDS  -> executeAssertBounds(instruction);
            case ASSERT_FLUSH   -> executeAssertFlush(instruction);
            case ASSERT_PRINTS  -> executeAssertPrints(instruction);
            case DRAW           -> executeDraw((DrawInstruction) instruction);
            case DRAWFLUSH      -> executeDrawflush((DrawflushInstruction) instruction);
            case END            -> { counter.setIntValue(0); yield !getFlag(ExecutionFlag.STOP_ON_END_INSTRUCTION); }
            case FORMAT         -> executeFormat((FormatInstruction) instruction);
            case GETLINK        -> executeGetlink((GetlinkInstruction) instruction);
            case JUMP           -> executeJump((JumpInstruction) instruction);
            case LOOKUP         -> executeLookup((LookupInstruction) instruction);
            case OP             -> executeOp((OpInstruction) instruction);
            case PACKCOLOR      -> executePackColor((PackColorInstruction) instruction);
            case PRINT          -> executePrint((PrintInstruction) instruction);
            case PRINTCHAR      -> executePrintChar((PrintCharInstruction) instruction);
            case PRINTFLUSH     -> executePrintflush((PrintflushInstruction) instruction);
            case READ           -> executeRead((ReadInstruction) instruction);
            case SENSOR         -> executeSensor((SensorInstruction) instruction);
            case SET            -> executeSet((SetInstruction) instruction);
            case STOP           -> executeStop((StopInstruction) instruction);
            case UBIND, WAIT    -> throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Instruction not supported by Mindcode emulator.");
            case WRITE          -> executeWrite((WriteInstruction) instruction);
            default             -> {
                if (instruction.getOutputs() == 0) yield true;
                throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Instruction not supported by Mindcode emulator.");
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <T extends MindustryObject> void blockOperation(String description, LogicValue value, Class<T> type,
            Consumer<T> operation, Runnable cleanup) throws ExecutionException {
        MindustryVariable variable = getExistingVariable(value);
        if (variable.getObject() == null) {
            cleanup.run();
            throw new ExecutionException(ERR_NOT_AN_OBJECT, "Variable '%s' is not an object.", value.toMlog());
        } else if (!type.isInstance(variable.getObject())) {
            cleanup.run();
            throw new ExecutionException(ERR_UNSUPPORTED_BLOCK_OPERATION, "Unsupported operation '%s' on '%s' (class %s).",
                    description, value.toMlog(), variable.getObject().getClass().getSimpleName());
        } else {
            operation.accept((T) variable.getObject());
        }
    }

    private <T extends MindustryObject> void blockOperation(String description, LogicValue value, Class<T> type,
            Consumer<T> operation) throws ExecutionException {
        blockOperation(description, value, type, operation, () -> {});
    }


    private boolean executeAssertEquals(LogicInstruction ix) {
        String expected = getExistingVariable(ix.getArg(0)).printExact();
        String actual = getExistingVariable(ix.getArg(1)).printExact();
        String title = getExistingVariable(ix.getArg(2)).printExact();
        assertions.add(new Assertion(expected, actual, title));
        return true;
    }

    private AssertionType getAssertionType(String value) {
        AssertionType type = AssertionType.byName(value);
        if (type == null) {
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Unsupported assertion type '%s'.", value);
        }
        return type;
    }

    private AssertOp getAssertOp(String value) {
        AssertOp op = AssertOp.byName(value);
        if (op == null) {
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Unsupported assert operation '%s'.", value);
        }
        return op;
    }

    private boolean executeAssertBounds(LogicInstruction ix) {
        AssertionType type = getAssertionType(ix.getArg(0).toMlog());
        MindustryVariable multiple = getExistingVariable(ix.getArg(1));
        MindustryVariable min = getExistingVariable(ix.getArg(2));
        AssertOp opMin = getAssertOp(ix.getArg(3).toMlog());
        MindustryVariable value = getExistingVariable(ix.getArg(4));
        AssertOp opMax = getAssertOp(ix.getArg(5).toMlog());
        MindustryVariable max = getExistingVariable(ix.getArg(6));

        if ((value.isObject() ? type.objFunction.get(value.getObject()) : type.function.get(value.getDoubleValue()))
                && (type != AssertionType.multiple || (value.getDoubleValue() % multiple.getDoubleValue() == 0))
                && (opMin.function.get(min.getDoubleValue(), value.getDoubleValue()))
                && (opMax.function.get(value.getDoubleValue(), max.getDoubleValue()))) {
            // We're okay
        } else {
            if (getFlag(ERR_RUNTIME_CHECK_FAILED)) {
                throw new ExecutionException(ERR_RUNTIME_CHECK_FAILED, "Failed runtime check: '%s'.",
                        getExistingVariable(ix.getArg(7)).printExact());
            } else {
                warn("Failed runtime check: '%s'.", getExistingVariable(ix.getArg(7)).printExact());
            }
        }

        return true;
    }

    private boolean executeAssertFlush(LogicInstruction ix) {
        textBuffer.prepareAssert();
        return true;
    }

    private boolean executeAssertPrints(LogicInstruction ix) {
        String expected = getExistingVariable(ix.getArg(0)).printExact();
        String actual = textBuffer.getAssertedOutput();
        String title = getExistingVariable(ix.getArg(1)).printExact();
        assertions.add(new Assertion(expected, actual, title));
        return true;
    }

    private boolean executeDraw(DrawInstruction ix) {
        graphicsBuffer.draw(ix);
        return true;
    }

    private boolean executeDrawflush(DrawflushInstruction ix) {
        if (ix.getDisplay() == LogicNull.NULL) {
            graphicsBuffer.clear();
        } else {
            blockOperation("drawflush", ix.getDisplay(), LogicDisplay.class,
                    display -> display.drawflush(graphicsBuffer));
        }
        return true;
    }

    private boolean executeGetlink(GetlinkInstruction ix) {
        MindustryVariable index = getExistingVariable(ix.getIndex());
        MindustryVariable result = getOrCreateVariable(ix.getResult());
        if (index.invalidNumber()) {
            result.setNull();
            throw new ExecutionException(ERR_NOT_A_NUMBER, "Invalid numeric value in getlink index '%s'.", ix.getIndex().toMlog());
        }
        if (index.invalidNumber() || index.getIntValue() < 0 || index.getIntValue() >= blockMap.size()) {
            result.setNull();
            throw new ExecutionException(ERR_INVALID_LINK, "Invalid link index %d.", index.getIntValue());
        }
        result.setObject(blocks.get(index.getIntValue()));
        return true;
    }

    private boolean executeFormat(FormatInstruction ix) {
        MindustryVariable var = getExistingVariable(ix.getValue());
        if (!textBuffer.format(var.print(instructionProcessor))) {
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
                throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid jump condition '%s'.", ix.getCondition());
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
        Map<Integer, ? extends MindustryContent> lookupMap = metadata.getLookupMap(type.getKeyword());
        if (lookupMap == null) {
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid lookup type '%s'.", type.getKeyword());
        }
        MindustryContent object = lookupMap.get(index.getIntValue());
        MindustryVariable result = getOrCreateVariable(ix.getResult());
        result.setObject(object);
        if (index.invalidNumber()) {
            throw new ExecutionException(ERR_NOT_A_NUMBER, "Invalid numeric value in lookup index '%s'.", ix.getIndex().toMlog());
        }
        if (object == null || index.invalidNumber()) {
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
            throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Invalid op operation '%s'.", ix.getOperation());
        }
        op.execute(target, a, b);
        return true;
    }

    private boolean executePackColor(PackColorInstruction ix) {
        // Special handling for instructions generated by #sort-variables
        if (ix.getArg(0) == LogicNull.NULL || ix.getArg(0) == LogicNumber.ZERO) {
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
        textBuffer.print(var.print(instructionProcessor));
        return true;
    }

    private boolean executePrintChar(PrintCharInstruction ix) {
        MindustryVariable var = getExistingVariable(ix.getValue());
        if (var.isObject()) {
            if (var.getObject() == null) return true;
            textBuffer.print(Objects.requireNonNullElse(var.getObject().iconString(metadata), ""));
        } else {
            textBuffer.print(String.valueOf((char)Math.floor(var.getDoubleValue())));
        }
        return true;
    }

    private boolean executePrintflush(PrintflushInstruction ix) {
        if (ix.getBlock() == LogicNull.NULL) {
            textBuffer.printflush(null);
        } else {
            blockOperation("drawflush", ix.getBlock(), MessageBlock.class,
                    message -> message.printflush(textBuffer), () -> textBuffer.printflush(null));
        }
        return true;
    }

    private boolean executeRead(ReadInstruction ix) {
        MindustryVariable target = getOrCreateVariable(ix.getResult());
        MindustryVariable index = getExistingVariable(ix.getIndex());
        MindustryVariable object = getExistingVariable(ix.getMemory());
        if (object.getObject() instanceof MindustryString str) {
            target.setDoubleValue(getChar(str, index.getIntValue()));
        } else {
            blockOperation("read", ix.getMemory(), Memory.class,
                    memory -> target.setDoubleValue(memory.read(index.getIntValue())));
        }
        return true;
    }

    private double getChar(MindustryString str, int index) {
        return index >= 0 && index < str.value().length() ? str.value().charAt(index) : Double.NaN;
    }

    private boolean executeSensor(SensorInstruction ix) {
        MindustryVariable property = getExistingVariable(ix.getProperty());
        MindustryVariable object = getExistingVariable(ix.getObject());
        MindustryObject inner = object.getObject();
        MindustryVariable target = getOrCreateVariable(ix.getResult());
        switch (property.getExistingObject().name()) {
            case "@size" -> {
                if (inner instanceof MindustryString str) {
                    target.setIntValue(str.value().length());
                    return true;
                }
            }
            case "@type" -> {
                target.setObject(inner != null ? inner.type() : null);
                return true;
            }
            case "@id" -> {
                if (inner != null && inner.id() >= 0) {
                    target.setLongValue(inner.id());
                } else {
                    target.setNull();
                }
                return true;
            }
        }

        throw new ExecutionException(ERR_UNSUPPORTED_OPCODE, "Instruction not supported by Mindcode emulator.");
    }

    private boolean executeSet(SetInstruction ix) {
        MindustryVariable target = getOrCreateVariable(ix.getArg(0));
        MindustryVariable value = getExistingVariable(ix.getArg(1));
        target.assign(value);
        return true;
    }

    private boolean executeStop(StopInstruction ix) {
        if (getFlag(DUMP_VARIABLES_ON_STOP) && traceCount < traceLimit) {
            info("\nstop instruction encountered, dumping variable values:");
            variables.getAllVariables().stream()
                    .map(v -> v.getName() + ": " + v.printExact())
                    .forEach(this::info);
        }
        return !getFlag(STOP_ON_STOP_INSTRUCTION);
    }

    private boolean executeWrite(WriteInstruction ix) {
        MindustryVariable source = getExistingVariable(ix.getArg(0));
        MindustryVariable index = getExistingVariable(ix.getIndex());
        blockOperation("write", ix.getMemory(), Memory.class,
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
