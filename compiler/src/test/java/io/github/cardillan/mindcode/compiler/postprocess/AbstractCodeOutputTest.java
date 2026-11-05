package io.github.cardillan.mindcode.compiler.postprocess;

import io.github.cardillan.mindcode.compiler.AbstractCompilerTestBase;
import io.github.cardillan.mindcode.compiler.CompilationPhase;
import io.github.cardillan.mindcode.compiler.MindcodeCompiler;
import io.github.cardillan.mindcode.compiler.optimization.OptimizationLevel;
import io.github.cardillan.mindcode.messages.ExpectedMessages;
import io.github.cardillan.mindcode.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;

@NullMarked
public abstract class AbstractCodeOutputTest extends AbstractCompilerTestBase {

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
