package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.Directive;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectiveProcessorTest {

    @Test
    void processesDirectiveTarget() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, null, new Directive(null, null, "target", "ML6"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertEquals(ProcessorVersion.V6, profile.getProcessorVersion());
    }

    @Test
    void processesDirectiveOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, null, new Directive(null, null, "optimization", "basic"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertTrue(profile.getOptimizationLevels().values().stream().allMatch(l -> l == OptimizationLevel.BASIC));
    }

    @Test
    void processesDirectiveInstructionLimit() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setInstructionLimit(1);
        Seq seq = new Seq(null, null, new Directive(null, null, "instruction-limit", "900"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertEquals(900, profile.getInstructionLimit());
    }

    @Test
    void processesDirectivePasses() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setOptimizationPasses(1);
        Seq seq = new Seq(null, null, new Directive(null, null, "passes", "10"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertEquals(10, profile.getOptimizationPasses());
    }

    @Test
    void processesDirectiveGoal() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setGoal(GenerationGoal.SIZE);
        Seq seq = new Seq(null, null, new Directive(null, null, "goal", "speed"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertEquals(GenerationGoal.SPEED, profile.getGoal());
    }

    @Test
    void processesDirectiveRemarks() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.setRemarks(Remarks.NONE);
        Seq seq = new Seq(null, null, new Directive(null, null, "remarks", "active"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertEquals(Remarks.ACTIVE, profile.getRemarks());
    }

    @Test
    void processesDirectiveSortVariables() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, null, new Directive(null, null, "sort-variables", "params,globals"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertEquals(List.of(SortCategory.PARAMS, SortCategory.GLOBALS), profile.getSortVariables());
    }

    @Test
    void refusesInvalidOption() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, null, new Directive(null, null, "fluffyBunny", "basic"));
        List<MindcodeMessage> messages = new ArrayList<>();
        DirectiveProcessor.processDirectives(seq, profile, messages::add);
        assertEquals(List.of(MindcodeCompilerMessage.error("Unknown compiler directive 'fluffyBunny'.")), messages);
    }

    @Test
    void refusesInvalidValue() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        Seq seq = new Seq(null, null, new Directive(null, null, "target", "fluffyBunny"));
        List<MindcodeMessage> messages = new ArrayList<>();
        DirectiveProcessor.processDirectives(seq, profile, messages::add);
        assertEquals(List.of(MindcodeCompilerMessage.error("Invalid value 'fluffyBunny' of compiler directive 'target'.")), messages);
    }
}
