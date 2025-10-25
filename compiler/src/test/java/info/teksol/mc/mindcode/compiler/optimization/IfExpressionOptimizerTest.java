package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class IfExpressionOptimizerTest extends AbstractOptimizerTest<IfExpressionOptimizer> {

    @Override
    protected Class<IfExpressionOptimizer> getTestedClass() {
        return IfExpressionOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.JUMP_OPTIMIZATION,
                Optimization.SINGLE_STEP_ELIMINATION,
                Optimization.JUMP_STRAIGHTENING,
                Optimization.TEMP_VARIABLES_ELIMINATION,
                Optimization.UNREACHABLE_CODE_ELIMINATION,
                Optimization.IF_EXPRESSION_OPTIMIZATION
        );
    }

    @Test
    void optimizesBasicCase() {
        assertCompilesTo("""
                        str = x > 0 ? "positive" : "negative";
                        print(str);
                        """,
                createInstruction(SELECT, ":str", "greaterThan", ":x", "0", q("positive"), q("negative")),
                createInstruction(PRINT, ":str")
        );
    }

    @Test
    void optimizesTrueBranchCompoundCondition() {
        assertCompilesTo("""
                        str = @unit.@dead === 0 ? "alive" : "dead";
                        print(str);
                        """,
                createInstruction(SENSOR, tmp(0), "@unit", "@dead"),
                createInstruction(SELECT, ":str", "strictEqual", tmp(0), "0", q("alive"), q("dead")),
                createInstruction(PRINT, ":str")
        );
    }

    @Test
    void optimizesTrueBranchInvertible() {
        assertCompilesTo("""
                        #set target = 7;
                        str = if x >= 0 then
                            "positive";
                        else
                            x = -x;
                            "negative";
                        end;
                        print(str);
                        """,
                createInstruction(SET, "str", q("positive")),
                createInstruction(JUMP, var(1001), "greaterThanEq", "x", "0"),
                createInstruction(OP, "sub", "x", "0", "x"),
                createInstruction(SET, "str", q("negative")),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "str")
        );
    }

    @Test
    void ignoresUnprocessableStatements() {
        assertCompilesTo("""
                        if x >= 0 then
                            print("positive");
                        else
                            print("negative");
                        end;
                        """,
                createInstruction(JUMP, var(1000), "lessThan", "x", "0"),
                createInstruction(PRINT, q("positive")),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("negative")),
                createInstruction(LABEL, var(1001))
        );
    }

    @Test
    void mergesWritesInLargeBranches() {
        assertCompilesTo("""
                        y = if x >= 0 then
                            print("positive");
                            x;
                        else
                            print("negative");
                            -x;
                        end;
                        print(y);
                        """,
                createInstruction(JUMP, var(1000), "lessThan", "x", "0"),
                createInstruction(PRINT, q("positive")),
                createInstruction(SET, "y", "x"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("negative")),
                createInstruction(OP, "sub", "y", "0", "x"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "y")
        );
    }

    @Test
    void mergesLookupsInLargeBranches() {
        assertCompilesTo("""
                        a = if @links > 5 then
                            print("Five");
                            lookup(:unit, 5);
                        else
                            print("Ten");
                            lookup(:unit, 10);
                        end;
                        print(a);
                        """,
                createInstruction(JUMP, var(1000), "lessThanEq", "@links", "5"),
                createInstruction(PRINT, q("Five")),
                createInstruction(LOOKUP, "unit", "a", "5"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("Ten")),
                createInstruction(LOOKUP, "unit", "a", "10"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "a")
        );
    }

    @Test
    void mergesMemoryReads() {
        assertCompilesTo("""
                        allocate heap in cell1[0 ... 64];
                        value = $setting == 1 ? $low : $high;
                        print(value);
                        """,
                createInstruction(READ, ":value", "cell1", "2"),
                createInstruction(READ, tmp(0), "cell1", "0"),
                createInstruction(JUMP, label(1), "notEqual", tmp(0), "1"),
                createInstruction(READ, ":value", "cell1", "1"),
                createInstruction(LABEL, label(1)),
                createInstruction(PRINT, ":value")
        );
    }

    @Test
    void swapsBranchesForCompoundConditions() {
        assertCompilesTo("""
                        #set emulate-strict-not-equal = false;
                        if @unit.@dead === 0 then
                            print("alive");
                        else
                            print("dead");
                        end;
                        """,
                createInstruction(SENSOR, tmp(0), "@unit", "@dead"),
                createInstruction(JUMP, label(0), "strictEqual", tmp(0), "0"),
                createInstruction(PRINT, q("dead")),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(PRINT, q("alive")),
                createInstruction(LABEL, label(1))
        );
    }

    @Test
    void detectsLeakingBlocks() {
        assertCompilesTo("""
                        while y > 0 do
                            y = if x > 0 then
                                if z == 0 then break; end;
                                2;
                            else
                                -2;
                            end;
                        end;
                        """,
                createInstruction(JUMP, var(1002), "lessThanEq", "y", "0"),
                createInstruction(JUMP, var(1003), "lessThanEq", "x", "0"),
                createInstruction(JUMP, var(1002), "equal", "z", "0"),
                createInstruction(SET, var(2), "2"),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(2), "-2"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "y", var(2)),
                createInstruction(LABEL, var(1002))
        );
    }

    @Test
    void optimizesChainedStatementsUsingSelect() {
        assertCompilesTo("""
                        #set target = 8;
                        #set optimization = experimental;
                        param x = 5;
                        y = if x < 0 then
                            "negative";
                        elsif x > 0 then
                            "positive";
                        else
                            "zero";
                        end;
                        print(y);
                        """,
                createInstruction(SET, "x", "5"),
                createInstruction(SELECT, tmp(4), "greaterThan", "x", "0", q("positive"), q("zero")),
                createInstruction(SELECT, tmp(1), "lessThan", "x", "0", q("negative"), tmp(4)),
                createInstruction(PRINT, tmp(1))
        );
    }

    @Test
    void optimizesChainedStatements() {
        assertCompilesTo("""
                        #set target = 7;
                        y = if x < 0 then
                            "negative";
                        elsif x > 0 then
                            "positive";
                        else
                            "zero";
                        end;
                        print(y);
                        """,
                createInstruction(SET, ":y", q("negative")),
                createInstruction(JUMP, label(1), "lessThan", ":x", "0"),
                createInstruction(SET, ":y", q("zero")),
                createInstruction(JUMP, label(3), "lessThanEq", ":x", "0"),
                createInstruction(SET, ":y", q("positive")),
                createInstruction(LABEL, label(3)),
                createInstruction(LABEL, label(1)),
                createInstruction(PRINT, ":y")
        );
    }

    @Test
    void optimizesChainedAssignments() {
        assertCompilesTo("""
                        a = b = rand(10) > 5 ? 1 : 2;
                        print(a, b);
                        """,
                createInstruction(OP, "rand", tmp(0), "10"),
                createInstruction(SELECT, ":b", "greaterThan", tmp(0), "5", "1", "2"),
                createInstruction(SET, ":a", ":b"),
                createInstruction(PRINT, ":a"),
                createInstruction(PRINT, ":b")
        );
    }

    @Test
    void optimizesChainedAssignments2() {
        // The set after select is normally eliminated by the DFO.
        assertCompilesTo("""
                        a = print(b = rand(10) > 5 ? 1 : 2);
                        print(a, b);
                        """,
                createInstruction(OP, "rand", tmp(0), "10"),
                createInstruction(SELECT, ":b", "greaterThan", tmp(0), "5", "1", "2"),
                createInstruction(PRINT, ":b"),
                createInstruction(SET, ":a", ":b"),
                createInstruction(PRINT, ":a"),
                createInstruction(PRINT, ":b")
        );
    }

    @Test
    void preservesDecisionVariable() {
        assertCompilesTo("""
                        i = rand(10);
                        i = i % 2 ? 5 : 6;
                        print(i);
                        """,
                createInstruction(OP, "rand", ":i", "10"),
                createInstruction(OP, "mod", tmp(1), ":i", "2"),
                createInstruction(SELECT, ":i", "notEqual", tmp(1), "false", "5", "6"),
                createInstruction(PRINT, ":i")
        );
    }

    @Test
    void pullsInstructionsIntoBranches() {
        assertCompilesTo("""
                        #set target = 7;
                        param a = 5;
                        print(a < 10 ? "units" : a < 100 ? "tens" : a < 1000 ? "hundreds" : "thousands");
                        """,
                createInstruction(SET, "a", "5"),
                createInstruction(JUMP, label(0), "greaterThanEq", "a", "10"),
                createInstruction(PRINT, q("units")),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(JUMP, label(2), "greaterThanEq", "a", "100"),
                createInstruction(PRINT, q("tens")),
                createInstruction(JUMP, label(3), "always"),
                createInstruction(LABEL, label(2)),
                createInstruction(JUMP, label(4), "greaterThanEq", "a", "1000"),
                createInstruction(PRINT, q("hundreds")),
                createInstruction(JUMP, label(5), "always"),
                createInstruction(LABEL, label(4)),
                createInstruction(PRINT, q("thousands")),
                createInstruction(LABEL, label(5)),
                createInstruction(LABEL, label(3)),
                createInstruction(LABEL, label(1))
        );
    }


    @Test
    void optimizesFunctionCallArgumentsUsingSelect() {
        assertCompilesTo("""
                        #set target = 8;
                        #set optimization = experimental;
                        param a = 5;
                        print(a < 10 ? "units" : a < 100 ? "tens" : a < 1000 ? "hundreds" : "thousands");
                        """,
                createInstruction(SET, "a", "5"),
                createInstruction(SELECT, tmp(6), "lessThan", "a", "1000", q("hundreds"), q("thousands")),
                createInstruction(SELECT, tmp(7), "lessThan", "a", "100", q("tens"), tmp(6)),
                createInstruction(SELECT, tmp(1), "lessThan", "a", "10", q("units"), tmp(7)),
                createInstruction(PRINT, tmp(1))
        );
    }

    @Test
    void recognizesSelfModifyingInstructions() {
        assertCompilesTo("""
                        Y1 = rand(0);
                        DIR1 = @thisx;
                        if DIR1 == 1 then
                            Y1 = Y1 + 1;
                        else
                            Y1 = Y1 - 1;
                        end;
                        print(Y1);
                        """,
                createInstruction(OP, "rand", "Y1", "0"),
                createInstruction(SET, "DIR1", "@thisx"),
                createInstruction(JUMP, var(1000), "notEqual", "DIR1", "1"),
                createInstruction(OP, "add", "Y1", "Y1", "1"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", "Y1", "Y1", "1"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "Y1")
        );
    }
}
