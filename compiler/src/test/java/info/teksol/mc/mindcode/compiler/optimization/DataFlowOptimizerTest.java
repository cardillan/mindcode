package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class DataFlowOptimizerTest extends AbstractOptimizerTest<DataFlowOptimizer> {

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED);
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        // Need to test interactions with all optimizers except function inlining
        return Optimization.LIST.stream().filter(o -> o != Optimization.FUNCTION_INLINING).toList();
    }

    @Override
    protected Class<DataFlowOptimizer> getTestedClass() {
        return DataFlowOptimizer.class;
    }

    @Nested
    class Arrays {
        // This test makes sure the assignments were all fully evaluated in one pass.
        @Test
        void handlesArrays() {
            assertCompilesTo("""
                            const SIZE = 2;
                            param LIMIT = SIZE;
                            
                            var a[SIZE];
                            
                            for i in 0 ... LIMIT do
                                a[i] = 2 * (i + 1);
                            end;
                            
                            for i in 0 ... LIMIT do
                                println(a[i]);
                            end;
                            """,
                    createInstruction(LABEL, "__start__"),
                    createInstruction(SET, "LIMIT", "2"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(SETADDR, ".a*wret", label(12)),
                    createInstruction(JUMP, label(2), "greaterThanEq", "0", "LIMIT"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(OP, "add", tmp(2), ":i", "1"),
                    createInstruction(OP, "mul", tmp(3), "2", tmp(2)),
                    createInstruction(SET, ".a*w", tmp(3)),
                    createInstruction(OP, "mul", tmp(6), ":i", "2"),
                    createInstruction(MULTICALL, label(8), tmp(6), "marker0"),
                    createInstruction(LABEL, label(12)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(6), "lessThan", ":i", "LIMIT"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(SETADDR, ".a*rret", label(13)),
                    createInstruction(JUMP, "__start__", "greaterThanEq", "0", "LIMIT"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(OP, "mul", tmp(7), ":i", "2"),
                    createInstruction(MULTICALL, label(10), tmp(7), "marker1"),
                    createInstruction(LABEL, label(13)),
                    createInstruction(PRINT, ".a*r"),
                    createInstruction(PRINT, q("\n")),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(7), "lessThan", ":i", "LIMIT"),
                    createInstruction(END),
                    createInstruction(MULTILABEL, label(8), "marker0"),
                    createInstruction(SET, ".a*0", ".a*w"),
                    createInstruction(RETURN, ".a*wret"),
                    createInstruction(MULTILABEL, label(9), "marker0"),
                    createInstruction(SET, ".a*1", ".a*w"),
                    createInstruction(RETURN, ".a*wret"),
                    createInstruction(MULTILABEL, label(10), "marker1"),
                    createInstruction(SET, ".a*r", ".a*0"),
                    createInstruction(RETURN, ".a*rret"),
                    createInstruction(MULTILABEL, label(11), "marker1"),
                    createInstruction(SET, ".a*r", ".a*1"),
                    createInstruction(RETURN, ".a*rret")
            );
        }
    }

    @Nested
    class Assignments {
        // This test makes sure the assignments were all fully evaluated in one pass.
        @Test
        void handlesAssignmentSequences() {
            assertCompilesTo(
                    expectedMessages()
                            .add("Optimization passes limit (1) reached.")
                            .add("Optimization passes limited at 1."),
                    """
                            #set passes = 1;
                            i = 0;
                            print(i);
                            i += 1;
                            print(i);
                            i += 1;
                            print(i);
                            """,
                    createInstruction(PRINT, q("012"))
            );
        }

        @Test
        void handlesChainAssignment() {
            assertCompilesTo("""
                            param FROM_INDEX = 0;
                            param OFFSET_Y = 2;
                            cry = cly = FROM_INDEX == 0 ? 0 : OFFSET_Y;
                            print(cry, cly);
                            """,
                    createInstruction(SET, "FROM_INDEX", "0"),
                    createInstruction(SET, "OFFSET_Y", "2"),
                    createInstruction(SET, var(1), "OFFSET_Y"),
                    createInstruction(JUMP, var(1001), "notEqual", "FROM_INDEX", "0"),
                    createInstruction(SET, var(1), "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, var(1)),
                    createInstruction(PRINT, var(1))
            );
        }

        @Test
        void handlesSelfReference() {
            assertCompilesTo("""
                            param TICKS = 100;
                            nextTick = @tick;
                            prevTick = @tick;
                            currTick = @tick;
                            
                            nextTick = nextTick + TICKS;
                            if @tick > nextTick + TICKS then
                                prevTick = @tick;
                                nextTick = prevTick + TICKS;
                                currTick = prevTick;
                            end;
                            print(nextTick, prevTick, currTick);
                            """,
                    createInstruction(SET, "TICKS", "100"),
                    createInstruction(SET, "nextTick", "@tick"),
                    createInstruction(SET, "prevTick", "@tick"),
                    createInstruction(SET, "currTick", "@tick"),
                    createInstruction(OP, "add", "nextTick", "nextTick", "TICKS"),
                    createInstruction(OP, "add", var(1), "nextTick", "TICKS"),
                    createInstruction(JUMP, var(1000), "lessThanEq", "@tick", var(1)),
                    createInstruction(SET, "prevTick", "@tick"),
                    createInstruction(OP, "add", "nextTick", "prevTick", "TICKS"),
                    createInstruction(SET, "currTick", "prevTick"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, "nextTick"),
                    createInstruction(PRINT, "prevTick"),
                    createInstruction(PRINT, "currTick")
            );
        }
    }

    @Nested
    class CaseStatements {
        @Test
        void handlesCaseExpressions() {
            assertCompilesTo("""
                            a = case cell1[0]
                                when 0, 1, 2 then 10;
                                when 10 .. 20 then 20;
                                else 30;
                            end;
                            print(a);
                            """,
                    createInstruction(READ, var(0), "cell1", "0"),
                    createInstruction(JUMP, var(1002), "equal", var(0), "0"),
                    createInstruction(JUMP, var(1002), "equal", var(0), "1"),
                    createInstruction(JUMP, var(1001), "notEqual", var(0), "2"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(1), "10"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "lessThan", var(0), "10"),
                    createInstruction(JUMP, var(1004), "lessThanEq", var(0), "20"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(1), "20"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(1), "30"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, var(1))
            );
        }

        @Test
        void handlesCaseExpressionsWithWhenSideEffects() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'x' is not initialized."),
                    """
                            case switch1.@enabled
                                when 1, x = 2 then print(x);
                            end;
                            """,
                    createInstruction(LABEL, "__start__"),
                    createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                    createInstruction(JUMP, var(1002), "equal", var(0), "1"),
                    createInstruction(SET, "x", "2"),
                    createInstruction(JUMP, "__start__", "notEqual", var(0), "2"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, "x")
            );
        }

        @Test
        void handlesConstantCaseExpressions() {
            assertCompilesTo("""
                            n = 1;
                            sort = case n when 1 then sorter1; end;
                            print(sort);
                            """,
                    createInstruction(SET, var(0), "sorter1"),
                    createInstruction(PRINT, var(0))
            );
        }
    }

    @Nested
    class ExitPoints {
        @Test
        void handlesConditionalReturnStatements() {
            assertCompilesTo("""
                            #set optimization = advanced;
                            
                            print(foo(rand(10)));
                            def foo(d)
                                if d % 2 == 0 then
                                    return 1;
                                end;
                            end;
                            """,
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(OP, "mod", var(2), var(0), "2"),
                    createInstruction(JUMP, var(1002), "notEqual", var(2), "0"),
                    createInstruction(SET, var(1), "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, var(1))
            );
        }

        @Test
        void handlesEndInFunction() {
            assertCompilesTo("""
                            def foo(n)
                                if n > 0 then
                                    return n;
                                end;
                                end();
                            end;
                            print(foo(rand(10)));
                            """,
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(JUMP, var(1001), "greaterThan", var(0), "0"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, var(0))
            );
        }

        @Test
        void handlesReturnInLoop() {
            assertCompilesTo("""
                            #set optimization = advanced;
                            noinit var UNIT_S1;
                            if UNIT_S1 == null then UNIT_S1 = findUnit(); end;
                            inline def findUnit()
                                while true do
                                    return @unit;
                                end;
                            end;
                            """,
                    createInstruction(LABEL, "__start__"),
                    createInstruction(JUMP, "__start__", "notEqual", "UNIT_S1", "null"),
                    createInstruction(SET, var(2), "@unit"),
                    createInstruction(SET, "UNIT_S1", var(2))
            );
        }

        @Test
        void handlesReturnStatements() {
            assertCompilesTo("""
                            inline def foo(n)
                              return n;
                            end;
                            
                            print(foo(4));
                            """,
                    createInstruction(PRINT, "4")
            );
        }

        @Test
        void handlesReturnStatementsFromIf() {
            assertCompiles("""
                    #set jump-optimization = none;
                    #set unreachable-code-elimination = none;
                    param A = 10;
                    print(foo());
                    noinline def foo()
                        if A > 0 then return true; else return false; end;
                    end;
                    """
            );
        }

        @Test
        void handlesReturnStatementsWithConditionalExpressions() {
            assertCompilesTo("""
                            noinline def fib(n)
                                return n < 0 ? 0 : 1;
                            end;
                            
                            println(fib(2));
                            """,
                    createInstruction(SET, ":fn0:n", "2"),
                    createInstruction(SETADDR, ":fn0*retaddr", label(1)),
                    createInstruction(CALL, label(0), ":fn0*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":fn0*retval"),
                    createInstruction(PRINT, q("\n")),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":fn0*retval", "1"),
                    createInstruction(JUMP, label(4), "greaterThanEq", ":fn0:n", "0"),
                    createInstruction(SET, ":fn0*retval", "0"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(RETURN, ":fn0*retaddr")
            );
        }
    }

    @Nested
    class Expressions {
        @Test
        void avoidsIncompatibleLiterals() {
            assertCompilesTo("""
                            #set target = 7;
                            base = 2;
                            a = base ** 8;
                            b = base ** a;
                            print(a, b);
                            """,
                    createInstruction(OP, "pow", "b", "2", "256"),
                    createInstruction(PRINT, "256"),
                    createInstruction(PRINT, "b")
            );
        }

        @Test
        void optimizesAddAfterSub() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, sub, tmp0, a, P1),
                            createInstruction(OP, add, tmp1, tmp0, P10),    // (a - 1) + 10
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp0, P1, a),
                            createInstruction(OP, add, tmp1, tmp0, P10),    // (1 - a) + 10
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp0, a, P1),
                            createInstruction(OP, add, tmp1, P10, tmp0),    // 10 + (a - 1)
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp0, P1, a),
                            createInstruction(OP, add, tmp1, P10, tmp0),    // 10 + (1 - a)
                            createInstruction(PRINT, tmp1)
                    ),
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, add, tmp1, a, P9),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp1, P11, a),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, add, tmp1, a, P9),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp1, P11, a),
                            createInstruction(PRINT, tmp1)
                    )
            );
        }

        @Test
        void optimizesBuiltInExpressions() {
            assertCompilesTo("""
                            print(@coal == @lead);
                            print(@coal != @lead);
                            print(@coal < @lead);
                            print(@coal > @lead);
                            print(@coal <= @lead);
                            print(@coal >= @lead);
                            print(@coal === @lead);
                            """,
                    createInstruction(PRINT, q("0100110"))
            );
        }

        @Test
        void optimizesComplexExpressions() {
            assertCompilesTo("""
                            a = rand(10);
                            b = rand(10);
                            print(1 + sqrt(a * a + b * b));
                            print(2 + sqrt(a * a + b * b));
                            print(1 + sqrt(a * a + b * b));
                            """,
                    createInstruction(OP, "rand", "a", "10"),
                    createInstruction(OP, "rand", "b", "10"),
                    createInstruction(OP, "mul", var(2), "a", "a"),
                    createInstruction(OP, "mul", var(3), "b", "b"),
                    createInstruction(OP, "add", var(4), var(2), var(3)),
                    createInstruction(OP, "sqrt", var(5), var(4)),
                    createInstruction(OP, "add", var(6), "1", var(5)),
                    createInstruction(PRINT, var(6)),
                    createInstruction(OP, "add", var(11), "2", var(5)),
                    createInstruction(PRINT, var(11)),
                    createInstruction(PRINT, var(6))
            );
        }

        @Test
        void optimizesDifferentStringExpressions() {
            assertCompilesTo("""
                            a = "A";
                            b = "B";
                            print(a == b);
                            print(a != b);
                            print(a < b);
                            print(a > b);
                            print(a <= b);
                            print(a >= b);
                            print(a === b);
                            """,
                    createInstruction(PRINT, q("0100110"))
            );
        }

        @Test
        void optimizesDivAfterDiv() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, div, tmp0, a, P2),
                            createInstruction(OP, div, tmp1, tmp0, P4),    // (a / 2) / 4
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp0, P2, a),
                            createInstruction(OP, div, tmp1, tmp0, P4),    // (2 / a) / 4
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp0, a, P2),
                            createInstruction(OP, div, tmp1, P4, tmp0),    // 4 / (a / 2)
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp0, P2, a),
                            createInstruction(OP, div, tmp1, P4, tmp0),    // 4 / (2 / a)
                            createInstruction(PRINT, tmp1)
                    ),
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, div, tmp1, a, P8),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp1, P0_5, a),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp1, P8, a),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, mul, tmp1, a, P2),
                            createInstruction(PRINT, tmp1)
                    )
            );
        }

        @Test
        void optimizesDivAfterMul() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, mul, tmp0, a, P2),
                            createInstruction(OP, div, tmp1, tmp0, P4),    // (a * 2) / 4
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, mul, tmp0, P2, a),
                            createInstruction(OP, div, tmp1, tmp0, P4),    // (2 * a) / 4
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, mul, tmp0, a, P2),
                            createInstruction(OP, div, tmp1, P4, tmp0),    // 4 / (a * 2)
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, mul, tmp0, P2, a),
                            createInstruction(OP, div, tmp1, P4, tmp0),    // 4 / (2 * a)
                            createInstruction(PRINT, tmp1)
                    ),
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, div, tmp1, a, P2),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp1, a, P2),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp1, P2, a),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp1, P2, a),
                            createInstruction(PRINT, tmp1)
                    )
            );
        }

        @Test
        void optimizesEqualStringExpressions() {
            assertCompilesTo("""
                            a = "A";
                            b = "A";
                            print(a == b);
                            print(a != b);
                            print(a < b);
                            print(a > b);
                            print(a <= b);
                            print(a >= b);
                            print(a === b);
                            """,
                    createInstruction(PRINT, q("1000111"))
            );
        }

        @Test
        void optimizesExtendedSubexpressions() {
            assertCompilesTo("""
                            a = 1;
                            b = rand(10);
                            c = 2;
                            d = a + b + c;
                            print(d);
                            """,
                    createInstruction(OP, "rand", "b", "10"),
                    createInstruction(OP, "add", "d", "b", "3"),
                    createInstruction(PRINT, "d")
            );
        }

        @Test
        void optimizesMulAfterDiv() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, div, tmp0, a, P2),
                            createInstruction(OP, mul, tmp1, tmp0, P4),    // (a / 2) * 4
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp0, P2, a),
                            createInstruction(OP, mul, tmp1, tmp0, P4),    // (2 / a) * 4
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp0, a, P2),
                            createInstruction(OP, mul, tmp1, P4, tmp0),    // 4 * (a / 2)
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp0, P2, a),
                            createInstruction(OP, mul, tmp1, P4, tmp0),    // 4 * (2 / a)
                            createInstruction(PRINT, tmp1)
                    ),
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, mul, tmp1, a, P2),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp1, P8, a),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, mul, tmp1, a, P2),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, div, tmp1, P8, a),
                            createInstruction(PRINT, tmp1)
                    )
            );
        }

        @Test
        void optimizesSubAfterAdd() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, add, tmp0, a, P1),
                            createInstruction(OP, sub, tmp1, tmp0, P10),    // (a + 1) - 10
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, add, tmp0, P1, a),
                            createInstruction(OP, sub, tmp1, tmp0, P10),    // (1 + a) - 10
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, add, tmp0, a, P1),
                            createInstruction(OP, sub, tmp1, P10, tmp0),    // 10 - (a + 1)
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, add, tmp0, P1, a),
                            createInstruction(OP, sub, tmp1, P10, tmp0),    // 10 - (1 + a)
                            createInstruction(PRINT, tmp1)
                    ),
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, sub, tmp1, a, P9),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp1, a, P9),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp1, P9, a),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp1, P9, a),
                            createInstruction(PRINT, tmp1)
                    )
            );
        }

        @Test
        void optimizesSubAfterAdd0() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, add, tmp0, a, P1),
                            createInstruction(OP, sub, tmp1, tmp0, P10),    // (a + 1) - 10
                            createInstruction(PRINT, tmp1)
                    ),
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, sub, tmp1, a, P9),
                            createInstruction(PRINT, tmp1)
                    )
            );
        }

        @Test
        void optimizesSubAfterSub() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, sub, tmp0, a, P1),
                            createInstruction(OP, sub, tmp1, tmp0, P10),    // (a - 1) - 10
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp0, P1, a),
                            createInstruction(OP, sub, tmp1, tmp0, P10),    // (1 - a) - 10
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp0, a, P1),
                            createInstruction(OP, sub, tmp1, P10, tmp0),    // 10 - (a - 1)
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp0, P1, a),
                            createInstruction(OP, sub, tmp1, P10, tmp0),    // 10 - (1 - a)
                            createInstruction(PRINT, tmp1)
                    ),
                    List.of(
                            createInstruction(OP, rand, a, P10),
                            createInstruction(OP, sub, tmp1, a, P11),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp1, N9, a),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, sub, tmp1, P11, a),
                            createInstruction(PRINT, tmp1),
                            createInstruction(OP, add, tmp1, a, P9),
                            createInstruction(PRINT, tmp1)
                    )
            );
        }

        @Test
        void optimizesSubexpressions() {
            assertCompilesTo("""
                            a = rand(10);
                            b = a + 1;
                            c = 1 + (a + 1);
                            d = 2 + (1 + a);
                            print(a, b, c, d);
                            """,
                    createInstruction(OP, "rand", "a", "10"),
                    createInstruction(OP, "add", "b", "a", "1"),
                    createInstruction(OP, "add", "c", "a", "2"),
                    createInstruction(OP, "add", "d", "a", "3"),
                    createInstruction(PRINT, "a"),
                    createInstruction(PRINT, "b"),
                    createInstruction(PRINT, "c"),
                    createInstruction(PRINT, "d")
            );
        }
    }

    @Nested
    class ExternalMemory {
        @Test
        void optimizesMemoryAccess() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'i' is not initialized."),
                    """
                            if cell1[i] > cell1[i + 1] then
                                a = cell1[i];
                                cell1[i] = cell1[i + 1];
                                cell1[i + 1] = a;
                            end;
                            """,
                    createInstruction(LABEL, "__start__"),
                    createInstruction(OP, "add", var(3), "i", "1"),
                    createInstruction(READ, var(1), "cell1", "i"),
                    createInstruction(READ, var(4), "cell1", var(3)),
                    createInstruction(JUMP, "__start__", "lessThanEq", var(1), var(4)),
                    createInstruction(READ, "a", "cell1", "i"),
                    createInstruction(READ, var(13), "cell1", var(3)),
                    createInstruction(WRITE, var(13), "cell1", "i"),
                    createInstruction(WRITE, "a", "cell1", var(3))
            );
        }

        @Test
        void optimizesMemoryWriteAccess() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            $A = rand(10);
                            print($A);
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "cell1", "null"),
                    createInstruction(OP, "rand", var(1), "10"),
                    createInstruction(WRITE, var(1), "cell1", "0"),
                    createInstruction(READ, var(0), "cell1", "0"),
                    createInstruction(PRINT, var(0))
            );
        }
    }

    @Nested
    class FunctionCalls {
        @Test
        void compilesRecursiveFibonacci() {
            assertCompilesTo("""
                            allocate stack in bank1[0...512];
                            def fib(n)
                                n < 2 ? n : fib(n - 1) + fib(n - 2);
                            end;
                            print(fib(10));
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1001), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(SET, ":fn0:n", "10"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1002), ":fn0*retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, ":fn0*retval"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, ":fn0*retval", ":fn0:n"),
                    createInstruction(JUMP, var(1005), "lessThan", ":fn0:n", "2"),
                    createInstruction(PUSH, "bank1", ":fn0:n"),
                    createInstruction(OP, "sub", ":fn0:n", ":fn0:n", "1"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1006), ":fn0*retval"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(POP, "bank1", ":fn0:n"),
                    createInstruction(SET, var(4), ":fn0*retval"),
                    createInstruction(PUSH, "bank1", var(4)),
                    createInstruction(OP, "sub", ":fn0:n", ":fn0:n", "2"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1007), ":fn0*retval"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(POP, "bank1", var(4)),
                    createInstruction(OP, "add", ":fn0*retval", var(4), ":fn0*retval"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(RETURNREC, "bank1")
            );
        }

        @Test
        public void handlesConstantFunctionReturn() {
            assertCompilesTo("""
                            def foo(n)
                                print(n);
                                5;
                            end;
                            print(foo(2));
                            print(foo(3));
                            """,
                    createInstruction(SET, ":fn0:n", "2"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1001)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, ":fn0*retval"),
                    createInstruction(SET, ":fn0:n", "3"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1002)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, ":fn0*retval"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, ":fn0:n"),
                    createInstruction(SET, ":fn0*retval", "5"),
                    createInstruction(RETURN, ":fn0*retaddr")
            );
        }

        @Test
        void handlesFunctionArgumentSetup() {
            assertCompilesTo("""
                            def getBit(bitIndex)
                              bitIndex * 2;
                            end;
                            
                            for n in 1 .. 1000 do
                                print(getBit(n \\ 2));
                            end;
                            getBit(0);
                            """,
                    createInstruction(SET, ":n", "1"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(OP, "idiv", ":fn0:bitIndex", ":n", "2"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1004)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(PRINT, ":fn0*retval"),
                    createInstruction(OP, "add", ":n", ":n", "1"),
                    createInstruction(JUMP, var(1007), "lessThanEq", ":n", "1000"),
                    createInstruction(SET, ":fn0:bitIndex", "0"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1005)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "mul", ":fn0*retval", ":fn0:bitIndex", "2"),
                    createInstruction(RETURN, ":fn0*retaddr")
            );
        }

        @Test
        public void handlesInputOutputRecursiveFunctionParameters() {
            // When passing/retrieving arguments into/out of recursive calls, the compiler generates temporary variables
            // in situations where arguments in recursive calls are swapped. This test ensures all the unnecessary
            // variables are properly eliminated.
            assertCompilesTo("""
                            allocate stack in bank1;
                            def foo(n, in out a, in out b)
                                if n == 0 then
                                    a = 5;
                                    b = 10;
                                else
                                    foo(n - 1, out b, out a);
                                end;
                            end;
                            foo(10, in 1, in 2);
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1001), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(SET, ":fn0:n", "10"),
                    createInstruction(SET, ":fn0:a", "1"),
                    createInstruction(SET, ":fn0:b", "2"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1002), ":fn0*retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1004), "notEqual", ":fn0:n", "0"),
                    createInstruction(SET, ":fn0:a", "5"),
                    createInstruction(SET, ":fn0:b", "10"),
                    createInstruction(RETURNREC, "bank1"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(5), ":fn0:a"),
                    createInstruction(OP, "sub", ":fn0:n", ":fn0:n", "1"),
                    createInstruction(SET, ":fn0:a", ":fn0:b"),
                    createInstruction(SET, ":fn0:b", var(5)),
                    createInstruction(CALLREC, "bank1", var(1000), var(1006), ":fn0*retval"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(SET, var(7), ":fn0:b"),
                    createInstruction(SET, ":fn0:b", ":fn0:a"),
                    createInstruction(SET, ":fn0:a", var(7)),
                    createInstruction(RETURNREC, "bank1")
            );
        }

        @Test
        public void handlesInputRecursiveFunctionParameters() {
            // When passing arguments into recursive calls, the compiler generates temporary variables
            // in situations where arguments in recursive calls are swapped. This test ensures all the unnecessary
            // variables are properly eliminated.
            assertCompilesTo("""
                            allocate stack in bank1;
                            def foo(n, a, b, c, d)
                                if n == 0 then
                                    print(a, b, c, d);
                                else
                                    foo(n - 1, b, c, d, a);
                                end;
                            end;
                            foo(10, 1, 2, 3, 4);
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1001), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(SET, ":fn0:n", "10"),
                    createInstruction(SET, ":fn0:a", "1"),
                    createInstruction(SET, ":fn0:b", "2"),
                    createInstruction(SET, ":fn0:c", "3"),
                    createInstruction(SET, ":fn0:d", "4"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1002), ":fn0*retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1004), "notEqual", ":fn0:n", "0"),
                    createInstruction(PRINT, ":fn0:a"),
                    createInstruction(PRINT, ":fn0:b"),
                    createInstruction(PRINT, ":fn0:c"),
                    createInstruction(PRINT, ":fn0:d"),
                    createInstruction(RETURNREC, "bank1"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(7), ":fn0:a"),
                    createInstruction(OP, "sub", ":fn0:n", ":fn0:n", "1"),
                    createInstruction(SET, ":fn0:a", ":fn0:b"),
                    createInstruction(SET, ":fn0:b", ":fn0:c"),
                    createInstruction(SET, ":fn0:c", ":fn0:d"),
                    createInstruction(SET, ":fn0:d", var(7)),
                    createInstruction(CALLREC, "bank1", var(1000), var(1006), ":fn0*retval"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(RETURNREC, "bank1")
            );
        }

        @Test
        void optimizesFunctionArguments() {
            assertCompilesTo("""
                            def foo(n)
                                print(n * 2);
                            end;
                            foo(2);
                            foo(2);
                            """,
                    createInstruction(SET, ":fn0:n", "2"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1001)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1002)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "mul", var(2), ":fn0:n", "2"),
                    createInstruction(PRINT, var(2)),
                    createInstruction(RETURN, ":fn0*retaddr")
            );
        }

        @Test
        public void preservesGlobalVariablesWithFunctionCalls() {
            assertCompilesTo("""
                            inline def bar(n)
                                foo(n);
                                print(n);
                            end;
                            def foo(n)
                                print(n);
                            end;
                            X = rand(1000);
                            Y = rand(1000);
                            foo(X);
                            bar(Y);
                            """,
                    createInstruction(OP, "rand", ".X", "1000"),
                    createInstruction(OP, "rand", ".Y", "1000"),
                    createInstruction(SET, ":fn0:n", ".X"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1001)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, ":fn0:n", ".Y"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1003)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(PRINT, ".Y"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, ":fn0:n"),
                    createInstruction(RETURN, ":fn0*retaddr")
            );
        }

        @Test
        public void preservesVariableStateAcrossPushAndPop() {
            // Explanation of the test:
            // The recursive call foo(m, n - 1) modifies __fn0_n (it is set to n - 1 when passing new value to the recursive call)
            // Data Flow analysis of push/pop should determine the value of n remains unchanged after the call
            // Because of this, it subsequently determines the __tmp1 variable in loop condition can be replaced by __fn0_n
            assertCompilesTo("""
                            allocate stack in bank1[0...512];
                            def foo(n)
                                if n > 0 then
                                    foo(n - 1);
                                end;
                            end;
                            print(foo(10));
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1001), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(SET, ":fn0:n", "10"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1002), ":fn0*retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, ":fn0*retval"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, ":fn0*retval", "null"),
                    createInstruction(JUMP, var(1005), "lessThanEq", ":fn0:n", "0"),
                    createInstruction(OP, "sub", ":fn0:n", ":fn0:n", "1"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1006), ":fn0*retval"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(RETURNREC, "bank1")
            );
        }

        //@Test
        public void preservesVariableStateAcrossPushAndPopInLoop() {
            // Explanation of the test:
            // The recursive call foo(m, n - 1) modifies __fn0_n (it is set to n - 1 when passing new value to the recursive call)
            // Data Flow analysis of push/pop should determine the value of n remains unchanged after the call
            // Because of this, it subsequently determines the __tmp1 variable in loop condition can be replaced by __fn0_n
            //
            // TODO Data Flow analysis currently doesn't understand push/pop. Needs to implement a functionality to save
            //      variable state on push and restore it on pop. Might be difficult, as invalidating a variable
            //      invalidates the entire subtree.
            assertCompilesTo("""
                            allocate stack in bank1[0...512];
                            def foo(n)
                                for i in 1 .. n do
                                    print(n);
                                    foo(n - 1);
                                end;
                            end;
                            foo(10);
                            """,
                    createInstruction(SET, "__sp", "0"),
                    createInstruction(SET, "__fn0_n", "10"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn0_i", "1"),
                    createInstruction(JUMP, var(1005), "greaterThan", "1", "__fn0_n"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(PRINT, "__fn0_n"),
                    createInstruction(PUSH, "bank1", "__fn0_n"),
                    createInstruction(PUSH, "bank1", "__fn0_i"),
                    createInstruction(OP, "sub", "__fn0_n", "__fn0_n", "1"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1006), "__fn0retval"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(POP, "bank1", "__fn0_i"),
                    createInstruction(POP, "bank1", "__fn0_n"),
                    createInstruction(OP, "add", "__fn0_i", "__fn0_i", "1"),
                    createInstruction(JUMP, var(1007), "lessThanEq", "__fn0_i", "__fn0_n"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(RETURNREC, "bank1")
            );
        }
    }

    @Nested
    class Globals {
        @Test
        void correctlyProcessesFunctions() {
            assertGeneratesMessages(
                    expectedMessages(),
                    """
                            A = 10;
                            for i = 0; i < A; i += 1 do
                                foo(2);
                            end;
                            
                            noinline def foo(n)
                                println(n);
                                bar(n);
                            end;
                            
                            noinline def bar(x)
                                A = x;
                            end;
                            """
            );
        }

        @Test
        void leavesGlobalParameters() {
            assertCompilesTo("""
                            param a = 1;
                            b = 2;
                            print(a * b);
                            """,
                    createInstruction(SET, "a", "1"),
                    createInstruction(OP, "mul", var(0), "a", "2"),
                    createInstruction(PRINT, var(0))
            );
        }
    }

    @Nested
    class IfStatements {
        @Test
        void IdentifiesUninitializedVariables() {
            assertCompilesTo(expectedMessages()
                            .add("Variable 'a' is not initialized.")
                            .add("Variable 'b' is not initialized."),
                    """
                            if switch1.@enabled then
                                a = 1;
                            else
                                b = 1;
                            end;
                            print(a, b);
                            """,
                    createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                    createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                    createInstruction(SET, "a", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "b", "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, "a"),
                    createInstruction(PRINT, "b")
            );
        }

        @Test
        void compilesProperComparison2() {
            assertCompilesTo("""
                            inline def eval(b)
                                b ? "T" : "F";
                            end;
                            
                            inline def compare(a, b)
                                print(eval(a > b), eval(a < b));
                            end;
                            
                            param A = 0;
                            compare(A, A);
                            """,
                    createInstruction(PRINT, q("FF"))
            );
        }

        @Test
        void evaluatesConstantIfsFully() {
            assertCompilesTo("""
                            i = 1;
                            print(i % 2 == 0 ? 1 : 2);
                            """,
                    createInstruction(PRINT, "2")
            );
        }

        @Test
        void evaluatesConstantIfsInLoopFully() {
            assertCompilesTo("""
                            def getBit(bitIndex)
                                bitIndex % 2;
                            end;
                            
                            for i in 1 ... 2 do
                                print(getBit(i) ? 1 : 0);
                            end;
                            """,
                    createInstruction(PRINT, "1")
            );
        }

        @Test
        void handlesOptimizedIfStatements() {
            assertCompilesTo("""
                            a = 10;
                            print(a > 5 ? "High" : "Low");
                            """,
                    createInstruction(PRINT, q("High"))
            );
        }

        @Test
        void handlesSingleBranchIfStatements() {
            assertCompilesTo("""
                            a = 1;
                            if switch1.@enabled then
                                a = 2;
                            end;
                            print(a);
                            """,
                    createInstruction(SET, "a", "1"),
                    createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                    createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                    createInstruction(SET, "a", "2"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, "a")
            );
        }

        @Test
        void optimizesAssignmentsInConditions() {
            assertCompilesTo("""
                            if result = !rand(10) then
                                print("A");
                            end;
                            print(result);
                            """,
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(OP, "equal", "result", var(0), "false"),
                    createInstruction(JUMP, var(1000), "equal", "result", "false"),
                    createInstruction(PRINT, q("A")),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, "result")
            );
        }

        @Test
        void optimizesBranchedExpressions() {
            assertCompilesTo("""
                            a = rand(10);
                            b = a + 1;
                            if switch1.@enabled then
                                c = 2 * (a + 1);
                                print(c);
                            else
                                c = 10;
                                print(c);
                            end;
                            print(b, c);
                            """,
                    createInstruction(OP, "rand", "a", "10"),
                    createInstruction(OP, "add", "b", "a", "1"),
                    createInstruction(SENSOR, var(2), "switch1", "@enabled"),
                    createInstruction(JUMP, var(1000), "equal", var(2), "false"),
                    createInstruction(OP, "mul", "c", "2", "b"),
                    createInstruction(PRINT, "c"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "c", "10"),
                    createInstruction(PRINT, "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, "b"),
                    createInstruction(PRINT, "c")
            );
        }

        @Test
        void processesIfStatements() {
            assertCompilesTo("""
                            a = 0;
                            b = 0;
                            if switch1.@enabled then
                                a = 1;
                                b = 2;
                            else
                                a = 2;
                                b = 3;
                                print(a);
                            end;
                            print(b);
                            """,
                    createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                    createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                    createInstruction(SET, "b", "2"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "b", "3"),
                    createInstruction(PRINT, "2"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, "b")
            );
        }

        @Test
        void removesUnneededAssignmentsInConditions() {
            assertCompilesTo("""
                            a = 0;
                            if switch1.@enabled then
                                a = 1;
                                b = a;
                            else
                                a = 2;
                                b = 1;
                            end;
                            print(a, b);
                            """,
                    createInstruction(SET, "a", "2"),
                    createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                    createInstruction(JUMP, var(1001), "equal", var(0), "false"),
                    createInstruction(SET, "a", "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, "a"),
                    createInstruction(PRINT, "1")
            );
        }
    }

    @Nested
    class InlineFunctions {
        @Test
        public void handlesArgumentsInExpressions() {
            assertCompilesTo("""
                            inline def bar(n)
                                print(n + 1);
                            end;
                            bar(5);
                            """,
                    createInstruction(PRINT, "6")
            );
        }

        @Test
        public void handlesBlockNames() {
            assertCompilesTo("""
                            inline def bar(n)
                                print(n);
                            end;
                            bar(switch1);
                            """,
                    createInstruction(PRINT, "switch1")
            );
        }

        @Test
        public void handlesChainedVariables() {
            assertCompilesTo("""
                            inline def bar(n)
                                a = n;
                                print(a);
                            end;
                            bar(5);
                            """,
                    createInstruction(PRINT, "5")
            );
        }

        @Test
        void handlesComplexInlineFunctionArgumentSetup() {
            assertCompilesTo("""
                            printDomeStatus(@silicon, "\\n[green]Silicon[] status:\\n");
                            inline def printDomeStatus(item, text)
                                print(text);
                                level = dome1.sensor(item);
                                print($"  dome:  [green]$[]\\n", level);
                            end;
                            """,
                    createInstruction(PRINT, q("\n[green]Silicon[] status:\n  dome:  [green]{0}[]\n")),
                    createInstruction(SENSOR, ":fn0:level", "dome1", "@silicon"),
                    createInstruction(FORMAT, ":fn0:level")
            );
        }

        @Test
        public void handlesInlineFunctionsGlobalVariables() {
            assertCompilesTo("""
                            inline def bar(n)
                                print(n);
                            end;
                            X = rand(1000);
                            bar(X);
                            """,
                    createInstruction(OP, "rand", "X", "1000"),
                    createInstruction(PRINT, "X")
            );
        }

        @Test
        public void handlesNestedParameters() {
            assertCompilesTo("""
                            inline def foo(n)
                                print(n);
                            end;
                            inline def bar(n)
                                foo(n);
                            end;
                            bar(5);
                            """,
                    createInstruction(PRINT, "5")
            );
        }

        @Test
        public void handlesSimpleParameters() {
            assertCompilesTo("""
                            inline def bar(n)
                                print(n);
                            end;
                            bar(5);
                            """,
                    createInstruction(PRINT, "5")
            );
        }

        @Test
        void passesParameterToFunctionRegressionTest() {
            assertCompilesTo("""
                            #set optimization = advanced;
                            inline def d(n)
                                n;
                            end;
                            print(1 < d(2));
                            printflush(message1);
                            """,
                    createInstruction(PRINT, "true"),
                    createInstruction(PRINTFLUSH, "message1")
            );
        }

        @Test
        public void preservesModifiedVariables() {
            assertCompilesTo("""
                            inline def bar(n)
                                while n < 1000 do
                                    n += 1;
                                    print(n);
                                end;
                            end;
                            bar(0);
                            """,
                    createInstruction(SET, "__fn0_n", "0"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "__fn0_n", "__fn0_n", "1"),
                    createInstruction(PRINT, "__fn0_n"),
                    createInstruction(JUMP, var(1005), "lessThan", "__fn0_n", "1000")
            );
        }

        @Test
        public void preservesVolatileVariables() {
            assertCompilesTo("""
                            inline def bar(n)
                                print(n);
                            end;
                            bar(@time);
                            """,
                    createInstruction(SET, "__fn0_n", "@time"),
                    createInstruction(PRINT, "__fn0_n")
            );
        }
    }

    @Nested
    class Loops {
        @Test
        void handlesContinueInLoops() {
            assertCompilesTo("""
                            for i in 1 ... 1000 do
                                if i == 5 then
                                    continue;
                                else
                                    str = i;
                                end;
                                print(str);
                            end;
                            """,
                    createInstruction(SET, "i", "1"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(JUMP, var(1001), "equal", "i", "5"),
                    createInstruction(PRINT, "i"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1005), "lessThan", "i", "1000")
            );
        }

        @Test
        void handlesDeadEnds() {
            assertCompilesTo("""
                            for i in 1 ... 1000 do
                                print(i);
                                if i == 5 then
                                    continue;
                                else
                                    break;
                                end;
                                print(i + 1);
                            end;
                            """,
                    createInstruction(LABEL, "__start__"),
                    createInstruction(SET, "i", "1"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(PRINT, "i"),
                    createInstruction(JUMP, "__start__", "notEqual", "i", "5"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1005), "lessThan", "i", "1000")
            );
        }

        @Test
        void handlesDescendingLoops() {
            assertCompilesTo("""
                            const LENGTH = 1500;
                            for row = LENGTH - 2; row >= 0; row -= 1 do
                                for col = row + 1; col >= 0; col -= 1 do
                                    print(col);
                                end;
                            end;
                            """,
                    createInstruction(SET, "row", "1498"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(OP, "add", "col", "row", "1"),
                    createInstruction(JUMP, var(1005), "lessThan", "col", "0"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(PRINT, "col"),
                    createInstruction(OP, "sub", "col", "col", "1"),
                    createInstruction(JUMP, var(1007), "greaterThanEq", "col", "0"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "sub", "row", "row", "1"),
                    createInstruction(JUMP, var(1006), "greaterThanEq", "row", "0")
            );
        }

        @Test
        void handlesDoWhileLoops() {
            assertCompilesTo("""
                            i = 0;
                            do
                                min = cell1[i];
                                i += 1;
                            while i < 1000;
                            print(min);
                            """,
                    createInstruction(SET, "i", "0"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(READ, "min", "cell1", "i"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1000), "lessThan", "i", "1000"),
                    createInstruction(PRINT, "min")
            );
        }

        @Test
        void handlesForEachLoops() {
            assertCompilesTo("""
                            #set loop-unrolling = none;
                            for i in 1, 2, 3 do
                                print(i);
                            end;
                            """,
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(SET, ":i", "1"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003), "marker0"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(SET, ":i", "2"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SETADDR, var(0), var(1005)),
                    createInstruction(SET, ":i", "3"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, ":i"),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1005), "marker0")
            );
        }

        @Test
        void handlesForEachLoopsWithModifications() {
            assertCompilesTo("""
                            #set loop-unrolling = none;
                            for out i, out j in a, b, c, d do
                                i = rand(10);
                                j = 2 * i;
                            end;
                            
                            print(a, b, c, d);
                            """,
                    createInstruction(SETADDR, var(0), var(1003)),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(MULTILABEL, var(1003), "marker0"),
                    createInstruction(SET, ":a", ":i"),
                    createInstruction(SET, ":b", ":j"),
                    createInstruction(SETADDR, var(0), var(1004)),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "rand", ":i", "10"),
                    createInstruction(OP, "mul", ":j", "2", ":i"),
                    createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                    createInstruction(MULTILABEL, var(1004), "marker0"),
                    createInstruction(SET, ":c", ":i"),
                    createInstruction(SET, ":d", ":j"),
                    createInstruction(PRINT, ":a"),
                    createInstruction(PRINT, ":b"),
                    createInstruction(PRINT, ":c"),
                    createInstruction(PRINT, ":d")
            );
        }

        @Test
        void handlesIfsInLoops() {
            assertCompilesTo("""
                            a = 1;
                            b = rand(10);
                            
                            while true do
                                c = a ? 2 * b : 1;
                                b = b + c;
                            end;
                            """,
                    createInstruction(OP, "rand", "b", "10"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "mul", var(1), "2", "b"),
                    createInstruction(OP, "add", "b", "b", var(1)),
                    createInstruction(JUMP, var(1000), "always")
            );
        }

        @Test
        void handlesNestedLoops() {
            assertCompilesTo("""
                            for i = 0; i < 1000; i += 1 do
                                for j = 0; j < 1000; j += 1 do
                                    print(i, j);
                                end;
                            end;
                            """,
                    createInstruction(SET, "i", "0"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(SET, "j", "0"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(PRINT, "i"),
                    createInstruction(PRINT, "j"),
                    createInstruction(OP, "add", "j", "j", "1"),
                    createInstruction(JUMP, var(1007), "lessThan", "j", "1000"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1006), "lessThan", "i", "1000")
            );
        }

        @Test
        void handlesNonConstantExpressions() {
            assertCompilesTo("""
                            x = 0;
                            while true do
                                print(x + 1);
                                x = rand(10);
                                print(x + 1);
                            end;
                            """,
                    createInstruction(SET, "x", "0"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "add", var(0), "x", "1"),
                    createInstruction(PRINT, var(0)),
                    createInstruction(OP, "rand", "x", "10"),
                    createInstruction(OP, "add", var(2), "x", "1"),
                    createInstruction(PRINT, var(2)),
                    createInstruction(JUMP, var(1000), "always")
            );
        }

        @Test
        void handlesRangedForLoops() {
            // min is uninitialized because we do not know the loop body will execute
            assertCompilesTo(expectedMessages()
                            .add("Variable 'SIZE' is not initialized.")
                            .add("Variable 'min' is not initialized."),
                    """
                            for i in 0 ... SIZE - 1 do
                                min = cell1[i];
                            end;
                            print(min);
                            """,
                    createInstruction(OP, "sub", var(1), "SIZE", "1"),
                    createInstruction(SET, "i", "0"),
                    createInstruction(JUMP, var(1002), "greaterThanEq", "0", var(1)),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(READ, "min", "cell1", "i"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1003), "lessThan", "i", var(1)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, "min")
            );
        }

        @Test
        void handlesWhileLoops() {
            assertCompilesTo("""
                            i = 0;
                            while i < 10000 do
                                i = i + 1;
                            end;
                            print(i);
                            """,
                    createInstruction(SET, "i", "0"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1003), "lessThan", "i", "10000"),
                    createInstruction(PRINT, "i")
            );
        }

        @Test
        void optimizesDataFlowAfterLoopOptimization() {
            assertCompilesTo("""
                            index = rand(10);
                            parent = 0;
                            while (child = parent * 2 + 1) <= index do
                                parent = child;
                            end;
                            print(parent);
                            """,
                    createInstruction(OP, "rand", "index", "10"),
                    createInstruction(SET, "parent", "0"),
                    createInstruction(SET, "child", "1"),
                    createInstruction(JUMP, var(1002), "greaterThan", "1", "index"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, "parent", "child"),
                    createInstruction(OP, "mul", var(1), "child", "2"),
                    createInstruction(OP, "add", "child", var(1), "1"),
                    createInstruction(JUMP, var(1003), "lessThanEq", "child", "index"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, "parent")
            );
        }

        @Test
        void properlyInvalidatesExpressions() {
            assertCompilesTo("""
                            a = rand(10) > 5;
                            b = a > 5;
                            c = b or a < 5;
                            d = a;
                            
                            while true do
                                if c then
                                    d += 1;
                                    c = b or d < 5;
                                end;
                            end;
                            """,
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(OP, "greaterThan", "a", var(0), "5"),
                    createInstruction(OP, "greaterThan", "b", "a", "5"),
                    createInstruction(OP, "lessThan", var(3), "a", "5"),
                    createInstruction(OP, "or", "c", "b", var(3)),
                    createInstruction(SET, "d", "a"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "c", "false"),
                    createInstruction(OP, "add", "d", "d", "1"),
                    createInstruction(OP, "lessThan", var(4), "d", "5"),
                    createInstruction(OP, "or", "c", "b", var(4)),
                    createInstruction(JUMP, var(1000), "always")


            );
        }
    }

    @Nested
    class Main {
        @Test
        void keepsLatestAssignment() {
            assertCompilesTo("""
                            index = 0;
                            b = rand(10);
                            index = 0;
                            while index < 1000 do
                                print(b);
                                index = index + 1;
                            end;
                            """,
                    createInstruction(OP, "rand", "b", "10"),
                    createInstruction(SET, "index", "0"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(PRINT, "b"),
                    createInstruction(OP, "add", "index", "index", "1"),
                    createInstruction(JUMP, var(1003), "lessThan", "index", "1000")
            );
        }

        @Test
        void leavesUninitializedVariables() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'flag' is not initialized."),
                    """
                            print(flag);
                            flag = 1;
                            """,
                    createInstruction(PRINT, "flag"),
                    createInstruction(SET, "flag", "1")        // This instruction is kept
            );
        }

        @Test
        void removesUnneededVariables() {
            assertCompilesTo("""
                            i = 5;
                            j = 10;
                            j = 15;
                            i = j + 2;
                            print(i, j);
                            """,
                    createInstruction(PRINT, q("1715"))
            );
        }
    }

    @Nested
    class Volatile {

        @Test
        void recognizesNonvolatileSensorWithGetLink() {
            assertCompilesTo(
                    """
                            block = getlink(0);
                            a = block.@type;
                            b = block.@type;
                            print(a, b);
                            """,
                    createInstruction(GETLINK, "block", "0"),
                    createInstruction(SENSOR, "a", "block", "@type"),
                    createInstruction(PRINT, "a"),
                    createInstruction(PRINT, "a")
            );
        }

        @Test
        void recognizesNonvolatileSensorWithVariables() {
            // `message1` is a block and is considered volatile when handling `sensor` instruction.
            // By assigning it to a variable, volatility is removed and superfluous calls are eliminated
            assertCompilesTo(
                    """
                            x = message1;
                            a = x.@type;
                            b = x.@type;
                            print(a, b);
                            """,
                    createInstruction(SENSOR, "a", "message1", "@type"),
                    createInstruction(PRINT, "a"),
                    createInstruction(PRINT, "a")
            );
        }

        @Test
        void recognizesSensorAsVolatileWithLinkedBlocks() {
            // Linked blocks are considered immutable: no defensive copies are made. However, since the block can get
            // linked to a different instance, their properties might change and
            assertCompilesTo(
                    """
                            a = message1.@type;
                            b = message1.@type;
                            print(a, b);
                            """,
                    createInstruction(SENSOR, ":a", "message1", "@type"),
                    createInstruction(SENSOR, ":b", "message1", "@type"),
                    createInstruction(PRINT, ":a"),
                    createInstruction(PRINT, ":b")
            );
        }

        @Test
        void recognizesVolatileSensorWithVariables() {
            // `@dead` is a volatile property and needs to be reread every time.
            assertCompilesTo(
                    """
                            x = message1;
                            a = x.@dead;
                            b = x.@dead;
                            print(a, b);
                            """,
                    createInstruction(SENSOR, ":a", "message1", "@dead"),
                    createInstruction(SENSOR, ":b", "message1", "@dead"),
                    createInstruction(PRINT, ":a"),
                    createInstruction(PRINT, ":b")
            );
        }

        @Test
        void respectsNonEvaluableBuiltIns() {
            assertCompilesTo(
                    """
                            print(@mapw - 10);
                            """,
                    createInstruction(OP, "sub", var(0), "@mapw", "10"),
                    createInstruction(PRINT, var(0))
            );
        }

        @Test
        void respectsVolatileVariables() {
            assertCompilesTo("""
                            volatile var A = 10;
                            sync(A);
                            before = A;
                            wait(1000);
                            after = A;
                            print(after - before);
                            """,
                    createInstruction(SET, "A", "10"),
                    createInstruction(SYNC, "A"),
                    createInstruction(SET, "before", "A"),
                    createInstruction(WAIT, "1000"),
                    createInstruction(SET, "after", "A"),
                    createInstruction(OP, "sub", var(2), "after", "before"),
                    createInstruction(PRINT, var(2))
            );
        }
    }

    @Nested
    class Warnings {
        @Test
        void generatesNoWarningsAboutFunctionParameters() {
            assertCompiles("""
                    def foo(n)
                    print(n);
                    end;
                    foo(0);
                    foo(1);
                    """
            );
        }

        @Test
        void generatesUninitializedWarning() {
            assertGeneratesMessage(
                    "Variable 'i' is not initialized.",
                    """
                            j = i;
                            i = 10;      // Avoid the warning generated by dead code eliminator
                            print(i, j);
                            """
            );
        }

        @Test
        void generatesWarningsAboutLocalVariables() {
            assertGeneratesMessage(
                    "Variable 'foo.n' is not initialized.",
                    """
                             def foo()
                                 print(n);
                             end;
                             foo();
                             foo();
                            """
            );
        }

        @Test
        void producesNoWarningsOnBasicLevel() {
            assertCompiles("""
                    #set optimization = basic;
                    for n in 0 ... 1000 do
                        j = switch1.@enabled;
                        print(j);
                    end;
                    """
            );
        }

        @Test
        void producesNoWarningsOnBasicLoop() {
            assertCompiles("""
                    #set optimization = basic;
                    while true do
                        i = switch1.@enabled;
                    end;
                    print(i);
                    """
            );
        }

        @Test
        void producesNoWarningsOnBasicLoop2() {
            assertCompiles("""
                    #set optimization = basic;
                    x = 159;
                    while x > 10 do
                        x1 = x - 5;
                        x = x1;
                    end;
                    """
            );
        }

        @Test
        void recognizesIfStatementInitialization() {
            assertCompiles("""
                    if switch1.@enabled then
                        a = rand(10);
                        b = rand(10);
                    else
                        a = -rand(10);
                        b = -rand(10);
                    end;
                    print(a, b);
                    """
            );
        }

        @Test
        void recognizesPartialIfStatementInitialization() {
            assertGeneratesMessage(
                    "Variable 'b' is not initialized.",
                    """
                            if switch1.@enabled then
                                a = 1;
                                b = a;
                            else
                                a = 2;
                                print(a);
                            end;
                            print(b);
                            """
            );
        }
    }
}