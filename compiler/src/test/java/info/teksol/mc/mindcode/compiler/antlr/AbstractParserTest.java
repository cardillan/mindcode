package info.teksol.mc.mindcode.compiler.antlr;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.AbstractCompilerTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeParser.AstModuleContext;
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
