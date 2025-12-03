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
                    createInstruction(SET, ":i", "0"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "lessThan", tmp(0), ":i", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(3))
            );
        }

        @Test
        void compilesInfiniteLoop() {
            assertCompilesTo("""
                            for ;; do
                                j += i;
                            end;
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(3))
            );
        }

        @Test
        void compilesForLoopWithMemoryAccess() {
            assertCompilesTo("""
                            for i = 0; i < 10; i++ do
                                cell1[i] = i;
                            end;
                            """,
                    createInstruction(SET, ":i", "0"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "lessThan", tmp(0), ":i", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(WRITE, ":i", "cell1", tmp(1)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(3), ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(3))
            );
        }
    }
}
