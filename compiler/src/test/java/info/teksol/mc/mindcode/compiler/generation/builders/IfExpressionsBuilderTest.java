package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class IfExpressionsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class TernaryOperators {
        @Test
        void compilesTernaryOperator() {
            assertCompilesTo("""
                            a = b ? c : d;
                            """,
                    createInstruction(JUMP, var(1001), "equal", "b", "false"),
                    createInstruction(SET, var(0), "c"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(0), "d"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesTernaryOpPriority() {
            assertCompilesTo("""
                            a = b > c ? b : c;
                            """,
                    createInstruction(OP, "greaterThan", var(0), "b", "c"),
                    createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                    createInstruction(SET, var(1), "b"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, var(1), "c"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "a", var(1))
            );
        }

        @Test
        void compilesNestedTernaryOperators() {
            assertCompilesTo(
                    "a = (b > c) ? 1 : (d > e) ? 2 : 3;",
                    createInstruction(OP, "greaterThan", var(0), "b", "c"),
                    createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                    createInstruction(SET, var(1), "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "greaterThan", var(2), "d", "e"),
                    createInstruction(JUMP, var(1002), "equal", var(2), "false"),
                    createInstruction(SET, var(3), "2"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(3), "3"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(1), var(3)),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "a", var(1)),
                    createInstruction(END)
            );
        }

    }

    @Nested
    class IfExpressions {
        @Test
        void compilesSimpleIfElseStatement() {
            assertCompilesTo("""
                            a = if b then c; else d; end;
                            """,
                    createInstruction(JUMP, var(1001), "equal", "b", "false"),
                    createInstruction(SET, var(0), "c"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(0), "d"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesCompoundIfElseStatement() {
            assertCompilesTo("""
                            a = if b then
                                B;
                            elsif c then
                                C;
                            else
                                D;
                            end;
                            """,
                    createInstruction(JUMP, var(1001), "equal", "b", "false"),
                    createInstruction(SET, var(0), "B"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "equal", "c", "false"),
                    createInstruction(SET, var(1), "C"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(1), "D"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(0), var(1)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesSingleIfBranch() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            if $A > $B then
                                x = $A;
                                $A = $B;
                                $B = x;
                            end;
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(READ, var(1), "bank1", "1"),
                    createInstruction(OP, "greaterThan", var(2), var(0), var(1)),
                    createInstruction(JUMP, var(1001), "equal", var(2), "false"),
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(SET, ":x", var(0)),
                    createInstruction(READ, var(1), "bank1", "1"),
                    createInstruction(WRITE, var(1), "bank1", "0"),
                    createInstruction(WRITE, ":x", "bank1", "1"),
                    createInstruction(SET, var(3), ":x"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(LABEL, var(1002))
            );
        }

        @Test
        void compilesCompoundIfStatementWithoutElseBranch() {
            assertCompilesTo("""
                            if a-- then
                                x = 1;
                            elsif i++ < 10 then
                                x = 2;
                            end;
                            """,
                    createInstruction(SET, var(0), "a"),
                    createInstruction(OP, "sub", "a", "a", "1"),
                    createInstruction(JUMP, var(1001), "equal", var(0), "false"),
                    createInstruction(SET, "x", "1"),
                    createInstruction(SET, var(1), "x"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(2), "i"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(OP, "lessThan", var(3), var(2), "10"),
                    createInstruction(JUMP, var(1003), "equal", var(3), "false"),
                    createInstruction(SET, "x", "2"),
                    createInstruction(SET, var(4), "x"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(4), "null"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(1), var(4)),
                    createInstruction(LABEL, var(1002))
            );
        }
    }

}
