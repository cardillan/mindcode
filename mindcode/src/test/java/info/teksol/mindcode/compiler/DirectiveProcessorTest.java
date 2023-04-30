package info.teksol.mindcode.compiler;

import info.teksol.mindcode.ast.Directive;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectiveProcessorTest {

    @Test
    void processesDirectiveTarget() {
        CompilerProfile profile = CompilerProfile.noOptimizations();
        Seq seq = new Seq(new Directive("target", "ML6"));
        DirectiveProcessor.processDirectives(seq, profile);
        assertEquals(ProcessorVersion.V6, profile.getProcessorVersion());
    }
    @Test
    void processesDirectiveOptimization() {
        CompilerProfile profile = CompilerProfile.noOptimizations();
        Seq seq = new Seq(new Directive("optimization", "basic"));
        DirectiveProcessor.processDirectives(seq, profile);
        assertTrue(profile.getOptimizationLevels().values().stream().allMatch(l -> l == OptimizationLevel.BASIC));
    }

    @Test
    void refusesInvalidOption() {
        CompilerProfile profile = CompilerProfile.noOptimizations();
        Seq seq = new Seq(new Directive("fluffyBunny", "basic"));
        assertThrows(UnknownCompilerDirectiveException.class, () -> DirectiveProcessor.processDirectives(seq, profile));
    }

    @Test
    void refusesInvalidValue() {
        CompilerProfile profile = CompilerProfile.noOptimizations();
        Seq seq = new Seq(new Directive("target", "fluffyBunny"));
        assertThrows(InvalidCompilerDirectiveException.class, () -> DirectiveProcessor.processDirectives(seq, profile));
    }
}
