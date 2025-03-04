package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.AbstractTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;

@NullMarked
public abstract class AbstractCodeOutputTest extends AbstractTestBase {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.PRINTER;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setAllOptimizationLevels(OptimizationLevel.NONE);
    }

    protected void assertOutputs(ExpectedMessages expectedMessages, String source, String expected) {
        process(expectedMessages,
                createInputFiles(source),
                c -> {},
                compiler -> {
                    evaluateResults(compiler, expected);
                    return null;
                }
        );
    }

    protected void assertOutputs(String source, String expected) {
        assertOutputs(expectedMessages(), source, expected);
    }

    private void evaluateResults(MindcodeCompiler compiler, String expected) {
        String actual = compiler.getOutput();
        Assertions.assertEquals(expected, actual);
    }

}
