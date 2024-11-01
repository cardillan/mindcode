package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.Operation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

@Order(99)
class ExpressionOptimizerTest extends AbstractOptimizerTest<ExpressionOptimizer> {

    @Override
    protected Class<ExpressionOptimizer> getTestedClass() {
        return ExpressionOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(Optimization.EXPRESSION_OPTIMIZATION);
    }

    @Test
    void optimizesMulThenFloor1() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, mul, tmp0, value, K1000),
                        createInstruction(OP, floor, result, tmp0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(OP, idiv, result, value, K0001),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesMulThenFloor2() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, mul, tmp0, K1000, value),
                        createInstruction(OP, floor, result, tmp0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(OP, idiv, result, value, K0001),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesDivThenFloor() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, div, tmp0, value, K1000),
                        createInstruction(OP, floor, result, tmp0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(OP, idiv, result, value, K1000),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesNonConstantDivThenFloor() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, div, tmp0, value, divisor),
                        createInstruction(OP, floor, result, tmp0),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(OP, idiv, result, value, divisor),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresNonTemporaryVariables() {
        assertDoesNotOptimize(
                createInstruction(OP, div, foo, value, K1000),
                createInstruction(OP, floor, result, foo),
                createInstruction(END)
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        assertDoesNotOptimize(
                createInstruction(OP, div, tmp0, value, K1000),
                createInstruction(OP, floor, result, tmp0),
                createInstruction(SET, another, tmp0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        assertDoesNotOptimize(
                createInstruction(OP, floor, result, tmp0),
                createInstruction(OP, div, tmp0, value, K1000),
                createInstruction(END)
        );
    }

    @Test
    void ignoresNonConstantMultiplicands() {
        assertDoesNotOptimize(
                createInstruction(OP, mul, tmp0, value, divisor),
                createInstruction(OP, floor, result, tmp0),
                createInstruction(END)
        );
    }

    @Test
    void optimizesUselessSet() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SET, tmp0, tmp0),
                        createInstruction(SET, tmp1, tmp0),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp1, tmp0),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesThisXY() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SENSOR, tmp0, thiz, x),
                        createInstruction(SENSOR, tmp1, thiz, y),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, thisx),
                        createInstruction(SET, tmp1, thisy),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesSensor() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SENSOR, tmp0, lead, id),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, P1),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesLookup() {
        assertOptimizesTo(
                List.of(
                        createInstruction(LOOKUP, item, tmp0, P1),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, lead),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesMulBy0() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.MUL, tmp0, a, P0),
                        createInstruction(OP, Operation.MUL, tmp1, P0, b),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, P0),
                        createInstruction(SET, tmp1, P0),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesMulBy1() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.MUL, tmp0, a, P1),
                        createInstruction(OP, Operation.MUL, tmp1, P1, b),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, a),
                        createInstruction(SET, tmp1, b),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesDivBy1() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.DIV, tmp0, a, P1),
                        createInstruction(OP, Operation.DIV, tmp1, P1, b),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, a),
                        createInstruction(OP, Operation.DIV, tmp1, P1, b),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesAddOf0() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.ADD, tmp0, a, P0),
                        createInstruction(OP, Operation.ADD, tmp1, P0, b),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, a),
                        createInstruction(SET, tmp1, b),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesSubOf0() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.SUB, tmp0, a, P0),
                        createInstruction(OP, Operation.SUB, tmp1, P0, b),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, a),
                        createInstruction(SET, tmp1, b),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesEqualitiesOnIdentity() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.EQUAL, tmp0, a, a),
                        createInstruction(OP, Operation.LESS_THAN_EQ, tmp1, a, a),
                        createInstruction(OP, Operation.GREATER_THAN_EQ, tmp2, a, a),
                        createInstruction(OP, Operation.STRICT_EQUAL, tmp3, a, a),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, LogicBoolean.TRUE),
                        createInstruction(SET, tmp1, LogicBoolean.TRUE),
                        createInstruction(SET, tmp2, LogicBoolean.TRUE),
                        createInstruction(SET, tmp3, LogicBoolean.TRUE),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesInequalitiesOnIdentity() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.NOT_EQUAL, tmp0, a, a),
                        createInstruction(OP, Operation.LESS_THAN, tmp1, a, a),
                        createInstruction(OP, Operation.GREATER_THAN, tmp2, a, a),
                        createInstruction(END)
                ),

                List.of(
                        createInstruction(SET, tmp0, LogicBoolean.FALSE),
                        createInstruction(SET, tmp1, LogicBoolean.FALSE),
                        createInstruction(SET, tmp2, LogicBoolean.FALSE),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesSubXorOnIdentity() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.SUB, tmp0, a, a),
                        createInstruction(OP, Operation.XOR, tmp1, a, a),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(SET, tmp0, P0),
                        createInstruction(SET, tmp1, P0),
                        createInstruction(END)
                )
        );
    }
}
