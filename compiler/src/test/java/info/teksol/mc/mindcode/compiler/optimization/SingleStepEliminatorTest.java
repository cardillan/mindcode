package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class SingleStepEliminatorTest extends AbstractOptimizerTest<SingleStepEliminator> {

    @Override
    protected Class<SingleStepEliminator> getTestedClass() {
        return SingleStepEliminator.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.JUMP_NORMALIZATION,
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.SINGLE_STEP_ELIMINATION
        );
    }

    @Test
    void removesSingleJump() {
        assertCompilesTo("""
                        if x then
                            print(1);
                        end;
                        """,
                createInstruction(JUMP, var(1000), "equal", "x", "false"),
                createInstruction(PRINT, "1"),
                createInstruction(LABEL, var(1000))
        );
    }

    @Test
    void removesTwoJumps() {
        assertCompilesTo("""
                        if x then
                            if y then
                                print(1);
                            end;
                        end;
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
                        if x then
                            print(a);
                        else
                            print(b);
                        end;
                        """,
                createInstruction(JUMP, var(1000), "equal", "x", "false"),
                createInstruction(PRINT, "a"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "b"),
                createInstruction(LABEL, var(1001))
        );
    }

    @Test
    void keepsDynamicJumps() {
        assertCompilesTo("""
                        #set optimization = advanced;
                        param x = 4;
                        sum = 0;
                        for i in 0 ... 5 do
                            sum += x;
                            if sum > 10 then
                                break;
                            end;
                        end;
                        print(sum);
                        """,
                createInstruction(SET, "x", "4"),
                createInstruction(SET, ":sum", "x"),
                createInstruction(JUMP, var(1002), "greaterThan", "x", "10"),
                createInstruction(OP, "add", ":sum", "x", "x"),
                createInstruction(JUMP, var(1002), "greaterThan", ":sum", "10"),
                createInstruction(OP, "add", ":sum", ":sum", "x"),
                createInstruction(JUMP, var(1002), "greaterThan", ":sum", "10"),
                createInstruction(OP, "add", ":sum", ":sum", "x"),
                createInstruction(JUMP, var(1002), "greaterThan", ":sum", "10"),
                createInstruction(OP, "add", ":sum", ":sum", "x"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, ":sum")
        );
    }
}
