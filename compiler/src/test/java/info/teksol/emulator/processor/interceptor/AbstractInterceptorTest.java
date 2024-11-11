package info.teksol.emulator.processor.interceptor;

import info.teksol.emulator.blocks.Memory;
import info.teksol.emulator.blocks.MindustryBlock;
import info.teksol.emulator.processor.AbstractProcessorTest;
import info.teksol.emulator.processor.Processor;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionLabelResolver;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.DebugPrinter;
import info.teksol.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mindcode.compiler.optimization.Optimizer;
import info.teksol.util.ExpectedMessages;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractInterceptorTest extends AbstractProcessorTest {

    private static final boolean INTERCEPT = false;

    private DebugPrinter debugPrinter = super.getDebugPrinter();

    @Override
    protected DebugPrinter getDebugPrinter() {
        return debugPrinter;
    }

    @Override
    protected void testAndEvaluateCode(TestCompiler compiler, String title, String code, Map<String, MindustryBlock> blocks,
            ExpectedMessages expectedMessages, OutputEvaluator evaluator, Path logFile) {
        debugPrinter = INTERCEPT ? new InterceptingDebugPrinter(compiler, evaluator) : super.getDebugPrinter();
        super.testAndEvaluateCode(compiler, title, code, blocks, expectedMessages, evaluator, logFile);
    }

    private class InterceptingDebugPrinter extends DiffDebugPrinter {
        private final OutputEvaluator evaluator;
        private final TestCompiler compiler;
        private ProgramVersion previous;
        private ProgramVersion errant;
        private String title;

        public InterceptingDebugPrinter(TestCompiler compiler, OutputEvaluator evaluator) {
            super(3);
            setDiffMargin(10000);
            this.evaluator = evaluator;
            this.compiler = compiler;
        }

        private List<String> runProgram(List<LogicInstruction> program) {
            List<LogicInstruction> instructions = LogicInstructionLabelResolver.resolve(compiler.processor, compiler.profile, program);
            Processor processor = new Processor(ExpectedMessages.none(), 1000);
            processor.addBlock("bank1", Memory.createMemoryBank());
            processor.addBlock("bank2", Memory.createMemoryBank());
            processor.run(instructions, MAX_STEPS);
            return processor.getPrintOutput();
        }


        @Override
        public void registerIteration(Optimizer optimizer, String title, List<LogicInstruction> program) {
            if (errant == null) {
                List<String> actualOutput = runProgram(program);
                if (evaluator.compare(false, actualOutput)) {
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
                printDiff(messageConsumer, errant.getTitle(), previous.getProgram(), errant.getProgram());
            }
        }

        @Override
        protected boolean printAll() {
            return true;
        }
    }
}
