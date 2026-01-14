package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.*;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.GlobalCompilerProfile;
import info.teksol.mc.profile.options.Target;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

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

    public BasicEmulator(MessageConsumer messageConsumer, GlobalCompilerProfile profile, EmulatorSchematic schematic, int traceLimit) {
        errorHandler = new EmulatorErrorHandler(messageConsumer, profile.getExecutionFlags(), traceLimit);
        stepLimit = profile.getStepLimit();
        fps = profile.getEmulatorFps();

        // Create shared components
        Target target = profile.getEmulatorTarget();
        LStrings strings = LStrings.create(target.version());
        MindustryMetadata metadata = MindustryMetadata.forVersion(target.version());
        globalVars = LGlobalVars.create(metadata);

        schematic.getBlocks().stream()
                .filter(LogicBlock.class::isInstance)
                .map(LogicBlock.class::cast)
                .forEach(logicBlock -> {
                    LParser parser = LParser.create(errorHandler, metadata, strings, logicBlock.getCode(), logicBlock.isPrivileged());
                    LAssembler assembler = LAssembler.create(errorHandler, metadata, strings, globalVars, logicBlock.isPrivileged());
                    LExecutor executor = LExecutor.create(metadata, assembler, this, logicBlock);
                    executor.load(parser);
                    executors.add(executor);
                });

        running = new BitSet(executors.size());
    }

    //<editor-fold desc="Running">
    @Override
    public void run(int stepLimit) {
        for (int index = 0; index < executors.size(); index++) {
            LExecutor executor = executors.get(index);
            if (executor.isEmpty()) {
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

    public ExecutorResults getExecutorResults(int executor) {
        return executors.get(executor);
    }

    @Override
    public List<Assertion> getAllAssertions() {
        return executors.stream().flatMap(e -> e.getAssertions().stream()).toList();
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
