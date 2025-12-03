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
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(OP, "add", ":a", ":a", "1"),
                    createInstruction(SET, tmp(1), tmp(0)),
                    createInstruction(SET, tmp(3), ":b"),
                    createInstruction(OP, "sub", ":b", ":b", "1"),
                    createInstruction(SET, tmp(4), tmp(3)),
                    createInstruction(READ, tmp(5), "cell1", tmp(4)),
                    createInstruction(WRITE, tmp(5), "cell1", tmp(1)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(OP, "lessThan", tmp(6), ":a", ":b"),
                    createInstruction(JUMP, label(1), "notEqual", tmp(6), "false"),
                    createInstruction(LABEL, label(3))
            );
        }

        @Test
        void compilesEmptyDoWhileLoop() {
            assertCompilesTo("""
                            do while cell1[a++] > 0;
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(OP, "add", ":a", ":a", "1"),
                    createInstruction(SET, tmp(1), tmp(0)),
                    createInstruction(READ, tmp(2), "cell1", tmp(1)),
                    createInstruction(OP, "greaterThan", tmp(3), tmp(2), "0"),
                    createInstruction(JUMP, label(1), "notEqual", tmp(3), "false"),
                    createInstruction(LABEL, label(3))
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
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "lessThan", tmp(0), ":a", ":b"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(SET, tmp(1), ":a"),
                    createInstruction(OP, "add", ":a", ":a", "1"),
                    createInstruction(SET, tmp(2), tmp(1)),
                    createInstruction(SET, tmp(4), ":b"),
                    createInstruction(OP, "sub", ":b", ":b", "1"),
                    createInstruction(SET, tmp(5), tmp(4)),
                    createInstruction(READ, tmp(6), "cell1", tmp(5)),
                    createInstruction(WRITE, tmp(6), "cell1", tmp(2)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(3))
            );
        }

        @Test
        void compilesEmptyWhileLoop() {
            assertCompilesTo("""
                            while a++ < 10 do
                            end;
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(OP, "add", ":a", ":a", "1"),
                    createInstruction(OP, "lessThan", tmp(1), tmp(0), "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), "false"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(3))
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
