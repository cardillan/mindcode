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

@NullMarked
public class BasicEmulator implements Emulator {
    private final EmulatorMessageHandler messageHandler;
    private final LGlobalVars globalVars;
    private final List<LExecutor> executors = new ArrayList<>();
    private final BitSet running;

    public final int stepLimit;
    public final double fps;

    public int step = 1;
    public int noopSteps;

    public BasicEmulator(MessageConsumer messageConsumer, GlobalCompilerProfile profile, EmulatorSchematic schematic) {
        messageHandler = new EmulatorMessageHandler(messageConsumer, profile.getExecutionFlags(), profile.getTraceLimit());
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
                    LParser parser = LParser.create(messageHandler, metadata, strings, logicBlock.getCode(),
                            logicBlock.isPrivileged(), profile.isEnforceInstructionLimit());
                    LAssembler assembler = LAssembler.create(messageHandler, metadata, strings, globalVars, logicBlock.isPrivileged());
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

        messageHandler.trace("%nProgram execution trace:");

        int frame = 1;
        double tickValue = 0f;
        double delta = 60f / fps;

        while (!running.isEmpty()) {
            globalVars.update(tickValue);

            messageHandler.trace("%nFrame %d: @tick %.2f,  @time %.2f, delta %.4f", frame, tickValue, globalVars.getTime(), delta);

            for (int index = 0; index < executors.size(); index++) {
                LExecutor executor = executors.get(index);
                messageHandler.trace("%n    Processor %s (%s):", executor.getProcessorId(),
                        executor.finished() ? "inactive" : executor.active() ? "active" : "waiting");
                executor.runTick(index, delta);

                running.set(index, executor.active());
            }

            frame++;
            tickValue += delta;
        }
    }

    public boolean allFinished(int executor, boolean finished) {
        if (finished) running.clear(executor);

        if (step >= stepLimit) {
            messageHandler.error(ExecutionFlag.ERR_EXECUTION_LIMIT_EXCEEDED, "Execution step limit of %,d exceeded.", stepLimit);
            return true;
        } else {
            return running.isEmpty();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Providing outputs">
    @Override
    public boolean isError() {
        return messageHandler.isError();
    }

    @Override
    public int getExecutionSteps() {
        return step;
    }

    @Override
    public int getNoopSteps() {
        return noopSteps;
    }

    @Override
    public int getExecutorCount() {
        return executors.size();
    }

    @Override
    public List<? extends ExecutorResults> getExecutorResults() {
        return executors;
    }

    @Override
    public ExecutorResults getExecutorResults(int executor) {
        return executors.get(executor);
    }

    @Override
    public List<Assertion> getAllAssertions() {
        return executors.stream().flatMap(e -> e.getAssertions().stream()).toList();
    }

    //</editor-fold>

    //<editor-fold desc="Executor support">
    public EmulatorMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public double tickDelta() {
        return 1.0 / 60.0;
    }
    //</editor-fold>
}
