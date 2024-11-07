package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class BuiltInFunctionsTest extends AbstractGeneratorTest {

    @Test
    void generatesPrintln() {
        assertCompilesTo("""
                        println();
                        println(10);
                        println("foo");
                        """,
                createInstruction(PRINT, q("\n")),
                createInstruction(PRINT, "10"),
                createInstruction(PRINT, q("\n")),
                createInstruction(PRINT, q("foo")),
                createInstruction(PRINT, q("\n")),
                createInstruction(END)
        );
    }

    @Test
    void handlesEmptyPrint() {
        assertCompilesTo("""
                        print($"");
                        print($"foo");
                        """,
                createInstruction(PRINT, q("foo")),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesPositionalParameters() {
        assertCompilesTo("""
                        print($"x: $, y: $, z: $", x, y, 10);
                        """,
                createInstruction(PRINT, q("x: ")),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, q(", y: ")),
                createInstruction(PRINT, "y"),
                createInstruction(PRINT, q(", z: ")),
                createInstruction(PRINT, "10"),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesEscapedDollarSign() {
        assertCompilesTo("""
                        print($"Amount: \\$$", 100);
                        """,
                createInstruction(PRINT, q("Amount: $")),
                createInstruction(PRINT, "100"),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesEscapedEscapes() {
        assertCompilesTo("""
                        print($"Amount: \\$\\\\$", 100);
                        """,
                createInstruction(PRINT, q("Amount: $\\")),
                createInstruction(PRINT, "100"),
                createInstruction(END)
        );
    }

    @Test
    void printFmtCatchesTooFewArguments() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Not enough arguments for 'print' format string."),
                """
                        print($"Text: $ $ $");
                        """
        );
    }

    @Test
    void printFmtCatchesTooManyArguments() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Too many arguments for 'print' format string."),
                """
                        print($"Text: $", 10, 20);
                        """
        );
    }

    @Test
    void printFmtRefusesInterpolatedExpressions() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Unsupported expression 'rand(10)' inside format string - only variable names are allowed."),
                """
                        print($"Text: ${rand(10)}");
                        """
        );
    }

    @Test
    void printFmtRefusesUnclosedExpressions() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Invalid format string (missing '}' after '${')."),
                """
                        print($"Text: ${x$y");
                        """
        );
    }

    @Test
    void printFmtHandlesVariableReference() {
        assertCompilesTo("""
                        x = 10;
                        print($"x=$x");
                        """,
                createInstruction(SET, "x", "10"),
                createInstruction(PRINT, q("x=")),
                createInstruction(PRINT, "x"),
                createInstruction(END)

        );
    }

    @Test
    void printFmtHandlesLocalVariableReference() {
        assertCompilesTo("""
                        def foo(x)
                            print($"x=$x");
                        end;
                        foo(5);
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(PRINT, q("x=")),
                createInstruction(PRINT, "__fn0_x"),
                createInstruction(SET, var(0), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesGlobalVariableReference() {
        assertCompilesTo("""
                        def foo()
                            print($"X=$X");
                        end;
                        X = 10;
                        foo();
                        """,
                createInstruction(SET, "X", "10"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("X=")),
                createInstruction(PRINT, "X"),
                createInstruction(SET, var(0), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesEnclosedVariableReference() {
        assertCompilesTo("""
                        print($"Time: ${time}sec");
                        """,
                createInstruction(PRINT, q("Time: ")),
                createInstruction(PRINT, "time"),
                createInstruction(PRINT, q("sec")),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesSequentialVariableReference() {
        assertCompilesTo("""
                        print($"Text: ${x}$y");
                        """,
                createInstruction(PRINT, q("Text: ")),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "y"),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesVariableReferenceThenPositionalArgument() {
        assertCompilesTo("""
                        print($"Text: ${x}$", y);
                        """,
                createInstruction(PRINT, q("Text: ")),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "y"),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesAdjacentPositionalArguments() {
        assertCompilesTo("""
                        print($"Text: ${}${}", x, y);
                        """,
                createInstruction(PRINT, q("Text: ")),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "y"),
                createInstruction(END)
        );
    }


    @Test
    void printFmtAcceptsSpacesInsideBrackets() {
        assertCompilesTo("""
                        print($"Text: ${ }${ y }", x);
                        """,
                createInstruction(PRINT, q("Text: ")),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "y"),
                createInstruction(END)
        );
    }

    @Test
    void generatesRemarks() {
        assertCompilesTo("""
                        remark("foo");
                        remark("bar");
                        """,
                createInstruction(REMARK, q("foo")),
                createInstruction(REMARK, q("bar")),
                createInstruction(END)
        );
    }

    @Test
    void generatesRemarksWithFormatting() {
        assertCompilesTo("""
                        x = 10; y = 15;
                        remark("Position: $x, $y");
                        """,
                createInstruction(SET, "x", "10"),
                createInstruction(SET, "y", "15"),
                createInstruction(REMARK, q("Position: ")),
                createInstruction(REMARK, "x"),
                createInstruction(REMARK, q(", ")),
                createInstruction(REMARK, "y"),
                createInstruction(END)
        );
    }
    @Test
    void printHandlesFormattableStringLiterals() {
        assertCompilesTo("""
                        x = 10;
                        print($"Position: $x");
                        """,
                createInstruction(SET, "x", "10"),
                createInstruction(PRINT, q("Position: ")),
                createInstruction(PRINT, "x"),
                createInstruction(END)
        );
    }

    @Test
    void printlnHandlesFormattableStringLiterals() {
        assertCompilesTo("""
                        x = 10;
                        println($"Position: $x");
                        """,
                createInstruction(SET, "x", "10"),
                createInstruction(PRINT, q("Position: ")),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, q("\n")),
                createInstruction(END)
        );
    }

    @Test
    void printFmtHandlesFormattableStringLiterals() {
        assertCompilesTo("""
                        x = 10;
                        print($"Position: $x");
                        """,
                createInstruction(SET, "x", "10"),
                createInstruction(PRINT, q("Position: ")),
                createInstruction(PRINT, "x"),
                createInstruction(END)
        );
    }

    @Test
    void remarkHandlesFormattableStringLiterals() {
        assertCompilesTo("""
                        x = 10;
                        remark($"Position: $x");
                        """,
                createInstruction(SET, "x", "10"),
                createInstruction(REMARK, q("Position: ")),
                createInstruction(REMARK, "x"),
                createInstruction(END)
        );
    }

}
