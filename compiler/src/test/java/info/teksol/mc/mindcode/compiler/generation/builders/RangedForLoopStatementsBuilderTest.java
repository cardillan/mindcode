package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class RangedForLoopStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class RangedForLoops {
        @Test
        void compilesConstantForLoopInclusive() {
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
                            allocate heap in bank1;
                            for $A in a ... b do
                                j += i;
                            end;
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(SET, var(0), ":b"),
                    createInstruction(WRITE, ":a", "bank1", "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(JUMP, var(1003), "greaterThanEq", var(1), var(0)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
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
            assertCompilesTo("""
                            for cell1[n] in a ... b do
                                n++;
                            end;
                            """,
                    createInstruction(SET, var(0), "b"),
                    createInstruction(SET, var(1), "n"),
                    createInstruction(WRITE, "a", "cell1", var(1)),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(READ, var(2), "cell1", var(1)),
                    createInstruction(JUMP, var(1003), "greaterThanEq", var(2), var(0)),
                    createInstruction(SET, var(3), "n"),
                    createInstruction(OP, "add", "n", "n", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(READ, var(2), "cell1", var(1)),
                    createInstruction(OP, "add", var(2), var(2), "1"),
                    createInstruction(WRITE, var(2), "cell1", var(1)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }
    }
}
