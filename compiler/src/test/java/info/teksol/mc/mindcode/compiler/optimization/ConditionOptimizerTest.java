package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.optimization.Optimization.CONDITION_OPTIMIZATION;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class ConditionOptimizerTest extends AbstractOptimizerTest<ConditionOptimizer> {

    @Override
    protected Class<ConditionOptimizer> getTestedClass() {
        return ConditionOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(CONDITION_OPTIMIZATION);
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
                createInstruction(READ, tmp(0), "cell1", "4"),
                createInstruction(JUMP, label(0), "notEqual", tmp(0), "0"),
                createInstruction(SET, tmp(2), "false"),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(WRITE, "true", "cell1", "4"),
                createInstruction(OP, "add", ":n", ":n", "1"),
                createInstruction(SET, tmp(2), ":n"),
                createInstruction(LABEL, label(1)),
                createInstruction(SET, ":value", tmp(2))
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
                createInstruction(LABEL, label(0)),
                createInstruction(JUMP, label(2), "lessThanEq", ":n", "0"),
                createInstruction(SET, tmp(1), ":n"),
                createInstruction(OP, "add", ":n", ":n", "1"),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(LABEL, label(2)),
                createInstruction(LABEL, label(3)),
                createInstruction(JUMP, label(5), "notEqual", ":n", "null"),
                createInstruction(SET, tmp(3), ":n"),
                createInstruction(OP, "add", ":n", ":n", "1"),
                createInstruction(JUMP, label(3), "always"),
                createInstruction(LABEL, label(5))
        );
    }

    @Test
    void processesUserVariables() {
        assertCompilesTo("""
                        #set data-flow-optimization = experimental;
                        #set dead-code-elimination = experimental;
                        #set temp-variables-elimination = experimental;
                        alive = @unit.@dead == 0;
                        if alive then
                            print("alive");
                        end;
                        """,
                createInstruction(SENSOR, tmp(0), "@unit", "@dead"),
                createInstruction(JUMP, label(0), "notEqual", tmp(0), "0"),
                createInstruction(PRINT, q("alive")),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(LABEL, label(1))
        );
    }

    @Test
    void preservesStrictEqualConditions() {
        assertCompilesTo("""
                        #set emulate-strict-not-equal = false;
                        #set dead-code-elimination = advanced;
                        #set temp-variables-elimination = advanced;
                        if @unit.@dead === 0 then
                            print("alive");
                        end;
                        """,
                createInstruction(SENSOR, tmp(0), "@unit", "@dead"),
                createInstruction(OP, "strictEqual", tmp(1), tmp(0), "0"),
                createInstruction(JUMP, label(0), "equal", tmp(1), "false"),
                createInstruction(PRINT, q("alive")),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(LABEL, label(1))
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
                        createInstruction(LABEL, label0)
                ),
                List.of(
                        createInstruction(JUMP, label0, Operation.STRICT_EQUAL, a, b),
                        createInstruction(PRINT, message),
                        createInstruction(LABEL, label0)
                )
        );
    }

    @Test
    void ignoresDifferentVariables() {
        assertDoesNotOptimize(
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp1, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0)
        );
    }

    @Test
    void ignoresWrongConditions() {
        profile.setEmulateStrictNotEqual(false);
        assertDoesNotOptimize(
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(JUMP, label0, Condition.EQUAL, tmp0, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0)
        );
    }

    @Test
    void ignoresWrongOrder() {
        assertDoesNotOptimize(
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, c, LogicBoolean.FALSE),
                createInstruction(OP, Operation.STRICT_EQUAL, c, a, b),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0)
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
                createInstruction(OP, "or", tmp(1), "a", "b"),
                createInstruction(JUMP, label(0), "equal", tmp(1), "false"),
                createInstruction(PRINT, q("yes")),
                createInstruction(SET, tmp(2), q("yes")),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(SET, tmp(2), "null"),
                createInstruction(LABEL, label(1))
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
                createInstruction(OP, "rand", tmp(0), "10"),
                createInstruction(OP, "abs", tmp(2), tmp(0)),
                createInstruction(JUMP, "__start__", "greaterThan", tmp(2), "0"),
                createInstruction(PRINT, q("yes"))


        );
    }
}
