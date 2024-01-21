package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.logic.Operation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class ExpressionOptimizerTest extends AbstractOptimizerTest<ExpressionOptimizer> {

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
    void optimizesConstantId() {
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
}
