package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.generator.TooFewPrintfArgumentsException;
import info.teksol.mindcode.mindustry.generator.TooManyPrintfArgumentsException;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuiltInFunctionsTest extends AbstractGeneratorTest {

    @Test
    void generatesPrintln() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(SET, var(0), "10"),
                        createInstruction(PRINT, var(0)),
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(SET, var(1), "\"foo\""),
                        createInstruction(PRINT, var(1)),
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(END)
                ),
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
                        createInstruction(SET, var(0), "10"),
                        createInstruction(PRINT, "\"x: \""),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINT, "\", y: \""),
                        createInstruction(PRINT, "y"),
                        createInstruction(PRINT, "\", z: \""),
                        createInstruction(PRINT, var(0)),
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
                        createInstruction(SET, var(0), "100"),
                        createInstruction(PRINT, "\"Amount: $\""),
                        createInstruction(PRINT, var(0)),
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
                        createInstruction(SET, var(0), "10"),
                        createInstruction(SET, "x", var(0)),
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
                        createInstruction(SET, var(0), "5"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "__fn0_x", var(0)),
                        createInstruction(PRINT, "\"x=\""),
                        createInstruction(PRINT, "__fn0_x"),
                        createInstruction(SET, var(1), "null"),
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
                        createInstruction(SET, var(0), "10"),
                        createInstruction(SET, "X", var(0)),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(PRINT, "\"X=\""),
                        createInstruction(PRINT, "X"),
                        createInstruction(SET, var(1), "null"),
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
