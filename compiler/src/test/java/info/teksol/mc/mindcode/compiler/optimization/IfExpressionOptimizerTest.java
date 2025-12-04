package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
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
                Optimization.CONDITION_OPTIMIZATION,
                Optimization.SINGLE_STEP_ELIMINATION,
                Optimization.JUMP_STRAIGHTENING,
                Optimization.TEMP_VARIABLES_ELIMINATION,
                Optimization.UNREACHABLE_CODE_ELIMINATION,
                Optimization.IF_EXPRESSION_OPTIMIZATION
        );
    }

    @Nested
    class FullEvaluation {

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
                    createInstruction(SET, ":str", q("positive")),
                    createInstruction(JUMP, label(1), "greaterThanEq", ":x", "0"),
                    createInstruction(OP, "sub", ":x", "0", ":x"),
                    createInstruction(SET, ":str", q("negative")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":str")
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
                    createInstruction(JUMP, label(0), "lessThan", ":x", "0"),
                    createInstruction(PRINT, q("positive")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("negative")),
                    createInstruction(LABEL, label(1))
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
                    createInstruction(JUMP, label(0), "lessThan", ":x", "0"),
                    createInstruction(PRINT, q("positive")),
                    createInstruction(SET, ":y", ":x"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("negative")),
                    createInstruction(OP, "sub", ":y", "0", ":x"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":y")
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
                    createInstruction(JUMP, label(0), "lessThanEq", "@links", "5"),
                    createInstruction(PRINT, q("Five")),
                    createInstruction(LOOKUP, "unit", ":a", "5"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("Ten")),
                    createInstruction(LOOKUP, "unit", ":a", "10"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":a")
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
                    createInstruction(JUMP, label(2), "lessThanEq", ":y", "0"),
                    createInstruction(JUMP, label(3), "lessThanEq", ":x", "0"),
                    createInstruction(JUMP, label(2), "equal", ":z", "0"),
                    createInstruction(SET, tmp(2), "2"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(2), "-2"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, ":y", tmp(2)),
                    createInstruction(LABEL, label(2))
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
        void recognizesSelfModifyingInstructions() {
            assertCompilesTo("""
                            y1 = rand(0);
                            x1 = @thisx;
                            if x1 == 1 then
                                y1 = y1 + 1;
                            else
                                y1 = y1 - 1;
                            end;
                            print(y1);
                            """,
                    createInstruction(OP, "rand", ".y1", "0"),
                    createInstruction(SET, ".x1", "@thisx"),
                    createInstruction(JUMP, label(0), "notEqual", ".x1", "1"),
                    createInstruction(OP, "add", ".y1", ".y1", "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "sub", ".y1", ".y1", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ".y1")
            );
        }
    }

    @Nested
    class ShortCircuit {

        @Test
        void aaa() {
            assertCompilesTo("""
                            #set target = 8.0;
                            
                            if a and b === null then
                                print("Positive");
                            else
                                print("Negative");
                            end;
                            """,
                    createInstruction(JUMP, label(0), "equal", ":a", "false"),
                    createInstruction(JUMP, label(2), "strictEqual", ":b", "null"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("Negative")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, q("Positive")),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void movesTrueBranchInFrontWithOr() {
            assertCompilesTo("""
                            #set target = 7;
                            str = if x >= 0 or y >= 0 then
                                "positive";
                            else
                                x = -x;
                                "negative";
                            end;
                            print(str);
                            """,
                    createInstruction(SET, ":str", q("positive")),
                    createInstruction(JUMP, label(1), "greaterThanEq", ":x", "0"),
                    createInstruction(JUMP, label(1), "greaterThanEq", ":y", "0"),
                    createInstruction(OP, "sub", ":x", "0", ":x"),
                    createInstruction(SET, ":str", q("negative")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":str")
            );
        }

        @Test
        void notMovesTrueBranchInFrontWithAnd() {
            assertCompilesTo("""
                            #set target = 7;
                            str = if x >= 0 and y >= 0 then
                                "positive";
                            else
                                x = -x;
                                "negative";
                            end;
                            print(str);
                            """,
                    createInstruction(JUMP, label(0), "lessThan", ":x", "0"),
                    createInstruction(JUMP, label(0), "lessThan", ":y", "0"),
                    createInstruction(SET, ":str", q("positive")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "sub", ":x", "0", ":x"),
                    createInstruction(SET, ":str", q("negative")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":str")
            );
        }

        @Test
        void movesFalseBranchInFrontWithAnd() {
            assertCompilesTo("""
                            #set target = 7;
                            str = if x >= 0 and y >= 0 then
                                x = -x;
                                "positive";
                            else
                                "negative";
                            end;
                            print(str);
                            """,
                    createInstruction(SET, ":str", q("negative")),
                    createInstruction(JUMP, label(1), "lessThan", ":x", "0"),
                    createInstruction(JUMP, label(1), "lessThan", ":y", "0"),
                    createInstruction(OP, "sub", ":x", "0", ":x"),
                    createInstruction(SET, ":str", q("positive")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":str")
            );
        }

        @Test
        void notMovesFalseBranchInFrontWithOr() {
            assertCompilesTo("""
                            #set target = 7;
                            str = if x >= 0 or y >= 0 then
                                x = -x;
                                "positive";
                            else
                                "negative";
                            end;
                            print(str);
                            """,
                    createInstruction(JUMP, label(2), "greaterThanEq", ":x", "0"),
                    createInstruction(JUMP, label(0), "lessThan", ":y", "0"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(OP, "sub", ":x", "0", ":x"),
                    createInstruction(SET, ":str", q("positive")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":str", q("negative")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":str")
            );
        }

        @Test
        void ignoresUnprocessableStatements() {
            assertCompilesTo("""
                            str = a and b or c ? "positive" : "negative";
                            print(str);
                            """,
                    createInstruction(JUMP, label(3), "equal", ":a", "false"),
                    createInstruction(JUMP, label(2), "notEqual", ":b", "false"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(JUMP, label(0), "equal", ":c", "false"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":str", q("positive")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":str", q("negative")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":str")
            );
        }

        @Test
        void mergesWritesInLargeBranches() {
            assertCompilesTo("""
                            y = if x >= 0 and y >= 0 then
                                print("positive");
                                x;
                            else
                                print("negative");
                                -x;
                            end;
                            print(y);
                            """,
                    createInstruction(JUMP, label(0), "lessThan", ":x", "0"),
                    createInstruction(JUMP, label(0), "lessThan", ":y", "0"),
                    createInstruction(PRINT, q("positive")),
                    createInstruction(SET, ":y", ":x"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("negative")),
                    createInstruction(OP, "sub", ":y", "0", ":x"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":y")
            );
        }

        @Test
        void mergesLookupsInLargeBranches() {
            assertCompilesTo("""
                            a = if @links > 5 and @links < 10 then
                                print("Five");
                                lookup(:unit, 5);
                            else
                                print("Ten");
                                lookup(:unit, 10);
                            end;
                            print(a);
                            """,
                    createInstruction(JUMP, label(0), "lessThanEq", "@links", "5"),
                    createInstruction(JUMP, label(0), "greaterThanEq", "@links", "10"),
                    createInstruction(PRINT, q("Five")),
                    createInstruction(LOOKUP, "unit", ":a", "5"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("Ten")),
                    createInstruction(LOOKUP, "unit", ":a", "10"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":a")
            );
        }

        @Test
        void mergesMemoryReads() {
            assertCompilesTo("""
                            allocate heap in cell1[0 ... 64];
                            value = $x == 1 or $x == 2 ? $low : $high;
                            print(value);
                            """,
                    createInstruction(READ, ":value", "cell1", "1"),
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(JUMP, label(1), "equal", tmp(0), "1"),
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(JUMP, label(1), "equal", tmp(0), "2"),
                    createInstruction(READ, ":value", "cell1", "2"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":value")
            );
        }

        @Test
        void swapsBranchesForCompoundConditions() {
            assertCompilesTo("""
                            #set emulate-strict-not-equal = false;
                            if active and @unit.@dead === 0 then
                                print("alive");
                            else
                                print("dead");
                            end;
                            """,
                    createInstruction(JUMP, label(0), "equal", ":active", "false"),
                    createInstruction(SENSOR, tmp(0), "@unit", "@dead"),
                    createInstruction(JUMP, label(2), "strictEqual", tmp(0), "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("dead")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, q("alive")),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void detectsLeakingBlocks() {
            assertCompilesTo("""
                            while y > 0 do
                                y = if x > 0 and x < 10 then
                                    if z == 0 then break; end;
                                    2;
                                else
                                    -2;
                                end;
                            end;
                            """,
                    createInstruction(JUMP, label(2), "lessThanEq", ":y", "0"),
                    createInstruction(JUMP, label(3), "lessThanEq", ":x", "0"),
                    createInstruction(JUMP, label(3), "greaterThanEq", ":x", "10"),
                    createInstruction(JUMP, label(2), "equal", ":z", "0"),
                    createInstruction(SET, tmp(3), "2"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(3), "-2"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, ":y", tmp(3)),
                    createInstruction(LABEL, label(2))
            );
        }

        @Test
        void optimizesChainedStatements() {
            assertCompilesTo("""
                            #set target = 7;
                            #set optimization = advanced;
                            noinit var x, y;
                            y = if x < 0 and y < 0 then
                                "negative";
                            elsif x > 0 and y > 0 then
                                "positive";
                            else
                                "indecisive";
                            end;
                            print(y);
                            """,
                    createInstruction(JUMP, label(0), "greaterThanEq", ".x", "0"),
                    createInstruction(JUMP, label(0), "greaterThanEq", ".y", "0"),
                    createInstruction(SET, tmp(2), q("negative")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(2), q("indecisive")),
                    createInstruction(JUMP, label(5), "lessThanEq", ".x", "0"),
                    createInstruction(JUMP, label(5), "lessThanEq", ".y", "0"),
                    createInstruction(SET, tmp(2), q("positive")),
                    createInstruction(LABEL, label(5)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ".y", tmp(2)),
                    createInstruction(PRINT, tmp(2))
            );
        }

        @Test
        void pullsInstructionsIntoBranches() {
            assertCompilesTo("""
                            #set target = 7;
                            param a = 5;
                            print(a < 10 and b < 10 ? "units" : a < 100 and b < 100 ? "tens" : a < 1000 and b < 1000  ? "hundreds" : "thousands");
                            """,
                    createInstruction(SET, "a", "5"),
                    createInstruction(JUMP, label(0), "greaterThanEq", "a", "10"),
                    createInstruction(JUMP, label(0), "greaterThanEq", ":b", "10"),
                    createInstruction(PRINT, q("units")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(4), "greaterThanEq", "a", "100"),
                    createInstruction(JUMP, label(4), "greaterThanEq", ":b", "100"),
                    createInstruction(PRINT, q("tens")),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(JUMP, label(8), "greaterThanEq", "a", "1000"),
                    createInstruction(JUMP, label(8), "greaterThanEq", ":b", "1000"),
                    createInstruction(PRINT, q("hundreds")),
                    createInstruction(JUMP, label(9), "always"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(PRINT, q("thousands")),
                    createInstruction(LABEL, label(9)),
                    createInstruction(LABEL, label(5)),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void recognizesSelfModifyingInstructions() {
            assertCompilesTo("""
                            y1 = rand(0);
                            x1 = @thisx;
                            if x1 == 1 or x1 == 2 then
                                y1 = y1 + 1;
                            else
                                y1 = y1 - 1;
                            end;
                            print(y1);
                            """,
                    createInstruction(OP, "rand", ":y1", "0"),
                    createInstruction(SET, ":x1", "@thisx"),
                    createInstruction(JUMP, label(2), "equal", ":x1", "1"),
                    createInstruction(JUMP, label(0), "notEqual", ":x1", "2"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(OP, "add", ":y1", ":y1", "1"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "sub", ":y1", ":y1", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":y1")
            );
        }
    }
}
