package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.List;
import java.util.function.Consumer;

import static info.teksol.util.CollectionUtils.findFirstIndex;

public abstract class AbstractOptimizerTest<T extends Optimizer> extends AbstractGeneratorTest {
    private static final AstContext STATIC_AST_CONTEXT = AstContext.createRootNode();

    protected abstract Class<T> getTestedClass();

    protected abstract List<Optimization> getAllOptimizations();

    protected DebugPrinter createDebugPrinter() {
        return new FilteredDiffDebugPrinter();
    }

    // TODO problem: manually created instructions do not have properly set up contexts
    //      Testing is only possible for optimizers not relying on AstContext being available.
    protected void assertOptimizesTo(CompilerProfile profile, List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        GeneratorOutput generatorOutput = new GeneratorOutput(instructions, STATIC_AST_CONTEXT);
        List<LogicInstruction> actual = optimizeInstructions(profile, generatorOutput);
        assertLogicInstructionsMatch(expected, actual);
    }

    protected void assertOptimizesTo(List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        assertOptimizesTo(createCompilerProfile(), instructions, expected);
    }

    // TODO problem: manually created instructions do not have properly set up contexts
    //      Testing is only possible for optimizers not relying on AstContext being available.
    protected void assertDoesNotOptimize(CompilerProfile profile, LogicInstruction... instructions) {
        List<LogicInstruction> expected = List.of(instructions);
        GeneratorOutput generatorOutput = new GeneratorOutput(expected, STATIC_AST_CONTEXT);
        List<LogicInstruction> actual = optimizeInstructions(profile, generatorOutput);
        assertLogicInstructionsMatch(expected, actual);
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
        return new GeneratorOutput(instructions, generatorOutput.rootAstContext());
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
                List<ProgramVersion> selected = diffLevel2();
                int index = findFirstIndex(selected, v -> v.getOptimizerClass() == testedClass);
                return index > 0 && index < selected.size()  ? selected.subList(index - 1, index + 1) : List.of();
            }
        }

        @Override
        protected boolean printAll() {
            return true;
        }
    }
}
