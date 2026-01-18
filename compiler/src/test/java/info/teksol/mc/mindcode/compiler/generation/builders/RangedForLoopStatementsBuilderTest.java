package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class RangedForLoopStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class DescendingRangedForLoops {
        @Test
        void compilesConstantForLoopExclusive() {
            assertCompilesTo("""
                            for i in 1 ... 10 descending do
                                j += i;
                            end;
                            """,
                    createInstruction(OP, "sub", ":i", "10", "1"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "lessThan", ":i", "1"),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "sub", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesConstantForLoopInclusive() {
            assertCompilesTo("""
                            for i in 1 .. 10 descending do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, ":i", "10"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "lessThan", ":i", "1"),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "sub", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesExternalVariableForLoop() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            for $A in a ... b descending do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":a"),
                    createInstruction(OP, "sub", tmp(0), ":b", "1"),
                    createInstruction(WRITE, tmp(0), "bank1", "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(READ, tmp(2), "bank1", "0"),
                    createInstruction(JUMP, label(2), "lessThan", tmp(2), tmp(1)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(READ, tmp(3), "bank1", "0"),
                    createInstruction(OP, "sub", tmp(0), tmp(3), "1"),
                    createInstruction(WRITE, tmp(0), "bank1", "0"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesMemoryAccessForLoop() {
            assertCompilesTo("""
                            for cell1[n] in a ... b descending do
                                n++;
                            end;
                            """,
                    createInstruction(SET, tmp(0), ":n"),
                    createInstruction(SET, tmp(2), ":a"),
                    createInstruction(OP, "sub", tmp(1), ":b", "1"),
                    createInstruction(WRITE, tmp(1), "cell1", tmp(0)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(READ, tmp(3), "cell1", tmp(0)),
                    createInstruction(JUMP, label(2), "lessThan", tmp(3), tmp(2)),
                    createInstruction(SET, tmp(4), ":n"),
                    createInstruction(OP, "add", ":n", ":n", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(READ, tmp(5), "cell1", tmp(0)),
                    createInstruction(OP, "sub", tmp(1), tmp(5), "1"),
                    createInstruction(WRITE, tmp(1), "cell1", tmp(0)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesVariableForLoop() {
            assertCompilesTo("""
                            for i in a .. b descending do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(SET, ":i", ":b"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "lessThan", ":i", tmp(0)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "sub", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }
    }

    @Nested
    class RangedForLoops {
        @Test
        void compilesConstantForLoopExclusive() {
            assertCompilesTo("""
                            for i in 1 ... 10 do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, ":i", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(3), "greaterThanEq", ":i", "10"),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(3))
            );
        }

        @Test
        void compilesConstantForLoopInclusive() {
            assertCompilesTo("""
                            for i in 1 .. 10 do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, ":i", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(3), "greaterThan", ":i", "10"),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(3))
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
                    createInstruction(SET, tmp(1), ":b"),
                    createInstruction(WRITE, ":a", "bank1", "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(READ, tmp(2), "bank1", "0"),
                    createInstruction(JUMP, label(2), "greaterThanEq", tmp(2), tmp(1)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(READ, tmp(3), "bank1", "0"),
                    createInstruction(OP, "add", tmp(0), tmp(3), "1"),
                    createInstruction(WRITE, tmp(0), "bank1", "0"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesMemoryAccessForLoop() {
            assertCompilesTo("""
                            for cell1[n] in a ... b do
                                n++;
                            end;
                            """,
                    createInstruction(SET, tmp(0), ":n"),
                    createInstruction(SET, tmp(2), ":b"),
                    createInstruction(WRITE, ":a", "cell1", tmp(0)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(READ, tmp(3), "cell1", tmp(0)),
                    createInstruction(JUMP, label(2), "greaterThanEq", tmp(3), tmp(2)),
                    createInstruction(SET, tmp(4), ":n"),
                    createInstruction(OP, "add", ":n", ":n", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(READ, tmp(5), "cell1", tmp(0)),
                    createInstruction(OP, "add", tmp(1), tmp(5), "1"),
                    createInstruction(WRITE, tmp(1), "cell1", tmp(0)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesVariableForLoop() {
            assertCompilesTo("""
                            for i in a .. b do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(SET, ":i", ":a"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(3), "greaterThan", ":i", tmp(0)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(3))
            );
        }

        @Test
        void compilesForLoopWithLogicBuiltIns() {
            // Note: this test verifies no defensive copy is made for @links, despite it being a volatile variable
            assertCompilesTo("""
                            for i in 0 ... @links do
                                print(i);
                            end;
                            """,
                    createInstruction(SET, ":i", "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "greaterThanEq", ":i", "@links"),
                    createInstruction(PRINT, ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }

    }
}
