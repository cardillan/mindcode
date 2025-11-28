package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class SelectOptimizerTest extends AbstractOptimizerTest<SelectOptimizer> {

    @Override
    protected Class<SelectOptimizer> getTestedClass() {
        return SelectOptimizer.class;
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
                Optimization.SELECT_OPTIMIZATION

        );
    }

    @Nested
    class FullEvaluation {
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
        void optimizesChainedStatements() {
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
        void optimizesFunctionCallArgumentsUsingSelect() {
            assertCompilesTo("""
                            #set target = 8;
                            #set goal = size;
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
    }
}
