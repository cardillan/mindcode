package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class PropagateJumpTargetsTest extends AbstractOptimizerTest<PropagateJumpTargets> {

    @Override
    protected Class<PropagateJumpTargets> getTestedClass() {
        return PropagateJumpTargets.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.CONDITIONAL_JUMPS_IMPROVEMENT,
                Optimization.JUMP_TARGET_PROPAGATION,
                Optimization.TEMP_VARIABLES_ELIMINATION
        );
    }

    @Test
    void propagatesThroughUnconditionalTargets() {
        assertCompilesTo("""
                        if a
                            if b
                                print(b)
                            end
                            print(a)
                        end
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(JUMP, "__start__", "equal", "a", "false"),
                createInstruction(JUMP, var(1002), "equal", "b", "false"),
                createInstruction(PRINT, "b"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "a"),
                createInstruction(JUMP, "__start__", "always"),
                createInstruction(END)
        );
    }

    @Test
    void propagatesThroughConditionalTargets() {
        assertCompilesTo("""
                        while c == null
                            c = getlink(1)
                            if c == null
                                print("Not found")
                            end
                        end
                        print("Done")
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "notEqual", "c", "null"),
                createInstruction(GETLINK, "c", "1"),
                createInstruction(JUMP, var(1002), "notEqual", "c", "null"),
                createInstruction(PRINT, q("Not found")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("Done")),
                createInstruction(END)
        );
    }

    @Test
    void ignoresVolatileVariables() {
        assertCompilesTo("""
                        while @time < wait
                            n += 1
                            if @time < wait
                                print("Waiting")
                            end
                        end
                        print("Done")
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "@time", "wait"),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(JUMP, var(1000), "greaterThanEq", "@time", "wait"),
                createInstruction(PRINT, q("Waiting")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("Done")),
                createInstruction(END)
        );
    }
}
