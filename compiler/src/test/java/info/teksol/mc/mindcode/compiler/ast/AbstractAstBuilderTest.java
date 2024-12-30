package info.teksol.mc.mindcode.compiler.ast;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.AbstractTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstModule;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;

import java.util.List;

@NullMarked
public class AbstractAstBuilderTest extends AbstractTestBase {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.AST_BUILDER;
    }

    protected AstModule build(ExpectedMessages expectedMessages, InputFiles inputFiles) {
        return process(expectedMessages, inputFiles, null, c -> c.getModule(inputFiles.getMainInputFile()));
    }

    protected void assertBuildsTo(ExpectedMessages expectedMessages, String source, AstMindcodeNode expected) {
        AstMindcodeNode actual = build(expectedMessages, InputFiles.fromSource(source));
        Assertions.assertEquals(expected, actual);
    }

    protected void assertBuildsTo(ExpectedMessages expectedMessages, String source, List<AstMindcodeNode> expected) {
        assertBuildsTo(expectedMessages, source, new AstModule(EMPTY, expected));
    }

    protected void assertBuildsTo(String source, List<AstMindcodeNode> expected) {
        assertBuildsTo(expectedMessages(), source, expected);
    }

    protected void assertBuildsTo(String source, AstMindcodeNode expected) {
        assertBuildsTo(expectedMessages(), source, expected);
    }

    protected void assertBuilds(String source) {
        build(expectedMessages(), InputFiles.fromSource(source));
    }
}
