package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class ForEachLoopStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class DescendingIterations {
        @Test
        void compilesBasicParallelIterations() {
            assertCompilesTo("""
                            for i in 1, 2; j in 3, 4 descending do print(i+j); end;
                            """,
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", "4"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":i", "2"),
                    createInstruction(SET, ":j", "3"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(1), ":i", ":j"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesOutputParallelIteration() {
            assertCompilesTo("""
                            for i in 1, 2; out j in a, b descending do j = i; end;
                            """,
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", ":b"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":b", ":j"),
                    createInstruction(SET, ":i", "2"),
                    createInstruction(SET, ":j", ":a"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":a", ":j"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesUnevenParallelIteration() {
            assertCompilesTo("""
                            for i, j in 1, 2, 3, 4 descending; out k in a, b do k = i + j; end;
                            """,
                    createInstruction(SET, ":j", "4"),
                    createInstruction(SET, ":i", "3"),
                    createInstruction(SET, ":k", ":a"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":a", ":k"),
                    createInstruction(SET, ":j", "2"),
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":k", ":b"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(1), ":i", ":j"),
                    createInstruction(SET, ":k", tmp(1)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":b", ":k"),
                    createInstruction(LABEL, label(2))
            );
        }
    }

    @Nested
    class ForEachLoopsInputIterators {
        @Test
        void compilesBasicForEachLoop() {
            assertCompilesTo("""
                            for i in 10, 20, 30 do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, ":i", "10"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":i", "20"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":i", "30"),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesBasicForEachLoopWithDeclaration() {
            assertCompilesTo("""
                            for var i in 10, 20, 30 do
                                j += i;
                            end;
                            """,
                    createInstruction(SET, ":i", "10"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":i", "20"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":i", "30"),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesBasicForEachLoopExternal() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            for i in $A, $B, $C do
                                j += i;
                            end;
                            """,
                    createInstruction(READ, tmp(1), "bank1", "0"),
                    createInstruction(SET, ":i", tmp(1)),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(READ, tmp(2), "bank1", "1"),
                    createInstruction(SET, ":i", tmp(2)),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(READ, tmp(3), "bank1", "2"),
                    createInstruction(SET, ":i", tmp(3)),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesMultipleIteratorsForEachLoop() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            for i, j in $A + 10, $B + 20, $C + 30, $D + 40 do
                                n = j + i;
                            end;
                            """,
                    createInstruction(READ, tmp(1), "bank1", "0"),
                    createInstruction(OP, "add", tmp(2), tmp(1), "10"),
                    createInstruction(SET, ":i", tmp(2)),
                    createInstruction(READ, tmp(3), "bank1", "1"),
                    createInstruction(OP, "add", tmp(4), tmp(3), "20"),
                    createInstruction(SET, ":j", tmp(4)),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(READ, tmp(5), "bank1", "2"),
                    createInstruction(OP, "add", tmp(6), tmp(5), "30"),
                    createInstruction(SET, ":i", tmp(6)),
                    createInstruction(READ, tmp(7), "bank1", "3"),
                    createInstruction(OP, "add", tmp(8), tmp(7), "40"),
                    createInstruction(SET, ":j", tmp(8)),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(9), ":j", ":i"),
                    createInstruction(SET, ":n", tmp(9)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesExternalIteratorForEachLoop() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            for $A in 1, 2, 3 do
                                $B = $A;
                            end;
                            """,
                    createInstruction(WRITE, "1", "bank1", "0"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(WRITE, "2", "bank1", "0"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(WRITE, "3", "bank1", "0"),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(READ, tmp(1), "bank1", "0"),
                    createInstruction(WRITE, tmp(1), "bank1", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(LABEL, label(2))
            );
        }
    }

    @Nested
    class ForEachLoopsOutputIterators {
        @Test
        void compilesBasicForEachOutputLoop() {
            assertCompilesTo("""
                            for out i in a, b, c do
                                ++i;
                            end;
                            """,
                    createInstruction(SET, ":i", ":a"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":a", ":i"),
                    createInstruction(SET, ":i", ":b"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":b", ":i"),
                    createInstruction(SET, ":i", ":c"),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(SET, ":c", ":i"),
                    createInstruction(LABEL, label(2))

            );
        }

        @Test
        void compilesBasicForEachOutputLoopWithDeclaration() {
            assertCompilesTo("""
                            for var out i in a, b, c do
                                ++i;
                            end;
                            """,
                    createInstruction(SET, ":i", ":a"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":a", ":i"),
                    createInstruction(SET, ":i", ":b"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":b", ":i"),
                    createInstruction(SET, ":i", ":c"),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(SET, ":c", ":i"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesBasicForEachOutputLoopExternal() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            for out i in $A, $B, $C do
                                ++i;
                            end;
                            """,
                    createInstruction(READ, tmp(1), "bank1", "0"),
                    createInstruction(SET, ":i", tmp(1)),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(WRITE, ":i", "bank1", "0"),
                    createInstruction(READ, tmp(2), "bank1", "1"),
                    createInstruction(SET, ":i", tmp(2)),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(WRITE, ":i", "bank1", "1"),
                    createInstruction(READ, tmp(3), "bank1", "2"),
                    createInstruction(SET, ":i", tmp(3)),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(WRITE, ":i", "bank1", "2"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesMultipleIteratorsForEachLoop() {
            assertCompilesTo("""
                            for var i, out j in 1, a, 2, b, 3, c, 4, d do
                                j = i;
                            end;
                            """,
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", ":a"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":a", ":j"),
                    createInstruction(SET, ":i", "2"),
                    createInstruction(SET, ":j", ":b"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":b", ":j"),
                    createInstruction(SET, ":i", "3"),
                    createInstruction(SET, ":j", ":c"),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(SET, ":c", ":j"),
                    createInstruction(SET, ":i", "4"),
                    createInstruction(SET, ":j", ":d"),
                    createInstruction(SETADDR, tmp(0), label(6)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(6)),
                    createInstruction(SET, ":d", ":j"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesMultipleOutIteratorsForEachLoop() {
            assertCompilesTo("""
                            for out i, out j in a, b, c, d do
                                i = 1;
                                j = 2;
                            end;
                            """,
                    createInstruction(SET, ":i", ":a"),
                    createInstruction(SET, ":j", ":b"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":a", ":i"),
                    createInstruction(SET, ":b", ":j"),
                    createInstruction(SET, ":i", ":c"),
                    createInstruction(SET, ":j", ":d"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", "2"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":c", ":i"),
                    createInstruction(SET, ":d", ":j"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesExternalIteratorForEachLoop() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            for out $A in a, b, c do
                                $A = 10;
                            end;
                            """,
                    createInstruction(WRITE, ":a", "bank1", "0"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(READ, tmp(1), "bank1", "0"),
                    createInstruction(SET, ":a", tmp(1)),
                    createInstruction(WRITE, ":b", "bank1", "0"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(READ, tmp(1), "bank1", "0"),
                    createInstruction(SET, ":b", tmp(1)),
                    createInstruction(WRITE, ":c", "bank1", "0"),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(WRITE, "10", "bank1", "0"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(READ, tmp(1), "bank1", "0"),
                    createInstruction(SET, ":c", tmp(1)),
                    createInstruction(LABEL, label(2))
            );
        }
    }

    @Nested
    class InvalidLoops {
        @Test
        void refusesWrongNumberOfValues() {
            assertGeneratesMessages(expectedMessages()
                            .add("The number of values in the list (3) must be an integer multiple of the number of iterators (2).")
                            .add("Not enough values to supply this iterator group (provided: 3, required: 4).")
                    ,
                    "for i, j in 1, 2, 3 do end;");
        }

        @Test
        void refusesConstantAsIterator() {
            assertGeneratesMessages(expectedMessages().add("Assignment to constant or parameter 'i' not allowed."),
                    "const i = 10; for i in 1, 2, 3 do end;");
        }

        @Test
        void refusesParameterAsIterator() {
            assertGeneratesMessages(expectedMessages().add("Assignment to constant or parameter 'i' not allowed."),
                    "param i = 10; for i in 1, 2, 3 do end;");
        }

        @Test
        void refusesLiteralsAsOutValues() {
            assertGeneratesMessages(expectedMessages().add("Variable expected.").repeat(3),
                    "for out i in 1, 2, 3 do end;");
        }

        @Test
        void refusesUnevenListLengths() {
            assertGeneratesMessage(
                    "Not enough values to supply this iterator group (provided: 1, required: 2).",
                    "for i in 1, 2; j in 3 do end;");
        }

    }

    @Nested
    class ParallelIterations {
        @Test
        void compilesBasicParallelIterations() {
            assertCompilesTo("""
                            for i in 1, 2; j in 3, 4 do print(i+j); end;
                            """,
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", "3"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":i", "2"),
                    createInstruction(SET, ":j", "4"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(1), ":i", ":j"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesOutputParallelIteration() {
            assertCompilesTo("""
                            for i in 1, 2; out j in a, b do j = i; end;
                            """,
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", ":a"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":a", ":j"),
                    createInstruction(SET, ":i", "2"),
                    createInstruction(SET, ":j", ":b"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":j", ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":b", ":j"),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void compilesUnevenParallelIteration() {
            assertCompilesTo("""
                            for i, j in 1, 2, 3, 4; out k in a, b do k = i + j; end;
                            """,
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", "2"),
                    createInstruction(SET, ":k", ":a"),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ":a", ":k"),
                    createInstruction(SET, ":i", "3"),
                    createInstruction(SET, ":j", "4"),
                    createInstruction(SET, ":k", ":b"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(1), ":i", ":j"),
                    createInstruction(SET, ":k", tmp(1)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ":b", ":k"),
                    createInstruction(LABEL, label(2))
            );
        }
    }

    @Nested
    class Subarrays {
        @Test
        void compilesInternalSubarrayForLoop() {
            assertCompilesTo("""
                            var a[10];
                            for i in a[5 .. 6] do print(i); end;
                            """,
                    createInstruction(SET, ":i", ".a*5"),
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003)),
                    createInstruction(SET, ":i", ".a*6"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, ":i"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(MULTIJUMP, var(0), "0", "0"),
                    createInstruction(MULTILABEL, var(1004)),
                    createInstruction(LABEL, var(1002))
            );
        }

        @Test
        void compilesExternalSubarrayForLoop() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[10];
                            for i in a[5 .. 6] do print(i); end;
                            """,
                    createInstruction(READ, tmp(5), "cell1", "5"),
                    createInstruction(SET, ":i", tmp(5)),
                    createInstruction(SETADDR, tmp(10), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(READ, tmp(6), "cell1", "6"),
                    createInstruction(SET, ":i", tmp(6)),
                    createInstruction(SETADDR, tmp(10), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(10), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(LABEL, label(2))

            );
        }

        @Test
        void compilesMemorySubarrayForLoop() {
            assertCompilesTo("""
                            for i in cell1[5 .. 6] do print(i); end;
                            """,
                    createInstruction(READ, tmp(1), "cell1", "5"),
                    createInstruction(SET, ":i", tmp(1)),
                    createInstruction(SETADDR, tmp(0), label(3)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(READ, tmp(2), "cell1", "6"),
                    createInstruction(SET, ":i", tmp(2)),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, ":i"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(LABEL, label(2))
            );
        }
    }
}
