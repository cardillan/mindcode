package info.teksol.mc.mindcode.tests.interceptor;

import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.blocks.BlockPosition;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.blocks.MemoryBlock;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.Optimizer;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionLabelResolver;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.tests.AbstractProcessorTest;
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
            LogicInstructionLabelResolver resolver = new LogicInstructionLabelResolver(compiler.globalCompilerProfile(),
                    ip, getRootAstContext());
            List<LogicInstruction> instructions = resolver.resolve(program);
            String code = LogicInstructionPrinter.toString(ip, instructions,
                    compiler.globalCompilerProfile().isSymbolicLabels(), compiler.globalCompilerProfile().getMlogIndent());

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
