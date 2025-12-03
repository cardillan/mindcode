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
                    createInstruction(JUMP, label(1), "equal", ":b", "false"),
                    createInstruction(SET, tmp(0), ":c"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(0), ":d"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":a", tmp(0))
            );
        }

        @Test
        void compilesTernaryOpPriority() {
            assertCompilesTo("""
                            a = b > c ? b : c;
                            """,
                    createInstruction(OP, "greaterThan", tmp(0), ":b", ":c"),
                    createInstruction(JUMP, label(0), "equal", tmp(0), "false"),
                    createInstruction(SET, tmp(1), ":b"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(1), ":c"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":a", tmp(1))
            );
        }

        @Test
        void compilesNestedTernaryOperators() {
            assertCompilesTo(
                    "a = (b > c) ? 1 : (d > e) ? 2 : 3;",
                    createInstruction(OP, "greaterThan", tmp(0), ":b", ":c"),
                    createInstruction(JUMP, label(0), "equal", tmp(0), "false"),
                    createInstruction(SET, tmp(1), "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "greaterThan", tmp(2), ":d", ":e"),
                    createInstruction(JUMP, label(2), "equal", tmp(2), "false"),
                    createInstruction(SET, tmp(3), "2"),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(3), "3"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), tmp(3)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":a", tmp(1))
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
                    createInstruction(JUMP, label(1), "equal", ":b", "false"),
                    createInstruction(SET, tmp(0), ":c"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(0), ":d"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":a", tmp(0))
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
                    createInstruction(JUMP, label(1), "equal", ":b", "false"),
                    createInstruction(SET, tmp(0), ":B"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(3), "equal", ":c", "false"),
                    createInstruction(SET, tmp(1), ":C"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), ":D"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), tmp(1)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":a", tmp(0))
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
                    createInstruction(READ, tmp(0), "bank1", "0"),
                    createInstruction(READ, tmp(1), "bank1", "1"),
                    createInstruction(OP, "greaterThan", tmp(2), tmp(0), tmp(1)),
                    createInstruction(JUMP, label(0), "equal", tmp(2), "false"),
                    createInstruction(READ, tmp(0), "bank1", "0"),
                    createInstruction(SET, ":x", tmp(0)),
                    createInstruction(READ, tmp(1), "bank1", "1"),
                    createInstruction(WRITE, tmp(1), "bank1", "0"),
                    createInstruction(WRITE, ":x", "bank1", "1"),
                    createInstruction(SET, tmp(3), ":x"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(3), "null"),
                    createInstruction(LABEL, label(1))
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
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(OP, "sub", ":a", ":a", "1"),
                    createInstruction(JUMP, label(1), "equal", tmp(0), "false"),
                    createInstruction(SET, ":x", "1"),
                    createInstruction(SET, tmp(1), ":x"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(2), ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(OP, "lessThan", tmp(3), tmp(2), "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(3), "false"),
                    createInstruction(SET, ":x", "2"),
                    createInstruction(SET, tmp(4), ":x"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(4), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(1), tmp(4)),
                    createInstruction(LABEL, label(2))
            );
        }
    }

}
