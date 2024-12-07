package info.teksol.mc.mindcode.compiler.ast;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.AbstractTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
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

    // HELPER FUNCTIONS

    protected static final AstIdentifier
            identifier = id("identifier"),
            object = id("object"),
            operand = id("operand"),
            left = id("left"),
            right = id("right"),
            a = id("a"),
            b = id("b"),
            c = id("c"),
            d = id("d"),
            e = id("e");

    protected static final AstLiteral
            l0 = number(0),
            l1 = number(1);

    protected static final AstFunctionArgument NONE = new AstFunctionArgument(EMPTY);

    protected static AstIdentifier id(String name) {
        return new AstIdentifier(EMPTY, name);
    }

    protected static AstIdentifier ext(String name) {
        return new AstIdentifier(EMPTY, "$" + name, true);
    }

    protected static AstBuiltInIdentifier builtIn(String name) {
        return new AstBuiltInIdentifier(EMPTY, name);
    }

    protected static AstLiteralDecimal number(int number) {
        return new AstLiteralDecimal(EMPTY, String.valueOf(number));
    }

    protected static AstLiteralFloat number(double number) {
        return new AstLiteralFloat(EMPTY, String.valueOf(number));
    }

    protected static AstFunctionArgument arg(AstExpression expression) {
        return new AstFunctionArgument(EMPTY, expression, false, false);
    }

    protected static AstFunctionArgument arg(String name) {
        return new AstFunctionArgument(EMPTY, id(name), false, false);
    }

    protected static AstFunctionArgument argIn(String name) {
        return new AstFunctionArgument(EMPTY, id(name), true, false);
    }

    protected static AstFunctionArgument argOut(String name) {
        return new AstFunctionArgument(EMPTY, id(name), false, true);
    }

    protected static AstFunctionArgument argInOut(String name) {
        return new AstFunctionArgument(EMPTY, id(name), true, true);
    }

    protected static List<AstFunctionArgument> args(AstFunctionArgument... args) {
        return List.of(args);
    }

    protected static AstFunctionCall call(AstIdentifier name, AstFunctionArgument... args) {
        return new AstFunctionCall(EMPTY, null, name, args(args));
    }

    protected static AstFunctionCall call(AstExpression object, AstIdentifier name, AstFunctionArgument... args) {
        return new AstFunctionCall(EMPTY, object, name, args(args));
    }
}
