package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
abstract class LoopUnrollerTestBase extends AbstractOptimizerTest<LoopUnroller> {
    private final OptimizationLevel level;

    protected LoopUnrollerTestBase(OptimizationLevel level) {
        this.level = level;
    }

    protected static Predicate<LogicInstruction> excludeLabelInstructions() {
        return ix -> !(ix instanceof LabelInstruction);
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile()
                .setOptimizationLevel(Optimization.LOOP_UNROLLING, level)
                .setGoal(GenerationGoal.SPEED);
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Override
    protected Class<LoopUnroller> getTestedClass() {
        return LoopUnroller.class;
    }

    @Test
    void ignoresDegenerateLoops() {
        assertCompilesTo("""
                        i = 0;
                        while i < 1000 do
                            print(i);
                        end;
                        """,
                createInstruction(PRINT, "0")
        );
    }

    @Test
    void preservesBranchedIterations() {
        assertCompilesTo("""
                        i = 0;
                        while i < 10 do
                            i = i + (i % 2 ? 1 : 2);
                            print(i);
                        end;
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "mod", var(1), "i", "2"),
                createInstruction(JUMP, var(1003), "equal", var(1), "false"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", "i", "i", "2"),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, var(1005), "lessThan", "i", "10")
        );
    }

    @Test
    void preservesEmptyLoops() {
        assertCompilesTo("""
                for i = -5; i < -10; i += 1 do
                    print("a");
                end;
                """
        );
    }

    @Test
    void preservesNestedLoops() {
        assertCompilesTo("""
                        i = 0;
                        while i < 10 do
                            while i < 5 do
                                i += 1;
                            end;
                            print(i);
                        end;
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1006)),
                createInstruction(JUMP, var(1005), "greaterThanEq", "i", "5"),
                createInstruction(LABEL, var(1007)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1007), "lessThan", "i", "5"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, var(1006), "lessThan", "i", "10")
        );
    }

    @Test
    void processesEntryCondition() {
        assertCompilesTo("""
                        #set loop-optimization = none;
                        for i in 1 .. 3 do
                            print(i);
                        end;
                        """,
                createInstruction(PRINT, q("123"))
        );
    }

    @Test
    void processesExitCondition() {
        assertCompilesTo("""
                        #set loop-optimization = none;
                        i = 1;
                        do
                            print(i);
                            i += 1;
                        while i <= 3;
                        """,
                createInstruction(PRINT, q("123"))
        );
    }

    @Test
    void unrollsComplexLoop() {
        assertCompilesTo("""
                        i = 0;
                        while i < 6 do
                            i += 2;
                            print(i);
                            i -= 1;
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("213243546576"))
        );
    }

    @Test
    void unrollsCStyleLoop() {
        assertCompilesTo("""
                        for i = 0; i < 10; i += 1 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsDecreasingLoopExclusive() {
        assertCompilesTo("""
                        for i = 9; i > 2; i -= 1 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("9876543"))
        );
    }

    @Test
    void unrollsDecreasingLoopInclusive() {
        assertCompilesTo("""
                        for i = 9; i >= 0; i -= 1 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("9876543210"))
        );
    }

    @Test
    void unrollsDoWhileLoop() {
        assertCompilesTo("""
                        i = 0;
                        do
                            print(i);
                            i += 1;
                        while i < 10;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsExclusiveRangeIterationLoop() {
        assertCompilesTo("""
                        for i in 0 ... 10 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsInclusiveRangeIterationLoop() {
        assertCompilesTo("""
                        for i in 0 .. 9 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsListIterationLoops() {
        assertCompilesTo("""
                        for i in 1, 2, 3 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("123"))
        );
    }

    @Test
    void unrollsListIterationLoopsWithModifications() {
        assertCompilesTo("""
                        n = 0;
                        for out i, out j in a, b, c, d, e, f do
                            i = n += 1;
                            j = 2 * n;
                        end;
                        
                        for i in a, b, c, d, e, f do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("122436"))
        );
    }

    @Test
    void unrollsListIterationLoopsWithThreeIterators() {
        assertCompilesTo("""
                        for i, j, k in 1, 2, 3, 4, 5, 6, 7, 8, 9 do
                            print(i, j, k);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("123456789"))
        );
    }

    @Test
    void unrollsListIterationLoopWithBreak() {
        assertCompilesTo("""
                        for i in 1, 2, 3 do
                            print(i);
                            if i == 2 then break; end;
                        end;
                        """,
                createInstruction(PRINT, q("12"))
        );
    }

    @Test
    void unrollsListIterationLoopWithContinue() {
        assertCompilesTo("""
                        for i in 1, 2, 3 do
                            if i == 2 then continue; end;
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("13"))
        );
    }

    @Test
    void unrollsLoopsWithBreak() {
        assertCompilesTo("""
                        for i in 0 .. 10 do
                            if i == 6 then break; end;
                            print(i);
                        end;
                        print(".");
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("012345."))
        );
    }

    @Test
    void unrollsLoopsWithContinue() {
        assertCompilesTo("""
                        for i in 0 .. 9 do
                            if i % 2 then continue; end;
                            print(i);
                        end;
                        print(".");
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("02468."))
        );
    }

    @Test
    void unrollsLoopWithExpressionInCondition() {
        assertCompilesTo("""
                        i = 0;
                        while (i += 1) < 10 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("123456789"))
        );
    }

    @Test
    void unrollsLoopWithFunctionCalls() {
        assertCompilesTo("""
                        def foo(in out a, in out b)
                            a += 2;
                            b -= 1;
                        end;
                        
                        x = y = 0;
                        for i in 1 ... 10 do
                            foo(out x, out y);
                            print(x + y);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("123456789"))
        );
    }

    @Test
    void unrollsNegativeLoop() {
        assertCompilesTo("""
                        for i = -5; i > -10; i -= 1 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("-5-6-7-8-9"))
        );
    }

    @Test
    void unrollsNegativeLoop2() {
        assertCompilesTo("""
                        for i = -9; i <= -5; i += 1 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("-9-8-7-6-5"))
        );
    }

    @Test
    void unrollsNegativeLoop3() {
        assertCompilesTo("""
                        for i = 5; i >= -5; i -= 1 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("543210-1-2-3-4-5"))
        );
    }

    @Test
    void unrollsNestedLoops() {
        assertCompilesTo("""
                        for i in 1 .. 5 do
                            for j in i .. 5 do
                                print(" ", 10 * i + j);
                            end;
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q(" 11 12 13 14 15 22 23 24 25 33 34 35 44 45 55"))
        );
    }

    @Test
    void unrollsNestedLoops2() {
        assertCompilesTo("""
                        MainLoop:
                        for i in 0 ... 10 do
                            if i % 5 == 2 then
                                for j in 0 ... 10 do
                                    print(j);
                                    if j == i then continue MainLoop; end;
                                end;
                            end;
                        end;
                        print(".");
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("01201234567."))
        );
    }

    @Test
    void unrollsRangeIterationLoop() {
        assertCompilesTo("""
                        for i in 0 .. 9 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsSingleIterationLoop() {
        assertCompilesTo("""
                        for i in 1 .. 1 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, "1")
        );
    }

    @Test
    void unrollsUnevenLoop() {
        assertCompilesTo("""
                        for i = 0; i < 3; i += 2 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("02"))
        );
    }

    @Test
    void unrollsUnevenLoop2() {
        assertCompilesTo("""
                        for i = 0; i <= 3; i += 2 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("02"))
        );
    }

    @Test
    void unrollsUnevenLoop3() {
        assertCompilesTo("""
                        for i = 0; i <= 4; i += 2 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("024"))
        );
    }

    @Test
    void unrollsUpdatesInConditions() {
        assertCompilesTo("""
                        i = 0;
                        while (i += 1) < 10 do
                            print(i);
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("123456789"))
        );
    }

    @Test
    void unrollsWhileLoop() {
        assertCompilesTo("""
                        i = 0;
                        while i < 10 do
                            print(i);
                            i += 1;
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q("0123456789"))
        );
    }

    @Test
    void unrollsLoopsWithHoistedFunctionCalls() {
        assertCompiles("""
                        noinline void foo(a) print(a); end;
                        for i in 0 ... 3 do foo(i); end;
                        for j in 0 ... 3 do foo(j); end;
                        """
        );
    }
}
