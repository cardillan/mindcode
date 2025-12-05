package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class ExpressionOptimizerTest extends AbstractOptimizerTest<ExpressionOptimizer> {

    @Override
    protected Class<ExpressionOptimizer> getTestedClass() {
        return ExpressionOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(Optimization.EXPRESSION_OPTIMIZATION);
    }

    @Nested
    class LookupInstruction {
        @Test
        void optimizesLookup() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(LOOKUP, item, tmp0, P1)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, lead)
                    )
            );
        }
    }

    @Nested
    class OpConstantOperand {

        @Test
        void optimizesMulBy0() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, Operation.MUL, tmp0, a, P0),
                            createInstruction(OP, Operation.MUL, tmp1, P0, b)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, P0),
                            createInstruction(SET, tmp1, P0)
                    )
            );
        }

        @Test
        void optimizesMulBy1() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, Operation.MUL, tmp0, a, P1),
                            createInstruction(OP, Operation.MUL, tmp1, P1, b)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, a),
                            createInstruction(SET, tmp1, b)
                    )
            );
        }

        @Test
        void optimizesDivBy1() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, Operation.DIV, tmp0, a, P1),
                            createInstruction(OP, Operation.DIV, tmp1, P1, b)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, a),
                            createInstruction(OP, Operation.DIV, tmp1, P1, b)
                    )
            );
        }

        @Test
        void optimizesAddOf0() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, Operation.ADD, tmp0, a, P0),
                            createInstruction(OP, Operation.ADD, tmp1, P0, b)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, a),
                            createInstruction(SET, tmp1, b)
                    )
            );
        }

        @Test
        void optimizesSubOf0() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, Operation.SUB, tmp0, a, P0),
                            createInstruction(OP, Operation.SUB, tmp1, P0, b)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, a),
                            createInstruction(OP, Operation.SUB, tmp1, P0, b)
                    )
            );
        }
    }

    @Nested
    class OpIdenticalOperands {

        @Test
        void optimizesEqualitiesOnIdentity() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, Operation.EQUAL, tmp0, a, a),
                            createInstruction(OP, Operation.LESS_THAN_EQ, tmp1, a, a),
                            createInstruction(OP, Operation.GREATER_THAN_EQ, tmp2, a, a),
                            createInstruction(OP, Operation.STRICT_EQUAL, tmp3, a, a)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, LogicBoolean.TRUE),
                            createInstruction(SET, tmp1, LogicBoolean.TRUE),
                            createInstruction(SET, tmp2, LogicBoolean.TRUE),
                            createInstruction(SET, tmp3, LogicBoolean.TRUE)
                    )
            );
        }

        @Test
        void optimizesInequalitiesOnIdentity() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, Operation.NOT_EQUAL, tmp0, a, a),
                            createInstruction(OP, Operation.LESS_THAN, tmp1, a, a),
                            createInstruction(OP, Operation.GREATER_THAN, tmp2, a, a)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, LogicBoolean.FALSE),
                            createInstruction(SET, tmp1, LogicBoolean.FALSE),
                            createInstruction(SET, tmp2, LogicBoolean.FALSE)
                    )
            );
        }

        @Test
        void optimizesSubXorOnIdentity() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, Operation.SUB, tmp0, a, a),
                            createInstruction(OP, Operation.BITWISE_XOR, tmp1, a, a)
                    ),
                    List.of(
                            createInstruction(SET, tmp0, P0),
                            createInstruction(SET, tmp1, P0)
                    )
            );
        }
    }

    @Nested
    class OpExpressionSimplifications {
        @Test
        void optimizesMulThenFloor1() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, mul, tmp0, value, K1000),
                            createInstruction(OP, floor, result, tmp0)
                    ),
                    List.of(
                            createInstruction(OP, idiv, result, value, K0001)
                    )
            );
        }

        @Test
        void optimizesMulThenFloor2() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, mul, tmp0, K1000, value),
                            createInstruction(OP, floor, result, tmp0)
                    ),
                    List.of(
                            createInstruction(OP, idiv, result, value, K0001)
                    )
            );
        }

        @Test
        void optimizesDivThenFloor() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, div, tmp0, value, K1000),
                            createInstruction(OP, floor, result, tmp0)
                    ),
                    List.of(
                            createInstruction(OP, idiv, result, value, K1000)
                    )
            );
        }

        @Test
        void optimizesNonConstantDivThenFloor() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, div, tmp0, value, divisor),
                            createInstruction(OP, floor, result, tmp0)
                    ),

                    List.of(
                            createInstruction(OP, idiv, result, value, divisor)
                    )
            );
        }

        @Test
        void protectsNonTemporaryVariables() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(OP, div, foo, value, K1000),
                            createInstruction(OP, floor, result, foo)
                    ),

                    List.of(
                            createInstruction(OP, div, foo, value, K1000),
                            createInstruction(OP, idiv, result, value, K1000)
                    )
            );
        }

        @Test
        void ignoresVariablesWithMultipleUsage() {
            assertDoesNotOptimize(
                    createInstruction(OP, div, tmp0, value, K1000),
                    createInstruction(OP, floor, result, tmp0),
                    createInstruction(SET, another, tmp0)
            );
        }

        @Test
        void ignoresInstructionsInWrongOrder() {
            assertDoesNotOptimize(
                    createInstruction(OP, floor, result, tmp0),
                    createInstruction(OP, div, tmp0, value, K1000)
            );
        }

        @Test
        void ignoresNonConstantMultiplicands() {
            assertDoesNotOptimize(
                    createInstruction(OP, mul, tmp0, value, divisor),
                    createInstruction(OP, floor, result, tmp0)
            );
        }
    }

    @Nested
    class PackColorInstruction {
        @Test
        void optimizesPackColor() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(PACKCOLOR, tmp0, P0_5, P0, P1, P1)
                    ),
                    List.of(
                            createInstruction(SET, tmp0, LogicColor.create(EMPTY, "%7f00ff"))
                    )
            );
        }
    }

    @Nested
    class SetInstruction {
        @Test
        void optimizesUselessSet() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SET, tmp0, tmp0),
                            createInstruction(SET, a, a),
                            createInstruction(SET, tmp1, tmp0)
                    ),

                    List.of(
                            createInstruction(SET, tmp1, tmp0)
                    )
            );
        }
    }

    @Nested
    class SelectInstruction {
        @Test
        void optimizesTrueCondition() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SELECT, var, Condition.EQUAL, P0, P0, c, d),
                            createInstruction(SELECT, var, Condition.LESS_THAN, P0, P1, c, d)
                    ),

                    List.of(
                            createInstruction(SET, var, c),
                            createInstruction(SET, var, c)
                    )
            );
        }

        @Test
        void optimizesFalseCondition() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SELECT, var, Condition.EQUAL, P0, P1, c, d),
                            createInstruction(SELECT, var, Condition.GREATER_THAN, P0, P1, c, d)
                    ),

                    List.of(
                            createInstruction(SET, var, d),
                            createInstruction(SET, var, d)
                    )
            );
        }

        @Test
        void optimizesSameArgumentsEqualCondition() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SELECT, var, Condition.EQUAL, a, a, c, d),
                            createInstruction(SELECT, var, Condition.GREATER_THAN_EQ, a, a, c, d),
                            createInstruction(SELECT, var, Condition.LESS_THAN_EQ, a, a, c, d),
                            createInstruction(SELECT, var, Condition.STRICT_EQUAL, a, a, c, d)
                    ),

                    List.of(
                            createInstruction(SET, var, c),
                            createInstruction(SET, var, c),
                            createInstruction(SET, var, c),
                            createInstruction(SET, var, c)
                    )
            );
        }

        @Test
        void optimizesSameArgumentsNotEqualCondition() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SELECT, var, Condition.NOT_EQUAL, a, a, c, d),
                            createInstruction(SELECT, var, Condition.GREATER_THAN, a, a, c, d),
                            createInstruction(SELECT, var, Condition.LESS_THAN, a, a, c, d)
                    ),

                    List.of(
                            createInstruction(SET, var, d),
                            createInstruction(SET, var, d),
                            createInstruction(SET, var, d)
                    )
            );
        }

        @Test
        void optimizesIdenticalTrueFalseValues() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SELECT, var, Condition.LESS_THAN, a, b, c, c)
                    ),

                    List.of(
                            createInstruction(SET, var, c)
                    )
            );
        }

        @Test
        void doesNotOptimizeGeneralCase() {
            assertDoesNotOptimize(
                    createInstruction(SELECT, var, Condition.LESS_THAN, a, b, c, d)
            );
        }

        @Test
        void turnsSelectIntoOp() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SELECT, var, Condition.LESS_THAN_EQ, a, b, P0, P1),
                            createInstruction(SELECT, var, Condition.LESS_THAN_EQ, a, b, P1, P0)
                    ),

                    List.of(
                            createInstruction(OP, Condition.GREATER_THAN, var, a, b),
                            createInstruction(OP, Condition.LESS_THAN_EQ, var, a, b)
                    )
            );
        }
    }

    @Nested
    class SensorInstruction {
        @Test
        void optimizesThisXY() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SENSOR, tmp0, thiz, x),
                            createInstruction(SENSOR, tmp1, thiz, y)
                    ),

                    List.of(
                            createInstruction(SET, tmp0, thisx),
                            createInstruction(SET, tmp1, thisy)
                    )
            );
        }

        @Test
        void optimizesIdName() {
            assertOptimizesTo(
                    List.of(
                            createInstruction(SENSOR, tmp0, lead, id),
                            createInstruction(SENSOR, tmp1, oreCoal, name)
                    ),
                    List.of(
                            createInstruction(SET, tmp0, P1),
                            createInstruction(SET, tmp1, LogicString.create("ore-coal"))
                    )
            );
        }
    }

    @Nested
    class CompiledCode {
        @Test
        void optimizesArrayAccess() {
            assertCompilesTo("""
                        var a[10];
                        a[0] = 1;
                        a[1] = 2;
                        a[2] = 3;
                        print(a[0], a[1], a[2]);
                        """,
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(PRINT, ".a*0"),
                    createInstruction(PRINT, ".a*1"),
                    createInstruction(PRINT, ".a*2")
            );
        }

        @Test
        void observesLocalOptions() {
            assertCompilesTo("""
                        param a = 0;
                        print(0 / a);
                        #setlocal expression-optimization = basic;
                        print(0 / b);
                        """,
                    createInstruction(SET, "a", "0"),
                    createInstruction(SET, tmp(0), "0"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(OP, "div", tmp(1), "0", ":b"),
                    createInstruction(PRINT, tmp(1))
            );
        }
    }
}
