package info.teksol.mindcode.v3.compiler.generation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class CodeGeneratorWhileLoopsTest extends AbstractCodeGeneratorTest {

    @Nested
    class DoWhileLoops {
        @Test
        void compilesDoWhileLoop() {
            assertCompiles("""
                            do
                                cell1[a++] = cell1[b--];
                            while a < b;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(SET, var(2), "b"),
                    createInstruction(OP, "sub", "b", "b", "1"),
                    createInstruction(READ, var(3), "cell1", var(2)),
                    createInstruction(WRITE, var(3), "cell1", var(0)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(OP, "lessThan", var(4), "a", "b"),
                    createInstruction(JUMP, var(1001), "notEqual", var(4), "false"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesEmptyDoWhileLoop() {
            assertCompiles("""
                            do while cell1[a++] > 0;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(READ, var(1), "cell1", var(0)),
                    createInstruction(OP, "greaterThan", var(2), var(1), "0"),
                    createInstruction(JUMP, var(1001), "notEqual", var(2), "false"),
                    createInstruction(LABEL, var(1003))
            );
        }
    }

    @Nested
    class WhileLoops {
        @Test
        void compilesWhileLoop() {
            assertCompiles("""
                            while a < b do
                                cell1[a++] = cell1[b--];
                            end;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "lessThan", var(0), "a", "b"),
                    createInstruction(JUMP, var(1003), "equal", var(0), "false"),
                    createInstruction(SET, var(1), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(SET, var(3), "b"),
                    createInstruction(OP, "sub", "b", "b", "1"),
                    createInstruction(READ, var(4), "cell1", var(3)),
                    createInstruction(WRITE, var(4), "cell1", var(1)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesEmptyWhileLoop() {
            assertCompiles("""
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
