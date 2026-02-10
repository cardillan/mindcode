package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.*;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.emulator.blocks.graphics.GraphicsBuffer;
import info.teksol.mc.emulator.mimex.target60.LExecutor60;
import info.teksol.mc.mindcode.logic.arguments.AssertOp;
import info.teksol.mc.mindcode.logic.arguments.AssertionType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static info.teksol.mc.emulator.ExecutionFlag.*;

@NullMarked
public abstract class LExecutorBase implements LExecutor {
    private static final int maxInstructionScale = 5;
    private static final int maxGraphicsBuffer = 256;
    private static final int maxDisplayBuffer = 1024;

    protected final MindustryMetadata metadata;
    protected final LAssembler assembler;
    protected final BasicEmulator emulator;
    protected final LogicBlock logicBlock;
    protected final EmulatorMessageHandler messageHandler;

    protected final List<LInstruction> instructions = new ArrayList<>();
    protected final Map<String, LVar> vars = new LinkedHashMap<>();
    protected final List<MindustryBuilding> linkedBlocks = new ArrayList<>();

    protected final List<Assertion> assertions = new ArrayList<>();
    protected final TextBuffer textBuffer;
    protected final GraphicsBuffer graphicsBuffer;

    protected final LVar counter;
    protected final LVar unit;
    protected final LVar thisv;
    protected final LVar thisx;
    protected final LVar thisy;
    protected final LVar links;
    protected final LVar ipt;

    // Performance statistics
    protected int steps;
    protected int noopSteps;
    protected int[] profile = new int[0];
    protected double waitTime;
    protected double accumulatorLoss;

    // Run parameters

    // Behavioral flags
    protected boolean accumulatorHalving = true;
    protected boolean yieldPreservesAccumulator = false;

    /// Instruction accumulator
    protected double accumulator = 0;

    /// When set to true, this executor is active. The yielding instructions (`stop` and `wait`) make an executor
    /// inactive.
    protected boolean active = true;

    /// When set to true, this executor has finished running the code. The execution will actually stop when all
    /// executors within the emulator (i.e., all processors) have finished.
    protected boolean finished = false;

    /// Yield the execution for the rest of the tick
    protected boolean yield = false;

    /// Current tick's delta (in ticks)
    protected double delta;

    /// Index of the instruction being executed. All errors are reported against this instruction.
    protected int index;

    public LExecutorBase(MindustryMetadata metadata, LAssembler assembler, BasicEmulator emulator, LogicBlock logicBlock) {
        this.metadata = metadata;
        this.assembler = assembler;
        this.emulator = emulator;
        this.logicBlock = logicBlock;
        this.messageHandler = emulator.getMessageHandler();

        int maxTextBuffer = metadata.getProcessorVersion() == ProcessorVersion.V6 ? 256 : 400;
        textBuffer = new TextBuffer(messageHandler, 10000, maxTextBuffer);
        graphicsBuffer = new GraphicsBuffer(maxGraphicsBuffer);

        // Create default variables
        counter = assembler.var("@counter");
        unit = assembler.var("@unit");
        thisv = assembler.var("@this");
        thisx = assembler.var("@thisx");
        thisy = assembler.var("@thisy");
        links = assembler.var("@links");
        ipt = assembler.var("@ipt");
        ipt.numval = logicBlock.getIpt();

        // Set up 'this' processor
        logicBlock.setExecutor(this);
        thisv.objval = logicBlock;
        thisx.numval = logicBlock.x();
        thisy.numval = logicBlock.y();

        builders.put("noop", NoopI::new);

        builders.put("assertbounds", AssertBoundsI::new);
        builders.put("assertequals", AssertEqualsI::new);
        builders.put("assertflush", AssertFlushI::new);
        builders.put("assertprints", AssertPrintsI::new);
        builders.put("error", ErrorI::new);
    }

    //<editor-fold desc="Initialization">
    @Override
    public void load(LParser parser) {
        parser.parse().stream().map(this::build).forEach(instructions::add);
        profile = new int[instructions.size()];
        vars.putAll(assembler.getVars());
        logicBlock.getLinks().forEach(this::addBlock);
        instructions.addAll(assembler.getInstructions());
        finished = parser.isError() || assembler.isError();
    }

