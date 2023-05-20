package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.compiler.optimization.Optimization.*;
import static info.teksol.mindcode.compiler.optimization.OptimizationLevel.AGGRESSIVE;
import static info.teksol.mindcode.logic.Opcode.*;

class ImproveNegativeConditionalJumpsTest extends AbstractOptimizerTest<ImproveNegativeConditionalJumps> {

    @Override
    protected Class<ImproveNegativeConditionalJumps> getTestedClass() {
        return ImproveNegativeConditionalJumps.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(CONDITIONAL_JUMPS_IMPROVEMENT);
    }

    @Test
    void collapsesUnnecessaryConditionals() {
        assertCompilesTo("""
                        value = if cell1[4] == 0
                            false
                        else
                            cell1[4] = true
                            n += 1
                        end
                        """,
                createInstruction(READ, var(0), "cell1", "4"),
                createInstruction(JUMP, var(1000), "notEqual", var(0), "0"),
                createInstruction(SET, var(2), "false"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(WRITE, "true", "cell1", "4"),
                createInstruction(OP, "add", var(3), "n", "1"),
                createInstruction(SET, "n", var(3)),
                createInstruction(SET, var(2), var(3)),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "value", var(2)),
                createInstruction(END)
        );
    }

    @Test
    void collapsesJumps() {
        assertCompilesTo("""
                        while n > 0
                            n += 1
                        end
                        while n == null
                            n += 1
                        end
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "lessThanEq", "n", "0"),
                createInstruction(OP, "add", var(1), "n", "1"),
                createInstruction(SET, "n", var(1)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "notEqual", "n", "null"),
                createInstruction(OP, "add", var(3), "n", "1"),
                createInstruction(SET, "n", var(3)),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(END)
        );
    }

    @Test
    void preservesUserVariables() {
        assertCompilesTo(createCompilerProfile()
                        .setOptimizationLevel(DEAD_CODE_ELIMINATION, AGGRESSIVE)
                        .setOptimizationLevel(OUTPUT_TEMPS_ELIMINATION, AGGRESSIVE),
                """
                        alive = @unit.dead === 0
                        if alive
                            print(alive)
                        end
                        """,
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(OP, "strictEqual", "alive", var(0), "0"),
                createInstruction(JUMP, var(1000), "equal", "alive", "false"),
                createInstruction(PRINT, "alive"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void preservesStrictEqualConditions() {
        assertCompilesTo(createCompilerProfile()
                        .setOptimizationLevel(DEAD_CODE_ELIMINATION, AGGRESSIVE)
                        .setOptimizationLevel(OUTPUT_TEMPS_ELIMINATION, AGGRESSIVE),
                """
                        if @unit.dead === 0
                            print(alive)
                        end
                        """,
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(OP, "strictEqual", var(1), var(0), "0"),
                createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                createInstruction(PRINT, "alive"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }
}