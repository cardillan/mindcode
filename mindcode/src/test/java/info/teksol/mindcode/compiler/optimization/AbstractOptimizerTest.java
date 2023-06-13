package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.generator.CallGraph;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.List;
import java.util.function.Consumer;

import static info.teksol.util.CollectionUtils.findFirstIndex;
import static info.teksol.util.CollectionUtils.findLastIndex;

public abstract class AbstractOptimizerTest<T extends Optimizer> extends AbstractGeneratorTest {
        protected abstract Class<T> getTestedClass();

    protected abstract List<Optimization> getAllOptimizations();

    protected DebugPrinter createDebugPrinter() {
        return new FilteredDiffDebugPrinter();
    }

    protected void assertOptimizesTo(CompilerProfile profile, List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        // rootAstContext is intentionally null
        // This method cannot be used to test optimizers that rely on AST context structure, because
        // at this moment the AST context is not built for manually created instructions
        GeneratorOutput generatorOutput = new GeneratorOutput(CallGraph.createEmpty(), instructions, mockAstRootContext);
        List<LogicInstruction> actual = optimizeInstructions(profile, generatorOutput);
        assertLogicInstructionsMatch(expected, actual);
    }

    protected void assertOptimizesTo(List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        assertOptimizesTo(createCompilerProfile(), instructions, expected);
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

    protected MindcodeOptimizer createMindcodeOptimizer(CompilerProfile profile) {
        return new MindcodeOptimizer(instructionProcessor, profile, messages::add);
    }

    protected List<LogicInstruction> optimizeInstructions(CompilerProfile profile, GeneratorOutput generatorOutput) {
        final DebugPrinter debugPrinter = createDebugPrinter();
        final List<LogicInstruction> result;
        final MindcodeOptimizer optimizer = createMindcodeOptimizer(profile);
        optimizer.setDebugPrinter(debugPrinter);
        result = optimizer.optimize(generatorOutput);
        debugPrinter.print(s -> messages.add(MindcodeMessage.debug(s)));
        return result;
    }

    @Override
    protected GeneratorOutput generateInstructions(CompilerProfile profile, String code) {
        GeneratorOutput generatorOutput = super.generateInstructions(profile, code);
        List<LogicInstruction> instructions = optimizeInstructions(profile, generatorOutput);
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
        public void registerIteration(Optimizer optimizer, int iteration, List<LogicInstruction> program) {
            super.registerIteration(optimizer, iteration, program);
            if (optimizer != null && optimizer.getClass() == testedClass) {
                activated = true;
            }
        }

        @Override
        public void print(Consumer<String> messageConsumer) {
            if (testedClass != null && !activated) {
                throw new RuntimeException("No instructions processed by " + testedClass.getSimpleName() + ".");
            }
            super.print(messageConsumer);
        }

        @Override
        protected List<ProgramVersion> selectProgramVersions() {
            if (testedClass == null) {
                return diffLevel1();
            } else {
                List<ProgramVersion> selected = diffLevel3();
                int from = findFirstIndex(selected, v -> v.getOptimizerClass() == testedClass);
                int to = findLastIndex(selected, v -> v.getOptimizerClass() == testedClass);
                return from > 0 && to >= from && to < selected.size()  ? selected.subList(from - 1, to) : List.of();
            }
        }

        @Override
        protected boolean printAll() {
            return true;
        }
    }
}
