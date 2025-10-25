package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.optimization.Optimization.JUMP_OPTIMIZATION;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
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
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(SET, var(2), "n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "value", var(2))
        );
    }

    @Test
    void collapsesJumps() {
        assertCompilesTo("""
                        while n > 0 do
                            n++;
                        end;
                        while n == null do
                            n++;
                        end;
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "lessThanEq", "n", "0"),
                createInstruction(SET, var(1), "n"),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "notEqual", "n", "null"),
                createInstruction(SET, var(3), "n"),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005))
        );
    }

    @Test
    void preservesUserVariables() {
        assertCompilesTo("""
                        #set dead-code-elimination = advanced;
                        #set temp-variables-elimination = advanced;
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
        assertCompilesTo("""
                        #set emulate-strict-not-equal = false;
                        #set dead-code-elimination = advanced;
                        #set temp-variables-elimination = advanced;
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
    void OptimizesStrictEqualConditionsInTarget8() {
        assertCompilesTo("""
                        #set dead-code-elimination = advanced;
                        #set temp-variables-elimination = advanced;
                        if @unit.@dead === 0 then
                            print(alive);
                        end;
                        """,
                createInstruction(SENSOR, tmp(0), "@unit", "@dead"),
                createInstruction(JUMP, label(0), "strictNotEqual", tmp(0), "0"),
                createInstruction(PRINT, ":alive"),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(LABEL, label(1))
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
        profile.setEmulateStrictNotEqual(false);
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

    @Test
    void processesInlineFunctions() {
        assertCompilesTo("""
                        #set optimization = advanced;
                        inline def isZero(x)
                            abs(x) <= 0;
                        end;
                        if isZero(rand(10)) then
                            print("yes");
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(OP, "rand", var(0), "10"),
                createInstruction(OP, "abs", var(2), var(0)),
                createInstruction(JUMP, "__start__", "greaterThan", var(2), "0"),
                createInstruction(PRINT, q("yes"))


        );
    }
}