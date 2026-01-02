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
import info.teksol.mc.profile.options.Target;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static info.teksol.mc.emulator.ExecutionFlag.TRACE_EXECUTION;

@NullMarked
public class MimexEmulator implements Emulator {
    private final EmulatorErrorHandler errorHandler;
    private final LExecutor executor;

    public MimexEmulator(MessageConsumer messageConsumer, Target target, Set<ExecutionFlag> flags, String code, int traceLimit) {
        errorHandler = new EmulatorErrorHandler(messageConsumer, flags, traceLimit);

        // Create components
        boolean privileged = target.edition() == ProcessorEdition.WORLD_PROCESSOR;
        MindustryMetadata metadata = MindustryMetadata.forVersion(target.version());
        LStrings strings = LStrings.create(target.version());
        LGlobalVars globalVars = LGlobalVars.create(metadata, privileged);
        LParser parser = LParser.create(errorHandler, metadata, strings, code, privileged);
        LAssembler assembler = LAssembler.create(errorHandler, metadata, strings, globalVars);

        executor = LExecutor.create(metadata, assembler, this);
        executor.load(parser);
    }

    //<editor-fold desc="Setting up">
    @Override
    public void addBlock(String name, MindustryBuilding block) {
        executor.addBlock(name, block);
    }
    //</editor-fold>

    //<editor-fold desc="Running">
    @Override
    public void run(List<LogicInstruction> program, int stepLimit) {
        if (executor.instructions().isEmpty()) {
            executor.textBuffer().append("No program to run.");
            return;
        }

        if (errorHandler.getFlag(TRACE_EXECUTION)) {
            errorHandler.info("%nProgram execution trace:");
        }

        while (!executor.finished()) {
            executor.runOnce(stepLimit);
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
        return executor.steps();
    }

    @Override
    public int noopSteps() {
        return executor.noopSteps();
    }

    @Override
    public int instructionCount() {
        return executor.instructions().size();
    }

    @Override
    public int coveredCount() {
        return IntStream.of(executor.getProfile()).map(i -> i > 0 ? 1 : 0).sum();
    }

    @Override
    public int[] getProfile() {
        return executor.getProfile();
    }

    @Override
    public List<Assertion> getAssertions() {
        return executor.getAssertions();
    }

    @Override
    public TextBuffer getTextBuffer() {
        return executor.textBuffer();
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
