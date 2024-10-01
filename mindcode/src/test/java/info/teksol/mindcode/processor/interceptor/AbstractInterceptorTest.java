package info.teksol.mindcode.processor.interceptor;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionLabelResolver;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.DebugPrinter;
import info.teksol.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mindcode.compiler.optimization.Optimizer;
import info.teksol.mindcode.processor.AbstractProcessorTest;
import info.teksol.mindcode.processor.MindustryMemory;
import info.teksol.mindcode.processor.MindustryObject;
import info.teksol.mindcode.processor.Processor;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractInterceptorTest extends AbstractProcessorTest {

    private static final boolean INTERCEPT = false;

    private DebugPrinter debugPrinter = super.getDebugPrinter();

    @Override
    protected DebugPrinter getDebugPrinter() {
        return debugPrinter;
    }

    @Override
    protected void testAndEvaluateCode(TestCompiler compiler, String title, String code, List<MindustryObject> blocks,
            OutputEvaluator evaluator, Path logFile) {
        debugPrinter = INTERCEPT ? new InterceptingDebugPrinter(compiler, evaluator) : super.getDebugPrinter();
        super.testAndEvaluateCode(compiler, title, code, blocks, evaluator, logFile);
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
            Processor processor = new Processor();
            processor.addBlock(MindustryMemory.createMemoryBank("bank1"));
            processor.addBlock(MindustryMemory.createMemoryBank("bank2"));
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
