package info.teksol.mc.mindcode.tests.interceptor;

import info.teksol.mc.emulator.blocks.Memory;
import info.teksol.mc.emulator.blocks.MindustryBlock;
import info.teksol.mc.emulator.processor.Processor;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.Optimizer;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionLabelResolver;
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

    private static final boolean INTERCEPT = false;

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
            Map<String, MindustryBlock> blocks, RunEvaluator evaluator, @Nullable Path logFile) {
        this.evaluator = evaluator;
        super.testAndEvaluateCode(title, expectedMessages, code, blocks, evaluator, logFile);
    }

    private class InterceptingDebugPrinter extends DiffDebugPrinter {
        private final MindcodeCompiler compiler;
        private @Nullable ProgramVersion previous;
        private @Nullable ProgramVersion errant;
        private @Nullable String title;

        public InterceptingDebugPrinter(MindcodeCompiler compiler) {
            super(3);
            setDiffMargin(10000);
            this.compiler = compiler;
        }

        private Processor runProgram(List<LogicInstruction> program) {
            List<LogicInstruction> instructions = LogicInstructionLabelResolver.resolve(compiler.instructionProcessor(), compiler.compilerProfile(), program);
            Processor processor = new Processor(compiler.instructionProcessor(), expectedMessages(), 1000);
            processor.addBlock("bank1", Memory.createMemoryBank());
            processor.addBlock("bank2", Memory.createMemoryBank());
            processor.run(instructions, MAX_STEPS);
            return processor;
        }


        @Override
        public void registerIteration(@Nullable Optimizer optimizer, String title, List<LogicInstruction> program) {
            if (errant == null) {
                Processor processor = runProgram(program);
                assert evaluator != null;
                if (evaluator.asExpected(false, processor)) {
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
