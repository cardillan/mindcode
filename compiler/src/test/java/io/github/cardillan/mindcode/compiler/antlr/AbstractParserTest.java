package io.github.cardillan.mindcode.compiler.antlr;

import io.github.cardillan.mindcode.common.InputFiles;
import io.github.cardillan.mindcode.compiler.AbstractCompilerTestBase;
import io.github.cardillan.mindcode.compiler.CompilationPhase;
import io.github.cardillan.mindcode.compiler.antlr.MindcodeParser.AstModuleContext;
import io.github.cardillan.mindcode.messages.ExpectedMessages;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public abstract class AbstractParserTest extends AbstractCompilerTestBase {

    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.PARSER;
    }

    protected AstModuleContext parse(ExpectedMessages expectedMessages, InputFiles inputFiles) {
        return Objects.requireNonNull(process(expectedMessages, inputFiles, null,
                c -> c.getParseTree(inputFiles.getMainInputFile())));
    }

    protected void assertParses(String source) {
        assertGeneratesMessages(expectedMessages(), source);
    }
}
