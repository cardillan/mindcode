package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class BooleanOptimizerTest extends AbstractOptimizerTest<BooleanOptimizer> {

    @Override
    protected Class<BooleanOptimizer> getTestedClass() {
        return BooleanOptimizer.class;
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
                Optimization.BOOLEAN_OPTIMIZATION
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
                            #set target = 8m;
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
                            #set target = 8m;
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

    @Nested
    class PureBooleanExpressions {
        @Test
        void overallInlineTest() {
            assertCompilesTo("""
                            #set optimization = experimental;
                            inline def foo(a, b, c, d)
                                (a and b) or (c and d);
                            end;
                            
                            inline def bar(a, b, c, d)
                                (a or b) and (c or d);
                            end;
                            
                            noinit x, y, z;
                            volatile v;
                            
                            v = (foo(0, x, y, z));
                            v = (foo(x, 0, y, z));
                            v = (foo(x, y, 0, z));
                            v = (foo(x, y, z, 0));
                            v = (bar(0, x, y, z));
                            v = (bar(x, 0, y, z));
                            v = (bar(x, y, 0, z));
                            v = (bar(x, y, z, 0));
                            v = (foo(1, x, y, z));
                            v = (foo(x, 1, y, z));
                            v = (foo(x, y, 1, z));
                            v = (foo(x, y, z, 1));
                            v = (bar(1, x, y, z));
                            v = (bar(x, 1, y, z));
                            v = (bar(x, y, 1, z));
                            v = (bar(x, y, z, 1));
                            print(v);
                            """,
                    createInstruction(OP, "land", ".v", ".y", ".z"),
                    createInstruction(OP, "land", ".v", ".y", ".z"),
                    createInstruction(OP, "land", ".v", ".x", ".y"),
                    createInstruction(OP, "land", ".v", ".x", ".y"),
                    createInstruction(OP, "or", tmp(34), ".y", ".z"),
                    createInstruction(OP, "land", ".v", tmp(34), ".x"),
                    createInstruction(OP, "land", ".v", tmp(34), ".x"),
                    createInstruction(OP, "or", tmp(36), ".x", ".y"),
                    createInstruction(OP, "land", ".v", tmp(36), ".z"),
                    createInstruction(OP, "land", ".v", tmp(36), ".z"),
                    createInstruction(OP, "land", tmp(38), ".y", ".z"),
                    createInstruction(OP, "or", ".v", tmp(38), ".x"),
                    createInstruction(OP, "or", ".v", tmp(38), ".x"),
                    createInstruction(OP, "land", tmp(40), ".x", ".y"),
                    createInstruction(OP, "or", ".v", tmp(40), ".z"),
                    createInstruction(OP, "or", ".v", tmp(40), ".z"),
                    createInstruction(OP, "or", ".v", ".y", ".z"),
                    createInstruction(OP, "or", ".v", ".y", ".z"),
                    createInstruction(OP, "or", ".v", ".x", ".y"),
                    createInstruction(OP, "or", ".v", ".x", ".y"),
                    createInstruction(PRINT, ".v")
            );
        }

        @Test
        void overallDefaultTest() {
            assertCompilesTo("""
                            #set optimization = experimental;
                            #set if-expression-optimization = none;
                            
                            inline def foo(a, b, c, d)
                                (a and b) or (c and d);
                            end;
                            
                            inline def bar(a, b, c, d)
                                (a or b) and (c or d);
                            end;
                            
                            noinit var x, y, z;
                            
                            print(foo(0, x, y, z));
                            print(foo(x, 0, y, z));
                            print(foo(x, y, 0, z));
                            print(foo(x, y, z, 0));
                            print(bar(0, x, y, z));
                            print(bar(x, 0, y, z));
                            print(bar(x, y, 0, z));
                            print(bar(x, y, z, 0));
                            
                            print(foo(1, x, y, z));
                            print(foo(x, 1, y, z));
                            print(foo(x, y, 1, z));
                            print(foo(x, y, z, 1));
                            print(bar(1, x, y, z));
                            print(bar(x, 1, y, z));
                            print(bar(x, y, 1, z));
                            print(bar(x, y, z, 1));
                            """,
                    createInstruction(OP, "land", tmp(1), ".y", ".z"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(OP, "land", tmp(5), ".x", ".y"),
                    createInstruction(PRINT, tmp(5)),
                    createInstruction(OP, "land", tmp(7), ".x", ".y"),
                    createInstruction(PRINT, tmp(7)),
                    createInstruction(OP, "or", tmp(33), ".y", ".z"),
                    createInstruction(OP, "land", tmp(9), tmp(33), ".x"),
                    createInstruction(PRINT, tmp(9)),
                    createInstruction(PRINT, tmp(9)),
                    createInstruction(OP, "or", tmp(35), ".x", ".y"),
                    createInstruction(OP, "land", tmp(13), tmp(35), ".z"),
                    createInstruction(PRINT, tmp(13)),
                    createInstruction(PRINT, tmp(13)),
                    createInstruction(OP, "or", tmp(17), tmp(1), ".x"),
                    createInstruction(PRINT, tmp(17)),
                    createInstruction(PRINT, tmp(17)),
                    createInstruction(OP, "land", tmp(39), ".x", ".y"),
                    createInstruction(OP, "or", tmp(21), tmp(39), ".z"),
                    createInstruction(PRINT, tmp(21)),
                    createInstruction(PRINT, tmp(21)),
                    createInstruction(PRINT, tmp(33)),
                    createInstruction(PRINT, tmp(33)),
                    createInstruction(PRINT, tmp(35)),
                    createInstruction(PRINT, tmp(35))
            );
        }

        @Test
        void optimizesAndAnd() {
            assertCompilesTo("""
                            noinit var a, b, c;
                            print(a and b and c);
                            """,
                    createInstruction(OP, "land", tmp(1), ".a", ".b"),
                    createInstruction(OP, "land", tmp(0), tmp(1), ".c"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void optimizesAndAfterOr() {
            assertCompilesTo("""
                            noinit var a, b, c;
                            print(a and (b or c));
                            """,
                    createInstruction(OP, "or", tmp(1), ".b", ".c"),
                    createInstruction(OP, "land", tmp(0), tmp(1), ".a"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void optimizesOrAfterAnd() {
            assertCompilesTo("""
                            noinit var a, b, c;
                            print(a or (b and c));
                            """,
                    createInstruction(OP, "land", tmp(1), ".b", ".c"),
                    createInstruction(OP, "or", tmp(0), tmp(1), ".a"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void optimizesOrOr() {
            assertCompilesTo("""
                            noinit var a, b, c;
                            print(a or b or c);
                            """,
                    createInstruction(OP, "or", tmp(1), ".a", ".b"),
                    createInstruction(OP, "or", tmp(0), tmp(1), ".c"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void optimizesOrBeforeAnd() {
            assertCompilesTo("""
                            noinit var a, b, c;
                            print((a or b) and c);
                            """,
                    createInstruction(OP, "or", tmp(1), ".a", ".b"),
                    createInstruction(OP, "land", tmp(0), tmp(1), ".c"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void optimizesAndBeforeOr() {
            assertCompilesTo("""
                            noinit var a, b, c;
                            print((a and b) or c);
                            """,
                    createInstruction(OP, "land", tmp(1), ".a", ".b"),
                    createInstruction(OP, "or", tmp(0), tmp(1), ".c"),
                    createInstruction(PRINT, tmp(0))
            );
        }
    }

    @Nested
    class ConversionToFullEvaluation {
        @Test
        void optimizesForSpeed() {
            assertCompilesTo("""
                            if switch1.enabled and @unit != null then
                                x = 10; y = 20;
                            else
                                x = 5; y = 7;
                            end;
                            print(x, y);
                            """,
                    createInstruction(SENSOR, tmp(0), "switch1", "@enabled"),
                    createInstruction(OP, "land", tmp(3), tmp(0), "@unit"),
                    createInstruction(SELECT, ":x", "notEqual", tmp(3), "false", "10", "5"),
                    createInstruction(SELECT, ":y", "notEqual", tmp(3), "false", "20", "7"),
                    createInstruction(PRINT, ":x"),
                    createInstruction(PRINT, ":y")
            );
        }

        @Test
        void doesNotOptimizesForSpeed() {
            assertCompilesTo("""
                            if switch1.enabled and switch2.enabled then
                                x = 10; y = 20;
                            else
                                x = 5; y = 7;
                            end;
                            print(x, y);
                            """,
                    createInstruction(SENSOR, tmp(0), "switch1", "@enabled"),
                    createInstruction(JUMP, label(0), "equal", tmp(0), "false"),
                    createInstruction(SENSOR, tmp(1), "switch2", "@enabled"),
                    createInstruction(JUMP, label(0), "equal", tmp(1), "false"),
                    createInstruction(SET, ":x", "10"),
                    createInstruction(SET, ":y", "20"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":x", "5"),
                    createInstruction(SET, ":y", "7"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":x"),
                    createInstruction(PRINT, ":y")
            );
        }

        @Test
        void optimizesForSize() {
            assertCompilesTo("""
                            #set goal = size;
                            if switch1.enabled and switch2.enabled then
                                x = 10; y = 20;
                            else
                                x = 5; y = 7;
                            end;
                            print(x, y);
                            """,
                    createInstruction(SENSOR, tmp(0), "switch1", "@enabled"),
                    createInstruction(SENSOR, tmp(1), "switch2", "@enabled"),
                    createInstruction(OP, "land", tmp(3), tmp(0), tmp(1)),
                    createInstruction(SELECT, ":x", "notEqual", tmp(3), "false", "10", "5"),
                    createInstruction(SELECT, ":y", "notEqual", tmp(3), "false", "20", "7"),
                    createInstruction(PRINT, ":x"),
                    createInstruction(PRINT, ":y")
            );
        }
    }

    @Nested
    class SingleVariableAssignment {
        @Test
        void optimizesForSpeed() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'x' is not used."),
                    """
                            #set optimization = experimental;
                            noinit a, b, c;
                            volatile x = a > 0 or b > 0 ? "yes" : "no";
                            """,
                    createInstruction(SELECT, tmp(3), "greaterThan", ".a", "0", q("yes"), q("no")),
                    createInstruction(SELECT, ".x", "greaterThan", ".b", "0", q("yes"), tmp(3))
            );
        }

        @Test
        void doesNotOptimizeAdditionalOperationsForSpeed() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'x' is not used."),
                    """
                            volatile x = a > 0 or @unit.@dead ? 10 : 20;
                            """,
                    createInstruction(JUMP, label(0), "lessThanEq", ".a", "0"),
                    createInstruction(SET, tmp(2), "10"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SENSOR, tmp(1), "@unit", "@dead"),
                    createInstruction(SELECT, tmp(2), "equal", tmp(1), "false", "20", "10"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ".x", tmp(2))
            );
        }

        @Test
        void doesNotOptimizeTooManyArgumentsForSpeed() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'x' is not used."),
                    """
                            volatile x = a > 0 or b > 0 or c > 0 or d > 0 ? 10 : 20;
                            """,
                    createInstruction(JUMP, label(2), "greaterThan", ".a", "0"),
                    createInstruction(JUMP, label(2), "greaterThan", ":b", "0"),
                    createInstruction(JUMP, label(0), "lessThanEq", ":c", "0"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(4), "10"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SELECT, tmp(4), "lessThanEq", ":d", "0", "20", "10"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ".x", tmp(4))
            );
        }

        @Test
        void optimizesForSize() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'x' is not used."),
                    """
                            #set goal = size;
                            volatile x = a > 0 or b > 0 or c > 0 or @unit.@dead ? 10 : 20;
                            """,
                    createInstruction(SELECT, tmp(5), "greaterThan", ":a", "0", "10", "20"),
                    createInstruction(SELECT, tmp(6), "greaterThan", ":b", "0", "10", tmp(5)),
                    createInstruction(SELECT, tmp(7), "greaterThan", ":c", "0", "10", tmp(6)),
                    createInstruction(SENSOR, tmp(3), "@unit", "@dead"),
                    createInstruction(SELECT, ".x", "notEqual", tmp(3), "false", "10", tmp(7))
            );
        }
    }

    @Nested
    class LastJumpConversion {

        @Test
        void optimizesToSelect() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'x' is not used."),
                    """
                            volatile x = switch1.enabled and !@unit.@dead and amount > 0 ? "yes" : "no";
                            """,
                    createInstruction(SENSOR, tmp(0), "switch1", "@enabled"),
                    createInstruction(JUMP, label(0), "equal", tmp(0), "false"),
                    createInstruction(SENSOR, tmp(1), "@unit", "@dead"),
                    createInstruction(JUMP, label(0), "notEqual", tmp(1), "false"),
                    createInstruction(SELECT, tmp(4), "lessThanEq", ":amount", "0", q("no"), q("yes")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(4), q("no")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ".x", tmp(4))
            );
        }

        @Test
        void optimizesToOp() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'x' is not used."),
                    """
                            volatile x = switch1.enabled and !@unit.@dead and amount === null;
                            """,
                    createInstruction(SENSOR, tmp(0), "switch1", "@enabled"),
                    createInstruction(JUMP, label(0), "equal", tmp(0), "false"),
                    createInstruction(SENSOR, tmp(1), "@unit", "@dead"),
                    createInstruction(JUMP, label(0), "notEqual", tmp(1), "false"),
                    createInstruction(OP, "strictEqual", tmp(4), ":amount", "null"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(4), "false"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ".x", tmp(4))
            );
        }

        @Test
        void doesNotOptimizeWithoutSelect() {
            assertCompilesTo(
                    expectedMessages().add("Variable 'x' is not used."),
                    """
                            #set target = 7m;
                            volatile x = switch1.enabled and !@unit.@dead and amount > 0 ? "yes" : "no";
                            """,
                    createInstruction(SENSOR, tmp(0), "switch1", "@enabled"),
                    createInstruction(JUMP, label(0), "equal", tmp(0), "false"),
                    createInstruction(SENSOR, tmp(1), "@unit", "@dead"),
                    createInstruction(JUMP, label(0), "notEqual", tmp(1), "false"),
                    createInstruction(JUMP, label(0), "lessThanEq", ":amount", "0"),
                    createInstruction(SET, tmp(4), q("yes")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(4), q("no")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ".x", tmp(4))
            );
        }
    }
}
