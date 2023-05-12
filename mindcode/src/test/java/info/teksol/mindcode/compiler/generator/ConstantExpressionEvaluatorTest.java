package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.mimex.Icons;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConstantExpressionEvaluatorTest  extends AbstractGeneratorTest {

    @Test
    void removesConstantsFromCode() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "100"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                const VALUE = 100
                                print(VALUE)
                                """
                        )
                )
        );
    }

    @Test
    void removesConstantsInPrintf() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, q("Value: ")),
                        createInstruction(PRINT, "100"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                const VALUE = 100
                                printf("Value: $VALUE")
                                """
                        )
                )
        );
    }

    @Test
    void evaluatesExpressionsWithBinaryLiterals() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "15"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                print(0b0011 + 0b1100)
                                """
                        )
                )
        );
    }

    @Test
    void acceptsStringConstants() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, q("Hello")),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                const TEXT = "Hello"
                                print(TEXT)
                                """
                        )
                )
        );
    }

    @Test
    void refusesVariableBasedConstant() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                a = 10
                                const A = a
                                """
                        )
                )
        );
    }

    @Test
    void refusesNondeterministicConstant() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                const A = rand(10)
                                """
                        )
                )
        );
    }

    @Test
    void refusesFunctionBasedConstant() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                def foo() 5 end
                                const A = foo()
                                """
                        )
                )
        );
    }

    @Test
    void refusesMlogIncompatibleConstants() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                const A = 10**40
                                """
                        )
                )
        );
    }

    @Test
    void evaluatesStringConcatenation() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", q("AB")),
                        createInstruction(PRINT, "a"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                a = "A" + "B"
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void evaluatesStringConcatenationWithConstant() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", q("AB")),
                        createInstruction(PRINT, "a"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                const A = "A"
                                a = A + "B"
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void evaluatesStringConcatenationWithIcon() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", q("[]" + Icons.getIcons().get("ITEM-COAL").format())),
                        createInstruction(PRINT, "a"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                a = "[]" + ITEM-COAL
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void evaluatesStringConcatenationWithNumber() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", q("Total: 10")),
                        createInstruction(PRINT, "a"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                const COUNT = 10
                                a = "Total: " + COUNT
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void evaluatesBooleanConcatenationWithString() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", q("1 is true")),
                        createInstruction(PRINT, "a"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                const TRUTH = 1 < 10
                                a = TRUTH + " is true"
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void refusesInvalidStringOperation() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                a = "A" - "B"
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void refusesPartialStringConcatenation() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                a = "A" + B
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void refusesStringFunction() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                a = max("A", "B")
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void refusesPartialStringFunction() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                a = max("A", 0)
                                print(a)
                                """
                        )
                )
        );
    }

    @Test
    void refusesUnaryStringFunction() {
        assertThrows(GenerationException.class,
                () -> generateUnoptimized(
                        (Seq) translateToAst("""
                                a = not "A"
                                print(a)
                                """
                        )
                )
        );
    }
}
