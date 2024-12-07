package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.PRINT;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.SET;

@NullMarked
class BuiltinFunctionMlogBuilderTest extends AbstractCodeGeneratorTest {

    @Test
    void compilesMlogFunction() {
        assertCompilesTo("""
                        a = 10;
                        mlog("foo", "bar", in "baz", in a, out b, 0, null, true);
                        print(b);
                        """,
                createInstruction(SET, "a", "10"),
                customInstruction("foo", "bar", q("baz"), "a", "b", "0", "null", "true"),
                createInstruction(PRINT, "b")
        );
    }

    @Test
    void refusesInvalidOpcode() {
        assertGeneratesMessage(
                "First argument to the 'mlog' function must be a string literal.",
                "mlog(foo);");
    }

    @Test
    void refusesMissingArgument() {
        assertGeneratesMessage("All arguments to the 'mlog' function need to be specified.",
                """
                        mlog("foo", , 0);
                        """);
    }

    @Test
    void refusesExpression() {
        assertGeneratesMessage("All arguments to the 'mlog' function must be literals or user variables.",
                """
                        mlog("foo", x * y);
                        """);
    }

    @Test
    void refusesMissingKeywords() {
        assertGeneratesMessage("A variable passed to the 'mlog' function must use the 'in' and/or 'out' modifiers.",
                """
                        mlog("foo", x);
                        """);
    }

    @Test
    void refusesOutKeywordWithStringLiteral() {
        assertGeneratesMessage("Variable expected.",
                """
                        mlog("foo", out "bar");
                        """);
    }

    @Test
    void refusesInKeywordWithNumericLiteral() {
        assertGeneratesMessage("A numeric literal passed to the 'mlog' function must not use any modifier.",
                """
                        mlog("foo", in 0);
                        """);
    }

    @Test
    void refusesOutKeywordWithNumericLiteral() {
        assertGeneratesMessage("Variable expected.",
                """
                        mlog("foo", out 0);
                        """);
    }
}