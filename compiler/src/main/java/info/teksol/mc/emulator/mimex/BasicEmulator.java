package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.Assertion;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.emulator.TextBuffer;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.profile.GlobalCompilerProfile;
import info.teksol.mc.profile.options.Target;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

import static info.teksol.mc.emulator.ExecutionFlag.TRACE_EXECUTION;

@NullMarked
public class BasicEmulator implements Emulator {
    private final EmulatorErrorHandler errorHandler;
    private final LGlobalVars globalVars;
    private final List<LExecutor> executors = new ArrayList<>();
    private final BitSet running;

    public final int stepLimit;
    public final double fps;

    public int step;
    public int noopSteps;

    public BasicEmulator(MessageConsumer messageConsumer, GlobalCompilerProfile profile, String code, int traceLimit) {
        errorHandler = new EmulatorErrorHandler(messageConsumer, profile.getExecutionFlags(), traceLimit);
        stepLimit = profile.getStepLimit();
        fps = profile.getEmulatorFps();

        // Create components
        Target target = profile.getEmulatorTarget();
        boolean privileged = target.edition() == ProcessorEdition.WORLD_PROCESSOR;
        MindustryMetadata metadata = MindustryMetadata.forVersion(target.version());
        LStrings strings = LStrings.create(target.version());
        globalVars = LGlobalVars.create(metadata, privileged);
        LParser parser = LParser.create(errorHandler, metadata, strings, code, privileged);
        LAssembler assembler = LAssembler.create(errorHandler, metadata, strings, globalVars);

        LExecutor executor = LExecutor.create(metadata, assembler, this);
        executor.load(parser);
        executors.add(executor);

        running = new BitSet(executors.size());
    }

    //<editor-fold desc="Setting up">
    @Override
    public void addBlock(String name, MindustryBuilding block) {
        executors.getFirst().addBlock(name, block);
    }
    //</editor-fold>

    //<editor-fold desc="Running">
    @Override
    public void run(List<LogicInstruction> program, int stepLimit) {
        for (int index = 0; index < executors.size(); index++) {
            LExecutor executor = executors.get(index);
            if (executor.instructions().isEmpty()) {
                executor.textBuffer().append("No program to run.");
            } else {
                running.set(index);
            }
        }

        if (errorHandler.getFlag(TRACE_EXECUTION)) {
            errorHandler.info("%nProgram execution trace:");
        }

        int tickCount = 0;
        double tickValue = 0f;
        double delta = 60f / fps;

        while (!running.isEmpty()) {
            globalVars.update(tickValue);

            if (errorHandler.getFlag(TRACE_EXECUTION)) {
                errorHandler.info("New tick started (order %d, value %g)", tickCount, tickValue);
            }

            for (int index = 0; index < executors.size(); index++) {
                LExecutor executor = executors.get(index);
                executor.runTick(index, delta);

                if (executor.finished()) {
                    running.clear(index);
                }
            }

            tickCount++;
            tickValue += delta;
        }
    }

    public boolean allFinished(int executor, boolean finished) {
        if (finished) running.clear(executor);

        if (step >= stepLimit) {
            errorHandler.error(ExecutionFlag.ERR_EXECUTION_LIMIT_EXCEEDED, "Execution step limit of %,d exceeded.", stepLimit);
            return true;
        } else {
            return running.isEmpty();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Providing outputs">
    @Override
    public boolean isError() {
        return errorHandler.isError();
    }

    @Override
    public int executionSteps() {
        return step;
    }

    @Override
    public int noopSteps() {
        return noopSteps;
    }

    @Override
    public int getExecutorCount() {
        return executors.size();
    }

    @Override
    public int instructionCount(int executor) {
        return executors.get(executor).instructions().size();
    }

    @Override
    public int coveredCount(int executor) {
        return IntStream.of(executors.get(executor).getProfile()).map(i -> i > 0 ? 1 : 0).sum();
    }

    @Override
    public int[] getProfile(int executor) {
        return executors.get(executor).getProfile();
    }

    @Override
    public List<Assertion> getAssertions(int executor) {
        return executors.get(executor).getAssertions();
    }

    @Override
    public TextBuffer getTextBuffer(int executor) {
        return executors.get(executor).textBuffer();
    }

    //</editor-fold>

    //<editor-fold desc="Executor support">
    public EmulatorErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public double tickDelta() {
        return 1.0 / 60.0;
    }
    //</editor-fold>
}
