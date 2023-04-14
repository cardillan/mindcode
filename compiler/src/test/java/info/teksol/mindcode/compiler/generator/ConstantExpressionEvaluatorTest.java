package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;
import static info.teksol.mindcode.logic.Opcode.END;
import static org.junit.jupiter.api.Assertions.*;

public class ConstantExpressionEvaluatorTest  extends AbstractGeneratorTest {

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