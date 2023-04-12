package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.generator.TooFewPrintfArgumentsException;
import info.teksol.mindcode.compiler.generator.TooManyPrintfArgumentsException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuiltInFunctionsTest extends AbstractGeneratorTest {

    @Test
    void generatesPrintln() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(PRINT, "10"),
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(PRINT, "\"foo\""),
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(END)),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "println() "
                                + "println(10) "
                                + "println(\"foo\")"
                        )
                )
        );
    }

    @Test
    void handlesEmptyPrintf() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"foo\""),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf() "
                                + "printf(\"\") "
                                + "printf(\"foo\")"
                        )
                )
        );
    }

    @Test
    void printfHandlesPositionalParameters() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"x: \""),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINT, "\", y: \""),
                        createInstruction(PRINT, "y"),
                        createInstruction(PRINT, "\", z: \""),
                        createInstruction(PRINT, "10"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf(\"x: $, y: $, z: $\", x, y, 10)"
                        )
                )
        );
    }

    @Test
    void printfHandlesEscapedDollarSign() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"Amount: $\""),
                        createInstruction(PRINT, "100"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf(\"Amount: \\$$\", 100)"
                        )
                )
        );
    }

    @Test
    void printfCatchesTooFewArguments() {
        assertThrows(TooFewPrintfArgumentsException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf(\"Text: $\")"
                        )
                )
        );
    }

    @Test
    void printfCatchesTooManyArguments() {
        assertThrows(TooManyPrintfArgumentsException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf(\"Text: $\", 10, 20)"
                        )
                )
        );
    }

    @Test
    void printfHandlesVariableReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "10"),
                        createInstruction(PRINT, "\"x=\""),
                        createInstruction(PRINT, "x"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "x = 10 "
                                + "printf(\"x=$x\")"
                        )
                )
        );
    }

    @Test
    void printfHandlesLocalVariableReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "__fn0_x", "5"),
                        createInstruction(PRINT, "\"x=\""),
                        createInstruction(PRINT, "__fn0_x"),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "def foo(x) "
                                + "    printf(\"x=$x\") "
                                + "end "
                                + "foo(5)"
                        )
                )
        );
    }

    @Test
    void printfHandlesGlobalVariableReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "X", "10"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(PRINT, "\"X=\""),
                        createInstruction(PRINT, "X"),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "def foo() "
                                + "    printf(\"X=$X\") "
                                + "end "
                                + "X = 10 "
                                + "foo()"
                        )
                )
        );
    }

    @Test
    void printfHandlesEnclosedVariableReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"Time: \""),
                        createInstruction(PRINT, "time"),
                        createInstruction(PRINT, "\"sec\""),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf(\"Time: ${time}sec\")"
                        )
                )
        );
    }

    @Test
    void printfHandlesSequentialVariableReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"Text: \""),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINT, "y"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf(\"Text: ${x}$y\")"
                        )
                )
        );
    }

    @Test
    void printfHandlesVariableReferenceThenPositionalArgument() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"Text: \""),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINT, "y"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf(\"Text: ${x}$\", y)"
                        )
                )
        );
    }

    @Test
    void printfHandlesAdjacentPositionalArguments() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"Text: \""),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINT, "y"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "printf(\"Text: ${}${}\", x, y)"
                        )
                )
        );
    }

}
