package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.TimingMessage;
import info.teksol.mindcode.compiler.generator.CallGraph;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractOptimizerTest<T extends Optimizer> extends AbstractGeneratorTest {
    protected abstract Class<T> getTestedClass();

    protected abstract List<Optimization> getAllOptimizations();

    protected DebugPrinter createDebugPrinter() {
        return new FilteredDiffDebugPrinter();
    }

    protected void assertOptimizesTo(CompilerProfile profile, List<LogicInstruction> instructions,
            List<LogicInstruction> expected, Predicate<String> ignoredMessageFilter) {
        // rootAstContext is intentionally null
        // This method cannot be used to test optimizers that rely on AST context structure, because
        // at this moment the AST context is not built for manually created instructions
        TestCompiler compiler = createTestCompiler();
        GeneratorOutput generatorOutput = new GeneratorOutput(CallGraph.createEmpty(), instructions, mockAstRootContext);
        List<LogicInstruction> actual = optimizeInstructions(compiler, generatorOutput);
        assertMessagesAndLogicInstructionsMatch(compiler, expected, actual, ignoredMessageFilter);
    }

    protected void assertOptimizesTo(CompilerProfile profile, List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        assertOptimizesTo(profile, instructions, expected, s -> false);
    }

    protected void assertOptimizesTo(List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        assertOptimizesTo(createCompilerProfile(), instructions, expected);
    }

    protected void assertOptimizesTo(List<LogicInstruction> instructions, List<LogicInstruction> expected,
            Predicate<String> ignoredMessageFilter) {
        assertOptimizesTo(createCompilerProfile(), instructions, expected, ignoredMessageFilter);
    }

    protected void assertDoesNotOptimize(CompilerProfile profile, LogicInstruction... instructions) {
        List<LogicInstruction> list = List.of(instructions);
        assertOptimizesTo(profile, list, list);
    }

    protected void assertDoesNotOptimize(LogicInstruction... instructions) {
        assertDoesNotOptimize(createCompilerProfile(), instructions);
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        CompilerProfile profile = super.createCompilerProfile();
        profile.setAllOptimizationLevels(OptimizationLevel.OFF);
        for (Optimization optimization : getAllOptimizations()) {
            profile.setOptimizationLevel(optimization, OptimizationLevel.AGGRESSIVE);
        }
        return profile;
    }

    protected MindcodeOptimizer createMindcodeOptimizer(TestCompiler compiler) {
        return new MindcodeOptimizer(compiler.processor, compiler.profile, compiler.messages::add);
    }

    protected List<LogicInstruction> optimizeInstructions(TestCompiler compiler, GeneratorOutput generatorOutput) {
        final DebugPrinter debugPrinter = createDebugPrinter();
        final List<LogicInstruction> result;
        final MindcodeOptimizer optimizer = createMindcodeOptimizer(compiler);
        optimizer.setDebugPrinter(debugPrinter);
        result = optimizer.optimize(generatorOutput);
        debugPrinter.print(s -> compiler.messages.add(MindcodeMessage.debug(s)));
        return result;
    }

    @Override
    protected GeneratorOutput generateInstructions(TestCompiler compiler, String code) {
        GeneratorOutput generatorOutput = super.generateInstructions(compiler, code);
        long optimize = System.nanoTime();
        List<LogicInstruction> instructions = optimizeInstructions(compiler, generatorOutput);
        compiler.messages.add(new TimingMessage("Optimize", ((System.nanoTime() - optimize) / 1_000_000L)));
        return new GeneratorOutput(generatorOutput.callGraph(), instructions, generatorOutput.rootAstContext());
    }

    private class FilteredDiffDebugPrinter extends DiffDebugPrinter {
        private final Class<T> testedClass;

        private boolean activated = false;

        public FilteredDiffDebugPrinter() {
            super(2);
            setDiffMargin(10000);
            testedClass = getTestedClass();
        }

        @Override
        public void registerIteration(Optimizer optimizer, int pass, int iteration, List<LogicInstruction> program) {
            super.registerIteration(optimizer, pass, iteration, program);
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
