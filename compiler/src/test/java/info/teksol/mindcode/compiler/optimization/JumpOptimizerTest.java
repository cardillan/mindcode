package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.Operation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.compiler.optimization.Optimization.*;
import static info.teksol.mindcode.compiler.optimization.OptimizationLevel.ADVANCED;
import static info.teksol.mindcode.logic.Opcode.*;

class JumpOptimizerTest extends AbstractOptimizerTest<JumpOptimizer> {

    @Override
    protected Class<JumpOptimizer> getTestedClass() {
        return JumpOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(JUMP_OPTIMIZATION);
    }

    @Test
    void collapsesUnnecessaryConditionals() {
        assertCompilesTo("""
                        value = if cell1[4] == 0 then
                            false;
                        else
                            cell1[4] = true;
                            n += 1;
                        end;
                        """,
                createInstruction(READ, var(0), "cell1", "4"),
                createInstruction(JUMP, var(1000), "notEqual", var(0), "0"),
                createInstruction(SET, var(2), "false"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(WRITE, "true", "cell1", "4"),
                createInstruction(OP, "add", var(3), "n", "1"),
                createInstruction(SET, "n", var(3)),
                createInstruction(SET, var(2), "n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "value", var(2)),
                createInstruction(END)
        );
    }

    @Test
    void collapsesJumps() {
        assertCompilesTo("""
                        while n > 0 do
                            n += 1;
                        end;
                        while n == null do
                            n += 1;
                        end;
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "lessThanEq", "n", "0"),
                createInstruction(OP, "add", var(1), "n", "1"),
                createInstruction(SET, "n", var(1)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "notEqual", "n", "null"),
                createInstruction(OP, "add", var(3), "n", "1"),
                createInstruction(SET, "n", var(3)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(END)
        );
    }

    @Test
    void preservesUserVariables() {
        assertCompilesTo(createTestCompiler(
                createCompilerProfile()
                        .setOptimizationLevel(DEAD_CODE_ELIMINATION, ADVANCED)
                        .setOptimizationLevel(TEMP_VARIABLES_ELIMINATION, ADVANCED)
                ),
                """
                        alive = @unit.@dead === 0;
                        if alive then
                            print(alive);
                        end;
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
        assertCompilesTo(createTestCompiler(
                createCompilerProfile()
                        .setOptimizationLevel(DEAD_CODE_ELIMINATION, ADVANCED)
                        .setOptimizationLevel(TEMP_VARIABLES_ELIMINATION, ADVANCED)
                ),
                """
                        if @unit.@dead === 0 then
                            print(alive);
                        end;
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


    @Test
    void optimizesMinimalSequence() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                        createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp0, LogicBoolean.FALSE),
                        createInstruction(PRINT, message),
                        createInstruction(LABEL, label0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(JUMP, label0, Operation.STRICT_EQUAL, a, b),
                        createInstruction(PRINT, message),
                        createInstruction(LABEL, label0),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresUserVariables() {
        assertDoesNotOptimize(
                createInstruction(OP, Operation.STRICT_EQUAL, c, a, b),
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, c, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresDifferentVariables() {
        assertDoesNotOptimize(
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp1, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresWrongConditions() {
        assertDoesNotOptimize(
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(JUMP, label0, Condition.EQUAL, tmp0, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresWrongOrder() {
        assertDoesNotOptimize(
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp0, LogicBoolean.FALSE),
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );
    }

    @Test
    void processesLogicalOr() {
        assertCompilesTo("""
                        param a = 0;
                        param b = 1;
                        if a || b then print("yes"); end;
                        """,
                createInstruction(SET, "a", "0"),
                createInstruction(SET, "b", "1"),
                createInstruction(OP, "or", var(0), "a", "b"),
                createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                createInstruction(PRINT, q("yes")),
                createInstruction(SET, var(2), q("yes")),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(2), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)

        );
    }
}