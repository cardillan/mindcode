package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.logic.Condition;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.compiler.optimization.Optimization.*;
import static info.teksol.mindcode.compiler.optimization.OptimizationLevel.ADVANCED;
import static info.teksol.mindcode.logic.Opcode.*;

@Order(99)
class JumpOverJumpEliminatorTest extends AbstractOptimizerTest<JumpOverJumpEliminator> {

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
                        .setOptimizationLevel(DEAD_CODE_ELIMINATION, ADVANCED)
                        .setOptimizationLevel(SINGLE_STEP_JUMP_ELIMINATION, ADVANCED)
                        .setOptimizationLevel(CONDITIONAL_JUMPS_OPTIMIZATION, ADVANCED)
                        .setOptimizationLevel(JUMP_OVER_JUMP_ELIMINATION, ADVANCED)
                ),
                """
                        while true do
                            print("In loop");
                            if @unit.dead === 0 then
                                break;
                            end;
                        end;
                        print("Out of loop");
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "equal", "true", "false"),
                createInstruction(PRINT, q("In loop")),
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(JUMP, var(1002), "strictEqual", var(0), "0"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("Out of loop"))
        );
    }

    @Test
    void optimizesFunctionReturn() {
        assertCompilesTo(createTestCompiler(createCompilerProfile().setAllOptimizationLevels(ADVANCED)),
                """
                        displayItem();
                        
                        def displayItem()
                            amount = vault1.coal;
                            if amount == 0 then return; end;
                            print(amount % 10);
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SENSOR, "__fn0_amount", "vault1", "@coal"),
                createInstruction(JUMP, "__start__", "equal", "__fn0_amount", "0"),
                createInstruction(OP, "mod", var(4), "__fn0_amount", "10"),
                createInstruction(PRINT, var(4))
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