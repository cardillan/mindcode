package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class IteratedForLoopStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class IteratedForLoops {
        @Test
        void compilesSimpleForLoop() {
            assertCompilesTo("""
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
            assertCompilesTo("""
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

        @Test
        void compilesForLoopWithMemoryAccess() {
            assertCompilesTo("""
                            for i = 0; i < 10; i++ do
                                cell1[i] = i;
                            end;
                            """,
                    createInstruction(SET, "i", "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "lessThan", var(0), "i", "10"),
                    createInstruction(JUMP, var(1003), "equal", var(0), "false"),
                    createInstruction(SET, var(1), "i"),
                    createInstruction(WRITE, "i", "cell1", var(1)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(3), "i"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }
    }
}
