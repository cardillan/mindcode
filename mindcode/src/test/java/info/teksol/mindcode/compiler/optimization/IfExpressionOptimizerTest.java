package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class IfExpressionOptimizerTest extends AbstractOptimizerTest<IfExpressionOptimizer> {

    @Override
    protected Class<IfExpressionOptimizer> getTestedClass() {
        return IfExpressionOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.CONDITIONAL_JUMPS_IMPROVEMENT,
                Optimization.SINGLE_STEP_JUMP_ELIMINATION,
                Optimization.JUMP_OVER_JUMP_ELIMINATION,
                Optimization.TEMP_VARIABLES_ELIMINATION,
                Optimization.UNREACHABLE_CODE_ELIMINATION,
                Optimization.IF_EXPRESSION_OPTIMIZATION
        );
    }

    @Test
    void optimizesBasicCase() {
        assertCompilesTo("""
                        str = x > 0 ? "positive" : "negative"
                        print(str)
                        """,
                createInstruction(SET, "str", q("negative")),
                createInstruction(JUMP, var(1001), "lessThanEq", "x", "0"),
                createInstruction(SET, "str", q("positive")),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "str"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesTrueBranchCompoundCondition() {
        assertCompilesTo("""
                        str = @unit.dead === 0 ? "alive" : "dead"
                        print(str)
                        """,
                createInstruction(SET, "str", q("alive")),
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(JUMP, var(1001), "strictEqual", var(0), "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "str", q("dead")),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "str"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesTrueBranchInvertible() {
        assertCompilesTo("""
                        str = if x >= 0
                            "positive"
                        else
                            x = -x
                            "negative"
                        end
                        print(str)
                        """,
                createInstruction(SET, "str", q("positive")),
                createInstruction(JUMP, var(1001), "greaterThanEq", "x", "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "mul", "x", "-1", "x"),
                createInstruction(SET, "str", q("negative")),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "str"),
                createInstruction(END)
        );
    }

    @Test
    void ignoresUnprocessableStatements() {
        assertCompilesTo("""
                        if x >= 0
                            print("positive")
                        else
                            print("negative")
                        end
                        """,
                createInstruction(JUMP, var(1000), "lessThan", "x", "0"),
                createInstruction(PRINT, q("positive")),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("negative")),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void mergesWritesInLargeBranches() {
        assertCompilesTo("""
                        y = if x >= 0
                            print("positive")
                            x
                        else
                            print("negative");
                            -x
                        end
                        print(y)
                        """,
                createInstruction(JUMP, var(1000), "lessThan", "x", "0"),
                createInstruction(PRINT, q("positive")),
                createInstruction(SET, "y", "x"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("negative")),
                createInstruction(OP, "mul", "y", "-1", "x"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "y"),
                createInstruction(END)
        );
    }

    @Test
    void mergesMemoryReads() {
        assertCompilesTo("""
                        allocate heap in cell1[0 ... 64]
                        value = $setting == 1 ? $low : $high
                        print(value)
                        """,
                createInstruction(READ, "value", "cell1", "2"),
                createInstruction(READ, var(0), "cell1", "0"),
                createInstruction(JUMP, var(1001), "notEqual", var(0), "1"),
                createInstruction(READ, "value", "cell1", "1"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "value"),
                createInstruction(END)
        );
    }

    @Test
    void swapsBranchesForCompoundConditions() {
        assertCompilesTo("""
                        if @unit.dead === 0
                            print("alive")
                        else
                            print("dead")
                        end
                        """,
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(JUMP, var(1000), "strictEqual", var(0), "0"),
                createInstruction(PRINT, q("dead")),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("alive")),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void detectsLeakingBlocks() {
        assertCompilesTo("""
                        while y > 0
                            y = if x > 0
                                if z == 0 break end
                                2
                            else
                                -2
                            end
                        end
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "lessThanEq", "y", "0"),
                createInstruction(JUMP, var(1003), "lessThanEq", "x", "0"),
                createInstruction(JUMP, var(1002), "equal", "z", "0"),
                createInstruction(LABEL, var(1005)),
                createInstruction(LABEL, var(1006)),
                createInstruction(SET, var(2), "2"),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(2), "-2"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "y", var(2)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void optimizesChainedStatements() {
        assertCompilesTo("""
                        y = if x < 0
                            "negative"
                        elsif x > 0
                            "positive"
                        else
                            "zero"
                        end
                        print(y)
                        """,
                createInstruction(SET, "y", q("negative")),
                createInstruction(JUMP, var(1001), "lessThan", "x", "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "y", q("zero")),
                createInstruction(JUMP, var(1003), "lessThanEq", "x", "0"),
                createInstruction(SET, "y", q("positive")),
                createInstruction(LABEL, var(1003)),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "y"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesChainedAssignments() {
        assertCompilesTo("""
                        a = b = rand(10) > 5 ? 1 : 2
                        print(a, b)
                        """,
                createInstruction(SET, "b", "2"),
                createInstruction(OP, "rand", var(0), "10"),
                createInstruction(JUMP, var(1001), "lessThanEq", var(0), "5"),
                createInstruction(SET, "b", "1"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "a", "b"),
                createInstruction(PRINT, "a"),
                createInstruction(PRINT, "b"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesChainedAssignments2() {
        assertCompilesTo("""
                        a = print(b = rand(10) > 5 ? 1 : 2)
                        print(a, b)
                        """,
                createInstruction(SET, "b", "2"),
                createInstruction(OP, "rand", var(0), "10"),
                createInstruction(JUMP, var(1001), "lessThanEq", var(0), "5"),
                createInstruction(SET, "b", "1"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "b"),
                createInstruction(SET, "a", "b"),
                createInstruction(PRINT, "a"),
                createInstruction(PRINT, "b"),
                createInstruction(END)
        );
    }

    @Test
    void preservesDecisionVarialbe() {
        assertCompilesTo("""
                        i = rand(10)
                        i = i % 2 ? 5 : 6
                        print(i)
                        """,
                createInstruction(OP, "rand", "i", "10"),
                createInstruction(OP, "mod", var(1), "i", "2"),
                createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                createInstruction(SET, "i", "5"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "i", "6"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "i"),
                createInstruction(END)
        );
    }
}