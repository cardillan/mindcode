package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.compiler.optimization.Optimization.INPUT_TEMPS_ELIMINATION;
import static info.teksol.mindcode.logic.Opcode.*;

// NOTE LogicInstructionGenerator now generates code that doesn't require InputTempEliminator
//      All tests expecting optimization to happen that did not fail when the optimizer was disabled were removed.
public class InputTempEliminatorTest extends AbstractOptimizerTest<InputTempEliminator> {

    @Override
    protected Class<InputTempEliminator> getTestedClass() {
        return InputTempEliminator.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(INPUT_TEMPS_ELIMINATION);
    }

    @Test
    void optimizesBasicCase() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SET, tmp0, P0),
                        createInstruction(DRAW, color, tmp0, tmp0, tmp0, P255),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(DRAW, color, P0, P0, P0, P255),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresNonTemporaryVariables() {
        assertDoesNotOptimize(
                createInstruction(SET, C, P0),
                createInstruction(DRAW, color, C, C, C, P255),
                createInstruction(END)
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        assertDoesNotOptimize(
                createInstruction(SET, tmp0, P0),
                createInstruction(SET, tmp0, P1),
                createInstruction(DRAW, color, tmp0, tmp0, tmp0, P255),
                createInstruction(END)
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        assertDoesNotOptimize(
                createInstruction(DRAW, color, tmp0, tmp0, tmp0, P255),
                createInstruction(SET, tmp0, P0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresWrongArgumentType() {
        assertDoesNotOptimize(
                createInstruction(SET, tmp0, P0),
                createInstruction(GETLINK, tmp0, P1),
                createInstruction(END)
        );
    }

    @Test
    void consecutiveSetsWithNoRelationshipAreLeftAlone() {
        assertDoesNotOptimize(
                createInstruction(SET, a, P0),
                createInstruction(SET, b, P1)
        );
    }
}
