package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class IteratedForLoopStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class IteratedForLoops {
        @Test
        void compilesSimpleForLoop() {
            assertCompiles("""
                            for i = 0; i < 10; i++ do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, "i", "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "lessThan", var(0), "i", "10"),
                    createInstruction(JUMP, var(1003), "equal", var(0), "false"),
                    createInstruction(OP, "add", "j", "j", "i"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(1), "i"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesInfiniteLoop() {
            assertCompiles("""
                            for ;; do
                                j += i;
                            end;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", "j", "j", "i"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }
    }
}
