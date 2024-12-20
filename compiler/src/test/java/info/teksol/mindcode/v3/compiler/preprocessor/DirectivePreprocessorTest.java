package info.teksol.mindcode.v3.compiler.preprocessor;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.Remarks;
import info.teksol.mindcode.compiler.SortCategory;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.v3.MessageConsumer;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstDirectiveSet;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstDirectiveValue;
import info.teksol.util.ExpectedMessages;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static info.teksol.mindcode.InputPosition.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
class DirectivePreprocessorTest {

    private AstDirectiveSet directive(String option, String... values) {
        return new AstDirectiveSet(EMPTY,
                new AstDirectiveValue(EMPTY, option),
                Stream.of(values).map(v -> new AstDirectiveValue(EMPTY, v)).toList());
    }

    private void processDirective(MessageConsumer messageConsumer, CompilerProfile profile, String option, String... values) {
        new DirectivePreprocessor(new PreprocessorContextImpl(messageConsumer, profile))
                .visitDirectiveSet(directive(option, values));
    }

    private void processDirective(CompilerProfile profile, String option, String... values) {
        processDirective(ExpectedMessages.throwOnMessage(), profile, option, values);
    }

    @Test
    void processesDirectiveTarget() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "target", "ML6");
        assertEquals(ProcessorVersion.V6, profile.getProcessorVersion());
    }

    @Test
    void processesDirectiveOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "optimization", "basic");
        assertTrue(profile.getOptimizationLevels().values().stream().allMatch(l -> l == OptimizationLevel.BASIC));
    }

    @Test
    void processesDirectiveInstructionLimit() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setInstructionLimit(1);
        processDirective(profile, "instruction-limit", "900");
        assertEquals(900, profile.getInstructionLimit());
    }

    @Test
    void processesDirectivePasses() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setOptimizationPasses(1);
        processDirective(profile, "passes", "10");
        assertEquals(10, profile.getOptimizationPasses());
    }

    @Test
    void processesDirectiveGoal() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setGoal(GenerationGoal.SIZE);
        processDirective(profile, "goal", "speed");
        assertEquals(GenerationGoal.SPEED, profile.getGoal());
    }

    @Test
    void processesDirectiveRemarks() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setRemarks(Remarks.NONE);
        processDirective(profile, "remarks", "active");
        assertEquals(Remarks.ACTIVE, profile.getRemarks());
    }

    @Test
    void processesDirectiveSortVariables() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        processDirective(profile, "sort-variables", "params", "globals");
        assertEquals(List.of(SortCategory.PARAMS, SortCategory.GLOBALS), profile.getSortVariables());
    }

    @Test
    void refusesInvalidOption() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        ExpectedMessages.create()
                .add("Unknown compiler directive 'fluffyBunny'.")
                .validate(consumer -> processDirective(consumer, profile, "fluffyBunny", "basic"));
    }

    @Test
    void refusesInvalidValue() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        ExpectedMessages.create()
                .add("Invalid value 'fluffyBunny' of compiler directive 'target'.")
                .validate(consumer -> processDirective(consumer, profile, "target", "fluffyBunny"));
    }
}
