package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.END;
import static info.teksol.mindcode.logic.Opcode.PRINT;
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


}