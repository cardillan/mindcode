package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.v3.InputFiles;
import info.teksol.mindcode.v3.MindcodeCompiler;
import info.teksol.mindcode.v3.compiler.antlr.AbstractParserTest;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstExpressionList;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class AbstractAstBuilderTest extends AbstractParserTest {

    protected AstMindcodeNode build(ExpectedMessages expectedMessages, InputFiles inputFiles, boolean validate) {
        MindcodeCompiler compiler = new MindcodeCompiler(MindcodeCompiler.CompilationPhase.AST_BUILDER, expectedMessages);
        compiler.compile(inputFiles.getMainInputFile());

        if (validate) {
            expectedMessages.validate();
        }
        return compiler.getSyntaxTree(inputFiles.getMainInputFile());
    }

    protected void assertBuilds(ExpectedMessages expectedMessages, String source, AstMindcodeNode expected) {
        AstMindcodeNode actual = build(expectedMessages, InputFiles.fromSource(source), true);
        Assertions.assertEquals(expected, actual);
    }

    protected void assertBuilds(ExpectedMessages expectedMessages, String source, List<AstMindcodeNode> expected) {
        assertBuilds(expectedMessages, source, new AstExpressionList(EMPTY, expected));
    }

    protected void assertBuilds(String source, List<AstMindcodeNode> expected) {
        assertBuilds(expectedMessages(), source, expected);
    }

    protected void assertBuilds(String source, AstMindcodeNode expected) {
        assertBuilds(expectedMessages(), source, expected);
    }

    @Override
    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String source) {
        build(expectedMessages, InputFiles.fromSource(source), true);
    }

    protected void assertBuilds(String source) {
        build(expectedMessages(), InputFiles.fromSource(source), true);
    }

}
