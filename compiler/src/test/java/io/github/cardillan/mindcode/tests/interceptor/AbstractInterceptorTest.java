package io.github.cardillan.mindcode.tests.interceptor;

import io.github.cardillan.mindcode.compiler.MindcodeCompiler;
import io.github.cardillan.mindcode.compiler.MindcodeInternalError;
import io.github.cardillan.mindcode.compiler.generation.StackTracker;
import io.github.cardillan.mindcode.compiler.optimization.DiffDebugPrinter;
import io.github.cardillan.mindcode.compiler.optimization.Optimizer;
import io.github.cardillan.mindcode.compiler.postprocess.FinalInstructionResolver;
import io.github.cardillan.mindcode.compiler.postprocess.LogicInstructionPrinter;
import io.github.cardillan.mindcode.emulator.Emulator;
import io.github.cardillan.mindcode.emulator.EmulatorSchematic;
import io.github.cardillan.mindcode.emulator.blocks.BlockPosition;
import io.github.cardillan.mindcode.emulator.blocks.LogicBlock;
import io.github.cardillan.mindcode.emulator.blocks.MemoryBlock;
import io.github.cardillan.mindcode.emulator.blocks.MindustryBuilding;
import io.github.cardillan.mindcode.emulator.mimex.BasicEmulator;
import io.github.cardillan.mindcode.logic.instructions.LogicInstruction;
import io.github.cardillan.mindcode.messages.ExpectedMessages;
import io.github.cardillan.mindcode.tests.AbstractProcessorTest;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public abstract class AbstractInterceptorTest extends AbstractProcessorTest {

    private static final boolean INTERCEPT = true;

    @Override
    protected void setDebugPrinterProvider(MindcodeCompiler compiler) {
        if (INTERCEPT) {
            compiler.setDebugPrinterProvider(level -> new InterceptingDebugPrinter(compiler));
        } else {
            super.setDebugPrinterProvider(compiler);
        }
    }

    private @Nullable RunEvaluator evaluator;

    @Override
    protected void testAndEvaluateCode(@Nullable String title, ExpectedMessages expectedMessages, String code,
            Map<String, MindustryBuilding> blocks, RunEvaluator evaluator, @Nullable Path logFile) {
        this.evaluator = evaluator;
        super.testAndEvaluateCode(title, expectedMessages, code, blocks, evaluator, logFile);
    }

    private class InterceptingDebugPrinter extends DiffDebugPrinter {
        private final MindcodeCompiler compiler;
        private @Nullable ProgramVersion previous;
        private @Nullable ProgramVersion errant;

        public InterceptingDebugPrinter(MindcodeCompiler compiler) {
            super(3);
            setDiffMargin(10000);
            this.compiler = compiler;
        }

        private Emulator runProgram(List<LogicInstruction> program) {
            FinalInstructionResolver resolver = new FinalInstructionResolver(compiler.globalCompilerProfile(),
                    ip, StackTracker.mockInternalStack(), rootAstContext());
            List<LogicInstruction> instructions = resolver.resolve(program);
            String code = LogicInstructionPrinter.toString(ip, instructions,
                    compiler.globalCompilerProfile().isSymbolicLabels(), compiler.globalCompilerProfile().getMlogIndent(), true);

            LogicBlock logicBlock = LogicBlock.createLogicProcessor(ip.getMetadata(), BlockPosition.ZERO_POSITION, code);
            logicBlock.addBlock("bank1", MemoryBlock.createMemoryBank(ip.getMetadata(), BlockPosition.ZERO_POSITION));
            logicBlock.addBlock("bank2", MemoryBlock.createMemoryBank(ip.getMetadata(), BlockPosition.ZERO_POSITION));
            EmulatorSchematic emulatorSchematic = new EmulatorSchematic(List.of(logicBlock));
            Emulator emulator = new BasicEmulator(expectedMessages(), compiler.globalCompilerProfile(), emulatorSchematic);
            emulator.run(STEP_LIMIT);
            return emulator;
        }


        @Override
        public void registerIteration(@Nullable Optimizer optimizer, String title, List<LogicInstruction> program) {
            if (errant == null) {
                Emulator emulator = runProgram(program);
                assert evaluator != null;
                if (!emulator.isError() && evaluator.asExpected(false, emulator)) {
                    previous = new ProgramVersion(optimizer, title, program);
                } else {
                    if (previous == null) {
                        throw new MindcodeInternalError("Test fails on the unoptimized code.");
                    }
                    errant = new ProgramVersion(optimizer, title, program);
                }
            }
        }

        @Override
        public void print(Consumer<String> messageConsumer) {
            if (errant != null) {
                assert previous != null;
                printDiff(System.out::println, errant.getTitle(), previous.getProgram(), errant.getProgram());
            }
        }

        @Override
        protected boolean printAll() {
            return true;
        }
    }
}
