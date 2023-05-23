package info.teksol.mindcode.compiler;

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
        CompilerProfile profile = CompilerProfile.noOptimizations();
        Seq seq = new Seq(null, new Directive(null, "target", "ML6"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertEquals(ProcessorVersion.V6, profile.getProcessorVersion());
    }

    @Test
    void processesDirectiveOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations();
        Seq seq = new Seq(null, new Directive(null, "optimization", "basic"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertTrue(profile.getOptimizationLevels().values().stream().allMatch(l -> l == OptimizationLevel.BASIC));
    }

    @Test
    void processesDirectiveGoal() {
        CompilerProfile profile = CompilerProfile.noOptimizations();
        profile.setGoal(GenerationGoal.SIZE);
        Seq seq = new Seq(null, new Directive(null, "goal", "speed"));
        DirectiveProcessor.processDirectives(seq, profile, m -> {});
        assertEquals(GenerationGoal.SPEED, profile.getGoal());
    }

    @Test
    void refusesInvalidOption() {
        CompilerProfile profile = CompilerProfile.noOptimizations();
        Seq seq = new Seq(null, new Directive(null, "fluffyBunny", "basic"));
        List<CompilerMessage> messages = new ArrayList<>();
        DirectiveProcessor.processDirectives(seq, profile, messages::add);
        assertEquals(List.of(MindcodeMessage.error("Unknown compiler directive 'fluffyBunny'.")), messages);
    }

    @Test
    void refusesInvalidValue() {
        CompilerProfile profile = CompilerProfile.noOptimizations();
        Seq seq = new Seq(null, new Directive(null, "target", "fluffyBunny"));
        List<CompilerMessage> messages = new ArrayList<>();
        DirectiveProcessor.processDirectives(seq, profile, messages::add);
        assertEquals(List.of(MindcodeMessage.error("Invalid value 'fluffyBunny' of compiler directive 'target'.")), messages);
    }
}
