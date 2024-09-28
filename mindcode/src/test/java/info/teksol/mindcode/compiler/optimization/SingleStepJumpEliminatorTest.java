package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class SingleStepJumpEliminatorTest extends AbstractOptimizerTest<SingleStepJumpEliminator> {

    @Override
    protected Class<SingleStepJumpEliminator> getTestedClass() {
        return SingleStepJumpEliminator.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.CONDITIONAL_JUMPS_NORMALIZATION,
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.SINGLE_STEP_JUMP_ELIMINATION
        );
    }

    @Test
    void removesSingleJump() {
        assertCompilesTo("""
                        if x print(1) end
                        """,
                createInstruction(JUMP, var(1000), "equal", "x", "false"),
                createInstruction(PRINT, "1"),
                createInstruction(LABEL, var(1000))
        );
    }

    @Test
    void removesTwoJumps() {
        assertCompilesTo("""
                        if x if y print(1) end end
                        """,
                createInstruction(JUMP, var(1000), "equal", "x", "false"),
                createInstruction(JUMP, var(1002), "equal", "y", "false"),
                createInstruction(PRINT, "1"),
                createInstruction(LABEL, var(1002)),
                createInstruction(LABEL, var(1000))
        );
    }

    @Test
    void keepsIsolatedJumps() {
        assertCompilesTo("""
                        if x print(a) else print(b) end
                        """,
                createInstruction(JUMP, var(1000), "equal", "x", "false"),
                createInstruction(PRINT, "a"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "b"),
                createInstruction(LABEL, var(1001))
        );
    }
}