    protected boolean getFlag(ExecutionFlag flag) {
        return messageHandler.getFlag(flag);
    }

    private void addBlock(String name, MindustryBuilding block) {
        vars.computeIfPresent(name, (_, v) -> v.link(block));
        linkedBlocks.add(block);
        links.numval = linkedBlocks.size();
    }
    //</editor-fold>

    //<editor-fold desc="Execution">
    @Override
    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    @Override
    public boolean active() {
        return active && !finished;
    }

    @Override
    public boolean finished() {
        return finished;
    }

    @Override
    public boolean yield() {
        return yield;
    }

    @Override
    public void runTick(int executorIndex, double delta) {
        this.delta = delta;

        int maxAccumulator = maxInstructionScale * ipt.numi();
        if (accumulator > maxAccumulator) {
            accumulatorLoss += accumulator - maxAccumulator;
            accumulator = maxAccumulator;
        }

        int limit = accumulatorHalving ? 0 : 1;

        while (accumulator >= limit) {
            if (emulator.allFinished(executorIndex, finished)) {
                finished = true;
                return;
            }

            runOnce();
            var saved = accumulator--;
            if (accumulatorHalving) limit++;
            if (yield) {
                if (yieldPreservesAccumulator) accumulator = saved;
                messageHandler.trace("            Yielding execution");
                yield = false;
                break;
            }
        }

        accumulator += delta * ipt.numi();
    }

    public void runOnce() {
        if (counter.numval >= instructions.size() || counter.numval < 0) {
            counter.numval = 0;
        }

        if (counter.numval < instructions.size()) {
            counter.isobj = false;
            index = (int) counter.numval++;

            LInstruction instruction = instructions.get(index);
            messageHandler.beginExecution(index, instruction);

            traceInputs();
            active = true;
            instruction.run();
            traceOutputs();

            int newIndex = (int) counter.numval;
            if (newIndex != counter.numval) {
                error(ERR_INVALID_COUNTER, "Non-integer value of of '@counter' (%s) .", counter.printExact());
            }

            if (newIndex < 0 || newIndex > instructions.size()) {
                error(ERR_INVALID_COUNTER, "Value of '@counter' (%d) outside valid range (0 to %d).", newIndex, instructions.size());
            }

            steps++;
            profile[index]++;
            emulator.step++;
        }

        if (counter.numval >= instructions.size()) {
            finish(STOP_ON_PROGRAM_END);
        }
    }

    private final List<String> traceVars = new ArrayList<>();

    private void traceInputs() {
        if (messageHandler.getFlag(TRACE_EXECUTION)) {
            LInstruction instruction = instructions.get(index);
            messageHandler.trace("        Step %d, acc. %.2f, instruction #%d: %s", emulator.step, accumulator, index, instruction);
            traceVars.clear();
            instruction.vars().forEach(v -> traceVars.add(String.format("            %s --> %s", v.name, v.trace())));
        }
    }

