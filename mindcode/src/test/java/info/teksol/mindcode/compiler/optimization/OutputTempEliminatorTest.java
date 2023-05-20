package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class OutputTempEliminatorTest extends AbstractOptimizerTest<OutputTempEliminator> {

    @Override
    protected Class<OutputTempEliminator> getTestedClass() {
        return OutputTempEliminator.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.OUTPUT_TEMPS_ELIMINATION
        );
    }

    @Test
    void optimizesBasicCase() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SENSOR, tmp0, vault1, coal),
                        createInstruction(SET, var, tmp0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(SENSOR, var, vault1, coal),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SENSOR, var, vault1, coal),
                        createInstruction(SET, result, var),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(SENSOR, var, vault1, coal),
                        createInstruction(SET, result, var),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SENSOR, tmp0, vault1, coal),
                        createInstruction(SET, result, tmp0),
                        createInstruction(SET, another, tmp0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(SENSOR, tmp0, vault1, coal),
                        createInstruction(SET, result, tmp0),
                        createInstruction(SET, another, tmp0),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        assertOptimizesTo(
                List.of(
                        createInstruction(PRINT, K1),
                        createInstruction(SET, result, tmp0),
                        createInstruction(SENSOR, tmp0, vault1, coal),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(PRINT, K1),
                        createInstruction(SET, result, tmp0),
                        createInstruction(SENSOR, tmp0, vault1, coal),
                        createInstruction(END)
                )
        );
    }

    @Test
    void mergesTwoSets() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SET, tmp0, a),
                        createInstruction(SET, b, tmp0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(SET, b, a),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresIncompatibleSets() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SET, tmp0, a),
                        createInstruction(SET, tmp0, b),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(SET, tmp0, a),
                        createInstruction(SET, tmp0, b),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresWrongArgumentType() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SENSOR, tmp1, vault1, tmp0),
                        createInstruction(SET, result, tmp0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(SENSOR, tmp1, vault1, tmp0),
                        createInstruction(SET, result, tmp0),
                        createInstruction(END)
                )
        );
    }

    // Taken from OptimizeGetlinkThenSetTest

    @Test
    void optimizesGetlinkFollowedBySet() {
        assertCompilesTo("""
                        target = getlink(0)
                        """,
                createInstruction(GETLINK, "target", "0"),
                createInstruction(END)
        );
    }

    @Test
    void leavesGetlinkFollowedBySetForDifferentStuffAlone() {
        assertDoesNotOptimize(
                createInstruction(GETLINK, a, K0),
                createInstruction(SET, b, K1)
        );
    }

    @Test
    void leavesGetlinkFollowedByGetlinkAlone() {
        assertDoesNotOptimize(
                createInstruction(GETLINK, a, K0),
                createInstruction(GETLINK, b, K1),
                createInstruction(READ, c, cell1, K1)
        );
    }

    @Test
    void ignoresTerminalGetlink() {
        assertDoesNotOptimize(
                createInstruction(GETLINK, a, K0)
        );
    }

    // Taken from OptimizeOpThenSetTest

    @Test
    void optimizesOpThenSet() {
        assertCompilesTo("""
                        r = rand(100)
                        """,
                createInstruction(OP, "rand", "r", "100"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesOpOpThenSet() {
        assertCompilesTo("""
                        state = min(max(state, MIN), MAX)
                        """,
                createInstruction(OP, "max", var(0), "state", "MIN"),
                createInstruction(OP, "min", "state", var(0), "MAX"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesOpThenSetFromBinaryOp() {
        assertCompilesTo("""
                        remaining = capacity - current
                        """,
                createInstruction(OP, "sub", "remaining", "capacity", "current"),
                createInstruction(END)
        );
    }

    // Taken from OptimizeReadThenSetTest

    @Test
    void improvesReadThenSet() {
        assertCompilesTo("""
                        boo = cell1[0]
                        """,
                createInstruction(READ, "boo", "cell1", "0"),
                createInstruction(END)
        );
    }

    @Test
    void leavesReadThenSetButOtherValueAlone() {
        assertDoesNotOptimize(
                createInstruction(READ, tmp0, cell1, K1),
                createInstruction(SET, tmp1, K1),
                createInstruction(END)
        );
    }

    // Taken from OptimizeSensorThenSetTest

    @Test
    void optimizesSensorThenSet() {
        assertCompilesTo("""
                        numsilicon = STORAGE.silicon
                        """,
                createInstruction(SENSOR, "numsilicon", "STORAGE", "@silicon"),
                createInstruction(END)
        );
    }

    @Test
    void supportsConsecutiveSensors() {
        assertCompilesTo("""
                        numgraphite = container1.graphite
                        numcoal = container1.coal
                        """,
                createInstruction(SENSOR, "numgraphite", "container1", "@graphite"),
                createInstruction(SENSOR, "numcoal", "container1", "@coal"),
                createInstruction(END)
        );
    }

    @Test
    void leavesSensorThenSetWithUnrelatedAlone() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, a, vault1, coal),
                createInstruction(SET, b, K1)
        );
    }

    @Test
    void leavesSensorNonSetAlone() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, a, vault1, coal),
                createInstruction(PRINT, a)
        );
    }

    @Test
    void ignoresConsecutiveSensors() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, a, vault1, coal),
                createInstruction(SENSOR, b, conveyor1, enabled)
        );
    }

    @Test
    void leavesSingleSensorAlone() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, a, vault1, coal)
        );
    }
}
