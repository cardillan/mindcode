package info.teksol.mindcode.v3.compiler.generation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class CodeGeneratorIfExpressionsTest extends AbstractCodeGeneratorTest {

    @Nested
    class TernaryOperators {
        @Test
        void compilesTernaryOperator() {
            assertCompiles("""
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
        void compilesSimpleIfElseStatement() {
            assertCompiles("""
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
            assertCompiles("""
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
            assertCompiles("""
                            allocate heap in bank1;
                            if $A > $B then
                                x = $A;
                                $A = $B;
                                $B = x;
                            end;
                            """,
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(READ, var(1), "bank1", "1"),
                    createInstruction(OP, "greaterThan", var(2), var(0), var(1)),
                    createInstruction(JUMP, var(1001), "equal", var(2), "false"),
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(SET, "x", var(0)),
                    createInstruction(READ, var(1), "bank1", "1"),
                    createInstruction(WRITE, var(1), "bank1", "0"),
                    createInstruction(WRITE, "x", "bank1", "1"),
                    createInstruction(SET, var(3), "x"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(LABEL, var(1002))
            );
        }

        @Test
        void compilesCompoundIfStatementWithoutElseBranch() {
            assertCompiles("""
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
