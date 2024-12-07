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
                    createInstruction(SET, var(1), "i"),
                    createInstruction(JUMP, var(1003), "equal", var(1), "1"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1005), "equal", var(1), "2"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(SET, var(0), "b"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(OP, "add", var(2), "c", "d"),
                    createInstruction(SET, var(0), var(2)),
                    createInstruction(LABEL, var(1001))
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
                    createInstruction(SET, var(1), "i"),
                    createInstruction(JUMP, var(1004), "lessThan", var(1), "1"),
                    createInstruction(JUMP, var(1003), "lessThan", var(1), "10"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1007), "lessThan", var(1), "10"),
                    createInstruction(JUMP, var(1006), "lessThanEq", var(1), "20"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(SET, var(0), "b"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", var(2), "c", "d"),
                    createInstruction(SET, var(0), var(2)),
                    createInstruction(LABEL, var(1001))
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
                    createInstruction(SET, var(1), "i"),
                    createInstruction(JUMP, var(1004), "lessThan", var(1), "1"),
                    createInstruction(JUMP, var(1003), "lessThan", var(1), "10"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(JUMP, var(1005), "lessThan", var(1), "50"),
                    createInstruction(JUMP, var(1003), "lessThan", var(1), "90"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(JUMP, var(1003), "equal", var(1), "100"),
                    createInstruction(JUMP, var(1003), "equal", var(1), "110"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1008), "lessThan", var(1), "10"),
                    createInstruction(JUMP, var(1007), "lessThanEq", var(1), "20"),
                    createInstruction(LABEL, var(1008)),
                    createInstruction(JUMP, var(1009), "lessThan", var(1), "30"),
                    createInstruction(JUMP, var(1007), "lessThanEq", var(1), "40"),
                    createInstruction(LABEL, var(1009)),
                    createInstruction(JUMP, var(1007), "equal", var(1), "200"),
                    createInstruction(JUMP, var(1007), "equal", var(1), "500"),
                    createInstruction(JUMP, var(1006), "always"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(SET, var(0), "b"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(OP, "add", var(2), "c", "d"),
                    createInstruction(SET, var(0), var(2)),
                    createInstruction(LABEL, var(1001))
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
                    createInstruction(SET, var(1), "i"),
                    createInstruction(JUMP, var(1003), "equal", var(1), "1"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(0), "a"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1005), "equal", var(1), "2"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(SET, var(0), "b"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(0), "null"),
                    createInstruction(LABEL, var(1001))
            );
        }

        @Test
        void compilesCaseExpressionWithoutWhen() {
            assertCompilesTo("""
                            case i
                                else 5;
                            end;
                            """,
                    createInstruction(SET, var(1), "i"),
                    createInstruction(SET, var(0), "5"),
                    createInstruction(LABEL, var(1001))
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
                    createInstruction(OP, "rand", var(1), "2"),
                    createInstruction(OP, "floor", var(2), var(1)),
                    createInstruction(SET, var(3), var(2)),
                    createInstruction(JUMP, var(1002), "equal", var(3), "0"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(0), "1000"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1004), "equal", var(3), "1"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(0), "null"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(0), "null"),
                    createInstruction(LABEL, var(1000))
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(READ, var(1), "bank1", "0"),
                    createInstruction(SET, var(2), var(1)),
                    createInstruction(READ, var(3), "bank1", "1"),
                    createInstruction(JUMP, var(1003), "equal", var(2), var(3)),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(READ, var(4), "bank1", "2"),
                    createInstruction(SET, var(0), var(4)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(READ, var(5), "bank1", "3"),
                    createInstruction(JUMP, var(1006), "lessThan", var(2), var(5)),
                    createInstruction(READ, var(6), "bank1", "4"),
                    createInstruction(JUMP, var(1005), "lessThanEq", var(2), var(6)),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(READ, var(7), "bank1", "5"),
                    createInstruction(SET, var(0), var(7)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(READ, var(8), "bank1", "6"),
                    createInstruction(SET, var(0), var(8)),
                    createInstruction(LABEL, var(1001))
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
                    createInstruction(SET, var(1), "i"),
                    createInstruction(JUMP, var(1003), "equal", var(1), "1"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(0), "2"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1005), "equal", var(1), "2"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(SET, var(0), "3"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(0), "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "i", var(0))
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
                    createInstruction(SET, var(1), "i"),
                    createInstruction(SET, var(2), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(JUMP, var(1003), "equal", var(1), var(2)),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(JUMP, var(1003), "equal", var(1), "a"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(0), "2"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(3), "b"),
                    createInstruction(OP, "sub", "b", "b", "1"),
                    createInstruction(JUMP, var(1005), "equal", var(1), var(3)),
                    createInstruction(OP, "sub", "b", "b", "1"),
                    createInstruction(JUMP, var(1005), "equal", var(1), "b"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(SET, var(0), "3"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(0), "0"),
                    createInstruction(LABEL, var(1001))
            );
        }
    }
}
