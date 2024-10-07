package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuiltInFunctionsLogic7Test extends AbstractGeneratorTest {

    @Override
    protected ProcessorVersion getProcessorVersion() {
        return ProcessorVersion.V7A;
    }

    @Test
    void generatesPrintln() {
        assertCompilesTo("""
                        println()
                        println(10)
                        println("foo")
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
    void handlesEmptyPrintf() {
        assertCompilesTo("""
                        printf("")
                        printf("foo")
                        """,
                createInstruction(PRINT, q("foo")),
                createInstruction(END)
        );
    }

    @Test
    void printfHandlesPositionalParameters() {
        assertCompilesTo("""
                        printf("x: $, y: $, z: $", x, y, 10)
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
    void printfHandlesEscapedDollarSign() {
        assertCompilesTo("""
                        printf("Amount: \\$$", 100)
                        """,
                createInstruction(PRINT, q("Amount: $")),
                createInstruction(PRINT, "100"),
                createInstruction(END)
        );
    }

    @Test
    void printfCatchesTooFewArguments() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        printf("Text: $")
                        """
                )
        );
    }

    @Test
    void printfCatchesTooManyArguments() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        printf("Text: $", 10, 20)
                        """
                )
        );
    }

    @Test
    void printfHandlesVariableReference() {
        assertCompilesTo("""
                        x = 10
                        printf("x=$x")
                        """,
                createInstruction(SET, "x", "10"),
                createInstruction(PRINT, q("x=")),
                createInstruction(PRINT, "x"),
                createInstruction(END)

        );
    }

    @Test
    void printfHandlesLocalVariableReference() {
        assertCompilesTo("""
                        def foo(x)
                            printf("x=$x")
                        end
                        foo(5)
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
    void printfHandlesGlobalVariableReference() {
        assertCompilesTo("""
                        def foo()
                            printf("X=$X")
                        end
                        X = 10
                        foo()
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
    void printfHandlesEnclosedVariableReference() {
        assertCompilesTo("""
                        printf("Time: ${time}sec")
                        """,
                createInstruction(PRINT, q("Time: ")),
                createInstruction(PRINT, "time"),
                createInstruction(PRINT, q("sec")),
                createInstruction(END)
        );
    }

    @Test
    void printfHandlesSequentialVariableReference() {
        assertCompilesTo("""
                        printf("Text: ${x}$y")
                        """,
                createInstruction(PRINT, q("Text: ")),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "y"),
                createInstruction(END)
        );
    }

    @Test
    void printfHandlesVariableReferenceThenPositionalArgument() {
        assertCompilesTo("""
                        printf("Text: ${x}$", y)
                        """,
                createInstruction(PRINT, q("Text: ")),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "y"),
                createInstruction(END)
        );
    }

    @Test
    void printfHandlesAdjacentPositionalArguments() {
        assertCompilesTo("""
                        printf("Text: ${}${}", x, y)
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
                        remark("foo")
                        remark("bar")
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
    void printfHandlesFormattableStringLiterals() {
        assertCompilesTo("""
                        x = 10;
                        printf($"Position: $x");
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
