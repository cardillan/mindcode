package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.AccumulatingLogicInstructionPipeline;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static info.teksol.util.CollectionUtils.findFirstIndex;

public abstract class AbstractOptimizerTest<T extends Optimizer> extends AbstractGeneratorTest {
    protected abstract Class<T> getTestedClass();

    protected abstract List<Optimization> getAllOptimizations();

    protected void assertOptimizesTo(CompilerProfile profile, List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        List<LogicInstruction> actual = optimizeInstructions(profile, instructions);
        assertLogicInstructionsMatch(expected, actual);
    }

    protected void assertOptimizesTo(List<LogicInstruction> instructions, List<LogicInstruction> expected) {
        assertOptimizesTo(createCompilerProfile(), instructions, expected);
    }

    protected void assertDoesNotOptimize(CompilerProfile profile, LogicInstruction... instructions) {
        List<LogicInstruction> expected = List.of(instructions);
        List<LogicInstruction> actual = optimizeInstructions(profile, expected);
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

    @NotNull
    protected LogicInstructionPipeline createLogicInstructionPipeline(CompilerProfile profile,
            AccumulatingLogicInstructionPipeline terminus, DebugPrinter debugPrinter) {
        return OptimizationPipeline.createPipelineForProfile(instructionProcessor,
                terminus, profile, debugPrinter, messages::add);
    }

    protected List<LogicInstruction> optimizeInstructions(CompilerProfile profile, List<LogicInstruction> instructions) {
        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
        FilteredDiffDebugPrinter debugPrinter = new FilteredDiffDebugPrinter();
        LogicInstructionPipeline pipeline = createLogicInstructionPipeline(profile, terminus, debugPrinter);

        pipeline.process(instructions);
        if (!debugPrinter.activated) {
            throw new RuntimeException("No instructions processed by " + debugPrinter.testedClass.getSimpleName() + ".");
        }
        debugPrinter.print(s -> messages.add(MindcodeMessage.debug(s)));
        return terminus.getResult();
    }

    @Override
    protected List<LogicInstruction> generateInstructions(CompilerProfile profile, String code) {
        return optimizeInstructions(profile, super.generateInstructions(profile, code));
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
        public void instructionEmitted(Optimizer optimizer, LogicInstruction instruction) {
            super.instructionEmitted(optimizer, instruction);
            if (testedClass == null || optimizer.getClass() == testedClass) {
                activated = true;
            }
        }

        @Override
        public void iterationFinished(Optimizer optimizer, int iteration, List<LogicInstruction> program) {
            super.iterationFinished(optimizer, iteration, program);
            if (testedClass == null || optimizer.getClass() == testedClass) {
                activated = true;
            }
        }

        @Override
        protected List<ProgramVersion> selectProgramVersions() {
            if (testedClass == null) {
                return diffLevel1();
            } else {
                List<ProgramVersion> selected = diffLevel2();
                int index = findFirstIndex(selected, v -> v.optimizer.getClass() == testedClass);
                return index > 0 && index < selected.size()  ? selected.subList(index - 1, index + 1) : List.of();
            }
        }

        @Override
        protected boolean printAll() {
            return true;
        }
    }
}
