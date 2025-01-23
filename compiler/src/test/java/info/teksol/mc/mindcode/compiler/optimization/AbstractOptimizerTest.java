package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertAll;

@NullMarked
public abstract class AbstractOptimizerTest<T extends Optimizer> extends AbstractCodeGeneratorTest {
    protected abstract @Nullable Class<T> getTestedClass();

    protected abstract List<Optimization> getAllOptimizations();

    @Override
    protected void setDebugPrinterProvider(MindcodeCompiler compiler) {
        compiler.setDebugPrinterProvider(level -> getDebugPrinter());
    }

    protected DebugPrinter getDebugPrinter() {
        return new FilteredDiffDebugPrinter();
    }

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.OPTIMIZER;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        CompilerProfile profile = super.createCompilerProfile();
        profile.setAllOptimizationLevels(OptimizationLevel.NONE);
        for (Optimization optimization : getAllOptimizations()) {
            profile.setOptimizationLevel(optimization, OptimizationLevel.EXPERIMENTAL);
        }
        profile.setOptimizationPasses(100);
        return profile;
    }


    protected void assertOptimizesTo(List<LogicInstruction> instructions, List<LogicInstruction> expected, ExpectedMessages expectedMessages) {
        // This method cannot be used to test optimizers that rely on AST context structure, because
        // at this moment the AST context is not built for manually created instructions
        List<MindcodeMessage> messages = new ArrayList<>();
        List<LogicInstruction> actual = optimizeInstructions(messages::add, instructions);
        assertAll(
                () -> evaluateResults(expected, actual, messages),
                () -> expectedMessages.validate(messages)
        );
    }

    protected void assertOptimizesTo(CompilerProfile profile, List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        throw new UnsupportedOperationException();
        //assertOptimizesTo(profile, instructions, expected, expectedMessages());
    }

    protected void assertOptimizesTo(List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        assertOptimizesTo(instructions, expected, expectedMessages());
    }

    protected void assertDoesNotOptimize(CompilerProfile profile, LogicInstruction... instructions) {
        throw new UnsupportedOperationException();
        //List<LogicInstruction> list = List.of(instructions);
        //assertOptimizesTo(profile, list, list);
    }

    protected void assertDoesNotOptimize(LogicInstruction... instructions) {
        List<LogicInstruction> list = List.of(instructions);
        assertOptimizesTo(list, list, expectedMessages());
    }

    protected OptimizationCoordinator createMindcodeOptimizer(MessageConsumer messageConsumer) {
        return new OptimizationCoordinator(ip, profile, messageConsumer);
    }

    protected List<LogicInstruction> optimizeInstructions(MessageConsumer messageConsumer, List<LogicInstruction> instructions) {
        final DebugPrinter debugPrinter = getDebugPrinter();
        final List<LogicInstruction> result;
        final OptimizationCoordinator optimizer = createMindcodeOptimizer(messageConsumer);
        optimizer.setDebugPrinter(debugPrinter);
        result = optimizer.optimize(CallGraph.createEmpty(), instructions, mockAstRootContext);
        debugPrinter.print(s -> messageConsumer.addMessage(new OptimizerMessage(MessageLevel.INFO, s)));
        return result;
    }

    private class FilteredDiffDebugPrinter extends DiffDebugPrinter {
        private final @Nullable  Class<T> testedClass;

        private boolean activated = false;

        public FilteredDiffDebugPrinter() {
            super(2);
            setDiffMargin(10000);
            testedClass = getTestedClass();
        }

        @Override
        public void registerIteration(@Nullable Optimizer optimizer, String title, List<LogicInstruction> program) {
            super.registerIteration(optimizer, title, program);
            if (optimizer != null && optimizer.getClass() == testedClass) {
                activated = true;
            }
        }

        @Override
        public void print(Consumer<String> messageConsumer) {
            if (testedClass != null && !activated) {
                throw new RuntimeException("No instructions processed by " + testedClass.getSimpleName() + ".");
            }
            List<ProgramVersion> sel = diffLevel3();
            for (int i = 1; i < sel.size(); i++) {
                if (sel.get(i).getOptimizerClass() == testedClass) {
                    printDiff(messageConsumer, sel.get(i).getTitle(), sel.get(i - 1).getProgram(), sel.get(i).getProgram());
                }
            }
        }

        @Override
        protected boolean printAll() {
            return true;
        }
    }
}
