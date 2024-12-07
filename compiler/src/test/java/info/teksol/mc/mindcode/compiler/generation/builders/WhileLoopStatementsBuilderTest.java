package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class WhileLoopStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class DoWhileLoops {
        @Test
        void compilesDoWhileLoop() {
            assertCompilesTo("""
                            do
                                cell1[a++] = cell1[b--];
                            while a < b;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SET, var(3), "b"),
                    createInstruction(OP, "sub", "b", "b", "1"),
                    createInstruction(SET, var(4), var(3)),
                    createInstruction(READ, var(5), "cell1", var(4)),
                    createInstruction(WRITE, var(5), "cell1", var(1)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(OP, "lessThan", var(6), "a", "b"),
                    createInstruction(JUMP, var(1001), "notEqual", var(6), "false"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesEmptyDoWhileLoop() {
            assertCompilesTo("""
                            do while cell1[a++] > 0;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(READ, var(2), "cell1", var(1)),
                    createInstruction(OP, "greaterThan", var(3), var(2), "0"),
                    createInstruction(JUMP, var(1001), "notEqual", var(3), "false"),
                    createInstruction(LABEL, var(1003))
            );
        }
    }

    @Nested
    class WhileLoops {
        @Test
        void compilesWhileLoop() {
            assertCompilesTo("""
                            while a < b do
                                cell1[a++] = cell1[b--];
                            end;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "lessThan", var(0), "a", "b"),
                    createInstruction(JUMP, var(1003), "equal", var(0), "false"),
                    createInstruction(SET, var(1), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(SET, var(2), var(1)),
                    createInstruction(SET, var(4), "b"),
                    createInstruction(OP, "sub", "b", "b", "1"),
                    createInstruction(SET, var(5), var(4)),
                    createInstruction(READ, var(6), "cell1", var(5)),
                    createInstruction(WRITE, var(6), "cell1", var(2)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesEmptyWhileLoop() {
            assertCompilesTo("""
                            while a++ < 10 do
                            end;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(OP, "lessThan", var(1), var(0), "10"),
                    createInstruction(JUMP, var(1003), "equal", var(1), "false"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void refusesAssigningLoopsToVariables() {
            assertGeneratesMessages(expectedMessages()
                            .addRegex("Parse error: .*").atLeast(1),
                    """
                            a = while b do b--; end;
                            """);
        }
    }
}
