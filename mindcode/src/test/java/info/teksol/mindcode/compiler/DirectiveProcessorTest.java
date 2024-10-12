package info.teksol.mindcode.compiler;

import info.teksol.mindcode.ast.Directive;
import info.teksol.mindcode.ast.DirectiveText;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.InputPosition.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectiveProcessorTest {

    private Directive directive(String option, String value) {
        return new Directive(EMPTY,
                new DirectiveText(EMPTY, option),
                new DirectiveText(EMPTY, value));
    }

    private Directive directive(String option, List<String> values) {
        return new Directive(EMPTY,
                new DirectiveText(EMPTY, option),
                values.stream().map(v -> new DirectiveText(EMPTY, v)).toList());
    }

    @Test
    void processesDirectiveTarget() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, directive("target", "ML6"));
        DirectiveProcessor.processDirectives(seq, profile, ExpectedMessages.refuseAll());
        assertEquals(ProcessorVersion.V6, profile.getProcessorVersion());
    }

    @Test
    void processesDirectiveOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, directive("optimization", "basic"));
        DirectiveProcessor.processDirectives(seq, profile, ExpectedMessages.refuseAll());
        assertTrue(profile.getOptimizationLevels().values().stream().allMatch(l -> l == OptimizationLevel.BASIC));
    }

    @Test
    void processesDirectiveInstructionLimit() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setInstructionLimit(1);
        Seq seq = new Seq(null, directive("instruction-limit", "900"));
        DirectiveProcessor.processDirectives(seq, profile, ExpectedMessages.refuseAll());
        assertEquals(900, profile.getInstructionLimit());
    }

    @Test
    void processesDirectivePasses() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setOptimizationPasses(1);
        Seq seq = new Seq(null, directive("passes", "10"));
        DirectiveProcessor.processDirectives(seq, profile, ExpectedMessages.refuseAll());
        assertEquals(10, profile.getOptimizationPasses());
    }

    @Test
    void processesDirectiveGoal() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setGoal(GenerationGoal.SIZE);
        Seq seq = new Seq(null, directive("goal", "speed"));
        DirectiveProcessor.processDirectives(seq, profile, ExpectedMessages.refuseAll());
        assertEquals(GenerationGoal.SPEED, profile.getGoal());
    }

    @Test
    void processesDirectiveRemarks() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setRemarks(Remarks.NONE);
        Seq seq = new Seq(null, directive("remarks", "active"));
        DirectiveProcessor.processDirectives(seq, profile, ExpectedMessages.refuseAll());
        assertEquals(Remarks.ACTIVE, profile.getRemarks());
    }

    @Test
    void processesDirectiveSortVariables() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, directive("sort-variables", List.of("params","globals")));
        DirectiveProcessor.processDirectives(seq, profile, ExpectedMessages.refuseAll());
        assertEquals(List.of(SortCategory.PARAMS, SortCategory.GLOBALS), profile.getSortVariables());
    }

    @Test
    void refusesInvalidOption() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, directive("fluffyBunny", "basic"));
        ExpectedMessages.create()
                .add("Unknown compiler directive 'fluffyBunny'.")
                .validate(consumer -> DirectiveProcessor.processDirectives(seq, profile, consumer));
    }

    @Test
    void refusesInvalidValue() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, directive("target", "fluffyBunny"));
        ExpectedMessages.create()
                .add("Invalid value 'fluffyBunny' of compiler directive 'target'.")
                .validate(consumer -> DirectiveProcessor.processDirectives(seq, profile, consumer));
    }
}
