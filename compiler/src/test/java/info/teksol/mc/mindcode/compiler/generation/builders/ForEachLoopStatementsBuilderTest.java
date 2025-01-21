package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class ForEachLoopStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class ForEachLoopsInputIterators {
        @Test
        void compilesBasicForEachLoop() {
            assertCompilesTo("""
                            for i in 10, 20, 30 do
                                j += i;
                            end;
                            """,
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(SET, ":i", "10"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003), "marker0"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(SET, ":i", "20"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(SET, ":i", "30"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(LABEL, var(1002))
            );
        }

        @Test
        void compilesBasicForEachLoopWithDeclaration() {
            assertCompilesTo("""
                            for var i in 10, 20, 30 do
                                j += i;
                            end;
                            """,
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(SET, ":i", "10"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003), "marker0"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(SET, ":i", "20"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(SET, ":i", "30"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(LABEL, var(1002))
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(SET, ":i", var(1)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(READ, var(2), "bank1", "1"),
                    createInstruction(SET, ":i", var(2)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(SETADDR, var(0), var(1006)),
                    createInstruction(READ, var(3), "bank1", "2"),
                    createInstruction(SET, ":i", var(3)),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", ":j", ":j", ":i"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1006), "marker0"),
                    createInstruction(LABEL, var(1003))
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(OP, "add", var(2), var(1), "10"),
                    createInstruction(SET, ":i", var(2)),
                    createInstruction(READ, var(3), "bank1", "1"),
                    createInstruction(OP, "add", var(4), var(3), "20"),
                    createInstruction(SET, ":j", var(4)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(READ, var(5), "bank1", "2"),
                    createInstruction(OP, "add", var(6), var(5), "30"),
                    createInstruction(SET, ":i", var(6)),
                    createInstruction(READ, var(7), "bank1", "3"),
                    createInstruction(OP, "add", var(8), var(7), "40"),
                    createInstruction(SET, ":j", var(8)),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", var(9), ":j", ":i"),
                    createInstruction(SET, ":n", var(9)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(LABEL, var(1003))
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(WRITE, "1", "bank1", "0"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(WRITE, "2", "bank1", "0"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(SETADDR, var(0), var(1006)),
                    createInstruction(WRITE, "3", "bank1", "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(WRITE, var(1), "bank1", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1006), "marker0"),
                    createInstruction(LABEL, var(1003))
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
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(SET, ":i", ":a"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003), "marker0"),
                    createInstruction(SET, ":a", ":i"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(SET, ":i", ":b"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SET, ":b", ":i"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(SET, ":i", ":c"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(SET, ":c", ":i"),
                    createInstruction(LABEL, var(1002))
            );
        }

        @Test
        void compilesBasicForEachOutputLoopWithDeclaration() {
            assertCompilesTo("""
                            for var out i in a, b, c do
                                ++i;
                            end;
                            """,
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(SET, ":i", ":a"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003), "marker0"),
                    createInstruction(SET, ":a", ":i"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(SET, ":i", ":b"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SET, ":b", ":i"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(SET, ":i", ":c"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(SET, ":c", ":i"),
                    createInstruction(LABEL, var(1002))
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(SET, ":i", var(1)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(WRITE, ":i", "bank1", "0"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(READ, var(2), "bank1", "1"),
                    createInstruction(SET, ":i", var(2)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(WRITE, ":i", "bank1", "1"),
                    createInstruction(SETADDR, var(0), var(1006)),
                    createInstruction(READ, var(3), "bank1", "2"),
                    createInstruction(SET, ":i", var(3)),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1006), "marker0"),
                    createInstruction(WRITE, ":i", "bank1", "2"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesMultipleIteratorsForEachLoop() {
            assertCompilesTo("""
                            for var i, out j in 1, a, 2, b, 3, c, 4, d do
                                j = i;
                            end;
                            """,
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", ":a"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003), "marker0"),
                    createInstruction(SET, ":a", ":j"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(SET, ":i", "2"),
                    createInstruction(SET, ":j", ":b"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SET, ":b", ":j"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(SET, ":i", "3"),
                    createInstruction(SET, ":j", ":c"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(SET, ":c", ":j"),
                    createInstruction(SETADDR, var(0), var(1006)),
                    createInstruction(SET, ":i", "4"),
                    createInstruction(SET, ":j", ":d"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, ":j", ":i"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1006), "marker0"),
                    createInstruction(SET, ":d", ":j"),
                    createInstruction(LABEL, var(1002))
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
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(SET, ":i", ":a"),
                    createInstruction(SET, ":j", ":b"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003), "marker0"),
                    createInstruction(SET, ":a", ":i"),
                    createInstruction(SET, ":b", ":j"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(SET, ":i", ":c"),
                    createInstruction(SET, ":j", ":d"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, ":i", "1"),
                    createInstruction(SET, ":j", "2"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SET, ":c", ":i"),
                    createInstruction(SET, ":d", ":j"),
                    createInstruction(LABEL, var(1002))
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(WRITE, ":a", "bank1", "0"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(SET, ":a", var(1)),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(WRITE, ":b", "bank1", "0"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(MULTILABEL, var(1005), "marker0"),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(SET, ":b", var(1)),
                    createInstruction(SETADDR, var(0), var(1006)),
                    createInstruction(WRITE, ":c", "bank1", "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(WRITE, "10", "bank1", "0"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1006), "marker0"),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(SET, ":c", var(1)),
                    createInstruction(LABEL, var(1003))
            );
        }
    }

    @Nested
    class InvalidLoops {
        @Test
        void refusesWrongNumberOfValues() {
            assertGeneratesMessages(expectedMessages()
                            .add("The number of values in the list (3) must be an integer multiple of the number of iterators (2)."),
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
    }
}