    private void traceOutputs() {
        if (messageHandler.getFlag(TRACE_EXECUTION)) {
            LInstruction instruction = instructions.get(index);
            traceVars.forEach(messageHandler::trace);
            instruction.vars().stream()
                    .filter(v -> v.updateStep == emulator.step)
                    .forEach(v -> messageHandler.trace("            %s <-- %s", v.name, v.trace()));

            int nextIndex = (int) counter.numval;
            if (nextIndex != index + 1 && !"stop".equals(instruction.opcode()) && !"wait".equals(instruction.opcode())) {
                String nextInstr = index + 1 < instructions.size()
                        ? " (avoided '" + instructions.get(index + 1) + "')"
                        : "";
                messageHandler.trace("            Jumped to #%d%s", nextIndex, nextInstr);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Results">
    private @Nullable String id;

    @Override
    public String getProcessorId() {
        if (id == null) {
            id = instructions.stream()
                    .filter(LExecutor60.SetI.class::isInstance)
                    .map(LExecutor60.SetI.class::cast)
                    .filter(ix -> ix.result.name.equals("*id"))
                    .map(ix -> extractId(ix.source.printExact()))
                    .filter(s -> !s.isEmpty())
                    .findFirst()
                    .orElseGet(logicBlock::processorId);
        }
        return id;
    }
    
    private String extractId(String text) {
        return Arrays.stream(text.split("\\n"))
                .filter(s -> s.startsWith("id: ") || s.startsWith("name: "))
                .reduce((a, b) -> a.startsWith("id: ") ? a : b)
                .map(s -> s.substring(s.indexOf(" ") + 1))
                .orElse("");
    }

    @Override
    public List<Assertion> getAssertions() {
        return assertions;
    }

    @Override
    public String getFormattedOutput() {
        return textBuffer.getFormattedOutput();
    }

    @Override
    public List<String> getPrintOutput() {
        return textBuffer.getPrintOutput();
    }

    @Override
    public double getAccumulatorLoss() {
        return accumulatorLoss;
    }

    @Override
    public int[] getProfile() {
        return profile;
    }

    @Override
    public List<String> getFormattedProfile() {
        return IntStream.range(0, profile.length)
                .mapToObj(i -> profile[i] >= 0
                        ? String.format("%6d: %s", profile[i], instructions.get(i))
                        : String.format("        %s", instructions.get(i)))
                .toList();
    }

    @Override
    public int getSteps() {
        return steps;
    }

    @Override
    public double getWaitTime() {
        return waitTime;
    }

    @Override
    public TextBuffer textBuffer() {
        return textBuffer;
    }
    //</editor-fold>

    //<editor-fold desc="Instruction support">

    // Note: statement building is performed by the LStatement/LStatements classes in Mindustry. As the emulator
    // doesn't implement this layer, the instruction building is contained here. Also, unlike Mindustry, the
    // instruction classes are not static and have implicit access to the executor instance.

    // Child classes populate instruction builders in constructor.
    protected final Map<String, Function<LStatement, LInstruction>> builders = new HashMap<>();

    private LInstruction build(LStatement statement) {
        return builders.getOrDefault(statement.opcode(), UnknownI::new).apply(statement);
    }

    protected boolean error(ExecutionFlag flag, @PrintFormat String message, Object... args) {
        if (messageHandler.error(flag, message, args)) {
            if (!finished) {
                messageHandler.trace("            Execution finished.");
                finished = true;
            }
            return true;
        }
        return false;
    }

    protected void finish(ExecutionFlag flag) {
        if (messageHandler.getFlag(flag)) {
            if (!finished) {
                // Less indent: finish not triggered by a specific instruction
                messageHandler.trace(flag == STOP_ON_PROGRAM_END
                        ? "%n        Execution finished."
                        : "            Execution finished.");
                finished = true;
            }
        }
    }

    protected @Nullable MindustryBuilding getLink(int index) {
        return index >= 0 && index < linkedBlocks.size() ? linkedBlocks.get(index) : null;
    }

    @Override
    public @Nullable LVar getOptionalVar(String name) {
        LVar result = vars.get(name);
        return result == null || result.constant ? null : result;
    }

    protected boolean checkWritable(LVar var) {
        if (var.constant) {
            error(ERR_ASSIGNMENT_TO_FIXED_VAR, "Cannot assign to fixed variable '%s'.", var.name);
            return false;
        } else {
            var.updateStep = emulator.step;
            return true;
        }
    }

    protected void copyVar(LVar var, LVar source) {
        if (checkWritable(var)) {
            var.set(source);
        }
    }

    protected void setVar(LVar var, double value) {
        if (checkWritable(var)) {
            var.setnum(value);
        }
    }

    protected void setVar(LVar var, @Nullable Object object) {
        if (checkWritable(var)) {
            var.setobj(object);
        }
    }

    protected void setNull(LVar var) {
        if (checkWritable(var)) {
            var.setobj(null);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Instructions">
    protected class UnknownI extends AbstractInstruction {
        public UnknownI(LStatement statement) {
            super(statement);
        }

        @Override
        public void run() {
            error(ERR_UNSUPPORTED_OPCODE, "Instruction '%s' not supported by Mindcode emulator.", opcode());
        }
    }

    protected abstract class AbstractInstruction implements LInstruction {
        private final String opcode;
        private final String text;
        private final List<LVar> vars;

        public AbstractInstruction(LStatement statement) {
            opcode = statement.opcode();
            text = statement.toString();

            List<LVar> vars = new ArrayList<>();
            for (int i = 0; i < statement.args(); i++) {
                if ("var".equals(statement.type(i))) {
                    LVar var = assembler.var(statement.arg((i)));
                    if (!var.constant && !vars.contains(var)) {
                        vars.add(var);
                    }
                }
            }

            this.vars = List.copyOf(vars);
        }

        @Override
        public String opcode() {
            return opcode;
        }

        @Override
        public List<LVar> vars() {
            return vars;
        }

        @Override
        public String toString() {
            return text;
        }
    }


    protected class NoopI extends AbstractInstruction {
        public NoopI(LStatement statement) {
            super(statement);
        }

        @Override
        public void run() {
            noopSteps++;
            emulator.noopSteps++;
        }
    }

    protected class AssertBoundsI extends AbstractInstruction {
        protected final @Nullable AssertionType type;
        protected final LVar multiple;
        protected final LVar min;
        protected final @Nullable AssertOp opMin;
        protected final LVar value;
        protected final @Nullable AssertOp opMax;
        protected final LVar max;
        protected final LVar message;

        public AssertBoundsI(LStatement statement) {
            super(statement);
            type = AssertionType.byName(statement.arg0());
            multiple = assembler.var(statement.arg1());
            min = assembler.var(statement.arg2());
            opMin = AssertOp.byName(statement.arg3());
            value = assembler.var(statement.arg4());
            opMax = AssertOp.byName(statement.arg5());
            max = assembler.var(statement.arg6());
            message = assembler.var(statement.arg7());
        }

        @Override
        public void run() {
            if (type == null || opMin == null || opMax == null) {
                error(ERR_UNSUPPORTED_OPCODE, "Invalid assertion type or operation.");
                return;
            }

            if ((value.isobj ? type.objFunction.get(value.objval) : type.function.get(value.numval))
                    && (type != AssertionType.multiple || (value.num() % multiple.num() == 0))
                    && (opMin.function.get(min.num(), value.num()))
                    && (opMax.function.get(value.num(), max.num()))) {
                // We're okay
            } else {
                if (!error(ERR_RUNTIME_CHECK_FAILED, "Failed runtime check: '%s'.", message.printExact())) {
                    messageHandler.warn("Failed runtime check: '%s'.", message.printExact());
                }
            }
        }
    }

    protected class AssertEqualsI extends AbstractInstruction {
        protected final LVar expected;
        protected final LVar actual;
        protected final LVar message;

        public AssertEqualsI(LStatement statement) {
            super(statement);
            expected = assembler.var(statement.arg0());
            actual = assembler.var(statement.arg1());
            message = assembler.var(statement.arg2());
        }

        @Override
        public void run() {
            assertions.add(new Assertion(expected.printExact(), actual.printExact(), message.printExact()));
        }
    }

    protected class AssertFlushI extends AbstractInstruction {
        protected final LVar flushIndex;

        public AssertFlushI(LStatement statement) {
            super(statement);
            flushIndex = assembler.var(statement.arg0());
        }

        @Override
        public void run() {
            flushIndex.setnum(textBuffer.prepareAssert());
        }
    }

    protected class AssertPrintsI extends AbstractInstruction {
        protected final LVar flushIndex;
        protected final LVar expected;
        protected final LVar message;

        public AssertPrintsI(LStatement statement) {
            super(statement);
            flushIndex = assembler.var(statement.arg0());
            expected = assembler.var(statement.arg1());
            message = assembler.var(statement.arg2());
        }

        @Override
        public void run() {
            assertions.add(new Assertion(expected.printExact(),
                    textBuffer.getAssertedOutput(flushIndex.numi()), message.printExact()));
        }
    }

    protected class ErrorI extends AbstractInstruction {
        protected final List<LVar> args;

        public ErrorI(LStatement statement) {
            super(statement);
            args = statement.arguments().stream().filter(s -> !"null".equals(s)).map(assembler::var).toList();
        }

        @Override
        public void run() {
            error(ERR_RUNTIME_CHECK_FAILED, "error() called: %s.",
                    String.join(" ", args.stream().map(LVar::printExact).toList()));
        }
    }
    //</editor-fold>
}
