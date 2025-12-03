package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class CaseExpressionsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class CaseExpressions {
        @Test
        void compilesSimpleCaseExpression() {
            assertCompilesTo("""
                            case i
                                when 1 then a;
                                when 2 then b;
                                else c + d;
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), "1"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(JUMP, label(5), "equal", tmp(1), "2"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", tmp(2), ":c", ":d"),
                    createInstruction(SET, tmp(0), tmp(2)),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesRangeBasedCaseExpression() {
            assertCompilesTo("""
                            case i
                                when 1 ... 10 then a;
                                when 10 .. 20 then b;
                                else c + d;
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(JUMP, label(4), "lessThan", tmp(1), "1"),
                    createInstruction(JUMP, label(3), "lessThan", tmp(1), "10"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(JUMP, label(7), "lessThan", tmp(1), "10"),
                    createInstruction(JUMP, label(6), "lessThanEq", tmp(1), "20"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(OP, "add", tmp(2), ":c", ":d"),
                    createInstruction(SET, tmp(0), tmp(2)),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesMultipleAndRangeBasedCaseExpression() {
            assertCompilesTo("""
                            case i
                                when 1 ... 10, 50 ... 90, 100, 110 then a;
                                when 10 .. 20, 30 .. 40, 200, 500 then b;
                                else c + d;
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(JUMP, label(4), "lessThan", tmp(1), "1"),
                    createInstruction(JUMP, label(3), "lessThan", tmp(1), "10"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(JUMP, label(5), "lessThan", tmp(1), "50"),
                    createInstruction(JUMP, label(3), "lessThan", tmp(1), "90"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(JUMP, label(3), "equal", tmp(1), "100"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), "110"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(JUMP, label(8), "lessThan", tmp(1), "10"),
                    createInstruction(JUMP, label(7), "lessThanEq", tmp(1), "20"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(JUMP, label(9), "lessThan", tmp(1), "30"),
                    createInstruction(JUMP, label(7), "lessThanEq", tmp(1), "40"),
                    createInstruction(LABEL, label(9)),
                    createInstruction(JUMP, label(7), "equal", tmp(1), "200"),
                    createInstruction(JUMP, label(7), "equal", tmp(1), "500"),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(OP, "add", tmp(2), ":c", ":d"),
                    createInstruction(SET, tmp(0), tmp(2)),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesCaseExpressionWithNul() {
            assertCompilesTo("""
                            var text = case number
                                when 0, 1 then
                                    "A number I like";
                                when 10**5 .. 10**9 then
                                    "A very big number";
                                when null then
                                    "Oops, a null";
                                else
                                    "An ugly number";
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":number"),
                    createInstruction(JUMP, label(2), "strictEqual", tmp(1), "0"),
                    createInstruction(JUMP, label(2), "equal", tmp(1), "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), q("A number I like")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(5), "lessThan", tmp(1), "100000"),
                    createInstruction(JUMP, label(4), "lessThanEq", tmp(1), "1000000000"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), q("A very big number")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(JUMP, label(7), "strictEqual", tmp(1), "null"),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(SET, tmp(0), q("Oops, a null")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(0), q("An ugly number")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ".text", tmp(0))
            );
        }

        @Test
        void compilesCaseExpressionWithoutElse() {
            assertCompilesTo("""
                            case i
                                when 1 then a;
                                when 2 then b;
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(JUMP, label(2), "equal", tmp(1), "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(4), "equal", tmp(1), "2"),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesCaseExpressionWithoutWhen() {
            assertCompilesTo("""
                            case i
                                else 5;
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(SET, tmp(0), "5"),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesCaseExpressionWithEmptyBranch() {
            assertCompilesTo("""
                            case floor(rand(2))
                                when 0 then
                                    1000;
                                when 1 then
                                    // no op
                            end;
                            """,
                    createInstruction(OP, "rand", tmp(1), "2"),
                    createInstruction(OP, "floor", tmp(2), tmp(1)),
                    createInstruction(SET, tmp(3), tmp(2)),
                    createInstruction(JUMP, label(2), "equal", tmp(3), "0"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), "1000"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(4), "equal", tmp(3), "1"),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesCaseWithExternalVariables() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            case $A
                                when $B then $C;
                                when $D .. $E then $F;
                                else $G;
                            end;
                            """,
                    createInstruction(READ, tmp(1), "bank1", "0"),
                    createInstruction(SET, tmp(2), tmp(1)),
                    createInstruction(READ, tmp(3), "bank1", "1"),
                    createInstruction(JUMP, label(2), "equal", tmp(2), tmp(3)),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(READ, tmp(4), "bank1", "2"),
                    createInstruction(SET, tmp(0), tmp(4)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(READ, tmp(5), "bank1", "3"),
                    createInstruction(JUMP, label(5), "lessThan", tmp(2), tmp(5)),
                    createInstruction(READ, tmp(6), "bank1", "4"),
                    createInstruction(JUMP, label(4), "lessThanEq", tmp(2), tmp(6)),
                    createInstruction(LABEL, label(5)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(READ, tmp(7), "bank1", "5"),
                    createInstruction(SET, tmp(0), tmp(7)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(READ, tmp(8), "bank1", "6"),
                    createInstruction(SET, tmp(0), tmp(8)),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesCaseModifyingExpressionVariable() {
            assertCompilesTo("""
                            i = case i
                                when 1 then 2;
                                when 2 then 3;
                                else 0;
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), "1"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(0), "2"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(JUMP, label(5), "equal", tmp(1), "2"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, tmp(0), "3"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), "0"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":i", tmp(0))
            );
        }

        @Test
        void compilesCaseWithSideEffectsInWhenClause() {
            assertCompilesTo("""
                            case i
                                when a++, ++a then 2;
                                when b--, --b then 3;
                                else 0;
                            end;
                            """,
                    createInstruction(SET, tmp(1), ":i"),
                    createInstruction(SET, tmp(2), ":a"),
                    createInstruction(OP, "add", ":a", ":a", "1"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), tmp(2)),
                    createInstruction(OP, "add", ":a", ":a", "1"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), ":a"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(0), "2"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(3), ":b"),
                    createInstruction(OP, "sub", ":b", ":b", "1"),
                    createInstruction(JUMP, label(5), "equal", tmp(1), tmp(3)),
                    createInstruction(OP, "sub", ":b", ":b", "1"),
                    createInstruction(JUMP, label(5), "equal", tmp(1), ":b"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, tmp(0), "3"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), "0"),
                    createInstruction(LABEL, label(1))
            );
        }
    }
}
