package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.logic.Condition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.compiler.optimization.Optimization.*;
import static info.teksol.mindcode.compiler.optimization.OptimizationLevel.AGGRESSIVE;
import static info.teksol.mindcode.logic.Opcode.*;

public class JumpOverJumpEliminatorTest extends AbstractOptimizerTest<JumpOverJumpEliminator> {

    @Override
    protected Class<JumpOverJumpEliminator> getTestedClass() {
        return JumpOverJumpEliminator.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(JUMP_OVER_JUMP_ELIMINATION);
    }

    @Test
    void optimizesBreakInWhileLoop() {
        assertCompilesTo(createTestCompiler(
                createCompilerProfile()
                        .setOptimizationLevel(DEAD_CODE_ELIMINATION, AGGRESSIVE)
                        .setOptimizationLevel(SINGLE_STEP_JUMP_ELIMINATION, AGGRESSIVE)
                        .setOptimizationLevel(CONDITIONAL_JUMPS_IMPROVEMENT, AGGRESSIVE)
                        .setOptimizationLevel(JUMP_OVER_JUMP_ELIMINATION, AGGRESSIVE)
                ),
                """
                        while true
                            print("In loop")
                            if @unit.dead === 0
                                break
                            end
                        end
                        print("Out of loop")
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "equal", "true", "false"),
                createInstruction(PRINT, q("In loop")),
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(JUMP, var(1002), "strictEqual", var(0), "0"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("Out of loop")),
                createInstruction(END)
        );
    }

    @Test
    void optimizesMinimalSequence() {
        assertOptimizesTo(
                List.of(
                        createInstruction(LABEL, label0),
                        createInstruction(JUMP, label1, Condition.EQUAL, a, b),
                        createInstruction(JUMP, label0, Condition.ALWAYS),
                        createInstruction(LABEL, label1),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(LABEL, label0),
                        createInstruction(JUMP, label0, Condition.NOT_EQUAL, a, b),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresStrictEqual() {
        assertDoesNotOptimize(
                createInstruction(LABEL, label0),
                createInstruction(JUMP, label1, Condition.STRICT_EQUAL, a, b),
                createInstruction(JUMP, label0, Condition.ALWAYS),
                createInstruction(LABEL, label1),
                createInstruction(END)
        );
    }

    @Test
    void ignoresDistantJumps() {
        assertDoesNotOptimize(
                createInstruction(LABEL, label0),
                createInstruction(JUMP, label1, Condition.STRICT_EQUAL, a, b),
                createInstruction(JUMP, label0, Condition.ALWAYS),
                createInstruction(PRINT, a),
                createInstruction(LABEL, label1),
                createInstruction(END)
        );
    }

    @Test
    void ignoresConditionalJumps() {
        assertDoesNotOptimize(
                createInstruction(LABEL, label0),
                createInstruction(JUMP, label1, Condition.EQUAL, a, b),
                createInstruction(JUMP, label0, Condition.EQUAL, c, d),
                createInstruction(LABEL, label1),
                createInstruction(END)
        );
    }
}