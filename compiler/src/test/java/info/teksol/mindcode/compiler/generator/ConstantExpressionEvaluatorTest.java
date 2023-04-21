package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.END;
import static info.teksol.mindcode.logic.Opcode.PRINT;

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
                        createInstruction(PRINT, "\"Value: \""),
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
}