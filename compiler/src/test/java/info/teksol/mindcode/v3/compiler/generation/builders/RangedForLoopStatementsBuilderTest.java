package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class RangedForLoopStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class RangedForLoops {
        @Test
        void compilesConstantForLoopInclusive() {
            assertCompiles("""
                            for i in 1 .. 10 do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, "i", "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "greaterThan", "i", "10"),
                    createInstruction(OP, "add", "j", "j", "i"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesConstantForLoopExclusive() {
            assertCompiles("""
                            for i in 1 ... 10 do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, "i", "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "greaterThanEq", "i", "10"),
                    createInstruction(OP, "add", "j", "j", "i"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesVariableForLoop() {
            assertCompiles("""
                            for i in a .. b do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, var(0), "b"),
                    createInstruction(SET, "i", "a"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "greaterThan", "i", var(0)),
                    createInstruction(OP, "add", "j", "j", "i"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesExternalVariableForLoop() {
            assertCompiles("""
                            allocate heap in bank1;
                            for $A in a ... b do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, var(0), "b"),
                    createInstruction(WRITE, "a", "bank1", "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(JUMP, var(1003), "greaterThanEq", var(1), var(0)),
                    createInstruction(OP, "add", "j", "j", "i"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(OP, "add", var(1), var(1), "1"),
                    createInstruction(WRITE, var(1), "bank1", "0"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesMemoryAccessForLoop() {
            assertCompiles("""
                            for cell1[n] in a ... b do
                                n++;
                            end;
                            """,
                    createInstruction(SET, var(0), "b"),
                    createInstruction(WRITE, "a", "cell1", "n"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(READ, var(1), "cell1", "n"),
                    createInstruction(JUMP, var(1003), "greaterThanEq", var(1), var(0)),
                    createInstruction(SET, var(2), "n"),
                    createInstruction(OP, "add", "n", "n", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(READ, var(1), "cell1", "n"),
                    createInstruction(OP, "add", var(1), var(1), "1"),
                    createInstruction(WRITE, var(1), "cell1", "n"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }
    }
}
